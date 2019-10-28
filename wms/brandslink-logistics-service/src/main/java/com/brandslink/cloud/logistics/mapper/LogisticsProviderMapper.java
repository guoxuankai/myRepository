package com.brandslink.cloud.logistics.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.logistics.model.LogisticsProviderModel;
import org.apache.ibatis.annotations.Param;

public interface LogisticsProviderMapper extends BaseMapper<LogisticsProviderModel> {

    void insertUpdateSelective(LogisticsProviderModel providerModel);

    LogisticsProviderModel getByLogisticsCode(String logisticsCode);

    LogisticsProviderModel selectProviderInfoByMethodID(@Param("methodId") Long methodId);
}