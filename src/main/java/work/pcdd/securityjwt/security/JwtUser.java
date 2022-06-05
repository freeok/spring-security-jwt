package work.pcdd.securityjwt.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author pcdd
 * @date 2021/3/27
 */
@Getter
public class JwtUser extends User {

    private static final long serialVersionUID = 1L;
    private final Long userId;
    private final String email;

    public JwtUser(Long userId, String email, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.email = email;
    }

}
