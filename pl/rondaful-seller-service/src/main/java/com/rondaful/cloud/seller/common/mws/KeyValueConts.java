package com.rondaful.cloud.seller.common.mws;

import com.rondaful.cloud.seller.common.mws.model.IdList;

import java.util.Arrays;

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
	
	
	
	/*
	 -------------------- amazon account info -----------------------
	开发者名称：Rionsr
	开发者ID:952565366074(欧洲站)
	AWS Access Key:AKIAIDQJ6PTG7DNWYX7A
	secret key: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
	MWS Authorisation Token:  amzn.mws.dab428a1-ed97-fd8d-e045-950d619f6f58
	
	Seller account identifiers for zhichu985
	Seller ID:  A1H4T44EIW2FJ1
	Marketplace ID:  A13V1IB3VIYZZH (Amazon.fr)
	A1F83G8C2ARO7P (Amazon.co.uk)
	A1PA6795UKMFR9 (Amazon.de)
	A1RKKUPIHCS9HS (Amazon.es)
	APJ6JRA9NG5V4 (Amazon.it)
	Seller-Developer Authorisation
	MWS Authorisation Token:  amzn.mws.dab428a1-ed97-fd8d-e045-950d619f6f58
	 */
	public static final String accessKeyId = "AKIAIDQJ6PTG7DNWYX7A";
	public static final String secretAccessKey = "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";
	public static final String appName = "zhichu985";
	public static final String appVersion = "2017-01-05"; 
	
	// https://mws.amazonservices.com	ATVPDKIKX0DER
	public static final IdList marketplaces = new IdList(Arrays.asList("ATVPDKIKX0DER"));
	public static final String reportType  = "_GET_XML_BROWSE_TREE_DATA_";
	public static final String merchantId = "";


	/**
	 *
	 */
	public enum amazonProductIdTye{
		//ASIN、GCID、SellerSKU、UPC、EAN、ISBN 和 JAN
		ASIN("ASIN"),
		GCID("GCID"),
		SellerSKU("SellerSKU"),
		UPC("UPC"),
		EAN("EAN"),
		ISBN("ISBN"),
		JAN("JAN");

		private String type;

		amazonProductIdTye(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}


	
}
