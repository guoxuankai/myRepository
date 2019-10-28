package com.rondaful.cloud.order.controller;

import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.model.vo.freight.LogisticsDetailVo;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListDTO;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.ExcelUtil;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.OrderInfoVO;
import com.rondaful.cloud.order.entity.OrderRecord;
import com.rondaful.cloud.order.entity.PLOrderInfoDTO;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SysOrderLog;
import com.rondaful.cloud.order.entity.SystemExport;
import com.rondaful.cloud.order.entity.erpentity.WareHouseDeliverCallBack;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.entity.wmsdto.WareHouseWmsCallBack;
import com.rondaful.cloud.order.model.dto.syncorder.SplitPackageDTO;
import com.rondaful.cloud.order.model.dto.sysOrderInvoice.SysOrderInvoiceInsertOrUpdateDTO;
import com.rondaful.cloud.order.model.vo.sysOrderList.SysOrderVo;
import com.rondaful.cloud.order.service.*;
import com.rondaful.cloud.order.utils.ExcelImportUtil;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import com.rondaful.cloud.order.utils.ParamCheckUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "品连系统订单控制层")
@RestController
@RequestMapping(value = "/sysOrder")
public class SystemOrderController extends BaseController {
    @Autowired
    private ISystemOrderCommonService systemOrderCommonService;
    @Autowired
    private ISystemOrderService systemOrderService;
    @Autowired
    private IreplenishService replenishService;
    @Autowired
    private ISysOrderService sysOrderService;
    @Autowired
    private ISysOrderLogService sysOrderLogService;
    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    @Autowired
    private ISysOrderInvoiceService sysOrderInvoiceService;

    @Autowired
    private ISysOrderListService sysOrderListService;

    @Autowired
    private ISplitPackgeService splitPackgeService;

    @Autowired
    private IWmsService wmsService;

    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SystemOrderController.class);

    @ApiOperation(value = "内部：供应商服务调用-wujiachuang")
    @GetMapping("/getOrderInfoToSupplier")
    public OrderInfoVO getOrderInfoToSupplier(String packageId) {
        return sysOrderService.getOrderInfoToSupplier(packageId);
    }

    @ApiOperation(value = "内部调用：供财务查询订单数据（前端调用）-wujiachuang")
    @PostMapping("/getPLOrderInfo")
    public List<PLOrderInfoDTO> getPLOrderInfo(@RequestBody List<String> sysOrderIds) {
        return sysOrderService.getPLOrderInfo(sysOrderIds);
    }

    @ApiOperation(value = "内部调用：供财务批量查询订单数据（后台调用）-wujiachuang")
    @PostMapping("/getPLOrderInfoBatch")
    public List<PLOrderInfoDTO> getPLOrderInfoBatch(@RequestBody List<String> sysOrderIds) {
        return sysOrderService.getPLOrderInfoBatch(sysOrderIds);
    }

    @ApiOperation(value = "内部调用：供财务查询订单状态-wujiachuang")
    @PostMapping("/judgePLOrderStatus")
    public boolean judgePLOrderStatus(@RequestBody List<String> sysOrderIds) {
        return sysOrderService.judgePLOrderStatus(sysOrderIds);
    }

    @AspectContrLog(descrption = "更改已发货90天的采购订单为已完成状态(30分钟一次)-wujiachuang", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "更改已发货90天的采购订单为已完成状态(30分钟一次)-wujiachuang")
    @PostMapping("/updateSysOrderStatus")
    public void updateSysOrderStatus(){
        logger.info("执行定时任务：更改已发货90天的采购订单为已完成状态！");
        sysOrderService.updateSysOrderStatus();
    }
    @AspectContrLog(descrption ="预估利润计算", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "预估利润计算")
    @PostMapping("/estimatedProfitCalculation")
    @ApiImplicitParam(name = "sysOrder", value = "系统订单对象", required = true)
    public Map<String, Object> estimatedProfitCalculation(@RequestBody SysOrder sysOrder) {
        return sysOrderService.getGrossMargin(sysOrder);
    }

    @ApiOperation(value = "取消作废订单")
    @PostMapping("/cancelInvalidOrders")
    @ApiImplicitParam(name = "orderId", value = "订单ID", dataType = "string", paramType = "query", required = true)
    public String cancelInvalidOrders(String orderId) {
        return sysOrderService.cancelInvalidOrders(orderId);
    }

    @ApiOperation(value = "查询订单时间轴-wujiachuang")
    @PostMapping("/queryOrderSchedule")
    @ApiImplicitParam(name = "orderId", value = "订单ID", dataType = "string", paramType = "query", required = true)
    public Map<String, Object> queryOrderSchedule(String orderId) {
        return sysOrderLogService.queryOrderSchedule(orderId);
    }

    @ApiOperation(value = "更改手工标记异常信息-wujiachuang")
    @PostMapping("/updateMarkException")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单ID", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "text", value = "异常信息文本", dataType = "string", paramType = "query", required = true)
    })
    public void updateMarkException(String orderId, String text) {
        sysOrderService.updateMarkException(orderId, text);
    }

    @ApiOperation(value = "手工新增系统订单-wujiachuang")
    @PostMapping("/createSysOrder")
    public String createSysOrder(@RequestBody SysOrder sysOrder) {
        return sysOrderService.addSysOrder(sysOrder);
    }

    @ApiOperation(value = "编辑系统订单-wujiachuang")
    @PostMapping("/editSysOrder")
    @ApiImplicitParam(name = "area", value = "编辑哪块区域（1:地址 2:物流 3:全部）", dataType = "string", paramType = "query", required = true)
    public String editSysOrder(@RequestBody SysOrderNew orderNew, String area) {
        return sysOrderService.updateSysOrder(orderNew, area);
    }

    @ApiOperation(value = "根据来源订单号查找品连订单号-wujiachuang")
    @PostMapping("/queryPlOrderIdBySourceOrderId")
    @ApiImplicitParam(name = "sourceOrderIds", value = "来源平台订单号集合", required = true)
    public Map<String, List<String>> queryPlOrderIdBySourceOrderId(@RequestBody List<String> sourceOrderIds) {
        return sysOrderService.queryPlOrderIdBySourceOrderId(sourceOrderIds);
    }

    @AspectContrLog(descrption = "更改系统订单为售后状态-wujiachuang", actionType = SysLogActionType.UDPATE)
    @PostMapping("/updateOrderStatus")
    @ApiOperation(value = "更改系统订单为售后状态 -wujiachuang")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysOrderId",value = "系统订单ID",dataType = "string", paramType = "query", required = true ),
            @ApiImplicitParam(name = "status",value = "售后状态（0未售后  1全部售后 2部分售后）",dataType = "byte", paramType = "query", required = true )
    })
    public void updateOrderStatus(String sysOrderId,byte status) {
        sysOrderService.updateOrderStatus(sysOrderId,status);
    }

    @AspectContrLog(descrption = "更改包裹详情某SKU为售后状态-wujiachuang", actionType = SysLogActionType.UDPATE)
    @PostMapping("/updateOrderPackageItemStatus")
    @ApiOperation(value = "更改包裹详情某SKU为售后状态 -wujiachuang")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderTrackId",value = "包裹号",dataType = "string", paramType = "query", required = true ),
            @ApiImplicitParam(name = "sku",value = "订单项SKU",dataType = "string", paramType = "query", required = true ),
            @ApiImplicitParam(name = "status",value = "售后状态（0未售后  1售后）",dataType = "byte", paramType = "query", required = true )
    })
    public void updateOrderPackageItemStatus(String orderTrackId,String sku,byte status) {
        sysOrderService.updateOrderPackageItemStatus(orderTrackId, sku, status);
    }

    @ApiOperation(value = "订单操作日志 -wujiachuang")
    @GetMapping("/sysOrderLog")
    @ApiImplicitParam(name = "sysOrderId", value = "系统订单Id", dataType = "string", paramType = "query", required = true)
    public List<SysOrderLog> sysOrderLog(String sysOrderId) {
        List<SysOrderLog> list = sysOrderLogService.selectSysOrderLogByOrderId(sysOrderId);
        for (SysOrderLog sysOrderLog : list) {
            sysOrderLog.setContent(Utils.translation(sysOrderLog.getContent()));
        }
        return list;
    }

    @ApiOperation(value = "获取订单数据记录 -wujiachuang")
    @PostMapping("/getOrderRecord")
    public OrderRecord getOrderRecord() {
        OrderRecord orderRecord = sysOrderService.getOrderRecord(getLoginUserInformationByToken.getUserDTO().getTopUserId().toString());
        return orderRecord;
    }

    @ApiOperation(value = "获取当前用户上个月的预估利润和当月的预估利润 -wujiachuang")
    @GetMapping("/getUserProfit")
    public Map<String, BigDecimal> getUserProfit() {
        return sysOrderService.getUserProfit(getLoginUserInformationByToken.getUserDTO().getTopUserLoginName());
    }

    @ApiOperation(value = "解析excel新增订单-wujiachuang", notes = "解析excel新增订单（批量）")
    @PostMapping("/insertSysOrderByExcel")
    public Object insertSysOrderByExcel(HttpServletRequest request, HttpServletResponse response, @RequestParam("file") MultipartFile file) {
        try {
            List<ArrayList<String>> readResult = null;//总行记录
            //判断文件是否为空
            if ((file).isEmpty()) {
                return new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "上传文件为空");
            }
            //判断文件大小
            long size = file.getSize();
            String name = file.getOriginalFilename();
            if (StringUtils.isBlank(name) || size == 0) {
                return new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "文件名为空");
            }
            //获取文件后缀
            String postfix = ExcelImportUtil.getPostfix(name);
            //读取文件内容
            if (StringUtils.equals("xlsx", postfix)) {
                SysOrder sysOrder = new SysOrder();
                //解析excel文件
                readResult = ExcelImportUtil.readXlsx(file);
                //遍历结果
                for (ArrayList<String> arrayList : readResult) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        //设置vo插入系统订单
                        //设置vo插入系统订单项
                    }
                }
            } else if (StringUtils.equals("xls", postfix)) {
                SysOrder sysOrder = new SysOrder();
                //解析excel文件
                readResult = ExcelImportUtil.readXls(file);
                if (readResult == null || readResult.size() == 0) {
                    return new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "文件解析结果为空");
                }
                //遍历结果
                for (ArrayList<String> arrayList : readResult) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        //设置vo插入系统订单
                        //设置vo插入系统订单项
                    }
                }
            } else {
                return new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "数据写入失败");
            }
            return new GlobalException(ResponseCodeEnum.RETURN_CODE_100200, "数据写入成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常");
        }
    }

    @AspectContrLog(descrption = "(卖家系统)订单批量导出", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "(卖家系统)批量导出订单-wujiachuang")
    @GetMapping("/exportSystemOrders")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currPage", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "orderStatus", value = "订单发货状态:待发货,缺货,配货中,已拦截,已发货,部分发货,已作废,已完成", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "isAfterSaleOrder", value = "是否为售后订单（0否1是，默认查询全部）", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "splittedOrMerged", value = "订单类型：general或者split或者merged", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "platformSellerAccount", value = "卖家平台账号（店铺名）", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sourceOrderId", value = "来源平台订单号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sellerPlAccount", value = "卖家品连账号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sysOrderId", value = "品连系统订单ID", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "recordNumber", value = "平台记录号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "orderTrackId", value = "订单参考号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "isLogisticsAbnormal", value = "是否物流异常订单", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "startDate", value = "订单创建起始时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "endDate", value = "订单创建结束时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "startTime", value = "订单发货起始时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "endTime", value = "订单发货结束时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "orderSource", value = "订单来源:other:0 eBay订单:4 Amazon订单:5 AliExpress订单:6", dataType = "byte", paramType = "query", required = false),
            @ApiImplicitParam(name = "payStatus", value = "付款状态：待付款:0 冻结中:11  已付款:21  已付款，待补扣:30", dataType = "byte", paramType = "query", required = false),
            @ApiImplicitParam(name = "errorOrder", value = "异常订单：1", dataType = "byte", paramType = "query", required = false),
            @ApiImplicitParam(name = "sku", value = "品连SKU", dataType = "string", paramType = "query", required = false)
    })
    public ResponseEntity<byte[]> exportSystemOrders(String sku,Byte errorOrder, Byte payStatus, Byte orderSource, String recordNumber, String orderTrackId, String isAfterSaleOrder,
                                                     String sourceOrderId, String isLogisticsAbnormal, String splittedOrMerged,
                                                     String platformSellerAccount, String sellerPlAccount, String sysOrderId,
                                                     String orderStatus, String startDate, String endDate, String startTime,
                                                     String endTime, String currPage, String row) throws IOException {
        List<SystemExport> systemExportList = getSysOrders(sku,true,errorOrder, payStatus, orderSource, recordNumber, orderTrackId, isAfterSaleOrder, sourceOrderId, isLogisticsAbnormal, splittedOrMerged, platformSellerAccount, sellerPlAccount, sysOrderId, orderStatus, startDate, endDate, startTime, endTime);
        return getResponseEntity(systemExportList);
    }

    public ResponseEntity<byte[]> getResponseEntity(List<SystemExport> systemExportList) throws IOException {
        String[] header = {Utils.translation("订单号"), Utils.translation("平台订单号"), Utils.translation("卖家"), Utils.translation("店铺账号"), Utils.translation("包裹号"), Utils.translation("平台SKU"), Utils.translation("刊登人"), Utils.translation("品连Sku"), Utils.translation("采购单价（$）"), Utils.translation("运费单价（$）"), Utils.translation("数量"), Utils.translation("商品总价（$）"), Utils.translation("总运费（$）"), Utils.translation("订单金额（$）"), Utils.translation("平台销售金额"), Utils.translation("预估利润（$）"), Utils.translation("预估利润率"), Utils.translation("仓库"), Utils.translation("邮寄方式"), Utils.translation("跟踪单号"), Utils.translation("订单状态"), Utils.translation("创建时间"), Utils.translation("发货时间")};
        String[] key = {"orderId", "platformOrderId", "seller", "sellerId", "packageId", "platformSku", "placer", "plSkus", "itemPrice", "freightUnitPrice", "itemCount", "goodsTotalPrice", "totalFreight", "prices", "platformSalesAmount", "profit", "profitMargin", "wareHouse", "deliveryMethod", "trackId", "orderStatus", "createTime", "deliveryTime"};
        String[] width = {"3000", "3000", "3000", "3000", "3000", "8000", "6000", "6000", "6000", "6000", "3000", "6000", "6000", "3000", "3000", "6000", "8000", "6000", "6000", "3000", "3000", "3000", "3000", "3000"};
        return ExcelUtil.outExcel(Utils.translation("品连订单报表"), ExcelUtil.fileStream(sysOrderService.export(systemExportList), ExcelUtil.createMap(header, key, width)), request);
    }

    public List<SystemExport> getSysOrders(String sku,boolean isSeller,Byte errorOrder, Byte payStatus, Byte orderSource, String recordNumber, String orderTrackId, String isAfterSaleOrder, String sourceOrderId, String isLogisticsAbnormal, String splittedOrMerged, String platformSellerAccount, String sellerPlAccount, String sysOrderId, String orderStatus, String startDate, String endDate, String startTime, String endTime) {
        List<SystemExport> systemExportList = null;
        try {
            systemExportList = sysOrderService.getExportResults(sku,isSeller,errorOrder, payStatus, orderSource, recordNumber, orderTrackId, isAfterSaleOrder, sourceOrderId, isLogisticsAbnormal, splittedOrMerged, platformSellerAccount, sellerPlAccount, sysOrderId, orderStatus, startDate, endDate, startTime, endTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (systemExportList == null) {
            return null;
        }
        return systemExportList;
    }

    @AspectContrLog(descrption = "(品连管理系统)订单批量导出", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "(品连系统)批量导出订单-wujiachuang")
    @GetMapping("/exportSystemOrdersByPL")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currPage", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "orderStatus", value = "订单发货状态:待发货,缺货,配货中,已拦截,已发货,部分发货,已作废,已完成", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "isAfterSaleOrder", value = "是否为售后订单（0否1是，默认查询全部）", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "splittedOrMerged", value = "订单类型：general或者split或者merged", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "platformSellerAccount", value = "卖家平台账号（店铺名）", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sourceOrderId", value = "来源平台订单号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sellerPlAccount", value = "卖家品连账号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sysOrderId", value = "品连系统订单ID", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "recordNumber", value = "平台记录号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "orderTrackId", value = "订单参考号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "isLogisticsAbnormal", value = "是否物流异常订单", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "startDate", value = "订单创建起始时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "endDate", value = "订单创建结束时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "startTime", value = "订单发货起始时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "endTime", value = "订单发货结束时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "orderSource", value = "订单来源:other:0 eBay订单:4 Amazon订单:5 AliExpress订单:6", dataType = "byte", paramType = "query", required = false),
            @ApiImplicitParam(name = "payStatus", value = "付款状态：待付款:0 冻结中:11  已付款:21  已付款，待补扣:30", dataType = "byte", paramType = "query", required = false),
            @ApiImplicitParam(name = "errorOrder", value = "异常订单：1", dataType = "byte", paramType = "query", required = false),
            @ApiImplicitParam(name = "sku", value = "品连SKU", dataType = "string", paramType = "query", required = false)
    })
    public ResponseEntity<byte[]> exportSystemOrdersByPL(String sku,Byte errorOrder, Byte payStatus, Byte orderSource, String recordNumber, String orderTrackId, String isAfterSaleOrder,
                                                         String sourceOrderId, String isLogisticsAbnormal, String splittedOrMerged,
                                                         String platformSellerAccount, String sellerPlAccount, String sysOrderId,
                                                         String orderStatus, String startDate, String endDate, String startTime,
                                                         String endTime, String currPage, String row) throws IOException, ParseException {
        List<SystemExport> systemExportList = getSysOrders(sku,false,errorOrder, payStatus, orderSource, recordNumber, orderTrackId, isAfterSaleOrder, sourceOrderId, isLogisticsAbnormal, splittedOrMerged, platformSellerAccount, sellerPlAccount, sysOrderId, orderStatus, startDate, endDate, startTime, endTime);
        return getResponseEntity(systemExportList);
    }

    @AspectContrLog(descrption = "不定条件查询系统订单-wujiachuang", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "不定条件查询系统订单-wujiachuang")
    @PostMapping("/selectSysOrderByMultiCondition")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currPage", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "orderStatus", value = "订单发货状态:待发货,缺货,配货中,已拦截,已发货,部分发货,已作废,已完成", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "isAfterSaleOrder", value = "是否为售后订单（0否1是，默认查询全部）", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "splittedOrMerged", value = "订单类型：general或者split或者merged", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "platformSellerAccount", value = "卖家平台账号（店铺名）", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sourceOrderId", value = "来源平台订单号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sellerPlAccount", value = "卖家品连账号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sysOrderId", value = "品连系统订单ID", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "recordNumber", value = "平台记录号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "orderTrackId", value = "订单参考号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "isLogisticsAbnormal", value = "是否物流异常订单", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "startDate", value = "订单创建起始时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "endDate", value = "订单创建结束时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "startTime", value = "订单发货起始时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "endTime", value = "订单发货结束时间", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "orderSource", value = "订单来源:other:0 eBay订单:4 Amazon订单:5 AliExpress订单:6", dataType = "byte", paramType = "query", required = false),
            @ApiImplicitParam(name = "payStatus", value = "付款状态：待付款:0 冻结中:11  已付款:21  已付款，待补扣:30", dataType = "byte", paramType = "query", required = false),
            @ApiImplicitParam(name = "errorOrder", value = "异常订单：1", dataType = "byte", paramType = "query", required = false),
            @ApiImplicitParam(name = "sku", value = "品连SKU", dataType = "string", paramType = "query", required = false)
    })
    public Page selectSysOrderByMultiCondition(String sku,Byte errorOrder, Byte payStatus, Byte orderSource, String recordNumber, String orderTrackId, String isAfterSaleOrder,
                                                   String sourceOrderId, String isLogisticsAbnormal, String splittedOrMerged,
                                                   String platformSellerAccount, String sellerPlAccount, String sysOrderId,
                                                   String orderStatus, String startDate, String endDate, String startTime,
                                                   String endTime, String currPage, String row) {
        Page.builder(currPage, row);
        return new Page(sysOrderService.selectSysOrderByMultiCondition(sku,errorOrder, payStatus, orderSource, recordNumber, orderTrackId, isAfterSaleOrder, sourceOrderId, isLogisticsAbnormal, splittedOrMerged, platformSellerAccount, sellerPlAccount, sysOrderId, orderStatus, startDate, endDate, startTime, endTime, null, null));
    }

    @ApiOperation(value = "通过卖家平台账号（店铺名）查询系统订单-wujiachuang")
    @GetMapping("/selectSysOrdersByStatus")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currPage", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "platformSellerAccount", value = "卖家平台账号", dataType = "string", paramType = "query", required = true)
    })
    public Page selectAmazonOrdersByOrderStatus(String currPage, String row, String platformSellerAccount) {
        Page.builder(currPage, row);
        Page<SysOrder> page = sysOrderService.selectSysOrdersByPlatformSellerAccount(platformSellerAccount);
        return page;
    }

    @ApiOperation(value = "通过卖家品连账号查询系统订单-wujiachuang")
    @PostMapping("/selectSysOrderBySellerPlAccount")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sellerPlAccount", value = "卖家品连账号", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "currPage", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true),
    })
    public Page selectSysOrderBySellerPlAccount(String sellerPlAccount, String currPage, String row) {
        Page.builder(currPage, row);
        Page<SysOrder> page = sysOrderService.selectSysOrderBySellerPlAccount(sellerPlAccount);
        return page;
    }

    @ApiOperation(value = "通过品连系统订单ID查询订单详情-wujiachuang")
    @GetMapping("/getSysOrderDetailByPlOrderId")
    @ApiImplicitParam(name = "orderId", value = "系统订单Id", dataType = "string", paramType = "query", required = true)
    public SysOrderNew getSysOrderDetailByPlOrderId(String orderId) {
       return sysOrderService.getSysOrderContainAllSkuByOrderId(orderId);
    }

    @ApiOperation(value = "通过品连系统订单ID查询订单详情（供内部售后服务调用)-wujiachuang")
    @GetMapping("/getSysOrderDetailByPlOrderIdToCMS")
    @ApiImplicitParam(name = "orderId", value = "系统订单Id", dataType = "string", paramType = "query", required = true)
    public SysOrderNew getSysOrderDetailByPlOrderIdToCMS(String orderId) {
        return  sysOrderService.queryOrderByOther(orderId);
    }

    @ApiOperation(value = "通过品连系统订单ID查询订单详情-wujiachuang")
    @GetMapping("/getSysOrderDetail")
    @ApiImplicitParam(name = "orderId", value = "系统订单Id", dataType = "string", paramType = "query", required = true)
    public SysOrderNew getSysOrderDetail(String orderId) {
        return  sysOrderService.queryOrderByOther(orderId);
    }

    @ApiOperation(value = "批量查询订单详情(供内部财务服务调用)-wujiachuang")
    @PostMapping("/getSysOrderPackageInfo")
    public Map<String,List<SysOrderPackage>> getSysOrderDetailByPlOrderId(@RequestBody List<String> orderList) {
        Map<String,List<SysOrderPackage>> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(orderList)) {
            orderList.forEach(orderId -> {
                SysOrderNew orderNew = sysOrderService.getSysOrderDetailByPlOrderId(orderId);
                if (orderNew != null) {
                    map.put(orderNew.getSysOrderId(), orderNew.getSysOrderPackageList());
                }
            });
        }
            return map;
    }

    @AspectContrLog(descrption = "查询用户当月或者上月的毛利 -wujiachuang", actionType = SysLogActionType.QUERY)
    @GetMapping("/findUserGrossMargin/{type}")
    @ApiOperation(value = "查询用户当月或者上月的毛利 -wujiachuang")
    public Map<String, Object> findUserGrossMargin(
            @ApiParam(value = "Y当月、N上月、X全部", name = "type", required = true) @PathVariable String type) {
        if (type.toUpperCase().equals("X") || type.toUpperCase().equals("N") || type.toUpperCase().equals("Y")) {
            return sysOrderService.findUserGrossMargin(getLoginUserInformationByToken.getUserDTO().getTopUserLoginName(), type.toUpperCase());
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        }
    }

    @ApiOperation(value = "作废系统订单-wujiachuang")
    @GetMapping("/invalidOrders")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysOrderId", value = "系统订单ID", dataType = "string", paramType = "query", required = true)
    })
    public String invalidOrders(String sysOrderId) {
        return sysOrderService.deleteInvalidOrders(sysOrderId);
    }

    @ApiOperation(value = "拦截系统订单-wujiachuang")
    @PostMapping("/interceptSystemOrder")
    @ApiImplicitParam(name = "sysOrderId", value = "系统订单ID", dataType = "string", paramType = "query", required = true)
    public String interceptSystemOrder(String sysOrderId) throws Exception {
        String msg = sysOrderService.interceptSystemOrder(sysOrderId);
        if (msg.equals(Constants.InterceptResponse.RESPONSE_1)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, Constants.InterceptResponse.RESPONSE_1);
        }
        return msg;
    }
    @ApiOperation(value = "对730版本的已拦截订单进行取消冻结-wujiachuang")
    @GetMapping("/intercept")
    @ApiImplicitParam(name = "name", value = "姓名", dataType = "string", paramType = "query", required = true)
    public Object intercept(String name)  {
        if ("wujiachuang".equalsIgnoreCase(name)) {
       return sysOrderService.intercept();
        }else{
            return "gun";
        }
    }




//    @PostMapping("/saveSplittedSysOrder")
//    @ApiOperation("拆分系统订单")
//    @ApiImplicitParams({@ApiImplicitParam(name = "sysOrders", value = "拆分订单后保存被拆分系统订单子订单对象集合", paramType = "body", required = true)})
//    public void saveSplittedSysOrder(@RequestBody List<SysOrder> sysOrders) throws Exception {
//        systemOrderService.saveSplittedSysOrder(sysOrders);
//    }

    @PostMapping("/saveSplittedSysPackage")
    @ApiOperation("拆分包裹")
    @AspectContrLog(descrption = "拆分包裹", actionType = SysLogActionType.UDPATE)
    public void saveSplittedSysPackage(@RequestBody SplitPackageDTO splitPackageDTO) throws Exception {
        splitPackgeService.saveSplittedSysPackage(splitPackageDTO);
    }

//    @GetMapping("/cancelSplittedSysOrder")
//    @ApiOperation("撤销拆分系统订单")
//    @ApiImplicitParams({@ApiImplicitParam(name = "sysOrderId", value = "已拆分的系统订单号", paramType = "query", required = true)})
//    public void cancelSplittedSysOrder(@RequestParam("sysOrderId") String sysOrderId) {
//        systemOrderService.cancelSplittedSysOrder(sysOrderId);
//    }

    @GetMapping("/cancelSplittedSysPackage")
    @ApiOperation("撤销拆分包裹")
    @AspectContrLog(descrption = "撤销拆分包裹", actionType = SysLogActionType.UDPATE)
    public void cancelSplittedSysPackage(@RequestParam("sysOrderId") String sysOrderId) {
        splitPackgeService.cancelSplittedSysPackage(sysOrderId);
    }

//    @PostMapping("/saveMergedSysOrder")
//    @ApiOperation("合并系统订单")
//    @ApiImplicitParams({@ApiImplicitParam(name = "sysOrderIds", value = "合并系统订单的子订单", paramType = "body", required = true)})
//    public String saveMergedSysOrder(@RequestBody List<String> sysOrderIds) throws Exception {
//        return systemOrderService.saveMergedSysOrder(sysOrderIds);
//    }

    @PostMapping("/saveMergedSysPackage")
    @ApiOperation("合并包裹")
    @AspectContrLog(descrption = "合并包裹", actionType = SysLogActionType.UDPATE)
    public void saveMergedSysPackage(@RequestBody List<String> sysOrderIds) throws Exception {
        splitPackgeService.saveMergedSysPackage(sysOrderIds);
    }

//    @GetMapping("/cancelMergedSysOrder")
//    @ApiOperation("取消合并系统订单")
//    @ApiImplicitParams({@ApiImplicitParam(name = "sysOrderId", value = "取消合并系统订单ID", paramType = "query", required = true)})
//    public void cancelMergedSysOrder(String sysOrderId) {
//        systemOrderService.cancelMergedSysOrder(sysOrderId);
//    }

    @PostMapping("/cancelMergedSysPackage")
    @ApiOperation("取消合并包裹")
    @AspectContrLog(descrption = "取消合并包裹", actionType = SysLogActionType.UDPATE)
    public void cancelMergedSysPackage(@RequestBody List<String> sysOrderIds) {
        splitPackgeService.cancelMergedSysPackage(sysOrderIds);
    }

    @PostMapping("/canBeDeliveredPlatformOrder")
    @ApiOperation("校验平台订单能否继续发货 | 已发货的平台订单做个拦截（返回值0:可以发货,1:平台已经标记发货,2:仓库已禁用,3:物流已禁用,4:未缴纳押金）")
    @ApiImplicitParams({@ApiImplicitParam(name = "sysOrderId", value = "请求发货的系统订单ID", paramType = "query", required = true)})
    public Integer canBeDeliveredPlatformOrder(String sysOrderId) throws Exception {
        return systemOrderService.canBeDeliveredPlatformOrder(sysOrderId);
    }

    @PostMapping("/deliverGoodSingle")
    @ApiOperation("单个订单发货")
    @ApiImplicitParams({@ApiImplicitParam(name = "sysOrderId", value = "请求发货的系统订单ID", paramType = "query", required = true)})
    public void deliverGoodSingle(String sysOrderId) throws Exception {
        systemOrderService.deliverGoodSingleNew(sysOrderId,false);
    }

    @PostMapping("/deliverGoodBatch")
    @ApiOperation("批量订单发货")
    @ApiImplicitParams({@ApiImplicitParam(name = "sysOrderIds", value = "请求发货的系统订单ID集合", paramType = "body", required = true)})
    public List<SysOrder> deliverGoodBatch(@RequestBody List<String> sysOrderIds) throws Exception {
        if (0 == 0)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "批量发货本版本不可用哦。。。");
        if (CollectionUtils.isEmpty(sysOrderIds) || sysOrderIds.size() == 0)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求发货订单ID不能为空。。。");
        if (sysOrderIds.size() == 1)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请调用单个发货接口。。。");
        return systemOrderService.deliverGoodBatch(sysOrderIds);
    }

    @PostMapping("/replenishDeliverGood")
    @ApiOperation("订单补发货:目前售后补发货调用")
    @ApiImplicitParams({@ApiImplicitParam(name = "sysOrder", value = "请求补发货的系统订单对象", paramType = "body", required = true)})
    public String replenishDeliverGood(@RequestBody SysOrderNew sysOrder) throws Exception {
        return replenishService.replenishDeliverGood(sysOrder);
    }

    @PostMapping("/updatePlSysOrder")
    @ApiOperation("ERP仓库发货回调")
    @ApiImplicitParams({@ApiImplicitParam(name = "deliverCallBack", value = "ERP仓库发货回调", paramType = "body", required = true)})
    public void orderWareHouseDeliverCallBack(@RequestBody WareHouseDeliverCallBack deliverCallBack) throws Exception {
        logger.error("ERP回调的数据为: {}", FastJsonUtils.toJsonString(deliverCallBack));
        ParamCheckUtil.isNull(deliverCallBack);
        deliverCallBack.setWarehouseType("ERP");
        systemOrderService.wareHouseDeliverCallBackNew(deliverCallBack);
    }

    @PostMapping("/updateWmsSysOrder")
    @ApiOperation("WMS仓库发货回调")
    @ApiImplicitParams({@ApiImplicitParam(name = "wmsCallBack", value = "WMS仓库发货回调", paramType = "body", required = true)})
    public String orderWareHouseWmsCallBack(@RequestBody WareHouseWmsCallBack wmsCallBack) throws Exception {
        logger.info("WMS回调的数据为: {}", FastJsonUtils.toJsonString(wmsCallBack));
        ParamCheckUtil.isNull(wmsCallBack);

        WareHouseDeliverCallBack deliverCallBack = new WareHouseDeliverCallBack();
        deliverCallBack.setActualShipCost(wmsCallBack.getActualShipCost());
        deliverCallBack.setOrderTrackId(wmsCallBack.getPackageNum());
        deliverCallBack.setShipTrackNumber(wmsCallBack.getTrackingNum());
        deliverCallBack.setShipOrderId(wmsCallBack.getWaybillNum());

        //deliverCallBack.setSpeed(WmsEnum.getSpeedCode(wmsCallBack.getDeliveryStatus()));
        deliverCallBack.setSpeed("shipping_time");
        deliverCallBack.setUpdateTime(wmsCallBack.getDeliveryTime());
        deliverCallBack.setWarehouseType("WMS");
        systemOrderService.wareHouseDeliverCallBackNew(deliverCallBack);
        logger.info("WMS回调完成: {}", deliverCallBack.getOrderTrackId());
        return "success";
    }

    @PostMapping("/updatePayStatus")
    @ApiOperation("更新支付状态 | 内部调用接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderNo", value = "系统订单号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "status", value = "订单支付状态", required = true, dataType = "String")})
    public void updatePayStatus(String orderNo, String status) {
        systemOrderService.updatePayStatus(orderNo, status);
    }

    @GetMapping("/querySysOrderERPSpeedInfo/{sysOrderId}/{orderTrackId}")
    @ApiOperation("根据系统订单号查询ERP发货进度信息")
    public Map<Object, Object> querySysOrderShippingInfo(
            @ApiParam(name = "sysOrderId", value = "系统订单号")
            @PathVariable String sysOrderId,
            @ApiParam(name = "orderTrackId", value = "ERP订单跟踪号")
            @PathVariable String orderTrackId) throws Exception {
        return systemOrderService.querySysOrderERPSpeedInfo(new HashMap<String, String>() {{
            this.put("sysOrderId", sysOrderId);
            this.put("orderTrackId", orderTrackId);
        }});
    }

    @GetMapping("/getSysOrderERPSpeedInfo")
    @ApiOperation("根据系统订单号查询ERP发货信息")
    public void getSysOrderERPSpeedInfo() throws Exception {
        systemOrderService.getSysOrderERPSpeedInfo();
    }

    @GetMapping("/getSysOrderWMSSpeedInfo")
    @ApiOperation("根据系统订单号查询WMS发货信息")
    public void getSysOrderWMSSpeedInfo() throws Exception {
        wmsService.getSysOrderWMSSpeedInfo();
    }

    @ApiOperation("发票PDF导出")
    @PostMapping("/invoice/export")
    public ResponseEntity<byte[]> billExport(@RequestBody SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {

        String name = "invoice_" + DateUtils.formatDate(new Date(), DateUtils.FORMAT_1);

        // 读取，获取流
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/octet-stream");
        if ("firefox".equals(getExplorerType(request))) {
            String excelName = new String((name + ".pdf").getBytes("GB2312"), "ISO-8859-1");
            headers.add("Content-Disposition", "attachment; filename=" + excelName);
        } else {
            String excelName = URLEncoder.encode(name + ".pdf", "UTF-8");
            headers.add("Content-Disposition", "attachment;filename=" + excelName);
        }
        String pdfPath = sysOrderInvoiceService.exportPDF(name, sysOrderInvoiceInsertOrUpdateDTO, sysOrderInvoiceInsertOrUpdateDTO.getSysOrderId());
        return new ResponseEntity<byte[]>(inputToByte(new FileInputStream(new File(pdfPath))), headers, HttpStatus.OK);
    }

    @ApiOperation("发票信息保存")
    @PostMapping("/invoice/save")
    public void saveInvoice(@RequestBody SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO){
        sysOrderInvoiceService.saveInvoiceInfo(sysOrderInvoiceInsertOrUpdateDTO);
    }

    private static byte[] inputToByte(InputStream inStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[2048];
        int bytesRead = 0;
        while ((bytesRead = inStream.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    /* *功能描述
     * @date 2019/07/22
     * @param [page, row]
     * @return Page
     * @author lz
     */
    @ApiOperation("系统订单列表查询|author:lz")
    @PostMapping("/sysOrderList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页数",paramType = "query",required = true),
            @ApiImplicitParam(name="row",value = "行数",paramType = "query",required = true)
    })
    public Page sysOrderList(String page, String row, @RequestBody SysOrderVo sysOrderVo){
        Page.builder(page,row);
        return sysOrderListService.page(sysOrderVo);
    }

    @PostMapping("/getSuitLogistics")
    @ApiOperation("获取合适的物流方式列表|author:chenjiangxin")
    public List<LogisticsDetailVo> getSuitLogisticsByType(@RequestBody SearchLogisticsListDTO searchLogisticsListDTO) {
        return systemOrderService.getSuitLogisticsByType(searchLogisticsListDTO);
    }
    @Autowired
    private IImportExcelOrderInterface excelOrderService;
    /**
     * 用于批量上传订单
     * @param file
     * @return
     */
    @ApiOperation(value = "批量导入excel订单")
    @PostMapping("/excel-import")
    public String importOrderFormExcel(@RequestParam(value = "file") MultipartFile file) {
        UserDTO userDTO = getLoginUserInformationByToken.getUserDTO();
        //1.判断文件
        if (file == null || file.isEmpty()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "上传文件不可为空");
        }
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            excelOrderService.resolverExcelAndSaveDate(workbook, userDTO);
        } catch (IOException | InterruptedException e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"excel文件解析异常");
        }
        return "success";
    }
}