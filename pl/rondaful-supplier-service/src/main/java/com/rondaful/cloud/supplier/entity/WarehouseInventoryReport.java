package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 
 * 
 * @author lxx
 * @date 2018-12-04 15:53:16
 */
@ApiModel(value ="WarehouseInventoryReport")
public class WarehouseInventoryReport implements Serializable {

    @ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "入库仓库数")
    private Integer entInvWareHouseCount;

    @ApiModelProperty(value = "库存商品总数")
    private Integer invCommidtyTotal;
    
    @ApiModelProperty(value = "预警商品总数")
    private Integer warnInvCommidtyTotal;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getEntInvWareHouseCount() {
		return entInvWareHouseCount;
	}

	public void setEntInvWareHouseCount(Integer entInvWareHouseCount) {
		this.entInvWareHouseCount = entInvWareHouseCount;
	}

	public Integer getInvCommidtyTotal() {
		return invCommidtyTotal;
	}

	public void setInvCommidtyTotal(Integer invCommidtyTotal) {
		this.invCommidtyTotal = invCommidtyTotal;
	}

	public Integer getWarnInvCommidtyTotal() {
		return warnInvCommidtyTotal;
	}

	public void setWarnInvCommidtyTotal(Integer warnInvCommidtyTotal) {
		this.warnInvCommidtyTotal = warnInvCommidtyTotal;
	}
    

}