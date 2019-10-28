package com.brandslink.cloud.finance.pojo.vo;

import com.brandslink.cloud.finance.pojo.base.BaseSortVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author yangzefei
 * @Classname SysAccountFlowVo
 * @Description 查询资金流水模型
 * @Date 2019/9/3 14:26
 */
@Data
public class SysAccountFlowVo extends BaseSortVo {

    @ApiModelProperty(value = "收支类型。1:支出,2:收入")
    private Integer orderType;

    @ApiModelProperty(value = "费用类型 6:物流费,7:充值费")
    private Integer costType;

    @ApiModelProperty(value = "创建开始时间")
    private Date createStartTime;

    @ApiModelProperty(value = "创建结束时间")
    private Date createEndTime;
}
