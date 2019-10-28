package com.rondaful.cloud.seller.service.impl;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.ebay.sdk.call.*;
import com.ebay.soap.eBLBaseComponents.*;
import com.google.common.collect.Lists;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.mapper.*;
import com.rondaful.cloud.seller.service.EbayRecordAttributeSelectService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.util.eBayUtil;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.seller.entity.ebay.BrandMPN;
import com.rondaful.cloud.seller.entity.ebay.InternationalShippingServiceOption;
import com.rondaful.cloud.seller.entity.ebay.ListingVariant;
import com.rondaful.cloud.seller.entity.ebay.ProductListingDetails;
import com.rondaful.cloud.seller.entity.ebay.ShippingServiceOptions;
import com.rondaful.cloud.seller.entity.ebay.TicketListingDetails;
import com.rondaful.cloud.seller.entity.ebay.store.CustomCategory;
import com.rondaful.cloud.seller.entity.ebay.store.Store;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;
import com.rondaful.cloud.seller.service.IEbayBaseService;
import com.rondaful.cloud.seller.vo.PublishListingVO;


/**
 *ebay api参考地址 
 *https://developer.ebay.com/Devzone/XML/docs/Reference/eBay/index.html#CallIndex 
 *开发者中心
 *https://developer.ebay.com/devzone/account
 * @author songjie
 *
 */

@Service
public class EbayBaseServiceImpl implements IEbayBaseService {

    private final Logger logger = LoggerFactory.getLogger(EbayBaseServiceImpl.class);
    
    private String EBAY_URL_REQUEST = "https://api.ebay.com/wsapi";
	
	@Autowired
	private EbaySiteMapper siteMapper;

	@Autowired
	private EbayCountryMapper countryMapper;

	@Autowired
	private EbayProductCategoryMapper categoryMapper;

	@Autowired
	private EbayProductCategoryAttributeMapper categoryAttributeMapper;

	@Autowired
	private EbayProductCategoryFeatureMapper featureMapper;
	
	@Autowired
	private EbayPublishListingErrorMapper listingErrorMapper;
	
	@Autowired
	private EbayPublishListingNewMapper listingMapper;
	
	@Autowired
	private EbaySiteDetailMapper siteDetailMapper;
	
    @Autowired
    private AuthorizationSellerService authorizationSellerService;

	@Autowired
	private EbayRecordAttributeSelectService ebayRecordAttributeSelectService;
    
    @Autowired
    private RedisUtils redisUtils;

	@SuppressWarnings("unchecked")
	@Override
	public List<EbaySite> findSiteByValue(EbaySite site) {
		List<EbaySite> page = null;
		String key = "ebay:allSite";
		if (StringUtils.isNotEmpty(site.getSiteName())){
			key = site.getSiteName();
		}
		if (redisUtils.exists(key)){
			return (List<EbaySite>) redisUtils.get(key);
		}
		page = siteMapper.page(site);
		redisUtils.set(key, page,86400L);
		return page;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EbayCountry> findCountryByValue(EbayCountry country) {
		String key = "ebay:country-"+country.getType();
		if (redisUtils.exists(key)){
			return (List<EbayCountry>) redisUtils.get(key);
		}
		List<EbayCountry> selectByValue = countryMapper.selectByValue(country);
		redisUtils.set(key, selectByValue,86400L);
		return selectByValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EbayProductCategory> findCategoryByValue(EbayProductCategory category) {
		if (StringUtils.isBlank(category.getSite())){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "请检查参数[site]");
		}
		String key = "ebay:"+category.getSite()+"-"+category.getCategorylevel()+"-"+category.getCategoryparentid();
		if (redisUtils.exists(key)){
			return (List<EbayProductCategory>) redisUtils.get(key);
		}
		List<EbayProductCategory> page = categoryMapper.page(category);
		redisUtils.set(key, page,86400L);
		return page;
	}

	@SuppressWarnings("unchecked")
	@Override  
	public Map<String, Object> getCategoryAttributeAndFeatures(EbayProductCategory category) {
		if (StringUtils.isBlank(category.getSite()) || StringUtils.isBlank(category.getCategoryid())) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "请检查参数[categoryid,site]");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		EbayProductCategoryAttribute attributeObj = new EbayProductCategoryAttribute();
		attributeObj.setSite(category.getSite());
		attributeObj.setCategoryId(category.getCategoryid());
		String key = "ebay:attribute"+"-"+category.getSite()+"-"+category.getCategoryid();
		List<EbayProductCategoryAttribute> attributePage = null;
		if (redisUtils.exists(key)){
			attributePage =  (List<EbayProductCategoryAttribute>) redisUtils.get(key);
		}else{
			attributePage = categoryAttributeMapper.page(attributeObj);
			redisUtils.set(key, attributePage,2592000L);
		}
		logger.info("站点{}获取{}属性{}条",category.getSite(),category.getCategoryid(),attributePage.size());
		if (CollectionUtils.isNotEmpty(attributePage)) {
			map.put("attributeList", attributePage);
		} 
		EbayProductCategoryFeature feature = new EbayProductCategoryFeature();
		feature.setSite(category.getSite());
		feature.setCategoryId(category.getCategoryid());
		List<EbayProductCategoryFeature> featurepage = null;
		key = "ebay:feature"+"-"+category.getSite()+"-"+category.getCategoryid();
		if (redisUtils.exists(key)){
			featurepage =  (List<EbayProductCategoryFeature>) redisUtils.get(key);
		}else{
			featurepage = featureMapper.page(feature);
			//将小字转为大写供前端统一处理
			featurepage.forEach(featureObj ->{
				String listingDuration = featureObj.getListingDuration();
				listingDuration = listingDuration.replace("chinese", "Chinese");
				listingDuration = listingDuration.replace("fixedPriceItem", "FixedPriceItem");
				featureObj.setListingDuration(listingDuration);
			});
			redisUtils.set(key, featurepage,2592000L);
		}
		logger.info("站点{}获取{}特征{}条",category.getSite(),category.getCategoryid(),featurepage.size());
		if (CollectionUtils.isNotEmpty(featurepage)) {
			map.put("feature", featurepage);
		}
		return map;
	}

	/**
	 * 刊登结束后对listing表进行状态修改
	 * @param id
	 * @param status
	 * @param itemId
	 * @param onLineTime 
	 */
	public void updateListingById(Long id,Integer status,String itemId,Date onLineTime){
		EbayPublishListingNew obj = new EbayPublishListingNew();
		obj.setId(id);
		obj.setStatus(status);
		obj.setUpdateTime(new Date());
		if (StringUtils.isNotBlank(itemId))
			obj.setItemid(itemId);
		if (null != onLineTime)
			obj.setOnlineTime(onLineTime);
		listingMapper.updateByPrimaryKeySelective(obj);	
	}
	
	@Override
	public String addItem(PublishListingVO vo)  {
		logger.info("ebay 刊登addItem start>>{}",vo.getId());
		ItemType item = buildItem(vo);
		AddItemCall api = null;
		try {
			api =  new AddItemCall(getApiContext(String.valueOf(vo.getEmpowerId())));
			api.setItem(item);
			api.setSite(SiteCodeType.fromValue(vo.getSite())); //指定站点
			api.addItem();
			logger.info("ebay 刊登addItem end>>{},{}",vo.getId(),item.getItemID());
			updateListingById(vo.getId(), 4, item.getItemID(), new Date());
			listingErrorMapper.deleteByListingId(vo.getId());
			ebayRecordAttributeSelectService.saveEbayRecordAttributeSelect(vo.getId());
			return item.getItemID();
		} catch (Exception e){
			e.printStackTrace();
			setListingError(api.getResponseObject(), vo.getId());
			updateListingById(vo.getId(), 5, null, null);	
		}
		return null;
	}
	
	@Override
	public String addFixedPriceItem(PublishListingVO vo) {
		logger.info("ebay 多属性刊登 start>>{}",vo.getId());
		ItemType item = buildItem(vo);
		AddFixedPriceItemCall api = null;
		try {
			api =  new AddFixedPriceItemCall(getApiContext(String.valueOf(vo.getEmpowerId())));
			api.setItem(item);
			api.setSite(SiteCodeType.fromValue(vo.getSite())); //指定站点
			api.addFixedPriceItem();
			logger.info("ebay 多属性刊登 end>>{},{}",vo.getId(),item.getItemID());
			updateListingById(vo.getId(), 4, item.getItemID(), new Date());
			listingErrorMapper.deleteByListingId(vo.getId());
			ebayRecordAttributeSelectService.saveEbayRecordAttributeSelect(vo.getId());
			return item.getItemID();
		} catch (Exception e){
			setListingError(api.getResponseObject(), vo.getId());
			updateListingById(vo.getId(), 5, null, null);
		}
		return null;
	}
	
	@Override
	public String reviseFixedPriceItem(PublishListingVO vo) {
		logger.info("ebay 多属性刊登修改 start>>{},{}",vo.getId(),vo.getItemid());
		ItemType item = buildItem(vo);
		ReviseFixedPriceItemCall reviseItemCall = null;
		try {
			reviseItemCall = new ReviseFixedPriceItemCall(getApiContext(String.valueOf(vo.getEmpowerId())));
			item.setItemID(vo.getItemid());
			reviseItemCall.setItemToBeRevised(item);
			reviseItemCall.reviseFixedPriceItem();
			logger.info("ebay 多属性刊登修改 end>>{},{}",vo.getId(),vo.getItemid());
			EbayPublishListingNew obj = new EbayPublishListingNew();
			obj.setId(vo.getId());
			obj.setUpdateStatus(2);
			obj.setUpdateTime(new Date());
	 		listingMapper.updateByPrimaryKeySelective(obj);
			listingErrorMapper.deleteByListingId(vo.getId()); 
			return vo.getItemid();
		} catch (Exception e){
			EbayPublishListingNew obj = new EbayPublishListingNew();
			obj.setId(vo.getId());
			obj.setUpdateStatus(3);
			obj.setUpdateTime(new Date());
			listingMapper.updateByPrimaryKeySelective(obj);
			setListingError(reviseItemCall.getResponseObject(), vo.getId());
		}		
		return null;
	}
	
	@Override
	public String reviseItem(PublishListingVO vo){
		logger.info("ebay 刊登修改 start>>{},{}",vo.getId(),vo.getItemid());
		ItemType item = buildItem(vo);
		ReviseItemCall reviseItemCall = null;
		try {
			reviseItemCall = new ReviseItemCall(getApiContext(String.valueOf(vo.getEmpowerId())));
			item.setItemID(vo.getItemid());
			reviseItemCall.setItemToBeRevised(item);
			reviseItemCall.reviseItem();
			logger.info("ebay 刊登修改 start>>{},{}",vo.getId(),vo.getItemid());

			EbayPublishListingNew obj = new EbayPublishListingNew();
			obj.setId(vo.getId());
			obj.setUpdateStatus(2);
			obj.setUpdateTime(new Date());
			listingMapper.updateByPrimaryKeySelective(obj);

			listingErrorMapper.deleteByListingId(vo.getId()); 
			return vo.getItemid();
		} catch (Exception e){
			e.printStackTrace();
			setListingError(reviseItemCall.getResponseObject(), vo.getId());

			EbayPublishListingNew obj = new EbayPublishListingNew();
			obj.setId(vo.getId());
			obj.setUpdateStatus(3);
			obj.setUpdateTime(new Date());
			listingMapper.updateByPrimaryKeySelective(obj);
		}		
		return null;
	}
	
	/**
	 * 刊登日志收集
	 * @param responseObject
	 * @param id
	 * @param status  只有当刊登时才将status传入  status = 5
	 */
	public void setListingError(AbstractResponseType responseObject,Long id){
		try {
			ErrorType[] errors = null;
			if (null == responseObject){
				ErrorType type = new ErrorType();
				//不能得到的错误信息全设为超时
				type.setShortMessage("connection timeout");
				type.setLongMessage("connection timeout");
				type.setSeverityCode(SeverityCodeType.WARNING);
				errors = new ErrorType[]{type};
			}else{
				errors = responseObject.getErrors();
			}
			List<EbayPublishListingError> errorList = new ArrayList<EbayPublishListingError>();
			for (ErrorType type : errors){
				EbayPublishListingError error = new EbayPublishListingError();
				error.setListingId(id.intValue());
				BeanUtils.copyProperties(type, error);
				if (null != responseObject)
					error.setMsg(responseObject.getMessage());
				error.setSeverityCode(type.getSeverityCode().value());
				error.setCreationTime(new Date());
				errorList.add(error);
			}
			listingErrorMapper.batchInsert(errorList);
		} catch (Exception e) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getMsg(), "ebay日志记录出错");
		}
		
	}
	
	@Override
	public String endItem(PublishListingVO vo){
		String itemId = vo.getItemid();
		logger.info("ebay 下架 start>>{}",itemId);
		EndItemCall endItemCall = null;
		try {
			endItemCall = new EndItemCall(getApiContext(String.valueOf(vo.getEmpowerId())));
		    endItemCall.setItemID(itemId);
		    endItemCall.setEndingReason(EndReasonCodeType.NOT_AVAILABLE);
		    endItemCall.endItem();
		    logger.info("ebay 下架 end>>{}",itemId);
		    listingErrorMapper.deleteByListingId(vo.getId()); 
		    return itemId;
		} catch (Exception e) {
			logger.error("{}产品下架失败",itemId);
			setListingError(endItemCall.getResponseObject(), vo.getId());
		}
		return null;
	}
	
	@Override
	public String endFixedPriceItem(PublishListingVO vo) {
		String itemId = vo.getItemid();
		logger.info("ebay 多属性下架 start>>{}",itemId);
		EndFixedPriceItemCall endFixedPriceItemCall = null;
		try {
			endFixedPriceItemCall = new EndFixedPriceItemCall(getApiContext(String.valueOf(vo.getEmpowerId())));
			endFixedPriceItemCall.setItemID(itemId);
			endFixedPriceItemCall.setEndingReason(EndReasonCodeType.NOT_AVAILABLE);
			endFixedPriceItemCall.endFixedPriceItem();
		    logger.info("ebay 多属性下架 end>>{}",itemId);
			listingErrorMapper.deleteByListingId(vo.getId()); 
		    return itemId;
		} catch (Exception e) {
			logger.error("{}产品多属性下架失败",itemId);
			setListingError(endFixedPriceItemCall.getResponseObject(), vo.getId());
		}
		return null;
	}
	
	/**
	 * ebay 刊登数据组装
	 * @param vo 数据来源对象
	 * @return
	 */
	public ItemType buildItem(PublishListingVO vo){
		ItemType item = new ItemType();
		//标题
		item.setTitle(vo.getTitle());
		//站点
		item.setSite(SiteCodeType.fromValue(vo.getSite()));
		if (StringUtils.isNotBlank(vo.getSubTitle())){
			//副标题要额外收费
			item.setSubTitle(vo.getSubTitle()); 
		}
		//描述
		item.setDescription(vo.getListingDesc());
		//产品分类
		CategoryType cat = new CategoryType();
		cat.setCategoryID(vo.getProductCategory1());
		item.setPrimaryCategory(cat);
		//产品第二分类
		if (StringUtils.isNotBlank(vo.getProductCategory2())){
			CategoryType second = new CategoryType();
			second.setCategoryID(vo.getProductCategory2());
			item.setSecondaryCategory(second);
		}
		//店铺
		if (StringUtils.isNotBlank(vo.getStoreCategory1())){
			StorefrontType frontType = new StorefrontType();
			frontType.setStoreCategoryID(Long.valueOf(vo.getStoreCategory1()));
			if (StringUtils.isNotBlank(vo.getStoreCategory2())){
				frontType.setStoreCategory2ID(Long.valueOf(vo.getStoreCategory2()));
			}
			item.setStorefront(frontType);
		}
		//产品图片
		String picture = vo.getPicture();
		if (StringUtils.isNotEmpty(picture)){
			String[] split = picture.split(",");
			PictureDetailsType pic = new PictureDetailsType();
			pic.setPictureURL(split);
			//橱窗展示
			if(vo.getGalleryTypeCode()!=null){
			    //None Featured Gallery Plus
                switch (vo.getGalleryTypeCode()){
                    case "None":
                        pic.setGalleryType(GalleryTypeCodeType.NONE);
                        break;
                    case "Featured":
                        pic.setGalleryType(GalleryTypeCodeType.FEATURED);
                        break;
                    case "Gallery":
                        pic.setGalleryType(GalleryTypeCodeType.GALLERY);
                        break;
                    case "Plus":
                        pic.setGalleryType(GalleryTypeCodeType.PLUS);
                        break;
                    default:{
                        pic.setGalleryType(GalleryTypeCodeType.GALLERY);
                    }
                }
            }else{
                pic.setGalleryType(GalleryTypeCodeType.GALLERY);
            }
			item.setPictureDetails(pic);
		}
		//产品属性
		List<NameValueListType> nameValueListTypeList = new ArrayList<NameValueListType>();
		List<EbayPublishListingAttribute> attributeList = vo.getAttributeValue();
		if (CollectionUtils.isNotEmpty(attributeList)){
			for (EbayPublishListingAttribute listingAttribute : attributeList){
				NameValueListType nameValueListType = new NameValueListType();
				nameValueListType.setName(listingAttribute.getAttributeKey());
				String attributeValue = listingAttribute.getAttributeValue();
				List<String> list = JSONObject.parseArray(attributeValue, String.class);
				String [] attributeValueStr = new String[list.size()];
				list.toArray(attributeValueStr);
				nameValueListType.setValue(attributeValueStr);
				nameValueListTypeList.add(nameValueListType);
			}
			NameValueListType[] str = new NameValueListType[nameValueListTypeList.size()];
			NameValueListType[] array = nameValueListTypeList.toArray(str);
			NameValueListArrayType arryType = new NameValueListArrayType();
			arryType.setNameValueList(array);
			item.setItemSpecifics(arryType);
		}
		
		//币种,价格方式设置
		item.setCurrency(CurrencyCodeType.fromValue(vo.getCurrency())); //设置币种
		item.setListingType(ListingTypeCodeType.FIXED_PRICE_ITEM);  //设置为固价
		BigDecimal startPrice = vo.getStartPrice(); //起始价
		if (vo.getListingType() !=2){ //多属性时，值放入变体信息保存
			AmountType startAmountType = new AmountType();
			startAmountType.setValue(startPrice.doubleValue());
			item.setStartPrice(startAmountType);
			item.setQuantity(vo.getQuantity());
			item.setSKU(vo.getPlatformSku());
		}
		if (vo.getListingType() == 1){ //如果是单属性一口价  查看是否开启议价模式
			Boolean bestOfferEnabled = vo.getBestOfferEnabled();
			if (bestOfferEnabled){ //使用了议价模式
				BestOfferDetailsType detailsType = new BestOfferDetailsType();
				detailsType.setBestOfferEnabled(true);
				item.setBestOfferDetails(detailsType);
				//设置议价金额
				ListingDetailsType listingDetailsType = new ListingDetailsType();
				AmountType amountType = new AmountType();
				amountType.setValue(vo.getAutoAcceptPrice().doubleValue());
				listingDetailsType.setBestOfferAutoAcceptPrice(amountType);
				AmountType offer = new AmountType();
				offer.setValue(vo.getMinimumBestOfferPrice().doubleValue());
				listingDetailsType.setMinimumBestOfferPrice(offer);
				item.setListingDetails(listingDetailsType);
			}
		}
		if (vo.getListingType() == 3 ){  //如果是拍卖
			item.setListingType(ListingTypeCodeType.CHINESE); //设置为拍卖价
			if (null != vo.getFloorPrice() && null != vo.getBuyItNowPrice()){
				AmountType floorAmountType = new AmountType();
				BigDecimal floorPrice = vo.getFloorPrice();
				floorAmountType.setValue(floorPrice.doubleValue());
				item.setFloorPrice(floorAmountType);
				AmountType nowAmountType = new AmountType();
				BigDecimal buyItNowPrice = vo.getBuyItNowPrice();
				nowAmountType.setValue(buyItNowPrice.doubleValue());
				item.setBuyItNowPrice(nowAmountType);
			}
		}
		//产品详细信息 
		if (vo.getProductListingDetails()!=null){
			item.setProductListingDetails(buildProductListingDetailsType(vo));
		}
		//启用商品卖家要求(不让符合以下要求的买家购买我的商品)
		/**
		 * json格式:
		 * {"maximumItemCount":1,"maximumUnpaidItemStrikesCount":1,"maximumUnpaidItemStrikesDuration":"Days_14","minimumFeedbackScore":1,"shipToRegistrationCountry":true,"zeroFeedbackScore":true}
		 */
		if (!vo.getDisableBuyerRequirements()){
			EbayPublishBuyerRequirements buyerRequirements = vo.getDisableBuyerRequirementsValue();
			if (buyerRequirements!=null){
				item.setDisableBuyerRequirements(false);
				BuyerRequirementDetailsType brDetails = new BuyerRequirementDetailsType();
				brDetails.setShipToRegistrationCountry(buyerRequirements.getShipToRegistrationCountry()); //主要运送地址在我的运送范围之外
				brDetails.setZeroFeedbackScore(buyerRequirements.getZeroFeedbackScore()); //启用信用指标小于0的
				MaximumItemRequirementsType itemRequrementsType = new MaximumItemRequirementsType();
				//在过去10天内曾出价或购买我的物品，已达到我所设定的限制 
				itemRequrementsType.setMaximumItemCount(buyerRequirements.getMaximumItemCount());
				//这项限制只选用于买家信用指数等于或低于
				itemRequrementsType.setMinimumFeedbackScore(buyerRequirements.getMinimumFeedbackScore());
				brDetails.setMaximumItemRequirements(itemRequrementsType);
				MaximumUnpaidItemStrikesInfoType maximumUnpaidItemStrikesInfoType = new MaximumUnpaidItemStrikesInfoType();
				maximumUnpaidItemStrikesInfoType.setCount(buyerRequirements.getMaximumUnpaidItemStrikesCount());
				if(!StringUtils.isBlank(buyerRequirements.getMaximumUnpaidItemStrikesDuration())) {
					maximumUnpaidItemStrikesInfoType.setPeriod(PeriodCodeType.fromValue(buyerRequirements.getMaximumUnpaidItemStrikesDuration()));
				}
				brDetails.setMaximumUnpaidItemStrikesInfo(maximumUnpaidItemStrikesInfoType);
				item.setBuyerRequirementDetails(brDetails);
			}
		}
		if (vo.getListingType() ==2){
			//变体属性组装
			item.setVariations(buildVariationsType(vo));
		}
		//刊登天数
		item.setListingDuration(vo.getListingDuration());
		//物品所在地
		item.setLocation(vo.getLocal());
		//物品所在国家
		item.setCountry(CountryCodeType.fromValue(vo.getCountry()));
		//支付方法  印度站点的不默认paypal支付  付款说明
//		if (SiteCodeType.INDIA.value().equals(vo.getSite())){
			String paymentOption = vo.getPaymentOption();
			List<String> list = JSONObject.parseArray(paymentOption, String.class);
			if(CollectionUtils.isNotEmpty(list)){
			   List<BuyerPaymentMethodCodeType> codeTypeList = new ArrayList<>();
			   list.forEach(l->{
				   codeTypeList.add(BuyerPaymentMethodCodeType.fromValue(l));
			   });
			   BuyerPaymentMethodCodeType[] paymentMethodCodeType = new BuyerPaymentMethodCodeType[codeTypeList.size()];
			   codeTypeList.toArray(paymentMethodCodeType);
			   item.setPaymentMethods(paymentMethodCodeType);
			}
//		}else{
//			String paymentOption = vo.getPaymentOption();
//			List<String> list = JSONObject.parseArray(paymentOption, String.class);
//			List<BuyerPaymentMethodCodeType> codeTypeList = new ArrayList<>();
//			codeTypeList.add(BuyerPaymentMethodCodeType.PAY_PAL);
//			if(CollectionUtils.isNotEmpty(list)){
//				list.forEach(l->{
//					codeTypeList.add(BuyerPaymentMethodCodeType.fromValue(l));
//				});
//			}
//			BuyerPaymentMethodCodeType[] paymentMethodCodeType = new BuyerPaymentMethodCodeType[codeTypeList.size()];
//			codeTypeList.toArray(paymentMethodCodeType);
//			item.setPaymentMethods(paymentMethodCodeType);
//		}
		//是否立即付款
		if(vo.getAutoPay()!=null){
			item.setAutoPay(vo.getAutoPay());
		}
		//付款说明 字段取消
		//item.setPaymentInstructions();

		//paypal邮箱地址
		item.setPayPalEmailAddress(vo.getPaypal());
		//物品新旧程度  
		if (StringUtils.isNotBlank(vo.getConditionId()) && !"null".equals(vo.getConditionId()))
			item.setConditionID(Integer.valueOf(vo.getConditionId()));
		//物品状况描述 (卖方使用这个字符串字段来更清楚地描述不是全新产品的状态)
		item.setConditionDescription(vo.getConditionDescription());
		//发货时间
		item.setDispatchTimeMax(vo.getDispatchTimeMax());
		//货运政策
		item.setShippingDetails(buildShippingDetails(vo));
		//退款政策
	    item.setReturnPolicy(buildReturnPolicyDetails(vo));
	    logger.info("刊登数据组装{},{}",vo.getTitle(),JSONObject.toJSONString(item));
		return item;
	}
	
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
	public VariationsType buildVariationsType(PublishListingVO vo){
		VariationsType variationsType = new VariationsType();
		List<EbayPublishListingVariant> variantList = vo.getVariant();
		//变体信息处理
		if (variantList!=null && variantList.size()>0){
			//set 接收数据用来去掉重复的数据 ebay接收数据如果有重复的数据会给出异常,详细格式参考文档
			Map<String,Set<String>> variantSpecificsSetMap = new HashMap<>();
			List<VariationType> variationTypeList = new ArrayList<>();
			for (EbayPublishListingVariant listingVariant : variantList){
				VariationType variationType = new VariationType();
				variationType.setSKU(listingVariant.getPlatformSku());
				AmountType startPrice = new AmountType();
				startPrice.setValue(null == listingVariant.getStartPrice()?0:listingVariant.getStartPrice().doubleValue());
				variationType.setStartPrice(startPrice);
				variationType.setQuantity(listingVariant.getQuantity());

				//upc ean
				VariationProductListingDetailsType variationProductListingDetailsType = new VariationProductListingDetailsType();
				variationProductListingDetailsType.setEAN(listingVariant.getEan());
				variationProductListingDetailsType.setUPC(listingVariant.getUpc());
				variationProductListingDetailsType.setISBN(listingVariant.getIsbn());
				variationType.setVariationProductListingDetails(variationProductListingDetailsType);
				//upc ean

				String multiattribute = listingVariant.getMultiattribute(); //变体中的自定属性信息
				List<EbayPublishListingVariantValue> variantValueList = JSONObject.parseArray(multiattribute, EbayPublishListingVariantValue.class);
				
				List<NameValueListType> nameValueList = new ArrayList<>();
				for (EbayPublishListingVariantValue variantValue : variantValueList){
					if (variantSpecificsSetMap.containsKey(variantValue.getVariantKey())){
						Set<String> list = variantSpecificsSetMap.get(variantValue.getVariantKey());
						list.add(variantValue.getVariantValue());
						variantSpecificsSetMap.put(variantValue.getVariantKey(), list);
					}else{
						Set<String> list = new HashSet<String>();
						list.add(variantValue.getVariantValue());
						variantSpecificsSetMap.put(variantValue.getVariantKey(), list);	
					}
					NameValueListType nameValueListType = new NameValueListType();
					nameValueListType.setName(variantValue.getVariantKey());
					nameValueListType.setValue(variantValue.getVariantValue().split(","));
					nameValueList.add(nameValueListType);
				}
				NameValueListArrayType nameValueListArrayType = new NameValueListArrayType();
				NameValueListType [] nameValueArray = new NameValueListType[nameValueList.size()];
				nameValueListArrayType.setNameValueList(nameValueList.toArray(nameValueArray));
				variationType.setVariationSpecifics(nameValueListArrayType);
				variationTypeList.add(variationType);
				
			}
		
			List<NameValueListType> nameValueListTypeList = new ArrayList<>();
			for (Map.Entry<String, Set<String>> entry : variantSpecificsSetMap.entrySet()) { 
				NameValueListType nameValueListType = new NameValueListType();
				nameValueListType.setName(entry.getKey());
				Set<String> list = entry.getValue();
				String [] strArray = new String[list.size()];
				list.toArray(strArray);
				nameValueListType.setValue(list.toArray(strArray));
				nameValueListTypeList.add(nameValueListType);
			}
			NameValueListArrayType nameValueListArrayType = new NameValueListArrayType();
			NameValueListType [] nameValueListStr = new NameValueListType[nameValueListTypeList.size()];
			nameValueListArrayType.setNameValueList(nameValueListTypeList.toArray(nameValueListStr));
			variationsType.setVariationSpecificsSet(nameValueListArrayType);
			VariationType[] variationArray = new VariationType[variationTypeList.size()];
			variationsType.setVariation(variationTypeList.toArray(variationArray));
			
			//变体信息图片向ebay组装
			List<EbayPublishListingVariantPicture> pictureList = vo.getVariantPicture();
			if (pictureList!=null && pictureList.size()>0){
				//数据经map处理，将相同key集合起来。方便下面ebay变体图片处理
				Map<String,List<EbayPublishListingVariantPicture>> map = new HashMap<String,List<EbayPublishListingVariantPicture>>();
				for (EbayPublishListingVariantPicture p : pictureList){
					if (map.containsKey(p.getVariantKey())){
						List<EbayPublishListingVariantPicture> list = map.get(p.getVariantKey());
						list.add(p);
						map.put(p.getVariantKey(), list);
					}else{
						List<EbayPublishListingVariantPicture> list = new ArrayList<EbayPublishListingVariantPicture>();
						list.add(p);
						map.put(p.getVariantKey(), list);
					}
				}
				//图片处理
				List<PicturesType> picturesTypeList = new ArrayList<>();
				for (Map.Entry<String, List<EbayPublishListingVariantPicture>> entry : map.entrySet()) {
					List<EbayPublishListingVariantPicture> value = entry.getValue();
					PicturesType picturesType = new PicturesType();
					picturesType.setVariationSpecificName(entry.getKey());
					List<VariationSpecificPictureSetType> variationSpecificPictureSetTypeList = new ArrayList<>();
					for(EbayPublishListingVariantPicture pictureObj: value){
						VariationSpecificPictureSetType variationPictureType = new VariationSpecificPictureSetType();
						variationPictureType.setVariationSpecificValue(pictureObj.getVariantValue());
						if (StringUtils.isNotBlank(pictureObj.getPicture())){
							String[] pictureArray = pictureObj.getPicture().split(",");
							variationPictureType.setPictureURL(pictureArray);
						}
						variationSpecificPictureSetTypeList.add(variationPictureType);
					}
					VariationSpecificPictureSetType [] picTypeArray = new VariationSpecificPictureSetType[variationSpecificPictureSetTypeList.size()];
					picturesType.setVariationSpecificPictureSet(variationSpecificPictureSetTypeList.toArray(picTypeArray));
					picturesTypeList.add(picturesType);
				}
			PicturesType [] picturesTypeArray =new PicturesType[picturesTypeList.size()];
			picturesTypeList.toArray(picturesTypeArray);
			variationsType.setPictures(picturesTypeArray);
			}

		}
		return variationsType;
	}
	
	/**
	 * json格式:
	 * {"brandMPN":{"brand":"数值1","mPN":"数值2"},"eAN":"ean","iSBN":"isbn","includeStockPhotoURL":true,"includeeBayProductDetails":true,"productReferenceID":"epid","returnSearchResultOnDuplicates":true,"ticketListingDetails":{"eventTitle":"aaaaa","printedDate":"bbbb","printedTime":"ccc","venue":"cccccc"},"uPC":"upc","useFirstProduct":true,"useStockPhotoURLAsGallery":true}
	 * @param vo
	 * @return
	 */
	public ProductListingDetailsType buildProductListingDetailsType(PublishListingVO vo){
		ProductListingDetailsType productListingDetailsType = new ProductListingDetailsType();
		EbayPublishListingVariant productListingDetails = vo.getProductListingDetails();
//		ProductListingDetails listingDetails = JSONObject.toJavaObject(parseObject,ProductListingDetails.class);
		if (StringUtils.isNotBlank(productListingDetails.getMpn())){
			BrandMPNType brandMPNType = new BrandMPNType();
			brandMPNType.setMPN(productListingDetails.getMpn());
			//brandMPNType.setBrand(brandMPN.getBrand());
			productListingDetailsType.setBrandMPN(brandMPNType);
		}
		if (StringUtils.isNotBlank(productListingDetails.getEan())){
			productListingDetailsType.setEAN(productListingDetails.getEan());
		}
		if (StringUtils.isNotBlank(productListingDetails.getIsbn())){
			productListingDetailsType.setISBN(productListingDetails.getIsbn());
		}
		if (StringUtils.isNotBlank(productListingDetails.getUpc())){
			productListingDetailsType.setUPC(productListingDetails.getUpc());
		}
//		if (listingDetails.isIncludeeBayProductDetails()){
//			productListingDetailsType.setIncludeeBayProductDetails(listingDetails.isIncludeeBayProductDetails());
//		}
//		if (listingDetails.isIncludeStockPhotoURL()){
//			productListingDetailsType.setIncludeeBayProductDetails(listingDetails.isIncludeStockPhotoURL());
//		}

		//productReferenceId 对应前端界面上的epid;
		if (StringUtils.isNotBlank(productListingDetails.getProductReferenceID())){
			productListingDetailsType.setProductReferenceID(productListingDetails.getProductReferenceID());
		}
//		if (listingDetails.isReturnSearchResultOnDuplicates()){
//			productListingDetailsType.setReturnSearchResultOnDuplicates(listingDetails.isReturnSearchResultOnDuplicates());
//		}
//		if (null != listingDetails.getTicketListingDetails()){
//			TicketListingDetails ticketListingDetails = listingDetails.getTicketListingDetails();
//			TicketListingDetailsType type = new TicketListingDetailsType();
//			type.setEventTitle(ticketListingDetails.getEventTitle());
//			type.setPrintedDate(ticketListingDetails.getPrintedDate());
//			type.setPrintedTime(ticketListingDetails.getPrintedTime());
//			type.setVenue(ticketListingDetails.getVenue());
//			productListingDetailsType.setTicketListingDetails(type);
//		}
//		if (listingDetails.isUseFirstProduct()){
//			productListingDetailsType.setUseFirstProduct(listingDetails.isUseFirstProduct());
//		}
//		if (listingDetails.isUseStockPhotoURLAsGallery()){
//			productListingDetailsType.setUseStockPhotoURLAsGallery(listingDetails.isUseStockPhotoURLAsGallery());
//		}
		return productListingDetailsType;
	}
	
	 /**
	  * 退货政策  
	  * RETURNS_ACCEPTED  同意
	  * RETURNS_NOT_ACCEPTED 不同意
	  * json格式:
	  * {"internationalRefundOption":"MoneyBack","internationalReturnsAcceptedOption":"ReturnsAccepted","internationalReturnsWithinOption":"Days_14","internationalShippingCostPaidByOption":"Buyer","refundOption":"MoneyBack","returnsAcceptedOption":"ReturnsAccepted","returnsWithinOption":"Days_14","shippingCostPaidByOption":"Buyer"}
	  * @param vo
	  * @return
	  */
	 public ReturnPolicyType buildReturnPolicyDetails(PublishListingVO vo){
		 ReturnPolicyType returnPolicy = new ReturnPolicyType();
		 JSONObject parseObject = JSONObject.parseObject(JSONObject.toJSONString(vo.getReturnPolicy()));
		 ReturnPolicyType obj = JSONObject.toJavaObject(parseObject, ReturnPolicyType.class);
		 //国内
		 if( StringUtils.isNotBlank(obj.getReturnsAcceptedOption()) && obj.getReturnsAcceptedOption().equals(ReturnsAcceptedOptionsCodeType.RETURNS_ACCEPTED.value())){ //支技国内退款政策
			 returnPolicy.setReturnsAcceptedOption(ReturnsAcceptedOptionsCodeType.RETURNS_ACCEPTED.value());  //同意退款
			 //接收到的值为空,则默认为money_back退款方式
			 if (StringUtils.isNotBlank(obj.getRefundOption())){
				 returnPolicy.setRefundOption(obj.getRefundOption()); //以何种方式支付退款
			 }else{
				 returnPolicy.setRefundOption(RefundOptionsCodeType.MONEY_BACK.value());
			 }
			 returnPolicy.setReturnsWithinOption(obj.getReturnsWithinOption());   //日期
			 returnPolicy.setShippingCostPaidByOption(obj.getShippingCostPaidByOption()); //运费是买家，还是卖家
		 }else{
			 returnPolicy.setReturnsAcceptedOption(ReturnsAcceptedOptionsCodeType.RETURNS_NOT_ACCEPTED.value());
		 }
		 //国际
		 if( StringUtils.isNotBlank(obj.getInternationalReturnsAcceptedOption()) && obj.getInternationalReturnsAcceptedOption().equals(ReturnsAcceptedOptionsCodeType.RETURNS_ACCEPTED.value())){ //支技国际退款政策
			 returnPolicy.setInternationalReturnsAcceptedOption(ReturnsAcceptedOptionsCodeType.RETURNS_ACCEPTED.value());  //同意退款
			 if (StringUtils.isNotBlank(obj.getRefundOption())){
				 returnPolicy.setInternationalRefundOption(obj.getRefundOption()); //以何种方式支付退款
			 }else{
				 returnPolicy.setInternationalRefundOption(RefundOptionsCodeType.MONEY_BACK.value());
			 }
			 returnPolicy.setInternationalReturnsWithinOption(obj.getReturnsWithinOption());   //日期
			 returnPolicy.setInternationalShippingCostPaidByOption(obj.getShippingCostPaidByOption()); //运费是买家，还是卖家
		 }else{
			 returnPolicy.setInternationalReturnsAcceptedOption(ReturnsAcceptedOptionsCodeType.RETURNS_NOT_ACCEPTED.value());
		 }
		 return returnPolicy;
	 }
	 
	 /**
	  * 运输政策https://developer.ebay.com/Devzone/XML/docs/Reference/eBay/types/ShippingServiceOptionsType.html#ShippingServiceAdditionalCost
	  * shippingServiceAdditionalCost  如果同一买家购买同一系列产品的多个数量，则每件额外产品的运费
	  *接收到的json格式:
	  *{"excludeShipToLocation":"中国,美国","globalShipping":true,"internationalShippingServiceOptions":{"shipToLocation":"A","shippingService":"国际运输服务","shippingServiceAdditionalCost":"运输附加费","shippingServiceCost":"运输费用","shippingServicePriority":1},"paymentInstructions":"这是运运输","shippingServiceOptions":{"freeShipping":true,"shippingService":"服务1","shippingServiceAdditionalCost":"服务附加费","shippingServiceCost":"运费","shippingServicePriority":2}}
	  * @param vo
	  * @return
	  */
	  public ShippingDetailsType buildShippingDetails(PublishListingVO vo) {
		  EbayPublishListingShipping listingShipping = vo.getShippingService();
	      
		  ShippingDetailsType sd =new ShippingDetailsType();
		  if(StringUtils.isEmpty(listingShipping.getPaymentInstructions())){
			  sd.setPaymentInstructions(listingShipping.getPaymentInstructions());  //付款描述
		  }
	      sd.setShippingType(ShippingTypeCodeType.FLAT); //付款方式。 默认为flat
	      //sd.setGlobalShipping(listingShipping.getGlobalShipping()); //是否发货到全球
	      String excludeshiptolocation = listingShipping.getExcludeShipToLocation(); 
	      if (StringUtils.isNotEmpty(excludeshiptolocation) && !"none".equals(excludeshiptolocation)){
	    	  sd.setExcludeShipToLocation(excludeshiptolocation.split(",")); 
	      }
	      //国内运输方案 
	      List<EbayPublishListingShippingTransport> shippingList = listingShipping.getShippingServiceOptions();
	      if (shippingList!=null && shippingList.size()>0){
	    	  	List<ShippingServiceOptionsType> optionsType = new ArrayList<>();
				int index = 1;
				for (EbayPublishListingShippingTransport options : shippingList){
					if (StringUtils.isNotBlank(options.getShippingService())){
					     ShippingServiceOptionsType shippingOptions  = new ShippingServiceOptionsType();
					     if(options.getFreeShipping()==null){
							 options.setFreeShipping(false);
						 }
					     shippingOptions.setFreeShipping(options.getFreeShipping()); //免运费

					     shippingOptions.setShippingService(options.getShippingService()); //运输服务方式
					     //优先级前端未传,通过下标索引来获取
					     shippingOptions.setShippingServicePriority(index++); //服务优先权 １　－４
					     if (options.getFreeShipping()!=null && !options.getFreeShipping()){ //是否免运费
						     AmountType amountType = new AmountType(); //运输费用
						     String shippingServiceCost = StringUtils.isNotBlank(options.getShippingServiceCost())?options.getShippingServiceCost():"0";
						     amountType.setValue(Double.valueOf(shippingServiceCost));
						     shippingOptions.setShippingServiceCost(amountType);
						     AmountType addCost = new AmountType(); //附加费用
						     String additionalCost = StringUtils.isNotBlank(options.getShippingServiceAdditionalCost())?options.getShippingServiceAdditionalCost():"0";
						     addCost.setValue(Double.valueOf(additionalCost));
						     shippingOptions.setShippingServiceAdditionalCost(addCost);
					     }
					     optionsType.add(shippingOptions);
					}
				}
				//list 转字符串数组
				ShippingServiceOptionsType[] array = new ShippingServiceOptionsType[optionsType.size()];
				optionsType.toArray(array);
				sd.setShippingServiceOptions(array);
	      }
	      //国际运输方案
		  List<EbayPublishListingShippingTransport> parseArray = listingShipping.getInternationalShippingServiceOptions();
	      if (parseArray!=null && parseArray.size()>0){
	    	  List<InternationalShippingServiceOptionsType> internationalList = new ArrayList<>();
			  int index = 1;
	    	  for(EbayPublishListingShippingTransport option : parseArray){
	    		  if (StringUtils.isNotBlank(option.getShippingService())){
		    		  InternationalShippingServiceOptionsType iType = new InternationalShippingServiceOptionsType();
		    	      iType.setShippingServicePriority(index++); //优先级 1-5
		    		  iType.setShippingService(option.getShippingService()); //运输服务
		    		  if (StringUtils.isNotBlank(option.getShipToLocation())){
		    			  //运输到全球值为 Worldwide
		    			 List<String> list = JSONObject.parseArray(option.getShipToLocation(),String.class);
		    			 String [] array = new String[list.size()];
		    			 list.toArray(array);
		    			 iType.setShipToLocation(array); 
		    		  }
		    		  AmountType amountType = new AmountType(); //运输费用
					  String shippingServiceCost = StringUtils.isNotBlank(option.getShippingServiceCost())?option.getShippingServiceCost():"0";
					  amountType.setValue(Double.valueOf(shippingServiceCost));
					  iType.setShippingServiceCost(amountType);
					  AmountType addCost = new AmountType(); //附加费用
					  String additionalCost = StringUtils.isNotBlank(option.getShippingServiceAdditionalCost())?option.getShippingServiceAdditionalCost():"0";
					  addCost.setValue(Double.valueOf(additionalCost));
					  iType.setShippingServiceAdditionalCost(addCost);
					  internationalList.add(iType);
	    		  }
	    	  }
	    	  InternationalShippingServiceOptionsType [] serviceOption =  new InternationalShippingServiceOptionsType[internationalList.size()];
	    	  internationalList.toArray(serviceOption);
	    	  sd.setInternationalShippingServiceOption(serviceOption);
	      }
	      return sd;
	  }

	@SuppressWarnings("unchecked")
	@Override
	public List<EbaySiteDetail> findSiteDetail(EbaySiteDetail record) {
		String key = "ebay:"+record.getSite()+"-detail";
		if (redisUtils.exists(key)){
			return (List<EbaySiteDetail>) redisUtils.get(key);
		}
		List<EbaySiteDetail> page = siteDetailMapper.page(record);
		redisUtils.set(key, page,86400L);
		return page;
	}

	public void updateEbaySiteDetailshipping(){
		EbaySiteDetail record = new EbaySiteDetail();
		List<EbaySiteDetail> page = siteDetailMapper.page(record);
		for(EbaySiteDetail siteDetail:page){
			String key = "ebay:"+siteDetail.getSite()+"-detail";
			redisUtils.remove(key);
//			String shipping = this.shippingStr(siteDetail.getShipping());
//			EbaySiteDetail updateEbaySiteDetail = new EbaySiteDetail();
//			updateEbaySiteDetail.setId(siteDetail.getId());
//			updateEbaySiteDetail.setShipping(shipping);
//			siteDetailMapper.updateByPrimaryKeySelective(updateEbaySiteDetail);
		}
	}

	private String shippingStr(String str){
		JSONObject objJson = JSONObject.parseObject(str);
		String internation = objJson.getString("internation");
		System.out.println(internation);
		JSONArray array =JSONObject.parseArray(internation);

		JSONArray arrayNew = new JSONArray();
		for(int i=0;i<array.size();i++){
			JSONObject arrayJSONObject= array.getJSONObject(i);
			String shippingService = arrayJSONObject.getString("shippingService");
			try {
				ShippingServiceCodeType.fromValue(shippingService);
				arrayNew.add(arrayJSONObject);
			} catch (Exception e) {
				System.out.println(shippingService);
			}
		}
		objJson.put("internation",arrayNew.toJSONString());


		String domestic = objJson.getString("domestic");
		System.out.println(domestic);
		JSONArray arraydomestic =JSONObject.parseArray(domestic);

		JSONArray arrayNewdomestic = new JSONArray();
		for(int i=0;i<arraydomestic.size();i++){
			JSONObject arrayJSONObject= arraydomestic.getJSONObject(i);
			String shippingService = arrayJSONObject.getString("shippingService");
			try {
				System.out.println(shippingService);
				ShippingServiceCodeType.fromValue(shippingService);
				arrayNewdomestic.add(arrayJSONObject);
			} catch (Exception e) {
				System.out.println(shippingService);
			}
		}
		objJson.put("domestic",arrayNewdomestic.toJSONString());

	  	return objJson.toJSONString();
	}

	@Override
	public Double verifyAddItem(PublishListingVO vo) {
		ItemType item = buildItem(vo);
		VerifyAddItemCall api = null;
		try {
			api =  new VerifyAddItemCall(getApiContext(String.valueOf(vo.getEmpowerId())));
			api.setSite(SiteCodeType.fromValue(vo.getSite())); //指定站点
			api.setItem(item);
			FeesType verifyAddItem = api.verifyAddItem();
			return  eBayUtil.findFeeByName(verifyAddItem.getFee(), "ListingFee").getFee().getValue();
		} catch (GlobalException e){
			logger.error("verifyAddItem{}",e);
			throw new GlobalException(e.getErrorCode(),e.getMessage());
		} catch (Exception e){
			logger.error("verifyAddItem{}",e);
			ErrorType[] errors = null;
			if (null != api.getResponseObject()){
				errors = api.getResponseObject().getErrors();
				if(errors!=null && errors.length>0){
					ErrorType errorType =errors[0];
					if("Error".equals(errorType.getSeverityCode().value())) {
						String code = errorType.getErrorCode();
						String message = errorType.getLongMessage();
						throw new GlobalException(code, message);
					}
				}
			}
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,e.getMessage());
		}finally {
			if (null != api.getResponseObject()) {
				ErrorType[] errors = api.getResponseObject().getErrors();
				if(errors!=null && errors.length>0) {
					this.saveErrer(errors);
				}
			}
		}
	}

	@Override
	public Double verifyAddFixedPriceItem(PublishListingVO vo) {
		ItemType item = buildItem(vo);
		VerifyAddFixedPriceItemCall api = null;
		try {
			api =  new VerifyAddFixedPriceItemCall(getApiContext(String.valueOf(vo.getEmpowerId())));
			api.setSite(SiteCodeType.fromValue(vo.getSite())); //指定站点
			api.setItem(item);
			FeesType verifyAddItem = api.verifyAddFixedPriceItem();
			return  eBayUtil.findFeeByName(verifyAddItem.getFee(), "ListingFee").getFee().getValue();
		}catch (GlobalException e){
			logger.error("verifyAddItem{}",e);
			throw new GlobalException(e.getErrorCode(),e.getMessage());
		} catch (Exception e){
			logger.error("verifyAddFixedPriceItem{}",e);
			ErrorType[] errors = null;
			if (null != api.getResponseObject()){
				errors = api.getResponseObject().getErrors();
				if(errors!=null && errors.length>0){
					ErrorType errorType =errors[0];
					if("Error".equals(errorType.getSeverityCode().value())) {
						String code = errorType.getErrorCode();
						String message = errorType.getLongMessage();
						throw new GlobalException(code, message);
					}
				}
			}
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,e.getMessage());
		}finally {
			if (null != api.getResponseObject()) {
				ErrorType[] errors = api.getResponseObject().getErrors();
				if(errors!=null && errors.length>0) {
					this.saveErrer(errors);
				}
			}
		}
	}

	public void saveErrer(ErrorType[] errors){
		List<EbayPublishListingError> errorList = Lists.newArrayList();
		for (ErrorType type : errors){
			EbayPublishListingError error = new EbayPublishListingError();
			BeanUtils.copyProperties(type, error);
			error.setSeverityCode(type.getSeverityCode().value());
			error.setCreationTime(new Date());
			errorList.add(error);
		}
		listingErrorMapper.batchInsert(errorList);
	}

	@Override
	public String loadCateory(String categoryId,String site) {
		String key = "ebay:loadCateoryName"+site+categoryId;
		if (redisUtils.exists(key)){
			return (String) redisUtils.get(key);
		}
		StringBuilder sb = new StringBuilder();
		String cateoryName = getCategory(sb,categoryId,site,true);
		redisUtils.set(key, cateoryName,86400L);
		return cateoryName;
	}
	
	/**
	 * 依次查找数据
	 * @param categoryId
	 * @param site
	 * @return
	 */
	public String getCategory(StringBuilder sb, String categoryId,String site,boolean first){
		EbayProductCategory category = new EbayProductCategory();
		category.setCategoryid(categoryId);
		category.setSite(site);
		List<EbayProductCategory> categoryList  = categoryMapper.page(category);
		if (CollectionUtils.isNotEmpty(categoryList)){
			EbayProductCategory categorys = categoryList.get(0);
			if (first){
				if (null == categorys.getLeafcategory()){ 
					return categoryId+"不是最子级";
				}
			}
			sb.insert(0, categorys.getCategoryname());
			if (categorys.getCategorylevel() != 1){
				sb.insert(0, ",");
				getCategory(sb,categorys.getCategoryparentid(), site,false);
			}
			return sb.toString();
		}
		return "No matches";
	}


	@Override
	public String getItem(String itemId) {
		try {
			//GetItemCall getItemCall = new GetItemCall(getApiContext());
			//getItemCall.setItemID(itemId);
			//ItemType item = getItemCall.getItem();
			return null;
		} catch (Exception e){
			logger.error("getItem{}",e);
		}
		return null;
		
	}

	@Override
	public List<Map<String, String>> fuzzyQuery(String empowerId,String site, String title) {
		try {
			List<Map<String, String>> list = new ArrayList<>();
			GetSuggestedCategoriesCall getSuggestedCategoriesCall = new GetSuggestedCategoriesCall(getApiContext(empowerId));
	        getSuggestedCategoriesCall.setSite(SiteCodeType.fromValue(site));
	        getSuggestedCategoriesCall.setQuery(title);
	        SuggestedCategoryType[] suggestedCategories = getSuggestedCategoriesCall.getSuggestedCategories();
	        for (SuggestedCategoryType category : suggestedCategories){
	        	StringBuilder sb = new StringBuilder();
	        	CategoryType categoryType = category.getCategory();
	        	String[] categoryParentName = categoryType.getCategoryParentName();
	        	for(String b : categoryParentName){
	           		sb.append(b);
	           		sb.append(">");
	        	}
	        	Map<String, String> map = new HashMap<>();
	        	sb.append(categoryType.getCategoryName());
	        	map.put(categoryType.getCategoryID(), sb.toString());
	        	list.add(map);
	        }
	        return list;
		} catch (Exception e) {
			logger.error("fuzzyQuery{}",e);
		}
	    return null;
	}

	@Override
	public Store getStore(String userId) {
		Store store = new Store();
		try {
//			GetStoreCall getStoreCall = new GetStoreCall(getApiContext());
//			getStoreCall.setUserID(userId);
//			StoreType returnedStoreType = getStoreCall.getReturnedStoreType();
//			StoreCustomCategoryArrayType customCategories = returnedStoreType.getCustomCategories();
//			if (null != customCategories){
//				StoreCustomCategoryType[] customCategoryArray = customCategories.getCustomCategory();
//				List<CustomCategory> categoryList = new ArrayList<>();
//				for (StoreCustomCategoryType type : customCategoryArray){
//					CustomCategory customCategory = new CustomCategory();
//					customCategory.setCategoryID(type.getCategoryID());
//					customCategory.setName(type.getName());
//					customCategory.setOrder(type.getOrder());
//					StoreCustomCategoryType[] childCategory = type.getChildCategory();
//					if (null != childCategory && childCategory.length>0){
//						customCategory.setChildCategory(getChildCustomCategory(childCategory));
//					}
//					categoryList.add(customCategory);
//					store.setCustomCategories(categoryList);
//				}
//			}
		} catch (Exception e) {
			logger.error("getStore{}",e);
		}
		return store;
	}
	
	public List<CustomCategory> getChildCustomCategory(StoreCustomCategoryType[] childCategory){
		List<CustomCategory> list = new ArrayList<>();
		for (StoreCustomCategoryType child : childCategory){
			CustomCategory childCustomCategory = new CustomCategory();
			StoreCustomCategoryType[] childCategory2 = child.getChildCategory();
			if (null != childCategory2 &&childCategory2.length>0){
				childCustomCategory.setChildCategory(getChildCustomCategory(childCategory2));
			}
			childCustomCategory.setCategoryID(child.getCategoryID());
			childCustomCategory.setName(child.getName());
			childCustomCategory.setOrder(child.getOrder());
			list.add(childCustomCategory);
		}
		return list;
	}

	@Override
	public String relistItem(PublishListingVO vo) {
		logger.info("ebay重刊登 start>>{}",vo.getId());
		ItemType item = buildItem(vo);
		RelistItemCall api = null;
		try {
			api =  new RelistItemCall(getApiContext(String.valueOf(vo.getEmpowerId())));
			item.setItemID(vo.getItemid());
			api.setItemToBeRelisted(item);
			api.setSite(SiteCodeType.fromValue(vo.getSite())); //指定站点
			api.relistItem();
			logger.info("ebay重刊登 end>>{},{}",vo.getId(),item.getItemID());

			EbayPublishListingNew obj = new EbayPublishListingNew();
			obj.setStatus(4);
			obj.setItemid(item.getItemID());
			obj.setId(vo.getId());
			obj.setOnlineTime(new Date());
			obj.setUpdateStatus(2);
			obj.setUpdateTime(new Date());
			listingMapper.updateByPrimaryKeySelective(obj);
			listingErrorMapper.deleteByListingId(vo.getId());
			return item.getItemID();
		} catch (Exception e){
			setListingError(api.getResponseObject(), vo.getId());

			EbayPublishListingNew obj = new EbayPublishListingNew();
			obj.setId(vo.getId());
			obj.setUpdateStatus(3);
			obj.setUpdateTime(new Date());
			listingMapper.updateByPrimaryKeySelective(obj);
		}
		return null;
	}

	@Override
	public String relistFixedPriceItem(PublishListingVO vo) {
		logger.info("ebay多属性重刊登 start>>{}",vo.getId());
		ItemType item = buildItem(vo);
		RelistFixedPriceItemCall api = null;
		try {
			api =  new RelistFixedPriceItemCall(getApiContext(String.valueOf(vo.getEmpowerId())));
			item.setItemID(vo.getItemid());
			api.setItemToBeRelisted(item);
			api.setSite(SiteCodeType.fromValue(vo.getSite())); //指定站点
			api.relistFixedPriceItem();
			logger.info("ebay多属性重刊登 end>>{},{}",vo.getId(),item.getItemID());

			EbayPublishListingNew obj = new EbayPublishListingNew();
			obj.setId(vo.getId());
			obj.setUpdateStatus(2);
			obj.setStatus(4);
			obj.setItemid(item.getItemID());
			obj.setOnlineTime(new Date());
			obj.setUpdateTime(new Date());
			listingMapper.updateByPrimaryKeySelective(obj);
			listingErrorMapper.deleteByListingId(vo.getId()); 
			return item.getItemID();
		} catch (Exception e){
			setListingError(api.getResponseObject(), vo.getId());
			EbayPublishListingNew obj = new EbayPublishListingNew();
			obj.setId(vo.getId());
			obj.setUpdateStatus(3);
			obj.setUpdateTime(new Date());
			listingMapper.updateByPrimaryKeySelective(obj);
		}
		return null;
	}

	@Override
	public Double verifyRelistItem(PublishListingVO vo) {
		ItemType item = buildItem(vo);
		VerifyRelistItemCall api = null;
		try {
			api = new VerifyRelistItemCall(getApiContext(String.valueOf(vo.getEmpowerId())));
			api.setSite(SiteCodeType.fromValue(vo.getSite())); //指定站点
			item.setItemID(vo.getItemid());
			api.setItem(item);
			api.verifyRelistItem();
			FeesType verifyAddItem = api.getReturnedFees();
			return  eBayUtil.findFeeByName(verifyAddItem.getFee(), "ListingFee").getFee().getValue();
		} catch (Exception e){
			logger.error("verifyRelistItem{}",e);
		}
		return null;
	}
	
	@Override
	public ApiContext getApiContext(String empowerId) throws IOException {
		logger.info("获取token------------------------>");
//		String key = empowerId+"-token";
//		String token = (String) redisUtils.get(key);
//		if(StringUtils.isBlank(token)){
			Empower empower = authorizationSellerService.selectByPrimaryKey(empowerId);
			if (null == empower || StringUtils.isBlank(empower.getToken())){
				logger.info("用户授权id{}",empowerId);
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"获取ebayToken有误,检查用户授权");
			}
		String	token = empower.getToken();
		logger.info("empowerId{},token{}",empowerId,token);
//			redisUtils.set(key, token);
//		}
		ApiContext apiContext = new ApiContext();
		ApiCredential cred = apiContext.getApiCredential();
		cred.seteBayToken(token);
		apiContext.setApiServerUrl(EBAY_URL_REQUEST);
		return apiContext;
	}

	
//	public ApiContext getApiContext() throws IOException {
//		ApiContext apiContext = new ApiContext();
//		ApiCredential cred = apiContext.getApiCredential();
//		cred.seteBayToken(
//				"AgAAAA**AQAAAA**aAAAAA**M60AXA**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6AAmYSgCpWEpwidj6x9nY+seQ**Y5wFAA**AAMAAA**NoPAqb/JWBFrrs/rkKJX3F6mHSnKr2i1DZEeMrZOUVSEH205WTQaU0H9EAUtW+uEoFWpdYBM1OaCJ0X66ecB4xY1jBmCxSD2wLt6MF9D6GWJFW3MlVL8fuNYL/JErKFFrusj1j7XUPNv1gp+gQghjbHqAeqiSqvf+129+yh3QnvdWf73iNMgtK15FUSnS8ahdQlugfrk9kZbpOdeFZ/HuWnDxo+vUy9URWWTZhpohQhhP35wOEd2zxXof8vtvgov/Yhxadas15+T2h0GrtdJq8yNqcVT/7oYk7ZS6nWlshUVv7E6EVydNTcqFLX/0XHLUqtFfMNN7lZtnKPO1ELsts5Y8D0pIQPGbeQ/aPbH238gM3gAX5RDPMCMqdv+IGuJeAYdOW4U+QXA+wax57nBwEN1PLDv1/YdRwgebtmyqAsIAb6calSLP/ZyBzZq0BBzIX+f2YwaGhdeXG55s3d0kqY7xyo9O2k9WX12na/InifjCLy3AvrGbzs4G14tWoLpFZnM9zkOAnnmb0CBlv52rFOr1i2/xFg3SKnay0iQx7uAGJPZsOZfD79MlpzJKeaPP0pzS408bUoycbpRfCREYuMAMGoLOpCP2xBxEiycdxrPkZ7xDeSQAxhXHlXKaXB3H/QmUAsP3gllMn5kDxD5/zMaUwLFxGq/wrEvnBlpo9RbeFc/Xhhac1LVoCMHXqMjQ9BIQZlTGSG4lbpvg5k8smP+69tJahyrru9FyCMIONNQ/h2gqmeArJI7vVA9yP7a");
//		apiContext.setApiServerUrl("https://api.ebay.com/wsapi");
//		return apiContext;
//	}
	
}
