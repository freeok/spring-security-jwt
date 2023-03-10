package work.pcdd.securityjwt.security;

import com.auth0.jwt.JWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import work.pcdd.securityjwt.common.util.JwtUtils;

import java.io.IOException;

/**
 * OncePerRequestFilter可以保证一次外部请求，只执行一次过滤方法，对于服务器内部之间的forward等请求，不会再次执行过滤方法
 * 主要目的是为了兼容不同的WEB容器，因为Servlet版本不同，执行的过程也不同（2.3过滤一切请求，2.4只过滤外部请求）
 * 建议：在Spring环境下使用Filter，继承OncePerRequestFilter，而非实现Filter接口
 *
 * @author pcdd
 * create by 2021/3/27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${jwt.token-prefix}")
    private String tokenPrefix;
    @Value("${jwt.token-name}")
    private String tokenName;

    /**
     * doFilterInternal方法中的逻辑是只要header中有jwt，就会经过该过滤器
     * 因此公开的API也会走一遍doFilterInternal的流程，这对性能有一定影响，而且是不必要的
     * 2种解决方法，前端请求公开API时不携带token；后端重写如下shouldNotFilter方法
     *
     * @return true表示不过滤，false反之
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // do something...
        return false;
    }

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
            String userId = JWT.decode(token).getId();

            // token合法性通过，开始校验有效性（根据userId断持有此token的用户的当前状态是否正常）
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
            log.info("UserDetails: {}", userDetails);

            // 表示当前访问系统的用户，封装了principal(CustomUser)、credentials、authorities(角色和权限)
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken
                    (userDetails, null, userDetails.getAuthorities());

            // 将 request.getRemoteAddr() 保存到authentication的details属性中
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

            // 参数为null时，访问需要鉴权的API会触发 InsufficientAuthenticationException（401）
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } else {
            log.info("请求未携带token");
        }

        filterChain.doFilter(req, resp);
    }

}
