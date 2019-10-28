package com.rondaful.cloud.seller.entity;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 速卖通分类属性表
 * 实体类对应的数据表为：  aliexpress_category_attribute
 * @author ch
 * @date 2019-03-20 11:39:22
 */
@ApiModel(value ="AliexpressCategoryAttribute")
public class AliexpressCategoryAttribute implements Serializable {
    private static final long serialVersionUID = 4135339565252057913L;
    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty(value = "属性id")
    private Long attributeId;

    @ApiModelProperty(value = "发布属性展现样式")
    private String attributeShowTypeValue;

    @ApiModelProperty(value = "sku属性是否可自定义名称")
    private Boolean customizedName;

    @ApiModelProperty(value = "sku属性是否可自定义图片")
    private Boolean customizedPic;

    @ApiModelProperty(value = "文本输入框型属性输入格式（文本|数字）")
    private String inputType;

    @ApiModelProperty(value = "发布属性是否关键")
    private Boolean keyAttribute;

    @ApiModelProperty(value = "属性名称")
    private String attributeName;

    @ApiModelProperty(value = "英文属性名称")
    private String attributeNameEn;

    @ApiModelProperty(value = "all属性名称,属性多语言名称")
    private String attributeNameAll;

    @ApiModelProperty(value = "发布属性是否必填")
    private Boolean required;

    @ApiModelProperty(value = "发布属性是否是sku")
    private Boolean sku;

    @ApiModelProperty(value = "sku属性展现样式（色卡|普通）")
    private String skuStyleValue;

    @ApiModelProperty(value = "sku维度（1维~6维）")
    private Integer spec;

    @ApiModelProperty(value = "发布属性单位rate和标准属性对换比率unit_name单位名称")
    private String units;

    @ApiModelProperty(value = "属性是否可见")
    private Boolean visible;

    //属性下拉选择值
    private List<AliexpressCategoryAttributeSelect> attributeSelectList = Lists.newArrayList();


    public List<AliexpressCategoryAttributeSelect> getAttributeSelectList() {
        return attributeSelectList;
    }

    public void setAttributeSelectList(List<AliexpressCategoryAttributeSelect> attributeSelectList) {
        this.attributeSelectList = attributeSelectList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeShowTypeValue() {
        return attributeShowTypeValue;
    }

    public void setAttributeShowTypeValue(String attributeShowTypeValue) {
        this.attributeShowTypeValue = attributeShowTypeValue == null ? null : attributeShowTypeValue.trim();
    }

    public Boolean getCustomizedName() {
        return customizedName;
    }

    public void setCustomizedName(Boolean customizedName) {
        this.customizedName = customizedName;
    }

    public Boolean getCustomizedPic() {
        return customizedPic;
    }

    public void setCustomizedPic(Boolean customizedPic) {
        this.customizedPic = customizedPic;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType == null ? null : inputType.trim();
    }

    public Boolean getKeyAttribute() {
        return keyAttribute;
    }

    public void setKeyAttribute(Boolean keyAttribute) {
        this.keyAttribute = keyAttribute;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName == null ? null : attributeName.trim();
    }

    public String getAttributeNameEn() {
        return attributeNameEn;
    }

    public void setAttributeNameEn(String attributeNameEn) {
        this.attributeNameEn = attributeNameEn == null ? null : attributeNameEn.trim();
    }

    public String getAttributeNameAll() {
        return attributeNameAll;
    }

    public void setAttributeNameAll(String attributeNameAll) {
        this.attributeNameAll = attributeNameAll == null ? null : attributeNameAll.trim();
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getSku() {
        return sku;
    }

    public void setSku(Boolean sku) {
        this.sku = sku;
    }

    public String getSkuStyleValue() {
        return skuStyleValue;
    }

    public void setSkuStyleValue(String skuStyleValue) {
        this.skuStyleValue = skuStyleValue == null ? null : skuStyleValue.trim();
    }

    public Integer getSpec() {
        return spec;
    }

    public void setSpec(Integer spec) {
        this.spec = spec;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units == null ? null : units.trim();
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}