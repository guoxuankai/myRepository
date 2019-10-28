package com.rondaful.cloud.supplier.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.supplier.config.async.AsyncExport;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.model.dto.KeyValueDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.AppCountDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.CombineSelectDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.InventoryDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.InventoryQueryDTO;
import com.rondaful.cloud.supplier.model.enums.StatusEnums;
import com.rondaful.cloud.supplier.model.request.basic.WarehouseSelectDTO;
import com.rondaful.cloud.supplier.model.request.inventory.PageQueryReq;
import com.rondaful.cloud.supplier.service.IInventoryService;
import com.rondaful.cloud.supplier.service.IWarehouseBasicsService;
import com.rondaful.cloud.supplier.service.impl.ExcelExportServerImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xqq
 * @Date: 2019/6/14
 * @Description:
 */
@Api(description = "库存明细相关接口")
@RestController
@RequestMapping("/warehouse/inventory/")
public class InventoryController extends BaseController{
    private final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private IInventoryService inventoryService;
    @Autowired
    private IWarehouseBasicsService basicsService;
    @Resource
    private AsyncExport asyncExport;

    @ApiOperation(value = "库存数据初始化")
    @GetMapping("init")
    public void init(){
        this.inventoryService.init();
    }

    @ApiOperation(value = "分页获取库存明细")
    @PostMapping("getsPage")
    public PageDTO<InventoryDTO> getsPage(PageQueryReq req){
        if (StringUtils.isEmpty(req.getPinlianSku())&&StringUtils.isEmpty(req.getSupplierSku())&&req.getWarehouseId()==null){
            return new PageDTO<>(0,1);
        }
        InventoryQueryDTO dto=new InventoryQueryDTO();
        BeanUtils.copyProperties(req,dto);
        UserDTO userDTO=super.userToken.getUserDTO();
        if (UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())){
            if (!userDTO.getManage()){
                List<UserAccountDTO> binds=userDTO.getBinds();
                if (CollectionUtils.isEmpty(binds)){
                    return new PageDTO<>(0,1);
                }
                for (UserAccountDTO dto1:binds) {
                    if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(dto1.getBindType())){
                        dto.setSupplierIds(JSONArray.parseArray(JSONObject.toJSONString(dto1.getBindCode()),Integer.class));
                    }
                }
            }
        }else if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())){
            List<Integer> userIds=new ArrayList<>(1);
            userIds.add(userDTO.getTopUserId());
            dto.setSupplierIds(userIds);
        }else if (UserEnum.platformType.SELLER.getPlatformType().equals(userDTO.getPlatformType())){
            dto.setSellerId(userDTO.getTopUserId());
        }
        dto.setLanguageType(super.request.getHeader("i18n"));
        return this.inventoryService.getsPage(dto);
    }

    @ApiOperation(value = "修改预警值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warehouseId", value = "仓库id", dataType = "Integer", paramType = "query",required = true),
            @ApiImplicitParam(name = "warnVal", value = "预警值:-1(不开启)", dataType = "Integer", paramType = "query",required = true),
            @ApiImplicitParam(name = "pinlianSkus", value = "品连sku(json.tostring)", dataType = "String", paramType = "query",required = true)
    })
    @PostMapping("updateWarnVal")
    public Integer updateWarnVal(Integer warehouseId,Integer warnVal,String pinlianSkus){
        return this.inventoryService.updateWarnVal(warehouseId, JSONArray.parseArray(pinlianSkus,String.class),warnVal,super.userToken.getUserDTO().getLoginName());
    }

    @ApiOperation(value = "根据sku列表获取仓库列表(必须在同一个仓库)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skus", value = "sku数组,json.tostring", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "skuMap", value = "数量", dataType = "Integer", paramType = "query")
    })
    @PostMapping("getCombineSku")
    public List<CombineSelectDTO> getCombineSku(String skus,String skuMap){
        List<String> skuList=new ArrayList<>();
        JSONObject jsonObject=null;
        if (StringUtils.isNotEmpty(skus)){
            skuList=JSONArray.parseArray(skus,String.class);
        }else if (StringUtils.isNotEmpty(skuMap)){
            jsonObject=JSONObject.parseObject(skuMap);
            skuList=new ArrayList<>(jsonObject.keySet());
        }
        return this.inventoryService.getCombineSku(skuList,super.request.getHeader("i18n"),jsonObject);
    }

    @ApiOperation(value = "根据sku列表获取仓库列表(有一个存在就返回,且库存大于0)")
    @ApiImplicitParam(name = "skus", value = "sku数组,json.tostring", dataType = "String", paramType = "query",required = true)
    @PostMapping("getOrSku")
    public List<KeyValueDTO> getOrSku(String skus){
        return this.inventoryService.getOrSku(JSONArray.parseArray(skus,String.class),super.request.getHeader("i18n"));
    }

    @ApiOperation(value = "导出库存明细")
    @ApiImplicitParam(name = "warehouseId", value = "仓库id", dataType = "Integer", paramType = "query",required = true)
    @GetMapping("export")
    public void export(Integer warehouseId){
        UserDTO userDTO=super.userToken.getUserDTO();
        Map<String,Object> dataParams=new HashMap<>();
        dataParams.put("type", ExcelExportServerImpl.EXPORT_INVENTORY);
        JSONObject jsParams=new JSONObject();
        jsParams.put("warehouseId",warehouseId);
        UserAll userAll=super.userToken.getUserInfo();
        if (userAll!=null&&UserEnum.platformType.SUPPLIER.getPlatformType().equals(userAll.getUser().getPlatformType())){
            jsParams.put("userId",userAll.getUser().getTopUserId()==0?userAll.getUser().getUserid():userAll.getUser().getTopUserId());
        }
        jsParams.put("i18n",super.request.getHeader("i18n"));
        dataParams.put("params",jsParams.toJSONString());
        this.asyncExport.inventoryExport(dataParams,userDTO.getUserId(),userDTO.getTopUserId(),userDTO.getPlatformType());
    }

    @ApiOperation(value = "app预警值不足页面")
    @GetMapping("getAppFirst")
    public PageDTO<InventoryDTO> getAppFirst(){
        UserDTO userDTO=super.userToken.getUserDTO();
        List<Integer> warehouseIds=new ArrayList<>();
        List<Integer> supplierIds=new ArrayList<>();
        Map<Integer, List<Integer>> map=super.getBinds();
        supplierIds.add(userDTO.getTopUserId());
        if (!userDTO.getManage()){
            warehouseIds.addAll(map.get(WAREHOUSE_ID_LIST));
            if (CollectionUtils.isEmpty(warehouseIds)){
                return null;
            }
        }
        List<WarehouseSelectDTO> list=this.basicsService.getSelect(warehouseIds,supplierIds,null, StatusEnums.ACTIVATE.getStatus());
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        for (WarehouseSelectDTO dto:list) {
            for (WarehouseSelectDTO child:dto.getChilds()) {
                PageQueryReq req=new PageQueryReq();
                req.setCurrentPage(1);
                req.setPageSize(5);
                req.setStatus(2);
                req.setWarehouseId(child.getId());
                PageDTO<InventoryDTO> pageDTO=this.getsPage(req);
                if (CollectionUtils.isNotEmpty(pageDTO.getList())){
                    return pageDTO;
                }
            }
        }
        return null;
    }

    @ApiOperation(value = "app库存汇总")
    @GetMapping("getAppCount")
    public AppCountDTO getAppCount(){
        UserDTO userDTO=super.userToken.getUserDTO();
        Map<Integer, List<Integer>> map=super.getBinds();
        return this.inventoryService.getCount(userDTO.getTopUserId(),map.get(WAREHOUSE_ID_LIST));
    }

}
