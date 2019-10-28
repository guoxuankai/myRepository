package com.brandslink.cloud.finance.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import com.brandslink.cloud.finance.pojo.dto.CustomerFlowDetailDto;
import com.brandslink.cloud.finance.pojo.entity.CustomerFlowDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
public interface CustomerFlowDetailMapper extends BaseMapper<CustomerFlowDetail> {
    List<CustomerFlowDetailDto> getByCustomerFlowId(@Param("customerFlowId") Integer customerFlowId);
}