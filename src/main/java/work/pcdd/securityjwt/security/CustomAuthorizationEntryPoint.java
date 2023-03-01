package work.pcdd.securityjwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import work.pcdd.securityjwt.common.util.R;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 访问需要登录的接口，未携带token时，自定义的返回结果
 *
 * @author pcdd
 * @date 2021/3/24
 */
@Slf4j
@Component
public class CustomAuthorizationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        log.error("访问需要登录的接口，未携带token时，自定义的返回结果", e);
        response.setContentType("application/json;charset=utf-8");
        // 设置http状态码为401
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(R.fail(401, e.getMessage())));
        out.flush();
        out.close();
    }

}
