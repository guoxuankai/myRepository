package com.rondaful.cloud.commodity.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rondaful.cloud.commodity.entity.CommodityBase;
import com.rondaful.cloud.commodity.entity.SellerAuth;
import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.commodity.enums.WarehouseFirmEnum;
import com.rondaful.cloud.commodity.mapper.CommodityBaseMapper;
import com.rondaful.cloud.commodity.mapper.CommodityBelongSellerMapper;
import com.rondaful.cloud.commodity.mapper.SellerAuthMapper;
import com.rondaful.cloud.commodity.service.TongToolService;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Api(description = "推送通途控制层")
@RequestMapping("/tongtool")
@RestController
public class TongToolController {

	@Autowired
	private TongToolService tongToolService;
	
	@Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;
	
	@Autowired
	private CommodityBaseMapper commodityBaseMapper;
	
	@Autowired
    private CommodityBelongSellerMapper commodityBelongSellerMapper;
	
	@Autowired
	private SellerAuthMapper sellerAuthMapper;
	
	
	@GetMapping("/checkAuth")
	@ApiOperation(value = "校验是否有授权信息", notes = "")
	public int checkAuth() {
		Long sellerId=null;
		UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	if (UserEnum.platformType.SELLER.getPlatformType().equals(user.getPlatformType())) {//卖家平台
        		if (user.getUserid() != null && user.getTopUserId() != null) {
        			if (user.getTopUserId() == 0) {//主账号
        				sellerId=Long.parseLong(String.valueOf(user.getUserid()));
        			}else {
        				sellerId=Long.parseLong(String.valueOf(user.getTopUserId()));
    				}
				}
        	}
        }
        if (sellerId==null) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "获取卖家ID失败");
		}
        SellerAuth authInfo=sellerAuthMapper.selectBySellerId(sellerId);
		if (authInfo == null || StringUtils.isBlank(authInfo.getAppKey()) || StringUtils.isBlank(authInfo.getAppSecret())) {
			return 0;
		}else {
			return 1;
		}
	}
	
	
	@PostMapping("/pushSelected")
	@ApiOperation(value = "推送选中的spu", notes = "")
	public Map<String, Object> pushToTongTool(@RequestBody List<Long> ids) {
		Map<String, Object> resultMap=null;
		Long sellerId=null;
		UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	if (UserEnum.platformType.SELLER.getPlatformType().equals(user.getPlatformType())) {//卖家平台
        		if (user.getUserid() != null && user.getTopUserId() != null) {
        			if (user.getTopUserId() == 0) {//主账号
        				sellerId=Long.parseLong(String.valueOf(user.getUserid()));
        			}else {
        				sellerId=Long.parseLong(String.valueOf(user.getTopUserId()));
    				}
				}
        	}
        }
        if (sellerId==null) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "获取卖家ID失败");
		}
		
		CommodityBase param=new CommodityBase();
		param.setIds(ids);
		List<CommodityBase> baseList=commodityBaseMapper.page(param);
		if (baseList != null && baseList.size()>0) {
			resultMap=tongToolService.pushToTongTool(baseList,sellerId);
		}else {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品不存在");
		}
		return resultMap;
	}
	
	
	@GetMapping("/pushAll")
	@ApiOperation(value = "推送所有", notes = "")
	public void pushAll() {
		List<String> limitIds=null;
		Long sellerId=null;
		UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	if (UserEnum.platformType.SELLER.getPlatformType().equals(user.getPlatformType())) {//卖家平台
        		if (user.getUserid() != null && user.getTopUserId() != null) {
        			if (user.getTopUserId() == 0) {//主账号
        				sellerId=Long.parseLong(String.valueOf(user.getUserid()));
        				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getUserid())));
        			}else {
        				sellerId=Long.parseLong(String.valueOf(user.getTopUserId()));
        				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getTopUserId())));
    				}
				}
        	}
        }
        if (sellerId==null) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "获取卖家ID失败");
		}
        
        SellerAuth authInfo=sellerAuthMapper.selectBySellerId(sellerId);
		if (authInfo == null || StringUtils.isBlank(authInfo.getAppKey()) || StringUtils.isBlank(authInfo.getAppSecret())) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请先进行通途授信息权的绑定");
		}
		
		Map<String,Object> param=new HashMap<String,Object>();
        if (limitIds != null && limitIds.size()>0) {
        	param.put("limitIds", limitIds);
		}
        param.put("belongSellerId", sellerId);
        param.put("sellerId", sellerId);
        param.put("isUp", true);
		new Thread(){
			public void run() {
				tongToolService.pushAllByPage(param);
			}
		}.start();
		
	}
	
	
}
