package com.brandslink.cloud.finance.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.finance.pojo.dto.SysAccountFlowDto;
import com.brandslink.cloud.finance.pojo.entity.SysAccountFlow;
import com.brandslink.cloud.finance.pojo.vo.SysAccountFlowVo;

import java.util.List;

public interface SysAccountFlowMapper extends BaseMapper<SysAccountFlow> {
    List<SysAccountFlowDto> getList(SysAccountFlowVo param);
}