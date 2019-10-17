package com.bdi.sselab.configuration;


import com.bdi.sselab.repository.userDepart.UserRepository;
import com.bdi.sselab.service.UserDetailServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.session.SimpleRedirectSessionInformationExpiredStrategy;
import org.springframework.web.cors.CorsUtils;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author wh
 * @date 2019/3/25
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRegistry sessionRegistry;
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
         .csrf().disable() // 禁用csrf，不太安全
                .cors()
                .and()
                .exceptionHandling()
                // 自定义认证响应，失败返回401
                .authenticationEntryPoint(new AjaxAuthenticationEntryPoint())
                .and()
                .authorizeRequests()
                .mvcMatchers("/login.html", "/static/**", "/JavaScript/").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin() // 表单登录接口
                .failureHandler(new AjaxAuthenticationFailureHandler()) // 登录失败
                .successHandler(new AjaxAuthenticationSuccessHandler()) // 登录成功
                .and()
                .logout() // 退出登录
                .logoutSuccessHandler(new AjaxLogoutSuccessHandler()) // 退出登录成功
                .permitAll().and()
                .httpBasic().and()
                .sessionManagement().invalidSessionUrl("/login.html").and()
                .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(sessionRegistry)
                .expiredUrl("/login.html");
    }

    /**
     * 定义登录响应，失败返回401
     */
    private class AjaxAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
//            sessionInformationExpiredStrategy();
            httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(),e.getMessage());
        }
    }
    /**
     * 登录失败处理类
     */
    private class AjaxAuthenticationFailureHandler implements AuthenticationFailureHandler {

        @Override
        public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
            httpServletResponse.sendError(
                    HttpStatus.UNAUTHORIZED.value(),
                    e.getMessage()
            );
        }
    }

    /**
     * 登录成功处理类
     */
    private class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

        @Override
        public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            mapper.writeValue(httpServletResponse.getWriter(),
                    userRepository.findByUsername(authentication.getName()));
            System.out.println(userRepository.findByUsername(authentication.getName()).getUsername());
        }
    }
    /**
     * 登出成功处理类
     */
    private class AjaxLogoutSuccessHandler implements LogoutSuccessHandler {

        @Override
        public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
            httpServletResponse.setStatus(HttpStatus.NO_CONTENT.value());
        }
    }

    /**
     * 登录认证过程
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        auth.
                userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);//对登录密码进行加盐哈希

    }


    private SessionInformationExpiredStrategy sessionInformationExpiredStrategy() {
        return new SimpleRedirectSessionInformationExpiredStrategy("/login.html");
    }

    @Bean
    public SessionRegistry sessionRegistry(){
        SessionRegistry sessionRegistry = new SessionRegistryImpl();
        return sessionRegistry;
    }

    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher(){
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }


}
