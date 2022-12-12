package work.pcdd.securityjwt.controller;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import work.pcdd.securityjwt.common.util.JwtUtils;
import work.pcdd.securityjwt.common.util.R;
import work.pcdd.securityjwt.model.dto.UserInfoDTO;
import work.pcdd.securityjwt.model.entity.UserInfo;
import work.pcdd.securityjwt.service.IUserInfoService;

/**
 * @author pcdd
 * @date 2022/6/5 周日
 */
@RestController
public class TestController {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private IUserInfoService userInfoService;

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

    @PreAuthorize("hasAuthority('user.edit')")
    @GetMapping("/fun7")
    public R fun7() {
        return R.ok("这个接口需要user.edit权限才能调用");
    }

    /**
     * 内存用户申请token，拥有user角色
     */
    @GetMapping("/token")
    public R token() {
        UserInfo userInfo = userInfoService.getById(2);
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(userInfo, userInfoDTO);
        userInfoDTO.setToken(jwtUtils.generateToken(userInfoDTO));
        return R.ok(userInfoDTO);
    }

}
