package com.brandslink.cloud.finance.pojo.vo;

import com.brandslink.cloud.finance.pojo.base.BaseVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author guoxuankai
 * @date 2019/8/22 17:35
 */
@ApiModel(value = "物流商收费查询实体")
@Data
public class LogisticsFeesVO extends BaseVO {

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "包裹号")
    private String packageNo;

    @ApiModelProperty(value = "物流运单")
    private String waybill;

    @ApiModelProperty(value = "邮寄方式")
    private String mailingMethod;

    @ApiModelProperty(value = "发货仓库")
    private String warehouse;

    @ApiModelProperty(value = "状态：1.待导入2.待确认3.已确认")
    private Byte status;

    @ApiModelProperty(value = "生效起始时间,yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "生效结束时间,yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

}
