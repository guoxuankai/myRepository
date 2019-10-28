package com.rondaful.cloud.seller.mapper;

import java.util.List;
import java.util.Map;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.seller.entity.amazon.AmazonReference;
import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.common.task.StatisticsPublishReport;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;

public interface AmazonPublishSubListingMapper extends BaseMapper<AmazonPublishSubListing> {
	int deleteByListingId(Long listingId);
	
	/**
	 *	 批量写入
	 * @param list
	 * @return
	 */
	Integer insertBatch(List<AmazonPublishSubListing> list);
	
	
	/**
	 *	 根据父id获取子表数据
	 * @param array
	 * 		父ids
	 * @return
	 */
	List<AmazonPublishSubListing> selectByListingId(Long array[]);
	
	Integer updateLoadTaskPulishSubBatch(Map<String,Object> map);
	
	/** 获取刊登需要获取报告结果的数据 */
	List<AmazonPublishSubListing> selectSubmitfeedIds();
	
	/** 默认submitfeed_id下所有的都是成功的 */
	Integer updatePulishDefaultSuccess(AmazonPublishSubListing amazonPublishSubListing);
	
	/** 更新非messageids为成功 */
	Integer updatePulishDefaultUNSuccessBatch(Map params);

	/**
	 * 查询要被更改的数据
	 * @param params 参数
	 * @return 结果
	 */
	List<AmazonPublishSubListing> getPublishDefaulteUNSecessBatch(Map params);
	
	
	/** 设置失败的数据 */
	Integer updatePulishErrorMsgByMessageId(AmazonPublishSubListing amazonPublishSubListing);
	
	/** 统计数据  */
	List<StatisticsPublishReport> selectStatisticsPublish();


	/** 对某个子项的统计数据  */
	StatisticsPublishReport selectStatisticsPublishBySku(AmazonPublishSubListing amazonPublishSubListing);
	
	
	/** 根据listing_id获取报告信息 */
	List<AmazonPublishSubListing> selectViewReportResultByListingId(AmazonPublishSubListing amazonPublishSubListing);
	
	/** 获取最后的一个messageid,在系统启动时加载，主要是防止 redis数据被删除导致这个messageid被重置*/
	Long selectLastMaxMessageId();


	/**
	 * 更新asin
	 */
	void updateAsinByListingIdAndSku(AmazonPublishSubListing sub);

	int updateByListingIdAndMsgType(AmazonPublishSubListing subListing);

	int updateByMsgTypeAndlistingId(Map<String, Object> map);

	int deleteByListingIdAndMsgType(@Param("listingId")String listingId, @Param("msgType")String msgType);

	List<AmazonPublishSubListing> findNoSkuSubListing();

	int updateBySubmitfeedIdSelective(AmazonPublishSubListing listing);
	
	int deleteByListingIdAndSku(Map<String, Object> map);

	/**
	 * 上架品连sku
	 * @param plSku s品连ku
	 * @return 结果
	 */
	int upPLSKU(@Param("plSku") String plSku);

	/**
	 * 下架品连sku
	 * @param plSku 品连sku
	 * @return 结果
	 */
	int downPLSKU(@Param("plSku") String plSku);

	void deleteBatch(Map<String, Object> map);

	void deleteByListingIdAndSkus(Map<String, Object> map);

	/**
	 * 根据map删除
	 * @param msgTypeArr,,,processStatus,,,listingId
	 * @return
	 */
	int deleteByMsgTypeArrProcessStatusListingIdMap(Map<String, Object> paramsMap);

    List<Map<String,Object>> countNotAsin();

	int deleteBatchBylistingIdProcessStatusMsgTypeParentType(AmazonPublishSubListing sub);

	List<AmazonReference> getAmazonReferenceByPage(AmazonPublishSubListing subListing);

    int tortPLSKU(@Param("plSku") String sku);
}
