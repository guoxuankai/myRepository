package com.rondaful.cloud.seller.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.security.UserSession;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.seller.common.aliexpress.ImgSet;
import com.rondaful.cloud.seller.common.aliexpress.JsonAnalysis;
import com.rondaful.cloud.seller.config.AliexpressConfig;
import com.rondaful.cloud.seller.dto.AliexpressPublishListingDTO;
import com.rondaful.cloud.seller.dto.AliexpressPublishListingExcelDTO;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.entity.aliexpress.*;
import com.rondaful.cloud.seller.enums.AliexpressEnum;
import com.rondaful.cloud.seller.enums.AliexpressOperationEnum;
import com.rondaful.cloud.seller.enums.AliexpressProductUnitEnum;
import com.rondaful.cloud.seller.mapper.*;
import com.rondaful.cloud.seller.remote.RemoteCommodityService;
import com.rondaful.cloud.seller.remote.RemoteOrderRuleService;
import com.rondaful.cloud.seller.service.IAliexpressBaseService;
import com.rondaful.cloud.seller.service.IAliexpressPublishListingService;
import com.rondaful.cloud.seller.service.IEbayPublishListingService;
import com.rondaful.cloud.seller.utils.GeneratePlateformSku;
import com.rondaful.cloud.seller.utils.ValidatorUtil;
import com.rondaful.cloud.seller.vo.AliexpressPublishListingSearchVO;
import com.rondaful.cloud.seller.vo.CodeAndValueVo;
import com.rondaful.cloud.seller.vo.CommodityStatusVO;
import com.rondaful.cloud.seller.vo.ResultPublishListingVO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import sun.misc.BASE64Encoder;


/**
 *速卖通 api参考地址
 *
 *开发者中心
 *
 * @author chenhan
 *
 */

@Service
public class AliexpressPublishListingServiceImpl implements IAliexpressPublishListingService {

    private final Logger logger = LoggerFactory.getLogger(AliexpressPublishListingServiceImpl.class);

	@Autowired
	private AliexpressPublishListingMapper aliexpressPublishListingMapper;
	@Autowired
	private AliexpressPublishListingDetailMapper aliexpressPublishListingDetailMapper;
	@Autowired
	private AliexpressPublishListingAttributeMapper aliexpressPublishListingAttributeMapper;
	@Autowired
	private AliexpressPublishListingProductMapper aliexpressPublishListingProductMapper;
	@Autowired
	private AliexpressPublishListingSkuPropertyMapper aliexpressPublishListingSkuPropertyMapper;
	@Autowired
	private AliexpressPublishListingProductSkusMapper aliexpressPublishListingProductSkusMapper;
	@Autowired
	private AliexpressOperationLogMapper aliexpressOperationLogMapper;
	@Autowired
	private AliexpressProductCountryPriceMapper aliexpressProductCountryPriceMapper;
	@Autowired
	private AliexpressCategoryAttributeSelectMapper aliexpressCategoryAttributeSelectMapper;
	@Autowired
	private AliexpressCategoryMapper aliexpressCategoryMapper;
	@Autowired
	private EmpowerMapper empowerMapper;
	@Autowired
	private AliexpressConfig config;
	@Autowired
	private RemoteOrderRuleService remoteOrderRuleService;
	@Autowired
	private RemoteCommodityService remoteCommodityService;
	@Autowired
	private AliexpressPublishListingErrorMapper aliexpressPublishListingErrorMapper;
	@Autowired
	private AliexpressCategoryAttributeMapper aliexpressCategoryAttributeMapper;
	@Autowired
	private GetLoginUserInformationByToken getUserInfo;
	@Autowired
	private  IEbayPublishListingService ebayPublishListingService;
	@Autowired
	private  IAliexpressBaseService aliexpressBaseService;

	@Override
	public Page<AliexpressPublishListingDTO> findPage(AliexpressPublishListingSearchVO vo) throws Exception {
		PageHelper.startPage(vo.getPage(), vo.getRow());
		List<AliexpressPublishListingDTO> list = aliexpressPublishListingMapper.findPage(vo);
		List<Long> publishListingIds = Lists.newArrayList();
		for(AliexpressPublishListingDTO dto:list){
			publishListingIds.add(dto.getId());
		}
		if(publishListingIds!=null &&publishListingIds.size()>0){
			List<AliexpressPublishListingSkuPropertyModel> listSkuPropertyModel = aliexpressPublishListingSkuPropertyMapper.getPublishListingSkuPropertyByListingIds(publishListingIds);
			List<AliexpressPublishListingProductSkus> listProductSkus = aliexpressPublishListingProductSkusMapper.getProductSkusByPublishListingIds(publishListingIds);
			Map<Long,String> mapAttributeSelect = Maps.newHashMap();
			boolean bool = true;
			if(StringUtils.isNotBlank(vo.getLanguage())){
				bool = false;
			}
			for(AliexpressPublishListingSkuPropertyModel model:listSkuPropertyModel){
				String selectName = mapAttributeSelect.get(model.getProductId());
				if(selectName!=null){
					mapAttributeSelect.put(model.getProductId(),selectName+"/"+(bool?model.getSelectName():model.getSelectNameEn()));
				}else {
					mapAttributeSelect.put(model.getProductId(),(bool?model.getSelectName():model.getSelectNameEn()));
				}
			}
			for(AliexpressPublishListingDTO dto:list) {
				for (AliexpressPublishListingProduct product : dto.getListProduct()) {
					product.setColourIdName(mapAttributeSelect.get(product.getId()));
					if(StringUtils.isNotEmpty(dto.getWarehouseCode()) && StringUtils.isNotEmpty(product.getPlSku())) {
						String warehouseId = dto.getWarehouseCode().split(",")[0];
						CommodityStatusVO commodityStatusVO = ebayPublishListingService.getCommodityStatusVOBySku(warehouseId, product.getPlSku(),4,null);
						if(commodityStatusVO!=null){
							product.setShowStatus(commodityStatusVO.getShowStatus());
							product.setAvailableQty(commodityStatusVO.getAvailableQty());
						}
					}

					if(listProductSkus!=null && listProductSkus.size()>0){
						//捆绑商品
						List<AliexpressPublishListingProductSkus> addProductSkus = Lists.newArrayList();
						for(AliexpressPublishListingProductSkus productSkus:listProductSkus){
							if(productSkus.getProductId().equals(product.getId())){
								if(StringUtils.isNotEmpty(dto.getWarehouseCode()) && StringUtils.isNotEmpty(product.getPlSku())) {
									String warehouseId = dto.getWarehouseCode().split(",")[0];
									CommodityStatusVO commodityStatusVO = ebayPublishListingService.getCommodityStatusVOBySku(warehouseId, product.getPlSku(),4,null);
									if(commodityStatusVO!=null){
										productSkus.setShowStatus(commodityStatusVO.getShowStatus());
										productSkus.setAvailableQty(commodityStatusVO.getAvailableQty());
									}
								}
								addProductSkus.add(productSkus);
							}
						}
						product.setListProductSkus(addProductSkus);
						listProductSkus.removeAll(addProductSkus);
					}
				}
			}
		}

		for(AliexpressPublishListingDTO dto:list){
			if(dto.getListProduct()!=null && dto.getListProduct().size()>0){
//				AliexpressPublishListingProduct product = dto.getListProduct().get(0);
//				dto.setPlSku(product.getPlSku());
//				dto.setPlatformSku(product.getPlatformSku());
//				dto.setInventory(product.getInventory());
//				dto.setRetailPrice(product.getRetailPrice());
				Integer inventory=0;
				BigDecimal startPrice = null;
				BigDecimal endPrice = null;
				for(AliexpressPublishListingProduct product:dto.getListProduct()) {
					if (product.getRetailPrice() != null) {
						if (startPrice == null || startPrice.doubleValue() > product.getRetailPrice().doubleValue()) {
							startPrice = product.getRetailPrice();
						}
						if (endPrice == null || startPrice.doubleValue() < product.getRetailPrice().doubleValue()) {
							endPrice = product.getRetailPrice();
						}
					}
					if(product.getInventory()!=null){
						inventory = inventory + product.getInventory();
					}
				}
				if (startPrice != null && endPrice != null) {
					if (startPrice.doubleValue() == endPrice.doubleValue()) {
						dto.setRetailPrice(startPrice.toString());
					} else {
						dto.setRetailPrice(startPrice.toString() + "~" + endPrice.toString());
					}
				}
				dto.setInventory(inventory);
			}
		}
		PageInfo<AliexpressPublishListingDTO> pageInfo = new PageInfo<>(list);
		Page<AliexpressPublishListingDTO> page = new Page<>(pageInfo);
		return  page;
	}

	@Override
	public List<AliexpressPublishListingExcelDTO> findAllExcel(AliexpressPublishListingSearchVO vo) {
		List<AliexpressPublishListingExcelDTO> listAll = aliexpressPublishListingMapper.findAllExcel(vo);
		//中英文
		boolean bool= StringUtils.isNotEmpty(vo.getLanguage())?true:false;
		List<Long> ids = Lists.newArrayList();
		if(listAll!=null){
			listAll.forEach(dto->{
				ids.add(dto.getId());
			});
		}
		List<AliexpressCategoryAttribute> listaliexpressCategoryAttribute = aliexpressCategoryAttributeMapper.page(null);
		List<AliexpressCategoryAttributeSelect> listaliexpressCategoryAttributeSelect = aliexpressCategoryAttributeSelectMapper.page(null);


		if(ids!=null) {
			//分类属性名称
			Map<Long,String> categoryAttributeMap = Maps.newHashMap();
			//分类属性值名称
			Map<Long,String> categoryAttributeSelectMap = Maps.newHashMap();
			listaliexpressCategoryAttribute.forEach(ca->{
				if(bool){
					categoryAttributeMap.put(ca.getAttributeId(),ca.getAttributeNameEn());
				}else{
					categoryAttributeMap.put(ca.getAttributeId(),ca.getAttributeName());
				}

			});
			listaliexpressCategoryAttributeSelect.forEach(cas->{
				if(bool){
					categoryAttributeSelectMap.put(cas.getSelectId(),cas.getSelectNameEn());
				}else{
					categoryAttributeSelectMap.put(cas.getSelectId(),cas.getSelectName());
				}
			});
			//查询到已有的数据
			List<AliexpressPublishListingProduct> listProductSum = null;
			List<AliexpressPublishListingAttribute> listAttributeSum = null;

			if (ids.size() > 1000) {
				//截取数据 每次查询1000个id
				int count = ids.size()/1000;
				//循环开始
				int i =0;
				//初始化数据
				listProductSum = Lists.newArrayList();
				listAttributeSum = Lists.newArrayList();
				while (i<count){
					//从多少开始
					int start = 0+(i*1000);
					//到多少结束
					int end = 1000+(i*1000);
					//截取id
					List<Long> subIds = ids.subList(start,end);
					List<AliexpressPublishListingProduct> listProduct = aliexpressPublishListingProductMapper.getPublishListingProductByListingIds(subIds);
					List<AliexpressPublishListingAttribute> listAttribute = aliexpressPublishListingAttributeMapper.getPublishListingAttributeByPublishListingIds(subIds);
					//判断是否有数据
					if(listProduct!=null && listProduct.size()>0) {
						listProductSum.addAll(listProduct);
					}
					if(listAttribute!=null && listAttribute.size()>0) {
						listAttributeSum.addAll(listAttribute);
					}
					i++;
				}
				//从多少开始
				int start = i*1000;
				//到多少结束
				int end = ids.size();
				//判断list的结束值是否还有数据需要查询
				if(end>start){
					List<Long> subIds = ids.subList(start,end);

					List<AliexpressPublishListingProduct> listProduct = aliexpressPublishListingProductMapper.getPublishListingProductByListingIds(subIds);
					List<AliexpressPublishListingAttribute> listAttribute = aliexpressPublishListingAttributeMapper.getPublishListingAttributeByPublishListingIds(subIds);
					//判断是否有数据
					if(listProduct!=null && listProduct.size()>0) {
						listProductSum.addAll(listProduct);
					}
					if(listAttribute!=null && listAttribute.size()>0) {
						listAttributeSum.addAll(listAttribute);
					}
				}

			} else if(ids.size()>0){
				listProductSum =  aliexpressPublishListingProductMapper.getPublishListingProductByListingIds(ids);
				listAttributeSum = aliexpressPublishListingAttributeMapper.getPublishListingAttributeByPublishListingIds(ids);
			}

			//数据组装
			for (AliexpressPublishListingExcelDTO dto:listAll) {
				dto.setPublishStatusName(AliexpressEnum.AliexpressStatusEnum.getValueByCode(dto.getPublishStatus(),bool));
				if(dto.getUpdateStatus()!=null) {
					dto.setUpdateStatusName(AliexpressEnum.AliexpressUpdatestatusEnum.getValueByCode(dto.getUpdateStatus(), bool));
				}
				if(dto.getCategoryId()!=null && dto.getCategoryId()>0) {
					dto.setCategoryName(aliexpressBaseService.getCategoryName(dto.getCategoryId(), bool));
				}
				if(dto.getProductDetails()!=null &&dto.getProductDetails().length()>32767 ) {
                    dto.setProductDetails(dto.getProductDetails().substring(0,32767));
                }
				//商品属性
				//移除属性
				List<AliexpressPublishListingAttribute> removeAttribute = Lists.newArrayList();
				//
				StringBuffer strCommodityProperty = new StringBuffer("");
				for(AliexpressPublishListingAttribute attribute:listAttributeSum){
					if(dto.getId().equals(attribute.getPublishListingId())){
						removeAttribute.add(attribute);
						String selectionMode = attribute.getSelectionMode();
						if(attribute.getValueType()!=null){
							if(attribute.getValueType()==1 ){
								strCommodityProperty.append(categoryAttributeMap.get(attribute.getCategoryAttributeId())+":");
								if(selectionMode.indexOf("[")==0){
									selectionMode = selectionMode.replace("[","");
									selectionMode = selectionMode.replace("]","");
									String[] strArray = selectionMode.split(",");
									for(String str:strArray){
										if(ValidatorUtil.isMath(str)) {
											strCommodityProperty.append(categoryAttributeSelectMap.get(Long.valueOf(str)));
										}
									}
								}else {
									if(ValidatorUtil.isMath(selectionMode)) {
										strCommodityProperty.append(categoryAttributeSelectMap.get(Long.valueOf(selectionMode)));
									}
								}

							}else if(attribute.getValueType()==2){
								strCommodityProperty.append(categoryAttributeMap.get(attribute.getCategoryAttributeId())+":");
								strCommodityProperty.append(selectionMode);
							}else if(attribute.getValueType()==3){
								strCommodityProperty.append(attribute.getAttributeCode()+":");
								strCommodityProperty.append(selectionMode);
							}
							strCommodityProperty.append("\n");
						}

					}
				}
				dto.setCommodityProperty(strCommodityProperty.toString());
				listAttributeSum.removeAll(removeAttribute);//移除已经使用的数据
				strCommodityProperty = null;
				//组合填充一个字段数据
				StringBuffer plSku  = new StringBuffer("");
				StringBuffer platformSku = new StringBuffer("");
				StringBuffer inventory = new StringBuffer("");
				StringBuffer skuProperty = new StringBuffer("");
				StringBuffer retailPrice = new StringBuffer("");
				StringBuffer skuImage = new StringBuffer("");

				//移除Product
				List<AliexpressPublishListingProduct> removeProduct = Lists.newArrayList();
				for(AliexpressPublishListingProduct product : listProductSum){
					if(dto.getId().equals(product.getPublishListingId())){
						removeProduct.add(product);
						plSku.append(product.getPlSku());
						platformSku.append(product.getPlatformSku());
						inventory.append(product.getInventory());
						//14:691;5:100014065
						if(StringUtils.isNotEmpty(product.getSmtSkuId())) {
							String[] smtSkuId = product.getSmtSkuId().split(";");
							boolean bsku = true;
							for(String skuId:smtSkuId){
								String[] skuselect = skuId.split(":");
								if(skuselect.length==2) {
									String skuselectId = skuselect[1];
									if (bsku) {
										skuProperty.append(categoryAttributeSelectMap.get(Long.valueOf(skuselectId)));
										bsku = false;
									} else {
										skuProperty.append("/" + categoryAttributeSelectMap.get(Long.valueOf(skuselectId)));
										bsku = false;
									}
								}
							}
						}
						retailPrice.append(product.getRetailPrice());
						skuImage.append(product.getProductImage());

						plSku.append("\n");
						platformSku.append("\n");
						inventory.append("\n");
						skuProperty.append("\n");
						retailPrice.append("\n");
						skuImage.append("\n");
					}
				}
				listProductSum.removeAll(removeProduct);

				dto.setPlSku(plSku.toString());
				dto.setPlatformSku(platformSku.toString());
				dto.setInventory(inventory.toString());
				dto.setSkuProperty(skuProperty.toString());
				dto.setRetailPrice(retailPrice.toString());
				dto.setSkuImage(skuImage.toString());

				plSku =null;
				platformSku=null;
				inventory=null;
				skuProperty = null;
				retailPrice = null;
				skuImage = null;
				String reduceStrategy = "";
				if("place_order_withhold".equals(dto.getReduceStrategy())){
					reduceStrategy = "下单减库存(place order withhold)";
				}else if("payment_success_deduct".equals(dto.getReduceStrategy())){
					reduceStrategy = "付款减库存(payment success deduct)";
				}
				dto.setReduceStrategy(reduceStrategy);
				if(dto.getUnit()!=null) {
					dto.setUnitName(AliexpressProductUnitEnum.getValueByCode(dto.getUnit()));
				}
				dto.setPackageVolume(dto.getPackageLength()+"*"+dto.getPackageWidth()+"*"+dto.getPackageHeight());
			}

		}



		return listAll;
	}


	@Override
	public Page<AliexpressPublishListingMobile> getAllMobile(String sellerId,List<Integer> empowerIds) {
		List<AliexpressPublishListingMobile> list = aliexpressPublishListingMapper.getAllMobile(sellerId,empowerIds);
		PageInfo<AmazonPublishListingMobile> pageInfo = new PageInfo(list);
		return new Page(pageInfo);
	}

    @Override
    public AliexpressPublishListingMobile getAliexpressPublishListingMobileById(Long id,String headeri18n) {
		AliexpressPublishListingMobile aliexpressPublishListingMobile = aliexpressPublishListingMapper.getAliexpressPublishListingMobileById(id);
		List<AliexpressPublishListingProduct> listProduct = aliexpressPublishListingProductMapper.getPublishListingProductByListingId(id);
		List<AliexpressPublishListingProductMobile> listProductMobile = Lists.newArrayList();

		List<Long> publishListingIds = Lists.newArrayList();
		publishListingIds.add(id);
		List<AliexpressPublishListingSkuPropertyModel> listSkuPropertyModel = aliexpressPublishListingSkuPropertyMapper.getPublishListingSkuPropertyByListingIds(publishListingIds);

		boolean bool= true;
		if (org.apache.commons.lang3.StringUtils.isNotBlank(headeri18n)) {
			bool = false;
		}


		for(AliexpressPublishListingProduct product:listProduct){
			AliexpressPublishListingProductMobile productMobile = new AliexpressPublishListingProductMobile();
			StringBuffer str = new StringBuffer();
			if(listSkuPropertyModel!=null){
				boolean boolModel = true;
				for(AliexpressPublishListingSkuPropertyModel model:listSkuPropertyModel){
					if(model.getProductId().equals(product.getId())){
						if(boolModel){
							str.append(bool?model.getSelectName():model.getSelectNameEn());
							boolModel = false;
						}else{
							str.append("/"+(bool?model.getSelectName():model.getSelectNameEn()));
						}
					}
				}
			}
			productMobile.setColourName(str.toString());
			productMobile.setId(product.getId());
			productMobile.setInventory(product.getInventory());
			productMobile.setRetailPrice(product.getRetailPrice());
			productMobile.setPlatformSku(product.getPlatformSku());
			productMobile.setPlSku(product.getPlSku());
			productMobile.setProductImage(product.getProductImage());
			listProductMobile.add(productMobile);
		}
		aliexpressPublishListingMobile.setListProductMobile(listProductMobile);
		return aliexpressPublishListingMobile;
    }

    @Override
	public AliexpressPublishListing insertPublishListing(AliexpressPublishRequest publishRequest, String loginUserName, Integer status, Empower empower) {
		if(status==AliexpressEnum.AliexpressStatusEnum.DRAFT.getCode()){
			if(publishRequest.getCategoryId4()==null){
				publishRequest.setCategoryId4(0L);
			}
			if(publishRequest.getCategoryId3()==null){
				publishRequest.setCategoryId3(0L);
			}
			if(publishRequest.getCategoryId2()==null){
				publishRequest.setCategoryId2(0L);
			}
			publishRequest.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.DRAFT.getCode());
		}else{
			this.check(publishRequest,false,null);
		}

		AliexpressPublishListing listing = new AliexpressPublishListing();
		listing.setPlatformListing(1);
		BeanUtils.copyProperties(publishRequest, listing);
		//用户
		UserDTO userDTO = getUserInfo.getUserDTO();
		if(userDTO.getManage()){
			listing.setSellerId(userDTO.getUserId().toString());
			listing.setCreateId(userDTO.getUserId().longValue());
			listing.setCreateName(userDTO.getLoginName());
		}else{
			listing.setSellerId(userDTO.getTopUserId().toString());
			listing.setCreateId(userDTO.getTopUserId().longValue());
			listing.setCreateName(userDTO.getTopUserLoginName());
		}

		Date date = new Date();
		listing.setCreateTime(date);
		listing.setUpdateTime(date);
		listing.setPlAccount(loginUserName);
		listing.setPublishStatus(status);
		listing.setVersion(1);
		int numInsertInto = aliexpressPublishListingMapper.insertSelective(listing);

		if(numInsertInto==0){//系统异常
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, ResponseCodeEnum.RETURN_CODE_100500.getMsg());
		}
		//详情部分
		AliexpressPublishListingDetail detail = new AliexpressPublishListingDetail();
		BeanUtils.copyProperties(publishRequest, detail);
		detail.setCreateTime(date);
		detail.setUpdateTime(date);
		detail.setPublishListingId(listing.getId());
		detail.setVersion(1);
		detail.setId(null);
		int numInsertDetail = aliexpressPublishListingDetailMapper.insertSelective(detail);
		if(numInsertDetail==0){//系统异常
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, ResponseCodeEnum.RETURN_CODE_100500.getMsg());
		}
		//平台商品sku映射
		for (AliexpressPublishListingProduct product:publishRequest.getListProduct()) {
			product.setPublishListingId(listing.getId());
			product.setCreateTime(date);
			product.setId(null);
			aliexpressPublishListingProductMapper.insertSelective(product);
			//int sort =1;
			for(AliexpressPublishListingSkuProperty skuProperty:product.getListSkuProperty()){
				skuProperty.setPublishListingId(listing.getId());
				skuProperty.setProductId(product.getId());
				skuProperty.setId(null);
				//skuProperty.setSort(sort);
				//sort++;
				aliexpressPublishListingSkuPropertyMapper.insertSelective(skuProperty);
			}
			for(AliexpressPublishListingProductSkus productSkus:product.getListProductSkus()){
				productSkus.setId(null);
				productSkus.setPublishListingId(listing.getId());
				productSkus.setProductId(product.getId());
				aliexpressPublishListingProductSkusMapper.insertSelective(productSkus);
			}

		}

		//属性设置
		for (AliexpressPublishListingAttribute attribute:publishRequest.getListAttribute()) {
			attribute.setPublishListingId(listing.getId());
			attribute.setCreateTime(date);
			attribute.setId(null);
			aliexpressPublishListingAttributeMapper.insertSelective(attribute);
		}
		//区域价格
		for (AliexpressProductCountryPrice productCountryPrice:publishRequest.getListProductCountryPrice()) {
			productCountryPrice.setListingId(listing.getId());
			productCountryPrice.setCreateTime(date);
			productCountryPrice.setId(null);
			aliexpressProductCountryPriceMapper.insertSelective(productCountryPrice);
		}

		//端使用方便数据回写编辑
//		AliexpressPublishListingUIExt uiExt = new AliexpressPublishListingUIExt();
//		uiExt.setExt(publishRequest.getExt());
//		uiExt.setCreationTime(date);
//		uiExt.setListingId(listing.getId());
//		aliexpressPublishListingUIExtMapper.insertSelective(uiExt);
		//日志
		String operationType = status==1? AliexpressOperationEnum.DRAFT.getCode():AliexpressOperationEnum.PUBLISH.getCode();
		this.insertAliexpressOperationLog(listing.getId(),loginUserName,operationType,publishRequest.getTitle(),Long.valueOf(userDTO.getUserId()));

		return listing;
	}


	@Override
	public AliexpressPublishListing updatePublishListing(AliexpressPublishRequest publishRequest, String loginUserName, Integer status, Empower empower) {
		AliexpressPublishListing aliexpressPublishListing = aliexpressPublishListingMapper.selectByPrimaryKey(publishRequest.getId());
		if(aliexpressPublishListing==null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "编辑对像id不能为空");
		}
		if(status==AliexpressEnum.AliexpressStatusEnum.DRAFT.getCode()){
			if(publishRequest.getCategoryId4()==null){
				publishRequest.setCategoryId4(0L);
			}
			if(publishRequest.getCategoryId3()==null){
				publishRequest.setCategoryId3(0L);
			}
			if(publishRequest.getCategoryId2()==null){
				publishRequest.setCategoryId2(0L);
			}
			publishRequest.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.DRAFT.getCode());
		}else{
			this.check(publishRequest,true,aliexpressPublishListing);
		}
		//状态是上线和下线状态 编辑时候状态不变
//		if(aliexpressPublishListing.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.SALE.getCode()
//				|| aliexpressPublishListing.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.END.getCode()){
//			status = aliexpressPublishListing.getPublishStatus();
//			publishRequest.setItemId(aliexpressPublishListing.getItemId());
//			this.updateProduct(publishRequest,empower);
//		}


		AliexpressPublishListing listing = new AliexpressPublishListing();
		BeanUtils.copyProperties(publishRequest, listing);
		listing.setPublishProductImage("1");//编辑的时候把上传速卖通图片地址改为1作为默认值
		Date date = new Date();
		listing.setUpdateTime(date);
		listing.setPlAccount(loginUserName);
		listing.setVersion(aliexpressPublishListing.getVersion());
		listing.setId(aliexpressPublishListing.getId());
        //状态是上线和下线状态 编辑时候状态不变
        if(aliexpressPublishListing.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.SALE.getCode()
                || aliexpressPublishListing.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.END.getCode()){
            listing.setUpdateStatus(1);//更新中
			status = aliexpressPublishListing.getPublishStatus();
        }
		listing.setPublishStatus(status);

		int numInsertInto = aliexpressPublishListingMapper.updateByPrimaryKeySelective(listing);

		if(numInsertInto==0){//系统异常
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, ResponseCodeEnum.RETURN_CODE_100500.getMsg());
		}
		//详情部分
		AliexpressPublishListingDetail detail = new AliexpressPublishListingDetail();
		BeanUtils.copyProperties(publishRequest, detail);
		detail.setUpdateTime(date);
		detail.setPublishListingId(listing.getId());

		aliexpressPublishListingDetailMapper.updateByPrimaryKeySelective(detail);

		//平台商品sku映射
		aliexpressPublishListingProductMapper.deleteByListingId(listing.getId());
		aliexpressPublishListingSkuPropertyMapper.deleteByListingId(listing.getId());
		aliexpressPublishListingProductSkusMapper.delectProductSkus(listing.getId());
		for (AliexpressPublishListingProduct product:publishRequest.getListProduct()) {
			product.setPublishListingId(listing.getId());
			product.setId(null);
			aliexpressPublishListingProductMapper.insertSelective(product);
			//int sort =1;
			for(AliexpressPublishListingSkuProperty skuProperty:product.getListSkuProperty()){
				skuProperty.setId(null);
				skuProperty.setPublishListingId(listing.getId());
				skuProperty.setProductId(product.getId());
				//skuProperty.setSort(sort);
				//sort++;
				aliexpressPublishListingSkuPropertyMapper.insertSelective(skuProperty);
			}
			for(AliexpressPublishListingProductSkus productSkus:product.getListProductSkus()){
				productSkus.setId(null);
				productSkus.setPublishListingId(listing.getId());
				productSkus.setProductId(product.getId());
				aliexpressPublishListingProductSkusMapper.insertSelective(productSkus);
			}
		}

		//属性设置
		aliexpressPublishListingAttributeMapper.deleteByListingId(listing.getId());
		for (AliexpressPublishListingAttribute attribute:publishRequest.getListAttribute()) {
			attribute.setPublishListingId(listing.getId());
			attribute.setId(null);
			aliexpressPublishListingAttributeMapper.insertSelective(attribute);
		}
		//区域价格
		aliexpressProductCountryPriceMapper.deleteByListingId(listing.getId());
		for (AliexpressProductCountryPrice productCountryPrice:publishRequest.getListProductCountryPrice()) {
			productCountryPrice.setId(null);
			productCountryPrice.setListingId(listing.getId());
			aliexpressProductCountryPriceMapper.insertSelective(productCountryPrice);
		}

//		//端使用方便数据回写编辑
//		AliexpressPublishListingUIExt uiExt = new AliexpressPublishListingUIExt();
//		uiExt.setExt(publishRequest.getExt());
//		uiExt.setListingId(publishRequest.getId());
//		aliexpressPublishListingUIExtMapper.updateByPrimaryKeySelective(uiExt);
		//日志
		String operationType = status==1? AliexpressOperationEnum.EDIT.getCode():AliexpressOperationEnum.PUBLISH.getCode();
		UserDTO userDTO = getUserInfo.getUserDTO();
		this.insertAliexpressOperationLog(listing.getId(),loginUserName,operationType,publishRequest.getTitle(),Long.valueOf(userDTO.getUserId()));

		return listing;
	}

	@Override
	public AliexpressPublishModel getPublishModelById(Long id,int type) {
		//刊登主
		AliexpressPublishListing listing= aliexpressPublishListingMapper.selectByPrimaryKey(id);
		if(listing==null){
			return null;
		}
		AliexpressPublishModel model = new AliexpressPublishModel();
		BeanUtils.copyProperties(listing, model);

		if((model.getCategoryId2()==null || model.getCategoryId2()==0) && model.getCategoryId1()!=null){
			List<AliexpressCategory> listAliexpressCategory = Lists.newArrayList();
			AliexpressCategory aliexpressCategory = aliexpressCategoryMapper.getCategoryByCategoryId(model.getCategoryId1());
			listAliexpressCategory.add(aliexpressCategory);
			while (aliexpressCategory.getCategoryParentId()>0){
				aliexpressCategory = aliexpressCategoryMapper.getCategoryByCategoryId(aliexpressCategory.getCategoryParentId());
				listAliexpressCategory.add(aliexpressCategory);
			}
			if(listAliexpressCategory.size()==4){
				model.setCategoryId1(listAliexpressCategory.get(3).getCategoryId());
				model.setCategoryId2(listAliexpressCategory.get(2).getCategoryId());
				model.setCategoryId3(listAliexpressCategory.get(1).getCategoryId());
				model.setCategoryId4(listAliexpressCategory.get(0).getCategoryId());
			}else if(listAliexpressCategory.size()==3){
				model.setCategoryId1(listAliexpressCategory.get(2).getCategoryId());
				model.setCategoryId2(listAliexpressCategory.get(1).getCategoryId());
				model.setCategoryId3(listAliexpressCategory.get(0).getCategoryId());
			}else if(listAliexpressCategory.size()==2){
				model.setCategoryId1(listAliexpressCategory.get(1).getCategoryId());
				model.setCategoryId2(listAliexpressCategory.get(0).getCategoryId());
			}
		}else {
			if(model.getCategoryId4()!=null && model.getCategoryId4()==0L){
				model.setCategoryId4(null);
			}
			if(model.getCategoryId3()!=null && model.getCategoryId3()==0L){
				model.setCategoryId3(null);
			}
			if(model.getCategoryId2()!=null && model.getCategoryId2()==0L){
				model.setCategoryId2(null);
			}
		}

		//刊登详情
		AliexpressPublishListingDetail detail = aliexpressPublishListingDetailMapper.getPublishListingDetailByPublishListingId(listing.getId());
		BeanUtils.copyProperties(detail, model);
		model.setId(listing.getId());
		List<AliexpressPublishListingProduct> listProduct = aliexpressPublishListingProductMapper.getPublishListingProductByListingId(listing.getId());
        List<AliexpressPublishListingSkuProperty> listSkuProperty = aliexpressPublishListingSkuPropertyMapper.getPublishListingSkuPropertyByListingId(listing.getId(),null);
		List<AliexpressPublishListingProductSkus> listProductSkus = aliexpressPublishListingProductSkusMapper.getProductSkusByPublishListingId(listing.getId());
        for(AliexpressPublishListingProduct product:listProduct){
            for(AliexpressPublishListingSkuProperty skuProperty:listSkuProperty){
                if(skuProperty.getProductId()!=null && skuProperty.getProductId().equals(product.getId())){
                    product.getListSkuProperty().add(skuProperty);
                }
            }
            if(listProductSkus!=null && listProductSkus.size()>0){
				for(AliexpressPublishListingProductSkus productSkus:listProductSkus){
					if(productSkus.getProductId()!=null && productSkus.getProductId().equals(product.getId())){
						product.getListProductSkus().add(productSkus);
					}
				}
			}
        }
		List<AliexpressPublishListingAttribute> listAttribute = aliexpressPublishListingAttributeMapper.getPublishListingAttributeByPublishListingId(listing.getId());
		List<AliexpressProductCountryPrice> listProductCountryPrice = aliexpressProductCountryPriceMapper.getProductCountryPriceByPublishListingId(listing.getId());
		model.setListAttribute(listAttribute);
		model.setListProduct(listProduct);
		model.setListProductCountryPrice(listProductCountryPrice);
//		if(type==2){
//			AliexpressPublishListingUIExt uiext = aliexpressPublishListingUIExtMapper.getExtByListingId(listing.getId());
//			if(uiext!=null) {
//				model.setExt(uiext.getExt());
//			}
//		}

		return model;
	}

	@Override
	public AliexpressPublishListing getAliexpressPublishListingById(Long id) {
		return aliexpressPublishListingMapper.selectByPrimaryKey(id);
	}

	@Override
	public String deleteAliexpressPublishListing(Long id) {
		AliexpressPublishListing listing = new AliexpressPublishListing();
		listing.setId(id);
		listing.setDeleteStatus(false);
		aliexpressPublishListingMapper.updateByPrimaryKeySelective(listing);
		return "true";
	}

	@Override
	public Long insertcopyAliexpressPublish(Long id,int type,Long empowerId,Integer publishStatus) {
		//刊登主
		AliexpressPublishListing listing= aliexpressPublishListingMapper.selectByPrimaryKey(id);
		if(listing==null){
			return null;
		}
		listing.setId(null);
		Date date= new Date();
		if(type==2){
			listing.setPublishStatus(publishStatus);
			listing.setPublishTime(date);
			listing.setEmpowerId(empowerId);
			Empower empower = empowerMapper.getEmpowerById(empowerId.intValue());
			listing.setPublishAccount(empower.getAccount());
		}else {
			listing.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.DRAFT.getCode());
			listing.setPublishTime(null);
		}
		listing.setVersion(0);
		listing.setCreateTime(date);
		listing.setUpdateTime(date);
        listing.setSuccessTime(null);

        listing.setOnlineTime(null);
        listing.setEndTimes(null);
        listing.setItemId(null);
		listing.setUpdateStatus(null);
		listing.setPlatformListing(1);
		//用户
		UserDTO userDTO = getUserInfo.getUserDTO();
		listing.setCreateId(userDTO.getUserId().longValue());
		listing.setCreateName(userDTO.getLoginName());
		if(userDTO.getManage()){
			listing.setSellerId(userDTO.getUserId().toString());
		}else{
			listing.setSellerId(userDTO.getTopUserId().toString());
		}
		aliexpressPublishListingMapper.insertSelective(listing);
		//刊登详情
		AliexpressPublishListingDetail detail = aliexpressPublishListingDetailMapper.getPublishListingDetailByPublishListingId(id);
		detail.setId(null);
		detail.setVersion(0);
		detail.setPublishListingId(listing.getId());
		detail.setCreateTime(date);
		detail.setUpdateTime(date);
		if(type==2){//批量刊登默认模板值
			detail.setFreightTemplateId(1000L);
			detail.setPromiseTemplateId(0L);
			detail.setGroupId(null);
		}
		aliexpressPublishListingDetailMapper.insertSelective(detail);

		List<AliexpressPublishListingProduct> listProduct = aliexpressPublishListingProductMapper.getPublishListingProductByListingId(id);
		List<AliexpressPublishListingAttribute> listAttribute = aliexpressPublishListingAttributeMapper.getPublishListingAttributeByPublishListingId(id);
		List<AliexpressProductCountryPrice> listProductCountryPrice = aliexpressProductCountryPriceMapper.getProductCountryPriceByPublishListingId(id);

		//平台商品sku映射
		List<AliexpressPublishListingProductSkus> listingProductSkus =  aliexpressPublishListingProductSkusMapper.getProductSkusByPublishListingId(id);
		for (AliexpressPublishListingProduct product:listProduct) {
			Long productId = product.getId();
			List<AliexpressPublishListingSkuProperty> listSkuProperty= aliexpressPublishListingSkuPropertyMapper.getPublishListingSkuPropertyByListingId(id,product.getId());
			product.setId(null);
			product.setPublishListingId(listing.getId());
			product.setCreateTime(date);
            product.setVersion(0);
            if(product.getPlSku()!=null) {
            	String plSku=product.getPlSku();
				String platformSku = GeneratePlateformSku.getAliexpressPlateformSku(plSku);
				List<AliexpressPublishListingProduct> list = this.getProductByPlatformSku(platformSku,listing.getId());
				while (list!=null && list.size()>0){
					platformSku = GeneratePlateformSku.getAliexpressPlateformSku(plSku);
					list = this.getProductByPlatformSku(platformSku,listing.getId());
				}
				product.setPlatformSku(platformSku);
			}else{
				product.setPlatformSku(null);
			}

			aliexpressPublishListingProductMapper.insertSelective(product);

			for(AliexpressPublishListingSkuProperty skuProperty:listSkuProperty){
                skuProperty.setId(null);
                skuProperty.setPublishListingId(listing.getId());
                skuProperty.setProductId(product.getId());
                aliexpressPublishListingSkuPropertyMapper.insertSelective(skuProperty);
			}
			if(listingProductSkus.size()>0) {
				for (AliexpressPublishListingProductSkus psku:listingProductSkus) {
					if(productId.equals(psku.getProductId())) {
						psku.setId(null);
						psku.setPublishListingId(listing.getId());
						psku.setProductId(product.getId());
						aliexpressPublishListingProductSkusMapper.insertSelective(psku);
					}
				}
			}

		}

		//属性设置
		for (AliexpressPublishListingAttribute attribute:listAttribute) {
			attribute.setCreateTime(date);
			attribute.setId(null);
			attribute.setPublishListingId(listing.getId());
            attribute.setVersion(0);
			if(type==2){
				if(attribute.getCategoryAttributeId()!=null && 2L==attribute.getCategoryAttributeId()){
					attribute.setSelectionMode("201512802");
				}
			}
			aliexpressPublishListingAttributeMapper.insertSelective(attribute);
		}
		//区域价格
		for (AliexpressProductCountryPrice productCountryPrice:listProductCountryPrice) {
			productCountryPrice.setListingId(listing.getId());
			productCountryPrice.setId(null);
			productCountryPrice.setCreateTime(date);
			aliexpressProductCountryPriceMapper.insertSelective(productCountryPrice);
		}
		return listing.getId();
	}

	@Override
	public Integer updateByPrimaryKeySelective(AliexpressPublishListing aliexpressPublishListing) {
		return aliexpressPublishListingMapper.updateByPrimaryKeySelective(aliexpressPublishListing);
	}
	@Override
	public Integer updateByPublishListingDetail(AliexpressPublishListingDetail detail) {
		return aliexpressPublishListingDetailMapper.updateByPrimaryKeySelective(detail);
	}



	@Override
	public Integer updateByAliexpressPublishListingProduct(AliexpressPublishListingProduct aliexpressPublishListingProduct) {
		return aliexpressPublishListingProductMapper.updateByPrimaryKeySelective(aliexpressPublishListingProduct);
	}

	@Override
	public List<AliexpressOperationLog> getAliexpressOperationLogBylistingId(Long listingId) {
		return aliexpressOperationLogMapper.getAliexpressOperationLogBylistingId(listingId);
	}

    @Override
    public List<AliexpressPublishListingError> getAliexpressPublishListingErrorBylistingId(Long listingId) {
		AliexpressPublishListingError error = new AliexpressPublishListingError();
		error.setListingId(listingId);
		return aliexpressPublishListingErrorMapper.page(error);
    }


    @Override
	public void updateAliexpressPublishListingSuccee(int type,Long id,String publishMessage) {
		//刊登类型
		Map<String,Object> map =null;
		if(type==3){
			map = JsonAnalysis.getEditaeproductMsg(publishMessage);
		}else{
			map = JsonAnalysis.getJosnMsg(publishMessage);
		}

		if(type==1 || type==3){

			AliexpressPublishListing aliexpressPublishListing = aliexpressPublishListingMapper.selectByPrimaryKey(id);
			AliexpressPublishListing aliexpressPublishListingNew = new AliexpressPublishListing();
			aliexpressPublishListingNew.setUpdateTime(new Date());
			aliexpressPublishListingNew.setId(id);
			aliexpressPublishListingNew.setVersion(aliexpressPublishListing.getVersion());
			aliexpressPublishListingNew.setPublishTime(new Date());
			if(map!=null){
				String success = map.get("success").toString();
				if("error_response".equals(success)){
					//刊登异常
					aliexpressPublishListingNew.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.PUBLISH_FAILED.getCode());
					String code = map.get("code").toString();
					String msg = map.get("msg").toString();
					String subMsg = msg+" "+map.get("subMsg").toString();
					this.insertAliexpressPublishListingError(id,code,subMsg,"Error");
				}else if("true".equals(success)){
					//审核中
					aliexpressPublishListingNew.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.AUDIT.getCode());
					String productId = map.get("productId").toString();
					aliexpressPublishListingNew.setItemId(Long.valueOf(productId));
					//映射到sku映射表中
					List<AliexpressPublishListingProduct> listProduct = aliexpressPublishListingProductMapper.getPublishListingProductByListingId(id);
                    List<AliexpressPublishListingProductSkus> listSkus = aliexpressPublishListingProductSkusMapper.getProductSkusByPublishListingId(id);
					List<SellerSkuMap>  listSellerSkuMap = new ArrayList<SellerSkuMap>();
					for(AliexpressPublishListingProduct product:listProduct) {
						if(product.getPlSku()==null){
							continue;
						}
						SellerSkuMap sellerSkuMap = new SellerSkuMap();
						sellerSkuMap.setPlatformSku(product.getPlatformSku());
						sellerSkuMap.setPlatform(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform());
						sellerSkuMap.setAuthorizationId(String.valueOf(aliexpressPublishListing.getEmpowerId()));
						listSellerSkuMap.add(sellerSkuMap);
						String skuGroup = product.getPlSku()+":1";
						if(listSkus!=null && listSkus.size()>0){
						    boolean bool = true;
                            for(AliexpressPublishListingProductSkus productSkus:listSkus){
                                if(product.getId().equals(productSkus.getProductId())){
                                    if(bool){
                                        skuGroup = productSkus.getPlSku()+":"+productSkus.getPlSkuNumber();
                                        bool = false;
                                    }else{
                                        skuGroup = "|"+productSkus.getPlSku()+":"+productSkus.getPlSkuNumber();
                                    }
                                }
                            }
                        }
                        sellerSkuMap.setSkuGroup(skuGroup);

					}
					if(listSellerSkuMap.size()>0) {
						String addSkuMaps = remoteCommodityService.addSkuMap(listSellerSkuMap);
						logger.info("remoteCommodityService -->{}", addSkuMaps);
					}
					List<CodeAndValueVo> data = new ArrayList<CodeAndValueVo>();
					listSellerSkuMap.forEach(sellersku->{
						CodeAndValueVo codeAndValueVo = new CodeAndValueVo();
						codeAndValueVo.setCode(sellersku.getPlSku());
						codeAndValueVo.setValue("1");
						data.add(codeAndValueVo);
					});
					String skuPublishNum = remoteCommodityService.updateSkuPublishNum(data);
					logger.info("updateSkuPublishNum -->{}",skuPublishNum);
					Long categoryId = 0L;
					if(aliexpressPublishListing.getCategoryId4()!=null && aliexpressPublishListing.getCategoryId4()>0){
						categoryId = aliexpressPublishListing.getCategoryId4();
					}else{
						if(aliexpressPublishListing.getCategoryId3()!=null && aliexpressPublishListing.getCategoryId3()>0){
							categoryId = aliexpressPublishListing.getCategoryId3();
						}else{
							if(aliexpressPublishListing.getCategoryId2()!=null && aliexpressPublishListing.getCategoryId2()>0){
								categoryId = aliexpressPublishListing.getCategoryId2();
							}else{
								categoryId = aliexpressPublishListing.getCategoryId1();
							}
						}
					}
					String categoryName = aliexpressBaseService.getCategoryName(categoryId,true);
					if(categoryName!=null){
						categoryName = categoryName.replace("#,"," 》");
					}
					String saveOrUpdateSpuCategory = remoteCommodityService.saveOrUpdateSpuCategory(aliexpressPublishListing.getPlSpu(),"Aliexpress",
							null,categoryId,categoryName);
					logger.info("saveOrUpdateSpuCategory -->{}",saveOrUpdateSpuCategory);
				}else if("false".equals(success)){
					//刊登异常返回
					aliexpressPublishListingNew.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.PUBLISH_FAILED.getCode());
					String errorCode = map.get("errorCode").toString();
					String errorMessage = map.get("errorMessage").toString();
					this.insertAliexpressPublishListingError(id,errorCode,errorMessage,"Error");
				}else if("errer".equals(success)){
					aliexpressPublishListingNew.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.PUBLISH_FAILED.getCode());
					this.insertAliexpressPublishListingError(id,"1000","connection timeout","Error");
				}
			}
			//销售的商品和下架的商品状态不变
			if(aliexpressPublishListing.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.SALE.getCode()
					|| aliexpressPublishListing.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.END.getCode()){
				if(AliexpressEnum.AliexpressStatusEnum.PUBLISH_FAILED.getCode()==aliexpressPublishListingNew.getPublishStatus()){
					aliexpressPublishListingNew.setUpdateStatus(3);
				}else{
					aliexpressPublishListingNew.setUpdateStatus(2);
				}
				aliexpressPublishListingNew.setPublishStatus(aliexpressPublishListing.getPublishStatus());

			}
			aliexpressPublishListingMapper.updateByPrimaryKeySelective(aliexpressPublishListingNew);


		}else if(type==2){//审核类型
			AliexpressPublishListing aliexpressPublishListing = aliexpressPublishListingMapper.selectByPrimaryKey(id);
			AliexpressPublishListing aliexpressPublishListingNew = new AliexpressPublishListing();
			aliexpressPublishListingNew.setUpdateTime(new Date());
			aliexpressPublishListingNew.setId(id);
			aliexpressPublishListingNew.setVersion(aliexpressPublishListing.getVersion());

			if(map!=null){
				String success = map.get("success").toString();
				if("error_response".equals(success)){
					//状态查询异常
					String msg = map.get("msg").toString();
					String subMsg = map.get("subMsg").toString();
					this.insertAliexpressPublishListingError(id,msg,subMsg,"Error");
				}else if("true".equals(success)){
					//商品状态。审核通过:approved;审核中:auditing;审核不通过:refuse
					String status = map.get("status").toString();
					if("approved".equals(status)){
						aliexpressPublishListingNew.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.SALE.getCode());
						aliexpressPublishListingErrorMapper.deleteAliexpressPublishListing(id);
					}else if("auditing".equals(status)){
						aliexpressPublishListingNew.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.AUDIT.getCode());
					}else if("refuse".equals(status)){
						aliexpressPublishListingNew.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.AUDIT_FAILED.getCode());
					}
					aliexpressPublishListingNew.setSuccessTime(new Date());
					aliexpressPublishListingMapper.updateByPrimaryKeySelective(aliexpressPublishListingNew);
				}else if("false".equals(success)){
					//异常返回
					String errorCode = map.get("errorCode").toString();
					String errorMessage = map.get("errorMessage").toString();
					this.insertAliexpressPublishListingError(id,errorCode,errorMessage,"Error");
				}else if("errer".equals(success)){
					this.insertAliexpressPublishListingError(id,"1000","connection timeout","Error");
				}else{
					//一旦为null  则是成功
					aliexpressPublishListingNew.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.SALE.getCode());
					aliexpressPublishListingMapper.updateByPrimaryKeySelective(aliexpressPublishListingNew);
				}
			}
		}
	}

    @Override
    public int updateAliexpressPublishListingProduct(AliexpressPublishUpdateRequest request) {
		AliexpressPublishListing aliexpressPublishListing = aliexpressPublishListingMapper.selectByPrimaryKey(request.getId());

		//查询刊登的skuid
		Map<String,Object> skuIdMap=Maps.newHashMap();
		if(aliexpressPublishListing!=null && aliexpressPublishListing.getItemId()!=null){
			//接口查询第三方商品skuid是多少
			Map<String, String> map = Maps.newHashMap();
			map.put("productId",aliexpressPublishListing.getItemId().toString());
			String body=this.postAliexpressApi(5,map,aliexpressPublishListing.getEmpowerId());
			skuIdMap = JsonAnalysis.getfindaeproductbyidJosnMsg(body);
		}
		//零售价开始
		BigDecimal retailPriceStart = null;
		//零售价结束
		BigDecimal retailPriceEnd = null;
		for(AliexpressPublishListingProduct product:request.getListProduct()){
			AliexpressPublishListingProduct newproduct = new AliexpressPublishListingProduct();
			newproduct.setId(product.getId());
			if(request.getType()==1){
				if(product.getRetailPrice()==null ||!(product.getRetailPrice().doubleValue() > 0 && product.getRetailPrice().doubleValue() <= 1000000)){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "零售价取值不在1-1000000之间");
				}

				if(retailPriceStart == null){
					retailPriceStart = product.getRetailPrice();
				}else{
					if(retailPriceStart.doubleValue()>product.getRetailPrice().doubleValue()){
						retailPriceStart = product.getRetailPrice();
					}
				}

				if(retailPriceEnd ==null){
					retailPriceEnd = product.getRetailPrice();
				}else{
					if(retailPriceEnd.doubleValue()<product.getRetailPrice().doubleValue()){
						retailPriceEnd = product.getRetailPrice();
					}
				}
				newproduct.setRetailPrice(product.getRetailPrice());
			}else if(request.getType()==2){
				if(product.getInventory() ==null || !(product.getInventory() > 0 && product.getInventory() <= 999999)){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "库存范围限制为1~999999");
				}
				newproduct.setInventory(product.getInventory());
			}

			if(request.getType()==1 && aliexpressPublishListing.getItemId()!=null){
				AliexpressPublishListingProduct productTemp = aliexpressPublishListingProductMapper.selectByPrimaryKey(product.getId());
				if(productTemp==null){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "刊登商品id不能为空");
				}
				Object skuID = skuIdMap.get(productTemp.getPlatformSku());
				if(skuID!=null){
					Map<String, String> paramsMap = Maps.newHashMap();
					paramsMap.put("productId",aliexpressPublishListing.getItemId().toString());
					paramsMap.put("skuId",skuID.toString());
					paramsMap.put("skuPrice",product.getRetailPrice().toString());
					String body = this.postAliexpressApi(3,paramsMap,aliexpressPublishListing.getEmpowerId());
					Map<String,Object> map = JsonAnalysis.getErrerJosnMsg(body);
					String success = map.get("success").toString();
					if("error_response".equals(success)){
						//修改价格异常
						String code = map.get("code").toString();
						String msg = map.get("msg").toString();
						String subMsg = msg+" "+map.get("subMsg").toString();
						this.insertAliexpressPublishListingError(request.getId(),code,subMsg,"Error");
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, subMsg);
					}else if("errer".equals(success)){
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求参数错误");
					}
				}
			}else if(request.getType()==2 && aliexpressPublishListing.getItemId()!=null){
				AliexpressPublishListingProduct productTemp = aliexpressPublishListingProductMapper.selectByPrimaryKey(product.getId());
				if(productTemp==null){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "刊登商品id不能为空");
				}
				Object skuID = skuIdMap.get(productTemp.getPlatformSku());
				if(skuID!=null) {
					Map<String, String> paramsMap = Maps.newHashMap();
					paramsMap.put("productId", aliexpressPublishListing.getItemId().toString());
					paramsMap.put("skuId", skuID.toString());
					paramsMap.put("ipmSkuStock", product.getInventory().toString());
					String body = this.postAliexpressApi(4, paramsMap, aliexpressPublishListing.getEmpowerId());
					Map<String,Object> map = JsonAnalysis.getErrerJosnMsg(body);
					String success = map.get("success").toString();
					if("error_response".equals(success)){
						//修改库存异常
						String code = map.get("code").toString();
						String msg = map.get("msg").toString();
						String subMsg = msg+" "+map.get("subMsg").toString();
						this.insertAliexpressPublishListingError(request.getId(),code,subMsg,"Error");
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, subMsg);
					}else if("errer".equals(success)){
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求参数错误");
					}
				}
			}

			aliexpressPublishListingProductMapper.updateByPrimaryKeySelective(newproduct);
		}
		if(request.getType()==1){
			AliexpressPublishListingDetail aliexpressPublishListingDetail = aliexpressPublishListingDetailMapper.getPublishListingDetailByPublishListingId(aliexpressPublishListing.getId());
			if(aliexpressPublishListingDetail!=null && !"absolute".equals(aliexpressPublishListingDetail.getRegionalPricing())) {
				List<AliexpressProductCountryPrice> listCountryPrice = aliexpressProductCountryPriceMapper.getProductCountryPriceByPublishListingId(request.getId());
				if (listCountryPrice != null && listCountryPrice.size() == 1) {
					AliexpressProductCountryPrice countryPrice = listCountryPrice.get(0);
					countryPrice.setRetailPriceStart(retailPriceStart);
					if (retailPriceEnd.doubleValue() > retailPriceStart.doubleValue()) {
						countryPrice.setRetailPriceEnd(retailPriceEnd);
					}
					aliexpressProductCountryPriceMapper.updateByPrimaryKeySelective(countryPrice);
				} else if (listCountryPrice != null && listCountryPrice.size() > 1) {
					for (AliexpressProductCountryPrice countryPrice : listCountryPrice) {
						countryPrice.setRetailPriceStart(retailPriceStart);
						if (retailPriceEnd.doubleValue() > retailPriceStart.doubleValue()) {
							countryPrice.setRetailPriceEnd(retailPriceEnd);
						}
						aliexpressProductCountryPriceMapper.updateByPrimaryKeySelective(countryPrice);
					}
				}
			}
		}
        return 1;
    }

	/**
	 * 调用中转项目的接口
	 * @param paramsMap
	 * @param type
	 * @param empowerId
	 * @return
	 */
    public String postAliexpressApi(int type,Map<String, String> paramsMap,Long empowerId){
		Empower empower = new Empower();
		empower.setStatus(1);
		empower.setEmpowerId(empowerId.intValue());
		empower.setPlatform(3);//速卖通平台
		empower = empowerMapper.selectOneByAcount(empower);
		if(empower==null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "账号状态异常");
		}
		String url = config.getAliexpressUrl();
		if(type==1){//上架
			url+="/api/aliexpress/onlineaeproduct";
		}else if(type==2){//下架
			url+="/api/aliexpress/offlineaeproduct";
		}else if(type==3){//编辑商品单个SKU价格
			url+="/api/aliexpress/editsingleskuprice";
		}else if(type==4){//编辑商品单个SKU库存
			url+="/api/aliexpress/editsingleskustock";
		}else if(type==5){//查询商品详情
			url+="/api/aliexpress/findaeproductbyid";
		}
		paramsMap.put("sessionKey", empower.getToken());
		String body = HttpUtil.post(url, paramsMap);
		return body;
	}

    @Override
    public String uploadimageforsdk(String imgUrl, String groupId, String sessionKey, Long empowerId) {
        if(StringUtils.isBlank(sessionKey) && empowerId!=null) {
            Empower empower = new Empower();
            empower.setStatus(1);
            empower.setEmpowerId(empowerId.intValue());
            empower.setPlatform(3);//速卖通平台
            empower = empowerMapper.selectOneByAcount(empower);

            sessionKey = empower.getToken();
        }

        String url = config.getAliexpressUrl()+"/api/aliexpress/uploadimageforsdk";
		Map<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put("sessionKey",sessionKey);
		paramsMap.put("imgBase64", this.encodeImageToBase64(imgUrl));

		String imgName = imgUrl.substring(imgUrl.lastIndexOf("/")+1);
		//是否有?在后面
		int num = imgName.indexOf("?");
		if(num>0){
			imgName = imgName.substring(0,num);
		}
		paramsMap.put("imgName", imgName);//图片名称
		String body= HttpUtil.post(url,paramsMap);
		if(body==null || "".equals(body)){
			return null;
		}
        JSONObject jsonObject = JSONObject.parseObject(body);
        Object uploadimageforsdkResponse = jsonObject.get("aliexpress_photobank_redefining_uploadimageforsdk_response");
        if(uploadimageforsdkResponse!=null){
            JSONObject jsonResult = JSONObject.parseObject(uploadimageforsdkResponse.toString());
            if(jsonResult!=null){
				JSONObject resultJson = JSONObject.parseObject(jsonResult.toString());
				Object result = resultJson.get("result");
				if(result!=null) {
					JSONObject photoJson = JSONObject.parseObject(result.toString());
					if(photoJson!=null){
						Object photobankUrl = photoJson.get("photobank_url");
						if(photobankUrl!=null){
							return photobankUrl.toString();
						}
					}

				}
            }
        }
        return null;
    }

	@Override
	public String uploadimageforsdkBase64(String base64,String imgName, String groupId, String sessionKey, Long empowerId) {
		if(StringUtils.isBlank(sessionKey) && empowerId!=null) {
			Empower empower = new Empower();
			empower.setStatus(1);
			empower.setEmpowerId(empowerId.intValue());
			empower.setPlatform(3);//速卖通平台
			empower = empowerMapper.selectOneByAcount(empower);

			sessionKey = empower.getToken();
		}

		String url = config.getAliexpressUrl()+"/api/aliexpress/uploadimageforsdk";
		Map<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put("sessionKey",sessionKey);
		paramsMap.put("imgBase64", base64);
		paramsMap.put("imgName", imgName);//图片名称
		String body= HttpUtil.post(url,paramsMap);
		if(body==null || "".equals(body)){
			return "";
		}
		JSONObject jsonObject = JSONObject.parseObject(body);
		Object uploadimageforsdkResponse = jsonObject.get("aliexpress_photobank_redefining_uploadimageforsdk_response");
		if(uploadimageforsdkResponse!=null){
			JSONObject jsonResult = JSONObject.parseObject(uploadimageforsdkResponse.toString());
			if(jsonResult!=null){
				JSONObject resultJson = JSONObject.parseObject(jsonResult.toString());
				Object result = resultJson.get("result");
				if(result!=null) {
					JSONObject photoJson = JSONObject.parseObject(result.toString());
					if(photoJson!=null){
						Object photobankUrl = photoJson.get("photobank_url");
						if(photobankUrl!=null){
							return photobankUrl.toString();
						}
					}

				}
			}
		}
		return "";
	}

	@Override
	public List<AliexpressPublishModel> getAliexpressPublishModelList(Integer publishStatus) {
		return aliexpressPublishListingMapper.getAliexpressPublishModelList(publishStatus);
	}

	/**
	 * 将网络图片编码为base64
	 *
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private String encodeImageToBase64(String imgUrl) {

		//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		//打开链接
		HttpURLConnection conn = null;
		try {
			URL url = new URL(imgUrl);
			conn = (HttpURLConnection) url.openConnection();
			//设置请求方式为"GET"
			conn.setRequestMethod("GET");
			//超时响应时间为5秒
			conn.setConnectTimeout(5 * 1000);
			//通过输入流获取图片数据
			InputStream inStream = conn.getInputStream();
			//得到图片的二进制数据，以二进制封装得到数据，具有通用性
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			//创建一个Buffer字符串
			byte[] buffer = new byte[1024];
			//每次读取的字符串长度，如果为-1，代表全部读取完毕
			int len = 0;
			//使用一个输入流从buffer里把数据读取出来
			while ((len = inStream.read(buffer)) != -1) {
				//用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
				outStream.write(buffer, 0, len);
			}
			//关闭输入流
			inStream.close();
			//byte[] data = outStream.toByteArray();
			//对字节数组Base64编码
			//BASE64Encoder encoder = new BASE64Encoder();
			//返回Base64编码过的字节数组字符串
			//String base64 = encoder.encode(data);
			return Base64Utils.encodeToString(outStream.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}





    /**
	 *
	 * @param listingId 刊登id
	 * @param operationUser 操作用户
	 * @param operationType 操作类型
	 * @param operationContent 操作内容
	 */
    public void insertAliexpressOperationLog(Long listingId,String operationUser,String operationType,String operationContent,Long userId){
		AliexpressOperationLog log = new AliexpressOperationLog();
		log.setListingId(listingId);
		log.setOperationTime(new Date());
		log.setOperationType(operationType);
		log.setOperationUser(operationUser);
		log.setOperationUserId(userId);
		aliexpressOperationLogMapper.insert(log);
	}

	/**
	 *
	 * @param listingId 刊登id
	 * @param errorCode 异常
	 * @param errorMessage 异常信息
	 * @param severityCode 刊登异常类型
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void insertAliexpressPublishListingError(Long listingId,String errorCode,String errorMessage,String severityCode){
		AliexpressPublishListingError log = new AliexpressPublishListingError();
		log.setListingId(listingId);
		log.setCreationTime(new Date());
		log.setErrorCode(errorCode);
		log.setErrorMessage(errorMessage);
		log.setSeverityCode(severityCode);
		aliexpressPublishListingErrorMapper.insert(log);
	}

    @Override
    public Integer getUserNameCount(String plAccount) {
		return aliexpressPublishListingMapper.getUserNameCount(plAccount);
    }
	@Override
	public void udpateAliexpressPublishListing(Long id,int type){
		if (id==null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");
		}
		AliexpressPublishListing publishListing = this.getAliexpressPublishListingById(id);
		if (publishListing == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id不存在");
		}
		AliexpressPublishListing updateObj = new AliexpressPublishListing();
		String msg="";
		String operationType="";
		try {
			updateObj.setId(id);
			if(type==1){
				if(publishListing.getPublishStatus()!=AliexpressEnum.AliexpressStatusEnum.END.getCode()){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "刊登状态不正确无法操作");
				}
				updateObj.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.SALE.getCode());
				updateObj.setOnlineTime(new Date());
				if(publishListing.getItemId()!=null) {
					Map<String, String> paramsMap = Maps.newHashMap();
					paramsMap.put("productId", publishListing.getItemId().toString());
					String body = this.postAliexpressApi(1, paramsMap, publishListing.getEmpowerId());
					Map<String,Object> map = JsonAnalysis.getErrerJosnMsg(body);
					String success = map.get("success").toString();
					if("error_response".equals(success)){
						//异常
						String code = map.get("code").toString();
						String msgs = map.get("msg").toString();
						String subMsg = msgs+" "+map.get("subMsg").toString();
						this.insertAliexpressPublishListingError(publishListing.getId(),code,subMsg,"Error");
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, subMsg);
					}else if("errer".equals(success)){
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求参数错误");
					}
				}
				operationType = AliexpressOperationEnum.PUTAWAY.getCode();
				msg="速卖通商品上架";
			}else {
				if(publishListing.getPublishStatus()!=AliexpressEnum.AliexpressStatusEnum.SALE.getCode()){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "刊登状态不正确无法操作");
				}
				updateObj.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.END.getCode());
				updateObj.setEndTimes(new Date());
				if (publishListing.getItemId() != null) {
					Map<String, String> paramsMap = Maps.newHashMap();
					paramsMap.put("productId", publishListing.getItemId().toString());
					String body = this.postAliexpressApi(2, paramsMap, publishListing.getEmpowerId());
					Map<String,Object> map = JsonAnalysis.getErrerJosnMsg(body);
					String success = map.get("success").toString();
					if("error_response".equals(success)){
						//修改库存异常
						String code = map.get("code").toString();
						String msgs = map.get("msg").toString();
						String subMsg = msgs+" "+map.get("subMsg").toString();
						this.insertAliexpressPublishListingError(publishListing.getId(),code,subMsg,"Error");
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, subMsg);
					}else if("errer".equals(success)){
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求参数错误");
					}
				}
				operationType = AliexpressOperationEnum.END.getCode();
				msg="速卖通商品下架";
			}
			int rows = aliexpressPublishListingMapper.updateByPrimaryKeySelective(updateObj);
			if(rows <= 0) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "更新记录数0条");
			}
			//刊登操作日志
			UserDTO userDTO = getUserInfo.getUserDTO();
			this.insertAliexpressOperationLog(publishListing.getId(),
					UserSession.getUserBaseUserInfo().getUsername(),operationType,msg,Long.valueOf(userDTO.getUserId()));

		} catch (GlobalException e) {
			throw e;
		}
	}

	@Override
	public List<AliexpressPublishListingProduct> getProductByPlatformSku(String platformSku, Long publishListingId) {
		return aliexpressPublishListingProductMapper.getProductByPlatformSku(platformSku,publishListingId);
	}

    @Override
    public List<Map<String, Object>> getAliexpressSkuNumber() {
        return  aliexpressPublishListingMapper.getAliexpressSkuNumber();
    }

	@Override
	public List<ResultPublishListingVO> getAliexpressResultPublishListingVO(List<String> platformSkus,Integer empowerId) {
		return aliexpressPublishListingMapper.getAliexpressResultPublishListingVO(platformSkus,empowerId);
	}


	/**
	 * z正在销售商品 上线 下线状态编辑修改
	 * @param model
	 * @param empower
	 */
	private void updateProduct(AliexpressPublishRequest model,Empower empower){
		String productImage= model.getProductImage();
		String mobileRemark= model.getMobileRemark();
		if(org.apache.commons.lang3.StringUtils.isNotBlank(model.getProductImage())){
			String[] arrayImg = model.getProductImage().split("\\|");
			boolean bool = true;
			StringBuffer strImgs=new StringBuffer();
			for(String imgStr:arrayImg){
				String img=this.uploadimageforsdk(imgStr,null,empower.getToken(),null);
				if(img!=null) {
					if(bool){
						bool=false;
						strImgs.append(img);
					}else {
						strImgs.append(";"+img);
					}
				}
			}
			model.setProductImage(strImgs.toString());
			strImgs = null;
		}
		List<AliexpressPublishListingProduct> oldList = Lists.newArrayList();
		for(AliexpressPublishListingProduct product : model.getListProduct()){
			oldList.add(product);
			if(org.apache.commons.lang3.StringUtils.isNotBlank(product.getPublishProductImage())){
				product.setProductImage(product.getPublishProductImage());
			}else {
				if (org.apache.commons.lang3.StringUtils.isNotBlank(product.getProductImage())) {
					String img = this.uploadimageforsdk(product.getProductImage(), null, empower.getToken(), null);
					if (img != null) {
						product.setProductImage(img);
					}
				}
			}
		}
		//end图片需要上传到速卖通图片库才能够刊登

		//详情描述图片地址转换
		if(model.getProductDetails()!=null) {
			Set<String> imgs = ImgSet.getImgSet(model.getProductDetails());
			if (imgs != null && imgs.size() > 0) {
				for (String imgSrc : imgs) {
					if (imgSrc.indexOf("base64") >= 0) {
						String[] strImg = imgSrc.split(",");
						if (strImg.length == 2) {
							String base64 = strImg[1];
							String imgName = strImg[0].replace("data:image/", "").replace(";base64", "");
							imgName = "img." + imgName;
							String img = this.uploadimageforsdkBase64(base64, imgName, null, empower.getToken(), null);
							model.setProductDetails(model.getProductDetails().replace(imgSrc, img));
						}


					} else {
						String img = this.uploadimageforsdk(imgSrc, null, empower.getToken(), null);
						model.setProductDetails(model.getProductDetails().replace(imgSrc, img));
					}
				}
			}
		}

		if(model.getMobileRemark()!=null) {
			//{"mobileDetail":[{"content":"line 1","type":"text"},{"content":"line 2","type":"text"},{"col":1,"images":[{"imgUrl":"http://ae01.alicdn.com/kf/1.jpeg"}],"type":"image"},{"content":"line 4","type":"text"},{"col":2,"images":[{"imgUrl":"http://ae01.alicdn.com/kf/2.jpeg"},{"imgUrl":"http://ae01.alicdn.com/kf/3.jpeg"}],"type":"image"},{"content":"line 6","type":"text"}],"version":"1.0","versionNum":1}
			StringBuffer mobileDetail = new StringBuffer("{\"mobileDetail\":[");
			Set<String> imgs = ImgSet.getImgSet(model.getMobileRemark());
			if (imgs != null && imgs.size() > 0) {
				List<String> imgsList = Lists.newArrayList();
				for (String imgSrc : imgs) {
					if (imgSrc.indexOf("base64") >= 0) {
						String[] strImg = imgSrc.split(",");
						if (strImg.length == 2) {
							String base64 = strImg[1];
							String imgName = strImg[0].replace("data:image/", "").replace(";base64", "");
							imgName = "img." + imgName;
							String img = this.uploadimageforsdkBase64(base64, imgName, null, empower.getToken(), null);
							model.setMobileRemark(model.getMobileRemark().replace(imgSrc, img));
							imgsList.add(img);
						}


					} else {
						String img = this.uploadimageforsdk(imgSrc, null, empower.getToken(), null);
						model.setMobileRemark(model.getMobileRemark().replace(imgSrc, img));
						imgsList.add(img);
					}
				}

				if(imgsList.size()>0){
					String details = model.getMobileRemark();
					int i = 0;
					for(String imgSrc:imgsList){
						String img="<img src=\""+imgSrc+"\">";
						String replaceStr = "[img"+i+"]";
						details = details.replace(img,replaceStr);
						i++;
					}
					for(int j=(i-1);j>=0;j--){
						String rStr = "\\[img"+j+"\\]";
						String[] imgDetails = details.split(rStr);
						if(imgDetails.length==2){
							String content = imgDetails[0];
							mobileDetail.append("{\"content\":\""+content+"\",\"type\":\"text\"},");
						}
						mobileDetail.append("{\"col\":"+(1)+",\"images\":[{\"imgUrl\":\""+imgsList.get(j)+"\"}],\"type\":\"image\"},");
						if(j==0){
							String content = imgDetails[1];
							mobileDetail.append("{\"content\":\"" + content + "\",\"type\":\"text\"}");
						}
					}
				}
			}else {
				mobileDetail.append("{\"content\":\""+model.getMobileRemark()+"\",\"type\":\"text\"}");
			}
			mobileDetail.append("],\"version\":\"1.0\",\"versionNum\":1}");
			model.setMobileRemark(mobileDetail.toString());
		}



		String url = config.getAliexpressUrl()+"/api/aliexpress/updateProduct";
		Map<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put("sessionKey", empower.getToken());

		paramsMap.put("jsonStr", JSONObject.toJSONString(model, SerializerFeature.WriteMapNullValue));
		String body = HttpUtil.post(url, paramsMap);
		Map<String,Object> map = JsonAnalysis.getEditaeproductMsg(body);

		String success = map.get("success").toString();
		if("error_response".equals(success)){
			String code = map.get("code").toString();
			String msg = map.get("msg").toString();
			String subMsg = msg+" "+map.get("subMsg").toString();
			this.insertAliexpressPublishListingError(model.getId(),code,subMsg,"Error");
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,subMsg);
		}else if("true".equals(success)){
			//刊登操作日志
			UserDTO userDTO = getUserInfo.getUserDTO();
			this.insertAliexpressOperationLog(model.getId(),
					userDTO.getUserName(),AliexpressOperationEnum.RELIST.getCode(),"发布刊登",Long.valueOf(userDTO.getUserId()));
		}else if("false".equals(success)){
			//刊登操作日志
			String code = map.get("errorCode").toString();
			String errorMessage = map.get("errorMessage").toString();
			this.insertAliexpressPublishListingError(model.getId(),code,errorMessage,"Error");
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,errorMessage);
		}else{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "connection timeout");
		}
		model.setListProduct(oldList);
		model.setProductImage(productImage);
		model.setMobileRemark(mobileRemark);
	}

	/**
	 * listing 对象校验
	 * @param publishRequest 接收到的数据对像
	 * @param isEdit 是否是编辑
	 */
	public void check(AliexpressPublishRequest publishRequest, boolean isEdit,AliexpressPublishListing publishListing){
		if (isEdit){
			if (null == publishRequest.getId() || publishListing==null){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "编辑对像id不能为空");
			}
			if(!(publishListing.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.DRAFT.getCode()
					|| publishListing.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.PUBLISH_FAILED.getCode()
					|| publishListing.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.SALE.getCode()
					|| publishListing.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.END.getCode())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "刊登状态不正确无法操作");
			}
			publishRequest.setPublishStatus(publishListing.getPublishStatus());
		}
		if (null == publishRequest.getEmpowerId()) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "授权账号ID为空");
		}
		if (null == publishRequest.getCategoryId1()) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品分类不能为空");
		}
		if (null == publishRequest.getPublishType()) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "刊登类型不能为空");
		}
		if (null == publishRequest.getPlSpuSku()) {
			//throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连SPU或者SKU不能为空");
		}else{
			if(publishRequest.getPlSpuSku().trim().length()>100){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连SPU或者SKU长度过长");
			}
		}
		if (null == publishRequest.getTitle()) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品标题不能为空");
		}else{
			if(publishRequest.getTitle().trim().length()>128){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品标题长度过长");
			}
		}



		if (StringUtils.isBlank(publishRequest.getProductImage())) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品图片不能为空");
		}
		Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = pattern.matcher(publishRequest.getProductImage());
		if (m.find()) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "速卖通刊登时,产品图片路径中不允许出现中文字符");
		}
		if (StringUtils.isBlank(publishRequest.getProductDetails())) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品描述不能为空");
		}
		if (publishRequest.getMobileTerminal()!=null && publishRequest.getMobileTerminal()) {
			if (StringUtils.isBlank(publishRequest.getMobileRemark())) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "无线描述不能为空");
			}
		}
		if (StringUtils.isBlank(publishRequest.getRegionalPricing())) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "区域调价不能为空");
		}
		//批发价
		if (publishRequest.getWholesale()!=null && publishRequest.getWholesale()) {
			if (publishRequest.getBulkOrder()==null || (publishRequest.getBulkOrder() < 0 && publishRequest.getBulkOrder()> 1000)) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "购买数限制为1~1000");
			}
			if (publishRequest.getBulkDiscount()==null || (publishRequest.getBulkDiscount() < 1 && publishRequest.getBulkDiscount()> 99)) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "减免折扣限制为1~99");
			}
		}

		if (StringUtils.isBlank(publishRequest.getReduceStrategy())) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "库存扣减策略不能为空");
		}
		if (publishRequest.getDeliveryTime()==null || (publishRequest.getDeliveryTime() < 0 && publishRequest.getDeliveryTime()>60)) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货期限制为1-7天");
		}
		if (publishRequest.getUnit()==null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "最小计量单位不能为空");
		}
		if (publishRequest.getSalesMethod()==null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "销售方式不能为空");
		}else{
		    if(publishRequest.getSalesMethod()==1){
                publishRequest.setLotNum(1);
            }else{
		        if(publishRequest.getLotNum()==null || (publishRequest.getLotNum() < 1 && publishRequest.getLotNum()>100000)){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "打包出售限制为2~100000");
                }
            }

        }
		if (publishRequest.getPackagingWeight()==null) {
			//取值范围:0.001-500.000
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品包装后的重量不能为空");
		}else{
			if(!(publishRequest.getPackagingWeight().doubleValue()>0 &&publishRequest.getPackagingWeight().doubleValue()<=500)){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品包装后的重量限制为0.001~500.00");
			}
		}


		if (publishRequest.getIsPackSell()!=null && publishRequest.getIsPackSell()) {
			if (publishRequest.getBuyersPurchase()==null || (publishRequest.getBuyersPurchase() <= 0 && publishRequest.getBuyersPurchase()>1000)) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "自定义重量参数范围限制为1~1000");
			}
			if (publishRequest.getBuyersMore()==null || (publishRequest.getBuyersMore() < 0 && publishRequest.getBuyersMore()>1000)) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "自定义重量参数范围限制为1~1000");
			}
			if (publishRequest.getIncreaseWeight()==null || (publishRequest.getIncreaseWeight().doubleValue() <= 0 && publishRequest.getIncreaseWeight().doubleValue()>500)) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "自定义重量范围限制0.001~500.00");
			}
		}
		if (publishRequest.getPackageLength()==null || (publishRequest.getPackageLength() <= 0 && publishRequest.getPackageLength() > 700)) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "长度范围限制为1~700");
		}
		if (publishRequest.getPackageWidth()==null || (publishRequest.getPackageWidth() <= 0 && publishRequest.getPackageWidth()>700)) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "宽度范围限制为1~700");
		}
		if (publishRequest.getPackageHeight()==null || (publishRequest.getPackageHeight() <= 0 && publishRequest.getPackageHeight()>700)) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "高度限制范围1~700");
		}
		if (publishRequest.getFreightTemplateId()==null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "运费模板不能为空");
		}
		if (publishRequest.getPromiseTemplateId()==null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "服务模板不能为空");
		}
		if (publishRequest.getWsValidNum()==null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品有效期不能为空");
		}

//		if(publishRequest.getListAttribute()==null || publishRequest.getListAttribute().size()==0){
//			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品属性不能为空");
//		}
		Map<Long,AliexpressPublishListingAttribute> mapPublishListingAttribute = Maps.newHashMap();
		for(AliexpressPublishListingAttribute attribute : publishRequest.getListAttribute()){
			attribute.setId(null);
			if (null == attribute.getValueType()){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分类属性类型不能为空");
			}
			if (attribute.getValueType()==3){
				if (null == attribute.getAttributeCode()) {
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "自定义值属性不能为空");
				}else{
					if(attribute.getAttributeCode().length()>300){
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "自定义值属性长度过长");
					}
				}
				if (StringUtils.isBlank(attribute.getSelectionMode())) {
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "自定义值不能为空");
				}else{
					if(attribute.getSelectionMode().length()>300){
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "自定义值长度过长");
					}
				}
			}else{
				if (null == attribute.getCategoryAttributeId()) {
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分类属性不能为空");
				}
			}
			mapPublishListingAttribute.put(attribute.getCategoryAttributeId(),attribute);
		}
		Long categoryId = 0L;
		if(publishRequest.getCategoryId4()!=null && publishRequest.getCategoryId4()>0){
			categoryId = publishRequest.getCategoryId4();
		}else{
			if(publishRequest.getCategoryId3()!=null && publishRequest.getCategoryId3()>0){
				categoryId = publishRequest.getCategoryId3();
			}else{
				if(publishRequest.getCategoryId2()!=null && publishRequest.getCategoryId2()>0){
					categoryId = publishRequest.getCategoryId2();
				}else{
					categoryId = publishRequest.getCategoryId1();
				}
			}
		}
		List<AliexpressCategoryAttribute> listAliexpressCategoryAttribute = aliexpressCategoryAttributeMapper.getAliexpressCategoryAttributeByCategoryIdList(categoryId,publishRequest.getEmpowerId());
		for(AliexpressCategoryAttribute aca:listAliexpressCategoryAttribute){
			if(!aca.getSku()) {
				if(aca.getRequired()) {
					AliexpressPublishListingAttribute aliexpressPublishListingAttribute = mapPublishListingAttribute.get(aca.getAttributeId());
					if(aliexpressPublishListingAttribute==null){
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, aca.getAttributeName()+"("+aca.getAttributeNameEn()+") not null");
					}
					if (StringUtils.isBlank(aliexpressPublishListingAttribute.getSelectionMode())) {
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, aca.getAttributeName()+"("+aca.getAttributeNameEn()+") not null");
					}
				}
			}
		}


		if(publishRequest.getListProduct()==null || publishRequest.getListProduct().size()==0){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品不能为空");
		}
		Map<String,Object> mapProduct= Maps.newHashMap();//判断颜色发货地尺寸是否重复
		Map<String,Object> mapProductSku= Maps.newHashMap();//sku是否重复
		Map<String,Object> mapProductPlatformSku= Maps.newHashMap();//平台sku是否重复
		Map<String,String> mapProductSmtSkuId= Maps.newHashMap();//速卖通商品属性唯一id
		int countSmtSkuId = 0;
		for(AliexpressPublishListingProduct product :publishRequest.getListProduct()){
			String key="key";//判断sku属性是否重复
			product.setId(null);

			if(StringUtils.isBlank(product.getPlSku())){
				//throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连sku不能为空");
			}else{
				if(mapProductSku.get(product.getPlSku())!=null){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连sku重复");
				}
				mapProductSku.put(product.getPlSku(),product.getPlSku());
			}
			if(StringUtils.isBlank(product.getPlatformSku())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台sku不能为空");
			}else{
				if(product.getPlatformSku()!=null && product.getPlatformSku().length()>100){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台sku长度过长");
				}
				if(mapProductPlatformSku.get(product.getPlatformSku())!=null){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台sku重复");
				}
				mapProductPlatformSku.put(product.getPlatformSku(),product.getPlatformSku());
				key+=product.getPlatformSku();
			}
			if(product.getInventory() ==null || (product.getInventory() < 0 && product.getInventory() < 999999)){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "库存范围限制为0~999999");
			}
			if(product.getRetailPrice()==null ||(product.getRetailPrice().doubleValue() < 0 && product.getRetailPrice().doubleValue() < 1000000)){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "零售价范围限制为1~1000000");
			}
			if(product.getListSkuProperty()!=null) {
				List<AliexpressCategoryAttribute> listCategoryAttribute=aliexpressCategoryAttributeMapper.getCategoryAttributeByCategoryId(categoryId);
				if(listCategoryAttribute!=null){
					for (AliexpressPublishListingSkuProperty skuProperty:product.getListSkuProperty()) {
						for(AliexpressCategoryAttribute attribute:listCategoryAttribute){
							if(attribute.getAttributeId().equals(Long.valueOf(skuProperty.getAttributeId()))){
								skuProperty.setSort(attribute.getSpec());
								break;
							}
						}
					}
					Collections.sort(product.getListSkuProperty(), new Comparator<AliexpressPublishListingSkuProperty>() {
						@Override
						public int compare(AliexpressPublishListingSkuProperty u1, AliexpressPublishListingSkuProperty u2) {
							if(u1.getSort()!=null && u2.getSort()!=null) {
								int diff = u1.getSort() - u2.getSort();
								if (diff > 0) {
									return 1;
								} else if (diff < 0) {
									return -1;
								}
							}
							return 0; //相等为0
						}
					});

				}


				StringBuffer smtSkuId = new  StringBuffer("");
				for (AliexpressPublishListingSkuProperty skuProperty:product.getListSkuProperty()) {
					if(skuProperty.getSelectAlias()!=null && skuProperty.getSelectAlias().length()>100){
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "详细信息别名文本过长");
					}
					if(StringUtils.isBlank(skuProperty.getAttributeId())){
						if(StringUtils.isBlank(skuProperty.getSelectId())){
							throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品属性不能为空");
						}else{
							key+=skuProperty.getSelectId();
						}
					}
					if("".equals(smtSkuId.toString())){
						smtSkuId.append(skuProperty.getAttributeId()+":"+skuProperty.getSelectId());
					}else{
						smtSkuId.append(";"+skuProperty.getAttributeId()+":"+skuProperty.getSelectId());
					}
				}
				mapProductSmtSkuId.put(product.getPlSku(),smtSkuId.toString());
				product.setSmtSkuId(smtSkuId.toString());
				if("".equals(smtSkuId.toString())){
					countSmtSkuId++;
				}
			}
			if(mapProduct.get(key)!=null){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"商品属性重复");
			}else{
				mapProduct.put(key,true);
			}
		}
		if(countSmtSkuId>1){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"商品属性重复");
		}
		if(mapProductPlatformSku!=null && mapProductPlatformSku.size()>0){
			List<AliexpressPublishListingProduct> listAliexpressPublishListingProduct = aliexpressPublishListingProductMapper.getPublishListingProductByPlatformSku(mapProductPlatformSku,publishRequest.getId());

			if(listAliexpressPublishListingProduct!=null && listAliexpressPublishListingProduct.size()>0){
				StringBuffer countProduct = new StringBuffer("");
				listAliexpressPublishListingProduct.forEach(p -> {
					countProduct.append(p.getPlatformSku()+",");
				});
				if(!"".equals(countProduct.toString())) {

					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100700, countProduct.toString());

					//throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100700, strPlatformSku.toString());

				}
			}
		}


		List<AliexpressProductCountryPrice> listProductCountryPrice = publishRequest.getListProductCountryPrice();
		if(listProductCountryPrice!=null && listProductCountryPrice.size()>0){
			List<AliexpressProductCountryPrice> removeList =Lists.newArrayList();
			for(AliexpressProductCountryPrice countryPrice : listProductCountryPrice){

				countryPrice.setId(null);

				if (StringUtils.isBlank(countryPrice.getCountryName())) {
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "区域缩写不能为空");
				}
//				if (StringUtils.isBlank(countryPrice.getPlSku())) {
//					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台sku不能为空");
//				}
				if(!"absolute".equals(publishRequest.getRegionalPricing())){
					if (StringUtils.isBlank(countryPrice.getSignBit())) {
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "正负号不能为空");
					}
				}
				if (countryPrice.getPricingRange()==null) {
					removeList.add(countryPrice);
					//throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "价格或者百分比不能为空");
				}else{
//					String smtSkuId=mapProductSmtSkuId.get(countryPrice.getPlSku());
//					if(smtSkuId!=null){
//						//smtSkuId = smtSkuId.replace(";",",");
//						countryPrice.setSmtSkuId(smtSkuId);
//					}
				}
			}
			publishRequest.getListProductCountryPrice().removeAll(removeList);
		}

		if(publishRequest.getCategoryId4()==null){
			publishRequest.setCategoryId4(0L);
		}
		if(publishRequest.getCategoryId3()==null){
			publishRequest.setCategoryId3(0L);
		}
		if(publishRequest.getCategoryId2()==null){
			publishRequest.setCategoryId2(0L);
		}
	}
}
