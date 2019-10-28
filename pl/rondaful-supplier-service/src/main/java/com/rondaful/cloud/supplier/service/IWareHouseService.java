package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.supplier.dto.*;
import com.rondaful.cloud.supplier.entity.CountryMap;
import com.rondaful.cloud.supplier.entity.WareHouseServiceProvider;
import com.rondaful.cloud.supplier.vo.WareHouseAuthorizeVO;
import com.rondaful.cloud.supplier.vo.WareHouseSearchVO;
import com.rondaful.cloud.supplier.vo.WareHouseSyncVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;



public interface IWareHouseService {
	
	
	/**
	 * 获取仓库服务商
	 * @return
	 */
	List<WareHouseServiceProvider>  getWareHouseList();
	
	/**
	 * 获取仓库国家
	 * @return
	 */
	List<CountryMap> getWareHouseCountry();
	
	/**
	 * 添加授权数据
	 * @param vo
	 */
	void addAuthorize(WareHouseAuthorizeVO vo);
	
	/**
	 * 修改授权数据
	 * @param vo
	 */
	void updateAuthorize(WareHouseAuthorizeVO vo);
	
	/**
	 * 修改仓库启用状态
	 * @param vo
	 */
	void updateWareHouse(WareHouseSyncVO vo);
	

	/**
	 * 查谒服务商下的授权，授权下的仓库
	 * @param vo
	 * @param wareHouseDetail  是否加载详细的仓库数据
	 * @return
	 */
	List<WareHouseServiceProviderDTO> getServiceProviderList(WareHouseSearchVO vo,boolean wareHouseDetail);
	
	/**
	 * 直接返回仓库
	 * @param userName
	 * @return
	 */
	List<WareHouseDetailDTO> getWareHouseByUser(String userName);
	
	/**
	 * 获取目的仓库
	 * @param serviceProviderId
	 * @param 是否参与权限  true 参与  false不参与
	 * @return
	 */
	List<PurposeWareHouseDTO> getPurposeWareHouse(WareHouseSearchVO vo,boolean loginFree);
	
	
	/**
	 * 获取卖家可用仓库
	 * @param userId
	 * @return
	 */
	List<HouseTypeDTO> getsHouseName(Integer userId);

	/**
	 * 根据仓库codep批量查询绑定名称
	 * @param codes
	 * @return
	 */
	List<HouseTypeDTO> getsNameByCode(List<String> codes);

	List<PurposeWareHouseDTO> getAvailableWareHouse(String type,String wareHouseCode,String countryCode,boolean loginFree);
	
	/**
	 * 供应商仓库列表返回
	 * @param vo
	 * @return
	 */
	List<WareHouseDetailDTO> getWareHouse(WareHouseSearchVO vo);
	
	/**
	 * 通过仓库code获取服务商相关数据
	 * @param wareHouseCode
	 * @return
	 */
	List<WareHouseServiceProviderDTO> getWareHouseServiceProviderByWareHouseCode(List<String> wareHouseCodeList);
	
	/**
	 * 通过仓库code获取授权信息
	 * @param wareHouseCodeList
	 * @return
	 */
	List<AuthorizeDTO> getAuthorizeByWareHouseCode(List<String> wareHouseCodeList);
	
	/**
	 * 通过服务商id获取服务商相关数据
	 * @param serviceId
	 * @return
	 */
	List<AuthorizeDTO> getWareHouseServiceProviderByServiceId(Integer serviceId);
	
	/**
	 * 通过仓库code查询授权信息
	 * @param warehouseCode
	 * @return
	 */
	AuthorizeDTO getAuthorizeByCode(String warehouseCode);

	/**
	 * 根据公司编码批量查询授权信息
	 * @param list
	 * @return
	 */
    List<AuthorizeDTO> getAuthorizeByCompanyCodeList(List<String> list);
    
    public Integer bindSuppplyChain(WareHouseServiceProvider t);
}
