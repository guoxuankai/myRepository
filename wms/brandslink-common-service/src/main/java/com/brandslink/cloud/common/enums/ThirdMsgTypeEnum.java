package com.brandslink.cloud.common.enums;

/**
 * @author yangzefei
 * @Classname ThirdMsgTypeEnum
 * @Description 推送至第三方的消息类型
 * @Date 2019/8/3 15:13
 */
public enum ThirdMsgTypeEnum {

    TEST("TS_10001","测试"),
    INVENTORY_CHANG("IC_10001","库存变动通知"),
    OUTBOUND_DELIVERY("CK_10001","出库发货通知");

    private String desc;
    private String msgCode;

    ThirdMsgTypeEnum(String msgCode, String desc) {
        this.desc = desc;
        this.msgCode = msgCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }
}
