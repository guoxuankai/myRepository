package com.rondaful.cloud.supplier.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.utils.StringUtils;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.granary.GranaryUtils;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.supplier.dto.LogisticsDTO;
import com.rondaful.cloud.supplier.entity.*;
import com.rondaful.cloud.supplier.remote.RemoteErpService;
import com.rondaful.cloud.supplier.service.IFreightService;
import com.rondaful.cloud.supplier.service.ILogisticsInfoService;
import com.rondaful.cloud.supplier.service.IThirdLogisticsService;
import com.rondaful.cloud.supplier.service.IWarehouseBasicsService;
import com.rondaful.cloud.supplier.utils.ExcelUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(description = "物流提供服务接口")
@RestController
@RequestMapping("/logisticsProvider")
public class LogisticsProviderController {

    private final Logger logger = LoggerFactory.getLogger(LogisticsProviderController.class);

    @Value("${aliexpress.logistics_url}")
    private String aliexpress_logistics_url;

    @Value("${wsdl.url}")
    private String wsdl_url;

    @Autowired
    private IWarehouseBasicsService warehouseBasicsService;

    @Autowired
    private RemoteErpService remoteErpService;

    @Autowired
    private ILogisticsInfoService logisticsInfoService;

    @Autowired
    private IThirdLogisticsService thirdLogisticsService;

    @Autowired
    private IFreightService IFreightService;

    @Autowired
    GranaryUtils granaryUtils;

    @Value("${oversea.warehouse}")
    private String overseaWarehouse;

    @Value("${granary.app_token}")
    private String default_app_token;

    @ApiOperation("直接查询erp的物流渠道信息")
    @GetMapping("/getErpLogistics")
    public Object getErpLogistics() {
        try {
            JSONObject carrier = remoteErpService.getCarrier();
            return carrier.getJSONArray("data");
        } catch (Exception e) {
            logger.error("直接查询erp的物流渠道信息异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @ApiOperation("直接查询谷仓的物流渠道信息")
    @RequestRequire(parameter = String.class, require = "warehouseCode")
    @GetMapping("/getGranaryLogistics")
    public Object getGranaryLogistics(String warehouseCode,String appKey,String appToken) {
        Object obj = null;
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("warehouseCode", warehouseCode);
            String paramJson = JSONObject.toJSONString(map);
            String result = granaryUtils.getInstance(appToken, appKey, wsdl_url, paramJson, "getShippingMethod").getCallService();
            JSONObject json = JSONObject.parseObject(result);
            if ("Failure".equals(json.getString("ask"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, json.getString("message"));
            } else {
                if (StringUtils.isNotEmpty(json.getString("data"))) {
                    obj = json.getString("data");
                }
            }
        } catch (Exception e) {
            logger.error("直接查询谷仓的物流渠道信息异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "调取谷仓接口口异常");
        }
        return obj;
    }

    @GetMapping("/insertErpLogistics")
    public void insertErpLogistics() {
        try {
            thirdLogisticsService.insertErpLogistics();
        } catch (Exception e) {
            logger.error("导入erp物流方式异常", e);
        }
    }

    @GetMapping("/insertAliexpressLogistics")
    public void insertAliexpressLogistics() {
        try {
            String result = HttpUtil.get(aliexpress_logistics_url);
            String data = JSONObject.parseObject(result).getString("data");
            List<AliexpressLogistics> aliexpressLogisticsList = JSONObject.parseArray(data, AliexpressLogistics.class);
            thirdLogisticsService.insertAliexpressLogistics(aliexpressLogisticsList);
        } catch (Exception e) {
            logger.error("插入aliexpress物流方式异常", e);
        }
    }

    @GetMapping("/importLogisticsMapper")
    public void importLogisticsMapper() {
        try {
            Workbook wb = ExcelUtils.importExcel("importLogisticsMapping.xlsx");
            Sheet sheet = wb.getSheetAt(0);
            List<LogisticsInfo> logisticsInfoList = new ArrayList<>();
            for (int i = 1; i < sheet.getLastRowNum(); i++) {
                LogisticsInfo logisticsInfo = new LogisticsInfo();
                if (StringUtils.isNotEmpty(sheet.getRow(i).getCell(0).toString())) {
                    logisticsInfo.setCode(sheet.getRow(i).getCell(0).toString());
                    logisticsInfo.setAmazonCarrier(sheet.getRow(i).getCell(1).toString());
                    logisticsInfo.setAmazonCode(sheet.getRow(i).getCell(2).toString());
                    logisticsInfo.setEbayCarrier(sheet.getRow(i).getCell(3).toString());
                    logisticsInfo.setAliexpressCode(sheet.getRow(i).getCell(4).toString());
                    logisticsInfoList.add(logisticsInfo);
                    if( i % 20 == 0){
                        thirdLogisticsService.updateLogisticsMappingList(logisticsInfoList);
                        logisticsInfoList.clear();
                    }
                }
            }

        } catch (Exception e) {
            logger.error("导入aliexpress物流方式异常", e);
        }
    }

    @ApiOperation(value = "查询物流方式名称（订单物流规则）", notes = "status物流方式状态，warehouseId 仓库id，shortName物流方式名称，type物流方式类型", response = LogisticsDTO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "物流方式状态", name = "status", dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", value = "物流方式名称", name = "shortName", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "物流方式类型 1品连仓库物流 2供应商仓库物流", name = "type", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "仓库id", name = "warehouseId", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "物流方式编码", name = "code", dataType = "String")})
    @GetMapping("/queryLogisticsList")
    public List<LogisticsDTO> queryLogisticsList(String status, String warehouseId, String shortName,
                                                 String type,String code) {
        LogisticsInfo param = new LogisticsInfo();
        List<LogisticsDTO> list = null;
        try {
            if (StringUtils.isNotEmpty(status)) {
                param.setStatus(status);
            }

            if (StringUtils.isNotEmpty(shortName)) {
                param.setShortName(shortName);
            }

            if (StringUtils.isNotEmpty(type)) {
                param.setType(type);
            }

            if (StringUtils.isNotEmpty(code)) {
                param.setCode(code);
            }

            if (StringUtils.isNotEmpty(warehouseId)) {
                param.setWarehouseId(warehouseId);
            }
            logger.info("查询物流方式接口开始：param={}", param);
            list = logisticsInfoService.queryLogisticsList();
        } catch (Exception e) {
            logger.error("查询物流方式接口异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        logger.info("查询物流方式结果：list={}", list);
        return list;
    }

    @ApiOperation(value = "仓库匹配物流方式", notes = "")
    @PostMapping("/matchLogistics")
    public List<LogisticsDTO> matchLogistics(@RequestBody FreightTrial freightTrial) throws Exception {
        logger.info("仓库匹配物流方式接口开始：freightTrial={}", freightTrial);
        List<LogisticsDTO> result = null;
        try {
            result = thirdLogisticsService.matchLogistics(freightTrial);
        } catch (GlobalException e) {
            logger.error("仓库匹配物流方式接口失败", e);
            if("GY".equals(e.getErrorCode().split("_")[0])){
                throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100410,e.getMessage());
            }else{
                throw new GlobalException(e.getErrorCode(),e.getMessage());
            }
        } catch (Exception e) {
            logger.error("仓库匹配物流方式接口异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        return result;
    }



    @ApiOperation(value = "通过物流方式code查询物流信息", notes = "logisticsCode物流方式代码")
    @GetMapping("/queryLogisticsByCode")
    public LogisticsDTO queryLogisticsByCode(String logisticsCode, Integer warehouseId) {
        logger.info("通过物流方式code查询物流信息接口开始：logisticsCode={},warehouseId={}", logisticsCode, warehouseId);
        LogisticsDTO result;
        try {
//            WarehouseDTO warehouseDTO = warehouseBasicsService.getByWarehouseId(warehouseId);
//            if(warehouseDTO == null || StringUtils.isEmpty(warehouseDTO.getWarehouseCode())){
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查不到该仓库信息");
//            }
//            List<String> overseaWarehouseList = Arrays.asList(overseaWarehouse.split(","));
//            if(overseaWarehouseList.contains(warehouseDTO.getWarehouseCode())){
//                warehouseDTO = warehouseBasicsService.getByAppTokenAndCode(default_app_token,warehouseDTO.getWarehouseCode().split("_")[1]);
//                if(warehouseDTO == null || warehouseDTO.getWarehouseCode() == null){
//                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查不到该仓库信息");
//                }
//                warehouseId = warehouseDTO.getWarehouseId();
//            }
            result = logisticsInfoService.queryLogisticsByCode(logisticsCode, warehouseId);
        } catch (Exception e) {
            logger.error("通过物流方式code查询物流信息接口异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        return result;
    }

    @ApiOperation(value = "根据物流条件查询仓库信息", notes = "logisticsCode 物流方式编码", response = WarehouseMsg.class)
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", value = "物流方式编码", name = "logisticsCode", required = true, dataType = "String")})
    @RequestRequire(require = "logisticsCode", parameter = String.class)
    @GetMapping("/queryWarehouse")
    public Set<WarehouseMsg> queryWarehouse(@RequestParam(value = "logisticsCode") String logisticsCode) {
        logger.info("查询仓库信息接口开始：logisticsCode={}", logisticsCode);
        Set<WarehouseMsg> set = null;
        try {
            set = logisticsInfoService.queryWarehouse(logisticsCode);
        } catch (Exception e) {
            logger.error("查询仓库信息接口异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        logger.info("查询物流方式结果：set={}", set);
        return set;
    }


    @ApiOperation(value = "更新ERP的物流方式")
    @GetMapping("/updateErpLogistics")
    public void updateErpLogistics(){
        try {
            thirdLogisticsService.updateErpLogistics();
        } catch (Exception e) {
            logger.error("更新ERP的物流方式", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @ApiOperation(value = "更新谷仓的物流方式")
    @GetMapping("/updateGranaryLogistics")
    public void updateGranaryLogistics(){
        try {

        } catch (Exception e) {
            logger.error("更新谷仓的物流方式", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }


    @GetMapping("/importCountry")
    public void importCountry() {
        try {
            Workbook wb = ExcelUtils.importExcel("post_code.xlsx");
            thirdLogisticsService.importCountry(wb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "获取国家信息")
    @GetMapping("/getCountry")
    public List<CountryMap> getCountry() {
        List<CountryMap> list = new ArrayList<CountryMap>();
        try {
            list = thirdLogisticsService.getCountry(new CountryMap());
        } catch (Exception e) {
            logger.error("获取国家信息异常", e);
        }
        return list;
    }


    @ApiOperation(value = "初始化wms物流方式")
    @GetMapping("/updateWmsLogistics")
    public void updateWmsLogistics() {
        try {
            thirdLogisticsService.updateWmsLogistics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
