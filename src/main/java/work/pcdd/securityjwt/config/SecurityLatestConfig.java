package work.pcdd.securityjwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import work.pcdd.securityjwt.security.CustomAccessDeniedHandler;
import work.pcdd.securityjwt.security.CustomAuthorizationEntryPoint;
import work.pcdd.securityjwt.security.CustomUserDetailsService;
import work.pcdd.securityjwt.security.JwtAuthenticationFilter;

/**
 * 从 Spring Boot 2.7.0（Spring Security 5.7.1）开始，WebSecurityConfigurerAdapter 已弃用
 *
 * @author pcdd
 * @date 2022/12/12
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityLatestConfig {

    /**
     * HttpSecurity 主要是权限控制规则
     *
     * @param customAccessDeniedHandler     当未登录或token失效时访问接口时，自定义的返回结果（401）
     * @param customAuthorizationEntryPoint 当没有权限访问接口时，自定义的返回结果（403）
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomAuthorizationEntryPoint customAuthorizationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler) throws Exception {

        return http
                // 关闭csrf过滤器，因为jwt不存储到cookie
                .csrf().disable()
                // 基于token，不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // 在UsernamePasswordAuthenticationFilter过滤器之前执行JWT登录授权过滤器，加了之后就不会每次启动应用时生成一串密钥了
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                // 处理未认证异常
                .authenticationEntryPoint(customAuthorizationEntryPoint)
                // 处理未授权异常
                .accessDeniedHandler(customAccessDeniedHandler)
                .and()

                .authorizeHttpRequests()
                // 忽略规则
                .antMatchers("/css/**", "/js/**", "favicon.ico")
                .permitAll()

                .and().build();
    }

    /**
     * 配置自定义UserDetailsService
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity, CustomUserDetailsService userDetailsService) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用的密码比较方式，单参构造为加密强度(4-31)，默认10
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

}
