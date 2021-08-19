package work.pcdd.securityjwt.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.pcdd.securityjwt.entity.User;
import work.pcdd.securityjwt.service.UserService;

/**
 * 很关键的一个类，授权就在这里
 *
 * @author 1907263405@qq.com
 * @date 2021/3/27 2:54
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * 根据用户名获取用户 - 用户的角色、权限等信息，非常重要的一个类，授权就在这
     *
     * @param username 用户名
     * @return 用户详情
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("token合法性通过，开始校验有效性，开始执行loadUserByUsername方法");

        User user = userService.getOne(new QueryWrapper<User>()
                .eq("username", username));
        System.out.println(user);

        Assert.notNull(user, "用户不存在");
        Assert.isTrue(user.getStatus() != -1, "该账户被锁定");
        Assert.isTrue(user.getStatus() != 0, "该账户被禁用");

        // 这里的User是Security内置的，不是自定义的
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                passwordEncoder.encode(user.getPassword()),
                user.getStatus() != 0,
                true,
                true,
                user.getStatus() != -1

                // 授权(设置角色和权限)在这里，字符串以逗号分隔
                // 大坑，角色必须以 ROLE_ 开头，必须以 ROLE_ 开头，必须以 ROLE_ 开头，
                // 否则依然是403，就是这个bug搞了一两天
                , AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_" + user.getRole()));
    }
}
