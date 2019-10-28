package com.brandslink.cloud.user.config;

import com.brandslink.cloud.common.annotation.WebSecurityConfigurerAdapterFlag;
import com.brandslink.cloud.common.security.config.TokenAuthenticationFilter;
import com.brandslink.cloud.common.security.config.UrlAccessDecisionManager;
import com.brandslink.cloud.common.security.config.UrlFilterInvocationSecurityMetadataSource;
import com.brandslink.cloud.common.security.handler.AuthenticationAccessDeniedHandler;
import com.brandslink.cloud.common.security.handler.MyAuthenticationFailureHandler;
import com.brandslink.cloud.common.security.handler.MyAuthenticationSuccessHandler;
import com.brandslink.cloud.common.security.handler.MyLogoutSuccessHandler;
import com.brandslink.cloud.common.utils.CommonMethodUtil;
import com.brandslink.cloud.user.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * webSecurity权限配置主类
 *
 * @ClassName WebSecurityConfig
 * @Author tianye
 * @Date 2019/5/27 18:15
 * @Version 1.0
 */
@Configuration
@EnableWebSecurity
@WebSecurityConfigurerAdapterFlag
//@EnableGlobalMethodSecurity(prePostEnabled=true)   开启注解支持
public class UserServerWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userEntityService;

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private AuthenticationAccessDeniedHandler authenticationAccessDeniedHandler;

    @Autowired
    private UrlAccessDecisionManager manager;

    @Autowired
    private UrlFilterInvocationSecurityMetadataSource securityMetadataSource;

    @Autowired
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private MyLogoutSuccessHandler myLogoutSuccessHandler;

    @Autowired
    private CommonMethodUtil commonMethodUtil;

    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userEntityService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        commonMethodUtil.CommonSettingWebSecurity(web);
    }

    /**
     * 配置springsecurity过滤器链， 如果配置了自定义鉴权withObjectPostProcessor，使用security的鉴权antMatchers().permitAll()就会失效，两者只能选其一
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .apply(smsCodeAuthenticationSecurityConfig)
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                        o.setAccessDecisionManager(manager);
                        o.setSecurityMetadataSource(securityMetadataSource);
                        return o;
                    }
                })
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin()
//                .loginPage("")
                .loginProcessingUrl("/testLogin")
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(myAuthenticationFailureHandler)
                .and()
                .logout().logoutUrl("/logout")
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .and()
                .cors().configurationSource(corsConfigurationSource)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(authenticationAccessDeniedHandler)
                .and().headers().cacheControl();

        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
