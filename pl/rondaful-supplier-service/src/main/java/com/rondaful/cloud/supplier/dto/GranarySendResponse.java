package com.rondaful.cloud.supplier.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 谷仓 入库单推送 result
 *
 * @ClassName GranarySendResponse
 * @Author tianye
 * @Date 2019/4/30 10:31
 * @Version 1.0
 */
public class GranarySendResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "返回状态")
    private String Status;

    @ApiModelProperty(value = "错误信息")
    private String ErrorMessage;

    private GranarySendResponse(String status, String errorMessage) {
        this.Status = status;
        this.ErrorMessage = errorMessage;
    }

    private GranarySendResponse(GranarySendResponseEnum responseEnum) {
        this(responseEnum.getStatus(), responseEnum.getMessage());
    }

    public GranarySendResponse() {
        this(GranarySendResponseEnum.RETURN_SUCCESS);
    }

    public GranarySendResponse setGranarySendResponse(GranarySendResponseEnum responseEnum){
        return new GranarySendResponse(responseEnum);
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public enum GranarySendResponseEnum {

        RETURN_SUCCESS("SUCCESS", ""),
        RETURN_FAILED("FAILED", "同步失败");

        private String status;
        private String message;

        GranarySendResponseEnum(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

    }
}
