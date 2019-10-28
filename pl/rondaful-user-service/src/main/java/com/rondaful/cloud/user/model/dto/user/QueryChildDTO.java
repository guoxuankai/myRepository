package com.rondaful.cloud.user.model.dto.user;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: xqq
 * @Date: 2019/5/2
 * @Description:
 */
public class QueryChildDTO extends QuerManagePageDTO {
    private static final long serialVersionUID = 2502249643098755346L;

    @ApiModelProperty(value = "查询平台类型",name = "platformType",dataType = "Integer")
    private Integer platformType;
    @ApiModelProperty(value = "查询账户id",name = "topUserId",dataType = "Integer")
    private Integer topUserId;

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }

    public Integer getTopUserId() {
        return topUserId;
    }

    public void setTopUserId(Integer topUserId) {
        this.topUserId = topUserId;
    }
}
