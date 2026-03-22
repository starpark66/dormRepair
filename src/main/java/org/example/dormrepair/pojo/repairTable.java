package org.example.dormrepair.pojo;

import java.security.Principal;
import java.time.LocalDateTime;

public class repairTable {
    private Long repairId;
    private String stuUserId;
    private String deviceType;
    private String description;//问题描述
    private Integer urgency;
    private String phonenum;
    private Integer status;
    private String repairUserId;//维修人员工号
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer score;

    public repairTable() {
    }

    public repairTable(Long repairId, String stuUserId, String deviceType, String description, Integer urgency, String phonenum, Integer status, String repairUserId, LocalDateTime createTime, LocalDateTime updateTime, Integer score) {
        this.repairId = repairId;
        this.stuUserId = stuUserId;
        this.deviceType = deviceType;
        this.description = description;
        this.urgency = urgency;
        this.phonenum = phonenum;
        this.status = status;
        this.repairUserId = repairUserId;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.score = score;
    }

    public Long getRepairId() {
        return repairId;
    }

    public void setRepairId(Long repairId) {
        this.repairId = repairId;
    }

    public String getStuUserId() {
        return stuUserId;
    }

    public void setStuUserId(String stuUserId) {
        this.stuUserId = stuUserId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUrgency() {
        return urgency;
    }

    public void setUrgency(Integer urgency) {
        this.urgency = urgency;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRepairUserId() {
        return repairUserId;
    }

    public void setRepairUserId(String repairUserId) {
        this.repairUserId = repairUserId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
