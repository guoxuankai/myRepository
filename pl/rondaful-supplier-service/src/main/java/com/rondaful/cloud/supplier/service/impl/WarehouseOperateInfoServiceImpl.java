package com.rondaful.cloud.supplier.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.UserUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.common.GetLoginInfo;
import com.rondaful.cloud.supplier.entity.WarehouseOperateInfo;
import com.rondaful.cloud.supplier.entity.WarehouseSync;
import com.rondaful.cloud.supplier.mapper.WarehouseOperateInfoMapper;
import com.rondaful.cloud.supplier.mapper.WarehouseSyncMapper;
import com.rondaful.cloud.supplier.rabbitmq.WareHouseSender;
import com.rondaful.cloud.supplier.remote.RemoteErpService;
import com.rondaful.cloud.supplier.remote.RemoteUserService;
import com.rondaful.cloud.supplier.service.IWarehouseOperateInfoService;
import com.rondaful.cloud.supplier.vo.UserInfoVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: WareHouseInfoServiceImpl
 * @Description: 同步ERP仓库列表
 * @author lxx
 * @date 2018年12月3日
 *
 */

@Service
public class WarehouseOperateInfoServiceImpl   implements IWarehouseOperateInfoService {

	private final static Logger log = LoggerFactory.getLogger(WarehouseOperateInfoServiceImpl.class);

	  @Value("${erp.url}")
      private String erpUrl;


	@Autowired
	WarehouseSyncMapper warehouseSyncMapper;

	@Autowired
    RemoteUserService remoteUserService;

	@Autowired
	WarehouseOperateInfoMapper operateInfoMapper;

	@Autowired
	GetLoginInfo getLoginInfo;

	@Autowired
	RemoteErpService remoteErpService;

	@Autowired
	GetLoginUserInformationByToken loginUserInfo;

	@Autowired
	private WarehouseSyncMapper houseSyncMapper;

	@Autowired
	private WareHouseSender wareHouseSender;

	/**
	 * ERP同步插入仓库列表
	 */
	@Override
	public void insertWarehouse() {
			List<WarehouseSync> warehouselist=new ArrayList<WarehouseSync>();
			JSONObject parseObject=remoteErpService.getWarehouse();
			JSONArray array = parseObject.getJSONArray("data");
			if( ! CollectionUtils.isEmpty(array)) {
				for (int i = 0; i < array.size(); i++) {
					WarehouseSync warehouseSync=new WarehouseSync();
					JSONObject data = (JSONObject) array.get(i);
					warehouseSync.setWarehouseCode(data.get("code").toString());
					warehouseSync.setWarehouseName(data.get("name").toString());
					warehouseSync.setCountryCode(data.get("country").toString());
					warehouseSync.setWarehouseProvider("利朗达");
					warehouseSync.setWarehouseType("0");
					warehouseSync.setSupplierId(100);
					warehouseSync.setSupplier("利朗达");
					warehouseSync.setSupplierCompanyName("深圳市利朗达科技有限公司");
					warehouselist.add(warehouseSync);
				}
				int count=warehouseSyncMapper.syncWarehouse(warehouselist);

				log.info("仓库插入成功条数："+count);
			}
		}
	/**
	 * 取得可用仓库列表
	 */
	@Override
	public List<WarehouseSync> getValidWarehouseList(){
		Map<String,Object> param=new HashMap<>();
		param.put("supplierId",null);
		return warehouseSyncMapper.selectValidWarehouse(param);
	}

	/**
	 * 根据ID启停仓库
	 */
	@Override
	public void updateWarehouseById(Map<String,String>  param) {
		param.put("lastUpdateBy", loginUserInfo.getUserInfo().getUser().getUsername());
		operateInfoMapper.updateWarehouseStatusById(param);
		//添加消息推送机制
		String id = param.get("id");
		String status = param.get("available");
		sendMsgToQueue(Long.valueOf(id), Integer.valueOf(status));
	}

	public void sendMsgToQueue(Long id,Integer status){
		try {
			WarehouseSync syncObject = houseSyncMapper.selectByPrimaryKey(id);
			if (null != syncObject){
				wareHouseSender.updateWareHouseStatus(new JSONObject() {
						{
							put("wareHouseName", syncObject.getWarehouseName());
							put("wareHouseCode", syncObject.getWarehouseCode());
							put("wareHouseType", syncObject.getWarehouseType());
							put("status",status);
						}
					}.toString());
				}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取得供应商分页
	 */
	@Override
	public Page<WarehouseOperateInfo> pageBySupplier(WarehouseOperateInfo operateInfo) {
		UserInfoVO userDetail = getUserDetail();
		List<String> wCodes = userDetail.getwCodes();
		if (CollectionUtils.isNotEmpty(wCodes)){
			operateInfo.setCodeList(wCodes);
		}else{
			operateInfo.setSupplier(String.valueOf(userDetail.getUserId()));
		}
		List<WarehouseOperateInfo> list = operateInfoMapper.pageBySupplier(operateInfo);
		list.forEach(l ->{
			l.setWarehouseName(Utils.translation(l.getWarehouseName()));
			l.setCountryName(Utils.translation(l.getCountryName()));
		});
		PageInfo<WarehouseOperateInfo> pageInfo = new PageInfo(list);
		return new Page(pageInfo);
	}
	
	public UserInfoVO getUserDetail(){
		UserInfoVO userInfo =  new UserInfoVO();
		UserDTO userDTO = loginUserInfo.getUserDTO();
		log.info("userDTO:{}",JSONObject.toJSONString(userDTO));
			//if(UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())) {
			  if (!userDTO.getManage()){
	                List<UserAccountDTO> list=userDTO.getBinds();
	                if (CollectionUtils.isNotEmpty(list)){
	                	UserAccountDTO userAccountDTO = list.get(0);
	                	userAccountDTO.getBindCode();
	                	userInfo.setwCodes(userAccountDTO.getBindCode());
	                }
	          //  }
			  //if(UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())) {
				  userInfo.setUserId(userDTO.getUserId());
				  userInfo.setPlatformType(userDTO.getPlatformType());
			 // }
		}
		return userInfo;
	}

	/**
	 * 根据供应商判断仓库操作表是否有数据，没有则插入
	 */
	@Override
	public void insertOperateInfo() {
		log.info("供应商Id"+getLoginInfo.getUserInfo().getTopUserId());
		Map<String,Object> param=new HashMap<>();
		param.put("supplierId",String.valueOf(getLoginInfo.getUserInfo().getTopUserId()));
		List<WarehouseSync> whList=warehouseSyncMapper.selectValidWarehouse(param);
		if( ! CollectionUtils.isEmpty(whList)) {
			for(WarehouseSync wh:whList) {
				param.put("warehouseCode", wh.getWarehouseCode());
				List<WarehouseOperateInfo> operateInfoList=operateInfoMapper.selectOperateInfoBySupplier(param);
				log.info("供应商操作仓库记录数量"+operateInfoList.size());
				//如果操作仓库记录数量为0，初始化表
				if(CollectionUtils.isEmpty(operateInfoList)) {
					//log.info("仓库信息"+whList);
						WarehouseOperateInfo warehouseOperateInfo=new WarehouseOperateInfo();
						warehouseOperateInfo.setWarehouseCode(wh.getWarehouseCode());
						warehouseOperateInfo.setSupplierId(getLoginInfo.getUserInfo().getTopUserId());
						//当前登录者信息
						UserAll user = loginUserInfo.getUserInfo();
						String supplier=user.getParentUser().getUsername()==null ? user.getUser().getUsername() : user.getParentUser().getUsername();
						warehouseOperateInfo.setSupplier(supplier);
						warehouseOperateInfo.setSupplierCompanyName(loginUserInfo.getUserInfo().getUser().getCompanyNameUser());
						warehouseOperateInfo.setCreateBy(loginUserInfo.getUserInfo().getUser().getUsername());
						warehouseOperateInfo.setLastUpdateBy(loginUserInfo.getUserInfo().getUser().getUsername());
						operateInfoMapper.insertOperateInfo(warehouseOperateInfo);
				}

			}

		}
	}


	/**
	 * 取得管理后台分页
	 */
	@Override
	public Page<WarehouseOperateInfo> pageByCms(WarehouseOperateInfo operateInfo) {
		List<WarehouseOperateInfo> list = operateInfoMapper.pageByCms(operateInfo);
		if (CollectionUtils.isNotEmpty(list)){
			list.forEach(l ->{
				 l.setSupplierCompanyName(Utils.translation(l.getSupplierCompanyName()));
				 l.setWarehouseName(Utils.translation(l.getWarehouseName()));
				 l.setWarehouseProvider(Utils.translation(l.getWarehouseProvider()));
			});
		}
		PageInfo<WarehouseOperateInfo> pageInfo = new PageInfo(list);
		 return new Page(pageInfo);
	}

	@Override
	public List<WarehouseOperateInfo> queryWarehouseMsg(WarehouseOperateInfo operateInfo ) {
		List<WarehouseOperateInfo> list = operateInfoMapper.pageByCms(operateInfo);
		 return list;
	}

	/**
	 * 根据仓库名称取得仓库信息
	 */
	@Override
	public List<WarehouseSync> selectWarehouseByParam(Map<String, String> param){
		return warehouseSyncMapper.selectWarehouseByParam(param);
	}
	/**
	 *
	* @Title: getSupplier
	* @Description: 远程调用用户服务取得供应商
	* @param @param token
	* @param @return    参数
	* @return String    返回类型
	* @throws
	 */
	private String getSupplier(String token) {
		String body = JSONObject.toJSONString(remoteUserService.getUserByToken(token));
		JSONObject convertBody = JSONObject.parseObject(body);
		JSONObject data = (JSONObject) convertBody.get("data");
		JSONObject user = (JSONObject) data.get("user");
		String supplier=user.getString("companyName");
		return supplier;
	}
	/**
	 * 取得国家列表
	 */
	@Override
	public List<WarehouseOperateInfo> getCountryList() {
		return operateInfoMapper.selectCountry();
	}
	
	/**
	 * 取得国家列表
	 */
	@Override
	public List<WarehouseOperateInfo> getCountryListByCode(String countryCode) {
		return operateInfoMapper.selectCountryByCode(countryCode);
	}

	/**
	 * 取得可用仓库列表
	 */
	@Override
	public Page<WarehouseSync> getValidWarehousePage(){
		Map<String,Object> param=new HashMap<>();
		List<WarehouseSync>  houseList = houseSyncMapper.selectValidWarehouse(param);
		PageInfo<WarehouseSync> pageInfo = new PageInfo (houseList);
		 return new Page(pageInfo);
	}
}
