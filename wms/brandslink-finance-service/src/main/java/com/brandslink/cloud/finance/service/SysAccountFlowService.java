package com.brandslink.cloud.finance.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.finance.pojo.dto.SysAccountFlowDto;
import com.brandslink.cloud.finance.pojo.entity.SysAccountFlow;
import com.brandslink.cloud.finance.pojo.vo.SysAccountFlowVo;

import java.util.List;

/**
 * @author yangzefei
 * @Classname SysAccountFlowService
 * @Description 客户资金流水服务
 * @Date 2019/8/26 10:07
 */
public interface SysAccountFlowService extends BaseService<SysAccountFlow> {
    List<SysAccountFlowDto> getList(SysAccountFlowVo param);
}
