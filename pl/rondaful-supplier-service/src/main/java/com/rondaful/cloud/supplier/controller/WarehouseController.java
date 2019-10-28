package com.rondaful.cloud.supplier.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.supplier.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;


import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.supplier.common.GetLoginInfo;
import com.rondaful.cloud.supplier.entity.CountryMap;
import com.rondaful.cloud.supplier.entity.WareHouseServiceProvider;
import com.rondaful.cloud.supplier.entity.WarehouseOperateInfo;
import com.rondaful.cloud.supplier.entity.WarehouseSync;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.service.IWareHouseService;
import com.rondaful.cloud.supplier.service.IWarehouseOperateInfoService;
import com.rondaful.cloud.supplier.vo.AvailableVO;
import com.rondaful.cloud.supplier.vo.WareHouseAuthorizeVO;
import com.rondaful.cloud.supplier.vo.WareHouseSearchVO;
import com.rondaful.cloud.supplier.vo.WareHouseSyncVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(description = "仓库基本接口")
@RestController
@RequestMapping(value = "/warehouse")
public class WarehouseController {

	private final Logger log = LoggerFactory.getLogger(WarehouseController.class);
	@Autowired
	IWarehouseOperateInfoService warehouseService;
	
	@Autowired
	GetLoginInfo getLoginInfo;
	
	@Autowired
	private IWareHouseService  wareHouserService;

	@Autowired
	protected GetLoginUserInformationByToken userToken;
	
	@AspectContrLog(descrption = "取得可用仓库列表",actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "取得可用仓库列表", notes = "")
    @RequestMapping(value = "/getValidWarehouseList", method = RequestMethod.POST)
    public List<WarehouseSync> getValidWarehouseList(){
    	return warehouseService.getValidWarehouseList();
    }
    
	@AspectContrLog(descrption = "查询仓库列表",actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "查询仓库列表", notes = "page当前页码，row每页显示行数", response = WarehouseOperateInfo.class)
	@PostMapping("/findWarehouse")
	@RequestRequire(require = "page, row", parameter = String.class)
	@ApiImplicitParams({
        @ApiImplicitParam(name = "countryCode", value = "国家编码", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "choiceValid", value = "前端传值：1：启用，0：停用，2：全部", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "warehouseType", value = "仓库类型：0：自营，1:品连，2：第三方", dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "warehouseCode", value = "仓库编码",  required = false, dataType = "string", paramType = "query"),
        })
	public Page<WarehouseOperateInfo> findWarehouse( String page, String row,String countryCode,String choiceValid,String warehouseType,String warehouseCode) {
    	log.info("仓库查询方法开始");
//    	warehouseService.insertOperateInfo();
		Page.builder(page, row);
		WarehouseOperateInfo operateInfo=new WarehouseOperateInfo();
//		operateInfo.setSupplierId(getLoginInfo.getSupplierId());
		choiceValid = "1";
		if(StringUtils.isNotBlank(choiceValid)) {
			operateInfo.setChoiceValid(choiceValid);
		}
		
		if(StringUtils.isNotBlank(countryCode)) {
			operateInfo.setCountryCode(countryCode);
		}
		
		if(StringUtils.isNotBlank(warehouseType)) {
			operateInfo.setWarehouseType(warehouseType);
		}
		if(StringUtils.isNotBlank(warehouseCode)){
			operateInfo.setWarehouseCode(warehouseCode);
		}
		log.info("仓库查询参数"+page);
		Page<WarehouseOperateInfo> p = warehouseService.pageBySupplier(operateInfo); 
		return p;
	}
    
    @ApiOperation(value = "查询仓库操作列表", notes = "page当前页码，row每页显示行数", response = WarehouseOperateInfo.class)
  	@PostMapping("/findWarehouseOperateInfo")
  	@RequestRequire(require = "page, row", parameter = String.class)
  	@ApiImplicitParams({
          @ApiImplicitParam(name = "warehouseProvider", value = "仓库服务商", required = false, dataType = "string", paramType = "query"),
          @ApiImplicitParam(name = "supplierCompanyName", value = "供应商", required = false, dataType = "string", paramType = "query"),
          @ApiImplicitParam(name = "warehouseType", value = "仓库类型", required = false, dataType = "string", paramType = "query"),
          @ApiImplicitParam(name = "choiceValid", value = "前端传值：1：启用，0：停用，2：全部", required = true, dataType = "string", paramType = "query")})
  	public Page<WarehouseOperateInfo> findWarehouseOperateInfo(String page, String row,
  			String warehouseProvider,String supplierCompanyName,String warehouseType,String choiceValid) {
  		Page.builder(page, row);
  		WarehouseOperateInfo operateInfo=new WarehouseOperateInfo();
  		//operateInfo.setSupplierId(getLoginInfo.getSupplierId());
  		if(StringUtils.isNotBlank(choiceValid)) {
  			operateInfo.setChoiceValid(choiceValid);
  		}
  		if(StringUtils.isNotBlank(warehouseProvider)) {
  			operateInfo.setWarehouseProvider(warehouseProvider);
  		}
  		if(StringUtils.isNotBlank(supplierCompanyName)) {
  			operateInfo.setSupplierCompanyName(supplierCompanyName);
  		}
  		if(StringUtils.isNotBlank(warehouseType)) {
  			operateInfo.setWarehouseType(warehouseType);
  		}
  		Page<WarehouseOperateInfo> p = warehouseService.pageByCms(operateInfo); 
  		return p;
  	}
    
    
    @ApiOperation(value = "更新仓库状态", notes = "", response = WarehouseSync.class)
    @RequestMapping(value = "/updateWarehouseById", method = RequestMethod.POST)
	public void updateWarehouseById(@ApiParam(name = "id", value = "", required = true)@RequestParam("id")Integer id,
			@ApiParam(name = "isAvailable", value = "0:停用，1：启用", required = true)@RequestParam("isAvailable")Integer isAvailable) {
		if(id==null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id不能为空");
		//String token=request.getHeader("token");
		Map<String,String> param=new HashMap<>();
		param.put("id", String.valueOf(id));
		param.put("available", String.valueOf(isAvailable));
    	warehouseService.updateWarehouseById(param);
	}
    
    @ApiOperation(value = "根据仓库名称取得仓库信息", notes = "")
    @RequestMapping(value = "/getWarehouseInfoByParam", method = RequestMethod.POST)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "warehouseName", value = "仓库名称", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "warehouseCode", value = "仓库编码", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "companyCode", value = "客户编码", required = false, dataType = "string", paramType = "query")
       })
    public List<WarehouseSync> getWarehouseInfoByParam(String warehouseName,String warehouseCode,String companyCode){
    	Map<String, String> param=new HashMap<>();
    	if(StringUtils.isNotBlank(warehouseName)) {
    		param.put("warehouseName", warehouseName);
    	}
    	if(StringUtils.isNotBlank(warehouseCode)) {
    		param.put("warehouseCode", warehouseCode);
    	}
    	if(StringUtils.isNotBlank(companyCode)) {
    		param.put("companyCode", companyCode);
    	}
    	return warehouseService.selectWarehouseByParam(param);
    }
    
    @ApiOperation(value = "取得国家列表", notes = "")
    @RequestMapping(value = "/getCountryList", method = RequestMethod.POST)
    public List<WarehouseOperateInfo> getCountryList(){
    	return warehouseService.getCountryList();
    }
    
    @ApiOperation(value = "根据编码取得国家列表", notes = "")
    @RequestMapping(value = "/getCountryListByCode", method = RequestMethod.POST)
    public List<WarehouseOperateInfo> getCountryListByCode(String countryCode){
    	if (StringUtils.isBlank(countryCode)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "国家编码参数不能为空");
		}
    	return warehouseService.getCountryListByCode(countryCode);
    }
    
    
    @GetMapping("/api/serviceProvider/list")
    @ApiOperation("仓库服务商数据")
    @AspectContrLog(descrption = "仓库服务商数据", actionType = SysLogActionType.QUERY)
	public List<WareHouseServiceProvider> getWareHouseServiceList(){
    	return  wareHouserService.getWareHouseList();
	}
    
    @GetMapping("/api/country/list")
    @ApiOperation("调用谷仓后获取的国家数据(字段少一点,并加了缓存)")
    @AspectContrLog(descrption = "调用谷仓后获取的国家数据", actionType = SysLogActionType.QUERY)
	public List<CountryMap> getWareHouseCountry(){
      return wareHouserService.getWareHouseCountry();
	}
    
    @PostMapping("/api/addAuthorize")
    @ApiOperation("添加仓库授权")
    @AspectContrLog(descrption = "添加仓库授权", actionType = SysLogActionType.QUERY)
	public void addAuthorize(@RequestBody WareHouseAuthorizeVO vo){
    	wareHouserService.addAuthorize(vo);
	}
    
    @PostMapping("/api/updateAuthorize")
    @ApiOperation("修改仓库授权信息")
    @AspectContrLog(descrption = "修改仓库授权信息", actionType = SysLogActionType.QUERY)
	public void upateAuthorize(@RequestBody WareHouseAuthorizeVO vo){
    	wareHouserService.updateAuthorize(vo);
	}
    
    @PostMapping("/api/updateWareHouseStatus")
    @ApiOperation("修改仓库启用状态")
    @AspectContrLog(descrption = "修改仓库启用状态", actionType = SysLogActionType.QUERY)
	public void updateWareHouseStatus(@RequestBody WareHouseSyncVO vo){
    	wareHouserService.updateWareHouse(vo);
	}
    
    
    @PostMapping("/api/getServiceProviderList")
    @ApiOperation("仓库服务商列表数据")
    @AspectContrLog(descrption = "服务商关联数据", actionType = SysLogActionType.QUERY)
	public List<WareHouseServiceProviderDTO> getServiceProviderList(@RequestBody WareHouseSearchVO vo){
    	return wareHouserService.getServiceProviderList(vo,true);
	}
    
    @PostMapping("/api/getServiceProviderListByWareHouseCode")
    @ApiOperation("通过仓库code查询仓库服务商数据")
    @AspectContrLog(descrption = "通过仓库code查询仓库服务商数据", actionType = SysLogActionType.QUERY)
	public List<WareHouseServiceProviderDTO> getServiceProviderListByWareHouseCode(@RequestBody List<String> wareHouseCodeList){
    	return wareHouserService.getWareHouseServiceProviderByWareHouseCode(wareHouseCodeList);
	}
    
    @PostMapping("/api/getServiceProviderListByServiceId")
    @ApiOperation("通过服务商id查询仓库服务商数据(默认返回全部)")
    @AspectContrLog(descrption = "通过服务商id查询仓库服务商数据", actionType = SysLogActionType.QUERY)
	public List<AuthorizeDTO> getServiceProviderListByServiceId(Integer serviceId){
    	return wareHouserService.getWareHouseServiceProviderByServiceId(serviceId);
	}
    
    @GetMapping("/api/getWareHouseByUser")
    @ApiOperation("根据用户名查询仓库")
    @AspectContrLog(descrption = "根据用户名查询仓库", actionType = SysLogActionType.QUERY)
	public List<WareHouseDetailDTO> getWareHouseByUser(String userName){
    	return wareHouserService.getWareHouseByUser(userName);
	}
    
    @GetMapping("/api/getPurposeWareHouse")
    @ApiOperation("获取目的仓库")
    @AspectContrLog(descrption = "获取目的仓库", actionType = SysLogActionType.QUERY)
	public List<PurposeWareHouseDTO> getPurposeWareHouse(@RequestParam("serviceId") Integer serviceId){
    	WareHouseSearchVO vo = new WareHouseSearchVO();
    	vo.setWareHouseStatus(1);
    	if (0 != serviceId) //服务商id为0表示全部查询
    		vo.setWareHouseServiceProvider(serviceId);
    	return wareHouserService.getPurposeWareHouse(vo,true);
	}
    /**
     * 与上面的获取目的使用同一个接口
     * @param vo
     * @return
     */
    @PostMapping("/api/getWareHouseByList")
    @ApiOperation("供应商获取仓库显示")
    @AspectContrLog(descrption = "供应商获取仓库显示", actionType = SysLogActionType.QUERY)
	public List<PurposeWareHouseDTO> getWareHouse(WareHouseSearchVO vo){
    	vo.setWareHouseStatus(1);
    	return wareHouserService.getPurposeWareHouse(vo,true);
	}
    
    @GetMapping("/api/getWareHouseByList")
    @ApiOperation("获取可用仓库 [type=0 自营  type =2 谷仓  type =10 所有 ,wareHouseCdoe= 仓库编码  ,countryCode =国家编码]")
    @AspectContrLog(descrption = "获取可用仓库", actionType = SysLogActionType.QUERY)
	public List<PurposeWareHouseDTO> getAvailableWareHouse(String type,String wareHouseCode,String countryCode){
    	return wareHouserService.getAvailableWareHouse(type,wareHouseCode,countryCode,true);
	}
    
    @PostMapping("/api/getAvailableWareHouse")
    @ApiOperation("获取可用仓库,对外接口,不用登录")
    @AspectContrLog(descrption = "获取可用仓库", actionType = SysLogActionType.QUERY)
	public List<PurposeWareHouseDTO> getAvailableWareHouse(@RequestBody AvailableVO vo){
    	return wareHouserService.getAvailableWareHouse(vo.getType(),vo.getWareHouseCode(),vo.getCountryCode(),false);
	}
    

	@AspectContrLog(descrption = "根据用户名获取可用的仓库", actionType = SysLogActionType.QUERY)
	@GetMapping("/api/getsHouseNameById")
	@ApiOperation("根据用户名获取可用的仓库")
	@ApiImplicitParam(name = "userId", value = "用户id",  dataType = "Integer", paramType = "query")
	public List<HouseTypeDTO> getsHouseNameById(Integer userId){
		if (userId==null){
			userId=userToken.getUserDTO().getTopUserId();
		}
		return wareHouserService.getsHouseName(userId);
	}

	@ApiOperation(value = "根据仓库code查询绑定名称")
	@ApiImplicitParam(name = "codes", value = "仓库code json字符串",  dataType = "String", paramType = "query")
	@PostMapping("/api/getsNameByCode")
	public List<HouseTypeDTO> getsNameByCode(String codes){
		if (StringUtils.isEmpty(codes)){
			return new ArrayList<>();
		}
		return wareHouserService.getsNameByCode(JSONArray.parseArray(codes,String.class));
	}

	@AspectContrLog(descrption = "仓库服务商绑定供应链公司", actionType = SysLogActionType.UDPATE)
	@ApiOperation(value = "绑定供应链公司")
	@PostMapping("/api/bindSuppplyChain")
	@ApiImplicitParams({
	   @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "string", paramType = "query"),
	   @ApiImplicitParam(name = "supplyChainId", value = "供应链Id", required = false, dataType = "string", paramType = "query"),
	   @ApiImplicitParam(name = "supplyChainCompany", value = "供应链公司", required = true, dataType = "string", paramType = "query")})
	public void bindSuppplyChain(Integer id, Integer supplyChainId,String supplyChainCompany){
		WareHouseServiceProvider t =new WareHouseServiceProvider(); 
		if(id != null)  t.setId(id);
		if(supplyChainId != null) t.setSupplyChainId(supplyChainId);
		if(StringUtils.isNotBlank(supplyChainCompany)) t.setSupplyChainCompany(supplyChainCompany);
		Integer bindCount= wareHouserService.bindSuppplyChain(t);
		log.info("更新数量：{}",bindCount);
		if(bindCount != 1) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常");
		}
	}

}
