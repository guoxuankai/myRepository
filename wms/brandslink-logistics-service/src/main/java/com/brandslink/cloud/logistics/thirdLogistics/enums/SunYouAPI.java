package com.brandslink.cloud.logistics.thirdLogistics.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SunYouAPI {

    CREATEANDCONFIRMPACKAGES("创建并预报包裹", "/createAndConfirmPackages"),
    GETPACKAGESDETAILS("获取包裹详情", "/getPackagesDetails"),
    GETPACKAGESSTATUS("获取包裹状态", "/getPackagesStatus"),
    GETPACKAGESTRACKINGNUMBER("获取包裹追踪号", "/getPackagesTrackingNumber"),
    DELETEPACKAGES("删除包裹", "/deletePackages"),
    GETPACKAGESLABELVARIABLES("获取包裹面单变量", "/getPackagesLabelVariables"),
    GETPACKAGESLABEL("获取包裹面单", "/getPackagesLabel"),
    FINDSHIPPINGMETHODS("查询邮寄方式", "/findShippingMethods"),
    OPERATIONPACKAGES("修改预报重量", "/operationPackages");

    @Getter
    private String msg;
    @Getter
    private String api;
}
