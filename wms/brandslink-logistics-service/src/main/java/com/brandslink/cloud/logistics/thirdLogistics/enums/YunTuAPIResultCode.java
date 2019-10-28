package com.brandslink.cloud.logistics.thirdLogistics.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum YunTuAPIResultCode {
    COMMIT_SUCCESS("提交成功", "0000"),
    COMMIT_FAILURE("提交失败", "1001"),
    API_ERROR("接口ApiKEy或ApiSecret错误", "1002"),
    SIGNATURE_ERROR("签名错误", "1003"),
    PARAM_ERROR("参数错误", "1004"),
    TELE_NUM_ERROR("手机号码错误", "1005"),
    NO_DATA_FOUND("未找到数据", "1006"),
    PORTION_SUCCESS("部分成功", "1011"),
    DATA_FORMAT_ERROR("日期格式错误", "2001"),
    BEYOND_LENGTH("长度超过限制", "2002"),
    CUSTOMER_CODE_INEXISTENCE("客户编号不存在", "2003"),
    ORDER_NUM_REPETITION("订单号存在重复", "2004"),
    ORDER_NUM_EXIST("订单号已存在", "2005"),
    METHOD_NO_TRACK_NUM("该运输方式没可用跟踪号", "2006"),
    TRACK_NUM_EXIST("跟踪号已存在", "2007"),
    AAA("未开通关税预付权限，请联系业务", "2008"),
    BBB("重量必须大于零", "2009"),
    CCC("该运输方式不发送到此国家", "2010"),
    DDD("申报信息备注格式不正确", "2011"),
    EEE("收件人电话格式不正确", "2012"),
    FFF("订单号只能由数字和字母组成", "2013"),
    PLATFORM_EXCEPTION("平台异常", "9999"),
    HHH("请求超过次数", "10000"),
    REGISTER_FAILURE("注册失败", "2019"),
    JJJ("用户名不能为空且只能为数字和字母", "2020"),
    KKK("用户密码输入为空", "2021"),
    LLL("用户确认密码输入不一致", "2022"),
    MMM("联系人不能为空", "2023"),
    NNN("联系人手机不能为空", "2024"),
    OOO("客户名称不能为空", "2025"),
    PPP("Email不能为空", "2026"),
    QQQ("地址不能为空", "2027"),
    RRR("平台来源不能为空", "2028"),
    SSS("用户名已存在", "2029"),
    DEAL_SUCCESS("处理成功", "5001"),
    UUU("单号类型[Type]不存在", "5002"),
    VVV("单号不能为空", "5003"),
    WWW("单号不存在", "5004"),
    XXX("订单不存在", "5005"),
    YYY("拦截原因不能为空", "5006"),
    ZZZ("订单不是已提交或已收货状态", "5007"),
    NOT_COMMITTED_ORDER("订单不是已提交状态", "5008"),
    INTERCEPT_FAILURE("拦截失败，请重试", "5009"),
    DELETE_FAILURE("删除失败，请重试", "5010"),
    INTERCEPT_SUCCESS("拦截成功", "5011"),
    DELETE_SUCCESS("删除成功", "5012"),
    DEAL_FAILURE("处理失败，请重试", "5099");

    @Getter
    private String msg;
    @Getter
    private String code;
}
