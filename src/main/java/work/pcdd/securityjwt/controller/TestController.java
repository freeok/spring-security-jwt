package work.pcdd.securityjwt.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import work.pcdd.securityjwt.common.util.JwtUtils;
import work.pcdd.securityjwt.common.util.R;
import work.pcdd.securityjwt.model.entity.UserInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pcdd
 * @date 2022/6/5 周日
 */
@RestController
public class TestController {

    @Autowired
    JwtUtils jwtUtils;

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/fun1")
    public R fun1() {
        return R.ok("当前正在访问系统的用户的详细信息", SecurityContextHolder.getContext().getAuthentication());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/fun2")
    public R fun2() {
        return R.ok("只要认证（登录），此接口就会调用成功");
    }

    //@Secured("ROLE_admin")
    // 这样写的话，admin就无法调用此接口了，若想接口user和admin都调用，不写@PreAuthorize注解即可
    @PreAuthorize("hasRole('user')")
    @GetMapping("/fun3")
    public R fun3() {
        return R.ok("user接口调用成功！");
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/fun4")
    public R fun4() {
        return R.ok("admin接口调用成功！");
    }

    @GetMapping("/fun5")
    public R fun5() {
        return R.ok("没有加任何权限注解，此接口谁都可以调用");
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/fun6")
    public R fun6() {
        return R.ok("这个接口只有匿名用户才能调用，已认证的用户反而无法调用");
    }

    @GetMapping("/token")
    public R token() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1L);
        userInfo.setRole("admin");
        Map<String, Object> map = new HashMap<>();
        map.put("user", userInfo);
        map.put("token", jwtUtils.generateToken(userInfo));
        return R.ok(map);
    }

}
