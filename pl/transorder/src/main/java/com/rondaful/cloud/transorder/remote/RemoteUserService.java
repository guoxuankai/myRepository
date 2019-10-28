package com.rondaful.cloud.transorder.remote;

import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "rondaful-user-service", fallback = RemoteUserService.RemoteUserSericeImpl.class)
public interface RemoteUserService {


    @ApiOperation(value = "根据传入用户id找到与其绑定的供应链公司(platformType:1卖家 0供应商)")
    @PostMapping("/provider/getSupplyChainByUserId")
    String getSupplyChainByUserId(@RequestParam("platformType") String platformType, @RequestBody List<Integer> userIdList);


    @Service
    class RemoteUserSericeImpl implements RemoteUserService {


        @Override
        public String getSupplyChainByUserId(String platformType, List<Integer> userIdList) {
            return null;
        }
    }
}

