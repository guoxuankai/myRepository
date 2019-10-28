package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.user.entity.SellerInfo;
import com.rondaful.cloud.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SellerMapper {

    /**
     * 注册用户
     *
     * @param user 用户信息
     * @return
     */
   // Integer sellerInsert(User user);

    /**
     * 根据子账户的parentId找到他的父级账户
     * @param parentId
     * @return
     */
    User getSellerParentUserBySubUserParentId(@Param("parentId") Integer parentId);

    /**
     * 根据邮箱和手机号找到用户
     *
     * @param map
     * @return
     */
    User getSellerUserByPhoneOrEmail(Map<String, Object> map);

    /**
     * 根据uid返回对应的账户信息
     *
     * @param id
     * @return
     */
    User selectByPrimaryKey(@Param("id") Integer id);

    /**
     * 发送手机验证码  卖家验证当前手机号或者是否被注册过
     * @param phone
     * @return
     */
    Integer isSellerUsernameByPhoneAndEmail(@Param("phone") String phone, @Param("email") String email);

    /**
     * 忘记密码===》根据手机号码及对应平台找到对应信息
     *
     * @param map
     * @return
     */
    User getSellerUserByPhoneAndPlatformType(Map<String, Object> map);

    /**
     * 忘记密码
     * @param map
     * @return
     */
    Integer sellerUserpasswordUpadate(Map<String, Object> map);

    /**
     * 根据uid返回对应的账户信息
     *
     * @param id
     * @return
     */
    User selectSellerByPrimaryKey(@Param("id") Integer id);

    /**
     * 修改密码
     *
     * @param map
     * @return
     */
    Integer sellerPasswordUpadate(Map<String, Object> map);

    /**
     * 修改账号资料
     *
     * @return
     */
    Integer sellerUpdateInfo(User user);

    /**
     * 修改供应链公司
     * @param userId
     * @param supplyChainCompany
     * @return
     */
	//Integer updateSupplyCompany(Integer userId,String supplyChainCompany,String remarks);
    
	
	/**
	 * 卖家列表查询
	 * @param user
	 * @return
	 */
	List<User> findAll(User user);
	
	
	User selectByUserId(Integer userId);

    /**
     *判断当前卖家信息是否已经添加
     * @param platformTypes
     * @param userId
     * @return
     */
    Integer isSellerInfo(@Param("platformTypes") Integer platformTypes, @Param("userId") Integer userId);

    /**
     * 添加卖家信息
     * @param sellerInfo
     * @return
     */
    Integer insertSellerInfo(SellerInfo sellerInfo);

    /**
     * 将账户改为已经初始化财务账号
     * @param map
     * @return
     */
    Integer sellerInitResultOk(Map<String,Object> map);

    /**
     * 判断该账户有无进行财务初始化
     * @param userId
     * @return
     */
    String isInitSeller(@Param("userId") Integer userId);

    /**
     * 判断该用户是否已经激活
     * @param userId
     * @return
     */
    Integer isSellerUserDelfag(@Param("userId") Integer userId);
    
    /**
     * 卖家判断当前手机是否已经绑定
     * @param map
     * @return
     */
    Integer isPhoneSellerUser(Map<String,Object> map);

    /**
     * 卖家手机号码绑定
     * @param map
     * @return
     */
    Integer sellerPhoneBinding(Map<String,Object> map);

    /**
     * 将登陆绑定手机的开关改为true
     * @param map
     * @return
     */
    Integer isLoginBangdingPhoneTrue(Map<String,Object> map);

    /**
     *获取是否需要绑定手机信息
     * @param userId
     * @return
     */
    boolean getIsPhoneBinding(@Param("userId") Integer userId);

    /**
     * 根据子账户的parentId找到他的父级账户
     * @param parentId
     * @return
     */
    User getParentUserBySubUserParentIdSeller(@Param("parentId") Integer parentId);

}
