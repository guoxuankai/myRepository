package com.rondaful.cloud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * 网关服务启动类
 * */
@EnableDiscoveryClient
@SpringBootApplication
@EnableZuulProxy
@EnableFeignClients
public class GatewayApplication extends WebMvcConfigurerAdapter {

    @Value("${swagger.enable}")
    public boolean isDev;

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("==============GatewayApplication启动成功=======================");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!isDev) {
            registry.addResourceHandler("swagger-ui.html", "/webjars/**")
                    .addResourceLocations("classpath");
        }
    }

}
