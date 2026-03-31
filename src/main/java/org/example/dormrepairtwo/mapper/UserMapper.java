package org.example.dormrepairtwo.mapper;

import org.apache.ibatis.annotations.*;
import org.example.dormrepairtwo.pojo.User;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    // 根据userId查询用户（判断是否注册）
    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    User selectByUserId(String userId);

    // 根据userId和密码查询用户（登录）
    @Select("SELECT * FROM user WHERE user_id = #{userId} AND password = #{password}")
    User selectByUserIdAndPassword(@Param("userId") String userId, @Param("password") String password);

    // 插入新用户（注册）
    @Insert("INSERT INTO user(user_id, password, role) VALUES(#{userId}, #{password}, #{role})")
    int insert(User user);

    //更新密码
    @Update("UPDATE user SET password = #{newPwd} WHERE user_id = #{userId}")
    int updatePassword(@Param("userId") String userId, @Param("newPwd") String newPwd);

    //管理员查看所有维修人员
    @Select("SELECT user_id, password, role FROM user WHERE role = 2")
    List<Map<String, Object>> getAllRepairman();

    //删除维修人员
    @Delete("DELETE FROM user WHERE user_id = #{userId} AND role = 2")
    int deleteRepairman(@Param("userId") String userId);
}