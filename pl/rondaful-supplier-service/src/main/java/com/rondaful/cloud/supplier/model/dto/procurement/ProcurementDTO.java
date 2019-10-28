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
public class ProcurementDTO implements Serializable {

    @ApiModelProperty(value = "")
    private String id;

    @ApiModelProperty(value = "仓库id")
    private Integer warehouseId;

    @ApiModelProperty(value = "供货商id")
    private Integer providerId;

    @ApiModelProperty(value = "采购人")
    private String buyer;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建人主账号id")
    private Integer topUserId;

    private Integer status;

    @ApiModelProperty(value = "采购商品清单")
    private List<ProcurementListDTO> items;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public List<ProcurementListDTO> getItems() {
        return items;
    }

    public void setItems(List<ProcurementListDTO> items) {
        this.items = items;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Integer getTopUserId() {
        return topUserId;
    }

    public void setTopUserId(Integer topUserId) {
        this.topUserId = topUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public static void main(String[] args) {
        List<ProcurementDTO> list=new ArrayList<>();
        ProcurementDTO dto=new ProcurementDTO();
        dto.setWarehouseId(91);
        dto.setTopUserId(12);
        dto.setBuyer("hehe");
        dto.setProviderId(25);

        List<ProcurementListDTO> list1=new ArrayList<>();
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

        list1.add(dto1);
        list1.add(dto2);
        dto.setItems(list1);
        list.add(dto);
        System.out.println(JSONObject.toJSONString(list));

    }
}
