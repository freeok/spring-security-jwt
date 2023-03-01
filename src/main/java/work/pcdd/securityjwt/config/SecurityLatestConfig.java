package work.pcdd.securityjwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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

    /**
     * HttpSecurity 主要是权限控制规则
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            CustomAuthorizationEntryPoint customAuthorizationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler) throws Exception {

        return httpSecurity
                // 关闭csrf过滤器，因jwt不存储在cookie（jwt要存储在localStorage中，否则仍有csrf风险）
                .csrf().disable()
                // 基于token，不需要session
                .sessionManagement(o -> o.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 在 UsernamePasswordAuthenticationFilter 过滤器之前执行JWT认证过滤器
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(o -> {
                    // 处理未认证异常
                    o.authenticationEntryPoint(customAuthorizationEntryPoint);
                    // 处理未授权异常
                    o.accessDeniedHandler(customAccessDeniedHandler);
                })
                .authorizeHttpRequests(o -> {
                    // 对于静态资源需要通过 requestMatchers 配置访问权限，对于方法可通过注解或配置类来控制权限
                    o.requestMatchers("/image/**").anonymous();
                    // 不加这句，无法访问公开API
                    o.anyRequest().permitAll();
                })
                .build();
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
