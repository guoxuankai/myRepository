package com.brandslink.cloud.logistics.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.logistics.entity.centre.CollectorVo;
import com.brandslink.cloud.logistics.model.CollectorMethodModel;
import com.brandslink.cloud.logistics.model.LogisticsCollectorModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LogisticsCollectorMapper extends BaseMapper<LogisticsCollectorModel> {

    void insertUpdateSelective(LogisticsCollectorModel collectorModel);

    List<CollectorMethodModel> selectMethodListByCollectorId(Long collectorId);

    CollectorVo getByCode(@Param("logisticsCode") String logisticsCode, @Param("methodCode")String methodCode,@Param("warehouseCode") String warehouseCode);


}