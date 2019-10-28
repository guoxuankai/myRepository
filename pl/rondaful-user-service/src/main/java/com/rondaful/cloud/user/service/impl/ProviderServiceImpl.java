package com.rondaful.cloud.user.service.impl;

import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.user.controller.model.provider.FinancialVerifyBean;
import com.rondaful.cloud.user.controller.model.provider.GetSupplyChinByUserIdOrUsername;
import com.rondaful.cloud.user.entity.GetSupplyChainByUserId;
import com.rondaful.cloud.user.entity.User;
import com.rondaful.cloud.user.entity.ChileUserListRequest;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.mapper.ProviderMapper;
import com.rondaful.cloud.user.mapper.SupplierMapper;
import com.rondaful.cloud.user.service.ProviderService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提供接口业务层
 * @author Administrator
 *	1.查询所有供应商的名称
 */
@Service("providerService")
public class ProviderServiceImpl implements ProviderService{

	@Autowired
	private ProviderMapper privoderMapper;
	
	@Autowired
	private SupplierMapper supplierMapper;
	
	private Logger logger = LoggerFactory.getLogger(ProviderServiceImpl.class);

	@Autowired
	private GetLoginUserInformationByToken getLoginUserInformationByToken;

	@Override
	public List<String> getUserNameByCompanyName(List<String> companyName) {
		List<String> userName = privoderMapper.getUserNameByCompanyName(companyName);
		return userName;
	}

	@Override
	public List<String> getSupplierName(Integer platformType) {
		List<String> supplierNames = privoderMapper.getSupplierName(platformType);
		if ( supplierNames == null ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435);
		return supplierNames;
	}

	@Override
	public List<User> getSupplierUserAll(Integer platformType) {
		List<User> supplierUserAll = new ArrayList<User>();//设置分页
		supplierUserAll = privoderMapper.getUserByPlatformType(platformType);//获取此平台的用户
		if ( supplierUserAll ==null ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435);
		return supplierUserAll;
	}

	/**
	 * 接收List供应商id获取供应商数据
	 * @param userIds
	 * @param platformType
	 * @return
	 */
	@Override
	public List<UserAll> getUserList(List<Integer> userIds,Integer platformType) {
		List<UserAll> userAllList = new ArrayList<UserAll>();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("userIds",userIds);
		map.put("platformType",platformType);
		List<User> users = privoderMapper.getSupplierUserList(map);
		if (users == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"获取用户信息失败，请重试");
		for (User u : users) {
			UserAll ua = new UserAll();
			UserCommon userCommon = new UserCommon();//当前的供应商账户信息转成公共包中的User对象
			try {
				BeanUtils.copyProperties(userCommon, u);
			} catch (Exception e) {
				logger.error("",e);
			} 
			ua.setUser(userCommon);
			ua.setParentUser(new UserCommon());
			if (u.getParentId() != null) {//获取当前供应商的父id
			User parentUser = privoderMapper.selectUserByUserIdProvider(u.getParentId());
			UserCommon manageUser = new UserCommon();
			try {
				BeanUtils.copyProperties(manageUser, parentUser);
			} catch (Exception e) {
				logger.error("",e);
			}
			ua.setParentUser(manageUser);
			}
			userAllList.add(ua);
		}
		return userAllList;
	}
	
	/************************根据平台类型获取用户信息************************/
	@Override
	public List<ChileUserListRequest> getUserInfoByPlatformType(Integer platformType) {
		List<ChileUserListRequest> user = null;
		Integer platformType02 = null;
		if (platformType.intValue() == 3) {
			platformType02 = platformType;
			platformType = null;
			user = privoderMapper.getUserInfoByPlatformType(platformType,platformType02);
		}else user = privoderMapper.getUserInfoByPlatformType(platformType,platformType02);
		if ( user.size() == 0 ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(),"获取用户信息失败，请重试");
		return user;
	}

	/**
	 * 财务调用---验证绑定银行卡接口
	 * @return
	 */
	@Override
	public FinancialVerifyBean financialCallVerificationBindBankCard() {
		UserAll userAll = getLoginUserInformationByToken.getUserInfo();
		FinancialVerifyBean financialVerifyBean = null;
		if (userAll != null) financialVerifyBean = privoderMapper.financialCallVerificationBindBankCard(userAll.getUser().getUserid());
		return financialVerifyBean;
	}

	/**
	 * 根据用户名或者id获取对应的供应链信息
	 * @param map
	 * @return
	 */
	@Override
	public GetSupplyChinByUserIdOrUsername getSupplyChinByUserIdOrUsername(Map<String, Object> map) {
		GetSupplyChinByUserIdOrUsername getSupplyChinByUserIdOrUsername = privoderMapper.getSupplyChinByUserIdOrUsername(map);
		return getSupplyChinByUserIdOrUsername;
	}

	/**
	 * 管理后台用户下拉列表
	 * @return
	 */
	@Override
	public List<String> getManageUsernameList() {
		List<String> manageUsernameList = privoderMapper.getManageUsernameList();
		return manageUsernameList;
	}

	/**
	 * 根据穿入用户id找到与其绑定的供应链公司
	 * @param platformtype
	 * @param userIdList
	 * @return
	 */
	@Override
	public List<GetSupplyChainByUserId> getSupplyChainByUserId(Integer platformtype, List<Integer> userIdList) {
		List<GetSupplyChainByUserId> supplyMap = new ArrayList<>();
		if (platformtype != null && userIdList != null){
			Map<String,Object> map = new HashMap<>();
			map.put("platformtype",platformtype);
			map.put("userIdList",userIdList);
			supplyMap = privoderMapper.getSupplyChainByUserId(map);
		}
		return supplyMap;
	}

	/**
	 * 根据供应商公司名称获取对应的用户id
	 * @param companyName
	 * @return
	 */
	@Override
	public List<Integer> getSupplierUserIdByCompanyName(List<String> companyName) {
		List<Integer> supplierIds = privoderMapper.getSupplierUserIdByCompanyName(companyName);
		return supplierIds;
	}

	@Override
	public User getSupplierUserBySupplierUserName(String supplierUserName,String supplierUserId,Integer platformType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("platformType",platformType);
		if (StringUtils.isNotBlank(supplierUserName))
			map.put("username", supplierUserName);
		if (StringUtils.isNotBlank(supplierUserId))
			map.put("userId", supplierUserId);
		return supplierMapper.getSupplierUserByPhoneOrEmail(map);
	
	}


}	
