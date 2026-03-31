package org.example.dormrepairtwo.controller;

import org.example.dormrepairtwo.exception.BusinessException;
import org.example.dormrepairtwo.util.BCryptUtil;
import org.example.dormrepairtwo.util.JwtUtil;
import org.springframework.web.bind.annotation.*;
import org.example.dormrepairtwo.pojo.User;
import org.example.dormrepairtwo.util.Result;
import org.example.dormrepairtwo.service.impl.UserService;
import org.example.dormrepairtwo.util.RegEx;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // 添加账号/密码格式校验 + 异常抛出（替代直接返回fail）
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {

        // 非空校验
        if (user.getUserId() == null || user.getPassword() == null || user.getRole() == null) {
            throw new BusinessException(400, "账号、密码、角色不能为空");
        }
        // 账号格式校验（学生/维修人员ID）
        if (!RegEx.checkUserId(user.getUserId())) {
            throw new BusinessException(400, "账号格式错误（学生：3125/3225开头；维修人员：0025开头，后接6位数字）");
        }
        // 密码格式校验（6-16位字母/数字/下划线）
        if (!RegEx.checkPassword(user.getPassword())) {
            throw new BusinessException(400, "密码格式错误（需6-16位字母、数字、下划线）");
        }
        // 角色校验（1=学生 2=维修人员）
        if (user.getRole() != 1 && user.getRole() != 2) {
            throw new BusinessException(400, "角色只能是1（学生）或2（维修人员）");
        }

        // 密码加密(4.1  13:21增加）
        String rawPassword = user.getPassword();
        String encodedPassword = BCryptUtil.encode(rawPassword);
        user.setPassword(encodedPassword);
        if (userService.isUserIdExist(user.getUserId())) {
            throw new BusinessException(400, "账号已存在，请勿重复注册");
        }

        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功", null);
        } else {
            throw new BusinessException(500, "注册失败");
        }
    }

    @PostMapping("/login")
    public Result<Object> login(@RequestBody User user) {
        // 非空校验
        if (user.getUserId() == null || user.getPassword() == null) {
            throw new BusinessException(400, "账号和密码不能为空");
        }
        User loginUser = userService.login(user.getUserId(), user.getPassword());
        //Map<String, Object> claims = new HashMap<>();
        //claims.put("userId", loginUser.getUserId());
        if (loginUser == null) {
            throw new BusinessException(401, "账号或密码错误");
        }
        String token = jwtUtil.generateToken(loginUser);
        return Result.success("登录成功", token);
    }

    @PutMapping("/updatePwd")
    public Result<Object> updatePassword(@RequestParam String userId, @RequestParam String newPassword) {
        // 密码格式校验
        if (!RegEx.checkPassword(newPassword)) {
            throw new BusinessException(400, "密码格式错误（需6-16位字母、数字、下划线）");
        }
        boolean success = userService.updatePassword(userId, newPassword);
        if (success) {
            return Result.success("密码更新成功", null); // 修复：移除new Object()
        } else {
            throw new BusinessException(500, "密码更新失败");
        }
    }

    @GetMapping("/repairman/list")
    public Result<List<Map<String, Object>>> getAllRepairmen() {
        List<Map<String, Object>> repairmen = userService.getAllRepairman();
        return Result.success("获取维修人员成功", repairmen);
    }

    @DeleteMapping("/repairman/delete")
    public Result<Object> deleteRepairman(@RequestParam String userId) {
        // 校验维修人员ID格式
        if (!RegEx.checkWorkerId(userId)) {
            throw new BusinessException(400, "维修人员ID格式错误（0025开头+6位数字）");
        }
        boolean success = userService.deleteRepairman(userId);
        if (success) {
            return Result.success("删除维修人员成功", null);
        } else {
            throw new BusinessException(400, "删除维修人员失败");
        }
    }
}