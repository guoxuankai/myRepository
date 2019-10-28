package com.rondaful.cloud.supplier.vo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 
 * 实体类对应的数据表为：  t_warehouse_inventory
 * @author lxx
 * @date 2018-12-04 15:53:16
 */
@ApiModel(value ="GranaryInventoryVO")
public class GranaryInventoryVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @ApiModelProperty(value = "操作单号")
    private String transaction_code;

    @ApiModelProperty(value = "商品编码")
    private String product_barcode;

    @ApiModelProperty(value = "客户商品编码")
    private String product_sku;
    
	@ApiModelProperty(value = "原始数量")
    private Integer originalQty;

	@ApiModelProperty(value = "目标数量")
    private Integer targetQty;
    
    @ApiModelProperty(value = "不良品数量")
    private Integer unsellableQty;
    
    @ApiModelProperty(value = "良品数量")
    private Integer sellableQty;
    
    @ApiModelProperty(value = "备货数量")
    private Integer stockingQty;
    
    @ApiModelProperty(value = "错误信息")
    private Integer type;
    
    @ApiModelProperty(value = "操作时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date transaction_time;
    
    @ApiModelProperty(value = "仓库编码")
    private String warehouse_code;

	public String getTransaction_code() {
		return transaction_code;
	}

	public void setTransaction_code(String transaction_code) {
		this.transaction_code = transaction_code;
	}

	public String getProduct_barcode() {
		return product_barcode;
	}

	public void setProduct_barcode(String product_barcode) {
		this.product_barcode = product_barcode;
	}

	public String getProduct_sku() {
		return product_sku;
	}

	public void setProduct_sku(String product_sku) {
		this.product_sku = product_sku;
	}

	public Integer getOriginalQty() {
		return originalQty;
	}

	public void setOriginalQty(Integer originalQty) {
		this.originalQty = originalQty;
	}

	public Integer getTargetQty() {
		return targetQty;
	}

	public void setTargetQty(Integer targetQty) {
		this.targetQty = targetQty;
	}

	public Integer getUnsellableQty() {
		return unsellableQty;
	}

	public void setUnsellableQty(Integer unsellableQty) {
		this.unsellableQty = unsellableQty;
	}

	public Integer getSellableQty() {
		return sellableQty;
	}

	public void setSellableQty(Integer sellableQty) {
		this.sellableQty = sellableQty;
	}

	public Integer getStockingQty() {
		return stockingQty;
	}

	public void setStockingQty(Integer stockingQty) {
		this.stockingQty = stockingQty;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getTransaction_time() {
		return transaction_time;
	}

	public void setTransaction_time(Date transaction_time) {
		this.transaction_time = transaction_time;
	}

	public String getWarehouse_code() {
		return warehouse_code;
	}

	public void setWarehouse_code(String warehouse_code) {
		this.warehouse_code = warehouse_code;
	}
    
}