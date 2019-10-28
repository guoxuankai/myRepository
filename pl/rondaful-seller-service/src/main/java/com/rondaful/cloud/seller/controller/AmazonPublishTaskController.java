package com.rondaful.cloud.seller.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.common.task.*;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.seller.enums.ReportTypeEnum;
import com.rondaful.cloud.seller.remote.RemoteLogisticsService;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 基础数据controller
 *
 * @author chenhan
 */
@Api(description = "定时器任务接口")
@RestController
@RequestMapping("/task")
public class AmazonPublishTaskController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(AmazonPublishTaskController.class);


    @Autowired
    private AmazonPublishListingService amazonPublishListingService;

    @Autowired
    private RemoteLogisticsService remoteLogisticsService;

    @Autowired
    private AuthorizationSellerService authorizationSellerService;


    @Autowired
    private RequestReportSynAsinTask requestReportSynAsinTask;

    @Autowired
    private GetReportRequestListSynAsinTask getReportRequestListSynAsinTask;

    @Autowired
    private GetReportSynAsinTask getReportSynAsinTask;

    @Autowired
    private GetMatchingProductListSynAsinTaxk getMatchingProductListSynAsinTaxk;

   
	@Autowired
	LoadProductTaskBatch loadProductTaskBatch;
	
	@Autowired
	LoadProductTaskBatchByUpdate loadProductTaskBatchByUpdate;
	
	@Autowired
	GetReportListTaskBatch getReportListTaskBatch;

	@Autowired
    private GetMatchingProductForIdTask getMatchingProductForIdTask;

	
	
	@Autowired
	ScanSuccessStatusTask scanSuccessStatusTask;
	
	@Autowired
	RedissLockUtil redissLockUtil;
	
    /**
     * 	刊登（批量刊登，针对状态6）
     */
    @PostMapping("/loadProductTaskBatch")
    @ApiOperation("Amazon刊登（批量刊登，针对状态6）")
    public void loadProductTaskBatch() {
        logger.debug("Amazon执行批量刊登");
        try {
        	new Thread(loadProductTaskBatch.get()).start();
        } catch (Exception e) {
            logger.error("Amazon执行批量刊登异常",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"Amazon执行批量刊登异常");
        }
    }
    
    
    /**
     * 	刊登（批量刊登，针对状态7）
     */
    @PostMapping("/loadProductTaskBatchByUpdate")
    @ApiOperation("Amazon刊登（批量刊登，针对状态7）")
    public void loadProductTaskBatchByUpdate() {
        logger.debug("Amazon执行批量刊登");
        try {
        	new Thread(loadProductTaskBatchByUpdate.get()).start();
        } catch (Exception e) {
            logger.error("Amazon执行批量刊登异常",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"Amazon执行批量刊登异常");
        }
    }
    
    /**
     * 	刊登（批量获取刊登报告）
     */
    @PostMapping("/getReportListTaskBatch")
    @ApiOperation("Amazon刊登（批量获取刊登报告）")
    public void getReportListTaskBatch() {
        logger.debug("Amazon刊登（批量获取刊登报告）");
        try {
        	new Thread(getReportListTaskBatch.get()).start();
            //getReportListTaskBatch.process();
        } catch (Exception e) {
            logger.error("Amazon刊登（批量获取刊登报告）",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"Amazon刊登（批量获取刊登报告）异常");
        }
    }
    
    
    /**
     * 	刊登（扫描报告）
     */
    @PostMapping("/scanSuccessStatusTask")
    @ApiOperation("Amazon刊登（扫描报告））")
    public void scanSuccessStatusTask() {
        logger.debug("Amazon刊登（扫描报告）");
        try {
        	new Thread(scanSuccessStatusTask.get()).start();
        } catch (Exception e) {
            logger.error("Amazon刊登（扫描报告）",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"Amazon刊登（扫描报告）异常");
        }
    }










// -------------------------  姚明写的



    /**
     * 查询授权数据，定时将授权数据redis作为生成在售商品报告的依据（全部商品商品报告）
     */
    @PostMapping("/upEmpowerListToRedis")
    @ApiOperation("查询授权数据，定时将授权数据redis作为生成在售商品报告的依据（全部商品报告）")
    public void upEmpowerListToRedis() {
        logger.info("查询授权数据，定时将授权数据redis作为生成在售商品报告的依据（全部商品报告）");
        try {
            requestReportSynAsinTask.upEmpowerListToRedis(ReportTypeEnum._GET_MERCHANT_LISTINGS_ALL_DATA_);
        } catch (Exception e) {
            logger.error("查询授权数据，定时将授权数据redis作为生成在售商品报告的依据（全部商品报告）异常",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查询授权数据，定时将授权数据redis作为生成在售商品报告的依据（全部商品报告）异常");
        }
    }



    /**
     * 向亚马逊请求创建报告（全部商品报告）
     */
    @PostMapping("/requestReportSysAsin")
    @ApiOperation("向亚马逊请求创建报告（全部商品报告）")
    public void requestReportSysAsin() {
        logger.info("调用向亚马逊请求创建报告任务（全部商品报告）");
        try {
            requestReportSynAsinTask.process(ReportTypeEnum._GET_MERCHANT_LISTINGS_ALL_DATA_);
        } catch (Exception e) {
            logger.error("调用向亚马逊请求创建报告任务（全部商品报告）异常",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"调用向亚马逊请求创建报告任务（全部商品报告）异常");
        }
    }

    /**
     * 获取报告状态（全部商品报告）
     */
    @PostMapping("/getReportRequestListSynAsin")
    @ApiOperation("调用向亚马逊获取报告状态（全部商品报告）")
    public void getReportRequestListSynAsin() {
        logger.info("调用向亚马逊获取报告状态（全部商品报告）任务");
        try {
            getReportRequestListSynAsinTask.process(ReportTypeEnum._GET_MERCHANT_LISTINGS_ALL_DATA_);
        } catch (Exception e) {
            logger.error("调用向亚马逊获取报告状态（全部商品报告）任务异常",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"调用向亚马逊获取报告状态（全部商品报告）任务异常");
        }
    }

    /**
     * 获取报告内容（全部商品报告）
     */
    @PostMapping("/getReportSynAsin")
    @ApiOperation("调用向亚马逊获取报告内容（全部商品报告）")
    public void getReportSynAsin() {
        logger.info("调用向亚马逊获取报告内容（全部商品报告）任务");
        try {
            getReportSynAsinTask.process(ReportTypeEnum._GET_MERCHANT_LISTINGS_ALL_DATA_);
        } catch (Exception e) {
            logger.error("调用向亚马逊获取报告内容（全部商品报告）任务异常",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"调用向亚马逊获取报告内容（全部商品报告）任务异常");
        }
    }


    /**
     * 同步品连没有的刊登数据
     */
    @PostMapping("/setPinLingHaveNotListing")
    @ApiOperation("同步品连没有的刊登数据")
    public void setPinLingHaveNotListing() {
        logger.info("同步品连没有的刊登数据任务（在售商品报告）任务");
        try {
            getMatchingProductForIdTask.process();
        } catch (Exception e) {
            logger.error("同步品连没有的刊登数据任务（在售商品报告）任务异常",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"同步品连没有的刊登数据任务（在售商品报告）任务异常");
        }
    }


    /**
     * 对比报告更改亚马逊刊登状态(商品报告)
     */
    @PostMapping("/updatePublishStatus")
    @ApiOperation("对比报告更改亚马逊刊登状态")
    public void updatePublishStatus() {
        logger.info("对比报告更改亚马逊刊登状态任务（在售商品报告）任务");
        try {
            amazonPublishListingService.updateDefaultToSuccess();
            amazonPublishListingService.updateSuccessToDefault();
        } catch (Exception e) {
            logger.error("对比报告更改亚马逊刊登状态任务（在售商品报告）任务异常",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"对比报告更改亚马逊刊登状态任务（在售商品报告）任务异常");
        }
    }





    /**
     * 刊登成功后立即获取ASIN编码
     */
    @PostMapping("/getMatchingProductListSynAsin")
    @ApiOperation("刊登成功后立即获取ASIN编码")
    public void getMatchingProductListSynAsin() {
        logger.info("调用刊登成功后立即调用亚马逊接口获取ASIN编码（不需要等到同步操作）任务");
        try {
             getMatchingProductListSynAsinTaxk.process();
        } catch (Exception e) {
            logger.error("调用刊登成功后立即调用亚马逊接口获取ASIN编码（不需要等到同步操作）任务异常",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"调用刊登成功后立即调用亚马逊接口获取ASIN编码（不需要等到同步操作）任务异常");
        }
    }
/*

    @Autowired
    private AmazonPublishListingService amazonPublishListingService;

    @Autowired
    private RemoteLogisticsService remoteLogisticsService;*/

    /**
     * 获取没有报告的asin（在售商品报告）
     */
    @PostMapping("/synchronizeAmazonInventory")
    @ApiOperation("同步亚马逊库存数量")
    public void synchronizeAmazonInventory() {
        logger.info("同步亚马逊库存数量 任务");
        try {
            ArrayList<Integer> amazonWarehouseIdList = amazonPublishListingService.getAmazonWarehouseIdList();
            if(amazonWarehouseIdList == null || amazonWarehouseIdList.size() == 0)
                return;

            Page<String> skuPage;
            List<String> skuList;
            String resulte;
            String dataString;
            JSONArray array;

            for(Integer warehouseId :   amazonWarehouseIdList){
                if(warehouseId == null)
                    continue;
                int row = 200;
                for(int page = 1,size = row; size == 200; page ++){
                    Page.builder(page, row);
                    skuPage = amazonPublishListingService.getAmazonListPlSkuByWarehouseId(warehouseId);
                    size = skuPage.getPageInfo().getSize();
                    skuList = skuPage.getPageInfo().getList();
                    if(skuList != null && skuList.size() >0){
                        try {
                            resulte = remoteLogisticsService.getsBySku(warehouseId, JSONObject.toJSONString(skuList));
                            logger.info("定时任务同步库存参数：{}，{}，返回结果{}",warehouseId,JSONObject.toJSONString(skuList),resulte);
                            dataString = Utils.returnRemoteResultDataString(resulte, "供应商服务异常");
                            array = JSONObject.parseArray(dataString);
                            for (Object o : array) {
                                if (o instanceof JSONObject) {
                                    JSONObject object = (JSONObject) o;
                                    amazonPublishListingService.updatePlSkuCount(new HashMap<String, String>(){{
                                     //   put("plSkuCount",object.getString("availableQty"));         //9.16版修改为本地可售
                                        put("plSkuCount",Integer.valueOf(object.getString("localAvailableQty "))<=0?"0":object.getString("localAvailableQty "));
                                        put("warehouseId",String.valueOf(warehouseId));
                                        put("plSku",object.getString("pinlianSku"));
                                    }});
                                }
                            }
                        }catch (Exception e){
                            logger.error("亚马逊同步查询库存异常",e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("同步亚马逊库存数量 任务异常",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"同步亚马逊库存数量 任务异常");
        }
    }






}


