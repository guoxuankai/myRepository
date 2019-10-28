package com.rondaful.cloud.seller.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据站点获得对应的货币
 * @author ouxiangfeng
 *
 */
public class CountryPriceConstants {
	private static final Map<String,String> localMap = localMap();
	
	/**
	 * TR:土耳其,	BR:巴西	CN:中国 ,这三个国家暂不做
	 *  @return
	 */
	public static String convert(String countryCode )
	{
		return localMap.get(countryCode);
	}
	private static Map<String,String> localMap()
	{
		Map<String,String> localM = new HashMap<>();
		localM.put("US", "USD");
		localM.put("GB", "GBP");
		localM.put("CA", "CAD");
		localM.put("JP", "JPY");
		localM.put("DE", "EUR");
		localM.put("FR", "EUR");
		localM.put("IT", "EUR");
		localM.put("ES", "EUR");
		localM.put("MX", "MXN");
		localM.put("AU", "AUD");
		localM.put("IN", "INR");
		return localM;
		
	}
	

}
