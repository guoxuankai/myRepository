package com.rondaful.cloud.seller.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ebay.sdk.TimeFilter;
import com.ebay.sdk.call.GetItemCall;
import com.ebay.sdk.call.GetSellerListCall;
import com.ebay.soap.eBLBaseComponents.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.entity.ebay.EbayListingMQModel;
import com.rondaful.cloud.seller.entity.ebay.ListingVariantAttribute;
import com.rondaful.cloud.seller.enums.EbayOperationEnum;
import com.rondaful.cloud.seller.mapper.*;
import com.rondaful.cloud.seller.rabbitmq.EbayOpertionSender;
import com.rondaful.cloud.seller.remote.RemoteCommodityService;
import com.rondaful.cloud.seller.remote.RemoteOrderRuleService;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;
import com.rondaful.cloud.seller.service.IEbayBaseService;
import com.rondaful.cloud.seller.service.IEbayListingService;
import com.rondaful.cloud.seller.service.IEbayPublishListingOperationLogService;
import jodd.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class EbayListingServiceImpl implements IEbayListingService {

	@Autowired
	private EbayPublishListingNewMapper listingMapper;
	@Autowired
	private IEbayListingService ebayListingService;
	@Autowired
	private EbayPublishListingDetailMapper ebayPublishListingDetailMapper;
	@Autowired
	private EbayPublishListingPriceMapper ebayPublishListingPriceMapper;
	@Autowired
	private EbayPublishListingReturnPolicyMapper ebayPublishListingReturnPolicyMapper;

	@Autowired
	private EbayPublishBuyerRequirementsMapper buyerRequirementsMapper;
	
	@Autowired
	private EbayPublishListingAttributeMapper listingAttributeMapper;
	
	@Autowired
	private EbayPublishListingShippingMapper listingShippingMapper;
	
	@Autowired
	private IEbayBaseService ebayBaseService;
	
	@Autowired
	private RemoteOrderRuleService remoteOrderRuleService;

	@Autowired
	private EbayPublishListingVariantMapper listingVariantMapper;

	@Autowired
	private EbayPublishListingVariantSkusMapper ebayPublishListingVariantSkusMapper;
	
	@Autowired
	private EbayPublishListingVariantPictureMapper listingVariantValuePictureMapper;

	@Autowired
	private EbayPublishListingShippingTransportMapper ebayPublishListingShippingTransportMapper;
	
	@Autowired
	private IEbayPublishListingOperationLogService logService;

	@Autowired
	private EbayOpertionSender sender;
	@Autowired
	private AuthorizationSellerService authorizationSellerService;
	@Autowired
	private RemoteCommodityService remoteCommodityService;


    private final Logger logger = LoggerFactory.getLogger(EbayListingServiceImpl.class);

	@Override
	public Integer getEbayListingList(Long empowerId, Long userId, String userName, String sellerId) {
		GetSellerListCall sellerListCall = null;
		logger.info("ebay GetSellerList start>>");
		Integer count= 0;
		try {
			sellerListCall = new GetSellerListCall(ebayBaseService.getApiContext(empowerId.toString()));
			Calendar timeFrom =  Calendar.getInstance();
			timeFrom.add(Calendar.DATE,-120);
			Calendar timeto =  Calendar.getInstance();
			TimeFilter filter = new TimeFilter(timeFrom,timeto);
			logger.info("ebay GetSellerList end>>{},{}",empowerId,sellerId);
			sellerListCall.setStartTimeFilter(filter);
			//sellerListCall.setEndTimeFilter();
			sellerListCall.getSellerList();
			ItemType[]  itemList = sellerListCall.getSellerList();
			if(itemList!=null) {

				Calendar startTime =  Calendar.getInstance();
				startTime.add(Calendar.DATE,-91);
				Calendar endTime =  Calendar.getInstance();

				List<EbayListingMQModel> listEbayListingMQModel = listingMapper.getEbayListingMQModelList(empowerId,startTime.getTime(),endTime.getTime());
				for (ItemType itemType:itemList) {
					EbayListingMQModel model = new EbayListingMQModel();
					model.setEmpowerId(empowerId);
					model.setSellerId(sellerId);
					model.setUserId(userId);
					model.setUserName(userName);
					model.setItemId(itemType.getItemID());
					sender.sendListing(model);
					for(EbayListingMQModel listingMQModel:listEbayListingMQModel){
						if(itemType.getItemID().equals(listingMQModel.getItemId())){
							listEbayListingMQModel.remove(listingMQModel);
							break;
						}
					}
				}
				if(listEbayListingMQModel!=null && listEbayListingMQModel.size()>0){
					for(EbayListingMQModel listingMQModel:listEbayListingMQModel){
						if(StringUtils.isNotEmpty(listingMQModel.getItemId())) {
							sender.sendListing(listingMQModel);
						}
					}
				}
				count = itemList.length;
			}
		} catch (Exception e){
			e.printStackTrace();
			logger.info("ebay GetSellerList 异常>>{}",e.getMessage());
		}
		return count;
	}

	@Override
	public void saveEbayListing(Long empowerId, Long userId, String userName, String sellerId, Long id, String itemId) {
		GetItemCall itemCall = null;
		logger.info("ebay GetItemCall start>>");
		try {
			itemCall = new GetItemCall(ebayBaseService.getApiContext(empowerId.toString()));
			itemCall.setItemID(itemId);
			DetailLevelCodeType [] s  = {DetailLevelCodeType.RETURN_ALL};
			itemCall.setDetailLevel(s);
			itemCall.setIncludeItemSpecifics(true);
			logger.info("ebay GetItemCall end>>{},{}",empowerId,sellerId);
			ItemType item = itemCall.getItem();
			if(item!=null) {
				EbayPublishListingNew ebayPublishListing = listingMapper.getEbayPublishListingByItemId(itemId,empowerId);
				if(ebayPublishListing==null){
					ebayListingService.insertEbayPublishListing(item,userId,userName,sellerId,empowerId);
				}else {
					ebayListingService.updateEbayPublishListing(item,userId,userName,sellerId,empowerId,ebayPublishListing);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
			logger.info("ebay GetItemCall 异常>>{}",e.getMessage());
			if(itemCall!=null) {
				ErrorType[] errors = itemCall.getApiException().getErrors();
				if (errors != null && errors.length > 0) {
					ErrorType errorType = errors[0];
					String code = errorType.getErrorCode();
					if("17".equals(code)){
						EbayPublishListingNew ebayPublishListing = listingMapper.getEbayPublishListingByItemId(itemId,empowerId);
						EbayPublishListingNew listing = new EbayPublishListingNew();
						listing.setId(ebayPublishListing.getId());
						listing.setStatus(8);
						listingMapper.updateByPrimaryKeySelective(listing);
					}
				}
			}
//			EbayListingMQModel model = new EbayListingMQModel();
//			model.setEmpowerId(empowerId);
//			model.setUserId(userId);
//			model.setUserName(userName);
//			model.setSellerId(sellerId);
//			model.setItemId(itemId);
//			sender.sendListing(model);
		}

	}

	/**
	 * 新增
	 * @param item
	 * @param userId
	 * @param userName
	 * @param sellerId
	 * @param empowerId
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public void insertEbayPublishListing(ItemType item,Long userId, String userName, String sellerId,Long empowerId) throws Exception{
		Date date = new Date();
		EbayPublishListingNew listing = this.setEbayPublishListingNew(item,sellerId,empowerId);
		listing.setCreateId(userId);
		listing.setCreateName(userName);
		listing.setPlatformListing(2);
		listing.setCreationTime(date);
		listing.setUpdateTime(date);

		logger.info("insertEbayPublishListing-->status:{},listingType:{}",listing.getStatus(),listing.getListingType());

		listingMapper.insertSelective(listing);
		Integer listingId=listing.getId().intValue();
		//详情部分
		EbayPublishListingDetail ebayPublishListingDetail = this.setEbayPublishListingDetail(item,listing.getListingType());
		ebayPublishListingDetail.setListingId(listing.getId());
		ebayPublishListingDetailMapper.insertSelective(ebayPublishListingDetail);
		//价格部分
		EbayPublishListingPrice ebayPublishListingPrice = this.setEbayPublishListingPrice(item,listing.getListingType());
		ebayPublishListingPrice.setListingId(listing.getId());
		ebayPublishListingPriceMapper.insertSelective(ebayPublishListingPrice);
		//退货政策
		EbayPublishListingReturnPolicy returnPolicy = this.setEbayPublishListingReturnPolicy(item);
		returnPolicy.setListingId(listing.getId());
		ebayPublishListingReturnPolicyMapper.insertSelective(returnPolicy);

		//卖家要求
		EbayPublishBuyerRequirements obj = this.setEbayPublishBuyerRequirements(item);
		obj.setListingId(listingId);
		obj.setCreationTime(new Date());
		buyerRequirementsMapper.insertSelective(obj);
		StringBuffer plSku = new StringBuffer("");
//		//多属性刊登变体数据处理
		if (listing.getListingType() ==2){
			Map<String,Object> map =this.setVariations(item,sellerId,empowerId);
			if(map!=null){
				List<EbayPublishListingVariantPicture> pictureList =(List<EbayPublishListingVariantPicture>)map.get("pictureList");
				//变体信息
				List<EbayPublishListingVariant> listVariant = (List<EbayPublishListingVariant>)map.get("listVariant");
				if(listVariant!=null){
					listVariant.forEach(publishListingVariant -> {
						publishListingVariant.setListingId(listingId);
						publishListingVariant.setCreationTime(new Date());
						publishListingVariant.setPicture(this.getVariantPicture(publishListingVariant.getMultiattribute(),pictureList));
						if(publishListingVariant.getPlSku()!=null && "".equals(plSku.toString())){
							plSku.append(publishListingVariant.getPlSku());
						}
						listingVariantMapper.insertSelective(publishListingVariant);
						if(publishListingVariant.getListVariantSkus()!=null && publishListingVariant.getListVariantSkus().size()>0){
							for(EbayPublishListingVariantSkus variantSkus:publishListingVariant.getListVariantSkus()){
								variantSkus.setVariantId(publishListingVariant.getId().longValue());
								variantSkus.setListingId(listing.getId());
								ebayPublishListingVariantSkusMapper.insertSelective(variantSkus);
							}
						}
					});
//					if (CollectionUtils.isNotEmpty(listVariant))
//						listingVariantMapper.insertBatchList(listVariant);

				}
				//变体对应的图片信息

				if(pictureList!=null) {
					pictureList.forEach(picture -> {
						picture.setListingId(listingId);
						picture.setCreationTime(new Date());
					});
					if (CollectionUtils.isNotEmpty(pictureList))
						listingVariantValuePictureMapper.insertBatchList(pictureList);
				}
			}
		}else{
			listingVariantMapper.deleteByValue(listingId);
			EbayPublishListingVariant variant = this.setEbayPublishListingVariant(item,sellerId,empowerId);
			variant.setListingId(listingId);
			variant.setCreationTime(new Date());
			listingVariantMapper.insertSelective(variant);
			if(variant.getPlSku()!=null) {
				plSku.append(variant.getPlSku());
			}
			if(variant.getListVariantSkus()!=null && variant.getListVariantSkus().size()>0){
				for(EbayPublishListingVariantSkus variantSkus:variant.getListVariantSkus()){
					variantSkus.setVariantId(variant.getId().longValue());
					variantSkus.setListingId(listing.getId());
					ebayPublishListingVariantSkusMapper.insertSelective(variantSkus);
				}
			}
		}
		//plsku映射spu
		if(!"".equals(plSku.toString())){
			EbayPublishListingNew spulisting = new EbayPublishListingNew();
			spulisting.setId(listing.getId());
			spulisting.setPlatformListing(1);
			String spu =this.getPlSpu(plSku.toString());
			if(spu!=null){
				spulisting.setPlSpu(spu);
			}
			listingMapper.updateByPrimaryKeySelective(spulisting);
		}

		//商品属性  (包含推荐属性,自定属性)
		List<EbayPublishListingAttribute> attributeList = this.setEbayPublishListingAttribute(item);
		if(attributeList!=null) {
			//值不为空的属性集合
			List<EbayPublishListingAttribute> attributeValueIsNotNullList = new ArrayList<>();
			for (EbayPublishListingAttribute attributeObj : attributeList) {
				if (StringUtils.isNotBlank(attributeObj.getAttributeValue())) {
					attributeObj.setListingId(listingId);
					attributeObj.setCreationTime(new Date());
					attributeValueIsNotNullList.add(attributeObj);
				}
			}
			//只保存value不为空的属性
			if (CollectionUtils.isNotEmpty(attributeValueIsNotNullList))
				listingAttributeMapper.insertAttributeList(attributeList);
			logger.info("insertPublishListing-->insert attribute end");
		}
		//运输服务
		EbayPublishListingShipping shipping = this.setShippingDetails(item);
		shipping.setListingId(listingId);
		shipping.setCreationTime(new Date());
		listingShippingMapper.insertSelective(shipping);
		logger.info("insertPublishListing-->insert shipping end");
		ebayPublishListingShippingTransportMapper.deleteByListingId(listing.getId());
		if(shipping.getInternationalShippingServiceOptions()!=null) {
			for (EbayPublishListingShippingTransport transport : shipping.getInternationalShippingServiceOptions()) {
				if (StringUtils.isBlank(transport.getShippingService())) {
					continue;
				}
				transport.setListingId(listing.getId());
				transport.setShippingId(shipping.getId().longValue());
				ebayPublishListingShippingTransportMapper.insertSelective(transport);
			}
		}
		if(shipping.getShippingServiceOptions()!=null) {
			for (EbayPublishListingShippingTransport transport : shipping.getShippingServiceOptions()) {
				if (StringUtils.isBlank(transport.getShippingService())) {
					continue;
				}
				transport.setListingId(listing.getId());
				transport.setShippingId(shipping.getId().longValue());
				ebayPublishListingShippingTransportMapper.insertSelective(transport);
			}
		}
		String type = EbayOperationEnum.SYNC.getCode();
		this.insertLog(userName,listingId,type,listing.getTitle(),userId);

	}

	private String getVariantPicture(String multiattribute,List<EbayPublishListingVariantPicture> variantPicture){
		String pictureUrl = "";
		if(StringUtils.isNotBlank(multiattribute)){
			//获取变体属性对应图片 ,只取一张
			List<ListingVariantAttribute> parseArray = JSONObject.parseArray(multiattribute, ListingVariantAttribute.class);
			if (CollectionUtils.isNotEmpty(parseArray)){
				for(ListingVariantAttribute attribute:parseArray){
					if(StringUtils.isNotEmpty(pictureUrl)){
						return pictureUrl;
					}
					if (StringUtils.isNotBlank(attribute.getVariantKey()) && StringUtils.isNotBlank(attribute.getVariantValue())){
						for(EbayPublishListingVariantPicture picture:variantPicture){
							if(attribute.getVariantKey().equals(picture.getVariantKey()) && attribute.getVariantValue().equals(picture.getVariantValue())){
								pictureUrl = StringUtils.isEmpty(picture.getPicture()) ?"":picture.getPicture().split(",")[0];
								break;
							}
						}

					}

				}
			}
		}
		return pictureUrl;
	}
	/**
	 *
	 * 修改
	 * @param item
	 * @param userId
	 * @param userName
	 * @param sellerId
	 * @param empowerId
	 * @param queryListing
	 * @throws Exception
	 */
	@Transactional
	public void updateEbayPublishListing(ItemType item,Long userId, String userName, String sellerId,Long empowerId,EbayPublishListingNew queryListing) throws Exception{
		Date date = new Date();
		EbayPublishListingNew listing = this.setEbayPublishListingNew(item,sellerId,empowerId);
		listing.setUpdateTime(date);
		listing.setId(queryListing.getId());

		listingMapper.updateByPrimaryKeySelective(listing);
		Integer listingId= listing.getId().intValue();

		//详情部分
		EbayPublishListingDetail ebayPublishListingDetail = this.setEbayPublishListingDetail(item,listing.getListingType());
		ebayPublishListingDetail.setListingId(listing.getId());
		ebayPublishListingDetailMapper.updateByPrimaryKeySelective(ebayPublishListingDetail);
		//价格部分
		ebayPublishListingPriceMapper.deleteByListingId(listing.getId());
		EbayPublishListingPrice ebayPublishListingPrice = this.setEbayPublishListingPrice(item,listing.getListingType());
		ebayPublishListingPrice.setListingId(listing.getId());
		ebayPublishListingPriceMapper.insertSelective(ebayPublishListingPrice);
		//退货政策
		ebayPublishListingReturnPolicyMapper.deleteByListingId(listing.getId());
		EbayPublishListingReturnPolicy returnPolicy = this.setEbayPublishListingReturnPolicy(item);
		returnPolicy.setListingId(listing.getId());
		ebayPublishListingReturnPolicyMapper.insertSelective(returnPolicy);
		//卖家要求
		EbayPublishBuyerRequirements del = new EbayPublishBuyerRequirements();
		del.setListingId(listingId);
		buyerRequirementsMapper.deleteByValue(del);

		EbayPublishBuyerRequirements obj = this.setEbayPublishBuyerRequirements(item);
		obj.setListingId(listingId);
		obj.setCreationTime(new Date());
		buyerRequirementsMapper.insertSelective(obj);
		StringBuffer plSku = new StringBuffer("");
//		//多属性刊登变体数据处理
		if (listing.getListingType() ==2){
			//刪除修改信息
			listingVariantMapper.deleteByValue(listingId);
			//删除变休关联的多属性
			//listingVariantValueMapper.deleteByValue(listingId);
			//删除变体相关的图片
			listingVariantValuePictureMapper.deleteByValue(listingId);
			Map<String,Object> map =this.setVariations(item,sellerId,empowerId);
			if(map!=null){
				List<EbayPublishListingVariantPicture> pictureList =(List<EbayPublishListingVariantPicture>)map.get("pictureList");
				//变体信息
				List<EbayPublishListingVariant> listVariant = (List<EbayPublishListingVariant>)map.get("listVariant");
				if(listVariant!=null){
					listVariant.forEach(publishListingVariant -> {
						publishListingVariant.setListingId(listingId);
						publishListingVariant.setCreationTime(new Date());
						publishListingVariant.setPicture(this.getVariantPicture(publishListingVariant.getMultiattribute(),pictureList));
						if(publishListingVariant.getPlSku()!=null && "".equals(plSku.toString())){
							plSku.append(publishListingVariant.getPlSku());
						}
						listingVariantMapper.insertSelective(publishListingVariant);
						if(publishListingVariant.getListVariantSkus()!=null && publishListingVariant.getListVariantSkus().size()>0){
							for(EbayPublishListingVariantSkus variantSkus:publishListingVariant.getListVariantSkus()){
								variantSkus.setVariantId(publishListingVariant.getId().longValue());
								variantSkus.setListingId(listing.getId());
								ebayPublishListingVariantSkusMapper.insertSelective(variantSkus);
							}
						}
					});
//					if (CollectionUtils.isNotEmpty(listVariant))
//						listingVariantMapper.insertBatchList(listVariant);
				}
				//变体对应的图片信息
				//List<EbayPublishListingVariantPicture> pictureList =(List<EbayPublishListingVariantPicture>)map.get("pictureList");
				if(pictureList!=null) {
					pictureList.forEach(picture -> {
						picture.setListingId(listingId);
						picture.setCreationTime(new Date());
					});
					if (CollectionUtils.isNotEmpty(pictureList))
						listingVariantValuePictureMapper.insertBatchList(pictureList);
				}
			}
		}else{
			listingVariantMapper.deleteByValue(listingId);
			EbayPublishListingVariant variant = this.setEbayPublishListingVariant(item,sellerId,empowerId);
			variant.setListingId(listingId);
			variant.setCreationTime(new Date());
			listingVariantMapper.insertSelective(variant);
			if(variant.getPlSku()!=null ){
				plSku.append(variant.getPlSku());
			}
			if(variant.getListVariantSkus()!=null && variant.getListVariantSkus().size()>0){
				for(EbayPublishListingVariantSkus variantSkus:variant.getListVariantSkus()){
					variantSkus.setVariantId(variant.getId().longValue());
					variantSkus.setListingId(listing.getId());
					ebayPublishListingVariantSkusMapper.insertSelective(variantSkus);
				}
			}
		}
		//plsku映射spu
		if(!"".equals(plSku.toString()) && StringUtil.isEmpty(listing.getPlSpu())){
			EbayPublishListingNew spulisting = new EbayPublishListingNew();
			spulisting.setId(listing.getId());
			spulisting.setPlatformListing(1);
			String spu =this.getPlSpu(plSku.toString());
			if(spu!=null){
				spulisting.setPlSpu(spu);
			}
			listingMapper.updateByPrimaryKeySelective(spulisting);
		}
		//商品属性  (包含推荐属性,自定属性)
		EbayPublishListingAttribute record = new EbayPublishListingAttribute();
		record.setListingId(listingId);
		listingAttributeMapper.deleteByValue(record);

		List<EbayPublishListingAttribute> attributeList = this.setEbayPublishListingAttribute(item);
		if(attributeList!=null) {
			//值不为空的属性集合
			List<EbayPublishListingAttribute> attributeValueIsNotNullList = new ArrayList<>();
			for (EbayPublishListingAttribute attributeObj : attributeList) {
				if (StringUtils.isNotBlank(attributeObj.getAttributeValue())) {
					attributeObj.setListingId(listingId);
					attributeObj.setCreationTime(new Date());
					attributeValueIsNotNullList.add(attributeObj);
				}
			}
			//只保存value不为空的属性
			if (CollectionUtils.isNotEmpty(attributeValueIsNotNullList))
				listingAttributeMapper.insertAttributeList(attributeList);

		}
		//运输服务
		listingShippingMapper.deleteByListingId(listingId);
		ebayPublishListingShippingTransportMapper.deleteByListingId(listing.getId());
		EbayPublishListingShipping shipping = this.setShippingDetails(item);
		shipping.setListingId(listingId);
		shipping.setCreationTime(new Date());
		listingShippingMapper.insertSelective(shipping);
		if(shipping.getInternationalShippingServiceOptions()!=null) {
			for (EbayPublishListingShippingTransport transport : shipping.getInternationalShippingServiceOptions()) {
				if (StringUtils.isBlank(transport.getShippingService())) {
					continue;
				}
				transport.setListingId(listing.getId());
				transport.setShippingId(shipping.getId().longValue());
				ebayPublishListingShippingTransportMapper.insertSelective(transport);
			}
		}
		if(shipping.getShippingServiceOptions()!=null) {
			for (EbayPublishListingShippingTransport transport : shipping.getShippingServiceOptions()) {
				if (StringUtils.isBlank(transport.getShippingService())) {
					continue;
				}
				transport.setListingId(listing.getId());
				transport.setShippingId(shipping.getId().longValue());
				ebayPublishListingShippingTransportMapper.insertSelective(transport);
			}
		}

		String type = EbayOperationEnum.SYNC.getCode();
		this.insertLog(userName,listingId,type,listing.getTitle(),userId);

	}
	/**
	 * 操作日志入库
	 * @param user
	 * @param listingId
	 * @param type
	 * @param content
	 * @throws Exception
	 */
	public void insertLog(String user,Integer listingId,String type,String content,Long userId) throws Exception{
		EbayPublishListingOperationLog log = new EbayPublishListingOperationLog();
		log.setListingId(listingId);
		log.setOperationContent(content);
		log.setOperationTime(new Date());
		log.setOperationType(type);
		log.setOperationUser(user);
		log.setOperationUserId(userId);
		logService.insert(log);
	}


	public EbayPublishListingNew setEbayPublishListingNew(ItemType item,String sellerId,Long empowerId) {
		EbayPublishListingNew listing = new EbayPublishListingNew();
		listing.setEmpowerId(empowerId);
		Empower empower = authorizationSellerService.selectByPrimaryKey(empowerId.toString());
		listing.setPublishAccount(empower.getAccount());
		SellingStatusType sellingStatusType = item.getSellingStatus();
		//正在销售
		if ("Active".equals(sellingStatusType.getListingStatus().value())) {
			listing.setStatus(4);
		}else {
			listing.setStatus(3);
		}
		listing.setSellerId(Long.valueOf(sellerId));
		//发布时间
		listing.setPublishTime(item.getSeller().getRegistrationDate().getTime());

		//下架时间
		listing.setEndTimes(item.getListingDetails().getEndTime().getTime());

		//上线时间
		listing.setOnlineTime(item.getListingDetails().getStartTime().getTime());

		listing.setItemid(item.getItemID());
		//标题
		listing.setTitle(item.getTitle());
		//站点
		listing.setSite(item.getSite().value());

		//副标题要额外收费
		listing.setSubTitle(item.getSubTitle());

		//产品分类
		listing.setProductCategory1(item.getPrimaryCategory().getCategoryID());
		//产品第二分类
		if(item.getSecondaryCategory()!=null) {
			listing.setProductCategory2(item.getSecondaryCategory().getCategoryID());
		}

		//产品图片
		PictureDetailsType picture = item.getPictureDetails();
		if(picture!=null){
			if(picture.getPictureURL()!=null){
				StringBuffer pictureImage = new StringBuffer();
				boolean bImage = true;
				for(String pictureURL:picture.getPictureURL()){
					if(bImage) {
						bImage = false;
						pictureImage.append(pictureURL);
					}else {
						pictureImage.append(","+pictureURL);
					}
				}
				listing.setPicture(pictureImage.toString());
			}
		}



//		//币种,价格方式设置
		listing.setCurrency(item.getCurrency().value()); //设置币种

		if(ListingTypeCodeType.FIXED_PRICE_ITEM.value().equals(item.getListingType().value())){
			VariationsType variationsType = item.getVariations();
			if(variationsType!=null && variationsType.getVariation()!=null && variationsType.getVariation().length>0){
				listing.setListingType(2);
			}else {
				listing.setListingType(1);
			}
		}else if(ListingTypeCodeType.CHINESE.value().equals(item.getListingType().value())){
			listing.setListingType(3);
		}else{
			listing.setListingType(4);//其他
		}
//		//发货时间
		listing.setDispatchTimeMax(item.getDispatchTimeMax());
		return listing;
	}

	public EbayPublishListingDetail setEbayPublishListingDetail(ItemType item,int listingType){
		EbayPublishListingDetail detail = new EbayPublishListingDetail();
		//描述
		detail.setListingDesc(item.getDescription());
		detail.setListingDescOriginal(item.getDescription());
		//店铺
		StorefrontType frontType = item.getStorefront();
		if (frontType!=null){
			if(frontType.getStoreCategoryID()>0){
				detail.setStoreCategory1(frontType.getStoreCategoryID()+"");
			}
			if(frontType.getStoreCategory2ID()>0){
				detail.setStoreCategory1(frontType.getStoreCategory2ID()+"");
			}
		}
		//产品图片
		PictureDetailsType picture = item.getPictureDetails();
		if(picture!=null){
			//橱窗展示
			detail.setGalleryTypeCode(picture.getGalleryType().value());
		}
//		//币种,价格方式设置
		detail.setCurrency(item.getCurrency().value()); //设置币种
//		//启用商品卖家要求(不让符合以下要求的买家购买我的商品)
//		/**
//		 * json格式:
//		 * {"maximumItemCount":1,"maximumUnpaidItemStrikesCount":1,"maximumUnpaidItemStrikesDuration":"Days_14","minimumFeedbackScore":1,"shipToRegistrationCountry":true,"zeroFeedbackScore":true}
//		 */
		detail.setDisableBuyerRequirements(item.isDisableBuyerRequirements()==null?true:item.isDisableBuyerRequirements());
//		if (vo.getListingType() ==2){
//			//变体属性组装
//			item.setVariations(buildVariationsType(vo));
//		}
//		//刊登天数
		detail.setListingDuration(item.getListingDuration());
//		//物品所在地
		detail.setLocal(item.getLocation());
//		//物品所在国家
		detail.setCountry(item.getCountry().value());
//		//支付方法    付款说明
		BuyerPaymentMethodCodeType[] paymentMethodCodeType = item.getPaymentMethods();
		StringBuffer paymentOption = new StringBuffer("[");
		if(paymentMethodCodeType!=null){
			boolean namevalueBool = true;
			for(BuyerPaymentMethodCodeType payment:paymentMethodCodeType){
				if(namevalueBool){
					namevalueBool = false;
					paymentOption.append("\""+payment.value()+"\"");
				}else {
					paymentOption.append(",\""+payment.value()+"\"");
				}
			}
		}
		paymentOption.append("]");
		detail.setPaymentOption(paymentOption.toString());


//		//是否立即付款
		detail.setAutoPay(item.isAutoPay());
//		//paypal邮箱地址
		detail.setPaypal(item.getPayPalEmailAddress());
//		//物品新旧程度
		detail.setConditionId(""+item.getConditionID());
//		//物品状况描述 (卖方使用这个字符串字段来更清楚地描述不是全新产品的状态)
		detail.setConditionDescription(item.getConditionDescription());
		return detail;
	}
	public EbayPublishListingPrice setEbayPublishListingPrice(ItemType item,int listingType){
		EbayPublishListingPrice price = new EbayPublishListingPrice();
		if (listingType !=2){ //多属性时，值放入变体信息保存
			AmountType startAmountType = item.getStartPrice();
			price.setStartPrice(BigDecimal.valueOf(startAmountType.getValue()));

		}
		if (listingType == 1){ //如果是单属性一口价  查看是否开启议价模式
			BestOfferDetailsType detailsType = item.getBestOfferDetails();
			if(detailsType!=null && detailsType.isBestOfferEnabled()!=null &&detailsType.isBestOfferEnabled()){
				price.setBestOfferEnabled(true);
				ListingDetailsType listingDetailsType = item.getListingDetails();
				if(listingDetailsType.getMinimumBestOfferPrice()!=null) {
					price.setMinimumBestOfferPrice(BigDecimal.valueOf(listingDetailsType.getMinimumBestOfferPrice().getValue()));
				}else{
					price.setMinimumBestOfferPrice(BigDecimal.ZERO);
				}
				if(listingDetailsType.getBestOfferAutoAcceptPrice()!=null){
					price.setAutoAcceptPrice(BigDecimal.valueOf(listingDetailsType.getBestOfferAutoAcceptPrice().getValue()));
				}else{
					price.setAutoAcceptPrice(BigDecimal.ZERO);
				}

			}else {
				price.setBestOfferEnabled(false);
			}
		}
		if (listingType == 3 ){  //如果是拍卖
			if (null != item.getFloorPrice() && item.getFloorPrice().getValue()>0){
				price.setFloorPrice(BigDecimal.valueOf(item.getFloorPrice().getValue()));
				price.setBuyItNowPrice(BigDecimal.valueOf(item.getBuyItNowPrice().getValue()));
			}
		}

		return price;
	}

	public EbayPublishListingReturnPolicy setEbayPublishListingReturnPolicy(ItemType item){
		EbayPublishListingReturnPolicy returnPolicy = new EbayPublishListingReturnPolicy();
		ReturnPolicyType returnPolicyType = item.getReturnPolicy();

		returnPolicy.setInternationalRefundOption(returnPolicyType.getInternationalRefundOption());
		returnPolicy.setReturnsWithinOption(returnPolicyType.getReturnsWithinOption());
		returnPolicy.setRefundOption(returnPolicyType.getRefundOption());
		returnPolicy.setShippingCostPaidByOption(returnPolicyType.getShippingCostPaidByOption());
		returnPolicy.setDescription(returnPolicyType.getDescription());
		returnPolicy.setInternationalReturnsWithinOption(returnPolicyType.getInternationalReturnsWithinOption());
		returnPolicy.setInternationalShippingCostPaidByOption(returnPolicyType.getInternationalShippingCostPaidByOption());
		returnPolicy.setInternationalReturnsAcceptedOption(returnPolicyType.getInternationalReturnsAcceptedOption());
		returnPolicy.setReturnsAcceptedOption(returnPolicyType.getReturnsAcceptedOption());
		return returnPolicy;
	}
	public EbayPublishListingVariant setEbayPublishListingVariant(ItemType item,String sellerId,Long empowerId){
		EbayPublishListingVariant variant = new EbayPublishListingVariant();
		variant.setPlatformSku(item.getSKU());
		variant.setQuantity(item.getQuantity());
		if(item.getStartPrice()!=null) {
			variant.setStartPrice(BigDecimal.valueOf(item.getStartPrice().getValue()));
		}
		//产品详细信息
		if (item.getProductListingDetails()!=null){
			ProductListingDetailsType productListingDetailsType = item.getProductListingDetails();
			variant.setEan(productListingDetailsType.getEAN());
			variant.setUpc(productListingDetailsType.getUPC());
			variant.setIsbn(productListingDetailsType.getISBN());
			variant.setProductReferenceID(productListingDetailsType.getProductReferenceID());
		}
		try {
			String skuMaps = remoteCommodityService.getSkuMapByPlatformSku(OrderRuleEnum.platformEnm.E_BAU.getPlatform(), empowerId.toString(),item.getSKU());
			List<EbayPublishListingVariantSkus> listVariantSkus = Lists.newArrayList();
			if(skuMaps!=null) {
				String result = Utils.returnRemoteResultDataString(skuMaps, "转换失败");
				JSONObject json = JSONObject.parseObject(result);

				if (json != null){
					JSONArray skuList = json.getJSONArray("skuList");
					if(skuList!=null && skuList.size()>0) {

						for (int i = 0; i < skuList.size(); i++) {
							JSONObject obj = skuList.getJSONObject(i);
							String systemSku = obj.getString("systemSku");
//                            String specValueEn = obj.getString("specValueEn");
//                            String specValueCn = obj.getString("specValueCn");
//                            String commodityNameCn = obj.getString("commodityNameCn");
//                            String commodityNameEn = obj.getString("commodityNameEn");
							Integer skuNum = obj.getInteger("skuNum");
							if(skuNum!=null && skuNum>1) {
								EbayPublishListingVariantSkus variantSkus = new EbayPublishListingVariantSkus();
								variantSkus.setPlSku(systemSku);
								variantSkus.setPlSkuNumber(skuNum);
								listVariantSkus.add(variantSkus);
							}
							if(i==0){//第一个sku为主sku
								variant.setPlSku(systemSku);
							}
						}
					}
				}
			}
			variant.setListVariantSkus(listVariantSkus);
		}catch (Exception e){
			e.printStackTrace();
		}
		return variant;
	}

//	/**
//	 * ebay 刊登数据组装
//	 * @param item 数据来源对象
//	 * @return
//	 */
//	public EbayPublishListing setEbayPublishListing(ItemType item,String sellerId,Long empowerId){
//		EbayPublishListing listing = new EbayPublishListing();
//		listing.setEmpowerId(empowerId.intValue());
//		Empower empower = authorizationSellerService.selectByPrimaryKey(empowerId.toString());
//		listing.setPublishAccount(empower.getAccount());
//		SellingStatusType sellingStatusType = item.getSellingStatus();
//				//正在销售
//		if("Active".equals(sellingStatusType.getListingStatus().value())){
//			listing.setStatus(4);
//		}else {
//			listing.setStatus(3);
//		}
//
//		//发布时间
//		listing.setPublishTime(item.getSeller().getRegistrationDate().getTime());
//
//		//下架时间
//		listing.setEndTimes(item.getListingDetails().getEndTime().getTime());
//
//		//上线时间
//		listing.setOnlineTime(item.getListingDetails().getStartTime().getTime());
//
//		listing.setItemid(item.getItemID());
//		//标题
//		listing.setTitle(item.getTitle());
//		//站点
//		listing.setSite(item.getSite().value());
//
//		//副标题要额外收费
//		listing.setSubTitle(item.getSubTitle());
//		//描述
//		listing.setListingDesc(item.getDescription());
//		listing.setListingDescOriginal(item.getDescription());
//		//产品分类
//		listing.setProductCategory1(item.getPrimaryCategory().getCategoryID());
//		//产品第二分类
//		if(item.getSecondaryCategory()!=null) {
//			listing.setProductCategory2(item.getSecondaryCategory().getCategoryID());
//		}
//		//店铺
//		StorefrontType frontType = item.getStorefront();
//		if (frontType!=null){
//			if(frontType.getStoreCategoryID()>0){
//				listing.setStoreCategory1(frontType.getStoreCategoryID()+"");
//			}
//			if(frontType.getStoreCategory2ID()>0){
//				listing.setStoreCategory1(frontType.getStoreCategory2ID()+"");
//			}
//		}
//		//产品图片
//		PictureDetailsType picture = item.getPictureDetails();
//		if(picture!=null){
//			//橱窗展示
//			listing.setGalleryTypeCode(picture.getGalleryType().value());
//			if(picture.getPictureURL()!=null){
//				StringBuffer pictureImage = new StringBuffer();
//				boolean bImage = true;
//				for(String pictureURL:picture.getPictureURL()){
//					if(bImage) {
//						bImage = false;
//						pictureImage.append(pictureURL);
//					}else {
//						pictureImage.append(","+pictureURL);
//					}
//				}
//				listing.setPicture(pictureImage.toString());
//			}
//		}
//
//
//
////		//币种,价格方式设置
//		listing.setCurrency(item.getCurrency().value()); //设置币种
////		item.setListingType(ListingTypeCodeType.FIXED_PRICE_ITEM);  //设置为固价
//		if(ListingTypeCodeType.FIXED_PRICE_ITEM.value().equals(item.getListingType().value())){
//			VariationsType variationsType = item.getVariations();
//			if(variationsType!=null && variationsType.getVariation()!=null && variationsType.getVariation().length>0){
//				listing.setListingType(2);
//			}else {
//				listing.setListingType(1);
//			}
//		}else if(ListingTypeCodeType.CHINESE.value().equals(item.getListingType().value())){
//			listing.setListingType(3);
//		}else{
//			listing.setListingType(4);//其他
//		}
////		BigDecimal startPrice = vo.getStartPrice(); //起始价
//		if (listing.getListingType() !=2){ //多属性时，值放入变体信息保存
//			AmountType startAmountType = item.getStartPrice();
//			listing.setStartPrice(BigDecimal.valueOf(startAmountType.getValue()));
//			listing.setQuantity(item.getQuantity());
//			listing.setPlatformSku(item.getSKU());
//			String skuMaps = remoteOrderRuleService.getSellerSkuMapByPlatformSku("eBay",
//					empowerId.toString(),item.getSKU(),sellerId);
//			if(skuMaps!=null) {
//				String result = Utils.returnRemoteResultDataString(skuMaps, "转换失败");
//				JSONObject json = JSONObject.parseObject(result);
//				if (json != null && json.get("plSku") != null){
//					listing.setPlSku(json.get("plSku").toString());
//				}
//			}
//		}
//		if (listing.getListingType() == 1){ //如果是单属性一口价  查看是否开启议价模式
//			BestOfferDetailsType detailsType = item.getBestOfferDetails();
//			if(detailsType!=null && detailsType.isBestOfferEnabled()!=null &&detailsType.isBestOfferEnabled()){
//				listing.setBestOfferEnabled(true);
//				ListingDetailsType listingDetailsType = item.getListingDetails();
//				listing.setMinimumBestOfferPrice(BigDecimal.valueOf(listingDetailsType.getMinimumBestOfferPrice().getValue()));
//				listing.setAutoAcceptPrice(BigDecimal.valueOf(listingDetailsType.getBestOfferAutoAcceptPrice().getValue()));
//			}else {
//				listing.setBestOfferEnabled(false);
//			}
//		}
//		if (listing.getListingType() == 3 ){  //如果是拍卖
//			if (null != item.getFloorPrice() && item.getFloorPrice().getValue()>0){
//				listing.setFloorPrice(BigDecimal.valueOf(item.getFloorPrice().getValue()));
//				listing.setBuyItNowPrice(BigDecimal.valueOf(item.getBuyItNowPrice().getValue()));
//			}
//		}
////		//产品详细信息
//		if (item.getProductListingDetails()!=null){
//			ProductListingDetailsType productListingDetailsType = item.getProductListingDetails();
//
//			ProductListingDetails listingDetails = new ProductListingDetails();
//			if (null != productListingDetailsType.getBrandMPN()){
//				BrandMPNType brandMPNType = productListingDetailsType.getBrandMPN();
//				BrandMPN brandMPN = new BrandMPN();
//				brandMPN.setMPN(brandMPNType.getMPN());
//				brandMPN.setBrand(brandMPNType.getBrand());
//				listingDetails.setBrandMPN(brandMPN);
//			}
//			listingDetails.setEAN(productListingDetailsType.getEAN());
//			listingDetails.setIncludeeBayProductDetails(productListingDetailsType.isIncludeeBayProductDetails());
//			if(productListingDetailsType.isIncludeStockPhotoURL()!=null) {
//				listingDetails.setIncludeeBayProductDetails(productListingDetailsType.isIncludeStockPhotoURL());
//			}
//			listingDetails.setISBN(productListingDetailsType.getISBN());
//			//productReferenceId 对应前端界面上的epid;
//			listingDetails.setProductReferenceID(productListingDetailsType.getProductReferenceID());
//			if(productListingDetailsType.isReturnSearchResultOnDuplicates()!=null) {
//				listingDetails.setReturnSearchResultOnDuplicates(productListingDetailsType.isReturnSearchResultOnDuplicates());
//			}
//			if (null != productListingDetailsType.getTicketListingDetails()){
//				TicketListingDetails ticketListingDetails =new TicketListingDetails();
//				TicketListingDetailsType type = productListingDetailsType.getTicketListingDetails();
//				ticketListingDetails.setEventTitle(type.getEventTitle());
//				ticketListingDetails.setPrintedDate(type.getPrintedDate());
//				ticketListingDetails.setPrintedTime(type.getPrintedTime());
//				ticketListingDetails.setVenue(type.getVenue());
//				listingDetails.setTicketListingDetails(ticketListingDetails);
//			}
//			listingDetails.setUPC(productListingDetailsType.getUPC());
//			if(productListingDetailsType.isUseFirstProduct()!=null) {
//				listingDetails.setUseFirstProduct(productListingDetailsType.isUseFirstProduct());
//			}
//			if(productListingDetailsType.isUseStockPhotoURLAsGallery()!=null) {
//				listingDetails.setUseStockPhotoURLAsGallery(productListingDetailsType.isUseStockPhotoURLAsGallery());
//			}
//			listing.setProductListingDetails(JSONObject.toJSONString(listingDetails));
//		}
//
////		//启用商品卖家要求(不让符合以下要求的买家购买我的商品)
////		/**
////		 * json格式:
////		 * {"maximumItemCount":1,"maximumUnpaidItemStrikesCount":1,"maximumUnpaidItemStrikesDuration":"Days_14","minimumFeedbackScore":1,"shipToRegistrationCountry":true,"zeroFeedbackScore":true}
////		 */
//		listing.setDisableBuyerRequirements(item.isDisableBuyerRequirements()==null?true:item.isDisableBuyerRequirements());
//
////		if (vo.getListingType() ==2){
////			//变体属性组装
////			item.setVariations(buildVariationsType(vo));
////		}
//
////		//刊登天数
//		listing.setListingDuration(item.getListingDuration());
////		//物品所在地
//		listing.setLocal(item.getLocation());
////		//物品所在国家
//		listing.setCountry(item.getCountry().value());
////		//支付方法    付款说明
//		BuyerPaymentMethodCodeType[] paymentMethodCodeType = item.getPaymentMethods();
//		StringBuffer paymentOption = new StringBuffer("[");
//		if(paymentMethodCodeType!=null){
//			boolean namevalueBool = true;
//			for(BuyerPaymentMethodCodeType payment:paymentMethodCodeType){
//				if(namevalueBool){
//					namevalueBool = false;
//					paymentOption.append("\""+payment.value()+"\"");
//				}else {
//					paymentOption.append(",\""+payment.value()+"\"");
//				}
//			}
//		}
//		paymentOption.append("]");
//		listing.setPaymentOption(paymentOption.toString());
//
//
////		//是否立即付款
//		listing.setAutoPay(item.isAutoPay());
//
//		listing.setPlatformSku(item.getSKU());
//
////		//paypal邮箱地址
//		listing.setPaypal(item.getPayPalEmailAddress());
////		//物品新旧程度
//		listing.setConditionId(""+item.getConditionID());
////		//物品状况描述 (卖方使用这个字符串字段来更清楚地描述不是全新产品的状态)
//		listing.setConditionDescription(item.getConditionDescription());
////		//发货时间
//		listing.setDispatchTimeMax(item.getDispatchTimeMax());
////		//货运政策
//		//item.setShippingDetails(buildShippingDetails(vo));
//
////		//退款政策
//		listing.setReturnPolicy(JSONObject.toJSONString(item.getReturnPolicy(), SerializerFeature.WriteMapNullValue));
//
//		return listing;
//	}

	/**
	 * 当刊登属性为多属性时。处理变体信息
	 * 文档参考地址:https://developer.ebay.com/Devzone/XML/docs/Reference/eBay/AddFixedPriceItem.html
	 * 详细的组装格式请参考文档
	 * json格式:
	 * [{"mpn":"999","multiattribute":"[{\"custom\":false,\"variantKey\":\"color\",\"variantValue\":\"red\"},{\"custom\":false,\"variantKey\":\"size\",\"variantValue\":\"100\"}]","plSku":"plsku","platformSku":"ptsku","quantity":"1","startPrice":"123456","upc":"aaaaa"},{"mpn":"999234","multiattribute":"[{\"custom\":false,\"variantKey\":\"color\",\"variantValue\":\"red\"},{\"custom\":false,\"variantKey\":\"size\",\"variantValue\":\"100\"}]","plSku":"pls423ku","platformSku":"2ptsku","quantity":"21","startPrice":"2123456","upc":"1aaaaa"}]
	 * [{"picture":"111,222,33","variantKey":"11111","variantValue":"222222222"},{"picture":"111,222,33","variantKey":"333","variantValue":"333"}]
	 * @param vo
	 * @return
	 */
	public Map<String,Object> setVariations(ItemType item,String sellerId,Long empowerId){
		Map<String ,Object> map = Maps.newHashMap();
		VariationsType variationsType = item.getVariations();
		VariationType[] variant=variationsType.getVariation();
		if(variant!=null){
			List<EbayPublishListingVariant> listVariant = Lists.newArrayList();
			for (VariationType vt:variant) {
				EbayPublishListingVariant ebayListingVariant = new EbayPublishListingVariant();
				ebayListingVariant.setPlatformSku(vt.getSKU());
				ebayListingVariant.setQuantity(vt.getQuantity());
				if(vt.getStartPrice()!=null) {
					ebayListingVariant.setStartPrice(BigDecimal.valueOf(vt.getStartPrice().getValue()));
				}
				if(vt.getVariationProductListingDetails()!=null) {
					ebayListingVariant.setEan(vt.getVariationProductListingDetails().getEAN());
					ebayListingVariant.setUpc(vt.getVariationProductListingDetails().getUPC());
					ebayListingVariant.setIsbn(vt.getVariationProductListingDetails().getISBN());
				}

				try {
					String skuMaps = remoteCommodityService.getSkuMapByPlatformSku(OrderRuleEnum.platformEnm.E_BAU.getPlatform(), empowerId.toString(),vt.getSKU());
					List<EbayPublishListingVariantSkus> listVariantSkus = Lists.newArrayList();
					if(skuMaps!=null) {
						String result = Utils.returnRemoteResultDataString(skuMaps, "转换失败");
						JSONObject json = JSONObject.parseObject(result);

						if (json != null){
							JSONArray skuList = json.getJSONArray("skuList");
							if(skuList!=null && skuList.size()>0) {

								for (int i = 0; i < skuList.size(); i++) {
									JSONObject obj = skuList.getJSONObject(i);
									String systemSku = obj.getString("systemSku");
//                            		String specValueEn = obj.getString("specValueEn");
//                            		String specValueCn = obj.getString("specValueCn");
//                            		String commodityNameCn = obj.getString("commodityNameCn");
//                            		String commodityNameEn = obj.getString("commodityNameEn");
									Integer skuNum = obj.getInteger("skuNum");
									if(skuNum!=null && skuNum>1) {
										EbayPublishListingVariantSkus variantSkus = new EbayPublishListingVariantSkus();
										variantSkus.setPlSku(systemSku);
										variantSkus.setPlSkuNumber(skuNum);
										listVariantSkus.add(variantSkus);
									}
									if(i==0){//第一个sku为主sku
										ebayListingVariant.setPlSku(systemSku);
									}
								}
							}
						}
					}
					ebayListingVariant.setListVariantSkus(listVariantSkus);
				}catch (Exception e){
					e.printStackTrace();
				}


				NameValueListArrayType nameValueListArray= vt.getVariationSpecifics();
				NameValueListType[] nameValueList = nameValueListArray.getNameValueList();
				StringBuffer multiattribute = new StringBuffer("[");
				if(nameValueList!=null && nameValueList.length>0) {
					boolean bool = true;
					for (NameValueListType namevalue : nameValueList) {

						JSONObject jvariant = new JSONObject();
						jvariant.put("custom",false);
						jvariant.put("variantKey",namevalue.getName());
						StringBuffer variantValue = new StringBuffer("");
						if(namevalue.getValue()!=null){
							boolean strbool = true;
							for (String str:namevalue.getValue()){
								if(strbool){
									strbool = false;
									variantValue.append(str);
								}else {
									variantValue.append(","+str);
								}
							}
						}
						jvariant.put("variantValue",variantValue.toString());
						if(bool) {
							bool =false;
							multiattribute.append(jvariant.toJSONString());
						}else {
							multiattribute.append(","+jvariant.toJSONString());
						}
					}
				}
				multiattribute.append("]");
				ebayListingVariant.setMultiattribute(multiattribute.toString());

				listVariant.add(ebayListingVariant);


				map.put("listVariant",listVariant);

			}

			List<EbayPublishListingVariantPicture> pictureList = Lists.newArrayList();
			PicturesType[] pictures = variationsType.getPictures();
			if(pictures!=null && pictures.length>0){
				for(PicturesType picturesType :pictures){
					String variationSpecificName = picturesType.getVariationSpecificName();
					VariationSpecificPictureSetType[]  variationSpecificPictureSet = picturesType.getVariationSpecificPictureSet();
					if(variationSpecificPictureSet!=null && variationSpecificPictureSet.length>0){
						for(VariationSpecificPictureSetType variationSpecificPictureSetType:variationSpecificPictureSet){
							EbayPublishListingVariantPicture ebayPublishListingVariantPicture = new EbayPublishListingVariantPicture();
							ebayPublishListingVariantPicture.setVariantKey(variationSpecificName);
							ebayPublishListingVariantPicture.setVariantValue(variationSpecificPictureSetType.getVariationSpecificValue());
							String[] pictureURL = variationSpecificPictureSetType.getPictureURL();
							if(pictureURL!=null && pictureURL.length>0){
								StringBuffer picture = new StringBuffer("");
								boolean imgBool= true;
								for(String imageurl:pictureURL){
									if(imgBool){
										imgBool= false;
										picture.append(imageurl);
									}else {
										picture.append(","+imageurl);
									}
								}
								ebayPublishListingVariantPicture.setPicture(picture.toString());
							}
							pictureList.add(ebayPublishListingVariantPicture);
						}
					}
				}
				map.put("pictureList",pictureList);
			}
		}
		return map;
	}


	/**
	 * 运输政策https://developer.ebay.com/Devzone/XML/docs/Reference/eBay/types/ShippingServiceOptionsType.html#ShippingServiceAdditionalCost
	 * shippingServiceAdditionalCost  如果同一买家购买同一系列产品的多个数量，则每件额外产品的运费
	 *接收到的json格式:
	 *{"excludeShipToLocation":"中国,美国","globalShipping":true,"internationalShippingServiceOptions":{"shipToLocation":"A","shippingService":"国际运输服务","shippingServiceAdditionalCost":"运输附加费","shippingServiceCost":"运输费用","shippingServicePriority":1},"paymentInstructions":"这是运运输","shippingServiceOptions":{"freeShipping":true,"shippingService":"服务1","shippingServiceAdditionalCost":"服务附加费","shippingServiceCost":"运费","shippingServicePriority":2}}
	 * @param item
	 * @return
	 */
	public EbayPublishListingShipping setShippingDetails(ItemType item) {
		EbayPublishListingShipping listingShipping = new EbayPublishListingShipping();
		ShippingDetailsType sd = item.getShippingDetails();

		listingShipping.setPaymentInstructions(sd.getPaymentInstructions());  //付款描述
		//sd.setShippingType(ShippingTypeCodeType.FLAT); //付款方式。 默认为flat
		//是否发货到全球
		listingShipping.setGlobalShipping(sd.isGlobalShipping());
		if(sd.getExcludeShipToLocation()!=null && sd.getExcludeShipToLocation().length>0 ){
			StringBuffer excludeShipToLocation = new StringBuffer("");
			boolean namevalueBool = true;
			for(String valueStr:sd.getExcludeShipToLocation()){
				if(namevalueBool){
					namevalueBool = false;
					excludeShipToLocation.append(valueStr);
				}else {
					excludeShipToLocation.append(","+valueStr);
				}
			}
			listingShipping.setExcludeShipToLocation(excludeShipToLocation.toString());
		}

		//国内运输方案
		ShippingServiceOptionsType[] array = sd.getShippingServiceOptions();
		if(array!=null && array.length>0){
			List<EbayPublishListingShippingTransport> listShippingServiceOptions = Lists.newArrayList();
			for(ShippingServiceOptionsType shippingServiceOptions:array){
				EbayPublishListingShippingTransport serviceOptions = new EbayPublishListingShippingTransport();
				serviceOptions.setTransportType(1);
				serviceOptions.setFreeShipping(shippingServiceOptions.isFreeShipping()==null ?false:shippingServiceOptions.isFreeShipping());
				serviceOptions.setShippingService(shippingServiceOptions.getShippingService());
				serviceOptions.setShippingServicePriority(shippingServiceOptions.getShippingServicePriority());

				AmountType additionalCostAmountType = shippingServiceOptions.getShippingServiceAdditionalCost();
				if(additionalCostAmountType!=null) {
					serviceOptions.setShippingServiceAdditionalCost(additionalCostAmountType.getValue()+"");
				}
				AmountType serviceCostAmountType = shippingServiceOptions.getShippingServiceAdditionalCost();
				if(serviceCostAmountType!=null) {
					serviceOptions.setShippingServiceCost(serviceCostAmountType.getValue()+"");
				}

				listShippingServiceOptions.add(serviceOptions);
			}


			listingShipping.setShippingServiceOptions(listShippingServiceOptions);
		}

		//国际运输方案
		InternationalShippingServiceOptionsType[] serviceOptionarray = sd.getInternationalShippingServiceOption();
		if(serviceOptionarray!=null && serviceOptionarray.length>0){

			List<EbayPublishListingShippingTransport> listInternationalShippingServiceOption = Lists.newArrayList();
			for(InternationalShippingServiceOptionsType international:serviceOptionarray){
				EbayPublishListingShippingTransport option = new EbayPublishListingShippingTransport();
				option.setTransportType(2);
				option.setShippingService(international.getShippingService());
				option.setShippingServicePriority(international.getShippingServicePriority());
				AmountType additionalCostAmountType = international.getShippingServiceAdditionalCost();
				if(additionalCostAmountType!=null) {
					option.setShippingServiceAdditionalCost(additionalCostAmountType.getValue()+"");
				}
				AmountType serviceCostAmountType = international.getShippingServiceAdditionalCost();
				if(serviceCostAmountType!=null) {
					option.setShippingServiceCost(serviceCostAmountType.getValue()+"");
				}

				option.setShipToLocation(JSONObject.toJSONString(international.getShipToLocation()));
				listInternationalShippingServiceOption.add(option);
			}

			listingShipping.setInternationalShippingServiceOptions(listInternationalShippingServiceOption);
		}

		return listingShipping;
	}


	//
	public EbayPublishBuyerRequirements setEbayPublishBuyerRequirements(ItemType item){
		EbayPublishBuyerRequirements buyerRequirements = new EbayPublishBuyerRequirements();

		BuyerRequirementDetailsType brDetails =item.getBuyerRequirementDetails();
		if(brDetails!=null) {
			buyerRequirements.setShipToRegistrationCountry(brDetails.isShipToRegistrationCountry()); //主要运送地址在我的运送范围之外
			buyerRequirements.setZeroFeedbackScore(brDetails.isZeroFeedbackScore()); //启用信用指标小于0的
			MaximumItemRequirementsType itemRequrementsType = brDetails.getMaximumItemRequirements();
			if (itemRequrementsType != null) {
				//在过去10天内曾出价或购买我的物品，已达到我所设定的限制 
				buyerRequirements.setMaximumItemCount(itemRequrementsType.getMaximumItemCount());
				//这项限制只选用于买家信用指数等于或低于
				buyerRequirements.setMinimumFeedbackScore(itemRequrementsType.getMinimumFeedbackScore());
			}
			MaximumUnpaidItemStrikesInfoType maximumUnpaidItemStrikesInfoType = brDetails.getMaximumUnpaidItemStrikesInfo();
			if (maximumUnpaidItemStrikesInfoType != null) {
				buyerRequirements.setMaximumUnpaidItemStrikesCount(maximumUnpaidItemStrikesInfoType.getCount());
				buyerRequirements.setMaximumUnpaidItemStrikesDuration(maximumUnpaidItemStrikesInfoType.getPeriod().value());
			}
		}
		return buyerRequirements;
	}

	//产品属性
	public List<EbayPublishListingAttribute> setEbayPublishListingAttribute(ItemType item){
		List<EbayPublishListingAttribute> listAttribute = Lists.newArrayList();
		NameValueListArrayType nameValueListArrayType = item.getItemSpecifics();
		if(nameValueListArrayType!=null){
			NameValueListType[] nameValueListTypeList = nameValueListArrayType.getNameValueList();
			if(nameValueListTypeList!=null){

				for (NameValueListType nameValue:nameValueListTypeList){
					EbayPublishListingAttribute attribute = new EbayPublishListingAttribute();
					attribute.setAttributeKey(nameValue.getName());
					String [] attributeValueStr = nameValue.getValue();
					StringBuffer attributeValue = new StringBuffer("[");
					boolean namevalueBool = true;
					if(attributeValueStr!=null){
						for(String valueStr:attributeValueStr){
							if(namevalueBool){
								namevalueBool = false;
								attributeValue.append("\""+valueStr+"\"");
							}else {
								attributeValue.append(",\""+valueStr+"\"");
							}
						}
					}
					attributeValue.append("]");
					attribute.setAttributeValue(attributeValue.toString());
					listAttribute.add(attribute);
				}
			}
		}
		return listAttribute;
	}

	/**
	 * 根据sku获取spu
	 * @param sku
	 * @return
	 */
	public String getPlSpu(String plSku){
		try {
			String commodityResult = remoteCommodityService.managerCommodity("1","1", null,
					null, null, null,
					null, null, null,
					null, plSku,null,null,
					null);
			String dataString = Utils.returnRemoteResultDataString(commodityResult, "商品服务异常");
			JSONObject object = JSONObject.parseObject(dataString);
			JSONObject pageInfo = object.getJSONObject("pageInfo");
			JSONArray list = pageInfo.getJSONArray("list");
			if(list != null && list.size() > 0){
				JSONObject o = (JSONObject) list.get(0);
				String spu = o.getString("SPU");
				if(spu!=null){
					return  spu;
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
