/*******************************************************************************
 * Copyright 2009-2017 Amazon Services. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 *
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at: http://aws.amazon.com/apache2.0
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 *******************************************************************************
 * Marketplace Web Service Products
 * API Version: 2011-10-01
 * Library Version: 2017-03-22
 * Generated: Wed Mar 22 23:24:32 UTC 2017
 */
package com.rondaful.cloud.seller.common.mwsProducts.samples;


import com.rondaful.cloud.seller.common.mws.KeyValueConts;
import com.rondaful.cloud.seller.common.mwsProducts.MWSEndpoint;
import com.rondaful.cloud.seller.common.mwsProducts.MarketplaceWebServiceProductsAsyncClient;
import com.rondaful.cloud.seller.common.mwsProducts.MarketplaceWebServiceProductsClient;
import com.rondaful.cloud.seller.common.mwsProducts.MarketplaceWebServiceProductsConfig;


/**
 * Configuration for MarketplaceWebServiceProducts samples.
 */
public class MarketplaceWebServiceProductsSampleConfig {


    /** Developer AWS access key. */
    private static final String accessKey = KeyValueConts.accessKeyId;

    /** Developer AWS secret key. */
    private static final String secretKey = KeyValueConts.secretAccessKey;

    /** The client application name. */
    private static final String appName = KeyValueConts.appName;    //  Rionsr todo maybe is this name

    /** The client application version. */
    private static final String appVersion = KeyValueConts.appVersion;    //  "2011-10-01"  todo maybe is this string

    /**
     * The endpoint for region service and version.
     * ex: serviceURL = MWSEndpoint.NA_PROD.toString();
     */
   // private static final String serviceURL = "https://mws.amazonservices.com/Products/2011-10-01";
    private static final String serviceURL = MWSEndpoint.EU_PROD.toString();

    /** The client, lazy initialized. Async client is also a sync client. */
    private static MarketplaceWebServiceProductsAsyncClient client = null;

    /**
     * Get a client connection ready to use.
     *
     * @return A ready to use client connection.
     */
    public static MarketplaceWebServiceProductsClient getClient() {
        return getAsyncClient();
    }

    /**
     * Get an async client connection ready to use.
     *
     * @return A ready to use client connection.
     */
    public static synchronized MarketplaceWebServiceProductsAsyncClient getAsyncClient() {
        if (client==null) {
            MarketplaceWebServiceProductsConfig config = new MarketplaceWebServiceProductsConfig();
            config.setServiceURL(serviceURL);
            // Set other client connection configurations here.
            client = new MarketplaceWebServiceProductsAsyncClient(accessKey, secretKey, 
                    appName, appVersion, config, null);
        }
        return client;
    }

}
