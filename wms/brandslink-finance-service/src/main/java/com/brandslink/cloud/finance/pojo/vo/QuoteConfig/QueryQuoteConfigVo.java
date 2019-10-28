package com.brandslink.cloud.finance.pojo.vo.QuoteConfig;
import com.brandslink.cloud.finance.pojo.base.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Author: zhangjinhua
 * @Date: 2019/8/30 11:16
 */
@Data
@ApiModel(value = "QueryCustomerConfigVo")
public class QueryQuoteConfigVo extends BaseVO {

    @ApiModelProperty(value = "配置类型 1:商品货型,2:仓储费库龄配置,3:卸货费配置,4:打包费配置")
    private Byte configType;
    @ApiModelProperty(value = "版本号")
    private String version;
    @ApiModelProperty(value = "配置状态 1:待提交,2:待生效,3:已生效,4:已失效")
    private Byte configStatus;

    @ApiModelProperty(value = "生效时间1")
    private String updateTime1;
    @ApiModelProperty(value = "生效时间2")
    private String updateTime2;
}
