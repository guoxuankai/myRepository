package com.rondaful.cloud.supplier.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.supplier.entity.DeliveryDetail;
import com.rondaful.cloud.supplier.entity.DeliveryRecord;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.service.IDeliveryRecordService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(description = "出库记录")
@RestController
@RequestMapping(value = "/delivery")
public class DeliveryRecordController {

	private final Logger log = LoggerFactory.getLogger(DeliveryRecordController.class);
	@Autowired
	private IDeliveryRecordService deliveryRecordService;
	
	@AspectContrLog(descrption = "查询出库记录",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "查询出库记录", notes = "page当前页码，row每页显示行数", response = DeliveryRecord.class)
	@PostMapping("/findDeliveryRecord")
	@RequestRequire(require = "page, row", parameter = String.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "页码" , required = true, dataType = "string", paramType = "query" ),
        @ApiImplicitParam(name = "row", value = "每页显示行数", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "deliveryId", value = "出库单号", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "sysOrderId", value = "订单号", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "orderStatus", value = "前端传值：2：已收货，1：已发货，0：已退货", required = false, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "choiceType", value = "选择日期类型 0：创建时间，1：收货时间，2：退货时间", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "beginDate", value = "开始时间", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "endDate", value = "结束时间", required = false, dataType = "string", paramType = "query")})
	public Page<DeliveryRecord> findDeliveryRecord(String page, String row,String choiceType, String deliveryId,String sysOrderId,Integer orderStatus,String beginDate,String endDate) {
		Page.builder(page, row);
		DeliveryRecord record=new DeliveryRecord();
		Page<DeliveryRecord> p=null;
		if (StringUtils.isNotBlank(deliveryId)) {
			record.setDeliveryId(deliveryId);
		}
		if (StringUtils.isNotBlank(sysOrderId)) {
			record.setSysOrderId(sysOrderId);
		}
		if(orderStatus!=null) {
				record.setOrderStatus(orderStatus);
		}
		if(StringUtils.isNotBlank(choiceType)) {
			record.setChoiceType(choiceType);
		}
		
		if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {
			
				record.setBeginDate(beginDate);
				record.setEndDate(endDate);
		} 
		try {
			
			 p = deliveryRecordService.page(record);
		} catch (Exception e) {
			log.error("出库记录异常：{}",e);
			e.printStackTrace();
		}
			
		
		return p;
	}
	
	@AspectContrLog(descrption = "查询出库记录详情",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "查询出库记录详情", notes = "")
	@PostMapping("/findDeliveryDetail")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "deliveryId", value = "出库单号", required = true, dataType = "string", paramType = "query")})
	public List<DeliveryDetail> findDeliveryDetail(String deliveryId) {
		if(deliveryId.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "出库单号不能为空");
		List<DeliveryDetail> dlList=null;
		try {
			 dlList= deliveryRecordService.getDeliveryDetail(deliveryId);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dlList;
	}
	
	@AspectContrLog(descrption = "导出出库记录",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "导出出库记录", notes = "")
	@GetMapping("/exportDeliveryRecord")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "deliveryId", value = "出库单号", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "sysOrderId", value = "订单号", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "orderStatus", value = "前端传值：2：已完成，1：已发货，0：已退货", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "choiceType", value = "选择日期类型 0：创建时间，1：收货时间，2：退货时间", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "beginDate", value = "开始时间", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "endDate", value = "结束时间", required = false, dataType = "string", paramType = "query")})
	public void exportDeliveryRecord(HttpServletResponse response,String choiceType,String deliveryId,String sysOrderId,Integer orderStatus,String beginDate,String endDate) {
		log.info("响应参数：{}",response.getLocale());
		DeliveryRecord record=new DeliveryRecord();
		if (StringUtils.isNotBlank(deliveryId)) {
			record.setDeliveryId(deliveryId);
		}
		if (StringUtils.isNotBlank(sysOrderId)) {
			record.setSysOrderId(sysOrderId);
		}
		if(orderStatus!=null) {
			if(StringUtils.isNotBlank(orderStatus.toString())) {
				record.setOrderStatus(orderStatus);
			}
		}
		if(StringUtils.isNotBlank(choiceType)) {
			record.setChoiceType(choiceType);
		}
		if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {
				record.setBeginDate(beginDate);
				record.setEndDate(endDate);
		}
			try {
				deliveryRecordService.exportDeliveryRecordExcel(record,response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	@AspectContrLog(descrption = "查询出库报表统计",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "查询出库报表统计", notes = "")
	@PostMapping("/selectDeliveryReport")
	public DeliveryRecord selectSkuCount() {
		DeliveryRecord record=null;
		try {
			record= deliveryRecordService.getDeliveryCount();
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return record;
	}
}
