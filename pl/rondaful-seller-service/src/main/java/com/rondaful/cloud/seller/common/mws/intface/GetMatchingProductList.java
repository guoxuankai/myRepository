package com.rondaful.cloud.seller.common.mws.intface;


import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.seller.common.mws.KeyValueConts;
import com.rondaful.cloud.seller.common.mwsProducts.MarketplaceWebServiceProductsAsyncClient;
import com.rondaful.cloud.seller.common.mwsProducts.MarketplaceWebServiceProductsClient;
import com.rondaful.cloud.seller.common.mwsProducts.MarketplaceWebServiceProductsConfig;
import com.rondaful.cloud.seller.common.mwsProducts.model.GetMatchingProductForIdRequest;
import com.rondaful.cloud.seller.common.mwsProducts.model.GetMatchingProductForIdResponse;
import com.rondaful.cloud.seller.common.mwsProducts.model.GetMatchingProductForIdResult;
import com.rondaful.cloud.seller.common.mwsProducts.model.IdListType;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdDeveloper;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 获取亚马逊上已刊登商品的信息 (姚明)
 */
@Component
public class GetMatchingProductList {

    private final Logger logger = LoggerFactory.getLogger(GetMatchingProductList.class);


    /**
     * 通过某个值去亚马逊查询asin
     *
     * @param sellerId      用户在亚马逊的卖家id
     * @param mwsAuthToken  用户亚马逊的账户token
     * @param marketplaceId 用户在亚马逊的站点id
     * @param idType        提交的参数类型 ASIN、GCID、SellerSKU、UPC、EAN、ISBN 和 JAN
     * @param ids           参数列表（最长为5）
     * @return 亚马逊返回信息
     */
    public HashMap<String, String> invokeMsg(String sellerId, String mwsAuthToken, String marketplaceId, String idType, ArrayList<String> ids) {
        try {
            logger.info("通过 :{} 去亚马逊查询asin,卖家ID：{}，站点：{}，参数：{}",idType,sellerId,marketplaceId,ids.toString());
            List<GetMatchingProductForIdResult> responses = this.sendAmazon(sellerId, mwsAuthToken, marketplaceId, idType, ids);
            HashMap<String, String> result = new HashMap<>();
            responses.forEach(res ->{
                if(res.getError() == null){
                    result.put(res.getId(),res.getProducts().getProduct().get(0).getIdentifiers().getMarketplaceASIN().getASIN());
                }else {
                    logger.error( idType +" : "+ res.getId() +" 获取asin异常：{}",res.getError().getMessage());
                    if((res.getId() + " is an invalid SellerSKU for marketplace " + marketplaceId).equalsIgnoreCase(res.getError().getMessage()))
                        System.out.println("sssssss");  //todo 这里暂时不作处理
                }
            });
            return result;
        } catch (Exception e) {
            logger.error("查询亚马逊 GetMatchingProductForId 接口报错",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(),e.getMessage());
        }
    }


    /**
     * 通过某个值去亚马逊查询刊登信息
     *
     * @param sellerId      用户在亚马逊的卖家id
     * @param mwsAuthToken  用户亚马逊的账户token
     * @param marketplaceId 用户在亚马逊的站点id
     * @param idType        提交的参数类型 ASIN、GCID、SellerSKU、UPC、EAN、ISBN 和 JAN
     * @param ids           参数列表（最长为5）
     * @return 亚马逊返回信息
     */
    public List<GetMatchingProductForIdResult>  sendAmazon(String sellerId, String mwsAuthToken, String marketplaceId, String idType, ArrayList<String> ids) {
        try {
            logger.info("通过 :{} 去亚马逊查询刊登信息,卖家ID：{}，站点：{}，参数：{}",idType,sellerId,marketplaceId,ids.toString());
            MarketplaceId markModel = MarketplaceIdList.resourceMarketplaceForKeyId().get(marketplaceId);
            MarketplaceIdDeveloper developer = markModel.getMarketplaceIdDeveloper();
            MarketplaceWebServiceProductsConfig config = new MarketplaceWebServiceProductsConfig();
            config.setServiceURL(markModel.getUri());
            MarketplaceWebServiceProductsClient client = new MarketplaceWebServiceProductsAsyncClient(developer.getAccessKeyId(), developer.getSecretAccessKey(),
                    developer.getName(), KeyValueConts.appVersion, config, null);
            GetMatchingProductForIdRequest request = new GetMatchingProductForIdRequest() {{
                setSellerId(sellerId);
                setMWSAuthToken(mwsAuthToken);
                setMarketplaceId(marketplaceId);
                setIdType(idType);
                setIdList(new IdListType() {{
                    setId(ids);
                }});
            }};
            GetMatchingProductForIdResponse matchingProductForId = client.getMatchingProductForId(request);
            List<GetMatchingProductForIdResult> responses = matchingProductForId.getGetMatchingProductForIdResult();
           return responses;
        } catch (Exception e) {
            logger.error("查询亚马逊 GetMatchingProductForId 接口报错",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(),e.getMessage());
        }
    }













    /**
     * 通过sku去亚马逊查询刊登信息
     *
     * @param sellerId      用户在亚马逊的卖家id
     * @param mwsAuthToken  用户亚马逊的账户token
     * @param marketplaceId 用户在亚马逊的站点id
     * @param ids           参数列表（最长为5）
     * @return 亚马逊返回信息
     */
    public HashMap<String, String> invokeSku(String sellerId, String mwsAuthToken, String marketplaceId, ArrayList<String> ids) {
        return this.invokeMsg(sellerId, mwsAuthToken, marketplaceId, KeyValueConts.amazonProductIdTye.SellerSKU.getType(), ids);
    }


    public static void main(String[] str){   //[00167168|yz|00167168002|11, 07241216001|*|07241216|18, 528576|yuzhou|A-1-9C7FE8BF|7, 56713216001|*|56713216|11, A-1-6BD76498-792320|*|A-1-6BD76498|8]
     /*   String sellerId = "A1H684G1PFRJBI";
        String mwsAuthToken = "amzn.mws.deb52903-66fb-ee46-6a85-9caefa8c616f";
        String marketplaceId = "ATVPDKIKX0DER";
        ArrayList<String> skus = new ArrayList<>();
        skus.add("8260SiCYaXhaokXzerW26lK9s2");
       *//* skus.add("07241216001|*|07241216|18");
        skus.add("528576|yuzhou|A-1-9C7FE8BF|7");
        skus.add("56713216001|*|56713216|11");
        skus.add("A-1-6BD76498-792320|*|A-1-6BD76498|8");*//*
        //strings.add("jgz9021614440");
        //strings.add("jgz9030518370");
        HashMap<String, String> maps = new GetMatchingProductList().invokeSku(sellerId,mwsAuthToken,marketplaceId,skus);
        System.out.println("1111");
        maps.forEach((key, value) -> System.out.println(key + "*******" + value));*/

        Date date = new Date();
        Date beforeDateByMinit = DateUtils.getBeforeDateByMinit(date, 5);
        System.out.println("");

    }

}
