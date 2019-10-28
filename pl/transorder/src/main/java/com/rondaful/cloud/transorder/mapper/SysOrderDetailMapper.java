package com.rondaful.cloud.transorder.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.transorder.entity.system.SysOrderDetail;

import java.util.List;

public interface SysOrderDetailMapper extends BaseMapper<SysOrderDetail> {
    void insertBatch(List<SysOrderDetail> sysOrderDetails);
}