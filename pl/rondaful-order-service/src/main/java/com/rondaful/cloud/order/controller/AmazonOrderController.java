package com.rondaful.cloud.order.controller;

import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersResponse;
import com.amazonservices.mws.orders._2013_09_01.samples.ListOrdersSample;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.ExcelUtil;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.entity.Amazon.AmazonEmpower;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrder;
import com.rondaful.cloud.order.entity.PlatformExport;
import com.rondaful.cloud.order.entity.Time;
import com.rondaful.cloud.order.mapper.AmazonEmpowerMapper;
import com.rondaful.cloud.order.service.IAmazonOrderService;
import com.rondaful.cloud.order.service.IAmazonUploadDataService;
import com.rondaful.cloud.order.service.ISysOrderService;
import com.rondaful.cloud.order.service.impl.AmazonOrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * 作者: wjc
 * 时间: 2018-12-03 15:58
 * 包名: com.rondaful.cloud.order.controller
 * 描述: 亚马逊平台订单相关接口
 */

@Api(description = "亚马逊平台订单控制层")
@RestController
@RequestMapping(value = "/amazonOrder")
public class AmazonOrderController extends BaseController {
    @Autowired
    private RedissLockUtil redissLockUtil;
    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;
    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AmazonOrderController.class);
    @Autowired
    private IAmazonOrderService amazonOrderService;
    @Autowired
    private AmazonEmpowerMapper EmpowerMapper;
    @Autowired
    private AmazonOrderServiceImpl orderService;
    private boolean autoSync = false;  //亚马逊自动同步订单标记，默认true：开启
    @Autowired
    private IAmazonUploadDataService amazonUploadDataService;
    @Autowired
    private ISysOrderService sysOrderService;

    @PostConstruct
    public void InitializedAmazonEmpowerData(){
        //服务启动后重置一次亚马逊授权信息表中 正在同步的都改为未同步状态且7分钟后才可以进行同步订单（自动或手动）操作
        EmpowerMapper.updateAmazonEmpowerDataReset();
    }

    @AspectContrLog(descrption = "自动上传亚马逊发货数据(30分钟一次)-wujiachuang", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "自动上传亚马逊发货数据(30分钟一次)-wujiachuang")
    @PostMapping("/autoUploadAmazonOrderDataTask")
    public void autoUploadAmazonOrderDataTask(){
        //定时任务之每半小时自动上传发货信息回亚马逊
        logger.info("定时任务___开始执行亚马逊上传数据操作！");
        sysOrderService.amazonDelivery();
    }

    @AspectContrLog(descrption = "检查亚马逊上传数据结果更新到数据库(15分钟一次)-wujiachuang", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "检查亚马逊上传数据结果更新到数据库(15分钟一次)-wujiachuang")
    @GetMapping("/autoCheckAmazonOrderUploadDataTask")
    public void autoCheckAmazonOrderUploadDataTask(){
        logger.info("定时任务____开始检查亚马逊上传数据结果并更新本地数据！");
        //每15分钟查询一次亚马逊上传数据状态更新至数据库，默认上传成功，失败了更新错误信息进上传信息表并更改品连上传数据的状态为失败！
        amazonUploadDataService.queryAmazonUploadDataAndDeal();
    }

    @AspectContrLog(descrption = "自动同步亚马逊订单(30分钟一次)", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "自动同步亚马逊订单(30分钟一次)-wujiachuang")
    @PostMapping("/autoGetAmazonOrders")
    public void getAmazonOrders() {
        logger.info("定时任务____开始执行亚马逊自动同步订单任务！");
        //远程调用授权接口获取亚马逊授权数据插入数据库,返回有效的授权信息
        List<AmazonEmpower> amazonEmpowerList = null;
        try {
            amazonEmpowerList = amazonOrderService.queryRemoteSellerServiceAndInsertDb("");
        } catch (Exception e) {
            logger.error("调用卖家服务获取授权信息失败!", e);
        }
        try {
            //同步亚马逊订单并通过SKU映射转入系统订单
            amazonOrderService.addAutoGetAmazonOrdersTask(amazonEmpowerList);
        } catch (ParseException e) {
            logger.error("自动同步亚马逊订单异常或亚马逊订单自动转入系统订单异常！" + e);
        }
    }

    @AspectContrLog(descrption = "检验亚马逊授权token是否有效-wujiachuang", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "检验亚马逊授权token是否有效-wujiachuang")
    @GetMapping("/checkAmazonTokenIsValid")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sellerId", value = "亚马逊卖家ID", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "marketplaceId", value = "亚马逊站点ID", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "mwsAuthToken", value = "亚马逊授权token", dataType = "string", paramType = "query", required = true)
    })
    public String checkAmazonTokenIsValid(String sellerId, String marketplaceId, String mwsAuthToken) {
        Time time = new Time(2011, 1, 1, 1, 1, 1, 1);
        try {
            ListOrdersResponse response = new ListOrdersSample().getAmazonOrders(time, sellerId, marketplaceId, mwsAuthToken);
            return "1";
        } catch (Exception e) {
            logger.error("授权token验证失败！失败原因：" + e.getMessage());
            return "0";
        }
    }

    @AspectContrLog(descrption = "通过品连账号手动同步亚马逊订单", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "通过品连账号手动同步亚马逊订单-wujiachuang")
    @GetMapping("/getAmazonOrdersByPLAccount")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "plAccount", value = "品连账号", dataType = "string", paramType = "query", required = true)
    })
    @RequestRequire(require = "plAccount", parameter = String.class)
    public String selectAmazonOrderByPLAccount(String plAccount) {
        plAccount = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        //根据品连用户名查询亚马逊授权信息
        List<AmazonEmpower> amazonEmpowerList = amazonOrderService.queryRemoteSellerServiceAndInsertDb(plAccount);
        if (CollectionUtils.isEmpty(amazonEmpowerList))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "查不到该用户的亚马逊授权信息!");
        try {
            List<List<AmazonEmpower>> lists = orderService.getLists(amazonEmpowerList);
            return amazonOrderService.addAutoGetAmazonOrders(lists, false);
        } catch (ParseException e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, ResponseCodeEnum.RETURN_CODE_100500.getMsg());
        }
    }

    @AspectContrLog(descrption = "平台订单手动转入系统", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "平台订单手动转入系统-wujiachuang")
    @PostMapping("/manualAmazonOrder2SysOrder")
    public String manualAmazonOrder2SysOrder(@RequestBody List<AmazonOrder> amazonOrderList) {
        if (getLoginUserInformationByToken.getUserInfo().getUser().getPlatformType() == 1 || getLoginUserInformationByToken.getUserInfo().getUser().getPlatformType() == 2) {
            if (CollectionUtils.isEmpty(amazonOrderList)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
            }
            for (AmazonOrder amazonOrder : amazonOrderList) {
                if (!redissLockUtil.tryLock(amazonOrder.getOrderId(), 0, 60)) {  //加锁
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "当前订单正在进行转单操作，请稍后尝试!");
                }
            }
            String result = amazonOrderService.addTurnToSysOrderAndUpdateStatus(amazonOrderList, false);
            for (AmazonOrder amazonOrder : amazonOrderList) {
                redissLockUtil.unlock(amazonOrder.getOrderId());//释放锁
            }
            return result;
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401);
        }
    }

    @AspectContrLog(descrption = "(品连管理系统)订单批量导出", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "(品连管理系统)订单批量导出-wujiachuang")
    @GetMapping("/exportAmazonOrders")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currPage", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "orderStatus", value = "亚马逊平台订单状态", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "plProcessStatus", value = "品连处理状态", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "shopName", value = "亚马逊店铺名", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sellerPlAccount", value = "卖家品连账号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "orderId", value = "亚马逊平台订单号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "startDate", value = "订单创建起始时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "endDate", value = "订单创建结束时间", dataType = "string", paramType = "query", required = false)
    })
    public ResponseEntity<byte[]> exportAmazonOrders(String shopName, String orderId, String sellerPlAccount,
                                                     String orderStatus, String plProcessStatus, String startDate,
                                                     String endDate, String currPage, String row) throws IOException {
        List<AmazonOrder> amazonOrderList = getAmazonOrders(shopName, orderId, sellerPlAccount, orderStatus, plProcessStatus, startDate, endDate);
        List<PlatformExport> platformExportList = amazonOrderService.setData(amazonOrderList, false);
        String[] header = {Utils.translation("平台订单号"), Utils.translation("下单时间"), Utils.translation("卖家"), Utils.translation("店铺账号"), Utils.translation("平台sku"), Utils.translation("商品单价（元）"), Utils.translation("商品数量"), Utils.translation("总售价（元）"), Utils.translation("收货人"), Utils.translation("国家"), Utils.translation("省/州"), Utils.translation("城市"), Utils.translation("地址"), Utils.translation("邮编"), Utils.translation("联系电话"), Utils.translation("订单状态"), Utils.translation("订单处理状态")};
        String[] key = {"orderId", "date", "seller", "sellerId", "platformSku", "itemPrice", "itemCount", "price", "name", "country", "province", "city", "address", "postcode ", "phone ", "orderStatus ", "processStatus "};
        String[] width = {"6000", "6000", "3000", "6000", "3000", "6000", "3000", "6000", "6000", "3000", "3000", "6000", "8000", "6000", "6000", "3000", "3000"};
        return ExcelUtil.outExcel(Utils.translation("亚马逊订单报表"), ExcelUtil.fileStream(amazonOrderService.export(platformExportList), ExcelUtil.createMap(header, key, width)), request);
    }

    @AspectContrLog(descrption = "(卖家系统)订单批量导出", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "(卖家系统)批量导出订单-wujiachuang")
    @GetMapping("/exportAmazonOrdersBySeller")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currPage", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "orderStatus", value = "亚马逊平台订单状态", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "plProcessStatus", value = "品连处理状态", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "shopName", value = "亚马逊店铺名", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sellerPlAccount", value = "卖家品连账号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "orderId", value = "亚马逊平台订单号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "startDate", value = "订单创建起始时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "endDate", value = "订单创建结束时间", dataType = "string", paramType = "query", required = false)
    })
    public ResponseEntity<byte[]> exportAmazonOrdersBySeller(String shopName, String orderId, String sellerPlAccount,
                                                             String orderStatus, String plProcessStatus, String startDate,
                                                             String endDate, String currPage, String row) throws IOException {
        List<AmazonOrder> amazonOrderList = getAmazonOrders(shopName, orderId, sellerPlAccount, orderStatus, plProcessStatus, startDate, endDate);
        List<PlatformExport> platformExportList = amazonOrderService.setData(amazonOrderList, true);
        String[] header = {Utils.translation("平台订单号"), Utils.translation("下单时间"), Utils.translation("店铺账号"), Utils.translation("平台sku"), Utils.translation("商品单价（元）"), Utils.translation("商品数量"), Utils.translation("总售价（元）"), Utils.translation("收货人"), Utils.translation("国家"), Utils.translation("省/州"), Utils.translation("城市"), Utils.translation("地址"), Utils.translation("邮编"), Utils.translation("联系电话"), Utils.translation("订单状态"), Utils.translation("订单处理状态")};
        String[] key = {"orderId", "date", "sellerId", "platformSku", "itemPrice", "itemCount", "price", "name", "country", "province", "city", "address", "postcode ", "phone ", "orderStatus ", "processStatus "};
        String[] width = {"6000", "6000", "6000", "3000", "6000", "3000", "6000", "6000", "3000", "3000", "6000", "8000", "6000", "6000", "3000", "3000"};
        return ExcelUtil.outExcel("亚马逊订单报表", ExcelUtil.fileStream(amazonOrderService.export(platformExportList), ExcelUtil.createMap(header, key, width)), request);
    }

    @AspectContrLog(descrption = "通过亚马逊平台订单号查询亚马逊订单", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "通过亚马逊平台订单号查询亚马逊订单-wujiachuang")
    @PostMapping("/selectAmazonOrderByOrderId")
    @ApiImplicitParams({@ApiImplicitParam(name = "orderId", value = "亚马逊平台订单号", dataType = "string", paramType = "query", required = true)})
    @RequestRequire(require = "orderId", parameter = String.class)
    public AmazonOrder selectAmazonOrderByOrderIdAndPaymentTime(String orderId) {
        return amazonOrderService.selectAmazonOrderByOrderId(orderId);
    }

    @AspectContrLog(descrption = "不定条件查询亚马逊平台订单", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "不定条件查询亚马逊平台订单-wujiachuang")
    @PostMapping("/selectAmazonOrderByMultiCondition")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currPage", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "orderStatus", value = "亚马逊平台订单状态", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "plProcessStatus", value = "品连处理状态", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "shopName", value = "亚马逊店铺名", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sellerPlAccount", value = "卖家品连账号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "orderId", value = "亚马逊平台订单号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "startDate", value = "订单创建起始时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "endDate", value = "订单创建结束时间", dataType = "string", paramType = "query", required = false)
    })
    public Page selectAmazonOrderByMultiCondition(String shopName, String orderId, String sellerPlAccount,
                                                  String orderStatus, String plProcessStatus, String startDate,
                                                  String endDate, String currPage, String row) {
        Page.builder(currPage, row);
        return new Page(amazonOrderService.selectAmazonOrderByMultiCondition(shopName, orderId, sellerPlAccount, orderStatus, plProcessStatus, startDate, endDate));
    }

    public List<AmazonOrder> getAmazonOrders(String shopName, String orderId, String sellerPlAccount,
                                             String orderStatus, String plProcessStatus, String startDate,
                                             String endDate) {
        List<AmazonOrder> amazonOrderList = null;
        try {
            amazonOrderList = amazonOrderService.getExportResults(shopName, orderId, sellerPlAccount, orderStatus, plProcessStatus, startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (amazonOrderList == null) {
            return null;
        }
        return amazonOrderList;
    }

    @AspectContrLog(descrption = "通过亚马逊订单ID查询订单详情", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "通过亚马逊订单ID查询订单详情-wujiachuang")
    @GetMapping("/getAmazonOrderDetailByOrderId")
    @ApiImplicitParam(name = "orderId", value = "亚马逊订单Id", dataType = "string", paramType = "query", required = true)
    public AmazonOrder getAmazonOrderDetailByOrderId(String orderId) {
        return amazonOrderService.getAmazonOrderDetailByOrderId(orderId);
    }
}