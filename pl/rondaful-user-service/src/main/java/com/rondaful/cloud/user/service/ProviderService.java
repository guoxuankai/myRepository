package com.rondaful.cloud.user.service;

import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.user.controller.model.provider.FinancialVerifyBean;
import com.rondaful.cloud.user.controller.model.provider.GetSupplyChinByUserIdOrUsername;
import com.rondaful.cloud.user.entity.GetSupplyChainByUserId;
import com.rondaful.cloud.user.entity.User;
import com.rondaful.cloud.user.entity.ChileUserListRequest;

import java.util.List;
import java.util.Map;

public interface ProviderService {

	/**
	 * 财务调用---验证绑定银行卡接口
	 * @return
	 */
	FinancialVerifyBean financialCallVerificationBindBankCard();

	/**
	 * 根据用户名或者id获取对应的供应链信息
	 * @param map
	 * @return
	 */
	GetSupplyChinByUserIdOrUsername getSupplyChinByUserIdOrUsername(Map<String,Object> map);

	/**
	 * 管理后台用户下拉列表
	 * @return
	 */
	List<String> getManageUsernameList();

	/**
	 * 根据穿入用户id找到与其绑定的供应链公司
	 * @param platformtype
	 * @param userIdList
	 * @return
	 */
	List<GetSupplyChainByUserId> getSupplyChainByUserId(Integer platformtype, List<Integer> userIdList);

	/**
	 * 根据供应商公司名称获取对应的用户id
	 * @param companyName
	 * @return
	 */
	List<Integer> getSupplierUserIdByCompanyName(List<String> companyName);
	
	/**
	 * 根据公司名称获取供应商名称(主账户)
	 * @param companyName
	 * @return
	 */
	List<String> getUserNameByCompanyName(List<String> companyName);

	/**
	 * 根据平台类型获取供应商数据
	 * @param platformType
	 * @return
	 */
	List<String> getSupplierName(Integer platformType);
	
	/**
	 * 获取注册供应商数据
	 * @param platformType
	 * @return
	 */
	List<User> getSupplierUserAll(Integer platformType);
	
	/**
	 * 接收List供应商id获取供应商数据
	 * @param userIds
	 * @return
	 */
	List<UserAll> getUserList(List<Integer> userIds,Integer platformType);
	
	/**
	 * 根据平台类型获取用户信息
	 * @param platformType
	 * @return
	 */
	List<ChileUserListRequest> getUserInfoByPlatformType(Integer platformType);

	/**
	 * 通过名字获取供应商
	 * @param supplierUserName
	 * @param platformType
	 * @return
	 */
	User getSupplierUserBySupplierUserName(String supplierUserName,String supplierUserId,Integer platformType);


}
