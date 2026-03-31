package org.example.dormrepairtwo.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RepairTable {
    private Long repairId;
    private String stuUserId;
    private String deviceType;
    private String description;//问题描述
    private Integer urgency;
    private String phonenum;
    private Integer status;
    private String repairUserId;//维修人员工号
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    private Integer score;
}
