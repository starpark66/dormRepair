package org.example.dormrepairtwo.controller;

import org.example.dormrepairtwo.exception.BusinessException;
import org.example.dormrepairtwo.mapper.StudentDormMapper;
import org.example.dormrepairtwo.pojo.RepairTable;
import org.example.dormrepairtwo.pojo.StudentDorm;
import org.example.dormrepairtwo.service.impl.RepairTableService;
import org.example.dormrepairtwo.util.RegEx;
import org.example.dormrepairtwo.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/repairs")
public class RepairTableController {
    private static final Logger logger = LoggerFactory.getLogger(RepairTableController.class);

    @Autowired
    private RepairTableService repairTableService;

    @Autowired
    private StudentDormMapper studentDormMapper;

    @PostMapping("/submit")
    public Result<Object> submitRepairRequest(@RequestBody RepairTable rt) {
        if (rt.getStuUserId() == null || rt.getPhonenum() == null || rt.getUrgency() == null) {
            throw new BusinessException(400, "学生ID、手机号、紧急程度不能为空");
        }
        if (!RegEx.checkStudentId(rt.getStuUserId())) {
            throw new BusinessException(400, "学生ID格式错误");
        }
        if (!RegEx.checkPhone(rt.getPhonenum())) {
            throw new BusinessException(400, "手机号格式错误（需为11位有效手机号）");
        }
        if (rt.getUrgency() < 1 || rt.getUrgency() > 3) {
            throw new BusinessException(400, "紧急程度只能是1（普通）、2（紧急）、3（非常紧急）");
        }

        boolean success = repairTableService.submitRepair(rt);
        if (success) {
            return Result.success("报修单提交成功", null);
        } else {
            throw new BusinessException(400, "请先绑定宿舍，再提交报修单");
        }
    }

    @GetMapping("/my/{stuUserId}")
    public Result<List<RepairTable>> getMyRepairs(@PathVariable String stuUserId) {
        if (stuUserId == null || !RegEx.checkStudentId(stuUserId)) {
            throw new BusinessException(400, "学号不能为空且格式需正确");
        }
        List<RepairTable> repairs = repairTableService.getMyRepairs(stuUserId);
        return Result.success("查询成功", repairs);
    }

    @PostMapping("/cancel/{repairId}")
    public Result<Object> cancelRepair(@PathVariable long repairId, @RequestParam String stuUserId) {
        if (stuUserId == null || !RegEx.checkStudentId(stuUserId)) {
            throw new BusinessException(400, "学号不能为空且格式需正确");
        }
        boolean success = repairTableService.cancelRepair(repairId, stuUserId);
        if (success) {
            return Result.success("报修单取消成功", null);
        } else {
            throw new BusinessException(403, "报修单取消失败（无权限/非待处理状态）");
        }
    }

    @GetMapping("/tasks/{repairUserId}")
    public Result<List<RepairTable>> getMyRepairTasks(@PathVariable String repairUserId) {
        if (repairUserId == null || !RegEx.checkWorkerId(repairUserId)) {
            throw new BusinessException(400, "维修人员ID不能为空且格式需正确（0025开头+6位数字）");
        }
        List<RepairTable> tasks = repairTableService.getMyRepairTasks(repairUserId);
        return Result.success("查询成功", tasks);
    }

    @GetMapping("/tasks/processing/{repairUserId}")
    public Result<List<RepairTable>> getMyProcessingTasks(@PathVariable String repairUserId) {
        if (repairUserId == null || !RegEx.checkWorkerId(repairUserId)) {
            throw new BusinessException(400, "维修人员ID不能为空且格式需正确");
        }
        List<RepairTable> tasks = repairTableService.getProcessingMyRepairs(repairUserId);
        return Result.success("查询成功", tasks);
    }

    @PostMapping("/complete/{repairId}")
    public Result<Object> completeRepair(@PathVariable Long repairId, @RequestParam String repairUserId) {
        if (repairUserId == null || !RegEx.checkWorkerId(repairUserId)) {
            throw new BusinessException(400, "维修人员ID不能为空且格式需正确");
        }
        boolean success = repairTableService.completeRepair(repairId, repairUserId);
        if (success) {
            return Result.success("报修单已标记为已完成", null);
        } else {
            throw new BusinessException(403, "操作失败（无权限/非处理中状态）");
        }
    }

    // ======================= 【管理员列表：已加宿舍】 =======================
    @GetMapping("/all")
    public Result<List<Map<String, Object>>> getAllRepairsTasks() {
        List<RepairTable> tasks = repairTableService.getAllRepairTasks();

        List<Map<String, Object>> result = tasks.stream().map(repair -> {
            Map<String, Object> map = new HashMap<>();
            map.put("repairId", repair.getRepairId());
            map.put("stuUserId", repair.getStuUserId());
            map.put("phonenum", repair.getPhonenum());
            map.put("deviceType", repair.getDeviceType());
            map.put("description", repair.getDescription());
            map.put("urgency", repair.getUrgency());
            map.put("status", repair.getStatus());
            map.put("repairUserId", repair.getRepairUserId());
            map.put("score", repair.getScore());
            map.put("createTime", repair.getCreateTime());
            map.put("updateTime", repair.getUpdateTime());

            // 查询宿舍
            StudentDorm dorm = studentDormMapper.selectByStudentId(repair.getStuUserId());
            String dormStr = "未绑定宿舍";
            if (dorm != null) {
                dormStr = dorm.getDormArea() + " " + dorm.getDormBuilding() + " " + dorm.getDormRoom();
            }
            map.put("dorm", dormStr);

            return map;
        }).collect(Collectors.toList());

        return Result.success("查询成功", result);
    }

    @GetMapping("/status/{status}")
    public Result<List<RepairTable>> getRepairsByStatus(@PathVariable int status) {
        if (status < 0 || status > 3) {
            throw new BusinessException(400, "状态值只能是0（待处理）、1（处理中）、2（已完成）、3（已取消）");
        }
        List<RepairTable> tasks = repairTableService.getRepairTaskByStatus(status);
        return Result.success("查询成功", tasks);
    }

    @PostMapping("/assign/{repairId}")
    public Result<Object> assignRepair(@PathVariable long repairId, @RequestParam String repairUserId) {
        if (repairUserId == null || !RegEx.checkWorkerId(repairUserId)) {
            throw new BusinessException(400, "维修人员ID不能为空且格式需正确（0025开头+6位数字）");
        }
        boolean success = repairTableService.assignRepairTask(repairId, repairUserId, 1);
        if (success) {
            return Result.success("报修单已分配给维修人员", null);
        } else {
            throw new BusinessException(403, "操作失败（报修单不存在/非待处理状态）");
        }
    }

    @PostMapping("/admin/cancel/{repairId}")
    public Result<Object> adminCancelRepair(@PathVariable long repairId) {
        boolean success = repairTableService.deleteRepairTask(repairId);
        if (success) {
            return Result.success("报修单已被管理员取消", null);
        } else {
            throw new BusinessException(403, "操作失败（报修单不存在/已处理/已完成）");
        }
    }

    // ======================= 【详情页：也加宿舍】 =======================
    @GetMapping("/detail/{repairId}")
    public Result<Map<String, Object>> getRepairDetail(@PathVariable long repairId) {
        RepairTable repair = repairTableService.getRepairDetailById(repairId);
        if (repair == null) {
            throw new BusinessException(404, "报修单不存在");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("repairId", repair.getRepairId());
        map.put("stuUserId", repair.getStuUserId());
        map.put("phonenum", repair.getPhonenum());
        map.put("deviceType", repair.getDeviceType());
        map.put("description", repair.getDescription());
        map.put("urgency", repair.getUrgency());
        map.put("status", repair.getStatus());
        map.put("repairUserId", repair.getRepairUserId());
        map.put("score", repair.getScore());
        map.put("createTime", repair.getCreateTime());
        map.put("updateTime", repair.getUpdateTime());

        // 查询宿舍
        StudentDorm dorm = studentDormMapper.selectByStudentId(repair.getStuUserId());
        String dormStr = "未绑定宿舍";
        if (dorm != null) {
            dormStr = dorm.getDormArea() + " " + dorm.getDormBuilding() + " " + dorm.getDormRoom();
        }
        map.put("dorm", dormStr);

        return Result.success("查询成功", map);
    }

    // 学生评分（只能评分自己的报修单，且只能评分已完成的报修单，且只能评分一次）
    @PostMapping("/score/{repairId}")
    public Result<Object> scoreRepair(@PathVariable long repairId, @RequestParam int score, @RequestParam String stuUserId) {
        if (score < 1 || score > 5) {
            throw new BusinessException(400, "评分必须在1到5之间");
        }
        boolean success = repairTableService.scoreRepair(repairId, score, stuUserId);
        if (success) {
            return Result.success("评分成功", null);
        } else {
            throw new BusinessException(500, "评分失败（报修单不存在/非已完成状态）");
        }
    }

    // 统计维修人员平均评分
    @GetMapping("/scores")
    public Result<List<Map<String, Object>>> getRepairmanScoreStat() {
        List<Map<String, Object>> stat = repairTableService.getRepairmanScoreStat();
        return Result.success("查询成功", stat);
    }

    // 按紧急程度查询报修单（1=普通，2=紧急，3=非常紧急）
    @GetMapping("/urgency/{urgency}")
    public Result<List<Map<String, Object>>> getRepairByUrgency(@PathVariable int urgency) {
        if (urgency < 1 || urgency > 3) {
            throw new BusinessException(400, "紧急程度必须在1（普通）到3（非常紧急）之间");
        }
        List<Map<String, Object>> list = repairTableService.getRepairByUrgency(urgency);
        return Result.success("查询成功", list);
    }
}