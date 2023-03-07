package work.pcdd.securityjwt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.pcdd.securityjwt.common.model.dto.AuthenticationRequest;
import work.pcdd.securityjwt.common.model.dto.AuthenticationResponse;
import work.pcdd.securityjwt.common.model.dto.TokenInfo;
import work.pcdd.securityjwt.common.model.dto.UserInfoDTO;
import work.pcdd.securityjwt.common.model.entity.UserInfo;
import work.pcdd.securityjwt.common.util.JwtUtils;
import work.pcdd.securityjwt.common.util.R;
import work.pcdd.securityjwt.service.AuthenticationService;
import work.pcdd.securityjwt.service.IUserInfoService;

import java.util.Objects;

/**
 * @author pcdd
 * @date 2023/03/05 16:14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final IUserInfoService userInfoService;

    @Override
    public R login(AuthenticationRequest authenticationRequest) {
        log.info("checkUsernameAndPassword begin");
        String loginType = authenticationRequest.getLoginType();
        UserInfo userInfo = null;

        // 根据username查询
        if (Objects.equals(loginType, "username")) {
            userInfo = userInfoService.getOne(new LambdaQueryWrapper<UserInfo>()
                    .eq(UserInfo::getUsername, authenticationRequest.getUsername()));
        }
        // 根据email查询
        if (Objects.equals(loginType, "email")) {
            userInfo = userInfoService.getOne(new LambdaQueryWrapper<UserInfo>()
                    .eq(UserInfo::getEmail, authenticationRequest.getUsername()));
        }

        // 用户不存在
        Assert.notNull(userInfo, "用户名或密码错误");
        // 密码错误
        Assert.isTrue(passwordEncoder.matches(authenticationRequest.getPassword(), userInfo.getPassword()), "用户名或密码错误");
        Assert.isTrue(userInfo.getStatus() != -1, "该账户被锁定");
        Assert.isTrue(userInfo.getStatus() != 0, "该账户被禁用");

        log.info("userInfo：{}", userInfo);
        log.info("checkUsernameAndPassword end");

        log.info("generateToken begin");
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(userInfo, userInfoDTO);
        // 生成jwt token
        String token = jwtUtils.generateToken(userInfoDTO, authenticationRequest);
        log.info("generateToken end");

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .tokenInfo(jwtUtils.getTokenInfo(token))
                .userInfoDTO(userInfoDTO)
                .build();

        return R.ok("登录成功", authenticationResponse);
    }

    @Override
    public R checkToken() {
        TokenInfo tokenInfo = jwtUtils.getTokenInfo();
        // userId
        Long loginId = tokenInfo.getLoginId();

        UserInfo userInfo = userInfoService.getOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getId, loginId));

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(userInfo, userInfoDTO);

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .tokenInfo(tokenInfo)
                .userInfoDTO(userInfoDTO)
                .build();

        return R.ok(authenticationResponse);
    }

}
