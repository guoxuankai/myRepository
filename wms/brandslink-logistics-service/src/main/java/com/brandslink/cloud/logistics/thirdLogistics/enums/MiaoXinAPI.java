package com.brandslink.cloud.logistics.thirdLogistics.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MiaoXinAPI {
    SELECTAUTH("身份认证", "/selectAuth.htm"),
    GETPRODUCTLIST("渠道列表", "/getProductList.htm"),
    CREATEORDERAPI("添加订单", "/createOrderApi.htm"),
    CREATEORDERBATCHAPI("批量下单", "/createOrderBatchApi.htm"),
    POSTORDERAPI("标记发货", "/postOrderApi.htm"),
    SELECTTRACK("轨迹查询", "/selectTrack.htm"),
    GETORDERTRACKINGNUMBER("获取跟踪号", "/getOrderTrackingNumber.htm"),
    UPDATEORDERWEIGHTBYAPI("更新预报重量", "/updateOrderWeightByApi.htm"),
    MODIFYINSURANCE("修改保险金额", "/modifyInsurance.htm"),

    PRINTLABEL("打印标签", "/order/FastRpt/PDF_NEW.aspx"),
    SELECTLABELTYPE("获取所有系统支持的打印类型", "/selectLabelType.htm"),
    /**
     * 特殊渠道标签打印地址
     */
    GETEUBPRINTPATH("E邮宝返回pdf路径", "/getEUBPrintPath.htm"),
    PRINTFPXAPI("FPX标签", "/printFpxApi.htm"),
    DOWNLOADONEWORLDLABEL("一级专线标签", "/downloadOneWorldLabel.htm");

    @Getter
    private String msg;
    @Getter
    private String api;
}