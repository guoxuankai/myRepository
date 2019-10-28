package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.inventory.Commodity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CommodityMapper extends BaseMapper<Commodity> {

    /**
     * 查询老表数据映射  迁移数据  过渡时期用
     * @return
     */
    @Deprecated
    List<Map> getsOldPage(@Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize);
    @Deprecated
    Integer getsOldCount();


    /**
     * 批量插入
     * @param list
     * @return
     */
    Integer insertBatch(@Param("list") List<Commodity> list);

    /**
     * 根据品连sku查询商品信息
     * @param pinlianSku
     * @return
     */
    Commodity getByPSku(@Param("pinlianSku") String pinlianSku);


}