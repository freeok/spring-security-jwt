package work.pcdd.securityjwt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.pcdd.securityjwt.common.util.JwtUtils;
import work.pcdd.securityjwt.common.util.R;
import work.pcdd.securityjwt.mapper.UserInfoMapper;
import work.pcdd.securityjwt.model.dto.LoginDTO;
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

    @Override
    public R login(LoginDTO loginDTO) {
        UserInfo userInfo = this.getOne(new QueryWrapper<UserInfo>()
                .eq("username", loginDTO.getUsername())
                .eq("password", loginDTO.getPassword()));

        Assert.notNull(userInfo, "用户名或密码错误");
        Assert.isTrue(userInfo.getStatus() != -1, "该账户被锁定");
        Assert.isTrue(userInfo.getStatus() != 0, "该账户被禁用");
        log.info("userInfo：{}", userInfo);

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(userInfo, userInfoDTO);

        // 生成jwt token
        String token = jwtUtils.generateToken(userInfoDTO);
        userInfoDTO.setToken(token);
        log.info("userInfoDTO：{}", userInfoDTO);

        return R.ok("登录成功", userInfoDTO);
    }

}
