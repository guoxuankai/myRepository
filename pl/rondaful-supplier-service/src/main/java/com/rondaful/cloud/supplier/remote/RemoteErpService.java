package com.rondaful.cloud.supplier.remote;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.ERPWarehouseEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RemoteErpService {
	/**
	 * 远程调用erp服务获取库存
	 * 
	 * @return 
	 */

	JSONObject getInventory(Map<String,String> params);
	

    /**
     * 	远程调用erp服务获取物流信息
     *
     * @return 物流信息
     */
    JSONObject getCarrier() throws IOException;
	
	
    /**
     * 	 调用erp计算物流费 以sku列表的方式
     * @param warehouse_code 仓库code
     * @param country_code 国家双字母简写
     * @param shipping_code_arr 物流方式列表
     * @param skus sku列表  String 的格式为： [{"sku":"DI0302801","num":"6"},]
     * @Param channel_id 所属平台
     * @Param city  城市
     * @return 返回数据
     */
	String erpTrialBySKUS(String warehouse_code, String country_code, List<String> shipping_code_arr,  List<Map<String, Object>> skus,String channel_id,String city,String postCode);
	
	 JSONObject getWarehouse();


	@Service
	class RemoteErpServiceImpl implements RemoteErpService {
		
		private final Logger logger = LoggerFactory.getLogger(RemoteErpServiceImpl.class);

		@Value("${erp.url}")
		private String erpUrl;
		
		@Override
		public JSONObject getInventory(Map<String,String> params) {
			String result = HttpUtil.postSendByFormData(erpUrl, "getInventory", params);
			// 取得返回的body
			JSONObject parseBody = JSONObject.parseObject(result);
			Integer status = parseBody.getInteger("status");
			String message = parseBody.getString("message");
			if (status == null || !"success".equals(message) || status != 1) {
				logger.error("调用ERP获取库存服务异常:{}", message);
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "调用ERP服务异常:"+message);
			}
			return JSONObject.parseObject(parseBody.getString("data"));

		}
		
		
        @Override
        public JSONObject getCarrier() throws IOException {
            String result = HttpUtil.postSendByFormData(erpUrl, ERPWarehouseEnum.methods.LOGISTICS_LIST.getMethod(), null);
            return this.parsingResult(result);
        }
		

		@Override
		public JSONObject getWarehouse() {
			String body = HttpUtil.postSendByFormData(erpUrl,ERPWarehouseEnum.methods.WAREHOUSE_LIST.getMethod(), null);
			JSONObject parseObject = JSONObject.parseObject(body);
			Integer status =  parseObject.getInteger("status");
			String message =  parseObject.getString("message");
			if (status == null) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "status is null");
			}
			// 判断接口是否返回成功，失败抛出异常
			if (status != 1 && !"success".equals(message)) {
				logger.error("调用ERP获取仓库服务异常：{}",message);
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "调用ERP服务异常:"+message);
			}
			return parseObject;
		}

		@Override
		public String erpTrialBySKUS(String warehouse_code, String country_code, List<String> shipping_code_arr,
				List<Map<String, Object>> skus,String channel_id,String city,String postCode){
            HashMap<String, String> postParam = new HashMap<>();
            postParam.put("search_type","2");
			postParam.put("city",city == null?null:city);
			postParam.put("zip",postCode == null?null:postCode);
            postParam.put("warehouse_code",warehouse_code);
            postParam.put("country_code",country_code);
            postParam.put("shipping_code_arr",JSONObject.toJSONString(shipping_code_arr));
            postParam.put("skus",JSONObject.toJSONString(skus));
            postParam.put("channel_id",JSONObject.toJSONString(channel_id));
            String result = HttpUtil.postSendByFormData(erpUrl,ERPWarehouseEnum.methods.TRAIL.getMethod(),postParam);
            logger.info("erpUrl={}",erpUrl);
            return result;
		}
		
		
        /**
         *  	解析返回结果
         * @param result 返回的结果字符串
         * @return 解析结果
         */

        private JSONObject parsingResult(String result) {
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                String message = jsonObject.getString("message");
                if (StringUtils.isNotBlank(message) && message.equalsIgnoreCase("success")) {
                    return jsonObject;
                }
            }
            return null;
        }

	}

}
