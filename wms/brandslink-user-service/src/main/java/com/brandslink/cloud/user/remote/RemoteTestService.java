package com.brandslink.cloud.user.remote;


import com.brandslink.cloud.user.entity.Attribute;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 远程调用服务
 * */
@FeignClient(name = "brandslink-cms2-service", fallback = RemoteTestService.RemoteTestServiceImpl.class)
public interface RemoteTestService {

    @PostMapping("/test")
    public Object updateTest(@RequestBody Attribute attribute);


    /**
     * 断路降级
     * */
    @Service
    class RemoteTestServiceImpl implements RemoteTestService {

        @Override
        public Object updateTest(Attribute attribute) {
            return fallback();
        }

        public Object fallback() {
            return "fallback";
        }
    }
}





