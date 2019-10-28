package com.brandslink.cloud.logistics.controller.logistics;

import com.brandslink.cloud.logistics.thirdLogistics.bean.MiaoXin.MiaoXinOrder;
import com.brandslink.cloud.logistics.thirdLogistics.bean.MiaoXin.MiaoXinPrintVO;
import com.brandslink.cloud.logistics.thirdLogistics.RemoteMiaoXinLogisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;


/**
 * @author zhangjinglei
 */
@Api("淼信物流接口")
@RestController
@RequestMapping("/miaoxin")
public class LogisticsMiaoXinController {

    @Autowired
    private RemoteMiaoXinLogisticsService miaoXinService;

    @PostMapping("/selectAuth")
    @ApiOperation(value = "身份认证")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "username", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "password", value = "密码", required = true, dataType = "String")})
    public String selectAuth(String username, String password) {
        return miaoXinService.selectAuth(username, password);
    }

    @PostMapping("/getProductList")
    @ApiOperation(value = "查询渠道列表")
    public String getProductList() {
        return miaoXinService.getProductList();
    }

    @PostMapping("/createOrderApi")
    @ApiOperation(value = "创建淼信物流运单")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "order", value = "创建淼信物流运单数据对象", required = true, dataType = "MiaoXinOrder")})
    public String createOrderApi(@RequestBody MiaoXinOrder order) throws UnsupportedEncodingException {
        return miaoXinService.createOrderApi(order);
    }

    @PostMapping("/createOrderBatchApi")
    @ApiOperation(value = "批量创建淼信物流运单")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "orders", value = "批量创建淼信物流运单数据对象集合", required = true, allowMultiple = true, dataType = "MiaoXinOrder")})
    public String createOrderBatchApi(@RequestBody List<MiaoXinOrder> orders) throws UnsupportedEncodingException {
        return miaoXinService.createOrderBatchApi(orders);
    }

    @PostMapping("/postOrderApi")
    @ApiOperation(value = "标记发货")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "customer_id", value = "客户ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "order_customerinvoicecode", value = "原单号", required = true, dataType = "String")})
    public String postOrderApi(String customer_id, String order_customerinvoicecode) {
        return miaoXinService.postOrderApi(customer_id, order_customerinvoicecode);
    }

    @PostMapping("/printLabel")
    @ApiOperation(value = "打印标签")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "vo", value = "打印标签数据对象", required = true, dataType = "MiaoXinPrintVO")})
    public String printLabel(@RequestBody MiaoXinPrintVO vo) throws Exception {
        return miaoXinService.printLabel(vo);
    }

    @PostMapping("/selectLabelType")
    @ApiOperation(value = "获取打印类型")
    public String selectLabelType() {
        return miaoXinService.selectLabelType();
    }

    @PostMapping("/selectTrack")
    @ApiOperation(value = "轨迹查询")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "documentCode", value = "订单号", required = true, dataType = "String")})
    public String selectTrack(String documentCode) {
        return miaoXinService.selectTrack(documentCode);
    }

    @PostMapping("/getOrderTrackingNumber")
    @ApiOperation(value = "获取跟踪号")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "documentCode", value = "订单号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "order_id", value = "订单ID", dataType = "long")})
    public String getOrderTrackingNumber(String documentCode, Long order_id) {
        return miaoXinService.getOrderTrackingNumber(documentCode, order_id);
    }

    @PostMapping("/updateOrderWeightByApi")
    @ApiOperation(value = "更新预报重量")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "customerId", value = "客户ID", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "orderNo", value = "订单号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "weight", value = "重量", dataType = "bigdecimal")})
    public String updateOrderWeightByApi(String customerId, String orderNo, BigDecimal weight) {
        return miaoXinService.updateOrderWeightByApi(customerId, orderNo, weight);
    }

    @PostMapping("/modifyInsurance")
    @ApiOperation(value = "修改保险金额")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "customerId", value = "客户ID", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "documentCode", value = "订单号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "insuranceValue", value = "保险金额", dataType = "bigdecimal")})
    public String modifyInsurance(String customerId, String documentCode, BigDecimal insuranceValue) {
        return miaoXinService.modifyInsurance(customerId, documentCode, insuranceValue);
    }
}
