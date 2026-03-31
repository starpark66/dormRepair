package org.example.dormrepairtwo.controller;

import jakarta.annotation.Resource;
import org.example.dormrepairtwo.exception.BusinessException;
import org.example.dormrepairtwo.pojo.StudentDorm;
import org.example.dormrepairtwo.service.impl.StudentDormService;
import org.example.dormrepairtwo.util.RegEx;
import org.example.dormrepairtwo.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/studentDorm")
public class StudentDormController {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(StudentDormController.class);

    @Resource
    private StudentDormService studentDormService;

    // 添加@PathVariable接收路径参数 + 先校验格式再查询
    @GetMapping("/info/{studentId}")
    public Result<StudentDorm> getStudentDormInfo(@PathVariable String studentId) {
        // 先校验学号格式（原逻辑：先查询再校验，无效查询）
        if (!RegEx.checkStudentId(studentId)) {
            logger.warn("学生ID格式错误：{}", studentId);
            throw new BusinessException(400, "学生ID格式不正确（需以3125/3225开头，后接6位数字）");
        }

        StudentDorm studentDorm = studentDormService.getStudentDormByStudentId(studentId);
        if (studentDorm != null) {
            return Result.success("查询成功", studentDorm);
        } else {
            throw new BusinessException(404, "未找到学生宿舍信息");
        }
    }

    //POST请求 + 校验字段修正 + 参数非空校验
    @PostMapping("/bind")
    public Result<?> bindStudentDorm(@RequestBody StudentDorm studentDorm) {
        // 非空校验
        if (studentDorm.getStudentId() == null || studentDorm.getDormRoom() == null) {
            throw new BusinessException(400, "学生ID和宿舍房号不能为空");
        }
        // 学号格式校验
        if (!RegEx.checkStudentId(studentDorm.getStudentId())) {
            throw new BusinessException(400, "学生ID格式不正确（需以3125/3225开头，后接6位数字）");
        }
        if (!RegEx.checkRoomNumber(studentDorm.getDormRoom())) {
            throw new BusinessException(400, "宿舍房号格式不正确（需为3位数字，如131、729）");
        }

        boolean success = studentDormService.addDorm(studentDorm);
        if (success) {
            return Result.success("绑定/修改成功", null);
        } else {
            throw new BusinessException(500, "绑定/修改失败");
        }
    }
}