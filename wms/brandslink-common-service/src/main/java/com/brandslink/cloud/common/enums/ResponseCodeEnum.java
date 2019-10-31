package com.brandslink.cloud.common.enums;

public enum ResponseCodeEnum implements ResponseCodeEnumSupper {

    /**
     * 定义返回码
     */

    RETURN_CODE_100001("100001", "用户名或密码错误"),
    RETURN_CODE_100002("100002", "原始密码错误"),
    RETURN_CODE_100003("100003", "账号已经存在，请重新输入账号"),
    RETURN_CODE_100004("100004", "角色名称已经存在，请重新输入角色"),
    RETURN_CODE_100005("100005", "请先删除子组织后再删除父组织"),
    RETURN_CODE_100006("100006", "该组织已经绑定账号，需先解绑后再删除"),
    RETURN_CODE_100007("100007", "该角色有绑定账户权限，请先更换账户角色后再删除"),
    RETURN_CODE_100008("100008", "该名称已存在，请重新输入"),
    RETURN_CODE_100010("100010", "职位名称不能重复，请重新输入"),
    RETURN_CODE_100011("100011", "姓名已经存在，请重新输入姓名"),
    RETURN_CODE_100012("100012", "角色名称不能包含'-'符号，请重新输入角色名称"),
    RETURN_CODE_100013("100013", "admin账号仅限登录后修改密码，不支持重置密码!"),
    RETURN_CODE_100014("100014", "新密码不能与原始密码相同，请重新输入"),
    RETURN_CODE_100400("100400", "请求参数错误"),
    RETURN_CODE_100401("100401", "权限不足"),
    RETURN_CODE_100402("100402", "数据库操作异常"),
    RETURN_CODE_100403("100403", "参数不允许为空"),
    RETURN_CODE_100404("100404", "请求资源不存在"),
    RETURN_CODE_100406("100406", "未登录"),
    RETURN_CODE_100407("100407", "根据requestUrl查询角色列表出错"),
    RETURN_CODE_100408("100408", "监听队列，接收仓库名称修改信息异常"),
    RETURN_CODE_100700("100700", "获取HttpServletRequest失败，请稍后重试"),
    RETURN_CODE_100701("100701", "请求未携带客户AppId，请携带客户AppId后重试"),
    RETURN_CODE_100702("100702", "请求未携带签名，请携带签名后重试"),
    RETURN_CODE_100703("100703", "参数 客户AppId 有误，请修改后重试"),
    RETURN_CODE_100704("100704", "验证签名不通过，请确认签名后重试"),
    RETURN_CODE_100705("100705", "暂不支持此种请求方式"),
    RETURN_CODE_100706("100706", "客户状态为非正常状态，请联系客服"),
    RETURN_CODE_100707("100707", "秘钥不允许为空，请携带秘钥重试"),
    RETURN_CODE_100708("100708", "客户编码已存在，请重新输入"),
    RETURN_CODE_100709("100709", "货主编码已存在，请重新输入"),
    RETURN_CODE_100710("100710", "货主名称已存在，请重新输入"),
    RETURN_CODE_100711("100711", "客户名称已存在，请重新输入"),
    RETURN_CODE_100712("100712", "公告内容超长，请重新输入"),
    RETURN_CODE_100713("100713", "不支持对应平台登录"),
    RETURN_CODE_100714("100714", "平台登录类型不能为空"),
    RETURN_CODE_100715("100715", "手机号已存在，请重新输入!"),
    RETURN_CODE_100716("100716", "填写的验证码不正确，请检查！"),
    RETURN_CODE_100717("100717", "当前登录系统无法获取OMS客户信息"),
    RETURN_CODE_100718("100718", "当前登录系统无法获取WMS用户信息"),
    RETURN_CODE_100719("100719", "审核信息资料不完整，无法审核!"),

    RETURN_CODE_100200("100200", "请求成功"),
    RETURN_CODE_100500("100500", "系统异常"),
    RETURN_CODE_100501("100501", "未查询出此运单号的商品信息");

    private String code;
    private String msg;

    private ResponseCodeEnum(String code, String msg) {
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
