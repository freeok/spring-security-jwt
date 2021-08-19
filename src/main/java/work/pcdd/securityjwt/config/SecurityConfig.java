package work.pcdd.securityjwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import work.pcdd.securityjwt.security.JwtAuthenticationFilter;
import work.pcdd.securityjwt.security.RestAccessDeniedHandler;
import work.pcdd.securityjwt.security.RestAuthorizationEntryPoint;
import work.pcdd.securityjwt.security.UserDetailsServiceImpl;

/**
 * @author 1907263405@qq.com
 * @date 2021/3/26 23:56
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    /**
     * 当未登录或token失效时访问接口时，自定义的返回结果（401）
     */
    @Autowired
    RestAuthorizationEntryPoint restAuthorizationEntryPoint;

    /**
     * 当没有权限访问接口时，自定义的返回结果（403）
     */
    @Autowired
    RestAccessDeniedHandler restAccessDeniedHandler;

    /**
     * WebSecurity 主要针对的全局的忽略规则
     */
    @Override
    public void configure(WebSecurity web) {
        // jwtAuthenticationFilter将忽略以下路径
        web.ignoring()
                // 放行的接口
                //.antMatchers("/**")
                //.antMatchers("/login")
                // 放行的资源
                .antMatchers(
                        HttpMethod.GET,
                        "/css/**",
                        "/js/**",
                        "favicon.ico"
                )
                //对于在header里面增加token等类似情况，放行所有OPTIONS请求。
                .antMatchers(HttpMethod.OPTIONS, "/**");
    }

    /**
     * HttpSecurity 主要是权限控制规则。
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

        // restAuthorizationEntryPoint 用来解决匿名用户访问无权限资源时的异常
        // restfulAccessDeniedHandler 用来解决认证过的用户访问无权限资源时的异常
        http.exceptionHandling()
                .authenticationEntryPoint(restAuthorizationEntryPoint)
                .accessDeniedHandler(restAccessDeniedHandler);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用的密码比较方式，单参构造为加密强度(4-31)，默认10
        return new BCryptPasswordEncoder();
    }


}
