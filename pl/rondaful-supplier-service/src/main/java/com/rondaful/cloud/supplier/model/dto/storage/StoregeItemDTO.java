package com.rondaful.cloud.supplier.model.dto.storage;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/18
 * @Description:
 */
public class StoregeItemDTO implements Serializable {
    private static final long serialVersionUID = 7359323522912417917L;

    @ApiModelProperty(value = "入库单id")
    private Long storageId;

    @ApiModelProperty(value = "箱号")
    private Integer boxNo;

    @ApiModelProperty(value = "箱子参考号")
    private String referenceBoxNo;

    @ApiModelProperty(value = "SKU")
    private String productSku;

    @ApiModelProperty(value = "数量")
    private Integer quantity;

    @ApiModelProperty(value = "FBA商品编码。必填条件：transit_type=5（FBA入库单）")
    private String fbaProductCode;

    @ApiModelProperty(value = "商品名称")
    private String commodityName;

    @ApiModelProperty(value = "商品英文名称")
    private String commodityNameEn;

    @ApiModelProperty(value = "中转预报数量")
    private Integer transitPreCount;

    @ApiModelProperty(value = "中转收货数量")
    private Integer transitReceivingCount;

    @ApiModelProperty(value = "海外仓预报数量")
    private Integer overseasPreCount;

    @ApiModelProperty(value = "海外端收货数量")
    private Integer overseasReceivingCount;

    @ApiModelProperty(value = "海外端上架数量")
    private Integer overseasShelvesCount;

    public Long getStorageId() {
        return storageId;
    }

    public void setStorageId(Long storageId) {
        this.storageId = storageId;
    }

    public Integer getBoxNo() {
        return boxNo;
    }

    public void setBoxNo(Integer boxNo) {
        this.boxNo = boxNo;
    }

    public String getReferenceBoxNo() {
        return referenceBoxNo;
    }

    public void setReferenceBoxNo(String referenceBoxNo) {
        this.referenceBoxNo = referenceBoxNo;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getFbaProductCode() {
        return fbaProductCode;
    }

    public void setFbaProductCode(String fbaProductCode) {
        this.fbaProductCode = fbaProductCode;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getCommodityNameEn() {
        return commodityNameEn;
    }

    public void setCommodityNameEn(String commodityNameEn) {
        this.commodityNameEn = commodityNameEn;
    }

    public Integer getTransitPreCount() {
        return transitPreCount;
    }

    public void setTransitPreCount(Integer transitPreCount) {
        this.transitPreCount = transitPreCount;
    }

    public Integer getTransitReceivingCount() {
        return transitReceivingCount;
    }

    public void setTransitReceivingCount(Integer transitReceivingCount) {
        this.transitReceivingCount = transitReceivingCount;
    }

    public Integer getOverseasPreCount() {
        return overseasPreCount;
    }

    public void setOverseasPreCount(Integer overseasPreCount) {
        this.overseasPreCount = overseasPreCount;
    }

    public Integer getOverseasReceivingCount() {
        return overseasReceivingCount;
    }

    public void setOverseasReceivingCount(Integer overseasReceivingCount) {
        this.overseasReceivingCount = overseasReceivingCount;
    }

    public Integer getOverseasShelvesCount() {
        return overseasShelvesCount;
    }

    public void setOverseasShelvesCount(Integer overseasShelvesCount) {
        this.overseasShelvesCount = overseasShelvesCount;
    }

    public static void main(String[] args) {
        List<StoregeItemDTO> list=new ArrayList<>();
        StoregeItemDTO dto1=new StoregeItemDTO();
        dto1.setBoxNo(1);
        dto1.setProductSku("C-1-107EEDB1-618861");
        dto1.setQuantity(11);
        StoregeItemDTO dto2=new StoregeItemDTO();
        dto2.setBoxNo(2);
        dto2.setProductSku("B-2-898868F4-287838");
        dto2.setQuantity(22);
        list.add(dto1);
        list.add(dto2);
        System.out.println(JSONObject.toJSONString(list));
    }
}
