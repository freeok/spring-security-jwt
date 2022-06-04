package work.pcdd.securityjwt.common.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import work.pcdd.securityjwt.model.vo.Result;

/**
 * RestControllerAdvice只能捕获controller层的异常，无法捕获filter中的异常
 * 这种Filter发生的404、405、500错误都会到Spring默认的异常处理。导致响应结果不是想要的json格式
 * <p>
 * ResponseStatus注解就是为了改变HTTP响应的状态码
 * 如果不使用@ResponseStatus，在处理方法正确执行的前提下，后台返回HTTP响应的状态码为200
 * 其实就是调用了response.setStatus方法
 *
 * @author 1907263405@qq.com
 * @date 2021/2/23 21:00
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 捕获实体校验异常（MethodArgumentNotValidException）
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handler(MethodArgumentNotValidException e) {
        log.error("实体校验异常：", e);
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();
        return Result.fail(400, objectError.getDefaultMessage());
    }

    /**
     * 捕获断言异常（IllegalArgumentException）
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Result handler(IllegalArgumentException e) {
        log.error("断言异常：", e);
        return Result.fail(400, e.getMessage());
    }

    /**
     * 捕获JWT校验异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JWTVerificationException.class)
    public Result handler(JWTVerificationException e) {
        log.error("JWT校验异常：", e);
        return Result.fail(400, e.getMessage());
    }

    /**
     * 捕捉404异常
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result handle(NoHandlerFoundException e) {
        return Result.fail(404, "请求的资源不存在");
    }

}
