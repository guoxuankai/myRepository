package com.rondaful.cloud.seller.entity.amazon;

import javax.xml.bind.annotation.XmlRootElement;

import com.rondaful.cloud.seller.generated.Header;

@XmlRootElement(name = "AmazonPublishComposition")
public class AmazonPublishComposition {
	
	/* 头 */
	private Header header;
	
	private String  messageType = "Product"; 
	private Boolean purgeAndReplace = Boolean.FALSE;

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}
	
	
}
