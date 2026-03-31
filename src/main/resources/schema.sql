CREATE DATABASE `dormrepair` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */

CREATE TABLE `repair_table` (
                                `repair_id` bigint NOT NULL AUTO_INCREMENT COMMENT '报修单号',
                                `stu_user_id` varchar(20) NOT NULL COMMENT '学生学号',
                                `device_type` varchar(20) NOT NULL COMMENT '维修类型',
                                `description` text NOT NULL COMMENT '问题描述',
                                `urgency` tinyint NOT NULL DEFAULT '1' COMMENT '紧急程度：1普通 2紧急 3非常紧急',
                                `phonenum` varchar(20) NOT NULL COMMENT '学生的电话号码',
                                `status` tinyint NOT NULL DEFAULT '0' COMMENT '0待处理 1处理中 2已完成 3已取消',
                                `repair_user_id` varchar(20) DEFAULT NULL COMMENT '维修人员工号',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建表时的时间',
                                `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                `score` tinyint DEFAULT NULL COMMENT '维修评分：1-5分',
                                PRIMARY KEY (`repair_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='报修单'

CREATE TABLE `student_dorm` (
                                `student_id` varchar(10) NOT NULL COMMENT '学生的学号',
                                `dorm_area` varchar(10) NOT NULL COMMENT '所属区域',
                                `dorm_building` varchar(10) NOT NULL COMMENT '楼栋',
                                `dorm_room` varchar(10) NOT NULL COMMENT '学生房间号',
                                `student_name` varchar(10) NOT NULL COMMENT '学生名字'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生宿舍信息'

CREATE TABLE `user` (
                        `user_id` varchar(20) NOT NULL COMMENT '用户学号或工号',
                        `password` varchar(100) NOT NULL COMMENT '密码',
                        `role` tinyint NOT NULL COMMENT '1=学生，2=维修人员，3=管理者',
                        PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户'

