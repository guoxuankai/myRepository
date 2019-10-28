package com.rondaful.cloud.order.service;

import com.rondaful.cloud.order.entity.orderRule.SkuMapRule;

public interface SkuMapRuleService {

    /**
     * 添加
     * @param rule 规则
     * @return 值
     */
    void insert(SkuMapRule rule);

    /**
     * 删除
     * @param id ID
     */
    void delete(Integer id);

    /**
     * 更新
     * @param rule 对象
     */
    void update(SkuMapRule rule);

    /**
     * 通过用户ID查询规则对象
     * @param rule 规则
     * @return 对象
     */
    SkuMapRule selectBySellerId(SkuMapRule rule);
}
