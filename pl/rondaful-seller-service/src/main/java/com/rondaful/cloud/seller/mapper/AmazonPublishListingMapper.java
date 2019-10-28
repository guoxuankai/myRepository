package com.rondaful.cloud.seller.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.AmazonPublishListingMobile;
import com.rondaful.cloud.seller.entity.amazon.AmazonQueryLoadTaskResult;


public interface AmazonPublishListingMapper  extends BaseMapper<AmazonPublishListing>  {

    /**
     * 复制刊登
     *
     * @param id 被复制的id
     */
    Integer copyPublish(@Param("id") Long id);
    
    /** 根据批次号查询 */
    List<AmazonPublishListing> selectBybatchNo(String batchNo);
    
    /**
     *	 根据批次号删除数据
     * @param batchNo
     */
    public void deleteByBatchNo(String batchNo);

    /**
     * 查询移动端显示列表
     * @param model 卖家账号
     * @return 列表
     */
    List<AmazonPublishListingMobile> selectAllMobile(AmazonPublishListing  model);

    /**
     * 查询用户刊登成功过的亚马逊刊登总数
     * @param plAccount 卖家账号
     * @return 条数
     */
    Integer selectCount(@Param("plAccount") String plAccount);
    
    AmazonPublishListing selectOne(AmazonPublishListing t);
    AmazonPublishListing selectBySubmitfeedId(AmazonPublishListing t);
    /**
     * 	根据卖家id，站点分组，并且状态为等待刊登的前50条数据
     * @return
     * 	 AmazonQueryLoadTaskResult
     */
    List<AmazonQueryLoadTaskResult> selectLoadTaskPulish(Map<String, Object> map);
    
    int updateLoadTaskPulishBatch(Map<String,Object> params);

    /**
     *  查询某个亚马逊卖家某个商城下的某个平台sku的数据
     * @param listing 参数
     * @return 列表值
     */
    List<AmazonPublishListing> findList(AmazonPublishListing listing);

    /**
     * 根据亚马逊卖家ID和刊登站点查询在线的没有asin的刊登数据，返回结果中的平台sku是字表中的sku
     * @param listing 参数
     * @return 列表值
     */
    List<AmazonPublishListing> selectNoAsinList(AmazonPublishListing listing);
    
    /**
     * 根据ids批量查询
     * @param params
     * @return
     */
    List<AmazonPublishListing> getBatchByIds(Map<String,Object> params);

	int updateLoadTaskPulishBatchOnlineTime(Map<String, Object> paramsMap);


    /**
     * 查询亚马逊刊登的仓库ID列表
     * @return 仓库ID列表
     */
    ArrayList<Integer> getAmazonWarehouseIdList();

    /**
     * 通过仓库ID查询对应品连sku列表
     * @param warehouseId 仓库ID
     * @return 品连sku列表
     */
    ArrayList<String> getAmazonListPlSkuByWarehouseId(@Param("warehouseId") Integer warehouseId);

    /**
     * 通过asin查询其主数据
     * @param asin asin
     * @return 主数据列表
     */
    List<AmazonPublishListing> selectByAsin(@Param("asin") String asin );


    /**
     * 更新库存
     * @param msg 参数
     */
    void updatePlSkuCount(HashMap<String,String> msg);

    List<AmazonPublishListing> findListIfOnline(@Param("ids") Long[] ids);

    void updatelistingStatus(@Param("onlineIds") Long[] onlineIds,@Param("updateStatus") Integer updateStatus);

    /**
     * 将错误数据改为正确的
     */
    void updateDefaultToSuccess();

    /**
     * 将在线状态改为失败的
     */
    void updateSuccessToDefault();
 
	List<AmazonPublishListing> getByplatformSkuAndSite(Map<String, Object> map);
}