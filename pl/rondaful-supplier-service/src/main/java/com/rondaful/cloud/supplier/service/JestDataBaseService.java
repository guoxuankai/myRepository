package com.rondaful.cloud.supplier.service;


import java.util.List;



/**
 *  @author: xieyanbin
 *  @Date: 2019/7/16 2019/7/16
 *  @Description: es文档操作
 */
public interface JestDataBaseService<T> {

    /**
     * 单条删除文档
     */
    boolean deleteItem(String index, String type, String id);


    /**
     * 批量创建索引
     */
    void batchIndex(String index, String type, List<T> list);

    /**
     * 单条索引(新增/更新)
     */
    void singleIndex(String index, String type, T t);

    /**
     * 指定索引ID创建文档
     */
    void singleIndexWithId(String index, String type, String id, T t);

    /**
     * 根据id查询
     */
    T queryById(String index, String type, String id, Class<T> clazz);


    /**
    * @Description
    * @Author  xieyanbin
    * @Param
    * @Return
    * @Exception 批量插入
    *
    */
    void insertBulk();
}
