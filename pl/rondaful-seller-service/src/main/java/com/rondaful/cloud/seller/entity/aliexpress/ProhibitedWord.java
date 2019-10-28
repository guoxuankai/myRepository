package com.rondaful.cloud.seller.entity.aliexpress;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class ProhibitedWord {
    @ApiModelProperty(value = "违禁内容")
    private String primaryWord;
    @ApiModelProperty(value = "违禁类型")
    private List<String> types;

    public String getPrimaryWord() {
        return primaryWord;
    }

    public void setPrimaryWord(String primaryWord) {
        this.primaryWord = primaryWord;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
