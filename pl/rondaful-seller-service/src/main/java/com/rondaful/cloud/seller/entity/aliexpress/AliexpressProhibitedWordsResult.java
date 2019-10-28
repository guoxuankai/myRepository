package com.rondaful.cloud.seller.entity.aliexpress;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 违禁词转换
 */
public class AliexpressProhibitedWordsResult {
    private static final long serialVersionUID = 8866622167198345483L;
    @ApiModelProperty(value = "商品的详细描述的违禁词")
    private List<ProhibitedWord> detailProhibitedWords;

    private Long errorCode;

    private String errorMessage;
    @ApiModelProperty(value = "关键字的违禁词")
    private List<ProhibitedWord> keywordsProhibitedWords;
    @ApiModelProperty(value = "商品类目属性的违禁词")
    private List<ProhibitedWord> productPropertiesProhibitedWords;
    @ApiModelProperty(value = "商品的标题的违禁词")
    private List<ProhibitedWord> titleProhibitedWords;


    public List<ProhibitedWord> getDetailProhibitedWords() {
        return detailProhibitedWords;
    }

    public void setDetailProhibitedWords(List<ProhibitedWord> detailProhibitedWords) {
        this.detailProhibitedWords = detailProhibitedWords;
    }

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

    public List<ProhibitedWord> getKeywordsProhibitedWords() {
        return keywordsProhibitedWords;
    }

    public void setKeywordsProhibitedWords(List<ProhibitedWord> keywordsProhibitedWords) {
        this.keywordsProhibitedWords = keywordsProhibitedWords;
    }

    public List<ProhibitedWord> getProductPropertiesProhibitedWords() {
        return productPropertiesProhibitedWords;
    }

    public void setProductPropertiesProhibitedWords(List<ProhibitedWord> productPropertiesProhibitedWords) {
        this.productPropertiesProhibitedWords = productPropertiesProhibitedWords;
    }

    public List<ProhibitedWord> getTitleProhibitedWords() {
        return titleProhibitedWords;
    }

    public void setTitleProhibitedWords(List<ProhibitedWord> titleProhibitedWords) {
        this.titleProhibitedWords = titleProhibitedWords;
    }
}
