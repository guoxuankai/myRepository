package com.rondaful.cloud.seller.enums;


/**
 * 亚马逊获取报告的枚举类型
 */
public enum ReportTypeEnum {

    _GET_MERCHANT_LISTINGS_DATA_("_GET_MERCHANT_LISTINGS_DATA_", "在售商品报告"),
    _GET_MERCHANT_LISTINGS_ALL_DATA_("_GET_MERCHANT_LISTINGS_ALL_DATA_","全部商品报告");

    /**
     * 枚举值
     */
    private String reportTyp;

    /**
     * 枚举描述
     */
    private String mssage;

    ReportTypeEnum(String reportTyp, String mssage) {
        this.reportTyp = reportTyp;
        this.mssage = mssage;
    }

    public String getReportTyp() {
        return reportTyp;
    }


    public String getMssage() {
        return mssage;
    }
}
