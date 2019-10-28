package com.rondaful.cloud.transorder.mapper;


import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.transorder.entity.OrderRule;
import com.rondaful.cloud.transorder.entity.OrderRuleWithBLOBs;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderRuleWarehouseMapper extends BaseMapper<OrderRuleWithBLOBs> {

    List<OrderRuleWithBLOBs> findAll(OrderRule rule);


}