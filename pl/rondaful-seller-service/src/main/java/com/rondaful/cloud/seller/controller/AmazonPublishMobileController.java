package com.rondaful.cloud.seller.controller;


import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.AmazonPublishListingMobile;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 *   亚马逊刊登 移动端 Cntroller
 * @author ouxiangfeng
 *
 */
@RestController
@RequestMapping("/amazonMobile")
@Api(description = "Amazon刊登接口-OuXiangFeng")
public class AmazonPublishMobileController {

    private final Logger logger = LoggerFactory.getLogger(AmazonPublishMobileController.class);

    private AmazonPublishListingService amazonPublishListingService;

    private GetLoginUserInformationByToken getUserInfo;

    @Autowired
    private AuthorizationSellerService authorizationSellerService;


    @Autowired
    public AmazonPublishMobileController(AmazonPublishListingService amazonPublishListingService, GetLoginUserInformationByToken getUserInfo) {
        this.amazonPublishListingService = amazonPublishListingService;
        this.getUserInfo = getUserInfo;
    }




    @ApiOperation("移动端查询亚马逊刊登列表")
    @GetMapping("/findAllMobile")
    @AspectContrLog(descrption="移动端查询亚马逊刊登列表",actionType= SysLogActionType.QUERY)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "当前页码", name = "page", dataType = "String", required = true),
            @ApiImplicitParam(paramType = "query", value = "每页显示行数", name = "row", dataType = "String", required = true)
    })
    public Page<AmazonPublishListingMobile> findAllMobile(String page, String row){
       // UserAll userInfo = getUserInfo.getUserInfo();
        UserDTO userdTO=getUserInfo.getUserDTO();
        List<String> bindCode =new ArrayList<>();
        AmazonPublishListing  model =  new AmazonPublishListing();
        if(userdTO.getManage()) {
            //主账号

        }else {
            //子账号
            List<UserAccountDTO> binds = userdTO.getBinds();
            if(CollectionUtils.isEmpty(binds)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401, "权限异常");
            }
            bindCode = binds.get(0).getBindCode();
            List<Empower> accounts = authorizationSellerService.getEmpowerByIds(strToInt(bindCode));
            if(accounts == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600, "权限异常");
            }
            bindCode.clear();
            for (Empower empower : accounts) {
                if(!bindCode.contains(empower.getPinlianAccount())) {
                    bindCode.add(empower.getAccount());
                }
            }
            model.setPublishAccounts(bindCode);
        }
        model.setPlAccount(userdTO.getTopUserLoginName());
        Page.builder(page, row);
        return amazonPublishListingService.selectAllMobile(model);
    }

    private List<Integer> strToInt(List<String> bindCode) {
        List<Integer> empowerIds=new ArrayList<>();
        for (String str : bindCode) {
            empowerIds.add(Integer.parseInt(str));
        }
        return empowerIds;
    }

    @ApiOperation("移动端查询亚马逊刊登成功过的条数")
    @GetMapping("/findCount")
    @AspectContrLog(descrption="移动端查询亚马逊刊登成功过的条数",actionType= SysLogActionType.QUERY)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "用户名", name = "userName", dataType = "String", required = true)
    })
    public Integer findCount(String userName){
        return amazonPublishListingService.selectCount(userName);
    }




}
