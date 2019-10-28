package com.rondaful.cloud.order.model.dto.syncorder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rondaful.cloud.order.entity.system.OrderProfitCalculation;
import com.rondaful.cloud.order.seller.Empower;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 系统订单DTO
 *
 * @author chenjiangxin
 * @date 2019-07-16 20:13:29
 */
@ApiModel(value = "SysOrderDTO")
public class SysOrderDTO extends OrderProfitCalculation implements Serializable{

    @ApiModelProperty(value = "平台订单总价")
    private BigDecimal platformTotalPrice;

    private Empower empower;

    @ApiModelProperty(value = "是否转入的订单：yes或者no")
    private String isConvertOrder;

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
    private Integer converSysStatus;

    @ApiModelProperty(value = "是否手工单：1是，0否")
    private Integer handOrder = 1;

    @ApiModelProperty(value = "订单来源:1手工创建,2批量导入,3第三方平台API推送,4eBay订单,5Amazon订单,6AliExpress订单,7Wish订单,8星商订单")
    private Integer orderSource;

    @ApiModelProperty(value = "订单发货状态:1待发货,2缺货,3配货中,4已拦截,5已发货,6已收货,7已作废,8已完成")
    private Integer orderDeliveryStatus;

    @ApiModelProperty(value = "是否为售后订单:0否,1是")
    private Integer isAfterSaleOrder;

    @ApiModelProperty(value = "卖家平台店铺ID-品连")
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

    @ApiModelProperty(value = "平台订单总价")
    private String platformTotal;

    @ApiModelProperty(value = "预估物流费")
    private BigDecimal estimateShipCost;

    @ApiModelProperty(value = "订单总售价:预估物流费+系统商品总金额")
    private BigDecimal total;

    @ApiModelProperty(value = "系统商品总价(商品单价X数量)")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "订单货款(平台抓取下来的金额)")
    private BigDecimal commoditiesAmount;

    @ApiModelProperty(value = "站点ID")
    private String marketplaceId;

    @ApiModelProperty(value = "支付ID")
    private String payId;

    @ApiModelProperty(value = "支付状态:0待支付,10冻结失败,11冻结成功,20付款中,21付款成功,22付款失败,30待补款,40已取消")
    private Integer payStatus;

    @ApiModelProperty(value = "支付方式:1账户余额,2微信,3支付宝,4线下支付")
    private Integer payMethod;

    @ApiModelProperty(value = "付款时间")
    private String payTime;

    @ApiModelProperty(value = "下单时间")
    private String orderTime;

    @ApiModelProperty(value = "订单创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    @ApiModelProperty(value = "发货时间")
    private String deliveryTime;

    @ApiModelProperty(value = "卖家填的物流费")
    private String shippingServiceCostStr;

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

    @ApiModelProperty(value = "仓库返回订单号")
    private String referenceId;

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

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "平台佣金率")
    private BigDecimal platformCommissionRate;

    @ApiModelProperty(value = "包邮类型。0-不包邮，1-包邮，2-部分包邮")
    private Integer freeFreightType;

    @ApiModelProperty(value = "订单商品信息")
    private List<SysOrderDetailDTO> sysOrderDetailList = new ArrayList<>();

    @ApiModelProperty(value = "订单包裹")
    private List<SysOrderPackageDTO> sysOrderPackageList = new ArrayList<>();

    @ApiModelProperty(value = "订单收货地址")
    private SysOrderReceiveAddressDTO sysOrderReceiveAddress;

    /***************************************************************/
    @ApiModelProperty(value = "平台站点(目前亚马逊时该字段必传字段)")
    private String site;

    @ApiModelProperty(value = "授权id")
    private Integer empowerId;

    @ApiModelProperty(value = "品连的sku")
    private List<String> skus;

    @ApiModelProperty(value = "产品成本")
    private BigDecimal productCost;

    @ApiModelProperty(value = "总重量")
    private BigDecimal totalWeight;

    @ApiModelProperty(value = "总体积")
    private BigDecimal totalBulk;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tb_sys_order_new
     *
     * @mbg.generated 2019-07-16 20:13:29
     */
    private static final long serialVersionUID = 1L;

    public Empower getEmpower() {
        return empower;
    }

    public void setEmpower(Empower empower) {
        this.empower = empower;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getTotalBulk() {
        return totalBulk;
    }

    public void setTotalBulk(BigDecimal totalBulk) {
        this.totalBulk = totalBulk;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public SysOrderDTO() {
    }

    public SysOrderDTO(String isConvertOrder, Integer id, String sysOrderId, String recordNumber, String sourceOrderId, String deliverDeadline, Integer converSysStatus, Integer orderSource, Integer orderDeliveryStatus, Integer isAfterSaleOrder, Integer platformShopId, String platformSellerAccount, String shopType, String platformSellerId, Integer sellerPlId, String sellerPlAccount, Integer supplyChainCompanyId, String supplyChainCompanyName, String platformTotal, BigDecimal estimateShipCost, BigDecimal total, BigDecimal orderAmount, BigDecimal commoditiesAmount, String marketplaceId, String payId, Integer payStatus, Integer payMethod, String payTime, String orderTime, Date createdTime, String deliveryTime, String shippingServiceCostStr, BigDecimal shippingServiceCost, BigDecimal sysShippingDiscount, BigDecimal interest, BigDecimal grossMargin, BigDecimal profitMargin, String buyerUserId, String buyerName, String buyerCheckoutMessage, String referenceId, Date createDate, String createBy, Date updateDate, String updateBy, String remark, BigDecimal platformCommissionRate, Integer freeFreightType, List<SysOrderDetailDTO> sysOrderDetailList, List<SysOrderPackageDTO> sysOrderPackageList, SysOrderReceiveAddressDTO sysOrderReceiveAddress, String site, Integer empowerId, List<String> skus, BigDecimal productCost, BigDecimal totalWeight, BigDecimal totalBulk) {
        this.isConvertOrder = isConvertOrder;
        this.id = id;
        this.sysOrderId = sysOrderId;
        this.recordNumber = recordNumber;
        this.sourceOrderId = sourceOrderId;
        this.deliverDeadline = deliverDeadline;
        this.converSysStatus = converSysStatus;
        this.orderSource = orderSource;
        this.orderDeliveryStatus = orderDeliveryStatus;
        this.isAfterSaleOrder = isAfterSaleOrder;
        this.platformShopId = platformShopId;
        this.platformSellerAccount = platformSellerAccount;
        this.shopType = shopType;
        this.platformSellerId = platformSellerId;
        this.sellerPlId = sellerPlId;
        this.sellerPlAccount = sellerPlAccount;
        this.supplyChainCompanyId = supplyChainCompanyId;
        this.supplyChainCompanyName = supplyChainCompanyName;
        this.platformTotal = platformTotal;
        this.estimateShipCost = estimateShipCost;
        this.total = total;
        this.orderAmount = orderAmount;
        this.commoditiesAmount = commoditiesAmount;
        this.marketplaceId = marketplaceId;
        this.payId = payId;
        this.payStatus = payStatus;
        this.payMethod = payMethod;
        this.payTime = payTime;
        this.orderTime = orderTime;
        this.createdTime = createdTime;
        this.deliveryTime = deliveryTime;
        this.shippingServiceCostStr = shippingServiceCostStr;
        this.shippingServiceCost = shippingServiceCost;
        this.sysShippingDiscount = sysShippingDiscount;
        this.interest = interest;
        this.grossMargin = grossMargin;
        this.profitMargin = profitMargin;
        this.buyerUserId = buyerUserId;
        this.buyerName = buyerName;
        this.buyerCheckoutMessage = buyerCheckoutMessage;
        this.referenceId = referenceId;
        this.createDate = createDate;
        this.createBy = createBy;
        this.updateDate = updateDate;
        this.updateBy = updateBy;
        this.remark = remark;
        this.platformCommissionRate = platformCommissionRate;
        this.freeFreightType = freeFreightType;
        this.sysOrderDetailList = sysOrderDetailList;
        this.sysOrderPackageList = sysOrderPackageList;
        this.sysOrderReceiveAddress = sysOrderReceiveAddress;
        this.site = site;
        this.empowerId = empowerId;
        this.skus = skus;
        this.productCost = productCost;
        this.totalWeight = totalWeight;
        this.totalBulk = totalBulk;
    }

    @Override
    public String toString() {
        return "SysOrderDTO{" +
                "isConvertOrder='" + isConvertOrder + '\'' +
                ", id=" + id +
                ", sysOrderId='" + sysOrderId + '\'' +
                ", recordNumber='" + recordNumber + '\'' +
                ", sourceOrderId='" + sourceOrderId + '\'' +
                ", deliverDeadline='" + deliverDeadline + '\'' +
                ", converSysStatus=" + converSysStatus +
                ", orderSource=" + orderSource +
                ", orderDeliveryStatus=" + orderDeliveryStatus +
                ", isAfterSaleOrder=" + isAfterSaleOrder +
                ", platformShopId=" + platformShopId +
                ", platformSellerAccount='" + platformSellerAccount + '\'' +
                ", shopType='" + shopType + '\'' +
                ", platformSellerId='" + platformSellerId + '\'' +
                ", sellerPlId=" + sellerPlId +
                ", sellerPlAccount='" + sellerPlAccount + '\'' +
                ", supplyChainCompanyId=" + supplyChainCompanyId +
                ", supplyChainCompanyName='" + supplyChainCompanyName + '\'' +
                ", platformTotal='" + platformTotal + '\'' +
                ", estimateShipCost=" + estimateShipCost +
                ", total=" + total +
                ", orderAmount=" + orderAmount +
                ", commoditiesAmount=" + commoditiesAmount +
                ", marketplaceId='" + marketplaceId + '\'' +
                ", payId='" + payId + '\'' +
                ", payStatus=" + payStatus +
                ", payMethod=" + payMethod +
                ", payTime='" + payTime + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", createdTime=" + createdTime +
                ", deliveryTime='" + deliveryTime + '\'' +
                ", shippingServiceCostStr='" + shippingServiceCostStr + '\'' +
                ", shippingServiceCost=" + shippingServiceCost +
                ", sysShippingDiscount=" + sysShippingDiscount +
                ", interest=" + interest +
                ", grossMargin=" + grossMargin +
                ", profitMargin=" + profitMargin +
                ", buyerUserId='" + buyerUserId + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", buyerCheckoutMessage='" + buyerCheckoutMessage + '\'' +
                ", referenceId='" + referenceId + '\'' +
                ", createDate=" + createDate +
                ", createBy='" + createBy + '\'' +
                ", updateDate=" + updateDate +
                ", updateBy='" + updateBy + '\'' +
                ", remark='" + remark + '\'' +
                ", platformCommissionRate=" + platformCommissionRate +
                ", freeFreightType=" + freeFreightType +
                ", sysOrderDetailList=" + sysOrderDetailList +
                ", sysOrderPackageList=" + sysOrderPackageList +
                ", sysOrderReceiveAddress=" + sysOrderReceiveAddress +
                ", site='" + site + '\'' +
                ", empowerId=" + empowerId +
                ", skus=" + skus +
                ", productCost=" + productCost +
                ", totalWeight=" + totalWeight +
                ", totalBulk=" + totalBulk +
                '}';
    }

    public String getIsConvertOrder() {
        return isConvertOrder;
    }

    public void setIsConvertOrder(String isConvertOrder) {
        this.isConvertOrder = isConvertOrder;
    }

    public BigDecimal getEstimateShipCost() {
        return estimateShipCost;
    }

    public void setEstimateShipCost(BigDecimal estimateShipCost) {
        this.estimateShipCost = estimateShipCost;
    }

    public String getShippingServiceCostStr() {
        return shippingServiceCostStr;
    }

    public void setShippingServiceCostStr(String shippingServiceCostStr) {
        this.shippingServiceCostStr = shippingServiceCostStr;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getPlatformTotal() {
        return platformTotal;
    }

    public void setPlatformTotal(String platformTotal) {
        this.platformTotal = platformTotal;
    }

    public BigDecimal getProductCost() {
        return productCost;
    }

    public void setProductCost(BigDecimal productCost) {
        this.productCost = productCost;
    }

    public List<String> getSkus() {
        return skus;
    }

    public void setSkus(List<String> skus) {
        this.skus = skus;
    }

    public Integer getEmpowerId() {
        return empowerId;
    }

    public void setEmpowerId(Integer empowerId) {
        this.empowerId = empowerId;
    }

    public List<SysOrderDetailDTO> getSysOrderDetailList() {
        return sysOrderDetailList;
    }

    public void setSysOrderDetailList(List<SysOrderDetailDTO> sysOrderDetailList) {
        this.sysOrderDetailList = sysOrderDetailList;
    }

    public List<SysOrderPackageDTO> getSysOrderPackageList() {
        return sysOrderPackageList;
    }

    public void setSysOrderPackageList(List<SysOrderPackageDTO> sysOrderPackageList) {
        this.sysOrderPackageList = sysOrderPackageList;
    }

    public SysOrderReceiveAddressDTO getSysOrderReceiveAddress() {
        return sysOrderReceiveAddress;
    }

    public void setSysOrderReceiveAddress(SysOrderReceiveAddressDTO sysOrderReceiveAddress) {
        this.sysOrderReceiveAddress = sysOrderReceiveAddress;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
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
        this.sysOrderId = sysOrderId == null ? null : sysOrderId.trim();
    }

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

    public Integer getConverSysStatus() {
        return converSysStatus;
    }

    public void setConverSysStatus(Integer converSysStatus) {
        this.converSysStatus = converSysStatus;
    }

    public Integer getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(Integer orderSource) {
        this.orderSource = orderSource;
    }

    public Integer getOrderDeliveryStatus() {
        return orderDeliveryStatus;
    }

    public void setOrderDeliveryStatus(Integer orderDeliveryStatus) {
        this.orderDeliveryStatus = orderDeliveryStatus;
    }

    public Integer getIsAfterSaleOrder() {
        return isAfterSaleOrder;
    }

    public void setIsAfterSaleOrder(Integer isAfterSaleOrder) {
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

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Integer getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(Integer payMethod) {
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public BigDecimal getPlatformCommissionRate() {
        return platformCommissionRate;
    }

    public void setPlatformCommissionRate(BigDecimal platformCommissionRate) {
        this.platformCommissionRate = platformCommissionRate;
    }

    public Integer getFreeFreightType() {
        return freeFreightType;
    }

    public void setFreeFreightType(Integer freeFreightType) {
        this.freeFreightType = freeFreightType;
    }

    public BigDecimal getPlatformTotalPrice() {
        return platformTotalPrice;
    }

    public void setPlatformTotalPrice(BigDecimal platformTotalPrice) {
        this.platformTotalPrice = platformTotalPrice;
    }

    public Integer getHandOrder() {
        return handOrder;
    }

    public void setHandOrder(Integer handOrder) {
        this.handOrder = handOrder;
    }
}