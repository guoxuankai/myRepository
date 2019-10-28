package com.rondaful.cloud.supplier.config.strategy;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * @Author: xqq
 * @Date: 2019/6/13
 * @Description:
 */
public class CommonStrategy implements PreciseShardingAlgorithm<String> {

    /**
     * Sharding.
     *
     * @param availableTargetNames available data sources or tables's names
     * @param shardingValue        sharding value
     * @return sharding result for data source or table's name
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {

        for (String tableName:availableTargetNames) {
            String ta=tableName+"_"+Math.abs(shardingValue.getValue().hashCode()%20);
            return tableName+"_"+Math.abs(shardingValue.getValue().hashCode()%20);
        }
        return null;
    }
}
