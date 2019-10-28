package com.rondaful.cloud.order.entity.cms;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@ApiModel(description = "返回前端的订单售后情况，作展示用")
public class OrderAfterVo {

    @ApiModelProperty(value = "申请时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "商品SKU")
    private String commoditySku;

    @ApiModelProperty(value = "名称")
    private String commodityName;

    @ApiModelProperty(value = "英文名称")
    private String commodityNameEn;

    @ApiModelProperty(value = "售后订单ID")
    private String orderAfterSalesId;

    @ApiModelProperty(value = "商品退款金额总价")
    private String commodityRefundMoney;

    @ApiModelProperty(value = "退货数量")
    private Long commodityNumber;

    @ApiModelProperty(value = "状态[ 1、待审核; 2、查看; 3、审核; 4、待确认; 5、已确认; 6、(已确认)系统超时自动确认; 7、已关闭; 8、(已关闭)系统超时已关闭; 9、全部确认; 10、退款完成; 11、已取消; 12、(已取消)系统超时关闭，没有编辑; 13、审核失败; 14、(审核失败)系统超时关闭，没有编辑; 15、编辑; 16、待退货; 17、提交物流信息; 18、退货中; 19、仓库已收货; 20、收货完成协商退款; 21、补发配货中; 22、补发已发货; 23、售后结案; 24、修改物流信息; 25、自动关闭; 26、拒绝退款; 27、退货完成等待退款; 29、自动收货; 30、重发; 31、同意退款; 32、取消; 33、新建提交; 34、修改提交; 35、退款中;36、拦截成功;37、作废]")
    private Long status;

    @ApiModelProperty(value = "售后类型[1-仅退款、2-退款+退货、3-补货]")
    private Long afterSalesType;

    public OrderAfterVo() {
    }

    public OrderAfterVo(Date createTime, String commoditySku, String commodityName, String commodityNameEn, String orderAfterSalesId, String commodityRefundMoney, Long commodityNumber, Long status, Long afterSalesType) {
        this.createTime = createTime;
        this.commoditySku = commoditySku;
        this.commodityName = commodityName;
        this.commodityNameEn = commodityNameEn;
        this.orderAfterSalesId = orderAfterSalesId;
        this.commodityRefundMoney = commodityRefundMoney;
        this.commodityNumber = commodityNumber;
        this.status = status;
        this.afterSalesType = afterSalesType;
    }

    @Override
    public String toString() {
        return "OrderAfterVo{" +
                "createTime=" + createTime +
                ", commoditySku='" + commoditySku + '\'' +
                ", commodityName='" + commodityName + '\'' +
                ", commodityNameEn='" + commodityNameEn + '\'' +
                ", orderAfterSalesId='" + orderAfterSalesId + '\'' +
                ", commodityRefundMoney='" + commodityRefundMoney + '\'' +
                ", commodityNumber=" + commodityNumber +
                ", status=" + status +
                ", afterSalesType=" + afterSalesType +
                '}';
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCommoditySku() {
        return commoditySku;
    }

    public void setCommoditySku(String commoditySku) {
        this.commoditySku = commoditySku;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getCommodityNameEn() {
        return commodityNameEn;
    }

    public void setCommodityNameEn(String commodityNameEn) {
        this.commodityNameEn = commodityNameEn;
    }

    public String getOrderAfterSalesId() {
        return orderAfterSalesId;
    }

    public void setOrderAfterSalesId(String orderAfterSalesId) {
        this.orderAfterSalesId = orderAfterSalesId;
    }

    public String getCommodityRefundMoney() {
        return commodityRefundMoney;
    }

    public void setCommodityRefundMoney(String commodityRefundMoney) {
        this.commodityRefundMoney = commodityRefundMoney;
    }

    public Long getCommodityNumber() {
        return commodityNumber;
    }

    public void setCommodityNumber(Long commodityNumber) {
        this.commodityNumber = commodityNumber;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getAfterSalesType() {
        return afterSalesType;
    }

    public void setAfterSalesType(Long afterSalesType) {
        this.afterSalesType = afterSalesType;
    }
}
