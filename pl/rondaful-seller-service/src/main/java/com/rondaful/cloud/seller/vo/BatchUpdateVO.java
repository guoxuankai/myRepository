package com.rondaful.cloud.seller.vo;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;

public class BatchUpdateVO {

	
	/*在标题前*/
	@ApiModelProperty(value = "在标题前")
	private String titleBeforeText;
	
	/*在标题后*/
	@ApiModelProperty(value = "在标题后")
	private String titleAfterText;
	
	/*单词替换JSON [{"oldText":"oldText","newText":"newText"},{"oldText":"oldText","newText":"newText"}]*/
	@ApiModelProperty(value = "单词替换JSON [{\"oldText\":\"oldText\",\"newText\":\"newText\"},{\"oldText\":\"oldText\",\"newText\":\"newText\"}]")
	private String replaceJsonStr;
	
	/*原价基础上操作百分比 {"operaTion":"operaTion","val":"val"}*/   
	@ApiModelProperty(value = "原价基础上操作百分比 {\"operaTion\":\"operaTion\",\"val\":\"val\"}")
	private String originalRatioJsonStr;
	
	/*原价基础上操作文本 {"operaTion":"operaTion","val":"val"}*/
	@ApiModelProperty(value = "原价基础上操作文本 {\"operaTion\":\"operaTion\",\"val\":\"val\"}")
	private String originalTextJsonStr;
	
	/*固定价格*/
	@ApiModelProperty(value = "固定价格")
	private String priceValue;
	 
	/*可售数*/
	@ApiModelProperty(value = "可售数")
	private Long quantityNum;
	
	/*listing备注*/
	@ApiModelProperty(value = "listing备注")
	private String listingDesc;

	/*修改id*/
	@ApiModelProperty(value = "修改id")
	@NotBlank(message="id不能为空")
	private String ids;
	
	/** Price.xsd Price BaseCurrencyCodeWithDefault 单位 */
	// USD,GBP,EUR,JPY,CAD,CNY,INR,AUD,BRL,MXN,TRY 
	@ApiModelProperty(value="标准 价格所属货币类型：USD,GBP,EUR,JPY,CAD,CNY,INR,AUD,BRL,MXN,TRY ",
			allowableValues ="USD,GBP,EUR,JPY,CAD,CNY,INR,AUD,BRL,MXN,TRY",required=true)
	@NotBlank(message="必须选择一个货币类型类型")
	private String standardPriceUnit;
	
	/*状态 草稿状态根据plsku修改，在线状态根据平台sku修改*/
	@ApiModelProperty(value = "状态 1: 草稿 3:在线",required=true)
	private Integer status;

	/**
	 * [{"listingId":1212,"skus":"qqqq,wwwww,eeee,rrrr"},{"listingId":1212,"skus":"qqqq,wwwww,eeee,rrrr"},{"listingId":1212,"skus":"qqqq,wwwww,eeee,rrrr"}]
	 */
	@ApiModelProperty(value = "选中需要编辑的sku集合,如果状态是草稿状态就是品连sku，如果状态是在线就是平台sku。格式[{\"listingId\":1212,\"skus\":\"qqqq,wwwww,eeee,rrrr\"},{\"listingId\":1212,\"skus\":\"qqqq,wwwww,eeee,rrrr\"},{\"listingId\":1212,\"skus\":\"qqqq,wwwww,eeee,rrrr\"}]")
	@NotBlank(message="需要修改的数据不能为空")
	private String updateItemsJson;


	
	public String getUpdateItemsJson() {
		return updateItemsJson;
	}


	public void setUpdateItemsJson(String updateItemsJson) {
		this.updateItemsJson = updateItemsJson;
	}


	public Integer getStatus() {
		return status;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}


	public String getStandardPriceUnit() {
		return standardPriceUnit;
	}


	public void setStandardPriceUnit(String standardPriceUnit) {
		this.standardPriceUnit = standardPriceUnit;
	}


	public String getIds() {
		return ids;
	}


	public void setIds(String ids) {
		this.ids = ids;
	}


	public String getTitleBeforeText() {
		return titleBeforeText;
	}


	public void setTitleBeforeText(String titleBeforeText) {
		this.titleBeforeText = titleBeforeText;
	}


	public String getTitleAfterText() {
		return titleAfterText;
	}


	public void setTitleAfterText(String titleAfterText) {
		this.titleAfterText = titleAfterText;
	}


	public String getReplaceJsonStr() {
		return replaceJsonStr;
	}


	public void setReplaceJsonStr(String replaceJsonStr) {
		this.replaceJsonStr = replaceJsonStr;
	}


	public String getOriginalRatioJsonStr() {
		return originalRatioJsonStr;
	}


	public void setOriginalRatioJsonStr(String originalRatioJsonStr) {
		this.originalRatioJsonStr = originalRatioJsonStr;
	}


	public String getOriginalTextJsonStr() {
		return originalTextJsonStr;
	}


	public void setOriginalTextJsonStr(String originalTextJsonStr) {
		this.originalTextJsonStr = originalTextJsonStr;
	}


	public String getPriceValue() {
		return priceValue;
	}


	public void setPriceValue(String priceValue) {
		this.priceValue = priceValue;
	}


	public Long getQuantityNum() {
		return quantityNum;
	}


	public void setQuantityNum(Long quantityNum) {
		this.quantityNum = quantityNum;
	}


	public String getListingDesc() {
		return listingDesc;
	}


	public void setListingDesc(String listingDesc) {
		this.listingDesc = listingDesc;
	}


	 public static class ReplaceJson{
		/*被替换的文本*/
		private String oldText;
		/*新的文本*/
		private String newText;
		public String getOldText() {
			return oldText;
		}
		public void setOldText(String oldText) {
			this.oldText = oldText;
		}
		public String getNewText() {
			return newText;
		}
		public void setNewText(String newText) {
			this.newText = newText;
		}
	}
	
	public static class OriginalJson{
		private String operaTion;
		private String val;
		public String getOperaTion() {
			return operaTion;
		}
		public void setOperaTion(String operaTion) {
			this.operaTion = operaTion;
		}
		public String getVal() {
			return val;
		}
		public void setVal(String val) {
			this.val = val;
		}
		
	}
	

	public enum OperEnum{
		SUBTRACT("SUBTRACT","减"),
		ADD("ADD","加");
		
		private String code;
		
		private String desc;
		
        private OperEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getDes() {
			return desc;
		}

		public void setDes(String desc) {
			this.desc = desc;
		}
		
	}



	
}
