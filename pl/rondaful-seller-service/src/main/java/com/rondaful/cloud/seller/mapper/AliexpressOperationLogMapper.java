package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressOperationLog;

import java.util.List;

public interface AliexpressOperationLogMapper extends BaseMapper<AliexpressOperationLog> {

    public List<AliexpressOperationLog> getAliexpressOperationLogBylistingId(Long listingId);
}