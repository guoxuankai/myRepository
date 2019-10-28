package com.brandslink.cloud.finance.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yangzefei
 * @Classname CenterDruidConfig
 * @Description 中心服务数据库配置
 * @Date 2019/9/4 13:46
 */
@Configuration
public class CenterDruidConfig {
    @Bean(name = "centerDataSource")
    @ConfigurationProperties(prefix = "spring.center.datasource")
    public DataSource setDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        return dataSource;
    }


    /**
     * 动态数据源: 通过AOP在不同数据源之间动态切换
     * @return
     */
    @Bean(name = "dynamicDataSource")
    public DataSource dataSource(@Qualifier("financeDataSource") DataSource finance,@Qualifier("centerDataSource") DataSource center) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        // 默认数据源
        dynamicDataSource.setDefaultTargetDataSource(finance);
        // 配置多数据源
        Map<Object, Object> dsMap = new HashMap(5);
        dsMap.put(DynamicDataSource.DEFAULT_DS, finance);
        dsMap.put(DynamicDataSource.CENTER_DS,center);
        dynamicDataSource.setTargetDataSources(dsMap);
        return dynamicDataSource;
    }
}
