package com.brandslink.cloud.user.remote;


import com.brandslink.cloud.common.entity.Result;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import net.sf.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 远程调用服务
 */
@FeignClient(name = "brandslink-center-service", fallback = CenterRemoteService.CenterRemoteServiceImpl.class)
public interface CenterRemoteService {

    @GetMapping("/warehouse/kv")
    Object getWarehouseDetail();


    /**
     * 断路降级
     */
    @Service
    class CenterRemoteServiceImpl implements CenterRemoteService {

        @Override
        public Object getWarehouseDetail() {
            return fallback();
        }

        public Object fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "中心服务异常"));
        }
    }
}





