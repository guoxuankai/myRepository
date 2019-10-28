package com.rondaful.cloud.seller.entity.amazon;

public class AmazonCategory {
    private Integer id;

    private Long categoryId;

    private String name;

    private String zhName;

    private String contextName;

    private Long parentId;

    private String pathId;

    private String path;

    private String attributes;

    private Integer childCount;

    private String childIds;

    private String feedProductType;

    private String site;

    private Integer createTime;

    
    private String enName;

    
    private String zhPath;
    
    private String enPath;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName == null ? null : zhName.trim();
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName == null ? null : contextName.trim();
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId == null ? null : pathId.trim();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path == null ? null : path.trim();
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes == null ? null : attributes.trim();
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public String getChildIds() {
        return childIds;
    }

    public void setChildIds(String childIds) {
        this.childIds = childIds == null ? null : childIds.trim();
    }

    public String getFeedProductType() {
        return feedProductType;
    }

    public void setFeedProductType(String feedProductType) {
        this.feedProductType = feedProductType == null ? null : feedProductType.trim();
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site == null ? null : site.trim();
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getZhPath() {
		return zhPath;
	}

	public void setZhPath(String zhPath) {
		this.zhPath = zhPath;
	}

	public String getEnPath() {
		return enPath;
	}

	public void setEnPath(String enPath) {
		this.enPath = enPath;
	}

	@Override
	public String toString() {
		return "AmazonCategory [id=" + id + ", categoryId=" + categoryId + ", name=" + name + ", zhName=" + zhName
				+ ", contextName=" + contextName + ", parentId=" + parentId + ", pathId=" + pathId + ", path=" + path
				+ ", attributes=" + attributes + ", childCount=" + childCount + ", childIds=" + childIds
				+ ", feedProductType=" + feedProductType + ", site=" + site + ", createTime=" + createTime + "]";
	}
    
}