package work.pcdd.securityjwt.common.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import work.pcdd.securityjwt.model.vo.Result;

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
@RestController
public class FilterErrorController extends BasicErrorController {

    public FilterErrorController(ServerProperties serverProperties) {
        super(new DefaultErrorAttributes(), serverProperties.getError());
    }

    /**
     * 覆盖默认的JSON响应
     */
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus httpStatus = this.getStatus(request);
        // 为null认为404，javax.servlet.error.exception由request.getAttributeNames()取得
        Object ex = request.getAttribute("javax.servlet.error.exception");

        String errMsg = "";
        // JWT校验异常，此处使用了JDK16特性：instanceof增强
        if (ex instanceof JWTVerificationException e) {
            errMsg = e.getMessage();
            httpStatus = HttpStatus.FAILED_DEPENDENCY;
        }
        if (ex == null) {
            errMsg = "请求的资源不存在";
            httpStatus = HttpStatus.NOT_FOUND;
        }

        //Map<String, Object> errorAttributes = this.getErrorAttributes(request, this.getErrorAttributeOptions(request, MediaType.ALL));

        // 有序
        Map<String, Object> map = new LinkedHashMap<>(3);
        // 999表示Filter中发生异常
        map.put("code", 999);
        map.put("msg", "Filter异常");
        map.put("data", errMsg);

        return new ResponseEntity<>(map, httpStatus);
    }

    @SneakyThrows
    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=utf-8");
        // 设置状态码为403
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.getWriter()
                .write(new ObjectMapper().writeValueAsString(
                        Result.fail(404, "请求的资源不存在")));
        return null;
    }

}




