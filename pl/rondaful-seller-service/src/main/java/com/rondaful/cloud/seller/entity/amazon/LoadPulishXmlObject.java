package com.rondaful.cloud.seller.entity.amazon;

import java.util.Random;

public class LoadPulishXmlObject {

	
	
	int v1 = new Random().nextInt(2) + 1;
	int v2 = new Random().nextInt(2) + 2;
	
	// 卖家id
	private String merchantIdentifier;
	//xml类型 , MessageTypeConstant.xxxx
	private String xmlType;
	// 更新在线数据
	private boolean isReplace = Boolean.FALSE;
	
	private String header = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" + 
			"<AmazonEnvelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\">\n" + 
			"    <Header>\n" + 
			"        <DocumentVersion>" + (v1 + "." + v2) + "</DocumentVersion>\n" + 
			"        <MerchantIdentifier>merchantIdentifier</MerchantIdentifier>\n" + 
			"    </Header>\n" + 
			"    <MessageType>xmlType</MessageType>\n" + 
			"    <PurgeAndReplace>false</PurgeAndReplace>\n";
	
	
	private StringBuffer body = new StringBuffer();
	
	private String end = "</AmazonEnvelope>";
	
	public LoadPulishXmlObject()
	{
	}
	public LoadPulishXmlObject(boolean isReplace)
	{
		this.isReplace = isReplace;
	}
	
	public String  toXml()
	{
		return this.header.replaceAll("merchantIdentifier", this.merchantIdentifier)
						  .replaceAll("xmlType", this.xmlType) 
						  //.replace("<PurgeAndReplace>false</PurgeAndReplace>", "<PurgeAndReplace>"+isReplace+"</PurgeAndReplace>")
						 + body.toString() 
						 + this.end;
	}

	public static void main(String[] args) {
		LoadPulishXmlObject o = new LoadPulishXmlObject();
		o.setMerchantIdentifier("tttttttttt");
		o.setXmlType("xy");
		o.toXml();
	}
	
	public String getMerchantIdentifier() {
		return merchantIdentifier;
	}

	public void setMerchantIdentifier(String merchantIdentifier) {
		this.merchantIdentifier = merchantIdentifier;
	}

	public String getXmlType() {
		return xmlType;
	}

	public void setXmlType(String xmlType) {
		this.xmlType = xmlType;
	}

	public StringBuffer getBody() {
		return body;
	}

	public void setBody(StringBuffer body) {
		this.body = body;
	}

	public boolean isReplace() {
		return isReplace;
	}

	public void setReplace(boolean isReplace) {
		this.isReplace = isReplace;
	}
	
	
	
	
	
}
