package com.amazonservices.mws;

import java.util.HashMap;
import java.util.Map;

/**
 * to Amazon.properties
 * @author ouxiangfeng
 *
 */
public class MarketplaceIdList {
	
	public static Map<String,MarketplaceId> createMarketplace() {
		Map<String,MarketplaceId> res = new HashMap<String,MarketplaceId>();
		
		MarketplaceId BR = new MarketplaceId();
		BR.setCountryCode("BR");
		BR.setCountryName("巴西");
		BR.setUri("https://mws.amazonservices.com");
		BR.setMarketplaceId("A2Q3Y263D00KWC");
		res.put(BR.getMarketplaceId(), BR);
		
		MarketplaceId CA = new MarketplaceId();
		CA.setCountryCode("CA");
		CA.setCountryName("加拿大");
		CA.setUri("https://mws.amazonservices.ca");
		CA.setMarketplaceId("A2EUQ1WTGCTBG2");
		res.put(CA.getMarketplaceId(), CA);
		
		MarketplaceId MX = new MarketplaceId();
		MX.setCountryCode("MX");
		MX.setCountryName("墨西哥");
		MX.setUri("https://mws.amazonservices.com.mx");
		MX.setMarketplaceId("A1AM78C64UM0Y8");
		res.put(MX.getMarketplaceId(), MX);
		
		MarketplaceId US = new MarketplaceId();
		US.setCountryCode("US");
		US.setCountryName("美國");
		US.setUri("https://mws.amazonservices.com");
		US.setMarketplaceId("ATVPDKIKX0DER");
		res.put(US.getMarketplaceId(), US);
		
		MarketplaceId DE = new MarketplaceId();
		DE.setCountryCode("DE");
		DE.setCountryName("德國");
		DE.setUri("https://mws-eu.amazonservices.com");
		DE.setMarketplaceId("A1PA6795UKMFR9");
		res.put(DE.getMarketplaceId(), DE);
		
		MarketplaceId ES = new MarketplaceId();
		ES.setCountryCode("ES");
		ES.setCountryName("西班牙");
		ES.setUri("https://mws-eu.amazonservices.com");
		ES.setMarketplaceId("A1RKKUPIHCS9HS");
		res.put(ES.getMarketplaceId(), ES);
		
		MarketplaceId FR = new MarketplaceId();
		FR.setCountryCode("FR");
		FR.setCountryName("法國");
		FR.setUri("https://mws-eu.amazonservices.com");
		FR.setMarketplaceId("A13V1IB3VIYZZH");
		res.put(FR.getMarketplaceId(), FR);
		
		MarketplaceId GB = new MarketplaceId();
		GB.setCountryCode("GB");
		GB.setCountryName("英國");
		GB.setUri("https://mws-eu.amazonservices.com");
		GB.setMarketplaceId("A1F83G8C2ARO7P");
		res.put(GB.getMarketplaceId(), GB);
		
		MarketplaceId IN = new MarketplaceId();
		IN.setCountryCode("IN");
		IN.setCountryName("印度");
		IN.setUri("https://mws.amazonservices.in");
		IN.setMarketplaceId("A21TJRUUN4KGV");
		res.put(IN.getMarketplaceId(), IN);
		
		MarketplaceId IT = new MarketplaceId();
		IT.setCountryCode("IT");
		IT.setCountryName("意大利");
		IT.setUri("https://mws-eu.amazonservices.com");
		IT.setMarketplaceId("APJ6JRA9NG5V4");
		res.put(IT.getMarketplaceId(), IT);
		
		MarketplaceId TR = new MarketplaceId();
		TR.setCountryCode("TR");
		TR.setCountryName("土耳其");
		TR.setUri("https://mws-eu.amazonservices.com");
		TR.setMarketplaceId("A33AVAJ2PDY3EV");
		res.put(TR.getMarketplaceId(), TR);
		
		MarketplaceId AU = new MarketplaceId();
		AU.setCountryCode("AU");
		AU.setCountryName("澳大利亞");
		AU.setUri("https://mws.amazonservices.com.au");
		AU.setMarketplaceId("A39IBJ37TRP1C6");
		res.put(AU.getMarketplaceId(), AU);
		
		MarketplaceId JP = new MarketplaceId();
		JP.setCountryCode("JP");
		JP.setCountryName("日本");
		JP.setUri("https://mws.amazonservices.jp");
		JP.setMarketplaceId("A1VC38T7YXB528");
		res.put(JP.getMarketplaceId(), JP);
		
		MarketplaceId CN = new MarketplaceId();
		CN.setCountryCode("CN");
		CN.setCountryName("中国");
		CN.setUri("https://mws.amazonservices.jp");
		CN.setMarketplaceId("A1VC38T7YXB528");
		res.put(CN.getMarketplaceId(), CN);
		return res;
		//System.out.println(JSON.toJSON(res));
	}
}
