package com.rondaful.cloud.user.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.user.entity.User;

public interface PublicCommomService {

	/**
	 *绑定手机号码===》判断该账户的手机是否存在
	 * @param id
	 * @param platformType
	 * @return
	 */
    String getBingdingUserPhone(Integer id,Integer platformType);

	/**
	 * 根据uid修改用户状态
	 * @param userIds
	 * @param status
	 * @return
	 */
    Integer updataStatus(List<Integer> userIds, Integer status);
    
    /**
     * 	根据账号id删除账号
     * 	1.查询当前账号的是否是删除，根据状态更改
     * @param userIds
     * @return
     */
    Integer delectAccount(List<Integer> userIds);
 
	
	/**
	 * 增加新用户
	 * 
	 * @param user
	 * @return
	 */
	Integer insertUser(User user) throws SQLException;

	/**
	 * 根据用户名查询用户信息
	 * 
	 * @param userName
	 * @return
	 */
//	User findByName(String userName);

	/**
	 * 修改子账号资料
	 * 
	 * @param user
	 * @return
	 */
	Integer updateInfo(User user,List<Integer> roleIds);

	/**
	 * 根据父id获取对应的用户名
	 * 
	 * @param parentId
	 * @return
	 */
//	List<String> getUserByParentId(Integer parentId,Integer platformType);

	/**
	 * 查询分页的用户列表
	 * 
	 * @param map 参数对象
	 * @return 分页结果
	 */
	Page<User> findAllByPage(Map<String,Object> map);


	/**
	 * 根据UserId & UserName查询 UserId & UserName
	 * 
	 * @param param
	 * @param paramType userId & userName
	 * @param type      0-供应商、1-卖家、2-后台
	 * @return
	 */
	JSONObject findUserIdOrUserName(String[] param, String paramType, Integer type) ;

}
