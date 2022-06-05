package work.pcdd.securityjwt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;
import work.pcdd.securityjwt.common.util.R;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理 filter 中的异常
 * 和 FilterErrorController 任选其一
 *
 * @author pcdd
 * @date 2021/4/4
 */
@Deprecated
@RestController
public class ErrorController {

    @RequestMapping("/filterEx")
    public R ok(HttpServletRequest request) throws Exception {
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        if (throwable != null) {
            return R.fail(throwable.getMessage());
        }
        throw new NoHandlerFoundException("", "", null);
    }

}
