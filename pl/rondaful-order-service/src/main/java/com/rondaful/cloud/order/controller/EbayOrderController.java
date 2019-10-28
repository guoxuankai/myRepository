package com.rondaful.cloud.order.controller;

import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.quartz.QuartzSchedulerUtil;
import com.rondaful.cloud.order.service.IConverEbayOrderService;
import com.rondaful.cloud.order.service.ISyncEbayOrderService;
import com.rondaful.cloud.order.service.ISystemOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "管理后台eBay订单操作（同步、转化、查询）")
@RestController
@RequestMapping("/ebaySysPlatform")
public class EbayOrderController extends BaseController {
    @Autowired
    private ISyncEbayOrderService syncEbayOrderService;
    @Autowired
    private IConverEbayOrderService converEbayOrderService;
    @Autowired
    private ISystemOrderService systemOrderService;
    @Autowired
    private QuartzSchedulerUtil quartzScheduler;

    private static Logger LOGGER = LoggerFactory.getLogger(EbayOrderController.class);

    @GetMapping("/syncEbay")
    @ApiOperation(value = "开启同步eBay订单任务,定时触发的动作交给rondaful-task-scheduler服务", notes = "")
    public String syncEbay() throws Exception {
        LOGGER.info("rondaful-task-scheduler开始调用ebay订单同步任务");
        return syncEbayOrderService.syncEbayOrders();
    }

    @GetMapping("/autoSyncEbay")
    @ApiOperation(value = "开启自动同步eBay订单任务", notes = "")
    public void autoSyncEbay() throws Exception {
//        syncEbayOrderService.autoSyncEBayOrders();
    }

    @GetMapping("/pauseAutoSyncEbay")
    @ApiOperation(value = "暂停自动同步eBay订单任务", notes = "")
    public void pauseAutoSyncEbay() throws SchedulerException {
//        syncEbayOrderService.pauseAutoSyncEbay();
    }

    @GetMapping("/manualSycEbay")
    @ApiOperation(value = "手动同步eBay订单", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modTimeFrom", value = "同步ebay订单开始时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "modTimeTo", value = "同步ebay订单结束时间", dataType = "string", paramType = "query", required = false)})
    public void manualSycEbay(@RequestParam("modTimeFrom") String modTimeFrom, @RequestParam("modTimeTo") String modTimeTo) {
        syncEbayOrderService.manualSycEbay(modTimeFrom, modTimeTo);
    }

    @GetMapping("/convertEbayToSysOrder")
    @ApiOperation(value = "开启转换eBay订单任务,定时触发的动作交给rondaful-task-scheduler服务", notes = "")
    public String convertEbayToSysOrder() throws Exception {
        LOGGER.error("rondaful-task-scheduler开始调用ebay订单转换任务");
        return converEbayOrderService.convertEbayToSysOrder();
    }

    @GetMapping("/autoConverEbay")
    @ApiOperation("开启自动转化ebay平台订单任务")
    public void autoConverEbay() throws Exception {
//        converEbayOrderService.autoConverEBayToSys();
    }

    @GetMapping("/pauseAutoConverEbay")
    @ApiOperation("暂停自动转化ebay平台订单任务")
    public void pauseAutoConverEbay() throws SchedulerException {
//        converEbayOrderService.pauseAutoConverEbay();
    }

    @PostMapping("/manualConverEbay")
    @ApiOperation("手动转化ebay平台订单")
    @ApiImplicitParams(@ApiImplicitParam(name = "ebayOrders", value = "手工转ebay平台订单到系统", dataType = "ArrayList", paramType = "body"))
    public String manualConverEbay(@RequestBody List<EbayOrder> ebayOrders) throws Exception {
        return converEbayOrderService.manualConverEbay(ebayOrders);
    }

    @GetMapping(value = "/pauseAllJob")
    @ApiOperation("暂停所有定时任务JOB")
    public void pauseAllJob() throws SchedulerException {
//        converEbayOrderService.pauseAllJob();
        LOGGER.error("______________暂停所有定时任务______________");
    }

    @GetMapping(value = "/resumeAllJob")
    @ApiOperation("恢复所有任务JOB")
    public void resumeAllJob() throws SchedulerException {
//        quartzScheduler.resumeAllJob();
        LOGGER.error("______________恢复所有定时任务______________");
    }

    @GetMapping(value = "/resumeJob")
    @ApiOperation("恢复某个任务JOB")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "job任务名称", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "jobGroup", value = "job任务组名", dataType = "string", paramType = "query", required = false)})
    public void resumeJob(@RequestParam("jobName") String jobName, @RequestParam("jobGroup") String jobGroup) throws SchedulerException {
//        quartzScheduler.resumeJob(jobName, jobGroup);
        LOGGER.error("________________恢复任务" + jobName + "______________");
    }

    @GetMapping(value = "/resumeAutoSyncEbayJob")
    @ApiOperation("恢复自动同步eBay订单任务")
    public void resumeAutoSyncEbayJob() throws SchedulerException {
//        quartzScheduler.resumeJob("autoSyncEbayOrdersJob", "autoSyncEbayOrdersGroup");
        LOGGER.error("________________恢复自动同步eBay订单任务______________");
    }

    @GetMapping(value = "/resumeAutoConverEbayJob")
    @ApiOperation("恢复自动转化eBay订单任务")
    public void resumeAutoConverEbayJob() throws SchedulerException {
//        quartzScheduler.resumeJob("autoConverEbayOrdersJob", "autoConverEbayOrdersGroup");
        LOGGER.error("________________恢复自动转化eBay订单任务______________");
    }

    @GetMapping(value = "/deleteJob")
    @ApiOperation("删除某个定时任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "job名", dataType = "string", paramType = "path", required = true),
            @ApiImplicitParam(name = "jobGroup", value = "job组名", dataType = "string", paramType = "path", required = true)})
    public void deleteJob(String jobName, String jobGroup) throws SchedulerException {
//        converEbayOrderService.deleteJob(jobName, jobGroup);
    }

    @PostMapping("/queryEbayOrderList")
    @ApiOperation("ebay平台订单列表查询")
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

    @PostMapping("/updateEbayDetailDeliverStatus")
    @ApiOperation("修改ebay订单项发货状态")
    @ApiImplicitParams({@ApiImplicitParam(name = "orderLineItemId", value = "ebay订单项ID", paramType = "query", required = true)})
    public void updateEbayDetailDeliverStatus(String orderLineItemId) {
        systemOrderService.updateEbayDetailDeliverStatus(orderLineItemId);
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

    @GetMapping("/dealConvertFailEbayOrder")
    @ApiOperation("处理转换失败的订单")
    public void dealConvertFailEbayOrder() {
        converEbayOrderService.dealConvertFailEbayOrder();
    }

    @GetMapping("/testSyncEbayOrder")
    public void testSyncEbayOrder() {
        LOGGER.info("测试ebay抓单");
        syncEbayOrderService.testSyncEbayOrder();
    }
}
