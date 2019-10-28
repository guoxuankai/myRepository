package com.rondaful.cloud.supplier.controller;


import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.supplier.dto.LogisticsDTO;
import com.rondaful.cloud.supplier.dto.LogisticsResponseDTO;
import com.rondaful.cloud.supplier.entity.LogisticsInfo;
import com.rondaful.cloud.supplier.entity.PlatformLogistics;
import com.rondaful.cloud.supplier.service.ILogisticsInfoService;
import com.rondaful.cloud.supplier.service.IWarehouseBasicsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "物流基本接口")
@RestController
@RequestMapping("/logistics")
public class LogisticsController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(LogisticsController.class);

    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    @Autowired
    private ILogisticsInfoService logisticsInfoService;

    @Autowired
    private IWarehouseBasicsService warehouseBasicsService;


    @ApiOperation(value = "供应商查询物流方式",
            notes = "currentPage当前页码，pageSize每页显示行数，status物流方式状态，warehouseName仓库名称，shortName物流方式名称"
                    + "carrierName物流商名称，type物流方式类型", response = LogisticsDTO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "当前页码", name = "currentPage", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "每页显示行数", name = "pageSize", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "物流方式名称", name = "shortName", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "物流商名称", name = "carrierName", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "物流方式类型 1品连仓库物流 2供应商仓库物流", name = "type", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "仓库id", name = "warehouseId", dataType = "String")})
    @GetMapping("/querySupplierLogisticsPage")
    public Page querySupplierLogisticsPage(@RequestParam(value = "currentPage", defaultValue = "1") String currentPage,
                                           @RequestParam(value = "pageSize", defaultValue = "10")String pageSize,HttpServletRequest request,
                                           String shortName,String carrierName,String type, String warehouseId) {
        Page.builder(currentPage, pageSize);
        LogisticsInfo param = new LogisticsInfo();
        Page page;
        try {
            param.setStatus("1");
            param.setRequest(request);
            if (StringUtils.isNotEmpty(shortName)) {
                param.setShortName(shortName);
            }

            if (StringUtils.isNotEmpty(carrierName)) {
                param.setCarrierName(carrierName);
            }

            if (StringUtils.isNotEmpty(warehouseId)) {
                param.setWarehouseId(warehouseId);
            }

            if (StringUtils.isNotEmpty(type)) {
                param.setType(type);
            }

            logger.info("供应商分页查询物流方式接口开始:param={}", param);
            page = logisticsInfoService.queryLogisticsListPage(param);
        } catch (Exception e) {
            logger.error("供应商分页查询物流方式接口异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        logger.info("供应商分页查询物流方式结果：page={}", page);
        return page;
    }

    @ApiOperation(value = "物流方式名称查询物流方式",notes = "shortName物流方式名称，type 物流方式类型", response = LogisticsResponseDTO.class)
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", value = "物流方式名称", name = "shortName", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "物流方式类型 1品连仓库物流 2供应商仓库物流", name = "type", dataType = "String")})
    @GetMapping("/queryLogisticsByName")
    public List<LogisticsResponseDTO> queryLogisticsByName(HttpServletRequest request, String shortName,String type) {
        LogisticsInfo param = new LogisticsInfo();
        UserDTO userDTO = super.userToken.getUserDTO();
        List<Integer> list = new ArrayList<>();
        if (UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())){
            if (!userDTO.getManage()){
                Map<Integer, List<Integer>> map=super.getBinds();
                List<Integer> list1=map.get(USER_ID_LIST);
                if (CollectionUtils.isEmpty(list1)){
                    return null;
                }
                list.remove(list1);
                list.addAll(list1);
            }
        }else if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())){
            if (userDTO.getManage()){
                list=this.warehouseBasicsService.getsIdBySupplierId(userDTO.getUserId());
            }else {
                Map<Integer, List<Integer>> map=super.getBinds();
                if (map.isEmpty()){
                    return null;
                }
                list=map.get(WAREHOUSE_ID_LIST);
            }
        }
        List<LogisticsResponseDTO> result = new ArrayList<>();

        try {
            param.setRequest(request);
            if (StringUtils.isNotEmpty(shortName)) {
                param.setShortName(shortName);
            }

            if (StringUtils.isNotEmpty(type)) {
                param.setType(type);
            }


            logger.info("物流方式名称查询物流方式接口开始:param={}", param);
            result = logisticsInfoService.queryLogisticsByName(param);
        } catch (Exception e) {
            logger.error("物流方式名称查询物流方式接口异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        logger.info("物流方式名称查询物流方式结果：result={}", result);
        return result;
    }


    @ApiOperation(value = "分页查询物流方式",
            notes = "currentPage当前页码，pageSize每页显示行数，status物流方式状态，warehouseId 仓库id，shortName物流方式名称"
                    + "carrierName物流商名称，type物流方式类型", response = LogisticsDTO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "当前页码", name = "currentPage", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "每页显示行数", name = "pageSize", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "物流方式状态  默认0 0停用 1启用", name = "status", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "物流方式名称", name = "shortName", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "物流商名称", name = "carrierName", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "物流方式类型 1品连仓库物流 2供应商仓库物流", name = "type", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "仓库id", name = "warehouseId", dataType = "String")})
    @GetMapping("/queryLogisticsPage")
    public Page queryLogisticsPage(@RequestParam(value = "currentPage", defaultValue = "1") String currentPage,
                                   @RequestParam(value = "pageSize", defaultValue = "10") String pageSize,
                                   String status, String warehouseId, String shortName,HttpServletRequest request,
                                   String carrierName,String type) {
        Page.builder(currentPage, pageSize);
        LogisticsInfo param = new LogisticsInfo();
        Page page;
        try {
            param.setRequest(request);
            if(StringUtils.isNotEmpty(status)) {
                param.setStatus(status);
            }

            if (StringUtils.isNotEmpty(shortName)) {
                param.setShortName(shortName);
            }

            if (StringUtils.isNotEmpty(carrierName)) {
                param.setCarrierName(carrierName);
            }

            if (StringUtils.isNotEmpty(type)) {
                param.setType(type);
            }

            if(StringUtils.isNotEmpty(warehouseId)){
                param.setWarehouseId(warehouseId);
            }

            logger.info("分页查询物流方式接口开始:param={}", param);
            page = logisticsInfoService.queryLogisticsListPage(param);
        } catch (Exception e) {
            logger.error("分页查询物流方式接口异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        logger.info("分页查询物流方式结果：page={}", page);
        return page;
    }


    @ApiOperation(value = "物流方式状态更新", notes = "warehouseId 仓库Id,code 物流方式code,status状态 0停用 1启用")
    @PostMapping("/updateStatusByCode")
    @RequestRequire(require = "warehouseId,code,status", parameter = LogisticsInfo.class)
    public void updateStatusByCode(@RequestBody LogisticsInfo param) {
        logger.info("物流方式状态更新接口开始,参数为：Logistics={}", param);
        try {
            UserCommon user = getLoginUserInformationByToken.getUserInfo().getUser();
            if (null == user) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406);
            }
            param.setLastUpdateBy(user.getUserid().longValue());
            logisticsInfoService.updateStatusByCode(param);
        } catch (Exception e) {
            logger.error("物流方状态更新异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @ApiOperation(value = "第三方支持的物流方式查询", response = PlatformLogistics.class)
    @GetMapping("/queryThirdLogistics")
    public Map<String, Object> queryThirdLogistics() {
        logger.info("第三方支持的物流方式查询接口开始");
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map = logisticsInfoService.queryThirdLogistics();
        } catch (Exception e) {
            logger.error("第三方物流方式查询异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        return map;
    }


    @ApiOperation(value = "绑定平台物流", notes = "warehouseId 仓库id,code 物流方式code ebayCarrier eBay物流商,amazonCarrier Amazon物流商,amazonCode Amazon物流方式")
    @PostMapping("/updateLogisticsMapping")
    public void updateLogisticsMapping(@RequestBody LogisticsInfo param) {
        logger.info("更新平台物流映射接口开始：param={}", param);
        try {
            UserCommon user = getLoginUserInformationByToken.getUserInfo().getUser();
            if (null == user) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406);
            }
            param.setLastUpdateBy(user.getUserid().longValue());
            logisticsInfoService.updateLogisticsMapping(param);
        } catch (Exception e) {
            logger.error("绑定平台物流异常", e);
            throw e;
        }
    }
}
