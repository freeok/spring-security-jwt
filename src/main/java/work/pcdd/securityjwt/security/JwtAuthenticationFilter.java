package work.pcdd.securityjwt.security;

import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import work.pcdd.securityjwt.common.util.JwtUtils;
import work.pcdd.securityjwt.model.entity.UserInfo;
import work.pcdd.securityjwt.service.IUserInfoService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author pcdd
 * @date 2021/3/27
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @Value("${jwt.token-prefix}")
    private String tokenPrefix;
    @Value("${jwt.token-name}")
    private String tokenName;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
        log.info("request uri: {}", req.getRequestURI());

        String s = req.getHeader(tokenName);
        // 存在token
        if (StringUtils.hasText(s) && s.startsWith(tokenPrefix)) {
            // 处理空白字符
            String t = s.trim().replaceAll("\\s+", " ");
            String token = t.substring(t.indexOf(" ") + 1);
            log.info("请求携带的token：{}", token);
            log.info("开始校验token");
            jwtUtils.verifyToken(token);
            log.info("token校验通过");

            // 获取token中的userId
            String userId = JWT.decode(token).getAudience().get(0);

            // 根据 userId 查询 username
            UserInfo userInfo = userInfoService.getOne(new LambdaQueryWrapper<UserInfo>()
                    .select(UserInfo::getUsername)
                    .eq(UserInfo::getId, userId));
            Assert.notNull(userInfo, "用户不存在");

            // token合法性通过，开始校验有效性（根据用户名判断持有此token的用户的当前状态是否正常）
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userInfo.getUsername());
            log.info("UserDetails:" + userDetails);
            log.info("Authorities:" + userDetails.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken
                    (userDetails, null, userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

            // 注释掉这句就会触发CustomAuthorizationEntryPoint(401)
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } else {
            log.info("请求未携带token");
        }

        // 若直接执行这条语句，就会执行自定义未登录的返回结果（CustomAuthorizationEntryPoint）
        filterChain.doFilter(req, resp);
    }

}
