package com.rondaful.cloud.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.order.service.impl.GoodCangOrderInterceptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wh
 * @description 测试guc仓订单接口
 * @date 2019/4/26
 */
@Api(description = "自测谷仓取消订单控制层")
@RestController
@RequestMapping("/goodCang/testOrder/")
public class GoodCangOrderController {

    private Logger logger = LoggerFactory.getLogger(GoodCangOrderController.class);

    @Autowired
    private GoodCangOrderInterceptService goodCangOrderInterceptService;

    /**
     * 测试谷仓取消订单接口
     *
     * @param referenceId 谷仓订单号
     * @return integer orderStatus 取消订单状态码 2：拦截成功 , 3:拦截失败
     */
    @ApiOperation(value = "测试谷仓取消订单接口")
    @GetMapping("testCancelOrder")
    @ApiImplicitParam(name = "orderTrackId", value = "系统订单跟踪号", dataType = "String", paramType = "query", required = true)
    public Object testCancelOrder(String referenceId) {
        String reason = "拦截订单";
        try {
            logger.info("开始调用谷仓取消订单接口==============》");
//            return goodCangOrderInterceptService.cancelOrder(referenceId, reason);
            return null;
        } catch (Exception e) {
            logger.error("调用谷仓取消订单出现异常============================》");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统远程调用谷仓api异常");
        }
    }

    /**
     * 测试根据仓库code调用取消订单
     *
     * @param warehouseId 系统订单仓库ID
     * @param referenceId   对应reference_id推送至谷仓订单号
     * @return 拦截失败 or 拦截失败 or 走ERP拦截订单流程
     */
    @ApiOperation(value = "测试根据仓库code调用取消订单")
    @GetMapping("testWarehouseCancelOrder")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warehouseCode", value = "系统订单仓库code", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "referenceId", value = "对应reference_id推送至谷仓订单号", dataType = "string", paramType = "query", required = true)
    })
    public String testWarehouseCancelOrder(String warehouseId, String referenceId) {
        boolean b = goodCangOrderInterceptService.judgeGcWatrhouse(warehouseId);
        if (b) {
            String jsonData = goodCangOrderInterceptService.cancelOrder(referenceId, "", warehouseId);
            System.out.println("jsondata:" + jsonData);
            Integer cancelOrderStatus = JSONObject.parseObject(jsonData).getInteger("cancel_status");
            if ((Integer.valueOf(2)).equals(cancelOrderStatus)) {
                return "谷仓拦截成功";
            }
            return "谷仓拦截失败";
        }
        return "走ERP拦截订单流程";
    }
}
