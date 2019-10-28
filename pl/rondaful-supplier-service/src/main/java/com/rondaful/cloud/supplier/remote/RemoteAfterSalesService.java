package com.rondaful.cloud.supplier.remote;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.vo.OrderAfterSalesReceiptMessageVo;


@FeignClient(name = "rondaful-cms-service", fallback = RemoteAfterSalesService.RemoteAfterSalesServiceImpl.class)
public interface RemoteAfterSalesService {

	@GetMapping("/afterSales/getOrderBySupplier")
	public String findAfterSalesPage(@RequestParam("json") JSONObject json);

	@GetMapping("/afterSales/supplierReport")
	public ResponseEntity<byte[]> supplierReport(@RequestParam("json") JSONObject json);

	@GetMapping("/afterSales/getConfirmBySupplier")
	public String getConfirmBySupplier(@RequestParam("json") JSONObject json);

	@GetMapping("/afterSales/findSupplierConfirmDetailsByOrderId/{orderId}")
	public String findSupplierConfirmDetailsByOrderId(@PathVariable("orderId") String orderId);

	@PostMapping("/afterSales/supplierConfirm/{numberingId}/{type}")
	public String supplierConfirm(@PathVariable("numberingId") String numberingId, @PathVariable("type") Integer type, @RequestBody List<OrderAfterSalesReceiptMessageVo> list);

	@GetMapping("/replenishment/getSupplierReplenishment/{numberingId}")
	public Object findSupplierReplenishmentByNumberingId(@PathVariable("numberingId") String numberingId);

	@GetMapping("/replenishment/getSupplierReplenishmentByOrderId/{orderId}")
    Object findSupplierReplenishmentByOrderId(@PathVariable("orderId") String orderId);

	@GetMapping("/afterSales/findSupplierConfirmDetailsByOrderIdAndSku/{orderTrackId}/{commoditySku}")
	public String findSupplierConfirmDetailsByOrderIdAndSku(@PathVariable("orderTrackId") String orderTrackId,@PathVariable("commoditySku") String commoditySku);


	@GetMapping("/replenishment/getSupplierReplenishmentByOrderIdAndSku/{orderTrackId}/{commoditySku}")
	Object findSupplierReplenishmentByOrderIdAndSku(@PathVariable("orderTrackId")String orderTrackId,@PathVariable("commoditySku") String commoditySku);



	@Service
	class RemoteAfterSalesServiceImpl implements RemoteAfterSalesService {

		@Override
		public String findAfterSalesPage(JSONObject json) {
			return null;
		}

		public Object fallback() {
			return JSONObject.toJSON(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "品连后台服务异常"));
		}

		@Override
		public ResponseEntity<byte[]> supplierReport(JSONObject json) {
			return null;
		}

		@Override
		public String getConfirmBySupplier(JSONObject json) {
			return null;
		}

		@Override
		public String findSupplierConfirmDetailsByOrderId(String orderId) {
			return null;
		}

		@Override
		public String supplierConfirm(String numberingId, Integer type, List<OrderAfterSalesReceiptMessageVo> list) {
			return null;
		}

		@Override
		public Object findSupplierReplenishmentByNumberingId(String numberingId) {
			// TODO Auto-generated method stub
			return fallback();
		}

		@Override
		public Object findSupplierReplenishmentByOrderId(String orderId) {
			return fallback();
		}

		@Override
		public String findSupplierConfirmDetailsByOrderIdAndSku(String orderId, String commoditySku) {
			return null;
		}

		@Override
		public Object findSupplierReplenishmentByOrderIdAndSku(String orderId, String commoditySku) {
			return null;
		}

	}

}
