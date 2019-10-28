package com.rondaful.cloud.seller.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.constant.SystemConstants;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.entity.AfterSalesApprovalModel;
import com.rondaful.cloud.seller.entity.OrderAfterSalesModel;
import com.rondaful.cloud.seller.remote.RemoteAfterSalesService;
import com.rondaful.cloud.seller.vo.OrderAfterSalesSerchVo;
import com.rondaful.cloud.seller.vo.OrderAfterSalesVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(description = "卖家售后(退款)查询")
@RestController
@RequestMapping("afterSales/refund")
public class OrderAfterSalesRefundController {

	@Resource
	private RemoteAfterSalesService remoteAfterSalesService;

	public String[] refundType = { "refund", "refundAndReturnedPurchase" };

	private String message = "服务异常";

	@AspectContrLog(descrption = "新增售后退款退货订单", actionType = SysLogActionType.ADD)
	@PostMapping("/addRefund")
	@ApiOperation(value = "新增售后退款退货订单")
	public void addRefund(@RequestBody OrderAfterSalesVo oasv) {
		checkParam(oasv);
		String type = refundType[0];
		String data = remoteAfterSalesService.addRefund(oasv);
		Utils.getResultData(data, SystemConstants.nameType.SYS_CMS, message);
	}

	@AspectContrLog(descrption = "修改售后退款退货订单", actionType = SysLogActionType.UDPATE)
	@PutMapping("/updateRefund/{numberingId}")
	@ApiOperation(value = "修改售后退款退货订单")
	public void updateRefund(@RequestBody OrderAfterSalesVo oasv, @ApiParam(value = "售后编码ID", name = "numberingId", required = true) @PathVariable String numberingId) {
		checkParam(oasv);
		String type = refundType[0];
		String data = remoteAfterSalesService.updateRefund(oasv, numberingId);
		Utils.getResultData(data, SystemConstants.nameType.SYS_CMS, message);
	}

	@AspectContrLog(descrption = "售后退款退货订单状态修改", actionType = SysLogActionType.UDPATE)
	@PutMapping("/updateRefundStatus/{numberingId}/{status}")
	@ApiOperation(value = "售后退款退货订单状态修改")
	public void updateRefundStatus(@ApiParam(value = "售后编号ID", name = "numberingId") @PathVariable String numberingId,
			@ApiParam(value = "状态", name = "status") @PathVariable int status, @RequestBody AfterSalesApprovalModel asam) {
		if(status != 17 && status != 11) //17、提交物流信息; 11、已取消; 
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "参数有误!");
		String type = refundType[0];
		String data = remoteAfterSalesService.updateRefundStatus(asam, status, numberingId);
		Utils.getResultData(data, SystemConstants.nameType.SYS_CMS, message);
	}

	/**
	 * 根据不同场景校验
	 * 
	 * @param oasv oasv
	 */
	private void checkParam(OrderAfterSalesVo oasv) {
		if (StringUtils.isBlank(oasv.getOrderId()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单号不能为空!");
		if (StringUtils.isBlank(oasv.getSeller()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家品连账号不能为空!");
		if (StringUtils.isBlank(oasv.getOrderDetails()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单详情不能为空!");
		if (StringUtils.isBlank(oasv.getPostage()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单邮费不能为空!");
		if (StringUtils.isBlank(oasv.getTrackingId()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "系统订单参考号不能为空!");
		if (StringUtils.isBlank(oasv.getRefundReason()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "退款原因不能为空!");
		if (oasv.getOrderCommodity().size() < 1)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "退款商品不能为空!");
	}

}
