package com.brandslink.cloud.logistics.service;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.logistics.model.MethodZoneCountryModel;
import com.brandslink.cloud.logistics.model.MethodZoneFreightModel;

import java.util.List;

public interface IMethodZoneFreightService extends BaseService<MethodZoneFreightModel> {

    Long editMethodZoneFreight(MethodZoneFreightModel zoneModel) throws Exception;

    MethodZoneFreightModel selectZoneByID(Long zoneId);

    List<MethodZoneCountryModel> selectZoneIDByMethodIDCountry(Long methodId, String[] countryArray);

    void deleteZoneByID(Long zoneId);

    Page<MethodZoneFreightModel> selectMethodZoneList(Long methodId);
}
