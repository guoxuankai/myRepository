package com.rondaful.cloud.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.user.model.dto.dep.DepartmentDTO;
import com.rondaful.cloud.user.model.dto.dep.DepartmentTreeDTO;
import com.rondaful.cloud.user.model.request.AddDepartmentReq;
import com.rondaful.cloud.user.service.IDepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
 * @Date: 2019/4/24
 * @Description:
 */
@Api(description = "组织架构",hidden = true)
@RestController
@RequestMapping("department/")
public class DepartmentController extends BaseController{
    @Autowired
    private IDepartmentService departmentService;

    @ApiOperation(value = "添加组织及职位")
    @PostMapping("add")
    public Integer add(AddDepartmentReq req){
        DepartmentDTO dto=new DepartmentDTO();
        BeanUtils.copyProperties(req,dto);
        dto.setCreateBy(super.userToken.getUserDTO().getLoginName());
        if (StringUtils.isNotEmpty(req.getPositionNames())){
            dto.setPositionNames(JSONArray.parseArray(req.getPositionNames(),String.class));
        }
        if (!UserEnum.platformType.CMS.getPlatformType().equals(super.userToken.getUserDTO().getPlatformType())){
            dto.setPlatform(super.userToken.getUserDTO().getPlatformType().byteValue());
        }else {
            dto.setAttribution(super.userToken.getUserDTO().getTopUserId());
        }
        return departmentService.add(dto);
    }

    @ApiOperation(value = "获取组织架构树")
    @GetMapping("getTree")
    public List<DepartmentTreeDTO> getTree(Integer userId,Byte platformType){
        Byte platform=null;
        UserDTO userDTO=super.userToken.getUserDTO();
        Integer attribution=null;
        if (platformType==null||userId==null){
            platform=userDTO.getPlatformType().byteValue();
            attribution=userDTO.getTopUserId();
        }else {
            attribution=userId;
            platform=platformType;
        }
        return this.departmentService.getTree(platform,attribution);
    }

    @ApiOperation(value = "修改组织结构")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", value = "组织id", dataType = "Integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "positionNames", value = "职位名称json数组.toString", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "departmentName", value = "组织名", dataType = "Integer", paramType = "query", required = true)
    })
    @PostMapping("update")
    public Integer update(Integer departmentId,String positionNames,String departmentName){
        List<String> pos=new ArrayList<>();
        if (StringUtils.isNotEmpty(positionNames)){
            pos=JSONArray.parseArray(positionNames,String.class);
        }
        return this.departmentService.update(departmentId,pos,super.userToken.getUserDTO().getLoginName(),departmentName);
    }

    @ApiOperation(value = "根据id删除组织")
    @ApiImplicitParam(name = "departmentId", value = "组织id", dataType = "Integer", paramType = "query", required = true)
    @PostMapping("delete")
    public Integer delete(Integer departmentId){
        return this.departmentService.delete(departmentId);
    }



}
