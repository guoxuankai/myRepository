package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.seller.entity.AmazonPublishTemplateOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AmazonPublishTemplateOrderMapper {

    /**
     * 添加一个新的序列
     * @param order order
     */
    void insert(AmazonPublishTemplateOrder order);

    /**
     * 将一个序列加1
     * @param key key
     */
    int addValue1(@Param("key") String key);

    /**
     * 查询一个序列
     * @param key key
     * @return 返回值
     */
    AmazonPublishTemplateOrder findByKey(@Param("key") String key);

}
