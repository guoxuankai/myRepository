package com.brandslink.cloud.logistics.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.logistics.model.MethodZoneCountryModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MethodZoneCountryMapper extends BaseMapper<MethodZoneCountryModel> {

    List<MethodZoneCountryModel> selectBatchMethodZoneCountryList(@Param("methodId") Long methodId, @Param("list") List<MethodZoneCountryModel> list);

    List<Long> selectSharedZoneCountryByZoneID(Long countryId);

    void deleteByZoneId(Long zoneId);

    List<MethodZoneCountryModel> selectZoneIDByMethodIDCountry(Long methodId, String[] countryArray);
}