package com.rondaful.cloud.transorder.enums;

import java.text.MessageFormat;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2019-01-16 10:52
 * 包名: com.rondaful.cloud.common.enums
 * 描述:
 */
public class OrderHandleLogEnum {
    public enum Content {
        NEW_ORDER("新建订单【xxx】"),
        EDIT_ORDER("编辑发货仓库变更为【参数1】；邮寄方式变更为【参数2】"),
        EDIT_PACKAGE("编辑包裹【参数1】：发货仓库变更为【参数2】；邮寄方式变更为【参数3】"),
        ORDER_RULE("订单规则自动匹配发货仓库【参数1】，自动匹配物流方式【参数2】"),
        STOCK_OUT("订单【xxx】所选仓库对应商品缺货"),
        PUSH_SUCCESS("订单【xxx】发货推送成功"),
        FREEZE_SUCCESS("订单【xxx】发货冻结金额成功"),
        FREEZE_FAILURE("订单【xxx】发货冻结金额失败"),
        CANCEL_FREEZE_SUCCESS("订单【xxx】发货取消冻结金额成功"),
        CANCEL_FREEZE_FAILURE("订单【xxx】发货取消冻结金额失败"),
        PUSH_FAIL_1("订单【XXX】发货推送失败，物流方式不支持"),
        PUSH_FAIL_2("订单【XXX】发货，库存不足"),
        PUSH_FAIL_3("订单【XXX】发货推送失败，未填写仓库或物流方式不支持"),
        SPLIT_1("订单【参数1】拆分成功"),
        SPLIT_2("订单【参数1】拆分成功，拆分子订单号【参数2】和【参数3】和【参数4】"),
        SPLIT_3("订单【参数1】拆分成功，拆分子订单号【参数2】和【参数3】和【参数4】和【参数5】"),
        MERGE_SUCCESS("订单【参数1】合并成功，合并主订单号【参数2】"),
        PACKAGE_SPLIT("订单【参数1】拆分包裹成功,拆分后的包裹号为【参数2】,【参数3】，...."),
        REVOCATION_PACKAGE_SPLIT("订单【XXX】撤销拆分包裹成功"),
        PACKAGE_MERGE("订单【参数1】和订单【参数2】合并包裹成功，合并后的包裹号为【参数3】"),
        REVOCATION_PACKAGE_MERGE("订单【XXX】撤销合并包裹成功"),
        PICKING_CARGO("订单【参数1】配货中"),
        SHIPPED("订单【参数1】已发货，跟踪号【参数2】"),
        RETURN_SALE_MONEY_APPLY("订单【参数1】申请退货退款，售后单号【参数2】"),
        RETURN_MONEY_APPLY("订单【参数1】申请仅退款，售后单号【参数2】"),
        RESHIPPED_APPLY("订单【参数1】申请补发货，售后单号【参数2】"),
        REFUNDED("订单【参数1】已退款，售后单号【参数2】"),
        RESHIPPED_FINISH("订单【参数1】补发货完成，售后单号【参数2】"),
        RETURN_SALE_MONEY_FINISH("订单【参数1】退货退款完成，售后单号【参数2】"),
        RECEIVED("订单【xxx】已收货"),
        CANCEL("订单【xxx】作废"),
        PACKAGE_INTERCEPT("包裹【xxx】已拦截"),
        PACKAGE_INTERCEPT_FAILURE("包裹【xxx】拦截失败"),
        ORDER_INTERCEPT("订单【xxx】已拦截"),
        ORDER_INTERCEPT_FAILURE("订单【xxx】拦截失败"),
        ORDER_INTERCEPT_PARTLY_FAILURE("订单【xxx】部分拦截失败"),
        GOODCANGLIST("获取谷仓订单【xxx】出错，出错原因为【xxx】"),
        CANCELINVALID("订单【xxx】取消作废"),
        COMPLETED("订单【xxx】已完成"),
        SKU_BIND_INSERT("平台SKU【XXX】已绑定品连SKU【XXX】"),
        SKU_BIND_UPDATE("平台SKU【XXX】改绑至品连SKU【XXX】"),
        SKU_BIND_REMOVE("平台SKU【XXX】被移除"),
        MAPPING_PUBLISH_RULE_SUCCESS("自动匹配刊登规则成功"),
        MAPPING_WAREHOUSE_RULE_SUCCESS("自动匹配仓库规则【XXX】成功"),
        MAPPING_LOGISTICS_RULE_SUCCESS("自动匹配物流规则【XXX】成功"),
        MAPPING_ORDER_RULE_FAILURE("自动匹配物流规则失败");
        private String msg;

        Content(String msg) {
            this.msg = msg;
        }

        public String newOrder(String parm1) {
            String pattern = "新建订单【{0}】";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String editOrder(String parm1, String parm2) {
            String pattern = "编辑发货仓库变更为【{0}】；邮寄方式变更为【{1}】";
            Object[] params = new Object[]{parm1, parm2};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String editPackage(String parm1, String parm2,String parm3) {
            String pattern = "编辑包裹【{0}】：发货仓库变更为【{1}】；邮寄方式变更为【{2}】";
            Object[] params = new Object[]{parm1, parm2,parm3};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String orderRule(String parm1, String parm2) {
            String pattern = "订单规则自动匹配发货仓库【{0}】，自动匹配物流方式【{1}】";
            Object[] params = new Object[]{parm1, parm2};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String stockOut(String parm1) {
            String pattern = "订单【{0}】所选仓库对应商品缺货";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String pushSuccess(String parm1) {
            String pattern = "订单【{0}】发货推送成功";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String freezeSuccess(String parm1) {
            String pattern = "订单【{0}】发货冻结金额成功";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }
        public String freezeFailure(String parm1) {
            String pattern = "订单【{0}】发货冻结金额失败";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }
        public String cancelFreezeSuccess(String parm1) {
            String pattern = "订单【{0}】发货取消冻结金额成功";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }
        public String cancelFreezeFailure(String parm1) {
            String pattern = "订单【{0}】发货取消冻结金额失败";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String pushFail_1(String parm1) {
            String pattern = "订单【{0}】发货推送失败，物流方式不支持";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String pushFail_2(String parm1) {
            String pattern = "订单【{0}】发货，库存不足";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String pushFail_3(String parm1) {
            String pattern = "订单【{0}】发货推送失败，未填写仓库或物流方式不支持";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }


        /**
         * 拆分包裹订单操作日志  wujiachuang
         * @param parm   需要拆分包裹的订单号
         * @param parm1  拆分后的包裹号
         * @return
         */
        public String packageSplit(String parm,String... parm1) {
            String pattern = "拆分后的包裹号为";
            for (int i = 0; i < parm1.length; i++) {
                pattern+="【{"+i+"}】,";
            }
            pattern = pattern.substring(0, pattern.length()-1);
            String value = MessageFormat.format(pattern, parm1);
            String patterr2 = "订单【{0}】拆分包裹成功,";
            Object[] params2 = new Object[]{parm};
            String value2 = MessageFormat.format(patterr2, params2);
            return value2 + value;
        }

        public String revocationPackageSplit(String parm) {
            String pattern = "订单【{0}】撤销拆分包裹成功";
            Object[] params = new Object[]{parm};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String revocationPackageMerge(String parm) {
            String pattern = "订单【{0}】撤销合并包裹成功";
            Object[] params = new Object[]{parm};
            String value = MessageFormat.format(pattern, params);
            return value;
        }


        /**
         * 合并的订单操作日志  wujiachuang
         * @param parm 合并后的包裹号
         * @param parm1 合并的订单号
         * @return
         */
        public String packageMerge(String parm,String... parm1) {
            String pattern = "";
            for (int i = 0; i < parm1.length; i++) {
                pattern += "和订单【{" + i + "}】";
            }
            pattern = pattern.substring(1, pattern.length());
            String value = MessageFormat.format(pattern, parm1);
            String pattern2 = "合并包裹成功，合并后的包裹号为【{0}】";
            Object[] params2 = new Object[]{parm};
            String value2 = MessageFormat.format(pattern2, params2);
            return value+value2;
        }


        public String split_1(String parm1) {
            String pattern = "订单【{0}】拆分成功";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }
        public String split_2(String parm1, String... param) {
            String pattern = "订单【{0}】拆分成功，拆分子订单号为";
            for(int i=0;i<param.length;i++){
                pattern += "【{"+i+"}】，";
            }
            Object[] params = new Object[]{parm1, param};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String mergeSuccess(String param) {
            String pattern = "合并成功，合并生成的主订单号【{0}】";
            Object[] params = new Object[]{param};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String pickingCargo(String param1, String param2) {
            String pattern = "订单【{0}】包裹【{1}】配货中";
            Object[] params = new Object[]{param1, param2};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String shipped(String parm1, String parm2, String parm3) {
            String pattern = "订单【{0}】, 包裹【{2}】，已发货，跟踪号【{1}】";
            Object[] params = new Object[]{parm1, parm2, parm3};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String returnSaleAndMoney_Apply(String parm1, String parm2) {
            String pattern = "订单【{0}】申请退货退款，售后单号【{1}】";
            Object[] params = new Object[]{parm1, parm2};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String returnMoney_Apply(String parm1, String parm2) {
            String pattern = "订单【{0}】申请仅退款，售后单号【{1}】";
            Object[] params = new Object[]{parm1, parm2};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String reshipped_Apply(String parm1, String parm2) {
            String pattern = "订单【{0}】申请补发货，售后单号【{1}】";
            Object[] params = new Object[]{parm1, parm2};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String refunded(String parm1, String parm2) {
            String pattern = "订单【{0}】已退款，售后单号【{1}】";
            Object[] params = new Object[]{parm1, parm2};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String reshipped_Finish(String parm1, String parm2) {
            String pattern = "订单【{0}】补发货完成，售后单号【{1}】";
            Object[] params = new Object[]{parm1, parm2};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String returnSaleAndMoney_Finish(String parm1, String parm2) {
            String pattern = "订单【{0}】退货退款完成，售后单号【{1}】";
            Object[] params = new Object[]{parm1, parm2};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String received(String parm1) {
            String pattern = "订单【{0}】已收货";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String cancel(String parm1) {
            String pattern = "订单【{0}】作废";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String packageIntercept(String parm1) {
            String pattern = "包裹【{0}】已拦截";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }
        public String packageInterceptFailure(String parm1) {
            String pattern = "包裹【{0}】拦截失败";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }
        public String orderIntercept(String parm1) {
            String pattern = "订单【{0}】已拦截";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }
        public String orderInterceptFailure(String parm1) {
            String pattern = "订单【{0}】拦截失败";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }
        public String orderInterceptPartlyFailure(String parm1) {
            String pattern = "订单【{0}】部分拦截失败";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String goodCangList(String parm1,String parm2) {
            String pattern = "获取谷仓订单【{0}】出错，出错原因为【{1}】";
            Object[] params = new Object[]{parm1,parm2};
            String value = MessageFormat.format(pattern, params);
            return value;
        }
        public String cancelInvalid(String parm1) {
            String pattern = "订单【{0}】取消作废";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String completed(String parm1) {
            String pattern = "订单【{0}】已完成";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String skuBindInsert(String parm1,String parm2) {
            String pattern = "平台SKU【{0}】已绑定品连SKU【{1}】";
            Object[] params = new Object[]{parm1,parm2};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String skuBindUpdate(String parm1,String parm2) {
            String pattern = "平台SKU【{0}】改绑至品连SKU【{1}】";
            Object[] params = new Object[]{parm1,parm2};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String skuBindRemove(String parm1) {
            String pattern = "平台SKU【{0}】被移除";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String mappingPublishRuleSuccess() {
            return "自动匹配刊登规则成功";
        }

        public String mappingWarehouseRuleSuccess(String parm1) {
            String pattern = "自动匹配仓库规则【{0}】成功";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String mappingLogisticsRuleSuccess(String parm1) {
            String pattern = "自动匹配物流规则【{0}】成功";
            Object[] params = new Object[]{parm1};
            String value = MessageFormat.format(pattern, params);
            return value;
        }

        public String mappingOrderRuleFailure() {
            return  "自动匹配物流规则失败";
        }

    }

    public enum OrderStatus {
        STATUS_1("订单待付款", "1"),
        STATUS_2("订单缺货", "2"),
        STATUS_3("订单待发货", "3"),
        STATUS_4("订单已拦截", "4"),
        STATUS_5("订单已发货", "5"),
        STATUS_6("订单部分发货", "6"),
        STATUS_7("订单已作废", "7"),
        STATUS_13("订单已完成", "8"),
//        STATUS_8("订单已退款", "8"),
        STATUS_9("订单已退货", "9"),
        STATUS_10("订单已拆分", "10"),
        STATUS_11("订单已合并", "11"),
        STATUS_12("订单补发货", "12"),
        STATUS_14("订单拦截中", "13");

        private String msg;
        private String param;

        OrderStatus(String param, String msg) {
            this.msg = msg;
            this.param = param;
        }

        public Byte getMsg() {
            return Byte.valueOf(msg);
        }
    }

    public enum Operator {
        SYSTEM("系统");
        private String msg;

        Operator(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

    }
}
