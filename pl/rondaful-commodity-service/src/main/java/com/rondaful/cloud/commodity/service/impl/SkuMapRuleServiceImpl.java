package com.rondaful.cloud.commodity.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rondaful.cloud.commodity.entity.SkuMapRule;
import com.rondaful.cloud.commodity.mapper.SkuMapRuleMapper;
import com.rondaful.cloud.commodity.service.SkuMapRuleService;

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
}
