package com.amazonservices.mws.uploadData.common.mws.intface;

import com.amazonservices.mws.uploadData.common.mws.KeyValueConts;

public class BaseMission {
	final String accessKeyId =  KeyValueConts.accessKeyId; //"<Your Access Key ID>";
    final String secretAccessKey = KeyValueConts.secretAccessKey;//"<Your Secret Access Key>";

    final String appName = KeyValueConts.appName; //"<Your Application or Company Name>";
    final String appVersion =KeyValueConts.appVersion; // "<Your Application Version or Build Number or Release Date>";
    
}
