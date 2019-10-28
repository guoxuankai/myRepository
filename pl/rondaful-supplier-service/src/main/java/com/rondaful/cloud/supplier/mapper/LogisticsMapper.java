package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.Logistics.Logistics;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LogisticsMapper extends BaseMapper<Logistics> {

    /**
     * 批量插入物流方式
     * @param list
     * @return
     */
    Integer insertBatch(@Param("list") List<Logistics> list);

    /**
     * 分页查询
     * @param queryType
     * @param text
     * @param warehouseId
     * @return
     */
    List<Logistics> getsPage(@Param("queryType") Integer queryType,@Param("text") String text,@Param("warehouseId") Integer warehouseId);

    /**
     * 修改状态
     * @param id
     * @param status
     * @return
     */
    Integer updateStatus(@Param("id") Integer id,@Param("status") Integer status);

    /**
     * 根据code获取
     * @param code
     * @return
     */
    Logistics getByCode(@Param("code") String code,@Param("warehouseId") Integer warehouseId);

    /**
     * 获取仓库下所有的物流方式
     * @param warehouseId
     * @return
     */
    List<Logistics> getByWarehouseId(@Param("warehouseId") Integer warehouseId,@Param("codes") List<String> codes);
}