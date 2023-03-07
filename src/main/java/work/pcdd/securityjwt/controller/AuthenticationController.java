package work.pcdd.securityjwt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.pcdd.securityjwt.common.model.dto.AuthenticationRequest;
import work.pcdd.securityjwt.common.model.dto.AuthenticationResponse;
import work.pcdd.securityjwt.common.model.dto.UserInfoDTO;
import work.pcdd.securityjwt.common.model.entity.UserInfo;
import work.pcdd.securityjwt.common.util.JwtUtils;
import work.pcdd.securityjwt.common.util.R;
import work.pcdd.securityjwt.service.AuthenticationService;
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
    private final AuthenticationService authenticationService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public R login(@Validated @RequestBody AuthenticationRequest authenticationRequest) {
        return authenticationService.login(authenticationRequest);
    }

    /**
     * 检查token
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/check")
    public R checkToken() {
        return authenticationService.checkToken();
    }

    /**
     * 测试用，免密获取token，模拟id为2的用户
     */
    @GetMapping("/quick-token")
    public R quickToken(@RequestParam(required = false) Long timeout) {
        UserInfo userInfo = userInfoService.getById(2);
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(userInfo, userInfoDTO);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(null, null, "username");
        String token = jwtUtils.generateToken(userInfoDTO, authenticationRequest, timeout);

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .tokenInfo(jwtUtils.getTokenInfo(token))
                .userInfoDTO(userInfoDTO)
                .build();

        return R.ok(authenticationResponse);
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
