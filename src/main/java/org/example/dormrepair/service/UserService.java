package org.example.dormrepair.service;

import org.example.dormrepair.mapper.UserMapper;
import org.example.dormrepair.pojo.User;
import org.example.dormrepair.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Map;

public class UserService {
    //登录
    public User login(String userId,String password){
        try(SqlSession sqlSession = MyBatisUtil.getSqlSession()){//获取数据库连接
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);//拿mapper
            return mapper.selectByUserIdAndPassword(userId,password);//让mapper到数据库里面查询
        }
    }

    //注册
    public boolean register(User user){
        try(SqlSession session = MyBatisUtil.getSqlSession()){
            UserMapper mapper = session.getMapper(UserMapper.class);
            return mapper.insert(user)>0;
        }
    }

    //验证防止重复ID
    public boolean isUserIdExist(String userId){
        try(SqlSession session = MyBatisUtil.getSqlSession()){
            UserMapper mapper = session.getMapper(UserMapper.class);
            return mapper.selectByUserId(userId)!=null;//是否不为空
        }
    }

    //改密码
    public boolean updatePassword(String userId,String password){
        try(SqlSession session = MyBatisUtil.getSqlSession()){
            UserMapper mapper = session.getMapper(UserMapper.class);
            return mapper.updatePassword(userId,password)>0;
        }
    }

    //管理员获取维修人员
    public List<Map<String, Object>> getAllRepairman() {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            return mapper.getAllRepairman();
        }
    }

    //删除维修人员
    public boolean deleteRepairman(String userId) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            int rows = mapper.deleteRepairman(userId);
            session.commit();
            return rows > 0;
        }
    }
}
