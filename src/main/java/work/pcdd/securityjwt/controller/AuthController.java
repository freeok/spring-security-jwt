package work.pcdd.securityjwt.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import work.pcdd.securityjwt.common.util.JwtUtils;
import work.pcdd.securityjwt.common.util.R;
import work.pcdd.securityjwt.model.dto.LoginDTO;
import work.pcdd.securityjwt.model.dto.UserInfoDTO;
import work.pcdd.securityjwt.model.entity.UserInfo;
import work.pcdd.securityjwt.service.IUserInfoService;

/**
 * @author pcdd
 * @date 2021/4/3
 */
@Slf4j
@RestController
public class AuthController {

    @Autowired
    IUserInfoService userInfoService;
    @Autowired
    JwtUtils jwtUtils;

    /**
     * 登录
     */
    @PostMapping("/login")
    public R login(@Validated @RequestBody LoginDTO loginDTO) {
        return userInfoService.login(loginDTO);
    }

    /**
     * 免密获取token，模拟用户id为2的用户
     */
    @GetMapping("/token")
    public R token() {
        UserInfo userInfo = userInfoService.getById(2);
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(userInfo, userInfoDTO);
        userInfoDTO.setToken(jwtUtils.generateToken(userInfoDTO));
        return R.ok(userInfoDTO);
    }

    /**
     * 获取当前用户的token信息
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/tokenInfo")
    public R tokenInfo() {
        return R.ok(jwtUtils.getTokenInfo());
    }

    /**
     * 获取当前用户的认证信息
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/authenticated")
    public R fun1() {
        return R.ok("当前正在访问系统的用户的详细信息", SecurityContextHolder.getContext().getAuthentication());
    }

}
