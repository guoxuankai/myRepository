package com.rondaful.cloud.order.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rondaful.cloud.order.entity.PayOrderInfo;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.WarehouseShipExceptionVo;
import com.rondaful.cloud.order.entity.cms.OrderAfterVo;
import com.rondaful.cloud.order.model.dto.sysOrderInvoice.SysOrderInvoiceInsertOrUpdateDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
/**
 * 系统订单表
 * 实体类对应的数据表为：  tb_sys_order_new
 * @author chenjiangxin
 * @date 2019-07-18 17:20:20
 */
@ApiModel(value ="SysOrderNew")
@Data
public class SysOrderNew extends OrderProfitCalculation implements Serializable  {
    @ApiModelProperty(value = "是否低于利润阈值")
    boolean isLessThanThreshold;

    @ApiModelProperty(value = "其他被合并订单的信息")
    List<PayOrderInfo> payOrderInfos;

    @ApiModelProperty(value = "发货异常信息")
    List<WarehouseShipExceptionVo> warehouseShipExceptionVoList;

    @ApiModelProperty(value = "手工标记异常")
    private String markException;

    @ApiModelProperty(value = "是否异常订单：yes或者no")
    private String isErrorOrder;

    @ApiModelProperty(value = "是否转入的订单：yes或者no")
    private String isConvertOrder;

    @ApiModelProperty(value = "订单类型：general或者split或者merge")
    private String splittedOrMerged;

    @ApiModelProperty(value = "平台佣金")
    private BigDecimal platformCommission;

    @ApiModelProperty(value = "平台下单时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date platformOrderTime;

    @ApiModelProperty(value = "平台最迟发货时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private  Date lastShippingTime;

    @ApiModelProperty(value = "接受发票对象")
    private SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO;

    @ApiModelProperty(value = "是否缺货")
    private boolean isOOS;

    @ApiModelProperty(value = "平台发货状态")
    private String platformOrderStatus;

    @ApiModelProperty(value = "采购订单详情")
    private List<SysOrderDetail> sysOrderDetails;

    @ApiModelProperty(value = "售后订单详情")
    private List<OrderAfterVo> orderAfterVoList;

    @ApiModelProperty(value = "订单包裹集合")
    private List<SysOrderPackage> sysOrderPackageList;

    @ApiModelProperty(value = "收货地址对象")
    private SysOrderReceiveAddress sysOrderReceiveAddress;

    @ApiModelProperty(value = "顺序号")
    private Integer id;

    @ApiModelProperty(value = "系统订单ID")
    private String sysOrderId;

    @ApiModelProperty(value = "卖家平台订单销售记录号")
    private String recordNumber;

    @ApiModelProperty(value = "来源订单ID")
    private String sourceOrderId;

    @ApiModelProperty(value = "最迟发货时间(取所有子单最早时间)")
    private String deliverDeadline;

    @ApiModelProperty(value = "订单转入状态:0待处理,1转入成功,2转入失败,3部分转入成功")
    private Byte converSysStatus;

    @ApiModelProperty(value = "订单来源:1手工创建,2批量导入,3第三方平台API推送,4eBay订单,5Amazon订单,6AliExpress订单,7Wish订单,8星商订单")
    private Byte orderSource;

    @ApiModelProperty(value = "订单发货状态:1待发货,2缺货,3配货中,4已拦截,5已发货,6已收货,7已作废,8已完成")
    private Byte orderDeliveryStatus;

    @ApiModelProperty(value = "是否为售后订单:0否,1是")
    private Byte isAfterSaleOrder;

    @ApiModelProperty(value = "卖家平台店铺ID")
    private Integer platformShopId;

    @ApiModelProperty(value = "卖家平台店铺名")
    private String platformSellerAccount;

    @ApiModelProperty(value = "卖家平台店铺类型")
    private String shopType;

    @ApiModelProperty(value = "卖家平台账号")
    private String platformSellerId;

    @ApiModelProperty(value = "卖家品连ID")
    private Integer sellerPlId;

    @ApiModelProperty(value = "卖家品连账号")
    private String sellerPlAccount;

    @ApiModelProperty(value = "卖家供应链ID")
    private Integer supplyChainCompanyId;

    @ApiModelProperty(value = "卖家供应链名称")
    private String supplyChainCompanyName;

    @ApiModelProperty(value = "订单总售价:预估物流费+系统商品总金额")
    private BigDecimal total;

    @ApiModelProperty(value = "系统商品总价(商品单价X数量)")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "预估物流费")
    private BigDecimal estimateShipCost;

    @ApiModelProperty(value = "订单货款(平台抓取下来的金额)")
    private BigDecimal commoditiesAmount;

    @ApiModelProperty(value = "站点ID")
    private String marketplaceId;

    @ApiModelProperty(value = "支付ID")
    private String payId;

    @ApiModelProperty(value = "支付状态:0待支付,10冻结失败,11冻结成功,20付款中,21付款成功,22付款失败,30待补款,40已取消")
    private Byte payStatus;

    @ApiModelProperty(value = "支付方式:1账户余额,2微信,3支付宝,4线下支付")
    private Byte payMethod;

    @ApiModelProperty(value = "付款时间")
    private String payTime;

    @ApiModelProperty(value = "下单时间")
    private String orderTime;

    @ApiModelProperty(value = "订单创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    @ApiModelProperty(value = "发货时间")
    private String deliveryTime;

    @ApiModelProperty(value = "卖家填的平台物流费")
    private BigDecimal shippingServiceCost;

    @ApiModelProperty(value = "系统折扣运费")
    private BigDecimal sysShippingDiscount;

    @ApiModelProperty(value = "支付/提现利息")
    private BigDecimal interest;

    @ApiModelProperty(value = "毛利")
    private BigDecimal grossMargin;

    @ApiModelProperty(value = "利润率")
    private BigDecimal profitMargin;

    @ApiModelProperty(value = "买家ID")
    private String buyerUserId;

    @ApiModelProperty(value = "买家姓名")
    private String buyerName;

    @ApiModelProperty(value = "买家留言")
    private String buyerCheckoutMessage;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")    private Date updateDate;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "平台佣金率")
    private BigDecimal platformCommissionRate;

    @ApiModelProperty(value = "包邮类型。0-不包邮，1-包邮，2-部分包邮")
    private Byte freeFreightType;

    @ApiModelProperty(value = "平台订单总价")
    private BigDecimal platformTotalPrice;

    public BigDecimal getPlatformTotalPrice() {
        return platformTotalPrice;
    }

    public void setPlatformTotalPrice(BigDecimal platformTotalPrice) {
        this.platformTotalPrice = platformTotalPrice;
    }

    public List<PayOrderInfo> getPayOrderInfos() {
        return payOrderInfos;
    }

    public void setPayOrderInfos(List<PayOrderInfo> payOrderInfos) {
        this.payOrderInfos = payOrderInfos;
    }

    public List<WarehouseShipExceptionVo> getWarehouseShipExceptionVoList() {
        return warehouseShipExceptionVoList;
    }

    public void setWarehouseShipExceptionVoList(List<WarehouseShipExceptionVo> warehouseShipExceptionVoList) {
        this.warehouseShipExceptionVoList = warehouseShipExceptionVoList;
    }

    public String getMarkException() {
        return markException;
    }

    public void setMarkException(String markException) {
        this.markException = markException;
    }

    public String getIsErrorOrder() {
        return isErrorOrder;
    }

    public void setIsErrorOrder(String isErrorOrder) {
        this.isErrorOrder = isErrorOrder;
    }

    public String getIsConvertOrder() {
        return isConvertOrder;
    }

    public void setIsConvertOrder(String isConvertOrder) {
        this.isConvertOrder = isConvertOrder;
    }

    public String getSplittedOrMerged() {
        return splittedOrMerged;
    }

    public void setSplittedOrMerged(String splittedOrMerged) {
        this.splittedOrMerged = splittedOrMerged;
    }

    public BigDecimal getPlatformCommission() {
        return platformCommission;
    }

    public void setPlatformCommission(BigDecimal platformCommission) {
        this.platformCommission = platformCommission;
    }

    public Date getPlatformOrderTime() {
        return platformOrderTime;
    }

    public void setPlatformOrderTime(Date platformOrderTime) {
        this.platformOrderTime = platformOrderTime;
    }

    public Date getLastShippingTime() {
        return lastShippingTime;
    }

    public void setLastShippingTime(Date lastShippingTime) {
        this.lastShippingTime = lastShippingTime;
    }

    public SysOrderInvoiceInsertOrUpdateDTO getSysOrderInvoiceInsertOrUpdateDTO() {
        return sysOrderInvoiceInsertOrUpdateDTO;
    }

    public void setSysOrderInvoiceInsertOrUpdateDTO(SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO) {
        this.sysOrderInvoiceInsertOrUpdateDTO = sysOrderInvoiceInsertOrUpdateDTO;
    }

    public boolean getIsLessThanThreshold() {
        return isLessThanThreshold;
    }

    public void setIsLessThanThreshold(Boolean isLessThanThreshold) {
        this.isLessThanThreshold = isLessThanThreshold;
    }


    public boolean isLessThanThreshold() {
        return isLessThanThreshold;
    }

    public void setLessThanThreshold(boolean lessThanThreshold) {
        isLessThanThreshold = lessThanThreshold;
    }

    public String getPlatformOrderStatus() {
        return platformOrderStatus;
    }

    public void setPlatformOrderStatus(String platformOrderStatus) {
        this.platformOrderStatus = platformOrderStatus;
    }

    public List<SysOrderDetail> getSysOrderDetails() {
        return sysOrderDetails;
    }

    public void setSysOrderDetails(List<SysOrderDetail> sysOrderDetails) {
        this.sysOrderDetails = sysOrderDetails;
    }

    public List<OrderAfterVo> getOrderAfterVoList() {
        return orderAfterVoList;
    }

    public void setOrderAfterVoList(List<OrderAfterVo> orderAfterVoList) {
        this.orderAfterVoList = orderAfterVoList;
    }

    public List<SysOrderPackage> getSysOrderPackageList() {
        return sysOrderPackageList;
    }

    public void setSysOrderPackageList(List<SysOrderPackage> sysOrderPackageList) {
        this.sysOrderPackageList = sysOrderPackageList;
    }

    public SysOrderReceiveAddress getSysOrderReceiveAddress() {
        return sysOrderReceiveAddress;
    }

    public void setSysOrderReceiveAddress(SysOrderReceiveAddress sysOrderReceiveAddress) {
        this.sysOrderReceiveAddress = sysOrderReceiveAddress;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSysOrderId() {
        return sysOrderId;
    }

    public void setSysOrderId(String sysOrderId) {
        this.sysOrderId = sysOrderId;
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber;
    }

    public String getSourceOrderId() {
        return sourceOrderId;
    }

    public void setSourceOrderId(String sourceOrderId) {
        this.sourceOrderId = sourceOrderId;
    }

    public String getDeliverDeadline() {
        return deliverDeadline;
    }

    public void setDeliverDeadline(String deliverDeadline) {
        this.deliverDeadline = deliverDeadline;
    }

    public Byte getConverSysStatus() {
        return converSysStatus;
    }

    public void setConverSysStatus(Byte converSysStatus) {
        this.converSysStatus = converSysStatus;
    }

    public Byte getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(Byte orderSource) {
        this.orderSource = orderSource;
    }

    public Byte getOrderDeliveryStatus() {
        return orderDeliveryStatus;
    }

    public void setOrderDeliveryStatus(Byte orderDeliveryStatus) {
        this.orderDeliveryStatus = orderDeliveryStatus;
    }

    public Byte getIsAfterSaleOrder() {
        return isAfterSaleOrder;
    }

    public void setIsAfterSaleOrder(Byte isAfterSaleOrder) {
        this.isAfterSaleOrder = isAfterSaleOrder;
    }

    public Integer getPlatformShopId() {
        return platformShopId;
    }

    public void setPlatformShopId(Integer platformShopId) {
        this.platformShopId = platformShopId;
    }

    public String getPlatformSellerAccount() {
        return platformSellerAccount;
    }

    public void setPlatformSellerAccount(String platformSellerAccount) {
        this.platformSellerAccount = platformSellerAccount;
    }

    public String getShopType() {
        return shopType;
    }

    public void setShopType(String shopType) {
        this.shopType = shopType;
    }

    public String getPlatformSellerId() {
        return platformSellerId;
    }

    public void setPlatformSellerId(String platformSellerId) {
        this.platformSellerId = platformSellerId;
    }

    public Integer getSellerPlId() {
        return sellerPlId;
    }

    public void setSellerPlId(Integer sellerPlId) {
        this.sellerPlId = sellerPlId;
    }

    public String getSellerPlAccount() {
        return sellerPlAccount;
    }

    public void setSellerPlAccount(String sellerPlAccount) {
        this.sellerPlAccount = sellerPlAccount;
    }

    public Integer getSupplyChainCompanyId() {
        return supplyChainCompanyId;
    }

    public void setSupplyChainCompanyId(Integer supplyChainCompanyId) {
        this.supplyChainCompanyId = supplyChainCompanyId;
    }

    public String getSupplyChainCompanyName() {
        return supplyChainCompanyName;
    }

    public void setSupplyChainCompanyName(String supplyChainCompanyName) {
        this.supplyChainCompanyName = supplyChainCompanyName;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getEstimateShipCost() {
        return estimateShipCost;
    }

    public void setEstimateShipCost(BigDecimal estimateShipCost) {
        this.estimateShipCost = estimateShipCost;
    }

    public BigDecimal getCommoditiesAmount() {
        return commoditiesAmount;
    }

    public void setCommoditiesAmount(BigDecimal commoditiesAmount) {
        this.commoditiesAmount = commoditiesAmount;
    }

    public String getMarketplaceId() {
        return marketplaceId;
    }

    public void setMarketplaceId(String marketplaceId) {
        this.marketplaceId = marketplaceId;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public Byte getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Byte payStatus) {
        this.payStatus = payStatus;
    }

    public Byte getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(Byte payMethod) {
        this.payMethod = payMethod;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public BigDecimal getShippingServiceCost() {
        return shippingServiceCost;
    }

    public void setShippingServiceCost(BigDecimal shippingServiceCost) {
        this.shippingServiceCost = shippingServiceCost;
    }

    public BigDecimal getSysShippingDiscount() {
        return sysShippingDiscount;
    }

    public void setSysShippingDiscount(BigDecimal sysShippingDiscount) {
        this.sysShippingDiscount = sysShippingDiscount;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getGrossMargin() {
        return grossMargin;
    }

    public void setGrossMargin(BigDecimal grossMargin) {
        this.grossMargin = grossMargin;
    }

    public BigDecimal getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(BigDecimal profitMargin) {
        this.profitMargin = profitMargin;
    }

    public String getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerCheckoutMessage() {
        return buyerCheckoutMessage;
    }

    public void setBuyerCheckoutMessage(String buyerCheckoutMessage) {
        this.buyerCheckoutMessage = buyerCheckoutMessage;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getPlatformCommissionRate() {
        return platformCommissionRate;
    }

    public void setPlatformCommissionRate(BigDecimal platformCommissionRate) {
        this.platformCommissionRate = platformCommissionRate;
    }

    public Byte getFreeFreightType() {
        return freeFreightType;
    }

    public void setFreeFreightType(Byte freeFreightType) {
        this.freeFreightType = freeFreightType;
    }

    public boolean isOrderInfoIsIntact() {
        return orderInfoIsIntact;
    }

    public void setOrderInfoIsIntact(boolean orderInfoIsIntact) {
        this.orderInfoIsIntact = orderInfoIsIntact;
    }
    public boolean getOrderInfoIsIntact() {
        return this.orderInfoIsIntact;
    }

    private boolean orderInfoIsIntact;

    public boolean getIsOOS() {
        return isOOS;
    }

    public void setIsOOS(boolean b) {
        this.isOOS=b;
    }
}