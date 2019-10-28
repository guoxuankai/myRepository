package com.rondaful.cloud.supplier.dto;

/**
 * 谷仓API返回公共报文头，用于统一校验调用接口是否成功
 *
 * @ClassName GranaryResponseInterface
 * @Author tianye
 * @Date 2019/4/29 17:24
 * @Version 1.0
 */
public interface GranaryResponseInterface {

    GranaryResponseBase.ErrorInfo getError();

    String getMessage();

    String getAsk();
}
