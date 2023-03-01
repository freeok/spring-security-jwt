package work.pcdd.securityjwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import work.pcdd.securityjwt.security.CustomAccessDeniedHandler;
import work.pcdd.securityjwt.security.CustomAuthorizationEntryPoint;
import work.pcdd.securityjwt.security.CustomUserDetailsServiceImpl;
import work.pcdd.securityjwt.security.JwtAuthenticationFilter;

/**
 * 从Spring Boot 2.7.0（Spring Security 5.7.1）开始，WebSecurityConfigurerAdapter 已弃用
 *
 * @author pcdd
 * @date 2021/3/26
 */
@Deprecated
// @Configuration
// @RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private CustomUserDetailsServiceImpl userDetailsService;
    /**
     * 当未登录或token失效时访问接口时，自定义的返回结果（401）
     */
    private CustomAuthorizationEntryPoint customAuthorizationEntryPoint;
    /**
     * 当没有权限访问接口时，自定义的返回结果（403）
     */
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * HttpSecurity 主要是权限控制规则
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 重写后Please sign in界面消失
        http.csrf().disable()
                // 基于token，不需要session
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 在UsernamePasswordAuthenticationFilter过滤器之前执行JWT登录授权过滤器，加了之后就不会每次启动应用时生成一串密钥了
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling()
                // restAuthorizationEntryPoint 用来解决匿名用户访问无权限资源时的异常
                .authenticationEntryPoint(customAuthorizationEntryPoint)
                // restfulAccessDeniedHandler 用来解决认证过的用户访问无权限资源时的异常
                .accessDeniedHandler(customAccessDeniedHandler);

        http.authorizeHttpRequests()
                // 忽略规则
                .antMatchers("/css/**", "/js/**", "favicon.ico")
                .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    /**
     * 此处循环依赖了
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用的密码比较方式，单参构造为加密强度(4-31)，默认10
        return new BCryptPasswordEncoder();
    }

}
