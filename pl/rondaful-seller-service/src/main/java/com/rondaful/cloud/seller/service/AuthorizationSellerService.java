package com.rondaful.cloud.seller.service;

import java.util.List;
import java.util.Map;

import com.rondaful.cloud.seller.dto.EmpAccountDTO;
import org.apache.ibatis.annotations.Param;

import com.ebay.sdk.ApiException;
import com.ebay.sdk.SdkException;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.EmpowerLog;
import com.rondaful.cloud.seller.vo.EmpowerVo;

public interface AuthorizationSellerService {


	/**
	 * ebay授权数据第一次查询
	 * @param empower
	 * @return
	 */
	Integer insertObjectSelective(Empower empower);


	/**
	 * 校验有无重复授权信息
	 * @param webName
	 * @param thirdPartyName
	 * @return
	 */
	Integer checkAccount(String webName,String thirdPartyName);



	/**
	 * 通过平台， 平台账号 ， 可能再加上站点来查询授权id
	 * @param empower 授权对象，封装参数
	 * @return 授权对象
	 */
	Empower selectOneByAcount(Empower empower);

	Empower selectByPrimaryKey(String empowerId);


	/**
	 * 多条件查询授权信息
	 * @param account
	 * @param status
	 * @return
	 */
	List<Empower> selectObjectByAccount(Integer empowerId,String pinlianAccount, Integer status, String account, Integer platform,Integer pinlianId);


	List<Empower>  selectObjectByAccountDataLimit(Empower empower);
	
	
	/**
	 * 查询列表信息
	 * @param empower
	 * @return
	 */
	Page<Empower> findAll(Empower empower, String page, String row);

	/**
	 * 查询授权列表 不分页
	 * @param empower 参数信息
	 * @return 授权列表
	 */
	List<Empower> findAllNoPage(Empower empower);


	/**
	 * 查询列表信息2
	 * @param empower
	 * @return
	 */
	List<Empower> findAll(Integer platform,Integer status);



	/**
	 * 亚马逊店铺授权信息入库
	 * @param authorizationSeller
	 */
	Integer insertAuthorizationSellerAmazon(Empower empower);


	/**
	 * ebay店铺授权信息入库
	 * @param authorizationSeller
	 */
	Integer insertAuthorizationSellerEbay(Empower empower);


	/**
	 * 根据用户的id获取亚马逊的授权token
	 * @param sellerId
	 * @return
	 */
	String  getTokenBySellerId(Integer sellerId);


	/**
	 * 通过账号(店铺名称)停用
	 * @param empowerId
	 * @return
	 */
	int blockUpByAccount(String account,Integer id);

	/**
	 * 通过账号(店铺名称)停用
	 * @param empowerId
	 * @return
	 */
	int startUsingByAccount(String account,Integer id);


	/**
	 * 获取账号的sessionId
	 */
	String  getSessionID();


	String  getUrl();


	/**
	 * 获取账号的token
	 * @throws Exception
	 * @throws SdkException
	 * @throws ApiException
	 */
	String  getToken(String sessionID) throws ApiException, SdkException, Exception;


	/**
	 * 保存ebay账号的token信息
	 * @param empower
	 */
//	void insertTokenEbay(Empower empower);


	/**
	 * 过期停用
	 * @param status
	 */
	int pastDueBlockUp(String account,Integer status);


	/**
	 * 查询所有账户
	 * @return
	 */
	List<String> selectAccounts();


	/**
	 * 查询所有账户
	 * @return
	 */
	List<String> selectAllAccounts();

	/**
	 * 查询当前账户外的店铺账号
	 * @return
	 */
	List<String> selectOtherAccounts(Integer empowerId);


	/**
	 * 查询日志
	 * @return
	 */
	List<EmpowerLog> selectLogById(Integer id,String handler);


	/**
	 * 查询数据
	 * @param t
	 * @return
	 */
	Empower selectAmazonAccount(Empower t);



	/**
	 * 店铺重新授权
	 * @param empower
	 * @return
	 */
	void updateSelectiveAmazon(Empower empower);


	/**
	 * 店铺重新授权
	 * @param empower
	 * @return
	 */
	void updateSelectiveEbay(Empower empower);


	/**
	 * 授权编辑
	 * @param empower
	 */
	void editAuthorization(Empower empower);


	/**
	 * 速卖通授权
	 * @param empower
	 */
	public Integer insertAuthorizationSellerAliexpress(Empower empower);

	/**
	 * 速卖通重新授权
	 * @param empower
	 */
	public void updateSelectiveAliexpress(Empower empower);
	
	
	/**
	 * 通过品连账号查询店铺信息
	 * @param pinlianAccounts
	 * @return
	 */
	List<EmpowerVo> selectInfoByUserIds(List<Integer> pinlianIds);
	
	
	/**
	 * 通过品连查询店铺信息
	 * @param pinlianAccounts
	 * @return
	 */
	List<EmpAccountDTO> selectInfoByAccounts(List<String>  pinlianAccounts);

    /**
     *
     * @param empowerIds
     * @return
     */
	List<EmpAccountDTO> getEmpNameByIds(List<Integer>  empowerIds);


	List<Empower> getEmpowerByIds(List<Integer> ids);

	

} 

