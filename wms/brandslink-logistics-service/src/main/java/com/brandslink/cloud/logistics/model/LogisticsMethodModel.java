package com.brandslink.cloud.logistics.model;

import com.alibaba.fastjson.JSONArray;
import com.brandslink.cloud.logistics.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 邮寄方式表
 * 实体类对应的数据表为：  t_logistics_method
 *
 * @author zhangjinglei
 * @date 2019-07-18 17:19:37
 */
@Data
@ApiModel(value = "LogisticsMethodModel")
public class LogisticsMethodModel extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "物流商简称")
    private String logisticsShortened;

    @ApiModelProperty(value = "物流商编码")
    private String logisticsCode;

    @ApiModelProperty(value = "寄件人信息")
    private LogisticsMethodAddressModel sender;

    @ApiModelProperty(value = "揽收人信息")
    private LogisticsMethodAddressModel collectMan;

    @ApiModelProperty(value = "退货人信息")
    private LogisticsMethodAddressModel refunder;

    @ApiModelProperty(value = "邮寄方式揽收商仓库关系对象集合")
    private List<MethodCollectorRelationModel> relationList = new ArrayList<>();

    @ApiModelProperty(value = "顺序号")
    private Long id;

    @ApiModelProperty(value = "邮寄方式名称")
    private String logisticsMethodName;

    @ApiModelProperty(value = "邮寄方式编码")
    private String logisticsMethodCode;

    @ApiModelProperty(value = "是否有效（1：是，2否）")
    private Byte isValid;

    @ApiModelProperty(value = "结算方式（1：月结，2：半月结，3：周结）")
    private Byte clearingForm;

    @ApiModelProperty(value = "地址最大长度")
    private Short maxAddressLength;

    @ApiModelProperty(value = "有无跟踪单号")
    private Byte haveTrackNum;

    @ApiModelProperty(value = "关联物流商表主键")
    private Long providerId;

    @ApiModelProperty(value = "可发货平台[{'code': '平台编码', 'name': '平台名称', 'type': '是否有效（1是2否）'}],JSONArray类型")
    private JSONArray supportPlatform;

    @ApiModelProperty(value = "寄件人信息(关联邮寄方式角色表主键)")
    private Long senderId;

    @ApiModelProperty(value = "揽收人信息(关联邮寄方式角色表主键)")
    private Long collectManId;

    @ApiModelProperty(value = "退货人信息(关联邮寄方式角色表主键)")
    private Long refunderId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_logistics_method
     *
     * @mbg.generated 2019-07-18 17:19:37
     */
    private static final long serialVersionUID = 1L;
}