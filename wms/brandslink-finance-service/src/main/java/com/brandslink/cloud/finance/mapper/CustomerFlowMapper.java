package com.brandslink.cloud.finance.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.finance.pojo.dto.CustomerFlowDto;
import com.brandslink.cloud.finance.pojo.entity.CustomerFlow;
import com.brandslink.cloud.finance.pojo.vo.CustomerFlowVo;

import java.util.List;

public interface CustomerFlowMapper extends BaseMapper<CustomerFlow> {
    List<CustomerFlowDto> getList(CustomerFlowVo param);

    CustomerFlowDto selectById(Integer id);

    CustomerFlow selectBySourceNo(String sourceNo);
}