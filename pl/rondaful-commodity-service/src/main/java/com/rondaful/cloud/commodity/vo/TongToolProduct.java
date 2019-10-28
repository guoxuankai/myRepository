package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;
import java.util.List;

/**
* @Description:通途商品对象
* @author:范津 
* @date:2019年9月12日 下午4:54:29
 */
public class TongToolProduct implements Serializable{

	private static final long serialVersionUID = 1L;

	//变参货品列表
	private List<TongToolGoods> goods;
	
	//详细描述列表
	private List<TongToolDetailDescriptions> detailDescriptions;
	
	//商品中文报关名称
	private String declareCnName;
	
	//商品英文报关名称
	private String declareEnName;
	
	//海关编码
	private String hsCode;
	
	//商品图片url列表
	private List<String> imgUrls;
	
	//商户ID
	private String merchantId;
	
	//商品编号PCL(品连spu)
	private String productCode;
	
	//商品名称
	private String productName;
	
	//英文配货名称
	private String productPackingEnName;
	
	//中文配货名称
	private String productPackingName;
	
	//商品状态；停售：0，在售：1，试卖：2，清仓：4
	private String productStatus;
	
	//销售类型；普通销售：0，变参销售：1；
	private String salesType;

	private String skus;
	
	
	public List<TongToolGoods> getGoods() {
		return goods;
	}

	public void setGoods(List<TongToolGoods> goods) {
		this.goods = goods;
	}

	public List<TongToolDetailDescriptions> getDetailDescriptions() {
		return detailDescriptions;
	}

	public void setDetailDescriptions(List<TongToolDetailDescriptions> detailDescriptions) {
		this.detailDescriptions = detailDescriptions;
	}

	public String getDeclareCnName() {
		return declareCnName;
	}

	public void setDeclareCnName(String declareCnName) {
		this.declareCnName = declareCnName;
	}

	public String getDeclareEnName() {
		return declareEnName;
	}

	public void setDeclareEnName(String declareEnName) {
		this.declareEnName = declareEnName;
	}

	public String getHsCode() {
		return hsCode;
	}

	public void setHsCode(String hsCode) {
		this.hsCode = hsCode;
	}

	public List<String> getImgUrls() {
		return imgUrls;
	}

	public void setImgUrls(List<String> imgUrls) {
		this.imgUrls = imgUrls;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductPackingEnName() {
		return productPackingEnName;
	}

	public void setProductPackingEnName(String productPackingEnName) {
		this.productPackingEnName = productPackingEnName;
	}

	public String getProductPackingName() {
		return productPackingName;
	}

	public void setProductPackingName(String productPackingName) {
		this.productPackingName = productPackingName;
	}

	public String getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}

	public String getSalesType() {
		return salesType;
	}

	public void setSalesType(String salesType) {
		this.salesType = salesType;
	}

	public String getSkus() {
		return skus;
	}

	public void setSkus(String skus) {
		this.skus = skus;
	}
	
}
