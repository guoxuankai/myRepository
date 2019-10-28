package com.rondaful.cloud.order.enums;

public class SkuEnmus {


    /**
     * sku类型
     */
    public enum skuType{

        /**
         * 平台sku
         */
        PLATFORM("platformSku","平台sku"),

        /**
         * 品连sku
         */
        PLIAN("plianSku","品连sku");

        private String key;
        private String message;


        skuType(String key, String message) {
            this.key = key;
            this.message = message;
        }

        public String getKey() {
            return key;
        }

      /*  public void setKey(String key) {
            this.key = key;
        }*/

        public String getMessage() {
            return message;
        }

       /* public void setMessage(String message) {
            this.message = message;
        }*/
    }



    /**
     * 规则类型
     */
    public enum ruleType{  //规则类型[splitByNum:按位数分隔  spliteByChar:按字符分隔 ]

        /**
         * 平台sku
         */
        splitByNum("splitByNum","按位数分隔"),

        /**
         * 品连sku
         */
        spliteByChar("spliteByChar","按字符分隔");

        private String type;
        private String message;


        ruleType(String type, String message) {
            this.type = type;
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }}


}
