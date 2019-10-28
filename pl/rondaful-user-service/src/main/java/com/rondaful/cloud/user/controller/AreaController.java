package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.user.entity.AreaCode;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.model.dto.area.AreaCodeDTO;
import com.rondaful.cloud.user.model.dto.area.PhoneCodeDTO;
import com.rondaful.cloud.user.service.IAreaCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/25
 * @Description:
 */
@Api(description = "区域数据")
@RestController
@RequestMapping("area/code/")
public class AreaController extends BaseController{

    @Autowired
    private IAreaCodeService areaCodeService;

    @ApiOperation(value = "获取区域树")
    @GetMapping("getTree")
    public List<AreaCodeDTO> getTree(){
        return this.areaCodeService.getTre(super.request.getHeader("i18n"));
    }

    @ApiOperation(value = "获取手机区号")
    @GetMapping("open/getsPhoneCode")
    public List<PhoneCodeDTO> getsPhoneCode(){
        return this.areaCodeService.getsPhoneCode(super.request.getHeader("i18n"));
    }

    @ApiOperation(value = "获取国家信息")
    @GetMapping("getCountry")
    public List<AreaCode> getCountry(@RequestParam(value = "level", defaultValue = "1") Integer level,
                                     @RequestParam(value = "parentId", defaultValue = "") Integer parentId){
        return areaCodeService.getsByLevel(level, parentId);
    }

    @ApiOperation(value = "获取国家信息")
    @GetMapping("getArea")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "countryName", value = "国家名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "countryCode", value = "国家编码", dataType = "string", paramType = "query") })
    public AreaCode getArea(@RequestParam(value = "countryName", defaultValue = "") String countryName,
                            @RequestParam(value = "countryCode", defaultValue = "")String countryCode){
        if(StringUtils.isEmpty(countryCode) && StringUtils.isEmpty(countryName)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435);
        }
        return this.areaCodeService.getArea(countryName,countryCode);
    }

}
