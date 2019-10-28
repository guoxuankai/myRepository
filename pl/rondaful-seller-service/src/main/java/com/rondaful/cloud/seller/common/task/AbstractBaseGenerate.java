package com.rondaful.cloud.seller.common.task;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.exception.ProcessXmlException;
import com.rondaful.cloud.seller.generated.Inventory;
import com.rondaful.cloud.seller.generated.Price;
import com.rondaful.cloud.seller.generated.Product;
import com.rondaful.cloud.seller.generated.ProductImage;
import com.rondaful.cloud.seller.generated.Relationship;

public abstract class AbstractBaseGenerate {
	/** 生成商品 */
	abstract Product generagteProduct(AmazonRequestProduct<?> requestProduct,boolean isParent);
	
	/** 生成库存 */
	abstract Inventory generagteInventory(AmazonRequestProduct<?> requestProduct,boolean isParent);
	
	/** 生成图片 */
	abstract List<ProductImage> generagteProductImage(AmazonRequestProduct<?> requestProduct,boolean isParent);
	
	/** 生成价格 */
	abstract Price generagtePrice(AmazonRequestProduct<?> requestProduct,boolean isParent);
	
	/** 生成关系 */
	abstract Relationship generagteRelationship(String parentSku,List<String> childSkus, boolean isParent);
	
	
	   /** 判二个值，一个为空，或二个都为空，  返回true为正常数据 */
	 protected boolean checkOneNull(Object arg1,Object arg2) throws ProcessXmlException
	 {
	 	// 都不空，或 都为空，
	 	if((arg1 != null && arg2 != null))
	 	{
	 		return Boolean.TRUE;
	 	}
	 	if(arg1 == null && arg2 == null)
	 	{
	 		return Boolean.FALSE;
	 	}
	 	throw new ProcessXmlException(ResponseCodeEnum.RETURN_CODE_100403,"参数错误");
	 }
	 
	 
	protected Map<String, BigInteger> toMapBigInteger(String json) {
		Map<String, BigInteger> result = new HashMap<>();
		Map<String, String> jsonMap = JSON.parseObject(json, Map.class);
		if (jsonMap == null || jsonMap.isEmpty()) {
			return result;
		}
		Iterator keySet = jsonMap.keySet().iterator();
		while (keySet.hasNext()) {
			String key = (String) keySet.next();
			result.put(key, new BigInteger(jsonMap.get(key)));
		}
		return result;
	}

	protected  Map<String, String> toMapString(String json) {
		return JSON.parseObject(json, Map.class);
	}
	
	// <![CDATA[ ]]>
	protected String addCDATA(String str)
	{
		/*return str.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
		.replaceAll("&", "&amp;").replaceAll("'", "&apos;").replaceAll("\"", "&quot;");*/
		return str;
	}
}
