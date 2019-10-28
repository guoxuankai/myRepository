package com.brandslink.cloud.logistics.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.logistics.model.MethodZoneFreightModel;

import java.util.List;

public interface MethodZoneFreightMapper extends BaseMapper<MethodZoneFreightModel> {

    MethodZoneFreightModel selectZoneByID(Long zoneId);

    List<MethodZoneFreightModel> selectMethodZoneList(Long methodId);

    void updateMethodInfoByMethodID(MethodZoneFreightModel zoneModel);
}