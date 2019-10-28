package com.rondaful.cloud.seller.common.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.seller.common.mws.intface.GetMatchingProductList;
import com.rondaful.cloud.seller.constants.PublishRequestReport;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.AmazonPublishReport;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.enums.ReportTypeEnum;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.service.AmazonPublishReportService;
import com.rondaful.cloud.seller.service.AmazonPublishSubListingService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;


/**
 * 当获取的报告有异时，使用sku查询asin
 */
@Component
public class GetMatchingProductListSynAsinTaxk implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(GetMatchingProductListSynAsinTaxk.class);

    private static final String upListBeforeKey = "GetMatching_NotAsinListing_";

    @Autowired
    private GetMatchingProductList getMatchingProductList;

    @Autowired
    private RedissLockUtil redissLockUtil;
    @Autowired
    private RedisUtils redisUtils;


    @Autowired
    private AmazonPublishReportService amazonPublishReportService;

    @Autowired
    private AmazonPublishListingService amazonPublishListingService;

    @Autowired
    private AmazonPublishSubListingService amazonPublishSubListingService;

    @Override
    public void run() {
        process();
    }

    public List<Map<String, Object>> getNotAsinLising(){

        String toKey = upListBeforeKey + "setList"; //
        JSONObject listJsonMessageFromRight = redisUtils.getListJsonMessageFromRight(toKey);
        if(listJsonMessageFromRight != null){
//            redisUtils.removeList(toKey);
//            logger.info("ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
            Map<String, Object> notAsin = (Map<String, Object>) JSON.toJSON(listJsonMessageFromRight);
            List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
            list.add(notAsin);
            logger.info( " key为： " + toKey + " 的没有asin的已刊登redis列表没有处理完，取出的notAsin为"+JSON.toJSONString(notAsin));
            return list;
        }else {
            List<Map<String, Object>> allNotAsinSkuIds = amazonPublishSubListingService.countNotAsin();
            if (allNotAsinSkuIds!= null || allNotAsinSkuIds.size()>0) {
                for (Map<String,Object> mapAsin:allNotAsinSkuIds) {
                    Long listingId = (Long) mapAsin.get("listingId");
                    List<AmazonPublishListing> listIfOnline = amazonPublishListingService.findListIfOnline(new Long[]{listingId});
                    if (listIfOnline == null || listIfOnline.size() == 0) {
                        logger.info("找不到对应的listing");
                        continue;
                    }
                    if (mapAsin.get("successCount").equals(BigDecimal.valueOf((Long) mapAsin.get("totalCount"))) || listIfOnline.get(0).getUpdateStatus() > 0) {
                        String str = JSON.toJSONString(mapAsin);
                        JSONObject jsonObject = JSONObject.parseObject(str);
                        redisUtils.setListJsonMessageFromLift(toKey, jsonObject);
                    }
                }
                logger.info(" key为： " + toKey + " 的没有asin的已刊登redis列表已经处理完了，再重数据库里面查找放到列表");
            }else {
                logger.info( " key为： " + toKey + " 的没有asin的已刊登redis列表已经处理完了，数据库里面也没有需要拉取asin码的sku" );
            }
        }
        return null;
    }


    /**
     * 刊登成功后立即获取ASIN编码
     *
     */
    public void process() {
        logger.info("刊登成功后立即获取ASIN编码");
        for (int i=0;i<50;i++) {
            List<Map<String, Object>> notAsinList = getNotAsinLising();
            if (null != notAsinList && notAsinList.size() > 0) {
                logger.info("有需要获取ASIN编码的sku");
                String merchantId = (String) notAsinList.get(0).get("merchantId");
                String marktplaceId = (String) notAsinList.get(0).get("marktplaceId");
                String token = (String) notAsinList.get(0).get("token");
                ArrayList<String> ids = new ArrayList<>();
                for (Map map : notAsinList) {
                    if (merchantId.equals(map.get("merchantId").toString()) && marktplaceId.equals(map.get("marktplaceId").toString())) {
                        ids.add((String) map.get("sku"));
                    }
                }
                //抓住异常不抛出让循环继续
                HashMap<String, String> results=null;
                try {
                    results = getMatchingProductList.invokeSku(merchantId, token, marktplaceId, ids);
                }catch (Exception e){
                    logger.error("查询亚马逊 GetMatchingProductForId 接口报错,什么都没有得到",e);
                    continue;
                }
                logger.info("amazon返回的数据>>>>>>>>>>>>>" + JSON.toJSONString(results));
                AmazonPublishSubListing sub = null;
                AmazonPublishListing ps = null;
                if (null != results && results.size() != 0) {
                    for (Map.Entry<String, String> en : results.entrySet()) {
                        logger.info("返回的sku和asin数据{}{}", en.getKey(), en.getValue());
                        for (Map notAsinMap : notAsinList) {
                            if (notAsinMap.get("sku").toString().equalsIgnoreCase(en.getKey())) {
                                Long listingId = Long.valueOf(notAsinMap.get("listingId").toString());
                                sub = new AmazonPublishSubListing();
                                sub.setListingId(listingId);
                                sub.setAsin(en.getValue());
                                sub.setSku(en.getKey());
                                amazonPublishSubListingService.updateAsinByListingIdAndSku(sub);  //修改sub表
                                //把对应 listingId查找出来 并比对sku，如一样就设置进去
                                AmazonPublishListing publishListing = amazonPublishListingService.selectByPrimaryKey(listingId);
                                if (null == publishListing) {
                                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "找不到对应的listing数据");
                                }
                                String publishMessage = publishListing.getPublishMessage();
                                JSONObject parse = JSONObject.parseObject(publishMessage);
                                String sku = String.valueOf(parse.get("sku"));
                                Object asin = parse.get("asin");
                                ps = new AmazonPublishListing();
                                ps.setId(listingId);
                                ps.setUpdateTime(new Date());
                                if (sku.equals(notAsinMap.get("sku").toString()) && null == asin) {  //设置外层asin，并设置
                                    logger.info("进入设置外层asin");
                                    parse.put("asin", en.getValue());
                                    if (null == publishListing.getAsin()) {
                                        ps.setAsin(en.getValue());
                                    }
                                    ps.setPublishMessage(JSONObject.toJSONString(parse));
                                    try {
                                        amazonPublishListingService.updateByPrimaryKeySelective(ps);
                                    } catch (Exception e) {
                                        logger.info("修改listing表添加asin报错", e);
                                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), e.getMessage());
                                    }
                                }
                                List<Map<String, Object>> varRequestProductList = (List<Map<String, Object>>) parse.get("varRequestProductList");
                                logger.info("varRequestProductList的值" + JSON.toJSONString(varRequestProductList));
                                for (Map<String, Object> product : varRequestProductList) {    //设置内层asin
                                    String skuSon = (String) product.get("sku");
                                    String asinSon = (String) product.get("asin");
                                    if (skuSon.equals(notAsinMap.get("sku").toString()) && null == asinSon) {
                                        logger.info("进入设置内层asin");
                                        product.put("asin", en.getValue());
                                        ps.setPublishMessage(JSONObject.toJSONString(parse));
                                        try {
                                            amazonPublishListingService.updateByPrimaryKeySelective(ps);
                                        } catch (Exception e) {
                                            logger.info("修改listing表添加asin报错", e);
                                            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), e.getMessage());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    }

