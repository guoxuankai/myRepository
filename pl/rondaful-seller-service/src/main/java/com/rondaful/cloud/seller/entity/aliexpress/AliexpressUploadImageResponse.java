package com.rondaful.cloud.seller.entity.aliexpress;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


public class AliexpressUploadImageResponse implements Serializable {

    private static final long serialVersionUID = 6498294886364158729L;

    private Long errorCode;

    private String errorMessage;
    @ApiModelProperty(value = "图片名称")
    private String fileName;

    private Long height;

    private Boolean isSuccess;

    private String photobankTotalSize;
    @ApiModelProperty(value = "上传图片成功的图片")
    private String photobankUrl;

    private String photobankUsedSize;

    private String status;

    private Long width;

    public Long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Long errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getPhotobankTotalSize() {
        return photobankTotalSize;
    }

    public void setPhotobankTotalSize(String photobankTotalSize) {
        this.photobankTotalSize = photobankTotalSize;
    }

    public String getPhotobankUrl() {
        return photobankUrl;
    }

    public void setPhotobankUrl(String photobankUrl) {
        this.photobankUrl = photobankUrl;
    }

    public String getPhotobankUsedSize() {
        return photobankUsedSize;
    }

    public void setPhotobankUsedSize(String photobankUsedSize) {
        this.photobankUsedSize = photobankUsedSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }
}
