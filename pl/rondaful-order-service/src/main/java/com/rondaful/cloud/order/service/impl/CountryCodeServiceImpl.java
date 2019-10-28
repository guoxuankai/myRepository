package com.rondaful.cloud.order.service.impl;

import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.order.entity.CountryCode;
import com.rondaful.cloud.order.mapper.CountryCodeMapper;
import com.rondaful.cloud.order.service.ICountryCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CountryCodeServiceImpl extends BaseServiceImpl<CountryCode> implements ICountryCodeService {

    private final CountryCodeMapper countryCodeMapper;

    @Autowired
    public CountryCodeServiceImpl(CountryCodeMapper countryCodeMapper) {
        this.countryCodeMapper = countryCodeMapper;
    }

    @Override
    public List<CountryCode> queryList(CountryCode code) {
        return countryCodeMapper.page(code);
    }

    @Override
    public CountryCode findCountryByISO(String countryCode) {
        return countryCodeMapper.selectCountryByISO(countryCode);
    }
}
