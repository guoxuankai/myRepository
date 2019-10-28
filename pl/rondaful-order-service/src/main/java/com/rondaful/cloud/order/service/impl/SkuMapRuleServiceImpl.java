package com.rondaful.cloud.order.service.impl;

import com.rondaful.cloud.order.entity.orderRule.SkuMapRule;
import com.rondaful.cloud.order.mapper.SkuMapRuleMapper;
import com.rondaful.cloud.order.service.SkuMapRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SkuMapRuleServiceImpl implements SkuMapRuleService {

    @Autowired
    private SkuMapRuleMapper skuMapRuleMapper;


    @Override
    public void insert(SkuMapRule rule) {
         skuMapRuleMapper.insert(rule);
    }

    @Override
    public void delete(Integer id) {
        skuMapRuleMapper.delete(id);
    }

    @Override
    public void update(SkuMapRule rule) {
        skuMapRuleMapper.update(rule);
    }

    @Override
    public SkuMapRule selectBySellerId(SkuMapRule rule) {
        return skuMapRuleMapper.selectBySellerId(rule);
    }
}
