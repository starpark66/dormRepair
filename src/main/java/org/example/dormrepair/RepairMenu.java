package org.example.dormrepair;

import org.example.dormrepair.mapper.UserMapper;
import org.example.dormrepair.pojo.repairTable;
import org.example.dormrepair.service.RepairTableService;
import org.example.dormrepair.service.UserService;
import org.example.dormrepair.util.MyBatisUtil;
import org.example.dormrepair.util.RegEx;
import org.example.dormrepair.pojo.User;
import org.apache.ibatis.session.SqlSession;
import java.util.List;
import java.util.Scanner;

public class RepairMenu {
    public void enter(Scanner sc,String repairUserId) {
        System.out.println("===== 维修人员菜单 =====");
        while (true) {
            System.out.println("\n1. 查看我的当前维修任务");
            System.out.println("2. 更新报修状态");
            System.out.println("3.查询我的全部维修任务");
            System.out.println("4.修改密码");
            System.out.println("5. 退出登录");
            System.out.print("请选择操作：");

            int choice = sc.nextInt();
            sc.nextLine(); // 吸收换行

            switch (choice) {
                case 1:
                    System.out.println("=====查看我的维修任务=====");
                    RepairTableService rTService = new RepairTableService();
                    List<repairTable> myTasks = rTService.getMyRepairTasks(repairUserId);
                    if (myTasks.isEmpty()) {
                        System.out.println("暂无分配给您的维修任务！");
                        break;
                    }
                    java.time.format.DateTimeFormatter formatter =
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    for (repairTable rt: myTasks){
                        String statusStr = switch (rt.getStatus()){
                            case 0 -> "待处理";
                            case 1 -> "处理中";
                            case 2 -> "已完成";
                            case 3 -> "已取消";
                            default -> "未知状态";
                        };

                        String urgencyStr = switch (rt.getUrgency()) {
                            case 1 -> "普通";
                            case 2 -> "紧急";
                            case 3 -> "非常紧急";
                            default -> "未知紧急程度";
                        };
                        System.out.println("----------------------------------------------");
                        System.out.println("报修单ID: " + rt.getRepairId());
                        System.out.println("学生账号: " + rt.getStuUserId());
                        System.out.println("设备类型: " + rt.getDeviceType());
                        System.out.println("故障描述：" + rt.getDescription());
                        System.out.printf("紧急程度：%s | 联系电话：%s\n",urgencyStr,rt.getPhonenum());
                        System.out.println("当前状态：" + statusStr);
                        System.out.println("创建时间："+rt.getCreateTime().format(formatter));

                    }
                        System.out.println("----------------------------------------------");
                    break;
                case 2:
                    System.out.println("更新报修状态");
                    System.out.println("请输入要完成的报修ID：");
                    long repairId = sc.nextLong();
                    RepairTableService rTService2 = new RepairTableService();
                    boolean isComplete = rTService2.completeRepair(repairId,repairUserId);
                    if (isComplete){
                        System.out.println("报修ID【"+repairId+"】已被标记为【已完成】！");
                    }
                    else {
                        System.out.println("操作失败，请检查报修ID或任务状态！");
                    }
                    break;
                case 3:
                    System.out.println("\n===== 查询我的全部维修任务 =====");
                    System.out.println("1. 查看全部任务（含已完成/已取消）");
                    System.out.println("2. 仅查看处理中任务");
                    System.out.print("请选择查询类型：");
                    int queryType = sc.nextInt();
                    sc.nextLine();

                    RepairTableService rTService3 = new RepairTableService();
                    List<repairTable> taskList;

                    // 根据选择调用不同 Service 方法
                    if (queryType == 1) {
                        taskList = rTService3.getAllMyRepairsTasks(repairUserId);
                        System.out.println("\n===== 我的全部维修任务 =====");
                    } else if (queryType == 2) {
                        taskList = rTService3.getProcessingMyRepairs(repairUserId);
                        System.out.println("\n===== 我的处理中维修任务 =====");
                    } else {
                        System.out.println("输入错误，请选择 1 或 2！");
                        break;
                    }

                    // 输出任务列表
                    if (taskList.isEmpty()) {
                        System.out.println("暂无符合条件的维修任务！");
                        break;
                    }

                    java.time.format.DateTimeFormatter formatter2 = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    for (repairTable rt : taskList) {
                        String statusStr = switch (rt.getStatus()) {
                            case 0 -> "待处理";
                            case 1 -> "处理中";
                            case 2 -> "已完成";
                            case 3 -> "已取消";
                            default -> "未知状态";
                        };
                        String urgencyStr = switch (rt.getUrgency()) {
                            case 1 -> "普通";
                            case 2 -> "紧急";
                            case 3 -> "非常紧急";
                            default -> "未知紧急程度";
                        };

                        System.out.println("----------------------------------------");
                        System.out.printf("报修ID：%d%n", rt.getRepairId());
                        System.out.printf("学生账号：%s%n", rt.getStuUserId());
                        System.out.printf("设备类型：%s%n", rt.getDeviceType());
                        System.out.printf("故障描述：%s%n", rt.getDescription());
                        System.out.printf("紧急程度：%s | 联系电话：%s%n", urgencyStr, rt.getPhonenum());
                        System.out.printf("当前状态：%s%n", statusStr);
                        System.out.printf("创建时间：%s%n", rt.getCreateTime().format(formatter2));
                        if (rt.getStatus() == 2 && rt.getScore() != null) {
                            System.out.printf("学生评分：%d分%n", rt.getScore());
                        }
                        else {
                            System.out.println("学生评分：暂无评分");
                        }
                    }
                    System.out.println("----------------------------------------");
                    break;
                case  4:
                    // 修改密码
                    System.out.println("\n=====修改密码=====");
                    System.out.println("请输入当前旧密码：");
                    String oldPwd = sc.nextLine();
                    try (SqlSession session = MyBatisUtil.getSqlSession()){
                        UserMapper mapper = session.getMapper(UserMapper.class);
                        User user = mapper.selectByUserId(repairUserId);
                        if (!user.getPassword().equals(oldPwd)) {
                            System.out.println("原密码错误！修改终止！");
                            break;
                        }
                    }
                    System.out.println("请输入新密码：");
                    String newPwd = sc.nextLine();
                    System.out.println("请再次输入新密码：");
                    String reNewPwd = sc.nextLine();
                    if (!RegEx.checkPassword(newPwd)){
                        System.out.println("密码需要在6~16位之间！");
                        break;
                    }
                    if (!newPwd.equals(reNewPwd)) {
                        System.out.println("两次输入的新密码不一致！修改终止！");
                        break;
                    }
                    if (oldPwd.equals(newPwd)) {
                        System.out.println("新密码不能与原密码相同！");
                    }
                    boolean isUpdateSuccess = new UserService().updatePassword(repairUserId, newPwd);
                    if (isUpdateSuccess) {
                        System.out.println("密码修改成功！请重新登录！");
                        return;
                    }
                    else {
                        System.out.println("密码修改失败！");
                    }
                    break;
                case 5:
                    System.out.println("已退出维修人员菜单");
                    return;
                default:
                    System.out.println("输入错误，请重新选择");
            }
        }
    }
}