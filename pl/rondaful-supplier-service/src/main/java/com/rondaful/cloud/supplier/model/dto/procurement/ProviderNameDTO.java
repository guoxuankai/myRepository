package com.rondaful.cloud.supplier.model.dto.procurement;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/25
 * @Description:
 */
public class ProviderNameDTO implements Serializable {
    private static final long serialVersionUID = -4069419420518273546L;

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "供货商名称")
    private String providerName;

    @ApiModelProperty(value = "buyer")
    private String buyer;

    @ApiModelProperty(value = "一级分类")
    private String levelOne;

    @ApiModelProperty(value = "二级分类")
    private String levelTwo;

    @ApiModelProperty(value = "三级分类")
    private String levelThree;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getLevelOne() {
        return levelOne;
    }

    public void setLevelOne(String levelOne) {
        this.levelOne = levelOne;
    }

    public String getLevelTwo() {
        return levelTwo;
    }

    public void setLevelTwo(String levelTwo) {
        this.levelTwo = levelTwo;
    }

    public String getLevelThree() {
        return levelThree;
    }

    public void setLevelThree(String levelThree) {
        this.levelThree = levelThree;
    }
}
