package com.rondaful.cloud.seller.service;

import java.util.*;

import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.seller.entity.amazon.AmazonReference;
import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.security.UserSession;
import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.seller.dto.CommodityDTO;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.AmazonPublishListingMobile;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.amazon.AmazonQueryLoadTaskResult;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.seller.vo.AmazonDisposePriceVO;
import com.rondaful.cloud.seller.vo.BatchUpdateVO;

public interface AmazonPublishListingService  extends BaseService<AmazonPublishListing>  {

    /**
     * 	复制刊登
     *
     * @param id 被复制的id
     */
    void copyPublish(@Param("id") Long id);
    
    /**
     * 保存数据
     * @param amazonPublishListing
     */
    AmazonPublishListing saveOrUpdate(AmazonRequestProduct<?> requestProduct, String loginUserId,Integer status,Empower empower, Date successTime );
    
    /** 根据批次号查询 */
    List<AmazonPublishListing> selectBybatchNo(String batchNo);
    
    void deleteByBatchNo(String batchNo);

    AmazonPublishListing selectByPrimaryKey(Long id);

    int  updateByPrimaryKeySelective(AmazonPublishListing amazonPublishListing);

    /**
     * 查询移动端显示列表
     * */
    Page<AmazonPublishListingMobile> selectAllMobile( AmazonPublishListing  model);

    /**
     * 查询用户刊登成功过的亚马逊刊登总数
     * @param plAccount 卖家账号
     * @return 条数
     */
    Integer selectCount(String plAccount);
    
    AmazonPublishListing selectOne(AmazonPublishListing t);
    
    AmazonPublishListing selectBySubmitfeedId(AmazonPublishListing t);
    
    /**
     * 	根据卖家id，站点分组，并且状态为等待刊登的前50条数据
     * @return
     * 	 AmazonQueryLoadTaskResult
     */
    List<AmazonQueryLoadTaskResult> selectLoadTaskPulish(Map<String, Object> map);

    int updateLoadTaskPulishBatch(Long ids[],Integer PublishListStatus,String remark);
    int updateLoadTaskPulishBatch(Long ids[],Integer PublishListStatus,String remark,boolean isUpdateType);

    /**
     *  查询某个亚马逊卖家某个商城下的某个平台sku的数据
     * @param listing 参数
     * @return 列表值
     */
    List<AmazonPublishListing> findList(AmazonPublishListing listing);

   /**
    * 批量更新
    * @param batchUpdateVO
    */
	void batchUpdate(BatchUpdateVO batchUpdateVO,String operatorName,Integer operatorId);

	void updateOnline(AmazonRequestProduct requestProduct,String operatorName,Integer operatorId);

    /**
     * 根据亚马逊卖家ID和刊登站点查询在线的没有asin的刊登数据，返回结果中的平台sku是字表中的sku
     * @param listing 参数
     * @return 列表值
     */
    List<AmazonPublishListing> selectNoAsinList(AmazonPublishListing listing);

    /**
     * 修改上线时间
     * @param paramsMap
     */
	void updateLoadTaskPulishBatchOnlineTime(Map<String, Object> paramsMap);


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
    Page<String> getAmazonListPlSkuByWarehouseId(Integer warehouseId);

    /**
     * 更新库存
     * @param msg 参数
     */
	void updatePlSkuCount(HashMap<String,String> msg);

	/**
	 * 计算最终售价
	 */
	List<CommodityDTO> disposePrice(AmazonDisposePriceVO amazonDisposePriceVO);

	/**
	 * 平台sku和站点查询
	 * @param platformSku
	 * @param site
	 * @return
	 */
	List<AmazonPublishListing> getByplatformSkuAndSite(List<String> platformSku,Integer empowerId);

    /**
     * 查看这个sku是否登录过
     * */
    List<AmazonPublishListing> findListIfOnline(Long[] ids);
    /**
     * 通过asin查询其主数据
     * @param asin asin
     * @return 主数据列表
     */
    List<AmazonPublishListing> selectByAsin(String asin );


    void updatelistingStatus(Long[] onlineIds, Integer updateSuccess);

    /**
     * 根据ids批量查询
     * @param params
     * @return
     */
    List<AmazonPublishListing> getBatchByIds(Map<String,Object> params);

    /**
     * 将错误数据改为正确的
     */
    void updateDefaultToSuccess();

    /**
     * 将在线状态改为失败的
     */
    void updateSuccessToDefault();


    /**
     * 查询引用列表
     * @param subListing 参数
     * @return 结果
     */
    Page<AmazonReference> getAmazonReferenceByPage(AmazonPublishSubListing subListing);


}
