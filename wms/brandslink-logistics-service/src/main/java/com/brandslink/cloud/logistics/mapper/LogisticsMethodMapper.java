package com.brandslink.cloud.logistics.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.logistics.entity.centre.MethodVO;
import com.brandslink.cloud.logistics.entity.centre.LogisticsFreight;
import com.brandslink.cloud.logistics.entity.centre.LogisticsFreightCallBack;
import com.brandslink.cloud.logistics.model.LogisticsMethodModel;
import com.brandslink.cloud.logistics.model.LogisticsProviderModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LogisticsMethodMapper extends BaseMapper<LogisticsMethodModel> {

    void insertUpdateSelective(LogisticsMethodModel methodModel);

    LogisticsMethodModel selectMethodBasicInfoByID(Long methodId);

    List<String> selectAllUsedSequence(Long providerId);

    List<MethodVO> selectLogisticsMethod(@Param("warehouse") String warehouse);

    List<LogisticsFreightCallBack> selectMethodFreightByMultiCondition(LogisticsFreight logisticsFreight);

    void updateMethodInfoByProviderID(LogisticsMethodModel methodModel);

}