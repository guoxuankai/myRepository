package com.brandslink.cloud.finance.pojo.vo;

import com.brandslink.cloud.finance.pojo.base.BaseSortVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author yangzefei
 * @Classname CustomerFlowVo
 * @Description 客户流水查询实体
 * @Date 2019/8/27 14:32
 */
@Data
public class CustomerFlowVo extends BaseSortVo {
    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;
    @ApiModelProperty(value = "客户编码")
    private String customerCode;
    @ApiModelProperty(value = "流水单号")
    private String orderNo;
    @ApiModelProperty(value = "来源单号")
    private String sourceNo;
    @ApiModelProperty(value = "费用类型 1:存储费,2:入库费,3:销退费,4:出库费,5:订单拦截费,6:物流费,7:充值费")
    private Integer costType;
    @ApiModelProperty(value = "收支类型。1:支出,2:收入")
    private Integer orderType;
    @ApiModelProperty(value = "开始计费时间")
    private Date startBillTime;
    @ApiModelProperty(value = "结束计费时间")
    private Date endBillTime;
    @ApiModelProperty(value = "开始创建时间")
    private Date startCreateTime;
    @ApiModelProperty(value = "结束创建时间")
    private Date endCreateTime;
}
