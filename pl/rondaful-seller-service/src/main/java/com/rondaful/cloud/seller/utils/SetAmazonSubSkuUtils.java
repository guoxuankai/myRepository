package com.rondaful.cloud.seller.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.service.AmazonPublishSubListingService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 由于某些sub表中的数据没有sku等字段值，在此使用代码将其生成写入
 */

@Component
public class SetAmazonSubSkuUtils {

    private static final Logger logger = LoggerFactory.getLogger(SetAmazonSubSkuUtils.class);

    @Autowired
    private AmazonPublishListingService amazonPublishListingService;

    @Autowired
    private AmazonPublishSubListingService amazonPublishSubListingService;


    public void setSke() {
        List<AmazonPublishSubListing> noSkuSubListing = amazonPublishSubListingService.findNoSkuSubListing();
        if (noSkuSubListing == null || noSkuSubListing.size() == 0)
            return;

        AmazonPublishListing listing;
        List<AmazonPublishSubListing> amazonPublishSubListings;
        AmazonPublishSubListing result = new AmazonPublishSubListing();
        for (AmazonPublishSubListing sub : noSkuSubListing) {
            if (sub.getListingId() != null) {
                listing = amazonPublishListingService.selectByPrimaryKey(sub.getListingId());
                if (listing != null && StringUtils.isNotBlank(listing.getPublishMessage())) {
                    amazonPublishSubListings = amazonPublishSubListingService.selectPage(new AmazonPublishSubListing() {{
                        setListingId(sub.getListingId());
                    }});
                    if (!CollectionUtils.isEmpty(amazonPublishSubListings)) {
                        try {
                            String publishSite = listing.getPublishSite();
                            String marketplaceId = MarketplaceIdList.createMarketplace().get(publishSite).getMarketplaceId();
                            result.setMarketplaceId(marketplaceId);
                            JSONObject object = JSONObject.parseObject(listing.getPublishMessage());
                            String merchantIdentifier = object.getString("merchantIdentifier");
                            result.setMerchantId(merchantIdentifier);

                            updateMessage(object, amazonPublishSubListings, result);

                            JSONArray array = object.getJSONArray("varRequestProductList");
                            JSONObject subObj;
                            for (Object o : array) {
                                if (o instanceof JSONObject) {
                                    subObj = (JSONObject) o;
                                    updateMessage(subObj, amazonPublishSubListings, result);
                                }
                            }
                        } catch (Exception e) {
                            logger.error("ListingId : " + sub.getListingId() + " 数据填充时报错：", e);
                        }
                    }
                }
            }
        }
    }


    private void updateMessage(JSONObject object, List<AmazonPublishSubListing> amazonPublishSubListings, AmazonPublishSubListing result) {
        String sku = object.getString("sku");
        if (StringUtils.isNotBlank(sku)) {
            for (AmazonPublishSubListing subListing : amazonPublishSubListings) {
                if (StringUtils.isNotBlank(subListing.getMarketplaceId()) && StringUtils.isNotBlank(subListing.getMerchantId())
                        && StringUtils.isNotBlank(subListing.getSku()) && StringUtils.isNotBlank(subListing.getPlSku()))
                    continue;
                if (checkSku(subListing.getXmls(), sku)) {
                    result.setId(subListing.getId());
                    result.setSku(sku);
                    result.setPlSku(object.getString("plSku"));
                    logger.info("id w为：{}的更新了",result.getId());
                    amazonPublishSubListingService.updateByPrimaryKeySelective(result);
                }
            }
        }
    }


    private boolean checkSku(String xmlString, String sku) {
        if (StringUtils.isNotBlank(xmlString) && StringUtils.isNotBlank(sku)) {
            int i1 = xmlString.indexOf("<SKU><![CDATA[");
            if(i1 != -1){
                String sub2 = xmlString.substring(i1 + 14, xmlString.indexOf("]]></SKU>")).trim();
                if(sku.equalsIgnoreCase(sub2))
                    return true;
            }else {
                int i = xmlString.indexOf("<SKU>");
                if(i != -1){
                    String sub1 = xmlString.substring(i + 5, xmlString.indexOf("</SKU>")).trim();
                    if(sku.equalsIgnoreCase(sub1))
                        return true;
                }
            }
        }
        return false;
    }


}
