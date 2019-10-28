package com.brandslink.cloud.logistics.controller;


import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.utils.RedisUtils;
import com.brandslink.cloud.logistics.entity.Attribute;
import com.brandslink.cloud.logistics.thirdLogistics.RemoteYunTuLogisticsService;
import com.brandslink.cloud.logistics.rabbitmq.MQSender;
import com.brandslink.cloud.logistics.remote.RemoteCenterService;
import com.brandslink.cloud.logistics.service.impl.AttributeServiceImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * 通用控制层
 */
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

    @Autowired
    private RemoteYunTuLogisticsService yunTuLogisticsService;
    @Autowired
    private RemoteCenterService remoteCenterService;

    //@Autowired
    //private ElasticsearchUtil elasticsearchUtil;

    @Autowired
    MQSender mqSender;

    @Autowired
    RedisUtils redisUtils;

    //@ApolloConfig
    //private Config config;

    private final static Logger _log = LoggerFactory.getLogger(CommonController.class);

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

    @RequestMapping(value = "/demo", method = RequestMethod.GET)
    @ApiOperation(value = "demo", notes = "")
    @ApiImplicitParams({@ApiImplicitParam(name = "jsonStr", value = "请求参数", paramType = "query")})
    public String demo(String jsonStr) throws Exception {
//        yunTuLogisticsService.getCountry();

//        return yunTuLogisticsService.getShippingMethods(jsonStr);

//        return yunTuLogisticsService.getGoodsType();

//        Map<String, Object> map = JSONObject.parseObject(jsonStr, Map.class);
//        return yunTuLogisticsService.getPriceTrial((String) map.get("countryCode"), MathUtils.getBigDecimal(map.get("weight")), (Integer) map.get("length"),
//                (Integer) map.get("width"), (Integer) map.get("height"), (Integer) map.get("packageType"));

//        ArrayList<String> list = JSONObject.parseObject(jsonStr, ArrayList.class);
//        return yunTuLogisticsService.getTrackingNumber(list);

//        return yunTuLogisticsService.getSender(jsonStr);

        return yunTuLogisticsService.getOrder(jsonStr);

//        return null;
    }

    @GetMapping(value = "/getSKUInfo")
    @ApiOperation(value = "demo", notes = "")
//    @ApiImplicitParams({@ApiImplicitParam(name = "sku", value = "请求参数", paramType = "query")})
    public String getSKUInfo() throws Exception {
        String[] sku = {"sku103"};
        String skuInfoBySku = remoteCenterService.getSkuInfoBySku(sku);
        return skuInfoBySku;
    }

    @GetMapping(value = "/deliver")
    @ApiOperation(value = "demo", notes = "")
    public String deliver() {
        String[] sku = {"sku103"};
        String skuInfoBySku = remoteCenterService.getSkuInfoBySku(sku);
        return skuInfoBySku;
    }
}
