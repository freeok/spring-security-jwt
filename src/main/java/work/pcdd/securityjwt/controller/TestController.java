package work.pcdd.securityjwt.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.pcdd.securityjwt.common.util.R;

/**
 * @author pcdd
 * create by 2022/6/5
 */
@RestController
public class TestController {

    @GetMapping("/f1")
    public R noAuth() {
        return R.ok("没有加任何权限注解，此接口谁都可以调用");
    }

    @PreAuthorize("hasRole('user')")
    @GetMapping("/f2")
    public R hasRoleUser() {
        return R.ok("user 接口调用成功！");
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/f3")
    public R hasRoleAdmin() {
        return R.ok("admin 接口调用成功！");
    }

    @PreAuthorize("hasAuthority('user.view')")
    @GetMapping("/f4")
    public R hasAuthority() {
        return R.ok("这个接口需要 user.view 权限才能访问");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/f5")
    public R isAuthenticated() {
        return R.ok("只要认证（登录），此接口就会调用成功");
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/f6")
    public R isAnonymous() {
        return R.ok("这个接口只有匿名用户才能访问，已认证的用户反而无法访问");
    }

    /**
     * Spring EL 表达式应用举例：获取用户书架，只能获取token持有者的
     * principal.username 为用户 id，见 CustomUser 的 super构造参数
     */
    @PreAuthorize("hasRole('admin') and principal.username == #userId.toString()")
    @GetMapping("/f7")
    public R getBookcase(@RequestParam Long userId, Authentication authentication) {
        return R.ok(authentication);
    }

}
