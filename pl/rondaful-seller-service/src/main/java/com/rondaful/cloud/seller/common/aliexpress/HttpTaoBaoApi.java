package com.rondaful.cloud.seller.common.aliexpress;

import com.qimencloud.api.DefaultQimenCloudClient;
import com.qimencloud.api.QimenCloudRequest;
import com.qimencloud.api.QimenCloudResponse;
import com.rondaful.cloud.seller.config.AliexpressConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class HttpTaoBaoApi {
    @Autowired
    private AliexpressConfig config;

    public String getTaoBaoApi(String apiMethodName, Map<String,Object> map){
        String gatewayUrl = config.getGatewayUrl();
        String appKey = config.getAppKey();
        String appSecret = config.getAppSecret();
        String url = gatewayUrl;//"http://19a449xd7y.api.taobao.com/router/qmtest";
        DefaultQimenCloudClient client = new DefaultQimenCloudClient(url, appKey, appSecret);
        QimenCloudRequest req=new QimenCloudRequest();
        req.setApiMethodName(apiMethodName);
        req.setTargetAppKey(appKey);
        try {
            req.addQueryParam("signValue",SignMd5.createSign(map));

            for(Map.Entry<String,Object> key : map.entrySet()){
                if(key.getValue()==null){
                    continue;
                }
                req.addQueryParam(key.getKey(),key.getValue().toString());
            }
            QimenCloudResponse rsp = client.execute(req);
            return rsp.getBody();
        }catch (Exception e){
            e.printStackTrace();

        }
        return null;
    }
}
