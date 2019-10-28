package com.rondaful.cloud.commodity.entity;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 商品详情表
 * 实体类对应的数据表为：  t_commodity_details
 * @author zzx
 * @date 2018-12-04 14:04:33
 */
@ApiModel(value ="CommodityDetails")
public class CommodityDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	 
    @ApiModelProperty(value = "唯一id")
    private Long id;

    @ApiModelProperty(value = "关联商品id")
    private Long commodityId;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "产品特性中文，多个以|隔开")
    private String productFeaturesCn;

    @ApiModelProperty(value = "产品特性英文，多个以|隔开")
    private String productFeaturesEn;

    @ApiModelProperty(value = "产品主图，多个以|隔开", required = true)
    private String masterPicture;

    @ApiModelProperty(value = "产品附图，多个以|隔开", required = true)
    private String additionalPicture;
    
    @ApiModelProperty(value = "搜索关键字")
    private String searchKeywords;

    @ApiModelProperty(value = "商品亮点1")
    private String strength1;

    @ApiModelProperty(value = "商品亮点2")
    private String strength2;

    @ApiModelProperty(value = "商品亮点3")
    private String strength3;

    @ApiModelProperty(value = "商品亮点4")
    private String strength4;

    @ApiModelProperty(value = "商品亮点5")
    private String strength5;

    @ApiModelProperty(value = "包装清单")
    private String packingList;

    @ApiModelProperty(value = "商品描述")
    private String commodityDesc;

    @ApiModelProperty(value = "商品资质图片，多个以|隔开，最多5个")
    private String provePicture;

    @ApiModelProperty(value = "注意事项")
    private String attentions;
   

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getProductFeaturesEn() {
        return productFeaturesEn;
    }

    public void setProductFeaturesEn(String productFeaturesEn) {
        this.productFeaturesEn = productFeaturesEn == null ? null : productFeaturesEn.trim();
    }

    public String getMasterPicture() {
        return masterPicture;
    }

    public void setMasterPicture(String masterPicture) {
        this.masterPicture = masterPicture == null ? null : masterPicture.trim();
    }

    public String getAdditionalPicture() {
        return additionalPicture;
    }

    public void setAdditionalPicture(String additionalPicture) {
        this.additionalPicture = additionalPicture == null ? null : additionalPicture.trim();
    }

	public String getSearchKeywords() {
		return searchKeywords;
	}

	public void setSearchKeywords(String searchKeywords) {
		this.searchKeywords = searchKeywords;
	}

	public String getStrength1() {
		return strength1;
	}

	public void setStrength1(String strength1) {
		this.strength1 = strength1;
	}

	public String getStrength2() {
		return strength2;
	}

	public void setStrength2(String strength2) {
		this.strength2 = strength2;
	}

	public String getStrength3() {
		return strength3;
	}

	public void setStrength3(String strength3) {
		this.strength3 = strength3;
	}

	public String getStrength4() {
		return strength4;
	}

	public void setStrength4(String strength4) {
		this.strength4 = strength4;
	}

	public String getStrength5() {
		return strength5;
	}

	public void setStrength5(String strength5) {
		this.strength5 = strength5;
	}

	public String getPackingList() {
		return packingList;
	}

	public void setPackingList(String packingList) {
		this.packingList = packingList;
	}

	public String getCommodityDesc() {
		return commodityDesc;
	}

	public void setCommodityDesc(String commodityDesc) {
		this.commodityDesc = commodityDesc;
	}

	public String getProvePicture() {
		return provePicture;
	}

	public void setProvePicture(String provePicture) {
		this.provePicture = provePicture;
	}

	public String getProductFeaturesCn() {
		return productFeaturesCn;
	}

	public void setProductFeaturesCn(String productFeaturesCn) {
		this.productFeaturesCn = productFeaturesCn;
	}

	public String getAttentions() {
		return attentions;
	}

	public void setAttentions(String attentions) {
		this.attentions = attentions;
	}
    
}