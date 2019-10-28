package com.rondaful.cloud.order.entity.wmsdto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ProjectName: Rondaful
 * @Package: com.rondaful.cloud.order.entity.wmsdto
 * @ClassName: cancalWmsOrderDTO
 * @Author: Superhero
 * @Description: 取消WMS订单请求参数对象
 * @Date: 2019/8/12 16:01
 */
@Data
@ApiModel(value = "取消WMS订单请求参数对象")
public class CancalWmsOrderDTO {
    @ApiModelProperty(value = "包裹ID")
    private String packageNum;
}
