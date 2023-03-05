package work.pcdd.securityjwt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.pcdd.securityjwt.common.util.JwtUtils;
import work.pcdd.securityjwt.common.util.R;
import work.pcdd.securityjwt.mapper.UserInfoMapper;
import work.pcdd.securityjwt.model.dto.LoginDTO;
import work.pcdd.securityjwt.model.dto.LoginSuccess;
import work.pcdd.securityjwt.model.dto.TokenInfo;
import work.pcdd.securityjwt.model.dto.UserInfoDTO;
import work.pcdd.securityjwt.model.entity.UserInfo;
import work.pcdd.securityjwt.service.IUserInfoService;

import java.util.Objects;

/**
 * @author pcdd
 * @date 2021-03-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public R login(LoginDTO loginDTO) {
        log.info("checkUsernameAndPassword begin");
        String loginType = loginDTO.getLoginType();
        UserInfo userInfo = null;

        // 根据username查询
        if (Objects.equals(loginType, "username")) {
            userInfo = this.getOne(new LambdaQueryWrapper<UserInfo>()
                    .eq(UserInfo::getUsername, loginDTO.getUsername()));
        }
        // 根据email查询
        if (Objects.equals(loginType, "email")) {
            userInfo = this.getOne(new LambdaQueryWrapper<UserInfo>()
                    .eq(UserInfo::getEmail, loginDTO.getUsername()));
        }

        // 用户不存在
        Assert.notNull(userInfo, "用户名或密码错误");
        // 密码错误
        Assert.isTrue(passwordEncoder.matches(loginDTO.getPassword(), userInfo.getPassword()), "用户名或密码错误");
        Assert.isTrue(userInfo.getStatus() != -1, "该账户被锁定");
        Assert.isTrue(userInfo.getStatus() != 0, "该账户被禁用");

        log.info("userInfo：{}", userInfo);
        log.info("checkUsernameAndPassword end");

        log.info("generateToken begin");
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(userInfo, userInfoDTO);
        // 生成jwt token
        String token = jwtUtils.generateToken(userInfoDTO, loginDTO);
        log.info("generateToken end");

        LoginSuccess loginSuccess = new LoginSuccess();
        loginSuccess.setUserInfoDTO(userInfoDTO);
        loginSuccess.setTokenInfo(jwtUtils.getTokenInfo(token));

        return R.ok("登录成功", loginSuccess);
    }

    @Override
    public R checkToken() {
        TokenInfo tokenInfo = jwtUtils.getTokenInfo();
        // userId
        Long loginId = tokenInfo.getLoginId();

        UserInfo userInfo = this.getOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getId, loginId));

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(userInfo, userInfoDTO);

        LoginSuccess loginSuccess = new LoginSuccess();
        loginSuccess.setTokenInfo(tokenInfo);
        loginSuccess.setUserInfoDTO(userInfoDTO);

        return R.ok(loginSuccess);
    }
}
