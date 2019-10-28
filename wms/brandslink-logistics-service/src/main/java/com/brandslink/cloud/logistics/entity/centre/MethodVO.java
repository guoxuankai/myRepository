package com.brandslink.cloud.logistics.entity.centre;

import com.alibaba.fastjson.JSONArray;
import com.brandslink.cloud.logistics.entity.common.CommonBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "MethodVO")
public class MethodVO implements Serializable {

    @ApiModelProperty(value = "顺序号")
    private Long id;

    @ApiModelProperty(value = "邮寄方式名称")
    private String methodName;

    @ApiModelProperty(value = "邮寄方式编码")
    private String methodCode;

    @ApiModelProperty(value = "是否有效")
    private Byte isValid;

    @ApiModelProperty(value = "关联物流商表主键")
    private Long providerId;

    @ApiModelProperty(value = "物流商简称")
    private String providerShortened;

    @ApiModelProperty(value = "物流商编码")
    private String providerCode;

    @ApiModelProperty(value = "可发货平台（JSON）")
    private JSONArray supportPlatform;

    @ApiModelProperty(value = "仓库集合")
    private List<CommonBean> warehouseList = new ArrayList<>();
}
