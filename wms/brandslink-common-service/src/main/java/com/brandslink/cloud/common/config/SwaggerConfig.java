package com.brandslink.cloud.common.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
@PropertySource("classpath:swagger.properties")
public class SwaggerConfig {

    @Value("${swagger.enable}")
    public boolean isDev;

    @Value("${spring.application.name}")
    public String application;
    
    @Value("${api.version}")
    private String version;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(isDev)
                .apiInfo(apiInfo())
                .select().apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("com.brandslink.cloud"))
                .paths(PathSelectors.any())
                .build();
    }


    private ApiInfo apiInfo() {
        switch (application) {
            case "brandslink-user-service":
                application = "用户服务API管理";
                break;
            case "brandslink-logistics-service":
                application = "物流服务API管理";
                break;
            case "brandslink-report-service":
                application = "报表服务API管理";
                break;
            case "brandslink-outbound-service":
                application = "出库服务API管理";
                break;
            case "brandslink-inbound-service":
                application = "入库服务API管理";
                break;
            case "brandslink-center-service":
                application = "中心服务API管理";
                break;
            case "brandslink-warehouse-service":
                application = "库内服务API管理";
                break;
            case "brandslink-finance-service":
                application = "财务服务API管理";
                break;
        }
        return new ApiInfoBuilder()
                .title(application)
                .description("API 版本： " + version.split("-")[0])
                .version(version.split("-")[0])
                .build();
    }

}
