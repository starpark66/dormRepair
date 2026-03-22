package org.example.dormrepair.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.dormrepair.pojo.studentDorm;

public interface StudentDormMapper {
    //1.根据学号查学生宿舍信息
    @Select("SELECT * FROM student_dorm WHERE student_id=#{studentId}")
    studentDorm selectByStudentId(String studentId);

    //2.插入学生宿舍信息
    @Insert("INSERT INTO student_dorm(student_id,dorm_area," +
            "dorm_building,dorm_room,student_name)"

            +"VALUES(#{studentId},#{dormArea}," +
            "#{dormBuilding},#{dormRoom},#{studentName})")
    int insert(studentDorm stuDorm);

    //3.学生是否已经绑定宿舍
    @Select("SELECT COUNT(*) FROM student_dorm WHERE student_id = #{studentId}")
        int countByStudentId(String studentId);

    //4.修改宿舍
    @Update("UPDATE student_dorm SET dorm_area=#{dormArea},dorm_building=#{dormBuilding}," +
            "dorm_room=#{dormRoom},student_name=#{studentName} WHERE student_id=#{studentId}")
    int updateByStudentId(studentDorm stuDorm);
}
