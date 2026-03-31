package org.example.dormrepairtwo.service.impl;

import org.example.dormrepairtwo.exception.BusinessException;
import org.example.dormrepairtwo.mapper.UserMapper;
import org.example.dormrepairtwo.pojo.User;
import org.example.dormrepairtwo.util.BCryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
public class UserService {
    //日志实例
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    // 登录
    public User login(String userId, String password) {
        User user = userMapper.selectByUserId(userId);
        if (user == null) {
            logger.warn("用户{}登录失败：用户不存在", userId);
            throw new BusinessException(500, "用户不存在");
        }
        /*
        if (!user.getPassword().equals(password)) {
            logger.warn("用户{}登录失败：密码错误", userId);
            throw new BusinessException(500,"密码错误");
        }
        4.1 13:25增加密码加密验证
         */
        if (!BCryptUtil.matches(password, user.getPassword())) {
            logger.warn("用户{}登录失败：密码错误", userId);
            throw new BusinessException(500, "密码错误");
        }
        logger.info("用户{}登录成功", userId);
        return user;
    }

    // 注册
    public boolean register(User user) {
        boolean result = userMapper.insert(user) > 0;
        if (result) {
            logger.info("用户{}注册成功", user.getUserId());
        } else {
            logger.warn("用户{}注册失败", user.getUserId());
        }
        return result;
    }

    // 验证用户ID是否存在
    public boolean isUserIdExist(String userId) {
        return userMapper.selectByUserId(userId) != null;
    }

    // 修改密码
    public boolean updatePassword(String userId, String password) {
        boolean result = userMapper.updatePassword(userId, password) > 0;
        if (result) {
            logger.info("用户{}密码修改成功", userId);
        } else {
            logger.warn("用户{}密码修改失败", userId);
        }
        return result;
    }

    // 管理员获取所有维修人员
    public List<Map<String, Object>> getAllRepairman() {
        return userMapper.getAllRepairman();
    }

    // 删除维修人员
    public boolean deleteRepairman(String userId) {
        boolean result = userMapper.deleteRepairman(userId) > 0;
        if (result) {
            logger.info("管理员删除维修人员{}成功", userId);
        } else {
            logger.warn("管理员删除维修人员{}失败", userId);
        }
        return result;
    }
}