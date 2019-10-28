package com.rondaful.cloud.commodity.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.commodity.dto.PageDTO;
import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.commodity.mapper.CommodityBelongSellerMapper;
import com.rondaful.cloud.commodity.remote.RemoteUserService;
import com.rondaful.cloud.commodity.service.ProviderApiService;
import com.rondaful.cloud.commodity.vo.ApiCategoryResponseVo;
import com.rondaful.cloud.commodity.vo.ApiProductQueryVo;
import com.rondaful.cloud.commodity.vo.ApiQueryBaseVo;
import com.rondaful.cloud.commodity.vo.ApiSpuResponse;
import com.rondaful.cloud.common.annotation.OpenAPI;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.RemoteUtil;



/**
* @Description:对外公司接口
* @author:范津 
* @date:2019年8月3日 下午4:51:09
 */
@RequestMapping("/third")
@RestController
public class ProviderThirdController {
	private final static Logger log = LoggerFactory.getLogger(ProviderThirdController.class);
	
	@Autowired
    private ProviderApiService providerApiService;
	
	@Autowired
    private RemoteUserService remoteUserService;
	
	@Autowired
	private CommodityBelongSellerMapper commodityBelongSellerMapper;
	
	
	@OpenAPI
	@PostMapping("/app/product/list")
    public PageDTO<ApiSpuResponse> productList(ApiProductQueryVo vo,String appId) {
		Long belongSellerId = 0L;
		boolean is981=false;
		List<String> limitIds=null;
		List<Integer> userIds=null;
		List<Map> binds=null;
        RemoteUtil.invoke(remoteUserService.getByAppKey(appId));
		Map remoteMap = RemoteUtil.getMap();
		if (remoteMap != null) {
			binds=(List<Map>) remoteMap.get("binds");
		}
		if (binds != null && binds.size()>0) {
			userIds=new ArrayList<Integer>();
			for (Map bind : binds) {
				if ((Integer)bind.get("bindType")==1) {
					userIds.addAll((List)bind.get("bindCode"));
				}
			}
		}
		if (userIds != null && userIds.size()>0) {
			limitIds=new ArrayList<String>();
			for (Integer userId : userIds) {
				if (userId==981) {
					is981=true;
					belongSellerId=981L;
				}
				List<String> limList=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(userId)));
				if (limList != null && limList.size()>0) {
					limitIds.addAll(limList);
				}
			}
		}
		
		PageDTO<ApiSpuResponse> result=new PageDTO<ApiSpuResponse>();
		log.info("外部调用商品api接口参数===>{}", JSON.toJSON(vo).toString());
		if (StringUtils.isBlank(vo.getPage())) {
			throw new GlobalException(ResponseCodeEnum.SP_200402);
		}
		if (StringUtils.isBlank(vo.getRow())) {
			throw new GlobalException(ResponseCodeEnum.SP_200403);
		}
        Map<String,Object> param=new HashMap<String,Object>();
        param.put("page", vo.getPage());
        param.put("row", vo.getRow());
        param.put("category_level_1", vo.getCategoryLevel1());
        param.put("category_level_2", vo.getCategoryLevel2());
        param.put("category_level_3", vo.getCategoryLevel3());
        param.put("systemSku", vo.getSystemSku());
        param.put("SPU", vo.getSpu());
        param.put("vendibilityPlatform", vo.getVendibilityPlatform());
        param.put("freeFreight", vo.getFreeFreight());
        param.put("commodityNameEn", vo.getCommodityNameEn());
		param.put("commodityNameCn", vo.getCommodityNameCn());
		if (limitIds != null && limitIds.size()>0) {
        	param.put("limitIds", limitIds);
		}
		param.put("belongSellerId", belongSellerId);
    	
		Page<ApiSpuResponse> p = null;
    	try {
    		p = providerApiService.getSpuList(param);
    		if (p != null) {
    			result.setCurrentPage(Long.parseLong(vo.getPage()));
    			result.setTotalCount(p.getPageInfo().getTotal());
    			result.setList(p.getPageInfo().getList());
			}
    		
		} catch (Exception e) {
			throw new GlobalException(ResponseCodeEnum.SP_200401);
		}
        return result;
    }
	
	
	@OpenAPI
	@PostMapping("/app/category/list")
    public PageDTO<ApiCategoryResponseVo> listCategory(ApiQueryBaseVo vo) {
		log.info("外部调用商品分类api接口参数===>{}",JSON.toJSON(vo).toString());
		PageDTO<ApiCategoryResponseVo> result=new PageDTO<ApiCategoryResponseVo>();
		if (StringUtils.isBlank(vo.getPage())) {
			throw new GlobalException(ResponseCodeEnum.SP_200402);
		}
		if (StringUtils.isBlank(vo.getRow())) {
			throw new GlobalException(ResponseCodeEnum.SP_200403);
		}
        Page p = null;
        try {
        	Page.builder(vo.getPage(), vo.getRow());
			p = providerApiService.getCategoryList();
			if (p != null) {
    			result.setCurrentPage(Long.parseLong(vo.getPage()));
    			result.setTotalCount(p.getPageInfo().getTotal());
    			result.setList(p.getPageInfo().getList());
			}
		} catch (Exception e) {
			throw new GlobalException(ResponseCodeEnum.SP_200401);
		}
        return result;
    }
	

}
