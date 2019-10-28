package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.EmpowerLog;
import com.rondaful.cloud.seller.vo.EmpowerSearchVO;
import io.swagger.models.auth.In;

import java.util.List;

public interface IEmpowerService {


	/**
	 * 新增店铺授权
	 * @param empower
	 * @return
	 */
	public int insertEmpower(Empower empower);

	/**
	 * 新增店铺授权
	 * @param empower
	 * @return
	 */
	public int updateEmpower(Empower empower);

	/**
	 * 店铺停用启用
	 * @param empowerId
	 * @param status
	 * @return
	 */
	public int updateEmpowerByStatus(Integer empowerId, Integer status);
	/**
	 * 查询列表信息
	 */
	public Page<Empower> getEmpowerPage(EmpowerSearchVO vo);

	/**
	 * 全部的账号id和名称
	 * @return
	 */
	List<Empower> getEmpowerAll(Integer platform,Integer status,Integer pinlianId);

	/**
	 * 授权日志查询
	 * @param id
	 * @param loginName
	 * @return
	 */
	List<EmpowerLog> getEmpowerLogById(Integer id,String loginName);

	/**
	 * 修改到期时间
	 */
	void checkEndTime();


	/**
	 * eBay获取请求url
	 * @return
	 */
	public String getEbayUrl();

	/**
	 * 店铺详情
	 * @param id
	 * @return
	 */
	Empower getEmpowerById(Integer id);

	/**
	 * 亚马逊店铺详细方法
	 * @param vo
	 * @return
	 */
	List<Empower> getEmpowerVO(EmpowerSearchVO vo);



	int insertSelective(Empower empower);

	int updateByPrimaryKeySelective(Empower record);

	int deleteByPrimaryKey(Integer empowerId);

	/**
	 * 验证账号是否重复
	 * @param account
	 * @param platform
	 * @param empowerId
	 * @return
	 */
	List<Empower> checkEmpowerAccount(String account, Integer platform,Integer empowerId);

	/**
	 * 亚马逊验证站点有没有重复
	 * @param webName
	 * @param thirdPartyName
	 * @param empowerId
	 * @return
	 */
	Integer checkAccountWebName(String webName, String thirdPartyName,Integer empowerId);

	/**
	 * 验证eBay账号是否重复
	 * @param account
	 * @param platform
	 * @param empowerId
	 * @param paypalAccount01
	 * @param paypalAccount02
	 * @return
	 */
	int checkEmpowerPaypal(String account, Integer platform,Integer empowerId,String paypalAccount01,String paypalAccount02);
	/**
	 * 日志
	 * @param empowerId
	 * @param loginName
	 * @param operation
	 */
	public void insertEmpowerLog(Integer empowerId,String loginName,String operation);

	/**
	 * MQ通知店铺快要到期
	 */
	public void sendMsgEmpower();

	/**
	 * 迁移店铺
	 * @param empowerId
	 * @param pinlianId
	 * @param pinlianAccount
	 * @param account
	 * @param platform
	 */
	void updateMigrateEmpowerRent(Integer empowerId,Integer pinlianId,String pinlianAccount,String account,Integer platform);
}
