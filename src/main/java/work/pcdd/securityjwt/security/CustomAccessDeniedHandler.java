package work.pcdd.securityjwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import work.pcdd.securityjwt.common.util.R;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 访问没有权限的接口时，自定义返回结果
 * 其实这个类可以不用写，通过捕获AccessDeniedException异常即可，若两者都写了，则优先捕获全局异常
 *
 * @author pcdd
 * @date 2021/3/24
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        log.error("访问没有权限的接口时，自定义返回结果", e);
        response.setContentType("application/json;charset=utf-8");
        // 设置http状态码为403
        response.setStatus(HttpStatus.FORBIDDEN.value());

        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(R.fail(403, e.getMessage())));
        out.flush();
        out.close();
    }

}
