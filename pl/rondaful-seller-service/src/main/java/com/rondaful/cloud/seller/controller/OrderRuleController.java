package com.rondaful.cloud.seller.controller;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.entity.CountryCode;
import com.rondaful.cloud.seller.entity.LogisticsDTO;
import com.rondaful.cloud.seller.entity.OrderRule;
import com.rondaful.cloud.seller.entity.OrderRuleSort;
import com.rondaful.cloud.seller.entity.OrderRuleWithBLOBs;
import com.rondaful.cloud.seller.entity.WarehouseMsg;
import com.rondaful.cloud.seller.entity.WarehouseSync;
import com.rondaful.cloud.seller.remote.RemoteCountryService;
import com.rondaful.cloud.seller.remote.RemoteLogisticsService;
import com.rondaful.cloud.seller.remote.RemoteOrderRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(description = "订单规则相关接口")
@RestController
@RequestMapping("/orderRule")
public class OrderRuleController {

    private final Logger logger = LoggerFactory.getLogger(OrderRuleController.class);

    private static final String ORDER_ERROR = "订单服务异常";


    private final RemoteOrderRuleService remoteOrderRuleService;

    private final RemoteLogisticsService remoteLogisticsService;

    private final RemoteCountryService remoteCountryService;

    private final GetLoginUserInformationByToken getUserInfo;

    @Autowired
    public OrderRuleController(RemoteOrderRuleService remoteOrderRuleService,
                               RemoteLogisticsService remoteLogisticsService,
                               RemoteCountryService remoteCountryService,
                               GetLoginUserInformationByToken getUserInfo) {
        this.remoteOrderRuleService = remoteOrderRuleService;
        this.remoteLogisticsService = remoteLogisticsService;
        this.remoteCountryService = remoteCountryService;
        this.getUserInfo = getUserInfo;
    }

    @GetMapping("/getOrderRuleParamDetail")
    @ApiOperation("check OrderRule message")
    public OrderRuleWithBLOBs getOrderRuleParamDetail() {
        return new OrderRuleWithBLOBs();
    }

    @AspectContrLog(descrption = "seller添加订单规则", actionType = SysLogActionType.ADD)
    @PostMapping("/addRule/{type}")
    @ApiOperation("添加订单规则")
    public String addRule(
            @RequestBody OrderRuleWithBLOBs rule,
            @ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type", required = true)
            @PathVariable String type) {
        UserDTO userDTO = getUserInfo.getUserDTO();
        if (!userDTO.getPlatformType().equals(UserEnum.platformType.SELLER.getPlatformType())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401.getCode(), "只有卖家账户能使用该功能");
        }
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]");
        }
        Boolean manage = userDTO.getManage();
        if (manage) {
            rule.setSellerId(String.valueOf(userDTO.getUserId()));
            rule.setSellerAccount(userDTO.getLoginName());
        } else {
            rule.setSellerId(String.valueOf(userDTO.getTopUserId()));
            rule.setSellerAccount(userDTO.getTopUserLoginName());
        }
        if (StringUtils.isBlank(rule.getSellerId())
                || StringUtils.isBlank(rule.getSellerAccount())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
        }
        rule.setPlatformMark("S"); //卖家平台登录时，默认平台标志为：S
        String result = remoteOrderRuleService.addRule(rule, type);
        return Utils.returnRemoteResultDataString(result, ORDER_ERROR);
    }

    @AspectContrLog(descrption = "seller更新订单规则", actionType = SysLogActionType.UDPATE)
    @PutMapping("/updateRule/{type}")
    @ApiOperation("更新订单规则")
    public String updateRule(
            @RequestBody OrderRuleWithBLOBs rule,
            @ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type", required = true)
            @PathVariable String type) {
        UserDTO userDTO = getUserInfo.getUserDTO();
        if (!userDTO.getPlatformType().equals(UserEnum.platformType.SELLER.getPlatformType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401.getCode(), "只有卖家账户能使用该功能");
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]");
        if (rule.getId() == null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id为空");
        String result = remoteOrderRuleService.updateRule(rule, type);
        return Utils.returnRemoteResultDataString(result, ORDER_ERROR);
    }

    @AspectContrLog(descrption = "seller交换订单规则优先级", actionType = SysLogActionType.UDPATE)
    @PutMapping("/swopPriority/{type}")
    @ApiOperation("交换两个规则的优先级")
    public void swopPriority(
            @ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type", required = true) @PathVariable String type,
            @RequestBody OrderRuleSort swop) {
        UserDTO userDTO = getUserInfo.getUserDTO();
        if (!userDTO.getPlatformType().equals(UserEnum.platformType.SELLER.getPlatformType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401.getCode(), "只有卖家账户能使用该功能");
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        if (swop.getId1() == null || swop.getId2() == null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
        String result = remoteOrderRuleService.swopPriority(type, swop);
        Utils.returnRemoteResultDataString(result, ORDER_ERROR);
    }

    @AspectContrLog(descrption = "seller将规则置顶或者置尾", actionType = SysLogActionType.UDPATE)
    @PutMapping("/topOrTailPriority/{type}")
    @ApiOperation("将一个订单规则的优先级置顶或者置尾")
    public void topOrTailPriority(
            @ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type", required = true)
            @PathVariable String type,
            @RequestBody OrderRuleSort sort) {
        UserDTO userDTO = getUserInfo.getUserDTO();
        if (!userDTO.getPlatformType().equals(UserEnum.platformType.SELLER.getPlatformType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401.getCode(), "只有卖家账户能使用该功能");
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]");
        if (sort.getId() == null || StringUtils.isBlank(sort.getWay()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
        if (!sort.getWay().equalsIgnoreCase(OrderRuleEnum.prioritySortWay.TO_TOP.getWay()) &&
                !sort.getWay().equalsIgnoreCase(OrderRuleEnum.prioritySortWay.TO_TAIL.getWay()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        String result = remoteOrderRuleService.topOrTailPriority(type, sort);
        Utils.returnRemoteResultDataString(result, ORDER_ERROR);
    }

    @AspectContrLog(descrption = "seller删除订单规则", actionType = SysLogActionType.DELETE)
    @DeleteMapping("/delete/{type}/{id}")
    @ApiOperation("删除订单规则")
    public void delete(
            @ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type", required = true)
            @PathVariable String type,
            @ApiParam(value = "订单规则id", name = "id", required = true) @PathVariable Long id) {
        UserDTO userDTO = getUserInfo.getUserDTO();
        if (!userDTO.getPlatformType().equals(UserEnum.platformType.SELLER.getPlatformType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401.getCode(), "只有卖家账户能使用该功能");
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]");
        String result = remoteOrderRuleService.delete(type, id);
        Utils.returnRemoteResultDataString(result, ORDER_ERROR);
    }

    @AspectContrLog(descrption = "seller查询订单规则列表", actionType = SysLogActionType.QUERY)
    @GetMapping("/queryRuleList/{type}")
    @ApiOperation("查询规则列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startCreateTime", value = "开始创建时间[yyyy-YY-dd]", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endCreateTime", value = "结束创建时间[yyyy-YY-dd]", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "映射状态[1:启用  2:停用 ]", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "ruleName", value = "规则名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sellerAccount", value = "卖家品连账号", dataType = "string", paramType = "query"),
    })
    public List<OrderRuleWithBLOBs> queryRuleList(
            @ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type", required = true)
            @PathVariable String type,
            @ApiIgnore OrderRule rule) {
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]");
        UserDTO userDTO = getUserInfo.getUserDTO();

        rule.setSellerAccount(userDTO.getManage() ? userDTO.getLoginName() : userDTO.getTopUserLoginName());
        rule.setPlatformMark("S"); //卖家平台默认只能查询卖家平台下属于自己账户的订单规则
        String s = remoteOrderRuleService.queryRuleList(type, rule);
        String dataString = Utils.returnRemoteResultDataString(s, ORDER_ERROR);
        try {
            return JSONObject.parseArray(dataString, OrderRuleWithBLOBs.class);
        } catch (Exception e) {
            logger.error("查询规则列表异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询规则列表异常");
        }
    }

    @AspectContrLog(descrption = "seller查询订单规则详情", actionType = SysLogActionType.QUERY)
    @GetMapping("/queryRuleById/{type}/{id}")
    @ApiOperation(value = "根据id查询订单规则详情")
    public OrderRuleWithBLOBs queryRuleById(
            @ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type", required = true)
            @PathVariable String type,
            @ApiParam(value = "订单规则id", name = "id", required = true) @PathVariable Long id) {
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]");

        String result = remoteOrderRuleService.queryRuleById(type, id);
        String dataString = Utils.returnRemoteResultDataString(result, ORDER_ERROR);
        try {
            return JSONObject.parseObject(dataString, OrderRuleWithBLOBs.class);
        } catch (Exception e) {
            logger.error("根据id查询订单规则详异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据id查询订单规则详异常");
        }
    }

    @AspectContrLog(descrption = "sellerERP查询物流方式", actionType = SysLogActionType.QUERY)
    @GetMapping("/findErpLogisticsList")
    @ApiOperation("查询ERP服务的物流方式列表")
    @ApiImplicitParam(value = "仓库id，当传入时查询支持该仓库的物流方式", name = "warehouseId", dataType = "String", paramType = "query")
    public List<Object> findErpLogisticsList(String warehouseId) {
        String result;
        if (StringUtils.isNotBlank(warehouseId)) {
            result = remoteLogisticsService.getErpLogisticsIncludeWH(warehouseId);
        } else
            result = remoteLogisticsService.getErpLogistics();
        String dataString = Utils.returnRemoteResultDataString(result, "供应商服务异常");
        try {
            return JSONObject.parseArray(dataString);
        } catch (Exception e) {
            logger.error("查询ERP服务的物流方式列表异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询ERP服务的物流方式列表异常");
        }
    }

    @AspectContrLog(descrption = "sellerERP查询物流方式详情", actionType = SysLogActionType.QUERY)
    @GetMapping("/findErpLogisticsDetail/{code}")
    @ApiOperation("查询ERP服务的物流方式详情")
    public JSONObject findErpLogisticsDetail(
            @ApiParam(value = "物流信息code", name = "code", required = true)
            @PathVariable String code) {
        String result = remoteLogisticsService.getErpLogisticsDetail(code);
        String dataString = Utils.returnRemoteResultDataString(result, "供应商服务异常");
        try {
            return JSONObject.parseObject(dataString);
        } catch (Exception e) {
            logger.error("查询ERP服务的物流方式详情异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询ERP服务的物流方式详情异常");
        }
    }

    @AspectContrLog(descrption = "seller查询国家列表", actionType = SysLogActionType.QUERY)
    @GetMapping("/queryCountryList")
    @ApiOperation("查询国家列表")
    public List<CountryCode> queryList(CountryCode code) {
        String result = remoteCountryService.queryList(code.getId(), code.getIso(), code.getIso3(), code.getName(),
                code.getNameZh(), code.getNicename(), code.getNumcode(), code.getPhonecode());
        String dataString = Utils.returnRemoteResultDataString(result, ORDER_ERROR);
        try {
            return JSONObject.parseArray(dataString, CountryCode.class);
        } catch (Exception e) {
            logger.error("查询国家列表异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询国家列表异常");
        }
    }

    //todo 一个查询用户账户信息  同项目查询信息列表

    @AspectContrLog(descrption = "seller查询可用仓库列表", actionType = SysLogActionType.QUERY)
    @ApiOperation("取得可用仓库列表")
    @GetMapping("/getValidWarehouseList")
    public List<WarehouseSync> getValidWarehouseList() {
        String result = remoteLogisticsService.getValidWarehouseList();
        String dataString = Utils.returnRemoteResultDataString(result, "供应商服务异常");
        return JSONObject.parseArray(dataString, WarehouseSync.class);
    }

    @AspectContrLog(descrption = "seller查询物流方式列表", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "根据条件查询物流方式",
            notes = "status物流方式状态，warehouseName仓库名称，shortName物流方式名称"
                    + "supplier供应商名称，type物流方式类型", response = LogisticsDTO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "物流方式状态 0停用 1启用", name = "status", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "仓库名称", name = "warehouseName", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "物流方式名称", name = "shortName", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "供应商用户id", name = "supplier", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "物流方式类型 默认0 0自营仓库物流", name = "type", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "仓库编码", name = "warehouseCode", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "物流方式id", name = "id", dataType = "String")})
    @GetMapping("/queryLogisticsList")
    public List<LogisticsDTO> queryLogisticsList(
            @RequestParam(value = "status", defaultValue = "") String status,
            @RequestParam(value = "warehouseName", defaultValue = "") String warehouseName,
            @RequestParam(value = "shortName", defaultValue = "") String shortName,
            @RequestParam(value = "supplier", defaultValue = "") String supplier,
            @RequestParam(value = "type", defaultValue = "") String type,
            @RequestParam(value = "warehouseCode", defaultValue = "") String warehouseCode,
            @RequestParam(value = "id", defaultValue = "") String id
    ) {
        String result = remoteLogisticsService.queryLogisticsList(StringUtils.isBlank(status) ? null : status, StringUtils.isBlank(warehouseName) ? null : warehouseName,
                StringUtils.isBlank(shortName) ? null : shortName, StringUtils.isBlank(supplier) ? null : supplier, StringUtils.isBlank(type) ? null : type,
                StringUtils.isBlank(warehouseCode) ? null : warehouseCode, StringUtils.isBlank(id) ? null : id);
        String dataString = Utils.returnRemoteResultDataString(result, "供应商服务异常");
        try {
            List<LogisticsDTO> logisticsDTOS = JSONObject.parseArray(dataString, LogisticsDTO.class);
            logisticsDTOS.forEach(l -> l.setShortName(Utils.translation(l.getShortName())));
            return logisticsDTOS;
        } catch (Exception e) {
            logger.error("远程调用获取物流方式异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询物流方式异常");
        }
    }

    @AspectContrLog(descrption = "sellre查询物流信息详情", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "根据物流条件查询仓库信息", notes = "logisticsCode 物流方式编码", response = WarehouseMsg.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "物流方式编码", name = "logisticsCode", required = true, dataType = "String")})
    @GetMapping("/queryWarehouse")
    public List<WarehouseMsg> queryWarehouse(@RequestParam(value = "logisticsCode") String logisticsCode) {
        String result = remoteLogisticsService.queryWarehouse(logisticsCode);
        String dataString = Utils.returnRemoteResultDataString(result, "供应商服务异常");
        try {
            return JSONObject.parseArray(dataString, WarehouseMsg.class);
        } catch (Exception e) {
            logger.error("远程调用获取支持指定邮寄方式的仓库列表", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询仓库列表异常");
        }
    }


    //一个查询商品sku接口(sku映射中)


}
