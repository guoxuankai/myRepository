package com.rondaful.cloud.order.enums;

import java.util.Objects;

public class PayInfoEnum {
    public enum PayStatusEnum {

        TO_BE_PAY("待支付", (byte) 0),
        FREEZE_FAILURE("冻结失败", (byte) 10),
        FREEZE_SUCCESS("冻结成功", (byte) 11),
        IN_THE_PAYMENT("付款中", (byte) 20),
        PAYMENT_SUCCESS("付款成功", (byte) 21),
        PAYMENT_FAILURE("付款失败", (byte) 22),
        GENERATION_OF_FILLING_MONEY("待补款", (byte) 30),
        CANCELLED("已取消", (byte) 40);

        public String msg;
        public Byte value;

        PayStatusEnum(String msg, Byte value) {
            this.msg = msg;
            this.value = value;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Byte getValue() {
            return value;
        }

        public void setValue(Byte value) {
            this.value = value;
        }
    }


    public enum PayMethodEnum {

        ACCOUNT_BALANCE("USD账户", (byte) 1),
        WECHAT("微信", (byte) 2),
        PayTreasure("支付宝", (byte) 3),
        OFFLINE_PAYMENT("线下支付", (byte) 4);

        public String payMethodMsg;
        public byte payMethod;

        PayMethodEnum(String payMethodMsg, byte payMethod) {
            this.payMethodMsg = payMethodMsg;
            this.payMethod = payMethod;
        }

        public String getPayMethodMsg() {
            return payMethodMsg;
        }

        public void setPayMethodMsg(String payMethodMsg) {
            this.payMethodMsg = payMethodMsg;
        }

        public byte getPayMethod() {
            return payMethod;
        }

        public void setPayMethod(byte payMethod) {
            this.payMethod = payMethod;
        }

        public static byte getPayMethod(String payMethodMsg) {
            PayMethodEnum[] payMethodEnums = PayMethodEnum.values();
            for (PayMethodEnum payMethodEnum : payMethodEnums) {
                if (Objects.equals(payMethodMsg, payMethodEnum.getPayMethodMsg())) {
                    return payMethodEnum.getPayMethod();
                }
            }

            return 0;
        }
    }
}