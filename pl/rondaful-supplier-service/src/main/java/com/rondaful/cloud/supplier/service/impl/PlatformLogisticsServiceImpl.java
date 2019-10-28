package com.rondaful.cloud.supplier.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.ebay.eis.dto.responses.AddressInfoResponses;
import com.ebay.eis.dto.responses.ConsignPreferenceInfoResponses;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.supplier.entity.Logistics.ErpProviderLogistics;
import com.rondaful.cloud.supplier.entity.Logistics.LogisticsAuth;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.mapper.LogisticsAuthMapper;
import com.rondaful.cloud.supplier.model.dto.FeignResult;
import com.rondaful.cloud.supplier.model.dto.logistics.AliexpressAddressDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.LogisticsAddress;
import com.rondaful.cloud.supplier.remote.RemoteEdisService;
import com.rondaful.cloud.supplier.remote.RemoteOrderService;
import com.rondaful.cloud.supplier.remote.RemoteSellerService;
import com.rondaful.cloud.supplier.service.IPlatformLogisticsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Service
class PlatformLogisticsServiceImpl implements IPlatformLogisticsService {

    private final Logger logger = LoggerFactory.getLogger(PlatformLogisticsServiceImpl.class);

    @Autowired
    private LogisticsAuthMapper logisticsAuthMapper;

    @Autowired
    private RemoteSellerService remoteSellerService;

    @Autowired
    private RemoteOrderService remoteOrderService;

    @Autowired
    private RemoteEdisService remoteEdisService;

    @Value("${aliexpress.address_url}")
    private String aliexpress_address_url;

    @Override
    public ErpProviderLogistics getEdis(String packageId) {
        ErpProviderLogistics erpProviderLogistics = new ErpProviderLogistics();
        FeignResult feignResult = remoteOrderService.getOrderInfoToSupplier(packageId); //通过包裹id获取平台订单号，店铺id,仓库名称
        if(!feignResult.getSuccess()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"获取店铺id信息异常");
        Map<String,Object> map = (Map<String,Object>)feignResult.getData();
        String warehouseName = map.get("deliveryWarehouse").toString(); //仓库名称
        List<String> platformOrderId = (List<String>)map.get("platformOrderIdList"); //平台订单号
        Integer platformShopId = (Integer) map.get("platformShopId"); //店铺id
        if(CollectionUtils.isNotEmpty(platformOrderId)){
            erpProviderLogistics.setPlatform_order_id(platformOrderId);
        }
        JSONObject ebayStr = this.getEmpower(platformShopId,1);   //通过店铺id获取ebayid
        String ebayId = ebayStr.getJSONArray("data").getJSONObject(0).getString("refreshToken");
        Integer edisType = ebayStr.getJSONArray("data").getJSONObject(0).getInteger("ebayEdis");
        erpProviderLogistics.setType(edisType);
        erpProviderLogistics.setEbay_name(ebayId);
        if(null == edisType || edisType == 0){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"该账号未授权");
        }else if(edisType == 2 || edisType == 3){
            if(StringUtils.isEmpty(ebayId)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取不到ebayId");
            LogisticsAuth authResult =  logisticsAuthMapper.queryLogisticsAuth(new LogisticsAuth(null,null,null,1));  //查询edis账户信息
            if(null == authResult ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"ebay授权信息为空");
            String token = remoteEdisService.getToken(authResult.getDevelopId().toString(),authResult.getSecret()); //获取token
            if(StringUtils.isEmpty(token)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取不到ebay信息");
            erpProviderLogistics.setInterface_token(token);
            List<AddressInfoResponses> addressInfoList = remoteEdisService.getEdisAddress(token,ebayId);  //获取地址
            for(AddressInfoResponses addressInfo :addressInfoList){
                if(addressInfo.getName().contains(warehouseName))  erpProviderLogistics.setInterface_user_password(addressInfo.getAddressId());
            }
            List<ConsignPreferenceInfoResponses> preferenceInfoList = remoteEdisService.getConsignPreference(token,ebayId); //获取交运偏好
            for(ConsignPreferenceInfoResponses preferenceInfo:preferenceInfoList){
                if(preferenceInfo.getName().contains(warehouseName)) erpProviderLogistics.setCustomer_code(preferenceInfo.getConsignPreferenceId());
            }
        }
        return erpProviderLogistics;
    }

    @Override
    public ErpProviderLogistics getAliExpress(String packageId) {
        ErpProviderLogistics erpProviderLogistics = new ErpProviderLogistics();
        FeignResult feignResult = remoteOrderService.getOrderInfoToSupplier(packageId); //通过包裹id获取平台订单号，店铺id,仓库名称
        if(!feignResult.getSuccess()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"获取店铺id信息异常");
        Map<String,Object> map = (Map<String,Object>)feignResult.getData();
        String warehouseName = map.get("deliveryWarehouse").toString(); //仓库名称
        if(StringUtils.isEmpty(warehouseName)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"没找到仓库信息");
        String city = warehouseName.substring(0,2);
        List<String> platformOrderId = (List<String>)map.get("platformOrderIdList"); //平台订单号
        Integer platformShopId = (Integer) map.get("platformShopId"); //店铺id
        if(CollectionUtils.isNotEmpty(platformOrderId)) erpProviderLogistics.setPlatform_order_id(platformOrderId);
        JSONObject aliStr = this.getEmpower(platformShopId,3);   //通过店铺id获取ebayid
        String aliToken = aliStr.getJSONArray("data").getJSONObject(0).getString("token");
        AliexpressAddressDTO addressDTO = this.getAliexpressAddress(aliToken);
        if(CollectionUtils.isNotEmpty(addressDTO.getPickupAddress())){
            for(LogisticsAddress pickupAddress:addressDTO.getPickupAddress()){
                if(pickupAddress.getCity().contains(city)){
                    erpProviderLogistics.setPickup_id(String.valueOf(pickupAddress.getAddress_id()));
                }
            }
        }
        if(CollectionUtils.isNotEmpty(addressDTO.getRefundAddress())){
            for(LogisticsAddress refundAddress:addressDTO.getRefundAddress()){
                if(refundAddress.getCity().contains(city)){
                    erpProviderLogistics.setRefund_id(String.valueOf(refundAddress.getAddress_id()));
                }
            }
        }
        if(CollectionUtils.isNotEmpty(addressDTO.getSendAddress())){
            if(warehouseName.contains("中山")){
                city = "zhong shan";
            }else if(warehouseName.contains("金华")){
                city = "jin hua";
            }
            for(LogisticsAddress sendAddress:addressDTO.getSendAddress()){
                if(sendAddress.getCity().contains(city)){
                    erpProviderLogistics.setSender_id(String.valueOf(sendAddress.getAddress_id()));
                }
            }
        }
        erpProviderLogistics.setInterface_token(aliToken);
        erpProviderLogistics.setInterface_user_key("3ea7671586c73d5c808bc9a10aba4fff");
        erpProviderLogistics.setInterface_user_name("25958822");
        return erpProviderLogistics;
    }


     private  AliexpressAddressDTO getAliexpressAddress(String token){
        AliexpressAddressDTO aaddressDTO = new AliexpressAddressDTO();
        String dataStr = "";
        try {
            dataStr = HttpUtil.get(aliexpress_address_url + token);
        }catch (Exception e){
            logger.error("调用aliexpress地址接口异常",e.getMessage());
        }
        JSONObject jsonStr = JSONObject.parseObject(dataStr);
        if("200".equals(jsonStr.getString("code")) && StringUtils.isNotEmpty(jsonStr.getString("data"))){
            aaddressDTO = JSONObject.parseObject(jsonStr.getString("data"),AliexpressAddressDTO.class);
        }
        return aaddressDTO;
    }

    private  JSONObject getEmpower(Integer platformShopId,Integer platform){
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("bindCode",Arrays.asList(platformShopId));
        jsonParam.put("platform",platform);
        jsonParam.put("dataType",10);
        jsonParam.put("page",1);
        jsonParam.put("row",100);
        JSONObject jsonStr = remoteSellerService.getEmpowerSearchVO(jsonParam);
        if(!"true".equals(jsonStr.getString("success")) || CollectionUtils.isEmpty(jsonStr.getJSONArray("data"))) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"调用授权接口异常");
        }
        return jsonStr;
    }

//    public static void main(String[] args) {
//        OAuthApi apiInstance = new OAuthApi();
//        FetchTokenResponses responses;
//        try {
//            responses = apiInstance.fetchToken("https://api.edisebay.com/v1/api","39710122","da11a14dcda344808e54d296ef3dbbfd9761");
//            EbayClient client = new DefaultEbayClient("https://api.edisebay.com/v1/api",responses.getToken());
//            GetConsignPreferenceListRequest req = new GetConsignPreferenceListRequest();
//            GetConsignPreferenceListRequestData reqData = new GetConsignPreferenceListRequestData();
//            List<ConsignPreferenceInfoResponses> preferenceList = new ArrayList<>();
//            reqData.setPageNumber(1);
//            reqData.setPageSize(500);
//            req.setEbayId("huiyiyu");
//            req.setData(reqData);
//            req.setMessageId(DateUtils.dateToString(new Date(),DateUtils.FORMAT_5));
//            req.setTimestamp(System.currentTimeMillis());
//            GetConsignPreferenceListResponses rsp = client.execute(req);
//            List<ConsignPreferenceInfoResponses> preferenceInfoResponses = rsp.getData().getConsignPreferenceList();
//            System.out.println(preferenceInfoResponses);
//            }catch (ApiException e){
////                logger.error("调用edis接口异常",e);
//                throw  new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"调用edis接口失败");
//            }
//    }
}


