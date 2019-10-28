package com.brandslink.cloud.logistics.controller.logistics;

import com.brandslink.cloud.logistics.thirdLogistics.RemoteYunTuLogisticsService;
import com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu.YunTuOrder;
import com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu.YunTuUser;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangjinglei
 */
@Api("云途物流接口")
@RestController
@RequestMapping("/yuntu")
public class LogisticsYunTuController {

    @Autowired
    private RemoteYunTuLogisticsService yunTuService;

    private final static Logger _log = LoggerFactory.getLogger(LogisticsYunTuController.class);

    @GetMapping("/getCountry")
    @ApiOperation(value = "查询国家简码")
    public String getCountry() throws Exception {
        return yunTuService.getCountry();
    }

    @GetMapping("/getShippingMethods")
    @ApiOperation(value = "查询运输方式")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "countryCode", value = "国家简码", dataType = "String")})
    public String getShippingMethods(String countryCode) throws Exception {
        return yunTuService.getShippingMethods(countryCode);
    }

    @GetMapping("/getGoodsType")
    @ApiOperation(value = "查询货品类型")
    public String getGoodsType() throws Exception {
        return yunTuService.getGoodsType();
    }

    @GetMapping("/getPriceTrial")
    @ApiOperation(value = "查询价格")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "countryCode", value = "国家简码", dataType = "String", required = true),
            @ApiImplicitParam(paramType = "query", name = "weight", value = "包裹重量，单位kg,支持3位小数", dataType = "bigdecimal", required = true),
            @ApiImplicitParam(paramType = "query", name = "length", value = "包裹长度,单位cm,不带小数", dataType = "int", required = true),
            @ApiImplicitParam(paramType = "query", name = "width", value = "包裹宽度,单位cm,不带小数", dataType = "int", required = true),
            @ApiImplicitParam(paramType = "query", name = "height", value = "包裹高度,单位cm,不带小数", dataType = "int", required = true),
            @ApiImplicitParam(paramType = "query", name = "packageType", value = "包裹类型，1-包裹，2-文件，3-防水袋，默认1", dataType = "int", required = true)})
    public String getPriceTrial(@RequestParam String countryCode, @RequestParam BigDecimal weight, @RequestParam Integer length,
                                @RequestParam Integer width, @RequestParam Integer height, @RequestParam Integer packageType) throws Exception {
        return yunTuService.getPriceTrial(countryCode, weight, length, width, height, packageType);
    }

    @GetMapping("/getTrackingNumber")
    @ApiOperation(value = "查询跟踪号")
    public String getTrackingNumber(@ApiParam(name = "orderNumbers", value = "客户订单号数组，多个逗号隔开", required = true) @RequestParam("orderNumbers") List<String> orderNumbers) throws Exception {
        return yunTuService.getTrackingNumber(orderNumbers);
    }

    @GetMapping("/getSender")
    @ApiOperation(value = "查询发件人信息（单个）")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "orderNumber", value = "物流系统运单号，客户订单或跟踪号", dataType = "String", required = true)})
    public String getSender(String orderNumber) throws Exception {
        return yunTuService.getSender(orderNumber);
    }

    @PostMapping("/createOrder")
    @ApiOperation(value = "创建云途物流运单")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "orders", value = "订单数据对象", required = true, allowMultiple = true, dataType = "YunTuOrder")})
    public String createOrder(@RequestBody List<YunTuOrder> orders) throws Exception {
        return yunTuService.createOrder(orders);
    }

    @GetMapping("/getOrder")
    @ApiOperation(value = "查询运单")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "orderNumber", value = "物流系统运单号，客户订单或跟踪号", dataType = "String", required = true)})
    public String getOrder(String orderNumber) throws Exception {
        return yunTuService.getOrder(orderNumber);
    }

    @PostMapping("/updateWeight")
    @ApiOperation(value = "修改云途物流运单预报重量")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderNumber", value = "订单号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "weight", value = "修改重量", required = true, dataType = "bigdecimal")})
    public String updateWeight(String orderNumber, BigDecimal weight) throws Exception {
        return yunTuService.updateWeight(orderNumber, weight);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除云途物流运单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderNumber", value = "单号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "orderType", value = "单号类型：1-云途单号,2-客户订单号,3-跟踪号", required = true, dataType = "int")})
    public String delete(String orderNumber, Integer orderType) throws Exception {
        return yunTuService.delete(orderNumber, orderType);
    }

    @PostMapping("/intercept")
    @ApiOperation(value = "拦截云途物流运单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderNumber", value = "单号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "orderType", value = "单号类型：1-云途单号,2-客户订单号,3-跟踪号", required = true, dataType = "int"),
            @ApiImplicitParam(paramType = "query", name = "remark", value = "拦截原因", required = true, dataType = "String")})
    public String intercept(String orderNumber, Integer orderType, String remark) throws Exception {
        return yunTuService.intercept(orderNumber, orderType, remark);
    }

    @PostMapping("/print")
    @ApiOperation(value = "批量打印运单标签")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "orderNumbers", value = "客户订单数组", required = true, allowMultiple = true, dataType = "String")})
    public String print(@RequestBody List<String> orderNumbers) throws Exception {
        return yunTuService.print(orderNumbers);
    }

    @GetMapping("/getShippingFeeDetail")
    @ApiOperation(value = "查询云途物流运单运费明细")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "wayBillNumber", value = "运单号", required = true, dataType = "String")})
    public String getShippingFeeDetail(String wayBillNumber) throws Exception {
        return yunTuService.getShippingFeeDetail(wayBillNumber);
    }

    @PostMapping("/register")
    @ApiOperation(value = "注册云途物流用户")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "user", value = "注册云途物流用户数据对象", required = true, dataType = "YunTuUser")})
    public String register(@RequestBody YunTuUser user) throws Exception {
        return yunTuService.register(user);
    }

    @GetMapping("/getTrackInfo")
    @ApiOperation(value = "查询物流轨迹信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "orderNumber", value = "订单号", required = true, dataType = "String")})
    public String getTrackInfo(String orderNumber) throws Exception {
        return yunTuService.getTrackInfo(orderNumber);
    }
}
