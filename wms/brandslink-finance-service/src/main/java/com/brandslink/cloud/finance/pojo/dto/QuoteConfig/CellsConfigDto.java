package com.brandslink.cloud.finance.pojo.dto.QuoteConfig;

import com.brandslink.cloud.finance.pojo.vo.QuoteConfig.ConfigCellsCla;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Author: zhangjinhua
 * @Date: 2019/9/6 10:52
 */
@Data
@ApiModel("CellsConfigDto")
public class CellsConfigDto extends ConfigCellsCla {
    @ApiModelProperty("货型分类名称")
    private String rowName;

    @ApiModelProperty("行顺序")
    private Integer rowIndex;

}
