package com.rondaful.cloud.supplier.controller;

import com.rondaful.cloud.common.annotation.OpenAPI;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.dto.FreightTrialDTO;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.entity.LogisticsInfo;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.model.dto.basics.InitWarehouseDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.InventoryDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.InventoryQueryDTO;
import com.rondaful.cloud.supplier.model.enums.ResponseErrorCode;
import com.rondaful.cloud.supplier.model.request.third.InventoryReq;
import com.rondaful.cloud.supplier.model.request.third.ThirdBaseReq;
import com.rondaful.cloud.supplier.model.request.third.ThirdFreightReq;
import com.rondaful.cloud.supplier.model.response.provide.third.WarehouseReq;
import com.rondaful.cloud.supplier.model.response.third.WarehouseInventoryReq;
import com.rondaful.cloud.supplier.service.IFreightService;
import com.rondaful.cloud.supplier.service.IInventoryService;
import com.rondaful.cloud.supplier.service.ILogisticsInfoService;
import com.rondaful.cloud.supplier.service.IWarehouseBasicsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/7/29
 * @Description:
 */
@Api(description = "供应商第三放应用开放接口")
@RestController
@RequestMapping("/third/app/")
public class ThirdAppController {

    private final Logger logger = LoggerFactory.getLogger(ThirdAppController.class);

    @Autowired
    private IWarehouseBasicsService basicsService;

    @Autowired
    private ILogisticsInfoService logisticsInfoService;

    @Autowired
    private IInventoryService inventoryService;

    @Autowired
    private IFreightService freightService;


    @ApiOperation(value = "提供星商可用仓库列表", notes = "")
    @OpenAPI(isRequire = false)
    @PostMapping(value = "/getValidWarehouseList")
    public List<WarehouseReq> getValidWarehouseList(ThirdBaseReq req){
        List<InitWarehouseDTO> list= null;
        try {
            list = this.basicsService.getAuth(null);
        } catch (Exception e) {
            throw new GlobalException(ResponseErrorCode.GY_RETURN_CODE_200501);
        }
        List<WarehouseReq> result=new ArrayList<>(list.size());
        list.forEach(dto -> {
            dto.getItem().forEach(item->{
                WarehouseReq warehouseReq=new WarehouseReq(dto.getName()+"-"+item.getName(),item.getId().toString(),item.getCountryCode());
                warehouseReq.setWarehouseNameEn(dto.getName()+"-"+ Utils.translation(item.getName()));
                result.add(warehouseReq);
            });
        });
        return result;
    }


    @OpenAPI
    @ApiOperation(value = "获取仓库可用的物流方式列表",notes ="")
    @RequestRequire(parameter=String.class,require="warehouseId,pageNum,pageSize")
    @GetMapping("/getLogisticsList")
    public Page getLogisticsList(String warehouseId, String pageNum, String pageSize) {
        Page page = null;
        try {
            page.builder(pageNum, pageSize);
            page = logisticsInfoService.queryLogisticsListById(new LogisticsInfo(null,warehouseId,"1"));
        }catch(Exception e) {
            throw new GlobalException(ResponseErrorCode.GY_RETURN_CODE_200510);
        }
        return page;
    }

    @ApiOperation(value = "提供星商查询仓库库存")
    @OpenAPI
    @PostMapping("getProductInventory")
    public PageDTO<WarehouseInventoryReq> getProductInventory(InventoryReq req) {
        PageDTO<WarehouseInventoryReq> result=new PageDTO<>();
        InventoryQueryDTO queryDTO=new InventoryQueryDTO();
        BeanUtils.copyProperties(req,queryDTO);
        queryDTO.setCurrentPage(req.getPage());
        if (StringUtils.isNotEmpty(req.getWarehouseCode())){
            queryDTO.setWarehouseId(Integer.valueOf(req.getWarehouseCode()));
        }
        if (CollectionUtils.isNotEmpty(req.getPinlianSkus())&&req.getPinlianSkus().size()>250){
            throw new GlobalException(ResponseErrorCode.GY_RETURN_CODE_200516, "sku批量查询最大支持250个");
        }
        queryDTO.setPinlianSku(req.getPinlianSku());
        PageDTO<InventoryDTO> page= null;
        try {
            page = this.inventoryService.getsPage(queryDTO);
        } catch (Exception e) {
            throw new GlobalException(ResponseErrorCode.GY_RETURN_CODE_200501);
        }
        result.setCurrentPage(req.getPage());
        result.setTotalCount(page.getTotalCount());
        List<WarehouseInventoryReq> data=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(page.getList())){
            for (InventoryDTO dto:page.getList()) {
                WarehouseInventoryReq inventoryReq=new WarehouseInventoryReq();
                BeanUtils.copyProperties(dto,inventoryReq);
                inventoryReq.setWarehouseCode(dto.getWarehouseId().toString());
                data.add(inventoryReq);
            }
        }
        result.setList(data);
        return result;
    }


    @OpenAPI
    @ApiOperation(value = "获取运费")
    @PostMapping("/getFreightTrial")
    public List<FreightTrialDTO> getFreightTrial( ThirdFreightReq thirdFreightReq) {
        logger.info("获取运费接口开始：thirdFreightReq={}", thirdFreightReq);
        List<FreightTrialDTO> freightTrialDTOS = new ArrayList<>();
        try {
            checkParam(thirdFreightReq);
            freightTrialDTOS = freightService.getFreight(thirdFreightReq);
        }catch(GlobalException e) {
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        }catch(Exception e) {
            throw new GlobalException(ResponseErrorCode.GY_RETURN_CODE_200512);
        }
        return freightTrialDTOS;
    }


    private void checkParam(ThirdFreightReq param) {
        if(null == param.getWarehouseId()) {
            throw new GlobalException(ResponseErrorCode.GY_RETURN_CODE_200403, "仓库id不能为空");
        }
        if(StringUtils.isEmpty(param.getCountryCode())) {
            throw new GlobalException(ResponseErrorCode.GY_RETURN_CODE_200403, "国家code不能为空");
        }
        if(StringUtils.isEmpty(param.getPostCode())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "邮编不能为空");
        }
        if(CollectionUtils.isEmpty(param.getSkuList())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "sku不能为空");
        }

    }

}
