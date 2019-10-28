package com.rondaful.cloud.order.entity.cms;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ApiModel(description = "订单系统调用根据订单号查询售后订单详情")
public class OrderAfterSalesOrderDetailsModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "售后编号")
    private String numberingId;

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "退款金额")
    private String refundMoney;

    @ApiModelProperty(value = "状态[ 1、待审核; 2、查看; 3、审核; 4、待确认; 5、已确认; 6、(已确认)系统超时自动确认; 7、已关闭; 8、(已关闭)系统超时已关闭; 9、全部确认; 10、退款完成; 11、已取消; 12、(已取消)系统超时关闭，没有编辑; 13、审核失败; 14、(审核失败)系统超时关闭，没有编辑; 15、编辑; 16、待退货; 17、提交物流信息; 18、退货中; 19、仓库已收货; 20、收货完成协商退款; 21、补发配货中; 22、补发已发货; 23、售后结案; 24、修改物流信息; 25、自动关闭; 26、拒绝退款; 27、退货完成等待退款; 29、自动收货; 30、重发; 31、同意退款; 32、取消; 33、新建提交; 34、修改提交; 35、退款中;36、拦截成功;37、作废]")
    private Long status;

//    @JSONField(format = DateUtils.FORMAT_2)
    @ApiModelProperty(value = "申请时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "售后类型[1-仅退款、2-退款+退货、3-补货]")
    private Long afterSalesType;

    private List<OrderAfterSalesCommodityOrderDetail> orderAfterSalesCommodityOrderDetails;

    public List<OrderAfterSalesCommodityOrderDetail> getOrderAfterSalesCommodityOrderDetails() {
        return orderAfterSalesCommodityOrderDetails;
    }

    public void setOrderAfterSalesCommodityOrderDetails(List<OrderAfterSalesCommodityOrderDetail> orderAfterSalesCommodityOrderDetails) {
        this.orderAfterSalesCommodityOrderDetails = orderAfterSalesCommodityOrderDetails;
    }

    public String getNumberingId() {
        return numberingId;
    }

    public void setNumberingId(String numberingId) {
        this.numberingId = numberingId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRefundMoney() {
        return refundMoney;
    }

    public void setRefundMoney(String refundMoney) {
        this.refundMoney = refundMoney;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getAfterSalesType() {
        return afterSalesType;
    }

    public void setAfterSalesType(Long afterSalesType) {
        this.afterSalesType = afterSalesType;
    }
}
