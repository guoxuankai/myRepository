package com.brandslink.cloud.logistics.mapper;

import com.brandslink.cloud.logistics.entity.Attribute;
import com.brandslink.cloud.common.mapper.BaseMapper;

public interface AttributeMapper extends BaseMapper<Attribute> {
    int update(Attribute attribute);
}