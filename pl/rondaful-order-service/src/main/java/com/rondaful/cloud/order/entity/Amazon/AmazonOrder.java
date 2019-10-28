package com.rondaful.cloud.order.entity.Amazon;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rondaful.cloud.order.entity.SysOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Amazon 订单表
 * 实体类对应的数据表为：  tbl_amazon_order
 * @author wjc
 * @date 2018-12-06 15:30:55
 */
@ApiModel(value ="AmazonOrder")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AmazonOrder implements Serializable {

    @ApiModelProperty(value = "发货时间")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:sss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:sss")
    private Date deliveryTime;

    @ApiModelProperty(value = "亚马逊店铺ID")
    private Integer plSellerAccountId;

    @ApiModelProperty(value = "亚马逊店铺ID")
    private Integer amazonShopId;

    @ApiModelProperty(value = "亚马逊店铺名")
    private String amazonShopName;

    @ApiModelProperty(value = "亚马逊授权token")
    private String mwsToken;

    @ApiModelProperty(value = "系统订单")
    private List<SysOrder> sysOrders;

    @ApiModelProperty(value = "订单详细商品信息")
    private List<AmazonOrderDetail> amazonOrderDetails;

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "卖家平台账号")
    private String amazonSellerAccount;

    @ApiModelProperty(value = "站点ID")
    private String marketplaceId;

    @ApiModelProperty(value = "亚马逊订单号")
    private String orderId;

    @ApiModelProperty(value = "订单状态")
    private String orderStatus;

    @ApiModelProperty(value = "下单时间")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:sss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:sss")
    private Date paymentTime;

    @ApiModelProperty(value = "最迟发货时间")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:sss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:sss")
    private Date latestShipTime;

    @ApiModelProperty(value = "最近一次更新时间")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:sss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:sss")
//    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;

    @ApiModelProperty(value = "付款方式")
    private String paymentMethod;

    @ApiModelProperty(value = "订单总金额")
    private BigDecimal orderTotal;

    @ApiModelProperty(value = "订单总金额的货币代码")
    private String currencyCode;

    @ApiModelProperty(value = "买家姓名")
    private String buyerName;

    @ApiModelProperty(value = "收货人姓名")
    private String consigneeName;

    public List<SysOrder> getSysOrders() {
        return sysOrders;
    }

    public void setSysOrders(List<SysOrder> sysOrders) {
        this.sysOrders = sysOrders;
    }

    public Date getLatestShipTime() {
        return latestShipTime;
    }

    public void setLatestShipTime(Date latestShipTime) {
        this.latestShipTime = latestShipTime;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public AmazonOrder() {
    }

    public Integer getAmazonShopId() {
        return amazonShopId;
    }

    public void setAmazonShopId(Integer amazonShopId) {
        this.amazonShopId = amazonShopId;
    }

    public String getAmazonShopName() {
        return amazonShopName;
    }

    public void setAmazonShopName(String amazonShopName) {
        this.amazonShopName = amazonShopName;
    }

    public Integer getPlSellerAccountId() {
        return plSellerAccountId;
    }

    public void setPlSellerAccountId(Integer plSellerAccountId) {
        this.plSellerAccountId = plSellerAccountId;
    }

    public AmazonOrder(Integer plSellerAccountId, Integer amazonShopId, String amazonShopName, String mwsToken, List<SysOrder> sysOrders, List<AmazonOrderDetail> amazonOrderDetails, Integer id, String amazonSellerAccount, String marketplaceId, String orderId, String orderStatus, Date paymentTime, Date latestShipTime, Date lastUpdateTime, String paymentMethod, BigDecimal orderTotal, String currencyCode, String buyerName, String consigneeName, String country, String countryCode, String state, String city, String district, String address1, String address2, String address3, String postalCode, String phone, String buyerEmail, String plSellerAccount, String plAmazonPayStatus, String plExchangeRate, BigDecimal plConvertUsaAmount, Double plActualShipping, String plTrackNumber, Byte plShipMethod, String plShippingCarrierUsed, Byte plProcessStatus, BigDecimal plTotalBulk, BigDecimal plTotalWeight, String plReferenceId, Date createDate, Date updateDate) {
        this.plSellerAccountId = plSellerAccountId;
        this.amazonShopId = amazonShopId;
        this.amazonShopName = amazonShopName;
        this.mwsToken = mwsToken;
        this.sysOrders = sysOrders;
        this.amazonOrderDetails = amazonOrderDetails;
        this.id = id;
        this.amazonSellerAccount = amazonSellerAccount;
        this.marketplaceId = marketplaceId;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.paymentTime = paymentTime;
        this.latestShipTime = latestShipTime;
        this.lastUpdateTime = lastUpdateTime;
        this.paymentMethod = paymentMethod;
        this.orderTotal = orderTotal;
        this.currencyCode = currencyCode;
        this.buyerName = buyerName;
        this.consigneeName = consigneeName;
        this.country = country;
        this.countryCode = countryCode;
        this.state = state;
        this.city = city;
        this.district = district;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.postalCode = postalCode;
        this.phone = phone;
        this.buyerEmail = buyerEmail;
        this.plSellerAccount = plSellerAccount;
        this.plAmazonPayStatus = plAmazonPayStatus;
        this.plExchangeRate = plExchangeRate;
        this.plConvertUsaAmount = plConvertUsaAmount;
        this.plActualShipping = plActualShipping;
        this.plTrackNumber = plTrackNumber;
        this.plShipMethod = plShipMethod;
        this.plShippingCarrierUsed = plShippingCarrierUsed;
        this.plProcessStatus = plProcessStatus;
        this.plTotalBulk = plTotalBulk;
        this.plTotalWeight = plTotalWeight;
        this.plReferenceId = plReferenceId;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public String getMwsToken() {
        return mwsToken;
    }

    public void setMwsToken(String mwsToken) {
        this.mwsToken = mwsToken;
    }

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "国家代码")
    private String countryCode;

    @ApiModelProperty(value = "省/州")
    private String state;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "区、县")
    private String district;

    @ApiModelProperty(value = "地址1")
    private String address1;

    @ApiModelProperty(value = "地址2")
    private String address2;

    @ApiModelProperty(value = "地址3")
    private String address3;

    @ApiModelProperty(value = "邮政编码")
    private String postalCode;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String buyerEmail;

    @ApiModelProperty(value = "卖家品连账号")
    private String plSellerAccount;

    public String getPlSellerAccount() {
        return plSellerAccount;
    }

    public void setPlSellerAccount(String plSellerAccount) {
        this.plSellerAccount = plSellerAccount;
    }

    @ApiModelProperty(value = "订单支付状态")

    private String plAmazonPayStatus;

    @ApiModelProperty(value = "汇率")
    private String plExchangeRate;

    @ApiModelProperty(value = "付款折合人民币金额")
    private BigDecimal plConvertUsaAmount;

    @ApiModelProperty(value = "实际运费")
    private Double plActualShipping;

    @ApiModelProperty(value = "跟踪号")
    private String plTrackNumber;

    @ApiModelProperty(value = "邮寄方式的可取值")
    private Byte plShipMethod;

    @ApiModelProperty(value = "货物承运公司")
    private String plShippingCarrierUsed;

    @ApiModelProperty(value = "订单处理状态:0待处理，1转入成功，2转入失败，3部分转入成功")
    private Byte plProcessStatus;

    @ApiModelProperty(value = "平台订单的产品总体积=单个体积x数量，单位是m³")
    private BigDecimal plTotalBulk;

    @ApiModelProperty(value = "平台订单的产品总重量=单个重量x数量，单位是g")
    private BigDecimal plTotalWeight;

    @ApiModelProperty(value = "全球配送订单的唯一编号,发货用")
    private String plReferenceId;

    @ApiModelProperty(value = "记录创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @ApiModelProperty(value = "记录更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

 /*   @ApiModelProperty(value = "订单详细商品信息集合")
    private List<AmazonOrderDetail> amazonOrderDetailList;*/

    public List<AmazonOrderDetail> getAmazonOrderDetails() {
        return amazonOrderDetails;
    }

    public void setAmazonOrderDetails(List<AmazonOrderDetail> amazonOrderDetails) {
        this.amazonOrderDetails = amazonOrderDetails;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**

     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tbl_amazon_order
     *
     * @mbg.generated 2018-12-06 15:30:55
     */
    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAmazonSellerAccount() {
        if (amazonSellerAccount == null) {
            return "";
        }
        return amazonSellerAccount;
    }

    public void setAmazonSellerAccount(String amazonSellerAccount) {
        this.amazonSellerAccount = amazonSellerAccount == null ? null : amazonSellerAccount.trim();
    }

    public String getMarketplaceId() {
        if (marketplaceId == null) {
            return "";
        }
        return marketplaceId;
    }

    public void setMarketplaceId(String marketplaceId) {
        this.marketplaceId = marketplaceId == null ? null : marketplaceId.trim();
    }

    public String getOrderId() {
        if (orderId == null) {
            return "";
        }
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getOrderStatus() {
        if (orderStatus == null) {
            return "";
        }
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus == null ? null : orderStatus.trim();
    }

    public String getPaymentMethod() {
        if (paymentMethod == null) {
            return "";
        }
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod == null ? null : paymentMethod.trim();
    }

    public BigDecimal getOrderTotal() {
        if (orderTotal == null) {
            return BigDecimal.valueOf(0);
        }
        return orderTotal;
    }

    public void setOrderTotal(BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getCurrencyCode() {
        if (currencyCode == null) {
            return "";
        }
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode == null ? null : currencyCode.trim();
    }

    public String getBuyerName() {
        if (buyerName == null) {
            return "";
        }
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName == null ? null : buyerName.trim();
    }

    public String getConsigneeName() {
        if (consigneeName == null) {
            return "";
        }
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName == null ? null : consigneeName.trim();
    }

    public String getCountry() {
        if (country == null) {
            return "";
        }
        return country;
    }

    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    public String getState() {
        if (state == null) {
            return "";
        }
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    public String getCity() {
        if (city == null) {
            return "";
        }
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getDistrict() {
        if (district == null) {
            return "";
        }
        return district;
    }

    public void setDistrict(String district) {
        this.district = district == null ? null : district.trim();
    }

    public String getAddress1() {
        if (address1 == null) {
            return "";
        }
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1 == null ? null : address1.trim();
    }

    public String getAddress2() {
        if (address2 == null) {
            return "";
        }
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2 == null ? null : address2.trim();
    }

    public String getAddress3() {
        if (address3 == null) {
            return "";
        }
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3 == null ? null : address3.trim();
    }

    public String getPostalCode() {
        if (postalCode == null) {
            return "";
        }
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode == null ? null : postalCode.trim();
    }

    public String getPhone() {
        if (phone == null) {
            return "";
        }
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getBuyerEmail() {
        if (buyerEmail == null) {
            return "";
        }
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail == null ? null : buyerEmail.trim();
    }

    public String getPlAmazonPayStatus() {
        if (plAmazonPayStatus == null) {
            return "";
        }
        return plAmazonPayStatus;
    }

    public void setPlAmazonPayStatus(String plAmazonPayStatus) {
        this.plAmazonPayStatus = plAmazonPayStatus == null ? null : plAmazonPayStatus.trim();
    }

    public String getPlExchangeRate() {
        if (plExchangeRate == null) {
            return "";
        }
        return plExchangeRate;
    }

    public void setPlExchangeRate(String plExchangeRate) {
        this.plExchangeRate = plExchangeRate == null ? null : plExchangeRate.trim();
    }

    public BigDecimal getPlConvertUsaAmount() {
        if (plConvertUsaAmount == null) {
            return BigDecimal.valueOf(0);
        }
        return plConvertUsaAmount;
    }

    public void setPlConvertUsaAmount(BigDecimal plConvertUsaAmount) {
        this.plConvertUsaAmount = plConvertUsaAmount;
    }

    public Double getPlActualShipping() {
        if (plActualShipping == null) {
            return Double.valueOf(0);
        }
        return plActualShipping;
    }

    public void setPlActualShipping(Double plActualShipping) {
        this.plActualShipping = plActualShipping;
    }

    public String getPlTrackNumber() {
        if (plTrackNumber == null) {
            return "";
        }
        return plTrackNumber;
    }

    public void setPlTrackNumber(String plTrackNumber) {
        this.plTrackNumber = plTrackNumber == null ? null : plTrackNumber.trim();
    }

    public Byte getPlShipMethod() {
        if (plShipMethod == null) {
            return 0;
        }
        return plShipMethod;
    }

    public void setPlShipMethod(Byte plShipMethod) {
        this.plShipMethod = plShipMethod;
    }

    public String getPlShippingCarrierUsed() {
        if (plShippingCarrierUsed == null) {
            return "";
        }
        return plShippingCarrierUsed;
    }

    public void setPlShippingCarrierUsed(String plShippingCarrierUsed) {
        this.plShippingCarrierUsed = plShippingCarrierUsed == null ? null : plShippingCarrierUsed.trim();
    }

    public Byte getPlProcessStatus() {
        if (plProcessStatus == null) {
            return 0;
        }
        return plProcessStatus;
    }

    public void setPlProcessStatus(Byte plProcessStatus) {
        this.plProcessStatus = plProcessStatus;
    }

    public BigDecimal getPlTotalBulk() {
        if (plTotalBulk == null) {
            return BigDecimal.valueOf(0);
        }
        return plTotalBulk;
    }

    public void setPlTotalBulk(BigDecimal plTotalBulk) {
        this.plTotalBulk = plTotalBulk;
    }

    public BigDecimal getPlTotalWeight() {
        if (plTotalWeight == null) {
            return BigDecimal.valueOf(0);
        }
        return plTotalWeight;
    }

    public void setPlTotalWeight(BigDecimal plTotalWeight) {
        this.plTotalWeight = plTotalWeight;
    }

    public String getPlReferenceId() {
        if (plReferenceId == null) {
            return "";
        }
        return plReferenceId;
    }

    public void setPlReferenceId(String plReferenceId) {
        this.plReferenceId = plReferenceId == null ? null : plReferenceId.trim();
    }

    @Override
    public String toString() {
        return "AmazonOrder{" +
                "deliveryTime=" + deliveryTime +
                ", plSellerAccountId=" + plSellerAccountId +
                ", amazonShopId=" + amazonShopId +
                ", amazonShopName='" + amazonShopName + '\'' +
                ", mwsToken='" + mwsToken + '\'' +
                ", sysOrders=" + sysOrders +
                ", amazonOrderDetails=" + amazonOrderDetails +
                ", id=" + id +
                ", amazonSellerAccount='" + amazonSellerAccount + '\'' +
                ", marketplaceId='" + marketplaceId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", paymentTime=" + paymentTime +
                ", latestShipTime=" + latestShipTime +
                ", lastUpdateTime=" + lastUpdateTime +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", orderTotal=" + orderTotal +
                ", currencyCode='" + currencyCode + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", consigneeName='" + consigneeName + '\'' +
                ", country='" + country + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", address3='" + address3 + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", phone='" + phone + '\'' +
                ", buyerEmail='" + buyerEmail + '\'' +
                ", plSellerAccount='" + plSellerAccount + '\'' +
                ", plAmazonPayStatus='" + plAmazonPayStatus + '\'' +
                ", plExchangeRate='" + plExchangeRate + '\'' +
                ", plConvertUsaAmount=" + plConvertUsaAmount +
                ", plActualShipping=" + plActualShipping +
                ", plTrackNumber='" + plTrackNumber + '\'' +
                ", plShipMethod=" + plShipMethod +
                ", plShippingCarrierUsed='" + plShippingCarrierUsed + '\'' +
                ", plProcessStatus=" + plProcessStatus +
                ", plTotalBulk=" + plTotalBulk +
                ", plTotalWeight=" + plTotalWeight +
                ", plReferenceId='" + plReferenceId + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

}