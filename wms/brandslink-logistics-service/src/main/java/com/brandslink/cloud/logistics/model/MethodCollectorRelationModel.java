package com.brandslink.cloud.logistics.model;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "MethodCollectorRelationModel")
public class MethodCollectorRelationModel implements Serializable {

    @ApiModelProperty(value = "邮寄方式名称")
    private String methodName;

    @ApiModelProperty(value = "邮寄方式编码")
    private String methodCode;

    @ApiModelProperty(value = "揽收商编码")
    private String collectorCode;

    @ApiModelProperty(value = "所属仓库：揽收方式（JSON）")
    private JSONArray warehouseCollectType;

    @ApiModelProperty(value = "顺序号")
    private Long id;

    @ApiModelProperty(value = "关联邮寄方式表主键")
    private Long methodId;

    @ApiModelProperty(value = "关联物流揽收商表主键")
    private Long collectorId;

    @ApiModelProperty(value = "关联物流商表主键")
    private Long providerId;

    @NotBlank(message = "邮寄方式对应揽收商的仓库编码不能为空")
    @ApiModelProperty(value = "此邮寄方式对应揽收商的仓库编码")
    private String warehouse;

    @NotBlank(message = "邮寄方式对应揽收商的仓库名称不能为空")
    @ApiModelProperty(value = "此邮寄方式对应揽收商的仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "揽收商名称")
    private String collectorName;

    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    @ApiModelProperty(value = "更新人")
    private String updateBy;
}
