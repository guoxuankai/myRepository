package com.rondaful.cloud.supplier.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.granary.GranaryUtils;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.UserUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.dto.*;
import com.rondaful.cloud.supplier.entity.CountryMap;
import com.rondaful.cloud.supplier.entity.WareHouseAuthorize;
import com.rondaful.cloud.supplier.entity.WareHouseServiceProvider;
import com.rondaful.cloud.supplier.entity.WarehouseSync;
import com.rondaful.cloud.supplier.enums.WareHouse;
import com.rondaful.cloud.supplier.mapper.CountryMapMapper;
import com.rondaful.cloud.supplier.mapper.WareHouseAuthorizeMapper;
import com.rondaful.cloud.supplier.mapper.WareHouseServiceProviderMapper;
import com.rondaful.cloud.supplier.mapper.WarehouseSyncMapper;
import com.rondaful.cloud.supplier.rabbitmq.WareHouseSender;
import com.rondaful.cloud.supplier.service.IWareHouseService;
import com.rondaful.cloud.supplier.vo.UserInfoVO;
import com.rondaful.cloud.supplier.vo.WareHouseAuthorizeVO;
import com.rondaful.cloud.supplier.vo.WareHouseSearchVO;
import com.rondaful.cloud.supplier.vo.WareHouseSyncVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 非自营仓库
 * @author songjie
 *
 */
@Service
public class WareHouseServiceImpl implements IWareHouseService {
	
	private final static Logger log = LoggerFactory.getLogger(WareHouseServiceImpl.class);
	
	@Value("${wsdl.url}")
	private String wsdlUrl;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private WareHouseServiceProviderMapper wareHouseServiceProviderMapper;
	
	@Autowired
	private CountryMapMapper countryMapMapper;
	
	@Autowired
	private WareHouseAuthorizeMapper authorizeMapper;

	@Autowired
	private UserUtils userUtils;
	
	@Autowired
	private WarehouseSyncMapper houseSyncMapper;
	
	@Autowired
	private WareHouseSender wareHouseSender;
	
	@Autowired
	GetLoginUserInformationByToken getLoginUserInformationByToken;

	@Autowired
	GranaryUtils granaryUtils;
	
	@Override
	public List<WareHouseServiceProvider> getWareHouseList() {
		 List<WareHouseServiceProvider> serviceList = wareHouseServiceProviderMapper.selectByParamValue(new WareHouseServiceProvider());
		 serviceList.forEach(s->{
			 s.setServiceProviderName(Utils.translation(s.getServiceProviderName()));
		 });
		return serviceList;
		
	}
	
	/**
	 * 绑定供应链信息
	* @Title: bindSuppplyChain
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param     参数
	* @return void    返回类型
	* @throws
	 */
	@Override
	public Integer bindSuppplyChain(WareHouseServiceProvider t) {
		return wareHouseServiceProviderMapper.updateByPrimaryKey(t);
	}

	@Override
	public List<CountryMap> getWareHouseCountry() {
		String key = "CountryMap";
		//先redis查
		List<CountryMap> list = (List<CountryMap>) redisUtils.get(key);
		if (CollectionUtils.isEmpty(list)){
			Page.builder("1", "300");
			CountryMap country = new CountryMap();
			list = countryMapMapper.page(country);
			if (CollectionUtils.isNotEmpty(list)){
				redisUtils.set(key, list);
			}
		}
		return list;
	}

	@Override
	public void addAuthorize(WareHouseAuthorizeVO vo) {
		String company_code = check(vo);
		WareHouseAuthorize authorize = new WareHouseAuthorize();
		authorize.setAppKey(vo.getAppKey());
		authorize.setAppToken(vo.getAppToken());
		List<WareHouseAuthorize> page = authorizeMapper.page(authorize);
		if (CollectionUtils.isNotEmpty(page)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "此帐户已授权");
		}
		authorize.setCustomName(vo.getCustomName());
		authorize.setServiceId(vo.getServiceId());
		authorize.setCreateTime(new Date());
		authorize.setUpdateTime(new Date());
		authorize.setCompanyCode(company_code);
		authorize.setCreateUser(userUtils.getUser().getUsername());
		authorizeMapper.insertSelective(authorize);
		getAuthorizeWarehouse(company_code, authorize);
	}

	/**
	 * 拉取用户授权后谷仓对应的仓库
	 * @param company_code
	 * @param authorize
	 */
	public void getAuthorizeWarehouse(String company_code,WareHouseAuthorize authorize){
		boolean flag = true;
		int page =1;
		List<WarehouseSync> synAllList = new ArrayList<WarehouseSync>();
		while(flag){
			String returnValue = getCallServiceValue(authorize, page);
			JSONObject parseObject = JSONObject.parseObject(returnValue);
			String ask = parseObject.getString("ask");
			if ("Failure".equals(ask)){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, parseObject.getString("message"));
			}
			flag = false;
			//判断获取到的数据是否还有下一页的数据，如果有。则继续加载
			if ("true".equals(parseObject.getString("nextPage"))){
				flag = true;
				page++;
			}
			String data = parseObject.getString("data");
			List<GetWarehouseDTO> parseArray = JSONObject.parseArray(data, GetWarehouseDTO.class);
			List<WarehouseSync> synList = new ArrayList<WarehouseSync>();
			parseArray.forEach(p ->{
				WarehouseSync sync = new WarehouseSync();
				sync.setCountryCode(p.getCountry_code());
				sync.setCreateDate(new Date());
				sync.setStatus(false);
				sync.setWarehouseCode("GC_"+company_code+"_"+p.getWarehouse_code()); //组成特殊code用来区别唯一性
				sync.setWarehouseName(p.getWarehouse_name());
				sync.setWarehouseNameEn(p.getWarehouse_name());
				sync.setWarehouseType(String.valueOf(WareHouse.WareHouseTypeEnum.HouseType_2.getCode()));
				sync.setWarehouseProvider(WareHouse.WareHouseProviderEnum.HouseProviderType_1.getMsg());
				sync.setCompanyCode(company_code);
				synList.add(sync);
				synAllList.add(sync); //将所有的数据
			});
			houseSyncMapper.syncWarehouse(synList);
		}
		//授权成功后发送数据到队列
		wareHouseSender.wareHouseAuthorize(new JSONObject() {
			{
				put("appToken",authorize.getAppToken());
				put("appKey", authorize.getAppKey());
				put("wareHouseList", synAllList);
			}
		}.toString());
	}
	
	/**
	 * 请求谷仓接口调用,每次默认10条数据加载
	 * @param authorize
	 * @param page
	 * @return
	 */
	public String getCallServiceValue(WareHouseAuthorize authorize,int page){
		String serviceName = "getWarehouse";
		HashMap<String, Object> hashMap = new HashMap<String,Object>();
	    hashMap.put("pageSize", 10);
		hashMap.put("page", page);
		String paramJson = JSONObject.toJSONString(hashMap);
		String returnValue;
		try {
			returnValue = granaryUtils.getInstance(authorize.getAppToken(), authorize.getAppKey(), wsdlUrl, paramJson, serviceName).getCallService();
		} catch (Exception e) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取谷仓仓库失败");
		}
		JSONObject parseObject = JSONObject.parseObject(returnValue);
		String ask = parseObject.getString("ask");
		if ("Failure".equals(ask)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, parseObject.getString("message"));
		}
		return returnValue;
	}
	
	
	@Override
	public void updateAuthorize(WareHouseAuthorizeVO vo) {
		if (vo.getId() == null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "编辑对像id不能为空");
		}
		if (StringUtils.isNotBlank(vo.getCustomName()) && vo.getCustomName().length()>50)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "自定义名称不能超过50个字符");
		WareHouseAuthorize t = new WareHouseAuthorize();
		t.setId(vo.getId());
		if (null != vo.getStatus())
			t.setStatus(vo.getStatus());
		if (StringUtils.isNotBlank(vo.getCustomName()))
			t.setCustomName(vo.getCustomName());
		t.setUpdateTime(new Date());
		t.setUpdateUser(userUtils.getUser().getUsername());
		authorizeMapper.updateByPrimaryKeySelective(t);
	}
	
	
	public String check(WareHouseAuthorizeVO vo){
		if (StringUtils.isBlank(vo.getAppKey()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "appKey不能为空!");
		if (StringUtils.isBlank(vo.getAppToken()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "appToken不能为空!");
		if (StringUtils.isNotBlank(vo.getCustomName()) && vo.getCustomName().length()>50)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "自定义名称不能超过50个字符");
		if (null == vo.getServiceId())
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "serviceId不能为空");
		String serviceName ="getAccount";
		String returnValue;
		try {
			returnValue = granaryUtils.getInstance(vo.getAppToken(), vo.getAppKey(), wsdlUrl, null, serviceName).getCallService();
		} catch (Exception e) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "验证appToken,appKey失败");
		}
		JSONObject parseObject = JSONObject.parseObject(returnValue);
		String ask = parseObject.getString("ask");
		if ("Failure".equals(ask)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, parseObject.getString("message"));
		}
		Object object = parseObject.get("data");
		JSONObject companyObject = JSONObject.parseObject(JSONObject.toJSONString(object));
		//获取返回的客户编码,用来进行后续的数据关联和仓库code编码自定生成。
		return companyObject.getString("company_code");
	}

	@Override
	public List<WareHouseServiceProviderDTO> getServiceProviderList(WareHouseSearchVO vo,boolean wareHouseDetail) {
		WareHouseServiceProvider serviceProvider = new WareHouseServiceProvider();
		if (vo.getWareHouseServiceProvider() != null){
			serviceProvider.setId(vo.getWareHouseServiceProvider());
		}
		List<WareHouseServiceProviderDTO> serviceProviderDTOList = new ArrayList<WareHouseServiceProviderDTO>();
		//查询所有的服务商
		List<WareHouseServiceProvider> serviceProvidcer = wareHouseServiceProviderMapper.selectByParamValue(serviceProvider);
		if (CollectionUtils.isNotEmpty(serviceProvidcer)){
			serviceProvidcer.forEach(service ->{
				List<WareHouseAuthorizeDTO> authorizeDTOList = new ArrayList<WareHouseAuthorizeDTO>();
				WareHouseAuthorize wareHouseAuthorize = new WareHouseAuthorize();
				wareHouseAuthorize.setServiceId(service.getId());
					//获取这个服务商下的授权用户信息
					List<WareHouseAuthorize> authorizeList = authorizeMapper.page(wareHouseAuthorize);
					if (CollectionUtils.isNotEmpty(authorizeList)){
						authorizeList.forEach(author ->{
							WareHouseAuthorizeDTO dto = new WareHouseAuthorizeDTO();
							dto.setAuthorizeId(author.getId());
							dto.setAuthorizeStatus(author.getStatus());
							dto.setCompanyCode(author.getCompanyCode());
							dto.setCustomName(author.getCustomName());
							dto.setAppKey(author.getAppKey());
							dto.setAppToken(author.getAppToken());
							//是否需要显示详细数据
							if (wareHouseDetail){
								//获取授权用户下的仓库
								List<WareHouseDetailDTO> houseList = getWareHouseDetailDTOList(vo,author.getCompanyCode());
								houseList.forEach(s ->{
									s.setWareHouseName(Utils.translation(s.getWareHouseName()));
									s.setCountryName(Utils.translation(s.getCountryName()));
								});
								dto.setWareHouseDetailDTOList(houseList);
							}
							authorizeDTOList.add(dto);
						});
					}
					WareHouseServiceProviderDTO providerDTO = new WareHouseServiceProviderDTO();
					providerDTO.setAuthorizeList(authorizeDTOList);
					providerDTO.setServiceId(service.getId());
					providerDTO.setServiceProviderName(service.getServiceProviderName());
					providerDTO.setServiceProviderName(Utils.translation(service.getServiceProviderName()));
					providerDTO.setSupplyChainId(service.getSupplyChainId());
					providerDTO.setSupplyChainCompany(Utils.translation(service.getSupplyChainCompany()));
					serviceProviderDTOList.add(providerDTO);
			});
		}
		return serviceProviderDTOList;
	}

	/**
	 * 获取仓库
	 * @param vo
	 * @param companyCode
	 * @return
	 */
	public List<WareHouseDetailDTO> getWareHouseDetailDTOList(WareHouseSearchVO vo,String companyCode){
		WarehouseSync sync = new WarehouseSync();
		sync.setCompanyCode(companyCode);
		if (StringUtils.isNotEmpty(vo.getWareHouseCountry())){
			sync.setCountryCode(vo.getWareHouseCountry());
		}
		if (null != vo.getWareHouseStatus()){
			//sync.setStatus(vo.getWareHouseStatus() ==1?true:false);
			sync.setStatusStr(String.valueOf(vo.getWareHouseStatus()));
		}
		if (StringUtils.isNotEmpty(vo.getWareHouseName())){
			sync.setWarehouseName(vo.getWareHouseName());
		}
		if (CollectionUtils.isNotEmpty(vo.getWareHouseCode())){
			sync.setWarehouseCodeList(vo.getWareHouseCode());
		}
		List<WareHouseDetailDTO> wareHouseDetailDTOList = new ArrayList<WareHouseDetailDTO>();
		List<WarehouseSync> selectWarehouseListByObjectParam = houseSyncMapper.selectWarehouseListByObjectParam(sync);
		if (CollectionUtils.isNotEmpty(selectWarehouseListByObjectParam)){
			selectWarehouseListByObjectParam.forEach(object ->{
				WareHouseDetailDTO dto = new WareHouseDetailDTO();
				dto.setCompanyCode(companyCode);
				dto.setCountryCode(object.getCountryCode());
				dto.setWareHouseCode(object.getWarehouseCode());
				dto.setWareHouseId(object.getId());
				dto.setWareHouseName(object.getWarehouseName());
				dto.setWareHouseStatus(object.isStatus());
				dto.setWareHouseType(object.getWarehouseType());
				dto.setCountryName(object.getCountryName());
				wareHouseDetailDTOList.add(dto);
			});
		}
		return wareHouseDetailDTOList;
	}

	@Override
	public List<WareHouseDetailDTO> getWareHouseByUser(String userName) {
		if (StringUtils.isBlank(userName)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "用户名参数不能为空");
		}
		WareHouseAuthorize wareHouseAuthorize = new WareHouseAuthorize();
		wareHouseAuthorize.setCreateUser(userName);
		//查看这个用户添加了几个授权
		List<WareHouseAuthorize> authorizeList = authorizeMapper.page(wareHouseAuthorize);
		//一个集合将所有的数据返回不用区分是那一个授权
		List<WareHouseDetailDTO> dtoAllList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(authorizeList)){
			WarehouseSync sync = new WarehouseSync();
			authorizeList.forEach(author ->{
				sync.setCompanyCode(author.getCompanyCode());
				sync.setStatus(true); //只返回启用的
				List<WarehouseSync> syncList = houseSyncMapper.selectWarehouseListByObjectParam(sync);
				setWareHouseDetailDTO(syncList, dtoAllList, author);
			});
		}
	
		//这批数据是自营的，没有用户信息，一样返回
		List<WarehouseSync> wareHouseSyncList  = houseSyncMapper.selectByWarehouseType("0");
		setWareHouseDetailDTO(wareHouseSyncList, dtoAllList, null);
		return dtoAllList;
	}
	
	public void setWareHouseDetailDTO(List<WarehouseSync> wareHouseSyncList,List<WareHouseDetailDTO> dtoAllList, WareHouseAuthorize authorize){
		if (CollectionUtils.isNotEmpty(wareHouseSyncList)){
			wareHouseSyncList.forEach(object ->{
				WareHouseDetailDTO dto = new WareHouseDetailDTO();
				dto.setCompanyCode(object.getCompanyCode());
				dto.setCountryCode(object.getCountryCode());
				dto.setWareHouseCode(object.getWarehouseCode());
				dto.setWareHouseId(object.getId());
				dto.setWareHouseName(object.getWarehouseName());
				dto.setWareHouseStatus(object.isStatus());
				dto.setWareHouseType(object.getWarehouseType());
				if (null != authorize){
					dto.setAppKey(authorize.getAppKey());
					dto.setAppToken(authorize.getAppToken());
				}
				dtoAllList.add(dto);
			});
		}
	}

	@Override
	public void updateWareHouse(WareHouseSyncVO vo) {
		if (vo.getId() == null || vo.getStatus() == null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "参数值不能为空");
		}
		// TODO Auto-generated method stub
		WarehouseSync sync = new WarehouseSync();
		sync.setId(vo.getId());
		sync.setStatus(vo.getStatus()==1?true:false);
		houseSyncMapper.updateByPrimaryKeySelective(sync);
		//状态为停用,启用时发送队列推送
		sendMsgToQueue(Long.valueOf(vo.getId()),vo.getStatus());
	}
	
	public void sendMsgToQueue(Long id,Integer status){
		try {
			WarehouseSync syncObject = houseSyncMapper.selectByPrimaryKey(id);
			if (null != syncObject){
				WareHouseAuthorize authorize = new WareHouseAuthorize();
				authorize.setCompanyCode(syncObject.getCompanyCode());
				List<WareHouseAuthorize> page = authorizeMapper.page(authorize);
				if (CollectionUtils.isNotEmpty(page)){
					WareHouseAuthorize wareHouseAuthorize = page.get(0);
					wareHouseSender.updateWareHouseStatus(new JSONObject() {
						{
							put("wareHouseName", syncObject.getWarehouseName());
							put("wareHouseCode", syncObject.getWarehouseCode());
							put("wareHouseType", syncObject.getWarehouseType());
							put("appToken", wareHouseAuthorize.getAppToken());
							put("appKey", wareHouseAuthorize.getAppKey());
							put("status",status);
						}
					}.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取用户绑定的仓库关系
	 * @return
	 */
	public UserInfoVO getUserDetail(){
		UserInfoVO userInfo =  new UserInfoVO();
		UserDTO userDTO = getLoginUserInformationByToken.getUserDTO();
		log.info("userDTO:{}",JSONObject.toJSONString(userDTO));
		//	if(UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())) {
			  if (!userDTO.getManage()){
	                List<UserAccountDTO> list=userDTO.getBinds();
	                if (CollectionUtils.isNotEmpty(list)){
	                	UserAccountDTO userAccountDTO = list.get(0);
	                	userAccountDTO.getBindCode();
	                	userInfo.setwCodes(userAccountDTO.getBindCode());
	                }
	           }
		//}
		userInfo.setPlatformType(userDTO.getPlatformType());	  
		return userInfo;
	}

	@Override
	public List<PurposeWareHouseDTO> getPurposeWareHouse(WareHouseSearchVO vo,boolean loginFree) {
		//是否参与权限数据检查
		if (loginFree){
			UserInfoVO userInfo = getUserDetail();
			if (userInfo.getwCodes() != null){
				List<String> getwCodes = userInfo.getwCodes();
				log.info("用户绑定仓库数量:{}",getwCodes.size());
				if (CollectionUtils.isNotEmpty(getwCodes)){
					//获取用户配置好的绑定仓库
					vo.setWareHouseCode(getwCodes);
				}
			}
		}
		return getList(vo);
	}
	
	public List<PurposeWareHouseDTO> getList(WareHouseSearchVO vo){
		List<PurposeWareHouseDTO> dtoList = new ArrayList<>();
		WareHouseAuthorize wareHouseAuthorize = new WareHouseAuthorize();
		wareHouseAuthorize.setServiceId(vo.getWareHouseServiceProvider());
		wareHouseAuthorize.setStatus(1); //只显示授权启用的仓库
		List<WareHouseAuthorize> authorizeList = authorizeMapper.page(wareHouseAuthorize);
		if (CollectionUtils.isNotEmpty(authorizeList)){
			authorizeList.forEach(author ->{
				String customName = author.getCustomName();
				List<WareHouseDetailDTO> wareHouseDetailDTOList = getWareHouseDetailDTOList(vo,author.getCompanyCode());
				if (CollectionUtils.isNotEmpty(wareHouseDetailDTOList)){
					wareHouseDetailDTOList.forEach(detail ->{
						PurposeWareHouseDTO dto = new PurposeWareHouseDTO();
						dto.setWareHouseCode(detail.getWareHouseCode());
						dto.setWareHouseName(customName+"-"+detail.getWareHouseName());
						dto.setCountryCode(detail.getCountryCode());
						dto.setCountryName(detail.getCountryName());
						dto.setWareHouseType(detail.getWareHouseType());
						dtoList.add(dto);
					});
				}
			});
		}
		return dtoList;
	}

	@Override
	public List<PurposeWareHouseDTO> getAvailableWareHouse(String type,String wareHousecode,String countryCode,boolean loginFree) {
		if (StringUtils.isBlank(type)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询类型不能为空");	
		}
		UserInfoVO userInfo = getUserDetail();
		log.info("userInfoPlatformType",userInfo.getPlatformType());
		List<PurposeWareHouseDTO> purposeWareHouse = new ArrayList<>();
		//查询谷仓的可用仓库
		if (type.equals("2") || type.equals("10")){
			WareHouseSearchVO vo = new WareHouseSearchVO();
			vo.setWareHouseStatus(1);
			vo.setWareHouseCountry(countryCode);
			if (StringUtils.isNotBlank(wareHousecode)){
				List<String> s = new ArrayList<>();
				s.add(wareHousecode);
				vo.setWareHouseCode(s);
			}else{
				//如果是非供应商管理登录。直接加载所有的。
				if (userInfo.getwCodes() != null && userInfo.getPlatformType() ==0){
					List<String> getwCodes = userInfo.getwCodes();
					if (CollectionUtils.isNotEmpty(getwCodes)){
						//获取用户配置好的绑定仓库
						vo.setWareHouseCode(getwCodes);
					}
				}
			}
			purposeWareHouse = getPurposeWareHouse(vo,loginFree);
		}
		//查询自营的可用仓库
		if (type.equals("0") || type.equals("10")){
			Map<String,Object> param=new HashMap<>();
			param.put("warehouseType", "0");
			if (StringUtils.isNotBlank(countryCode))
				param.put("countryCode", countryCode);
			if (StringUtils.isNotEmpty(wareHousecode)){
				param.put("warehouseCode", wareHousecode);
			}
			List<String> warehouseCodeList = userInfo.getwCodes();
			if (CollectionUtils.isNotEmpty(warehouseCodeList) && userInfo.getPlatformType() ==0){
				param.put("warehouseCodeList",warehouseCodeList);
			}else if (userInfo.getPlatformType() ==0){
				param.put("supplier", userUtils.getUser().getUserid());
			}
			List<WarehouseSync> sync = houseSyncMapper.selectValidWarehouse(param);
			if (CollectionUtils.isNotEmpty(sync)){
				for(WarehouseSync c : sync){
					PurposeWareHouseDTO dto = new PurposeWareHouseDTO();
					dto.setCountryCode(c.getCountryCode());
					dto.setWareHouseCode(c.getWarehouseCode());
					dto.setWareHouseName(c.getWarehouseName());
					dto.setWareHouseType(c.getWarehouseType());
					purposeWareHouse.add(dto);
				}
				if (CollectionUtils.isNotEmpty(purposeWareHouse)){
					 purposeWareHouse.forEach(s->{
						 s.setWareHouseName(Utils.translation(s.getWareHouseName()));
					 });
				}
			}
		}
		return purposeWareHouse;
	}

	/**
	 * 获取卖家可用仓库
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public List<HouseTypeDTO> getsHouseName(Integer userId) {
		return this.toHouseDTO(this.houseSyncMapper.getsHouseName(userId));
	}

	/**
	 * 根据仓库codep批量查询绑定名称
	 *
	 * @param codes
	 * @return
	 */
	@Override
	public List<HouseTypeDTO> getsNameByCode(List<String> codes) {
		return this.toHouseDTO(this.houseSyncMapper.getsNameByCode(codes));
	}

	/**
	 * 转换树状结构
	 * @param list
	 * @return
	 */
	private List<HouseTypeDTO> toHouseDTO(List<WarehouseSync> list){
		List<HouseTypeDTO> result=new ArrayList<>();
		if (CollectionUtils.isEmpty(list)){
			return result;
		}
		Map<String,List<HouseNameDTO>> map=new HashMap<>(2);
		for (WarehouseSync houseDO:list) {
			if (!houseDO.isStatus()){
				continue;
			}
			if (!map.containsKey(houseDO.getWarehouseProvider())){
				map.put(houseDO.getWarehouseProvider(),new ArrayList<>());
			}
			map.get(houseDO.getWarehouseProvider()).add(new HouseNameDTO(houseDO.getWarehouseCode(),houseDO.getWarehouseName()));
		}
		for (Map.Entry<String,List<HouseNameDTO>> entry:map.entrySet()) {
			result.add(new HouseTypeDTO(entry.getKey(),entry.getValue()));
		}
		return result;
	}

	@Override
	public List<WareHouseDetailDTO> getWareHouse(WareHouseSearchVO vo) {

		return null;
	}

	@Override
	public List<WareHouseServiceProviderDTO> getWareHouseServiceProviderByWareHouseCode(List<String> wareHouseCodeList) {
		if (CollectionUtils.isEmpty(wareHouseCodeList)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "参数不能为空");	
		}
		//通过仓库code 获取用来进行数据关联的companyCode
		List<String> companyCodeList = new ArrayList<>();
		wareHouseCodeList.forEach(s ->{
			String[] split = s.split("_");
			if (split.length>2){
				//GC_G1149_CNTC
				companyCodeList.add(split[1]);
			}
		});
		List<WareHouseServiceProviderDTO>   serviceProviderList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(companyCodeList)){
			List<Integer> serviceProviderId = authorizeMapper.getServiceProviderId(companyCodeList);
			for (Integer s : serviceProviderId){
				WareHouseSearchVO vo = new WareHouseSearchVO();
				vo.setWareHouseServiceProvider(s);
				serviceProviderList = getServiceProviderList(vo,false);
			}
		}
		return serviceProviderList;
	}

	@Override
	public List<AuthorizeDTO> getWareHouseServiceProviderByServiceId(Integer serviceId) {
		WareHouseAuthorize wareHouseAuthorize = new WareHouseAuthorize();
		wareHouseAuthorize.setServiceId(serviceId);
		wareHouseAuthorize.setStatus(1);//只查询可用的
		List<WareHouseAuthorize> list = authorizeMapper.page(wareHouseAuthorize);
		List<AuthorizeDTO> dtoList = new ArrayList<>();
		list.forEach(l ->{
			AuthorizeDTO dto = new AuthorizeDTO();
			dto.setAppKey(l.getAppKey());
			dto.setAppToken(l.getAppToken());
			dto.setCompanyCode(l.getCompanyCode());
			dto.setCustomName(l.getCustomName());
			dtoList.add(dto);
		});
		return dtoList;
	}

	@Override
	public AuthorizeDTO getAuthorizeByCode(String warehouseCode) {
		return authorizeMapper.selectAuthorizeBywarehouseCode(warehouseCode);
	}

	@Override
	public List<AuthorizeDTO> getAuthorizeByCompanyCodeList(List<String> list) {
		return authorizeMapper.getAuthorizeByCompanyCodeList(list);
	}

	@Override
	public List<AuthorizeDTO> getAuthorizeByWareHouseCode(List<String> wareHouseCodeList) {
		// TODO Auto-generated method stub
		if (CollectionUtils.isEmpty(wareHouseCodeList)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "参数不能为空");	
		}
		//通过仓库code 获取用来进行数据关联的companyCode
		List<String> companyCodeList = new ArrayList<>();
		wareHouseCodeList.forEach(s ->{
			String[] split = s.split("_");
			if (split.length>2){
				//GC_G1149_CNTC
				companyCodeList.add(split[1]);
			}
		});
		return authorizeMapper.getAuthorizeByCompanyCodeList(companyCodeList);
	}
}
