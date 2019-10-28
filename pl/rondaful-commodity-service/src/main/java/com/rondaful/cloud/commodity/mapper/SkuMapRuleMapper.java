package com.rondaful.cloud.commodity.mapper;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.commodity.entity.SkuMapRule;

public interface SkuMapRuleMapper {

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
    SkuMapRule selectBySellerId(@Param("sellerId")String sellerId);




}
