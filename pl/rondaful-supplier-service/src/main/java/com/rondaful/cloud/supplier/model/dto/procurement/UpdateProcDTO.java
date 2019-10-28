package com.rondaful.cloud.supplier.model.dto.procurement;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/21
 * @Description:
 */
public class UpdateProcDTO implements Serializable {
    private static final long serialVersionUID = -7106117321189007940L;

    @ApiModelProperty(value = "采购单id")
    private Long id;

    @ApiModelProperty(value = "采购单项id")
    private Long itemId;

    @ApiModelProperty(value = "采购数量")
    private Integer buyAmount;

    @ApiModelProperty(value = "入库数量")
    private Integer putawayAmount;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "运费")
    private BigDecimal freight;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(Integer buyAmount) {
        this.buyAmount = buyAmount;
    }

    public Integer getPutawayAmount() {
        return putawayAmount;
    }

    public void setPutawayAmount(Integer putawayAmount) {
        this.putawayAmount = putawayAmount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }


    public static void main(String[] args) {
        List<UpdateProcDTO> list=new ArrayList<>();
        UpdateProcDTO dto=new UpdateProcDTO();
        dto.setBuyAmount(33);
        dto.setFreight(new BigDecimal(33));
        dto.setItemId(2354439946504241153L);
        dto.setId(2354439946497949697L);
        dto.setPrice(new BigDecimal("1.0"));
        list.add(dto);
        System.out.println(JSONObject.toJSONString(list));
    }
}
