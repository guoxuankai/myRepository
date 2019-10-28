package com.rondaful.cloud.seller.entity.amazon;

public class AmazonElementRelation {
    private Integer id;

    private Integer parentElementId;

    private Integer elementId;

    private String name;

    private String type;

    private Integer createTime;

    private Integer minOccurs;

    private Integer maxOccurs;

    private Boolean isElement;

    private Integer parentTypeId;

    private String doc;

    private String restriction;

    private Integer typeId;

    private String base;

    private Short sequence;

    private Boolean typeClassId;

    private Short checkId;

    private Boolean loop;

    private Boolean variation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentElementId() {
        return parentElementId;
    }

    public void setParentElementId(Integer parentElementId) {
        this.parentElementId = parentElementId;
    }

    public Integer getElementId() {
        return elementId;
    }

    public void setElementId(Integer elementId) {
        this.elementId = elementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public Integer getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(Integer minOccurs) {
        this.minOccurs = minOccurs;
    }

    public Integer getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(Integer maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public Boolean getIsElement() {
        return isElement;
    }

    public void setIsElement(Boolean isElement) {
        this.isElement = isElement;
    }

    public Integer getParentTypeId() {
        return parentTypeId;
    }

    public void setParentTypeId(Integer parentTypeId) {
        this.parentTypeId = parentTypeId;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc == null ? null : doc.trim();
    }

    public String getRestriction() {
        return restriction;
    }

    public void setRestriction(String restriction) {
        this.restriction = restriction == null ? null : restriction.trim();
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base == null ? null : base.trim();
    }

    public Short getSequence() {
        return sequence;
    }

    public void setSequence(Short sequence) {
        this.sequence = sequence;
    }

    public Boolean getTypeClassId() {
        return typeClassId;
    }

    public void setTypeClassId(Boolean typeClassId) {
        this.typeClassId = typeClassId;
    }

    public Short getCheckId() {
        return checkId;
    }

    public void setCheckId(Short checkId) {
        this.checkId = checkId;
    }

    public Boolean getLoop() {
        return loop;
    }

    public void setLoop(Boolean loop) {
        this.loop = loop;
    }

    public Boolean getVariation() {
        return variation;
    }

    public void setVariation(Boolean variation) {
        this.variation = variation;
    }
}