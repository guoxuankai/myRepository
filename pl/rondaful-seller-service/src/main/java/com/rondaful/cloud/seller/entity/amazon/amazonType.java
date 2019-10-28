package com.rondaful.cloud.seller.entity.amazon;

public class amazonType {
    private Integer id;

    private String name;

    private Integer parentTypeId;

    private String type;

    private String doc;

    private Boolean attribute;

    private String extension;

    private Boolean defined;

    private Integer extensionId;

    private Boolean sequence;

    private Short typeClassId;

    private Short checkId;

    private String base;

    private String restriction;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getParentTypeId() {
        return parentTypeId;
    }

    public void setParentTypeId(Integer parentTypeId) {
        this.parentTypeId = parentTypeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc == null ? null : doc.trim();
    }

    public Boolean getAttribute() {
        return attribute;
    }

    public void setAttribute(Boolean attribute) {
        this.attribute = attribute;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension == null ? null : extension.trim();
    }

    public Boolean getDefined() {
        return defined;
    }

    public void setDefined(Boolean defined) {
        this.defined = defined;
    }

    public Integer getExtensionId() {
        return extensionId;
    }

    public void setExtensionId(Integer extensionId) {
        this.extensionId = extensionId;
    }

    public Boolean getSequence() {
        return sequence;
    }

    public void setSequence(Boolean sequence) {
        this.sequence = sequence;
    }

    public Short getTypeClassId() {
        return typeClassId;
    }

    public void setTypeClassId(Short typeClassId) {
        this.typeClassId = typeClassId;
    }

    public Short getCheckId() {
        return checkId;
    }

    public void setCheckId(Short checkId) {
        this.checkId = checkId;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base == null ? null : base.trim();
    }

    public String getRestriction() {
        return restriction;
    }

    public void setRestriction(String restriction) {
        this.restriction = restriction == null ? null : restriction.trim();
    }
}