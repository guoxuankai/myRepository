package com.rondaful.cloud.gateway.remote;


import com.rondaful.cloud.gateway.entity.Massage;
import com.rondaful.cloud.gateway.enums.ResponseCodeEnum;
import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 远程调用用户服务
 * */
@FeignClient(name = "rondaful-user-service", fallback = RemoteUserService.RemoteServiceImpl.class)
public interface RemoteUserService {

    @PostMapping("/login/supplierLogin")
    public Object loginForSupplier(@RequestParam("username") String mobile, @RequestParam("password") String pwd, @RequestParam("code") String code);

    @PostMapping("/login/sellerLogin")
    public Object loginForSeller(@RequestParam("username") String username, @RequestParam("password") String password);

    @PostMapping("/login/manageUserLogin")
    public Object loginForCms(@RequestParam("username") String username, @RequestParam("password") String password);

    @PostMapping("login/home")
    public Object login(@RequestParam("userName") String userName, @RequestParam("passWord") String passWord,@RequestParam("type") Integer type);


    /**
     * 断路降级
     * */
    @Service
    class RemoteServiceImpl implements RemoteUserService {


        public Object fallback() {
            return JSONObject.fromObject(new Massage(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "用户服务异常"));
        }

        @Override
        public Object loginForSupplier(String mobile, String pwd, String code) {
            return fallback();
        }

        @Override
        public Object loginForSeller(String mobile, String pwd) {
            return fallback();
        }

        @Override
        public Object loginForCms(String username, String password) {
            return fallback();
        }

        @Override
        public Object login(String username, String password, Integer type) {
            return fallback();
        }
    }
}





