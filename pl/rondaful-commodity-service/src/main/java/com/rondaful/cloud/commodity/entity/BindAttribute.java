package com.rondaful.cloud.commodity.entity;

public class BindAttribute {
    private Long id;

    private String erpAttrName;

    private String erpAttrValue;

    private String attrCnName;

    private String attrEnName;

    private String attrCnValue;

    private String attrEnValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getErpAttrName() {
        return erpAttrName;
    }

    public void setErpAttrName(String erpAttrName) {
        this.erpAttrName = erpAttrName == null ? null : erpAttrName.trim();
    }

    public String getErpAttrValue() {
        return erpAttrValue;
    }

    public void setErpAttrValue(String erpAttrValue) {
        this.erpAttrValue = erpAttrValue == null ? null : erpAttrValue.trim();
    }

    public String getAttrCnName() {
        return attrCnName;
    }

    public void setAttrCnName(String attrCnName) {
        this.attrCnName = attrCnName == null ? null : attrCnName.trim();
    }

    public String getAttrEnName() {
        return attrEnName;
    }

    public void setAttrEnName(String attrEnName) {
        this.attrEnName = attrEnName == null ? null : attrEnName.trim();
    }

    public String getAttrCnValue() {
        return attrCnValue;
    }

    public void setAttrCnValue(String attrCnValue) {
        this.attrCnValue = attrCnValue == null ? null : attrCnValue.trim();
    }

    public String getAttrEnValue() {
        return attrEnValue;
    }

    public void setAttrEnValue(String attrEnValue) {
        this.attrEnValue = attrEnValue == null ? null : attrEnValue.trim();
    }
}