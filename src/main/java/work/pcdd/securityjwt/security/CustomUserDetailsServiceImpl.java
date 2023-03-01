package work.pcdd.securityjwt.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.pcdd.securityjwt.model.entity.UserInfo;
import work.pcdd.securityjwt.service.IUserInfoService;

/**
 * 授权
 *
 * @author pcdd
 * @date 2021/3/27
 */
@Slf4j
@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("开始执行 loadUserByUsername 方法");

        UserInfo userInfo = userInfoService.getOne(new QueryWrapper<UserInfo>()
                .eq("username", username));

        log.info("userInfo:{}", userInfo);
        Assert.notNull(userInfo, "用户不存在");
        Assert.isTrue(userInfo.getStatus() != -1, "该账户被锁定");
        Assert.isTrue(userInfo.getStatus() != 0, "该账户被禁用");

        log.info("开始授权（角色和权限）");
        return new CustomUser(
                userInfo.getId(), userInfo.getEmail(),
                userInfo.getUsername(),
                passwordEncoder.encode(userInfo.getPassword()),
                userInfo.getStatus() != 0,
                true,
                true,
                userInfo.getStatus() != -1,
                // 授权(设置角色和权限)在这里，字符串以逗号分隔
                // 角色必须以 ROLE_ 开头！否则默认是权限
                AuthorityUtils.commaSeparatedStringToAuthorityList(userInfo.getRole()));
    }

}
