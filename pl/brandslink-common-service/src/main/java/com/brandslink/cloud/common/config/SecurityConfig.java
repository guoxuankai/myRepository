//package com.brandslink.cloud.common.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
//@Configuration
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Value("${swagger.enable}")
//    public boolean isDev;
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable();
//        if (!isDev) {
//            http.authorizeRequests().antMatchers("/**/actuator/service-registry/**").authenticated().anyRequest().permitAll().and().httpBasic();
//        }
//    }
//
//    /*@Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/**");
//    }*/
//
//}
//
