package com.brandslink.cloud.logistics.service.impl;

import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.logistics.mapper.CountryRemoteFeeMapper;
import com.brandslink.cloud.logistics.mapper.MethodZoneCountryMapper;
import com.brandslink.cloud.logistics.mapper.MethodZoneFreightMapper;
import com.brandslink.cloud.logistics.model.MethodZoneCountryModel;
import com.brandslink.cloud.logistics.service.IMethodZoneCountryService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MethodZoneCountryServiceImpl extends BaseServiceImpl<MethodZoneCountryModel> implements IMethodZoneCountryService {
    @Autowired
    private MethodZoneCountryMapper zoneCountryMapper;
    @Autowired
    private CountryRemoteFeeMapper remoteFeeMapper;
    @Autowired
    private MethodZoneFreightMapper zoneMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteZoneCountry(Long countryId) {
        MethodZoneCountryModel zoneCountry = zoneCountryMapper.selectByPrimaryKey(countryId);
        if (zoneCountry != null){
            Long zoneId = zoneCountry.getZoneId();
            zoneCountryMapper.deleteByPrimaryKey(countryId);
            remoteFeeMapper.deleteByCountryId(countryId);
            if (CollectionUtils.isEmpty(zoneCountryMapper.selectSharedZoneCountryByZoneID(zoneId))) {
                zoneMapper.deleteByPrimaryKey(zoneId);
            }
        }
    }
}
