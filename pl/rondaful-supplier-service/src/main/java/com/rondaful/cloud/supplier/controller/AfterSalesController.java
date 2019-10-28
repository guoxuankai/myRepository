package com.rondaful.cloud.supplier.controller;

import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.constant.SystemConstants;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.utils.UserUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.dto.SupplierReplenishmentDTO;
import com.rondaful.cloud.supplier.entity.OrderAfterSalesModel;
import com.rondaful.cloud.supplier.entity.OrderAfterSalesSupplierConfirmDetailsModel;
import com.rondaful.cloud.supplier.entity.OrderAfterSalesSupplierConfirmModel;
import com.rondaful.cloud.supplier.remote.RemoteAfterSalesService;
import com.rondaful.cloud.supplier.vo.OrderAfterSalesReceiptMessageVo;
import com.rondaful.cloud.supplier.vo.OrderAfterSalesSerchVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(description = "供应商售后查询")
@RestController
public class AfterSalesController extends BaseController {

	private String message = "服务异常";

	@Resource
	private RemoteAfterSalesService remoteAfterSalesService;

	@Resource
	private UserUtils userUtils;

	@AspectContrLog(descrption = "查询供应商售后对应订单", actionType = SysLogActionType.QUERY)
	@SuppressWarnings("unchecked")
	@ApiOperation(value = "查询供应商售后对应订单")
	@GetMapping("/afterSales/findOrderBySupplier")
	public Page<OrderAfterSalesModel> findOrderBySupplier(OrderAfterSalesSerchVo oassv) {
		if (oassv.getStatus() != null && oassv.getStatus().intValue() != 5 && oassv.getStatus().intValue() != 7 && oassv.getStatus().intValue() != 10)
			oassv.setStatus(null);
		JSONObject json = (JSONObject) JSONObject.toJSON(oassv);
		String data = remoteAfterSalesService.findAfterSalesPage(json);
		data = Utils.getResultData(data, SystemConstants.nameType.SYS_CMS, message);
		return new Page<OrderAfterSalesModel>(JSONObject.parseObject(JSONObject.parseObject(data).getJSONObject("pageInfo").toJSONString(), PageInfo.class));
	}

	@AspectContrLog(descrption = "供应商报表导出", actionType = SysLogActionType.QUERY)
	@GetMapping("/afterSales/supplierReport")
	@ApiOperation(value = "供应商报表导出")
	public ResponseEntity<byte[]> supplierReport(OrderAfterSalesSerchVo param) throws IOException {
		JSONObject json = (JSONObject) JSONObject.toJSON(param);
		return remoteAfterSalesService.supplierReport(json);
	}

	@AspectContrLog(descrption = "分页查询供应商确认列表", actionType = SysLogActionType.QUERY)
	@SuppressWarnings({ "unchecked" })
	@GetMapping("/getConfirmBySupplier")
	@ApiOperation(value = "分页查询供应商确认列表")
	public Page<OrderAfterSalesSupplierConfirmModel> getConfirmBySupplier(OrderAfterSalesSerchVo param) {
		JSONObject json = (JSONObject) JSONObject.toJSON(param);
		String data = remoteAfterSalesService.getConfirmBySupplier(json);
		data = Utils.getResultData(data, SystemConstants.nameType.SYS_CMS, message);
		return new Page<OrderAfterSalesSupplierConfirmModel>(JSONObject.parseObject(JSONObject.parseObject(data).getJSONObject("pageInfo").toJSONString(), PageInfo.class));
	}

//	@AspectContrLog(descrption = "分页查询供应商退款确认列表", actionType = SysLogActionType.QUERY)
//	@SuppressWarnings({ "unchecked" })
//	@GetMapping("/getConfirmBySupplierRefundMoney")
//	@ApiOperation(value = "分页查询供应商退款确认列表")
//	public Page<OrderAfterSalesSupplierConfirmModel> getConfirmBySupplierRefundMoney(OrderAfterSalesSerchVo param) {
//		param.setAfterSalesType(1L);
//		String data=getConfirmBySupplierParam(param);
//		return new Page<OrderAfterSalesSupplierConfirmModel>(JSONObject.parseObject(JSONObject.parseObject(data).getJSONObject("pageInfo").toJSONString(), PageInfo.class));
//	}
//	@AspectContrLog(descrption = "分页查询供应商退货确认列表", actionType = SysLogActionType.QUERY)
//	@SuppressWarnings({ "unchecked" })
//	@GetMapping("/getConfirmBySupplierRefundMoneyAndGoods")
//	@ApiOperation(value = "分页查询供应商确认列表")
//	public Page<OrderAfterSalesSupplierConfirmModel> getConfirmBySupplierRefundMoneyAndGoods(OrderAfterSalesSerchVo param) {
//		param.setAfterSalesType(2L);
//		String data=getConfirmBySupplierParam(param);
//		return new Page<OrderAfterSalesSupplierConfirmModel>(JSONObject.parseObject(JSONObject.parseObject(data).getJSONObject("pageInfo").toJSONString(), PageInfo.class));
//	}
//	@AspectContrLog(descrption = "分页查询供应商补发货确认列表", actionType = SysLogActionType.QUERY)
//	@SuppressWarnings({ "unchecked" })
//	@GetMapping("/getConfirmBySupplierReplenishment")
//	@ApiOperation(value = "分页查询供应商补发货确认列表")
//	public Page<OrderAfterSalesSupplierConfirmModel> getConfirmBySupplierReplenishment(OrderAfterSalesSerchVo param) {
//		param.setAfterSalesType(3L);
//		String data=getConfirmBySupplierParam(param);
//		return new Page<OrderAfterSalesSupplierConfirmModel>(JSONObject.parseObject(JSONObject.parseObject(data).getJSONObject("pageInfo").toJSONString(), PageInfo.class));
//	}


	@AspectContrLog(descrption = "供应商根据订单ID查询详情", actionType = SysLogActionType.QUERY)
	@GetMapping("/findSupplierConfirmDetailsByOrderId/{orderId}")
	@ApiOperation(value = "供应商根据订单ID查询详情")
	public OrderAfterSalesSupplierConfirmDetailsModel findSupplierConfirmDetailsByOrderId(@ApiParam(value = "订单ID", name = "orderId") @PathVariable String orderId) {
		String data = remoteAfterSalesService.findSupplierConfirmDetailsByOrderId(orderId);
		data = Utils.getResultData(data, SystemConstants.nameType.SYS_CMS, message);
		return JSONObject.parseObject(data, OrderAfterSalesSupplierConfirmDetailsModel.class);
	}

	@AspectContrLog(descrption = "供应商根据包裹号和sku查询详情", actionType = SysLogActionType.QUERY)
	@GetMapping("/findSupplierConfirmDetailsByOrderIdAndSku/{orderTrackId}/{commoditySku}")
	@ApiOperation(value = "供应商根据包裹号和sku查询详情")
	public OrderAfterSalesSupplierConfirmDetailsModel findSupplierConfirmDetailsByOrderIdAndSku(@ApiParam(value = "包裹号", name = "orderTrackId") @PathVariable String orderTrackId,
																								@ApiParam(value = "商品sku", name = "commoditySku") @PathVariable String commoditySku
	) {
		String data = remoteAfterSalesService.findSupplierConfirmDetailsByOrderIdAndSku(orderTrackId,commoditySku);
		data = Utils.getResultData(data, SystemConstants.nameType.SYS_CMS, message);
		return JSONObject.parseObject(data, OrderAfterSalesSupplierConfirmDetailsModel.class);
	}


	@AspectContrLog(descrption = "供应商确认接口", actionType = SysLogActionType.UDPATE)
	@PostMapping("/supplierConfirm/{numberingId}/{type}")
	@ApiOperation(value = "供应商确认接口(确认退货时需要添加list数据，其他不需要为[])")
	public void supplierConfirm(@ApiParam(value = "售后ID", name = "numberingId", required = true) @PathVariable String numberingId, @ApiParam(value = "类型[1退款、2收货、3补发]", name = "type", required = true) @PathVariable Integer type, @RequestBody List<OrderAfterSalesReceiptMessageVo> list) {
		String data = remoteAfterSalesService.supplierConfirm(numberingId, type, list);
		data = Utils.getResultData(data, SystemConstants.nameType.SYS_CMS, message);
	}
	
	
	@AspectContrLog(descrption = "供应商详情接口", actionType = SysLogActionType.UDPATE)
	@GetMapping("/supplierConfirm/{numberingId}")
	@ApiOperation(value = "供应商详情接口")
	public SupplierReplenishmentDTO get(@PathVariable("numberingId") String numberingId){
		Object data = remoteAfterSalesService.findSupplierReplenishmentByNumberingId(numberingId);
		JSONObject js = (JSONObject) JSONObject.toJSON(data);
		if (js.containsKey("data")) {
			return JSONObject.parseObject(js.getJSONObject("data").toJSONString(), SupplierReplenishmentDTO.class);
		}
		return null;
	}
	@AspectContrLog(descrption = "供应商详情接口根据订单Id查询", actionType = SysLogActionType.UDPATE)
	@GetMapping("/supplierConfirmByOrderId/{orderId}")
	@ApiOperation(value = "供应商详情接口根据订单Id查询")
	public SupplierReplenishmentDTO getByOrderId(@PathVariable("orderId") String orderId){
		Object data = remoteAfterSalesService.findSupplierReplenishmentByOrderId(orderId);
		JSONObject js = (JSONObject) JSONObject.toJSON(data);
		if (js.containsKey("data")) {
			return JSONObject.parseObject(js.getJSONObject("data").toJSONString(), SupplierReplenishmentDTO.class);
		}
		return null;
	}

	@AspectContrLog(descrption = "根据包裹号和sku查询供应商返回详情接口", actionType = SysLogActionType.UDPATE)
	@GetMapping("/supplierConfirmByOrderIdAndSku/{orderTrackId}/{commoditySku}")
	@ApiOperation(value = "根据包裹号和sku查询供应商返回详情接口")
	public SupplierReplenishmentDTO getByOrderIdAndSku(
			@ApiParam(value = "包裹号", name = "orderTrackId") @PathVariable String orderTrackId,
			@ApiParam(value = "商品sku", name = "commoditySku") @PathVariable String commoditySku
	){
		Object data = remoteAfterSalesService.findSupplierReplenishmentByOrderIdAndSku(orderTrackId,commoditySku);
		JSONObject js = (JSONObject) JSONObject.toJSON(data);
		if (js.containsKey("data")) {
			return JSONObject.parseObject(js.getJSONObject("data").toJSONString(), SupplierReplenishmentDTO.class);
		}
		return null;
	}


//	private String getConfirmBySupplierParam(OrderAfterSalesSerchVo param){
//		JSONObject json = (JSONObject) JSONObject.toJSON(param);
//		String data = remoteAfterSalesService.getConfirmBySupplier(json);
//		return Utils.getResultData(data, SystemConstants.nameType.SYS_CMS, message);
//
//	}

}
