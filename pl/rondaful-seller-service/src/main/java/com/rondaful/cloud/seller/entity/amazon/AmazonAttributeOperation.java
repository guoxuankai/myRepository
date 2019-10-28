package com.rondaful.cloud.seller.entity.amazon;

import com.rondaful.cloud.seller.entity.AmazonTemplateAttribute;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "亚马逊属性操作对象")
public class AmazonAttributeOperation {

    @ApiModelProperty(value = "站点",required = true)
    private String site;

    @ApiModelProperty(value = "父级模板",required = true)
    private String templateParent;

    @ApiModelProperty(value = "二级模板",required = true)
    private String templateChild;

    @ApiModelProperty(value = "需要更新的属性列表")
    private List<AmazonTemplateAttribute> updateAttributes;

    @ApiModelProperty(value = "新添加的属性列表")
    private List<AmazonTemplateAttribute> addAttribute;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getTemplateParent() {
        return templateParent;
    }

    public void setTemplateParent(String templateParent) {
        this.templateParent = templateParent;
    }

    public String getTemplateChild() {
        return templateChild;
    }

    public void setTemplateChild(String templateChild) {
        this.templateChild = templateChild;
    }

    public List<AmazonTemplateAttribute> getUpdateAttributes() {
        return updateAttributes;
    }

    public void setUpdateAttributes(List<AmazonTemplateAttribute> updateAttributes) {
        this.updateAttributes = updateAttributes;
    }

    public List<AmazonTemplateAttribute> getAddAttribute() {
        return addAttribute;
    }

    public void setAddAttribute(List<AmazonTemplateAttribute> addAttribute) {
        this.addAttribute = addAttribute;
    }
}
