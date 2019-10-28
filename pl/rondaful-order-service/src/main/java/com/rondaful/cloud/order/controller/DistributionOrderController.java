package com.rondaful.cloud.order.controller;

import com.rondaful.cloud.common.annotation.OpenAPI;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.CancelOrderDTO;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.enums.OrderCodeEnum;
import com.rondaful.cloud.order.model.xingShang.response.SysOrderPackageXS;
import com.rondaful.cloud.order.model.xingShang.response.SysOrderXS;
import com.rondaful.cloud.order.service.IDistributionOrderService;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author: zhangjinglei
 * @description: 星商订单相关接口
 * @date: 2019/5/4
 */
@Api(description = "星商订单控制层")
@RestController
@RequestMapping(value = "/distributionOrder")
public class DistributionOrderController extends BaseController {
    @Autowired
    private IDistributionOrderService distributionOrderService;

    private final static Logger _log = LoggerFactory.getLogger(DistributionOrderController.class);

    @OpenAPI
    @ApiOperation(value = "第三方供应商创建订单接口")
    @PostMapping("/createSysOrderForDistribution")
    public Map<String, String> createSysOrderForXS(SysOrder sysOrder, HttpServletRequest request) throws Exception {
        return distributionOrderService.createSysOrderForXSNew(request, sysOrder);
    }

    @OpenAPI
    @ApiOperation(value = "分销商通过品连订单号查询系统订单")
    @GetMapping("/queryDistributionSysOrderByID")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "platformShopId", value = "卖家店铺", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sysOrderId", value = "品连系统订单ID", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "sourceOrderId", value = "来源订单ID", dataType = "string", paramType = "query", required = true)})
    public SysOrderXS queryDistributionSysOrderByID(HttpServletRequest request, @RequestParam("platformShopId") Integer platformShopId,
                                                    @RequestParam("sysOrderId") String sysOrderId,
                                                    @RequestParam("sourceOrderId") String sourceOrderId) {
        return distributionOrderService.queryDistributionSysOrderByID(request, platformShopId, sysOrderId, sourceOrderId);
    }

    @OpenAPI
    @ApiOperation(value = "分销商取消订单")
    @PostMapping("/cancelDistributionOrder")
    public void cancelDistributionOrder(CancelOrderDTO cancelOrderDTO, HttpServletRequest request) throws Exception {
        _log.info("分销商取消订单参数CancelOrderDTO：{}", FastJsonUtils.toJsonString(cancelOrderDTO));
        String msg = distributionOrderService.cancelDistributionOrder(request, cancelOrderDTO.getPlatformShopId(), cancelOrderDTO.getSysOrderId(),
                cancelOrderDTO.getSourceOrderId(), cancelOrderDTO.getCancelReason());
        if (msg.equals(Constants.InterceptResponse.RESPONSE_1)) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300138);
        }
    }

    @OpenAPI
    @ApiOperation(value = "分销商通过品连订单号查询订单包裹信息")
    @GetMapping("/queryDistributionSysOrderPackageByOrderId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "platformShopId", value = "卖家店铺ID", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sysOrderId", value = "品连系统订单ID", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "sourceOrderId", value = "来源订单ID", dataType = "string", paramType = "query", required = true)})
    public List<SysOrderPackageXS> queryDistributionSysOrderPackageByID(Integer platformShopId,
                                                                        String sysOrderId, String sourceOrderId, HttpServletRequest request) {
        return distributionOrderService.queryDistributionSysOrderPackageByID(request, platformShopId, sysOrderId, sourceOrderId);
    }
}