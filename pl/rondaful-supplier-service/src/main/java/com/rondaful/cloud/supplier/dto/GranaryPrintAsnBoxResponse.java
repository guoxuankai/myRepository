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
public class GranaryPrintAsnBoxResponse extends GranaryResponseBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "返回的数据")
    private ImageInfo data;

    public ImageInfo getData() {
        return data;
    }

    public void setData(ImageInfo data) {
        this.data = data;
    }

    public static class ImageInfo {

        @ApiModelProperty(value = "返回文件base64")
        private String labelImage;

        @ApiModelProperty(value = "文件类型 1：png，2：pdf")
        private String imageType;

        public String getLabelImage() {
            return labelImage;
        }

        public void setLabelImage(String labelImage) {
            this.labelImage = labelImage;
        }

        public String getImageType() {
            return imageType;
        }

        public void setImageType(String imageType) {
            this.imageType = imageType;
        }
    }
}
