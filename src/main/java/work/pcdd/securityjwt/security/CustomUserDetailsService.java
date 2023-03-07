package work.pcdd.securityjwt.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.pcdd.securityjwt.common.model.entity.UserInfo;
import work.pcdd.securityjwt.service.IUserInfoService;

/**
 * 授权
 *
 * @author pcdd
 * @date 2021/3/27
 */
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserInfoService userInfoService;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.info("开始执行 loadUserByUsername 方法");

        // 性能优化：从缓存中查询
        UserInfo userInfo = userInfoService.getOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getId, userId));

        Assert.notNull(userInfo, "用户不存在");
        log.info("userInfo: {}", userInfo);

        log.info("开始授权（角色和权限）");
        return new CustomUser(userInfo,
                // 授权(设置角色和权限)在这里，字符串以逗号分隔
                // 角色必须以 ROLE_ 开头！否则默认是权限
                AuthorityUtils.commaSeparatedStringToAuthorityList(userInfo.getPermissions()));
    }

}
