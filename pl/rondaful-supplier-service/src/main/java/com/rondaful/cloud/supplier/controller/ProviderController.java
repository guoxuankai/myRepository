package com.rondaful.cloud.supplier.controller;

import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.model.dto.KeyValueDTO;
import com.rondaful.cloud.supplier.model.dto.procurement.ProviderDTO;
import com.rondaful.cloud.supplier.model.dto.procurement.ProviderNameDTO;
import com.rondaful.cloud.supplier.model.dto.procurement.ProviderPageDTO;
import com.rondaful.cloud.supplier.model.dto.procurement.ProviderQueryPageDTO;
import com.rondaful.cloud.supplier.model.request.procurement.ProviderReq;
import com.rondaful.cloud.supplier.model.request.procurement.QueryPageProviderReq;
import com.rondaful.cloud.supplier.service.IProviderService;
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
 * @Date: 2019/6/19
 * @Description:
 */
@Api(description = "供货商相关服务")
@RestController
@RequestMapping("/provider/")
public class ProviderController extends BaseController{

    @Autowired
    private IProviderService providerService;


    @ApiOperation(value = "新增供应商")
    @PostMapping("add")
    public Integer add(ProviderReq req){
        UserDTO userDTO=super.userToken.getUserDTO();
        ProviderDTO dto=new ProviderDTO();
        BeanUtils.copyProperties(req,dto);
        dto.setCreateBy(userDTO.getLoginName());
        dto.setSupplierId(userDTO.getTopUserId());
        return this.providerService.add(dto);
    }

    @ApiOperation(value = "修改供应商")
    @PostMapping("update")
    public Integer update(ProviderReq req){
        ProviderDTO dto=new ProviderDTO();
        BeanUtils.copyProperties(req,dto);
        dto.setUpdateBy(super.userToken.getUserDTO().getLoginName());
        return this.providerService.update(dto);
    }


    @ApiOperation(value = "修改状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "供货商id", dataType = "Integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "status", value = "状态:1-启用,2-审核中,3-审核失败,4-禁用", dataType = "Integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "remake", value = "备注", dataType = "String", paramType = "query" )
    })
    @PostMapping("updateStatus")
    public Integer updateStatus(Integer id,Integer status,String remake){
        return this.providerService.updateStatus(id,status,remake,super.userToken.getUserDTO().getLoginName());
    }

    @ApiOperation(value = "根据id获取详情")
    @ApiImplicitParam(name = "id", value = "供货商id", dataType = "Integer", paramType = "query", required = true)
    @GetMapping("get")
    public ProviderDTO get(Integer id){
        return this.providerService.get(id);
    }

    @ApiOperation(value = "分页获取参数")
    @PostMapping("getsPage")
    public PageDTO<ProviderPageDTO> getsPage(QueryPageProviderReq req){
        ProviderQueryPageDTO queryPageDTO=new ProviderQueryPageDTO();
        BeanUtils.copyProperties(req,queryPageDTO);
        queryPageDTO.setSupplierId(super.userToken.getUserDTO().getTopUserId());
        return this.providerService.getsPage(queryPageDTO);
    }

    @ApiOperation(value = "获取供货商名称下拉列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "supplierId", value = "供货商id(供货商平台可以不传)", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pinlianSku", value = "pinlianSku", dataType = "String", paramType = "query")
    })
    @GetMapping("getSelectName")
    public List<KeyValueDTO> getSelectName(Integer supplierId,String pinlianSku){
        if (supplierId==null){
            supplierId=super.userToken.getUserDTO().getTopUserId();
        }
        return this.providerService.getSelectName(supplierId,pinlianSku);
    }

    @ApiOperation(value = "根据供应商及列表获取供货商名称及采购人")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "supplierId", value = "供货商id(供货商平台可以不传)", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "levelThree", value = "商品的三级分类", dataType = "String", paramType = "query")
    })
    @GetMapping("getsProviderName")
    private List<ProviderNameDTO> getsProviderName(Integer supplierId, String levelThree){
        UserDTO userDTO=super.userToken.getUserDTO();
        if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())&&supplierId==null){
            supplierId=userDTO.getTopUserId();
        }
        return this.providerService.getsProviderName(supplierId,levelThree);
    }


}
