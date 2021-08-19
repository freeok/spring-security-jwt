package work.pcdd.securityjwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import work.pcdd.securityjwt.common.vo.Result;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.locks.Lock;

/**
 * 当未登录或token失效时访问接口时，自定义的返回结果
 *
 * @author 1907263405@qq.com
 * @date 2021/3/24 21:52
 */
@Component
public class RestAuthorizationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        System.out.println("------------------当未登录或token失效时访问接口时，自定义的返回结果");
        response.setContentType("application/json;charset=utf-8");
        // 设置状态码为401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter out = response.getWriter();

        out.write(new ObjectMapper().writeValueAsString(Result.fail(401, "访问此资源需要完全身份验证")));
        out.flush();
        out.close();
    }
}
