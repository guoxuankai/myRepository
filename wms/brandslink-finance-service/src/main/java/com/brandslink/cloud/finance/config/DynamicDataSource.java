package com.brandslink.cloud.finance.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author yangzefei
 * @Classname DynamicDataSource
 * @Description 动态数据库
 * @Date 2019/9/4 10:54
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSource.class);


    @Override
    protected Object determineCurrentLookupKey() {
        log.debug("数据源为:{}", getDB());
        return getDB();
    }

    /**
     * 默认数据源
     */
    public static final String DEFAULT_DS = "finance";
    /**
     * 中心服务数据源
     */
    public static final String CENTER_DS = "center";

    private static final ThreadLocal<String> contextHolder =new InheritableThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return DEFAULT_DS;
        }
    };

    /**
     * 设置数据源名
     * @param dbName 数据源名称
     */
    public static void setDB(String dbName) {
        log.debug("切换到{}数据源", dbName);
        contextHolder.set(dbName);
    }

    /**
     * 获取数据源名
     * @return 返回数据源名称
     */
    public static String getDB() {
        return contextHolder.get();
    }

    /**
     * 清除数据源名
     */
    public static void clearDB() {
        contextHolder.remove();
    }


}
