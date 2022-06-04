package work.pcdd.securityjwt.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import work.pcdd.securityjwt.model.dto.LoginDTO;
import work.pcdd.securityjwt.model.vo.Result;
import work.pcdd.securityjwt.service.UserService;

import javax.servlet.http.HttpServletResponse;

/**
 * @author 1907263405@qq.com
 * @date 2021/4/3 22:40
 */
@Slf4j
@RestController
public class AccountController {

    @Autowired
    UserService userService;

    @GetMapping("/abc")
    public Result err() {
        return Result.fail(-1, "filter异常");
    }

    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginDTO loginDTO, HttpServletResponse resp) {
        log.info("登录Controller执行！");
        log.info("AccountController loginDTO = " + loginDTO);
        return userService.login(loginDTO, resp);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/logout")
    public Result logout() {
        // do something
        return Result.success("注销成功", null);
    }

}
