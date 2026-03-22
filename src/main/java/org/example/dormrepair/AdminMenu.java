package org.example.dormrepair;

import org.apache.ibatis.session.SqlSession;
import org.example.dormrepair.mapper.UserMapper;
import org.example.dormrepair.pojo.User;
import org.example.dormrepair.pojo.repairTable;
import org.example.dormrepair.service.RepairTableService;
import org.example.dormrepair.service.UserService;
import org.example.dormrepair.util.MyBatisUtil;
import org.example.dormrepair.util.RegEx;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AdminMenu {
    public void enter(Scanner sc) {
        System.out.println("===== 管理员菜单 =====");
        while (true) {
            System.out.println("\n1. 查看所有报修单（可分类查看）");
            System.out.println("2. 删除报修单");
            System.out.println("3. 查看报修单详情");
            System.out.println("4. 分配报修任务(分配后自动变成状态变成处理中)");
            System.out.println("5. 查看维修人员评分情况(降序)");
            System.out.println("6. 查看所有维修人员");
            System.out.println("7. 修改密码");
            System.out.println("8. 根据紧急情况查看报修单");
            System.out.println("9. 删除维修人员");
            System.out.println("10. 退出登录");
            System.out.print("请选择操作：");

            int choice = sc.nextInt();
            sc.nextLine(); // 吸收换行

            switch (choice) {
                case 1:
                    System.out.println("\n===== 查看所有报修单 =====");
                    System.out.println("1. 查看全部报修单");
                    System.out.println("2. 仅查看待处理报修单");
                    System.out.println("3. 仅查看处理中报修单");
                    System.out.println("4. 仅查看已完成报修单");
                    System.out.println("5. 仅查看已取消报修单");
                    System.out.print("请选择查询类型：");
                    int queryType = sc.nextInt();
                    sc.nextLine(); // 吸收换行

                    RepairTableService rtService = new RepairTableService();
                    List<repairTable> taskList;

                    switch (queryType) {
                        case 1:
                            taskList = rtService.getAllRepairTasks();
                            System.out.println("\n===== 全部报修单 =====");
                            break;
                        case 2:
                            taskList = rtService.getRepairTaskByStatus(0);
                            System.out.println("\n===== 待处理报修单 =====");
                            break;
                        case 3:
                            taskList = rtService.getRepairTaskByStatus(1);
                            System.out.println("\n===== 处理中报修单 =====");
                            break;
                        case 4:
                            taskList = rtService.getRepairTaskByStatus(2);
                            System.out.println("\n===== 已完成报修单 =====");
                            break;
                        case 5:
                            taskList = rtService.getRepairTaskByStatus(3);
                            System.out.println("\n===== 已取消报修单 =====");
                            break;
                        default:
                            System.out.println("输入错误，请选择 1~5！");
                            return;
                    }

                    if (taskList.isEmpty()) {
                        System.out.println("暂无符合条件的报修单！");
                        break;
                    }

                    java.time.format.DateTimeFormatter formatter =
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
                        System.out.printf("创建时间：%s%n", rt.getCreateTime().format(formatter));
                        if (rt.getRepairUserId() != null) {
                            System.out.printf("维修人员账号：%s%n", rt.getRepairUserId());
                        }
                        if (rt.getStatus() == 2 && rt.getScore() != null) {
                            System.out.printf("学生评分：%d分%n", rt.getScore());
                        }
                    }
                    System.out.println("----------------------------------------");
                    break;
                case 2:
                    System.out.println("\n===== 删除报修单 =====");
                    System.out.print("请输入要删除的报修ID：");
                    long repairId = sc.nextLong();
                    sc.nextLine();

                    // 二次确认，防止误删
                    System.out.print("确认删除该报修单？（输入 y 确认）：");
                    String confirm = sc.nextLine();
                    if (!confirm.equalsIgnoreCase("y")) {
                        System.out.println("已取消删除操作");
                        break;
                    }

                    RepairTableService rtService2 = new RepairTableService();
                    boolean isDelete = rtService2.deleteRepairTask(repairId);

                    if (isDelete) {
                        System.out.println("报修ID【" + repairId + "】删除成功！");
                    } else {
                        System.out.println("删除失败，请检查报修ID或状态！");
                    }
                    break;
                case 3:
                    System.out.println("\n===== 查看报修单详情 =====");
                    System.out.print("请输入要查询的报修ID：");
                    Long repairId2 = sc.nextLong();
                    sc.nextLine(); // 吸收换行

                    RepairTableService rtService3 = new RepairTableService();
                    repairTable rt = rtService3.getRepairDetailById(repairId2);

                    if (rt == null) {
                        System.out.println("该报修单不存在！");
                        break;
                    }

                    java.time.format.DateTimeFormatter formatter2 =
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
                    System.out.println("【报修单详情】");
                    System.out.printf("报修ID：%d%n", rt.getRepairId());
                    System.out.printf("学生账号：%s%n", rt.getStuUserId());
                    System.out.printf("设备类型：%s%n", rt.getDeviceType());
                    System.out.printf("故障描述：%s%n", rt.getDescription());
                    System.out.printf("紧急程度：%s%n", urgencyStr);
                    System.out.printf("联系电话：%s%n", rt.getPhonenum());
                    System.out.printf("当前状态：%s%n", statusStr);
                    System.out.printf("创建时间：%s%n", rt.getCreateTime().format(formatter2));
                    if (rt.getRepairUserId() != null) {
                        System.out.printf("维修人员账号：%s%n", rt.getRepairUserId());
                    }
                    if (rt.getStatus() == 2 && rt.getScore() != null) {
                        System.out.printf("学生评分：%d分%n", rt.getScore());
                    }
                    else {
                        System.out.println("学生评分：暂无评分");
                    }
                    System.out.println("----------------------------------------");
                    break;
                case 4:
                    System.out.println("\n===== 分配报修任务 =====");
                    System.out.print("请输入要分配的报修ID：");
                    Long repairId3 = sc.nextLong();
                    sc.nextLine();

                    System.out.print("请输入接收任务的维修人员账号：");
                    String repairUserId = sc.nextLine();

                    // 二次确认
                    System.out.print("确认分配该报修单给维修人员【" + repairUserId + "】？（输入 y 确认）：");
                    String confirm2 = sc.nextLine();
                    if (!confirm2.equalsIgnoreCase("y")) {
                        System.out.println("已取消分配操作");
                        break;
                    }

                    RepairTableService rtService4 = new RepairTableService();
                    boolean isAssign = rtService4.assignRepairTask(repairId3, repairUserId,1);

                    if (isAssign) {
                        System.out.println("报修ID【" + repairId3 + "】已成功分配给维修人员【" + repairUserId + "】，状态变更为【处理中】！");
                    } else {
                        System.out.println("分配失败，请检查报修ID或维修人员账号！");
                    }
                    break;
                case 5:
                    System.out.println("查看维修人员评分情况");
                    RepairTableService rtService5 = new RepairTableService();
                    List<Map<String,Object>> statList = rtService5.getRepairmanScoreStat();

                    if (statList.isEmpty()) {
                        System.out.println("暂无评分数据！");
                        break;
                    }
                    int rank = 1;
                    for (Map<String,Object> stat : statList) {
                        String repairUserId2 = (String) stat.get("repair_user_id");
                        //Double avgScoreObj = (Double) stat.get("avg_score");
                        BigDecimal avgScoreBig = (BigDecimal) stat.get("avg_score");
                        double avgScore= (avgScoreBig != null) ? avgScoreBig.doubleValue():0.0;
                        long count = (Long) stat.get("count");
                        System.out.printf("%d.维修工号：%s | 平均评分：%.2f分 | 评分次数：%d次%n", rank++, repairUserId2, avgScore, count);
                    }

                    break;

                case 6:
                    System.out.println("===== 所有维修人员 =====");
                    UserService userService = new UserService();
                    List<Map<String, Object>> repairmanList = userService.getAllRepairman();

                    if (repairmanList.isEmpty()) {
                        System.out.println("暂无维修人员");
                        break;
                    }

                    int rank2 = 1;
                    for (Map<String, Object> map : repairmanList) {
                        String userId = (String) map.get("user_id");
                        System.out.printf("%d.工号：%s%n",rank2++, userId);
                    }
                    break;

                case 7:
                    System.out.println("===== 修改管理员密码 =====");
                    System.out.println("\n=====修改密码=====");
                    System.out.println("请输入当前旧密码：");
                    String oldPwd = sc.nextLine();
                    try (SqlSession session = MyBatisUtil.getSqlSession()){
                        UserMapper mapper = session.getMapper(UserMapper.class);
                        User user = mapper.selectByUserId("0025000001");
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
                        break;
                    }
                    boolean isUpdateSuccess = new UserService().updatePassword("0025000001", newPwd);
                    if (isUpdateSuccess) {
                        System.out.println("密码修改成功！请重新登录！");
                        return;
                    }
                    else {
                        System.out.println("密码修改失败！");
                    }
                    break;

                case 8:
                    System.out.println("===== 按紧急程度查看报修单 =====");
                    System.out.println("1. 普通");
                    System.out.println("2. 紧急");
                    System.out.println("3. 非常紧急");
                    System.out.print("请选择紧急程度：");
                    int urgency = Integer.parseInt(sc.nextLine());

                    RepairTableService rtService6 = new RepairTableService();
                    List<Map<String, Object>> list = rtService6.getRepairByUrgency(urgency);

                    if (list.isEmpty()) {
                        System.out.println("暂无该类报修单");
                        break;
                    }

                    for (Map<String, Object> map : list) {
                        System.out.printf("报修ID：%s | 学生ID：%s | 紧急程度：%s | 当前状态：%s | 报修时间：%s%n",
                                map.get("repair_id"),
                                map.get("stu_user_id"),
                                getUrgencyText((Integer) map.get("urgency")), // 转成文字
                                getStatusText((Integer) map.get("status")),
                                map.get("create_time"));
                    }
                    break;

                case 9:
                    System.out.println("===== 删除维修人员 =====");
                    System.out.print("请输入要删除的维修工号：");
                    String userId = sc.nextLine();

                    UserService userService6 = new UserService();
                    boolean success = userService6.deleteRepairman(userId);

                    if (success) {
                        System.out.println("删除成功！");
                    } else {
                        System.out.println("删除失败，请检查工号或该人员是否为维修人员");
                    }
                    break;

                case 10:
                    System.out.println("已退出管理员菜单");
                    return;

                default:
                    System.out.println("输入错误，请重新选择");
            }
        }
    }
    private String getUrgencyText(Integer urgency) {
        if (urgency == null) return "未知";
        return switch (urgency) {
            case 1 -> "普通";
            case 2 -> "紧急";
            case 3 -> "非常紧急";
            default -> "未知";
        };
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待处理";
            case 1 -> "处理中";
            case 2 -> "已完成";
            case 3 -> "已取消";
            default -> "未知";
        };
    }
}