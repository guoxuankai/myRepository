package com.brandslink.cloud.logistics.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.logistics.model.MethodZoneCountryModel;

public interface IMethodZoneCountryService extends BaseService<MethodZoneCountryModel> {

    void deleteZoneCountry(Long countryId);
}
