package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface SupplierMapper {

	/**
	 * 供应商登录===》 根据邮箱和手机号找到用户
	 *
	 * @param map
	 * @return
	 */
	User getSupplierUserByPhoneOrEmail(Map<String, Object> map);

	/**
	 * 供应商登录===》 根据子账户的parentId找到他的父级账户
	 * @param parentId
	 * @return
	 */
	User getSupplierParentUserBySubUserParentId(@Param("parentId") Integer parentId);

	/**
	 * 个人中心===》根据账号id获取用户信息
	 * @param id
	 * @return
	 */
	User getSupplierUserById(@Param("id") Integer id);

	/**
	 * 发送手机验证码  供应商验证当前手机号或者是否被注册过
	 * @param phone
	 * @return
	 */
	Integer isSupplierUsernameByPhoneAndEmail(@Param("phone") String phone, @Param("email") String email);

	/**
	 * 忘记密码===》根据手机号码及对应平台找到对应信息
	 *
	 * @param map
	 * @return
	 */
	User getSupplierUserByPhoneAndPlatformType(Map<String, Object> map);

	/**
	 * 修改密码
	 *
	 * @param map
	 * @return
	 */
	Integer supplierPasswordUpadate(Map<String, Object> map);

	/**
	 * 根据id找到用户
	 * @param id
	 * @return
	 */
	User selectSupplierByPrimaryKey(@Param("id") Integer id);

	/**
	 * 忘记密码
	 *
	 * @param map
	 * @return
	 */
	Integer supplierUserPasswordUpadate(Map<String, Object> map);

	/**
	 * 修改账号资料
	 *
	 * @return
	 */
	Integer supplierUpdateInfo(User user);

	/**
	 * 判断该账户有无进行财务初始化
	 * @param userId
	 * @return
	 */
	String isInitSupplier(@Param("userId") Integer userId);

	/**
	 * 将账户改为已经初始化财务账号
	 * @param map
	 * @return
	 */
	Integer supplierInitResultOk(Map<String,Object> map);

	/**
	 * 判断该供应商用户是否已经激活
	 * @param userId
	 * @return
	 */
	Integer isSupplierUserDelfag(@Param("userId") Integer userId);

}
