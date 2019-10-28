package com.brandslink.cloud.user.controller;


import com.brandslink.cloud.user.entity.Attribute;
import com.brandslink.cloud.user.rabbitmq.MQSender;
import com.brandslink.cloud.user.service.impl.AttributeServiceImpl;
import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.utils.RedisUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * 通用控制层
 * */
@RestController
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
    RedisUtils redisUtils;

    //@ApolloConfig
    //private Config config;





    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ApiOperation(value = "test", notes = "")
    public String update(Attribute attribute) throws InterruptedException, IOException {
        //Object result = attributeService.updateAttribute(attribute);
        //mqSender.commodityLowerframes("测试2.1 mq发送");
        //elasticsearchUtil.search("test2", "person", null);
        //System.out.println(attributeService.page(null));
        //mqSender.commodityLowerframes("abcd");
        Page p = attributeService.page(null);
        return test1 + test2;
    }


}
