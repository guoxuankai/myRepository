package com.rondaful.cloud.user.enums;
import com.rondaful.cloud.common.enums.ResponseCodeEnumSupper;


public enum ResponseCodeEnum implements ResponseCodeEnumSupper{

    /**
     * 定义返回码
     * */
	
	//用户相关异常
	RETURN_CODE_100406("100406","未登录"),
	RETURN_CODE_100435("100435","请求失败，请重试"),
    RETURN_CODE_100001("100001","用户名或密码错误"),
    RETURN_CODE_100400("100400","请求参数错误"),
    RETURN_CODE_100401("100401","权限不足"),
    RETURN_CODE_100403("100403","参数不允许为空"),
    RETURN_CODE_100407("100407","该账号已经被注册,手机，用户名均不能重复注册，请重新输入"),
    RETURN_CODE_100408("100408","账号状态更改失败"),
    RETURN_CODE_100409("100409","账号删除状态更改失败"),
    RETURN_CODE_100410("100410","用户角色表修改失败"),
    RETURN_CODE_100411("100411","创建账号资料失败"),
    RETURN_CODE_100412("100412","删除用户角色关系表失败"),
    RETURN_CODE_100419("100419","用户列表展示失败"), 
    RETURN_CODE_100422("100422","您申请的供应商资格失败，请检验您提交的信息"),
    RETURN_CODE_100425("100425","您登录的用户不存在或者改用户已被注销，请重试"),
    RETURN_CODE_100426("100426","您当前账户不属于卖家账户，请确认"),
    RETURN_CODE_100430("100430","未登录或登录超时请重新登录"),
    RETURN_CODE_100431("100431","密码修改失败，请重试"),
    RETURN_CODE_100432("100432","获取卖家列表失败，请重试"),
    RETURN_CODE_100433("100433","转入注册接口"),
    RETURN_CODE_100434("100434","个人中心获取失败，请重试！"),
    RETURN_CODE_100436("100436","用户没有角色或者没有任何权限"),
    
    
    //角色相关异常
    RETURN_CODE_100413("100413","角色创建失败"),
    RETURN_CODE_100414("100414","角色权限关系创建失败"),
    RETURN_CODE_100415("100415","查无此角色"), 
    RETURN_CODE_100416("100416","角色状态修改失败"), 
    RETURN_CODE_100417("100417","删除角色失败"), 
    RETURN_CODE_100418("100418","修改角色资料失败"), 
    RETURN_CODE_100420("100420","角色列表展示失败"), 
    
    //操作日志相关的异常
    RETURN_CODE_100421("100421","获取用户操作日志信息成功"), 
    
    //验证码相关异常
    RETURN_CODE_100427("100427","没有生成验证码信息"),
    RETURN_CODE_100428("100428","未填写验证码信息"),
    RETURN_CODE_100429("100429","验证码错误"),
    RETURN_CODE_100423("100423","您的验证码已失效，请重新获取验证码"),
    RETURN_CODE_100424("100424","您的验证码错误，请重新输入"),
    
    RETURN_CODE_100200("100200","请求成功"),
    RETURN_CODE_100500("100500","系统异常");





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
