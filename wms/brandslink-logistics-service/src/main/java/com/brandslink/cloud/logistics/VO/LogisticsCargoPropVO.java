package com.brandslink.cloud.logistics.VO;

import com.brandslink.cloud.logistics.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 物流货物属性表
 * 实体类对应的数据表为：  t_logistics_cargo_prop
 *
 * @author zhangjinglei
 * @date 2019-07-17 17:18:49
 */
@Data
@ApiModel(value = "LogisticsCargoPropVO")
public class LogisticsCargoPropVO extends BaseEntity implements Serializable {
    @ApiModelProperty(value = "顺序号")
    private Long id;

    @NotBlank(message = "货物属性编码不可为空")
    @Size(max = 50, message = "货物属性编码字符个数必须小于50")
    @ApiModelProperty(value = "货物属性编码")
    private String cargoCode;

    @NotBlank(message = "货物属性名称不可为空")
    @Size(max = 100, message = "货物属性名称字符个数必须小于100")
    @ApiModelProperty(value = "货物属性名称")
    private String cargoName;

    @NotNull(message = "是否有效不能为空")
    @ApiModelProperty(value = "是否有效（1：是，2否）")
    private Byte isValid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_logistics_cargo_prop
     *
     * @mbg.generated 2019-07-17 17:18:49
     */
    private static final long serialVersionUID = 1L;
}