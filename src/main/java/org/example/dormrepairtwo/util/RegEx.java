package org.example.dormrepairtwo.util;

public class RegEx {
    //学号校验
    public static boolean checkStudentId(String userId){
        String regEx = "^(3125|3225)\\d{6}$";
        return userId != null && userId.matches(regEx);
    }

    //工号校验
    public static boolean checkWorkerId(String userId){
        String regEx = "^0025\\d{6}$";
        return userId != null && userId.matches(regEx);
    }

    //手机号校验
    public static  boolean checkPhone(String phone){
        String regEx = "^1[3-9]\\d{9}$";
        return phone != null && phone.matches(regEx);
    }

    //用户账户校验
    public static boolean checkUserId(String userId){
        return checkStudentId(userId)||checkWorkerId(userId);
    }

    //密码校验
    public static boolean checkPassword(String password){
        String regEx = "^\\w{6,16}$";
        return password != null && password.matches(regEx);
    }

    //宿舍房号
    public static boolean checkRoomNumber(String roomNumber){
        String regEx = "^[1-7][0-3][1-9]$";
        return roomNumber != null && roomNumber.matches(regEx);
    }
}
