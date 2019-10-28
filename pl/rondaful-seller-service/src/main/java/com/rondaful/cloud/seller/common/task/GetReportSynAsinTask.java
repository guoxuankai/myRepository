package com.rondaful.cloud.seller.common.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.seller.common.mws.intface.GetReport;
import com.rondaful.cloud.seller.constants.PublishRequestReport;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.entity.amazon.AmazonPublishListStatus;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.enums.AmazonPublishEnums;
import com.rondaful.cloud.seller.enums.ReportTypeEnum;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.mapper.EmpowerMapper;
import com.rondaful.cloud.seller.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 获取报告结果
 */
@Component
public class GetReportSynAsinTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(GetReportSynAsinTask.class);
    @Autowired
    private RedissLockUtil redissLockUtil;

    @Autowired
    private GetReport getReport;

    @Autowired
    private AmazonPublishReportService amazonPublishReportService;

    @Autowired
    private AmazonPublishReportDetailService amazonPublishReportDetailService;

    @Autowired
    private AmazonPublishListingService amazonPublishListingService;

    @Autowired
    private AmazonPublishSubListingService amazonPublishSubListingService;

    @Autowired
    private EmpowerMapper empowerMapper;

    @Autowired
    private AmazonPublishReportTimeService amazonPublishReportTimeService;

    private static final String upListBeforeKey = "AmazonGetReport_";
    private static final String upListBeforeKeyIP = "AmazonGetReport_IP_";


    @Override
    public void run() {
        process(ReportTypeEnum._GET_MERCHANT_LISTINGS_DATA_);
    }

    // private static boolean flag = true;


    /**
     * 获取报告结果
     *
     * @param reportType 报告类型
     */
    public void process(ReportTypeEnum reportType) {
        logger.info("开始执行获取报告结果，报告类型：{}", reportType.getReportTyp());
        String addr = null;
        try {
            addr = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            logger.error("获取服务器IP异常", e);
            return;
        }

        String localIpKey = upListBeforeKeyIP + reportType.getReportTyp() + PublishRequestReport.reportProgress.GetReport.getTheInterface() + "_" + addr;
        logger.info("服务器获取亚马逊报告类容IpKey: {}", localIpKey);
        if (!redissLockUtil.tryLock(localIpKey, 10, 60 * 10)) //等待10秒，10分放开锁
        {
            logger.debug(localIpKey + " 本服务正在执行(获取报告结果) 。。 ");
            return;
        }


        /*if (!flag) {
            logger.info("开始执行获取报告结果，(机器还在执行)报告类型：{}", reportType.getReportTyp());
            return;
        } else {
            logger.info("开始执行获取报告结果，(机器开始执行)报告类型：{}", reportType.getReportTyp());
            flag = false;
        }*/
        try {
            AmazonPublishReport report = new AmazonPublishReport();
            report.setReportType(reportType.getReportTyp());
            report.setReportStatus(PublishRequestReport.reportStatus.REPORT_ID.getStatus());
            AmazonPublishReport amazonPublishReport = amazonPublishReportService.selectFirstOne(report);
            if (amazonPublishReport == null)
                return;
            if (StringUtils.isBlank(amazonPublishReport.getGeneratedReportId())) {
                amazonPublishReport.setReportStatus(PublishRequestReport.reportStatus.PUBLISH.getStatus());
                amazonPublishReportService.updateByPrimaryKeySelective(amazonPublishReport);
                return;
            }

            String marketplaceId = amazonPublishReport.getMarketplaceId();
            String merchant = amazonPublishReport.getMerchantId();
            String token = amazonPublishReport.getMwsauthToken();
            String reportId = amazonPublishReport.getGeneratedReportId();


            String lockKey = upListBeforeKey + reportType.getReportTyp() + PublishRequestReport.reportProgress.GetReport.getTheInterface() + "_" + merchant;
            if (!redissLockUtil.tryLock(lockKey, 10, 60 * 10)) //等待10秒，10分放开锁
            {
                logger.debug(lockKey + " 其它服务正在执行。locking....");
                return;
            }
            File file = new File("/logs/rondaful-seller-service/" + reportId + ".txt");   //String filePath = "/logs/rondaful-order-service/"+ feedSubmissionId +".xml";
            try {
                Date startDate = new Date();
                amazonPublishReport.setReportStatus(PublishRequestReport.reportStatus.OPERATION.getStatus());
                amazonPublishReportService.updateByPrimaryKeySelective(amazonPublishReport);
                JSONObject object = new JSONObject();
                AmazonRequestReportResult invoke = getReport.invoke(marketplaceId, merchant, reportType, token, reportId, file, object);
                if (StringUtils.isBlank(invoke.getErrorCode())) {
                    //todo 成功，解析数据
                    MyInteger size = new MyInteger();
                    int i = this.synchronousListing(file, marketplaceId, merchant, size, reportId, object.getString("charSet"), amazonPublishReport.getBeginTime(), startDate);
                    if (i == 0) {
                        Date endDate = new Date();
                        amazonPublishReport.setReportStatus(PublishRequestReport.reportStatus.FINISH.getStatus());
                        amazonPublishReport.setErrorMessage("共：" + size.getL() + "  条数据，耗时(秒) ：" + (endDate.getTime() - startDate.getTime()) / 1000);
                        amazonPublishReportService.updateByPrimaryKeySelective(amazonPublishReport);
                    } else if (i == 3) {   //数据异常没有ASIN码
                        amazonPublishReport.setReportStatus(PublishRequestReport.reportStatus.NO_ASIN.getStatus());
                        amazonPublishReport.setErrorMessage("报告没有ASIN码");
                        amazonPublishReportService.updateByPrimaryKeySelective(amazonPublishReport);
                    }
                    if (size.getVersion() != null && size.getVersion() != 0) {
                        AmazonPublishReportDetail detail = new AmazonPublishReportDetail();
                        detail.setVersion(size.getVersion());
                        detail.setMerchantId(merchant);
                        detail.setMarketplaceId(marketplaceId);
                        amazonPublishReportDetailService.deleteByVersion(detail);
                    }
                    AmazonPublishReportTime amazonPublishReportTime = new AmazonPublishReportTime();
                    amazonPublishReportTime.setMarketplaceId(marketplaceId);
                    amazonPublishReportTime.setMerchantId(merchant);
                    MarketplaceId id = MarketplaceIdList.createMarketplaceForKeyId().get(marketplaceId);
                    if (id != null) {
                        amazonPublishReportTime.setPublishSite(id.getCountryCode());
                    } else {
                        amazonPublishReportTime.setPublishSite("AA");
                    }
                    String beginTime = amazonPublishReport.getBeginTime();
                    amazonPublishReportTime.setReportTime(DateUtils.parseDate(beginTime, DateUtils.FORMAT_2));
                    amazonPublishReportTime.setUpdateTime(startDate);
                    amazonPublishReportTime.setCreateTime(startDate);
                    amazonPublishReportTimeService.addOrUpdateReportTime(amazonPublishReportTime);
                } else {
                    //失败
                    amazonPublishReport.setReportStatus(PublishRequestReport.reportStatus.REPORT_ERROR.getStatus());
                    amazonPublishReport.setErrorMessage(invoke.getResultDescription());
                    amazonPublishReportService.updateByPrimaryKeySelective(amazonPublishReport);
                }
            } catch (Exception e) {
                logger.error("获取报告结果异常", e);
                amazonPublishReport.setReportStatus(PublishRequestReport.reportStatus.REPORT_ERROR.getStatus());
                amazonPublishReport.setErrorMessage("报告以获取，但解析异常");
                amazonPublishReportService.updateByPrimaryKeySelective(amazonPublishReport);
            } finally {
                logger.debug("释放同步锁.");
                redissLockUtil.unlock(lockKey); // 解放锁
                boolean delete = file.delete();
            }
        } catch (Exception e) {
            logger.error("获取报告结果之前异常", e);
        } finally {
            logger.debug("释放本机锁（获取刊登类容）.");
            redissLockUtil.unlock(localIpKey);
        }

    }

    public class MyInteger {
        private Integer l;

        private Long version;

        public Integer getL() {
            return l;
        }

        public void setL(Integer l) {
            this.l = l;
        }

        public Long getVersion() {
            return version;
        }

        public void setVersion(Long version) {
            this.version = version;
        }
    }

    /**
     * 同步数据
     *
     * @param file 文件
     */
    private int synchronousListing(File file, String marketplaceId, String merchant, MyInteger size, String greId, String charSet, String portTime, Date date) throws IOException {
        FileReader fr = null;
        BufferedReader bf = null;
        int re = 0;
        try {
            bf = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), StringUtils.isBlank(charSet) ? "Cp1252" : charSet));
            fr = new FileReader(file);
            String str;
            ArrayList<JSONObject> jsons = new ArrayList<>();
            JSONObject json;
            String[] split1 = new String[0];
            int i = 1;
            while ((str = bf.readLine()) != null) {
                String[] split = str.split("\\t");
                if (i == 1) {
                    split1 = split;
                } else {
                    json = new JSONObject();
                    for (int j = 0; j < split.length; j++) {   //Content-Type: text/plain;charset=Cp1252 TODO  此处乱码 尝试 URLEncoder 编码解码
                        json.put(split1[j], split[j]);
                    }
                    if (i == 2) {
                        if (StringUtils.isBlank(json.getString("asin1")) && StringUtils.isBlank(json.getString("asin")))
                            re = 3;
                    }
                    jsons.add(json);
                }
                i++;
            }
            size.setL(jsons.size());
            if (size.getL() > 0) {   //当存在同步数据时进行同步

                //sub
                AmazonPublishListing param = new AmazonPublishListing();
                param.setMerchantIdentifier(merchant);
                MarketplaceId marketplaceId1 = MarketplaceIdList.resourceMarketplaceForKeyId().get(marketplaceId);
                param.setPublishSite(marketplaceId1.getCountryCode());
                List<AmazonPublishListing> list;
                String sku;

                AmazonPublishReportDetail reportDetail = new AmazonPublishReportDetail();
                reportDetail.setMarketplaceId(marketplaceId);
                reportDetail.setMerchantId(merchant);

                AmazonPublishReportDetail amazonPublishReportDetail = amazonPublishReportDetailService.selectLastOne(reportDetail);
                if (amazonPublishReportDetail == null || amazonPublishReportDetail.getVersion() == null) {
                    reportDetail.setVersion(0L);
                    size.setVersion(0L);
                } else {
                    long l = amazonPublishReportDetail.getVersion() + 1;
                    size.setVersion(l);
                    reportDetail.setVersion(l);
                }

                reportDetail.setPortTime(portTime);
                reportDetail.setCreateTime(date);
                reportDetail.setUpdateTime(date);
                String countryCode = "aa";
                for (JSONObject object : jsons) {
                    System.out.println(JSONObject.toJSONString(object));
                    sku = object.getString("seller-sku");
                    if (StringUtils.isBlank(sku) && marketplaceId1.getCountryCode().equalsIgnoreCase("JP")) {
                        sku = object.getString("出品者SKU");
                    }  //如果其他站点也改变了 key那么这里接着判断其他站点的数据
                    if (StringUtils.isBlank(sku)) {
                        logger.error("merchant为：" + merchant + " marketplaceId为： " + marketplaceId + " 的报告详情有误，内容：" + JSONObject.toJSONString(object));
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "报告数据异常，没有sku");
                    } else {
                        countryCode = marketplaceId1.getCountryCode();
                    }
                    param.setPlatformSku(sku);
                    list = amazonPublishListingService.findList(param);
                    reportDetail.setSku(sku);
                    reportDetail.setContent(JSONObject.toJSONString(object));
                    reportDetail.setStatus(object.getString("status"));
                    if (countryCode.equalsIgnoreCase("JP")) {
                        reportDetail.setStatus(object.getString("ステータス"));
                    }
                    try {
                        if (list.size() == 0) {                   //不是品连刊登,添加 报告详情的时候添加为不存在
                            reportDetail.setIsExist(AmazonPublishEnums.isExist.NOT_EXIST.getCode());
                        } else if (list.size() == 1) {             //品连刊登 添加 报告详情的时候添加为存在
                            reportDetail.setIsExist(AmazonPublishEnums.isExist.EXIST.getCode());
                            if (list.get(0).getPublishStatus().equals(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE)
                                    || list.get(0).getPublishStatus().equals(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_FAIL)) {
                                if (re == 0) {  // 如果是一个正常的报告
                                    //根据 ListingId  和   sku 向sub 表中更新asin码
                                    /*AmazonPublishSubListing sub = new AmazonPublishSubListing();
                                    sub.setListingId(list.get(0).getId());
                                    sub.setSku(sku);
                                    sub.setAsin(object.getString("asin1"));
                                    amazonPublishSubListingService.updateAsinByListingIdAndSku(sub);*/
                                    //判断Listing 中的平台sku是否等于报告中的sku，如果是更新 listing中的asin，并同步更新描述等信息。 否则只更新描述等信息。
                                    AmazonPublishListing amazonPublishListing = null;
                                    if (countryCode.equalsIgnoreCase("JP")) {
                                        amazonPublishListing = this.updateAmazonPublishListingTOJP(list.get(0), object, sku);
                                    } else {
                                        amazonPublishListing = this.updateAmazonPublishListing(list.get(0), object, sku);
                                    }

                                    amazonPublishListingService.updateByPrimaryKeySelective(amazonPublishListing);
                                }
                            }
                        } else {                                // 同一卖家同一店铺下有相同的在线sku  说明数据异常   添加 报告详情的时候添加为存在
                            reportDetail.setIsExist(AmazonPublishEnums.isExist.EXIST.getCode());
                            logger.error("同步更新亚马逊刊登数据时发现sku：" + sku + " 有多个刊登数据");
                        }
                        // 添加报告详情
                        reportDetail.setId(null);
                        amazonPublishReportDetailService.insert(reportDetail);
                    } catch (Exception e) {
                        logger.error("报告正常，处理数据时出现异常，数据为：" + JSONObject.toJSONString(object) + "报告ID为： " + greId, e);
                    }
                }
            }
            return re;
        } catch (Exception e) {
            logger.error("解析报告结果异常", e);
            throw e;
        } finally {
            try {
                fr.close();
            } catch (Exception ignored) {
            } finally {
                try {
                    bf.close();
                } catch (Exception ignored) {
                }
            }
        }
    }


    /**
     * 封装被更新listing对象
     *
     * @param list   list
     * @param object 同步数据
     * @return 封装结果
     */
    public AmazonPublishListing updateAmazonPublishListing(AmazonPublishListing list, JSONObject object, String sku) {
        try {
            list.setTitle(object.getString("item-name"));
            String publishMessage = list.getPublishMessage();
            String asin = object.getString("asin1");
            if (list.getPlatformSku().equalsIgnoreCase(sku))
                list.setAsin(asin);
            if (StringUtils.isNotBlank(publishMessage)) {
                JSONObject obj = JSONObject.parseObject(publishMessage);
                if (StringUtils.isNotBlank(object.getString("item-description")))
                    obj.put("description", object.getString("item-description"));
                if (obj.getString("sku").equalsIgnoreCase(sku)) {
                    if (StringUtils.isNotBlank(object.getString("item-name")))
                        obj.put("title", object.getString("item-name"));
                    obj.put("standardPrice", object.getString("price"));
                    obj.put("quantity", object.getString("quantity"));
                    obj.put("asin", object.getString("asin1"));
                } else {
                    JSONArray array = obj.getJSONArray("varRequestProductList");
                    JSONObject subObj;
                    for (Object o : array) {
                        if (o instanceof JSONObject) {
                            subObj = (JSONObject) o;
                            if (subObj.getString("sku").equalsIgnoreCase(sku)) {
                                if (StringUtils.isNotBlank(object.getString("item-name")))
                                    subObj.put("title", object.getString("item-name"));
                                subObj.put("quantity", object.getString("quantity"));
                                subObj.put("standardPrice", object.getString("price"));
                                subObj.put("asin", object.getString("asin1"));
                            }
                        }
                    }
                }
                list.setPublishMessage(JSONObject.toJSONString(obj));
            }
        } catch (Exception e) {
            logger.error("亚马逊获取报告，封装被更新listing对象时出现异常，被更新的listingID为：" + list.getId());
        }
        return list;
    }


    /**
     * 封装被更新listing对象（日本站的）
     *
     * @param list   list
     * @param object 同步数据
     * @return 封装结果
     */
    public AmazonPublishListing updateAmazonPublishListingTOJP(AmazonPublishListing list, JSONObject object, String sku) {
        try {
            list.setTitle(object.getString("商品名"));
            String publishMessage = list.getPublishMessage();
            String asin = object.getString("asin1");
            if (list.getPlatformSku().equalsIgnoreCase(sku))
                list.setAsin(asin);
            if (StringUtils.isNotBlank(publishMessage)) {
                JSONObject obj = JSONObject.parseObject(publishMessage);
                if (StringUtils.isNotBlank(object.getString("item-description"))) {
                    obj.put("description", object.getString("item-description"));
                }
                if (obj.getString("sku").equalsIgnoreCase(sku)) {
                    if (StringUtils.isNotBlank(object.getString("商品名")))
                        obj.put("title", object.getString("商品名"));
                    obj.put("standardPrice", object.getString("価格"));
                    obj.put("quantity", object.getString("数量"));
                    obj.put("asin", object.getString("asin1"));
                } else {
                    JSONArray array = obj.getJSONArray("varRequestProductList");
                    JSONObject subObj;
                    for (Object o : array) {
                        if (o instanceof JSONObject) {
                            subObj = (JSONObject) o;
                            if (subObj.getString("sku").equalsIgnoreCase(sku)) {
                                if (StringUtils.isNotBlank(object.getString("商品名")))
                                    subObj.put("title", object.getString("商品名"));
                                subObj.put("quantity", object.getString("数量"));
                                subObj.put("standardPrice", object.getString("価格"));
                                subObj.put("asin", object.getString("asin1"));
                            }
                        }
                    }
                }
                list.setPublishMessage(JSONObject.toJSONString(obj));
            }
        } catch (Exception e) {
            logger.error("亚马逊获取报告，封装被更新listing对象时出现异常，被更新的listingID为：" + list.getId());
        }
        return list;
    }





    /*public static void main(String[] str){
        String sou = "{\"product-id\":\"192998970516\",\"merchant-shipping-group\":\"Modello Amazon predefinito\",\"item-is-marketplace\":\"y\",\"zshop-browse-path\":\"\",\"expedited-shipping\":\"\",\"zshop-boldface\":\"\",\"price\":\"2563\",\"item-name\":\"LANTHOME|??? computer\",\"seller-sku\":\"Afket|B-1-453A24F0-383872|johnny|8212\",\"zshop-shipping-fee\":\"\",\"will-ship-internationally\":\"\",\"quantity\":\"1000\",\"fulfillment-channel\":\"DEFAULT\",\"zshop-category1\":\"\",\"bid-for-featured-placement\":\"\",\"image-url\":\"\",\"zshop-storefront-feature\":\"\",\"asin3\":\"\",\"item-note\":\"\",\"open-date\":\"29/04/2019 08:06:59 MEST\",\"product-id-type\":\"3\",\"add-delete\":\"\",\"listing-id\":\"0429VVHWDCU\",\"asin1\":\"B07R4M6JXW\",\"asin2\":\"\",\"item-description\":\"???? LANTHOME|??? computer a a a a a a a ????\",\"pending-quantity\":\"0\",\"item-condition\":\"11\"}";
        JSONObject object = JSONObject.parseObject(sou);

        String  sku = object.getString("seller-sku");

        String message = "{\"searchTerms\":[\"a\"],\"packageWeight\":1200.00,\"dimensionLength\":20.00,\"description\":\"???? LANTHOME|??? computer a a a a a a a ????\",\"productCategoryForInteger\":[289365],\"title\":\"LANTHOME|??? ?? aa wanggeng\",\"manufacturer\":\"莲碧泉\",\"productCategory\":[289365],\"productCategory1Path\":\"Alimentari e cura della casa 》Birra 》 vino e alcolici 》Vino fruttato e stagionale\",\"standardProductID\":\"\",\"varRequestProductList\":[{\"standardProductType\":\"UPC\",\"images\":[{\"sKU\":\"B-1-453A24F0-383872\",\"imageType\":\"Main\",\"imageLocation\":\"http://rondaful-file-test.oss-cn-shenzhen.aliyuncs.com/product/2019-03-25/d3eda3d6c0a449b_47dc9676a8cd23ef4f02611957c931f8.jpg\"},{\"sKU\":\"B-1-453A24F0-383872\",\"imageType\":\"Swatch\",\"imageLocation\":\"http://rondaful-file-test.oss-cn-shenzhen.aliyuncs.com/product/2019-03-25/39c9274062cb414_1f7627d411d0a29f3b6da8935134ac51.jpg\"}],\"quantity\":\"1000\",\"searchTerms\":[],\"standardPrice\":\"2356\",\"standardPriceUnit\":\"EUR\",\"productCategoryForInteger\":[],\"title\":\"LANTHOME|??? computer\",\"standardProductID\":\"192998872568\",\"varRequestProductList\":[],\"bulletPoint\":[],\"categoryPropertyJson\":\"{\\\"classificationData\\\":{},\\\"parentage\\\":\\\"child\\\",\\\"productType\\\":{\\\"helmet\\\":{\\\"variationData\\\":{\\\"variationTheme\\\":\\\"Size\\\",\\\"parentage\\\":\\\"child\\\",\\\"size\\\":\\\"S\\\"},\\\"department\\\":[\\\"sd\\\"],\\\"styleKeywords\\\":[\\\"sad\\\"],\\\"safetyRating\\\":[\\\"BSI 6658 Type A Certified\\\"],\\\"modelName\\\":\\\"aa\\\",\\\"modelYear\\\":\\\"2017\\\",\\\"colorSpecification\\\":{\\\"color\\\":\\\"red\\\",\\\"colorMap\\\":\\\"Red\\\"},\\\"size\\\":\\\"sda\\\",\\\"partInterchangeData\\\":{\\\"oeManufacturer\\\":\\\"fsd\\\",\\\"partInterchangeInfo\\\":\\\"asdf\\\"},\\\"modelNumber\\\":\\\"SB-122\\\"}},\\\"battery\\\":{\\\"batterySubgroup\\\":[{}]}}\",\"mfrPartNumber\":\"Afket|B-1-453A24F0-383872|johnny|8211\",\"isMultiattribute\":false,\"sku\":\"Afket|B-1-453A24F0-383872|johnny|8211\",\"plSku\":\"B-1-453A24F0-383872\"},{\"standardProductType\":\"UPC\",\"images\":[{\"sKU\":\"B-1-453A24F0-549696\",\"imageType\":\"Main\",\"imageLocation\":\"http://rondaful-file-test.oss-cn-shenzhen.aliyuncs.com/product/2019-03-25/98a70a597f0b48f_1f393d8d184fd81c73c8cfca8c977065.jpg\"},{\"sKU\":\"B-1-453A24F0-549696\",\"imageType\":\"Swatch\",\"imageLocation\":\"http://rondaful-file-test.oss-cn-shenzhen.aliyuncs.com/product/2019-03-25/ccd32f4d0da74ce_1f0b29b2453665803de5671f157b70da.jpg\"}],\"quantity\":\"1000\",\"searchTerms\":[],\"standardPrice\":\"2563\",\"standardPriceUnit\":\"EUR\",\"productCategoryForInteger\":[],\"title\":\"LANTHOME|??? computer\",\"standardProductID\":\"192998970516\",\"varRequestProductList\":[],\"bulletPoint\":[],\"categoryPropertyJson\":\"{\\\"classificationData\\\":{},\\\"parentage\\\":\\\"child\\\",\\\"productType\\\":{\\\"helmet\\\":{\\\"variationData\\\":{\\\"variationTheme\\\":\\\"Size\\\",\\\"parentage\\\":\\\"child\\\",\\\"size\\\":\\\"M\\\"},\\\"department\\\":[\\\"sd\\\"],\\\"styleKeywords\\\":[\\\"sad\\\"],\\\"safetyRating\\\":[\\\"BSI 6658 Type A Certified\\\"],\\\"modelName\\\":\\\"aa\\\",\\\"modelYear\\\":\\\"2017\\\",\\\"colorSpecification\\\":{\\\"color\\\":\\\"red\\\",\\\"colorMap\\\":\\\"Red\\\"},\\\"size\\\":\\\"sda\\\",\\\"partInterchangeData\\\":{\\\"oeManufacturer\\\":\\\"fsd\\\",\\\"partInterchangeInfo\\\":\\\"asdf\\\"},\\\"modelNumber\\\":\\\"SB-122\\\"}},\\\"battery\\\":{\\\"batterySubgroup\\\":[{}]}}\",\"mfrPartNumber\":\"Afket|B-1-453A24F0-383872|johnny|8212\",\"isMultiattribute\":false,\"sku\":\"Afket|B-1-453A24F0-383872|johnny|8212\",\"plSku\":\"B-1-453A24F0-549696\"}],\"merchantIdentifier\":\"A1MLXBPVBMFWB7\",\"mfrPartNumber\":\"Afket|B-1-453A24F0-383872|johnny|821\",\"countryCode\":\"IT\",\"id\":4077,\"sku\":\"Afket|B-1-453A24F0-383872|johnny|821\",\"brand\":\"LANTHOME|莲碧泉\",\"plSku\":\"B-1-453A24F0\",\"dimensionUnitOfMeasure\":\"CM\",\"itemWeight\":1000.00,\"standardProductType\":\"UPC\",\"batchNo\":\"200c161f-e4c0-4d45-a0c7-e36e55f76914\",\"images\":[{\"sKU\":\"B-1-453A24F0\",\"imageType\":\"Main\",\"imageLocation\":\"http://rondaful-file-test.oss-cn-shenzhen.aliyuncs.com/product/2019-03-25/a5f5f0888efe4c3_chenshan2.jpg\"},{\"sKU\":\"B-1-453A24F0\",\"imageType\":\"Swatch\",\"imageLocation\":\"http://rondaful-file-test.oss-cn-shenzhen.aliyuncs.com/product/2019-03-25/39c9274062cb414_1f7627d411d0a29f3b6da8935134ac51.jpg\"},{\"sKU\":\"B-1-453A24F0\",\"imageType\":\"Swatch\",\"imageLocation\":\"http://rondaful-file-test.oss-cn-shenzhen.aliyuncs.com/product/2019-03-25/ccd32f4d0da74ce_1f0b29b2453665803de5671f157b70da.jpg\"}],\"dimensionHeight\":18.00,\"quantity\":\"1000\",\"fulfillmentLatency\":2,\"templatesName\":\"autoAccessory\",\"dimensionWidth\":20.00,\"standardPrice\":\"2564\",\"standardPriceUnit\":\"EUR\",\"weightUnitOfMeasure\":\"GR\",\"templatesName2\":\"helmet\",\"bulletPoint\":[\"a\",\"a\",\"a\",\"a\",\"a\"],\"categoryPropertyJson\":\"{\\\"classificationData\\\":{},\\\"parentage\\\":\\\"parent\\\",\\\"productType\\\":{\\\"helmet\\\":{\\\"variationData\\\":{\\\"variationTheme\\\":\\\"Size\\\",\\\"parentage\\\":\\\"parent\\\"},\\\"department\\\":[\\\"sd\\\"],\\\"styleKeywords\\\":[\\\"sad\\\"],\\\"safetyRating\\\":[\\\"BSI 6658 Type A Certified\\\"],\\\"modelName\\\":\\\"aa\\\",\\\"modelYear\\\":\\\"2017\\\",\\\"colorSpecification\\\":{\\\"color\\\":\\\"red\\\",\\\"colorMap\\\":\\\"Red\\\"},\\\"size\\\":\\\"sda\\\",\\\"partInterchangeData\\\":{\\\"oeManufacturer\\\":\\\"fsd\\\",\\\"partInterchangeInfo\\\":\\\"asdf\\\"},\\\"modelNumber\\\":\\\"SB-122\\\"}},\\\"battery\\\":{\\\"batterySubgroup\\\":[{}]}}\",\"spu\":\"B-1-453A24F0\",\"isMultiattribute\":true}";

        JSONObject obj = JSONObject.parseObject(message);
        obj.put("description", object.getString("item-description"));
        if (obj.getString("sku").equalsIgnoreCase(sku)) {
            obj.put("title", object.getString("item-name"));
            obj.put("standardPrice", object.getString("price"));
            obj.put("quantity", object.getString("pending-quantity"));
            obj.put("asin", object.getString("asin1"));
        } else {
            JSONArray array = obj.getJSONArray("varRequestProductList");
            JSONObject subObj;
            for (Object o : array) {
                if (o instanceof JSONObject) {
                    subObj = (JSONObject) o;
                    if (subObj.getString("sku").equalsIgnoreCase(sku)) {
                        subObj.put("title", object.getString("item-name"));
                        subObj.put("standardPrice", object.getString("price"));
                        subObj.put("quantity", object.getString("pending-quantity"));
                        subObj.put("asin", object.getString("asin1"));
                    }
                }
            }
        }
        System.out.println(JSONObject.toJSONString(obj));




    }*/


}
