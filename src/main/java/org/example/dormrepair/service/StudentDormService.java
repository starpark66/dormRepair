package org.example.dormrepair.service;

import org.example.dormrepair.mapper.StudentDormMapper;
import org.example.dormrepair.pojo.studentDorm;
import org.example.dormrepair.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

public class StudentDormService {
    //有没有绑定宿舍
    public boolean hasDorm(String studentId){
        try(SqlSession sqlSession = MyBatisUtil.getSqlSession()){
            StudentDormMapper mapper = sqlSession.getMapper(StudentDormMapper.class);
            int count = mapper.countByStudentId(studentId);
            return count>0;
        }
    }

    //绑定
    public boolean addDorm(studentDorm studentDorm){
        try(SqlSession sqlSession = MyBatisUtil.getSqlSession()){
            StudentDormMapper mapper = sqlSession.getMapper(StudentDormMapper.class);
            int result;
            if (hasDorm(studentDorm.getStudentId())){
                result = mapper.updateByStudentId(studentDorm);
            }
            else {
                result = mapper.insert(studentDorm);
            }
            return result > 0;
        }
    }

    //修改
    public boolean updateDorm(studentDorm stuDorm){
        try(SqlSession sqlSession = MyBatisUtil.getSqlSession()){
            StudentDormMapper mapper = sqlSession.getMapper(StudentDormMapper.class);
            int rows = mapper.updateByStudentId(stuDorm);
            //开了自动提交，这里取消commit
            return rows > 0;
        }
    }
}
