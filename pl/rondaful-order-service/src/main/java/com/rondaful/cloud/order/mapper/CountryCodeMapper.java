package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.CountryCode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CountryCodeMapper extends BaseMapper<CountryCode> {
    int deleteByPrimaryKey(Integer id);

    int insert(CountryCode record);

    int insertSelective(CountryCode record);

    CountryCode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CountryCode record);

    int updateByPrimaryKey(CountryCode record);

    /**
     * 查询列表
     * */
    List<CountryCode> page(CountryCode t);

    String findCountryByISO(String countryCode);

    CountryCode selectCountryByISO(String countryCode);
}