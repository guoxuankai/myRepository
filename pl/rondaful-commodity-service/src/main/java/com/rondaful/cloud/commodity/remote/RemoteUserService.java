package com.rondaful.cloud.commodity.remote;


import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;

import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

/**
 * 远程调用用户服务
 * */
@FeignClient(name = "rondaful-user-service", fallback = RemoteUserService.RemoteServiceImpl.class)
public interface RemoteUserService {

    @GetMapping("/provider/getSupplierList")
    public Object getSupplierList(@RequestParam("userIds")Set userIds, @RequestParam("platformType")Integer platformType);
    
    @PostMapping("/provider/getByAppKey")
    public Object getByAppKey(@RequestParam("appKey")String appKey);
    
    @PostMapping("/provider/insertDown")
    public Object insertDown(@RequestParam("job") String job,@RequestParam("userId") Integer userId,@RequestParam("topUserId") Integer topUserId,@RequestParam("platformType") Integer platformType);

    @PostMapping("/provider/updateDownStatus")
    public Object updateDownStatus(@RequestParam("id") Integer id,@RequestParam("url") String url,@RequestParam("status") Integer status);
    
    
    /**
     * 断路降级
     * */
    @Service
    class RemoteServiceImpl implements RemoteUserService {
    	public Object fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "用户服务异常"));
        }

        @Override
        public Object getSupplierList(Set userIds, Integer platformType) {
            return fallback();
        }

		@Override
		public Object getByAppKey(String appKey) {
			return fallback();
		}

		@Override
		public Object insertDown(String job, Integer userId, Integer topUserId, Integer platformType) {
			return fallback();
		}

		@Override
		public Object updateDownStatus(Integer id, String url, Integer status) {
			return fallback();
		}
    }
}





