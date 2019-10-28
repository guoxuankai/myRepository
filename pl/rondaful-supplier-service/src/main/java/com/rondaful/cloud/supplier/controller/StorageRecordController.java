package com.rondaful.cloud.supplier.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.model.dto.KeyValueDTO;
import com.rondaful.cloud.supplier.model.dto.storage.*;
import com.rondaful.cloud.supplier.model.enums.WarehouseFirmEnum;
import com.rondaful.cloud.supplier.model.request.storage.QueryPageReq;
import com.rondaful.cloud.supplier.model.request.storage.StorageRecordReq;
import com.rondaful.cloud.supplier.service.IStorageRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: xqq
 * @Date: 2019/6/18
 * @Description:
 */
@Api(description = "入库单相关操作")
@RestController
@RequestMapping("/storage/record/")
public class StorageRecordController extends BaseController{

    @Autowired
    private IStorageRecordService recordService;

    @ApiOperation(value = "新建入库单")
    @PostMapping("add")
    public String add(@RequestBody StorageRecordReq req){
        StorageRecordDTO dto=new StorageRecordDTO();
        BeanUtils.copyProperties(req,dto);
        if (StringUtils.isNotEmpty(req.getCollectings())){
            List<StorageCollectingDTO> collectingDTOS= JSONArray.parseArray(req.getCollectings(),StorageCollectingDTO.class);
            dto.setCollectings(collectingDTOS);
        }
        if (StringUtils.isNotEmpty(req.getItems())){
            List<StoregeItemDTO> items=JSONArray.parseArray(req.getItems(),StoregeItemDTO.class);
            dto.setItems(items);
        }
        if (StringUtils.isNotEmpty(req.getSpecific())){
            StorageSpecificDTO specificDTO= JSONObject.parseObject(req.getSpecific(),StorageSpecificDTO.class);
            dto.setSpecificDTO(specificDTO);
        }

        if (WarehouseFirmEnum.GOODCANG.getCode().hashCode()==req.getWarehouseTopId()){
            dto.setFirmCode(WarehouseFirmEnum.GOODCANG.getCode());
        }else if (WarehouseFirmEnum.WMS.getCode().hashCode()==req.getWarehouseTopId()){
            dto.setFirmCode(WarehouseFirmEnum.WMS.getCode());
        }else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"未知仓库服务商");
        }
        UserDTO userDTO=super.userToken.getUserDTO();
        dto.setCreateBy(userDTO.getLoginName());
        dto.setTopUserId(userDTO.getTopUserId());
        return this.recordService.add(dto);
    }

    @ApiOperation(value = "修改入库单")
    @PostMapping("update")
    public Integer update(@RequestBody StorageRecordReq req){
        StorageRecordDTO dto=new StorageRecordDTO();
        BeanUtils.copyProperties(req,dto);
        if (StringUtils.isNotEmpty(req.getCollectings())){
            List<StorageCollectingDTO> collectingDTOS= JSONArray.parseArray(req.getCollectings(),StorageCollectingDTO.class);
            dto.setCollectings(collectingDTOS);
        }
        if (StringUtils.isNotEmpty(req.getItems())){
            List<StoregeItemDTO> items=JSONArray.parseArray(req.getItems(),StoregeItemDTO.class);
            dto.setItems(items);
        }
        if (StringUtils.isNotEmpty(req.getSpecific())){
            StorageSpecificDTO specificDTO= JSONObject.parseObject(req.getSpecific(),StorageSpecificDTO.class);
            dto.setSpecificDTO(specificDTO);
        }
        if (WarehouseFirmEnum.GOODCANG.getCode().hashCode()==req.getWarehouseTopId()){
            dto.setFirmCode(WarehouseFirmEnum.GOODCANG.getCode());
        }else if (WarehouseFirmEnum.WMS.getCode().hashCode()==req.getWarehouseTopId()){
            dto.setFirmCode(WarehouseFirmEnum.WMS.getCode());
        }else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"未知仓库服务商");
        }
        dto.setUpdateBy(super.userToken.getUserDTO().getLoginName());
        return this.recordService.update(dto);
    }

    @ApiOperation(value = "分页获取库存明细")
    @PostMapping("getsPage")
    public PageDTO<StoragePageDTO> getsPage(QueryPageReq req){
        StorageQueryPageDTO dto=new StorageQueryPageDTO();
        BeanUtils.copyProperties(req,dto);
        UserDTO userDTO=super.userToken.getUserDTO();
        if (req.getWarehouseId()==null){
            Map<Integer, List<Integer>> map= super.getBinds();
            if (map!=null){
                map.forEach((k,v)->{
                    if (WAREHOUSE_ID_LIST.equals(k)){
                        dto.setWarehouseId(v);
                    }else if(USER_ID_LIST.equals(k)){
                        dto.setSupplierId(v);
                    }
                });
            }
            if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())&&!userDTO.getManage()){
                List<Integer> supplierIds=new ArrayList<>(1);
                supplierIds.add(userDTO.getTopUserId());
                dto.setSupplierId(supplierIds);
            }
        }else {
            List<Integer> warehouseIds=new ArrayList<>(1);
            warehouseIds.add(req.getWarehouseId());
            dto.setWarehouseId(warehouseIds);
            if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())){
                List<Integer> supplierIds=new ArrayList<>(1);
                supplierIds.add(userDTO.getTopUserId());
                dto.setSupplierId(supplierIds);
            }
        }
        if (req.getUserId()!=null){
            if (CollectionUtils.isEmpty(dto.getSupplierId())){
                dto.setSupplierId(new ArrayList<>());
            }
            dto.getSupplierId().add(req.getUserId());
        }
        dto.setLanguageType(super.request.getHeader("i18n"));
        return this.recordService.getsPage(dto);
    }

    @ApiOperation(value = "入库单id获取订单详细信息")
    @ApiImplicitParam(name = "id", value = "入库单id", required = true, dataType = "int", paramType = "query")
    @GetMapping("getById")
    public StorageRecordDTO getById(Long id){
        return this.recordService.getById(id);
    }

    @ApiOperation(value = "获取服务方式code")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "0：空运，1：海运散货 2：快递，3：铁运 ，4：海运整柜", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "warehouseId", value = "仓库id", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping("getsSmCode")
    public List getsSmCode(Integer type,Integer warehouseId){
        return this.recordService.getsSmCode(type,warehouseId);
    }

    @ApiOperation(value = "获取中转仓库")
    @ApiImplicitParam(name = "warehouseId", value = "仓库id", dataType = "Integer", paramType = "query", required = true)
    @GetMapping("getTransferWarehouse")
    public List getTransferWarehouse(Integer warehouseId){
        return this.recordService.getTransferWarehouse(warehouseId);
    }

    @ApiOperation(value = "获取增值税")
    @ApiImplicitParam(name = "warehouseId", value = "仓库id", dataType = "Integer", paramType = "query", required = true)
    @GetMapping("getsVat")
    @Deprecated
    public List<VatListDTO> getsVat(Integer warehouseId){
        return this.recordService.getsVat(warehouseId);
    }

    @ApiOperation(value = "获取进出口公司编码")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "warehouseId", value = "仓库id", dataType = "Integer", paramType = "query", required = true),
        @ApiImplicitParam(name = "type", value = "进出口商,1：进口商,2：出口商", dataType = "Integer", paramType = "query", required = true)
    })
    @GetMapping("getCompany")
    public List<KeyValueDTO> getCompany(Integer warehouseId, Integer type){
        return this.recordService.getCompany(warehouseId,type);
    }

    @ApiOperation(value = "修改备注")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "入库单id", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "desc", value = "备注", dataType = "String", paramType = "query", required = true)
    })
    @PostMapping("updateDesc")
    public Integer updateDesc(Long id,String desc){
        if (StringUtils.isEmpty(desc)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"备注不能为空");
        }
        return this.recordService.updateDesc(id,desc,super.userToken.getUserDTO().getLoginName());
    }

    @ApiOperation(value = "删除入库单")
    @ApiImplicitParam(name = "id", value = "入库单id", dataType = "Long", paramType = "query", required = true)
    @GetMapping("del")
    public Integer del(Long id){
        return this.recordService.del(id);
    }

    @ApiOperation(value = "提交待审核入库单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "入库单id", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "receivingShippingType", value = "运输方式", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "trackingNumber", value = "跟踪号", dataType = "Long", paramType = "query")
    })
    @GetMapping("audit")
    public Integer audit(Long id,String receivingShippingType,String trackingNumber){
        return this.recordService.audit(id,super.userToken.getUserDTO().getLoginName(),receivingShippingType,trackingNumber);
    }

    @ApiOperation(value = "打印箱唛")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "入库单id", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "printSize", value = "打印尺寸（1:A4，2:100*100，3:100*150）", dataType = "Integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "printType", value = "打印类型(1：入库清单，2：箱唛)", dataType = "Integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "boxArr", value = "print_type=1时，为空 print_type=2时，必填 入库单箱号(一次不能超过50个)，string[].tojsonstring", dataType = "String", paramType = "query", required = false)
    })
    @GetMapping("printBox")
    public void printBox(Long id,Integer printSize,Integer printType,String boxArr){
        BoxDTO boxDTO=this.recordService.printBox(id,printSize,printType,JSONArray.parseArray(boxArr,String.class));
        String fileName=id.toString().concat("_box").concat(".").concat(boxDTO.getFileType()==1?"png":"pdf");
        switch (boxDTO.getFileType()){
            case 1:
                super.response.setHeader("content-Type", "image/png");
                break;
            case 2:
                super.response.setHeader("content-Type", "application/pdf");
                break;
            default:
                return;
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("Content-Length", String.valueOf(boxDTO.getData().length));
        try (OutputStream out=super.response.getOutputStream()){
            out.write(boxDTO.getData());
        } catch (IOException e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"响应文件异常");
        }
    }

    @ApiOperation(value = "打印sku")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "入库单id", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "printSize", value = "打印尺寸（1：60*20，2：70*30，3：100*30）", dataType = "Integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "printCode", value = "打印编码(1：产品名称，2：made in china，5：商品英文名称，3：1和2，，6:1和5 ， 7：2和5,8:1和2和5都选，4：都不选)", dataType = "Integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "skuArr", value = "产品编码(一次不能超过50个)，string[].tojsonstring", dataType = "String", paramType = "query", required = false)
    })
    @GetMapping("printSku")
    public void printSku(Long id,Integer printSize,Integer printCode,String skuArr){
        BoxDTO boxDTO=this.recordService.printSku(id,printSize,printCode,JSONArray.parseArray(skuArr,String.class));
        String fileName=id.toString().concat("_sku").concat(".").concat(boxDTO.getFileType()==1?"png":"pdf");
        switch (boxDTO.getFileType()){
            case 1:
                super.response.setHeader("content-Type", "image/png");
                break;
            case 2:
                super.response.setHeader("content-Type", "application/pdf");
                break;
            default:
                return;
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("Content-Length", String.valueOf(boxDTO.getData().length));
        try (OutputStream out=super.response.getOutputStream()){
            out.write(boxDTO.getData());
        } catch (IOException e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"响应文件异常");
        }
    }

}
