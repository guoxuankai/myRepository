package com.rondaful.cloud.commodity.controller;

import com.rondaful.cloud.commodity.entity.SkuMapRule;
import com.rondaful.cloud.commodity.mapper.SkuMapRuleMapper;
import com.rondaful.cloud.commodity.service.SkuMapRuleService;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *  平台sku映射匹配规则
 */
@Api(description = "平台sku映射匹配规则")
@RestController
public class SkuMapRuleController {

    private static final Logger logger = LoggerFactory.getLogger(SkuMapRuleController.class);
    
    @Autowired
    private SkuMapRuleService skuMapRuleService;
    
    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;
    
    @Autowired
    private SkuMapRuleMapper skuMapRuleMapper;


    @AspectContrLog(descrption = "添加用户sku规则", actionType = SysLogActionType.ADD)
    @PostMapping("/skuMap/insertSkuMapRule")
    @ApiOperation("添加用户sku规则")
    public SkuMapRule insertSkuMapRule(@RequestBody SkuMapRule skuMapRule) {
        try {
        	//判断是否登录
            UserAll userAll=getLoginUserInformationByToken.getUserInfo();
            if (userAll!=null) {
            	UserCommon user = userAll.getUser();
            	if (UserEnum.platformType.SELLER.getPlatformType().equals(user.getPlatformType())) {//卖家平台
            		if (user.getUserid() != null && user.getTopUserId() != null) {
            			if (user.getTopUserId() == 0) {//主账号
            				skuMapRule.setSellerId(String.valueOf(user.getUserid()));
                            skuMapRule.setSellerAccount(user.getLoginName());
            			}else {
            				skuMapRule.setSellerId(String.valueOf(user.getTopUserId()));
                            skuMapRule.setSellerAccount(user.getLoginName());
        				}
    				}
            		
            		skuMapRuleService.insert(skuMapRule);
            	}
    		}
            return skuMapRule;
        } catch (Exception e) {
            logger.error("添加用户sku规则失败", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "添加失败");
        }
    }

    @AspectContrLog(descrption = "删除用户sku规则", actionType = SysLogActionType.DELETE)
    @DeleteMapping("/skuMap/deleteSkuMapRule/{id}")
    @ApiOperation("删除用户sku规则")
    public void deleteSkuMapRule(@ApiParam(value = "sku映射id", name = "id", required = true) @PathVariable Integer id) {
        try {
            skuMapRuleService.delete(id);
        } catch (Exception e) {
            logger.error("删除用户sku规则异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "删除失败");
        }
    }

    @AspectContrLog(descrption = "更新用户sku规则", actionType = SysLogActionType.UDPATE)
    @PutMapping("/skuMap/updateSkuMapRule")
    @ApiOperation("更新用户sku规则")
    public void updateSkuMapRule(@RequestBody SkuMapRule skuMapRule) {
        if (skuMapRule.getId() == null || skuMapRule.getId() == 0)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403.getCode(), "ID不能为空");
        
        try {
            skuMapRuleService.update(skuMapRule);
        } catch (Exception e) {
            logger.error("更新用户sku规则失败", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "更新失败");
        }
    }

    @AspectContrLog(descrption = "查询用户sku规则", actionType = SysLogActionType.QUERY)
    @GetMapping("/skuMap/selectMyRule")
    @ApiOperation("查询用户sku规则")
    public SkuMapRule selectMyRule() {
    	SkuMapRule result=null;
    	String sellerId="";
        try {
        	//判断是否登录
            UserAll userAll=getLoginUserInformationByToken.getUserInfo();
            if (userAll!=null) {
            	UserCommon user = userAll.getUser();
            	if (UserEnum.platformType.SELLER.getPlatformType().equals(user.getPlatformType())) {//卖家平台
            		if (user.getUserid() != null && user.getTopUserId() != null) {
            			if (user.getTopUserId() == 0) {//主账号
            				sellerId=String.valueOf(user.getUserid());
            			}else {
            				sellerId=String.valueOf(user.getTopUserId());
        				}
    				}
            		result=skuMapRuleMapper.selectBySellerId(sellerId);
            	}
    		}
            return result;
        } catch (Exception e) {
            logger.error("查询用户sku规则异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询失败");
        }
    }





}
