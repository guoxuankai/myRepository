package com.rondaful.cloud.gateway.config;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
public class DocumentationConfig implements SwaggerResourcesProvider {
    @Override
    public List<SwaggerResource> get() {
        List resources = new ArrayList<>();
        resources.add(swaggerResource("调试授权api接口", "/v2/api-docs", "1.0"));
        resources.add(swaggerResource("商品服务api接口", "/commodity/rest/api/doc", "1.0"));
        resources.add(swaggerResource("财务服务api接口", "/finance/rest/api/doc", "1.0"));
        resources.add(swaggerResource("订单服务api接口", "/order/rest/api/doc", "1.0"));
        resources.add(swaggerResource("卖家服务api接口", "/seller/rest/api/doc", "1.0"));
        resources.add(swaggerResource("供应商服务api接口", "/supplier/rest/api/doc", "1.0"));
        resources.add(swaggerResource("用户服务api接口", "/user/rest/api/doc", "1.0"));
        resources.add(swaggerResource("后台管理服务api接口", "/cms/rest/api/doc", "1.0"));
        //resources.add(swaggerResource("任务调度中心api接口", "/scheduler/rest/api/doc", "1.0"));
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}
