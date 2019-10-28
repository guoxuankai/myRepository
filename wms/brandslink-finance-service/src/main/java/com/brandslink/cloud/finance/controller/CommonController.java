package com.brandslink.cloud.finance.controller;


import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.finance.pojo.entity.Attribute;
import com.brandslink.cloud.finance.rabbitmq.MQSender;
import com.brandslink.cloud.finance.service.impl.AttributeServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * 通用控制层
 */
@RestController
@RequestMapping(value = "/common")
public class CommonController extends BaseController {

    @Autowired
    private HttpServletRequest request;

    @Value("${test-1}")
    private String test1;

    @Value("${apollo.meta}")
    private String test2;

    @Autowired
    private AttributeServiceImpl attributeService;

    //@Autowired
    //private ElasticsearchUtil elasticsearchUtil;

    @Autowired
    MQSender mqSender;

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ApiOperation(value = "test", notes = "")
    public String update(Attribute attribute) throws InterruptedException, IOException {
        //Object result = attributeService.updateAttribute(attribute);
        //mqSender.commodityLowerframes("测试2.1 mq发送");
        //elasticsearchUtil.search("test2", "person", null);
        //System.out.println(attributeService.page(null));
        //mqSender.commodityLowerframes("abcd");
//        Page p = attributeService.page(null);
        return "success";

    }


}
