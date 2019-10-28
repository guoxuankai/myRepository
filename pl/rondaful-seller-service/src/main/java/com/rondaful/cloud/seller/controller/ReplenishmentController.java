package com.rondaful.cloud.seller.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.constant.SystemConstants;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.dto.ReplenishmentDTO;
import com.rondaful.cloud.seller.remote.RemoteAfterSalesService;
import com.rondaful.cloud.seller.vo.AfterSalesApprovalVO;
import com.rondaful.cloud.seller.vo.ReplenishmentVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * 补货controller
 * @author songjie
 *
 */
@Api(description = "补发货相关接口")
@RestController
@RequestMapping("/replenishment")
public class ReplenishmentController{
	@Autowired
	private RemoteAfterSalesService cmsService;
	
	
	@PostMapping("/add")
	@ApiOperation(value = "新增补货订单")
	public void insert(@RequestBody ReplenishmentVO oasv) throws Exception {
		
		 Object obj = cmsService.insert(oasv);
		 Utils.returnRemoteResultDataString( JSONObject.toJSONString(obj),  SystemConstants.nameType.SYS_CMS.name());
	}
	
	@PostMapping("/update/{numberingId}")
	@ApiOperation(value = "编辑补货")
	public void update(@RequestBody ReplenishmentVO oasv,
			@ApiParam(value = "售后编码ID", name = "numberingId", required = true) @PathVariable String numberingId) throws Exception {
		Object update = cmsService.update(oasv,numberingId);
		Utils.returnRemoteResultDataString( JSONObject.toJSONString(update),  SystemConstants.nameType.SYS_CMS.name());
	}
	
	
	@GetMapping("/getReplenishment/{numberingId}")
	@ApiOperation(value = "根据售后编号ID查询详情")
	public ReplenishmentDTO findReplenishmentByNumberingId(
			@ApiParam(value = "售后编号ID", name = "numberingId") @PathVariable String numberingId)throws Exception {
		Object data = cmsService.findReplenishmentByNumberingId(numberingId);
		JSONObject js = (JSONObject) JSONObject.toJSON(data);
		if (js.containsKey("data")) {
			return JSONObject.parseObject(js.getJSONObject("data").toJSONString(), ReplenishmentDTO.class);
		}
		return null;
	}
	
	@PostMapping("/updateReplenishmentStatus/{numberingId}/{status}")
	@ApiOperation(value = "补货订单状态修改")
	public void updateReplenishmentStatus(@ApiParam(value = "售后编号ID", name = "numberingId") @PathVariable String numberingId,
			@ApiParam(value = "", name = "status") @PathVariable Integer status,
			@RequestBody AfterSalesApprovalVO asam) throws Exception {
		Object updateReplenishmentStatus = cmsService.updateReplenishmentStatus(asam, numberingId, status);
		Utils.returnRemoteResultDataString( JSONObject.toJSONString(updateReplenishmentStatus),  SystemConstants.nameType.SYS_CMS.name());
	}   

	@GetMapping("/updateStatus/{status}/{numberingId}")
	@ApiOperation(value = "根据id直接修改状态")
	public void updateStatus(@ApiParam(value = "补货编码ID", name = "numberingId", required = true) @PathVariable String numberingId,
							 @ApiParam(value = "状态码", name = "status", required = true) @PathVariable String status) throws Exception {
		Object updateStatus = cmsService.updateStatus(numberingId,status);
		Utils.returnRemoteResultDataString( JSONObject.toJSONString(updateStatus),  SystemConstants.nameType.SYS_CMS.name());
	}

}
