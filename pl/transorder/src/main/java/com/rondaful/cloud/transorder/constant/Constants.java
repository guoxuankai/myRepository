package com.rondaful.cloud.transorder.constant;

/**
 * 常量
 *
 * @author Blade
 * @date 2019-06-20 15:37:02
 **/
public interface Constants {

    /**
     * 附加运费比例
     */
    String ADDITIONAL_FREIGHT_RATE = "0.02";

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

}
