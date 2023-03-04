package work.pcdd.securityjwt.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import work.pcdd.securityjwt.model.dto.UserInfoDTO;
import work.pcdd.securityjwt.model.entity.UserInfo;

import java.util.Collection;

/**
 * @author pcdd
 * @date 2021/3/27
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class CustomUser extends User {

    private final UserInfoDTO userInfoDTO;

    public CustomUser(UserInfo userInfo, Collection<? extends GrantedAuthority> authorities) {
        super(userInfo.getUsername(),
                userInfo.getPassword(),
                userInfo.getStatus() != 0,
                true,
                true,
                userInfo.getStatus() != -1,
                authorities);

        UserInfoDTO target = new UserInfoDTO();
        BeanUtils.copyProperties(userInfo, target);

        this.userInfoDTO = target;
    }

}
