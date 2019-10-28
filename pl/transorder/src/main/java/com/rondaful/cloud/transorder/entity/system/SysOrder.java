package com.rondaful.cloud.transorder.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 系统订单表
 * 实体类对应的数据表为：  tb_sys_order_new
 *
 * @author guoxuankai
 * @date 2019-09-21 10:55:08
 */
@ApiModel(value = "SysOrder")
public class SysOrder extends SysOrderKey implements Serializable {

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

    @ApiModelProperty(value = "订单发货状态:1待发货,2缺货,3配货中,4已拦截,5已发货,6部分发货,7已作废,8已完成")
    private Byte orderDeliveryStatus;

    @ApiModelProperty(value = "是否为售后订单:0否,1全部售后,2部分售后")
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

    @ApiModelProperty(value = "平台佣金率")
    private BigDecimal platformCommissionRate;

    @ApiModelProperty(value = "包邮类型。0-不包邮，1-包邮，2-部分包邮")
    private Byte freeFreightType;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "订单类型：general或者split或者merged")
    private String splittedOrMerged;

    @ApiModelProperty(value = "是否异常订单：yes(是),no(否)")
    private String isErrorOrder;

    @ApiModelProperty(value = "是否转入的订单：yes(是),no(否)")
    private String isConvertOrder;

    @ApiModelProperty(value = "手工标记异常信息")
    private String markException;

    @ApiModelProperty(value = "平台订单总价")
    private BigDecimal platformTotalPrice;

    @ApiModelProperty(value = "订单明细")
    private List<SysOrderDetail> SysOrderDetails;


    /***************************************************************/

    @ApiModelProperty(value = "所属平台名称")
    private String platformName;


    @ApiModelProperty(value = "所属平台类型(1 ebay   2 Amazon)")
    private Integer platformType;

    @ApiModelProperty(value = "店铺id")
    private String empowerid;

    @ApiModelProperty(value = "站点")
    private String site;


    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tb_sys_order_new
     *
     * @mbg.generated 2019-09-21 10:55:08
     */
    private static final long serialVersionUID = 1L;

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber == null ? null : recordNumber.trim();
    }

    public String getSourceOrderId() {
        return sourceOrderId;
    }

    public void setSourceOrderId(String sourceOrderId) {
        this.sourceOrderId = sourceOrderId == null ? null : sourceOrderId.trim();
    }

    public String getDeliverDeadline() {
        return deliverDeadline;
    }

    public void setDeliverDeadline(String deliverDeadline) {
        this.deliverDeadline = deliverDeadline == null ? null : deliverDeadline.trim();
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
        this.platformSellerAccount = platformSellerAccount == null ? null : platformSellerAccount.trim();
    }

    public String getShopType() {
        return shopType;
    }

    public void setShopType(String shopType) {
        this.shopType = shopType == null ? null : shopType.trim();
    }

    public String getPlatformSellerId() {
        return platformSellerId;
    }

    public void setPlatformSellerId(String platformSellerId) {
        this.platformSellerId = platformSellerId == null ? null : platformSellerId.trim();
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
        this.sellerPlAccount = sellerPlAccount == null ? null : sellerPlAccount.trim();
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
        this.supplyChainCompanyName = supplyChainCompanyName == null ? null : supplyChainCompanyName.trim();
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
        this.marketplaceId = marketplaceId == null ? null : marketplaceId.trim();
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId == null ? null : payId.trim();
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
        this.payTime = payTime == null ? null : payTime.trim();
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime == null ? null : orderTime.trim();
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
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
        this.deliveryTime = deliveryTime == null ? null : deliveryTime.trim();
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
        this.buyerUserId = buyerUserId == null ? null : buyerUserId.trim();
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName == null ? null : buyerName.trim();
    }

    public String getBuyerCheckoutMessage() {
        return buyerCheckoutMessage;
    }

    public void setBuyerCheckoutMessage(String buyerCheckoutMessage) {
        this.buyerCheckoutMessage = buyerCheckoutMessage == null ? null : buyerCheckoutMessage.trim();
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
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
        this.createBy = createBy == null ? null : createBy.trim();
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
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
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public String getSplittedOrMerged() {
        return splittedOrMerged;
    }

    public void setSplittedOrMerged(String splittedOrMerged) {
        this.splittedOrMerged = splittedOrMerged == null ? null : splittedOrMerged.trim();
    }

    public String getIsErrorOrder() {
        return isErrorOrder;
    }

    public void setIsErrorOrder(String isErrorOrder) {
        this.isErrorOrder = isErrorOrder == null ? null : isErrorOrder.trim();
    }

    public String getIsConvertOrder() {
        return isConvertOrder;
    }

    public void setIsConvertOrder(String isConvertOrder) {
        this.isConvertOrder = isConvertOrder == null ? null : isConvertOrder.trim();
    }

    public String getMarkException() {
        return markException;
    }

    public void setMarkException(String markException) {
        this.markException = markException == null ? null : markException.trim();
    }

    public BigDecimal getPlatformTotalPrice() {
        return platformTotalPrice;
    }

    public void setPlatformTotalPrice(BigDecimal platformTotalPrice) {
        this.platformTotalPrice = platformTotalPrice;
    }

    public List<SysOrderDetail> getSysOrderDetails() {
        return SysOrderDetails;
    }

    public void setSysOrderDetails(List<SysOrderDetail> sysOrderDetails) {
        SysOrderDetails = sysOrderDetails;
    }


    public String getEmpowerid() {
        return empowerid;
    }

    public void setEmpowerid(String empowerid) {
        this.empowerid = empowerid;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }
}