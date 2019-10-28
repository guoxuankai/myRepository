package com.rondaful.cloud.order.controller;


import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.order.entity.goodcang.GoodCangSubscibe.GoodCangAccepDto;
import com.rondaful.cloud.order.service.IGoodCangService;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "拉取谷仓订单")
@RestController
@RequestMapping(value = "/goodCang")
public class GoodCangController {

    @Autowired
    private IGoodCangService goodCangService;


    private static Logger _log = LoggerFactory.getLogger(GoodCangController.class);

    @GetMapping(value = "/getGoodCangOrderList")
    public void getGoodCangOrderList() {

        try {

            _log.error("开启拉取谷仓列表定时任务开启");
            goodCangService.getGoodCangOrderList();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("调用谷仓获取订单列表出错", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "调用谷仓列表定时任务异常。。。" + e);
        }

        _log.info("开启拉取谷仓列表定时任务结束");
    }

    @GetMapping(value = "/getOrderByCode")   // TODO  暂时没用
    public void getOrderByCode(String orderCode, String referenceNo) {
        try {
            goodCangService.getOrderByCodeAndUpdateStatus(orderCode, referenceNo);
        } catch (Exception e) {
            _log.error("调用谷仓根据订单号获取单票订单信息异常：", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "调用谷仓根据订单号获取单票订单信息。。。" + e);
        }
    }

    @AspectContrLog(descrption = "谷仓API推送订阅", actionType = SysLogActionType.QUERY)
    @ApiOperation("谷仓API推送订阅，接收谷仓推送过来的数据")
    @PostMapping(value = "/gcAPISubscribe")
    public String goodCangAPISubscribe(GoodCangAccepDto dto) {
        Map<String, String> map = new HashMap<>(4);
        _log.info("谷仓API推送订阅，接收谷仓推送过来的数据: {}", FastJsonUtils.toJsonString(dto));
        try {
            goodCangService.goodCangAPISubscribe(dto);
            map.put("Status", "SUCCESS");
            map.put("ErrorMessage", "");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("Status", "FAILED");
            map.put("ErrorMessage", "同步失败");
            _log.error("_谷仓API推送订阅，接收谷仓推送过来的数据异常", e);
        }
        return FastJsonUtils.toJsonString(map);
    }
}
