package com.rondaful.cloud.seller.controller;


import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPublishListingMobile;
import com.rondaful.cloud.seller.rabbitmq.AliexpressSender;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;
import com.rondaful.cloud.seller.service.IAliexpressPublishListingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *   速卖通刊登 移动端 Controller
 * @author chenhan
 *
 */
@RestController
@RequestMapping("/aliexpressMobile")
@Api(description = "Aliexpress刊登接口")
public class AliexpressPublishMobileController extends BaseController{

    private final Logger logger = LoggerFactory.getLogger(AliexpressPublishMobileController.class);

    @Autowired
    private IAliexpressPublishListingService aliexpressPublishListingService;
    @Autowired
    private GetLoginUserInformationByToken getUserInfo;
    @Autowired
    AuthorizationSellerService authorizationSellerService;
    @Autowired
    private AliexpressSender aliexpressSender;


    @ApiOperation("移动端查询速卖通刊登列表")
    @GetMapping("/findAllMobile")
    @AspectContrLog(descrption="移动端查询速卖通刊登列表",actionType= SysLogActionType.QUERY)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "当前页码", name = "page", dataType = "String", required = true),
            @ApiImplicitParam(paramType = "query", value = "每页显示行数", name = "row", dataType = "String", required = true)
    })
    public Page<AliexpressPublishListingMobile> findAllMobile(String page, String row){
        UserDTO userDTO = getUserInfo.getUserDTO();
        String sellerId;
        List<Integer> empowerIds = null;
        if(userDTO.getManage()){
            sellerId = userDTO.getUserId().toString();
        }else{
            sellerId = userDTO.getTopUserId().toString();
            empowerIds = this.getEmpowerIds(userDTO.getBinds());
            if(empowerIds==null || empowerIds.size()==0){
                return null;
            }
        }
        Page.builder(page, row);
        return aliexpressPublishListingService.getAllMobile(sellerId,empowerIds);
    }
    @ApiOperation("移动端查询速卖通刊登详情")
    @GetMapping("/getAliexpressPublishListingMobileById")
    @AspectContrLog(descrption="移动端查询速卖通刊登详情",actionType= SysLogActionType.QUERY)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "刊登id", name = "id", dataType = "int", required = true)
    })
    public AliexpressPublishListingMobile getAliexpressPublishListingMobileById(Long id){
        String headeri18n = request.getHeader("i18n");
        return aliexpressPublishListingService.getAliexpressPublishListingMobileById(id,headeri18n);
    }


    @ApiOperation("移动端查询速卖通刊登成功过的条数")
    @GetMapping("/findAliexpressPublishCount")
    @AspectContrLog(descrption="移动端查询速卖通刊登成功过的条数",actionType= SysLogActionType.QUERY)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "用户名", name = "userName", dataType = "String", required = true)
    })
    public Integer findAliexpressPublishCount(String userName){
        return aliexpressPublishListingService.getUserNameCount(userName);
    }




}
