package com.rondaful.cloud.commodity.constant;

import java.util.HashMap;
import java.util.Map;

public class CommonConstant {

	/**平台-亚马逊*/
	public static final String PLATFORM_AMAOZN = "Amazon";
	
	/**谷仓接口-获取商品列表*/
	public static final String GC_GET_PRODUCT_SKU_LIST= "getProductSkuList";
	
	/**谷仓接口-获取账号*/
	public static final String GC_GET_ACCOUNT= "getAccount";
	
	/**谷仓接口-新增商品*/
	public static final String GC_ADD_PRODUCT= "addProduct";
	
	/**谷仓接口-编辑商品*/
	public static final String GC_EDIT_PRODUCT= "editProduct";
	
	/**谷仓接口-获取分类*/
	public static final String GC_GET_CATEGORY= "getCategory";
	
	/**谷仓接口-新运费试算*/
	public static final String GC_GET_CALCULATEDELIVERYFEE= "getCalculateDeliveryFee";
	
	
	/**wms接口-新增分类*/
	public static final String WMS_ADD_CATEGORY= "/center/category/add";
	
	/**wms接口-更新分类*/
	public static final String WMS_UPDATE_CATEGORY= "/center/category/update";
	
	/**wms接口-新增商品*/
	public static final String WMS_ADD_PRODUCT= "/center/sku/add";
	
	/**wms接口-修改商品*/
	public static final String WMS_UPDATE_PRODUCT= "/center/sku/update";
	
	
	/**通途接口-获取app_token*/
	public static final String TONGTOOL_GET_APP_TOKEN= "/open-platform-service/devApp/appToken";
	
	/**通途接口-获取商户ID*/
	public static final String TONGTOOL_GET_MERCHANTID= "/open-platform-service/partnerOpenInfo/getAppBuyerList";
	
	/**通途接口-创建商品*/
	public static final String TONGTOOL_POST_CREATE_PRODUCT= "/api-service/openapi/tongtool/createProduct";
	
	
	
	/** 默认 **/
	public static final String[] DEF = { "rondaful.oss-cn-shenzhen.aliyuncs.com", "rondaful-file-test.oss-cn-shenzhen.aliyuncs.com", "rondaful-file-dev.oss-cn-shenzhen.aliyuncs.com" };
	/** 域名 **/
	public static final String[] DO_MAIN = { "img.brandslink.com", "testimg.brandslink.com", "devimg.brandslink.com" };
	
	
	public static Map<String, String> getName(Map<String, String> map) {
		Map<String, String> data = new HashMap<>();
		map.forEach((k, v) -> {
			v = v.replace(DEF[0], DO_MAIN[0]).replace(DEF[1], DO_MAIN[1]).replace(DEF[2], DO_MAIN[2]);
			if (k.length()>16) {
				data.put(k.substring(16, k.length()), v);
			}else {
				data.put(k, v);
			}
		});
		return data;
	}
}
