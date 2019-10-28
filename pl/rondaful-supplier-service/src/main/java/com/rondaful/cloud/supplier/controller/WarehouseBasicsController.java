package com.rondaful.cloud.supplier.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.model.dto.KeyValueDTO;
import com.rondaful.cloud.supplier.model.dto.basics.*;
import com.rondaful.cloud.supplier.model.enums.WarehouseFirmEnum;
import com.rondaful.cloud.supplier.model.request.basic.*;
import com.rondaful.cloud.supplier.model.response.basic.WarehouseReq;
import com.rondaful.cloud.supplier.service.ILogisticsService;
import com.rondaful.cloud.supplier.service.IWarehouseBasicsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
 * @Date: 2019/6/11
 * @Description:
 */
@Api(description = "仓库授权基本信息接口")
@RestController
@RequestMapping("/warehouse/basics/")
public class  WarehouseBasicsController extends BaseController {

    @Autowired
    private IWarehouseBasicsService basicsService;
    @Autowired
    private ILogisticsService logisticsService;

    @ApiOperation(value = "新增仓库")
    @PostMapping("add")
    public Integer add(AddBasicReq req){
        UserDTO userDTO=super.userToken.getUserDTO();
        WarehouseFirmDTO dto=new WarehouseFirmDTO();
        BeanUtils.copyProperties(req,dto);
        if (UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())){
            if (dto.getSupplierId()==null){
                dto.setSupplierId(0);
            }
        }else if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())){
            dto.setSupplierId(userDTO.getTopUserId());
        }else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"无权操作");
        }
        /*if (WarehouseFirmEnum.RONDAFUL.getCode().equals(req.getFirmCode())&&(!UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())||!userDTO.getManage())){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"添加私有仓库只有超级管理员才有权限操作");
        }*/
        dto.setCreateBy(userDTO.getLoginName());
        Integer result=this.basicsService.add(dto);
        if (result>0){
            this.logisticsService.init(result);
        }
        return result;
    }

    @ApiOperation(value = "同步仓库")
    @GetMapping("update")
    public Integer update(){
        return this.basicsService.update();
    }

    @ApiOperation(value = "根据仓库id删除所属列表")
    @ApiImplicitParam(name = "firmId", value = "仓库服务商id", dataType = "Integer",paramType = "query",required = true)
    @GetMapping("del")
    public Integer del(Integer firmId){
        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"该功能现关闭,特殊情况联系客服");
        //return this.basicsService.del(firmId);
    }

    @ApiOperation(value = "获取仓库树列表")
    @GetMapping("getTree")
    public List<WarehouseLogisTreeDTO> getTree(){
        UserDTO userDTO=super.userToken.getUserDTO();
        List<Integer> supplierIds=new ArrayList<>(3);
        if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())){
            supplierIds.add(0);
            supplierIds.add(userDTO.getTopUserId());
        }
        return this.basicsService.getTree(supplierIds,super.request.getHeader("i18n"));
    }

    @ApiOperation(value = "修改状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warehouseIds", value = "仓库id数组  转json字符串", dataType = "Integer",paramType = "query",required = true),
            @ApiImplicitParam(name = "status", value = "状态 1-ok  4-no", dataType = "Integer",paramType = "query",required = true)
    })
    @PostMapping("updateStatus")
    public Integer updateStatus(String warehouseIds,Integer status){
        return this.basicsService.updateStatus(JSONArray.parseArray(warehouseIds,Integer.class),status);
    }

    @ApiOperation(value = "获取服务商名称")
    @ApiImplicitParam(name = "type", value = "1-公共,0-私营,空全部", dataType = "Integer",paramType = "query",required = true)
    @GetMapping("getsServiceName")
    public List<WarehouseServiceDTO> getsServiceName(Integer type){
        return this.basicsService.getsServiceName(type);
    }


    @ApiOperation(value = "根据服务商获取仓库列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serviceCode", value = "仓库服务商编码", dataType = "String",paramType = "query",required = true),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "Integer",paramType = "query")
    })
    @GetMapping("getSelectByServiceCode")
    public List<WarehouseSelectDTO> getSelectByServiceCode(String serviceCode,Integer userId){
        if (userId==null){
            if (WarehouseFirmEnum.RONDAFUL.getCode().equals(serviceCode)){
                UserDTO userDTO=super.userToken.getUserDTO();
                if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())){
                    userId=userDTO.getTopUserId();
                }
            }else {
                userId=0;
            }
        }
        return this.basicsService.getSelectByServiceCode(serviceCode,userId);
    }

    @ApiOperation(value = "获取筛选仓库")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "supplierId", value = "供应商主账号id", dataType = "Integer",paramType = "query"),
            @ApiImplicitParam(name = "type", value = "1-公有仓,2-私有仓,空-全部", dataType = "Integer",paramType = "query"),
            @ApiImplicitParam(name = "warehouseId", value = "仓库id", dataType = "Integer",paramType = "query")
    })
    @GetMapping("getSelect")
    public List<WarehouseSelectDTO> getSelect(Integer supplierId,Integer type,Integer warehouseId){
        List<Integer> warehouseIds=new ArrayList<>();
        List<Integer> supplierIds=new ArrayList<>();
        UserDTO userDTO=super.userToken.getUserDTO();
        if (supplierId!=null||warehouseId!=null){
            if (warehouseId!=null){
                warehouseIds.add(warehouseId);
            }
            if (supplierId!=null){
                supplierIds.add(supplierId);
            }
            if (type==null||type==1){
                supplierIds.add(0);
            }
        }else {
            if (type==null||type==2){
                if (userDTO.getManage()){
                    if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())){
                        supplierIds=super.getBinds().get(USER_ID_LIST);
                    }
                }else {
                    if (UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())){
                        supplierIds=userDTO.getManage()?new ArrayList<>(0):super.getBinds().get(USER_ID_LIST);
                    }else if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())){
                        warehouseIds=super.getBinds().get(WAREHOUSE_ID_LIST);
                        supplierIds.add(userDTO.getTopUserId());
                    }
                }
            }
            if (type==null||type==1){
                if (type!=null&&type==1){
                    supplierIds.add(0);
                }
                if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())){
                    if (userDTO.getManage()){
                        supplierIds.add(0);
                    }else {
                        warehouseIds=super.getBinds().get(WAREHOUSE_ID_LIST);
                    }
                }
            }
            if (!userDTO.getManage()&&CollectionUtils.isEmpty(supplierIds)){
                if (UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())&&CollectionUtils.isEmpty(supplierIds)){
                    //return new ArrayList<>();
                }else if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())&&CollectionUtils.isEmpty(supplierIds)){
                    return new ArrayList<>();
                }
            }
        }
        return this.basicsService.getSelect(warehouseIds,supplierIds,super.request.getHeader("i18n"),type);
    }

    @ApiOperation(value = "获取服务商账号")
    @ApiImplicitParam(name = "supplyId", value = "供应商id  ", dataType = "Integer", paramType = "query")
    @GetMapping("getsFirm")
    public List<WarehouseSelectDTO> getsFirm(Integer supplyId){
        List<Integer> supplyIds=new ArrayList<>(2);
        supplyIds.add(0);
        if (supplyId==null){
            supplyId=super.userToken.getUserDTO().getUserId();
        }
        supplyIds.add(supplyId);
        return this.basicsService.getsFirm(supplyIds);
    }

    @ApiOperation(value = "根据仓库id获取服务商")
    @ApiImplicitParam(name = "warehouseId", value = "仓库id ", dataType = "Integer", paramType = "query",required = true)
    @GetMapping("getByWarehouseId")
    public WarehouseReq getByWarehouseId(Integer warehouseId){
        WarehouseReq result=new WarehouseReq();
        WarehouseDTO dto=this.basicsService.getByWarehouseId(warehouseId);
        if (dto==null){
            return result;
        }
        BeanUtils.copyProperties(dto,result,"warehouseName");
        result.setWarehouseName(StringUtils.isEmpty(super.request.getHeader("i18n"))?dto.getWarehouseName(): Utils.translation(dto.getWarehouseName()));
        if (WarehouseFirmEnum.GOODCANG.getCode().equals(dto.getFirmCode())){
            AccountDTO accountDTO=this.basicsService.getAccount(dto.getAppKey(),dto.getAppToken());
            if (accountDTO!=null){
                BeanUtils.copyProperties(accountDTO,result);
            }
        }
        return result;
    }


    @ApiOperation(value = "获取当前账号的仓库列表")
    @GetMapping("getsWarehouseList")
    public List<WarehouseCountryDTO> getsWarehouseList(){
        List<Integer> supplierIds=new ArrayList<>(2);
        List<Integer> warehouseIds=new ArrayList<>();
        UserDTO userDTO=super.userToken.getUserDTO();
        if (userDTO.getManage()){
            supplierIds.add(0);
            supplierIds.add(userDTO.getUserId());
        }else {
            if (CollectionUtils.isEmpty(userDTO.getBinds())){
                return null;
            }
            warehouseIds=JSONObject.parseArray(JSONObject.toJSONString(userDTO.getBinds().get(0).getBindCode()),Integer.class);
        }
        return this.basicsService.getsWarehouseList(supplierIds,warehouseIds,super.request.getHeader("i18n"));
    }


    @ApiOperation(value = "分页获取仓库列表")
    @GetMapping("getsPage")
    public PageDTO<WarehousePageDTO> getsPage(WarehousePageReq req){
        WarehouseQueryDTO dto=new WarehouseQueryDTO();
        BeanUtils.copyProperties(req,dto);
        if (StringUtils.isNotEmpty(req.getName())){
            dto.setId(Integer.valueOf(req.getName()));
        }
        if (StringUtils.isNotEmpty(req.getFirmCode())){
            if (req.getFirmCode().equals(String.valueOf(WarehouseFirmEnum.GOODCANG.getCode().hashCode()))){
                dto.setFirmCode(WarehouseFirmEnum.GOODCANG.getCode());
            }else if (req.getFirmCode().equals(String.valueOf(WarehouseFirmEnum.RONDAFUL.getCode().hashCode()))){
                dto.setFirmCode(WarehouseFirmEnum.RONDAFUL.getCode());
            }else if (req.getFirmCode().equals(String.valueOf(WarehouseFirmEnum.WMS.getCode().hashCode()))){
                dto.setFirmCode(WarehouseFirmEnum.WMS.getCode());
            }
        }
        List<Integer> supplierIds=new ArrayList<>();
        List<Integer> warehouseIds=new ArrayList<>();
        UserDTO userDTO=super.userToken.getUserDTO();
        if (req.getSupplierId()!=null||req.getWarehouseId()!=null){
            if (req.getSupplierId()!=null){
                supplierIds.add(req.getSupplierId());
            }
            if (req.getWarehouseId()!=null){
                warehouseIds.add(req.getWarehouseId());
            }
        }
        switch (req.getType()){
            case 1:
                supplierIds.add(0);
                break;
            case 2:
                if (UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())){
                    supplierIds=super.getBinds().get(USER_ID_LIST);
                    if (!userDTO.getManage()&&CollectionUtils.isEmpty(supplierIds)){
                        return new PageDTO<>(0,req.getCurrentPage());
                    }
                }else if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())){
                    if (userDTO.getManage()){
                        supplierIds.add(userDTO.getUserId());
                    }else {
                        warehouseIds=super.getBinds().get(WAREHOUSE_ID_LIST);
                        supplierIds.add(userDTO.getTopUserId());
                        if (CollectionUtils.isEmpty(warehouseIds)){
                            return new PageDTO<>(0,req.getCurrentPage());
                        }
                    }
                }
                break;
            default:
                return new PageDTO<>(0,req.getCurrentPage());
        }
        dto.setWarehouseIds(warehouseIds);
        dto.setSupplierIds(supplierIds);
        dto.setLanguageType(super.request.getHeader("i18n"));
        return this.basicsService.getsPage(dto);
    }


    @ApiOperation(value = "修改仓库地址")
    @PostMapping("updateAddress")
    public Integer updateAddress(AddressReq req){
        AddressDTO dto=new AddressDTO();
        BeanUtils.copyProperties(req,dto);
        return this.basicsService.updateAddress(dto);
    }

    @ApiOperation(value = "获取仓库地址")
    @ApiImplicitParam(name = "warehouseId", value = "仓库id", dataType = "Integer", paramType = "query", required = true)
    @GetMapping("getAddress")
    public AddressDTO getAddress(Integer warehouseId){
        return this.basicsService.getAddress(warehouseId,super.request.getHeader("i18n"));
    }

    @ApiOperation(value = "获取仓库服务商账号id")
    @ApiImplicitParam(name = "type", value = "1-公有仓,2-私有仓,空-全部", dataType = "Integer",paramType = "query")
    @GetMapping("getsAccount")
    public List<String> getsAccount(Integer type){
        List<Integer> supplierIds=new ArrayList<>();
        switch (type){
            case 1:
                supplierIds.add(0);
                break;
            case 2:
                UserDTO userDTO=super.userToken.getUserDTO();
                if (UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())){
                    if (!userDTO.getManage()){
                        if (CollectionUtils.isNotEmpty(userDTO.getBinds())){
                            for (UserAccountDTO dto:userDTO.getBinds()) {
                                if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(dto.getBindType())){
                                    supplierIds.addAll(JSONArray.parseArray(JSONObject.toJSONString(dto.getBindCode()),Integer.class));
                                }
                            }
                        }
                        if (CollectionUtils.isEmpty(supplierIds)){
                            return null;
                        }
                    }
                }else if(UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())){
                    supplierIds.add(userDTO.getTopUserId());
                }else {
                    return null;
                }
                break;
                default:
                    return null;
        }
        return this.basicsService.getsAccount(supplierIds);
    }

    @ApiOperation(value = "获取仓库名")
    @ApiImplicitParam(name = "type", value = "1-公有仓,2-私有仓,空-全部", dataType = "Integer",paramType = "query")
    @GetMapping("getsWarehouseName")
    public List<KeyValueDTO> getsWarehouseName(Integer type){
        List<Integer> supplierIds=new ArrayList<>();
        switch (type){
            case 1:
                supplierIds.add(0);
                break;
            case 2:
                UserDTO userDTO=super.userToken.getUserDTO();
                if (UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())){
                    if (!userDTO.getManage()){
                        if (CollectionUtils.isNotEmpty(userDTO.getBinds())){
                            for (UserAccountDTO dto:userDTO.getBinds()) {
                                if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(dto.getBindType())){
                                    supplierIds.addAll(JSONArray.parseArray(JSONObject.toJSONString(dto.getBindCode()),Integer.class));
                                }
                            }
                        }
                        if (CollectionUtils.isEmpty(supplierIds)){
                            return null;
                        }
                    }
                }else if(UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())){
                    supplierIds.add(userDTO.getTopUserId());
                }else {
                    return null;
                }
                break;
            default:
                return null;
        }
        return this.basicsService.getsWarehouseName(supplierIds,super.request.getHeader("i18n"));
    }

    @ApiOperation(value = "页面搜索特殊条件下使用(不推荐其他接口使用)")
    @ApiImplicitParam(name = "type", value = "1-公有仓,2-私有仓", dataType = "Integer",paramType = "query",required = true)
    @GetMapping("getSelectList")
    public List<WarehouseSelectDTO> getSelectList(Integer type){
        UserDTO userDTO=super.userToken.getUserDTO();
        Integer supplierId=null;
        if (type!=null&&type==1){
            supplierId=0;
        }else if (type!=null&&type==2){
            supplierId=UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())?userDTO.getTopUserId():null;
        }else {
            return null;
        }
        return this.basicsService.getSelectList(supplierId,super.request.getHeader("i18n"));
    }

}
