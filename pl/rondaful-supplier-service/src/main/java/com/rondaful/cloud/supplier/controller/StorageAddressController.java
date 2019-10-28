package com.rondaful.cloud.supplier.controller;

import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.AddressDTO;
import com.rondaful.cloud.supplier.model.request.inventory.AddressReq;
import com.rondaful.cloud.supplier.service.IStorageAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/8/7
 * @Description:
 */
@Api(description = "入库常用地址")
@RestController
@RequestMapping("/storage/address/")
public class StorageAddressController extends BaseController {

    @Autowired
    private IStorageAddressService addressService;


    @ApiOperation(value = "新增联系地址")
    @PostMapping("add")
    public Integer add(AddressReq req){
        AddressDTO dto=new AddressDTO();
        BeanUtils.copyProperties(req,dto);
        dto.setSupplierId(super.userToken.getUserDTO().getTopUserId());
        return this.addressService.add(dto);
    }

    @ApiOperation(value = "修改联系地址")
    @PostMapping("update")
    public Integer update(AddressReq req){
        AddressDTO dto=new AddressDTO();
        BeanUtils.copyProperties(req,dto);
        return this.addressService.update(dto);
    }

    @ApiOperation(value = "删除联系地址")
    @ApiImplicitParam(name = "id", value = "联系地址id", dataType = "Long", paramType = "query", required = true)
    @GetMapping("del")
    public Integer del(Integer id){
        return this.addressService.del(id);
    }

    @ApiOperation(value = "获取当前登录供应商的所有联系地址")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "联系人电话", name = "phone", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "当前页", name = "currentPage", dataType = "Integer",required = true),
            @ApiImplicitParam(paramType = "query", value = "展示条数", name = "pageSize", dataType = "Integer",required = true)
    })
    @GetMapping("getsBySupplierId")
    public PageDTO<AddressDTO> getsBySupplierId(String phone, Integer currentPage, Integer pageSize){
        return this.addressService.getsBySupplierId(super.userToken.getUserDTO().getTopUserId(),phone,currentPage,pageSize);
    }
}
