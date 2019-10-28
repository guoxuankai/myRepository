package com.rondaful.cloud.supplier.service;


    /**
     *  @author: xieyanbin
     *  @Date: 2019/7/16 2019/7/16
     *  @Description: es索引操作
     */
public interface CommonJestIndexService {

	/**
     * 创建index
     */
    public void createIndex(String index);
    
    /**
     * 设置index的mapping
     */
    public void createIndexMapping(String index, String type, String mappingString);
    /**
     * 追加mapping字段
     *
     * @param index     index名称
     * @param type      type名称
     * @param fieldName 字段名称
     * @param fieldType 字段类型
     */
    public void addFieldMapping(String index, String type, String fieldName, String fieldType);
    
    /**
     * 获取index的mapping
     */
    public String getMapping(String indexName, String typeName);
    
    /**
     * 判断index是否存在
     */
    public boolean indexExist(String index);
    
    /**
     * 删除index
     */
    public void deleteIndex(String index);

    /**
     * 索引优化
     */
    public void optimizeIndex();
    
    /**
     * 清理缓存
     */
    public void clearCache();

}
