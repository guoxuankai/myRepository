package com.rondaful.cloud.gateway.fallback;

import com.rondaful.cloud.gateway.entity.Massage;
import com.rondaful.cloud.gateway.enums.ResponseCodeEnum;
import com.rondaful.cloud.gateway.exception.GlobalException;
import com.rondaful.cloud.gateway.utils.Utils;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Date;


/**
 * 商品服务断路器
 * */
@Component
public class CommodityServiceFallbackProvider implements ZuulFallbackProvider {

    @Override
    public String getRoute() {
        return "rondaful-commodity-service";
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return this.getStatusCode().value();
            }

            @Override
            public String getStatusText() throws IOException {
                return this.getStatusCode().getReasonPhrase();
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                Utils.print(JSONObject.fromObject(new Massage("100500", "商品服务异常")));
                return null;
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                MediaType mt = new MediaType("application", "json", Charset.forName("UTF-8"));
                headers.setContentType(mt);
                return headers;
            }
        };
    }

}
