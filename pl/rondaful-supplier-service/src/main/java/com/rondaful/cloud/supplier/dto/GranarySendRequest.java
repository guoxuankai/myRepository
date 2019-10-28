package com.rondaful.cloud.supplier.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 谷仓 入库单推送 request
 *
 * @ClassName GranarySendRequest
 * @Author tianye
 * @Date 2019/4/30 11:18
 * @Version 1.0
 */
public class GranarySendRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "入库单号")
    private String receiving_code;

    @ApiModelProperty(value = "参考号")
    private String reference_no;

    @ApiModelProperty(value = "状态Int,必填. 对应枚举 1：已入库")
    private Integer receiving_status;

    @ApiModelProperty(value = "仓库编码")
    private String warehouse_code;

    @ApiModelProperty(value = "仓库id")
    private Integer warehouse_id;

    @ApiModelProperty(value = "创建时间")
    private Date addTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "入库单类型 0标准 3中转(易渡代发) 4原标 5FBA")
    private Integer receiving_type;

    @ApiModelProperty(value = "入库明细")
    private List<ReceivingDetail> receivingDetail;

    public String getReceiving_code() {
        return receiving_code;
    }

    public void setReceiving_code(String receiving_code) {
        this.receiving_code = receiving_code;
    }

    public String getReference_no() {
        return reference_no;
    }

    public void setReference_no(String reference_no) {
        this.reference_no = reference_no;
    }

    public Integer getReceiving_status() {
        return receiving_status;
    }

    public void setReceiving_status(Integer receiving_status) {
        this.receiving_status = receiving_status;
    }

    public String getWarehouse_code() {
        return warehouse_code;
    }

    public void setWarehouse_code(String warehouse_code) {
        this.warehouse_code = warehouse_code;
    }

    public Integer getWarehouse_id() {
        return warehouse_id;
    }

    public void setWarehouse_id(Integer warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getReceiving_type() {
        return receiving_type;
    }

    public void setReceiving_type(Integer receiving_type) {
        this.receiving_type = receiving_type;
    }

    public List<ReceivingDetail> getReceivingDetail() {
        return receivingDetail;
    }

    public void setReceivingDetail(List<ReceivingDetail> receivingDetail) {
        this.receivingDetail = receivingDetail;
    }

    public static class ReceivingDetail{

        @ApiModelProperty(value = "商品编码")
        private String product_barcode;

        @ApiModelProperty(value = "客户商品编码")
        private String product_sku;

        @ApiModelProperty(value = "箱号编码")
        private String box_no;

        @ApiModelProperty(value = "参考箱号")
        private String reference_box_no;

        @ApiModelProperty(value = "送货数量")
        private Integer deliveryQty;

        @ApiModelProperty(value = "收货数量")
        private Integer receiptQty;

        @ApiModelProperty(value = "上架数量")
        private Integer putawayQty;

        @ApiModelProperty(value = "不良品数量")
        private Integer unsellableQty;

        @ApiModelProperty(value = "良品数量")
        private Integer sellableQty;

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

        public String getBox_no() {
            return box_no;
        }

        public void setBox_no(String box_no) {
            this.box_no = box_no;
        }

        public String getReference_box_no() {
            return reference_box_no;
        }

        public void setReference_box_no(String reference_box_no) {
            this.reference_box_no = reference_box_no;
        }

        public Integer getDeliveryQty() {
            return deliveryQty;
        }

        public void setDeliveryQty(Integer deliveryQty) {
            this.deliveryQty = deliveryQty;
        }

        public Integer getReceiptQty() {
            return receiptQty;
        }

        public void setReceiptQty(Integer receiptQty) {
            this.receiptQty = receiptQty;
        }

        public Integer getPutawayQty() {
            return putawayQty;
        }

        public void setPutawayQty(Integer putawayQty) {
            this.putawayQty = putawayQty;
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
    }

}
