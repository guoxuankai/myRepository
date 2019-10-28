package com.rondaful.cloud.order.enums;

import com.rondaful.cloud.common.enums.ResponseCodeEnumSupper;

/**
 * 订单对外接口编码
 *
 * @author Blade
 * @date 2019-08-08 11:33:03
 **/
public enum OrderCodeEnum implements ResponseCodeEnumSupper {

    /**
     * 定义返回码
     */

    RETURN_CODE_300100("DD_300100", "来源订单号不能为空"),
    RETURN_CODE_300101("DD_300101", "来源订单号不是以QT开头"),
    RETURN_CODE_300102("DD_300101", "该来源订单号已经存在"),
    RETURN_CODE_300103("DD_300103", "店铺ID不能为空或小于等于0"),
    RETURN_CODE_300104("DD_300104", "买家姓名不能为空"),
    RETURN_CODE_300105("DD_300105", "收货人姓名不能为空"),
    RETURN_CODE_300106("DD_300106", "收货目的地/国家代码不能为空"),
    RETURN_CODE_300107("DD_300107", "收货省/州名不能为空"),
    RETURN_CODE_300108("DD_300108", "收货城市不能为空"),
    RETURN_CODE_300109("DD_300109", "收货地址1不能为空"),
    RETURN_CODE_300110("DD_300110", "收货邮编不能为空"),
    RETURN_CODE_300111("DD_300111", "收货人电话不能为空"),
    RETURN_CODE_300112("DD_300112", "指定发货方式不能为空"),
    RETURN_CODE_300113("DD_300113", "发货仓库code或者物流方式code不能为空"),
    RETURN_CODE_300114("DD_300114", "订单项集合不能为空"),
    RETURN_CODE_300115("DD_300115", "品连SKU不能为空"),
    RETURN_CODE_300116("DD_300116", "品连SKU不能为空且不能为0"),
    RETURN_CODE_300117("DD_300117", "卖家店铺账户错误"),
    RETURN_CODE_300118("DD_300118", "国家编码不存在"),
    RETURN_CODE_300119("DD_300119", "含有非品连商品，查询不到数据"),
    RETURN_CODE_300120("DD_300120", "含有不可售商品"),
    RETURN_CODE_300121("DD_300121", "含有已下架商品"),
    RETURN_CODE_300122("DD_300122", "系统订单号不能为空"),
    RETURN_CODE_300123("DD_300123", "查询不到订单数据"),
    RETURN_CODE_300124("DD_300124", "无权查看此订单"),
    RETURN_CODE_300125("DD_300125", "该仓库不存在"),
    RETURN_CODE_300126("DD_300126", "该仓库无所售的商品，请重新选择仓库"),
    RETURN_CODE_300127("DD_300127", "该仓库不支持该物流方式"),
    RETURN_CODE_300128("DD_300128", "无权操作此订单"),
    RETURN_CODE_300129("DD_300129", "该订单的订单状态不能执行取消操作"),
    RETURN_CODE_300130("DD_300130", "错误的订单类型"),
    RETURN_CODE_300131("DD_300131", "包裹状态有误"),
    RETURN_CODE_300132("DD_300132", "系统繁忙"),
    RETURN_CODE_300133("DD_300133", "包裹状态非推送失败或待推送，拦截失败"),
    RETURN_CODE_300134("DD_300134", "仓库Id错误"),
    RETURN_CODE_300135("DD_300135", "拦截失败"),
//    RETURN_CODE_300136("DD_300136", "系统调用异常"),
    RETURN_CODE_300137("DD_300137", "该订单已存在，重复创建失败"),
    RETURN_CODE_300138("DD_300138", "部分拦截失败，请联系客服处理")
    ;

    private String code;
    private String msg;

    OrderCodeEnum(String code, String msg) {
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
