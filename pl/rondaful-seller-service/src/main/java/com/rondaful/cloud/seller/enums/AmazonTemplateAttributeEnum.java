package com.rondaful.cloud.seller.enums;

/**
 * 亚马逊模板属性相关
 */
public class AmazonTemplateAttributeEnum {


    /**
     * 模板启停状态
     */
    public enum IsDisabledE {   //是否启用 0： 启用，1：禁用
        OK(0,"启用"),
        NOT(1,"禁用");

        /** 枚举码. */
        private final Integer code;

        /** 描述信息. */
        private final String desc;

        private IsDisabledE(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }


    /**
     * 属性是否必填
     */
    public enum Required{  //0:可选，1：必选
        CAN(0,"可选"),
        REQ(1,"必须");

        /** 枚举码. */
        private final Integer code;

        /** 描述信息. */
        private final String desc;

        Required(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

}
