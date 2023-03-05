package work.pcdd.securityjwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
// @EnableGlobalMethodSecurity 已弃用
@EnableMethodSecurity
public class SecurityLatestConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider,
            CustomAuthorizationEntryPoint customAuthorizationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler) throws Exception {

        return httpSecurity
                .authorizeHttpRequests(o -> {
                    // 对于静态资源需要通过 requestMatchers 配置访问权限，对于方法可通过注解或配置类来控制权限
                    o.requestMatchers("/image/**").permitAll();
                    // 不加这句，无法访问公开API
                    o.anyRequest().permitAll();
                })
                // 在 UsernamePasswordAuthenticationFilter 过滤器之前执行JWT认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(o -> {
                    // 处理未认证异常
                    o.authenticationEntryPoint(customAuthorizationEntryPoint);
                    // 处理未授权异常
                    o.accessDeniedHandler(customAccessDeniedHandler);
                })
                // 关闭csrf过滤器，因jwt不存储在cookie（jwt要存储在localStorage中，否则仍有csrf风险）
                .csrf().disable()
                // 基于token，不需要session
                .sessionManagement(o -> o.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    /**
     * 配置自定义的 UserDetailsService 子类
     */
    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用的密码比较方式，单参构造为加密强度(4-31)，默认10
        return new BCryptPasswordEncoder(10);
    }

}
