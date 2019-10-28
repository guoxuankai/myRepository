package com.rondaful.cloud.seller.controller;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.entity.SellerSkuMap;
import com.rondaful.cloud.seller.remote.RemoteCommodityService;
import com.rondaful.cloud.seller.remote.RemoteOrderRuleService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

//@RestController
//@RequestMapping("/skuMap")
//@Api(description = "订单映射相关接口")
public class OrderSkuMapController {

    private final Logger logger = LoggerFactory.getLogger(OrderSkuMapController.class);

    private static final String ORDER_ERROR = "订单服务异常";
    private static final String COMMODITY_ERROR = "商品服务异常";

    private final RemoteOrderRuleService remoteOrderRuleService;

    private final RemoteCommodityService remoteCommodityService;

    private final GetLoginUserInformationByToken getUserInfo;


    @Autowired
    public OrderSkuMapController(RemoteOrderRuleService remoteOrderRuleService,
                                 RemoteCommodityService remoteCommodityService, GetLoginUserInformationByToken getUserInfo) {
        this.remoteOrderRuleService = remoteOrderRuleService;
        this.remoteCommodityService = remoteCommodityService;
        this.getUserInfo = getUserInfo;
    }

    @AspectContrLog(descrption = "seller批量添加sku映射", actionType = SysLogActionType.ADD)
    @PostMapping("/addSkuMaps")
    @ApiOperation("批量添加卖家与品连sku映射")
    public List<String> addSkuMaps(@RequestBody List<SellerSkuMap> maps) {
        UserDTO userDTO = getUserInfo.getUserDTO();
        if (!userDTO.getPlatformType().equals(UserEnum.platformType.SELLER.getPlatformType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401.getCode(), "只有卖家账户能使用该功能");
        for (SellerSkuMap m : maps) {
            if (StringUtils.isBlank(m.getPlSku())
                    || StringUtils.isBlank(m.getPlatformSku())
                    || m.getStatus() == null
                    || StringUtils.isBlank(m.getPlatform())
                    || StringUtils.isBlank(m.getAuthorizationId()))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
            if (!m.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform())
                    && !m.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform())
                    && !m.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.WISH.getPlatform())
//                    && !m.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform())

            )
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台不正确");
        }
        String result = remoteOrderRuleService.addSkuMaps(maps);
        String dataString = Utils.returnRemoteResultDataString(result, ORDER_ERROR);
        try {
            return JSONObject.parseArray(dataString, String.class);
        } catch (Exception e) {
            logger.error("批量添加卖家与品连sku映射异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "批量添加卖家与品连sku映射异常");
        }
    }

    @AspectContrLog(descrption = "seller更新sku映射", actionType = SysLogActionType.UDPATE)
    @PutMapping("/updateSkuMap")
    @ApiOperation("更新sku映射")
    public void updateSkuMap(@RequestBody SellerSkuMap map) {   //1=eBay；2=Amazon
        UserDTO userDTO = getUserInfo.getUserDTO();
        if (!userDTO.getPlatformType().equals(UserEnum.platformType.SELLER.getPlatformType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401.getCode(), "只有卖家账户能使用该功能");
        if (map.getId() == null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");
        if (StringUtils.isNotBlank(map.getPlatform())) {
            if (map.getPlatform().equalsIgnoreCase("1")) {
                map.setPlatform(OrderRuleEnum.platformEnm.E_BAU.getPlatform());
            } else if (map.getPlatform().equalsIgnoreCase("2")) {
                map.setPlatform(OrderRuleEnum.platformEnm.AMAZON.getPlatform());
            }
        }
        if (StringUtils.isNotBlank(map.getPlatform())
                && (!map.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform())
                && !map.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform())
                && !map.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.WISH.getPlatform())
                && !map.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform())
//                && !map.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.OTHER.getPlatform())
        )
        )
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "被更改的平台不正确");
        String result = remoteOrderRuleService.updateSkuMap(map);
        Utils.returnRemoteResultDataString(result, ORDER_ERROR);
    }

    @AspectContrLog(descrption = "seller查询sku映射", actionType = SysLogActionType.QUERY)
    @GetMapping("/queryMaps")
    @ApiOperation(value = "查询sku映射列表", notes = "page当前页码，row每页显示行数", response = SellerSkuMap.class)
    @RequestRequire(require = "page,row", parameter = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "startCreateTime", value = "开始创建时间[yyyy-YY-dd HH:mm:ss]", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endCreateTime", value = "结束创建时间[yyyy-YY-dd HH:mm:ss]", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "映射状态[1:启用  2:停用 ]", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "plSku", value = "品连sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "platformSku", value = "平台sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sellerSelfAccount", value = "卖家自定义账号(授权时产生)", dataType = "string", paramType = "query")
    })
    public Object queryMaps(@ApiIgnore SellerSkuMap model, String page, String row) {
        UserDTO userDTO = this.getUserInfo.getUserDTO();
        Boolean manage = userDTO.getManage();
        String result = remoteOrderRuleService.queryMaps(page, row, model.getStartCreateTime(), model.getEndCreateTime(),
                model.getStatus(), model.getPlSku(), model.getPlatformSku(), manage ? userDTO.getLoginName() : userDTO.getTopUserLoginName(),
                model.getSellerSelfAccount());
        String dataString = Utils.returnRemoteResultDataString(result, ORDER_ERROR);
        try {
            return JSONObject.parse(dataString);
        } catch (Exception e) {
            logger.error("查询sku映射异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询sku映射异常");
        }
    }

    @AspectContrLog(descrption = "seller删除sku映射", actionType = SysLogActionType.QUERY)
    @DeleteMapping("/deleteMap/{id}")
    @ApiOperation("删除sku映射")
    public String deleteMap(@ApiParam(value = "sku映射id", name = "id", required = true) @PathVariable Long id) {
        UserDTO userDTO = getUserInfo.getUserDTO();
        if (!userDTO.getPlatformType().equals(UserEnum.platformType.SELLER.getPlatformType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401.getCode(), "只有卖家账户能使用该功能");
        if (id == null || id == 0)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        String result = remoteOrderRuleService.deleteMap(id);
        return Utils.returnRemoteResultDataString(result, ORDER_ERROR);
    }

    @AspectContrLog(descrption = "seller查询商品分类列表", actionType = SysLogActionType.QUERY)
    @GetMapping("/category/list")
    @ApiOperation(value = "查询商品分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)
    })
    @RequestRequire(require = "page, row", parameter = String.class)
    public Object listCategory(String page, String row) {
        String result = remoteCommodityService.listCategory(page, row);
        String dataString = Utils.returnRemoteResultDataString(result, COMMODITY_ERROR);
        try {
            return JSONObject.parse(dataString);
        } catch (Exception e) {
            logger.error("查询分类列表异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询分类列表异常");
        }
    }

    @AspectContrLog(descrption = "sellerSPU维度查询商品列表", actionType = SysLogActionType.QUERY)
    @GetMapping("/commodity/list/manager")
    @ApiOperation(value = "商品列表查询(以spu为唯独分页)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "category_level_1", value = "一级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_level_2", value = "二级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_level_3", value = "三级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "创建开始时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "创建结束时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "autiState", value = "商品状态", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "commodityName", value = "商品名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "SKU", value = "商品sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "SPU", value = "系统spu", dataType = "string", paramType = "query")})
    @RequestRequire(require = "page, row", parameter = String.class)
    public Object managerCommodity(String page, String row, Long category_level_1, Long category_level_2,
                                   Long category_level_3, String startTime, String endTime, Integer autiState,
                                   String commodityName, String SKU, String SPU) {
        String result = remoteCommodityService.managerCommodity(page, row, category_level_1, category_level_2,
                category_level_3, startTime, endTime, autiState, commodityName, true, SKU, SPU, null, null);
        String dataString = Utils.returnRemoteResultDataString(result, COMMODITY_ERROR);
        return JSONObject.parse(dataString);
    }

    @AspectContrLog(descrption = "sellerSKU维度查询商品列表", actionType = SysLogActionType.QUERY)
    @GetMapping("/commodity/getSkuList")
    @ApiOperation(value = "获取sku商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "category_level_1", value = "一级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_level_2", value = "二级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_level_3", value = "三级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "commodityName", value = "商品名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "systemSku", value = "系统sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "supplierSku", value = "供应商sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vendibilityPlatform", value = "可售平台", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "SPU", value = "系统spu", dataType = "string", paramType = "query")
    })
    @RequestRequire(require = "page, row", parameter = String.class)
    public Object test(String page, String row, Long category_level_1, Long category_level_2, Long category_level_3,
                       String commodityName, String systemSku, String supplierSku, String vendibilityPlatform, String SPU) {
        String result = remoteCommodityService.getSkuList(page, row, category_level_1, category_level_2,
                category_level_3, commodityName, systemSku, supplierSku, SPU, vendibilityPlatform);
        String dataString = Utils.returnRemoteResultDataString(result, COMMODITY_ERROR);
        return JSONObject.parse(dataString);
    }


}
