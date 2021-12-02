package com.obitosnn.config.security;

import com.obitosnn.config.security.authentication.HttpStatus403AccessDeniedHandler;
import com.obitosnn.config.security.authentication.UnAuthorityEntryPoint;
import com.obitosnn.config.security.filter.LoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

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
    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    private LogoutHandler logoutHandler;
    @Autowired
    private LoginFilter loginFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin().disable()
                .exceptionHandling((exceptionHandling) -> {
                    exceptionHandling
                            .authenticationEntryPoint(new UnAuthorityEntryPoint())
                            .accessDeniedHandler(new HttpStatus403AccessDeniedHandler());
                })
                .authorizeRequests((urlRegistry) -> {
                    urlRegistry
                            .anyRequest()
                            .authenticated();
                })
                .csrf().disable()
                .sessionManagement().disable()
                .addFilter(loginFilter)
                .logout((logoutConfigurer) -> {
                    logoutConfigurer.logoutSuccessHandler(logoutSuccessHandler)
                            .addLogoutHandler(logoutHandler);
                })
                .httpBasic();
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

    @Bean("authenticationManager")
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return authenticationManager();
    }
}
