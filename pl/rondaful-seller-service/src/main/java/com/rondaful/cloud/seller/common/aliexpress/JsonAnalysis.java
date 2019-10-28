package com.rondaful.cloud.seller.common.aliexpress;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JsonAnalysis {
	private final static Logger logger = LoggerFactory.getLogger(JsonAnalysis.class);

	public static void main(String[] ars){
		String body ="{\"aliexpress_postproduct_redefining_findaeproductstatusbyid_response\":{\"result\":{\"product_id\":33006176403,\"time_stamp\":\"2019-04-25 09:38:28\"},\"request_id\":\"4e7kbllpmjx1\"}}";
		String str ="{\"aliexpress_postproduct_redefining_editsingleskuprice_response\":{\"result\":{\"error_code\":null,\"error_message\":\"API_EXCEPTION:13080037|:13080037:must be greater than or equal to 70%\",\"success\":false},\"request_id\":\"4z098yy09mva\"}}\n";
		System.out.println(JsonAnalysis.getErrerJosnMsg(str));

	}

	public static Map<String,Object> getGatewayMsg(String json){
		Map<String,Object> map= Maps.newHashMap();
		map.put("success","errer");

		JSONObject resJson = JSONObject.parseObject(json).getJSONObject("response");
        String obj ="";
        if(resJson==null){
            JSONObject jsonobj = JSONObject.parseObject(json);
			Object data = jsonobj.get("result_data");
			if(data!=null){
				obj = data.toString();
			}else{
				logger.info("getGatewayMsg="+json);
			}
			map.put("success",jsonobj.get("result_code_body"));
			map.put("msg",jsonobj.get("result_msg_body"));
        }else{
            Object data = resJson.get("result_data");
            if(data!=null){
				obj = data.toString();
			}else{
				logger.info("getGatewayMsg="+json);
			}
			map.put("success",resJson.get("result_code_body"));
			map.put("msg",resJson.get("result_msg_body"));
        }
        if(obj!=null && !"".equals(obj)) {
			obj = obj.substring(1);
		}
		map.put("data",obj);
		return map;
	}
	public static Map<String,Object> getMsgNew(String json){
		Map<String,Object> map= Maps.newHashMap();
		map.put("success","errer");

		JSONObject resJson = JSONObject.parseObject(json).getJSONObject("response");
		String obj ="";
		if(resJson==null){
			JSONObject jsonobj = JSONObject.parseObject(json);
			Object data = jsonobj.get("result_data").toString();
			if(data!=null){
				obj = data.toString();
			}else{
				logger.info("getGatewayMsg="+json);
			}
			map.put("success",jsonobj.get("result_code_body"));
			map.put("msg",jsonobj.get("result_msg_body"));
		}else{
			Object data = resJson.get("result_data");
			if(data!=null){
				obj = data.toString();
			}else{
				logger.info("getGatewayMsg="+json);
			}
			map.put("success",resJson.get("result_code_body"));
			map.put("msg",resJson.get("result_msg_body"));
		}
		map.put("data",obj);
		return map;
	}


	/**
	 * 解析修改商品
	 * @param str
	 * @return
	 */
	public static Map<String,Object> getEditaeproductMsg(String str){
		Map<String,Object> map= Maps.newHashMap();
		map.put("success","errer");
		JSONObject jsonObject = JSONObject.parseObject(str);
		if(jsonObject!=null){
			Object errorResponse = jsonObject.get("error_response");
			if(errorResponse!=null){
				map = JsonAnalysis.getErrorMsg(errorResponse.toString(),map);
			}else{
				Object editaeproductResponse = jsonObject.get("aliexpress_postproduct_redefining_editaeproduct_response");
				if(editaeproductResponse!=null){
					JSONObject resultJson = JSONObject.parseObject(editaeproductResponse.toString());
					Object result = resultJson.get("result");
					if(result!=null){
						JSONObject editProductJson = JSONObject.parseObject(result.toString());
						Object productId = editProductJson.get("product_id");
						if(productId!=null){
							map.put("success","true");
							map.put("productId",productId);
						}else {
							Object errorCode = editProductJson.get("error_code");
							Object errorMessage = editProductJson.get("error_message");
							map.put("success","false");
							map.put("errorCode",errorCode);
							map.put("errorMessage",errorMessage);
						}

					}
				}
			}
		}
		return map;
	}

	/**
	 * 解析异常信息
	 * @param str
	 * @return
	 */
	public static Map<String,Object> getErrerJosnMsg(String str){
		Map<String,Object> map= Maps.newHashMap();
		map.put("success","errer");
		JSONObject jsonObject = JSONObject.parseObject(str);
		if(jsonObject!=null){
			Object errorResponse = jsonObject.get("error_response");
			if(errorResponse!=null){
				map = JsonAnalysis.getErrorMsg(errorResponse.toString(),map);
			}else{
				map.put("success","true");
				JSONObject response = jsonObject.getJSONObject("aliexpress_postproduct_redefining_editsingleskuprice_response");
				if(response!=null){
					JSONObject result = response.getJSONObject("result");
					if(result!=null){
						Object code = result.get("error_code");
						Object msg = result.get("error_message");
						if(code!=null){
							map.put("success","error_response");
							map.put("code",code);
							map.put("msg",msg);
							map.put("subMsg","");

						}
					}
				}
			}
		}
		return map;
	}

	/**
	 * 解析商品详情
	 * @param str
	 * @return
	 */
	public static Map<String,Object> getfindaeproductbyidJosnMsg(String str){
		Map<String,Object> map=Maps.newHashMap();
		map.put("success","errer");
		JSONObject jsonObject = JSONObject.parseObject(str);
		if(jsonObject!=null){
			Object errorResponse = jsonObject.get("error_response");
			if(errorResponse!=null){
				map = JsonAnalysis.getErrorMsg(errorResponse.toString(),map);
			}else{
				Object postaeproductResponse = jsonObject.get("aliexpress_postproduct_redefining_findaeproductbyid_response");
				if(postaeproductResponse!=null){
					JSONObject resultJson = JSONObject.parseObject(postaeproductResponse.toString());
					Object result = resultJson.get("result");
					if(result!=null){
						JSONObject productJson = JSONObject.parseObject(result.toString());
						Object product = productJson.get("aeop_ae_product_s_k_us");
						if(product!=null){
							JSONObject productSkuJson = JSONObject.parseObject(product.toString());
							JSONArray productSku = productSkuJson.getJSONArray("aeop_ae_product_sku");
							map.put("success","true");
							for (int i=0;i<productSku.size();i++){
								JSONObject json=productSku.getJSONObject(i);
								String id = json.get("id").toString();
								String skuCode =json.get("sku_code").toString();
								map.put(skuCode,id);
							}
						}
					}
				}
			}
		}
		return map;
	}


	/**
	 * 解析刊登结果字符串
	 * @param str
	 * @return
	 */
	public static Map<String,Object> getJosnMsg(String str){
		Map<String,Object> map=Maps.newHashMap();
		map.put("success","errer");
		JSONObject jsonObject = JSONObject.parseObject(str);
		if(jsonObject!=null){
			Object errorResponse = jsonObject.get("error_response");
			if(errorResponse!=null){
				map = JsonAnalysis.getErrorMsg(errorResponse.toString(),map);
			}else{
				Object postaeproductResponse = jsonObject.get("aliexpress_postproduct_redefining_postaeproduct_response");
				if(postaeproductResponse!=null){
					JSONObject resultJson = JSONObject.parseObject(postaeproductResponse.toString());
					if(resultJson!=null){
						Object result= resultJson.get("result");
						if(result!=null){
							JSONObject productJson = JSONObject.parseObject(result.toString());
							Object productId = productJson.get("product_id");
							Object errorMessage = productJson.get("error_message");
							Object errorCode = productJson.get("error_code");
							Object isSuccess = productJson.get("is_success");
							map.put("success",isSuccess);
							map.put("productId",productId);
							map.put("errorMessage",errorMessage);
							map.put("errorCode",errorCode);
						}
					}
				}else {
					Object findaeproductstatusbyidResponse = jsonObject.get("aliexpress_postproduct_redefining_findaeproductstatusbyid_response");
					if(findaeproductstatusbyidResponse!=null) {
						JSONObject resultJson = JSONObject.parseObject(findaeproductstatusbyidResponse.toString());
						if (resultJson != null) {
							Object result= resultJson.get("result");
							if(result!=null) {
								JSONObject productJson = JSONObject.parseObject(result.toString());
								Object errorMessage = productJson.get("error_message");
								Object errorCode = productJson.get("error_code");
								Object status = productJson.get("status");
								Object product_id = productJson.get("product_id");
								if(status!=null){
									map.put("success", true);
								}else{
									//特殊处理 没有审核状态有订单id也是审核通过
									if(product_id!=null && status==null){
										map.put("success", true);
										status = "approved";
									}else {
										map.put("success", false);
									}
								}
								map.put("errorMessage", errorMessage);
								map.put("errorCode", errorCode);
								map.put("status", status);
							}
						}
					}
				}
			}
		}
		return map;
	}


	private static Map<String,Object> getErrorMsg(String errorResponse,Map<String,Object> map){
		JSONObject msgJson = JSONObject.parseObject(errorResponse.toString());
		Object code = msgJson.get("code");
		Object subCode = msgJson.get("sub_code");
		Object msg = msgJson.get("msg");
		Object subMsg = msgJson.get("sub_msg");
		map.put("success","error_response");
		map.put("msg",msg);
		map.put("subMsg",subMsg);
		map.put("code",code);
		map.put("sub_code",subCode);
		return map;
	}
}
