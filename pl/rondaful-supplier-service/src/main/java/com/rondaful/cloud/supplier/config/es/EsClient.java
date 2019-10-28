package com.rondaful.cloud.supplier.config.es;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: xqq
 * @Date: 2019/7/25
 * @Description:
 */
@Configuration
public class EsClient {

    public static GenericObjectPool<RestHighLevelClient> client(){

        EsClientPoolFactory factory = new EsClientPoolFactory();
        return new GenericObjectPool(factory,poolConfig());
    }

    public static GenericObjectPoolConfig poolConfig(){
        GenericObjectPoolConfig poolConfig=new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(10);
        return poolConfig;
    }

    //@Bean("getClient")
    public static RestHighLevelClient getClient() {
        try {
            return EsClient.client().borrowObject();
        } catch (Exception e) {
            return null;
        }
    }

    //@Bean("closeClient")
    public static void closeClient(RestHighLevelClient levelClient){
        EsClient.client().returnObject(levelClient);
    }
}
