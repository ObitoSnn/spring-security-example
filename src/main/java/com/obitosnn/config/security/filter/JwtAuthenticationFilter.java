package com.obitosnn.config.security.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.jwt.signers.JWTSigner;
import com.obitosnn.config.security.authentication.JwtAuthenticationToken;
import com.obitosnn.config.security.authentication.LoginAuthenticationSuccessHandler;
import com.obitosnn.config.security.authentication.cache.CacheProvider;
import com.obitosnn.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Jwt认证过滤器
 *
 * @author ObitoSnn
 */
@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final CacheProvider<String> cacheProvider;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   CacheProvider<String> cacheProvider,
                                   AuthenticationSuccessHandler successHandler,
                                   AuthenticationFailureHandler failureHandler) {
        super("/**");
        // authenticationManager不能为空
        setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler(successHandler);
        setAuthenticationFailureHandler(failureHandler);
        this.cacheProvider = cacheProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (!requiresAuthentication(request, response)) {
            chain.doFilter(request, response);

            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Request is to process authentication");
        }

        Authentication authResult;

        try {
            authResult = attemptAuthentication(request, response);
            if (authResult == null) {
                // return immediately as subclass has indicated that it hasn't completed
                // authentication
                return;
            }
        }
        catch (InternalAuthenticationServiceException failed) {
            logger.error(
                    "An internal error occurred while trying to authenticate the user.",
                    failed);
            unsuccessfulAuthentication(request, response, failed);

            return;
        }
        catch (AuthenticationException failed) {
            // Authentication failed
            unsuccessfulAuthentication(request, response, failed);

            return;
        }

        successfulAuthentication(request, response, chain, authResult);

        // 认证成功，放行过滤连
        chain.doFilter(request, response);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.debug("Jwt认证Filter");

        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AuthenticationException("请先登录") {};
        }

        //请求头携带的token
        String token = request.getHeader(LoginAuthenticationSuccessHandler.X_ACCESS_TOKEN);
        if (ObjectUtil.isEmpty(token)) {
            throw new AuthenticationException("无权访问,请携带token再次访问") {};
        }

        String contextToken = cacheProvider.get(TokenUtil.getInfoByToken(token));
        if (ObjectUtil.isNotEmpty(contextToken) && !token.equals(contextToken)) {
            throw new AuthenticationException("token无效") {};
        }

        JWTSigner signer = TokenUtil.getSigner(loginUser.getPassword());
        if (!TokenUtil.isValidate(token, signer)) {
            throw new AuthenticationException("token已过期") {};
        }

        return new JwtAuthenticationToken(token,
                loginUser.getAuthorities());
    }

    /**
     * 重写该方法，不更新{@link SecurityContext}中的{@link Authentication}
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {

        if (logger.isDebugEnabled()) {
            logger.debug("Authentication success. : "
                    + authResult);
        }

        getRememberMeServices().loginSuccess(request, response, authResult);

        // Fire event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(
                    authResult, this.getClass()));
        }

        getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }

    /**
     * 不清空{@link SecurityContextHolder}中的{@link SecurityContext}
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("Authentication request failed: " + failed.toString(), failed);
            logger.debug("Delegating to authentication failure handler " + getFailureHandler());
        }

        getRememberMeServices().loginFail(request, response);

        getFailureHandler().onAuthenticationFailure(request, response, failed);
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        SecurityContext context = SecurityContextHolder.getContext();
        return ObjectUtil.isNotEmpty(context.getAuthentication()) && super.requiresAuthentication(request, response)
                && !context.getAuthentication().getClass().isAssignableFrom(AnonymousAuthenticationToken.class);
    }
}
