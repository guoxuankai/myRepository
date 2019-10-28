package com.rondaful.cloud.seller.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ebay.soap.eBLBaseComponents.ShippingServiceCodeType;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.UserUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.dto.EbayHistoryDTO;
import com.rondaful.cloud.seller.dto.EbayPublishListingAPPDTO;
import com.rondaful.cloud.seller.dto.EbayPublishListingDTO;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.entity.ebay.ListingVariantAttribute;
import com.rondaful.cloud.seller.entity.ebay.ShippingDetail;
import com.rondaful.cloud.seller.enums.EbayOperationEnum;
import com.rondaful.cloud.seller.mapper.*;
import com.rondaful.cloud.seller.rabbitmq.EbayOpertionSender;
import com.rondaful.cloud.seller.remote.RemoteCommodityService;
import com.rondaful.cloud.seller.remote.RemoteOrderRuleService;
import com.rondaful.cloud.seller.remote.RemoteSupplierProviderService;
import com.rondaful.cloud.seller.remote.RemoteUserService;
import com.rondaful.cloud.seller.service.IEbayBaseService;
import com.rondaful.cloud.seller.service.IEbayPublishListingOperationLogService;
import com.rondaful.cloud.seller.service.IEbayPublishListingService;
import com.rondaful.cloud.seller.service.PublishTemplateService;
import com.rondaful.cloud.seller.vo.*;
import io.swagger.models.auth.In;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EbayPublishListingServiceImpl  implements IEbayPublishListingService  {

	@Autowired
	private EbayPublishListingNewMapper listingMapper;
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
	private EbayPublishListingErrorMapper listingErrorMapper;
	
	@Autowired
	private EbayPublishListingVariantMapper listingVariantMapper;
	
	@Autowired
	private EbayPublishListingVariantPictureMapper listingVariantValuePictureMapper;

	@Autowired
	private EbayPublishListingShippingTransportMapper ebayPublishListingShippingTransportMapper;
	
	@Autowired
	private IEbayPublishListingOperationLogService logService;
	
    @Autowired
	private UserUtils userInfo;

    @Autowired
    private EbayOpertionSender ebayOpertionSender;

	@Autowired
	private GetLoginUserInformationByToken getUserInfo;
	@Autowired
	private PublishStyleMapper publishStyleMapper;

	@Autowired
	private RemoteCommodityService remoteCommodityService;

	@Autowired
	private PublishTemplateService publishTemplateService;

	@Autowired
	private RemoteSupplierProviderService remoteSupplierProviderService;
	@Autowired
	private RemoteUserService remoteUserService;

	@Autowired
	private EbayPublishListingVariantSkusMapper ebayPublishListingVariantSkusMapper;

    private final Logger logger = LoggerFactory.getLogger(EbayBaseServiceImpl.class);
    
	@Override
	public Long insertPublishListing(PublishListingVO vo) throws Exception {
		//状态为草稿不做验证，其他状态验证
		if(1!=vo.getStatus()){
			check(vo,false);
		}else{
			if(vo.getListingType()!=2){
				EbayPublishListingVariant listingVariant = vo.getProductListingDetails();
				if(listingVariant==null){
					listingVariant = new EbayPublishListingVariant();
				}
				listingVariant.setPlSku(vo.getPlSku());
				listingVariant.setStartPrice(vo.getStartPrice());
				listingVariant.setPlatformSku(vo.getPlatformSku());
				listingVariant.setQuantity(vo.getQuantity());
				listingVariant.setEstimatedFreight(vo.getEstimatedFreight());
				listingVariant.setForecastProfits(vo.getForecastProfits());
				listingVariant.setPlatformCommission(vo.getPlatformCommission());
				listingVariant.setFreightFee(vo.getFreightFee());
				listingVariant.setOtherFee(vo.getOtherFee());
				listingVariant.setOtherFee1(vo.getOtherFee1());
				listingVariant.setOtherFee2(vo.getOtherFee2());
				listingVariant.setOtherFee3(vo.getOtherFee3());
				listingVariant.setProductReferenceID(vo.getProductReferenceID());
				vo.getVariant().add(listingVariant);
			}
		}

		EbayPublishListingNew listing = new EbayPublishListingNew();
		BeanUtils.copyProperties(vo, listing);
		listing.setPlatformListing(1);
		listing.setCreationTime(new Date());
		listing.setUpdateTime(new Date());
		//用户
		UserDTO userDTO = getUserInfo.getUserDTO();
		listing.setCreateId(userDTO.getUserId().longValue());
		listing.setCreateName(userDTO.getLoginName());
		if(userDTO.getManage()){
			listing.setSellerId(userDTO.getUserId().longValue());
		}else{
			listing.setSellerId(userDTO.getTopUserId().longValue());
		}
		logger.info("insertPublishListing-->status:{},listingType:{}",vo.getStatus(),vo.getListingType());
		if (listing.getStatus() == 2){
			listing.setPublishTime(new Date());
		}
		listingMapper.insertSelective(listing);

		logger.info("insertPublishListing-->insert listing end");
		Integer listingId = listing.getId().intValue();
		//详情部分
		EbayPublishListingDetail ebayPublishListingDetail = new EbayPublishListingDetail();
		BeanUtils.copyProperties(vo, ebayPublishListingDetail);
		ebayPublishListingDetail.setListingId(listing.getId());
		ebayPublishListingDetailMapper.insertSelective(ebayPublishListingDetail);
		//价格部分
		EbayPublishListingPrice ebayPublishListingPrice = new EbayPublishListingPrice();
		BeanUtils.copyProperties(vo, ebayPublishListingPrice);
		ebayPublishListingPrice.setListingId(listing.getId());
		ebayPublishListingPriceMapper.insertSelective(ebayPublishListingPrice);
		//退货政策
		EbayPublishListingReturnPolicy returnPolicy = vo.getReturnPolicy();
		returnPolicy.setListingId(listing.getId());
		ebayPublishListingReturnPolicyMapper.insertSelective(returnPolicy);

		//卖家要求
		if (!ebayPublishListingDetail.getDisableBuyerRequirements()){
			EbayPublishBuyerRequirements obj = vo.getDisableBuyerRequirementsValue();
			if (obj!=null){
				obj.setListingId(listingId);
				obj.setCreationTime(new Date());
				buyerRequirementsMapper.insertSelective(obj);
			}
			logger.info("insertPublishListing-->insert buyerRequirements end");
		}
		//多属性刊登变体数据处理
		if (vo.getListingType() ==2){ 
			setVariantValue(listingId , vo);
		}else{
			EbayPublishListingVariant listingVariant = null;
			if(vo.getVariant()!=null && vo.getVariant().size()>0){
				listingVariant = vo.getVariant().get(0);
			}else{
				listingVariant = new EbayPublishListingVariant();
			}
			listingVariant.setListingId(listingId);
			listingVariantMapper.insertSelective(listingVariant);
			if(vo.getListVariantSkus()!=null && vo.getListVariantSkus().size()>0) {
				for (EbayPublishListingVariantSkus variantSkus : vo.getListVariantSkus()) {
					variantSkus.setListingId(listing.getId());
					variantSkus.setVariantId(listingVariant.getId().longValue());
					ebayPublishListingVariantSkusMapper.insertSelective(variantSkus);
				}
			}
		}
		//商品属性  (包含推荐属性,自定属性)
		if (vo.getAttributeValue()!=null){
			List<EbayPublishListingAttribute> attributeList = vo.getAttributeValue();
			//值不为空的属性集合
			List<EbayPublishListingAttribute> attributeValueIsNotNullList = new ArrayList<>();
			for (EbayPublishListingAttribute attributeObj : attributeList){
				if (StringUtils.isNotBlank(attributeObj.getAttributeValue())){
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
		if (vo.getShippingService()!=null){
			EbayPublishListingShipping shipping = vo.getShippingService();
			shipping.setListingId(listingId);
			shipping.setCreationTime(new Date());
			shipping.setId(null);
			listingShippingMapper.insertSelective(shipping);
			Integer shippingId= shipping.getId();
			logger.info("insertPublishListing-->insert shipping end");
			for(EbayPublishListingShippingTransport transport:shipping.getInternationalShippingServiceOptions()){
				if(StringUtils.isBlank(transport.getShippingService())){
					continue;
				}
				transport.setListingId(listing.getId());
				transport.setShippingId(shippingId.longValue());
				transport.setTransportType(2);
				ebayPublishListingShippingTransportMapper.insertSelective(transport);
			}
			for(EbayPublishListingShippingTransport transport:shipping.getShippingServiceOptions()){
				if(StringUtils.isBlank(transport.getShippingService())){
					continue;
				}
				transport.setListingId(listing.getId());
				transport.setShippingId(shippingId.longValue());
				transport.setTransportType(1);
				ebayPublishListingShippingTransportMapper.insertSelective(transport);
			}

		}
		String type = listing.getStatus() ==1 ? EbayOperationEnum.DRAFT.getCode():EbayOperationEnum.PUBLISH.getCode();
		insertLog(String.valueOf(userInfo.getUser().getUsername()),listingId,type,vo.getTitle(),Long.valueOf(userDTO.getUserId()));
		//向ebay发送操作请求
		if (listing.getStatus() == 2){
			logger.info("insertPublishListing-->asyncTaskService");
			vo.setId(listing.getId());
			setRemoteOrderRule(vo);
			//asyncTaskService.publish(vo);
			ebayOpertionSender.send(vo);
		}
		return listing.getId();
	}

	@Override
	public int updateByPrimaryKeySelective(EbayPublishListingNew ebayPublishListingNew) {
		return listingMapper.updateByPrimaryKeySelective(ebayPublishListingNew);
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
	/**
	 * 多属性刊登编辑时,先删除关联关系
	 * @param id
	 */
	public void delVariaantValue(Integer listingId) throws Exception{
		//删除变体信息 
		listingVariantMapper.deleteByValue(listingId);
		//删除变休关联的多属性
		//listingVariantValueMapper.deleteByValue(listingId);
		//删除变体相关的图片
		listingVariantValuePictureMapper.deleteByValue(listingId);

		//删除组合sku
		ebayPublishListingVariantSkusMapper.delectVariantSkus(listingId.longValue());

	}

	
	/**
	 * 多属性刊登,变体信息处理
	 * @param id
	 * @param vo
	 * @throws Exception
	 */
	public void setVariantValue(Integer id,PublishListingVO vo){
		//变体信息
		List<EbayPublishListingVariant> parseArray = vo.getVariant();
		if (parseArray!=null && parseArray.size()>0){

			parseArray.forEach(publishListingVariant -> {
				publishListingVariant.setListingId(id);
				publishListingVariant.setCreationTime(new Date());
                publishListingVariant.setPicture(getVariantPicture(publishListingVariant.getMultiattribute(),vo.getVariantPicture()));

				listingVariantMapper.insertSelective(publishListingVariant);
				if(publishListingVariant.getListVariantSkus()!=null && publishListingVariant.getListVariantSkus().size()>0) {
					for (EbayPublishListingVariantSkus variantSkus : publishListingVariant.getListVariantSkus()) {
						variantSkus.setListingId(id.longValue());
						variantSkus.setVariantId(publishListingVariant.getId().longValue());
						variantSkus.setId(null);
						ebayPublishListingVariantSkusMapper.insertSelective(variantSkus);
					}
				}
			});
		}
		//变体对应的图片信息
		List<EbayPublishListingVariantPicture> pictureList = vo.getVariantPicture();
		if (pictureList!=null && pictureList.size()>0){
			pictureList.forEach(picture->{
				picture.setListingId(id);
				picture.setCreationTime(new Date());
			});
			if (CollectionUtils.isNotEmpty(pictureList))
				listingVariantValuePictureMapper.insertBatchList(pictureList);
		}	
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
	
	
	//将对应的sku写入 远程调用订单服务
	public void setRemoteOrderRule(PublishListingVO vo){
		List<SellerSkuMap>  map = new ArrayList<SellerSkuMap>();
		List<CodeAndValueVo> data = new ArrayList<CodeAndValueVo>();
		List<EbayPublishListingVariantSkus> listSkus = ebayPublishListingVariantSkusMapper.getVariantSkusByListingId(vo.getId());
		if (vo.getListingType() !=2) { //不是多属性刊登
			SellerSkuMap sellerSkuMap = new SellerSkuMap();
			sellerSkuMap.setPlatformSku(vo.getPlatformSku());
			sellerSkuMap.setPlatform(OrderRuleEnum.platformEnm.E_BAU.getPlatform());
			sellerSkuMap.setAuthorizationId(String.valueOf(vo.getEmpowerId()));
			String skuGroup = vo.getPlSku()+":1";
			boolean bool = true;
			for(EbayPublishListingVariantSkus variantSkus:listSkus){
				if(bool){
					skuGroup = variantSkus.getPlSku()+":"+variantSkus.getPlSkuNumber();
					bool = false;
				}else{
					skuGroup = "|"+variantSkus.getPlSku()+":"+variantSkus.getPlSkuNumber();
				}
			}
			sellerSkuMap.setSkuGroup(skuGroup);
			map.add(sellerSkuMap);
		}else{ //多属性刊登
			//获取变体信息
			List<EbayPublishListingVariant> parseArray = vo.getVariant();
			if (parseArray!=null){
				for (EbayPublishListingVariant listingVariant : parseArray){
					SellerSkuMap sellerSkuMap = new SellerSkuMap();
					sellerSkuMap.setPlatformSku(listingVariant.getPlatformSku());
					sellerSkuMap.setPlatform(OrderRuleEnum.platformEnm.E_BAU.getPlatform());
					sellerSkuMap.setAuthorizationId(String.valueOf(vo.getEmpowerId()));
					String skuGroup = listingVariant.getPlSku()+":1";
					if(listSkus!=null && listSkus.size()>0){
						boolean bool = true;
						for(EbayPublishListingVariantSkus variantSkus:listSkus){
							if(variantSkus.getVariantId().equals(listingVariant.getId().longValue())){
								if(bool){
									skuGroup = variantSkus.getPlSku()+":"+variantSkus.getPlSkuNumber();
									bool = false;
								}else{
									skuGroup = "|"+variantSkus.getPlSku()+":"+variantSkus.getPlSkuNumber();
								}
							}
						}
					}
					sellerSkuMap.setSkuGroup(skuGroup);
					map.add(sellerSkuMap);
				}
			}
		}
		String addSkuMaps = remoteCommodityService.addSkuMap(map);
		logger.info("remoteOrderRule -->{}",addSkuMaps);
		map.forEach(sellersku->{
			CodeAndValueVo codeAndValueVo = new CodeAndValueVo();
			codeAndValueVo.setCode(sellersku.getPlSku());
			codeAndValueVo.setValue("1");
			data.add(codeAndValueVo);
		});
		String skuPublishNum = remoteCommodityService.updateSkuPublishNum(data);
		logger.info("updateSkuPublishNum -->{}",skuPublishNum);

	}

	/**
	 * 
	 * @param listingId
	 * @param ext   是否查询ext信息给前段使用
	 * @return
	 */
	public PublishListingVO getListingByValue(Integer listingId,boolean ext){
		EbayPublishListingNew selectByPrimaryKey = listingMapper.selectByPrimaryKey(listingId.longValue());
		Long sellerId = this.getSellerId().longValue();
		if(!selectByPrimaryKey.getSellerId().equals(sellerId))
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401, "无权限查看该数据");
		}

		PublishListingVO vo = new PublishListingVO();
		vo.setSeller(selectByPrimaryKey.getSellerId().toString());
		//详情
		EbayPublishListingDetail ebayPublishListingDetail = ebayPublishListingDetailMapper.selectByPrimaryKey(selectByPrimaryKey.getId());
		BeanUtils.copyProperties(ebayPublishListingDetail, vo);
		if(ext){
			Long styleId = ebayPublishListingDetail.getStyleId();
			if(styleId!=null && styleId>0){
				PublishStyle publishStyle = publishStyleMapper.selectByPrimaryKey(styleId);
				if(publishStyle!=null){
					vo.setStyleName(publishStyle.getStyleName());
				}
			}
		}
		//单品价格部分
		if (selectByPrimaryKey.getListingType() !=2) {
			EbayPublishListingPrice ebayPublishListingPrice = ebayPublishListingPriceMapper.selectByPrimaryKey(selectByPrimaryKey.getId());
			BeanUtils.copyProperties(ebayPublishListingPrice, vo);
		}else{
			vo.setBestOfferEnabled(false);
		}
		//物品刊登退换货政策
		EbayPublishListingReturnPolicy returnPolicy = ebayPublishListingReturnPolicyMapper.selectByPrimaryKey(selectByPrimaryKey.getId());
		vo.setReturnPolicy(returnPolicy);
		//主表部分
		BeanUtils.copyProperties(selectByPrimaryKey, vo);

		//卖家要求
		EbayPublishBuyerRequirements requirements = new EbayPublishBuyerRequirements();
		requirements.setListingId(listingId);
		EbayPublishBuyerRequirements buyer = buyerRequirementsMapper.selectByValue(requirements);
		if(null != buyer){
			vo.setDisableBuyerRequirementsValue(buyer);
		}
		//产品属性
		EbayPublishListingAttribute ebayPublishListingAttribute = new EbayPublishListingAttribute();
		ebayPublishListingAttribute.setListingId(listingId);
		List<EbayPublishListingAttribute> attribute = listingAttributeMapper.page(ebayPublishListingAttribute);
		if (CollectionUtils.isNotEmpty(attribute)){
			vo.setAttributeValue(attribute);
		}
		//运输政策
		EbayPublishListingShipping ebayPublishListingShipping = new EbayPublishListingShipping();
		ebayPublishListingShipping.setListingId(listingId);
		List<EbayPublishListingShipping> shippingList = listingShippingMapper.page(ebayPublishListingShipping);
		if (CollectionUtils.isNotEmpty(shippingList)){ //如果有数据，也只是一条
			EbayPublishListingShipping shipping= shippingList.get(0);
			EbayPublishListingShippingTransport transportQuery = new EbayPublishListingShippingTransport();
			transportQuery.setListingId(selectByPrimaryKey.getId());
			List<EbayPublishListingShippingTransport> listTransport=ebayPublishListingShippingTransportMapper.page(transportQuery);
			if(listTransport!=null && listTransport.size()>0) {
				//国内运输
				List<EbayPublishListingShippingTransport> shippingServiceOptions = Lists.newArrayList();
				//国际运输
				List<EbayPublishListingShippingTransport> internationalShippingServiceOptions = Lists.newArrayList();
				for(EbayPublishListingShippingTransport t:listTransport){
					if(t.getTransportType()!=null && t.getTransportType()==1){
						shippingServiceOptions.add(t);
					}else  if(t.getTransportType()!=null && t.getTransportType()==2){
						internationalShippingServiceOptions.add(t);
					}
				}
				shipping.setInternationalShippingServiceOptions(internationalShippingServiceOptions);
				shipping.setShippingServiceOptions(shippingServiceOptions);
			}

			vo.setShippingService(shipping);
		}
		//多属性刊登变体信息 
		if (selectByPrimaryKey.getListingType() ==2){
			EbayPublishListingVariant ebayListingVariant = new EbayPublishListingVariant();
			ebayListingVariant.setListingId(listingId);
			List<EbayPublishListingVariant> ebaylistingVariantList = listingVariantMapper.page(ebayListingVariant);
			//查询组合商品数据
			List<EbayPublishListingVariantSkus> listVariantSkus = ebayPublishListingVariantSkusMapper.getVariantSkusByListingId(listingId.longValue());
			if(listVariantSkus!=null && listVariantSkus.size()>0){
				for(EbayPublishListingVariant variant:ebaylistingVariantList){
					List<EbayPublishListingVariantSkus> setlistVariantSkus = Lists.newArrayList();
					for (EbayPublishListingVariantSkus variantSkus:listVariantSkus){
						if(variantSkus.getVariantId().equals(variant.getId().longValue())){
							setlistVariantSkus.add(variantSkus);
						}
					}
					if(setlistVariantSkus.size()>0){
						variant.setListVariantSkus(setlistVariantSkus);
						listVariantSkus.removeAll(setlistVariantSkus);
					}
				}
			}
			vo.setVariant(ebaylistingVariantList);



			EbayPublishListingVariantPicture picture = new EbayPublishListingVariantPicture();
			picture.setListingId(listingId);
			List<EbayPublishListingVariantPicture> pictureList = listingVariantValuePictureMapper.page(picture);
			if (CollectionUtils.isNotEmpty(pictureList))
				vo.setVariantPicture(pictureList);
		}else{
			EbayPublishListingVariant ebayListingVariant = new EbayPublishListingVariant();
			ebayListingVariant.setListingId(listingId);
			List<EbayPublishListingVariant> ebaylistingVariantList = listingVariantMapper.page(ebayListingVariant);
			if(ebaylistingVariantList!=null && ebaylistingVariantList.size()>0) {
				EbayPublishListingVariant listingVariant = ebaylistingVariantList.get(0);
				vo.setProductListingDetails(listingVariant);
				vo.setPlSku(listingVariant.getPlSku());
				vo.setStartPrice(listingVariant.getStartPrice());
				vo.setPlatformSku(listingVariant.getPlatformSku());
				vo.setQuantity(listingVariant.getQuantity());
				vo.setEstimatedFreight(listingVariant.getEstimatedFreight());
				vo.setForecastProfits(listingVariant.getForecastProfits());
				vo.setPlatformCommission(listingVariant.getPlatformCommission());
				vo.setFreightFee(listingVariant.getFreightFee());
				vo.setOtherFee(listingVariant.getOtherFee());
				vo.setOtherFee1(listingVariant.getOtherFee1());
				vo.setOtherFee2(listingVariant.getOtherFee2());
				vo.setOtherFee3(listingVariant.getOtherFee3());
				vo.setProductReferenceID(listingVariant.getProductReferenceID());

				List<EbayPublishListingVariantSkus> listVariantSkus = ebayPublishListingVariantSkusMapper.getVariantSkusByListingId(listingId.longValue());
				if(listVariantSkus!=null && listVariantSkus.size()>0){
					vo.setListVariantSkus(listVariantSkus);
				}
			}
		}
		return vo;
	}

	@Override
	public PublishListingVO findListingById(Integer listingId) throws Exception  {
		return getListingByValue(listingId,true);
	}
	@Override
	public EbayPublishListingNew getListingById(Integer listingId){
		EbayPublishListingNew selectByPrimaryKey = listingMapper.selectByPrimaryKey(listingId.longValue());
		return selectByPrimaryKey;
	}

	@Override
	public List<Long> getByStatusTask(Integer status) {
		return listingMapper.getByStatusTask(status);
	}

	@Override
	public void updateListingRemarks(Integer id,String remarks) throws Exception {
		EbayPublishListingNew selectByPrimaryKey = listingMapper.selectByPrimaryKey(id.longValue());
		if (null == selectByPrimaryKey){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"编辑对像未找到!");
		}
		EbayPublishListingNew listing = new EbayPublishListingNew();
		listing.setId(id.longValue());
		listing.setRemarks(remarks);
		listing.setUpdateTime(new Date());
		listingMapper.updateByPrimaryKeySelective(listing);
		UserDTO userDTO = getUserInfo.getUserDTO();
		insertLog(String.valueOf(userInfo.getUser().getUsername()),listing.getId().intValue(),EbayOperationEnum.EDIT_REMARK.getCode(),remarks,Long.valueOf(userDTO.getUserId()));
	}
	
	public void updateListing(PublishListingVO vo) throws Exception {
		//状态为草稿不做验证，其他状态验证
		if(1!=vo.getStatus()){
			check(vo,true);
		}else{
			if(vo.getListingType()!=2){
				EbayPublishListingVariant listingVariant = vo.getProductListingDetails();
				if(listingVariant==null){
					listingVariant = new EbayPublishListingVariant();
				}
				listingVariant.setPlSku(vo.getPlSku());
				listingVariant.setStartPrice(vo.getStartPrice());
				listingVariant.setPlatformSku(vo.getPlatformSku());
				listingVariant.setQuantity(vo.getQuantity());
				listingVariant.setEstimatedFreight(vo.getEstimatedFreight());
				listingVariant.setForecastProfits(vo.getForecastProfits());
				listingVariant.setPlatformCommission(vo.getPlatformCommission());
				listingVariant.setFreightFee(vo.getFreightFee());
				listingVariant.setOtherFee(vo.getOtherFee());
				listingVariant.setOtherFee1(vo.getOtherFee1());
				listingVariant.setOtherFee2(vo.getOtherFee2());
				listingVariant.setOtherFee3(vo.getOtherFee3());
				listingVariant.setProductReferenceID(vo.getProductReferenceID());
				vo.getVariant().add(listingVariant);
			}
		}
		logger.info("updateListing-->start");
		EbayPublishListingNew queryListing = listingMapper.selectByPrimaryKey(vo.getId().longValue());
		if (null == queryListing){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"数据不存在!");
		}
		EbayPublishListingNew listing = new EbayPublishListingNew();
		BeanUtils.copyProperties(vo, listing);
		listing.setUpdateTime(new Date());
		if(queryListing.getStatus()==3 || queryListing.getStatus()==4) {
			listing.setStatus(queryListing.getStatus());
			listing.setUpdateStatus(1);
		}


		//用户
		UserDTO userDTO = getUserInfo.getUserDTO();
		//listing.setCreateId(userDTO.getUserId().longValue());
		//listing.setCreateName(userDTO.getLoginName());
		if(userDTO.getManage()){
			listing.setSellerId(userDTO.getUserId().longValue());
		}else{
			listing.setSellerId(userDTO.getTopUserId().longValue());
		}
		if (listing.getStatus() == 2){
			listing.setPublishTime(new Date());
		}
		listingMapper.updateByPrimaryKeySelective(listing);
		Integer listingId = listing.getId().intValue();
		logger.info("updateListing-->end");

		//详情部分
		EbayPublishListingDetail ebayPublishListingDetail = new EbayPublishListingDetail();
		BeanUtils.copyProperties(vo, ebayPublishListingDetail);
		ebayPublishListingDetail.setListingId(listing.getId());
		ebayPublishListingDetailMapper.updateByPrimaryKeySelective(ebayPublishListingDetail);
		//价格部分
		ebayPublishListingPriceMapper.deleteByListingId(listing.getId());
		EbayPublishListingPrice ebayPublishListingPrice = new EbayPublishListingPrice();
		BeanUtils.copyProperties(vo, ebayPublishListingPrice);
		ebayPublishListingPrice.setListingId(listing.getId());
		ebayPublishListingPrice.setId(null);
		ebayPublishListingPriceMapper.insertSelective(ebayPublishListingPrice);
		//退货政策
		ebayPublishListingReturnPolicyMapper.deleteByListingId(listing.getId());
		EbayPublishListingReturnPolicy returnPolicy = vo.getReturnPolicy();
		returnPolicy.setListingId(listing.getId());
		returnPolicy.setId(null);
		ebayPublishListingReturnPolicyMapper.insertSelective(returnPolicy);

		//卖家要求数据处理  先删后加原则
		EbayPublishBuyerRequirements del = new EbayPublishBuyerRequirements();
		del.setListingId(listingId);
		buyerRequirementsMapper.deleteByValue(del);
		EbayPublishBuyerRequirements obj = vo.getDisableBuyerRequirementsValue();
		if (obj!=null){
			obj.setListingId(listingId);
			obj.setCreationTime(new Date());
			buyerRequirementsMapper.insertSelective(obj);
			logger.info("updateListing-->requirements");
		}
		//商品属性数据处理
		List<EbayPublishListingAttribute> attributeList = vo.getAttributeValue();
		if (attributeList!=null && attributeList.size()>0){
			EbayPublishListingAttribute record = new EbayPublishListingAttribute();
			record.setListingId(listingId);
			listingAttributeMapper.deleteByValue(record);
			//值不为空的属性集合
			List<EbayPublishListingAttribute> attributeValueIsNotNullList = new ArrayList<>();
			for (EbayPublishListingAttribute attributeObj : attributeList){
				if (StringUtils.isNotBlank(attributeObj.getAttributeValue())){
					attributeObj.setListingId(listingId);
					attributeObj.setCreationTime(new Date());
					attributeValueIsNotNullList.add(attributeObj);
				}
			}
			//只保存value不为空的属性
			if (CollectionUtils.isNotEmpty(attributeValueIsNotNullList))
				listingAttributeMapper.insertAttributeList(attributeList);
			logger.info("updateListing-->attribute end");
		}
		//运输政策修改
		if (vo.getShippingService()!=null){
			listingShippingMapper.deleteByListingId(listingId);
			EbayPublishListingShipping shipping = vo.getShippingService();
			shipping.setListingId(listingId);
			shipping.setCreationTime(new Date());
			listingShippingMapper.insertSelective(shipping);
			logger.info("updateListing-->shipping end");
			ebayPublishListingShippingTransportMapper.deleteByListingId(listing.getId());
			for (EbayPublishListingShippingTransport transport : shipping.getInternationalShippingServiceOptions()) {
				transport.setTransportType(2);
				if(StringUtils.isBlank(transport.getShippingService())){
					continue;
				}
				transport.setListingId(listing.getId());
				transport.setShippingId(shipping.getId().longValue());
				ebayPublishListingShippingTransportMapper.insertSelective(transport);
			}
			for (EbayPublishListingShippingTransport transport : shipping.getShippingServiceOptions()) {
				transport.setTransportType(1);
				if(StringUtils.isBlank(transport.getShippingService())){
					continue;
				}
				transport.setListingId(listing.getId());
				transport.setShippingId(shipping.getId().longValue());
				ebayPublishListingShippingTransportMapper.insertSelective(transport);
			}

		}
		//变体信息修改  (先删后插入原则)
		//刪除修改信息
		delVariaantValue(listingId);
		if (vo.getListingType() == 2){
			//执行入库操作 
			setVariantValue(listingId,vo);
			logger.info("updateListing-->variantValue end");
		}else{
			EbayPublishListingVariant listingVariant = null;
			if(vo.getVariant()!=null && vo.getVariant().size()>0){
				listingVariant = vo.getVariant().get(0);
			}else{
				listingVariant = new EbayPublishListingVariant();
			}
			listingVariant.setListingId(listingId);
			listingVariantMapper.insertSelective(listingVariant);

			if(vo.getListVariantSkus()!=null && vo.getListVariantSkus().size()>0) {
				for (EbayPublishListingVariantSkus variantSkus : vo.getListVariantSkus()) {
					variantSkus.setListingId(listing.getId());
					variantSkus.setVariantId(listingVariant.getId().longValue());
					ebayPublishListingVariantSkusMapper.insertSelective(variantSkus);
				}
			}
		}
		insertLog(String.valueOf(userInfo.getUser().getUsername()),listingId,EbayOperationEnum.EDIT.getCode(),getUpdateValue(vo, queryListing),Long.valueOf(userDTO.getUserId()));
		//编辑提交类型为保存并刊登   并且修改前的数据不是下线状态
		if (vo.getStatus() == 2 || queryListing.getStatus() == 3 || queryListing.getStatus() == 4){
			setRemoteOrderRule(vo);
			vo.setStatus(queryListing.getStatus());
			vo.setItemid(queryListing.getItemid());
			//asyncTaskService.publish(vo);
			ebayOpertionSender.send(vo);
		}
	
	}
	
	@Override
	public void delListing(Integer listingId) throws Exception{
		EbayPublishListingNew selectByPrimaryKey = listingMapper.selectByPrimaryKey(listingId.longValue());
		if (null == selectByPrimaryKey){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"数据未找到");
		}
		Integer status = selectByPrimaryKey.getStatus();
		if (status ==2){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"数据正在刊登中,请稍后处理");
		}
		//如果是草稿或刊登失败的数据。则直接改为删除状态
		if (status == 1 || status == 5 || status ==8){
			EbayPublishListingNew listing = new EbayPublishListingNew();
			listing.setId(listingId.longValue());
			listing.setStatus(6); //改为删除状态
			listingMapper.updateByPrimaryKeySelective(listing);
		}else if (status ==4){  //在线状态改为下线
			String itemid = selectByPrimaryKey.getItemid();
			PublishListingVO vo = new PublishListingVO();
			vo.setItemid(itemid);
			vo.setId(listingId.longValue());
			vo.setEmpowerId(selectByPrimaryKey.getEmpowerId());
			//单属性产品下架
			if (StringUtils.isNotEmpty(itemid) && selectByPrimaryKey.getListingType() !=2){
				ebayBaseService.endItem(vo);
			}
			//多属性产品下架
			if (StringUtils.isNotEmpty(itemid) && selectByPrimaryKey.getListingType() ==2){
				ebayBaseService.endFixedPriceItem(vo);
			}
			EbayPublishListingNew listing = new EbayPublishListingNew();
			listing.setId(listingId.longValue());
			listing.setStatus(3); //改为下线
			listing.setEndTimes(new Date()); //下线时间
			listingMapper.updateByPrimaryKeySelective(listing);
			UserDTO userDTO = getUserInfo.getUserDTO();
			insertLog(String.valueOf(userInfo.getUser().getUsername()),listingId,EbayOperationEnum.END.getCode(),"",Long.valueOf(userDTO.getUserId()));
		}
	}

	@Override
	public void insertListingCopy(Integer listingId) throws Exception {
		EbayPublishListingNew listing = listingMapper.selectByPrimaryKey(listingId.longValue());
		listing.setCreationTime(new Date());
		listing.setUpdateTime(new Date());
		listing.setId(null);
		listing.setItemid(null);
		listing.setStatus(1); //复制为草稿状态
		listing.setUpdateStatus(null);
		listing.setPlatformListing(1);
		listing.setOnlineTime(null);
		listing.setPublishTime(null);
		listing.setEndTimes(null);

		//用户
		UserDTO userDTO = getUserInfo.getUserDTO();
		if(userDTO.getManage()){
			listing.setSellerId(userDTO.getUserId().longValue());
			listing.setCreateId(userDTO.getUserId().longValue());
			listing.setCreateName(userDTO.getLoginName());
		}else{
			listing.setSellerId(userDTO.getTopUserId().longValue());
			listing.setCreateId(userDTO.getTopUserId().longValue());
			listing.setCreateName(userDTO.getTopUserLoginName());
		}

		listingMapper.insertSelective(listing);

		//复制数据入库后的id
		Integer newListingId = listing.getId().intValue();

		//详情部分
		EbayPublishListingDetail ebayPublishListingDetail = ebayPublishListingDetailMapper.selectByPrimaryKey(listingId.longValue());
		ebayPublishListingDetail.setId(null);
		ebayPublishListingDetail.setListingId(listing.getId());
		ebayPublishListingDetailMapper.insertSelective(ebayPublishListingDetail);
		//价格部分
		EbayPublishListingPrice ebayPublishListingPrice = ebayPublishListingPriceMapper.selectByPrimaryKey(listingId.longValue());
		ebayPublishListingPrice.setId(null);
		ebayPublishListingPrice.setListingId(listing.getId());
		ebayPublishListingPriceMapper.insertSelective(ebayPublishListingPrice);
		//退货政策
		EbayPublishListingReturnPolicy returnPolicy = ebayPublishListingReturnPolicyMapper.selectByPrimaryKey(listingId.longValue());
		returnPolicy.setId(null);
		returnPolicy.setListingId(listing.getId());
		ebayPublishListingReturnPolicyMapper.insertSelective(returnPolicy);


		//复制多属性变体相关信息
		if (listing.getListingType() ==2){
			EbayPublishListingVariant variant = new EbayPublishListingVariant();
			variant.setListingId(listingId);
			List<EbayPublishListingVariant> variantPage = listingVariantMapper.page(variant);
			variantPage.forEach(variantObj ->{variantObj.setListingId(newListingId);
				String platformSku=null;
				if(variantObj.getPlSku()!=null) {
					platformSku = this.findPlatformSku(listing.getSite(), variantObj.getPlSku(),
							listing.getPublishAccount(), listing.getId(), 1, listing.getCreateName());
				}
				variantObj.setPlatformSku(platformSku);
				variantObj.setUpc(null);
				variantObj.setEan(null);
				variantObj.setIsbn(null);
			});
			if (CollectionUtils.isNotEmpty(variantPage)){
				listingVariantMapper.insertBatchList(variantPage);
			}
			EbayPublishListingVariantPicture picture = new EbayPublishListingVariantPicture();
			picture.setListingId(listingId);
			List<EbayPublishListingVariantPicture> picturePage = listingVariantValuePictureMapper.page(picture);
			picturePage.forEach(pictureObj ->{pictureObj.setListingId(newListingId);});
			if (CollectionUtils.isNotEmpty(picturePage)){
				listingVariantValuePictureMapper.insertBatchList(picturePage);
			}
		}else{
			EbayPublishListingVariant variant = new EbayPublishListingVariant();
			variant.setListingId(listingId);
			List<EbayPublishListingVariant> ebaylistingVariantList = listingVariantMapper.page(variant);
			EbayPublishListingVariant listingVariant = ebaylistingVariantList.get(0);
			listingVariant.setId(null);
			listingVariant.setUpc(null);
			listingVariant.setEan(null);
			listingVariant.setIsbn(null);
			String platformSku=null;
			if(listingVariant.getPlSku()!=null) {
				platformSku = this.findPlatformSku(listing.getSite(), listingVariant.getPlSku(),
						listing.getPublishAccount(), listing.getId(), 1, listing.getCreateName());
			}
			listingVariant.setPlatformSku(platformSku);
			listingVariant.setListingId(newListingId);
			listingVariantMapper.insertSelective(listingVariant);
		}
		//复制卖家要求
		EbayPublishBuyerRequirements buyerRequirements = new EbayPublishBuyerRequirements();
		buyerRequirements.setListingId(listingId);
		List<EbayPublishBuyerRequirements> buyerRequirementsList = buyerRequirementsMapper.page(buyerRequirements);
		buyerRequirementsList.forEach(buyer ->{
			buyer.setId(null);
			buyer.setListingId(newListingId);
			buyer.setCreationTime(new Date());
			buyerRequirementsMapper.insert(buyer);
		});
		//复制商品属性  (包含推荐属性,自定属性)
		EbayPublishListingAttribute attribute = new EbayPublishListingAttribute();
		attribute.setListingId(listingId);
		List<EbayPublishListingAttribute> listingAttributeList = listingAttributeMapper.page(attribute);
		if (CollectionUtils.isNotEmpty(listingAttributeList)){
			listingAttributeList.forEach(listingAttribute ->{
				listingAttribute.setId(null);
				listingAttribute.setListingId(newListingId);
				listingAttribute.setCreationTime(new Date());
			});
			listingAttributeMapper.insertAttributeList(listingAttributeList);
		}
		//复制运输服务
		EbayPublishListingShipping ebayPublishListingShipping = new EbayPublishListingShipping();
		ebayPublishListingShipping.setListingId(listingId);
		List<EbayPublishListingShipping> page = listingShippingMapper.page(ebayPublishListingShipping);

		EbayPublishListingShippingTransport transportQuery = new EbayPublishListingShippingTransport();
		transportQuery.setListingId(listingId.longValue());
		List<EbayPublishListingShippingTransport> listTransport = ebayPublishListingShippingTransportMapper.page(transportQuery);

		page.forEach(shipping ->{
			shipping.setId(null);
			shipping.setListingId(newListingId);
			shipping.setCreationTime(new Date());
			listingShippingMapper.insertSelective(shipping);
			for(EbayPublishListingShippingTransport transport:listTransport){
				transport.setId(null);
				transport.setListingId(listing.getId());
				transport.setShippingId(shipping.getId().longValue());
				ebayPublishListingShippingTransportMapper.insertSelective(transport);
			}
		});
		insertLog(String.valueOf(userInfo.getUser().getUsername()),newListingId,EbayOperationEnum.COPY.getCode(), listing.getTitle(),Long.valueOf(userDTO.getUserId()));
	}

	/**
	 *
	 * @param
	 * @param site
	 * @param skus
	 * @param sellerName
	 * @param listingId
	 * @param sellerNameNum
	 * @param userName
	 */
	private String findPlatformSku(String site, String skus, String sellerName, Long listingId, Integer sellerNameNum, String userName){
		//平台 1:amazon 2:eBay 3:wish 4:aliexpress
		String platformSku=null;
		try {
			Map<String,String> map = publishTemplateService.findPlatformSku(2,site,skus,sellerName,listingId,sellerNameNum,userName);
			if("name".equals(map.get("tuleType"))){
				return map.get(sellerName+"1");
			}else{
				return map.get(skus);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return platformSku;
	}

	@Override
	public void relistItem(Integer listingId) throws Exception {
		/**
		 * 上线后的产品时间到期后，重新上线
		 * 复制为草稿的产品。刊登上ebay
		 */
		PublishListingVO listingByValue = getListingByValue(listingId,false);
		if (3 != listingByValue.getStatus() && 1 != listingByValue.getStatus() && 5!=listingByValue.getStatus()){  //下架,复制后的草稿的才能重刊登。
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "此状态不能参与刊登");
		}
		//验证参数是否正常
		this.check(listingByValue,true);
		//费用校验
		if (listingByValue.getListingType() ==2){ //多属性费用校验
			Double fixedPrice = ebayBaseService.verifyAddFixedPriceItem(listingByValue);
		}else {
			Double fixedPrice = ebayBaseService.verifyAddItem(listingByValue);
		}

		String msg = listingByValue.getStatus() == 3 ?EbayOperationEnum.RELIST.getCode():EbayOperationEnum.DRAFT_RELIST.getCode();
		if (listingByValue.getStatus() == 5){
			msg = EbayOperationEnum.FAIL_RELIST.getCode();
		}
		//修改发布时间
		EbayPublishListingNew updatePublishListing = new EbayPublishListingNew();
		updatePublishListing.setId(listingId.longValue());
		updatePublishListing.setPublishTime(new Date());
		updatePublishListing.setUpdateTime(new Date());
		listingMapper.updateByPrimaryKeySelective(updatePublishListing);
		UserDTO userDTO = getUserInfo.getUserDTO();
		insertLog(String.valueOf(userInfo.getUser().getUsername()),listingId,msg, listingByValue.getTitle(),Long.valueOf(userDTO.getUserId()));
		//asyncTaskService.publish(listingByValue);
		ebayOpertionSender.send(listingByValue);
	}

	@Override
	public List<EbayPublishListingError> listingErrorView(Integer listingId) throws Exception {
		EbayPublishListingError error = new EbayPublishListingError();
		error.setListingId(listingId);
		return listingErrorMapper.page(error);
	}
	@Override
	public void insertListingError(EbayPublishListingError error){
		listingErrorMapper.insertSelective(error);
	}

	@Override
	public Page<EbayPublishListingDTO> find(PublishListingSearchVO vo) throws Exception {
		 Page.builder(vo.getPage(), vo.getRow());
		 List<EbayPublishListingDTO> list = listingMapper.findPage(vo);
		 List<Integer> userIds = new ArrayList<>();
		 List<Long> listingIds = new ArrayList<>();
		 list.forEach(listing ->{
		 	if(listing.getCreateId()!=null) {
				userIds.add(listing.getCreateId().intValue());
				listingIds.add(listing.getId().longValue());
			}
		 });
		 Map<Long,String> userMap = this.getUsers(userIds);
		 for(EbayPublishListingDTO listing:list){
			 listing.setStatusValue(getStatusValue(listing.getStatus()));
			 if(userMap!=null){
				 listing.setCreateName(userMap.get(listing.getCreateId()));
			 }
			 List<EbayPublishListingVariant> variantList=listing.getVariantList();
			 if(variantList!=null && StringUtils.isNotEmpty(listing.getWarehouseCode())){
				 variantList.forEach(variantSku->{
					 if(StringUtils.isNotEmpty(variantSku.getPlSku())){
                         String warehouseId =listing.getWarehouseCode().split(",")[0];
						 CommodityStatusVO commodityStatusVO = this.getCommodityStatusVOBySku(warehouseId, variantSku.getPlSku(),1,listing.getSite());
						 if(commodityStatusVO!=null){
							 variantSku.setShowStatus(commodityStatusVO.getShowStatus());
							 variantSku.setCommodityPriceUs(commodityStatusVO.getCommodityPriceUs());
							 variantSku.setAvailableQty(commodityStatusVO.getAvailableQty());
						 }
					 }
				 });
			 }
			 if(listingIds.size()>0) {
				 //捆绑sku
				 List<EbayPublishListingVariantSkus> listVariantSkus= ebayPublishListingVariantSkusMapper.getVariantSkusByListingIds(listingIds);
				 for (EbayPublishListingVariant variantSku : variantList) {
					 //捆绑sku
					 if (listVariantSkus != null && listVariantSkus.size() > 0) {
						 List<EbayPublishListingVariantSkus> addVariantSkus = Lists.newArrayList();
						 for (EbayPublishListingVariantSkus variantSkus : listVariantSkus) {
							 if (variantSkus.getVariantId().equals(variantSku.getId().longValue())) {
								 if (StringUtils.isNotEmpty(listing.getWarehouseCode()) && StringUtils.isNotEmpty(variantSku.getPlSku())) {
									 String warehouseId = listing.getWarehouseCode().split(",")[0];
									 CommodityStatusVO commodityStatusVO = this.getCommodityStatusVOBySku(warehouseId, variantSku.getPlSku(), 4, null);
									 if (commodityStatusVO != null) {
										 variantSkus.setShowStatus(commodityStatusVO.getShowStatus());
										 variantSkus.setAvailableQty(commodityStatusVO.getAvailableQty());
									 }
								 }
								 addVariantSkus.add(variantSkus);
							 }
						 }
						 variantSku.setListVariantSkus(addVariantSkus);
						 listVariantSkus.removeAll(addVariantSkus);
					 }
				 }
			 }


			 if(listing.getListingType()!=2){
				 if(variantList!=null && variantList.size()>0) {
					 EbayPublishListingVariant variant = variantList.get(0);
					 listing.setPlSku(variant.getPlSku());
					 listing.setPlatformSku(variant.getPlatformSku());
					 if(variant.getStartPrice()!=null) {
						 listing.setStartPrice(variant.getStartPrice().toString());
					 }
					 listing.setQuantity(variant.getQuantity());
				 }
			 }else {
				 if (variantList != null && variantList.size() == 1) {
					 EbayPublishListingVariant variant = variantList.get(0);
					 if (variant.getStartPrice() != null) {
						 listing.setStartPrice(variant.getStartPrice().toString());
					 }
                     variant.setPicture(listing.getPicture());
				 } else if (variantList != null && variantList.size() > 1) {
					 BigDecimal startPrice = null;
					 BigDecimal endPrice = null;
					 for (EbayPublishListingVariant variantSku : variantList) {
						 if (variantSku.getStartPrice() != null) {
							 if (startPrice == null || startPrice.doubleValue() > variantSku.getStartPrice().doubleValue()) {
								 startPrice = variantSku.getStartPrice();
							 }
							 if (endPrice == null || startPrice.doubleValue() < variantSku.getStartPrice().doubleValue()) {
								 endPrice = variantSku.getStartPrice();
							 }
						 }
					 }
					 if (startPrice != null && endPrice != null) {
					 	if(startPrice.doubleValue()==endPrice.doubleValue()) {
							listing.setStartPrice(startPrice.toString());
						}else
						{
							listing.setStartPrice(startPrice.toString()+"~"+endPrice.toString());
						}
					 }
				 }
			 }
		 }
		 //重新组装page对像。返回前端
		 PageInfo<EbayPublishListingDTO> pageInfo = new PageInfo<EbayPublishListingDTO>();
		 com.github.pagehelper.Page pages = (com.github.pagehelper.Page) list;
		 BeanUtils.copyProperties(pages, pageInfo);
		 pageInfo.setList(list);
		 pageInfo.setSize(pages.size());
		 Page<EbayPublishListingDTO> page = new Page<>(pageInfo);
		 return  page;
	}
	@Override
	public Map<Long,String> getUsers(List<Integer> userIds){
		if(userIds==null || userIds.size()==0){
			return null;
		}
		Map<Long,String> map = Maps.newHashMap();
		Integer[] userId = (Integer[]) userIds.toArray(new Integer[userIds.size()]);

		try {
			String userStr = remoteUserService.getSupplierList(userId,  1);
			logger.info("刊登时请求用户返回数据：{}", userStr);
			String jsonStr = Utils.returnRemoteResultDataString(userStr, "用户服务异常");
			List<UserVO> list= JSONObject.parseArray(jsonStr, UserVO.class);
			if(list!=null){
				for(UserVO uv:list){
					map.put(Long.valueOf(uv.getUserId()),uv.getUserName());
				}
			}

		}catch (Exception e){
			e.printStackTrace();
		}

		return map;
	}

	@Override
	public List<EbayPublishListingVariant> getListingVariantByItemIdPlatformSku(String itemId, String platformSku) {
		return listingVariantMapper.getListingVariantByItemIdPlatformSku(itemId,platformSku);
	}

	@Override
	public CommodityStatusVO getCommodityStatusVOBySku(String warehouseId,String sku,Integer platform,String siteCode){
		String result = remoteSupplierProviderService.getSellerInv(Integer.valueOf(warehouseId),sku,platform,siteCode);
		CommodityStatusVO vo = null;
		JSONObject jsonObject=JSONObject.parseObject(result);
		if (jsonObject!=null && jsonObject.getJSONObject("data")!=null){
			vo = JSONObject.parseObject(jsonObject.getString("data"),CommodityStatusVO.class);
			//判断顺序 上下架  是否侵权 ， 是否缺货  是否低于预警
			if(vo!=null){
				vo.setPlSku(sku);
				vo.setShowStatus(0);
				if(1 == vo.getStatus()){
					vo.setShowStatus(1);//下架
				}else{
					if(1 == vo.getTortFlag()){
						vo.setShowStatus(2);//侵权
					}else{
						if((vo.getAvailableQty()==null ||vo.getAvailableQty()==0) && vo.getTortFlag()==0){
							vo.setShowStatus(3);//缺货
						}else{
							if(vo.getWarnVal()!=null && vo.getWarnVal()>0){
								if(vo.getWarnVal()>=vo.getAvailableQty()) {
									vo.setShowStatus(4);//低于预警
								}
							}
						}
					}
				}
			}
		}
		return vo;
	}

	@Override
	public List<ResultPublishListingVO> getEbayResultPublishListingVO(Integer empowerId, List<String> platformSkus) {
		return listingMapper.getEbayResultPublishListingVO(empowerId,platformSkus);
	}

	@Override
    public Page<EbayHistoryDTO> getEbayHistoryPage(PublishListingSearchVO vo) throws Exception {
		Page.builder(vo.getPage(), vo.getRow());

		List<String> spus = listingMapper.getEbayHistoryPage(vo);

		List<EbayHistoryDTO> listPage = new ArrayList<>();
        if(spus!=null && spus.size()>0) {
            String spuStr = remoteCommodityService.getCommodityForSPU(spus);
            if (spuStr != null) {
                String result = Utils.returnRemoteResultDataString(spuStr, "转换失败");
                JSONArray jsonArray = JSONArray.parseArray(result);
                if (jsonArray.size() > 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        if (json != null && json.get("SPU") != null) {
                            String commodityNameEn = json.get("commodityNameEn").toString();
                            String plSpu = json.get("SPU").toString();

                            EbayHistoryDTO historyDTO = new EbayHistoryDTO();
                            historyDTO.setPlSpu(plSpu);
                            historyDTO.setTitle(commodityNameEn);
                            listPage.add(historyDTO);
                        }
                    }
                }


            }
        }
		//重新组装page对像。返回前端
		PageInfo<EbayHistoryDTO> pageInfo = new PageInfo<EbayHistoryDTO>();
		com.github.pagehelper.Page pages = (com.github.pagehelper.Page) spus;
		BeanUtils.copyProperties(pages, pageInfo);
		pageInfo.setList(listPage);
		pageInfo.setSize(pages.size());
		Page<EbayHistoryDTO> page = new Page<>(pageInfo);
		return page;
    }

    public String getStatusValue(Integer status){
		switch (status) {
		case 1:
			return "草稿";
		case 2:
			return "刊登中";
		case 3:
			return "已下线";
		case 4:
			return "在线";
		case 5:
			return "刊登失败";
		default:
			break;
		}
		return null;
	}
	


	@Override
	public Double verify(PublishListingVO vo) throws Exception {
		check(vo,false);
		if (vo.getListingType() ==2){ //多属性费用校验
			return ebayBaseService.verifyAddFixedPriceItem(vo);
		}
		return ebayBaseService.verifyAddItem(vo);
	}
	
	/**
	 * listing 对象校验 
	 * @param vo 接收到的数据对像
	 * @param isEdit 是否是编辑
	 */
	public void check(PublishListingVO vo,boolean isEdit){
		if (isEdit){
			if (null == vo.getId()){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "编辑对像id不能为空");
			}
		}
		if (null == vo.getEmpowerId())
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "授权信息不能为空");
		if (StringUtils.isBlank(vo.getSite()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "刊登站点不能为空");
		if (StringUtils.isBlank(vo.getProductCategory1()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品分类不能为空");
		if (vo.getProductCategory1().equals(vo.getProductCategory2()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品分类1与产品分类2相同");
		if (null == vo.getListingType())
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "刊登类型不能为空");
		if (StringUtils.isBlank(vo.getTitle()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "标题不能为空");
		if (StringUtils.isBlank(vo.getListingDuration()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "刊登天数不能为空");
		if (vo.getListingType() !=2){ //多属性刊登时。这两个值放放变体中上传
			if (null == vo.getQuantity() || 0>vo.getQuantity())
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "可售数不能为空,不能为负数");
			BigDecimal bigDecimal = new BigDecimal(0);
			if (null == vo.getStartPrice() || bigDecimal.compareTo(vo.getStartPrice())==1)
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "价格不能为空,不能为负数");
		}else{
			if (vo.getVariant()==null || vo.getVariant().size()==0){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "多属性刊登时,变体信息不能为空");
			}
		}
		//拍卖类型校验
		if (vo.getListingType() == 3){ 
			if (vo.getBestOfferEnabled()){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "拍卖类型刊登时,不接受最佳报价");
			}
//			if(null == vo.getBuyItNowPrice() || null == vo.getFloorPrice()){
//				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "拍卖类型刊登时,拍卖一口价与拍卖保底价不能为空");
//			}
		}
		if (vo.getBestOfferEnabled()==null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "自动接受价格或自动拒绝价格不能为空");
		}
		if (vo.getBestOfferEnabled() && (vo.getMinimumBestOfferPrice() == null || vo.getAutoAcceptPrice() == null)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "自动接受价格或自动拒绝价格不能为空");
		}
		if (StringUtils.isBlank(vo.getListingDesc())) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品描述不能为空");
		}
//		else{
//			if(vo.getStyleId()!=null){//填充模板
//				PublishStyle publishStyle = publishStyleMapper.selectByPrimaryKey(vo.getStyleId());
//				if(publishStyle!=null){
//					String content = publishStyle.getContent();
//					if(!StringUtils.isBlank(content)){
//						content = content.replace("[TITLE]",vo.getTitle());
//						content = content.replace("[DESCRIPTION]",vo.getListingDescOriginal());
//						vo.setListingDesc(content);
//					}else{
//						vo.setListingDesc(vo.getListingDescOriginal());
//					}
//				}else{
//					vo.setListingDesc(vo.getListingDescOriginal());
//				}
//			}else{
//				vo.setListingDesc(vo.getListingDescOriginal());
//			}
//
//		}
		if (StringUtils.isBlank(vo.getPicture()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品图片不能为空"); 
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
	    Matcher m = p.matcher(vo.getPicture());
	    if (m.find())
	    	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "ebay刊登时,产品图片路径中不允许出现中文字符"); 
		if (StringUtils.isBlank(vo.getLocal()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物品所在地不能为空");
		if (StringUtils.isBlank(vo.getCountry()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物品所在国家不能为空");
		if (StringUtils.isBlank(vo.getCurrency()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "币种不能为空");
		if (vo.getShippingService()==null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "运输服务不能为空");
		//运输服务栓验是否支持
		EbayPublishListingShipping listingShipping = vo.getShippingService();
		if (isEdit) {
			if (listingShipping.getId() == null) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "编辑对像shippingService:id不能为空");
			}
		}
	    //国内运输方案
		List<EbayPublishListingShippingTransport> shippingList = listingShipping.getShippingServiceOptions();
		StringBuilder sbService = new StringBuilder(); 
		Map<String, Map<String, String>> map = getShippingServiceMap(vo.getSite());
		//验证每一个的运输服务是否支持,不支持则返回给前端对应的运输方式的描述信息
		if(shippingList!=null) {
			shippingList.forEach(shipping -> {
				shipping.setTransportType(1);
				String service = shipping.getShippingService();
				if (StringUtils.isNotBlank(service)) {
//					try {
//						ShippingServiceCodeType.fromValue(service);
//					} catch (Exception e) {
//						Map<String, String> domestic = map.get("domestic");
//						String serviceDesc = domestic.get(service);
//						sbService.append(serviceDesc + " ");
//					}
				}
			});
		}
		if (sbService.length()>0){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国内运输暂不支持以下 "+sbService.toString()+" 运输方式");
		}
	    //国际运输方案
		List<EbayPublishListingShippingTransport> internationalShippingserviceL = listingShipping.getInternationalShippingServiceOptions();
		StringBuilder internationalService = new StringBuilder();
		if(internationalShippingserviceL!=null) {
			internationalShippingserviceL.forEach(internationalServiceShipping -> {
				internationalServiceShipping.setTransportType(2);
				String service = internationalServiceShipping.getShippingService();
				if (StringUtils.isNotBlank(service)) {
//					try {
//						ShippingServiceCodeType.fromValue(service);
//					} catch (Exception e) {
//						Map<String, String> internation = map.get("internation");
//						String internationServiceDesc = internation.get(service);
//						internationalService.append(internationServiceDesc + " ");
//					}
					if (StringUtils.isBlank(internationalServiceShipping.getShipToLocation())){
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "可送至国家不能为空");
					}
				}


			});
		}
		if (internationalService.length()>0){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国际运输暂时不支持以下 "+internationalService.toString()+" 运输方式");
		}
		if (vo.getReturnPolicy()==null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "退货正常不能为空");

		if(vo.getPaymentOption()!=null){
			List<String> listPayment = JSONObject.parseArray(vo.getPaymentOption(), String.class);
			boolean isPayPal = false;
			for (String strPayPal:listPayment){
				if("PayPal".equals(strPayPal)){
					isPayPal = true;
					break;
				}
			}
			if(isPayPal){
				if (StringUtils.isBlank(vo.getPaypal())) {
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "Paypal帐号不能为空");
				}
			}
		}
//		if (StringUtils.isBlank(vo.getPaypal()))
//			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "Paypal帐号不能为空");
		if (!vo.getDisableBuyerRequirements()){
			EbayPublishBuyerRequirements buyerRequirements = vo.getDisableBuyerRequirementsValue();
			if(buyerRequirements==null){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "当卖家要求开启时,要求不能为空");
			}
		}
		//校验站点与分类是否匹配
		EbayProductCategory category = new EbayProductCategory();
		category.setCategoryid(vo.getProductCategory1());
		category.setSite(vo.getSite());
		List<EbayProductCategory> findCategoryByValue = ebayBaseService.findCategoryByValue(category);
		if (CollectionUtils.isEmpty(findCategoryByValue)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "当前站点无法适用该分类");
		}
		if (StringUtils.isNotBlank(vo.getProductCategory2())){
			EbayProductCategory category2 = new EbayProductCategory();
			category2.setCategoryid(vo.getProductCategory2());
			category2.setSite(vo.getSite());
			List<EbayProductCategory> findCategoryByValue2 = ebayBaseService.findCategoryByValue(category);
			if (CollectionUtils.isEmpty(findCategoryByValue2)){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "当前站点无法适用该分类2");
			}
		}
		//校验站点与币种是否对应
		EbaySite ebaySite = new EbaySite();
		ebaySite.setSiteName(vo.getSite());
		ebaySite.setCurrency(vo.getCurrency());
		List<EbaySite> page = ebayBaseService.findSiteByValue(ebaySite);
		if (CollectionUtils.isEmpty(page)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, vo.getSite()+" 站点与币种  "+vo.getCurrency()+" 不匹配");
		}

		if(vo.getListingType()!=2){
			EbayPublishListingVariant listingVariant = vo.getProductListingDetails();
			if(listingVariant==null){
				listingVariant = new EbayPublishListingVariant();
			}
			listingVariant.setPlSku(vo.getPlSku());
			listingVariant.setStartPrice(vo.getStartPrice());
			listingVariant.setPlatformSku(vo.getPlatformSku());
			listingVariant.setQuantity(vo.getQuantity());
			listingVariant.setEstimatedFreight(vo.getEstimatedFreight());
			listingVariant.setForecastProfits(vo.getForecastProfits());
			listingVariant.setPlatformCommission(vo.getPlatformCommission());
			listingVariant.setFreightFee(vo.getFreightFee());
			listingVariant.setOtherFee(vo.getOtherFee());
			listingVariant.setOtherFee1(vo.getOtherFee1());
			listingVariant.setOtherFee2(vo.getOtherFee2());
			listingVariant.setOtherFee3(vo.getOtherFee3());
			listingVariant.setProductReferenceID(vo.getProductReferenceID());
			vo.getVariant().add(listingVariant);
		}
        Map<String,Object> mapProductPlatformSku= Maps.newHashMap();//平台sku是否重复
		List<EbayPublishListingVariant> parseArray = vo.getVariant();
		if (parseArray!=null && parseArray.size()>0) {
			for(EbayPublishListingVariant product:parseArray){
				String platformSku = product.getPlatformSku();
                if(mapProductPlatformSku.get(platformSku)!=null){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台sku重复");
                }
                mapProductPlatformSku.put(platformSku,platformSku);
				if(StringUtils.isBlank(platformSku)){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台sku不能为空");
				}else{
					Long id = vo.getId()==null?null:vo.getId().longValue();
					List<EbayPublishListingVariant> listPublishListingVariant = listingVariantMapper.getVariantByPlatformSku(platformSku,id);
					if(listPublishListingVariant.size()>0){
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台sku重复");
					}
				}
			}
		}

//        if (StringUtils.isBlank(vo.getShipCountry())) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "收货国家不能为空");
//        }
//        if (StringUtils.isBlank(vo.getLogisticsType())) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流类型不能为空");
//        }
//        if (StringUtils.isBlank(vo.getWarehouseCode())) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库不能为空");
//        }

	}
	
	/**
	 * 取国际,国内的运输方式用来匹配返回给前端
	 * @param site
	 * @return
	 */
	public Map<String,Map<String,String>> getShippingServiceMap(String site){
		Map<String,Map<String,String>> map = new HashMap<>();
		EbaySiteDetail obj = new EbaySiteDetail();
		obj.setSite(site);
		List<EbaySiteDetail> findSiteDetail = ebayBaseService.findSiteDetail(obj);
		EbaySiteDetail ebaySiteDetail = findSiteDetail.get(0);
		String shipping = ebaySiteDetail.getShipping();
		JSONObject parseObject = JSONObject.parseObject(shipping);
		Object domestic = parseObject.get("domestic");
		String jsonString = JSONObject.toJSONString(domestic);
		String substring = jsonString.substring(1,jsonString.length()-1).replaceAll("\\\\","");
		List<ShippingDetail> domesticParseArray = JSON.parseArray(substring, ShippingDetail.class);
		Map<String,String> mapDomestic = new HashMap<>();
		domesticParseArray.forEach(stic->{
			mapDomestic.put(stic.getShippingService(), stic.getDesc());
		});
		map.put("domestic", mapDomestic);
		Object internation = parseObject.get("internation");
		String internationJsonString = JSONObject.toJSONString(internation);
		String internationStr = internationJsonString.substring(1,internationJsonString.length()-1).replaceAll("\\\\","");
		List<ShippingDetail> internationDomesticParseArray = JSON.parseArray(internationStr, ShippingDetail.class);
		Map<String,String> mapInternation = new HashMap<>();
		internationDomesticParseArray.forEach(stic->{
			mapInternation.put(stic.getShippingService(), stic.getDesc());
		});
		map.put("internation", mapInternation);
		return map;
	}
	

	
	/**
	 * 定位编辑信息
	 * @param vo
	 * @param obj
	 * @return
	 */
	public String getUpdateValue(PublishListingVO vo,EbayPublishListingNew obj){
		StringBuilder sb = new StringBuilder();
		try {
//			if (!obj.getEmpowerId().equals(vo.getEmpowerId())||
//					!obj.getSite().equals(vo.getSite())||
//					!obj.getProductCategory1().equals(vo.getProductCategory1())||
//					!obj.getTitle().equals(vo.getTitle())||
//					!obj.getListingType().equals(vo.getListingType())||
//					!obj.getListingDuration().equals(vo.getListingDuration())){
//					sb.append("基本信息    ");
//				}
//				logger.info("基本数据信息比对end");
//				EbayPublishListingAttribute ebayPublishListingAttribute = new EbayPublishListingAttribute();
//				ebayPublishListingAttribute.setListingId(obj.getId());
//				List<EbayPublishListingAttribute> attribute = listingAttributeMapper.page(ebayPublishListingAttribute);
//				//编辑上传的值
//				List<EbayPublishListingAttribute> editAttribute = JSONObject.parseArray(vo.getAttributeValue(), EbayPublishListingAttribute.class);
//				if (CollectionUtils.isNotEmpty(attribute) && CollectionUtils.isNotEmpty(editAttribute) && attribute.size() == editAttribute.size()){
//					Map<String,String> map = new HashMap<>();
//					attribute.forEach(att->{
//						map.put(att.getAttributeKey(), att.getAttributeValue());
//					});
//					int index = 0;
//					//查看每一个值是否能匹配上
//					for(EbayPublishListingAttribute edit : editAttribute){
//						String string = map.get(edit.getAttributeKey());
//						if (null == string || !edit.getAttributeValue().equals(string)){
//							index++;
//							break;
//						}
//					}
//					if (index >0)
//						sb.append("属性值    ");
//				}else{
//					sb.append("属性值    ");
//				}
//				logger.info("属性值比对end");
//				if (vo.getListingType() !=3){
//					if (!obj.getPlSku().equals(vo.getPlSku())||
//						!obj.getQuantity().equals(vo.getQuantity())||
//						obj.getStartPrice().compareTo(vo.getStartPrice()) !=0){
//						sb.append("产品信息    ");
//					}
//				}else{
//					if (!obj.getPlSku().equals(vo.getPlSku())||
//						!obj.getQuantity().equals(vo.getQuantity())||
//						obj.getStartPrice().compareTo(vo.getStartPrice()) !=0||
//						obj.getBuyItNowPrice().compareTo(vo.getBuyItNowPrice())!=0 ||
//						obj.getFloorPrice().compareTo(vo.getFloorPrice())!=0){
//							sb.append("产品信息    ");
//					}
//				}
//				logger.info("产品信息比对end");
//				if (!vo.getPaypal().equals(obj.getPaypal())||
//					!vo.getPaymentOption().equals(obj.getPaymentOption())	){
//					sb.append("付款政策    ");
//				}
//				logger.info("付款政策比对end");
//
//				if (!vo.getConditionId().equals(obj.getConditionId())||
//						!vo.getConditionDescription().equals(obj.getConditionDescription())||
//						!vo.getDescription().equals(obj.getDescription())){
//						sb.append("描述信息    ");
//				}
//				logger.info("描述信息 end");
//
//				if (vo.getDisableBuyerRequirements() !=obj.getDisableBuyerRequirements()){
//					sb.append("买家条件     ");
//				}
//				logger.info("买家条件 end");
//
//				if (!vo.getReturnPolicy().equals(obj.getReturnPolicy())){
//					sb.append("退货政策      ");
//				}
//				logger.info("退货政策   end");
				
//				String shippingServie = null;
//				EbayPublishListingShipping ebayPublishListingShipping = new EbayPublishListingShipping();
//				ebayPublishListingShipping.setListingId(obj.getId());
//				List<EbayPublishListingShipping> shippingList = listingShippingMapper.page(ebayPublishListingShipping);
//				if (CollectionUtils.isNotEmpty(shippingList)){ //如果有据，也只是一条
//					shippingServie = JSONObject.toJSONString(shippingList.get(0));
//				}
//				if (!vo.getShippingService().equals(shippingServie)){
//					sb.append("物流政策   ");
//				}
		} catch (Exception e) {
			logger.debug("编辑比对异常");
		}
		return sb.toString();
	}

	@Override
	public Double verifyRelist(Integer listingId) throws Exception {
		if (null == listingId){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"请检查参数listingId");
		}
		PublishListingVO listingByValue = getListingByValue(listingId,false);
		if (null == listingByValue){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"未找到数据!");
		}
		if (listingByValue.getStatus() !=3){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"只能下架产品参与检测");
		}
		return ebayBaseService.verifyRelistItem(listingByValue);
	}

	@Override
	public Page<EbayPublishListingAPPDTO> findToAppByPage(PublishListingAppSearchVO vo) throws Exception {
		 Page.builder(vo.getPage(), vo.getRow());
		 List<EbayPublishListingAPPDTO> list = listingMapper.pageByApp(vo);
		 PageInfo<EbayPublishListingAPPDTO> pageInfo = new PageInfo<>(list);
		 Page<EbayPublishListingAPPDTO> page = new Page<>(pageInfo);
		 return  page;
	}

	@Override
	public int getOnlineCount(Long sellerId) throws Exception {
		return listingMapper.getOnlineCountBySeller(sellerId);
	}

	@Override
	public Map<String, Object> getDispatchTimeMax(EbayMaxTimeVO vo) {
		List<Map<String,Object>> list = listingMapper.getDispatchTimeMax(vo);
		Map<String, Object> map= Maps.newHashMap();
		if(list!=null){
			for(Map<String,Object> vv:list){
				map.put(vv.get("itemId").toString(),vv.get("maxTime"));

			}
		}
		return map;
	}
	@Override
	public List<Map<String, Object>> getEbaySkuNumber() {
		List<Map<String,Object>> list = listingMapper.getEbaySkuNumber();
		return list;
	}



	private Integer getSellerId(){
		Integer userId = null;
		UserDTO userDTO = getUserInfo.getUserDTO();
		if(userDTO.getManage()){
			userId = userDTO.getUserId();
		}else{
			userId = userDTO.getTopUserId();
		}
		return userId;
	}

}
