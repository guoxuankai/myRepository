package com.rondaful.cloud.commodity.service;

import com.rondaful.cloud.commodity.entity.SkuMapRule;

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

}
