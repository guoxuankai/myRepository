package com.rondaful.cloud.seller.utils;


import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.MyStringUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.controller.UpcmanageController;
import com.rondaful.cloud.seller.entity.AmazonTemplateAttribute;
import com.rondaful.cloud.seller.entity.AmazonTemplateRule;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.SiteCategory;
import com.rondaful.cloud.seller.entity.amazon.AmazonAttr;
import com.rondaful.cloud.seller.entity.amazon.AmazonCategory;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.entity.amazon.AmazonSubRequestProduct;
import com.rondaful.cloud.seller.enums.AmazonTemplateEnums;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.generated.ConditionInfo;
import com.rondaful.cloud.seller.generated.ProductImage;
import com.rondaful.cloud.seller.remote.RemoteCommodityService;
import com.rondaful.cloud.seller.service.AmazonCategoryService;
import com.rondaful.cloud.seller.service.AmazonTemplateAttributeService;
import com.rondaful.cloud.seller.service.AmazonTemplateRuleService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Component
public class AmazonTemplateUtils {

    private static Logger logger = LoggerFactory.getLogger(AmazonTemplateUtils.class);


    private final RemoteCommodityService remoteCommodityService;

    private static String TEMPLATE_CLASSPATH = "com.rondaful.cloud.seller.generated.";

    private final UpcmanageController upcmanageController;

    private final AmazonTemplateRuleService amazonTemplateRuleService;

    private final AmazonCategoryService amazonCategoryService;


    @Autowired
    private AmazonTemplateAttributeService amazonTemplateAttributeService;


    @Autowired
    public AmazonTemplateUtils(RemoteCommodityService remoteCommodityService, UpcmanageController upcmanageController, AmazonTemplateRuleService amazonTemplateRuleService, AmazonCategoryService amazonCategoryService) {
        this.remoteCommodityService = remoteCommodityService;
        this.upcmanageController = upcmanageController;
        this.amazonTemplateRuleService = amazonTemplateRuleService;
        this.amazonCategoryService = amazonCategoryService;
    }

    //一级模板是这些的，不需要处理二级模板
    public static List<String> enumClassTemplate = Arrays.asList(
            "Shoes",
            "Clothing",
            "Sports",
            "RawMaterials",
            "PowerTransmission",
            "SportsMemorabilia");

    //一级模板是这些的，不需要处理二级模板，,注意传入的二级模板的大小写
    public static List<String> enumClassChildTemplate = Arrays.asList(
            "professionalHealthCare",
            "Luggage",
            "electronicGiftCard",
            "furniture");

    //一二级模板组合是这些的，二级模板不需要处理
    public static List<String> enumClassCompositeTemplate = Arrays.asList(
            "ToysBaby.BabyProducts",
            "WineAndAlcohol.beer",
            "WineAndAlcohol.spirits");


    /**
     * 设置刊登数据第一分类和模板信息1
     *
     * @param request  刊登数据对象
     * @param spu      spu
     * @param siteCode 站点code
     */
    public void setFirstCategory(AmazonRequestProduct request, String spu, String siteCode,String templateParent,String templateChild) {
        try {
            request.setTemplatesName(templateParent);
            request.setTemplatesName2(templateChild);
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
            Long[] longs1 = new Long[2];

            longs[0] = siteCategory.getPlatCategoryID();
            if(siteCategory.getPlatCategoryID() != null){
                Long[] longs2 = new Long[1];
                longs2[0] = siteCategory.getPlatCategoryID();
                List<AmazonCategory> gb = amazonCategoryService.queryCategoryListByCategoryId(longs2, siteCode.equalsIgnoreCase("GB") ? "UK" : siteCode);
                if(gb != null && gb.size() >0){
                    longs1[0] = Long.valueOf(gb.get(0).getId());
                }
            }


            request.setProductCategory1Path(siteCategory.getCategoryPath());
            //Long[] longs = new Long[]{siteCategory.getPlatCategoryID()};
            request.setProductCategory(longs);
            request.setProductCategoryPLId(longs1);
            if(StringUtils.isNotBlank(templateParent)){
                request.setTemplatesName(templateParent);
                request.setTemplatesName2(templateChild);
            }else {
                request.setTemplatesName(siteCategory.getCategoryTemplate1());
                request.setTemplatesName2(siteCategory.getCategoryTemplate2());
            }

        } catch (Exception e) {
            logger.error("设置刊登数据第一分类和模板信息异常", e);
        }
    }

    /**
     * 设置刊登数据第二分类和模板信息
     *
     * @param request  刊登数据对象
     * @param spu      spu
     * @param siteCode 站点code
     * @param rule     guize
     */
    public void setSecondCategory(AmazonRequestProduct request, String spu, String siteCode, String rule) {
        try {
            List<JSONObject> jsonObjects = JSONObject.parseArray(rule, JSONObject.class);
            for (JSONObject object : jsonObjects) {
                for (Map.Entry<String, Object> en : object.entrySet()) {
                    if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.CategorySecond.CLASSIFY_MAP.getType())) {
                        String amazon = remoteCommodityService.querySpuSiteCategory(spu, "Amazon", siteCode);
                        String dataString = Utils.returnRemoteResultDataString(amazon, "设置刊登第二分类远程商品服务异常");
                        SiteCategory siteCategory = JSONObject.parseObject(dataString, SiteCategory.class);
                        if (siteCategory == null)
                            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600.getCode(), "未找到商品第二分类映射");
                        Long[] productCategory = request.getProductCategory();
                        if (siteCategory.getPlatCategoryID() != null)
                            productCategory[1] = siteCategory.getPlatCategoryID();
                        request.setProductCategory2Path(siteCategory.getCategoryPath());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("设置刊登数据第二分类和模板信息异常", e);
        }

    }

    /**
     * 设置刊登种类
     *
     * @param request           刊登数据对象
     * @param publishType       刊登类型，1:单属性格式,2:多属性格式
     * @param commoditySpecList 商品列表
     * @param publishTypeWeb  前端要求的刊登类型
     */
    public void setPublishType(AmazonRequestProduct request, Integer publishType, List<JSONObject> commoditySpecList,Integer publishTypeWeb) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (publishType.equals(AmazonTemplateEnums.PublishType.SINGLE_ATTRIBUTE.getType())) {
            request.setIsMultiattribute(false);
            if(publishTypeWeb != null && publishTypeWeb == 2){
                checkCanMul(request);
            }
        } else if (publishType.equals(AmazonTemplateEnums.PublishType.MULTIPLE_ATTRIBUTE.getType())) {  //com.rondaful.cloud.seller.generated.InstrumentPartsAndAccessories
            checkCanMul(request);
        } else if (publishType.equals(AmazonTemplateEnums.PublishType.AUTO_ATTRIBUTE.getType())) {
            if (commoditySpecList.size() == 1) {
                request.setIsMultiattribute(false);
                if(publishTypeWeb != null && publishTypeWeb == 2){
                    checkCanMul(request);
                }
            } else {
                checkCanMul(request);
            }

        }
    }


    private void checkCanMul(AmazonRequestProduct request){
        if (StringUtils.isBlank(request.getTemplatesName()) && StringUtils.isBlank(request.getTemplatesName2())) {
            request.setIsMultiattribute(true);
        } else {
            List<String> themeList = getThemeList(request);
            if (themeList != null && themeList.size() > 0) {
                request.setIsMultiattribute(true);
            } else
                request.setIsMultiattribute(false);
        }
    }

    private List<String> getThemeList(AmazonRequestProduct request) {
        try {
            Class classz;
            try {
                classz = Class.forName(TEMPLATE_CLASSPATH + DecapitalizeChar.decapitalizeUpperCase(request.getTemplatesName2()));
            } catch (Exception e) {
                classz = Class.forName(TEMPLATE_CLASSPATH + DecapitalizeChar.decapitalizeUpperCase(request.getTemplatesName()));
            }
            if (classz == null)
                return null;

          /*  List<AmazonTemplateAttribute> attrList = amazonTemplateAttributeService.selectByTemplateAndMarketplaceId(
                    request.getTemplatesName(), request.getTemplatesName2(), request.getCountryCode());
            List<String> ThemeList = new ArrayList<>();
            for (AmazonTemplateAttribute amazonTemplateAttribute : attrList) {
                if (amazonTemplateAttribute.getAttributeName().equals("VariationTheme")) {
                    ThemeList.addAll(Arrays.asList(amazonTemplateAttribute.getOptions().split("\\|")));
                    break;
                }
            }*/
            // return ClassReflectionUtil.calculateTheme(classz/*, ThemeList*/);
            return getList(request.getTemplatesName(), request.getTemplatesName2(), request.getCountryCode());
        } catch (Exception e) {
            logger.error("设置刊登种类异常", e);
            return null;
        }
    }


    private List<String> getList(String templateParentName, String childName, String marketplaceId) {
        List<AmazonTemplateAttribute> attrList = amazonTemplateAttributeService.selectByTemplateAndMarketplaceId(
                templateParentName, childName, marketplaceId);
        List<AmazonAttr> productParentAttr = new ArrayList<>();
        List<AmazonAttr> productChildAttr = new ArrayList<>();
		/*if(CollectionUtils.isEmpty(attrList))
		{
			logger.error("刊登模板未导入，无法进一步操作。");
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"刊登模板未导入，无法进一步操作。");
		}*/
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(attrList) && StringUtils.isBlank(attrList.get(0).getMarketplaceId())) {
            logger.error("刊登模板数据未经处理，无法进一步操作。");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "刊登模板数据未经处理，无法进一步操作。");
        }

        //如果数据库不配置必填项，则与xsd为标准
        if (org.apache.commons.collections.CollectionUtils.isEmpty(attrList)) {
            logger.debug("配置中找不到templateParentName:{},templateChildName:{}, marketplaceId:{}的数据，将会读取XSD属性", templateParentName, childName, marketplaceId);
            attrList = new ArrayList<>();
        }
        try {
            // 一级模板属性加载
            Class classzParent = Class.forName(ClassReflectionUtil.BASE_CLASS_PATH + DecapitalizeChar.decapitalizeUpperCase(templateParentName));
            ClassReflectionUtil.beanToJson(productParentAttr, classzParent, null, new ArrayList<AmazonAttr>(), attrList);
            List<String> themeList = ClassReflectionUtil.calculateTheme(classzParent);
            // 二级模板属性加载
            //有些模板没有二级模板，也有可能同一个prodctType中有对象，与String组合在一个productType中（如：WineAndAlcohol.productType），。
            // 注意：大小写
            if (StringUtils.isNotBlank(childName)
                    && !containsNoUpper(templateParentName, enumClassTemplate)  // 一级模板不在集合里的
                    && !containsNoUpper(childName, enumClassChildTemplate) // 二级模板不在集合里的
                    && !containsNoUpper(templateParentName + "." + childName, enumClassCompositeTemplate)) //  一.二 级模板不在集合里的
            {
                Class classzChild;
                if (childName.contains("$")) //带$为内部类
                {
                    classzChild = Class.forName(childName);
                } else {
                    classzChild = Class.forName(ClassReflectionUtil.BASE_CLASS_PATH + DecapitalizeChar.decapitalizeUpperCase(childName));
                }
                ClassReflectionUtil.beanToJson(productChildAttr, classzChild, null, new ArrayList<AmazonAttr>(), attrList);
                //themeList = ClassReflectionUtil.calculateTheme(classzChild);
                List<String> childThemeList = ClassReflectionUtil.calculateTheme(classzChild);
                if(!CollectionUtils.isEmpty(childThemeList)) // 如果二级模板有变体，则不使用父的变体
                {
                    themeList = childThemeList;
                }

            }

            // 主题解释 -------------------begin ------------------------
            //List<String> themeList = new ArrayList<>();
            for (AmazonTemplateAttribute attr : attrList) //如果配置了，就读取数据的
            {
                if ("variationTheme".equalsIgnoreCase(attr.getAttributeName()) &&
                        StringUtils.isNotBlank(attr.getOptions())) {
                    String[] arrays = attr.getOptions().split(ClassReflectionUtil.ATTR_ENUM_VALUES_PREX);
                    //List<String> listValues = new ArrayList<>(arrays.length);
                    themeList.clear();
                    Collections.addAll(themeList, arrays);
                    // themeList.addAll(Arrays.asList(attr.getOptions().split(ClassReflectionUtil.ATTR_ENUM_VALUES_PREX)));
                    break;
                }
            }
            return themeList;
        } catch (Exception e) {
            logger.error("create Amazon publish Listing get variationTheme error: ", e);
        }
        return new ArrayList<>();
    }

    private boolean containsNoUpper(String str, List<String> list) {
        if (StringUtils.isBlank(str) || CollectionUtils.isEmpty(list)) {
            return false;
        }
        for (String l : list) {
            if (l.equalsIgnoreCase(str))
                return true;
        }
        return false;
    }

    /**
     * 设置刊登发货天数
     *
     * @param request            刊登数据对象
     * @param fulfillmentLatency 从订单生成到发货之间的天数，默认2天内发货 (1到30之间的整数)
     */
    public void setFulfillmentLatency(AmazonRequestProduct request, Integer fulfillmentLatency) {
        request.setFulfillmentLatency(fulfillmentLatency);
        if (fulfillmentLatency == null || fulfillmentLatency < 1 || fulfillmentLatency > 30) {
            request.setFulfillmentLatency(2);
        }
    }

    /**
     * 从商品列表中选取第一个不为空的某属性
     *
     * @param commoditySpecList 商品列表
     * @param attr              属性名称
     * @return 属性值
     */
    public String getFirstMessage(List<JSONObject> commoditySpecList, String attr) {
        for (JSONObject commodity : commoditySpecList) {
            if (StringUtils.isNotBlank(commodity.getString(attr)))
                return commodity.getString(attr);
        }
        return null;
    }


    /**
     * 获取变体数据
     *
     * @param variationTheme 变体类型  Color-Size
     * @param commoditySpec  商品变体值 Type(Type):BlackGold(BlackGold)| Type(Type):BlackGold(BlackGold)
     * @return 变体结果
     */
    private JSONObject getVariationMessage(String variationTheme, String commoditySpec) {
        try {
            if (StringUtils.isBlank(commoditySpec))
                return null;
            JSONObject object = new JSONObject();
            object.put("commoditySpec", commoditySpec);
            if (StringUtils.isBlank(variationTheme) || StringUtils.isBlank(commoditySpec))
                return object;
            String[] split = variationTheme.split("-");
            String[] split1 = commoditySpec.split("\\|");
            for (String key : split) {
                for (String str : split1) {
                    String s;
                    String[] split2 = str.split(":");
                    if (split2[0].contains("(") && split2[0].contains(")")) {
                        s = split2[0].substring(split2[0].indexOf("(") + 1, split2[0].indexOf(")"));
                    } else {
                        s = split2[0];
                    }
                    if (s.equalsIgnoreCase(key) || (s + "Name").equalsIgnoreCase(key)) {
                        String value;
                        if (split2[1].contains("(") && split2[1].contains(")")) {
                            value = split2[1].substring(split2[1].indexOf("(") + 1, split2[1].indexOf(")"));
                        } else {
                            value = split2[1];
                        }
                        object.put(key, value);
                    }
                }
            }
            return object;
        } catch (Exception e) {
            logger.error("获取变体数据异常", e);
            return null;
        }
    }


    /**
     * 设置变体数据
     *
     * @param request 刊登数据对象
     * @param object  商品spu维度的数据对象
     */
    public void setVariationTheme(AmazonRequestProduct request, JSONObject object, AmazonTemplateRule rules,Integer publishType) throws Exception {
        List<JSONObject> commoditySpecList = JSONObject.parseArray(object.getJSONArray("commoditySpecList").toJSONString(), JSONObject.class);
        if (commoditySpecList == null || commoditySpecList.size() == 0)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600.getCode(), "商品为空");
        JSONObject firstCommodity = commoditySpecList.get(0);
        ArrayList<String> spuMasterImage = getSpuMasterImage(object);
        ArrayList<String> spuAdditionImage = getSpuAdditionImage(object);
        ArrayList<String> spuImage = getSpuImage(spuMasterImage, spuAdditionImage);
        ArrayList<String> skuMasterImages = getSkuMasterImages(commoditySpecList);
        ArrayList<String> skuAdditionalImage = getSkuAdditionalImage(commoditySpecList);
        this.setSizeAndWeight(request, firstCommodity);
        this.setPublishType(request, rules.getPublishType(), commoditySpecList,publishType);
        if (request.getIsMultiattribute()) {   //多属性刊登
            int i = 1;
            AmazonRequestProduct sub = null;
            JSONObject subVari = null;
            List<String> themeList = getThemeList(request);
            String commoditySpecKeys = object.getString("commoditySpecKeys");
            String[] split = commoditySpecKeys.split(",");
            request.setVariationTheme(getVariationTheme(themeList, Arrays.asList(split)));
            // todo
            Random random = new Random();
            JSONObject rondomCommodity = commoditySpecList.get(random.nextInt(commoditySpecList.size()));
            String spuRule = "[{\"shopName\":\"\"},{\"goodsSPU\":\"\"}]";
            request.setSku(getPlatformSku(spuRule, request.getShopId(), object.getString("SPU"), rondomCommodity.getString("systemSku"), rules.getId()));
            request.setBrand(getBrand(rules.getBrandRule(), request.getShopName(), object.getString("brandName")));
            setProductTitle(request, rules.getProductTitleRule(), getFirstMessage(commoditySpecList, "commodityNameEn"));
            setProductNo(request, rules.getProductNoRule());
            setProductPrice(request, rules.getProductPriceRule());
            setQuantity(request, rules.getQuantityRule());
            setManufacturer(request, rules.getManufacturerRule(), request.getShopName(), object.getString("producer"));
            setPartNumber(request, rules.getPartNumber());
            setSearchTerms(request, null, spliteMeg(object.getString("searchKeywords")));
            setBulletPoint(request, null, createStrengths(object, 5));
            setConditionInfo(request, null);
            setDescription(request, rules.getDescriptionRule(), createStrengths(object, 5), spliteMeg(object.getString("commodityDesc")), spliteMeg(object.getString("packingList")));
            setParentMainImage(request, rules.getParentMainImageRule(), spuImage, skuMasterImages, skuAdditionalImage);
            //setParentAdditionImage(request, rules.getParentAdditionImageRule(), spuImage, skuMasterImages, skuAdditionalImage);
            for (JSONObject commodity : commoditySpecList) {
                sub = new AmazonRequestProduct();
                sub.setVari(getVariationMessage(request.getVariationTheme(), commodity.getString("commoditySpec")));
                sub.setPlSku(commodity.getString("systemSku"));
                sub.setSku(getPlatformSku(rules.getPlatformSkuRule(), request.getShopId(), object.getString("SPU"), commodity.getString("systemSku"), rules.getId()));
                //sub.setSku(request.getSku() + i++);
                sub.setBrand(request.getBrand());
                setProductTitle(sub, rules.getProductTitleRule(), commodity.getString("commodityNameEn"));
                setProductPrice(sub, rules.getProductPriceRule());
                setQuantity(sub, rules.getQuantityRule());
                setProductNo(sub, rules.getProductNoRule());
                setPartNumber(sub, rules.getPartNumber());
                ArrayList<String> skuAdditionalImage2 = getSkuAdditionalImage2(commodity);
                ArrayList<String> skuMasterImages2 = getSkuMasterImages2(commodity);
                skuMasterImages2 = setChildMainImage(sub, rules.getChildMainImageRule(), skuMasterImages2);                           //todo 问产品
                skuAdditionalImage2.addAll(skuMasterImages2);
                setChildAdditionImage(sub, rules.getChildAdditionImageRule(), skuAdditionalImage2);
                request.getVarRequestProductList().add(sub);
            }
        } else {  //当属性刊登
            request.setPlSku(firstCommodity.getString("systemSku"));
            request.setSku(getPlatformSku(rules.getPlatformSkuRule(), request.getShopId(), object.getString("SPU"), firstCommodity.getString("systemSku"), rules.getId()));
            request.setBrand(getBrand(rules.getBrandRule(), request.getShopName(), object.getString("brandName")));
            setProductTitle(request, rules.getProductTitleRule(), firstCommodity.getString("commodityNameEn"));
            setProductNo(request, rules.getProductNoRule());
            setProductPrice(request, rules.getProductPriceRule());
            setQuantity(request, rules.getQuantityRule());
            setManufacturer(request, rules.getManufacturerRule(), request.getShopName(), object.getString("producer"));
            setPartNumber(request, rules.getPartNumber());
            setSearchTerms(request, null, spliteMeg(object.getString("searchKeywords")));
            setBulletPoint(request, null, createStrengths(object, 5));
            setConditionInfo(request, null);
            setDescription(request, rules.getDescriptionRule(), createStrengths(object, 5), spliteMeg(object.getString("commodityDesc")), spliteMeg(object.getString("packingList")));

            ArrayList<String> skuAdditionalImage2 = getSkuAdditionalImage2(firstCommodity);
            ArrayList<String> skuMasterImages2 = getSkuMasterImages2(firstCommodity);

            skuMasterImages2 = setChildMainImage(request, rules.getChildMainImageRule(), skuMasterImages2);     //todo 问产品
            skuAdditionalImage2.addAll(skuMasterImages2);
            setChildAdditionImage(request, rules.getChildAdditionImageRule(), skuAdditionalImage2);
        }
    }

    /**
     * 生成子项sku变动时的部分刊登数据
     *
     * @param object                  sku维度商品信息
     * @param amazonSubRequestProduct 数据返回对象
     * @param empower                 授权数据
     * @param rules                   规则数据
     */
    public void setSubPublish(JSONObject object, AmazonSubRequestProduct amazonSubRequestProduct, Empower empower, AmazonTemplateRule rules) {
        try {
            AmazonRequestProduct request = new AmazonRequestProduct();
            request.setShopId(String.valueOf(empower.getEmpowerId()));
            request.setShopName(empower.getAccount());

            String platformSku = getPlatformSku(rules.getPlatformSkuRule(), String.valueOf(empower.getEmpowerId()), object.getString("SPU"), amazonSubRequestProduct.getPlSku(), rules.getId());
            request.setSku(platformSku);
            String brandName = getBrand(rules.getBrandRule(), empower.getAccount(), object.getString("brandName"));
            request.setBrand(brandName);


            setProductTitle(request, rules.getProductTitleRule(), object.getString("commodityNameEn"));
            String title = request.getTitle();

            setManufacturer(request, rules.getManufacturerRule(), request.getShopName(), object.getString("producer"));
            String manufacturer = request.getManufacturer();

            setPartNumber(request, rules.getPartNumber());
            String mfrPartNumber = request.getMfrPartNumber();

            setQuantity(request, rules.getQuantityRule());
            Long quantity = request.getQuantity();

            try {
                ArrayList<String> skuAdditionalImage2 = getSkuAdditionalImage2(object);
                ArrayList<String> skuMasterImages2 = getSkuMasterImages2(object);
                skuMasterImages2 = setChildMainImage(request, rules.getChildMainImageRule(), skuMasterImages2);
                skuAdditionalImage2.addAll(skuMasterImages2);
                setChildAdditionImage(request, rules.getChildAdditionImageRule(), skuAdditionalImage2);
            } catch (Exception e) {
                logger.error("生成子项sku变动时的部分刊登数据图片部分 异常",e);
            }

            amazonSubRequestProduct.setImages(request.getImages());
            amazonSubRequestProduct.setSku(platformSku);
            amazonSubRequestProduct.setBrand(brandName);
            amazonSubRequestProduct.setManufacturer(manufacturer);
            amazonSubRequestProduct.setMfrPartNumber(mfrPartNumber);
            amazonSubRequestProduct.setTitle(title);
            amazonSubRequestProduct.setQuantity(quantity);
        } catch (Exception e) {
            logger.error("生成子项sku变动时的部分刊登数据 异常", e);
        }
    }


    /**
     * 设置刊登的尺寸和重量
     *
     * @param request        数据封装对象
     * @param firstCommodity spu下的第一个sku
     */
    private void setSizeAndWeight(AmazonRequestProduct request, JSONObject firstCommodity) {
        try {
            request.setDimensionUnitOfMeasure("CM");
            request.setDimensionLength(firstCommodity.getBigDecimal("commodityLength"));
            request.setDimensionWidth(firstCommodity.getBigDecimal("commodityWidth"));
            request.setDimensionHeight(firstCommodity.getBigDecimal("commodityHeight"));
            request.setWeightUnitOfMeasure("GR");
            request.setPackageWeight(firstCommodity.getBigDecimal("packingWeight"));
            request.setItemWeight(firstCommodity.getBigDecimal("commodityWeight"));
        } catch (Exception e) {
            logger.error("设置刊登的尺寸和重量", e);
        }
    }


    public ArrayList<String> getSkuAdditionalImage(List<JSONObject> commoditySpecList) {
        ArrayList<String> resulte = new ArrayList<>();
        String url;
        for (JSONObject object : commoditySpecList) {
            url = object.getString("additionalPicture");
            if (StringUtils.isNotBlank(url)) {
                Collections.addAll(resulte, url.split("\\|"));
            }
        }
        return resulte;
    }

    public ArrayList<String> getSkuAdditionalImage2(JSONObject commodity) {
        ArrayList<String> resulte = new ArrayList<>();
        try {
            String url = commodity.getString("additionalPicture");
            if (StringUtils.isNotBlank(url)) {
                Collections.addAll(resulte, url.split("\\|"));
            }
        }catch (Exception e){
         logger.error("获取sku附图2异常",e);
        }
        return resulte;
    }

    public ArrayList<String> getSkuMasterImages2(JSONObject commodity) {
        ArrayList<String> resulte = new ArrayList<>();
        try {
            String url = commodity.getString("masterPicture");
            if (StringUtils.isNotBlank(url)) {
                resulte.addAll(Arrays.asList(url.split("\\|")));
            }
        /*if (resulte.size() == 0)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600.getCode(), "商品sku主图集为空");*/
        }catch (Exception e){
            logger.error("获取sku主图2异常",e);
        }


        return resulte;
    }


    public ArrayList<String> getSkuMasterImages(List<JSONObject> commoditySpecList) {
        ArrayList<String> resulte = new ArrayList<>();
        String url;
        for (JSONObject object : commoditySpecList) {
            url = object.getString("masterPicture");
            if (StringUtils.isNotBlank(url)) {
                String[] split = url.split("\\|");
                List<String> strings = Arrays.asList(split);
                resulte.addAll(strings);
            }
        }
       /* if (resulte.size() == 0)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600.getCode(), "商品sku主图集为空");*/
        return resulte;
    }


    public ArrayList<String> getSpuMasterImage(JSONObject object) {
        ArrayList<String> images = new ArrayList<>();
        String masterPicture = object.getString("masterPicture");
        if (StringUtils.isNotBlank(masterPicture)) {
            String[] split = masterPicture.split("\\|");
            List<String> strings = Arrays.asList(split);
            images.addAll(strings);
        }
        return images;
    }

    public ArrayList<String> getSpuAdditionImage(JSONObject object) {
        ArrayList<String> images = new ArrayList<>();
        String additionalPicture = object.getString("additionalPicture");
        if (StringUtils.isNotBlank(additionalPicture)) {
            images.addAll(Arrays.asList(additionalPicture.split("\\|")));
        }
        return images;
    }


    public ArrayList<String> getSpuImage(ArrayList<String> spuMasterImage, ArrayList<String> spuAdditionImage) {
        ArrayList<String> images = new ArrayList<>();
        if (!CollectionUtils.isEmpty(spuMasterImage)) {
            images.addAll(spuMasterImage);
        }
        if (!CollectionUtils.isEmpty(spuAdditionImage)) {
            images.addAll(spuAdditionImage);
        }
        return images;
    }


    public List<String> spliteMeg(String str) {
        if (StringUtils.isBlank(str))
            return new ArrayList<>();
        String[] split = str.split(":::");
        return Arrays.asList(split);
    }

    /**
     * 获取商品亮点列表
     *
     * @param object 商品对象（spu）
     * @param i      去的属性个数
     * @return 亮点列表
     */
    public List<String> createStrengths(JSONObject object, int i) {
        ArrayList<String> resulte = new ArrayList<>();
        for (int j = 1; j <= i; j++) {
            String string = object.getString("strength" + j);
            if (StringUtils.isNotBlank(string.trim())) {
                resulte.add(string);
            }
        }
        return resulte;
    }


    /**
     * 生成平台sku
     *
     * @param rule       模板的sku规则
     * @param shopName   店铺名称
     * @param SPU        spu
     * @param SKU        sku
     * @param templateId 模板id
     * @return 平台sku
     */
    public String getPlatformSku(String rule, String shopName, String SPU, String SKU, Long templateId) {
        try {
            if (StringUtils.isBlank(rule))
                return null;
            List<JSONObject> jsonObjects = JSONObject.parseArray(rule, JSONObject.class);
            StringBuilder sb = new StringBuilder();
            Random random = new Random();
            String i = String.valueOf(amazonTemplateRuleService.findRuleOrder(templateId));
            String sub = null;
            for (JSONObject json : jsonObjects) {
                for (Map.Entry<String, Object> o : json.entrySet()) {
                    if (o.getKey().equalsIgnoreCase(AmazonTemplateEnums.PlatformSku.NOT_RULE_NO.getType())) {
                        return MyStringUtils.getRandomString(random.nextInt(24) + 15);
                    } else if (o.getKey().equalsIgnoreCase(AmazonTemplateEnums.PlatformSku.SHOP_NAME.getType())) {
                        sub = shopName;
                        //sub = "virtualShopName";
                    } else if (o.getKey().equalsIgnoreCase(AmazonTemplateEnums.PlatformSku.GOODS_SPU.getType())) {
                        sub = SPU;
                    } else if (o.getKey().equalsIgnoreCase(AmazonTemplateEnums.PlatformSku.PIN_LIN_SKU.getType())) {
                        sub = SKU;
                    } else if (o.getKey().equalsIgnoreCase(AmazonTemplateEnums.PlatformSku.FIXED_CONTENT.getType())) {
                        sub = String.valueOf(o.getValue());
                    } else if (o.getKey().equalsIgnoreCase(AmazonTemplateEnums.PlatformSku.INCR_NO.getType())) {
                        sub = i;
                    } else if (o.getKey().equalsIgnoreCase(AmazonTemplateEnums.PlatformSku.RANDOM_MEG.getType())) {
                        JSONObject obj = (JSONObject) o.getValue();
                        sub = this.getRandomMsg(obj);
                    }
                    if(StringUtils.isNotBlank(sub)){
                        sub = sub.trim();
                        if ((sb.length() + sub.length() + i.length() + 1) < 39) {
                            sb.append(sub).append("|");
                        }
                    }
                }
            }
            //这里无论是什么规则，平台sku生成后都要添加一个自增序列
            sb.append(i).append("|");

            String s = sb.toString();
            return s.substring(0, s.length() - 1);
        } catch (Exception e) {
            logger.error("生成平台sku异常", e);
            return null;
        }

    }

    private String getRandomMsg(JSONObject obj) {
        try {
            String source = obj.getString("source");
            String digits = obj.getString("digits");
            if (StringUtils.isBlank(source) || StringUtils.isBlank(digits)) {
                return null;
            }
            int integer = Integer.parseInt(digits);
            int length = source.length();
            StringBuilder sb = new StringBuilder();
            if (integer >= length)
                integer = length;
            Random random = new Random();
            for (; integer > 0; integer--) {
                sb.append(source.charAt(random.nextInt(integer)));
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error("生成sku取随机类容异常", e);
            return null;
        }
    }


    /**
     * 设置品牌名称
     *
     * @param rule        规则
     * @param shopName    店铺名称
     * @param actualBrand 实际品牌名
     * @return 品牌名
     */
    public String getBrand(String rule, String shopName, String actualBrand) {
        try {
            if (StringUtils.isBlank(rule))
                return null;
            List<JSONObject> jsonObjects = JSONObject.parseArray(rule, JSONObject.class);
            for (JSONObject object : jsonObjects) {
                for (Map.Entry<String, Object> en : object.entrySet()) {
                    if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.BrandRule.DEFAULT.getType())) {
                        String s =  (String) en.getValue();
                        return StringUtils.isBlank(s)?"":s;
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.BrandRule.SHOP_NAME.getType())) {
                        return shopName;
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.BrandRule.ACTUAL_BRAND.getType())) {
                        if (StringUtils.isNotBlank(actualBrand))
                            return actualBrand;
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.BrandRule.ACTUAL_BRAND_SET_DEFAULT.getType())) {
                        if (StringUtils.isNotBlank(actualBrand)) {
                            return actualBrand;
                        } else
                            return (String) en.getValue();
                    }
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("设置品牌名称异常", e);
            return null;
        }

    }

    /**
     * 设置商品标题
     *
     * @param request 刊登数据对象
     * @param rule    规则
     * @param enName  商品英文名称
     */
    public void setProductTitle(AmazonRequestProduct request, String rule, String enName) {
        try {
            List<JSONObject> jsonObjects = JSONObject.parseArray(rule, JSONObject.class);
            for (JSONObject object : jsonObjects) {
                for (Map.Entry<String, Object> en : object.entrySet()) {
                    if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.productTitle.GOODS_EN_NAME.getType())) {
                        request.setTitle(StringUtils.isBlank(enName) ? "" : enName);
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.productTitle.BRAND_ADD_GOODS_EN_NAME.getType())) {
                        request.setTitle((StringUtils.isBlank(request.getBrand())?"":(request.getBrand() + " ")) + (StringUtils.isBlank(enName) ? "" : enName));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("设置商品标题异常", e);
        }
    }


    /**
     * 设置商品编码
     *
     * @param request 刊登数据对象
     * @param rule    规则
     */
    public void setProductNo(AmazonRequestProduct request, String rule) {   //upcmanageController
        try {
            List<JSONObject> jsonObjects = JSONObject.parseArray(rule, JSONObject.class);
            for (JSONObject object : jsonObjects) {
                for (Map.Entry<String, Object> en : object.entrySet()) {
                    if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.productNo.INPUTBox.getType())) {

                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.productNo.UPC.getType())) {
                        Integer empowerId = null;
                        if (request.getMerchantIdentifier() != null) {
                            empowerId = Integer.valueOf(request.getMerchantIdentifier());
                        }
                        request.setStandardProductID(findProductNo("UPC", 1, 2, empowerId));
                        request.setStandardProductType("UPC");
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.productNo.EAN.getType())) {
                        Integer empowerId = null;
                        if (request.getMerchantIdentifier() != null) {
                            empowerId = Integer.valueOf(request.getMerchantIdentifier());
                        }
                        request.setStandardProductID(findProductNo("EAN", 1, 2, empowerId));
                        request.setStandardProductType("EAN");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("设置商品编码异常", e);
        }
    }


    private String findProductNo(String numbertype, Integer number, Integer usedplatform, Integer empowerId) {
        try {
            List<String> upc = upcmanageController.selectObject(numbertype, 1, 2, empowerId);
            if (CollectionUtils.isEmpty(upc)) {
                logger.warn("获取upc为空");
                return null;
            }
            return upc.get(0);
        } catch (Exception e) {
            logger.error("获取upc异常", e);
            return null;
        }
    }

    /**
     * 设置商品价格
     *
     * @param request 刊登数据
     * @param rule    规则
     */
    public void setProductPrice(AmazonRequestProduct request, String rule) {
        try {
            List<JSONObject> jsonObjects = JSONObject.parseArray(rule, JSONObject.class);
            for (JSONObject object : jsonObjects) {
                for (Map.Entry<String, Object> en : object.entrySet()) {
                    if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.productPrice.INPUT_BOX.getType())) {

                    }
                }
            }
        } catch (Exception e) {
            logger.error("设置商品价格异常", e);
        }
    }

    /**
     * 设置商品可售数
     *
     * @param request 刊登数据对象
     * @param rule    规则
     */
    public void setQuantity(AmazonRequestProduct request, String rule) {
        try {
            List<JSONObject> jsonObjects = JSONObject.parseArray(rule, JSONObject.class);
            for (JSONObject object : jsonObjects) {
                for (Map.Entry<String, Object> en : object.entrySet()) {
                    if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.quantity.DEFAULT.getType())) {
                        request.setQuantity(Long.valueOf((String) en.getValue()));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("设置商品可售数异常", e);
        }
    }

    /**
     * 设置制造商
     *
     * @param request      刊登数据对象
     * @param rule         规则
     * @param shipName     店铺名称
     * @param manufacturer 实际制造商
     */
    public void setManufacturer(AmazonRequestProduct request, String rule, String shipName, String manufacturer) {
        try {
            List<JSONObject> jsonObjects = JSONObject.parseArray(rule, JSONObject.class);
            for (JSONObject object : jsonObjects) {
                for (Map.Entry<String, Object> en : object.entrySet()) {
                    if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.manufacturer.DEFAULT.getType())) {
                        request.setManufacturer(en.getValue() == null?"":(String) en.getValue());
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.manufacturer.SHOP_NAME.getType())) {
                        request.setManufacturer(shipName);
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.manufacturer.BRAND.getType())) {
                        request.setManufacturer(manufacturer);
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.manufacturer.BRAND_NULL.getType())) {
                        request.setManufacturer(manufacturer);
                        if (StringUtils.isBlank(manufacturer)) {
                            request.setManufacturer((String) en.getValue());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("设置制造商异常", e);
        }
    }

    /**
     * 设置 partNumber
     *
     * @param request 刊登数据对象
     * @param rule    规则
     */
    public void setPartNumber(AmazonRequestProduct request, String rule) {
        try {
            List<JSONObject> jsonObjects = JSONObject.parseArray(rule, JSONObject.class);
            for (JSONObject object : jsonObjects) {
                for (Map.Entry<String, Object> en : object.entrySet()) {
                    if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.partNumber.DEFAULT.getType())) {
                        request.setMfrPartNumber(en.getValue() == null?"":(String) en.getValue());
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.partNumber.PLATFORM_SKU.getType())) {
                        request.setMfrPartNumber(request.getSku().length() >= 40 ? request.getSku().substring(0, 40) : request.getSku());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("设置 partNumber异常", e);
        }

    }

    /**
     * 生成刊登描述
     *
     * @param requestProduct 刊登数据
     * @param rule           规则
     * @param virtues        卖点列表，最多5个，每个字符串 CN===中文描述:::UK===英语描述格式 每个语言中以;分隔多个
     * @param des            商品描述， CN===中文描述:::UK===英语描述  列表中为 UK===英语描述
     * @param packList       包装清单 CN===中文描述:::UK===英语描述  列表中为 UK===英语描述
     */
    public void setDescription(AmazonRequestProduct requestProduct, String rule, List<String> virtues, List<String> des, List<String> packList) {
        try {
            String countryCode = MarketplaceIdList.createMarketplaceForKeyId().get(requestProduct.getCountryCode()).getCountryCode();
            List<JSONObject> jsonObjects = JSONObject.parseArray(rule, JSONObject.class);
            Random random = new Random();
            StringBuffer sb = new StringBuffer();
            for (JSONObject object : jsonObjects) {
                for (Map.Entry<String, Object> en : object.entrySet()) {
                    if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.description.RANDOM_WELCOME.getType())) {
                        try {
                            String value = en.getValue().toString();
                            List<String> strings = JSONObject.parseArray(value, String.class);
                            this.setStringBulderNotNoll(sb, strings.get(random.nextInt(strings.size())), "\n");
                        } catch (Exception e) {
                            logger.error("生成刊登描述异常,随机欢迎语", e);
                        }
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.description.GOODS_TITLE.getType())) {
                        try {
                            this.setStringBulderNotNoll(sb, requestProduct.getTitle(), "\n");
                        } catch (Exception e) {
                            logger.error("生成刊登描述异常,商品标题", e);
                        }
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.description.GOODS_VIRTUE.getType())) {
                        try {
                            if (!CollectionUtils.isEmpty(virtues)) {
                                virtues.forEach(v -> {
                                    String message = getMessage(Arrays.asList(v.split(":::")), countryCode);
                                    String[] split = message.split(";");
                                    this.setStringBulderNotNoll(sb, split[random.nextInt(split.length)], "\n");
                                });
                            }
                        } catch (Exception e) {
                            logger.error("生成刊登描述异常,商品卖点", e);
                        }
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.description.GOODS_DESCRIP.getType())) {
                        try {
                            if (!CollectionUtils.isEmpty(des)) {
                                this.setStringBulderNotNoll(sb, getMessage(des, countryCode), "\n");
                            }
                        } catch (Exception e) {
                            logger.error("生成刊登描述异常,商品描述", e);
                        }
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.description.PACK_LIST.getType())) {
                        try {
                            if (!CollectionUtils.isEmpty(packList)) {
                                this.setStringBulderNotNoll(sb, getMessage(packList, countryCode), "\n");
                            }
                        } catch (Exception e) {
                            logger.error("生成刊登描述异常,包装清单", e);
                        }
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.description.RANDOM_END.getType())) {
                        try {
                            String value = en.getValue().toString();
                            List<String> strings = JSONObject.parseArray(value, String.class);
                            this.setStringBulderNotNoll(sb, strings.get(random.nextInt(strings.size())), "\n");
                        } catch (Exception e) {
                            logger.error("生成刊登描述异常,随机结束语", e);
                        }
                    }
                }
            }
            requestProduct.setDescription(sb.toString());
        } catch (Exception e) {
            logger.error("生成刊登描述异常", e);
        }
    }

    private void setStringBulderNotNoll(StringBuffer sb, String str, String splite) {
        if (StringUtils.isNotBlank(str)) {
            sb.append(str).append(splite);
        }
    }


    /**
     * 设置刊登关键字
     *
     * @param requestProduct 刊登数据对象
     * @param rule           设置关键字规则
     * @param saerchTerms    关键字列表 CN===中文描述:::UK===英语描述  列表中为 UK===英语描述 每个语种多个关键字以 , 分隔
     */
    public void setSearchTerms(AmazonRequestProduct requestProduct, String rule, List<String> saerchTerms) {
        try {
            String countryCode = MarketplaceIdList.createMarketplaceForKeyId().get(requestProduct.getCountryCode()).getCountryCode();
            String message = getMessage(saerchTerms, countryCode);
            if (StringUtils.isBlank(message))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600.getCode(), "商品关键字为空");
            String[] split = message.split(",");
            List<String> strings = Arrays.asList(split);
            Collections.shuffle(strings);
            StringBuilder sb = new StringBuilder();
            for (String str : strings) {
                if (str.length() > 200)
                    continue;
                sb.append(str);
                if (sb.length() <= 200) {
                    requestProduct.getSearchTerms().add(str);
                    sb.append(" ");
                } else
                    break;
            }
        } catch (Exception e) {
            logger.error("设置刊登关键字异常", e);
        }

    }

    /**
     * 设置刊登亮点
     *
     * @param requestProduct 刊登数据对象
     * @param rule           设置亮点规则
     * @param bulletPoints   亮点列表，最多5个，每个字符串 CN===中文描述:::UK===英语描述 格式 每个语言中以;分隔多个
     */
    public void setBulletPoint(AmazonRequestProduct requestProduct, String rule, List<String> bulletPoints) {
        try {
            String countryCode = MarketplaceIdList.createMarketplaceForKeyId().get(requestProduct.getCountryCode()).getCountryCode();
            Random random = new Random();
            bulletPoints.forEach(v -> {
                String message = getMessage(Arrays.asList(v.split(":::")), countryCode);
                String[] split = message.split(";");
                String s = split[random.nextInt(split.length)];
                requestProduct.getBulletPoint().add(s);
            });
        } catch (Exception e) {
            logger.error("设置刊登亮点异常", e);
        }
    }

    /**
     * 设置刊登物品状况
     *
     * @param requestProduct 刊登数据对象
     * @param rule           设置状况规则
     */
    private void setConditionInfo(AmazonRequestProduct requestProduct, String rule) {
        try {
            ConditionInfo conditionInfo = new ConditionInfo();
            conditionInfo.setConditionType("New");
            requestProduct.setConditionInfo(conditionInfo);
        } catch (Exception e) {
            logger.error("设置刊登物品状况异常", e);
        }

    }

    /**
     * 设置刊登父体主图
     *
     * @param requestProduct    刊登数据对象
     * @param rule              规则
     * @param SPUImages         spu图片列表
     * @param skuMainImages     sku主图列表
     * @param skuAdditionImages sku附图列表
     */
    public void setParentMainImage(AmazonRequestProduct requestProduct, String rule, ArrayList<String> SPUImages, ArrayList<String> skuMainImages, ArrayList<String> skuAdditionImages) {
        try {
            SPUImages = this.chingeHttsToHttp(SPUImages);
            //skuMainImages = this.chingeHttsToHttp(skuMainImages);
            // skuAdditionImages = this.chingeHttsToHttp(skuAdditionImages);
            List<JSONObject> jsonObjects = JSONObject.parseArray(rule, JSONObject.class);
            Random random = new Random();
            ProductImage productImage = new ProductImage();
            productImage.setSKU(requestProduct.getSku());
            productImage.setImageType("Main");
            // ----------------------------------------------------------
            if (SPUImages != null && SPUImages.size() > 0) {
                int i = random.nextInt(SPUImages.size());
                productImage.setImageLocation(SPUImages.remove(i));
            }
            /*for (JSONObject object : jsonObjects) {
                for (Map.Entry<String, Object> en : object.entrySet()) {
                    if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.parentMainImage.FIND_TO_SPU.getType())) {
                        if (SPUImages != null && SPUImages.size() > 0) {
                            int i = random.nextInt(SPUImages.size());
                            productImage.setImageLocation(SPUImages.remove(i));
                        }
                        break;
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.parentMainImage.FIND_TO_SKU.getType())) {
                        int i = random.nextInt(skuMainImages.size());
                        productImage.setImageLocation(skuMainImages.remove(i));
                        break;
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.parentMainImage.SUPPLY_TO_SKU.getType())) {
                        if (SPUImages == null || SPUImages.size() == 0) {
                            int i = random.nextInt(skuMainImages.size());
                            productImage.setImageLocation(skuMainImages.remove(i));
                        } else {
                            int i = random.nextInt(SPUImages.size());
                            productImage.setImageLocation(SPUImages.remove(i));
                        }
                        break;
                    }
                }
            }*/
            requestProduct.getImages().add(productImage);
        } catch (Exception e) {
            logger.error("设置刊登父体主图异常", e);
        }
    }

    /**
     * 设置刊登父体附图
     *
     * @param requestProduct    刊登数据对象
     * @param rule              规则
     * @param SPUImages         spu图片列表
     * @param skuMainImages     sku主图列表
     * @param skuAdditionImages sku附图列表
     */
    public void setParentAdditionImage(AmazonRequestProduct requestProduct, String rule, ArrayList<String> SPUImages, ArrayList<String> skuMainImages, ArrayList<String> skuAdditionImages) {
        try {
            SPUImages = this.chingeHttsToHttp(SPUImages);
            skuMainImages = this.chingeHttsToHttp(skuMainImages);
            skuAdditionImages = this.chingeHttsToHttp(skuAdditionImages);
            List<JSONObject> jsonObjects = JSONObject.parseArray(rule, JSONObject.class);
            Random random = new Random();
            ProductImage productImage = null;
            int i = 0;
            for (JSONObject object : jsonObjects) {
                for (Map.Entry<String, Object> en : object.entrySet()) {
                    if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.parentAdditionImage.FIND_TO_SPU.getType())) {
                        i = setImage(en, random, productImage, SPUImages, requestProduct);
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.parentAdditionImage.FIND_TO_SKU.getType())) {
                        if (skuAdditionImages.size() == 0)
                            continue;
                        setImage(en, random, productImage, skuAdditionImages, requestProduct);
                    } else if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.parentAdditionImage.SUPPLY_TO_SKU.getType())) {
                        if (skuAdditionImages.size() == 0)
                            continue;
                        // int i = setImage(en, random, productImage, SPUImages, requestProduct);
                        if (i > 0) {
                            int k = SPUImages.size();
                            for (int j = 0; j < i && j < skuAdditionImages.size(); j++) {
                                productImage = new ProductImage();
                                productImage.setSKU(requestProduct.getSku());
                                productImage.setImageType("PT" + ++k);
                                productImage.setImageLocation(skuAdditionImages.get(j));
                                requestProduct.getImages().add(productImage);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("设置刊登父体附图异常", e);
        }
    }

    /**
     * 设置刊登子项主图
     *
     * @param requestProduct 刊登数据模板
     * @param rule           规则
     * @param skuImgs        sku 图片列表
     */
    public ArrayList<String> setChildMainImage(AmazonRequestProduct requestProduct, String rule, ArrayList<String> skuImgs) {
        try {
            skuImgs = this.chingeHttsToHttp(skuImgs);
            List<JSONObject> jsonObjects = JSONObject.parseArray(rule, JSONObject.class);
            Random random = new Random();
            ProductImage productImage = null;
            for (JSONObject object : jsonObjects) {
                for (Map.Entry<String, Object> en : object.entrySet()) {
                    if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.childMainImage.FIND_TO_SKU.getType())) {
                        int i = random.nextInt(skuImgs.size());
                        productImage = new ProductImage();
                        productImage.setSKU(requestProduct.getSku());
                        productImage.setImageType("Main");
                        productImage.setImageLocation(skuImgs.remove(i));
                        requestProduct.getImages().add(productImage);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("设置刊登子项主图异常", e);
        }
        return skuImgs;

    }

    /**
     * 设置刊登子项附图
     *
     * @param requestProduct 刊登对象
     * @param rule           规则
     * @param skuImgs        sku图片列表
     */
    public void setChildAdditionImage(AmazonRequestProduct requestProduct, String rule, ArrayList<String> skuImgs) {
        try {
            skuImgs = this.chingeHttsToHttp(skuImgs);
            List<JSONObject> jsonObjects = JSONObject.parseArray(rule, JSONObject.class);
            Random random = new Random();
            ProductImage productImage = null;
            for (JSONObject object : jsonObjects) {
                for (Map.Entry<String, Object> en : object.entrySet()) {
                    if (en.getKey().equalsIgnoreCase(AmazonTemplateEnums.childAdditionImage.FIND_TO_SKU.getType())) {
                        setImage(en, random, productImage, skuImgs, requestProduct);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("设置刊登子项附图异常", e);
        }
    }


    /**
     * 将url列表中的Https更换为Http
     *
     * @param urls url列表
     * @return 处理后的Url列表
     */
    private ArrayList<String> chingeHttsToHttp(ArrayList<String> urls) {
        if (!CollectionUtils.isEmpty(urls)) {
            ArrayList<String> newUrls = new ArrayList<>();
            for (String url : urls) {
                newUrls.add(url.contains("https") ? url.replace("https", "http") : url);
            }
            return newUrls;
        }
        return urls;
    }


    private int setImage(Map.Entry<String, Object> en, Random random, ProductImage productImage, List<String> SPUImages, AmazonRequestProduct requestProduct) {
        int result = 0;
        JSONObject value = (JSONObject) en.getValue();
        Integer min = value.getInteger("min");
        Integer max = value.getInteger("max");
        int i = 0;
        if (max <= min) {
            i = max;
        } else
            i = random.nextInt(max - min + 1) + min;
        int j = 0;
        if (i >= SPUImages.size()) {
            for (String url : SPUImages) {                          //TODO 如果有要求这里每次不会一样，可以使用随机数取,并remove
                productImage = new ProductImage();
                productImage.setSKU(requestProduct.getSku());
                productImage.setImageType("PT" + ++j);
                productImage.setImageLocation(url);
                requestProduct.getImages().add(productImage);
            }
            SPUImages = new ArrayList<>();
            result = i - SPUImages.size();
        } else {
            for (; j < i; ) {
                int i1 = random.nextInt(SPUImages.size());
                String url = SPUImages.get(i1);
                SPUImages.remove(i1);
                productImage = new ProductImage();
                productImage.setSKU(requestProduct.getSku());
                productImage.setImageType("PT" + ++j);
                productImage.setImageLocation(url);
                requestProduct.getImages().add(productImage);
            }
        }
        return result;
    }


    private String getMessage(List<String> des, String countryCode) {
        String str = null;
        String english = null;
        for (String d : des) {
            String[] split = d.split("===");
            if (split.length != 2)
                continue;
            if (split[0].trim().equalsIgnoreCase(countryCode)) {
                str = split[1].trim();
            }
            if (split[0].trim().equalsIgnoreCase("EN")) {
                english = split[1].trim();
            }
        }
        if (StringUtils.isBlank(str))
            str = english;
        return str;
    }


    private String getVariationTheme(List<String> themeList, List<String> goodsTheme) {
        if (themeList == null || themeList.size() == 0)
            return null;
        if (goodsTheme == null || goodsTheme.size() == 0)
            return null;
        int size = 0;
        String result = null;
        String[] strs;
        boolean flag = true;
        for (String theme : themeList) {
            strs = theme.split("-");
            for (String str : strs) {
                if (!contains(goodsTheme, str)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                if (strs.length > size) {
                    result = theme;
                    size = strs.length;
                }
            }
            flag = true;
        }
        return result;
    }

    private boolean contains(List<String> sou, String to) {
        if (sou == null || sou.size() == 0)
            return false;
        if (StringUtils.isBlank(to))
            return false;
        for (String s : sou) {
            if (s.trim().equalsIgnoreCase(to)) {
                return true;
            }
            if (s.trim().equalsIgnoreCase("Color") && to.equalsIgnoreCase("ColorName")) {
                return true;
            }
            if (s.trim().equalsIgnoreCase("Size") && to.equalsIgnoreCase("SizeName")) {
                return true;
            }
        }
        return false;
    }


    public static void main(String[] str) {
       /* String s = "asfasddfsadvfasd";
        String s1 = toUpperCaseFirstOne(s);
        System.out.println(s1);*/

        //System.out.println(new Random().n);
       /* String str1 = "[{\"随机欢迎语\":[\"aaa\",\"bbbbb\",\"ccccc\"],\"no\":\"1\"},{\"商品标题\":\"商品标题\",\"no\":\"2\"},{\"商品卖点\":\"商品卖点\",\"no\":\"3\"},{\"商品描述\":\"商品描述\",\"no\":\"2\"},{\"包装清单\":\"包装清单\",\"no\":\"2\"},{\"随机结束语\":[\"1111\",\"2222\",\"33333\"],\"no\":\"2\"}]";
        //System.out.println(JSONObject.);
        JSONArray objects = JSONObject.parseArray(str1);
        List<JSONObject> jsonObjects = JSONObject.parseArray(str1, JSONObject.class);
        for (JSONObject object : jsonObjects) {
            for (Map.Entry<String, Object> en : object.entrySet()) {
                if (en.getValue().toString() instanceof String)
                    System.out.println(en.getValue().toString());
            }
        }*/
        /*Random random = new Random();
        while (true){
            System.out.println(random.nextInt(3));
        }*/

       /* List<String> afb = new AmazonTemplateUtils(null,null,null).getThemeList(new AmazonRequestProduct() {{
            setTemplatesName2("ce");
            setTemplatesName("ce");
        }});

        System.out.println(afb);*/


    }


}
