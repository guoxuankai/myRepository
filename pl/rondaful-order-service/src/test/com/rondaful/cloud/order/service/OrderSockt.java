package com.rondaful.cloud.order.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import sun.net.www.http.HttpClient;

import java.io.IOException;

public class OrderSockt {
    public static void main(String[] args) throws IOException {

        HttpGet get=new HttpGet("http://cgi.ebay.com/ws/eBayISAPI.dll?ViewItem&amp;item=401775232279&amp;vti=Wattage%0920W%0AColor%09Warm+white");
        get.setHeader("token","AgAAAA**AQAAAA**aAAAAA**wj9KXQ**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6AMkouhDJWDoA6dj6x9nY+seQ**r/8FAA**AAMAAA**b2vhkPW+5kksLk6dBP6VXnDxsb0nCFbV1cF+TGlmwopmarZekpGFC9iBhNhSTHip7F6tPFWgezVYhRChOkulArQWZkOGb+VGqEnGzDIvLtjM9AiwdFHk5xkjQiorOPKMC4LZbcLOxcwiwzbpJ5upXmmmlVh5BFxkUVMCavyelA1/CGWRZmjVOxuJe21NbNu2+L2tkF10uzgRD99gGrQyl7YxJVO409S5jBxnaS9+wxVqZl9HD1M4P0f5MNEvkz6eTat5xDeNQWS5E5vU85L1laLFnFCO4pKq2w6te0A8hM99ypG8qgc5uJec3WXeU/ikrih6J7rkuWawFwmv/LxnUmHDxfbJyp/KkSzD/tcGu0E+uAAqTnRcjPyvtz/EhdJQxdboT/etsVzj7Qov7PrzP09J4qYKzDCZUJG+mB+nwv/lCkIzvb53oRwJ9PfY2Mbg0al4KPNHz1U2l5KxXwc/pgG203ghSScxWouJjpxpMg8LkWAIpIM031tfWf3Odt1xMyoU1ZaIXJ1N4+5GCPhIkE2AFJebwiIVtxUJWtGfG65lPcmIDxhu7D7u0aeOoRsCDDaXJXpwXC2fN54AOHVok8qoq46o3svNSxR0UWQrhmm3m8htHB5kT/kWbinV4kFa0nARj2hCE5MomQfb59jf6zWbyUsiBT7EmKqaH3DtKp4VJtyEA9g2ngGqBL6zNSNiUABKyOgwW7RRfbrGzywHdyG1lPbvRGqQ+1n6YAwx9SiAaMiHKoIiumMdFBN1ZEg/");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse  execute = httpClient.execute(get);
        HttpEntity entity = execute.getEntity();
        String result = EntityUtils.toString(entity, "UTF-8");
        System.out.println(result);


    }
}
