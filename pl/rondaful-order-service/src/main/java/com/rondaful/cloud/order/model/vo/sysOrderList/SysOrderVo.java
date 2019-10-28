package com.rondaful.cloud.order.model.vo.sysOrderList;

import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author: luozheng
 * @BelongsPackage:com.rondaful.cloud.order.model.vo.sysOrderList
 * @Date: 2019-07-22 10:30:07
 * @FileName:${FILENAME}
 * @Description:系统订单列表查询VO
 */
@ApiModel("系统订单列表查询VO|author:lz")
public class SysOrderVo implements Serializable {
    @ApiModelProperty("系统订单号")
    private String sysOrderId;
    @ApiModelProperty("平台订单号(ebay/Amazon/AliExpress等平台订单号)")
    private String platformOrderId;
    @ApiModelProperty("卖家店铺账号")
    private String sellerAccount;
    //detail详情表 item_name字段
//    @ApiModelProperty("商品中文名称")
//    private String commodityNameCn;
//    @ApiModelProperty("商品英文名称")
//    private String commodityNameEn;
    @ApiModelProperty("创建时间")
    private Date createDate;
//    @ApiModelProperty("平台sku")
//    private String platformSku;
//    @ApiModelProperty("品连sku")
//    private String pinlianSku;
    @ApiModelProperty("发货时间")
    private String deliveryTime;
//    @ApiModelProperty("包裹号")
//    private String packageNo;
    @ApiModelProperty("订单金额")
    private BigDecimal orderAmount;
    @ApiModelProperty("订单利润")
    private BigDecimal orderProfit;
    @ApiModelProperty("最迟发货时间")
    private String deliverDeadline;

    @ApiModelProperty("系统订单包裹")
    private List<SysOrderPackage> sysOrderPackageList;

    @ApiModelProperty("系统订单详情")
    private List<SysOrderDetail> sysOrderDetailList;

    public SysOrderVo(String sysOrderId, String platformOrderId, String sellerAccount, Date createDate, String deliveryTime, BigDecimal orderAmount, BigDecimal orderProfit, String deliverDeadline, List<SysOrderPackage> sysOrderPackageList, List<SysOrderDetail> sysOrderDetailList) {
        this.sysOrderId = sysOrderId;
        this.platformOrderId = platformOrderId;
        this.sellerAccount = sellerAccount;
        this.createDate = createDate;
        this.deliveryTime = deliveryTime;
        this.orderAmount = orderAmount;
        this.orderProfit = orderProfit;
        this.deliverDeadline = deliverDeadline;
        this.sysOrderPackageList = sysOrderPackageList;
        this.sysOrderDetailList = sysOrderDetailList;
    }

    public SysOrderVo() {
    }

    public String getSysOrderId() {
        return sysOrderId;
    }

    public void setSysOrderId(String sysOrderId) {
        this.sysOrderId = sysOrderId;
    }

    public String getPlatformOrderId() {
        return platformOrderId;
    }

    public void setPlatformOrderId(String platformOrderId) {
        this.platformOrderId = platformOrderId;
    }

    public String getSellerAccount() {
        return sellerAccount;
    }

    public void setSellerAccount(String sellerAccount) {
        this.sellerAccount = sellerAccount;
    }

//    public String getCommodityNameCn() {
//        return commodityNameCn;
//    }

//    public void setCommodityNameCn(String commodityNameCn) {
//        this.commodityNameCn = commodityNameCn;
//    }

//    public String getCommodityNameEn() {
//        return commodityNameEn;
//    }
//
//    public void setCommodityNameEn(String commodityNameEn) {
//        this.commodityNameEn = commodityNameEn;
//    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

//    public String getPlatformSku() {
//        return platformSku;
//    }
//
//    public void setPlatformSku(String platformSku) {
//        this.platformSku = platformSku;
//    }
//
//    public String getPinlianSku() {
//        return pinlianSku;
//    }
//
//    public void setPinlianSku(String pinlianSku) {
//        this.pinlianSku = pinlianSku;
//    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

//    public String getPackageNo() {
//        return packageNo;
//    }
//
//    public void setPackageNo(String packageNo) {
//        packageNo = packageNo;
//    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getOrderProfit() {
        return orderProfit;
    }

    public void setOrderProfit(BigDecimal orderProfit) {
        this.orderProfit = orderProfit;
    }

    public String getDeliverDeadline() {
        return deliverDeadline;
    }

    public void setDeliverDeadline(String deliverDeadline) {
        this.deliverDeadline = deliverDeadline;
    }

    public List<SysOrderPackage> getSysOrderPackageList() {
        return sysOrderPackageList;
    }

    public void setSysOrderPackageList(List<SysOrderPackage> sysOrderPackageList) {
        this.sysOrderPackageList = sysOrderPackageList;
    }

    public List<SysOrderDetail> getSysOrderDetailList() {
        return sysOrderDetailList;
    }



    public void setSysOrderDetailList(List<SysOrderDetail> sysOrderDetailList) {
        this.sysOrderDetailList = sysOrderDetailList;
    }
}
