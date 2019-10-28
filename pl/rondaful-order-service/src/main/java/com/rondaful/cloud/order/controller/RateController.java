package com.rondaful.cloud.order.controller;

import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.order.utils.RateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "汇率控制层")
@RestController
@RequestMapping(value = "/rate")
public class RateController extends BaseController {
    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RateController.class);
    @Autowired
    private RateUtil rateUtil;

    @AspectContrLog(descrption = "设置所有汇率到redis", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "设置所有汇率到redis")
    @GetMapping("/rateAll")
    public String setAllRateToRedids() {
        return rateUtil.getExchangeRate().toString();
    }


   /* @ApiOperation(value = "设置所有汇率到redis")
    @GetMapping("/testGetRate")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "调用哪个接口", name = "integer", dataType = "Integer", required = true),
            @ApiImplicitParam(paramType = "query", value = "货币代一", name = "str", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "货币代码二", name = "toStr", dataType = "String")
    })*/
   /* public String testGetRate(Integer integer, String str, String toStr) {
        if (integer == 1) {
            return rateUtil.getExchangeRate().toString();
        } else if (integer == 2) {
            rateUtil.initRateMessage();
            return null;
        } else if (integer == 3) {
            return rateUtil.remoteExchangeRateByCurrencyCode(str, toStr);
        }

        return null;
    }*/


    @ApiOperation(value = "查询汇率")
    @GetMapping("/GetRate")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "货币代一", name = "soStr", dataType = "String",required = true),
            @ApiImplicitParam(paramType = "query", value = "货币代码二", name = "toStr", dataType = "String",required = true)
    })
    public String testGetRate(String soStr, String toStr) {
        return rateUtil.remoteExchangeRateByCurrencyCode(soStr, toStr);
    }


}
