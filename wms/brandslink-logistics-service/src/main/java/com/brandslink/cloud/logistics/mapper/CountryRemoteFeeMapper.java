package com.brandslink.cloud.logistics.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.logistics.model.CountryRemoteFeeModel;

import java.util.List;

public interface CountryRemoteFeeMapper extends BaseMapper<CountryRemoteFeeModel> {

    void insertBatch(List<CountryRemoteFeeModel> remoteFeeList);

    void deleteByCountryId(Long countryId);

    void deleteBatchByCountryId(List<Long> countryIdList);

    List<CountryRemoteFeeModel> selectByCountryID(Long id);
}