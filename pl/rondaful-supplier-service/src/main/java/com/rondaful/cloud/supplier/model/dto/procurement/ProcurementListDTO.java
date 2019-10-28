package com.rondaful.cloud.supplier.model.dto.procurement;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/20
 * @Description:
 */
public class ProcurementListDTO implements Serializable {
    private static final long serialVersionUID = 4484892850598343899L;

    @ApiModelProperty(value = "订单商品的id")
    private String id;

    @ApiModelProperty(value = "品连sku")
    private String pinlianSku;

    @ApiModelProperty(value = "采购数量")
    private Integer buyAmount;

    @ApiModelProperty(value = "入库数量")
    private Integer putawayAmount;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "运费")
    private BigDecimal freight;

    public String getPinlianSku() {
        return pinlianSku;
    }

    public void setPinlianSku(String pinlianSku) {
        this.pinlianSku = pinlianSku;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static void main(String[] args) {
        List<ProcurementListDTO> list=new ArrayList<>();
        ProcurementListDTO dto1=new ProcurementListDTO();
        dto1.setBuyAmount(2);
        dto1.setPinlianSku("A-2-8454DDFE-931967");
        dto1.setPrice(new BigDecimal("33.2"));
        dto1.setFreight(new BigDecimal("5.69"));

        ProcurementListDTO dto2=new ProcurementListDTO();
        dto2.setBuyAmount(5);
        dto2.setPinlianSku("J-1-6A7C351C-609934");
        dto2.setPrice(new BigDecimal("4.6"));
        dto2.setFreight(new BigDecimal("3.26"));

        list.add(dto1);
        list.add(dto2);

        System.out.println(JSONObject.toJSONString(list));

    }
}
