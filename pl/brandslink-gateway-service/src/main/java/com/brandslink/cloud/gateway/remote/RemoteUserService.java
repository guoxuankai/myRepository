package com.brandslink.cloud.gateway.remote;


import com.brandslink.cloud.gateway.entity.Massage;
import com.brandslink.cloud.gateway.enums.ResponseCodeEnum;
import net.sf.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 远程调用用户服务
 */
@FeignClient(name = "brandslink-user-service", fallback = RemoteUserService.RemoteServiceImpl.class)
public interface RemoteUserService {

    @PostMapping("/testLogin")
    Object loginForPC(@RequestParam("username") String username, @RequestParam("password") String password);

    @PostMapping("/testLogin")
    Object loginForPDA(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("warehouseCode") String warehouseCode, @RequestParam("warehouseName") String warehouseName);

    @GetMapping("/user/getWarehouseDetailByAccount")
    Object selectWarehouseDetailsByAccount(@RequestParam("account") String account);


    /**
     * 断路降级
     */
    @Service
    class RemoteServiceImpl implements RemoteUserService {

        Object fallback() {
            return JSONObject.fromObject(new Massage(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "用户服务异常"));
        }

        @Override
        public Object loginForPC(String username, String password) {
            return fallback();
        }

        @Override
        public Object loginForPDA(String username, String password, String warehouseCode, String warehouseName) {
            return fallback();
        }

        @Override
        public Object selectWarehouseDetailsByAccount(String account) {
            return fallback();
        }
    }
}





