package com.rondaful.cloud.supplier.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 谷仓查询入库单列表API
 *
 * @ClassName WarehouseWarrantResponse
 * @Author tianye
 * @Date 2019/4/28 9:41
 * @Version 1.0
 */
public class GranaryQueryWarehouseWarrantListResponse extends GranaryResponseBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "每页大小")
    private int pageSize;

    @ApiModelProperty(value = "当前页")
    private int page;

    @ApiModelProperty(value = "总页数")
    private int pageCount;

    @ApiModelProperty(value = "总条数")
    private int totalCount;

    @ApiModelProperty(value = "入库单明细")
    private List<WarehouseWarrantListDetail> data;

    public static class WarehouseWarrantListDetail{

        @ApiModelProperty(value = "入库单号")
        private String receivingCode;

        @ApiModelProperty(value = "客户参考号")
        private String referenceNo;

        @ApiModelProperty(value = "0:标准入库单 3-中转入库单 5-FBA入库单")
        private int transitType;

        @ApiModelProperty(value = "服务方式(物流产品代码)")
        private String smCode;

        @ApiModelProperty(value = "入库单状态(0:草稿,1:待审核,2:审核不通过,3:中转仓待签收,4中转仓待收货,5:中转仓待配货,6:中转仓待发货,7:海外仓在途,8:海外仓收货中,9:海外仓收货完成,10:海外仓上架完成,100:废弃)")
        private int receivingStatus;

        @ApiModelProperty(value = "货运方式 0：空运，1：海运散货 2：快递，3铁运 ，4海运整柜")
        private int receivingShippingType;

        @ApiModelProperty(value = "创建日期")
        private String createAt;

        @ApiModelProperty(value = "修改日期")
        private String udpateAt;

        @ApiModelProperty(value = "海外仓仓库编码")
        private String warehouseCode;

        @ApiModelProperty(value = "海外目的仓收货总箱数")
        private int overseasBoxTotal;

        @ApiModelProperty(value = "海外目的仓收货总件数")
        private int overseasSkuTotal;

        @ApiModelProperty(value = "预报箱数")
        private int boxTotalCount;

        @ApiModelProperty(value = "预报sku件数")
        private int skuTotalCount;

        @ApiModelProperty(value = "跟踪号")
        private String trackingNumber;

        @ApiModelProperty(value = "国内中转仓库代码")
        private String transitWarehouseCode;

        public String getReceivingCode() {
            return receivingCode;
        }

        public void setReceivingCode(String receivingCode) {
            this.receivingCode = receivingCode;
        }

        public String getReferenceNo() {
            return referenceNo;
        }

        public void setReferenceNo(String referenceNo) {
            this.referenceNo = referenceNo;
        }

        public int getTransitType() {
            return transitType;
        }

        public void setTransitType(int transitType) {
            this.transitType = transitType;
        }

        public String getSmCode() {
            return smCode;
        }

        public void setSmCode(String smCode) {
            this.smCode = smCode;
        }

        public int getReceivingStatus() {
            return receivingStatus;
        }

        public void setReceivingStatus(int receivingStatus) {
            this.receivingStatus = receivingStatus;
        }

        public int getReceivingShippingType() {
            return receivingShippingType;
        }

        public void setReceivingShippingType(int receivingShippingType) {
            this.receivingShippingType = receivingShippingType;
        }

        public String getCreateAt() {
            return createAt;
        }

        public void setCreateAt(String createAt) {
            this.createAt = createAt;
        }

        public String getUdpateAt() {
            return udpateAt;
        }

        public void setUdpateAt(String udpateAt) {
            this.udpateAt = udpateAt;
        }

        public String getWarehouseCode() {
            return warehouseCode;
        }

        public void setWarehouseCode(String warehouseCode) {
            this.warehouseCode = warehouseCode;
        }

        public int getOverseasBoxTotal() {
            return overseasBoxTotal;
        }

        public void setOverseasBoxTotal(int overseasBoxTotal) {
            this.overseasBoxTotal = overseasBoxTotal;
        }

        public int getOverseasSkuTotal() {
            return overseasSkuTotal;
        }

        public void setOverseasSkuTotal(int overseasSkuTotal) {
            this.overseasSkuTotal = overseasSkuTotal;
        }

        public int getBoxTotalCount() {
            return boxTotalCount;
        }

        public void setBoxTotalCount(int boxTotalCount) {
            this.boxTotalCount = boxTotalCount;
        }

        public int getSkuTotalCount() {
            return skuTotalCount;
        }

        public void setSkuTotalCount(int skuTotalCount) {
            this.skuTotalCount = skuTotalCount;
        }

        public String getTrackingNumber() {
            return trackingNumber;
        }

        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }

        public String getTransitWarehouseCode() {
            return transitWarehouseCode;
        }

        public void setTransitWarehouseCode(String transitWarehouseCode) {
            this.transitWarehouseCode = transitWarehouseCode;
        }
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<WarehouseWarrantListDetail> getData() {
        return data;
    }

    public void setData(List<WarehouseWarrantListDetail> data) {
        this.data = data;
    }
}
