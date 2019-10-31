package com.brandslink.cloud.logistics.model;

import com.alibaba.fastjson.JSONArray;
import com.brandslink.cloud.logistics.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 物流揽收商表
 * 实体类对应的数据表为：  t_logistics_collector
 *
 * @author zhangjinglei
 * @date 2019-07-18 18:16:10
 */
@Data
@ApiModel(value = "LogisticsCollectorModel")
public class LogisticsCollectorModel extends BaseEntity implements Serializable {
    @ApiModelProperty(value = "顺序号")
    private Long id;

    @ApiModelProperty(value = "揽收商编码")
    private String collectorCode;

    @ApiModelProperty(value = "揽收商名称")
    private String collectorName;

    @ApiModelProperty(value = "联系人姓名")
    private String collector;

    @ApiModelProperty(value = "联系方式")
    private String mobile;

    @ApiModelProperty(value = "是否有效（1：是，2否）")
    private Byte isValid;

    @ApiModelProperty(value = "所属仓库：揽收方式[{'code': '仓库编码', 'name': '仓库名称', 'type': 揽收类型（1中转仓揽收，2自提，3自送货，4快递邮寄）}],JSONArray类型")
    private JSONArray warehouseCollectType;

    @ApiModelProperty(value = "收件国家")
    private String receiveCountry;

    @ApiModelProperty(value = "收件省/州")
    private String receiveState;

    @ApiModelProperty(value = "收件城市")
    private String receiveCity;

    @ApiModelProperty(value = "收件详细地址")
    private String receiveAddress;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_logistics_collector
     *
     * @mbg.generated 2019-07-18 18:16:10
     */
    private static final long serialVersionUID = 1L;
}