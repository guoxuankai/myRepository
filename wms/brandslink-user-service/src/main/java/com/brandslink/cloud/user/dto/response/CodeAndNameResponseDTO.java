package com.brandslink.cloud.user.dto.response;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 客户、货主 编码和名称信息
 *
 * @ClassName CodeAndNameResponseDTO
 * @Author tianye
 * @Date 2019/7/19 17:03
 * @Version 1.0
 */
public class CodeAndNameResponseDTO implements Serializable {

    @ApiModelProperty(value = "编码")
    private String dataCode;

    @ApiModelProperty(value = "名称")
    private String dataName;

    public String getDataCode() {
        return dataCode;
    }

    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }
}
