package com.amazonservices.mws.uploadData.common.mws;

import java.util.Arrays;

import com.amazonservices.mws.uploadData.common.mws.model.IdList;

public class KeyValueConts {
	
	/*
	 罗传芳: aws: AKIAJ6BPVGDVRSDDSYDQ
	罗传芳: saecretkey:9XK3PKxIdER1FCbaa6x2GZYgkoG2EVfrxZiHR7UN
	罗传芳: selleid:A10C4HPQ4BAH5E
	罗传芳: 账号purelemon美国站
	 */
	/*public static final String merchantId  = "A3B4VY4UDRBU3D";
	public static final String accessKeyId = "AKIAJF7ARKP3AKR7VP5Q";
	public static final String secretAccessKey = "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";
	public static final String appName = "Glossrise";*/
	public static final String merchantId  = "A10C4HPQ4BAH5E";
	public static final String accessKeyId = "AKIAJ6BPVGDVRSDDSYDQ";
	public static final String secretAccessKey = "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";
	public static final String appName = "purelemon";
	// https://mws.amazonservices.com	ATVPDKIKX0DER
	public static final IdList marketplaces = new IdList(Arrays.asList("ATVPDKIKX0DER"));
	
	public static final String reportType  = "_GET_XML_BROWSE_TREE_DATA_";
	
	public static final String appVersion = "2017-01-05"; 
}
