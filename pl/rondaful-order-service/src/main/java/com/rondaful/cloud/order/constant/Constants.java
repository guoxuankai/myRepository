package com.rondaful.cloud.order.constant;

/**
 * 常量
 *
 * @author Blade
 * @date 2019-06-20 15:37:02
 **/
public interface Constants {

    interface isConvertOrder {
        String YES = "yes";
        String NO = "no";
    }

    interface isErrorOrder {
        String YES = "yes";
        String NO = "no";
    }


    interface IsAfterOrder {
        Byte ALL_AFTER_SALE = 1;
        Byte PARTLY_AFTER_SALE = 2;
        Byte NO_AFTER_SALE = 0;
    }

    interface IsAfterSale {
        Integer YES = 1;
        Integer NO = 0;
    }

    interface Intercept {
        String INTERCEPT_SUCCESS = "拦截成功";
        String INTERCEPT_FAIL = "拦截失败";
        String INTERCEPT_PART_FAIL = "部分包裹拦截失败";
    }


    interface InterceptResponse {
        String RESPONSE_1 = "订单异常：包裹拦截成功，订单取消冻结失败！ 请联系客服处理。";
    }

    /**
     * 附加运费比例
     */
    String ADDITIONAL_FREIGHT_RATE = "0.02";

    interface InvoiceTemplate {
        String VAT_TAX_TYPE_1 = "1";
        String VAT_TAX_TYPE_2 = "2";
    }

    interface DistributionType {
        String DistributionType_GC = "GC";
        String DistributionType_QT = "QT";
    }

    interface CommodityBase {
        // 可售
        Integer CAN_SALE = 1;
        // 不可售
        Integer CANT_SALE = -1;
    }

    interface CommodityStatus {
        //待提交
        Integer WAIT_SUBMIT = -1;
        //审核中
        Integer CHECKING = 0;
        //待上架
        Integer WAIT_PUTAWAY = 1;
        //已拒绝
        Integer REFUSED = 2;
        //已上架
        Integer ALREADY_PUTAWAY = 3;
    }

    interface SysOrder {
        // 不包邮
        Integer NOT_FREE_FREIGHT = 0;
        // 包邮
        Integer FREE_FREIGHT = 1;
        // 部分包邮
        Integer PART_FREE_FREIGHT = 2;
        // 异常订单
        String ERROR_ORDER_NO = "no";
        // 正常订单
        String ERROR_ORDER_YES = "yes";
    }

    interface System {
        // 卖家
        Integer PLATFORM_TYPE_SELLER = 1;

        // 供应商
        Integer PLATFORM_TYPE_SUPPLIER = 0;
    }

    interface DefaultUser {
        String SYSTEM = "SYSTEM";
    }

    interface Warehouse {
        // 利朗达
        String RONDAFUL = "RONDAFUL";
        // 谷仓
        String GOODCANG = "GOODCANG";
        // WMS
        String WMS = "WMS";
    }

    interface SplitSymbol {
        // 逗号
        String COMMA = ",";
        // 句号
        String PERIOD = ".";
        // 星号
        String ASTERISK = "*";
        // 斜杆
        String SLASH = "/";
        // 井号
        String HASH_TAG = "#";
        // 双引号
        String DOUBLE_QUOTATION_MARKS = "\"";
    }

    interface RedisPrefix {
        String DELIVERY_PACKAGE = "DELIVERY_PACKAGE:";
    }

    interface Commodity {
        /**
         * 服务费类型--固定
         */
        String FARE_TYPE_FIXED = "1";

        /**
         * 服务费类型--百分比
         */
        String FATE_TYPE_PERCENT = "2";
    }

    interface WarehouseType {
        String ERP = "ERP";

        String GC = "GC";

        String WMS = "WMS";
    }

    /**
     * WMS 系统的一些常量
     */
    interface WmsSystem {
        /**
         * WMS 创建订单接口路径
         */
        String CREATE_ORDER_URL = "/outbound/order/create";

        /**
         * WMS 取消订单接口路径
         */
        String CANCEL_ORDER_URL = "/outbound/order/cancelOrder";

        /**
         * WMS 查询包裹状态接口路径
         */
        String FIND_PACKAGE_STATE = "/outbound/order/findPackageStatus";
    }

    interface CanBeDelivered {
        // 可以发货
        Integer CAN_BE_DELIVERRED = 0;
        // 平台已经标记发货
        Integer HAS_MARK = 1;
        // 仓库已禁用
        Integer WAREHOUSE_DISABLED = 2;
        // 物流已禁用
        Integer LOGISTICS_DISABLED = 3;
        // 是否缴纳押金
        Integer ERROR_ORDER_YES = 4;
    }

    interface WareHousAbleb {
        // 仓库启用
        Integer ABLED = 1;
        // 仓库禁用
        Integer DISABLED = 4;
    }

    interface LogisticsAbleb {
        // 物流方式启用
        Integer ABLED = 1;
        // 物流方式禁用
        Integer DISABLED = 0;
    }

    interface SysConfig {
        // 测试环境回标开关键
        String OPEN_TEST_BACK_MARK = "open_test_back_mark";

        // 测试环境回标开启
        String OPEN_TEST_BACK_MARK_YES = "yes";

        // 测试环境回标开启
        String OPEN_TEST_BACK_MARK_NO = "no";

        String SYS_PROD = "prod";

        String SYS_TEST = "test";
    }

    interface OnlineLogistics  {
        String EDIS = "edis";
        String ALIEXPRESS = "aliexpress";
    }

    interface EbayEdis  {
        //不启用
        Integer DISABLED = 0;

        //erp
        Integer AUTORIZATION_ERP = 1;

        //绑定品连edis
        Integer BIND_PL = 2;

        //我有自己的edis账号并且授权给品连
        Integer AUTORIZATION_PL = 3;
    }
}
