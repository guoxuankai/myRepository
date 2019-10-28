package com.rondaful.cloud.seller.entity.amazon;

import java.util.List;

import com.rondaful.cloud.seller.generated.ProductImage;

public class AmazonImageRequest implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9117342431894804173L; 
	
	private String listingId;
	private Images images;

	private Integer type;//0表示列表在线状态修改图片，1表示刊登失败状态修改图片
	
	private Integer warehouseId;

	 
	public Integer getWarehouseId() {
		return warehouseId;
	}



	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}



	public Integer getType() {
		return type;
	}



	public void setType(Integer type) {
		this.type = type;
	}



	public String getListingId() {
		return listingId;
	}



	public void setListingId(String listingId) {
		this.listingId = listingId;
	}



	public Images getImages() {
		return images;
	}



	public void setImages(Images images) {
		this.images = images;
	}



	public class Images
	{
		List<ProductImage> mainImage;
		List<List<ProductImage>> subImage;
		public List<ProductImage> getMainImage() {
			return mainImage;
		}
		public void setMainImage(List<ProductImage> mainImage) {
			this.mainImage = mainImage;
		}
		public List<List<ProductImage>> getSubImage() {
			return subImage;
		}
		public void setSubImage(List<List<ProductImage>> subImage) {
			this.subImage = subImage;
		}
		
		
	}
}
