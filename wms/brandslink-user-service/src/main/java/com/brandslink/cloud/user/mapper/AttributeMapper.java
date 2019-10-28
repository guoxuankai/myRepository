package com.brandslink.cloud.user.mapper;

import com.brandslink.cloud.user.entity.Attribute;
import com.brandslink.cloud.common.mapper.BaseMapper;

public interface AttributeMapper extends BaseMapper<Attribute> {
    int update(Attribute attribute);
}