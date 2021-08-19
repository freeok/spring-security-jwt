package work.pcdd.securityjwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.pcdd.securityjwt.common.dto.LoginDto;
import work.pcdd.securityjwt.common.vo.Result;
import work.pcdd.securityjwt.service.UserService;

import javax.servlet.http.HttpServletResponse;

/**
 * @author 1907263405@qq.com
 * @date 2021/4/3 22:40
 */
@RestController
public class AccountController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse resp) {
        System.out.println("登录Controller执行！");
        System.out.println("AccountController loginDto = " + loginDto);
        return userService.login(loginDto, resp);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/logout")
    public Result logout() {
        // do something
        return Result.success("注销成功", null);
    }


}
