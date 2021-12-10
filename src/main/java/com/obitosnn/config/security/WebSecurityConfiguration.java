package com.obitosnn.config.security;

import com.obitosnn.config.security.authentication.*;
import com.obitosnn.config.security.authentication.cache.CacheProvider;
import com.obitosnn.config.security.authentication.cache.impl.RedisCacheProviderImpl;
import com.obitosnn.config.security.filter.JwtAuthenticationFilter;
import com.obitosnn.config.security.filter.LoginFilter;
import com.obitosnn.config.web.filter.OptionsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * @author ObitoSnn
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    @Qualifier("customUserDetailService")
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final CacheProvider<String, String> cacheProvider;

    /**
     * 登出Handler
     */
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final LogoutHandler logoutHandler;

    /**
     * 登录认证Handler
     */
    private final AuthenticationSuccessHandler loginAuthenticationSuccessHandler;
    private final AuthenticationFailureHandler loginAuthenticationFailureHandler;

    /**
     * Jwt认证Handler
     */
    private final AuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
    private final AuthenticationFailureHandler jwtAuthenticationFailureHandler;

    private final AuthenticationEntryPoint unAuthorityEntryPoint = new UnAuthorityEntryPoint();
    private final AccessDeniedHandler httpStatus403AccessDeniedHandler = new HttpStatus403AccessDeniedHandler();

    private final SecurityContextRepository securityContextRepository;

    private final OptionsFilter optionsFilter;

    public WebSecurityConfiguration(RedisTemplate<String, Object> redisTemplate) {
        this.cacheProvider = new RedisCacheProviderImpl(redisTemplate);
        this.logoutSuccessHandler = new com.obitosnn.config.security.authentication.LogoutSuccessHandler();
        this.logoutHandler = new com.obitosnn.config.security.authentication.LogoutHandler(cacheProvider);
        this.loginAuthenticationSuccessHandler = new LoginAuthenticationSuccessHandler(cacheProvider);
        this.loginAuthenticationFailureHandler = new LoginAuthenticationFailureHandler();
        this.jwtAuthenticationSuccessHandler = new JwtAuthenticationSuccessHandler(cacheProvider);
        this.jwtAuthenticationFailureHandler = new JwtAuthenticationFailureHandler();
        this.securityContextRepository = new InMemorySecurityContextRepository(cacheProvider);
        this.optionsFilter = new OptionsFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin().disable()
                .exceptionHandling((exceptionHandling) -> {
                    exceptionHandling
                            .authenticationEntryPoint(unAuthorityEntryPoint)
                            .accessDeniedHandler(httpStatus403AccessDeniedHandler);
                })
                .authorizeRequests((urlRegistry) -> {
                    urlRegistry
                            .anyRequest()
                            .authenticated();
                })
                .csrf().disable()
                .sessionManagement().disable()
                .addFilter(loginFilter())
                .logout((logoutConfigurer) -> {
                    logoutConfigurer.logoutSuccessHandler(logoutSuccessHandler)
                            .addLogoutHandler(logoutHandler);
                })
                .addFilterBefore(optionsFilter, SecurityContextPersistenceFilter.class)
                .httpBasic();
        http.setSharedObject(SecurityContextRepository.class,
                securityContextRepository);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
        auth.eraseCredentials(false);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/error")
                .antMatchers("/pages/**")
                .antMatchers(HttpMethod.GET, "/")
                .antMatchers(HttpMethod.GET, "/doc.html")
                .antMatchers("/**/*.js")
                .antMatchers("/**/*.css")
                .antMatchers("/**/*.html")
                .antMatchers("/**/*.svg")
                .antMatchers("/**/*.pdf")
                .antMatchers("/**/*.jpg")
                .antMatchers("/**/*.png")
                .antMatchers("/**/*.ico")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/swagger**/**")
                .antMatchers("/webjars/**")
                .antMatchers("/v2/**");
    }

    // ========================= Bean =========================

    @Bean("authenticationManager")
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return authenticationManager();
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        return new LoginFilter(getAuthenticationManager(),
                loginAuthenticationSuccessHandler, loginAuthenticationFailureHandler);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(getAuthenticationManager(),
                cacheProvider, jwtAuthenticationSuccessHandler, jwtAuthenticationFailureHandler);
    }
}
