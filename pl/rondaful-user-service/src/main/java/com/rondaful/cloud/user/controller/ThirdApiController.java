package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.common.annotation.OpenAPI;
import com.rondaful.cloud.user.model.dto.area.AreaCodeDTO;
import com.rondaful.cloud.user.model.response.third.CountryResp;
import com.rondaful.cloud.user.service.IAreaCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/8/5
 * @Description:
 */
@Api(description = "第三方对外接口")
@RestController
@RequestMapping("/third/app/basic")
public class ThirdApiController {

    @Autowired
    private IAreaCodeService areaCodeService;

    @ApiOperation(value = "获取所有国家")
    @OpenAPI(isRequire = false)
    @PostMapping("getAllCountry")
    public List<CountryResp> getAllCountry(){
        List<AreaCodeDTO> list=this.areaCodeService.getTre(null);
        List<CountryResp> result=new ArrayList<>(list.size());
        list.forEach(dto->{
            result.add(new CountryResp(dto.getName(),dto.getCode()));
        });
        return result;
    }

}
