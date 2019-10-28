package com.rondaful.cloud.seller.common.mws.intface;

import com.rondaful.cloud.seller.common.mws.KeyValueConts;

//@Component
//@PropertySource(value = "classpath:bootstrap.yml",encoding="utf-8")
public class BaseMission {
	// @Value("${seller.amazon.accessKeyId}")
	public static final  String accessKeyId =  KeyValueConts.accessKeyId; //"<Your Access Key ID>";
	
	// @Value("${seller.amazon.secretAccessKey}")
	public static final  String secretAccessKey = KeyValueConts.secretAccessKey;//"<Your Secret Access Key>";

    public static final String appName = KeyValueConts.appName; //"<Your Application or Company Name>";
    public static final String appVersion =KeyValueConts.appVersion; // "<Your Application Version or Build Number or Release Date>";
	public String getAccessKeyId() {
		return accessKeyId;
	}
	public String getSecretAccessKey() {
		return secretAccessKey;
	}
    
}
