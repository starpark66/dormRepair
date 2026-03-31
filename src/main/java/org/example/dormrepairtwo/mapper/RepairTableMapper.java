package org.example.dormrepairtwo.mapper;

import org.apache.ibatis.annotations.*;
import org.example.dormrepairtwo.pojo.RepairTable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
public interface RepairTableMapper {
    // 插入报修单
    @Insert("INSERT INTO repair_table(stu_user_id, device_type, description," +
            " urgency, phonenum, status, repair_user_id, score) " +

            "VALUES(#{stuUserId}, #{deviceType}, #{description}," +
            " #{urgency}, #{phonenum}, #{status}, #{repairUserId}, #{score})")
    @Options(useGeneratedKeys = true, keyProperty = "repairId")
    int insert(RepairTable repairTable);

    // 根据学生学号查询他的所有报修单
    @Select("SELECT * FROM repair_table WHERE stu_user_id = #{userId}")
    List<RepairTable> selectByUserId(String userId);

    // 根据维修人员工号查询他的所有工单
    @Select("SELECT * FROM repair_table WHERE repair_user_id = #{repairUserId}")
    List<RepairTable> selectByRepairUserId(String repairUserId);

    // 根据报修单号查询单条记录
    @Select("SELECT * FROM repair_table WHERE repair_id = #{repairId}")
    RepairTable selectByRepairId(Long repairId);

    // 更新报修单状态（0待处理  1处理中  2已完成  3已取消）
    @Update("UPDATE repair_table SET status = #{status} WHERE repair_id = #{repairId}")
    int updateStatus(@Param("repairId") Long repairId, @Param("status") Integer status);

    // 学生给维修单评分
    @Update("UPDATE repair_table SET score = #{score} WHERE repair_id = #{repairId}")
    int updateScore(@Param("repairId") Long repairId, @Param("score") Integer score);

    // 查询所有报修单（管理员用）
    @Select("SELECT * FROM repair_table")
    List<RepairTable> selectAll();

    // 根据报修ID删除记录
    @Delete("DELETE FROM repair_table WHERE repair_id=#{repairId}")
    int deleteByRepairId(Long repairId);

    //分配维修任务
    @Update("UPDATE repair_table SET repair_user_id = #{repairUserId},status = #{status} WHERE repair_id = #{repairId}")
    int assignRepairTask(@Param("repairId") Long repairId, @Param("repairUserId") String repairUserId,
                         @Param("status") Integer status);

    //查看评分  管理员端
    @Select("SELECT repair_user_id ," +
            "AVG(score) as avg_score," +
            "COUNT(*) as count " +
            " FROM repair_table " +
            "WHERE status = 2 AND score IS NOT NULL " +
            " GROUP BY repair_user_id ORDER BY avg_score DESC")
    List<Map<String,Object>> getRepairmanScoreStat();

    // 按紧急程度查询报修单（1=普通，2=紧急，3=非常紧急）
    @Select("SELECT * FROM repair_table WHERE urgency = #{urgency} ORDER BY create_time DESC")
    List<Map<String, Object>> getRepairByUrgency(@Param("urgency") Integer urgency);

    @Select("SELECT * FROM repair_table WHERE status = #{status}")
    List<RepairTable> selectByStatus(@Param("status") Integer status);

    @Select("SELECT * FROM repair_table WHERE repair_user_id = #{repairUserId} AND status = #{status}")
    List<RepairTable> selectByRepairUserIdAndStatus(@Param("repairUserId") String repairUserId, @Param("status") Integer status);
}
