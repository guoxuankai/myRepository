package com.rondaful.cloud.seller.enums;

public class AliexpressEnum{
    /**
     * 速卖通刊登状态
     */
    public enum AliexpressStatusEnum {

        DRAFT(1,"草稿","Draft"),
        PUBLISH(2,"刊登中","Publishing"),
        PUBLISH_FAILED(3,"刊登失败","Fail to list"),
        AUDIT(4,"审核中","Under review"),
        AUDIT_FAILED(5,"审核失败","Audit failure"),
        SALE(6,"正在销售","On sale"),
        END(7,"下架","Ended")
        ;
        private int code;
        private String msg;
        private String msgEn;
        private AliexpressStatusEnum(int code, String msg,String msgEn) {
            this.code = code;
            this.msg = msg;
            this.msgEn = msgEn;
        }
        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        public String getMsgEn() {
            return msgEn;
        }


        public static String getValueByCode(int code,boolean bool){
            for(AliexpressStatusEnum aliexpress:AliexpressStatusEnum.values()){
                if(code==aliexpress.getCode()){
                    if(bool) {
                        return aliexpress.getMsgEn();
                    }else{
                        return aliexpress.getMsg();
                    }
                }
            }
            return  null;
        }

    }

    /**
     * 更新状态
     */
    public enum AliexpressUpdatestatusEnum {

        UPDATING(1,"更新中","updating"),
        UPDATE_UCCESSFULLY(2,"更新成功","update successfully"),
        UPDATE_FAILED(3,"更新失败","Update failed");
        private int code;
        private String msg;
        private String msgEn;
        private AliexpressUpdatestatusEnum(int code, String msg, String msgEn) {
            this.code = code;
            this.msg = msg;
            this.msgEn = msgEn;
        }
        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        public String getMsgEn() {
            return msgEn;
        }


        public static String getValueByCode(int code,boolean bool){
            for(AliexpressUpdatestatusEnum aliexpress: AliexpressUpdatestatusEnum.values()){
                if(code==aliexpress.getCode()){
                    if(bool) {
                        return aliexpress.getMsgEn();
                    }else{
                        return aliexpress.getMsg();
                    }
                }
            }
            return  null;
        }

    }

}