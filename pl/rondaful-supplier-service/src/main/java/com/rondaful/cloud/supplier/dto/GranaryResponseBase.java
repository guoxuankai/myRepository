package com.rondaful.cloud.supplier.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 谷仓API返回信息基类
 *
 * @ClassName GranaryResponseBase
 * @Author tianye
 * @Date 2019/4/28 18:39
 * @Version 1.0
 */
public class GranaryResponseBase implements Serializable,GranaryResponseInterface {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "响应标志，Success表示成功，Failure表示失败")
    private String ask;

    @ApiModelProperty(value = "消息提示")
    private String message;

    @ApiModelProperty(value = "错误信息")
    private ErrorInfo error;

    @ApiModelProperty(value = "分页信息")
    private Pagination pagination;

    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Pagination{

        @ApiModelProperty(value = "分页大小")
        private String pageSize;

        @ApiModelProperty(value = "当前页")
        private String page;

        public String getPageSize() {
            return pageSize;
        }

        public void setPageSize(String pageSize) {
            this.pageSize = pageSize;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }
    }

    public static class ErrorInfo{

        @ApiModelProperty(value = "错误信息内")
        private String errMessage;

        @ApiModelProperty(value = "错误码")
        private String errCode;

        public String getErrMessage() {
            return errMessage;
        }

        public void setErrMessage(String errMessage) {
            this.errMessage = errMessage;
        }

        public String getErrCode() {
            return errCode;
        }

        public void setErrCode(String errCode) {
            this.errCode = errCode;
        }
    }
}
