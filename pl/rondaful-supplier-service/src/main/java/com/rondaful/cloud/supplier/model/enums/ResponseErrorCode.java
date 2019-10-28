package com.rondaful.cloud.supplier.model.enums;

import com.rondaful.cloud.common.enums.ResponseCodeEnumSupper;

/**
 * @Author: xqq
 * @Date: 2019/8/5
 * @Description:
 */
public enum ResponseErrorCode  implements ResponseCodeEnumSupper {

    GY_RETURN_CODE_200403("GY_200403","参数不能为空"),

    GY_RETURN_CODE_200501("GY_200501","权限不足"),

    GY_RETURN_CODE_200510("GY_200510","没有该仓库的物流方式"),

    GY_RETURN_CODE_200511("GY_200511","该sku不存在"),

    GY_RETURN_CODE_200512("GY_200512","运费查询异常"),

    GY_RETURN_CODE_200513("GY_200513","sku错误"),

    GY_RETURN_CODE_200514("GY_200514","调用第三方服务无数据"),

    GY_RETURN_CODE_200515("GY_200515","调用第三方接口异常"),

    GY_RETURN_CODE_200516("GY_200516","sku批量查询最大支持250个");

    private String code;

    private String msg;



    ResponseErrorCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public void setCode(String code) {
        this.code = code;
    }


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

}
