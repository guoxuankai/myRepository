package com.brandslink.cloud.finance.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.finance.pojo.dto.ProductDto;
import com.brandslink.cloud.finance.pojo.vo.StockCostVo;

/**
 * @author yangzefei
 * @Classname CenterDbService
 * @Description 中心数据库服务
 * @Date 2019/9/4 17:03
 */
public interface CenterDbService  extends BaseService<ProductDto> {

    StockCostVo setStockCostVo(StockCostVo param);

    String getWarehouseName(String warehouseCode);
}
