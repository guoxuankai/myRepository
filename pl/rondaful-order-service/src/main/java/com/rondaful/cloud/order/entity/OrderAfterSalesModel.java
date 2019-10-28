package com.rondaful.cloud.order.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.rondaful.cloud.common.utils.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
@ApiModel(description = "售后订单")
@Data
public class OrderAfterSalesModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "售后编号")
	private String numberingId;

	@ApiModelProperty(value = "订单号")
	private String orderId;

	@ApiModelProperty(value = "卖家品连账号")
	private String seller;

	@ApiModelProperty(value = "退款金额")
	private String refundMoney;

	@ApiModelProperty(value = "图片[xxx,xxx]多个用逗号隔开")
	private String image;

	@ApiModelProperty(value = "订单详情[根据订单号查询得到的所有数据转为json]")
	private String orderDetails;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "邮费")
	private String postage;

	@ApiModelProperty(value = "状态[ 1、待审核; 2、查看; 3、审核; 4、待确认; 5、已确认; 6、(已确认)系统超时自动确认; 7、已关闭; 8、(已关闭)系统超时已关闭; 9、全部确认; 10、退款完成; 11、已取消; 12、(已取消)系统超时关闭，没有编辑; 13、审核失败; 14、(审核失败)系统超时关闭，没有编辑; 15、编辑; 16、待退货; 17、提交物流信息; 18、退货中; 19、仓库已收货; 20、收货完成协商退款; 21、补发配货中; 22、补发已发货; 23、售后结案; 24、修改物流信息; 25、自动关闭; 26、拒绝退款; 27、退货完成等待退款; 29、自动收货; 30、重发; 31、同意退款; 32、取消; 33、新建提交; 34、修改提交; 35、退款中;36、拦截成功;37、作废 ]")
	//如果传过来的是【0】，则查出【19、20】两个状态的售后，为了卖家端：【已收货、收货完成协商退款】均显示已收货
	private Long status;

	@ApiModelProperty(value = "退款原因来源于公共参数接口")
	private String refundReason;

	@JSONField(format = DateUtils.FORMAT_2)
	@ApiModelProperty(value = "申请时间")
	private Date createTime;

	@JSONField(format = DateUtils.FORMAT_2)
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;

	@ApiModelProperty(value = "售后类型[1-仅退款、2-退款+退货、3-补货]")
	private Long afterSalesType;

	@ApiModelProperty(value = "开始时间")
	private String startTime;

	@ApiModelProperty(value = "结束时间")
	private String endTime;

	@ApiModelProperty(value = "补货跟踪号")
	private String trackingId;

	@ApiModelProperty(value = "店铺")
	private String shop;

	@ApiModelProperty(value = "供应商")
	private String supplier;

	@ApiModelProperty(value = "补发货异常原因(订单系统中写入)")
	private String warehouseShipException;

	@ApiModelProperty(value = "仓库返回的订单号")
	private String referenceId;

	@ApiModelProperty(value = "谷仓或ERP返回的退件单号")
	private String gcRrpReturnCode;

	@ApiModelProperty(value = "退货到的仓库类型 0erp 2谷仓")
	private String warehouseType;

	private String shipNumber;
	private String[] shops;
}