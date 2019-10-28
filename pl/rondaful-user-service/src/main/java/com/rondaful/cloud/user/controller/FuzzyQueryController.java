package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.service.FuzzyQueryService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 模糊查询接口
 */
@RestController
public class FuzzyQueryController extends  BaseController{

    @Autowired
    private FuzzyQueryService fuzzyQueryService;

    Logger logger = LoggerFactory.getLogger(FuzzyQueryController.class);

    @ApiOperation("操作账号管理-操作员模糊查询")
    @PostMapping("/operationFuzzyQuery")
    @ApiImplicitParams({@ApiImplicitParam(name = "userid", value = "主账号id", dataType = "string",paramType = "query")})
    public List<String> getOperationUsernamr(String username){
        try {
            List<String> usernames = fuzzyQueryService.getOperationUsernamr(username);
            return usernames;
        } catch (Exception e) {
            logger.error("操作员模糊查询",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @ApiOperation("角色管理-角色代码模糊搜索")
    @PostMapping("/roleFuzzyQuery")
    @ApiImplicitParams({@ApiImplicitParam(name = "roleCode",value = "角色代码",dataType = "string",paramType = "query")})
    public List<String> getroleFuzzyQuery(String roleCode){
        try {
            List<String> roleCodes =  fuzzyQueryService.getroleFuzzyQuery(roleCode);
            return roleCodes;
        } catch (Exception e) {
            logger.error("角色代码模糊搜索",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @ApiOperation("供应商管理-供应商公司模糊搜索")  
    @PostMapping("/getSpplierCompanyFuzzyQuery")
    @ApiImplicitParams({@ApiImplicitParam(name = "spplierCompany",value = "公司名称",dataType = "string",paramType = "query")})
    public List<String> getSpplierCompanyFuzzyQuery(String spplierCompany){
        List<String> companyName =  fuzzyQueryService.getSpplierCompanyFuzzyQuery(spplierCompany);
        return companyName;
    }

  
    



}
