package com.rondaful.cloud.order.service;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.order.entity.CountryCode;

import java.util.List;

public interface ICountryCodeService extends BaseService<CountryCode> {

    @Override
    Page<CountryCode> page(CountryCode code);

    List<CountryCode> queryList(CountryCode code);

    /**
     * 根据iso编码查询国家
     *
     * @param countryCode countryCode
     * @return {@link CountryCode}
     */
    CountryCode findCountryByISO(String countryCode);
}
