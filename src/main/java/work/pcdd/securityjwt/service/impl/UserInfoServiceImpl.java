package work.pcdd.securityjwt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * @author pcdd
 * @date 2021-03-26
 */
@Slf4j
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public R login(LoginDTO loginDTO) {
        // 根据username查询用户
        UserInfo userInfo = this.getOne(new QueryWrapper<UserInfo>()
                .eq("username", loginDTO.getUsername()));

        // 用户不存在
        Assert.notNull(userInfo, "用户名或密码错误");
        // 密码错误
        Assert.isTrue(passwordEncoder.matches(loginDTO.getPassword(), userInfo.getPassword()), "用户名或密码错误");
        Assert.isTrue(userInfo.getStatus() != -1, "该账户被锁定");
        Assert.isTrue(userInfo.getStatus() != 0, "该账户被禁用");

        log.info("userInfo：{}", userInfo);

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(userInfo, userInfoDTO);

        // 生成jwt token
        String token = jwtUtils.generateToken(userInfoDTO);

        LoginSuccess loginSuccess = new LoginSuccess();
        loginSuccess.setUserInfoDTO(userInfoDTO);
        loginSuccess.setTokenInfo(jwtUtils.getTokenInfo(token));

        return R.ok("登录成功", loginSuccess);
    }

    @Override
    public R checkToken() {
        TokenInfo tokenInfo = jwtUtils.getTokenInfo();
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
