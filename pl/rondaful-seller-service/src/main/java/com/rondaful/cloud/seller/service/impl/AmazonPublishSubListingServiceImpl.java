package com.rondaful.cloud.seller.service.impl;

import java.lang.reflect.Array;
import java.util.*;

import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.SellerSkuMap;
import com.rondaful.cloud.seller.mapper.AmazonPublishListingMapper;
import com.rondaful.cloud.seller.remote.RemoteCommodityService;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.seller.common.task.AmazonReportListResult;
import com.rondaful.cloud.seller.common.task.StatisticsPublishReport;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.seller.mapper.AmazonPublishSubListingMapper;
import com.rondaful.cloud.seller.service.AmazonPublishSubListingService;

@Service
public class AmazonPublishSubListingServiceImpl  
		extends BaseServiceImpl<AmazonPublishSubListing> implements AmazonPublishSubListingService {
	private final Logger logger = LoggerFactory.getLogger(AmazonPublishSubListingServiceImpl.class);
	@Autowired
	AmazonPublishSubListingMapper amazonPublishSubListingMapper;

	@Autowired
	AmazonPublishListingMapper amazonPublishListingMapper;

	@Autowired
	RemoteCommodityService remoteCommodityService;

	@Autowired
	AuthorizationSellerService authorizationSellerService;
	
	@Override
	public int save(AmazonPublishSubListing amazonPublishSubListing) {
		return amazonPublishSubListingMapper.insert(amazonPublishSubListing);
		
	}

	@Override
	public AmazonPublishSubListing selectByPrimaryKey(Long primaryKey) {
		return amazonPublishSubListingMapper.selectByPrimaryKey(primaryKey);
	}

	@Override
	public int update(AmazonPublishSubListing t) {
		return amazonPublishSubListingMapper.updateByPrimaryKey(t);
	}

	@Override
	public List<AmazonPublishSubListing> selectPage(AmazonPublishSubListing t) {
		return amazonPublishSubListingMapper.page(t);
	}

	@Override
	public int deleteForBaseId(Long listingId) {
		return amazonPublishSubListingMapper.deleteByListingId(listingId);
	}

	@Override
	public int insertBatch(List<AmazonPublishSubListing> amazonPublishSubListing) {
		return amazonPublishSubListingMapper.insertBatch(amazonPublishSubListing);
	}

	@Override
	public List<AmazonPublishSubListing> selectByListingId(Long[] array) {
		return amazonPublishSubListingMapper.selectByListingId(array);
	}

	@Override
	public Integer updateLoadTaskPulishSubBatch(String resultMessage, Integer completeStatus, String pubmitfeedId,
			String processStatus,Long [] subIdsArr) {
		Map<String,Object> params = new HashMap<>();
		params.put("resultMessage", resultMessage);
		params.put("completeStatus", completeStatus);
		params.put("pubmitfeedId", pubmitfeedId);
		params.put("processStatus", processStatus);
		params.put("ids", subIdsArr);
		return amazonPublishSubListingMapper.updateLoadTaskPulishSubBatch(params);
	}

	@Override
	public List<AmazonPublishSubListing> selectSubmitfeedIds() {
		return amazonPublishSubListingMapper.selectSubmitfeedIds();
	}

	@Override
	// @Transactional(rollbackFor= Exception.class)
	public boolean updatePulishDefaultSuccess(String submitfeedId,List<AmazonReportListResult> resultList,AmazonPublishSubListing sublisting) {
		if(StringUtils.isBlank(submitfeedId) )
		{
			return Boolean.FALSE;
		}
		
		AmazonPublishSubListing _tempAmazonPublishSubListing = null;
		Set<Long> errorMessageIds = new HashSet<Long>();
		boolean isException = Boolean.TRUE; // 是否有异常,默认没异常
		
		//全部成功
		
		
		for(AmazonReportListResult res : resultList)
		{
			logger.debug("返回结果：{}",JSON.toJSONString(res));
			
			if(res.getMessageId() == null)
			{
				logger.debug("获取报告成功，无错误..。");
				break;
			}
			if(res.getMessageId() < 0) // message小于0的说明是请求异常，请求异常了，就不需要继续了。
			{
				logger.error("有异常的数据报告请求：{}",JSON.toJSONString(res));
				isException = Boolean.FALSE;
				break;
			}
			 if(res.getMessageId() != null &&  res.getMessageId() > 0) //messageid为空。说明整个报告都没有错误消息。
			 {
				 errorMessageIds.add(res.getMessageId());
				// res.getResultCode() 可能有警告
				_tempAmazonPublishSubListing = new AmazonPublishSubListing();
				_tempAmazonPublishSubListing.setResultMessage("["+res.getResultCode()+"]("+res.getResultMessageCode()+")"+res.getResultDescription());
				_tempAmazonPublishSubListing.setCompleteStatus("Error".equalsIgnoreCase(res.getResultCode()) ? 
						AmazonConstants.COMPLETE_STATUS_FAILED : AmazonConstants.COMPLETE_STATUS_COMPLETE);
				_tempAmazonPublishSubListing.setSubmitfeedId(sublisting.getSubmitfeedId());
				_tempAmazonPublishSubListing.setMessageId(res.getMessageId());
				_tempAmazonPublishSubListing.setMerchantId(sublisting.getMerchantId());
				_tempAmazonPublishSubListing.setMarketplaceId(sublisting.getMarketplaceId());
				amazonPublishSubListingMapper.updatePulishErrorMsgByMessageId(_tempAmazonPublishSubListing);
			 }
		}
		// 更新同一个submitfeedId非错误的为成功
		if(isException) //没异常
		{
			if(errorMessageIds.isEmpty()) // 没有任何错误报告
			{
				logger.debug("submitfeedId：{} 无错误，全部成功",submitfeedId);
				errorMessageIds.add(-1L); // 更新非-1的数据为成功
			}
			Map params = new HashMap();
			params.put("ids", errorMessageIds);
			params.put("submitfeedId", submitfeedId);
			params.put("merchantId", sublisting.getMerchantId());
			params.put("marketplaceId", sublisting.getMarketplaceId());
			List<AmazonPublishSubListing> batchs = amazonPublishSubListingMapper.getPublishDefaulteUNSecessBatch(params);   // todo 需要推送sku映射的数据
			if(batchs != null && batchs.size() > 0 ) {
				SellerSkuMap addmap;
				Empower empower;
				AmazonPublishListing listing;
				ArrayList<SellerSkuMap> voList = new ArrayList<>();
				for(AmazonPublishSubListing sub : batchs){
					try {
						listing = amazonPublishListingMapper.selectByPrimaryKey(sub.getListingId());
						empower = new Empower();
						empower.setStatus(1);
						empower.setPinlianAccount(listing.getPlAccount());
						empower.setWebName(sublisting.getMarketplaceId());
						empower.setThirdPartyName(sublisting.getMerchantId());
						empower.setPlatform(2);
						empower = authorizationSellerService.selectAmazonAccount(empower);
						addmap = new SellerSkuMap();
						addmap.setAuthorizationId(String.valueOf(empower.getEmpowerId()));
						addmap.setPlatform(OrderRuleEnum.platformEnm.AMAZON.getPlatform());
						addmap.setPlatformSku(sub.getSku());
						addmap.setSkuGroup(sub.getPlSku() + ":" + (sub.getPlSkuSaleNum() == null?1:sub.getPlSkuSaleNum()));
						voList.add(addmap);
					}catch (Exception e){
						logger.error("亚马逊刊登添加sku映射生成数据异常",e);
					}
				}
				if(voList.size() > 0){
					remoteCommodityService.addSkuMap(voList);
				}
			}
			amazonPublishSubListingMapper.updatePulishDefaultUNSuccessBatch(params);
		}
		return Boolean.TRUE;
	}
	

	@Override
	public Integer updatePulishErrorMsgByMessageId(AmazonPublishSubListing amazonPublishSubListing) {
		return amazonPublishSubListingMapper.updatePulishErrorMsgByMessageId(amazonPublishSubListing);
	}

	@Override
	public List<StatisticsPublishReport> selectStatisticsPublish() {
		// TODO Auto-generated method stub
		return amazonPublishSubListingMapper.selectStatisticsPublish();
	}

	@Override
	public List<AmazonPublishSubListing> selectViewReportResultByListingId(Long listingId) {
		AmazonPublishSubListing params = new AmazonPublishSubListing();
		params.setListingId(listingId);
		return amazonPublishSubListingMapper.selectViewReportResultByListingId(params);
	}

	@Override
	public Long selectLastMaxMessageId() {
		return amazonPublishSubListingMapper.selectLastMaxMessageId();
	}

	@Override
	public void updateAsinByListingIdAndSku(AmazonPublishSubListing sub) {
		amazonPublishSubListingMapper.updateAsinByListingIdAndSku(sub);
	}

	public int updateByListingIdAndMsgType(AmazonPublishSubListing publishSubListing) {
		return amazonPublishSubListingMapper.updateByListingIdAndMsgType(publishSubListing);
	}

	@Override
	public int deleteByListingIdAndMsgType(String listingId, String msgType) {
		
		return amazonPublishSubListingMapper.deleteByListingIdAndMsgType(listingId,msgType);
	}

	@Override
	public List<AmazonPublishSubListing> findNoSkuSubListing() {
		return amazonPublishSubListingMapper.findNoSkuSubListing();
	}

	@Override
	public int updateByPrimaryKeySelective(AmazonPublishSubListing listing){
		return amazonPublishSubListingMapper.updateByPrimaryKeySelective(listing);
	}
	
	@Override
	public int updateBySubmitfeedIdSelective(AmazonPublishSubListing listing){
		return amazonPublishSubListingMapper.updateBySubmitfeedIdSelective(listing);
	}
	
	public int deleteByListingIdAndSku(Long listingId,String sku) {
		Map<String, Object> map=new HashMap<>();
		map.put("listingId", listingId);
		map.put("sku", sku);
		return amazonPublishSubListingMapper.deleteByListingIdAndSku(map);
	}

	@Override
	public void deleteBatchById(List<Long> ids) {
		Map<String, Object> map =new HashMap<>();
		map.put("ids",ids);
		amazonPublishSubListingMapper.deleteBatch(map);
	}

	@Override
	public void deleteByListingIdAndSkus(Long id, Set<String> setSku) {
		Map<String, Object> map =new HashMap<>();
		map.put("skus",setSku);
		map.put("listingId",id);
		amazonPublishSubListingMapper.deleteByListingIdAndSkus(map);
	}

	@Override
	public List<Map<String, Object>> countNotAsin() {
		return amazonPublishSubListingMapper.countNotAsin();
	}

	@Override
	public int deleteByMsgTypeArrProcessStatusListingIdMap(Map<String, Object> paramsMap) {
		return amazonPublishSubListingMapper.deleteByMsgTypeArrProcessStatusListingIdMap(paramsMap);
	}

	@Override
	public int deleteBatchBylistingIdProcessStatusMsgTypeParentType(AmazonPublishSubListing sub) {
		// TODO Auto-generated method stub
		return amazonPublishSubListingMapper.deleteBatchBylistingIdProcessStatusMsgTypeParentType(sub);
	}

}
