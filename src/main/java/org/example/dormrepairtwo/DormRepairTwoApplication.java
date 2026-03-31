package org.example.dormrepairtwo;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "org.example.dormrepairtwo.mapper")
public class DormRepairTwoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DormRepairTwoApplication.class, args);
    }

}
