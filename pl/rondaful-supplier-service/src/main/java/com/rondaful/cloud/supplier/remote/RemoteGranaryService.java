package com.rondaful.cloud.supplier.remote;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.granary.GranaryUtils;
import com.rondaful.cloud.supplier.entity.FreightTrial;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


public interface RemoteGranaryService {
	/**
	 * 远程调用谷仓服务获取库存
	 * 
	 * @return 
	 * @throws Exception 
	 */

	JSONObject getInventory(Map<String,String> params,String appToken, String appKey) throws Exception;
	
	/**
	 * 	远程调用谷仓物流方式
	 * @param params
	 * @param appToken
	 * @param appKey
	 * @return
	 * @throws Exception
	 */
	JSONObject getShippingMethod(Map<String,Object> params,String appToken,String appKey) throws Exception;
	
	/**
	 * 远程调用谷仓运费试算
	 * @param param
	 * @param appKey
	 * @param appToken
	 * @return
	 * @throws Exception
	 */
	JSONObject getCalculateDeliveryFee(FreightTrial param,String appKey,String appToken,String warehouseCode);
	

	@Service
	class RemoteGranaryServiceImpl implements RemoteGranaryService {
		
		private final Logger logger = LoggerFactory.getLogger(RemoteGranaryServiceImpl.class);

		@Value("${wsdl.url}") 
		private String url;
		
		@Value("${wsdl.AppToken}")
		private String defaultAppToken;
		
		@Value("${wsdl.AppKey}")
		private String defaultAppKey;

		@Autowired
		GranaryUtils granaryUtils;
		
		
		@Override
		public JSONObject getInventory(Map<String,String> params,String appToken, String appKey) throws Exception {
			String jsonString = JSONObject.toJSONString(params);
			String serviceName = "getProductInventory";
			String	result = granaryUtils.getInstance(appToken,appKey,url,jsonString, serviceName).getCallService();
			return JSONObject.parseObject(result);
		}


		@Override
		public JSONObject getShippingMethod(Map<String, Object> params, String appToken, String appKey)
				throws Exception {
			String jsonString = JSONObject.toJSONString(params);
			String	result = granaryUtils.getInstance(appToken,appKey,url,jsonString, "getShippingMethod").getCallService();
			return JSONObject.parseObject(result);

		}


		@Override
		public JSONObject getCalculateDeliveryFee(FreightTrial param, String appKey, String appToken, String warehouseCode) {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("warehouse_code",warehouseCode);//仓库code
			map.put("country_code", param.getCountryCode());//国家编码
			map.put("postcode", param.getPostCode());//邮政编码
			if(StringUtils.isNotEmpty(param.getLogisticsCode())) map.put("sm_code",param.getLogisticsCode());
			map.put("sku",param.getList());//sku集合
			String jsonString = JSONObject.toJSONString(map);
			String result = null;
			try{
				result = granaryUtils.getInstance(appToken,appKey,url,jsonString, "getCalculateDeliveryFee").getCallService();
			}catch (Exception e){
				logger.info("e",e);
			}
			JSONObject json = JSONObject.parseObject(result);
			return json;
		}
	}
}
