package com.rondaful.cloud.seller.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.seller.enums.AmazonPublishEnums;
import com.rondaful.cloud.seller.remote.RemoteCommodityService;
import com.rondaful.cloud.seller.remote.RemoteLogisticsService;
import com.rondaful.cloud.seller.remote.RemoteSupplierProviderService;
import com.rondaful.cloud.seller.vo.CommodityStatusVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
public class AmazonSubListingUtil {

    private Logger logger = LoggerFactory.getLogger(AmazonSubListingUtil.class);

    @Autowired
    private RemoteCommodityService remoteCommodityService;

    @Autowired
    private RemoteLogisticsService remoteLogisticsService;
    @Autowired
    private RemoteSupplierProviderService remoteSupplierProviderService;

    /**
     * 设置列表数据的库存状态
     * @param subListing 子列表数据
     * @param warehouseId 仓库ID
     */
    public void setPlSkuStatusAndCount(List<AmazonPublishSubListing> subListing, Integer warehouseId){
        //  通过 requestProduct 拿到仓库ID，并循环 subListing 列表拿到sku列表值，返回sku库存数量。再双层循环插入到 subListing 中
        HashSet<String> limitSkus = new HashSet<>();
        for(AmazonPublishSubListing sub : subListing){
            if(StringUtils.isNotBlank(sub.getPlSku())){
                sub.setPlSkuStatus(AmazonPublishEnums.PLSKUStatus.UP.getCode());
                //9.16 判断是否侵权
                getAmazonCommodityStatusVOBySku(sub);
                limitSkus.add(sub.getPlSku());
            }else {
                sub.setPlSkuStatus(AmazonPublishEnums.PLSKUStatus.OTHER.getCode());
            }
        }

        if( warehouseId != null){
            try {
                if(limitSkus.size() >0) {
                    ArrayList<String> skus = new ArrayList<>(limitSkus);
                    String msg = remoteLogisticsService.getsBySku(warehouseId, JSONObject.toJSONString(skus));
                    logger.info("刊登时请求库存返回数据：{}", msg);
                    String dataString = Utils.returnRemoteResultDataString(msg, "供应商服务异常");
                    JSONArray array = JSONObject.parseArray(dataString);
                    JSONObject object;
                    for (Object o : array) {
                        if (o instanceof JSONObject) {
                            object = (JSONObject) o;
                            for (AmazonPublishSubListing sub : subListing) {
                                try {
                                    if(sub.getPlSku().equalsIgnoreCase(object.getString("pinlianSku"))){
                                        //    sub.setPlSkuCount(object.getLong("availableQty"));         //9.16版修改为本地可售
                                        sub.setPlSkuCount(object.getLong("localAvailableQty")<=0?0:object.getLong("localAvailableQty"));
                                    }
                                }catch (Exception e){
                                    logger.error("刊登库存数据比对时异常",e);
                                }
                            }
                        }
                    }
                }
            }catch (Exception e){
                logger.error("刊登时获取库存异常",e);
            }
        }
    }

    public void getAmazonCommodityStatusVOBySku(AmazonPublishSubListing sub) {
        MarketplaceId marketplaceId = MarketplaceIdList.resourceMarketplaceForKeyId().get(sub.getMarketplaceId());
        //平台，1：eBay，2：Amazon，3：wish，4：AliExpress
        String commoditySpec = remoteCommodityService.getCommoditySpecBySku(sub.getPlSku(), 2,marketplaceId.getCountryCode());
        String dataString = Utils.returnRemoteResultDataString(commoditySpec, "商品服务异常");
        JSONObject jsonObject = JSONObject.parseObject(dataString);
        if (null == jsonObject) {
            sub.setPlSkuStatus(AmazonPublishEnums.PLSKUStatus.OTHER.getCode());//查不到商品则判断为 未知
        } else {
            if (jsonObject.getInteger("state") != 3) { //3是上架状态，别的都是下架
                sub.setPlSkuStatus(AmazonPublishEnums.PLSKUStatus.DOWN.getCode());
            }
            if (jsonObject.getInteger("tortFlag") == 1) { //侵权
                sub.setPlSkuTort(1);
            }else if (jsonObject.getInteger("tortFlag") == 0){
                sub.setPlSkuTort(0);
            }
        }
    }
}







