package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.third.AppDTO;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.user.constants.UserConstants;
import com.rondaful.cloud.user.controller.model.provider.FinancialVerifyBean;
import com.rondaful.cloud.user.entity.*;
import com.rondaful.cloud.user.enums.UserStatusEnum;
import com.rondaful.cloud.user.model.dto.user.*;
import com.rondaful.cloud.user.service.*;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 提供接口服务
 * @author Administrator
 * 1.根据Token获取redis中的供应商登录数据
 *
 */
@Api(description = "内部调用相关接口(不想维护版)")
@RestController 
public class ProviderController {
	private Logger logger = LoggerFactory.getLogger(ProviderController.class);

	@Autowired
	private PublicCommomService publicCommomService;
	
	@Autowired
	IThirdAppService thirdAppService;

	@Autowired
	private ProviderService providerService;

	@Autowired
	private INewSupplierService supplierService;
	@Autowired
	private ISellerUserService sellerUserService;
	@Autowired
	private IManageUserService manageUserService;
	@Autowired
	private ISupplyChainUserService supplyChainUserService;
	@Autowired
	private IAreaCodeService areaCodeService;

	@ApiOperation(value ="根据传入用户id找到与其绑定的供应链公司")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "platformType",value = "用户平台：1卖家 0供应商", dataType = "string", paramType = "query",required = true ),
			@ApiImplicitParam(name = "userIdList",value = "用户平台：1卖家 0供应商", dataType = "string", paramType = "body",required = true )
	})
	@PostMapping(value = "/provider/getSupplyChainByUserId")
	public List<GetSupplyChainByUserId> getSupplyChainByUserId(String platformType, @RequestBody List<Integer> userIdList){
		List<GetSupplyChainByUserId> result=new ArrayList<>();
		for (Integer userId:userIdList) {
			GetSupplyChainByUserId dto=new GetSupplyChainByUserId();
			dto.setUserId(userId);
			switch (platformType){
				case "0":
					SupplierUserDetailDTO detailDTO=this.supplierService.getById(userId);
					if (detailDTO!=null){
						dto.setSupplyId(StringUtils.isEmpty(detailDTO.getSupplyChainCompany())?null:Integer.valueOf(detailDTO.getSupplyChainCompany()));
						dto.setSupplierCompanyName(detailDTO.getCompanyNameUser());
						dto.setSupplyChainCompanyName(detailDTO.getSupplyChainCompanyName());
					}
					result.add(dto);
					break;
				case "1":
					SellerUserDetailDTO detailDTO1=this.sellerUserService.getById(userId);
					if (detailDTO1!=null){
						dto.setSupplyId(StringUtils.isEmpty(detailDTO1.getSupplyChainCompany())?null:Integer.valueOf(detailDTO1.getSupplyChainCompany()));
						dto.setSupplierCompanyName(detailDTO1.getCompanyName());
						dto.setSupplyChainCompanyName(detailDTO1.getSupplyChainCompanyName());
					}
					result.add(dto);
					break;
				default:
					result.add(dto);
					continue;
			}

		}
		return result;
	}


	@AspectContrLog(descrption = "通过名字获取供应商", actionType = SysLogActionType.ADD)
	@ApiOperation(value = "通过名字获取供应商")
	@GetMapping(value = "/supplier/getSupplierUserBySupplierUserName")
	@Deprecated
	public User getSupplierUserBySupplierUserName(String supplierUserName,String supplierUserId,Integer platformType){
		return providerService.getSupplierUserBySupplierUserName(supplierUserName,supplierUserId, platformType);
	}

	@ApiOperation(value = "财务调用---验证绑定银行卡接口")
	@GetMapping(value = "api/financialCallVerificationBindBankCard")
	public FinancialVerifyBean financialCallVerificationBindBankCard(){
		FinancialVerifyBean financialVerifyBean = providerService.financialCallVerificationBindBankCard();
		return  financialVerifyBean;
	}




	@ApiOperation(value ="管理后台用户下拉列表")
	@GetMapping(value = "/getManageUsername")
	public List<String> getManageUsernameList(){
		return this.manageUserService.getAllName();
	}



	@ApiOperation(value ="根据供应商公司名称获取对应的用户id")
	@RequestMapping(value = "/provider/getSupplierUserIdByCompanyName", method = RequestMethod.GET)
    @Deprecated
	public List<Integer> getSupplierUserIdByCompanyName(@ApiParam(name = "companyName", value = "公司名称", required = true)@RequestParam("companyName") List<String> companyName) {
		List<Integer> supplierIds = null;
		if (companyName != null) supplierIds = providerService.getSupplierUserIdByCompanyName(companyName );
		logger.debug("获取供应商id成功");
		return supplierIds;
	}
	@ApiOperation(value ="获取供应商公司名称")
	@RequestMapping(value = "/provider/getSupplierUserName", method = RequestMethod.GET)
	public List<String> getSupplierUserName() {
		logger.debug("获取供应商公司名称");
		List<String> supplierUserNames = providerService.getSupplierName(UserConstants.SUPPLIERPLATFORM);
		logger.debug("获取供应商公司名称成功");
		return supplierUserNames;

	}

	@ApiOperation(value ="根据公司名称获取供应商名称(主账户)")
	@RequestMapping(value = "/provider/getUserNameByCompanyName", method = RequestMethod.GET)
    @Deprecated
	public List<String> getUserNameByCompanyName(@ApiParam(name = "companyName", value = "公司名称", required = true) @RequestParam("companyName") List<String> companyName){
		List<String> parentUsername = providerService.getUserNameByCompanyName(companyName);
		return parentUsername;
	}
	
	@ApiOperation(value ="获取注册供应商数据")
	@RequestMapping(value = "/provider/getSupplierUserData", method = RequestMethod.GET)
    @Deprecated
	public List<User> getSupplierUserData() {
		List<User> supplierUserRoleMenuAlls;
		supplierUserRoleMenuAlls = providerService.getSupplierUserAll(UserConstants.SUPPLIERPLATFORM);
		logger.debug("获取注册供应商数据成功");
		return supplierUserRoleMenuAlls;
	}
	


	@ApiOperation(value ="根据平台类型获取用户信息")
	@ApiImplicitParam(name = "platformType", value = "平台类型：0  供应商，1 卖家 , 3全部", dataType = "string", paramType = "query", required = true)
	@GetMapping(value = "/provider/getUserInfoByPlatformType")
	public List<ChileUserListRequest> getUserInfoByPlatformType(Integer platformType){

		List<ChileUserListRequest> result=new ArrayList<>();
		switch (platformType){
			case 1:
				result=this.getList(this.sellerUserService.getsParent(null,UserStatusEnum.ACTIVATE.getStatus()),result,platformType);
				break;
			case 0:
				result=this.getList(this.supplierService.getTopUser(null),result,platformType);
				break;
			default:
				result=this.getList(this.sellerUserService.getsParent(null,UserStatusEnum.ACTIVATE.getStatus()),result,1);
				result=this.getList(this.supplierService.getTopUser(null),result,0);

		}
		return result;
	}

	@ApiOperation(value = "根据卖家id获取upc")
	@ApiImplicitParam(name = "id", value = "卖家id", dataType = "Integer", paramType = "query", required = true)
	@GetMapping("/provider/getUpc")
	public Integer getUpc(Integer id){
		SellerUserDetailDTO userDTO=this.sellerUserService.getById(id);
		if (userDTO==null){
			return null;
		}
		return userDTO.getUpc()==null?0:userDTO.getUpc();
	}

	@ApiOperation(value = "根据供应链公司id获取信息")
	@ApiImplicitParam(name = "supplyId", value = "供应链公司id", dataType = "Integer", paramType = "query", required = true)
	@GetMapping("/provider/getSupplyById")
	public SupplyChainUserDTO getSupplyById(Integer supplyId){
		return this.supplyChainUserService.get(supplyId);
	}


	@ApiOperation(value = "根据code查询地区名")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "code", value = "地区编码", dataType = "String", paramType = "query", required = true),
			@ApiImplicitParam(name = "languageType", value = "语言编码", dataType = "String", paramType = "query")
	})
	@GetMapping("/provider/getNameByCode")
	public String getNameByCode(String code,String languageType,Integer level){
		if (StringUtils.isEmpty(code)){
			return null;
		}
		return this.areaCodeService.getNameByCode(code,languageType,level);
	}



	/************************查询卖家用户分页列表************************/
	@GetMapping("/provider/sellerUser/allUser")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
		@ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true),
		@ApiImplicitParam(name = "status", value = "账号状态 1 启用  0 停用", dataType = "Integer", paramType = "query",required = false),
		@ApiImplicitParam(name = "delFlag", value = "是否删除 默认 1 存在  0删除", dataType = "Integer", paramType = "query",required = false),
		@ApiImplicitParam(name = "isAll", value = "查询账户类型：3：全部卖家  1：主账户  2：子账户", dataType = "int", paramType = "query",required = true)})
	@RequestRequire(require = "page, row", parameter = String.class)
	@Deprecated
	public Page<User> findAllSellerUserByPage(String status, String delFlag, String page, String row,Integer isAll){
		Page.builder(page, row);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("status", Integer.parseInt(status));
		map.put("delFlag",Integer.parseInt(delFlag));
		map.put("platformType",UserConstants.SELLERPLATFORM);//设置为卖家平台
		map.put("isAll",isAll);
		return publicCommomService.findAllByPage(map);
	}




	private List<ChileUserListRequest> getList(BindAccountDTO dto, List<ChileUserListRequest> list, Integer platformType){
		if (dto!=null&& CollectionUtils.isNotEmpty(dto.getList())){
			for (BindAccountDetailDTO detailDTO:dto.getList()) {
				ChileUserListRequest request=new ChileUserListRequest(Integer.valueOf(detailDTO.getId()),detailDTO.getName(),platformType,detailDTO.getName());
				List<ChileUserListRequest> childs=new ArrayList<>();
				switch (platformType){
					case 0:
						List<NewSupplierUser> list1=this.supplierService.getChildName(Integer.valueOf(detailDTO.getId()));
						if (CollectionUtils.isEmpty(list1)){
							list.add(request);
							continue;
						}
						for (NewSupplierUser user:list1) {
							if (UserStatusEnum.ACTIVATE.getStatus().equals(user.getStatus())){
								childs.add(new ChileUserListRequest(Integer.valueOf(user.getId()),user.getUserName(),platformType,user.getLoginName()));
							}
						}
						break;
					case 1:
						List<NewSellerUser> list2=this.sellerUserService.getsChildName(Integer.valueOf(detailDTO.getId()));
						if (CollectionUtils.isEmpty(list2)){
							list.add(request);
							continue;
						}
						for (NewSellerUser user:list2) {
							if (UserStatusEnum.ACTIVATE.getStatus().equals(user.getStatus())){
								childs.add(new ChileUserListRequest(Integer.valueOf(user.getId()),user.getUserName(),platformType,user.getLoginName()));
							}
						}
						break;
					default:
						continue;
				}
				request.setChilds(childs);
				list.add(request);
			}
		}
		return list;
	}



}