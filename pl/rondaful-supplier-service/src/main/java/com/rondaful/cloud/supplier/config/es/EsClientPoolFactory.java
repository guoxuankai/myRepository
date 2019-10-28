package com.rondaful.cloud.supplier.config.es;

import com.rondaful.cloud.supplier.service.impl.DeliveryRecordServiceimpl;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: xqq
 * @Date: 2019/7/25
 * @Description:
 */
public class EsClientPoolFactory implements PooledObjectFactory<RestHighLevelClient> {

    private final static Logger logger = LoggerFactory.getLogger(EsClientPoolFactory.class);

    @Override
    public PooledObject<RestHighLevelClient> makeObject() throws Exception {
        RestHighLevelClient client=null;
        try {
            RestClientBuilder clientBuilder=RestClient.builder(
                    new HttpHost("47.107.181.119",9200,"http")
            );
            client=new RestHighLevelClient(clientBuilder);
        } catch (Exception e) {
            logger.error("es初始连接异常");
        }
        return new DefaultPooledObject<RestHighLevelClient>(client);
    }

    @Override
    public void destroyObject(PooledObject<RestHighLevelClient> pooledObject) throws Exception {
        RestHighLevelClient client=pooledObject.getObject();
        client.close();
    }

    @Override
    public boolean validateObject(PooledObject<RestHighLevelClient> pooledObject) {
        return true;
    }

    @Override
    public void activateObject(PooledObject<RestHighLevelClient> pooledObject) throws Exception {
        logger.info("我也不知道这个是干啥的::activateObject");
    }

    @Override
    public void passivateObject(PooledObject<RestHighLevelClient> pooledObject) throws Exception {
        logger.info("我也不知道这个是干啥的::passivateObject");
    }
}
