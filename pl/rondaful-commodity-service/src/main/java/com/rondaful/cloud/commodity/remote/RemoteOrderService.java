package com.rondaful.cloud.commodity.remote;


import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.entity.Result;
import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 远程调用订单服务
 * */
@FeignClient(name = "rondaful-order-service", fallback = RemoteOrderService.RemoteOrderServiceImpl.class)
public interface RemoteOrderService {

	@GetMapping("/rate/GetRate")
	public Object getRate(@RequestParam("soStr")String soStr,@RequestParam("toStr") String toStr);
	
	@GetMapping("/skuMap/queryMapsNoLimit")
	public Object queryMapsNoLimit(@RequestParam("plSku")String plSku);
	

    /**
     * 断路降级
     * */
    @Service
    class RemoteOrderServiceImpl implements RemoteOrderService {

        public Object fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "订单服务异常"));
        }

		@Override
		public Object getRate(String soStr, String toStr) {
			return fallback();
		}

		@Override
		public Object queryMapsNoLimit(String plSku) {
			return fallback();
		}
    }
}





