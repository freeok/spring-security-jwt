package work.pcdd.securityjwt.common.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import work.pcdd.securityjwt.common.util.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 处理Filter中发生的异常，将filter异常抛给全局异常而不是spring内置的异常处理
 * 我总结的方法有三种：
 * 1. 实现ErrorController接口，重写getErrorPath方法（作用和配置文件的server.error.path一样）,然后定义一个@GetMapping("/error")接口进行处理
 * 2. 继承BasicErrorController类，加一个构造方法，重写public ResponseEntity<Map<String, Object>> error(HttpServletRequest request)方法即可，但返回类型受限
 * 3. 在配置文件设置server.error.path的值，例如server.error.path=/error,然后定义一个@GetMapping("/error")接口进行处理
 * 方法1、3本质是一样的
 * <p>
 * 目前使用的是第2种方法，因为在SpringBoot2.6.x版本 ErrorController已是一个空接口
 *
 * @author pcdd
 * @date 2022/6/4
 */
@Slf4j
@Component
public class CustomErrorController extends BasicErrorController {

    public CustomErrorController(ServerProperties serverProperties) {
        super(new DefaultErrorAttributes(), serverProperties.getError());
    }

    /**
     * 覆盖默认的JSON响应
     */
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus httpStatus = this.getStatus(request);
        Object ex = request.getAttribute("javax.servlet.error.exception");

        // 有序
        Map<String, Object> map = new LinkedHashMap<>(3);
        // 表示Filter中发生异常，无法被全局异常捕获
        map.put("code", -1);
        map.put("msg", "Filter异常：" + httpStatus);

        if (ex instanceof Exception e) {
            // JWT校验异常，此处使用了JDK16特性：instanceof增强
            if (ex instanceof JWTVerificationException) {
                httpStatus = HttpStatus.FAILED_DEPENDENCY;
            }
            // Spring断言异常
            if (ex instanceof IllegalArgumentException) {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
            map.put("data", e.getMessage());
        } else {
            Map<String, Object> originalMsgMap = this.getErrorAttributes(request, this.getErrorAttributeOptions(request, MediaType.ALL));
            map.put("data", originalMsgMap);
        }

        log.error("Filter异常：{}", httpStatus);

        return new ResponseEntity<>(map, httpStatus);
    }

    @SneakyThrows
    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        // return super.errorHtml(request, response);
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpStatus.NOT_FOUND.value());
        // 即使设置了default-property-inclusion: non_null，此处data字段为null时仍会显示
        response.getWriter()
                .write(new ObjectMapper().writeValueAsString(
                        R.fail(404, "请求的资源不存在")));
        return null;
    }

}




