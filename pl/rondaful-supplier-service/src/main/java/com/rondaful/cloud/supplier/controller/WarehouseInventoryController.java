package com.rondaful.cloud.supplier.controller;

import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.minlog.Log;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.supplier.common.GetLoginInfo;
import com.rondaful.cloud.supplier.entity.InventoryDynamics;
import com.rondaful.cloud.supplier.entity.WarehouseInventory;
import com.rondaful.cloud.supplier.entity.WarehouseInventoryReport;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.service.IWarehouseInventoryService;
import com.rondaful.cloud.supplier.service.IWarehouseOperateInfoService;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "库存基本接口")
@RestController
@RequestMapping(value = "/inventory")
public class WarehouseInventoryController {

	private final Logger logger = LoggerFactory.getLogger(WarehouseInventoryController.class);
	@Autowired
	GetLoginInfo getLoginInfo;
	@Autowired
	IWarehouseOperateInfoService warehouseService;
	@Autowired
	private IWarehouseInventoryService inventoryService;

	@AspectContrLog(descrption = "根据供应商同步库存",actionType = SysLogActionType.ADD)
	@ApiOperation(value = "根据供应商同步库存", notes = "")
	@RequestMapping(value = "/syncInventoryBySupplierSku", method = RequestMethod.POST)
	public void syncInventoryBySupplierSku(@RequestBody List<String> skus) {
		if(CollectionUtils.isEmpty(skus))throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求列表不能为空");
		 try {
			 logger.info("同步ERP库存供应商skus:{}",skus);
			 inventoryService.syncInventoryBySupplierSku(JSONObject.toJSONString(skus));
		 } catch (Exception e) {
				e.printStackTrace();
		} 
	}
	
	@AspectContrLog(descrption = "查询仓库库存",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "查询仓库库存", notes = "page当前页码，row每页显示行数", response = WarehouseInventory.class)
	@PostMapping("/findInventory")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "页码" , required = true, dataType = "string", paramType = "query" ),
        @ApiImplicitParam(name = "row", value = "每页显示行数", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "warehouseCode", value = "仓库编码", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "supplierSku", value = "供应商SKU", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "pinlianSku", value = "品连SKU", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "supplier", value = "供应商", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "commodityName", value = "商品库存", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "choiceStatus", value = "前端传值：1：正常，0：低于预警", required = false, dataType = "string", paramType = "query")})
	@RequestRequire(require = "page, row", parameter = String.class)
	public Page<WarehouseInventory> findInventory(HttpServletRequest request, @Validated() String page, String row, String warehouseCode, String supplierSku, String pinlianSku, String commodityName, String choiceStatus, String supplier) {
		Page.builder(page, row);
		WarehouseInventory inv=new WarehouseInventory();
		String i18n=request.getHeader("i18n");
		logger.info("当前登录平台：{}",getLoginInfo.getUserInfo().getPlatformType());
		inv.setTopFlag(getLoginInfo.getUserInfo().getTopFlag());
		logger.info("当前登录账号状态：{}",getLoginInfo.getUserInfo().getTopFlag());
		if(getLoginInfo.getUserInfo().getPlatformType()==0) {
			logger.info("供应商ID:{}",getLoginInfo.getUserInfo().getTopUserId());
			inv.setSupplierId(getLoginInfo.getUserInfo().getTopUserId());
	    	if(CollectionUtils.isNotEmpty(getLoginInfo.getUserInfo().getwCodes())){
	    		inv.setwCodes(getLoginInfo.getUserInfo().getwCodes());
	    	}
   		}
   		if(getLoginInfo.getUserInfo().getPlatformType()==2) {
   			if(StringUtils.isNotBlank(supplier)) {
   				inv.setSupplier(supplier);
   			}
   			if(CollectionUtils.isNotEmpty(getLoginInfo.getUserInfo().getSuppliers())) {
   				inv.setSupplies(getLoginInfo.getUserInfo().getSuppliers());
   			}
   			
   		}
   		if(StringUtils.isNotBlank(choiceStatus)) {
   			inv.setChoiceStatus(choiceStatus);
   		}
		if(StringUtils.isNotBlank(warehouseCode)) {
			inv.setWarehouseCode(warehouseCode);
		}
		if(StringUtils.isNotBlank(supplierSku)) {
			inv.setSupplierSku(supplierSku);
		}
		if(StringUtils.isNotBlank(pinlianSku)) {
			inv.setPinlianSku(pinlianSku);
		}
		if(StringUtils.isNotBlank(commodityName)) {
			logger.info("国际化标识{}",i18n);
			if(StringUtils.isNotBlank(i18n)) {
				inv.setCommodityNameEn(commodityName);
			}else {
				inv.setCommodityName(commodityName);
			}
			
		}
		
		Page<WarehouseInventory> p = inventoryService.page(inv);
		return p;
	}
	

    @ApiOperation(value = "仓库库存导出", notes = "")
    @RequestMapping(value = "/exportInventoryExcel", method = RequestMethod.GET)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "warehouseCode", value = "仓库编码", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "supplierSku", value = "供应商SKU", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "pinlianSku", value = "品连SKU", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "commodityName", value = "商品库存", required = false, dataType = "string", paramType = "query"), 
        @ApiImplicitParam(name = "supplier", value = "供应商", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "choiceStatus", value = "前端传值：1：正常，0：低于预警 ", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "ids", value = "ids数组，多个id以逗号隔开传递", required = false, dataType = "string", paramType = "query")})
    public void exportInventoryExcel(HttpServletRequest request,HttpServletResponse response,String warehouseCode,String supplierSku,String pinlianSku,String commodityName,String supplier, @RequestParam(value="ids",required=false) String ids,String choiceStatus) throws IOException {
    	WarehouseInventory inv = new WarehouseInventory();
    	String i18n=request.getHeader("i18n");
    	logger.info("当前登录平台：{}",getLoginInfo.getUserInfo().getPlatformType());
		inv.setTopFlag(getLoginInfo.getUserInfo().getTopFlag());
		logger.info("当前登录账号状态：{}",getLoginInfo.getUserInfo().getTopFlag());
    		if(getLoginInfo.getUserInfo().getPlatformType()==0) {
    			inv.setSupplierId(getLoginInfo.getUserInfo().getTopUserId());//
	    		if(CollectionUtils.isNotEmpty(getLoginInfo.getUserInfo().getwCodes())){
	    			inv.setwCodes(getLoginInfo.getUserInfo().getwCodes());
	    		}
    		}
    		if(getLoginInfo.getUserInfo().getPlatformType()==2) {
    			if(StringUtils.isNotBlank(supplier)) {
       				inv.setSupplier(supplier);
       			}
    			if(CollectionUtils.isNotEmpty(getLoginInfo.getUserInfo().getSuppliers())) {
    				inv.setSupplies(getLoginInfo.getUserInfo().getSuppliers());
    			}
    			
    		}
    		if(StringUtils.isNotBlank(choiceStatus)) {
       			inv.setChoiceStatus(choiceStatus);
       		}
    		if(StringUtils.isNotBlank(warehouseCode)) {
    			inv.setWarehouseCode(warehouseCode);
    		}
    		if(StringUtils.isNotBlank(supplierSku)) {
    			inv.setSupplierSku(supplierSku);
    		}
    		if(StringUtils.isNotBlank(pinlianSku)) {
    			inv.setPinlianSku(pinlianSku);
    		}
    		if(StringUtils.isNotBlank(commodityName)) {
    			logger.info("国际化标识{}",i18n);
    			if(StringUtils.isNotBlank(i18n)) {
    				inv.setCommodityNameEn(commodityName);
    			}else {
    				inv.setCommodityName(commodityName);
    			}
    		}
    		try {
    			inventoryService.exportInventoryExcel(inv,ids,response);
				
			} catch (Exception e) {
				 e.printStackTrace();
			}
    		
    }
    
  /*  @ApiOperation(value = "管理后台仓库库存导出", notes = "")
    @RequestMapping(value = "/exportInventoryExcelByCms", method = RequestMethod.GET)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "warehouseCode", value = "仓库编码", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "supplierSku", value = "供应商SKU", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "pinlianSku", value = "品连SKU", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "supplierCompanyName", value = "供应商", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "commodityName", value = "商品库存", required = false, dataType = "string", paramType = "query"), 
        @ApiImplicitParam(name = "choiceStatus", value = "前端传值：1：正常，0：低于预警", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "ids", value = "ids数组，多个id以逗号隔开传递", required = false, dataType = "string", paramType = "query")})
    public void exportInventoryExcelByCms(HttpServletRequest request,HttpServletResponse response,String warehouseCode,String supplierSku,String pinlianSku,String commodityName, @RequestParam(value="ids",required=false) String ids,String choiceStatus,String supplierCompanyName) throws IOException {
    	WarehouseInventory param = new WarehouseInventory();
    	String i18n=request.getHeader("i18n");
    	logger.info("管理后台库存导出开始");
    		param.setChoiceStatus(choiceStatus);
	    	if(StringUtils.isNotBlank(supplierCompanyName)) {
				param.setSupplierCompanyName(supplierCompanyName);
	    	}
    		if(StringUtils.isNotBlank(warehouseCode)) {
    			param.setWarehouseCode(warehouseCode);
    		}
    		if(StringUtils.isNotBlank(supplierSku)) {
    			param.setSupplierSku(supplierSku);
    		}
    		if(StringUtils.isNotBlank(pinlianSku)) {
    			param.setPinlianSku(pinlianSku);
    		}
    		if(StringUtils.isNotBlank(commodityName)) {
    			logger.info("国际化标识{}",i18n);
    			if(StringUtils.isNotBlank(i18n)) {
    				param.setCommodityNameEn(commodityName);
    			}else {
    				param.setCommodityName(commodityName);
    			}
    		}
    		logger.info("管理后台库存导出参数:{}",param);
    		try {
    			inventoryService.exportInventoryExcelByCms(param,ids,response);
				
			} catch (Exception e) {
				logger.error("管理后台库存导出异常{}",e);
				 e.printStackTrace();
			}
    		
    }*/
    
	@AspectContrLog(descrption = "查询库存动态",actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "查询库存动态", notes = "page当前页码，row每页显示行数", response = InventoryDynamics.class)
	@PostMapping("/findInventoryDynamics")
	@RequestRequire(require = "page, row", parameter = String.class)
	@ApiImplicitParams({
		 @ApiImplicitParam(name = "operateType", value = "操作类型", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "warehouseCode", value = "仓库编码", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "supplierSku", value = "供应商SKU", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "pinlianSku", value = "品连SKU", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "beginDate", value = "开始时间", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "endDate", value = "结束时间", required = false, dataType = "string", paramType = "query")})
	public Page<InventoryDynamics> findInventoryDynamics(String page, String row,String operateType,String warehouseCode,String supplierSku,String pinlianSku,String beginDate,String endDate) {
		Page.builder(page, row);
		InventoryDynamics dynamics=new InventoryDynamics();
		dynamics.setSupplierId(getLoginInfo.getUserInfo().getTopUserId());
		if(StringUtils.isNotBlank(warehouseCode)) {
			dynamics.setWarehouseCode(warehouseCode);
		}
		if(StringUtils.isNotBlank(supplierSku)) {
			dynamics.setSupplierSku(supplierSku);
		}
		if(StringUtils.isNotBlank(pinlianSku)) {
			dynamics.setSku(pinlianSku);
		}
		if(StringUtils.isNotBlank(pinlianSku)) {
			dynamics.setSku(pinlianSku);
		}
		if(StringUtils.isNotBlank(beginDate) &&  StringUtils.isNotBlank(endDate)) {
			dynamics.setBeginDate(beginDate+"00:00:00");
			dynamics.setEndDate(endDate+"23:59:59");
		}
		Page<InventoryDynamics> p = inventoryService.pageDynamics(dynamics);
		return p;
	}
    
    @AspectContrLog(descrption = "导出库存动态",actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "导出库存动态", notes = "")
    @RequestMapping(value = "/exportInventoryDynamics", method = RequestMethod.GET)
    @ApiImplicitParams({
       @ApiImplicitParam(name = "operateType", value = "操作类型", required = false, dataType = "string", paramType = "query"),
       @ApiImplicitParam(name = "warehouseCode", value = "仓库编码", required = false, dataType = "string", paramType = "query"),
       @ApiImplicitParam(name = "supplierSku", value = "供应商SKU", required = false, dataType = "string", paramType = "query"),
       @ApiImplicitParam(name = "pinlianSku", value = "品连SKU", required = false, dataType = "string", paramType = "query"),
       @ApiImplicitParam(name = "ids", value = "ids数组，多个id以逗号隔开传递", required = false, dataType = "string", paramType = "query"),
       @ApiImplicitParam(name = "beginDate", value = "开始时间", required = false, dataType = "string", paramType = "query"),
       @ApiImplicitParam(name = "endDate", value = "结束时间", required = false, dataType = "string", paramType = "query")})
    public void exportInventoryDynamics(HttpServletResponse response,String operateType,String warehouseCode,String supplierSku,String pinlianSku,String beginDate,String endDate,@RequestParam(value="ids",required=false)String ids) throws IOException {
    	InventoryDynamics dynamics = new InventoryDynamics();
    	dynamics.setSupplierId(getLoginInfo.getUserInfo().getTopUserId());
	    	if(StringUtils.isNotBlank(warehouseCode)) {
				dynamics.setWarehouseCode(warehouseCode);
			}
			if(StringUtils.isNotBlank(supplierSku)) {
				dynamics.setSupplierSku(supplierSku);
			}
			if(StringUtils.isNotBlank(pinlianSku)) {
				dynamics.setSku(pinlianSku);
			}
			if(StringUtils.isNotBlank(pinlianSku)) {
				dynamics.setSku(pinlianSku);
			}
			if(StringUtils.isNotBlank(beginDate) &&  StringUtils.isNotBlank(endDate)) {
				dynamics.setBeginDate(beginDate+"00:00:00");
				dynamics.setEndDate(endDate+"23:59:59");
			}
			
    		try {
    			inventoryService.exportInventoryDynamics(dynamics,ids,response);
				
			} catch (Exception e) {
				 e.printStackTrace();
			}
    		
    }
    
    @AspectContrLog(descrption = "批量更新预警值",actionType = SysLogActionType.UDPATE)
    @ApiOperation(value = "批量更新预警值", notes = "")
	@RequestMapping(value = "/updateBatchWarnVal", method = RequestMethod.POST)
	public void updateBatchWarnVal(@ApiParam(name = "ids", value = "ids数组，多个id以逗号隔开传递", required = true)@RequestBody List<Integer> ids,
			@ApiParam(name = "warnVal", value = "预警值", required = true)@RequestParam("warnVal")Integer warnVal) {
    	if (ids.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "ids不能为空");
    	Map<String, Object> param=new HashMap<>();
		param.put("ids", ids);
		param.put("warnVal",  warnVal);
    	if(warnVal !=null) {
			inventoryService.updateBatchWarnVal(param);
		}
		
	}
    
    @AspectContrLog(descrption = "订单发货仓库库存",actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "订单发货仓库库存", notes = "")
    @RequestMapping(value = "/getInventoryListBySkus", method = RequestMethod.POST)
    public  Map<String,Object> getInventoryListBySkus(@ApiParam(name = "skus", value = "skus数组，多个sku以逗号隔开传递", required = true)@RequestBody List<String> skus) throws IOException {
    	if (CollectionUtils.isEmpty(skus)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "参数不能为空");
    		return inventoryService.getInventoryListBySkus(skus);
    }
   
    @ApiOperation(value = "订单拆合仓库库存", notes = "")
    @RequestMapping(value = "/getInventoryListByParams", method = RequestMethod.POST)
    public  Map<String,Object> getInventoryListByParams(@ApiParam(name = "invList", value = "skus数组，多个sku以逗号隔开传递", required = true)@RequestBody List<WarehouseInventory> invList) throws IOException {
    	if (CollectionUtils.isEmpty(invList)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "参数不能为空");
    		return inventoryService.getInventoryListByParams(invList);
    }
    
    @ApiOperation(value = "取得库存可用数", notes = "")
	@RequestMapping(value = "/getInventoryAvailableQty", method = RequestMethod.POST)
	public List<WarehouseInventory> getInventoryAvailableQty(@ApiParam(name = "skus", value = "skus数组，多个sku以逗号隔开传递", required = true)@RequestBody List<String> skus) {
		if (CollectionUtils.isEmpty(skus)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "skus不能为空");
		try {
			Map<String, Object> param =new HashMap<>();
			param.put("skus", skus);
			//TODO 此處修改
			try {
				param.put("supplierId",getLoginInfo.getUserInfo().getTopUserId());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("获取供应商ID出错。。。"+e);
			}
			List<WarehouseInventory> availableQty = inventoryService.getAvailableQty(param);
            return availableQty;
		}catch (Exception e){
			logger.error("通过sku列表查询库存失败",e);
			if(e instanceof GlobalException)
				throw  (GlobalException)e;
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500," 查询库存失败");
		}
	}
    
    
    @AspectContrLog(descrption = "根据供应商id查询库存商品报表统计",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "查询库存商品报表统计", notes = "")
	@PostMapping("/selectInvCommidtyReport")
	public WarehouseInventoryReport selectInvCommidtyReport() {
    	WarehouseInventoryReport invReport =null;
		try {
			invReport= inventoryService.getInvCommidtyReport();
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return invReport;
	}
    
}
