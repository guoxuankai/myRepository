package com.rondaful.cloud.seller.common.task;


import com.alibaba.fastjson.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.seller.common.mws.KeyValueConts;
import com.rondaful.cloud.seller.common.mws.intface.GetMatchingProductList;
import com.rondaful.cloud.seller.common.mwsProducts.model.GetMatchingProductForIdResult;
import com.rondaful.cloud.seller.common.mwsProducts.model.Product;
import com.rondaful.cloud.seller.common.mwsProducts.model.ProductList;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.seller.constants.AmazonPublishUpdateStatus;
import com.rondaful.cloud.seller.constants.PublishRequestReport;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.AmazonPublishReportDetail;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.amazon.AmazonPublishListStatus;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.seller.enums.AmazonPublishEnums;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.generated.ProductImage;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.service.AmazonPublishReportDetailService;
import com.rondaful.cloud.seller.service.AmazonPublishSubListingService;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;
import com.rondaful.cloud.seller.utils.AmazonSubListingUtil;
import com.sun.org.apache.xerces.internal.dom.DeferredElementNSImpl;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * 获取刊登数据（获取本地不存在的亚马逊刊登数据）
 */
@Component
public class GetMatchingProductForIdTask implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(GetMatchingProductForIdTask.class);


    @Autowired
    private GetMatchingProductList getMatchingProductList;

    @Autowired
    private AmazonPublishReportDetailService amazonPublishReportDetailService;

    @Autowired
    private AmazonPublishListingService amazonPublishListingService;

    @Autowired
    private AmazonPublishSubListingService amazonPublishSubListingService;

    @Autowired
    private AuthorizationSellerService authorizationSellerService;

    @Autowired
    private AmazonSubListingUtil amazonSubListingUtil;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedissLockUtil redissLockUtil;

    private String upListBeforeKey = "getAmazonDataTOPl_";

    public void process() {

        logger.info("----------------------------- 开始执行 同步亚马逊数据到本地了 ===========================================");

        AmazonPublishReportDetail param = new AmazonPublishReportDetail();
        param.setIsExist(AmazonPublishEnums.isExist.NOT_EXIST.getCode());                               //问产品，那些数据时需要插入到plain的？  todo

        AmazonPublishReportDetail detail = amazonPublishReportDetailService.selectLastOne(param);

        if (detail == null) {   // 全局暂时不存在  与亚马逊相比没有的数据
            return;
        }

        String lockKey = upListBeforeKey + PublishRequestReport.reportProgress.GetReportMsg.getTheInterface()+"_" + detail.getMerchantId();
        if(!redissLockUtil.tryLock(lockKey, 10, 60 * 10)) //等待10秒，10分放开锁
        {
            logger.debug(lockKey + " 其它服务正在执行。locking....");
            return ;
        }

        try {


            String token;
            Empower empower;
            try {
                empower = authorizationSellerService.selectAmazonAccount(new Empower() {{
                    setThirdPartyName(detail.getMerchantId());
                    setWebName(detail.getMarketplaceId());
                }});
                token = empower.getToken();
                if (StringUtils.isBlank(token)) {
                    logger.error("授权数据异常");
                    throw new Exception("授权数据异常");
                }
                if(!empower.getStatus().equals(1)){
                    logger.error("授权数据不在线");
                    throw new Exception("授权数据不在线");
                }
            } catch (Exception e) {
                logger.error("授权数据异常", e);
                detail.setMessage("授权数据异常");
                detail.setIsExist(AmazonPublishEnums.isExist.ERROR.getCode());
                amazonPublishReportDetailService.updateByPrimaryKeySelective(detail);
                return;
            }
            param.setMerchantId(detail.getMerchantId());
            param.setMarketplaceId(detail.getMarketplaceId());
            Page.builder(0, 10);
            Page<AmazonPublishReportDetail> page = amazonPublishReportDetailService.page(param);
            List<AmazonPublishReportDetail> list = page.getPageInfo().getList();

            List<AmazonPublishListing> listings;
            ArrayList<String> ids = new ArrayList<>();
            ArrayList<AmazonPublishReportDetail> sendList = new ArrayList<>();

            AmazonPublishListing listing = new AmazonPublishListing();
            listing.setMerchantIdentifier(detail.getMerchantId());
            MarketplaceId marketplaceId = MarketplaceIdList.resourceMarketplaceForKeyId().get(detail.getMarketplaceId());
            listing.setPublishSite(marketplaceId.getCountryCode());
            for (AmazonPublishReportDetail d : list) {
                listing.setPlatformSku(d.getSku());
                listings = amazonPublishListingService.findList(listing);
                if (listings != null && listings.size() > 0) {
                    d.setIsExist(AmazonPublishEnums.isExist.EXIST.getCode());
                    amazonPublishReportDetailService.updateByPrimaryKeySelective(d);
                } else {
                    sendList.add(d);
                    ids.add(d.getSku());
                }
                if (ids.size() >= 5)
                    break;
            }
            if (ids.size() == 0) {
                logger.info("发现本次操作的十个都已经是存在的了卖家ID {} 站点 {}", detail.getMerchantId(), detail.getMarketplaceId());
                return;
            }
            List<GetMatchingProductForIdResult> response = null;
            try {
                response = getMatchingProductList.sendAmazon(detail.getMerchantId(), token, detail.getMarketplaceId(), KeyValueConts.amazonProductIdTye.SellerSKU.getType(), ids);
            }catch (Exception e){
                logger.error("同步数据异常",e);
                toError(sendList,ids,AmazonPublishEnums.isExist.ERROR.getCode(),"请求亚马逊异常");
                return;
            }
            for (GetMatchingProductForIdResult result : response) {
                if (result.getError() != null || !result.getStatus().equalsIgnoreCase("success")) {                    //数据异常
                    this.forUpdateDetail(sendList, result, AmazonPublishEnums.isExist.ERROR.getCode(), result.getError().getCode() + " " + result.getError().getMessage());
                } else {
                    try {

                        //校验数据是否本地存在
                        listing.setPlatformSku(result.getId());
                        listings = amazonPublishListingService.findList(listing);
                        if (listings != null && listings.size() > 0) {
                            this.forUpdateDetail(sendList, result, AmazonPublishEnums.isExist.EXIST.getCode(), null);
                            continue;
                        }

                        AmazonPublishReportDetail simeDetail = this.getSimeDetail(sendList, result);
                        if (simeDetail == null) {
                            continue;
                        }
                        if (StringUtils.isBlank(simeDetail.getContent())) {
                            simeDetail.setIsExist(AmazonPublishEnums.isExist.ERROR.getCode());
                            simeDetail.setMessage("记录报告数据为空");
                            amazonPublishReportDetailService.updateByPrimaryKeySelective(simeDetail);
                            continue;
                        }

                        JSONObject object;
                        try {
                            object = JSONObject.parseObject(simeDetail.getContent());
                        } catch (Exception e) {
                            simeDetail.setIsExist(AmazonPublishEnums.isExist.ERROR.getCode());
                            simeDetail.setMessage("记录报告数据Json解析失败");
                            amazonPublishReportDetailService.updateByPrimaryKeySelective(simeDetail);
                            continue;
                        }

                        ProductList products = result.getProducts();
                        Product product = products.getProduct().get(0);
                        DeferredElementNSImpl attrs = (DeferredElementNSImpl) product.getAttributeSets().getAny().get(0);
                        // String sku = attrs.getElementsByTagName("ns2:PartNumber").item(0).getFirstChild().getNodeValue();                       //sku
                        String smallImage = attrs.getElementsByTagName("ns2:SmallImage").item(0).getFirstChild().getFirstChild().getNodeValue();       //图片地址
                        String asin = product.getIdentifiers().getMarketplaceASIN().getASIN();                                                                 //自己的asin

                        List<Object> any = product.getRelationships().getAny();

                        AmazonPublishListing amazonPublishListing = null;


                        // todo 这里改成全部走单属性  if (any == null || any.size() == 0)
                        if (any == null || any.size() == 0) {       //单属性 直接将本条数据存入品连数据库中
                            amazonPublishListing = createAmazonPublishListing(null, empower, object, AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE, asin, AmazonConstants.PUBLISH_TYPE_ONLY, simeDetail.getSku(), smallImage,simeDetail.getPortTime(),marketplaceId);
                        } else {                                  //多属性
                            Object o = any.get(0);
                            DeferredElementNSImpl ob = (DeferredElementNSImpl) o;
                            String relationShipName = ob.getLocalName();
                            if (relationShipName.equalsIgnoreCase(AmazonPublishEnums.getForIdRelationShips.CHILD.getCode())) {                                //本身是子体数据，要去寻找其对应的父体数据进行添加

                                //      ---------------

                                String fatherASIN = ob.getFirstChild().getFirstChild().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
                                ArrayList<String> newIds = new ArrayList<>();
                                newIds.add(fatherASIN);
                                List<GetMatchingProductForIdResult> faResponse = getMatchingProductList.sendAmazon(detail.getMerchantId(), token, detail.getMarketplaceId(), KeyValueConts.amazonProductIdTye.ASIN.getType(), newIds);
                                if (faResponse.get(0).getError() != null || !faResponse.get(0).getStatus().equalsIgnoreCase("success")) {
                                    this.forUpdateDetail(sendList, result, AmazonPublishEnums.isExist.ERROR.getCode(), "其夫asin为：" + fatherASIN + " ,但是在亚马逊没有数据");
                                } else {
                                    Product father = faResponse.get(0).getProducts().getProduct().get(0);
                                    DeferredElementNSImpl faAttrs = (DeferredElementNSImpl) father.getAttributeSets().getAny().get(0);
                                    // String faSku = faAttrs.getElementsByTagName("ns2:PartNumber").item(0).getFirstChild().getNodeValue();                       //sku
                                    String faSmallImage = faAttrs.getElementsByTagName("ns2:SmallImage").item(0).getFirstChild().getFirstChild().getNodeValue();       //图片地址
                                    String faAsin = father.getIdentifiers().getMarketplaceASIN().getASIN();                                                                 //自己的asin


                                    String faSku = null;         //   TODO    这里需要去listing表中和要等记录表中 通过asin查询 其faSku，如果没有查询到 要么按单属性处理 要不先不处理

                                    AmazonPublishSubListing subPar = new AmazonPublishSubListing() {{
                                        setMerchantId(simeDetail.getMerchantId());
                                        setMarketplaceId(simeDetail.getMarketplaceId());
                                        setAsin(faAsin);
                                    }};
                                    List<AmazonPublishSubListing> amazonPublishSubListings = amazonPublishSubListingService.selectPage(subPar);
                                    if(amazonPublishSubListings != null && amazonPublishSubListings.size() > 0){
                                        faSku = amazonPublishSubListings.get(0).getSku();
                                    }else {
                                        AmazonPublishReportDetail rePortPar = new AmazonPublishReportDetail();
                                        rePortPar.setMerchantId(simeDetail.getMerchantId());
                                        rePortPar.setMarketplaceId(simeDetail.getMarketplaceId());
                                        rePortPar.setContent("\"asin1\":\""+faAsin +"\"");
                                        AmazonPublishReportDetail detail1 = amazonPublishReportDetailService.selectLastOne(rePortPar);
                                        if(detail1 != null)
                                            faSku = detail1.getSku();
                                    }

                                    if(StringUtils.isNotBlank(faSku)){
                                        listing.setPlatformSku(faSku);
                                        listings = amazonPublishListingService.findList(listing);
                                        if (listings != null && listings.size() > 0) {                       //通过亚马逊其夫sku查询发现其父亲数据本地已存在
                                            AmazonPublishListing fatherListing = listings.get(0);
                                            String publishMessage = fatherListing.getPublishMessage();
                                            AmazonRequestProduct requestProduct = JSONObject.parseObject(publishMessage, AmazonRequestProduct.class);
                                            AmazonRequestProduct subRequestProduct = this.createRequestProduct(empower, object, asin, smallImage,marketplaceId);
                                            List varRequestProductList = requestProduct.getVarRequestProductList();
                                            if (varRequestProductList == null || varRequestProductList.size() == 0) {
                                                ArrayList<AmazonRequestProduct> amazonRequestProducts = new ArrayList<>();
                                                amazonRequestProducts.add(subRequestProduct);
                                                requestProduct.setVarRequestProductList(amazonRequestProducts);
                                            } else {
                                                varRequestProductList.add(subRequestProduct);
                                            }
                                            requestProduct.setIsMultiattribute(true);

                                            ObjectMapper mapper = new ObjectMapper();
                                            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                                            String publishMessageJson="";
                                            try {
                                                publishMessageJson=mapper.writeValueAsString(requestProduct);
                                            } catch (JsonProcessingException e) {
                                                logger.error("同步生成本地Listing数据装换json publishMessage异常",e);
                                            }


                                            fatherListing.setPublishMessage(publishMessageJson);
                                            fatherListing.setPublishType(AmazonConstants.PUBLISH_TYPE_MORE);
                                            amazonPublishListingService.updateByPrimaryKeySelective(fatherListing);
                                            requestProduct.setId(fatherListing.getId());
                                            AmazonPublishSubListing sub = new ProcessXmlDraftTask(requestProduct).createSub(requestProduct, subRequestProduct, new Date(), AmazonConstants.PARENT_TYPE_NO, redisUtils, detail.getMarketplaceId());
                                            ArrayList<AmazonPublishSubListing> subListing = new ArrayList<AmazonPublishSubListing>() {{
                                                add(sub);
                                            }};
                                            amazonSubListingUtil.setPlSkuStatusAndCount(subListing,null);

                                            amazonPublishSubListingService.insertBatch(subListing);
                                            amazonPublishListing = null;

                                            this.forUpdateDetail(sendList, result, AmazonPublishEnums.isExist.EXIST.getCode(), "本地已添加");


                                        } else {  //其父数据本地不存在

                                            param.setMerchantId(detail.getMerchantId());
                                            param.setMarketplaceId(detail.getMarketplaceId());
                                            param.setSku(faSku);
                                            param.setIsExist(null);
                                            AmazonPublishReportDetail fatherReport = amazonPublishReportDetailService.selectLastOne(param);
                                            if (fatherReport == null) {
                                                this.forUpdateDetail(sendList, result, AmazonPublishEnums.isExist.ERROR.getCode(), "其夫sku为：" + faSku + " ,但是本地没有数据也没有报告数据");
                                            } else {
                                                String content = fatherReport.getContent();
                                                if (StringUtils.isBlank(content)) {
                                                    this.forUpdateDetail(sendList, result, AmazonPublishEnums.isExist.ERROR.getCode(), "其夫sku为：" + faSku + " ,但是其没有报告内容");
                                                    continue;
                                                }

                                                JSONObject fatherObj;
                                                try {
                                                    fatherObj = JSONObject.parseObject(content);
                                                } catch (Exception e) {
                                                    simeDetail.setIsExist(AmazonPublishEnums.isExist.ERROR.getCode());   //  todo
                                                    simeDetail.setMessage("其夫sku为：" + faSku + " ,解析报告json失败");
                                                    amazonPublishReportDetailService.updateByPrimaryKeySelective(simeDetail);
                                                    continue;
                                                }

                                                amazonPublishListing = this.createAmazonPublishListing(null, empower, fatherObj, AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE, faAsin, AmazonConstants.PUBLISH_TYPE_ONLY, faSku, faSmallImage,simeDetail.getPortTime(),marketplaceId);
                                                AmazonRequestProduct subRequestProduct = this.createRequestProduct(empower, object, asin, smallImage,marketplaceId);

                                                String publishMessage = amazonPublishListing.getPublishMessage();
                                                AmazonRequestProduct faRequestProduct = JSONObject.parseObject(publishMessage, AmazonRequestProduct.class);

                                                ArrayList<AmazonRequestProduct> amazonRequestProducts = new ArrayList<>();
                                                amazonRequestProducts.add(subRequestProduct);
                                                faRequestProduct.setVarRequestProductList(amazonRequestProducts);
                                                faRequestProduct.setIsMultiattribute(true);
                                                ObjectMapper mapper = new ObjectMapper();
                                                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                                                String publishMessageJson="";
                                                try {
                                                    publishMessageJson=mapper.writeValueAsString(faRequestProduct);
                                                } catch (JsonProcessingException e) {
                                                    logger.error("同步生成本地父体Listing数据装换json publishMessage异常",e);
                                                }


                                                amazonPublishListing.setPublishMessage(publishMessageJson);
                                                amazonPublishListing.setPublishType(AmazonConstants.PUBLISH_TYPE_MORE);
                                            }
                                        }
                                    }else {
                                        amazonPublishListing = createAmazonPublishListing(null, empower, object, AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE, asin, AmazonConstants.PUBLISH_TYPE_ONLY, simeDetail.getSku(), smallImage,simeDetail.getPortTime(),marketplaceId);
                                    }

                                }

                                // --------------------

                            } else if (relationShipName.equalsIgnoreCase(AmazonPublishEnums.getForIdRelationShips.PARENT.getCode())) {                         //本身是父体数据，直接封装并添加带数据库中
                                amazonPublishListing = createAmazonPublishListing(null, empower, object, AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE, asin, AmazonConstants.PUBLISH_TYPE_ONLY, simeDetail.getSku(), smallImage,simeDetail.getPortTime(),marketplaceId);
                            }


                        }

                        if (amazonPublishListing != null) {
                            try {
                                int i = amazonPublishListingService.insertSelective(amazonPublishListing);
                                String publishMessage = amazonPublishListing.getPublishMessage();
                                AmazonRequestProduct requestProduct = JSONObject.parseObject(publishMessage, AmazonRequestProduct.class);
                                requestProduct.setId(amazonPublishListing.getId());
                                ExecutorService executor = Executors.newFixedThreadPool(1);
                                ProcessXmlDraftTask processXmlDraftTask = new ProcessXmlDraftTask(requestProduct, amazonSubListingUtil);
                                Future<String> futureResult = executor.submit(processXmlDraftTask);
                                String errorMsg = futureResult.get();
                                executor.shutdown();
                                if (errorMsg != null && errorMsg.length() > 0) {
                                    logger.warn("同步添加sub数据时异常，中止线操作，数据被手动删除，被删除的ID为：{}", amazonPublishListing.getId());
                                    amazonPublishSubListingService.deleteForBaseId(amazonPublishListing.getId());
                                    amazonPublishListingService.deleteByPrimaryKey(amazonPublishListing.getId());
                                    this.forUpdateDetail(sendList, result, AmazonPublishEnums.isExist.ERROR.getCode(), "添加子数据异常");
                                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601, errorMsg);
                                }else {
                                    this.forUpdateDetail(sendList, result, AmazonPublishEnums.isExist.EXIST.getCode(), "本地已添加");
                                }
                            } catch (Exception e) {
                                logger.error("同步生成sub数据时异常", e);
                            }
                        }
                    }catch (Exception e){
                        logger.error("sku 为： " +result.getId() + " 的数据同步数据会本地失败",e);
                        this.forUpdateDetail(sendList, result, AmazonPublishEnums.isExist.ERROR.getCode(), "同步数据回本地异常");
                    }
                }
            }



        }catch (Exception e){
            logger.error("同步本地不存在亚马逊刊登异常",e);
        }finally {
            redissLockUtil.unlock(lockKey); // 解放锁
        }
    }


    /**
     * 生成单属性或者多属性父体
     *
     * @param list    list
     * @param empower 授权数据
     * @param object  亚马逊同步数据
     * @return listing结果
     */
    private AmazonPublishListing createAmazonPublishListing(AmazonPublishListing list, Empower empower, JSONObject object, Integer publishStatus, String asin, Integer publishType, String sku, String smallImage,String startTime, MarketplaceId marketplaceId) {
        String conty = "aa";
        String title = object.getString("item-name");
        if(StringUtils.isBlank(title) && marketplaceId.getCountryCode().equalsIgnoreCase("JP")){
            title = object.getString("商品名");
        }

        if(StringUtils.isNotBlank(title)){
            conty = marketplaceId.getCountryCode();
        }


        list = new AmazonPublishListing();
        list.setPlAccount(empower.getPinlianAccount());
        list.setTitle(title);
        list.setPlatformSku(sku);
        list.setAsin(asin);
        list.setPublishSite(MarketplaceIdList.resourceMarketplaceForKeyId().get(empower.getWebName()).getCountryCode());
        list.setPublishAccount(empower.getAccount());
        list.setPublishType(publishType);
        list.setPublishStatus(publishStatus);
        //list.setRemark("亚马逊同步数据,ope-date:" + object.getString("open-date"));
        list.setPlSku("亚马逊同步数据");
        list.setAmwToken(empower.getToken());
        list.setMerchantIdentifier(empower.getThirdPartyName());
        list.setDataSource(2);
        Date date = new Date();
        list.setCreateTime(date);
        list.setUpdateTime(date);
        list.setBatchNo(UUID.randomUUID().toString());
        list.setProductImage(smallImage);
        Date date1 = DateUtils.parseDate(startTime, DateUtils.FORMAT_2);
        date1 = DateUtils.getBeforeDateByDay(date1,1);
        list.setOnlineTime(date);
        list.setUpdateStatus(AmazonPublishUpdateStatus.NOT_UPDATE);

        AmazonRequestProduct requestProduct = new AmazonRequestProduct();
        requestProduct.setMerchantIdentifier(list.getMerchantIdentifier());
        requestProduct.setCountryCode(list.getPublishSite());
        requestProduct.setSku(list.getPlatformSku());
        requestProduct.setQuantity(object.getLong("quantity"));
        if(conty.equalsIgnoreCase("JP")){
            requestProduct.setQuantity(object.getLong("数量"));
        }
       /* String price = object.getString("price");
        if(StringUtils.isBlank(price))
            price = "0";
        requestProduct.setStandardPrice(new BigDecimal(price));*/
        requestProduct.setStandardPriceUnit(marketplaceId.getCurrency());
        requestProduct.setStandardPrice(BigDecimal.valueOf(object.getDouble("price") == null?0:object.getDouble("price")));
        if(conty.equalsIgnoreCase("JP")){
            requestProduct.setStandardPrice(BigDecimal.valueOf(object.getDouble("価格") == null?0:object.getDouble("価格")));
        }
        requestProduct.setTitle(title);
        requestProduct.setDescription(object.getString("item-description"));
        requestProduct.setBatchNo(list.getBatchNo());
        requestProduct.setAsin(list.getAsin());
        requestProduct.setPlSku("亚马逊同步数据");
        requestProduct.setIsMultiattribute(publishType.equals(AmazonConstants.PUBLISH_TYPE_MORE));

        ProductImage productImage = new ProductImage();
        productImage.setSKU(list.getPlatformSku());
        productImage.setImageLocation(smallImage);
        productImage.setImageType("Main");
        ArrayList<ProductImage> productImages = new ArrayList<>();
        productImages.add(productImage);
        requestProduct.setImages(productImages);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String publishMessageJson="";
        try {
            publishMessageJson=mapper.writeValueAsString(requestProduct);
        } catch (JsonProcessingException e) {
            logger.error("同步生成本地Listing数据装换json publishMessage异常",e);
        }


        list.setPublishMessage(publishMessageJson);
        return list;
    }

    /**
     * 生成多属性子体数据
     *
     * @param empower 授权数据
     * @param object  子体对应的报告数据
     * @param asin    asin码
     */
    private AmazonRequestProduct createRequestProduct(Empower empower, JSONObject object, String asin, String smallImage, MarketplaceId marketplaceId) {
        String conty = "aa";
        String title = object.getString("item-name");
        if(StringUtils.isBlank(title) && marketplaceId.getCountryCode().equalsIgnoreCase("JP")){
            title = object.getString("商品名");
        }

        if(StringUtils.isNotBlank(title)){
            conty = marketplaceId.getCountryCode();
        }

        AmazonRequestProduct requestProduct = new AmazonRequestProduct();
        requestProduct.setMerchantIdentifier(empower.getThirdPartyName());
        requestProduct.setCountryCode(MarketplaceIdList.resourceMarketplaceForKeyId().get(empower.getWebName()).getCountryCode());
        requestProduct.setSku(object.getString("seller-sku"));
        if(conty.equalsIgnoreCase("JP")){
            requestProduct.setSku(object.getString("出品者SKU"));
        }
        requestProduct.setQuantity(object.getLong("quantity"));
        if(conty.equalsIgnoreCase("JP")){
            requestProduct.setQuantity(object.getLong("数量"));
        }
       // String price = object.getString("price");
       /* if(StringUtils.isBlank(price))
            price = "0";
        requestProduct.setStandardPrice(new BigDecimal(price));*/
       requestProduct.setStandardPriceUnit(marketplaceId.getCurrency());
        requestProduct.setStandardPrice(BigDecimal.valueOf(object.getDouble("price") == null?0:object.getDouble("price")));
        if(conty.equalsIgnoreCase("JP")){
            requestProduct.setStandardPrice(BigDecimal.valueOf(object.getDouble("価格") == null?0:object.getDouble("価格")));
        }
        requestProduct.setTitle(title);
        requestProduct.setDescription(object.getString("item-description"));
        //requestProduct.setBatchNo(list.getBatchNo());
        requestProduct.setAsin(asin);
        requestProduct.setPlSku("亚马逊同步数据");

        ProductImage productImage = new ProductImage();
        productImage.setSKU(requestProduct.getSku());
        productImage.setImageLocation(smallImage);
        productImage.setImageType("Main");
        ArrayList<ProductImage> productImages = new ArrayList<>();
        productImages.add(productImage);
        requestProduct.setImages(productImages);

        return requestProduct;
    }


    private void toError(ArrayList<AmazonPublishReportDetail> sendList, ArrayList<String> skus, Integer isExist, String message) {
        for (AmazonPublishReportDetail a : sendList) {
            for(String sku : skus){
                if(sku.equalsIgnoreCase(a.getSku())){
                    a.setIsExist(isExist);
                    a.setMessage(message);
                    amazonPublishReportDetailService.updateByPrimaryKeySelective(a);
                }
            }
        }
    }

    private void forUpdateDetail(ArrayList<AmazonPublishReportDetail> sendList, GetMatchingProductForIdResult result, Integer isExist, String message) {
        for (AmazonPublishReportDetail a : sendList) {
            if (a.getSku().equalsIgnoreCase(result.getId())) {
                a.setIsExist(isExist);
                a.setMessage(message);
                amazonPublishReportDetailService.updateByPrimaryKeySelective(a);
            }
        }
    }

    private AmazonPublishReportDetail getSimeDetail(ArrayList<AmazonPublishReportDetail> sendList, GetMatchingProductForIdResult result) {
        for (AmazonPublishReportDetail a : sendList) {
            if (a.getSku().equalsIgnoreCase(result.getId())) {
                return a;
            }
        }
        return null;
    }


    @Override
    public void run() {
        this.process();
    }


    public static void main(String [] ss){
  /*      String s = "{\"batchNo\":\"fbafdc70-af84-4ffd-a263-3292dfa2d5b0\",\"images\":[{\"sKU\":\"tanbea|M-1-2DFD5549-853674|UK|579\",\"imageType\":\"Main\",\"imageLocation\":\"http://g-ecx.images-amazon.com/images/G/29/x-site/icons/no-img-sm._CB1547651849_._SL75_.gif\"}],\"quantity\":\"0\",\"searchTerms\":[],\"supplierDeclaredDGHZRegulation\":[],\"description\":\"<p>???? yuzhou a a a a a a a ???</p>\",\"standardPrice\":\"\",\"productCategoryForInteger\":[],\"title\":\"yuzhou\",\"varRequestProductList\":[],\"merchantIdentifier\":\"A9O6B3N4CJQ7B\",\"bulletPoint\":[],\"targetAudience\":[],\"countryCode\":\"IT\",\"asin\":\"B07SNCBQ1T\",\"isMultiattribute\":false,\"sku\":\"tanbea|M-1-2DFD5549-853674|UK|579\",\"plSku\":\"亚马逊同步数据\"}";
        JSONObject object = JSONObject.parseObject(s);
        Double price = object.getDouble("price");
        BigDecimal bigDecimal = BigDecimal.valueOf(object.getDouble("price") == null ? 0 : object.getDouble("price"));
        AmazonRequestProduct requestProduct = new AmazonRequestProduct();
        requestProduct.setStandardPrice(BigDecimal.valueOf(object.getDouble("price") == null ? 0 : object.getDouble("price")));
        String s1 = JSONObject.toJSONString(requestProduct);


        System.out.println(price);*/

            JSONObject object = new JSONObject();
            object.put("price","");
            Double v = object.getDouble("price");
            System.out.println(object.toString());
            // == null ? 0 : object.getDouble("price");


        AmazonRequestProduct requestProduct = new AmazonRequestProduct();
        requestProduct.setSku("11111111111");
        ProductImage productImage = new ProductImage();
        productImage.setSKU(requestProduct.getSku());
        productImage.setImageLocation("11111111111111");
        productImage.setImageType("Main");
        ArrayList<ProductImage> productImages = new ArrayList<>();
        productImages.add(productImage);
        requestProduct.setImages(productImages);
        String s = JSONObject.toJSONString(requestProduct);

        Abbb abbb = new Abbb();
        abbb.setAaa("11111111111");
        abbb.setBBB("bbbbbbbbbbbb");


        String s2 = JSONObject.toJSONString(abbb);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String publishMessageJson="";
        try {
            publishMessageJson=mapper.writeValueAsString(abbb);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String str = "{\"searchTerms\":[\"xuexing kongbu yixing\"],\"packageWeight\":101.00,\"supplierDeclaredDGHZRegulation\":[\"\"],\"dimensionLength\":10.00,\"description\":\"\",\"title\":\"???? shouban?duoshuxing1?\",\"productCategoryForInteger\":[291385],\"productCategory\":[291385],\"productCategory1Path\":\"汽车  》 婴儿汽车座椅及配件  》 汽车座椅\",\"manufacturer\":\"海信大酒店\",\"standardProductID\":\"\",\"varRequestProductList\":[{\"standardProductType\":\"UPC\",\"images\":[{\"sku\":\"ecrhl7cAwYBGEK831kwpnOagM5z\",\"imageType\":\"Main\",\"imageLocation\":\"https://testseller.brandslink.com/EditPublishAmazon?amazonEdit=111717&status=1\"}],\"quantity\":\"1\",\"searchTerms\":[],\"supplierDeclaredDGHZRegulation\":[],\"standardPrice\":\"11111\",\"standardPriceUnit\":\"GBP\",\"title\":\"???? shouban?duoshuxing3?\",\"productCategoryForInteger\":[],\"standardProductID\":\"756319909069\",\"varRequestProductList\":[],\"bulletPoint\":[],\"categoryPropertyJson\":\"{\\\"classificationData\\\":{},\\\"parentage\\\":\\\"child\\\",\\\"productType\\\":{\\\"autoAccessoryMisc\\\":{\\\"variationData\\\":{\\\"variationTheme\\\":\\\"Size-Color\\\",\\\"parentage\\\":\\\"child\\\",\\\"size\\\":\\\"3\\\",\\\"sizeMap\\\":\\\"Small\\\",\\\"colorSpecification\\\":{\\\"color\\\":\\\"1\\\",\\\"colorMap\\\":\\\"2\\\"}},\\\"modelYear\\\":\\\"2\\\",\\\"colorSpecification\\\":{},\\\"itemPackageQuantity\\\":\\\"2\\\",\\\"numberOfItems\\\":\\\"2\\\",\\\"viscosity\\\":\\\"2\\\",\\\"partInterchangeData\\\":{},\\\"modelName\\\":\\\"2\\\",\\\"transmissionType\\\":\\\"2\\\"}},\\\"battery\\\":{\\\"batterySubgroup\\\":[{}]},\\\"tireType\\\":\\\"2\\\"}\",\"mfrPartNumber\":\"ecrhl7cAwYBGEK831kwpnOagM5z\",\"targetAudience\":[],\"asin\":\"B07VVNVQGD\",\"isMultiattribute\":false,\"sku\":\"ecrhl7cAwYBGEK831kwpnOagM5z\",\"plSku\":\"46689024003\"},{\"standardProductType\":\"UPC\",\"images\":[{\"sku\":\"Ci9Uf9jM5fjizCFwQj6bi\",\"imageType\":\"Main\",\"imageLocation\":\"https://testseller.brandslink.com/EditPublishAmazon?amazonEdit=111717&status=1\"}],\"quantity\":\"1\",\"searchTerms\":[],\"supplierDeclaredDGHZRegulation\":[],\"standardPrice\":\"11111\",\"standardPriceUnit\":\"GBP\",\"title\":\"???? shouban?duoshuxing4?\",\"productCategoryForInteger\":[],\"standardProductID\":\"784261911434\",\"varRequestProductList\":[],\"bulletPoint\":[],\"categoryPropertyJson\":\"{\\\"classificationData\\\":{},\\\"parentage\\\":\\\"child\\\",\\\"productType\\\":{\\\"autoAccessoryMisc\\\":{\\\"variationData\\\":{\\\"variationTheme\\\":\\\"Size-Color\\\",\\\"parentage\\\":\\\"child\\\",\\\"size\\\":\\\"6\\\",\\\"sizeMap\\\":\\\"Medium\\\",\\\"colorSpecification\\\":{\\\"color\\\":\\\"4\\\",\\\"colorMap\\\":\\\"5\\\"}},\\\"modelYear\\\":\\\"2\\\",\\\"colorSpecification\\\":{},\\\"itemPackageQuantity\\\":\\\"2\\\",\\\"numberOfItems\\\":\\\"2\\\",\\\"viscosity\\\":\\\"2\\\",\\\"partInterchangeData\\\":{},\\\"modelName\\\":\\\"2\\\",\\\"transmissionType\\\":\\\"2\\\"}},\\\"battery\\\":{\\\"batterySubgroup\\\":[{}]},\\\"tireType\\\":\\\"2\\\"}\",\"mfrPartNumber\":\"Ci9Uf9jM5fjizCFwQj6bi\",\"targetAudience\":[],\"asin\":\"B07VYX37S1\",\"isMultiattribute\":false,\"sku\":\"Ci9Uf9jM5fjizCFwQj6bi\",\"plSku\":\"46689024004\"}],\"merchantIdentifier\":\"A9O6B3N4CJQ7B\",\"mfrPartNumber\":\"1452|46689024|134\",\"countryCode\":\"GB\",\"id\":111717,\"sku\":\"1452|46689024|134\",\"plSku\":\"46689024\",\"brand\":\"三禾大神\",\"dimensionUnitOfMeasure\":\"CM\",\"itemWeight\":100.00,\"standardProductType\":\"UPC\",\"images\":[{\"sku\":\"1452|46689024|134\",\"imageType\":\"Main\",\"imageLocation\":\"https://testseller.brandslink.com/EditPublishAmazon?amazonEdit=111717&status=1\"}],\"batchNo\":\"c78406ee-8963-4906-a3e7-95d1ca1e7939\",\"dimensionHeight\":10.00,\"conditionInfo\":{\"conditionNote\":\"\",\"conditionType\":\"New\"},\"quantity\":\"0\",\"fulfillmentLatency\":2,\"templatesName\":\"autoAccessory\",\"logisticsCode\":\"\",\"dimensionWidth\":10.00,\"standardPrice\":\"\",\"standardPriceUnit\":\"GBP\",\"weightUnitOfMeasure\":\"GR\",\"templatesName2\":\"autoAccessoryMisc\",\"bulletPoint\":[\"yixing,xuexing,kongbu\"],\"categoryPropertyJson\":\"{\\\"classificationData\\\":{},\\\"parentage\\\":\\\"parent\\\",\\\"productType\\\":{\\\"autoAccessoryMisc\\\":{\\\"variationData\\\":{\\\"variationTheme\\\":\\\"Size-Color\\\",\\\"parentage\\\":\\\"parent\\\"},\\\"modelYear\\\":\\\"2\\\",\\\"colorSpecification\\\":{},\\\"itemPackageQuantity\\\":\\\"2\\\",\\\"numberOfItems\\\":\\\"2\\\",\\\"viscosity\\\":\\\"2\\\",\\\"partInterchangeData\\\":{},\\\"modelName\\\":\\\"2\\\",\\\"transmissionType\\\":\\\"2\\\"}},\\\"battery\\\":{\\\"batterySubgroup\\\":[{}]},\\\"tireType\\\":\\\"2\\\"}\",\"targetAudience\":[\"\"],\"warehouseId\":531,\"spu\":\"46689024\",\"asin\":\"B07W1WHW4J\",\"isMultiattribute\":true,\"logisticsType\":2}";

         object = JSONObject.parseObject(str);
        String s1 = JSONObject.toJSONString(object);


        System.out.println(s);


    }


    public static class Abbb{
        private String aaa;

        private String bbb;

        public String getAaa() {
            return aaa;
        }

        public void setAaa(String aaa) {
            this.aaa = aaa;
        }

        public String getBBB() {
            return bbb;
        }

        public void setBBB(String bbb) {
            this.bbb = bbb;
        }
    }





}
