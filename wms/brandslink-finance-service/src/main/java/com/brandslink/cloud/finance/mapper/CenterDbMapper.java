package com.brandslink.cloud.finance.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.finance.config.DataSource;
import com.brandslink.cloud.finance.config.DynamicDataSource;
import com.brandslink.cloud.finance.pojo.dto.ProductDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yangzefei
 * @Classname CenterDbMapper
 * @Description 中心服务数据库
 * @Date 2019/9/4 16:08
 */
public interface CenterDbMapper extends BaseMapper<ProductDto> {

    @DataSource(DynamicDataSource.CENTER_DS)
    String getWarehouseName(@Param("warehouseCode") String warehouseCode);

    @DataSource(DynamicDataSource.CENTER_DS)
    List<ProductDto> getBySkuList(@Param("list") List<String> list);
}
