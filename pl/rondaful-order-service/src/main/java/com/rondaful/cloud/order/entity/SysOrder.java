package com.rondaful.cloud.order.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rondaful.cloud.order.entity.cms.OrderAfterVo;
import com.rondaful.cloud.order.entity.system.OrderProfitCalculation;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDetailDTO;
import com.rondaful.cloud.order.model.dto.sysOrderInvoice.SysOrderInvoiceInsertOrUpdateDTO;
import com.rondaful.cloud.order.model.vo.sysOrderInvoice.SysOrderInvoiceVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 系统订单表
 * 实体类对应的数据表为：  tb_sys_order
 * @author ZJL
 * @date 2019-05-05 15:59:46
 */
@Data
@ApiModel(value ="SysOrder")
public class SysOrder extends OrderProfitCalculation implements Serializable {

    @ApiModelProperty(value = "平台订单总价")
    private BigDecimal platformTotalPrice;


    @ApiModelProperty(value = "内部调运费试算接口使用")
    List<SysOrderPackageDetailDTO> skuList;

    @ApiModelProperty(value = "物流类型(策略)：1.cheapest 2.integrated_optimal 3.fastest")
    private String logisticsStrategy;

    @ApiModelProperty(value = "仓库ID")
    private String deliveryWarehouseId;

    @ApiModelProperty(value = "供应商物流费")
    private BigDecimal actualShipCostBySupplier;

    @ApiModelProperty(value = "接受发票对象")
    private SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO;

    @ApiModelProperty(value = "发票信息")
    private SysOrderInvoiceVO sysOrderInvoiceVO;

    @ApiModelProperty(value = "手工标记订单异常信息")
    private String markException;

//    @ApiModelProperty(value = "售后订单详情")
//    private OrderAfterSalesOrderDetailsModel orderAfterSalesOrderDetailsModel;

    @ApiModelProperty(value = "售后订单详情")
    private List<OrderAfterVo> orderAfterVoList;

    @ApiModelProperty(value = "是否缺货")
    private Boolean isOOS;

    @ApiModelProperty(value = "是否手工单：1是，0否")
    private Integer handOrder = 1;

    @ApiModelProperty(value = "平台发货状态")
    private String platformOrderStatus;

    @ApiModelProperty(value = "平台订单总价")
    private String platformTotal;

    @ApiModelProperty(value = "卖家填的物流费")
    private String shippingServiceCostStr;

    @ApiModelProperty(value = "产品总价")
    private BigDecimal orderTotal;

    @ApiModelProperty(value = "产品数量")
    private Long itemNum;

    @ApiModelProperty(value = "体积")
    private BigDecimal totalBulk;

    @ApiModelProperty(value = "重量g")
    private BigDecimal totalWeight;

    @ApiModelProperty(value = "产品成本")
    private BigDecimal productCost;

    @ApiModelProperty(value = "平台站点(目前亚马逊时该字段必传字段)")
    private String site;

    @ApiModelProperty(value = "授权id")
    private Integer empowerId;

    @JsonIgnore
    @ApiModelProperty(value = "订单中的转入成功的sku列表", hidden = true)
    private List<String> skus;

    @ApiModelProperty(value = "子订单集合")
    private List<SysOrder> sysChildOrderList;

    @ApiModelProperty(value = "系统订单项实体类")
    private List<SysOrderDetail> sysOrderDetails;

    @ApiModelProperty(value = "顺序号")
    private Integer id;

    @ApiModelProperty(value = "系统订单ID")
    private String sysOrderId;

    @ApiModelProperty(value = "订单跟踪号")
    private String orderTrackId;

    @ApiModelProperty(value = "卖家平台订单销售记录号")
    private String recordNumber;

    @ApiModelProperty(value = "来源订单ID")
    private String sourceOrderId;

    @ApiModelProperty(value = "最迟发货时间(取所有子单最早时间)")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String deliverDeadline;

    @ApiModelProperty(value = "订单转入状态:0待处理,1转入成功,2转入失败,3部分转入成功")
    private Byte converSysStatus;

    @ApiModelProperty(value = "订单来源:1手工创建,2批量导入,3第三方平台API推送,4eBay订单,5Amazon订单,6AliExpress订单,7Wish订单")
    private Byte orderSource;

    @ApiModelProperty(value = "订单发货状态:1待发货,2缺货,3配货中,4已拦截,5已发货,6已收货,7已作废")
    private Byte orderDeliveryStatus;

    @ApiModelProperty(value = "是否为售后订单或已发货30天后的订单:0否,1是")
    private Byte isAfterSaleOrder;

    @ApiModelProperty(value = "主订单ID")
    private String mainOrderId;

    @ApiModelProperty(value = "拆分合并状态:1拆分,2合并")
    private Byte splittedOrMerged;

    @ApiModelProperty(value = "子订单集合:以#分隔")
    private String childIds;

    @ApiModelProperty(value = "该单是否有效:0有效,1无效")
    private Byte isValid;

    @ApiModelProperty(value = "卖家平台店铺ID")
    private Integer platformShopId;

    @ApiModelProperty(value = "卖家平台店铺名")
    private String platformSellerAccount;

    @ApiModelProperty(value = "店铺类型:0:PERSONAL/1:RENT")
    private String shopType;

    @ApiModelProperty(value = "卖家平台账号")
    private String platformSellerId;

    @ApiModelProperty(value = "卖家品连ID")
    private Integer sellerPlId;

    @ApiModelProperty(value = "卖家品连账号")
    private String sellerPlAccount;

    @ApiModelProperty(value = "卖家供应链ID")
    private Integer supplyChainCompanyId;

    @ApiModelProperty(value = "卖家供应链名称")
    private String supplyChainCompanyName;

    @ApiModelProperty(value = "订单总售价:预估物流费+系统商品总金额")
    private BigDecimal total;

    @ApiModelProperty(value = "系统商品总价(商品单价X数量)")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "订单货款(平台抓取下来的金额)")
    private BigDecimal commoditiesAmount;

    @ApiModelProperty(value = "站点ID")
    private String marketplaceId;

    @ApiModelProperty(value = "支付ID")
    private String payId;

    @ApiModelProperty(value = "支付状态:0待支付,10冻结失败,11冻结成功,20付款中,21付款成功,22付款失败,30待补款,40已取消")
    private Byte payStatus;

    @ApiModelProperty(value = "支付方式:1账户余额,2微信,3支付宝,4线下支付")
    private Byte payMethod;

    @ApiModelProperty(value = "付款时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String payTime;

    @ApiModelProperty(value = "下单时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String orderTime;

    @ApiModelProperty(value = "订单创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    @ApiModelProperty(value = "发货时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String deliveryTime;

    @ApiModelProperty(value = "仓库CODE")
    private String deliveryWarehouseCode;

    @ApiModelProperty(value = "订单发货仓库名称")
    private String deliveryWarehouse;

    @ApiModelProperty(value = "物流商CODE")
    private String shippingCarrierUsedCode;

    @ApiModelProperty(value = "物流商名称")
    private String shippingCarrierUsed;

    @ApiModelProperty(value = "邮寄方式CODE")
    private String deliveryMethodCode;

    @ApiModelProperty(value = "邮寄方式名称")
    private String deliveryMethod;

    @ApiModelProperty(value = "映射后的Amazon物流商名称")
    private String amazonCarrierName;

    @ApiModelProperty(value = "映射后的Amazon配送方式")
    private String amazonShippingMethod;

    @ApiModelProperty(value = "映射后的Ebay物流商名称")
    private String ebayCarrierName;

    @ApiModelProperty(value = "卖家填的平台物流费")
    private BigDecimal shippingServiceCost;

    @ApiModelProperty(value = "实际物流费")
    private BigDecimal actualShipCost;

    @ApiModelProperty(value = "预估物流费")
    private BigDecimal estimateShipCost;

    @ApiModelProperty(value = "系统折扣运费")
    private BigDecimal sysShippingDiscount;

    @ApiModelProperty(value = "跟踪单号")
    private String shipTrackNumber;

    @ApiModelProperty(value = "物流商单号")
    private String shipOrderId;

    @ApiModelProperty(value = "头程运费")
    private BigDecimal firstCarriage;

    @ApiModelProperty(value = "包装费用")
    private BigDecimal packingExpense;

    @ApiModelProperty(value = "支付/提现利息")
    private BigDecimal interest;

    @ApiModelProperty(value = "毛利")
    private BigDecimal grossMargin;

    @ApiModelProperty(value = "利润率")
    private BigDecimal profitMargin;

    @ApiModelProperty(value = "仓库返回订单号")
    private String referenceId;

    @ApiModelProperty(value = "买家ID")
    private String buyerUserId;

    @ApiModelProperty(value = "买家姓名")
    private String buyerName;

    @ApiModelProperty(value = "买家留言")
    private String buyerCheckoutMessage;

    @ApiModelProperty(value = "收货人姓名")
    private String shipToName;

    @ApiModelProperty(value = "收货目的地/国家代码")
    private String shipToCountry;

    @ApiModelProperty(value = "收货目的地/国家名称")
    private String shipToCountryName;

    @ApiModelProperty(value = "收货省/州名")
    private String shipToState;

    @ApiModelProperty(value = "收货城市")
    private String shipToCity;

    @ApiModelProperty(value = "收货地址1")
    private String shipToAddrStreet1;

    @ApiModelProperty(value = "收货地址2")
    private String shipToAddrStreet2;

    @ApiModelProperty(value = "收货地址3")
    private String shipToAddrStreet3;

    @ApiModelProperty(value = "收货邮编")
    private String shipToPostalCode;

    @ApiModelProperty(value = "收货人电话")
    private String shipToPhone;

    @ApiModelProperty(value = "收货人email")
    private String shipToEmail;

    @ApiModelProperty(value = "仓库发货异常信息")
    private String warehouseShipException;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "（该字段主要是提供给第三方供应商接口用）是否指定发货方式，" +
            "如果是，则 deliveryWarehouseCode 和 deliveryMethodCode 不为空。0-不指定，1-指定")
    private Integer appointDeliveryWay;

    @ApiModelProperty(value = "包邮类型。0-不包邮，1-包邮，2-部分包邮")
    private Integer freeFreightType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tb_sys_order
     *
     * @mbg.generated 2019-05-05 15:59:46
     */
    private static final long serialVersionUID = 1L;
}