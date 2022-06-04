package work.pcdd.securityjwt.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pcdd
 * @date 2021/3/27
 */
@Data
public class JwtUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final Long id;

    private final String username;

    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    private final boolean enabled;


    /*public JwtUser(Long id, String username, String password, List<GrantedAuthority> authorities, boolean enabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = mapToGrantedAuthorities(authorities);
        this.enabled = enabled;
    }

    public JwtUser(Long id, String username, String password, String authoritie, boolean enabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = mapToGrantedAuthorities(authoritie);
        this.enabled = enabled;
    }*/

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    private List<GrantedAuthority> mapToGrantedAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    private List<GrantedAuthority> mapToGrantedAuthorities(List<String> listRole) {
        return listRole.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
