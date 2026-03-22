package org.example.dormrepair;

import java.util.Scanner;
import org.example.dormrepair.pojo.User;
import org.example.dormrepair.service.UserService;
import org.example.dormrepair.util.RegEx;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final UserService userService = new UserService();

    public static void main(String[] args) {
        while (true) {
            System.out.println("===== 宿舍报修系统 =====");
            System.out.println("1. 登录");
            System.out.println("2. 注册");
            System.out.println("3. 退出");
            System.out.println("演示模式：角色有3种，学生3125或3225开头10位学号，维修人员0025开头10位工号，\n管理员0025000001，管理员密码为123456");
            System.out.print("请选择操作：");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    System.out.println("退出系统");
                    return;
                default:
                    System.out.println("输入错误，请重新选择");
            }
        }
    }

    private static void login() {
        System.out.print("请输入账号：");
        String userId = sc.nextLine();
        System.out.print("请输入密码：");
        String password = sc.nextLine();

        if (!RegEx.checkUserId(userId)) {
            System.out.println("账号格式错误！学生账号以3125/3225开头，维修/管理员以0025开头");
            return;
        }

        User user = userService.login(userId, password);
        if (user == null) {
            System.out.println("账号或密码错误！");
            return;
        }

        int role = user.getRole();
        // 强制保证超级管理员权限
        if ("0025000001".equals(userId) && role != 3) {
            System.out.println("警告：超级管理员权限异常，已自动修正为管理员权限");
            role = 3;
        }

        System.out.println("登录成功！当前角色：" + getRoleName(role));
        switch (role) {
            case 1:
                new StudentMenu().enter(sc, userId);
                break;
            case 2:
                new RepairMenu().enter(sc, userId);
                break;
            case 3:
                new AdminMenu().enter(sc);
                break;
            default:
                System.out.println("未知角色，无法进入系统");
        }
    }

    private static void register() {
        System.out.print("请输入账号：");
        String userId = sc.nextLine();
        System.out.print("请输入密码：");
        String password = sc.nextLine();
        System.out.print("请再次确认密码");
        String password2 = sc.nextLine();

        if (!RegEx.checkUserId(userId)) {
            System.out.println("账号格式错误！学生账号以3125/3225开头，维修/管理员以0025开头");
            return;
        }

        if (!password.equals(password2)){
            System.out.println("两次输入的密码不一致！");
            return;
        }

        if (userService.isUserIdExist(userId)) {
            System.out.println("该账号已注册！");
            return;
        }

        int role;
        if (userId.startsWith("3125") || userId.startsWith("3225")) {
            role = 1;
        } else if (userId.startsWith("0025")) {
            if ("0025000001".equals(userId)) {
                role = 3;
                System.out.println("检测到超级管理员账号，自动分配为管理员权限");
            } else {
                role = 2;
                System.out.println("自动分配为维修人员权限");
            }
        } else {
            System.out.println("账号前缀不合法！");
            return;
        }

        User user = new User(userId, password, role);
        boolean success = userService.register(user);
        System.out.println(success ? "注册成功！" : "注册失败！");
    }

    private static String getRoleName(int role) {
        switch (role) {
            case 1: return "学生";
            case 2: return "维修人员";
            case 3: return "管理员";
            default: return "未知角色";
        }
    }
}