package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.user.entity.GetSupplyChainByUserId;
import com.rondaful.cloud.user.entity.SellerUsername;
import com.rondaful.cloud.user.entity.SupplyChainCompanyListBean;
import com.rondaful.cloud.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ManageMapper {

	/**
	 * 管理员重置密码===>根据id找到对应的卖家
	 * @param userId
	 * @param platformType
	 * @return
	 */
	User selectUserByUserId(@Param("userId") Integer userId, @Param("platformType") Integer platformType);

	/************************卖家管理->卖家列表************************/
	List<User> getSellerUsers(User user);
	
	/************************卖家管理员重置密码************************/
	Integer passwordUpdate(Map<String,Object> map);
	
	/************************供应商管理->供应商列表************************/
	List<SupplyChainCompanyListBean> getSupplierUser(Map<String,Object> map);

	/**
	 * 创建主用户
	 * @param user
	 * @return
	 */
	Integer insertParentUser(User user);

	/**
	 * 卖家用户名-下拉列表
	 * @return
	 */
	List<SellerUsername> getSellerUsernameList();

	/**
	 *供应商用户名-下拉列表
	 * @return
	 */
	List<GetSupplyChainByUserId> getSupplierUsernameList(@Param("delFlag") Integer delFlag);

	/**
	 *供应链公司-下拉列表
	 * @return
	 */
	List<HashMap<String, Object>> getSupplyChainCompanyNameList();

	/**
	 * 忘记密码===》根据手机号码及对应平台找到对应信息
	 *
	 * @param map
	 * @return
	 */
	User getManageUserByPhoneAndPlatformType(Map<String, Object> map);

	/**
	 * 忘记密码 ===>修改
	 *
	 * @param map
	 * @return
	 */
	Integer manageUserpasswordUpadate(Map<String, Object> map);

	/**
	 * 根据uid返回对应的账户信息
	 * @param id
	 * @return
	 */
	User selectManageByPrimaryKey(@Param("id") Integer id);

	/**
	 * 修改密码
	 *
	 * @param map
	 * @return
	 */
	Integer mamageUserpasswordUpadate(Map<String, Object> map);

	/**
	 * 修改账号资料
	 *
	 * @return
	 */
	Integer manageUpdateInfo(User user);

	/**
	 * 根据邮箱和手机号找到用户
	 *
	 * @param map
	 * @return
	 */
	User getManageUserByPhoneOrEmail(Map<String, Object> map);

	/**
	 * 查询供应链公司状态
	 * @param supplyId
	 * @return
	 */
	Integer selectSupplyChainCompanyStatus(@Param("supplyId") Integer supplyId);

	/**
	 *与供应链公司进行绑定关系
	 * @param map
	 * @return
	 */
	Integer userAndSupplyChainBinding(Map<String,Object> map);

	/**
	 * 供应商列表===》获取供应链公司名称
	 * @param supplyId
	 * @return
	 */
	String getSupplyChainCompanyName(@Param("supplyId") Integer supplyId);

	/**
	 * 设置供应链===》更新user表中的供应链公司
	 * @param map
	 * @return
	 */
	Integer updateUserSupplyChainCompany(Map<String, Object> map);

	/**
	 * 设置供应链===》更新关联表中的数据
	 * @param map
	 * @return
	 */
	Integer updateuserOrSupply(Map<String,Object> map);

	/**
	 * 设置供应链===》 判断当前用户是否已经有和供应链的关联数据
	 * @param map
	 * @return
	 */
	Integer isUserAndSupplyData(Map<String,Object> map);

	/**
	 * 根据子账户的parentId找到他的父级账户
	 * @param parentId
	 * @return
	 */
	User getParentUserBySubUserParentIdManage(@Param("parentId") Integer parentId);

}
