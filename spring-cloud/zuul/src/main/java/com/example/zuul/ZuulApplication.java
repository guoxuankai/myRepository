package com.example.zuul;

import com.example.zuul.filter.LoginFilter;
import com.example.zuul.filter.PreFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
public class ZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulApplication.class, args);
    }


    @Bean
    public LoginFilter loginFilter() {
        return new LoginFilter();

    }

    @Bean
    public PreFilter preFilter() {
        return new PreFilter();

    }
}