package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.user.controller.model.provider.FinancialVerifyBean;
import com.rondaful.cloud.user.controller.model.provider.GetSupplyChinByUserIdOrUsername;
import com.rondaful.cloud.user.entity.GetSupplyChainByUserId;
import com.rondaful.cloud.user.entity.User;
import com.rondaful.cloud.user.entity.ChileUserListRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 提供接口的Mapper
 * @author Administrator
 *
 */
public interface ProviderMapper {




	/**
	 * 财务调用---验证绑定银行卡接口
	 * @return
	 */
	FinancialVerifyBean financialCallVerificationBindBankCard(@Param("userId") Integer userId);

	/**
	 * 根据用户名或者id获取对应的供应链信息
	 * @param map
	 * @return
	 */
	GetSupplyChinByUserIdOrUsername getSupplyChinByUserIdOrUsername(Map<String, Object> map);

	/**
	 * 管理后台用户下拉列表
	 * @return
	 */
	List<String> getManageUsernameList();

	/**
	 *根据穿入用户id找到与其绑定的供应链公司
	 * @param map
	 * @return
	 */
	List<GetSupplyChainByUserId> getSupplyChainByUserId(Map<String,Object> map);

	/**
	 * 根据供应商公司名称获取对应的用户id
	 * @param companyName
	 * @return
	 */
	List<Integer> getSupplierUserIdByCompanyName(List<String> companyName);
	
	/**
	 *根据公司名称获取供应商名称(主账户)
	 * @param companyName
	 * @return
	 */
	List<String> getUserNameByCompanyName(List<String> companyName);

	/**
	 * 获取供应商公司名称
	 * @return
	 */
	List<String> getSupplierName(@Param("platformType")Integer platformType);
	
	/**
	 * 根据平台获取对应的用户信息
	 * @param platformType
	 * @return
	 */
	List<User> getUserByPlatformType(@Param("platformType") Integer platformType);
	
	/**
	 * 接收List供应商id获取供应商数据
	 * @param map
	 * @return
	 */
	List<User> getSupplierUserList(Map<String,Object> map);
	
	/**
	 * 根据平台类型获取用户信息
	 * @param platformType
	 * @return
	 */
	List<ChileUserListRequest> getUserInfoByPlatformType(@Param("platformType")Integer platformType, @Param("platformType02")Integer platformType02);

	/**
	 * 接收List供应商id获取供应商数据===>根据id找到对应的用户
	 * @param userId
	 * @return
	 */
	User selectUserByUserIdProvider(@Param("userId") Integer userId);

}
