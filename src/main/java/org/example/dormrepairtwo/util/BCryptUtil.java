package org.example.dormrepairtwo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class BCryptUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    //加密方法，传入明文密码，返回加密后的密码
    public static String encode(String password) {
        return encoder.encode(password);
    }

    //匹配方法，传入明文密码和加密后的密码，返回是否匹配的结果
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
