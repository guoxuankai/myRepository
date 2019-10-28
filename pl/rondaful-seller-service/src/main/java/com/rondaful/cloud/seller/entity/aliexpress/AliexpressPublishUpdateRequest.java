package com.rondaful.cloud.seller.entity.aliexpress;

import com.google.common.collect.Lists;
import com.rondaful.cloud.seller.entity.AliexpressProductCountryPrice;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingAttribute;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingProduct;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * 接受刊登接交的参数
 * @author chenhan
 * Publish
 *
 */
@ApiModel(description = "接受刊登修改的参数AliexpressPublishUpdateRequest")
public class AliexpressPublishUpdateRequest implements java.io.Serializable {

	@ApiModelProperty(value = "主键刊登id")
	private Long id;

	@ApiModelProperty(value = "类型 1价格2库存")
	private Integer type;

	@ApiModelProperty(value = "刊登商品AliexpressPublishListingProduct")
	private List<AliexpressPublishListingProduct> listProduct = Lists.newArrayList();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public List<AliexpressPublishListingProduct> getListProduct() {
		return listProduct;
	}

	public void setListProduct(List<AliexpressPublishListingProduct> listProduct) {
		this.listProduct = listProduct;
	}
}
