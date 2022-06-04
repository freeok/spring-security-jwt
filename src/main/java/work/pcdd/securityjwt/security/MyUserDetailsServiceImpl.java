package work.pcdd.securityjwt.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.pcdd.securityjwt.model.entity.UserInfo;
import work.pcdd.securityjwt.service.IUserInfoService;

/**
 * 很关键的一个类，授权就在这里
 *
 * @author pcdd
 * @date 2021/3/27
 */
@Slf4j
@Service
public class MyUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 根据用户名获取用户角色、权限等信息，非常重要的一个类，授权就在这
     *
     * @param username 用户名
     * @return 用户详情
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("开始执行loadUserByUsername方法");

        UserInfo userInfo = userInfoService.getOne(new QueryWrapper<UserInfo>()
                .eq("username", username));
        log.info("userInfo:{}", userInfo);

        Assert.notNull(userInfo, "用户不存在");
        Assert.isTrue(userInfo.getStatus() != -1, "该账户被锁定");
        Assert.isTrue(userInfo.getStatus() != 0, "该账户被禁用");
        log.info("开始授权");

        // 这里的User是Security内置的类
        return new User(
                userInfo.getUsername(),
                passwordEncoder.encode(userInfo.getPassword()),
                userInfo.getStatus() != 0,
                true,
                true,
                userInfo.getStatus() != -1,
                // 授权(设置角色和权限)在这里，字符串以逗号分隔
                // 大坑，角色必须以 ROLE_ 开头，否则依然是403，就是这个bug搞了一两天
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_" + userInfo.getRole()));
    }

}
