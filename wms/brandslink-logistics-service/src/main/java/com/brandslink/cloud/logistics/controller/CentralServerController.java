package com.brandslink.cloud.logistics.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.annotation.OpenAPI;
import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.logistics.entity.LogisticsDeliverCallBack;
import com.brandslink.cloud.logistics.entity.centre.MethodVO;
import com.brandslink.cloud.logistics.entity.centre.*;
import com.brandslink.cloud.logistics.thirdLogistics.enums.TraderType;
import com.brandslink.cloud.logistics.model.LogisticsProviderModel;
import com.brandslink.cloud.logistics.service.ICentralServerService;
import com.brandslink.cloud.logistics.service.ILogisticsCollectorService;
import com.brandslink.cloud.logistics.service.ILogisticsProviderService;
import com.brandslink.cloud.logistics.service.LogisticsStrategyService;
import com.brandslink.cloud.logistics.service.impl.centre.LogisticsContext;
import com.brandslink.cloud.logistics.utils.PdfUtil;
import com.brandslink.cloud.logistics.utils.ValidateUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import javax.xml.bind.ValidationException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/centre")
public class CentralServerController {
    @Autowired
    private ICentralServerService centralServerService;
    @Autowired
    private ValidateUtils validateUtils;

    private final static Logger logger = LoggerFactory.getLogger(CentralServerController.class);

    @GetMapping("/selectLogisticsMethod")
    @ApiOperation(value = "根据仓库编码查询邮寄方式数据")
    @RequestRequire(require = "page, row", parameter = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", defaultValue = "10", paramType = "query"),
            @ApiImplicitParam(name = "warehouse", value = "仓库编码", dataType = "string", paramType = "query")})
    public Page<MethodVO> selectLogisticsMethod(String page, String row, String warehouse) {
        Page.builder(page, row);
        Page<MethodVO> p = centralServerService.selectLogisticsMethod(warehouse);
        return p;
    }

    @OpenAPI
    @PostMapping("/freight")
    @ApiOperation(value = "运费试算")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "logisticsFreight", value = "运费试算接收参数数据对象", required = true, dataType = "LogisticsFreight")})
    public List<LogisticsFreightCallBack> freight(LogisticsFreight logisticsFreight) throws ValidationException {
        validateUtils.validate(logisticsFreight);
        List<LogisticsFreightCallBack> list = centralServerService.freight(logisticsFreight);
        return list;
    }

    @PostMapping("/freightDebug")
    @ApiOperation(value = "运费试算")
    public List<LogisticsFreightCallBack> freightDebug(@RequestBody @Valid LogisticsFreight logisticsFreight) throws ValidationException {
        List<LogisticsFreightCallBack> list = centralServerService.freight(logisticsFreight);
        return list;
    }

    @PostMapping("/deliverSingle")
    @ApiOperation(value = "单个发货")
    public LogisticsDeliverCallBack deliverSingle(@RequestBody @Valid BaseOrder baseOrder) throws Exception {
        LogisticsDeliverCallBack callBack = centralServerService.deliverSingle(baseOrder);
        return callBack;
    }

    @Autowired
    private ILogisticsProviderService logisticsProviderService;
    @Autowired
    private ILogisticsCollectorService logisticsCollectorService;
    @Autowired
    private LogisticsStrategyService miaoxin;
    @Autowired
    private LogisticsStrategyService yuntu;

    @PostMapping("/createOrder")
    @ApiOperation(value = "创建物流运单")
    public PlaceOrderResult createOrder(@RequestBody @Valid BaseOrder baseOrder) throws Exception {
        logger.info("创建物流运单传入参数：{}", JSON.toJSONString(baseOrder));
        LogisticsProviderModel byLogisticsMethodCode = logisticsProviderService.getByLogisticsCode(baseOrder.getLogisticsCode());
        if (byLogisticsMethodCode == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "没有找到相应物流商");
        }

        LogisticsContext logisticsContext = null;
        if (byLogisticsMethodCode.getLogisticsCode().equals(TraderType.MIAOXIN.getCode())) {
            logisticsContext = new LogisticsContext(miaoxin);
        } else if (byLogisticsMethodCode.getLogisticsCode().equals(TraderType.YUNTU.getCode())) {
            logisticsContext = new LogisticsContext(yuntu);
        }
        if (logisticsContext == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "没有找到相应物流商");
        }
        PlaceOrderResult placeOrderResult = logisticsContext.createOrder(baseOrder);
        return placeOrderResult;
    }


    @PostMapping("/printLabel")
    @ApiOperation(value = "打印面单")
    public List<Map> printLabel(@RequestBody @Valid List<BaseLabel> baseLabels) {
        List<Map> resultList = new ArrayList<>();
        for (BaseLabel baseLabel : baseLabels) {
            String logisticsCode = baseLabel.getLogisticsCode();
            LogisticsProviderModel byLogisticsMethodCode = logisticsProviderService.getByLogisticsCode(logisticsCode);
            if (byLogisticsMethodCode == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "没有找到相应物流商");
            }
            LogisticsContext logisticsContext = null;
            if (logisticsCode.equals(TraderType.MIAOXIN.getCode())) {
                logisticsContext = new LogisticsContext(miaoxin);
            } else if (logisticsCode.equals(TraderType.YUNTU.getCode())) {
                logisticsContext = new LogisticsContext(yuntu);
            }
            if (logisticsContext == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "没有找到相应物流商");
            }
            Map<String, Object> map = new HashMap<>();
            try {
                PrintLabelResult printLabelResult = logisticsContext.printLabel(baseLabel);
                map = new HashMap<>();
                map.put("msg", "请求成功");
                map.put("success", true);
                map.put("printUrl", printLabelResult.getPrintUrl());
            } catch (Exception e) {
                map.put("msg", e.getMessage());
                map.put("success", false);
                map.put("printUrl", null);
            }
            map.put("waybillNumber", baseLabel.getWaybillNumber());
            map.put("orderNumber", baseLabel.getOrderNumber());

            resultList.add(map);
        }
        return resultList;
    }

    @PostMapping("/getOrderTrackingNumber")
    @ApiOperation(value = "获取跟踪号")
    public List<Map> getOrderTrackingNumber(@RequestBody @Valid List<BaseTrackingNumber> baseTrackingNumbers) {
        List<Map> resultList = new ArrayList<>();
        for (BaseTrackingNumber baseTrackingNumber : baseTrackingNumbers) {
            String logisticsCode = baseTrackingNumber.getLogisticsCode();
            LogisticsProviderModel byLogisticsMethodCode = logisticsProviderService.getByLogisticsCode(logisticsCode);
            if (byLogisticsMethodCode == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "没有找到相应物流商");
            }
            LogisticsContext logisticsContext = null;
            if (logisticsCode.equals(TraderType.MIAOXIN.getCode())) {
                logisticsContext = new LogisticsContext(miaoxin);
            } else if (logisticsCode.equals(TraderType.YUNTU.getCode())) {
                logisticsContext = new LogisticsContext(yuntu);
            }
            if (logisticsContext == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "没有找到相应物流商");
            }
            Map<String, Object> map = new HashMap<>();
            try {
                TrackingNumberResult trackingNumberResult = logisticsContext.getTrackingNumber(baseTrackingNumber);
                map = new HashMap<>();
                map.put("msg", "请求成功");
                map.put("success", true);
                map.put("trackingNumber", trackingNumberResult.getTrackingNumber());
            } catch (Exception e) {
                map.put("msg", e.getMessage());
                map.put("success", false);
                map.put("trackingNumber", null);
            }
            map.put("orderNumber", baseTrackingNumber.getOrderNumber());
            resultList.add(map);
        }
        return resultList;
    }

    @GetMapping("/getCollectorByCode")
    @ApiOperation(value = "获得揽收商信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logisticsCode", value = "物流商编码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "warehouseCode", value = "仓库编码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "methodCode", value = "邮寄方式编码", dataType = "string", paramType = "query")})
    public CollectorVo getCollectorByCode(@RequestParam("logisticsCode") String logisticsCode, @RequestParam("methodCode") String methodCode, @RequestParam("warehouseCode") String warehouseCode) {
        if (StringUtils.isBlank(logisticsCode)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "物流商编码不能为空");
        } else if (StringUtils.isBlank(methodCode)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "邮寄方式编码不能为空");
        } else if (StringUtils.isBlank(warehouseCode)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "仓库编码不能为空");
        }
        CollectorVo byCode = logisticsCollectorService.getByCode(logisticsCode, methodCode, warehouseCode);
        if (byCode == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "没有找到揽收商");
        }
        String jsonStr = byCode.getCollectType();
        byCode.setCollectType(null);
        List<JSONObject> arr = JSONObject.parseArray(jsonStr, JSONObject.class);
        for (JSONObject jsonObject : arr) {
            if (warehouseCode.equals(jsonObject.getString("code"))) {
                byCode.setCollectType(jsonObject.getString("type"));
                break;
            }
        }
        return byCode;
    }


    @GetMapping("/getPrintImg")
    @ApiOperation(value = "获取面单图片")
    public List getPrintImg(@RequestParam("printUrl") String printUrl) {
        InputStream in;
        try {
            RestTemplate rest = new RestTemplate();
            ResponseEntity<Resource> entity = rest.getForEntity(printUrl, Resource.class);
            in = entity.getBody().getInputStream();
        } catch (Exception e) {
            logger.error("传入的url参数未请求到pdf文件:", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "传入的url参数未请求到pdf文件");
        }
        List list = PdfUtil.pdfToPng(in);
        return list;
    }
}
