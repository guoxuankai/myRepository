package com.rondaful.cloud.commodity.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.commodity.constant.RedisKeyConstant;
import com.rondaful.cloud.commodity.entity.GoodCangCategory;
import com.rondaful.cloud.commodity.entity.GoodCangCategoryBind;
import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.commodity.enums.WarehouseFirmEnum;
import com.rondaful.cloud.commodity.mapper.GoodCangMapper;
import com.rondaful.cloud.commodity.remote.RemoteSupplierService;
import com.rondaful.cloud.commodity.service.GoodCangService;
import com.rondaful.cloud.commodity.vo.SkuPushVo;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.Utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 推送商品服务控制层
 **/
@Api(description = "推送商品服务控制层")
@RequestMapping("/granary")
@RestController
public class GoodCangController extends BaseController {

	private final static Logger log = LoggerFactory.getLogger(GoodCangController.class);

	@Autowired
	private GoodCangService goodCangService;

	@Autowired
	private GoodCangMapper goodCangMapper;
	
	@Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;
	
	@Autowired
    private RemoteSupplierService remoteSupplierService;
	
	@Autowired
	private RedisUtils redisUtils;
	
	

	@GetMapping("/category/list")
	@ApiOperation(value = "谷仓分类列表", notes = "")
	@AspectContrLog(descrption = "查询谷仓分类列表", actionType = SysLogActionType.QUERY)
	public List<GoodCangCategory> listCategory() {
		return goodCangService.findList();
	}

	@PostMapping("/category/bind")
	@ApiOperation(value = "绑定分类映射", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pinlianCategoty3Id", value = "品连三级分类ID", dataType = "int", paramType = "query", required = true),
			@ApiImplicitParam(name = "granaryCategoty3Id", value = "谷仓三级分类ID", dataType = "int", paramType = "query", required = true) })
	@AspectContrLog(descrption = "绑定分类映射", actionType = SysLogActionType.UDPATE)
	public void bindCategory(@ApiIgnore GoodCangCategoryBind bind) {
		if (bind.getPinlianCategoty3Id() == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连三级分类ID不能为空");
		if (bind.getGranaryCategoty3Id() == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "谷仓三级分类ID不能为空");
		goodCangService.addOrUpdateCategoryBind(bind);
	}

	@GetMapping("/category/getBind")
	@ApiOperation(value = "获取分类绑定关系", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pinlianCategoty3Id", value = "品连三级分类ID", dataType = "int", paramType = "query", required = true) })
	@AspectContrLog(descrption = "获取分类绑定关系", actionType = SysLogActionType.QUERY)
	public GoodCangCategoryBind getCangCategoryBind(Integer pinlianCategoty3Id) {
		if (pinlianCategoty3Id == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连三级分类ID不能为空");
		return goodCangMapper.getCategoryBindByCategoryId(pinlianCategoty3Id);
	}

	@GetMapping("/pushRecord/list")
	@ApiOperation(value = "sku推送记录列表", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "warehouseProviderCode", value = "仓库服务商编码", dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "warehouseProviderId", value = "仓库服务商ID", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "account", value = "账号", dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "systemSku", value = "品连sku", dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "commodityName", value = "商品名称", dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "productState", value = "商品状态", dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "pushState", value = "推送结果", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "queryTimeType", value = "查询时间类型，1：创建时间，2：更新时间", dataType = "int", paramType = "query")})
	@RequestRequire(require = "page, row", parameter = String.class)
	@AspectContrLog(descrption = "sku推送记录列表", actionType = SysLogActionType.QUERY)
	public Page pushRecordList(String page, String row, String warehouseProviderCode, 
			String account, String systemSku, String commodityName, String productState, Integer pushState, Integer queryTimeType,
			String startTime, String endTime,Integer warehouseProviderId) {
		
		if (warehouseProviderId != null) {
			if (WarehouseFirmEnum.GOODCANG.getCode().hashCode()==warehouseProviderId){
				warehouseProviderCode=WarehouseFirmEnum.GOODCANG.getCode();
	        }else if (WarehouseFirmEnum.WMS.getCode().hashCode()==warehouseProviderId){
	        	warehouseProviderCode=WarehouseFirmEnum.WMS.getCode();
	        }else {
	            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"未知仓库服务商");
	        }
		}
		
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("page", page);
		param.put("row", row);
		param.put("warehouseProviderCode", warehouseProviderCode);
		param.put("systemSku", systemSku);
		param.put("productState", productState);
		param.put("pushState", pushState);
		param.put("startTime", startTime);
		param.put("endTime", endTime);
		param.put("queryTimeType", queryTimeType);
		
		if (StringUtils.isNotBlank(account)) {
			Integer accountId=(Integer) redisUtils.get(RedisKeyConstant.KEY_ACCOUNT_ID_+account.trim());
			if (accountId == null) {
				Object obj=remoteSupplierService.getFirmByName(account.trim());
				JSONObject json=(JSONObject) JSON.toJSON(obj);
				if (json != null) {
					Integer data = (Integer) json.get("data");
					if (data != null) {
						accountId=data;
						redisUtils.set(RedisKeyConstant.KEY_ACCOUNT_ID_+account.trim(), data, 86400L);//1day
					}
				}
			}
			param.put("accountId", accountId);
		}
		
		//中英文搜索
		if (isEnNameSearch()) {
			param.put("commodityNameEn", commodityName);
		}else {
			param.put("commodityName", commodityName);
		}
		
		UserDTO userDto=getLoginUserInformationByToken.getUserDTO();
        if (userDto!=null) {
        	if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDto.getPlatformType())) {//供应商平台
        		if (!userDto.getManage()) {//非主账号
        			param.put("supplierId", Long.valueOf(userDto.getTopUserId()));
    			}else {
    				param.put("supplierId", Long.valueOf(userDto.getUserId()));
				}
        	}
        }else {
			return null;
		}
		
		Page p = goodCangService.getSkuPushRecordPage(param);
		return p;
	}
	
	@GetMapping("/pushLog/list")
	@ApiOperation(value = "推送操作日志列表", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "recordId", value = "推送记录ID", dataType = "Long", paramType = "query", required = true) })
	@RequestRequire(require = "page, row", parameter = String.class)
	@AspectContrLog(descrption = "推送操作日志列表", actionType = SysLogActionType.QUERY)
	public Page pushRecordLogList(String page, String row, Long recordId) {
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("page", page);
		param.put("row", row);
		param.put("recordId", recordId);
		Page p = goodCangService.querySkuPushLog(param);
		return p;
	}
	
	
	@PostMapping("/commodity/pushToGoodCang")
	@ApiOperation(value = "推送选中的sku", notes = "")
	public Map<String, Object> pushToGoodCang(@RequestBody SkuPushVo vo) {
		return goodCangService.pushSelectedSkusToGoodCang(vo.getAccountId(), vo.getType(), vo.getSkuList());
	}
	
	@PostMapping("/push/all")
	@ApiOperation(value = "商品列表-推送全部商品", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "accountId", value = "账号Id", dataType = "Long", paramType = "query", required = true),
			@ApiImplicitParam(name = "status", value = "sku状态", dataType = "Long", paramType = "query", required = true) })
	public Map<String, Object> pushAll(Integer accountId,Integer status) {
		if (accountId==null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
		}
		return goodCangService.pushAll(accountId,status);
	}
	
	@PostMapping("/rePush/batch")
	@ApiOperation(value = "仓库商品列表-批量推送", notes = "")
	public void pushBatch(@ApiParam(name = "ids", value = "推送记录id数组，多个id以逗号隔开传递", required = true) @RequestParam("ids") List<Long> ids) {
		if (ids==null || ids.size()==0) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id不能为空");
		}
		goodCangService.pushBatch(ids);
	}
	
	@GetMapping("/serprovider/list")
	@ApiOperation(value = "获取仓库服务商", notes = "")
	@AspectContrLog(descrption = "获取仓库服务商", actionType = SysLogActionType.QUERY)
	public List<String> listSerProvider() {
		List<String> list=new ArrayList<String>();
		for (WarehouseFirmEnum firm : WarehouseFirmEnum.values()) {
			StringBuilder sBuilder=new StringBuilder();
			sBuilder.append(firm.getCode()).append(",").append(Utils.translation(firm.getName()));
			list.add(sBuilder.toString());
		}
		return list;
	}
	
}
