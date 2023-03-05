package work.pcdd.securityjwt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.pcdd.securityjwt.common.util.JwtUtils;
import work.pcdd.securityjwt.common.util.R;
import work.pcdd.securityjwt.model.dto.LoginDTO;
import work.pcdd.securityjwt.model.dto.LoginSuccess;
import work.pcdd.securityjwt.model.dto.UserInfoDTO;
import work.pcdd.securityjwt.model.entity.UserInfo;
import work.pcdd.securityjwt.service.IUserInfoService;

/**
 * @author pcdd
 * @date 2021/4/3
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtUtils jwtUtils;
    private final IUserInfoService userInfoService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public R login(@Validated @RequestBody LoginDTO loginDTO) {
        return userInfoService.login(loginDTO);
    }

    /**
     * 检查token
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/check")
    public R checkToken() {
        return userInfoService.checkToken();
    }

    /**
     * 测试用，免密获取token，模拟id为2的用户
     */
    @GetMapping("/token")
    public R token(@RequestParam(required = false) Long timeout) {
        UserInfo userInfo = userInfoService.getById(1);
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(userInfo, userInfoDTO);

        LoginDTO loginDTO = new LoginDTO(null, null, "username");
        String token = jwtUtils.generateToken(userInfoDTO, loginDTO, timeout);

        LoginSuccess loginSuccess = new LoginSuccess();
        loginSuccess.setUserInfoDTO(userInfoDTO);
        loginSuccess.setTokenInfo(jwtUtils.getTokenInfo(token));

        return R.ok(loginSuccess);
    }

    /**
     * 获取当前用户的token信息
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/token-info")
    public R tokenInfo() {
        return R.ok(jwtUtils.getTokenInfo());
    }

    /**
     * 获取当前用户的认证信息
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/auth-info")
    public R getAuthentication() {
        return R.ok("当前正在访问系统的用户的详细信息", SecurityContextHolder.getContext().getAuthentication());
    }

}