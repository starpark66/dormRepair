package org.example.dormrepairtwo.service.impl;

import org.example.dormrepairtwo.mapper.StudentDormMapper;
import org.example.dormrepairtwo.pojo.StudentDorm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentDormService {

    //自动注入
    @Autowired
    private StudentDormMapper studentDormMapper;

    // 判断学生是否已经绑定宿舍
    public boolean hasDorm(String studentId) {
        int count = studentDormMapper.countByStudentId(studentId);
        return count > 0;
    }

    // 绑定/更新宿舍（有则更新，无则插入）
    // 事务管理：确保操作的原子性，避免数据不一致（要么全成功，要么全失败）
    @Transactional
    public boolean addDorm(StudentDorm studentDorm) {
        if (hasDorm(studentDorm.getStudentId())) {
            // 已绑定 → 更新
            return studentDormMapper.updateByStudentId(studentDorm) > 0;
        } else {
            // 未绑定 → 插入
            return studentDormMapper.insert(studentDorm) > 0;
        }
    }

    // 单独修改宿舍信息
    @Transactional
    public boolean updateDorm(StudentDorm stuDorm) {
        return studentDormMapper.updateByStudentId(stuDorm) > 0;
    }

    // 根据学生ID查询宿舍信息
    @Transactional
    public StudentDorm getStudentDormByStudentId(String studentId) {
        return studentDormMapper.selectByStudentId(studentId);
    }
}