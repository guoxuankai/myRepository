package com.brandslink.cloud.finance.pojo.vo;

import com.brandslink.cloud.finance.pojo.base.BaseSortVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangzefei
 * @Classname CustomerVo
 * @Description 客户查询条件
 * @Date 2019/8/30 9:53
 */
@Data
public class CustomerVo extends BaseSortVo {
    @ApiModelProperty(value = "客户名称")
    private String customerName;
}
