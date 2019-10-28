package com.rondaful.cloud.order.remote;

import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.order.enums.ResponseCodeEnum;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "rondaful-aliexpress-service", fallback = RemoteAliexpressService.RemoteAliexpressServiceImpl.class)
public interface RemoteAliexpressService {

    @PostMapping("/aliexpress/logistics/shipChild")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", value = "数据", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "token", value = "token", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "tradeOrderId", value = "订单号", dataType = "string", paramType = "query", required = true)
    })
    String shipChild(@RequestParam("data")String data, @RequestParam("token")String token, @RequestParam("tradeOrderId")String tradeOrderId);

    @Service
    class RemoteAliexpressServiceImpl implements RemoteAliexpressService{

        @Override
        public String shipChild(String data, String token, String tradeOrderId) {
            return null;
        }

        public String fallback() {
            return String.valueOf(JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "售后服务异常。。。")));
        }
    }
}
