package com.rondaful.cloud.supplier.config;

import com.rondaful.cloud.supplier.config.strategy.CommonStrategy;
import com.rondaful.cloud.supplier.config.strategy.NumCommonStrategy;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.*;

import javax.sql.DataSource;
import java.util.*;

@Configuration
@MapperScan(basePackages = "com.rondaful.cloud.supplier.mapper", sqlSessionTemplateRef = "supplierSqlSessionTemplate")
public class DruidConfig {

    private static final int TX_METHOD_TIMEOUT = 5;//事务超时（秒）

    private static final String AOP_POINTCUT_EXPRESSION = "execution (* com.rondaful.cloud.supplier..service.*.*(..))";//事务切面

    @Value("${spring.supplier.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.supplier.datasource.url}")
    private String url;
    @Value("${spring.supplier.datasource.username}")
    private String userName;
    @Value("${spring.supplier.datasource.password}")
    private String passWord;
    @Value("${spring.supplier.datasource.maxOpenPreparedStatements}")
    private Integer maxOpen;
    @Value("${spring.supplier.datasource.maxActive}")
    private Integer maxActive;

    @Bean(name = "supplierDataSource")
    public DataSource setDataSource() throws Exception{
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().addAll(this.tableRules());
        shardingRuleConfig.setDefaultDataSourceName("supplier");
        return ShardingDataSourceFactory.createDataSource(this.createDataSourceMap(), shardingRuleConfig,new Properties());
    }


    @Bean(name = "supplierSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("supplierDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //bean.setTypeAliasesPackage("");
        bean.setConfigLocation(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis-config.xml")[0]);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        return bean.getObject();
    }


    @Bean(name = "supplierSqlSessionTemplate")
    public SqlSessionTemplate setSqlSessionTemplate(@Qualifier("supplierSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }


    @Bean("supplierTxManager")
    public PlatformTransactionManager annotationDrivenTransactionManager(@Qualifier("supplierDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }


    @Bean("supplierTxAdvice")
    public TransactionInterceptor txAdvice(@Qualifier("supplierTxManager")PlatformTransactionManager transactionManager) {
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        /*只读事务，不做更新操作*/
        RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
        readOnlyTx.setReadOnly(true);
        readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED );
        /*当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务*/
        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
        requiredTx.setRollbackRules(
                Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        //requiredTx.setTimeout(TX_METHOD_TIMEOUT);//先不做设置，采用系统默认
        Map<String, TransactionAttribute> txMap = new HashMap<>();
        txMap.put("add*", requiredTx);
        txMap.put("save*", requiredTx);
        txMap.put("insert*", requiredTx);
        txMap.put("update*", requiredTx);
        txMap.put("delete*", requiredTx);
        txMap.put("select*", readOnlyTx);
        txMap.put("get*", readOnlyTx);
        txMap.put("query*", readOnlyTx);
        txMap.put("find*", readOnlyTx);
        source.setNameMap(txMap);
        TransactionInterceptor txAdvice = new TransactionInterceptor(transactionManager, source);
        return txAdvice;
    }


    @Bean
    public Advisor txAdviceAdvisor(@Qualifier("supplierTxAdvice") TransactionInterceptor txAdvice) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        return new DefaultPointcutAdvisor(pointcut, txAdvice);
    }


    private Map<String,DataSource> createDataSourceMap(){
        Map<String,DataSource> result = new HashMap<>();
        BasicDataSource dataSource=new BasicDataSource();
        dataSource.setDriverClassName(this.driverClassName);
        dataSource.setUrl(this.url);
        dataSource.setUsername(this.userName);
        dataSource.setPassword(this.passWord);
        dataSource.setMaxOpenPreparedStatements(this.maxOpen);
        dataSource.setMaxActive(this.maxActive);
        result.put("supplier",dataSource);
        return result;
    }

    private List<TableRuleConfiguration> tableRules(){
        List<TableRuleConfiguration> result=new ArrayList<>(10);
        TableRuleConfiguration inventory=new TableRuleConfiguration("t_inventory");
        inventory.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("warehouse_id",new NumCommonStrategy()));

        TableRuleConfiguration skuMap=new TableRuleConfiguration("t_sku_warehouse_map");
        skuMap.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("pinlian_sku",new CommonStrategy()));

        TableRuleConfiguration commodity=new TableRuleConfiguration("t_commodity");
        commodity.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("pinlian_sku",new CommonStrategy()));

        TableRuleConfiguration commoditySkuMap=new TableRuleConfiguration("t_commodity_sku_map");
        commoditySkuMap.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("supplier_sku",new CommonStrategy()));

        result.add(inventory);
        result.add(skuMap);
        result.add(commodity);
        result.add(commoditySkuMap);
        return result;
    }

}