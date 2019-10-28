package com.rondaful.cloud.supplier.remote;

import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.model.dto.FeignResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "rondaful-order-service", fallback = RemoteOrderService.RemoteOrderServiceImpl.class)
public interface RemoteOrderService {

	/**
	 * 获取汇率
	 * @param soStr 原货币代码
	 * @param toStr 转换后的货币代码
	 * @return 汇率
	 */
	@GetMapping("/rate/GetRate")
	String getRate(@RequestParam(value="soStr",defaultValue = "")String soStr,
				   @RequestParam(value="toStr",defaultValue = "")String toStr);


	@GetMapping("/sysOrder/getOrderInfoToSupplier")
	FeignResult getOrderInfoToSupplier(@RequestParam(value="packageId")String packageId);

	/**
	 * 获取汇率
	 * @param soStr 原货币代码
	 * @param toStr 转换后的货币代码
	 * @return 汇率
	 */
	@GetMapping("/rate/GetRate")
	FeignResult<BigDecimal> getRate1(@RequestParam(value="soStr",defaultValue = "CNY")String soStr,@RequestParam(value="toStr",defaultValue = "USD")String toStr);


	@Service
	class RemoteOrderServiceImpl implements RemoteOrderService {

		public FeignResult error(){
			return new FeignResult(false, ResponseCodeEnum.RETURN_CODE_100500.getCode(), "用户服务异常");
		}

		@Override
		public String getRate(String soStr, String toStr) {
			return null;
		}

		@Override
		public FeignResult getOrderInfoToSupplier(String packageId) {
			return error();
		}

		@Override
		public FeignResult<BigDecimal> getRate1(String soStr, String toStr) {
			return error();
		}
	}

}
