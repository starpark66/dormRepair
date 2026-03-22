package org.example.dormrepair.service;

import org.example.dormrepair.mapper.RepairTableMapper;
import org.example.dormrepair.mapper.UserMapper;
import org.example.dormrepair.pojo.repairTable;
import org.example.dormrepair.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.example.dormrepair.util.RegEx;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RepairTableService {
    private StudentDormService dormService = new StudentDormService();

    public boolean submitRepair(repairTable rt) {
        //有无宿舍？
        if (!dormService.hasDorm(rt.getStuUserId())) {
            System.out.println("请先绑定宿舍");
            return false;
        }
        //插入报修单记录
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairTableMapper mapper = session.getMapper(RepairTableMapper.class);
            return mapper.insert(rt) > 0;
        }
    }

    //查询学生的报修单
    public List<repairTable> getMyRepairs(String stuUserId) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairTableMapper mapper = session.getMapper(RepairTableMapper.class);
            return mapper.selectByUserId(stuUserId);
        }
    }

    //学生取消报修单
    public boolean cancelRepair(long repairId, String stuUserId) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairTableMapper mapper = session.getMapper(RepairTableMapper.class);
            //先查询报修单是否存在且属于该学生
            repairTable rt = mapper.selectByRepairId(repairId);
            if (rt == null || !rt.getStuUserId().equals(stuUserId)) {
                System.out.println("无此报修单或无权限取消");
                return false;
            }

            if (rt.getStatus() != 0) { // 0=待处理，1=处理中，2=已完成
                System.out.println("只能取消待处理的报修单");
                return false;
            }


            rt.setUpdateTime(LocalDateTime.now());
            int rows = mapper.updateStatus(repairId, 3);
            return rows > 0;
        }
    }

    //查看个人维修任务
    public List<repairTable> getMyRepairTasks(String repairUserId){
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairTableMapper mapper = session.getMapper(RepairTableMapper.class);
            return mapper.selectByRepairUserId(repairUserId);
        }
    }

    //确认完成报修任务
    public boolean completeRepair(long repairId,String repairUserId){
        try (SqlSession session = MyBatisUtil.getSqlSession()){
            RepairTableMapper mapper = session.getMapper(RepairTableMapper.class);
            repairTable rt = mapper.selectByRepairId(repairId);
            if (rt == null){
                System.out.println("报修单不存在");
                return false;
            }
            if (!repairUserId.equals(rt.getRepairUserId())){
                System.out.println("无权操作他人的维修任务！");
                return false;
            }
            if (rt.getStatus() != 1){
                System.out.println("只有【处理中】的任务才能标记为完成！");
                return false;
            }

            int rows = mapper.updateStatus(repairId, 2);
            return rows > 0;
        }
    }

    //查个人所有维修
    public List<repairTable> getAllMyRepairsTasks(String repairUserId){
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairTableMapper mapper = session.getMapper(RepairTableMapper.class);
            return mapper.selectByRepairUserId(repairUserId);
        }
    }

    //查处理中的任务
    public List<repairTable> getProcessingMyRepairs(String repairUserId){
        return getAllMyRepairsTasks(repairUserId).stream().filter(rt -> rt.getStatus() == 1)
                .collect(Collectors.toList());
    }

    //管理员专用的无筛选全报修单
    public List<repairTable> getAllRepairTasks(){
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairTableMapper mapper = session.getMapper(RepairTableMapper.class);
            return mapper.selectAll();
        }
    }

    //管理员的按状态筛选报修单
    public List<repairTable> getRepairTaskByStatus(Integer status){
        return getAllRepairTasks().stream().filter(rt -> rt.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    //管理员专用删除报修单
    public boolean deleteRepairTask(long repairId){
        try (SqlSession session = MyBatisUtil.getSqlSession()){
            RepairTableMapper mapper = session.getMapper(RepairTableMapper.class);
            repairTable rt = mapper.selectByRepairId(repairId);
            if (rt == null){
                System.out.println("该报修单不存在");
                return false;
            }
            if (rt.getStatus() == 1 || rt.getStatus() == 2){
                System.out.println("正在处理中或已完成的报修单不能删除！");
                return false;
            }
            int rows = mapper.deleteByRepairId(repairId);
            return rows > 0;
        }
    }

    //管理员查询单号ID来看报修单
    public repairTable getRepairDetailById(Long repairId){
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairTableMapper mapper = session.getMapper(RepairTableMapper.class);
            return mapper.selectByRepairId(repairId);
        }
    }

    //分配报修单给维修人员
    public boolean assignRepairTask(Long repairId, String repairUserId,Integer status){
        try (SqlSession session = MyBatisUtil.getSqlSession()){
            RepairTableMapper mapper = session.getMapper(RepairTableMapper.class);
            repairTable rt = mapper.selectByRepairId(repairId);
            if (rt == null){
                System.out.println("该报修单不存在");
                return false;
            }
            if (rt.getStatus() != 0){
                System.out.println("只能分配待处理的报修单！");
                return false;
            }
            if (!RegEx.checkWorkerId(repairUserId)){
                System.out.println("该维修人员账号不存在或格式错误！");
                return false;
            }
            int rows = mapper.assignRepairTask(repairId, repairUserId, 1);
            return rows > 0;

        }
    }

    //学生评分
    public boolean scoreRepair(Long repairId, Integer score){
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairTableMapper mapper = session.getMapper(RepairTableMapper.class);
            int rows = mapper.updateScore(repairId, score);
            return rows > 0;
        }
    }

    //获取评分
    public List<Map<String, Object>> getRepairmanScoreStat(){
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairTableMapper mapper = session.getMapper(RepairTableMapper.class);
            return mapper.getRepairmanScoreStat();
        }
    }

    //紧急程度看报修单
    public List<Map<String, Object>> getRepairByUrgency(Integer urgency) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairTableMapper mapper = session.getMapper(RepairTableMapper.class);
            return mapper.getRepairByUrgency(urgency);
        }
    }
}
