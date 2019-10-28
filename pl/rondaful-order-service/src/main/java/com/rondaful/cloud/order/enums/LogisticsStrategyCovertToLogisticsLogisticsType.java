package com.rondaful.cloud.order.enums;

import java.util.Objects;

/**
 * 订单的物流策略转换为物流服务的物流类型
 *
 * @author Blade
 * @date 2019-07-29 20:49:07
 **/
public enum  LogisticsStrategyCovertToLogisticsLogisticsType {

    CHEAPEST("cheapest", 1),

    INTEGRATED_OPTIMAL("integrated_optimal", 2),

    FASTEST("fastest", 3);

    private String logisticsStrategy;
    private int logisticsType;

    LogisticsStrategyCovertToLogisticsLogisticsType(String logisticsStrategy, int logisticsType) {
        this.logisticsStrategy = logisticsStrategy;
        this.logisticsType = logisticsType;
    }

    public String getLogisticsStrategy() {
        return logisticsStrategy;
    }

    public int getLogisticsType() {
        return logisticsType;
    }

    public static String getLogisticsStrategyByLogisticsType(int logisticsType) {
        LogisticsStrategyCovertToLogisticsLogisticsType[] arrays = LogisticsStrategyCovertToLogisticsLogisticsType.values();
        for (LogisticsStrategyCovertToLogisticsLogisticsType logistics : arrays) {
            if (logistics.getLogisticsType() == logisticsType) {
                return logistics.getLogisticsStrategy();
            }
        }

        return null;
    }

    public static int getLogisticsTypeByLogisticsStrategy(String logisticsStrategy) {
        LogisticsStrategyCovertToLogisticsLogisticsType[] arrays = LogisticsStrategyCovertToLogisticsLogisticsType.values();
        for (LogisticsStrategyCovertToLogisticsLogisticsType logistics : arrays) {
            if (Objects.equals(logisticsStrategy, logistics.getLogisticsStrategy())) {
                return logistics.getLogisticsType();
            }
        }

        return INTEGRATED_OPTIMAL.getLogisticsType();
    }
}
