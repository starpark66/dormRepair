package org.example.dormrepairtwo.service.impl;

import org.example.dormrepairtwo.mapper.RepairTableMapper;
import org.example.dormrepairtwo.pojo.RepairTable;
import org.example.dormrepairtwo.util.RegEx;
import org.example.dormrepairtwo.service.impl.StudentDormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RepairTableService {
    // 日志实例
    private static final Logger logger = LoggerFactory.getLogger(RepairTableService.class);

    @Autowired
    private RepairTableMapper repairTableMapper;

    @Autowired
    private StudentDormService dormService;

    // 提交报修
    @Transactional
    public boolean submitRepair(RepairTable rt) {
        if (!dormService.hasDorm(rt.getStuUserId())) {
            // 日志警告
            logger.warn("提交报修失败，学生{}未绑定宿舍", rt.getStuUserId());
            return false;
        }
        rt.setCreateTime(LocalDateTime.now());
        rt.setUpdateTime(LocalDateTime.now());
        return repairTableMapper.insert(rt) > 0;
    }

    // 查询我的报修
    public List<RepairTable> getMyRepairs(String stuUserId) {
        return repairTableMapper.selectByUserId(stuUserId);
    }

    // 学生取消报修
    @Transactional
    public boolean cancelRepair(long repairId, String stuUserId) {
        RepairTable rt = repairTableMapper.selectByRepairId(repairId);
        if (rt == null || !rt.getStuUserId().equals(stuUserId)) {
            logger.warn("学生{}取消报修单{}失败：无此报修单或无权限", stuUserId, repairId);
            return false;
        }
        if (rt.getStatus() != 0) {
            logger.warn("学生{}取消报修单{}失败：只能取消待处理(状态0)的报修单，当前状态{}", stuUserId, repairId, rt.getStatus());
            return false;
        }

        rt.setUpdateTime(LocalDateTime.now());
        return repairTableMapper.updateStatus(repairId, 3) > 0;
    }

    // 维修人员查看自己的任务
    public List<RepairTable> getMyRepairTasks(String repairUserId) {
        return repairTableMapper.selectByRepairUserId(repairUserId);
    }

    // 维修人员完成任务
    @Transactional
    public boolean completeRepair(Long repairId, String repairUserId) {
        RepairTable rt = repairTableMapper.selectByRepairId(repairId);
        if (rt == null) {
            logger.warn("维修人员{}完成任务{}失败：报修单不存在", repairUserId, repairId);
            return false;
        }
        if (!repairUserId.equals(rt.getRepairUserId())) {
            logger.warn("维修人员{}无权操作他人的维修任务{}", repairUserId, repairId);
            return false;
        }
        if (rt.getStatus() != 1) {
            logger.warn("维修人员{}完成任务{}失败：只有【处理中(状态1)】的任务才能标记为完成，当前状态{}", repairUserId, repairId, rt.getStatus());
            return false;
        }
        rt.setStatus(2);
        return repairTableMapper.updateStatus(repairId,2) > 0;
    }

    // 维修人员查看所有自己的任务
    public List<RepairTable> getAllMyRepairsTasks(String repairUserId) {
        return repairTableMapper.selectByRepairUserId(repairUserId);
    }

    // 查看处理中的任务
    public List<RepairTable> getProcessingMyRepairs(String repairUserId) {
        return repairTableMapper.selectByRepairUserIdAndStatus(repairUserId, 1);//1=处理中
    }

    // =============== 管理员功能 ===============

    // 管理员查看所有报修
    public List<RepairTable> getAllRepairTasks() {
        return repairTableMapper.selectAll();
    }

    // 按状态筛选
    public List<RepairTable> getRepairTaskByStatus(Integer status) {
        return repairTableMapper.selectByStatus(status);
    }

    // 管理员删除报修单
    @Transactional
    public boolean deleteRepairTask(long repairId) {
        RepairTable rt = repairTableMapper.selectByRepairId(repairId);
        if (rt == null) {
            logger.warn("管理员删除报修单{}失败：该报修单不存在", repairId);
            return false;
        }
        if (rt.getStatus() == 1 || rt.getStatus() == 2) {
            logger.warn("管理员删除报修单{}失败：正在处理中(1)或已完成(2)的报修单不能删除，当前状态{}", repairId, rt.getStatus());
            return false;
        }
        return repairTableMapper.deleteByRepairId(repairId) > 0;
    }

    // 查看报修单详情
    public RepairTable getRepairDetailById(Long repairId) {
        return repairTableMapper.selectByRepairId(repairId);
    }

    // 管理员分配报修单
    @Transactional
    public boolean assignRepairTask(Long repairId, String repairUserId, Integer status) {
        RepairTable rt = repairTableMapper.selectByRepairId(repairId);
        if (rt == null) {
            logger.warn("管理员分配报修单{}失败：该报修单不存在", repairId);
            return false;
        }
        if (rt.getStatus() != 0) {
            logger.warn("管理员分配报修单{}失败：只能分配待处理(0)的报修单，当前状态{}", repairId, rt.getStatus());
            return false;
        }
        if (!RegEx.checkWorkerId(repairUserId)) {
            logger.warn("管理员分配报修单{}失败：维修人员账号{}不存在或格式错误", repairId, repairUserId);
            return false;
        }

        return repairTableMapper.assignRepairTask(repairId, repairUserId, 1) > 0;
    }

    // 学生评分
    @Transactional
    public boolean scoreRepair(Long repairId, Integer score,String stuUserId) {
        RepairTable rt = repairTableMapper.selectByRepairId(repairId);
        if (rt == null) {
            logger.warn("学生{}评分报修单{}失败：该报修单不存在", stuUserId, repairId);
            return false;
        }
        if (!rt.getStuUserId().equals(stuUserId)) {
            logger.warn("学生{}无权评分报修单{}：只能评分自己的报修单", stuUserId, repairId);
            return false;
        }
        if (rt.getScore() != null) {
            logger.warn("学生{}评分报修单{}失败：该报修单已评分", stuUserId, repairId);
            return false;
        }
        if (rt.getStatus() != 2) {
            logger.warn("学生{}评分报修单{}失败：只有已完成(状态2)的报修单才能评分，当前状态{}", stuUserId, repairId, rt.getStatus());
            return false;
        }
        return repairTableMapper.updateScore(repairId, score) > 0;
    }

    // 统计维修人员评分
    public List<Map<String, Object>> getRepairmanScoreStat() {
        return repairTableMapper.getRepairmanScoreStat();
    }

    // 按紧急程度查询
    public List<Map<String, Object>> getRepairByUrgency(Integer urgency) {
        return repairTableMapper.getRepairByUrgency(urgency);
    }
}