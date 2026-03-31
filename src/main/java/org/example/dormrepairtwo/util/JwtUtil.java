package org.example.dormrepairtwo.util; // 改成你自己的包名

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.dormrepairtwo.pojo.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    // 固定密钥：从配置文件读取，不再动态生成
    public static SecretKey SECRET_KEY;
    // Token过期时间：从配置文件读取，不再硬编码
    public static long EXPIRATION_TIME;

    // 解决Spring不能给静态变量直接注入配置的问题
    @Value("${jwt.secret}")
    public void setSecretKey(String secret) {
        JwtUtil.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Value("${jwt.expiration}")
    public void setExpirationTime(long expiration) {
        JwtUtil.EXPIRATION_TIME = expiration;
    }

    /**
     * 生成Token：同时存入userId和role角色，为后续权限校验做准备
     * @param loginUser 登录的用户对象
     * @return 生成的JWT Token
     */
    public static String generateToken(User loginUser) {
        Map<String, Object> claims = new HashMap<>();
        // 把用户ID和角色都放进Token，后续拦截器可以直接获取，不用重复查库
        claims.put("userId", loginUser.getUserId());
        claims.put("role", loginUser.getRole());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 过期时间
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 签名算法
                .compact();
    }

    /**
     * 从Token中获取用户ID（兼容你原来的调用）
     */
    public static String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", String.class);
    }

   // 从Token中获取用户角色（兼容你原来的调用）

    public static Integer getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", Integer.class);
    }

    /**
     * 校验Token是否有效（兼容你原来的调用）
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // Token过期、签名错误、格式错误都会返回false
            return false;
        }
    }
}