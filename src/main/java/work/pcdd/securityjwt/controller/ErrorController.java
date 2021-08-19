package work.pcdd.securityjwt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;
import work.pcdd.securityjwt.common.vo.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理 filter 中的异常
 * 和 FilterExceptionHandler 任选其一
 *
 * @author 1907263405@qq.com
 * @date 2021/4/4 20:13
 */
@RestController
public class ErrorController {

    @RequestMapping("/filterEx")
    public Result ok(HttpServletRequest request) throws Exception {
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        if (throwable != null) {
            return Result.fail(throwable.getMessage());
        }
        throw new NoHandlerFoundException("", "", null);
    }

}
