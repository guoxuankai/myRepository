package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.basics.WarehouseService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WarehouseServiceMapper extends BaseMapper<WarehouseService> {

    /**
     * 获取仓库服务商列表
     * @return
     */
    List<WarehouseService> getAll(@Param("type") Integer type);

    /**
     * 修改服务商供应链公司
     * @param supplyId
     * @param serviceCode
     * @return
     */
    Integer updateByServiceCode(@Param("supplyId") Integer supplyId,@Param("serviceCode") String serviceCode);

    /**
     * 根据供应链公司id获取绑定服务商数量
     * @param supplId
     * @return
     */
    Integer getBindService(@Param("supplyId") Integer supplyId);
}