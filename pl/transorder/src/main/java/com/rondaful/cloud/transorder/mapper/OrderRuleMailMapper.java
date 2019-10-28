package com.rondaful.cloud.transorder.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.transorder.entity.OrderRule;
import com.rondaful.cloud.transorder.entity.OrderRuleWithBLOBs;

import java.util.List;

public interface OrderRuleMailMapper extends BaseMapper<OrderRuleWithBLOBs> {

    List<OrderRuleWithBLOBs> findAll(OrderRule rule);

}
