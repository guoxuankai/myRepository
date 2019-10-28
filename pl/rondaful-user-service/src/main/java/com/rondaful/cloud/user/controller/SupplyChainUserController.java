package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.KeyValueDTO;
import com.rondaful.cloud.user.model.dto.user.QuerySuppluChinaDTO;
import com.rondaful.cloud.user.model.dto.user.SupplyChainUserDTO;
import com.rondaful.cloud.user.model.dto.user.SupplyChainUserPageDTO;
import com.rondaful.cloud.user.model.request.user.SupplyChainQueryPageReq;
import com.rondaful.cloud.user.model.request.user.SupplyChainUserReq;
import com.rondaful.cloud.user.service.ISupplyChainUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
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
 * @Date: 2019/6/22
 * @Description:
 */
@Api(description = "供应链公司相关业务")
@RestController
@RequestMapping("supply/chain/")
public class SupplyChainUserController extends BaseController{

    @Autowired
    private ISupplyChainUserService userService;

    @ApiOperation(value = "新增供应链公司")
    @PostMapping("add")
    public Integer add(SupplyChainUserReq req){
        SupplyChainUserDTO dto=new SupplyChainUserDTO();
        BeanUtils.copyProperties(req,dto);
        dto.setCreateBy(super.userToken.getUserDTO().getLoginName());
        return this.userService.add(dto);
    }

    @ApiOperation(value = "供应链公司修改")
    @PostMapping("update")
    public Integer update(SupplyChainUserReq req){
        SupplyChainUserDTO dto=new SupplyChainUserDTO();
        BeanUtils.copyProperties(req,dto);
        dto.setUpdateBy(super.userToken.getUserDTO().getLoginName());
        return this.userService.update(dto);
    }

    @ApiOperation(value = "根据id获取供应链公司")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Integer", paramType = "query",required = true)
    @GetMapping("get")
    public SupplyChainUserDTO get(Integer id){
        return this.userService.get(id);
    }

    @ApiOperation(value = "分页查询供应链公司")
    @PostMapping("getsPage")
    public PageDTO<SupplyChainUserPageDTO> getsPage(SupplyChainQueryPageReq req){
        QuerySuppluChinaDTO dto=new QuerySuppluChinaDTO();
        BeanUtils.copyProperties(req,dto);
        if (req.getStartTime()==null||req.getEndTime()==null){
            dto.setDateType(null);
        }
        return this.userService.getsPage(dto);
    }


    @ApiOperation(value = "根据平台获取供应链公司下拉列表")
    @ApiImplicitParam(name = "type", value = "0-供应商,1-卖家,3-仓库", dataType = "Integer", paramType = "query",required = true)
    @GetMapping("getsSelect")
    public List<KeyValueDTO> getsSelect(Integer type){
        return this.userService.getsSelect(type);
    }


}
