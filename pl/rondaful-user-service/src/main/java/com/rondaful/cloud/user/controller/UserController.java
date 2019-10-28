package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.user.model.dto.user.ManageUserDTO;
import com.rondaful.cloud.user.model.dto.user.SellerUserDetailDTO;
import com.rondaful.cloud.user.model.dto.user.SupplierUserDetailDTO;
import com.rondaful.cloud.user.service.IManageUserService;
import com.rondaful.cloud.user.service.INewSupplierService;
import com.rondaful.cloud.user.service.ISellerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.user.service.PublicCommomService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(description = "获取userid & userName")
public class UserController {

	@Autowired
	private PublicCommomService publicCommomService;
	@Autowired
	private IManageUserService manageUserService;
	@Autowired
	private INewSupplierService supplierService;
	@Autowired
	private ISellerUserService sellerUserService;

	@ApiOperation(value = "获取UserId & UserName[此接口仅供后台调用]")
	@PostMapping(value = "/findUserIdOrUserName/{paramType}/{type}")
	public JSONObject findUserIdOrUserName(
			@ApiParam(value = "传入类型userId、userName", name = "paramType", required = true) @PathVariable String paramType,
			@ApiParam(value = "0-供应商、1-卖家、2-后台", name = "type", required = true) @PathVariable Integer type,
			@RequestBody String[] param) {
		if (param==null||param.length<1){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "查询类型不匹配");
		}
		if ("userId".equals(paramType)){
			return this.getById(type,param);
		}else if ("userName".equals(paramType)){
			return this.getByName(type,param);
		}else {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "查询类型不匹配");
		}

	}

	/**
	 * 根据名字查询id
	 * @param type
	 * @param names
	 * @return
	 */
	private JSONObject getByName(Integer type,String[] names){
		JSONObject result=new JSONObject();
		for (String name:names) {
			switch (type){
				case 0:
					SupplierUserDetailDTO supplierDTO=this.supplierService.getByName(name);
					result.put(name,supplierDTO==null?null:supplierDTO.getId());
					break;
				case 1:
					SellerUserDetailDTO sellerDTO=this.sellerUserService.getByName(name);
					result.put(name,sellerDTO==null?null:sellerDTO.getId());
					break;
				case 2:
					ManageUserDTO detailDTO=this.manageUserService.getByName(name);
					result.put(name,detailDTO==null?null:detailDTO.getId());
					break;
				default:
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "查询类型不匹配");
			}
		}
		return result;
	}

	private JSONObject getById(Integer type,String[] ids){
		JSONObject result=new JSONObject();
		for (String id:ids) {
			switch (type){
				case 0:
					SupplierUserDetailDTO supplierDTO=this.supplierService.getById(Integer.valueOf(id));
					result.put(id,supplierDTO==null?null:supplierDTO.getLoginName());
					break;
				case 1:
					SellerUserDetailDTO sellerDTO=this.sellerUserService.getById(Integer.valueOf(id));
					result.put(id,sellerDTO==null?null:sellerDTO.getLoginName());
					break;
				case 2:
					ManageUserDTO detailDTO=this.manageUserService.getById(Integer.valueOf(id),null);
					result.put(id,detailDTO==null?null:detailDTO.getLoginName());
					break;
				default:
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "查询类型不匹配");
			}
		}
		return result;
	}

}
