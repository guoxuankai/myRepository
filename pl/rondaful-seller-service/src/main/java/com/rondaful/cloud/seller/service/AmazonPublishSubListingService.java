package com.rondaful.cloud.seller.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rondaful.cloud.seller.common.task.AmazonReportListResult;
import com.rondaful.cloud.seller.common.task.StatisticsPublishReport;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;

public interface AmazonPublishSubListingService {
	/** 保存数据 */
	int save(AmazonPublishSubListing amazonPublishSubListing);
	
	/** 保存数据 */
	int insertBatch(List<AmazonPublishSubListing> amazonPublishSubListing);
	
	/**
	 * 查询
	 * @param amazonPublishSubListing
	 * @return
	 */
	AmazonPublishSubListing selectByPrimaryKey(Long primaryKey);
	
	int update(AmazonPublishSubListing t);
	/** 分页查询  */
	List<AmazonPublishSubListing> selectPage(AmazonPublishSubListing t);
	
	/**  根据主id删除子表数据 */
	int deleteForBaseId(Long listingId);
	
	
	/**
	 *	 根据父id获取子表数据
	 * @param array
	 * 		父ids
	 * @return
	 */
	List<AmazonPublishSubListing> selectByListingId(Long array[]);
	
	/**
	 *	 更新完成状态
	 * @param resultMessage
	 * @param completeStatus
	 * @param pubmitfeedId
	 * @param processStatus
	 * @return
	 */
	Integer updateLoadTaskPulishSubBatch(
			String resultMessage
			,Integer completeStatus
			,String pubmitfeedId
			,String processStatus
			,Long [] subIdsArr);
	
	
	/** 获取刊登需要获取报告结果的数据 */
	List<AmazonPublishSubListing> selectSubmitfeedIds();
	
	
	
	/** 默认submitfeed_id下所有的都是成功的 */
	boolean updatePulishDefaultSuccess(String submitfeedId,List<AmazonReportListResult> resultList,AmazonPublishSubListing sublisting);
	
	/** 设置失败的数据 */
	Integer updatePulishErrorMsgByMessageId(AmazonPublishSubListing amazonPublishSubListing);
	
	
	/** 统计数据  */
	List<StatisticsPublishReport> selectStatisticsPublish();
	
	
	/** 根据listing_id获取报告信息 */
	List<AmazonPublishSubListing> selectViewReportResultByListingId(Long listingId);
	
	
	/** 获取最后的一个messageid,在系统启动时加载，主要是防止 redis数据被删除导致这个messageid被重置*/
	Long selectLastMaxMessageId();


	/**
	 * 更新asin
	 */
	void updateAsinByListingIdAndSku(AmazonPublishSubListing sub);

	int updateByListingIdAndMsgType(AmazonPublishSubListing publishSubListing);

	int deleteByListingIdAndMsgType(String listingId, String msgType);


	List<AmazonPublishSubListing> findNoSkuSubListing();


	int updateByPrimaryKeySelective(AmazonPublishSubListing listing);
	
	public int updateBySubmitfeedIdSelective(AmazonPublishSubListing listing);
	
	int deleteByListingIdAndSku(Long listingId,String sku);

	void deleteBatchById(List<Long> ids);

	void deleteByListingIdAndSkus(Long id, Set<String> setSku);
	List<Map<String,Object>> countNotAsin();
	/**
	 * 根据map删除
	 * @param msgTypeArr,,,processStatus,,,listingId
	 * @return
	 */
	int deleteByMsgTypeArrProcessStatusListingIdMap(Map<String, Object> paramsMap);

	int deleteBatchBylistingIdProcessStatusMsgTypeParentType(AmazonPublishSubListing sub);

}
