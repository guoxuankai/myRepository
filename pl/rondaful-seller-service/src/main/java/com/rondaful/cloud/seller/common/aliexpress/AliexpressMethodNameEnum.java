package com.rondaful.cloud.seller.common.aliexpress;


public enum AliexpressMethodNameEnum {

    GETPHOTOBANKINFO("aliexpress.photo.getphotobankinfo","获取图片银行信息"),
    FINDPHOTOBANKGROUP("aliexpress.photo.findphotobankgroup","获取图片银行分组"),
    FINDIMAGEPAGE("aliexpress.photo.findimagepage","获取图片银行图片"),
    FINDPRODUCTPAGE("aliexpress.publish.findproductpage","获取商品列表分页查询"),
    FINDAEPRODUCTBYID("aliexpress.publish.findaeproductbyid","商品详情查询"),
    RENEWEXPIRE("aliexpress.publish.renewexpire","商品延长有效期"),
    FINDAEPRODUCTPROHIBITEDWORDS("aliexpress.publish.findaeproductprohibitedwords","商品违禁词查询"),
    UPLOADIMAGEFORSDK("aliexpress.publish.uploadimageforsdk","商品违禁词查询"),
    QUERYPHOTOBANKIMAGEBYPATHS("aliexpress.photo.queryphotobankimagebypaths","根据path查询图片信息"),
    END("","");

    private String code;
    private String msg;
    private AliexpressMethodNameEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public String getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }

}
