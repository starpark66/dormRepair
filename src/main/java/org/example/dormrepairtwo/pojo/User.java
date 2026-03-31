package org.example.dormrepairtwo.pojo;

import lombok.Data;

@Data  // 自动生成get、set、toString、构造器
public class User {
    private String userId;
    private String password;
    private Integer role;  // 1=学生 2=管理员
}