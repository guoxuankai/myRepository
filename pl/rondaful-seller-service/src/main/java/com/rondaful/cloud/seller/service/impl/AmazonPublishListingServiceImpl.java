
package com.rondaful.cloud.seller.service.impl;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.rondaful.cloud.seller.common.task.StatisticsPublishReport;
import com.rondaful.cloud.seller.constants.AmazonPostMethod;
import com.rondaful.cloud.seller.constants.AmazonPublishUpdateStatus;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.entity.amazon.AmazonReference;
import com.rondaful.cloud.seller.enums.AmazonPublishEnums;
import com.rondaful.cloud.seller.enums.PublishLogEnum;
import com.rondaful.cloud.seller.service.AmazonPublishReportDetailService;
import com.rondaful.cloud.seller.service.AmazonPublishReportTimeService;
import com.rondaful.cloud.seller.service.PublishLogService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.seller.common.task.ProcessXmlBatchUpdateTask;
import com.rondaful.cloud.seller.common.task.StatisticsPublishReport;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.seller.constants.AmazonPostMethod;
import com.rondaful.cloud.seller.dto.CommodityDTO;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.AmazonPublishListingMobile;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.amazon.AmazonPublishListStatus;
import com.rondaful.cloud.seller.entity.amazon.AmazonQueryLoadTaskResult;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.seller.enums.AmazonPublishEnums;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.generated.ProductImage;
import com.rondaful.cloud.seller.mapper.AmazonPublishListingMapper;
import com.rondaful.cloud.seller.mapper.AmazonPublishSubListingMapper;
import com.rondaful.cloud.seller.mapper.EmpowerMapper;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.utils.AmazonBachUpdate;
import com.rondaful.cloud.seller.utils.ComputeTemplateUtil;
import com.rondaful.cloud.seller.vo.AmazonDisposePriceVO;
import com.rondaful.cloud.seller.vo.BatchUpdateVO;


@Service
public class AmazonPublishListingServiceImpl extends BaseServiceImpl<AmazonPublishListing> implements AmazonPublishListingService {


	private final Logger logger = LoggerFactory.getLogger(AmazonPublishListingServiceImpl.class);
	
	@Autowired
    private AmazonPublishListingMapper amazonPublishListingMapper;
	
	@Autowired
    private EmpowerMapper empowerMapper;

	@Autowired
	private RedisUtils redisUtils;

	@Autowired
	private AmazonPublishSubListingMapper amazonPublishSubListingMapper;
	
	@Autowired
	private ComputeTemplateUtil computeTemplateUtil;

	@Autowired
	private AmazonPublishReportDetailService amazonPublishReportDetailService;

	@Autowired
	private AmazonPublishReportTimeService amazonPublishReportTimeService;
	@Autowired
	private PublishLogService publishLogService;

	
    @Autowired
    public AmazonPublishListingServiceImpl(AmazonPublishListingMapper amazonPublishListingMapper) {
        this.amazonPublishListingMapper = amazonPublishListingMapper;
    }

    @Override
    public void copyPublish(Long id) {
    	//select 
    	
        amazonPublishListingMapper.copyPublish(id);
    }

	@Override
	public AmazonPublishListing saveOrUpdate(AmazonRequestProduct<?> requestProduct, String loginUserName,Integer status,Empower empower ,Date successTime)  {
		//AmazonEnvelope amazonEnvelope = ClassXmlUtil.xmlToBean(amazonEnvelopeXml, AmazonEnvelope.class);
		logger.debug("刊登saveOrUpdate相关参数",JSON.toJSONString(requestProduct));
		if(requestProduct.getSearchTerms() != null && requestProduct.getSearchTerms().size() >0){
			requestProduct.setSearchTerms(requestProduct.getSearchTerms().stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()));
		}
		 
		requestProduct.setIsMultiattribute(Boolean.FALSE);
		if(CollectionUtils.isNotEmpty(requestProduct.getVarRequestProductList()))
		{
			requestProduct.setIsMultiattribute(Boolean.TRUE);
		}
		
		AmazonPublishListing amazonPublishListing = new AmazonPublishListing();
		if(requestProduct.getHasRequired() != null) {
			amazonPublishListing.setHasRequired(requestProduct.getHasRequired());
		}
		amazonPublishListing.setSaleUserId(requestProduct.getSaleUserId());
		
		amazonPublishListing.setPlAccount(loginUserName);
		amazonPublishListing.setPlatformSku(requestProduct.getSku());
		amazonPublishListing.setPlSku(requestProduct.getPlSku());
		amazonPublishListing.setPublishAccount(empower.getAccount());
		amazonPublishListing.setPublishSite(requestProduct.getCountryCode());
		amazonPublishListing.setMerchantIdentifier(empower.getThirdPartyName());
		amazonPublishListing.setSuccessTime(successTime == null ? null : successTime);
		amazonPublishListing.setLogisticsCode(requestProduct.getLogisticsCode());
		if(status != null)
		{
			amazonPublishListing.setPublishStatus(status);
		}
		amazonPublishListing.setPublishType(requestProduct.getIsMultiattribute() ? 1 : 2);
		amazonPublishListing.setTitle(requestProduct.getTitle());
		amazonPublishListing.setVersionData(requestProduct.getVersion());
		amazonPublishListing.setBatchNo(requestProduct.getBatchNo());
		amazonPublishListing.setAmwToken(empower.getToken());
		amazonPublishListing.setProductImage(CollectionUtils.isEmpty(requestProduct.getImages()) ? null : requestProduct.getImages().get(0).getImageLocation());
		amazonPublishListing.setUpdateTime(new Date());
		/*amazonPublishListing.setRemark(StringUtils.isNotBlank(requestProduct.getTemplatesName()) && StringUtils.isNotBlank(requestProduct.getTemplatesName2()) ?
				"["+requestProduct.getTemplatesName()+"-"+requestProduct.getTemplatesName2()+"]" : "草稿数据");*/
		amazonPublishListing.setExt(requestProduct.getExt());
		requestProduct.setExt(null);
		amazonPublishListing.setLogisticsType(requestProduct.getLogisticsType());
		amazonPublishListing.setWarehouseId(requestProduct.getWarehouseId());
		// 如果存在批次号，则认为是被编辑,否则是创创
		if(StringUtils.isNotBlank(amazonPublishListing.getBatchNo()))
		{
			//amazonPublishListing.setPublishMessage(JSON.toJSONString(requestProduct));
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			String publishMessageJson="";
			try {
				publishMessageJson=mapper.writeValueAsString(requestProduct);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			amazonPublishListing.setPublishMessage(publishMessageJson);
			List<AmazonPublishListing> list = amazonPublishListingMapper.selectBybatchNo(amazonPublishListing.getBatchNo());
			if(CollectionUtils.isEmpty(list)) // 有可能数据被保存并刊登时
			{
				/*if(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT == status) // 如果
				{*/
				String batchNo = UUID.randomUUID().toString();
				amazonPublishListing.setBatchNo(batchNo);
				amazonPublishListing.setCreateTime(new Date());
				requestProduct.setBatchNo(batchNo);
				//amazonPublishListing.setPublishMessage(JSON.toJSONString(requestProduct)); //这个本来应放在if前的，但需要设置批次号后才能生成json
				ObjectMapper objMapper = new ObjectMapper();
				objMapper.setSerializationInclusion(Include.NON_NULL);
				String publishMessage="";
				try {
					publishMessage=objMapper.writeValueAsString(requestProduct);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				amazonPublishListing.setPublishMessage(publishMessage);
				amazonPublishListing.setPublishStatus(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT);
				amazonPublishListing.setRemark("数据丢失，补偿数据为草稿");
				
				amazonPublishListingMapper.insertSelective(amazonPublishListing);
				list = amazonPublishListingMapper.selectBybatchNo(amazonPublishListing.getBatchNo());
				amazonPublishListing = list.get(0);
				/*}else {
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601, "操作数据失败，数据可能丢失");
				}*/
				// 
			}else
			{
				AmazonPublishListing tempObj = list.get(0);
				amazonPublishListing.setId(tempObj.getId());
				amazonPublishListingMapper.updateByPrimaryKeySelective(amazonPublishListing);
			}
		}else //新增数据
		{
			String batchNo = UUID.randomUUID().toString();
			amazonPublishListing.setBatchNo(batchNo);
			amazonPublishListing.setCreateTime(new Date());
			requestProduct.setBatchNo(batchNo);
			//amazonPublishListing.setPublishMessage(JSON.toJSONString(requestProduct)); //这个本来应放在if前的，但需要设置批次号后才能生成json
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			String publishMessageJson="";
			try {
				publishMessageJson=mapper.writeValueAsString(requestProduct);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			amazonPublishListing.setPublishMessage(publishMessageJson);
			//amazonPublishListing.setSuccessTime(new Date());
			amazonPublishListingMapper.insertSelective(amazonPublishListing);
		}
		return amazonPublishListingMapper.selectByPrimaryKey(amazonPublishListing.getId());
	}

	public static void main(String[] args) {
		AmazonRequestProduct requestProduct = new AmazonRequestProduct();
		List<ProductImage> images = new ArrayList<>();
		ProductImage image = new ProductImage();
		image.setImageLocation("http://xxxx");
		image.setImageType("main");
		image.setSKU("esdfwefe");
		images.add(image);
		
		
		requestProduct.setImages(images);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);  
        String mapJakcson;
		try {
			mapJakcson = mapper.writeValueAsString(requestProduct);
			System.out.println(mapJakcson);
		 String temp = JSONObject.toJSONString(requestProduct);
		 System.out.println(temp);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String temp = JSONObject.toJSONString(requestProduct);


	}
	
	@Override
	public List<AmazonPublishListing> selectBybatchNo(String batchNo) {
		return amazonPublishListingMapper.selectBybatchNo(batchNo);
	}

	@Override
	public void deleteByBatchNo(String batchNo) {
		amazonPublishListingMapper.deleteByBatchNo(batchNo);
		
	}

	@Override
	public AmazonPublishListing selectByPrimaryKey(Long id) {
		AmazonPublishListing listing = amazonPublishListingMapper.selectByPrimaryKey(id);
		// 7.27tkx注释
		//if(listing.getPublishStatus().equals(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_FAIL)){
			String s = this.setListingSubStatus(listing.getPublishMessage(), listing.getId(),listing.getPublishStatus(),listing);
			listing.setPublishMessage(s);
		//}
		return listing;

	}
	@Override
	public int updateByPrimaryKeySelective(AmazonPublishListing t)  {
		return amazonPublishListingMapper.updateByPrimaryKeySelective(t);
	}

	@Override
	public Page<AmazonPublishListingMobile> selectAllMobile(AmazonPublishListing  model) {
		List<AmazonPublishListingMobile> list = amazonPublishListingMapper.selectAllMobile(model);
		PageInfo<AmazonPublishListingMobile> pageInfo = new PageInfo(list);
		return new Page(pageInfo);
	}

	@Override
	public Integer selectCount(String plAccount) {
		return amazonPublishListingMapper.selectCount(plAccount);
	}

	@Override
	public AmazonPublishListing selectOne(AmazonPublishListing t) {
		// TODO Auto-generated method stub
		return amazonPublishListingMapper.selectOne(t);
	}

	@Override
	public int deleteByPrimaryKey(Long primaryKey) {
		return amazonPublishListingMapper.deleteByPrimaryKey(primaryKey);
	}

	@Override
	public int insert(AmazonPublishListing t) {
		return amazonPublishListingMapper.insert(t);
	}

	@Override
	public int insertSelective(AmazonPublishListing t) {
		return amazonPublishListingMapper.insertSelective(t);
	}

	@Override
	public int updateByPrimaryKey(AmazonPublishListing t) {
		return amazonPublishListingMapper.updateByPrimaryKey(t);
	}

	@Override
	public List<AmazonQueryLoadTaskResult> selectLoadTaskPulish(Map<String, Object> map) {
		return amazonPublishListingMapper.selectLoadTaskPulish(map);
	}

	/**
	 * 包括修改在线时间onlineTime
	 */
	@Override
	public int updateLoadTaskPulishBatch(Long[] ids, Integer publishStatus,String remark) {
		Map<String,Object> params = new HashMap<>();
		params.put("ids", ids);
		params.put("publishStatus", publishStatus);
		params.put("remark", remark);
		return amazonPublishListingMapper.updateLoadTaskPulishBatch(params);
	}
	@Override
	public int updateLoadTaskPulishBatch(Long[] ids, Integer publishStatus,String remark,boolean isNeedUpdate) {
		if(isNeedUpdate) {
			return 0;
		}
		return updateLoadTaskPulishBatch( ids,  publishStatus, remark) ;
	}



	@Override
	public List<AmazonPublishListing> findList(AmazonPublishListing listing) {
		return amazonPublishListingMapper.findList(listing);
	}
	
	
	
	@Override
	public void batchUpdate(BatchUpdateVO batchUpdateVO,String operatorName,Integer operatorId) {
		logger.info("------------batchUpdate()相关参数------------",JSON.toJSONString(batchUpdateVO));
		String[] ids = batchUpdateVO.getIds().split(",");
		//1文本  0百分比
		int isText=0;
		boolean createProductXml=false;//是否生成产品xml
		boolean createPriceXml=false;//是否生成价格xml
		boolean createInventoryXml=false;//是否生成库存xml
		
		Map<String, Object> map =new HashMap<>();
		map.put("ids", ids);
		List<AmazonPublishListing> list = amazonPublishListingMapper.getBatchByIds(map);
		if(CollectionUtils.isEmpty(list)) {
			logger.warn("--------batchUpdate()-----批量查询数据为空");
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"id不能为空");
		}
		
		String updateItemsJson = batchUpdateVO.getUpdateItemsJson();
		List<JSONObject> jsonArray = JSONArray.parseArray(updateItemsJson, JSONObject.class);
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
		for (AmazonPublishListing publishListing : list) {
			for (JSONObject jsonObject : jsonArray) {
				if(publishListing.getId().longValue()==jsonObject.getLongValue("listingId")) {
					//选择需要修改的sku
				List<String> skus =Arrays.asList(jsonObject.getString("skus").split(","));
			AmazonPublishListing update=new AmazonPublishListing();
			AmazonRequestProduct requestProduct = JSON.parseObject(publishListing.getPublishMessage(), AmazonRequestProduct.class);
			Integer publishType = publishListing.getPublishType();
			
			update.setId(publishListing.getId());
			
			if(!StringUtils.isNotBlank(requestProduct.getStandardPriceUnit())) {
				AmazonBachUpdate.SetStandardPriceUnit(requestProduct, publishType, batchUpdateVO.getStandardPriceUnit(),skus,
						batchUpdateVO.getStatus());
			}
			 
			if(StringUtils.isNotBlank(batchUpdateVO.getListingDesc())) {
				update.setRemark(batchUpdateVO.getListingDesc());
			}
			if(StringUtils.isNotBlank(batchUpdateVO.getOriginalRatioJsonStr())) {
				createPriceXml=true;
				//原价价格百分比处理
				requestProduct = AmazonBachUpdate.disposeStandardPrice(batchUpdateVO.getOriginalRatioJsonStr(),
						requestProduct,publishType,isText,skus,batchUpdateVO.getStatus());
				
			}
			if(StringUtils.isNotBlank(batchUpdateVO.getReplaceJsonStr())) {
				createProductXml=true;
				requestProduct=AmazonBachUpdate.setReplaceTitle(requestProduct,publishType,batchUpdateVO.getReplaceJsonStr(),skus,batchUpdateVO.getStatus());
				
			}
			if(StringUtils.isNotBlank(batchUpdateVO.getTitleBeforeText())) {
				createProductXml=true;
				requestProduct=AmazonBachUpdate.setAppendTitle(requestProduct,publishType,batchUpdateVO.getTitleBeforeText(),"BEFORE",skus,batchUpdateVO.getStatus());
				
			}
			if(StringUtils.isNotBlank(batchUpdateVO.getTitleAfterText())) {
				createProductXml=true;
				requestProduct=AmazonBachUpdate.setAppendTitle(requestProduct,publishType,batchUpdateVO.getTitleAfterText(),"AFTER",skus,batchUpdateVO.getStatus());
			}
			if(StringUtils.isNotBlank(batchUpdateVO.getOriginalTextJsonStr())) {
				createPriceXml=true;
				isText=1;
				//原价价格文本方式处理
				requestProduct = AmazonBachUpdate.disposeStandardPrice(batchUpdateVO.getOriginalTextJsonStr(),
						requestProduct,publishType,isText,skus,batchUpdateVO.getStatus());
				
			}
			if(StringUtils.isNotBlank(batchUpdateVO.getPriceValue())) {
				createPriceXml=true;
				requestProduct = AmazonBachUpdate.setPriceValue(requestProduct,publishType,batchUpdateVO.getPriceValue(),null,skus,batchUpdateVO.getStatus(),"batchUpdate");
			}
			if(batchUpdateVO.getQuantityNum() != null ) {
				createInventoryXml=true;
				requestProduct = AmazonBachUpdate.setQuantityNum(requestProduct,publishType,batchUpdateVO.getQuantityNum(),null,skus,batchUpdateVO.getStatus(),"batchUpdate");
			}
			update.setTitle(requestProduct.getTitle());
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			String publishMessageJson="";
			try {
				publishMessageJson=mapper.writeValueAsString(requestProduct);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} 
			update.setPublishMessage(publishMessageJson);
			
			MarketplaceId marketplaceId = MarketplaceIdList.createMarketplaceForKeyId().get(requestProduct.getCountryCode());
			if(marketplaceId == null)
			{
				marketplaceId = MarketplaceIdList.createMarketplace().get(requestProduct.getCountryCode());
			}
			requestProduct.setCountryCode(marketplaceId.getCountryCode());
			if(publishListing.getPublishStatus() == AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT)
			{
				logger.debug("更新了草稿数据，id={}",publishListing.getId());
				amazonPublishListingMapper.updateByPrimaryKeySelective(update);
				continue;
			}
			if(publishListing.getPublishStatus() != AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE
					&& publishListing.getPublishStatus() != AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_REST_PUSH) 
			{
				logger.warn("不能更新非在线及下线的数据:id={}",publishListing.getId());
				continue;
			}
			//在线状态去掉没有被修改的sku
			AmazonBachUpdate.deleteBysku(requestProduct,skus);
			//处理完成之后的AmazonMessage数据
			logger.debug("处理完成之后的AmazonMessage数据",JSON.toJSONString(requestProduct));
			try {
				if(!createProductXml&&!createPriceXml&&!createInventoryXml) {
					logger.info("是否生成XML------createProductXml={}createPriceXml={}createInventoryXml={}",
				createProductXml,createPriceXml,createInventoryXml);
				}else {
					StringBuilder errorFuture = new StringBuilder();
					ProcessXmlBatchUpdateTask processXmlTask = new ProcessXmlBatchUpdateTask(requestProduct,createProductXml,createPriceXml,createInventoryXml);
		            Future<String> futureResult = executor.submit(processXmlTask);
		            String errorMsg = futureResult.get();
					if(StringUtils.isNotEmpty(errorMsg))
					{
						errorFuture.append(errorMsg);
					}
					
					if(errorMsg.length() > 0) // 如果有错误，以异常方式返回错误信息
					{
						logger.warn("解释xml时出现错误，ID为：{}",publishListing.getId());
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601,errorFuture.toString());
					}
				}
				//7.25添加一个修改字段
				update.setUpdateStatus(AmazonPublishUpdateStatus.UPDATE_GOING);
				//生成完子表数据才去更新主表数据
				update.setPublishStatus(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_REST_PUSH);
				update.setUpdateStatus(AmazonPublishUpdateStatus.UPDATE_GOING);
				int j = amazonPublishListingMapper.updateByPrimaryKeySelective(update);
				logger.info("------批量更新————更新amazonPublishListing行数{}------",j);
				publishLogService.insert(publishMessageJson, PublishLogEnum.EDIT, operatorId, operatorName, publishListing.getId());
			} catch (Exception e) {
				logger.error("--------batchUpdate()--------PublishSubListing生成XML错误---------",e.getMessage());
				e.printStackTrace();
			}
		}
	}
		}
		executor.shutdown();
	}


	
	@Override
	public void updateOnline(AmazonRequestProduct requestProduct,String operatorName,Integer operatorId) {
		requestProduct.setExt(null); 
		logger.info("------updateOnline()相关参数------"+JSON.toJSONString(requestProduct));
		AmazonPublishListing update=new AmazonPublishListing();
		Long id = requestProduct.getId();
		AmazonPublishListing publishListing = amazonPublishListingMapper.selectByPrimaryKey(id);
		if(publishListing == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601,"------在线更新————更新AmazonPublishListing对象为空");
		}
		AmazonRequestProduct productMessage = JSON.parseObject(publishListing.getPublishMessage(),AmazonRequestProduct.class);
		
		productMessage = AmazonBachUpdate.setPriceValue(productMessage, publishListing.getPublishType(), requestProduct.getStandardPrice()==null ? "0" :requestProduct.getStandardPrice().toString(),
				requestProduct,null,null,"updateOnline");
		
		productMessage = AmazonBachUpdate.setQuantityNum(productMessage,publishListing.getPublishType(),requestProduct.getQuantity(),requestProduct,null,null,"updateOnline");
		
		productMessage = AmazonBachUpdate.setTitle(productMessage, publishListing.getPublishType(),requestProduct.getTitle(),requestProduct);
		
		productMessage.setBulletPoint(requestProduct.getBulletPoint());
		productMessage.setSearchTerms(requestProduct.getSearchTerms());
		
		productMessage.setConditionInfo(requestProduct.getConditionInfo());
		productMessage.setDescription(requestProduct.getDescription());
		if(publishListing.getPublishType() == AmazonConstants.PUBLISH_TYPE_ONLY) {
			//单属性
			update.setTitle(productMessage.getTitle());
		}
		MarketplaceId marketplaceId = MarketplaceIdList.createMarketplaceForKeyId().get(requestProduct.getCountryCode());
		if(marketplaceId == null)
		{
			marketplaceId = MarketplaceIdList.createMarketplace().get(requestProduct.getCountryCode());
		}
		requestProduct.setCountryCode(marketplaceId.getCountryCode());
		requestProduct=AmazonBachUpdate.setAsin(requestProduct,publishListing.getPublishType(),productMessage);
		try {
			ExecutorService executor = Executors.newFixedThreadPool(1);
			StringBuilder errorFuture = new StringBuilder();
			ProcessXmlBatchUpdateTask processXmlTask = new ProcessXmlBatchUpdateTask(requestProduct,true,true,true);
            Future<String> futureResult = executor.submit(processXmlTask);
            String errorMsg = futureResult.get();
			if(StringUtils.isNotEmpty(errorMsg))
			{
				errorFuture.append(errorMsg);
			}
            executor.shutdown();
			if(errorMsg.length() > 0) // 如果有错误，以异常方式返回错误信息
			{
				logger.warn("解释xml时出现错误，ID为：{}",publishListing.getId());
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601,errorFuture.toString());
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			String publishMessageJson="";
			try {
				publishMessageJson=mapper.writeValueAsString(productMessage);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			update.setPublishMessage(publishMessageJson);
			update.setPublishStatus(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_REST_PUSH);
			update.setId(id);
			//7.25添加一个修改字段
			update.setUpdateStatus(AmazonPublishUpdateStatus.UPDATE_GOING);
			int i = amazonPublishListingMapper.updateByPrimaryKeySelective(update);
			logger.info("------在线更新————更新amazonPublishListing影响行数{}------"+i);
			publishLogService.insert(publishMessageJson, PublishLogEnum.EDIT, operatorId, operatorName, requestProduct.getId());
		} catch (Exception e) {
			logger.error("--------updateOnline()--------PublishSubListing生成XML错误---------"+e.getMessage());
			e.printStackTrace();
		}
		
	}

	@Override
	public List<AmazonPublishListing> selectNoAsinList(AmazonPublishListing listing) {
		return amazonPublishListingMapper.selectNoAsinList(listing);
	}

	@Override
	public AmazonPublishListing selectBySubmitfeedId(AmazonPublishListing t) {
		return amazonPublishListingMapper.selectBySubmitfeedId(t);
	}

	@Override
	public void updateLoadTaskPulishBatchOnlineTime(Map<String, Object> paramsMap) {
		amazonPublishListingMapper.updateLoadTaskPulishBatchOnlineTime(paramsMap);
	}


	@Override
	public Page<AmazonPublishListing> page(AmazonPublishListing t) {
    	//如果在线修改状态不为空并且不为0则认为 刊登的状态为 在线状态或者在线修改图片状态
		if (null!=t.getUpdateStatus()&&0!=t.getUpdateStatus()){
			t.setPublishStatus(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE);
			List<Integer> integers = new ArrayList<>();
			integers.add(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_REST_PUSH);
			t.setPublishStatusList(integers);
			logger.info("设置在线修改状态中的对应的刊登状态");
		}
		List<AmazonPublishListing> list = this.amazonPublishListingMapper.page(t);
		for(AmazonPublishListing listing : list){
			// 7.27tkx注释
			//if(listing.getPublishStatus().equals(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_FAIL)){
				String s = this.setListingSubStatus(listing.getPublishMessage(), listing.getId(),listing.getPublishStatus(),listing);
				listing.setPublishMessage(s);
			//}
			listing = this.setSupplyStatus(listing);
		}
		PageInfo<AmazonPublishListing> pageInfo = new PageInfo(list);
		return new Page(pageInfo);
	}


	/**
	 * 设置各子项的库存状态
	 * @param listing 刊登数据
	 */
	private AmazonPublishListing setSupplyStatus(AmazonPublishListing listing){
    	try {
			AmazonPublishSubListing subListing = new AmazonPublishSubListing();
			subListing.setListingId(listing.getId());
			subListing.setMsgType(AmazonPostMethod.POST_PRODUCT_DATA);
			//amazonPublishSubListingMapper.page()
			String publishMessage = listing.getPublishMessage();
			JSONObject object = JSONObject.parseObject(publishMessage);
			String plSku = object.getString("plSku");
			if(listing.getPublishType() == AmazonConstants.PUBLISH_TYPE_ONLY){
				object = this.setSStatus(plSku,subListing,object,listing.getWarehouseId());
			}
			JSONArray array = object.getJSONArray("varRequestProductList");
			JSONObject subObj;
			for (Object o : array) {
				if (o instanceof JSONObject) {
					subObj = (JSONObject) o;
					plSku = subObj.getString("plSku");
					subObj = this.setSStatus(plSku,subListing,subObj,listing.getWarehouseId());
				}
			}
			listing.setPublishMessage(object.toJSONString());
		}catch (Exception e){
    		logger.error("刊登ID为: "+listing.getId()+" 的数据设置库存状态异常",e);
		}
    	return listing;
	}

	private JSONObject setSStatus( String plSku,AmazonPublishSubListing subListing,JSONObject object,Integer warehouseId){
		if(StringUtils.isNotBlank(plSku)){
			subListing.setPlSku(plSku);
			List<AmazonPublishSubListing> page = amazonPublishSubListingMapper.page(subListing);
			if(!CollectionUtils.isEmpty(page)){
				AmazonPublishSubListing sub = page.get(0);
				Integer plSkuStatus = sub.getPlSkuStatus();
				Integer plSkuTort = sub.getPlSkuTort();
				Long plSkuCount = sub.getPlSkuCount();
				if (plSkuStatus == null|| plSkuStatus.equals(AmazonPublishEnums.PLSKUStatus.OTHER.getCode())){
					object.put("supplyStatus", AmazonPublishEnums.SupplyStatus.OTHER.getCode());
				} else if(plSkuStatus.equals(AmazonPublishEnums.PLSKUStatus.DOWN.getCode())){
					object.put("supplyStatus", AmazonPublishEnums.SupplyStatus.DOWN.getCode());
				}else if(plSkuStatus.equals(AmazonPublishEnums.PLSKUStatus.UP.getCode())) {
					if (null!=plSkuTort&&1==plSkuTort){
						object.put("supplyStatus", AmazonPublishEnums.SupplyStatus.TORT.getCode());
					} else {
						if (plSkuCount == null||warehouseId==null) {
							object.put("supplyStatus", AmazonPublishEnums.SupplyStatus.OTHER.getCode());
						}else if (plSkuCount <= 0) {
							object.put("supplyStatus", AmazonPublishEnums.SupplyStatus.STOCK.getCode());
						} else if (plSkuCount < 20) {
							object.put("supplyStatus", AmazonPublishEnums.SupplyStatus.LESS.getCode());
						} else {
							object.put("supplyStatus", AmazonPublishEnums.SupplyStatus.NORMAL.getCode());
						}
					}
				}
//				if(plSkuStatus == null){
//					object.put("supplyStatus", AmazonPublishEnums.SupplyStatus.OTHER.getCode());
//				}else if(plSkuStatus.equals(AmazonPublishEnums.PLSKUStatus.DOWN.getCode())){
//					object.put("supplyStatus", AmazonPublishEnums.SupplyStatus.DOWN.getCode());
//				}else if(plSkuStatus.equals(AmazonPublishEnums.PLSKUStatus.UP.getCode())){
//					if(plSkuCount == null){
//						object.put("supplyStatus", AmazonPublishEnums.SupplyStatus.OTHER.getCode());
//					}else if(plSkuCount == 0){
//						object.put("supplyStatus", AmazonPublishEnums.SupplyStatus.STOCK.getCode());
//					}else if(plSkuCount < 20){
//						object.put("supplyStatus", AmazonPublishEnums.SupplyStatus.LESS.getCode());
//					}else {
//						object.put("supplyStatus", AmazonPublishEnums.SupplyStatus.NORMAL.getCode());
//					}
//				}else{
//					object.put("supplyStatus", AmazonPublishEnums.SupplyStatus.OTHER.getCode());
//				}
			}
		}
		return object;
	}




	/**
	 *
	 * @param publishMessage 刊登数据
	 * @return 处理后的刊登数据
	 */


	/**
	 * 设置listIng数据子项的刊登状态
	 * @param publishMessage 刊登数据
	 * @param listingId 刊登的listIngID
	 * @param publishStatus listing的状态
	 * @param listing 刊登对象
	 * @return
	 */
	private String setListingSubStatus(String publishMessage,Long listingId,Integer publishStatus,AmazonPublishListing listing){
		try {
			if(publishStatus.equals(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_FAIL) || publishStatus.equals(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE)){
				AmazonPublishSubListing subListing = new AmazonPublishSubListing();
				subListing.setListingId(listingId);
				JSONObject object = JSONObject.parseObject(publishMessage);
				String sku = object.getString("sku");
				subListing.setSku(sku);
				StatisticsPublishReport statisticsPublishReports = amazonPublishSubListingMapper.selectStatisticsPublishBySku(subListing);
				object.put("subPublishStatus",null);
				if(publishStatus.equals(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_FAIL)){
					if(statisticsPublishReports.getSuccessCount().equals(statisticsPublishReports.getTotalCount())){
						object.put("subPublishStatus",AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE);
					}else {
						object.put("subPublishStatus",AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_FAIL);
					}
				}
				JSONArray array = object.getJSONArray("varRequestProductList");
				JSONObject subObj;
				AmazonPublishReportDetail detail = new AmazonPublishReportDetail();

				AmazonPublishReportTime time = new AmazonPublishReportTime();
				time.setMerchantId(listing.getMerchantIdentifier());
				time.setPublishSite(listing.getPublishSite());
				List<AmazonPublishReportTime> amazonPublishReportTimes = amazonPublishReportTimeService.selectNoPage(time);
				Date reportTime = null;
				if(amazonPublishReportTimes != null && amazonPublishReportTimes.size() > 0){
					reportTime = amazonPublishReportTimes.get(0).getReportTime();
				}
				AmazonPublishReportDetail detail1;
				if(reportTime != null && reportTime.after(listing.getUpdateTime())){
					detail.setSku(sku);
					detail1 = amazonPublishReportDetailService.selectLastOne(detail);
					if(detail1 == null){
						//8.3修改bug
						object.put("asin",null);  //8.30需求 父体asin码为空
					}
				}
				for (Object o : array)
					if (o instanceof JSONObject) {
						subObj = (JSONObject) o;
						sku = subObj.getString("sku");
						subListing.setSku(sku);
						statisticsPublishReports = amazonPublishSubListingMapper.selectStatisticsPublishBySku(subListing);
						subObj.put("subPublishStatus",null);
						if(publishStatus.equals(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_FAIL)){
							if (statisticsPublishReports.getSuccessCount().equals(statisticsPublishReports.getTotalCount())) {
								subObj.put("subPublishStatus", AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE);
							} else {
								subObj.put("subPublishStatus", AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_FAIL);
							}
						}

						if(reportTime != null && reportTime.after(listing.getUpdateTime())){
							detail.setSku(sku);
							detail1 = amazonPublishReportDetailService.selectLastOne(detail);
							if(detail1 == null){
								//8.3修改bug
								if(publishStatus.equals(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE) ||
										(publishStatus.equals(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_FAIL) && subObj.getInteger("subPublishStatus").equals(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE) )
								){
									subObj.put("subPublishStatus", AmazonPublishListStatus.AMAZON_PUBLISH_SUB_STATUS_DELETE);
									subObj.put("asin",null);  //8.30需求
								}
							}
						}
					}
				return object.toJSONString();
			}else {
				if(StringUtils.isNotBlank(publishMessage)){
					AmazonRequestProduct requestProduct = JSONObject.parseObject(publishMessage, AmazonRequestProduct.class);
					//requestProduct.setSubPublishStatus(null);
					List<AmazonRequestProduct> varRequestProductList = requestProduct.getVarRequestProductList();
					for(AmazonRequestProduct sub:varRequestProductList){
						sub.setSubPublishStatus(null);
					}
					ObjectMapper mapper = new ObjectMapper();
					mapper.setSerializationInclusion(Include.NON_NULL);
					String publishMessageJson="";
					try {
						publishMessageJson=mapper.writeValueAsString(requestProduct);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
					return publishMessage;
				}else
					return publishMessage;

			}
		}catch (Exception e){
			logger.error("设置listIng数据子项的刊登状态异常",e);
			return publishMessage;
		}
	}







	@Override
	public ArrayList<Integer> getAmazonWarehouseIdList() {
		return amazonPublishListingMapper.getAmazonWarehouseIdList();
	}

	@Override
	public Page<String> getAmazonListPlSkuByWarehouseId(Integer warehouseId) {
		ArrayList<String> amazonListPlSkuByWarehouseId = amazonPublishListingMapper.getAmazonListPlSkuByWarehouseId(warehouseId);
		 return new Page(amazonListPlSkuByWarehouseId);
	}

	@Override
	public void updatePlSkuCount(HashMap<String, String> msg) {
		amazonPublishListingMapper.updatePlSkuCount(msg);
	}

	
	public List<AmazonPublishListing> getByplatformSkuAndSite(List<String> platformSku,Integer empowerId) {
		Map<String, Object> map =new HashMap<String, Object>();
		Empower empower = empowerMapper.getEmpowerById(empowerId);
		if(empower == null) {
			return null;
		}
		Map<String, MarketplaceId> maps = MarketplaceIdList.createMarketplaceForKeyId();
		MarketplaceId marketplaceId = maps.get(empower.getWebName());
		String countryCode = marketplaceId.getCountryCode();
		
		map.put("platformSkus", platformSku);
		map.put("publishSite", countryCode);
		map.put("merchantIdentifier", empower.getThirdPartyName());
		return amazonPublishListingMapper.getByplatformSkuAndSite(map);
	}
 
	@Override
	public List<AmazonPublishListing> selectByAsin(String asin) {
		return amazonPublishListingMapper.selectByAsin(asin);
	}

	/**
	 * 计算最终售价
	 */
	@Override
	public List<CommodityDTO> disposePrice(AmazonDisposePriceVO amazonDisposePriceVO) {
		return computeTemplateUtil.disposeComputePrice(amazonDisposePriceVO);
	}
	public List<AmazonPublishListing> findListIfOnline(Long[] ids){
		return amazonPublishListingMapper.findListIfOnline(ids);
	}
	public void updatelistingStatus(Long[] onlineIds, Integer updateSuccess){
		amazonPublishListingMapper.updatelistingStatus(onlineIds,updateSuccess);
	}

	@Override
	public List<AmazonPublishListing> getBatchByIds(Map<String, Object> params) {
		return amazonPublishListingMapper.getBatchByIds(params);
	}

	@Override
	public void updateDefaultToSuccess() {
		amazonPublishListingMapper.updateDefaultToSuccess();
	}

	@Override
	public void updateSuccessToDefault() {
		amazonPublishListingMapper.updateSuccessToDefault();
	}

	@Override
	public Page<AmazonReference> getAmazonReferenceByPage(AmazonPublishSubListing subListing) {
		List<AmazonReference> amazonReferenceByPage = amazonPublishSubListingMapper.getAmazonReferenceByPage(subListing);
		HashMap<Long, AmazonRequestProduct> map = new HashMap<>();
		AmazonPublishListing listing = null;
		AmazonRequestProduct requestProduct = null;
		for(AmazonReference reference: amazonReferenceByPage){
			if(map.get(reference.getId()) == null){
				listing = amazonPublishListingMapper.selectByPrimaryKey(reference.getId());
				requestProduct = JSONObject.parseObject(listing.getPublishMessage(), AmazonRequestProduct.class);
				map.put(reference.getId(),requestProduct);
			}
			requestProduct = map.get(reference.getId());
			if(requestProduct.getSku().equalsIgnoreCase(reference.getSku())){
				reference.setStandardProductType(requestProduct.getStandardProductType());
				reference.setStandardProductID(requestProduct.getStandardProductID());
			}else {
				if(requestProduct.getVarRequestProductList() != null && requestProduct.getVarRequestProductList().size() >0){
					List<AmazonRequestProduct> varRequestProductList = requestProduct.getVarRequestProductList();
					for(AmazonRequestProduct product: varRequestProductList){
						if(product.getSku().equalsIgnoreCase(reference.getSku())){
							reference.setStandardProductID(product.getStandardProductID());
							reference.setStandardProductType(product.getStandardProductType());
						}
					}
				}
			}
		}
		return new Page(amazonReferenceByPage);
	}
}





