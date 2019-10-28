package com.rondaful.cloud.seller.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.security.UserSession;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.common.task.ProcessXmlDraftTask;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.AmazonTemplateRule;
import com.rondaful.cloud.seller.entity.AmazonTemplateSiteMapping;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.amazon.AmazonPublishListStatus;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.seller.enums.AmazonTemplateAttributeEnum;
import com.rondaful.cloud.seller.enums.AmazonTemplateEnums;
import com.rondaful.cloud.seller.enums.PublishLogEnum;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.generated.ProductImage;
import com.rondaful.cloud.seller.remote.RemoteLogisticsService;
import com.rondaful.cloud.seller.remote.RemoteOrderRuleService;
import com.rondaful.cloud.seller.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class AmazonBatchCopyUtils {

    private Logger logger = LoggerFactory.getLogger(AmazonBatchCopyUtils.class);


    @Autowired
    private AmazonTemplateRuleService amazonTemplateRuleService;

    @Autowired
    private RemoteLogisticsService remoteLogisticsService;

    @Autowired
    private AmazonTemplateSiteMappingService amazonTemplateSiteMappingService;

    @Autowired
    private RemoteOrderRuleService remoteOrderRuleService;

    @Autowired
    private AmazonTemplateUtils amazonTemplateUtils;

    @Autowired
    private AmazonPublishListingService amazonPublishListingService;

    @Autowired
    private PublishLogService publishLogService;

    @Autowired
    private AmazonSubListingUtil amazonSubListingUtil;

    @Autowired
    private AmazonPublishSubListingService amazonPublishSubListingService;

    public static List<String> eurMarketplaces = Arrays.asList(
            "A1PA6795UKMFR9",  //德国
            "A1RKKUPIHCS9HS", // 西班牙
            "A13V1IB3VIYZZH",//法国
            "A1F83G8C2ARO7P", //英国
            "APJ6JRA9NG5V4" // 意大利
    );


    public void copyMore(AmazonPublishListing listing, List<Empower> empowers, AmazonTemplateRule defaultTemplate, JSONObject freightTrial, JSONObject commodity,String SPU) {
        Date date = new Date();
        Map<String, MarketplaceId> marketplaceForKeyId = MarketplaceIdList.createMarketplaceForKeyId();
        MarketplaceId listingMarket = MarketplaceIdList.createMarketplace().get(listing.getPublishSite());

        try {
            AmazonTemplateRule thisDefaultTem = null;
            ArrayList<AmazonPublishListing> copyList = new ArrayList<>();
            for (Empower empower : empowers) {
                List<AmazonTemplateRule> byEmpowerIdAndDefaultTemplate = amazonTemplateRuleService.getByEmpowerIdAndDefaultTemplate(empower.getThirdPartyName(), AmazonTemplateEnums.DefaultTemplate.DEFAULT.getType());
                if (byEmpowerIdAndDefaultTemplate != null && byEmpowerIdAndDefaultTemplate.size() > 0) {
                    thisDefaultTem = byEmpowerIdAndDefaultTemplate.get(0);
                } else {
                    thisDefaultTem = defaultTemplate;
                }

                MarketplaceId marketplaceId = marketplaceForKeyId.get(empower.getWebName());
                String logisticsCode = null;
                JSONObject skuCost = null;
                if (freightTrial != null) {
                    try {
                        freightTrial.put("countryCode", marketplaceId.getCountryCode());
                        String freightTrialByType = remoteLogisticsService.getFreightTrialByType(freightTrial);
                        String data = Utils.returnRemoteResultDataString(freightTrialByType, "获取预估物流费用异常");
                        JSONObject object = JSONObject.parseObject(data);
                        logisticsCode = object.getString("logisticsCode");
                        skuCost = object.getJSONObject("skuCost");
                    } catch (Exception e) {
                        logger.error("获取预估物流费异常", e);
                    }
                }

                AmazonPublishListing copyListing = new AmazonPublishListing();
                copyListing.setPlAccount(empower.getPinlianAccount());
                copyListing.setTitle(listing.getTitle());
                copyListing.setPlSku(listing.getPlSku());
                copyListing.setPlatformSku(amazonTemplateUtils.getPlatformSku(thisDefaultTem.getPlatformSkuRule(),String.valueOf(empower.getEmpowerId()),SPU,listing.getPlSku(),thisDefaultTem.getId()));
                copyListing.setPublishSite(marketplaceId.getCountryCode());
                copyListing.setPublishAccount(empower.getAccount());
                copyListing.setPublishType(listing.getPublishType());
                copyListing.setPublishStatus(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT);
                copyListing.setCreateTime(date);
                copyListing.setUpdateTime(date);
                copyListing.setBatchNo(UUID.randomUUID().toString().replaceAll("-", ""));
                copyListing.setRemark(listing.getRemark() + " copy from listingId: " + listing.getId());
                copyListing.setExt(listing.getExt());
                copyListing.setAmwToken(empower.getToken());
                copyListing.setMerchantIdentifier(empower.getThirdPartyName());
                copyListing.setHasRequired(listing.getHasRequired());
                copyListing.setLogisticsType(listing.getLogisticsType());
                copyListing.setWarehouseId(listing.getWarehouseId());
                copyListing.setLogisticsCode(logisticsCode);
                copyListing.setSaleUserId(listing.getSaleUserId());
                if (StringUtils.isNotBlank(listing.getPublishMessage())) {
                    AmazonRequestProduct<?> sourcePro = JSONObject.parseObject(listing.getPublishMessage(), AmazonRequestProduct.class);
                    AmazonRequestProduct toPro = new AmazonRequestProduct();
                    toPro.setHasRequired(sourcePro.getHasRequired());
                    toPro.setSku(copyListing.getPlatformSku());
                    toPro.setTempalteRuleId(thisDefaultTem.getId());
                    toPro.setStandardProductID(null);
                    toPro.setStandardProductType(null);
                    toPro.setIsMultiattribute(sourcePro.getIsMultiattribute());
                    toPro.setBrand(amazonTemplateUtils.getBrand(thisDefaultTem.getBrandRule(),empower.getAccount(),commodity.getString("brandName")));
                    toPro.setPlSku(sourcePro.getPlSku());
                    toPro.setPlSkuSaleNum(sourcePro.getPlSkuSaleNum());
                    toPro.setItemWeight(sourcePro.getItemWeight());
                    toPro.setPackageWeight(sourcePro.getPackageWeight());
                    toPro.setWeightUnitOfMeasure(sourcePro.getWeightUnitOfMeasure());
                    toPro.setDimensionHeight(sourcePro.getDimensionHeight());
                    toPro.setDimensionWidth(sourcePro.getDimensionWidth());
                    toPro.setDimensionLength(sourcePro.getDimensionLength());
                    toPro.setBatchNo(copyListing.getBatchNo());
                    toPro.setCountryCode(empower.getWebName());
                    toPro.setSaleUserId(sourcePro.getSaleUserId());

                    List<JSONObject> commoditySpecList = getCommoditySpecList(commodity);
                    amazonTemplateUtils.setProductTitle(toPro, thisDefaultTem.getProductTitleRule(), amazonTemplateUtils.getFirstMessage(commoditySpecList, "commodityNameEn"));
                    amazonTemplateUtils.setDescription(toPro, thisDefaultTem.getDescriptionRule(), amazonTemplateUtils.createStrengths(commodity, 5), amazonTemplateUtils.spliteMeg(commodity.getString("commodityDesc")), amazonTemplateUtils.spliteMeg(commodity.getString("packingList")));
                    copyListing.setTitle(toPro.getTitle());
                    toPro.setQuantity(sourcePro.getQuantity());
                    //toPro.setCountryCode(marketplaceId.getCountryCode());
                    toPro.setMerchantIdentifier(empower.getThirdPartyName());
                    amazonTemplateUtils.setBulletPoint(toPro, null, amazonTemplateUtils.createStrengths(commodity, 5));
                    amazonTemplateUtils.setFirstCategory(toPro, sourcePro.getSpu(), marketplaceId.getCountryCode(),null,null);
                    if (StringUtils.isNotBlank(sourcePro.getProductCategory2Path())) {
                        amazonTemplateUtils.setSecondCategory(toPro, sourcePro.getSpu(), marketplaceId.getCountryCode(), thisDefaultTem.getCategorySecondRule());
                    }

                    AmazonTemplateSiteMapping mapp = new AmazonTemplateSiteMapping();
                    mapp.setSite(marketplaceId.getCountryCode().equalsIgnoreCase("GB")?"UK":marketplaceId.getCountryCode());
                    mapp.setIsDisabled(AmazonTemplateAttributeEnum.IsDisabledE.OK.getCode());
                    mapp.setTemplateParent(sourcePro.getTemplatesName());
                    List<AmazonTemplateSiteMapping> allByList = amazonTemplateSiteMappingService.findAllByList(mapp);
                    if (allByList != null && allByList.size() > 0) {
                        toPro.setTemplatesName(sourcePro.getTemplatesName());
                    }else {
                        toPro.setTemplatesName(null);
                    }
                    mapp.setTemplateChild(sourcePro.getTemplatesName2());
                    allByList = amazonTemplateSiteMappingService.findAllByList(mapp);
                    if (allByList != null && allByList.size() > 0) {
                        toPro.setTemplatesName2(sourcePro.getTemplatesName2());
                    }else {
                        toPro.setTemplatesName2(null);
                    }

                    toPro.setCategoryPropertyJson(sourcePro.getCategoryPropertyJson());
                    toPro.setConditionInfo(sourcePro.getConditionInfo());
                    //toPro.setDesigner();
                    toPro.setDimensionUnitOfMeasure(sourcePro.getDimensionUnitOfMeasure());
                    toPro.setExt(sourcePro.getExt());
                    amazonTemplateUtils.setFulfillmentLatency(toPro,thisDefaultTem.getFulfillmentLatency());
                    //toPro.setHasRequired();
                    //toPro.setHazmatUnitedNationsRegulatoryID();
                    toPro.setItemPackageQuantity(sourcePro.getItemPackageQuantity());
                    toPro.setLogisticsCode(logisticsCode);
                    toPro.setLogisticsType(listing.getLogisticsType());
                    toPro.setWarehouseId(listing.getWarehouseId());
                    //toPro.setManufacturer(sourcePro.getManufacturer());
                    amazonTemplateUtils.setManufacturer(toPro,thisDefaultTem.getManufacturerRule(),empower.getAccount(),commodity.getString("producer"));
                    amazonTemplateUtils.setPartNumber(toPro, thisDefaultTem.getPartNumber());
                    toPro.setNumberOfItems(sourcePro.getNumberOfItems());
                    toPro.setPlSkuCount(sourcePro.getPlSkuCount());
                    toPro.setPlSkuStatus(sourcePro.getPlSkuStatus());
                    amazonTemplateUtils.setSearchTerms(toPro, null, amazonTemplateUtils.spliteMeg(commodity.getString("searchKeywords")));

                    toPro.setSpu(sourcePro.getSpu());
                    toPro.setSupplierDeclaredDGHZRegulation(sourcePro.getSupplierDeclaredDGHZRegulation());
                    toPro.setTargetAudience(sourcePro.getTargetAudience());
                    toPro.setCountryCode(marketplaceId.getCountryCode());
                    toPro.setStandardPriceUnit(marketplaceId.getCurrency());

                    if (sourcePro.getVarRequestProductList() != null && sourcePro.getVarRequestProductList().size() > 0) {
                        ArrayList<String> spuMasterImage = amazonTemplateUtils.getSpuMasterImage(commodity);
                        ArrayList<String> spuAdditionImage = amazonTemplateUtils.getSpuAdditionImage(commodity);
                        ArrayList<String> spuImage = amazonTemplateUtils.getSpuImage(spuMasterImage, spuAdditionImage);
                        ArrayList<String> skuMasterImages = amazonTemplateUtils.getSkuMasterImages(commoditySpecList);
                        ArrayList<String> skuAdditionalImage = amazonTemplateUtils.getSkuAdditionalImage(commoditySpecList);
                        amazonTemplateUtils.setParentMainImage(toPro, thisDefaultTem.getParentMainImageRule(), spuImage, skuMasterImages, skuAdditionalImage);
                        List<ProductImage> images = toPro.getImages();
                        copyListing.setProductImage(images.get(0).getImageLocation());

                        ArrayList<AmazonRequestProduct> amazonRequestProducts = new ArrayList<>();
                        AmazonRequestProduct subTo;
                        for (AmazonRequestProduct subSource : sourcePro.getVarRequestProductList()) {
                            subTo = new AmazonRequestProduct();
                            JSONObject rightCommoditySpec = getRightCommoditySpec(subSource.getPlSku(), commodity);
                            if(rightCommoditySpec == null)
                                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600,"找不到对应sku的商品");
                            // todo 子体复制
                            subTo.setStandardProductType(null);
                            subTo.setStandardProductID(null);
                            subTo.setPlSku(subSource.getPlSku());
                            subTo.setPlSkuSaleNum(subSource.getPlSkuSaleNum());
                            String systemSku = null;
                            if(rightCommoditySpec != null)
                                systemSku = rightCommoditySpec.getString("systemSku");
                            subTo.setSku(amazonTemplateUtils.getPlatformSku(thisDefaultTem.getPlatformSkuRule(), String.valueOf(empower.getEmpowerId()), commodity.getString("SPU"),systemSku , thisDefaultTem.getId()));


                            ArrayList<String> skuAdditionalImage2 = amazonTemplateUtils.getSkuAdditionalImage2(rightCommoditySpec);
                            ArrayList<String> skuMasterImages2 = amazonTemplateUtils.getSkuMasterImages2(rightCommoditySpec);
                            skuMasterImages2 = amazonTemplateUtils.setChildMainImage(subTo, thisDefaultTem.getChildMainImageRule(), skuMasterImages2);                           //todo 问产品
                            skuAdditionalImage2.addAll(skuMasterImages2);
                            amazonTemplateUtils.setChildAdditionImage(subTo, thisDefaultTem.getChildAdditionImageRule(), skuAdditionalImage2);

                            subTo.setQuantity(subSource.getQuantity());
                            setPrice(marketplaceId, subTo, subSource, skuCost, thisDefaultTem, commodity);

                            //amazonTemplateUtils.setProductTitle(subTo, thisDefaultTem.getProductTitleRule(), rightCommoditySpec.getString("commodityNameEn"));

                            subTo.setCategoryPropertyJson(subSource.getCategoryPropertyJson());
                            amazonTemplateUtils.setPartNumber(subTo, thisDefaultTem.getPartNumber());
                            subTo.setTitle(toPro.getTitle());

                            amazonRequestProducts.add(subTo);
                        }
                        toPro.setVarRequestProductList(amazonRequestProducts);
                    } else {
                        setPrice(marketplaceId, toPro, sourcePro, skuCost, thisDefaultTem, commodity);

                        JSONObject rightCommoditySpec = getRightCommoditySpec(toPro.getPlSku(), commodity);

                        ArrayList<String> skuAdditionalImage2 = amazonTemplateUtils.getSkuAdditionalImage2(rightCommoditySpec);
                        ArrayList<String> skuMasterImages2 = amazonTemplateUtils.getSkuMasterImages2(rightCommoditySpec);

                        skuMasterImages2 = amazonTemplateUtils.setChildMainImage(toPro,thisDefaultTem.getChildMainImageRule(), skuMasterImages2);     //todo 问产品
                        skuAdditionalImage2.addAll(skuMasterImages2);
                        amazonTemplateUtils.setChildAdditionImage(toPro, thisDefaultTem.getChildAdditionImageRule(), skuAdditionalImage2);
                        List<ProductImage> images = toPro.getImages();
                        copyListing.setProductImage(images.get(0).getImageLocation());
                    }
                    copyListing.setPublishMessage( messageToString(toPro,"批量复制生成publishMessage异常"));
                }
                copyList.add(copyListing);
            }
            saveToList(copyList);
        } catch (GlobalException e) {
            logger.error("多账号复制失败1", e);
            throw e;
        } catch (Exception e) {
            logger.error("多账号复制失败2", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601, "复制异常");
        }
    }

    public void copyShire(AmazonPublishListing listing, List<Empower> empowers, AmazonTemplateRule defaultTemplate, JSONObject freightTrial, JSONObject commodity) {
        Date date = new Date();
        MarketplaceId listingMarket = MarketplaceIdList.createMarketplace().get(listing.getPublishSite());
        for (Empower empower : empowers) {
            if (!eurMarketplaces.contains(empower.getWebName())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "目标店铺存在非欧洲站点");
            }
            if(empower.getWebName().equalsIgnoreCase(listingMarket.getMarketplaceId())){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "目标店铺存在原站点");
            }
        }
        Map<String, MarketplaceId> marketplaceForKeyId = MarketplaceIdList.createMarketplaceForKeyId();
        try {
            AmazonTemplateRule thisDefaultTem = null;
            ArrayList<AmazonPublishListing> copuList = new ArrayList<>();
            for (Empower empower : empowers) {
                List<AmazonTemplateRule> byEmpowerIdAndDefaultTemplate = amazonTemplateRuleService.getByEmpowerIdAndDefaultTemplate(empower.getThirdPartyName(), AmazonTemplateEnums.DefaultTemplate.DEFAULT.getType());
                if (byEmpowerIdAndDefaultTemplate != null && byEmpowerIdAndDefaultTemplate.size() > 0) {
                    thisDefaultTem = byEmpowerIdAndDefaultTemplate.get(0);
                } else {
                    thisDefaultTem = defaultTemplate;
                }

                MarketplaceId marketplaceId = marketplaceForKeyId.get(empower.getWebName());
                String logisticsCode = null;
               JSONObject skuCost = null;
                if (freightTrial != null) {
                    try {
                        freightTrial.put("countryCode", marketplaceId.getCountryCode());
                        String freightTrialByType = remoteLogisticsService.getFreightTrialByType(freightTrial);
                        String p = freightTrial.toJSONString();
                        String data = Utils.returnRemoteResultDataString(freightTrialByType, "获取预估物流费用异常");
                        JSONObject object = JSONObject.parseObject(data);
                        logisticsCode = object.getString("logisticsCode");
                        skuCost = object.getJSONObject("skuCost");
                    } catch (Exception e) {
                        logger.error("获取预估物流费异常", e);
                    }
                }

                AmazonPublishListing copyListing = new AmazonPublishListing();
                copyListing.setPlAccount(empower.getPinlianAccount());
                copyListing.setTitle(listing.getTitle());
                copyListing.setPlSku(listing.getPlSku());
                copyListing.setPlatformSku(listing.getPlatformSku());
                copyListing.setPublishSite(marketplaceId.getCountryCode());
                copyListing.setPublishAccount(empower.getAccount());
                copyListing.setPublishType(listing.getPublishType());
                copyListing.setPublishStatus(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT);
                copyListing.setCreateTime(date);
                copyListing.setUpdateTime(date);
                copyListing.setBatchNo(UUID.randomUUID().toString().replaceAll("-", ""));
                copyListing.setRemark(listing.getRemark() + " copy from listingId: " + listing.getId());
                copyListing.setExt(listing.getExt());
                copyListing.setProductImage(listing.getProductImage());
                copyListing.setAmwToken(empower.getToken());
                copyListing.setMerchantIdentifier(empower.getThirdPartyName());
                copyListing.setLogisticsType(listing.getLogisticsType());
                copyListing.setWarehouseId(listing.getWarehouseId());
                copyListing.setLogisticsCode(logisticsCode);
                copyListing.setSaleUserId(listing.getSaleUserId());
                if (StringUtils.isNotBlank(listing.getPublishMessage())) {
                    AmazonRequestProduct<?> sourcePro = JSONObject.parseObject(listing.getPublishMessage(), AmazonRequestProduct.class);
                    AmazonRequestProduct toPro = new AmazonRequestProduct();
                    toPro.setSku(sourcePro.getSku());
                    toPro.setHasRequired(sourcePro.getHasRequired());
                    toPro.setStandardProductID(sourcePro.getStandardProductID());
                    toPro.setStandardProductType(sourcePro.getStandardProductType());
                    toPro.setIsMultiattribute(sourcePro.getIsMultiattribute());
                    toPro.setImages(sourcePro.getImages());
                    toPro.setBrand(sourcePro.getBrand());
                    toPro.setPlSku(sourcePro.getPlSku());
                    toPro.setPlSkuSaleNum(sourcePro.getPlSkuSaleNum());
                    toPro.setItemWeight(sourcePro.getItemWeight());
                    toPro.setPackageWeight(sourcePro.getPackageWeight());
                    toPro.setWeightUnitOfMeasure(sourcePro.getWeightUnitOfMeasure());
                    toPro.setDimensionHeight(sourcePro.getDimensionHeight());
                    toPro.setDimensionWidth(sourcePro.getDimensionWidth());
                    toPro.setDimensionLength(sourcePro.getDimensionLength());
                    toPro.setBatchNo(copyListing.getBatchNo());
                    toPro.setDescription(sourcePro.getDescription());
                    toPro.setTitle(copyListing.getTitle());
                    toPro.setQuantity(sourcePro.getQuantity());
                    toPro.setCountryCode(marketplaceId.getCountryCode());
                    toPro.setMerchantIdentifier(empower.getThirdPartyName());
                    toPro.setSaleUserId(sourcePro.getSaleUserId());
                    List<String> bulletPoint = sourcePro.getBulletPoint();
                    toPro.setBulletPoint(sourcePro.getBulletPoint());
                    /*if (bulletPoint != null && bulletPoint.size() > 0) {
                        ArrayList<String> bullets = new ArrayList<>();
                        for (String str : bulletPoint) {
                            if (StringUtils.isNotBlank(str)) {
                                String[] split = str.split(",");
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < split.length; i++) {
                                    sb.append(Utils.translationTo(split[i], listingMarket.getBaiduLanguage(), marketplaceId.getBaiduLanguage()));
                                    if (i < split.length - 1) {
                                        sb.append(",");
                                    }
                                }
                                bullets.add(sb.toString());
                            }
                        }
                        toPro.setBulletPoint(bullets);
                    }*/
                    amazonTemplateUtils.setFirstCategory(toPro, sourcePro.getSpu(), marketplaceId.getCountryCode(),null,null);
                    if (StringUtils.isNotBlank(sourcePro.getProductCategory2Path())) {
                        amazonTemplateUtils.setSecondCategory(toPro, sourcePro.getSpu(), marketplaceId.getCountryCode(), thisDefaultTem.getCategorySecondRule());
                    }
                    if(StringUtils.isBlank(toPro.getProductCategory1Path()) || (toPro.getProductCategory() == null || toPro.getProductCategory()[0] == null || toPro.getProductCategory()[0] == 0)){
                        toPro.setHasRequired(1);
                    }

                    AmazonTemplateSiteMapping mapp = new AmazonTemplateSiteMapping();
                    mapp.setSite(marketplaceId.getCountryCode().equalsIgnoreCase("GB")?"UK":marketplaceId.getCountryCode());
                    mapp.setIsDisabled(AmazonTemplateAttributeEnum.IsDisabledE.OK.getCode());
                    mapp.setTemplateParent(sourcePro.getTemplatesName());
                    List<AmazonTemplateSiteMapping> allByList = amazonTemplateSiteMappingService.findAllByList(mapp);
                    if (allByList != null && allByList.size() > 0) {
                        toPro.setTemplatesName(sourcePro.getTemplatesName());
                    }else {
                        toPro.setTemplatesName(null);
                    }
                    mapp.setTemplateChild(sourcePro.getTemplatesName2());
                    allByList = amazonTemplateSiteMappingService.findAllByList(mapp);
                    if (allByList != null && allByList.size() > 0) {
                        toPro.setTemplatesName2(sourcePro.getTemplatesName2());
                    }else {
                        toPro.setTemplatesName2(null);
                    }
                    if(StringUtils.isBlank(toPro.getTemplatesName()) || StringUtils.isBlank(toPro.getTemplatesName2())){
                        toPro.setHasRequired(1);
                    }

                    toPro.setCategoryPropertyJson(sourcePro.getCategoryPropertyJson());
                    toPro.setConditionInfo(sourcePro.getConditionInfo());
                    toPro.setDimensionUnitOfMeasure(sourcePro.getDimensionUnitOfMeasure());
                    toPro.setExt(sourcePro.getExt());
                    toPro.setFulfillmentLatency(sourcePro.getFulfillmentLatency());
                    toPro.setItemPackageQuantity(sourcePro.getItemPackageQuantity());
                    toPro.setLogisticsCode(logisticsCode);
                    toPro.setLogisticsType(listing.getLogisticsType());
                    toPro.setWarehouseId(listing.getWarehouseId());
                    toPro.setManufacturer(sourcePro.getManufacturer());
                    toPro.setMfrPartNumber(sourcePro.getSku());
                    toPro.setNumberOfItems(sourcePro.getNumberOfItems());
                    toPro.setPlSkuCount(sourcePro.getPlSkuCount());
                    toPro.setPlSkuStatus(sourcePro.getPlSkuStatus());
                    toPro.setSearchTerms(sourcePro.getSearchTerms());
                    /*if (sourcePro.getSearchTerms() != null && sourcePro.getSearchTerms().size() > 0) {
                        ArrayList<String> strings = new ArrayList<>();
                        for (String sear : sourcePro.getSearchTerms()) {
                            strings.add(Utils.translationTo(sear, listingMarket.getBaiduLanguage(), marketplaceId.getBaiduLanguage()));
                        }
                        toPro.setSearchTerms(strings);
                    }*/
                    toPro.setSpu(sourcePro.getSpu());
                    toPro.setSupplierDeclaredDGHZRegulation(sourcePro.getSupplierDeclaredDGHZRegulation());
                    toPro.setTargetAudience(sourcePro.getTargetAudience());
                    toPro.setStandardPriceUnit(marketplaceId.getCurrency());

                    if (sourcePro.getVarRequestProductList() != null && sourcePro.getVarRequestProductList().size() > 0) {
                        ArrayList<AmazonRequestProduct> amazonRequestProducts = new ArrayList<>();
                        AmazonRequestProduct subTo;
                        for (AmazonRequestProduct subSource : sourcePro.getVarRequestProductList()) {
                            subTo = new AmazonRequestProduct();
                            // todo 子体复制
                            subTo.setStandardProductType(subSource.getStandardProductType());
                            subTo.setStandardProductID(subSource.getStandardProductID());
                            subTo.setImages(subSource.getImages());
                            subTo.setQuantity(subSource.getQuantity());
                            setPrice(marketplaceId, subTo, subSource, skuCost, thisDefaultTem, commodity);
                            if(subTo.getStandardPrice() == null)
                                toPro.setHasRequired(1);
                            subTo.setTitle(subSource.getTitle());
                            subTo.setCategoryPropertyJson(subSource.getCategoryPropertyJson());
                            subTo.setMfrPartNumber(subSource.getSku());
                            subTo.setSku(subSource.getSku());
                            subTo.setPlSku(subSource.getPlSku());
                            subTo.setPlSkuSaleNum(subSource.getPlSkuSaleNum());
                            amazonRequestProducts.add(subTo);
                        }
                        toPro.setVarRequestProductList(amazonRequestProducts);
                    } else {
                        setPrice(marketplaceId, toPro, sourcePro, skuCost, thisDefaultTem, commodity);
                        if(toPro.getStandardPrice() == null)
                            toPro.setHasRequired(1);
                    }
                    copyListing.setPublishMessage(this.messageToString(toPro,"复制欧洲站点生成publishMessage异常"));
                }
                copuList.add(copyListing);
            }
            saveToList(copuList);
        } catch (GlobalException e) {
            logger.error("复制欧洲站点失败1", e);
            throw e;
        } catch (Exception e) {
            logger.error("复制欧洲站点失败2", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601, "复制异常");
        }
    }

    private String messageToString(AmazonRequestProduct toPro,String erroMessage){
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String publishMessageJson="";
        try {
            publishMessageJson=mapper.writeValueAsString(toPro);
        } catch (JsonProcessingException e) {
            logger.error(erroMessage,e);
        }
        return publishMessageJson;
    }

    /**
     * 设置计价模板，价格，单位
     *
     * @param marketplaceId  站点信息
     * @param subTo          目标对象
     * @param subSource      原对象
     * @param skuCost        sku物流费用信息列表
     * @param thisDefaultTem 刊登模板规则
     * @param commodity      Spu对应商品
     */
    private void setPrice(MarketplaceId marketplaceId, AmazonRequestProduct subTo, AmazonRequestProduct subSource, JSONObject skuCost, AmazonTemplateRule thisDefaultTem, JSONObject commodity) {
        subTo.setStandardPriceUnit(marketplaceId.getCurrency());
        String computeTemplate = thisDefaultTem.getComputeTemplate();
        BigDecimal computeTemplateJson = createComputeTemplateJson(computeTemplate, skuCost, subSource.getPlSku(), commodity, marketplaceId.getCountryCode(),subTo);
        //subTo.setComputeTemplateJson(computeTemplate);
        subTo.setStandardPrice(getPriceResulte(computeTemplateJson, "USD", marketplaceId.getCurrency()));
    }


    private List<JSONObject> getCommoditySpecList(JSONObject commodity){
        List<JSONObject> commoditySpecList = JSONObject.parseArray(commodity.getJSONArray("commoditySpecList").toJSONString(), JSONObject.class);
        if (commoditySpecList == null || commoditySpecList.size() == 0)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600.getCode(), "商品为空");
        return commoditySpecList;
    }


    private JSONObject getRightCommoditySpec(String plSku,JSONObject commodity){
        List<JSONObject> commoditySpecList = this.getCommoditySpecList(commodity);
        for (JSONObject spec : commoditySpecList) {
            if (spec.getString("systemSku").equalsIgnoreCase(plSku)) {
                return spec;
            }
        }
        return null;
    }

    /**
     * 生成一个计价模板
     *
     * @param computeTemplate 规则
     * @param skuCost         预估物流费
     * @param plSku           plsku
     * @param commodity       商品数据
     * @return 价格
     */
    public BigDecimal createComputeTemplateJson(String computeTemplate, JSONObject skuCost, String plSku, JSONObject commodity, String countryCode, AmazonRequestProduct subTo) {

        JSONObject spe = this.getRightCommoditySpec(plSku,commodity);

        if (spe == null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600.getCode(), "商品不存在");
        }

        try {
            if (StringUtils.isBlank(computeTemplate)) {   //如果模板为空，设置价格为空
                logger.debug("sku {} 因为价格模板为空所以价格为空");
                return null;
            }
            BigDecimal freight = null;
            if (skuCost != null && skuCost.size() > 0) {
                for (Map.Entry<String, Object> en : skuCost.entrySet()) {
                    if (plSku.equalsIgnoreCase(en.getKey())) {
                        freight = new BigDecimal(en.getValue().toString());
                    }
                }
            }
            if (freight == null) {
                logger.debug("sku {} 因为预估物流费为空所以价格为空");
                return null;
            }
            JSONObject object = JSONObject.parseObject(computeTemplate);
            object.put("logisticsAddress", countryCode);
            String saleProfitStr = object.getString("saleProfit");  //销售利润率
            BigDecimal saleProfit = new BigDecimal(saleProfitStr);

            computeTemplate = JSONObject.toJSONString(object);
            subTo.setComputeTemplateJson(computeTemplate);

            BigDecimal ratio = new BigDecimal("0");  //售价消耗比例
            BigDecimal text = new BigDecimal("0");  // 售价消耗金额

            String brokeragePriceRatioStr = object.getString("brokeragePriceRatio");  //提成比例
            if (StringUtils.isNotBlank(brokeragePriceRatioStr)) {
                ratio = ratio.add(new BigDecimal(brokeragePriceRatioStr));
            }

            String brokeragePriceTextStr = object.getString("brokeragePriceText");   // 提成固定值
            if (StringUtils.isNotBlank(brokeragePriceTextStr)) {
                text = text.add(new BigDecimal(brokeragePriceTextStr));
            }

            String items = object.getString("items");
            if (StringUtils.isNotBlank(items)) {
                List<JSONObject> jsonObjects = JSONObject.parseArray(items, JSONObject.class);
                for (JSONObject item : jsonObjects) {
                    if (StringUtils.isNotBlank(item.getString("itemPriceRatio"))) {
                        ratio = ratio.add(new BigDecimal(item.getString("itemPriceRatio")));
                    }
                    if (StringUtils.isNotBlank(item.getString("itemPriceText"))) {
                        text = text.add(new BigDecimal(item.getString("itemPriceText")));
                    }
                }
            }

            saleProfit = saleProfit.divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP);
            ratio = ratio.divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP);


            String commodityPriceUsStr = spe.getString("commodityPriceUs");    //美元成本
            BigDecimal commodityPriceUs = new BigDecimal(commodityPriceUsStr);

            BigDecimal add = commodityPriceUs.add(text);
            BigDecimal subtract = new BigDecimal(1).subtract(ratio).subtract(saleProfit);
            BigDecimal divide = add.divide(subtract, 4, BigDecimal.ROUND_HALF_UP);
            return divide;
        } catch (Exception e) {
            logger.error("生成计价模板异常", e);
        }
        return null;
    }

    /**
     * 通过汇率计算指定币种装换值
     *
     * @param sour  原金额
     * @param soStr 原币种
     * @param toStr 模板币种
     * @return 结果
     */
    public BigDecimal getPriceResulte(BigDecimal sour, String soStr, String toStr) {
        try {
            BigDecimal rate = this.getRate(soStr, toStr);
            BigDecimal multiply = rate.multiply(sour);
            return  multiply.setScale(2, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {
            logger.error("汇率装换金额异常从 " + soStr + " 到 " + toStr + " 金额数 " + sour, e);
            return null;
        }

    }

    /**
     * 远程获取汇率
     *
     * @param soStr 原币种
     * @param toStr 目标币种
     * @return 汇率
     */
    public BigDecimal getRate(String soStr, String toStr) {
        String s = remoteOrderRuleService.GetRate(soStr, toStr);
        String rate = Utils.returnRemoteResultDataString(s, "查询汇率异常");
        BigDecimal rete = new BigDecimal(rate);
        return rete;
    }




    private void saveToList(ArrayList<AmazonPublishListing> listings ){

        for(AmazonPublishListing amazonPublishListing:listings){
            amazonPublishListingService.insertSelective(amazonPublishListing);

            //刊登操作日志
            publishLogService.insert(amazonPublishListing.getPublishMessage(), PublishLogEnum.valueOf("COPY"),
                    UserSession.getUserBaseUserInfo().getUserid(), UserSession.getUserBaseUserInfo().getUsername(),amazonPublishListing.getId());
            try {
                AmazonRequestProduct<?> product = JSONObject.parseObject(amazonPublishListing.getPublishMessage(), AmazonRequestProduct.class);
                product.setId(amazonPublishListing.getId());
                ExecutorService executor = Executors.newFixedThreadPool(1);
                ProcessXmlDraftTask processXmlDraftTask = new ProcessXmlDraftTask(product,amazonSubListingUtil);
                Future<String> futureResult = executor.submit(processXmlDraftTask);
                String errorMsg = futureResult.get();
                executor.shutdown();
                if(errorMsg != null && errorMsg.length() > 0)
                {
                    logger.warn("品连复制时生成sub数据时异常，中止线操作，数据被手动删除，被删除的ID为：{}",product.getId());
                    amazonPublishSubListingService.deleteForBaseId(product.getId());
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601,errorMsg);
                }
            }catch (Exception e){
                logger.error("品连复制时生成sub数据时异常",e);
            }
        }


    }



    /**
     * 查询获得第一分类
     * @param spu spu
     * @param siteCode 站点码（短码）
     * @return 第一分类
     *//*
	private String getCategory1Path(String spu, String siteCode){
		try {
			String amazon = remoteCommodityService.querySpuSiteCategory(spu, "Amazon", siteCode);
			logger.debug("第一分类远程商品服务amazon:{}", amazon);
			String dataString = Utils.returnRemoteResultDataString(amazon, "设置刊登第一分类远程商品服务异常");
			logger.debug("第一分类远程商品服务dataString:{}", dataString);

			SiteCategory siteCategory = JSONObject.parseObject(dataString, SiteCategory.class);
			logger.debug("第一分类远程商品服务siteCategory:{}", siteCategory);
			if (siteCategory == null) {
				logger.debug("获取商品分类数据错误");
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600.getCode(), "未找到商品第一分类映射");
			}
			Long[] longs = new Long[2];
			longs[0] = siteCategory.getPlatCategoryID();
			return siteCategory.getCategoryPath();
		}catch (Exception e){
			logger.error("获取第一分类异常，站点："+siteCode+" spu："+ spu ,e);
			return null;
		}
	}*/


}
