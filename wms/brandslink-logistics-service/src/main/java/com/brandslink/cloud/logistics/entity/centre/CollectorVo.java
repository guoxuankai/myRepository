package com.brandslink.cloud.logistics.entity.centre;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author guoxuankai
 * @date 2019/7/29 10:22
 */
@Data
@ApiModel(value = "CollectorVo")
public class CollectorVo {

    @ApiModelProperty(value = "揽收商名称")
    private String collectorName;

    @ApiModelProperty(value = "邮寄方式名称")
    private String methodName;

    @ApiModelProperty(value = "揽收方式")
    private String collectType;


}
