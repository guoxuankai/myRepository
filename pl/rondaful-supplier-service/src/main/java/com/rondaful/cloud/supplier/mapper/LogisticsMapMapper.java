package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.Logistics.LogisticsMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LogisticsMapMapper extends BaseMapper<LogisticsMap> {

    /**
     * 根据物流方式查询绑定平台列表
     * @param logisticsId
     * @return
     */
    List<LogisticsMap> getByLogisticsId(@Param("logisticsId") Integer logisticsId);

    /**
     * 删除映射
     * @param logisticsId
     * @return
     */
    Integer del(@Param("logisticsId") Integer logisticsId);

    /**
     * 批量插入平台映射
     * @param list
     * @return
     */
    Integer insertBatch(@Param("list") List<LogisticsMap> list);
}