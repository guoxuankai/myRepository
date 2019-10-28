package com.rondaful.cloud.seller.entity.aliexpress;

import java.io.Serializable;

public class AliexpressAeopAeProductProperty implements Serializable {

    private String attrName;

    private Long attrNameId;

    private String attrValue;

    private String attrValueEnd;

    private Long attrValueId;

    private String attrValueStart;

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public Long getAttrNameId() {
        return attrNameId;
    }

    public void setAttrNameId(Long attrNameId) {
        this.attrNameId = attrNameId;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    public String getAttrValueEnd() {
        return attrValueEnd;
    }

    public void setAttrValueEnd(String attrValueEnd) {
        this.attrValueEnd = attrValueEnd;
    }

    public Long getAttrValueId() {
        return attrValueId;
    }

    public void setAttrValueId(Long attrValueId) {
        this.attrValueId = attrValueId;
    }

    public String getAttrValueStart() {
        return attrValueStart;
    }

    public void setAttrValueStart(String attrValueStart) {
        this.attrValueStart = attrValueStart;
    }
}
