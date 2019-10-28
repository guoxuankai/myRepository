package com.brandslink.cloud.finance.pojo.vo.CustomerConfig;

import com.brandslink.cloud.finance.pojo.base.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: zhangjinhua
 * @Date: 2019/8/21 15:22
 */
@Data
@ApiModel(value = "QueryCustomerConfigVo")
public class QueryCustomerConfigVo extends BaseVO {
    @ApiModelProperty(value = "客户code")
    private Integer customerCode;
    @ApiModelProperty(value = "版本号")
    private String version;
    @ApiModelProperty(value = "状态")
    private Integer customerState;
    @ApiModelProperty(value = "生效时间1")
    private Date updateDate1;
    @ApiModelProperty(value = "生效时间2")
    private Date updateDate2;
}
