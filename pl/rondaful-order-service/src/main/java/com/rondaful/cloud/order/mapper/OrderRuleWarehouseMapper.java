package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.orderRule.OrderRule;
import com.rondaful.cloud.order.entity.orderRule.OrderRuleWithBLOBs;
import com.rondaful.cloud.order.model.SellerRuleModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderRuleWarehouseMapper extends BaseMapper<OrderRuleWithBLOBs> {

    int deleteByPrimaryKey(Long id);

    int insert(OrderRuleWithBLOBs record);

    int insertSelective(OrderRuleWithBLOBs record);

    OrderRuleWithBLOBs selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderRuleWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(OrderRuleWithBLOBs record);

    int updateByPrimaryKey(OrderRule record);

    List<OrderRuleWithBLOBs> findAll(OrderRule rule);

    int selectCount(OrderRule rule);

    void updateByPrimaryKeySelective2(OrderRuleWithBLOBs rule);

    /**
     * 根据
     * @param ruleType
     * @param rule
     * @return
     */
    List<OrderRuleWithBLOBs> findListByRuleType(@Param("ruleType") String ruleType,@Param("rule") OrderRule rule);
}