package com.rondaful.cloud.order.remote;


import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.order.enums.ResponseCodeEnum;
import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 远程调用服务
 * */
@FeignClient(name = "rondaful-test-service", fallback = RemoteTestService.RemoteTestServiceImpl.class)
public interface RemoteTestService {

    @GetMapping("/updateTest")
    public Object updateTest(@RequestParam("creatTime") String creatTime);


    /**
     * 断路降级
     * */
    @Service
    class RemoteTestServiceImpl implements RemoteTestService {

        @Override
        public Object updateTest(String creatTime) {
            return fallback();
        }

        public Object fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "**服务异常"));
        }
    }
}





