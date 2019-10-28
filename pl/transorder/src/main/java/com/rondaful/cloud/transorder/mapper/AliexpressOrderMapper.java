package com.rondaful.cloud.transorder.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.transorder.entity.aliexpress.AliexpressOrder;

import java.util.List;

public interface AliexpressOrderMapper extends BaseMapper<AliexpressOrder> {

    List<AliexpressOrder> getsByOrderIds(List<String> orderIds);

}