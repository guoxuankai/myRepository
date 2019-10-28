package com.rondaful.cloud.seller.controller;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.seller.common.task.GetMatchingProductListSynAsinTaxk;
import com.rondaful.cloud.seller.common.task.GetReportRequestListSynAsinTask;
import com.rondaful.cloud.seller.common.task.GetReportSynAsinTask;
import com.rondaful.cloud.seller.common.task.RequestReportSynAsinTask;
import com.rondaful.cloud.seller.entity.Commodity;
import com.rondaful.cloud.seller.enums.ReportTypeEnum;
import com.rondaful.cloud.seller.rabbitmq.TestSender;
import com.rondaful.cloud.seller.remote.RemoteOrderRuleService;
import com.rondaful.cloud.seller.service.ICommodityService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController extends BaseController {

    private final static Logger log = LoggerFactory.getLogger(TestController.class);

    @Value("${spring.commodity.datasource.name}")
    private String a;

    @Autowired
    private ICommodityService commodityService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private TestSender helloSender;



    @Autowired
    private RequestReportSynAsinTask requestReportTaskBatch;

    @Autowired
    private GetReportRequestListSynAsinTask getReportRequestListTaskBatch;

    @Autowired
    private GetReportSynAsinTask getReportTaskBatch;

    @Autowired
    private GetMatchingProductListSynAsinTaxk getMatchingProductListSynAsinTaxk;

    @Autowired
    private RemoteOrderRuleService remoteOrderRuleService;

    private Integer mess = 1;


    @ApiOperation(value = "测试", notes = "")
    @GetMapping("/map")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String test() {
        return a;
    }

    @ApiOperation(value = "查询所有商品", notes = "page当前页码，row每页显示行数", response = Commodity.class)
    @GetMapping("/findAll")
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page findAll(String page, String row) {
        Page.builder(page, row);
        Page p = commodityService.page(null);
        return p;
    }

    @ApiOperation(value = "更新商品信息", notes = "")
    @PostMapping("/update")
    public int update(Commodity commodity) throws NoSuchFieldException, IllegalAccessException {
        //Commodity co = commodityService.selectByPrimaryKey(commodity.getId());
        //co.setCommodityName(commodity.getCommodityName());

        int result = commodityService.updateByPrimaryKeySelective(commodity);
        //int i = 1/0;
        return result;
    }


    @GetMapping("/send")
    public void send() {
        helloSender.send();
    }


    @ApiOperation(value = "添加", notes = "")
    @PostMapping("/addMessageToList")
    public int addMessageToList() {
        try {
            redisUtils.setListJsonMessageFromLift("myList", new JSONObject() {{
                put("测试数据key", "ceshishuj value:" + mess);
            }});
            return mess++;
        } catch (Exception e) {
            log.error("添加", e);
        }
        return 1;
    }

    @ApiOperation(value = "获取", notes = "")
    @PostMapping("/getMessageToList")
    public Object getMessageToList() {
        try {
            Object myList = redisUtils.getListJsonMessageFromRight("myList");
            return myList;
        } catch (Exception e) {
            log.error("获取", e);
        }
        return null;
    }


    @ApiOperation(value = "清空", notes = "")
    @PostMapping("/removeMessageToList")
    public Object removeMessageToList() {
        try {
            // redisUtils.removeList("myList");
            String sellerSkuMapByPlatformSku = remoteOrderRuleService.getSellerSkuMapByPlatformSku("Amazon", "1399", "ckE2YvHBFtE017CBLULn1", "474");
            return null;
        } catch (Exception e) {
            log.error("获取", e);
        }
        return null;
    }


/*    @Autowired
    private RequestReportTaskBatch requestReportTaskBatch;

    @Autowired
    private GetReportRequestListTaskBatch getReportRequestListTaskBatch;

    @Autowired
    private GetReportTaskBatch getReportTaskBatch;*/


    @ApiOperation(value = "亚马逊刊登同步测试", notes = "")
    @PostMapping("/amazonListingTest")
    @ApiImplicitParam(name = "integer", value = "1：设置同步队列，2：请求报告，3：获取报告状态，4：获取报告并解析数据", dataType = "String", paramType = "query")
    public Object amazonListingTest(Integer integer) {
        try {
           if(integer == 1){
               requestReportTaskBatch.upEmpowerListToRedis(ReportTypeEnum._GET_MERCHANT_LISTINGS_DATA_);
           }else if(integer == 2){
               requestReportTaskBatch.process(ReportTypeEnum._GET_MERCHANT_LISTINGS_DATA_);
           }else if(integer == 3){
               getReportRequestListTaskBatch.process(ReportTypeEnum._GET_MERCHANT_LISTINGS_DATA_);
           }else if(integer == 4){
               getReportTaskBatch.process(ReportTypeEnum._GET_MERCHANT_LISTINGS_DATA_);
           }
//           else {
//               getMatchingProductListSynAsinTaxk.process(ReportTypeEnum._GET_MERCHANT_LISTINGS_DATA_);
//           }
        } catch (Exception e) {
            log.error("亚马逊刊登同步测试", e);
        }
        return null;
    }




}
