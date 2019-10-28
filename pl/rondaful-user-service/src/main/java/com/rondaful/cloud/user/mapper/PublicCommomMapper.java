package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PublicCommomMapper {

    /**
     * 	根据多个用户id查询用户信息
     * @param map
     * @return
     */
    List<User> selectByPrimaryKeys(Map<String,Object> map);

	/**
	 * 注册用户
	 * 
	 * @param user 用户信息
	 * @return
	 */
	Integer insert(User user);

	/**
	 * 根据uid修改账号状态
	 * 
	 * @param map
	 * @return
	 */
	Integer updataStatus(Map<String, Object> map);

	/**
	 * 删除账号 1.将当前账号的delF根据状态更改0,改为已删除状态
	 */
	Integer delectAccount(Map<String, Object> map);

	/**
	 * 修改子账号资料
	 * 
	 * @return
	 */
	Integer updateInfo(User user);

	/**
	 * 根据uid返回对应账户的手机
	 * 
	 * @param id
	 * @return
	 */
	String getBingdingUserPhone(@Param("id") Integer id,@Param("platformType") Integer platformType);

	/**
	 * 根据父id，状态，账户名查询用户信息
	 * 
	 * @map 查询条件
	 * @return 用户数据
	 */
	List<User> getUserByMap(Map<String, Object> map);

	/**
	 * 根据父id查询用户名
	 *
	 * @param parentId
	 * @return
	 */
	List<String> getUserNamesByParentId(@Param("parentId")Integer parentId, @Param("platformType")Integer platformType);

	/**
	 * 根据条件查询用户列表
	 * 
	 * @param map 用户对象参数
	 * @return 用户列表
	 */
	List<User> findAll(Map<String,Object> map);

	/**
	 * 根据UserId & UserName查询 UserId & UserName
	 * 
	 * @param param
	 * @param paramType userId & userName
	 * @param type      0-供应商、1-卖家、2-后台
	 * @return
	 */
	List<Map<String, String>> findUserIdOrUserName(@Param("param") String[] param, @Param("paramType") String paramType,
			@Param("type") Integer type);

	/**
	 * 查询修改后的手机号码是否在该平台注册
	 * @param map
	 * @return
	 */
	Integer isPhoneReg(Map<String,Object> map);
}
