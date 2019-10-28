package com.rondaful.cloud.user.service;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.user.controller.model.manage.CreateSellerUserBean;
import com.rondaful.cloud.user.entity.GetSupplyChainByUserId;
import com.rondaful.cloud.user.entity.SellerUsername;
import com.rondaful.cloud.user.entity.User;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface ManageService {

	/**
	 * 管理后台登录
	 * @param username
	 * @param password
	 * @return
	 */
	public Map<String,Object> manageUserLogin(String username, String password, Integer platformType,HttpServletResponse response) throws InvocationTargetException, IllegalAccessException;

	/************************卖家管理->卖家列表************************/
	Page getSellerUser(User user,String currPage,String row);
	
	/************************卖家管理员重置密码************************/
	Integer resetPassword(Integer userId,Integer platformType,String newPassword);

	/************************供应商管理->供应商列表************************/
	Page getSupplierUser(Map<String,Object> map,String currPage,String row);
	
	/************************创建用户************************/
	Integer insertParentUser(Object object,Integer platformType);
	
	/********************管理员修改用户状态********************/
	public Integer manageAccountDisabled(List<Integer> userIds, Integer status);

	/**
	 * 忘记密码
	 * @param phone
	 * @return
	 */
	Integer getManageUserByPhoneAndPlatformType(String phone,String email,Integer platformType,String newPassword);

	/**
	 * 修改密码
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	Integer managePasswordUpdate(Integer userId, String oldPassword, String newPassword);

	/**
	 * 卖家用户名-下拉列表
	 * @return
	 */
	List<SellerUsername> getSellerUsernameList();

	/**
	 * 供应商用户名-下拉列表
	 * @return
	 */
	List<GetSupplyChainByUserId> getSupplierUsernameList(Integer delFlag);

	/**
	 * 供应链公司-下拉列表
	 * @return
	 */
	List<Map<String,Object>> getSupplyChainCompanyNameList();

	/**
	 * 设置供应链
	 * @param map
	 * @return
	 */
	Integer setSupplyChainCompany(Map<String,Object> map);

}
