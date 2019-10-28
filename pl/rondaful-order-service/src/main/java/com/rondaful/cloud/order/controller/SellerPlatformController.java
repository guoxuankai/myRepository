package com.rondaful.cloud.order.controller;

import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.service.IConverEbayOrderService;
import com.rondaful.cloud.order.service.ISyncEbayOrderService;
import com.rondaful.cloud.order.service.ISystemOrderService;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "卖家平台eBay订单操作（同步、转化、查询）")
@RestController
@RequestMapping("/sellerPlatform")
public class SellerPlatformController extends BaseController {
    @Autowired
    private ISyncEbayOrderService syncEbayOrderService;
    @Autowired
    private IConverEbayOrderService converEbayOrderService;
    @Autowired
    private ISystemOrderService systemOrderService;

    private static Logger _log = LoggerFactory.getLogger(EbayOrderController.class);

    @GetMapping("/manualSycEbay")
    @ApiOperation(value = "卖家平台:手动同步eBay订单", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modTimeFrom", value = "同步ebay订单开始时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "modTimeTo", value = "同步ebay订单结束时间", dataType = "string", paramType = "query", required = false)})
    public void manualSycEbay(@RequestParam("modTimeFrom") String modTimeFrom, @RequestParam("modTimeTo") String modTimeTo) {
        syncEbayOrderService.manualSycEbay(modTimeFrom, modTimeTo);
    }

    @PostMapping("/manualConverEbay")
    @ApiOperation("卖家平台:手动转化ebay平台订单")
    @ApiImplicitParams(@ApiImplicitParam(name = "ebayOrders", value = "手工转ebay平台订单到系统", dataType = "ArrayList", paramType = "body"))
    public void manualConverEbay(@RequestBody List<EbayOrder> ebayOrders) throws Exception {
        converEbayOrderService.manualConverEbay(ebayOrders);
    }

    @PostMapping("/queryEbayOrderList")
    @ApiOperation("卖家平台:ebay平台订单列表查询")
    @RequestRequire(require = "page, row", parameter = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderStatus", value = "订单状态", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "converSysStatus", value = "订单转化状态", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "beginDate", value = "下单时间查询开始时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "下单时间查询结束时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "平台订单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "recordNumber", value = "平台订单编号", dataType = "string", paramType = "query")})
    public Page queryEbayOrderList(String page, String row, String orderStatus, String converSysStatus,
                                   String beginDate, String endDate, String orderId, String recordNumber) {
        Page.builder(page, row);
        UserCommon user = super.userToken.getUserInfo().getUser();
        Map<Integer, List<Integer>> map = super.getBinds(user);
        if (map == null) {
            return new Page(new ArrayList());
        }
        List<EbayOrder> ebayOrders = syncEbayOrderService.queryEbayOrderList(new HashMap() {{
            this.put("page", page);
            this.put("row", row);
            this.put("orderStatus", orderStatus);
            this.put("converSysStatus", converSysStatus);
            this.put("beginDate", beginDate);
            this.put("endDate", endDate);
            this.put("orderId", orderId);
            this.put("recordNumber", recordNumber);
            this.put("empowerIDList", map.get(1));
            this.put("plIDList", map.get(2));
        }});
        PageInfo pageInfo = new PageInfo(ebayOrders);
        return new Page(pageInfo);
    }

    @GetMapping("/queryEbayOrderDetail")
    @ApiOperation("ebay平台订单详情查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "orderId", value = "平台订单号", dataType = "string", paramType = "query", required = true)})
    public EbayOrder queryEbayOrderDetail(String orderId) {
        return syncEbayOrderService.queryEbayOrderDetail(orderId);
    }

    @GetMapping("/exportEbayOrderListExcel")
    @ApiOperation("导出ebay平台订单Excel表格")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderStatus", value = "订单状态", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "converSysStatus", value = "订单转化状态", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "beginDate", value = "下单时间查询开始时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "下单时间查询结束时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "平台订单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "orderNumber", value = "平台订单编号", dataType = "string", paramType = "query")})
    public void exportEbayExcel(HttpServletRequest request, HttpServletResponse response, String orderStatus, String converSysStatus,
                                String beginDate, String endDate, String orderId, String orderNumber) {
        systemOrderService.exportEbayOrderListExcel(new HashMap() {{
            this.put("orderStatus", orderStatus);
            this.put("converSysStatus", converSysStatus);
            this.put("beginDate", beginDate);
            this.put("endDate", endDate);
            this.put("orderId", orderId);
            this.put("orderNumber", orderNumber);
            this.put("request", request);
            this.put("response", response);
        }});
    }

    @PostMapping("/updateEbayDetailDeliverStatus")
    @ApiOperation("修改ebay订单项发货状态")
    @ApiImplicitParams({@ApiImplicitParam(name = "orderLineItemId", value = "ebay订单项ID", paramType = "query", required = true)})
    public void updateEbayDetailDeliverStatus(String orderLineItemId) {
        systemOrderService.updateEbayDetailDeliverStatus(orderLineItemId);
    }

    @PostMapping("/saveSplittedSysOrder")
    @ApiOperation("拆分订单操作后保存子订单集合")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "sysOrders", value = "拆分订单后保存被拆分系统订单子订单对象集合", paramType = "body", required = true)})
    public void saveSplittedSysOrder(@RequestBody List<SysOrder> sysOrders) throws Exception {
        systemOrderService.saveSplittedSysOrder(sysOrders);
    }

    @GetMapping("/cancelSplittedSysOrder")
    @ApiOperation("撤销已拆分的系统订单")
    @ApiImplicitParams({@ApiImplicitParam(name = "sysOrderId", value = "已拆分的系统订单号", paramType = "query", required = true)})
    public void cancelSplittedSysOrder(@RequestParam("sysOrderId") String sysOrderId) {
        systemOrderService.cancelSplittedSysOrder(sysOrderId);
    }

    @PostMapping("/saveMergedSysOrder")
    @ApiOperation("合并系统订单")
    @ApiImplicitParams({@ApiImplicitParam(name = "sysOrderIds", value = "合并系统订单的子订单", paramType = "body", required = true)})
    public void saveMergedSysOrder(@RequestBody List<String> sysOrderIds) throws Exception {
        systemOrderService.saveMergedSysOrder(sysOrderIds);
    }

    @GetMapping("/cancelMergedSysOrder")
    @ApiOperation("取消合并系统订单")
    @ApiImplicitParams({@ApiImplicitParam(name = "sysOrderId", value = "取消合并系统订单ID", paramType = "query", required = true)})
    public void cancelMergedSysOrder(String sysOrderId) {
        systemOrderService.cancelMergedSysOrder(sysOrderId);
    }

    @PostMapping("/deliverGoodSingle")
    @ApiOperation("单个订单发货")
    @ApiImplicitParams({@ApiImplicitParam(name = "sysOrderId", value = "请求发货的系统订单ID", paramType = "query", required = true)})
    public void deliverGoodSingle(String sysOrderId) throws Exception {
        systemOrderService.deliverGoodSingleNew(sysOrderId,false);
    }

    @PostMapping("/deliverGoodBatch")
    @ApiOperation("批量订单发货")
    @ApiImplicitParams({@ApiImplicitParam(name = "sysOrderIds", value = "请求发货的系统订单ID集合", paramType = "body", required = true)})
    public List<SysOrder> deliverGoodBatch(@RequestBody List<String> sysOrderIds) throws Exception {
        if (CollectionUtils.isEmpty(sysOrderIds) || sysOrderIds.size() == 0)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求发货订单ID不能为空。。。");
        if (sysOrderIds.size() == 1)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请调用单个发货接口。。。");
        return systemOrderService.deliverGoodBatch(sysOrderIds);
    }

    @GetMapping("/querySysOrderERPSpeedInfo/{sysOrderId}/{orderTrackId}")
    @ApiOperation("根据系统订单号查询ERP发货进度信息")
    public Map<Object, Object> querySysOrderShippingInfo(
            @ApiParam(name = "sysOrderId", value = "系统订单号")
            @PathVariable String sysOrderId,
            @ApiParam(name = "orderTrackId", value = "ERP订单跟踪号")
            @PathVariable String orderTrackId) throws Exception {
        return systemOrderService.querySysOrderERPSpeedInfo(new HashMap<String, String>() {{
            this.put("sysOrderId", sysOrderId);
            this.put("orderTrackId", orderTrackId);
        }});
    }
}
