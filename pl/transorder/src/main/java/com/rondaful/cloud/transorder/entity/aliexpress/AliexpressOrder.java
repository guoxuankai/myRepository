package com.rondaful.cloud.transorder.entity.aliexpress;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 速卖通订单主表
 * 实体类对应的数据表为：  t_aliexpress_order
 * @author guoxuankai
 * @date 2019-09-21 10:55:08
 */
@ApiModel(value ="AliexpressOrder")
public class AliexpressOrder implements Serializable {
    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty(value = "")
    private Long version;

    @ApiModelProperty(value = "交易编号 (父订单的交易编号)")
    private String orderId;

    @ApiModelProperty(value = "买家登录id")
    private String buyerLoginId;

    @ApiModelProperty(value = "	买家全名")
    private String buyerSignerFullName;

    @ApiModelProperty(value = "订单类型。（AE_COMMON:普通订单;AE_TRIAL:试用订单;AE_RECHARGE:手机充值订单)")
    private String bizType;

    @ApiModelProperty(value = "冻结状态")
    private String frozenStatus;

    @ApiModelProperty(value = "订单状态")
    private String orderStatus;

    @ApiModelProperty(value = "纠纷状态。(NO_ISSUE:无纠纷; IN_ISSUE:纠纷中; END_ISSUE:纠纷结束)")
    private String issueStatus;

    @ApiModelProperty(value = "订单创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;

    @ApiModelProperty(value = "支付成功时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtPayTime;

    @ApiModelProperty(value = "卖家全名")
    private String sellerSignerFullName;

    @ApiModelProperty(value = "卖家登录ID")
    private String sellerLoginId;

    @ApiModelProperty(value = "当前状态超时日期 （此时间为美国太平洋时间）")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date overTimeLeft;

    @ApiModelProperty(value = "邮寄目的国家")
    private String receiptCountry;

    @ApiModelProperty(value = "物流费用")
    private BigDecimal logisticsAmount;

    @ApiModelProperty(value = "物流费用币种")
    private String logisticsCode;

    @ApiModelProperty(value = "物流状态。（WAIT_SELLER_SEND_GOODS:等待卖家发货;")
    private String logisticsStatus;

    @ApiModelProperty(value = "品连物流状态")
    private String plLogisticsStatus;

    @ApiModelProperty(value = "品连物流发货时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date plLogisticsTime;

    @ApiModelProperty(value = "订单处理状态:0待处理，1转入成功，2转入失败，3部分转入成功")
    private Byte plProcessStatus;

    @ApiModelProperty(value = "品连账号")
    private String plAccount;

    @ApiModelProperty(value = "品连卖家id")
    private Integer plSellerId;

    @ApiModelProperty(value = "订单发货回传速卖通状态")
    private Byte callBackStatus;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creatTime;

    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "授权账号id")
    private Integer empowerId;

    @ApiModelProperty(value = "买家备注（订单级别）")
    private String memo;

    @ApiModelProperty(value = "订单明细")
    private List<AliexpressOrderChild> childs;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getBuyerLoginId() {
        return buyerLoginId;
    }

    public void setBuyerLoginId(String buyerLoginId) {
        this.buyerLoginId = buyerLoginId == null ? null : buyerLoginId.trim();
    }

    public String getBuyerSignerFullName() {
        return buyerSignerFullName;
    }

    public void setBuyerSignerFullName(String buyerSignerFullName) {
        this.buyerSignerFullName = buyerSignerFullName == null ? null : buyerSignerFullName.trim();
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType == null ? null : bizType.trim();
    }

    public String getFrozenStatus() {
        return frozenStatus;
    }

    public void setFrozenStatus(String frozenStatus) {
        this.frozenStatus = frozenStatus == null ? null : frozenStatus.trim();
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus == null ? null : orderStatus.trim();
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus == null ? null : issueStatus.trim();
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getGmtPayTime() {
        return gmtPayTime;
    }

    public void setGmtPayTime(Date gmtPayTime) {
        this.gmtPayTime = gmtPayTime;
    }

    public String getSellerSignerFullName() {
        return sellerSignerFullName;
    }

    public void setSellerSignerFullName(String sellerSignerFullName) {
        this.sellerSignerFullName = sellerSignerFullName == null ? null : sellerSignerFullName.trim();
    }

    public String getSellerLoginId() {
        return sellerLoginId;
    }

    public void setSellerLoginId(String sellerLoginId) {
        this.sellerLoginId = sellerLoginId == null ? null : sellerLoginId.trim();
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getOverTimeLeft() {
        return overTimeLeft;
    }

    public void setOverTimeLeft(Date overTimeLeft) {
        this.overTimeLeft = overTimeLeft;
    }

    public String getReceiptCountry() {
        return receiptCountry;
    }

    public void setReceiptCountry(String receiptCountry) {
        this.receiptCountry = receiptCountry == null ? null : receiptCountry.trim();
    }

    public BigDecimal getLogisticsAmount() {
        return logisticsAmount;
    }

    public void setLogisticsAmount(BigDecimal logisticsAmount) {
        this.logisticsAmount = logisticsAmount;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode == null ? null : logisticsCode.trim();
    }

    public String getLogisticsStatus() {
        return logisticsStatus;
    }

    public void setLogisticsStatus(String logisticsStatus) {
        this.logisticsStatus = logisticsStatus == null ? null : logisticsStatus.trim();
    }

    public String getPlLogisticsStatus() {
        return plLogisticsStatus;
    }

    public void setPlLogisticsStatus(String plLogisticsStatus) {
        this.plLogisticsStatus = plLogisticsStatus == null ? null : plLogisticsStatus.trim();
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getPlLogisticsTime() {
        return plLogisticsTime;
    }

    public void setPlLogisticsTime(Date plLogisticsTime) {
        this.plLogisticsTime = plLogisticsTime;
    }

    public Byte getPlProcessStatus() {
        return plProcessStatus;
    }

    public void setPlProcessStatus(Byte plProcessStatus) {
        this.plProcessStatus = plProcessStatus;
    }

    public String getPlAccount() {
        return plAccount;
    }

    public void setPlAccount(String plAccount) {
        this.plAccount = plAccount == null ? null : plAccount.trim();
    }

    public Integer getPlSellerId() {
        return plSellerId;
    }

    public void setPlSellerId(Integer plSellerId) {
        this.plSellerId = plSellerId;
    }

    public Byte getCallBackStatus() {
        return callBackStatus;
    }

    public void setCallBackStatus(Byte callBackStatus) {
        this.callBackStatus = callBackStatus;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getEmpowerId() {
        return empowerId;
    }

    public void setEmpowerId(Integer empowerId) {
        this.empowerId = empowerId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }

    public List<AliexpressOrderChild> getChilds() {
        return childs;
    }

    public void setChilds(List<AliexpressOrderChild> childs) {
        this.childs = childs;
    }
}