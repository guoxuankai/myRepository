package com.rondaful.cloud.supplier.entity.Logistics;

import java.util.List;

public class ErpProviderLogistics {

    //token
    private String interface_token;
    //ebayid
    private String ebay_name;
    //地址id
    private String interface_user_password;
    //平台订单号
    private List<String> platform_order_id;
    //交运偏好id
    private String customer_code;
    //类型 1:erp 2:品连 ,
    private Integer type;
    //发件人地址id
    private String sender_id;
    //揽收人地址id
    private String pickup_id;
    //退货人地址id
    private String refund_id;
    //应用key
    private String interface_user_name;
    //应用秘钥
    private String interface_user_key;

    public String getInterface_token() {
        return interface_token;
    }

    public void setInterface_token(String interface_token) {
        this.interface_token = interface_token;
    }

    public String getEbay_name() {
        return ebay_name;
    }

    public void setEbay_name(String ebay_name) {
        this.ebay_name = ebay_name;
    }

    public String getInterface_user_password() {
        return interface_user_password;
    }

    public void setInterface_user_password(String interface_user_password) {
        this.interface_user_password = interface_user_password;
    }

    public List<String> getPlatform_order_id() {
        return platform_order_id;
    }

    public void setPlatform_order_id(List<String> platform_order_id) {
        this.platform_order_id = platform_order_id;
    }

    public String getCustomer_code() {
        return customer_code;
    }

    public void setCustomer_code(String customer_code) {
        this.customer_code = customer_code;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getPickup_id() {
        return pickup_id;
    }

    public void setPickup_id(String pickup_id) {
        this.pickup_id = pickup_id;
    }

    public String getRefund_id() {
        return refund_id;
    }

    public void setRefund_id(String refund_id) {
        this.refund_id = refund_id;
    }

    public String getInterface_user_name() {
        return interface_user_name;
    }

    public void setInterface_user_name(String interface_user_name) {
        this.interface_user_name = interface_user_name;
    }

    public String getInterface_user_key() {
        return interface_user_key;
    }

    public void setInterface_user_key(String interface_user_key) {
        this.interface_user_key = interface_user_key;
    }

    @Override
    public String toString() {
        return "ErpProviderLogistics{" +
                "interface_token='" + interface_token + '\'' +
                ", ebay_name='" + ebay_name + '\'' +
                ", interface_user_password='" + interface_user_password + '\'' +
                ", platform_order_id=" + platform_order_id +
                ", customer_code='" + customer_code + '\'' +
                ", type=" + type +
                ", sender_id='" + sender_id + '\'' +
                ", pickup_id='" + pickup_id + '\'' +
                ", refund_id='" + refund_id + '\'' +
                ", interface_user_name='" + interface_user_name + '\'' +
                ", interface_user_key='" + interface_user_key + '\'' +
                '}';
    }
}
