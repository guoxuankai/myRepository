package com.brandslink.cloud.common.controller;


import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.brandslink.cloud.common.utils.SpringContextUtil.getActiveProfile;


/**
 * 系统API
 * */
@Api(description="系统API")
@Lazy
@RestController
public class ApiOfSystem extends BaseController {

    private final static Logger log = LoggerFactory.getLogger(ApiOfSystem.class);

    @Autowired
    private RestTemplate restTemplate;

    public final String profile = getActiveProfile();

    @Value("${operate.auth}")
    public String authPwd;

    @Value("${eureka.instance.instance-id}")
    public String instance;


    @ApiOperation(value = "服务上下线", notes = "")
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public void offLine() {
        operateAuth();
        Map map = new HashMap();
        String url = "http://" + instance + "/actuator/service-registry";
        ResponseEntity<JSONObject> response = restTemplate.getForEntity(url, JSONObject.class);
        String status = response.getBody().getString("status");
        if ("UP".equals(status)) {
            map.put("status", "OUT_OF_SERVICE");
        } else {
            map.put("status", "UP");
        }
        HttpEntity<Map> entity = new HttpEntity<Map>(map, new HttpHeaders(){{
            setContentType(MediaType.APPLICATION_JSON);
        }});
        response = restTemplate.postForEntity(url, entity, JSONObject.class);
        if (response.getStatusCode().isError())
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
    }


    /**
     * 操作认证
     */
    public void operateAuth() {
        if ("test".equals(profile))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        if ("prod".equals(profile)) {
            String auth = request.getParameter("auth");
            if (StringUtils.isBlank(auth) || !authPwd.equals(auth))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

}
