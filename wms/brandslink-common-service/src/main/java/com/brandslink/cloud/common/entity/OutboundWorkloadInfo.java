package com.brandslink.cloud.common.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 出库工作量信息
 */
public class OutboundWorkloadInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "仓库Code")
	private String warehouseCode;

	@ApiModelProperty(value = "客户")
	private String customer;


	@ApiModelProperty(value = "操作人")
	private String workingPeople;

	@ApiModelProperty(value = "订单类型")
	private String orderType;

	@ApiModelProperty(value = "操作类型(码表 1-拣货 2-播种 3-上打包 4-集包 5-发货)")
	private Integer operationType;

	@ApiModelProperty(value = "包裹类型")
	private String packageType;

	@ApiModelProperty(value = "包裹号")
	private String packageNumber;

	@ApiModelProperty(value = "拣货商品个数")
	private Integer pickingCommodityNum;

	@ApiModelProperty(value = "拣货次数")
	private Integer pickingTimeNum;

	@ApiModelProperty(value = "播种包裹数")
	private Integer sowingPackageNum;

	@ApiModelProperty(value = "播种商品个数")
	private Integer sowingCommodityNum;

	@ApiModelProperty(value = "播种单数")
	private Integer sowingOrderNum;

	@ApiModelProperty(value = "打包包裹数")
	private Integer packingPackageNum;

	@ApiModelProperty(value = "打包商品个数")
	private Integer packingCommodityNum;

	@ApiModelProperty(value = "集包袋数")
	private Integer packageBagNum;

	@ApiModelProperty(value = "集包包裹数")
	private Integer packageNum;

	@ApiModelProperty(value = "集包总重量")
	private String packageTotalWeight;

	@ApiModelProperty(value = "集包解散数")
	private Integer packageDissolutionNum;

	@ApiModelProperty(value = "发货袋数")
	private Integer deliveryBagNum;

	@ApiModelProperty(value = "发货包裹数")
	private Integer deliveryPackageNum;

	@ApiModelProperty(value = "发货次数")
	private Integer deliveryTimeNum;

	@ApiModelProperty(value = "包裹总重量")
	private String packageWeightSum;

	@ApiModelProperty(value = "SKU")
	private String productSku;

	public OutboundWorkloadInfo() {
	}

	/**
	 * 播种
	 * 
	 * @param warehouseCode
	 * @param workingPeople
	 * @param operationType
	 * @param sowingPackageNum
	 * @param sowingCommodityNum
	 * @param customer
	 */
	public OutboundWorkloadInfo(String packageNumber,String warehouseCode, String workingPeople,String orderType,String packageType, Integer operationType, Integer sowingPackageNum, Integer sowingCommodityNum,Integer sowingOrderNum, String customer) {
		this.warehouseCode = warehouseCode;
		this.workingPeople = workingPeople;
		this.operationType = operationType;
		this.sowingPackageNum = sowingPackageNum;
		this.sowingCommodityNum = sowingCommodityNum;
		this.customer = customer;
		this.packageNumber = packageNumber;
		this.orderType = orderType;
		this.packageType = packageType;
		this.sowingOrderNum = sowingOrderNum;
	}

	/**
	 *发货
	 * @param warehouseCode
	 * @param workingPeople
	 * @param operationType
	 * @param deliveryBagNum
	 * @param deliveryPackageNum
	 * @param deliveryTimeNum
	 * @param packageWeightSum
	 * @param customer
	 */
	public OutboundWorkloadInfo(String packageNumber,String warehouseCode, String workingPeople, String orderType, Integer operationType, String packageType, Integer deliveryBagNum, Integer deliveryPackageNum, Integer deliveryTimeNum, String packageWeightSum, String customer) {
		this.warehouseCode = warehouseCode;
		this.workingPeople = workingPeople;
		this.orderType = orderType;
		this.operationType = operationType;
		this.packageType = packageType;
		this.deliveryBagNum = deliveryBagNum;
		this.deliveryPackageNum = deliveryPackageNum;
		this.deliveryTimeNum = deliveryTimeNum;
		this.packageWeightSum = packageWeightSum;
		this.customer = customer;
		this.packageNumber = packageNumber;
	}

	/**
	 * 集包
	 * 
	 * @param warehouseCode
	 * @param workingPeople
	 * @param operationType
	 * @param packageBagNum
	 * @param packageNum
	 * @param packageTotalWeight
	 * @param customer
	 */
	public OutboundWorkloadInfo(String packageNumber,String warehouseCode, String workingPeople, String orderType, Integer operationType, String packageType, Integer packageBagNum, Integer packageNum, String packageTotalWeight, String customer) {
		this.warehouseCode = warehouseCode;
		this.workingPeople = workingPeople;
		this.orderType = orderType;
		this.operationType = operationType;
		this.packageType = packageType;
		this.packageBagNum = packageBagNum;
		this.packageNum = packageNum;
		this.packageTotalWeight = packageTotalWeight;
		this.customer = customer;
		this.packageNumber = packageNumber;
	}

	/**
	 * 打包
	 * 
	 * @param warehouseCode
	 * @param workingPeople
	 * @param orderType
	 * @param operationType
	 * @param packageType
	 * @param packingPackageNum
	 * @param packingCommodityNum
	 * @param customer
	 */

	public OutboundWorkloadInfo(String packageNumber,String warehouseCode, String workingPeople, String orderType, Integer operationType, String packageType, Integer packingPackageNum, Integer packingCommodityNum, String customer) {
		this.warehouseCode = warehouseCode;
		this.workingPeople = workingPeople;
		this.orderType = orderType;
		this.operationType = operationType;
		this.packageType = packageType;
		this.packingPackageNum = packingPackageNum;
		this.packingCommodityNum = packingCommodityNum;
		this.customer = customer;
		this.packageNumber = packageNumber;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public String getproductSku() {
		return productSku;
	}

	public void setproductSku(String productSku) {
		this.productSku = productSku;
	}

	public String getWorkingPeople() {
		return workingPeople;
	}

	public void setWorkingPeople(String workingPeople) {
		this.workingPeople = workingPeople;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public Integer getOperationType() {
		return operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public Integer getPickingCommodityNum() {
		return pickingCommodityNum;
	}

	public void setPickingCommodityNum(Integer pickingCommodityNum) {
		this.pickingCommodityNum = pickingCommodityNum;
	}

	public Integer getPickingTimeNum() {
		return pickingTimeNum;
	}

	public void setPickingTimeNum(Integer pickingTimeNum) {
		this.pickingTimeNum = pickingTimeNum;
	}

	public Integer getSowingPackageNum() {
		return sowingPackageNum;
	}

	public void setSowingPackageNum(Integer sowingPackageNum) {
		this.sowingPackageNum = sowingPackageNum;
	}

	public Integer getSowingCommodityNum() {
		return sowingCommodityNum;
	}

	public void setSowingCommodityNum(Integer sowingCommodityNum) {
		this.sowingCommodityNum = sowingCommodityNum;
	}

	public Integer getSowingOrderNum() {
		return sowingOrderNum;
	}

	public void setSowingOrderNum(Integer sowingOrderNum) {
		this.sowingOrderNum = sowingOrderNum;
	}

	public Integer getPackingPackageNum() {
		return packingPackageNum;
	}

	public void setPackingPackageNum(Integer packingPackageNum) {
		this.packingPackageNum = packingPackageNum;
	}

	public Integer getPackingCommodityNum() {
		return packingCommodityNum;
	}

	public void setPackingCommodityNum(Integer packingCommodityNum) {
		this.packingCommodityNum = packingCommodityNum;
	}

	public Integer getPackageBagNum() {
		return packageBagNum;
	}

	public void setPackageBagNum(Integer packageBagNum) {
		this.packageBagNum = packageBagNum;
	}

	public Integer getPackageDissolutionNum() {
		return packageDissolutionNum;
	}

	public void setPackageDissolutionNum(Integer packageDissolutionNum) {
		this.packageDissolutionNum = packageDissolutionNum;
	}

	public Integer getDeliveryBagNum() {
		return deliveryBagNum;
	}

	public void setDeliveryBagNum(Integer deliveryBagNum) {
		this.deliveryBagNum = deliveryBagNum;
	}

	public Integer getDeliveryPackageNum() {
		return deliveryPackageNum;
	}

	public void setDeliveryPackageNum(Integer deliveryPackageNum) {
		this.deliveryPackageNum = deliveryPackageNum;
	}

	public Integer getDeliveryTimeNum() {
		return deliveryTimeNum;
	}

	public void setDeliveryTimeNum(Integer deliveryTimeNum) {
		this.deliveryTimeNum = deliveryTimeNum;
	}

	public String getPackageTotalWeight() {
		return packageTotalWeight;
	}

	public void setPackageTotalWeight(String packageTotalWeight) {
		this.packageTotalWeight = packageTotalWeight;
	}

	public String getPackageWeightSum() {
		return packageWeightSum;
	}

	public void setPackageWeightSum(String packageWeightSum) {
		this.packageWeightSum = packageWeightSum;
	}

	public String getPackageNumber() {
		return packageNumber;
	}

	public void setPackageNumber(String packageNumber) {
		this.packageNumber = packageNumber;
	}

	public Integer getPackageNum() {
		return packageNum;
	}

	public void setPackageNum(Integer packageNum) {
		this.packageNum = packageNum;
	}

	public String getProductSku() {
		return productSku;
	}

	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}
}
