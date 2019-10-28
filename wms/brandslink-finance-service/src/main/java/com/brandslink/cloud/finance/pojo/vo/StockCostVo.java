package com.brandslink.cloud.finance.pojo.vo;

import com.brandslink.cloud.finance.pojo.dto.ProductDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author yangzefei
 * @Classname StockCostVo
 * @Description 入库、销退参数模型
 * @Date 2019/9/4 15:49
 */
@Data
public class StockCostVo extends BaseCostVo {

    @ApiModelProperty(value = "详情类型 1:存储费,2:入库费(免检),3:销退费,4:出库操作费,5:入库费(抽检),6:入库费(全检)")
    private Integer detailType;
    @ApiModelProperty(value = "商品信息详情")
    private List<StockCostDetailVo> items;

    @ApiModelProperty(value = "客户对应的商品信息",hidden = true)
    private Map<String,List<ProductDto>> customerMap;
}
