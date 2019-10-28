package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.user.model.dto.logger.CreditLoggerDTO;
import com.rondaful.cloud.user.service.ILoggerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xqq
 * @Date: 2019/7/25
 * @Description:
 */
@Api(description = "内部调用服务")
@RestController
@RequestMapping("/interior/service/")
public class InteriorServiceController {

    @Autowired
    private ILoggerService loggerService;

    @ApiOperation(value = "查询授信的日志")
    @ApiImplicitParams({
            @ApiImplicitParam( name = "userId", value = "用户ID", dataType = "Integer",paramType = "query",required = true),
            @ApiImplicitParam( name = "createBy", value = "创建人", dataType = "String",paramType = "query",required = true),
            @ApiImplicitParam( name = "operate", value = "操作描述", dataType = "String",paramType = "query",required = true),
            @ApiImplicitParam( name = "desc", value = "备注", dataType = "String",paramType = "query",required = true)
    })
    @PostMapping("insertCredits")
    public Integer insertCredits(Integer userId,String createBy,String operate,String desc){
        CreditLoggerDTO dto=new CreditLoggerDTO();
        dto.setCreateBy(createBy);
        dto.setDesc(desc);
        dto.setOperate(operate);
        dto.setUserId(userId);
        return this.loggerService.insertCredits(dto);
    }
}
