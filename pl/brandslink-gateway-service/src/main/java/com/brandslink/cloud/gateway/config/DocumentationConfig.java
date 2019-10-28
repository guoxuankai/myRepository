package com.brandslink.cloud.gateway.config;

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
        resources.add(swaggerResource("用户服务api接口", "/user/rest/api/doc", "1.0"));
        resources.add(swaggerResource("物流服务api接口", "/logistics/rest/api/doc", "1.0"));
        resources.add(swaggerResource("报表服务api接口", "/report/rest/api/doc", "1.0"));
        resources.add(swaggerResource("出库服务api接口", "/outbound/rest/api/doc", "1.0"));
        resources.add(swaggerResource("入库服务api接口", "/inbound/rest/api/doc", "1.0"));
        resources.add(swaggerResource("中心服务api接口", "/center/rest/api/doc", "1.0"));
        resources.add(swaggerResource("库内服务api接口", "/warehouse/rest/api/doc", "1.0"));
        resources.add(swaggerResource("财务服务api接口", "/finance/rest/api/doc", "1.0"));
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
