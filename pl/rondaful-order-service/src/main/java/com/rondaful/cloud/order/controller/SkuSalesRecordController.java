package com.rondaful.cloud.order.controller;

import com.rondaful.cloud.common.annotation.RequestRequire;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.order.entity.SkuSalesRecord;
import com.rondaful.cloud.order.service.ISkuSalesRecordService;
import com.rondaful.cloud.order.utils.GetLoginInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


@Api(description = "sku销售记录控制层")
@RestController
@RequestMapping(value = "/skuSalesRd")
public class SkuSalesRecordController{
    @Autowired
    private ISkuSalesRecordService skuSalesRecordService;
    @Autowired
    GetLoginInfo getLoginInfo;


    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SkuSalesRecordController.class);

    @ApiOperation(value = "查询sku销售记录", notes = "page当前页码，row每页显示行数", response = SkuSalesRecord.class)
    @PostMapping("/queryPage")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码" , required = true, dataType = "string", paramType = "query" ),
            @ApiImplicitParam(name = "row", value = "每页显示行数", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "beginDate", value = "发货开始时间", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "发货结束时间", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "deliveryWarehouseId", value = "发货仓库ID", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pinlianSku", value = "品连SKU", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sysOrderId", value = "订单号", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "orderTrackId", value = "包裹号", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "skuTitle", value = "商品名称", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "freeFreight", value = "是否包邮,1:包邮，0：不包邮", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sellerName", value = "卖家名称", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "wServiceName", value = "仓库服务商", required = false, dataType = "string", paramType = "query")})
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page<SkuSalesRecord> selectSkuSalesRecordByMultiCondition(String beginDate, String endDate, Integer deliveryWarehouseId, String pinlianSku,
                                                     String sysOrderId, String orderTrackId, String skuTitle,Integer freeFreight, String supplierName, String sellerName,
                                                                     String wServiceName, String page, String row) {
        Page.builder(page, row);
        SkuSalesRecord skuSalesRecord =new SkuSalesRecord();
        skuSalesRecord.setTopFlag(getLoginInfo.getUserInfo().getTopFlag());
        logger.info("当前登录账号状态：{}",getLoginInfo.getUserInfo().getTopFlag());
        if(getLoginInfo.getUserInfo().getPlatformType()==0) {
            logger.info("供应商ID:{}",getLoginInfo.getUserInfo().getTopUserId());
            skuSalesRecord.setSupplierId(getLoginInfo.getUserInfo().getTopUserId());
            if(CollectionUtils.isNotEmpty(getLoginInfo.getUserInfo().getwIds())){
                skuSalesRecord.setwIds(getLoginInfo.getUserInfo().getwIds());
            }
        }
        if(getLoginInfo.getUserInfo().getPlatformType() == 2) {
            if(CollectionUtils.isNotEmpty(getLoginInfo.getUserInfo().getSuppliers())) {
                skuSalesRecord.setSuppliers(getLoginInfo.getUserInfo().getSuppliers());
            }

        }
        if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {

            skuSalesRecord.setBeginDate(beginDate);
            skuSalesRecord.setEndDate(endDate);
        }
        skuSalesRecord.setDeliveryWarehouseId(deliveryWarehouseId);
        skuSalesRecord.setFreeFreight(freeFreight);
        if(StringUtils.isNotBlank(pinlianSku)){
            skuSalesRecord.setSku(pinlianSku);
        }
        if(StringUtils.isNotBlank(sysOrderId)){
            skuSalesRecord.setSysOrderId(sysOrderId);
        }
        if(StringUtils.isNotBlank(orderTrackId)){
            skuSalesRecord.setOrderTrackId(orderTrackId);
        }
        if(StringUtils.isNotBlank(skuTitle)){
            skuSalesRecord.setSkuTitle(skuTitle);
        }

        if(StringUtils.isNotBlank(supplierName)){
            skuSalesRecord.setSupplierName(supplierName);
        }
        if(StringUtils.isNotBlank(sellerName)){
            skuSalesRecord.setSellerName(sellerName);
        }
        skuSalesRecord.setWserviceName(wServiceName);
        Page<SkuSalesRecord> p = skuSalesRecordService.page(skuSalesRecord);
        return p;
    }

    @ApiOperation(value = "导出sku销售记录",response = SkuSalesRecord.class)
    @RequestMapping(value ="/export", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate", value = "发货开始时间", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "发货结束时间", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "deliveryWarehouseId", value = "发货仓库ID", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pinlianSku", value = "品连SKU", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sysOrderId", value = "订单号", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "orderTrackId", value = "包裹号", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "skuTitle", value = "商品名称", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sellerName", value = "卖家名称", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "wServiceName", value = "仓库服务商", required = false, dataType = "string", paramType = "query")})
    public void exportSkuSalesRecordByMultiCondition(HttpServletResponse response, String beginDate, String endDate, Integer deliveryWarehouseId, String pinlianSku,
                                                     String sysOrderId, String orderTrackId, String skuTitle, String supplierName, String sellerName,String wServiceName) {
        SkuSalesRecord skuSalesRecord =new SkuSalesRecord();
        if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {

            skuSalesRecord.setBeginDate(beginDate);
            skuSalesRecord.setEndDate(endDate);
        }
        skuSalesRecord.setDeliveryWarehouseId(deliveryWarehouseId);
        if(StringUtils.isNotBlank(pinlianSku)){
            skuSalesRecord.setSku(pinlianSku);
        }
        if(StringUtils.isNotBlank(sysOrderId)){
            skuSalesRecord.setSysOrderId(sysOrderId);
        }
        if(StringUtils.isNotBlank(orderTrackId)){
            skuSalesRecord.setOrderTrackId(orderTrackId);
        }
        if(StringUtils.isNotBlank(skuTitle)){
            skuSalesRecord.setSkuTitle(skuTitle);
        }
        if(StringUtils.isNotBlank(supplierName)){
            skuSalesRecord.setSupplierName(supplierName);
        }
        if(StringUtils.isNotBlank(sellerName)){
            skuSalesRecord.setSellerName(sellerName);
        }
        skuSalesRecord.setWserviceName(wServiceName);
         skuSalesRecordService.exportSkuSalesRecordExcel(skuSalesRecord,response);

    }

    @ApiOperation(value = "sku销售记录统计",response = SkuSalesRecord.class)
    @PostMapping("/statis")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate", value = "发货开始时间", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "发货结束时间", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "deliveryWarehouseId", value = "发货仓库ID", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pinlianSku", value = "品连SKU", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sysOrderId", value = "订单号", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "orderTrackId", value = "包裹号", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "skuTitle", value = "商品名称", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "supplierName", value = "供应商名称", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sellerName", value = "卖家名称", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "wServiceName", value = "仓库服务商", required = false, dataType = "string", paramType = "query")})
    public SkuSalesRecord statisSkuSalesRecordByMultiCondition(String beginDate, String endDate, Integer deliveryWarehouseId, String pinlianSku,
                                                     String sysOrderId, String orderTrackId, String skuTitle, String supplierName, String sellerName,String wServiceName) {
        SkuSalesRecord skuSalesRecord =new SkuSalesRecord();
        if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {

            skuSalesRecord.setBeginDate(beginDate);
            skuSalesRecord.setEndDate(endDate);
        }
        skuSalesRecord.setDeliveryWarehouseId(deliveryWarehouseId);
        if(StringUtils.isNotBlank(pinlianSku)){
            skuSalesRecord.setSku(pinlianSku);
        }
        if(StringUtils.isNotBlank(sysOrderId)){
            skuSalesRecord.setSysOrderId(sysOrderId);
        }
        if(StringUtils.isNotBlank(orderTrackId)){
            skuSalesRecord.setOrderTrackId(orderTrackId);
        }
        if(StringUtils.isNotBlank(skuTitle)){
            skuSalesRecord.setSkuTitle(skuTitle);
        }
        if(StringUtils.isNotBlank(supplierName)){
            skuSalesRecord.setSupplierName(supplierName);
        }
        if(StringUtils.isNotBlank(sellerName)){
            skuSalesRecord.setSellerName(sellerName);
        }
        skuSalesRecord.setWserviceName(wServiceName);
       return skuSalesRecordService.statisSkuSales(skuSalesRecord);

    }

}