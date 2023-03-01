package work.pcdd.securityjwt.common.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import work.pcdd.securityjwt.common.util.R;

import java.util.List;

/**
 * RestControllerAdvice只能捕获controller层的异常，无法捕获filter中的异常
 * 这种Filter发生的404、405、500错误都会到Spring默认的异常处理。导致响应结果不是想要的json格式
 * <p>
 * ResponseStatus注解就是为了改变HTTP响应的状态码
 * 如果不使用@ResponseStatus，在处理方法正确执行的前提下，后台返回HTTP响应的状态码为200
 * 其实就是调用了response.setStatus方法
 * <p>
 * 不要捕获Exception异常，会使Spring Security自定义异常失效！
 *
 * @author pcdd
 * @date 2021/2/23
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获JWT校验异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JWTVerificationException.class)
    public R handler(JWTVerificationException e) {
        log.error("JWT校验异常：", e);
        return R.fail(400, e.getMessage());
    }

    /**
     * 捕获Spring断言异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public R handler(IllegalArgumentException e) {
        log.error("断言异常：", e);
        return R.fail(400, e.getMessage());
    }

    /**
     * 捕获实体校验异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handler(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String msg = fieldErrors.get(0).getDefaultMessage();
        log.error("实体校验异常：{}", msg);
        return R.fail(400, msg);
    }

}
