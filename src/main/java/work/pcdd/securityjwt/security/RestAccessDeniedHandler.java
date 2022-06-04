package work.pcdd.securityjwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import work.pcdd.securityjwt.model.vo.Result;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 当接口没有权限时，自定义返回结果
 * 其实这个类可以不用写，通过捕获AccessDeniedException异常即可，若两者都写了，则优先捕获全局异常
 *
 * @author 1907263405@qq.com
 * @date 2021/3/24 21:59
 */
@Slf4j
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        log.info("------------------当接口没有权限时，自定义返回结果");

        response.setContentType("application/json;charset=utf-8");
        // 设置状态码为403
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(Result.fail(403, "权限不足")));
        out.flush();
        out.close();
    }

}
