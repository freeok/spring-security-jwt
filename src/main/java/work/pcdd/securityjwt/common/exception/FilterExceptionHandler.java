package work.pcdd.securityjwt.common.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * Filter异常处理，将filter异常抛给全局异常而不是spring内置的异常处理
 * 我总结的方法有三种：
 * 1. 实现ErrorController接口，重写getErrorPath方法（作用和配置文件的server.error.path一样）,然后定义一个@GetMapping("/error")接口进行处理
 * 2. 继承BasicErrorController类，加一个构造方法，重写public ResponseEntity<Map<String, Object>> error(HttpServletRequest request)方法即可，但返回类型受限
 * 3. 在配置文件设置server.error.path的值，例如server.error.path=/error,然后定义一个@GetMapping("/error")接口进行处理
 * 方法1、3本质是一样的
 * 目前使用的是第一种方法
 *
 * @author 1907263405@qq.com
 * @date 2021/4/4 19:05
 */
@RestController
public class FilterExceptionHandler implements ErrorController {
    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    public void handleError(HttpServletRequest request) throws Throwable {
        Object attribute = request.getAttribute("javax.servlet.error.exception");

        if (attribute != null) {
            throw (Throwable) attribute;
        } else { // 说明是404异常，抛给全局异常处理
            throw new NoHandlerFoundException("", "", null);
        }
    }
}




