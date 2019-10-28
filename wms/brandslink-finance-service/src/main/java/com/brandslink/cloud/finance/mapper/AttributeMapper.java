package com.brandslink.cloud.finance.mapper;

import com.brandslink.cloud.finance.pojo.entity.Attribute;
import com.brandslink.cloud.common.mapper.BaseMapper;

public interface AttributeMapper extends BaseMapper<Attribute> {
    int update(Attribute attribute);
}