package work.pcdd.securityjwt.security;

import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import work.pcdd.securityjwt.entity.User;
import work.pcdd.securityjwt.service.UserService;
import work.pcdd.securityjwt.util.JwtUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 1907263405@qq.com
 * @date 2021/3/27 0:27
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
        log.info("鉴权过滤器执行");
        String token = req.getHeader("Authorization");
        System.out.println("请求的api地址:" + req.getRequestURI());

        // 巨坑，之前没写这一段，配置类的antMatchers一直失效；如果请求头中没有Authorization信息则直接放行了
        if (!StringUtils.hasText(token)) {
            log.warn("无token");
            filterChain.doFilter(req, resp);
            return;
        }

        System.out.println("token存在，开始合法性校验");
        jwtUtils.verifyToken(token);

        // 获取token中的userId
        String userId = JWT.decode(token).getAudience().get(0);

        // 根据userId查询username
        User user = userService.getOne(new QueryWrapper<User>()
                .eq("id", userId).select("username"));

        Assert.notNull(user, "用户名不存在");

        // token合法性通过，开始校验有效性（根据用户名判断持有此token的用户的当前状态是否正常）
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        System.out.println("userDetails:" + userDetails);
        System.out.println("Authorities:" + userDetails.getAuthorities());


        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken
                (userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

        // 认证 注释掉这句就会执行RestfulAuthorizationEntryPoint(401)
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 若直接执行这条语句，就会执行 自定义 未登录 的返回结果（RestAuthorizationEntryPoint）
        filterChain.doFilter(req, resp);
    }

}
