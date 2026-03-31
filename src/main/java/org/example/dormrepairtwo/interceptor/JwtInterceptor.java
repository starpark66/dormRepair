package org.example.dormrepairtwo.interceptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dormrepairtwo.exception.BusinessException;
import org.example.dormrepairtwo.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    //SLF4J日志记录器
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtInterceptor.class);

    // 放行路径配置
    private static final List<String> IGNORE_URLS = Arrays.asList(
            "/api/users/login",
            "/api/users/register"
    );
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    // 移除JwtUtil注入（因为全是静态方法）
    public JwtInterceptor() {}

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        logger.info("拦截到请求: {} {}", request.getMethod(), request.getRequestURI());
        // 1. 放行非控制器请求（如静态资源、Swagger）
        if (!(handler instanceof HandlerMethod)) {
            logger.debug("非控制器请求，直接放行:{}", request.getRequestURI());
            return true;
        }
        // 2. 放行OPTIONS预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        // 3. 放行登录/注册接口
        String uri = request.getRequestURI();
        if (isIgnoreUrl(uri)) {
            return true;
        }
        // 4. 校验Token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            logger.warn("未携带令牌或格式错误: {}", uri);
            throw new BusinessException(401, "未携带令牌token或格式错误（正确格式：Bearer + 令牌）");
        }
        // 去掉Bearer前缀
        token = token.substring(7).trim(); // 增加trim()处理空格
        try {
            // 替换parseToken：直接调用JwtUtil的校验+解析逻辑（因为没有parseToken方法）
            if (!JwtUtil.validateToken(token)) {
                throw new Exception("Token无效");
            }
            // 手动解析Claims（补全JwtUtil缺失的parseToken逻辑）
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(JwtUtil.SECRET_KEY) // 直接用JwtUtil的静态密钥
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            // 打印解析成功日志
            logger.debug("Token解析成功，用户信息：{}", claims);
            request.setAttribute("claims", claims);
        } catch (Exception e) {
            // 打印Token无效日志
            logger.error("Token无效或已过期 → {}，异常：{}", uri, e.getMessage());
            throw new BusinessException(401, "Token无效或已过期");
        }

        // 打印校验通过日志
        logger.info("请求校验通过，放行：{}", uri);
        return true;
    }

    // 路径匹配判断
    private boolean isIgnoreUrl(String uri) {
        return IGNORE_URLS.stream()
                .anyMatch(ignoreUrl -> PATH_MATCHER.match(ignoreUrl, uri));
    }
}