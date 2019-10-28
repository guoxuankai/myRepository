package com.rondaful.cloud.seller.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.common.aliexpress.AliexpressMethodNameEnum;
import com.rondaful.cloud.seller.common.aliexpress.HttpTaoBaoApi;
import com.rondaful.cloud.seller.common.aliexpress.JsonAnalysis;
import com.rondaful.cloud.seller.config.AliexpressConfig;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.entity.aliexpress.*;
import com.rondaful.cloud.seller.enums.AliexpressEnum;
import com.rondaful.cloud.seller.enums.AliexpressOperationEnum;
import com.rondaful.cloud.seller.mapper.*;
import com.rondaful.cloud.seller.rabbitmq.AliexpressSender;
import com.rondaful.cloud.seller.remote.RemoteCommodityService;
import com.rondaful.cloud.seller.service.IAliexpressBaseService;
import com.rondaful.cloud.seller.service.IAliexpressListingService;
import com.rondaful.cloud.seller.service.IAliexpressPhotoBankService;
import com.rondaful.cloud.seller.service.IAliexpressPublishListingService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 *速卖通 api参考地址
 *
 *开发者中心
 *
 * @author chenhan
 *
 */

@Service
public class AliexpressListingServiceImpl implements IAliexpressListingService {

    private final Logger logger = LoggerFactory.getLogger(AliexpressListingServiceImpl.class);
    @Autowired
    private IAliexpressPhotoBankService aliexpressPhotoBankService;
    @Autowired
    private IAliexpressPublishListingService aliexpressPublishListingService;
    @Autowired
    private AliexpressConfig config;
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
    private AliexpressProductCountryPriceMapper aliexpressProductCountryPriceMapper;
    @Autowired
    private AliexpressCategoryAttributeMapper aliexpressCategoryAttributeMapper;
    @Autowired
    private EmpowerMapper empowerMapper;
    @Autowired
    private HttpTaoBaoApi httpTaoBaoApi;
    @Autowired
    private AliexpressSender sender;
    @Autowired
    private AliexpressCategoryMapper aliexpressCategoryMapper;
    @Autowired
    private RemoteCommodityService remoteCommodityService;
    @Autowired
    private IAliexpressBaseService aliexpressBaseService;

    public Empower getEmpowerById(Long empowerId){
        Empower empower = new Empower();
        empower.setStatus(1);
        empower.setEmpowerId(empowerId.intValue());
        empower.setPlatform(3);//速卖通平台
        empower = empowerMapper.selectOneByAcount(empower);
        if(empower==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "账号状态异常");
        }
        return empower;
    }

    @Override
    public void updateAliexpressListing(Long empowerId,Long userId,String userName,Long sellerId,Long id,Long itemId,BigDecimal productMinPrice,BigDecimal productMaxPrice){
        Empower empower =this.getEmpowerById(empowerId);
        Map<String, Object> map = Maps.newHashMap();
        map.put("productId",itemId.toString());
        map.put("sessionKey", empower.getToken());
        String body = httpTaoBaoApi.getTaoBaoApi(AliexpressMethodNameEnum.FINDAEPRODUCTBYID.getCode(),map);
        Map<String,Object> retMap=JsonAnalysis.getGatewayMsg(body);
        String success = retMap.get("success").toString();
        if("200".equals(success)){
            AliexpressListingModel aliexpressListingModel = JSONObject.parseObject(retMap.get("data").toString(), AliexpressListingModel.class);
            System.out.println(aliexpressListingModel.getProductId());
            AliexpressPublishListing aliexpressPublishListing = aliexpressPublishListingMapper.getAliexpressPublishListingByItemId(itemId,empowerId);

            if(aliexpressPublishListing!=null){
                this.updatePublishListing(aliexpressListingModel,aliexpressPublishListing,empower,userId,userName,sellerId,productMinPrice,productMaxPrice);
            }else{
                this.insertPublishListing(aliexpressListingModel,empower,userId,userName,sellerId,productMinPrice,productMaxPrice);
            }
        }else {
            String msg = retMap.get("msg").toString();
            throw new GlobalException(success, msg);
        }

    }


    public AliexpressPublishListing updatePublishListing(AliexpressListingModel aliexpressListingModel,
                                                         AliexpressPublishListing aliexpressPublishListing, Empower empower,Long userId,String userName,Long sellerId,BigDecimal productMinPrice,BigDecimal productMaxPrice) {
        if(aliexpressPublishListing==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "编辑对像id不能为空");
        }
        AliexpressPublishListing listing = this.setAliexpressPublishListing(aliexpressListingModel,empower,userId,userName,sellerId);

        Date date = new Date();
        listing.setUpdateTime(date);
        listing.setVersion(aliexpressPublishListing.getVersion());
        listing.setId(aliexpressPublishListing.getId());
        listing.setPlatformListing(aliexpressPublishListing.getPlatformListing());
        if(listing.getCategoryId4()!=null && listing.getCategoryId4()==0L){
            listing.setCategoryId4(null);
        }
        if(listing.getCategoryId3()!=null && listing.getCategoryId3()==0L){
            listing.setCategoryId3(null);
        }
        if(listing.getCategoryId2()!=null && listing.getCategoryId2()==0L){
            listing.setCategoryId2(null);
        }

        int numInsertInto = aliexpressPublishListingMapper.updateByPrimaryKeySelective(listing);

        if(numInsertInto==0){//系统异常
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, ResponseCodeEnum.RETURN_CODE_100500.getMsg());
        }

        //区域调价数据组装
        String configurationType ="percentage";
        AliexpressAeopNationalQuoteConfiguration quoteConfiguration= aliexpressListingModel.getAeopNationalQuoteConfiguration();
        List<AliexpressProductCountryPrice> listAliexpressProductCountryPrice = null;
        if(quoteConfiguration!=null){
            configurationType = quoteConfiguration.getConfigurationType();
            listAliexpressProductCountryPrice = this.setAliexpressProductCountryPrice(quoteConfiguration);
        }
        //详情部分
        AliexpressPublishListingDetail detail = this.setAliexpressPublishListingDetail(aliexpressListingModel);
        detail.setUpdateTime(date);
        detail.setPublishListingId(listing.getId());
        detail.setRegionalPricing(configurationType);
        aliexpressPublishListingDetailMapper.updateByPrimaryKeySelective(detail);

        //平台商品sku映射
        aliexpressPublishListingProductMapper.deleteByListingId(listing.getId());
        aliexpressPublishListingSkuPropertyMapper.deleteByListingId(listing.getId());
        this.insertAliexpressPublishListingProduct(aliexpressListingModel,listing.getId(),date,empower,listing.getPlatformListing());

        //属性设置
        aliexpressPublishListingAttributeMapper.deleteByListingId(listing.getId());
        List<AliexpressPublishListingAttribute> listAliexpressPublishListingAttribute = this.setAliexpressPublishListingAttribute(aliexpressListingModel,empower.getEmpowerId().longValue());
        for(AliexpressPublishListingAttribute attribute:listAliexpressPublishListingAttribute) {
            attribute.setPublishListingId(listing.getId());
            attribute.setCreateTime(date);
            aliexpressPublishListingAttributeMapper.insertSelective(attribute);
        }

        //区域价格
        aliexpressProductCountryPriceMapper.deleteByListingId(listing.getId());
        if(listAliexpressProductCountryPrice!=null) {
            if(productMinPrice==null || productMinPrice.doubleValue()<=0){
                for(AliexpressAeopAeProductSku productSku : aliexpressListingModel.getAeopAeProductSKUs()){
                    BigDecimal price = new BigDecimal(productSku.getSkuPrice());
                    if(productMinPrice==null || productMinPrice.doubleValue()>price.doubleValue()){
                        productMinPrice = price;
                    }
                    if(productMaxPrice==null || productMaxPrice.doubleValue()<price.doubleValue()){
                        productMinPrice = price;
                    }
                }
            }
            List<AliexpressCountry> listAliexpressCountry = aliexpressBaseService.getAliexpressCountryByList();
            Map<String,Long> mapCountry = Maps.newHashMap();
            if(listAliexpressCountry!=null){
                listAliexpressCountry.forEach(country->{
                    mapCountry.put(country.getAbbreviation(),country.getId());
                });

            }
            for (AliexpressProductCountryPrice productCountryPrice : listAliexpressProductCountryPrice) {
                productCountryPrice.setListingId(listing.getId());
                productCountryPrice.setCreateTime(date);
                productCountryPrice.setRetailPriceStart(productMinPrice);
                productCountryPrice.setRetailPriceEnd(productMaxPrice);
                if(mapCountry.size()>0 && productCountryPrice.getCountryName()!=null){
                    productCountryPrice.setCountryId(mapCountry.get(productCountryPrice.getCountryName()));
                }
                aliexpressProductCountryPriceMapper.insertSelective(productCountryPrice);
            }
        }

        //日志
        String operationType = AliexpressOperationEnum.SYNC.getCode();
        aliexpressPublishListingService.insertAliexpressOperationLog(listing.getId(),userName,operationType,aliexpressListingModel.getSubject(),userId);

        return listing;
    }

    public AliexpressPublishListing insertPublishListing(AliexpressListingModel aliexpressListingModel, Empower empower,Long userId,String userName,Long sellerId,BigDecimal productMinPrice,BigDecimal productMaxPrice) {
        Date date = new Date();
        AliexpressPublishListing listing = this.setAliexpressPublishListing(aliexpressListingModel,empower,userId,userName,sellerId);
        listing.setCreateTime(date);
        listing.setUpdateTime(date);
        listing.setVersion(1);
        listing.setPlatformListing(2);
        int numInsertInto = aliexpressPublishListingMapper.insertSelective(listing);

        if(numInsertInto==0){//系统异常
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, ResponseCodeEnum.RETURN_CODE_100500.getMsg());
        }

        //区域调价数据组装
        String configurationType ="percentage";
        AliexpressAeopNationalQuoteConfiguration quoteConfiguration= aliexpressListingModel.getAeopNationalQuoteConfiguration();
        List<AliexpressProductCountryPrice> listAliexpressProductCountryPrice = null;
        if(quoteConfiguration!=null){
            configurationType = quoteConfiguration.getConfigurationType();
            listAliexpressProductCountryPrice = this.setAliexpressProductCountryPrice(quoteConfiguration);
        }
        //end 区域调价数据组装

        //详情部分
        AliexpressPublishListingDetail detail = this.setAliexpressPublishListingDetail(aliexpressListingModel);

        detail.setCreateTime(date);
        detail.setUpdateTime(date);
        detail.setPublishListingId(listing.getId());
        detail.setVersion(1);
        detail.setRegionalPricing(configurationType);
        int numInsertDetail = aliexpressPublishListingDetailMapper.insertSelective(detail);
        if(numInsertDetail==0){//系统异常
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, ResponseCodeEnum.RETURN_CODE_100500.getMsg());
        }
        //平台商品sku映射
        this.insertAliexpressPublishListingProduct(aliexpressListingModel,listing.getId(),date,empower,listing.getPlatformListing());

        //属性设置
        List<AliexpressPublishListingAttribute> listAliexpressPublishListingAttribute = this.setAliexpressPublishListingAttribute(aliexpressListingModel,empower.getEmpowerId().longValue());
        for(AliexpressPublishListingAttribute attribute:listAliexpressPublishListingAttribute) {
            attribute.setPublishListingId(listing.getId());
            attribute.setCreateTime(date);
            aliexpressPublishListingAttributeMapper.insertSelective(attribute);
        }

        //区域价格
        if(listAliexpressProductCountryPrice!=null) {
            if(productMinPrice==null || productMinPrice.doubleValue()<=0){
                for(AliexpressAeopAeProductSku productSku : aliexpressListingModel.getAeopAeProductSKUs()){
                    BigDecimal price = new BigDecimal(productSku.getSkuPrice());
                    if(productMinPrice==null || productMinPrice.doubleValue()>price.doubleValue()){
                        productMinPrice = price;
                    }
                    if(productMaxPrice==null || productMaxPrice.doubleValue()<price.doubleValue()){
                        productMinPrice = price;
                    }
                }
            }
            List<AliexpressCountry> listAliexpressCountry = aliexpressBaseService.getAliexpressCountryByList();
            Map<String,Long> mapCountry = Maps.newHashMap();
            if(listAliexpressCountry!=null){
                listAliexpressCountry.forEach(country->{
                    mapCountry.put(country.getAbbreviation(),country.getId());
                });

            }
            for (AliexpressProductCountryPrice productCountryPrice : listAliexpressProductCountryPrice) {
                productCountryPrice.setListingId(listing.getId());
                productCountryPrice.setCreateTime(date);
                if(mapCountry.size()>0 && productCountryPrice.getCountryName()!=null){
                    productCountryPrice.setCountryId(mapCountry.get(productCountryPrice.getCountryName()));
                }
                aliexpressProductCountryPriceMapper.insertSelective(productCountryPrice);
            }
        }

        //日志
        String operationType = AliexpressOperationEnum.SYNC.getCode();
        aliexpressPublishListingService.insertAliexpressOperationLog(listing.getId(),userName,operationType,aliexpressListingModel.getSubject(),userId);
        return listing;
    }

     private List<AliexpressPublishListingAttribute> setAliexpressPublishListingAttribute(AliexpressListingModel aliexpressListingModel,Long empowerId){
         List<AliexpressPublishListingAttribute> listAliexpressPublishListingAttribute = Lists.newArrayList();
         List<AliexpressCategoryAttribute> listAliexpressCategoryAttribute = aliexpressCategoryAttributeMapper.getAliexpressCategoryAttributeByCategoryIdList(aliexpressListingModel.getCategoryId(),empowerId);
         Map<Long,Integer> valueTypeMap = Maps.newHashMap();
         Map<Long,Integer> mapcheckbox = Maps.newHashMap();
         for(AliexpressCategoryAttribute aca:listAliexpressCategoryAttribute){
             if(aca.getSku()){
                 continue;
             }
             if("input".equals(aca.getAttributeShowTypeValue())){
                 valueTypeMap.put(aca.getAttributeId(),2);
             }else{
                 if("check_box".equals(aca.getAttributeShowTypeValue())){
                     mapcheckbox.put(aca.getAttributeId(),1);
                 }
                 valueTypeMap.put(aca.getAttributeId(),1);
             }
         }
         for (AliexpressAeopAeProductProperty aeopAeProductProperty:aliexpressListingModel.getAeopAeProductPropertys()) {
             Integer valueType = valueTypeMap.get(aeopAeProductProperty.getAttrNameId());
             if(valueType==null){
                 valueType = 3;
             }


             AliexpressPublishListingAttribute attribute = new AliexpressPublishListingAttribute();
             attribute.setValueType(valueType);
             if(valueType==1){
                 //分类属性id
                 attribute.setCategoryAttributeId(aeopAeProductProperty.getAttrNameId());
                 attribute.setSelectionMode(aeopAeProductProperty.getAttrValueId()+"");
                 if(aeopAeProductProperty.getAttrValueId()==null){
                     continue;
                 }
                 if(mapcheckbox.size()>0 && mapcheckbox.get(aeopAeProductProperty.getAttrNameId())!=null) {
                     //复选框
                     boolean bool = false;
                     for (AliexpressPublishListingAttribute att : listAliexpressPublishListingAttribute) {
                         if (aeopAeProductProperty.getAttrNameId().equals(att.getCategoryAttributeId())) {
                             att.setSelectionMode(att.getSelectionMode() + "," + aeopAeProductProperty.getAttrValueId());
                             bool = true;
                             break;
                         }
                     }
                     if (bool) {
                         continue;
                     }
                 }

             }else if (valueType==2){
                 //分类属性id
                 attribute.setCategoryAttributeId(aeopAeProductProperty.getAttrNameId());
                 attribute.setSelectionMode(aeopAeProductProperty.getAttrValue());
                 if(aeopAeProductProperty.getAttrValue()==null){
                     continue;
                 }
             }else if (valueType==3){
                 attribute.setSelectionMode(aeopAeProductProperty.getAttrValue());
                 //自定义属性code/其他属性文本值
                 attribute.setAttributeCode(aeopAeProductProperty.getAttrName());
                 if(aeopAeProductProperty.getAttrName()==null){
                     attribute.setCategoryAttributeId(aeopAeProductProperty.getAttrNameId());
                     if(aeopAeProductProperty.getAttrValue()!=null){
                         attribute.setValueType(2);
                         attribute.setSelectionMode(aeopAeProductProperty.getAttrValue());
                     }else {
                         attribute.setValueType(1);
                         attribute.setSelectionMode(aeopAeProductProperty.getAttrValueId() + "");
                     }
                 }
             }else {
                 attribute.setSelectionMode(aeopAeProductProperty.getAttrValueId()+"");
                 attribute.setCategoryAttributeId(aeopAeProductProperty.getAttrNameId());
                 attribute.setAttributeCode(aeopAeProductProperty.getAttrName());
             }
             listAliexpressPublishListingAttribute.add(attribute);
         }
         if(mapcheckbox.size()>0){
             for(AliexpressPublishListingAttribute attribute:listAliexpressPublishListingAttribute){
                 if(attribute.getCategoryAttributeId()!=null && mapcheckbox.get(attribute.getCategoryAttributeId())!=null){
                     attribute.setSelectionMode("["+ attribute.getSelectionMode()+"]");
                 }
             }
         }

         return  listAliexpressPublishListingAttribute;
     }



    private AliexpressPublishListing setAliexpressPublishListing(AliexpressListingModel aliexpressListingModel,Empower empower,Long userId,String userName,Long sellerId){
        AliexpressPublishListing listing = new AliexpressPublishListing();

        listing.setEmpowerId(empower.getEmpowerId().longValue());
        listing.setPublishAccount(empower.getAccount());
        listing.setPlAccount(userName);
        listing.setCategoryId1(aliexpressListingModel.getCategoryId());
        List<AliexpressCategory> listAliexpressCategory = Lists.newArrayList();
        AliexpressCategory aliexpressCategory = aliexpressCategoryMapper.getCategoryByCategoryId(aliexpressListingModel.getCategoryId());
        listAliexpressCategory.add(aliexpressCategory);
        while (aliexpressCategory.getCategoryParentId()>0){
            aliexpressCategory = aliexpressCategoryMapper.getCategoryByCategoryId(aliexpressCategory.getCategoryParentId());
            listAliexpressCategory.add(aliexpressCategory);
        }
        if(listAliexpressCategory.size()==4){
            listing.setCategoryId1(listAliexpressCategory.get(3).getCategoryId());
            listing.setCategoryId2(listAliexpressCategory.get(2).getCategoryId());
            listing.setCategoryId3(listAliexpressCategory.get(1).getCategoryId());
            listing.setCategoryId4(listAliexpressCategory.get(0).getCategoryId());
        }else if(listAliexpressCategory.size()==3){
            listing.setCategoryId1(listAliexpressCategory.get(2).getCategoryId());
            listing.setCategoryId2(listAliexpressCategory.get(1).getCategoryId());
            listing.setCategoryId3(listAliexpressCategory.get(0).getCategoryId());
        }else if(listAliexpressCategory.size()==2){
            listing.setCategoryId1(listAliexpressCategory.get(1).getCategoryId());
            listing.setCategoryId2(listAliexpressCategory.get(0).getCategoryId());
        }

        listing.setTitle(aliexpressListingModel.getSubject());
        if(aliexpressListingModel.getAeopAeProductSKUs()!=null && aliexpressListingModel.getAeopAeProductSKUs().size()>1){
            listing.setPublishType(1);
        }else{
            listing.setPublishType(2);
        }
        //品连spu或sku
        //plSpuSku;
        String imageUrls = aliexpressListingModel.getImageURLs();
        if(imageUrls!=null){
            imageUrls = imageUrls.replace(";","|");
        }
        listing.setProductImage(imageUrls);


        listing.setSuccessTime(aliexpressListingModel.getGmtCreate());
        listing.setPublishTime(aliexpressListingModel.getGmtCreate());
        listing.setOnlineTime(aliexpressListingModel.getGmtModified());
        listing.setEndTimes(aliexpressListingModel.getWsOfflineDate());
        listing.setItemId(aliexpressListingModel.getProductId());
        listing.setSellerId(sellerId.toString());
        listing.setCreateId(userId);
        listing.setCreateName(userName);



        listing.setPlAccount(userName);
        //product_status_type	String	onSelling	产品的状态，包括onSelling（正在销售），offline（已下架），auditing（审核中），editingRequired（审核不通过）
        Integer status=AliexpressEnum.AliexpressStatusEnum.PUBLISH_FAILED.getCode();
        if("onSelling".equals(aliexpressListingModel.getProductStatusType())){
            status = AliexpressEnum.AliexpressStatusEnum.SALE.getCode();
        }else if("offline".equals(aliexpressListingModel.getProductStatusType())){
            status = AliexpressEnum.AliexpressStatusEnum.END.getCode();
        }else if("editingRequired".equals(aliexpressListingModel.getProductStatusType())){
            status = AliexpressEnum.AliexpressStatusEnum.AUDIT_FAILED.getCode();
        }else if("auditing".equals(aliexpressListingModel.getProductStatusType())){
            status = AliexpressEnum.AliexpressStatusEnum.AUDIT.getCode();
        }
        listing.setPublishStatus(status);
        return listing;
    }

    private List<AliexpressProductCountryPrice> setAliexpressProductCountryPrice(AliexpressAeopNationalQuoteConfiguration quoteConfiguration){
        List<AliexpressProductCountryPrice> listAliexpressProductCountryPrice = Lists.newArrayList();
        //	分国家定价规则类型[absolute: 为每个SKU直接设置价格绝对值；percentage：基于基准价格按比例配置; relative:相对原价涨或跌多少;

        String configurationType = quoteConfiguration.getConfigurationType();
        //调整比例为整数
        //:[{"shiptoCountry":"US","percentage":"5"},{"shiptoCountry":"RU","percentage":"-2"}]
        if(quoteConfiguration.getConfigurationData()!=null) {
            JSONArray dataArray = JSONObject.parseArray(quoteConfiguration.getConfigurationData());
            if ("percentage".equals(configurationType)) {
                if (dataArray!=null && dataArray.size() > 0) {
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONObject obj = dataArray.getJSONObject(i);
                        Object shiptoCountry = obj.get("shiptoCountry");
                        Object percentage = obj.get("percentage");
                        if(shiptoCountry!=null && percentage!=null){
                            AliexpressProductCountryPrice productCountryPrice = new AliexpressProductCountryPrice();
                            productCountryPrice.setCountryName(shiptoCountry.toString());
                            Double doupricingRange = Double.valueOf(percentage.toString());
                            if(doupricingRange>0){
                                productCountryPrice.setSignBit("+");
                            }else{
                                productCountryPrice.setSignBit("-");
                            }
                            BigDecimal pricingRange =  BigDecimal.valueOf(doupricingRange);
                            productCountryPrice.setPricingRange(pricingRange);
                            listAliexpressProductCountryPrice.add(productCountryPrice);
                        }else{
                            Object absoluteQuoteMap = obj.get("absoluteQuoteMap");
                            if(absoluteQuoteMap!=null){
                                JSONObject jsonObject = JSONObject.parseObject(absoluteQuoteMap.toString());
                                if(jsonObject!=null){
                                    for(Map.Entry<String, Object> jsonMap:jsonObject.entrySet()){
                                        AliexpressProductCountryPrice productCountryPrice = new AliexpressProductCountryPrice();
                                        productCountryPrice.setCountryName(shiptoCountry.toString());
                                        productCountryPrice.setPricingRange(new BigDecimal(jsonMap.getValue().toString()));
                                        productCountryPrice.setSmtSkuId(jsonMap.getKey());
                                        if(productCountryPrice.getPricingRange().doubleValue()>0){
                                            productCountryPrice.setSignBit("+");
                                        }else{
                                            productCountryPrice.setSignBit("-");
                                        }
                                        listAliexpressProductCountryPrice.add(productCountryPrice);
                                    }
                                }
                            }
                        }
                    }
                }
            } else if ("relative".equals(configurationType)) {//相对原价涨或跌多少
                //:[{"shiptoCountry":"US","relative":"5"},{"shiptoCountry":"RU","relative":"-2"}]
                if (dataArray!=null && dataArray.size() > 0) {
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONObject obj = dataArray.getJSONObject(i);
                        Object shiptoCountry = obj.get("shiptoCountry");
                        Object relative = obj.get("relative");
                        if(shiptoCountry!=null && relative!=null){
                            AliexpressProductCountryPrice productCountryPrice = new AliexpressProductCountryPrice();
                            productCountryPrice.setCountryName(shiptoCountry.toString());
                            Double doupricingRange = Double.valueOf(relative.toString());
                            if(doupricingRange>0){
                                productCountryPrice.setSignBit("+");
                            }else{
                                productCountryPrice.setSignBit("-");
                            }
                            BigDecimal pricingRange =  BigDecimal.valueOf(doupricingRange);
                            productCountryPrice.setPricingRange(pricingRange);
                            listAliexpressProductCountryPrice.add(productCountryPrice);
                        }else{
                            Object absoluteQuoteMap = obj.get("absoluteQuoteMap");
                            if(absoluteQuoteMap!=null){
                                JSONObject jsonObject = JSONObject.parseObject(absoluteQuoteMap.toString());
                                if(jsonObject!=null){
                                    for(Map.Entry<String, Object> jsonMap:jsonObject.entrySet()){
                                        AliexpressProductCountryPrice productCountryPrice = new AliexpressProductCountryPrice();
                                        productCountryPrice.setCountryName(shiptoCountry.toString());
                                        productCountryPrice.setPricingRange(new BigDecimal(jsonMap.getValue().toString()));
                                        productCountryPrice.setSmtSkuId(jsonMap.getKey());
                                        if(productCountryPrice.getPricingRange().doubleValue()>0){
                                            productCountryPrice.setSignBit("+");
                                        }else{
                                            productCountryPrice.setSignBit("-");
                                        }
                                        listAliexpressProductCountryPrice.add(productCountryPrice);
                                    }
                                }
                            }
                        }
                    }
                }
            } else if ("absolute".equals(configurationType)) {//直接设置价格
//              [
//               {"absoluteQuoteMap":{"14:193":1.0,"14:192":1.0},"shiptoCountry":"RU"},
//                {"absoluteQuoteMap":{"14:193":2.0},"shiptoCountry":"US"}
//              ]
                if (dataArray!=null && dataArray.size() > 0) {
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONObject obj = dataArray.getJSONObject(i);
                        Object shiptoCountry = obj.get("shiptoCountry");
                        Object absoluteQuoteMap = obj.get("absoluteQuoteMap");
                        if(absoluteQuoteMap!=null){
                            JSONObject jsonObject = JSONObject.parseObject(absoluteQuoteMap.toString());
                            if(jsonObject!=null){
                                for(Map.Entry<String, Object> jsonMap:jsonObject.entrySet()){
                                    AliexpressProductCountryPrice productCountryPrice = new AliexpressProductCountryPrice();
                                    productCountryPrice.setCountryName(shiptoCountry.toString());
                                    productCountryPrice.setPricingRange(new BigDecimal(jsonMap.getValue().toString()));
                                    productCountryPrice.setSmtSkuId(jsonMap.getKey());
                                    listAliexpressProductCountryPrice.add(productCountryPrice);
                                }
                            }
                        }
                    }
                }
            }
        }
        return listAliexpressProductCountryPrice;
    }

    private AliexpressPublishListingDetail setAliexpressPublishListingDetail(AliexpressListingModel aliexpressListingModel){
        AliexpressPublishListingDetail detail = new AliexpressPublishListingDetail();

        detail.setProductDetails(aliexpressListingModel.getDetail());

        if(aliexpressListingModel.getMobileDetail()!=null){
            detail.setMobileTerminal(true);
            AliexpressMobileDetail result = JSONObject.parseObject(aliexpressListingModel.getMobileDetail(), AliexpressMobileDetail.class);
            StringBuffer mobileDetail = new StringBuffer();
            if(result!=null && result.getMobileDetail()!=null){
                for(AliexpressMobileDetailContent content:result.getMobileDetail()){
                    if("image".equals(content.getType())){
                        if(content.getImages()!=null){
                            for(AliexpressMobileDetailContentImg img:content.getImages()){
                                mobileDetail.append(img.getImgUrl());
                            }
                        }
                    }else {
                        mobileDetail.append(content.getContent());
                    }
                }
            }

            detail.setMobileRemark(mobileDetail.toString());
        }else{
            detail.setMobileTerminal(false);
        }

        boolean wholesale = false;
        if(aliexpressListingModel.getBulkOrder()!=null) {
            //购买数量
            detail.setBulkOrder(aliexpressListingModel.getBulkOrder().intValue());
            //价格基础上减免折扣
            detail.setBulkDiscount(aliexpressListingModel.getBulkDiscount().intValue());
            wholesale = true;
        }
        //批发价
        detail.setWholesale(wholesale);

        //库存扣减策略，总共有2种：下单减库存(place_order_withhold)和支付减库存(payment_success_deduct)。
        detail.setReduceStrategy(aliexpressListingModel.getReduceStrategy());
        //发货期(取值范围:1-60;单位:天)
        if(aliexpressListingModel.getDeliveryTime()!=null)
            detail.setDeliveryTime(aliexpressListingModel.getDeliveryTime().intValue());
        //最小计量单位
        if(aliexpressListingModel.getProductUnit()!=null)
            detail.setUnit(aliexpressListingModel.getProductUnit().intValue());

        //销售方式 1按件出售 2打包出售（价格按照包计算）
        if(aliexpressListingModel.getPackageType()){
            detail.setSalesMethod(2);
        }else {
            detail.setSalesMethod(1);
        }
        //每包件数。 打包销售情况，lotNum>1,非打包销售情况,lotNum=1
        if(aliexpressListingModel.getLotNum()!=null)
            detail.setLotNum(aliexpressListingModel.getLotNum().intValue());

        //商品包装后的重量(公斤/袋)
        if(aliexpressListingModel.getPackageWidth()!=null)
            detail.setPackagingWeight(new BigDecimal(aliexpressListingModel.getPackageWidth()));

        //是否自定义计重.true为自定义计重,false反之
        boolean isPackSell=false;
        detail.setIsPackSell(isPackSell);
        if(aliexpressListingModel.getBaseUnit()!=null) {
            //买家购买
            detail.setBuyersPurchase(aliexpressListingModel.getBaseUnit().intValue());
            //买家每多买
            detail.setBuyersMore(aliexpressListingModel.getAddUnit().intValue());
            //重量增加
            detail.setIncreaseWeight(new BigDecimal(aliexpressListingModel.getAddWeight()));
        }

        //长
        if(aliexpressListingModel.getPackageLength()!=null)
            detail.setPackageLength(aliexpressListingModel.getPackageLength().intValue());

        //宽
        if(aliexpressListingModel.getPackageWidth()!=null)
            detail.setPackageWidth(aliexpressListingModel.getPackageWidth().intValue());

        //高
        if(aliexpressListingModel.getPackageHeight()!=null)
            detail.setPackageHeight(aliexpressListingModel.getPackageHeight().intValue());

        //运费模板
        detail.setFreightTemplateId(aliexpressListingModel.getFreightTemplateId());

        //服务模板
        detail.setPromiseTemplateId(aliexpressListingModel.getPromiseTemplateId());

        //商品分组
        detail.setGroupId(aliexpressListingModel.getGroupId());

        //商品有效天数
        detail.setWsValidNum(aliexpressListingModel.getWsValidNum().intValue());

        return detail;
    }

    private int insertAliexpressPublishListingProduct(AliexpressListingModel aliexpressListingModel,Long listingId,Date date,Empower empower,Integer platformListing){
        boolean bool = false;
        String plSpu ="";
        for (AliexpressAeopAeProductSku skuModel:aliexpressListingModel.getAeopAeProductSKUs()) {
            AliexpressPublishListingProduct product = new AliexpressPublishListingProduct();
            product.setPublishListingId(listingId);
            product.setCreateTime(date);
            product.setSmtSkuId(skuModel.getId());
            //平台sku
            product.setPlatformSku(skuModel.getSkuCode());
            //库存
            product.setInventory(skuModel.getIpmSkuStock().intValue());
            //零售价
            String  skuPrice = skuModel.getSkuPrice()==null?"0":skuModel.getSkuPrice();
            String skuMaps = remoteCommodityService.getSkuMapByPlatformSku(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform(), empower.getEmpowerId().toString(),skuModel.getSkuCode());
            List<AliexpressPublishListingProductSkus> listProductSkus = Lists.newArrayList();
            if(skuMaps!=null) {
                String result = Utils.returnRemoteResultDataString(skuMaps, "转换失败");
                JSONObject json = JSONObject.parseObject(result);
                if (json != null){
                    JSONArray skuList = json.getJSONArray("skuList");
                    if(skuList!=null && skuList.size()>0) {
                        bool = true;
                        for (int i = 0; i < skuList.size(); i++) {
                            JSONObject obj = skuList.getJSONObject(i);
                            String systemSku = obj.getString("systemSku");
//                            String specValueEn = obj.getString("specValueEn");
//                            String specValueCn = obj.getString("specValueCn");
//                            String commodityNameCn = obj.getString("commodityNameCn");
//                            String commodityNameEn = obj.getString("commodityNameEn");
                            Integer skuNum = obj.getInteger("skuNum");
                            if(skuNum!=null && skuNum>1) {
                                AliexpressPublishListingProductSkus productSkus = new AliexpressPublishListingProductSkus();
                                productSkus.setPlSku(systemSku);
                                productSkus.setPlSkuNumber(skuNum);
                                listProductSkus.add(productSkus);
                            }
                            if(i==0){//第一个sku为主sku
                                product.setPlSku(systemSku);
                                plSpu = obj.getString("spu");
                            }
                        }
                    }
                }
            }

            product.setRetailPrice(BigDecimal.valueOf(Double.valueOf(skuPrice)));


            String productImage="";
            List<AliexpressPublishListingSkuProperty> listSkuProperty = Lists.newArrayList();
            int i=1;
            if(skuModel.getAeopSKUPropertyList()!=null) {
                for (AliexpressAeopSkuProperty skuProperty : skuModel.getAeopSKUPropertyList()) {
                    AliexpressPublishListingSkuProperty property = new AliexpressPublishListingSkuProperty();
                    if (skuProperty.getSkuPropertyId() != null) {
                        property.setAttributeId(skuProperty.getSkuPropertyId().toString());
                    }
                    if (skuProperty.getPropertyValueId() != null) {
                        property.setSelectId(skuProperty.getPropertyValueId().toString());
                    }
                    property.setSelectAlias(skuProperty.getPropertyValueDefinitionName());
                    if (StringUtils.isNotBlank(skuProperty.getSkuImage())) {
                        productImage = skuProperty.getSkuImage();
                    }
                    property.setSort(i);
                    i++;
                    listSkuProperty.add(property);
                }
            }
            //图片
            product.setProductImage(productImage);
            aliexpressPublishListingProductMapper.insertSelective(product);
            for(AliexpressPublishListingSkuProperty skuProperty:listSkuProperty){
                skuProperty.setPublishListingId(listingId);
                skuProperty.setProductId(product.getId());
                aliexpressPublishListingSkuPropertyMapper.insertSelective(skuProperty);
            }
            //捆绑sku
            if(listProductSkus.size()>0){
                listProductSkus.forEach(productSkus->{
                    productSkus.setPublishListingId(listingId);
                    productSkus.setProductId(product.getId());
                    aliexpressPublishListingProductSkusMapper.insertSelective(productSkus);
                });

            }

        }
        //如果商品有映射则改成我们平台刊登的数据
        if(bool){
            AliexpressPublishListing aliexpressPublishListing = new AliexpressPublishListing();
            aliexpressPublishListing.setId(listingId);
            if(2==platformListing) {
                aliexpressPublishListing.setPlatformListing(1);
            }
            aliexpressPublishListing.setPlSpu(plSpu);
            aliexpressPublishListingMapper.updateByPrimaryKeySelective(aliexpressPublishListing);
        }
        return 1;
    }
    @Override
    public Long syncAliexpressPListingProductStatus(Long empowerId,Long sellerId,Long userId,String userName){
        List<String> listProductStatus=Lists.newArrayList();
        //商品业务状态，目前提供5种，输入参数分别是：上架:onSelling ；下架:offline ；审核中:auditing ；审核不通过:editingRequired；客服删除:service_delete
        listProductStatus.add("onSelling");
        listProductStatus.add("offline");
        listProductStatus.add("auditing");
        listProductStatus.add("editingRequired");
        Long productCount = 0L;
        for (String productStatus:listProductStatus){
            Long count = this.syncAliexpressPListing(empowerId,sellerId,productStatus,userId,userName);
            productCount = productCount+count;
        }
        return productCount;
    }

    private Long syncAliexpressPListing(Long empowerId,Long sellerId,String productStatus,Long userId,String userName){
        Long productCount = 0L;
        Map<String,Object> map = Maps.newHashMap();
        String token = aliexpressPhotoBankService.getEmpowerById(empowerId);
        map.put("sessionKey",token);
        map.put("pageSize",100L);
        map.put("currentPage",1L);
        map.put("productStatusType",productStatus);
        String json = httpTaoBaoApi.getTaoBaoApi(AliexpressMethodNameEnum.FINDPRODUCTPAGE.getCode(),map);
        Map<String,Object> retmap = JsonAnalysis.getGatewayMsg(json);
        String success = retmap.get("success").toString();

        if("200".equals(success)){
            AliexpressProductListModel model = JSONObject.parseObject(retmap.get("data").toString(), AliexpressProductListModel.class);

            if(model!=null) {
                if(model.getAeopAEProductDisplayDTOList()!=null){
                    for(AliexpressProductModel aliexpressProductModel : model.getAeopAEProductDisplayDTOList()){
                        AliexpressPhotoModel photoModelmodel = new AliexpressPhotoModel();
                        photoModelmodel.setEmpowerId(empowerId);
                        photoModelmodel.setProductStatusType(productStatus);
                        photoModelmodel.setSellerId(sellerId);
                        photoModelmodel.setToken(token);
                        photoModelmodel.setUserId(userId);
                        photoModelmodel.setUserName(userName);
                        photoModelmodel.setProductMaxPrice(aliexpressProductModel.getProductMinPrice());
                        photoModelmodel.setProductMinPrice(aliexpressProductModel.getProductMaxPrice());
                        photoModelmodel.setItemId(aliexpressProductModel.getProductId());
                        sender.sendListing(photoModelmodel);
                    }
                }
                productCount = model.getProductCount();
                if(model.getTotalPage()>1) {
                    for (long i = 2; i <= model.getTotalPage(); i++) {
                        AliexpressPhotoModel photoModelmodel = new AliexpressPhotoModel();
                        photoModelmodel.setToken(token);
                        photoModelmodel.setPageSize(100L);
                        photoModelmodel.setCurrentPage(i);
                        photoModelmodel.setProductStatusType(productStatus);

                        photoModelmodel.setEmpowerId(empowerId);
                        photoModelmodel.setSellerId(sellerId);
                        photoModelmodel.setUserId(userId);
                        photoModelmodel.setUserName(userName);
                        photoModelmodel.setType(3);
                        sender.sendListing(photoModelmodel);
                    }
                }
            }
        }
        return productCount;
    }

}
