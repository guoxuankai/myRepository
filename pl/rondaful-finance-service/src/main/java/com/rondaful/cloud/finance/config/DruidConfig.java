package com.rondaful.cloud.finance.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DruidConfig {

    @Value("${spring.finance.datasource.name}")
    private String name;
    @Value("${spring.finance.datasource.url}")
    private String url;
    @Value("${spring.finance.datasource.username}")
    private String username;
    @Value("${spring.finance.datasource.password}")
    private String password;
    @Value("${spring.finance.datasource.type}")
    private String type;
    @Value("${spring.finance.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.finance.datasource.filters}")
    private String filters;
    @Value("${spring.finance.datasource.maxActive}")
    private int maxActive;
    @Value("${spring.finance.datasource.initialSize}")
    private int initialSize;
    @Value("${spring.finance.datasource.maxWait}")
    private long maxWait;
    @Value("${spring.finance.datasource.minIdle}")
    private int minIdle;
    @Value("${spring.finance.datasource.timeBetweenEvictionRunsMillis}")
    private long timeBetweenEvictionRunsMillis;
    @Value("${spring.finance.datasource.minEvictableIdleTimeMillis}")
    private long minEvictableIdleTimeMillis;
    @Value("${spring.finance.datasource.validationQuery}")
    private String validationQuery;
    @Value("${spring.finance.datasource.testWhileIdle}")
    private boolean testWhileIdle;
    @Value("${spring.finance.datasource.testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${spring.finance.datasource.testOnReturn}")
    private boolean testOnReturn;
    @Value("${spring.finance.datasource.poolPreparedStatements}")
    private boolean poolPreparedStatements;
    @Value("${spring.finance.datasource.maxOpenPreparedStatements}")
    private int maxOpenPreparedStatements;
    @Value("${spring.finance.datasource.numTestsPerEvictionRun}")
    private int numTestsPerEvictionRun;
    @Value("${spring.finance.datasource.keepAlive}")
    private boolean keepAlive;


    @Bean
    public DataSource druidDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setName(name);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setDbType(type);
        druidDataSource.setDriverClassName(driverClassName);
        druidDataSource.setFilters(filters);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        druidDataSource.setValidationQuery(validationQuery);
        druidDataSource.setTestWhileIdle(testWhileIdle);
        druidDataSource.setTestOnBorrow(testOnBorrow);
        druidDataSource.setTestOnReturn(testOnReturn);
        druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
        druidDataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
        druidDataSource.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        druidDataSource.setKeepAlive(keepAlive);
        return druidDataSource;
    }

}