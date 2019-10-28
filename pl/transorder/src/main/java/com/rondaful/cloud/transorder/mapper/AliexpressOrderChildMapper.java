package com.rondaful.cloud.transorder.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.transorder.entity.aliexpress.AliexpressOrderChild;

import java.util.List;

public interface AliexpressOrderChildMapper extends BaseMapper<AliexpressOrderChild> {

    List<AliexpressOrderChild> getByParentId(String parentId);
}