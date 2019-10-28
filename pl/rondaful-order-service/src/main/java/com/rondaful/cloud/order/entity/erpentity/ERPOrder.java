package com.rondaful.cloud.order.entity.erpentity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value ="ERPOrder")//发货时调用ERP订单接收接口orderReceive使用
public class ERPOrder implements Serializable {
    @ApiModelProperty(value = "订单项集合")
    private List<ERPOrderDetail> order_detail;

    @ApiModelProperty(value = "订单项集合")
    private ERPShipping shipping;

    @ApiModelProperty(value = "订单号")
    private String order_number;//对应我们系统订单号:PL20181227101533742AAAAAAAA
    @ApiModelProperty(value = "买家")
    private String buyer;
    @ApiModelProperty(value = "买家ID")
    private String buyer_id;//交易号，财务模块实际付款返回的。可不传
//    @ApiModelProperty(value = "销售员")
//    private String seller;
//    @ApiModelProperty(value = "销售员ID")
//    private String seller_id;
    @ApiModelProperty(value = "收货人")
    private String consignee;
    @ApiModelProperty(value = "国家简称")
    private String country_code;
    @ApiModelProperty(value = "城市")
    private String city;
    @ApiModelProperty(value = "省份")
    private String province;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "地址2")
    private String address2;
    @ApiModelProperty(value = "邮编")
    private String zipcode;
    @ApiModelProperty(value = "电话")
    private String tel;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "邮箱地址")
    private String email;
    @ApiModelProperty(value = "支付ID")
    private String pay_id;//交易号，财务模块实际付款返回的。
    @ApiModelProperty(value = "支付费用")
    private Double pay_fee;//订单总售价：预估物流费+商品金额
    @ApiModelProperty(value = "最迟发货时间")
    private Integer uploaded_deadline;//这版先不要，待确认
    @ApiModelProperty(value = "订单金额")
    private Double order_amount;//系统订单总价
    @ApiModelProperty(value = "下单时间")
    private Integer order_time;
    @ApiModelProperty(value = "记录创建时间")
    private Integer create_time;//当前时间
    @ApiModelProperty(value = "支付时间")
    private Integer pay_time;//当前时间
    @ApiModelProperty(value = "订单留言")
    private String message;
    @ApiModelProperty(value = "渠道ID（固定传31）")
    private Integer channel_id;//传31
    @ApiModelProperty(value = "渠道账号ID（就是分销商ID）")
    private Integer channel_account_id;//系统卖家ID
    @ApiModelProperty(value = "渠道账号简称")
    private String channel_account_code;//brandslink
    @ApiModelProperty(value = "渠道订单ID")
    private Integer channel_order_id;//系统订单ID
    @ApiModelProperty(value = "渠道订单号")
    private String channel_order_number;//每次推送都新生成一个，在系统订单表里保存,此sysOrderId对应系统订单表中order_track_id
    @ApiModelProperty(value = "关联订单ID")
    private Integer related_order_id;//关联订单ID取来源订单ID
    @ApiModelProperty(value = "Wish订单是否海外仓（0-是 1-否）")
    private Integer is_wish_express;//默认传1
    @ApiModelProperty(value = "货币简写")
    private String currency_code;//默认CNY
    @ApiModelProperty(value = "渠道运费")
    private Double channel_shipping_free;//系统预估物流费
    @ApiModelProperty(value = "渠道折扣运费")
    private Double channel_shipping_discount;//系统预估物流费
    @ApiModelProperty(value = "渠道手续费")
    private Double channel_cost;//默认0
    @ApiModelProperty(value = "买家选择的物流")
    private String buyer_selected_logistics;//系统订单的货物邮寄方式
    @ApiModelProperty(value = "Wish订单是否妥投 1-是 0-否")
    private Integer requires_delivery_confirmation;//默认0
    @ApiModelProperty(value = "站点简称")
    private String site_code;//默认为""
    @ApiModelProperty(value = "商品总额")
    private Double goods_amount;//商品单价x数量再取和


    public List<ERPOrderDetail> getOrder_detail() {
        return order_detail;
    }

    public void setOrder_detail(List<ERPOrderDetail> order_detail) {
        this.order_detail = order_detail;
    }

    public ERPShipping getShipping() {
        return shipping;
    }

    public void setShipping(ERPShipping shipping) {
        this.shipping = shipping;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPay_id() {
        return pay_id;
    }

    public void setPay_id(String pay_id) {
        this.pay_id = pay_id;
    }

    public Double getPay_fee() {
        return pay_fee;
    }

    public void setPay_fee(Double pay_fee) {
        this.pay_fee = pay_fee;
    }

    public Integer getUploaded_deadline() {
        return uploaded_deadline;
    }

    public void setUploaded_deadline(Integer uploaded_deadline) {
        this.uploaded_deadline = uploaded_deadline;
    }

    public Double getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(Double order_amount) {
        this.order_amount = order_amount;
    }

    public Integer getOrder_time() {
        return order_time;
    }

    public void setOrder_time(Integer order_time) {
        this.order_time = order_time;
    }

    public Integer getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Integer create_time) {
        this.create_time = create_time;
    }

    public Integer getPay_time() {
        return pay_time;
    }

    public void setPay_time(Integer pay_time) {
        this.pay_time = pay_time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(Integer channel_id) {
        this.channel_id = channel_id;
    }

    public Integer getChannel_account_id() {
        return channel_account_id;
    }

    public void setChannel_account_id(Integer channel_account_id) {
        this.channel_account_id = channel_account_id;
    }

    public String getChannel_account_code() {
        return channel_account_code;
    }

    public void setChannel_account_code(String channel_account_code) {
        this.channel_account_code = channel_account_code;
    }

    public Integer getChannel_order_id() {
        return channel_order_id;
    }

    public void setChannel_order_id(Integer channel_order_id) {
        this.channel_order_id = channel_order_id;
    }

    public String getChannel_order_number() {
        return channel_order_number;
    }

    public void setChannel_order_number(String channel_order_number) {
        this.channel_order_number = channel_order_number;
    }

    public Integer getRelated_order_id() {
        return related_order_id;
    }

    public void setRelated_order_id(Integer related_order_id) {
        this.related_order_id = related_order_id;
    }

    public Integer getIs_wish_express() {
        return is_wish_express;
    }

    public void setIs_wish_express(Integer is_wish_express) {
        this.is_wish_express = is_wish_express;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public Double getChannel_shipping_free() {
        return channel_shipping_free;
    }

    public void setChannel_shipping_free(Double channel_shipping_free) {
        this.channel_shipping_free = channel_shipping_free;
    }

    public Double getChannel_shipping_discount() {
        return channel_shipping_discount;
    }

    public void setChannel_shipping_discount(Double channel_shipping_discount) {
        this.channel_shipping_discount = channel_shipping_discount;
    }

    public Double getChannel_cost() {
        return channel_cost;
    }

    public void setChannel_cost(Double channel_cost) {
        this.channel_cost = channel_cost;
    }

    public String getBuyer_selected_logistics() {
        return buyer_selected_logistics;
    }

    public void setBuyer_selected_logistics(String buyer_selected_logistics) {
        this.buyer_selected_logistics = buyer_selected_logistics;
    }

    public Integer getRequires_delivery_confirmation() {
        return requires_delivery_confirmation;
    }

    public void setRequires_delivery_confirmation(Integer requires_delivery_confirmation) {
        this.requires_delivery_confirmation = requires_delivery_confirmation;
    }

    public String getSite_code() {
        return site_code;
    }

    public void setSite_code(String site_code) {
        this.site_code = site_code;
    }

    public Double getGoods_amount() {
        return goods_amount;
    }

    public void setGoods_amount(Double goods_amount) {
        this.goods_amount = goods_amount;
    }
}
