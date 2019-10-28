package com.rondaful.cloud.seller.entity;


import io.swagger.annotations.ApiModel;

@ApiModel(description = "亚马逊刊登模板生成平台sku时用到自增数据的序列对象")
public class AmazonPublishTemplateOrder {

    /**
     * id
     */
    private Long id;

    /**
     * 队列的key ： amazonTemplateKey:templateId
     */
    private String key;

    /**
     * 队列的值
     */
    private Long value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
