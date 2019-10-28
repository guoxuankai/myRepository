package com.rondaful.cloud.commodity.controller;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.commodity.entity.SellerSkuMap;
import com.rondaful.cloud.commodity.mapper.SellerSkuMapMapper;
import com.rondaful.cloud.commodity.service.ISellerSkuMapService;
import com.rondaful.cloud.commodity.vo.SkuMapUpdateStatusVo;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;

import io.swagger.annotations.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@Api(description = "平台sku绑定")
@RestController
@RequestMapping("/skuMap")
public class SellerSkuMapController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(SellerSkuMapController.class);
    
    @Autowired
    private ISellerSkuMapService sellerSkuMapService;
    
    @Autowired
    private SellerSkuMapMapper sellerSkuMapMapper;

    @AspectContrLog(descrption = "Excel添加sku映射", actionType = SysLogActionType.ADD)
    @PostMapping("/addSkuMapsByExcel")
    @ApiOperation("Excel添加sku映射")
    public Map<String, Object> addSkuMapsByExcel(@RequestParam("files") MultipartFile[] files) {
        return sellerSkuMapService.addByExcel(files);
    }

    
    @GetMapping("/queryMaps")
    @ApiOperation(value = "查询sku映射列表", notes = "page当前页码，row每页显示行数", response = SellerSkuMap.class)
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "页数", dataType = "string", paramType = "query",required=true),
    	@ApiImplicitParam(name = "row", value = "每页行数", dataType = "string", paramType = "query",required=true),
        @ApiImplicitParam(name = "platform", value = "平台：amazon, eBay, wish, aliexpress,other", dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "platformSku", value = "平台sku", dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "plSku", value = "品连sku", dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "authorizationId", value = "店铺id", dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "startCreateTime", value = "开始创建时间", dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "endCreateTime", value = "结束创建时间", dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "status", value = "状态,1:启用  2:停用", dataType = "int", paramType = "query")
        })
    @RequestRequire(require = "page,row", parameter = String.class)
    public Page<SellerSkuMap> queryMaps(@ApiIgnore SellerSkuMap skuMap, String page, String row) {
    	Page.builder(page,row);
    	return sellerSkuMapService.page(skuMap);
    }
    
    @PostMapping("/addSkuMap")
    @ApiOperation(value = "新增sku映射", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "platform", value = "平台：amazon, eBay, wish, aliexpress,other", dataType = "string", paramType = "query",required=true),
            @ApiImplicitParam(name = "platformSku", value = "平台sku", dataType = "string", paramType = "query",required=true),
            @ApiImplicitParam(name = "authorizationId", value = "店铺id", dataType = "string", paramType = "query",required=true),
            @ApiImplicitParam(name = "skuGroup", value = "品连sku及数量组合，sku1:2|sku2:3", dataType = "string", paramType = "query",required=true)
            })
    @AspectContrLog(descrption = "新增sku映射",actionType = SysLogActionType.ADD)
    public void addSkuMap(@ApiIgnore SellerSkuMap skuMap) {
    	// 平台、平台sku、店铺ID
    	if (StringUtils.isBlank(skuMap.getPlatform()) || StringUtils.isBlank(skuMap.getPlatformSku()) || StringUtils.isBlank(skuMap.getAuthorizationId())) {
    		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
		}
		if (StringUtils.isBlank(skuMap.getSkuGroup())) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku及数量不能为空");
		}
		sellerSkuMapService.addSkuMap(skuMap);
    }
    
    @PostMapping("/updateSkuMap")
    @ApiOperation(value = "更新sku映射", notes = "")
    @ApiImplicitParams({
    	 	@ApiImplicitParam(name = "id", value = "sku映射的id", dataType = "int", paramType = "query",required=true),
            @ApiImplicitParam(name = "platform", value = "平台：amazon, eBay, wish, aliexpress,other", dataType = "string", paramType = "query",required=true),
            @ApiImplicitParam(name = "platformSku", value = "平台sku", dataType = "string", paramType = "query",required=true),
            @ApiImplicitParam(name = "authorizationId", value = "店铺id", dataType = "string", paramType = "query",required=true),
            @ApiImplicitParam(name = "skuGroup", value = "品连sku及数量组合，sku1:2|sku2:3", dataType = "string", paramType = "query",required=true),
            @ApiImplicitParam(name = "status", value = "状态，1:启用  2:停用", dataType = "Long", paramType = "query",required=true)
            })
    @AspectContrLog(descrption = "更新sku映射",actionType = SysLogActionType.UDPATE)
    public void updateSkuMap(@ApiIgnore SellerSkuMap skuMap) {
    	// 平台、平台sku、店铺ID、状态
    	if (StringUtils.isBlank(skuMap.getPlatform()) || StringUtils.isBlank(skuMap.getPlatformSku()) 
    			|| StringUtils.isBlank(skuMap.getAuthorizationId()) || skuMap.getStatus() == null) {
    		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
		}
		if (StringUtils.isBlank(skuMap.getSkuGroup())) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku及数量不能为空");
		}
		
		sellerSkuMapService.updateSkuMap(skuMap);
    }
    
    @PostMapping("/updateStatus")
    @ApiOperation(value = "更新状态", notes = "")
    @ApiImplicitParams({
    	 	@ApiImplicitParam(name = "id", value = "sku映射的id", dataType = "int", paramType = "query",required=true),
            @ApiImplicitParam(name = "status", value = "状态，1:启用  2:停用", dataType = "int", paramType = "query",required=true)
            })
    @AspectContrLog(descrption = "更新状态",actionType = SysLogActionType.UDPATE)
    public void updateStatus(SkuMapUpdateStatusVo vo) {
    	sellerSkuMapService.updateMapStatus(vo);
    }
    
    @AspectContrLog(descrption = "删除sku映射", actionType = SysLogActionType.DELETE)
    @DeleteMapping("/deleteMap/{id}")
    @ApiOperation("删除sku映射")
    public void deleteMap(@ApiParam(value = "sku映射id", name = "id", required = true) @PathVariable Long id) {
        if (id == null || id == 0) 
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        try {
            sellerSkuMapService.deleteByPrimaryKey(id);
        } catch (Exception e) {
            logger.error("删除sku映射异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }
    
    @GetMapping("/getSkuNameAndSpec")
    @ApiOperation(value = "获取sku的名称和属性", notes = "获取sku的名称和属性")
    public JSONObject getSkuNameAndSpec(@RequestParam("systemSku")String systemSku) {
    	return sellerSkuMapService.getSkuNameAndSpec(systemSku);
    }
    
    @GetMapping("/importTaskLog/export/{id}")
    @ApiOperation(value = "平台sku映射导入结果明细导出", notes = "")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "任务ID", dataType = "Long", paramType = "path",required=true) })
    public void exportLog(@PathVariable long id){
    	sellerSkuMapService.exportImportLogExcel(id, response);
    }

}
