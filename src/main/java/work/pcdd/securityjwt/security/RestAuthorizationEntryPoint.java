package work.pcdd.securityjwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import work.pcdd.securityjwt.model.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 当未登录或token失效时访问接口时，自定义的返回结果
 *
 * @author 1907263405@qq.com
 * @date 2021/3/24 21:52
 */
@Slf4j
@Component
public class RestAuthorizationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        log.info("------------------当未登录或token失效时访问接口时，自定义的返回结果");
        response.setContentType("application/json;charset=utf-8");
        // 设置状态码为401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter out = response.getWriter();

        out.write(new ObjectMapper().writeValueAsString(Result.fail(401, "访问此资源需要完全身份验证")));
        out.flush();
        out.close();
    }

}