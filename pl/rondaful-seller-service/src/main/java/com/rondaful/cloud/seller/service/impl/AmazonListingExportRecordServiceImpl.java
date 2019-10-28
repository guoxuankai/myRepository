package com.rondaful.cloud.seller.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rondaful.cloud.common.constant.ConstantAli;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.service.FileService;
import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.ExcelUtil;
import com.rondaful.cloud.common.utils.UserUtils;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.seller.entity.AmazonListingExportRecord;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.amazon.AmazonPublishListStatus;
import com.rondaful.cloud.seller.mapper.AmazonListingExportRecordMapper;
import com.rondaful.cloud.seller.remote.RemoteSupplierProviderService;
import com.rondaful.cloud.seller.remote.RemoteUserService;
import com.rondaful.cloud.seller.service.AmazonListingExportRecordService;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.utils.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.zip.ZipOutputStream;

@Service
@Transactional
public class AmazonListingExportRecordServiceImpl extends BaseServiceImpl<AmazonListingExportRecord> implements AmazonListingExportRecordService {
    private final Logger logger = LoggerFactory.getLogger(AmazonListingExportRecordServiceImpl.class);
    @Autowired
    AmazonPublishListingService amazonPublishListingService;
    private static final String msg = "亚马逊同步数据";
    @Autowired
    UserUtils userUtils;
    @Autowired
    RemoteSupplierProviderService remoteSupplierProviderService;
    @Autowired
    RemoteUserService remoteUserService;
    @Resource
    private FileService fileService;
    @Autowired
    AmazonListingExportRecordMapper amazonListingExportRecordMapper;

    @Value("${rondaful.system.env}")
    public String env;

    @Override
    public void export(AmazonPublishListing model, String exportType) throws IOException, ParseException {
        if (AmazonConstants.EXPORT_TYPE_SEARCH.equals(exportType)){
            model.setIds(null);
        }
        AmazonListingExportRecord exportRecord = new AmazonListingExportRecord();
        exportRecord.setCreateTime(new Date());
        exportRecord.setExportStatus(AmazonConstants.EXPORT_STATUS_INIT);
        exportRecord.setExportType(exportType);
        exportRecord.setPlAccount(userUtils.getUser().getUsername());
        String snow = String.valueOf(SnowFlakeUtil.nextId());
        String filename = "amz_listing_" + DateUtil.DateToDir(new Date()) + "_" + snow.substring(snow.length() - 8, snow.length());
        exportRecord.setExportName(filename);
        amazonListingExportRecordMapper.insertSelective(exportRecord);//插入导入记录
        logger.info("zzzzzzzzzzzzzzzzzzzzzzzz");
        String[] header = {"listingid", "ASIN", "品连SKU", "平台SKU", "标题", "color", "colormap", "size", "sizemap", "刊登账号",
                "刊登站点", "产品第一分类", "产品第二分类", "父级分类模板", "子类分类模板", "发货处理天数", "发货仓库", "物流类型", "编码类型", "编码",
                "价格", "利润率", "币种", "数量","品牌名", "制造商", "Part Number", "产品亮点1", "产品亮点2", "产品亮点3",
                "产品亮点4", "产品亮点5","搜索关键词", "产品描述", "长度","宽度","高度","尺寸单位","包装前重量","包装后重量",
                "重量单位","物品状态描述","销售人员", "主图url", "附图url1", "附图url2", "附图url3", "附图url4", "附图url5", "附图url6",
                "附图url7", "附图url8", "listing备注", "创建时间", "在线时间", "状态"};
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String i18n = request.getHeader("i18n");
        if ("en_us".equals(i18n)){
            header= new String[]{"listingid", "ASIN", "Pinglian SKU", "Platform SKU", "Title", "color", "colormap", "size", "sizemap", "Publish an account number",
                    "Publishing site", "Product Classification I", "Product Classification II", "Parent Classification Template", "Subclass classification template", "Days of Delivery Processing", "Delivery Warehouse", "Logistics Types", "Coding type", "Code",
                    "Price", "Profit margin", "currency", "Number", "Brand Name", "Manufacturer", "Part Number", "Product highlight 1", "Product highlights 2", "Product highlights 3",
                    "Product highlights 4", "Product highlights 5", "Search keywords", "Product Description", "length", "width", "height", "Dimensional unit", "Weight before packing", "Weight after packing",
                    "Weight", "Item Status Description", "Salesman", "Main graph URL", "Figure url1", "Figure url2", "Figure url3", "Figure url4", "Figure url5", "Figure url6",
                    "Figure url7", "Figure url8", "Listing Notes", "Creation time", "Online Time", "state"};
        }

        String[] finalHeader = header;
        new Thread(
                () -> {
                    logger.info("线程名" + Thread.currentThread().getName());
                    upRecord(model, filename, exportRecord.getId(), finalHeader,i18n);   //异步导入文件
                }
        ).start();
    }

    public void upRecord(AmazonPublishListing model, String filename, Long exportId,String[] header,String i18n) {
        logger.info("进入生成文件");
        logger.info("输入参数为" + JSONObject.toJSONString(model));
        AmazonListingExportRecord exportRecord = null; //把url插入导入记录
        try {
            String url = "";
            String jarroot = System.getProperty("user.home") + File.separator + Utils.getSerialNumber();
            String fileroot = jarroot + File.separator + filename;
            if (model.getPublishStatus() != null && model.getPublishStatus() == 3) {
                model.setTemp(msg);
            }
            logger.info("输入参数为" + JSONObject.toJSONString(model));
            Page<AmazonPublishListing> page = amazonPublishListingService.page(model);
            PageInfo<AmazonPublishListing> pageInfo = page.getPageInfo();
            List<AmazonPublishListing> list = pageInfo.getList();
            JSONArray array = new JSONArray();

            String[] key = {"id", "asin", "plSku", "platformSku", "title", "color", "colorMap", "size", "sizeMap", "plAccount",
                    "publishSite", "productCategory1Path", "productCategory2Path", "templatesName", "templatesName2", "fulfillmentLatency","warehouseName", "logisticsName", "standardProductType", "standardProductID",
                    "standardPrice", "profitMargin","standardPriceUnit","quantity" ,"brand", "manufacturer", "mfrPartNumber", "bulletPoint1","bulletPoint2", "bulletPoint3",
                    "bulletPoint4", "bulletPoint5","searchTerms", "description","dimensionLength","dimensionWidth","dimensionHeight","dimensionUnitOfMeasure","packageWeight","itemWeight",
                    "weightUnitOfMeasure","conditionType","userName", "mainImgUrl", "swatchImgUrl1", "swatchImgUrl2", "swatchImgUrl3", "swatchImgUrl4", "swatchImgUrl5", "swatchImgUrl6",
                    "swatchImgUrl7","swatchImgUrl8", "remark", "createTime", "onlineTime", "publishStatus"};
            String[] width = {"3000", "3000", "3000", "3000", "3000", "3000", "3000", "3000", "3000", "3000",
                              "3000", "3000", "3000", "3000", "3000", "3000", "3000", "3000", "3000", "3000",
                              "3000", "3000", "3000", "3000", "3000", "3000", "3000", "3000", "3000", "3000",
                              "3000", "3000", "3000", "3000", "3000", "3000", "3000", "3000", "3000", "3000",
                              "3000", "3000", "3000", "3000", "3000", "3000", "3000", "3000", "3000", "3000",
                              "3000", "3000", "3000", "3000", "3000", "3000"};
            logger.info("翻译出来的标题是"+JSON.toJSONString(header));
            logger.info("要导出的list为" + JSON.toJSONString(list));
            if (null != list && list.size() > 0) {
                for (AmazonPublishListing amazonPublishListing : list) {
                    Long id = amazonPublishListing.getId();
                    logger.info("此时导出的id为"+id);
                    String asin = amazonPublishListing.getAsin();
                    String plSku = amazonPublishListing.getPlSku();
                    String platformSku = amazonPublishListing.getPlatformSku();
                    String title = amazonPublishListing.getTitle();
                    String remark = amazonPublishListing.getRemark();
                    Date createTimeDate = amazonPublishListing.getCreateTime();
                    String createTime = null;
                    if (null != createTimeDate) {
                        createTime = DateUtils.dateToString(createTimeDate, DateUtils.FORMAT_2);
                    }

                    Integer publishStatusCode = amazonPublishListing.getPublishStatus();
                    String publishStatus = null;
                    if (null != publishStatusCode) {
                        publishStatus =checkPublishStatus(publishStatusCode,i18n);
                    }
                    Date onlineTimeDate = amazonPublishListing.getOnlineTime();
                    String onlineTime = null;
                    if (null != onlineTimeDate&& (AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE==publishStatusCode||AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_REST_PUSH==publishStatusCode)) {
                        onlineTime = DateUtils.dateToString(onlineTimeDate, DateUtils.FORMAT_2);
                    }
                    String userName = null;
                    Object sell = remoteUserService.getSupplyChinByUserIdOrUsername(amazonPublishListing.getSaleUserId(), null, 1);
                    if (sell != null) {
                        JSONObject selljs = (JSONObject) JSONObject.toJSON(sell);
                        if ("true".equals(selljs.getString("success"))) {
                            JSONObject jsonObjectcompany = selljs.getJSONObject("data");
                            userName = jsonObjectcompany.getString("userName");
                        }
                    }
                    String publishMessage = amazonPublishListing.getPublishMessage();
                    JSONObject jsonObject = JSONObject.parseObject(publishMessage);

                    StringBuilder stringBuilder = new StringBuilder();
                    JSONArray searchTermsArray = jsonObject.getJSONArray("searchTerms");
                    if (null!=searchTermsArray&&searchTermsArray.size()>0){
                        searchTermsArray.forEach(s->
                            stringBuilder.append(s)
                        );
                    }
                    String searchTerms=stringBuilder.toString();
                    String productCategory1Path = jsonObject.getString("productCategory1Path");
                    String productCategory2Path = jsonObject.getString("productCategory2Path");
                    String templatesName = jsonObject.getString("templatesName");
                    String templatesName2 = jsonObject.getString("templatesName2");
                    String weightUnitOfMeasure = jsonObject.getString("weightUnitOfMeasure");
                    Integer dimensionLength = jsonObject.getInteger("dimensionLength");
                    Integer dimensionWidth = jsonObject.getInteger("dimensionWidth");
                    Integer dimensionHeight = jsonObject.getInteger("dimensionHeight");
                    String dimensionUnitOfMeasure = jsonObject.getString("dimensionUnitOfMeasure");
                    Double packageWeight = jsonObject.getDouble("packageWeight");
                    Double itemWeight = jsonObject.getDouble("itemWeight");
                    Integer fulfillmentLatency = (Integer) jsonObject.get("fulfillmentLatency");
                    String brand = jsonObject.getString("brand");
                    String manufacturer = jsonObject.getString("manufacturer");
                    String mfrPartNumber = jsonObject.getString("mfrPartNumber");
                    String description = jsonObject.getString("description");
                    JSONObject conditionInfo = jsonObject.getJSONObject("conditionInfo");
                    String conditionType=null;
                    if (null!=conditionInfo){
                        conditionType=conditionInfo.getString("conditionType");
                    }
                    String quantity=jsonObject.getString("quantity");
                    List<String> bulletPoint = (List<String>) jsonObject.get("bulletPoint");
                    String bulletPoint1 = null;
                    String bulletPoint2 = null;
                    String bulletPoint3 = null;
                    String bulletPoint4 = null;
                    String bulletPoint5 = null;
                    if (null != bulletPoint && bulletPoint.size() > 0) {
                        Map<String, String> bulletPointMap = new HashMap<>();
                        for (int i = 0; i < bulletPoint.size(); i++) {
                            bulletPointMap.put("bulletPoint" + (i + 1), bulletPoint.get(i));
                        }
                        bulletPoint1 = bulletPointMap.get("bulletPoint1");
                        bulletPoint2 = bulletPointMap.get("bulletPoint2");
                        bulletPoint3 = bulletPointMap.get("bulletPoint3");
                        bulletPoint4 = bulletPointMap.get("bulletPoint4");
                        bulletPoint5 = bulletPointMap.get("bulletPoint5");
                    }
                    JSONArray images = (JSONArray) jsonObject.get("images");
                    String mainImgUrl = null;
                    String swatchImgUrl1 = null;
                    String swatchImgUrl2 = null;
                    String swatchImgUrl3 = null;
                    String swatchImgUrl4 = null;
                    String swatchImgUrl5 = null;
                    String swatchImgUrl6 = null;
                    String swatchImgUrl7 = null;
                    String swatchImgUrl8 = null;
                    if (null != images && images.size() > 0) {
                        Map<String, String> imgUrlMap = new HashMap<>();
                        for (int i = 0; i < images.size(); i++) {
                            JSONObject image = (JSONObject) images.get(i);
                            if ("Main".equals(image.get("imageType"))) {
                                mainImgUrl = (String) image.get("imageLocation");
                            } else if ("Swatch".equals(image.get("imageType"))) {
                                imgUrlMap.put("Swatch" + i, (String) image.get("imageLocation"));
                            }
                        }
                        swatchImgUrl1 = imgUrlMap.get("Swatch1");
                        swatchImgUrl2 = imgUrlMap.get("Swatch2");
                        swatchImgUrl3 = imgUrlMap.get("Swatch3");
                        swatchImgUrl4 = imgUrlMap.get("Swatch4");
                        swatchImgUrl5 = imgUrlMap.get("Swatch5");
                        swatchImgUrl6 = imgUrlMap.get("Swatch6");
                        swatchImgUrl7 = imgUrlMap.get("Swatch7");
                        swatchImgUrl8 = imgUrlMap.get("Swatch8");
                    }
                    //获取发货仓库名
                    Integer warehouseId = amazonPublishListing.getWarehouseId();
                    String warehouseName = null;
                    if (null != warehouseId && warehouseId != 0) {
                        Map map = getWareHouseById(warehouseId.toString());
                        warehouseName = (String) map.get("warehouseName");
                    }
                    Integer logisticsType = amazonPublishListing.getLogisticsType();
                    String logisticsName = null;
                    if (logisticsType != null) {
                        logisticsName =getLogisticsName(logisticsType,i18n);
                    }
                    String plAccount = amazonPublishListing.getPlAccount();
                    String publishSite = amazonPublishListing.getPublishSite();
                    JSONArray varRequestProductList = jsonObject.getJSONArray("varRequestProductList");
                    JSONObject jsonPare = new JSONObject();
                    jsonPare.put("id", id);
                    jsonPare.put("asin", asin);
                    jsonPare.put("plSku", plSku);
                    jsonPare.put("platformSku", platformSku);
                    jsonPare.put("title", title);
                    jsonPare.put("plAccount", plAccount);
                    jsonPare.put("searchTerms", searchTerms);
                    jsonPare.put("publishSite", publishSite);
                    jsonPare.put("productCategory1Path", productCategory1Path);
                    jsonPare.put("productCategory2Path", productCategory2Path);
                    jsonPare.put("templatesName", templatesName);
                    jsonPare.put("quantity",quantity);
                    jsonPare.put("dimensionLength", dimensionLength);
                    jsonPare.put("dimensionWidth", dimensionWidth);
                    jsonPare.put("dimensionHeight", dimensionHeight);
                    jsonPare.put("dimensionUnitOfMeasure", dimensionUnitOfMeasure);
                    jsonPare.put("packageWeight", packageWeight);
                    jsonPare.put("itemWeight", itemWeight);
                    jsonPare.put("weightUnitOfMeasure", weightUnitOfMeasure);
                    jsonPare.put("conditionType", conditionType);
                    jsonPare.put("standardProductID", jsonObject.getString("standardProductID"));
                    jsonPare.put("standardProductType", jsonObject.getString("standardProductType"));
                    jsonPare.put("templatesName2", templatesName2);
                    jsonPare.put("fulfillmentLatency", fulfillmentLatency);
                    jsonPare.put("warehouseName", warehouseName);
                    jsonPare.put("standardPrice", jsonObject.getString("standardPrice"));
                    jsonPare.put("logisticsName", logisticsName);
                    jsonPare.put("brand", brand);
                    jsonPare.put("manufacturer", manufacturer);
                    jsonPare.put("mfrPartNumber", mfrPartNumber);
                    jsonPare.put("bulletPoint1", bulletPoint1);
                    jsonPare.put("bulletPoint2", bulletPoint2);
                    jsonPare.put("bulletPoint3", bulletPoint3);
                    jsonPare.put("bulletPoint4", bulletPoint4);
                    jsonPare.put("bulletPoint5", bulletPoint5);
                    jsonPare.put("standardPriceUnit", jsonObject.getString("standardPriceUnit"));
                    jsonPare.put("description", description);
                    jsonPare.put("userName", userName);
                    jsonPare.put("mainImgUrl", mainImgUrl);
                    jsonPare.put("swatchImgUrl1", swatchImgUrl1);
                    jsonPare.put("swatchImgUrl2", swatchImgUrl2);
                    jsonPare.put("profitMargin", jsonObject.getString("profitMargin"));
                    jsonPare.put("swatchImgUrl3", swatchImgUrl3);
                    jsonPare.put("swatchImgUrl4", swatchImgUrl4);
                    jsonPare.put("swatchImgUrl5", swatchImgUrl5);
                    jsonPare.put("swatchImgUrl6", swatchImgUrl6);
                    jsonPare.put("swatchImgUrl7", swatchImgUrl7);
                    jsonPare.put("swatchImgUrl8", swatchImgUrl8);
                    jsonPare.put("remark", remark);
                    jsonPare.put("createTime", createTime);
                    jsonPare.put("onlineTime", onlineTime);
                    jsonPare.put("publishStatus", publishStatus);
                    array.add(jsonPare);
                    for (int i = 0; i < varRequestProductList.size(); i++) {
                         JSONObject varRequestProduc = (JSONObject) varRequestProductList.get(i);
                        Integer sps = varRequestProduc.getInteger("subPublishStatus");
                        String subPublishStatus = null;
                        if (sps != null) {
                            subPublishStatus = getSubPublishStatus(sps, i18n);
                        }
                        if (AmazonPublishListStatus.AMAZON_PUBLISH_SUB_STATUS_DELETE!=sps) { //疑似删除的不导出
                           JSONObject exportUse=varRequestProduc.getJSONObject("exportUse");
                            String color = null;
                            String colorMap = null;
                            String size = null;
                            String sizeMap = null;
                            if (null!=exportUse){
                                color=exportUse.getString("color");
                                colorMap=exportUse.getString("colorMap");
                                size=exportUse.getString("size");
                                sizeMap=exportUse.getString("sizeMap");
                            }
                            if (null!=exportUse&&StringUtils.isBlank(color)&&StringUtils.isBlank(colorMap)){
                                JSONObject colorSpecification = exportUse.getJSONObject("colorSpecification");
                                if (null!=colorSpecification){
                                    color=colorSpecification.getString("color");
                                    colorMap=colorSpecification.getString("colorMap");
                                }
                            }
//                            String categoryPropertyJsonString = varRequestProduc.getString("categoryPropertyJson");
//                            JSONObject categoryPropertyJson = null;
//                            JSONObject productType = null;
//                            if (null != categoryPropertyJsonString) {
//                                categoryPropertyJson = JSONObject.parseObject(categoryPropertyJsonString);
//                                productType = categoryPropertyJson.getJSONObject("productType");
//                            }
//                            JSONObject autoAccessoryMisc = null;
//                            if (null != productType) {
//                                autoAccessoryMisc = productType.getJSONObject("autoAccessoryMisc");
//                            }
//                            JSONObject variationData = null;
//                            if (null != autoAccessoryMisc) {
//                                variationData = autoAccessoryMisc.getJSONObject("variationData");
//                            }
//                            String size = null;
//                            String sizeMap = null;
//                            JSONObject colorSpecification = null;
//                            if (null != variationData) {
//                                size = variationData.getString("size");
//                                sizeMap = variationData.getString("sizeMap");
//                                colorSpecification = (JSONObject) variationData.get("colorSpecification");
//                            }
//                            String color = null;
//                            String colorMap = null;
//                            if (colorSpecification != null) {
//                                color = colorSpecification.getString("color");
//                                colorMap = colorSpecification.getString("colorMap");
//                            }
                            String standardProductType = varRequestProduc.getString("standardProductType");
                            String sonQuantity = varRequestProduc.getString("quantity");

                            String standardProductID = varRequestProduc.getString("standardProductID");
                            String standardPrice = varRequestProduc.getString("standardPrice");
                            String standardPriceUnit = varRequestProduc.getString("standardPriceUnit");
                            String profitMargin = varRequestProduc.getString("profitMargin");
                            JSONArray jsonArray = varRequestProduc.getJSONArray("images");
                            String plSonSku = varRequestProduc.getString("plSku");
                            String asinSon = varRequestProduc.getString("asin");
                            String sonTitle = varRequestProduc.getString("title");
                            String sonMfrPartNumber = varRequestProduc.getString("mfrPartNumber");
                            JSONObject imagesSon = null;
                            String sonMainImgUrl = null;
                            String skuSon = null;
                            String sonSwatchImgUrl1 = null;
                            String sonSwatchImgUrl2 = null;
                            String sonSwatchImgUrl3 = null;
                            String sonSwatchImgUrl4 = null;
                            String sonSwatchImgUrl5 = null;
                            String sonSwatchImgUrl6 = null;
                            String sonSwatchImgUrl7 = null;
                            String sonSwatchImgUrl8 = null;
                            if (null != jsonArray && jsonArray.size() > 0) {
                                imagesSon = (JSONObject) jsonArray.get(0);
                                skuSon = imagesSon.getString("sku");
                                Map<String, String> imgUrlMap = new HashMap<>();
                                for (int k = 0; k < jsonArray.size(); k++) {
                                    JSONObject image = (JSONObject) jsonArray.get(k);
                                    if ("Main".equals(image.get("imageType"))) {
                                        sonMainImgUrl = image.getString("imageLocation");
                                    } else if ("Swatch".equals(image.get("imageType"))) {
                                        imgUrlMap.put("Swatch" + k, image.getString("imageLocation"));
                                    }
                                }
                                sonSwatchImgUrl1 = imgUrlMap.get("Swatch1");
                                sonSwatchImgUrl2 = imgUrlMap.get("Swatch2");
                                sonSwatchImgUrl3 = imgUrlMap.get("Swatch3");
                                sonSwatchImgUrl4 = imgUrlMap.get("Swatch4");
                                sonSwatchImgUrl5 = imgUrlMap.get("Swatch5");
                                sonSwatchImgUrl6 = imgUrlMap.get("Swatch6");
                                sonSwatchImgUrl7 = imgUrlMap.get("Swatch7");
                                sonSwatchImgUrl8 = imgUrlMap.get("Swatch8");
                            }
//                        List<String> sonBulletPoint = (List<String>) varRequestProduc.get("bulletPoint");
//                        String sonBulletPoint1 = null;
//                        String sonBulletPoint2 = null;
//                        String sonBulletPoint3 = null;
//                        String sonBulletPoint4 = null;
//                        String sonBulletPoint5 = null;
//                        if (null != sonBulletPoint && sonBulletPoint.size() > 0) {
//                            Map<String, String> bulletPointMap = new HashMap<>();
//                            for (int k = 0; k < sonBulletPoint.size(); k++) {
//                                bulletPointMap.put("bulletPoint" + (k + 1), sonBulletPoint.get(k));
//                            }
//                            sonBulletPoint1 = bulletPointMap.get("bulletPoint1");
//                            sonBulletPoint2 = bulletPointMap.get("bulletPoint2");
//                            sonBulletPoint3 = bulletPointMap.get("bulletPoint3");
//                            sonBulletPoint4 = bulletPointMap.get("bulletPoint4");
//                            sonBulletPoint5 = bulletPointMap.get("bulletPoint5");
//                        }
                            JSONObject json = new JSONObject();
                            json.put("id", id);
                            json.put("asin", asinSon);
                            json.put("plSku", plSonSku);
                            json.put("platformSku", skuSon);
                            json.put("title", sonTitle);
                            json.put("color", color);
                            json.put("colorMap", colorMap);
                            json.put("size", size);
                            json.put("quantity", sonQuantity);
                            json.put("searchTerms", searchTerms);
                            json.put("subPublishStatus", subPublishStatus);
                            json.put("dimensionLength", dimensionLength);
                            json.put("dimensionWidth", dimensionWidth);
                            json.put("dimensionHeight", dimensionHeight);
                            json.put("dimensionUnitOfMeasure", dimensionUnitOfMeasure);
                            json.put("packageWeight", packageWeight);
                            json.put("itemWeight", itemWeight);
                            json.put("weightUnitOfMeasure", weightUnitOfMeasure);
                            json.put("conditionType", conditionType);
                            json.put("sizeMap", sizeMap);
                            json.put("plAccount", plAccount);
                            json.put("publishSite", publishSite);
                            json.put("productCategory1Path", productCategory1Path);
                            json.put("productCategory2Path", productCategory2Path);
                            json.put("templatesName", templatesName);
                            json.put("templatesName2", templatesName2);
                            json.put("fulfillmentLatency", fulfillmentLatency);
                            json.put("warehouseName", warehouseName);
                            json.put("logisticsName", logisticsName);
                            json.put("standardProductType", standardProductType);
                            json.put("standardProductID", standardProductID);
                            json.put("standardPrice", standardPrice);
                            json.put("profitMargin", profitMargin);
                            json.put("standardPriceUnit", standardPriceUnit);
                            json.put("brand", brand);
                            json.put("manufacturer", manufacturer);
                            json.put("mfrPartNumber", sonMfrPartNumber);
                            json.put("bulletPoint1", bulletPoint1);
                            json.put("bulletPoint2", bulletPoint2);
                            json.put("bulletPoint3", bulletPoint3);
                            json.put("bulletPoint4", bulletPoint4);
                            json.put("bulletPoint5", bulletPoint5);
                            json.put("description", description);
                            json.put("userName", userName);
                            json.put("mainImgUrl", sonMainImgUrl);
                            json.put("swatchImgUrl1", sonSwatchImgUrl1);
                            json.put("swatchImgUrl2", sonSwatchImgUrl2);
                            json.put("swatchImgUrl3", sonSwatchImgUrl3);
                            json.put("swatchImgUrl4", sonSwatchImgUrl4);
                            json.put("swatchImgUrl5", sonSwatchImgUrl5);
                            json.put("swatchImgUrl6", sonSwatchImgUrl6);
                            json.put("swatchImgUrl7", sonSwatchImgUrl7);
                            json.put("swatchImgUrl8", sonSwatchImgUrl8);
                            json.put("remark", remark);
                            json.put("createTime", createTime);
                            json.put("onlineTime", onlineTime);
                            json.put("publishStatus", subPublishStatus);
                            array.add(json);
                        }
                    }
                }
            } else {
                JSONObject jsonObject = new JSONObject();
                array.add(jsonObject);
            }
            logger.info("导出数据的数值是" + array.size());
            InputStream inputStream = ExcelUtil.fileStream(array, ExcelUtil.createMap(header, key, width));
            url = fileService.saveFile(ConstantAli.getEnv(env), ConstantAli.getFolder("seller"), filename + ".xlsx", inputStream, null, null, null);
            logger.info("文件路径是url" + url);
            exportRecord = new AmazonListingExportRecord();
            exportRecord.setId(exportId);
            exportRecord.setUrl(url);
            exportRecord.setExportStatus(AmazonConstants.EXPORT_STATUS_SUCCESS);
            amazonListingExportRecordMapper.updateByPrimaryKeySelective(exportRecord);
        } catch (Exception e) {
            exportRecord = new AmazonListingExportRecord();
            exportRecord.setId(exportId);
            exportRecord.setExportStatus(AmazonConstants.EXPORT_STATUS_FAIL);
            amazonListingExportRecordMapper.updateByPrimaryKeySelective(exportRecord);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "异步上传文件出错");
        }
    }

    private String getSubPublishStatus(Integer sps,String i18n) {
        String subPublishStatus = null;
        switch (sps) {
            case 3:
                if ("en_us".equals(i18n)){
                    subPublishStatus="Upload Success";
                }else {
                    subPublishStatus = "上传成功";
                }
                break;
            case 4:
                if ("en_us".equals(i18n)){
                    subPublishStatus="Upload failure";
                }else {
                    subPublishStatus = "上传失败";
                }
                break;
            case 100:
                if ("en_us".equals(i18n)){
                    subPublishStatus="Suspected deletion";
                }else {
                    subPublishStatus = "疑似删除";
                }
                break;
        }
        return subPublishStatus;
    }

    private String checkPublishStatus(Integer publishStatusCode,String i18n) {
        String publishStatus = null;
        switch (publishStatusCode) {
            case 1:
                if ("en_us".equals(i18n)){
                    publishStatus="draft";
                }else {
                    publishStatus = "草稿";
                }
                break;
            case 2:
                if ("en_us".equals(i18n)){
                    publishStatus="In the publication";
                }else {
                    publishStatus = "刊登中";
                }
                break;
            case 3:
                if ("en_us".equals(i18n)){
                    publishStatus="On-line";
                }else {
                    publishStatus = "在线";
                }
                break;
            case 4:
                if ("en_us".equals(i18n)){
                    publishStatus="Publication failure";
                }else {
                    publishStatus = "刊登失败";
                }
                break;
            case 5:
                if ("en_us".equals(i18n)){
                    publishStatus="Offline";
                }else {
                    publishStatus = "已下线";
                }
                break;
            case 6:
                if ("en_us".equals(i18n)){
                    publishStatus="In the publication";
                }else {
                    publishStatus = "刊登中";
                }
                break;
            case 7:
                if ("en_us".equals(i18n)){
                    publishStatus="Online Status Picture Update";
                }else {
                    publishStatus = "在线状态图片更新";
                }
                break;
        }
        return publishStatus;
    }

    public String getLogisticsName(Integer logisticsType,String i18n) {
        String logisticsName = null;
        switch (logisticsType) {
            case 1:
                if ("en_us".equals(i18n)){
                    logisticsName="The lowest price";
                }else {
                    logisticsName = "价格最低";
                }
                break;
            case 2:
                if ("en_us".equals(i18n)){
                    logisticsName="Comprehensive optimum";
                }else {
                    logisticsName = "综合最优";
                }
                break;
            case 3:
                if ("en_us".equals(i18n)){
                    logisticsName="The quickest aging";
                }else {
                    logisticsName = "时效最快";
                }
                break;
        }
        return logisticsName;
    }

    public Map getWareHouseById(String warehouseId) {
        Object warehouseById = remoteSupplierProviderService.getWarehouseById(Integer.valueOf(warehouseId));
        JSONObject js = (JSONObject) JSONObject.toJSON(warehouseById);
        String errorCode = js.getString("errorCode");
        if (!"100200".equals(errorCode)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "调用仓库Id查询仓库信息失败" + js.getString("msg"));
        }
        Map<String, Object> data = (Map<String, Object>) js.get("data");
        if (null == data) {
            logger.info("仓库Id不对导致仓库信息为空");
            data=new HashMap<String, Object>();
        }
        return data;
    }


}

























