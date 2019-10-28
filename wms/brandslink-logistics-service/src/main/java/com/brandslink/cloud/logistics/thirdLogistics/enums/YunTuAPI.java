package com.brandslink.cloud.logistics.thirdLogistics.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum YunTuAPI {
    GETCOUNTRY("查询国家简码", "/Common/GetCountry"),
    GETSHIPPINGMETHODS("查询运输方式", "/Common/GetShippingMethods"),
    GETGOODSTYPE("查询货品类型", "/Common/GetGoodsType"),
    GETPRICETRIAL("查询价格", "/Freight/GetPriceTrial"),
    GETTRACKINGNUMBER("查询跟踪号", "/Waybill/GetTrackingNumber"),
    GETSENDER("查询发件人信息", "/WayBill/GetSender"),
    CREATEORDER("运单申请", "/WayBill/CreateOrder"),
    GETORDER("查询运单", "/WayBill/GetOrder"),
    UPDATEWEIGHT("修改订单预报重量", "/WayBill/UpdateWeight"),
    DELETE("订单删除", "/WayBill/Delete"),
    INTERCEPT("订单拦截", "/WayBill/Intercept"),
    PRINT("标签打印", "/Label/Print"),
    GETSHIPPINGFEEDETAIL("查询物流运费明细", "/Freight/GetShippingFeeDetail"),
    REGISTER("用户注册", "/Common/Register"),
    GETTRACKINFO("查询物流轨迹信息", "/Tracking/GetTrackInfo");

    @Getter
    private String msg;
    @Getter
    private String api;
}