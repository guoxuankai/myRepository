package com.rondaful.cloud.supplier.service.impl;

import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.dto.LogisticsDTO;
import com.rondaful.cloud.supplier.dto.LogisticsResponseDTO;
import com.rondaful.cloud.supplier.entity.LogisticsInfo;
import com.rondaful.cloud.supplier.entity.PlatformLogistics;
import com.rondaful.cloud.supplier.entity.WarehouseMsg;
import com.rondaful.cloud.supplier.mapper.LogisticsInfoMapper;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.ThirdAppLogisticsDTO;
import com.rondaful.cloud.supplier.rabbitmq.LogisticsSender;
import com.rondaful.cloud.supplier.service.ILogisticsInfoService;
import com.rondaful.cloud.supplier.service.IWarehouseBasicsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class LogisticsInfoServiceImpl implements ILogisticsInfoService {


    @Autowired
    private LogisticsInfoMapper logisticsInfoMapper;

    @Autowired
    private LogisticsSender logisticsSender;
    
	@Autowired
	GetLoginUserInformationByToken getLoginUserInformationByToken;

	@Autowired
	IWarehouseBasicsService warehouseBasicsService;

    public Page queryLogisticsListPage(LogisticsInfo param) {
        List<LogisticsDTO> list = logisticsInfoMapper.queryLogisticsListPage(param);
        if (CollectionUtils.isNotEmpty(list)) {
        	String i18n = param.getRequest().getHeader("i18n");
        	if(StringUtils.isNotBlank(i18n)) {
                for (LogisticsDTO logisticsDTO : list) {
                    convertLanguage(logisticsDTO);
                }
        	}
        }
        PageInfo pageInfo = new PageInfo(list);
        return new Page(pageInfo);
    }

	public Page queryLogisticsListById(LogisticsInfo param) {
		List<ThirdAppLogisticsDTO> list = logisticsInfoMapper.queryLogisticsListById(param);
		PageInfo pageInfo = new PageInfo(list);
		return new Page(pageInfo);
	}


	public List<LogisticsResponseDTO> queryLogisticsByName(LogisticsInfo param) {
        List<LogisticsResponseDTO> result = new ArrayList<LogisticsResponseDTO>();
		List<LogisticsDTO> logisticsList = logisticsInfoMapper.queryLogisticsListPage(param);//根据物流方式名称查出物流方式集合
		if(CollectionUtils.isNotEmpty(logisticsList)){
			LogisticsResponseDTO logisticsResponseDTO;
			for(LogisticsDTO logisticsDTO:logisticsList) {
				logisticsResponseDTO = new LogisticsResponseDTO();
				List<LogisticsDTO> list = new ArrayList<LogisticsDTO>();
				WarehouseDTO warehouseDTO = warehouseBasicsService.getByWarehouseId(logisticsDTO.getWarehouseId());//获取仓库信息
				if(null == warehouseDTO) continue;
				logisticsResponseDTO.setWarehouseName(warehouseDTO.getName() +"-"+ warehouseDTO.getWarehouseName());
				logisticsResponseDTO.setWarehouseId(warehouseDTO.getWarehouseId().toString());
				logisticsResponseDTO.setWarehouseCode(warehouseDTO.getWarehouseCode());
				if(StringUtils.isNotBlank(param.getRequest().getHeader("i18n")))
					logisticsResponseDTO.setForeignWarehouseName(Utils.i18n(warehouseDTO.getName() +"-"+  warehouseDTO.getWarehouseName()));
				for(LogisticsDTO logistics:logisticsList) {
					if(StringUtils.isNotBlank(param.getRequest().getHeader("i18n"))) {
						logistics.setForeignShortName(Utils.i18n(logistics.getShortName()));
						logistics.setForeignCarrierName(Utils.i18n(logistics.getCarrierName()));
					}
					if(logistics.getWarehouseId().equals(logisticsDTO.getWarehouseId())) {
						list.add(logistics);
					}
				}
				logisticsResponseDTO.setList(list);
                result.add(logisticsResponseDTO);
			}
		}
		if(CollectionUtils.isNotEmpty(result)){
			for(int i = 0;i<result.size();i++){
				for  ( int  j  =  result.size()  -   1 ; j  >  i; j -- )  {
					if  (result.get(j).getWarehouseId().equals(result.get(i).getWarehouseId()))  {
						result.remove(j);
					}
				}
			}
		}
		return result;
	}
	

	@Override
	public List<LogisticsDTO> queryLogisticsList() {
		return logisticsInfoMapper.queryLogisticsList();
	}
    
	@Override
	public LogisticsDTO queryLogisticsByCode(String logisticsCode,Integer warehouseId) {
		return logisticsInfoMapper.queryLogisticsByCode(logisticsCode, warehouseId);
	}
	
	@Override
	public void updateStatusByCode(LogisticsInfo logistics) {
		Integer result = logisticsInfoMapper.updateStatusById(logistics);
		if("0".equals(logistics.getStatus()) && result > 0){
            logisticsSender.sendLogisticsDiscardMQ(logistics.getWarehouseId(),logistics.getCode());
        }
	}

    @Override
    public Map<String, Object> queryThirdLogistics() {
        Map<String, Object> map = new HashMap<String, Object>();
        List<PlatformLogistics> list = logisticsInfoMapper.queryThirdLogistics();
        List<String> aliexpressCode = logisticsInfoMapper.selectAliexpressCode();
        map.put("list", list);
        map.put("aliexpressCode", aliexpressCode);
        return map;
    }
    
	@Override
	public void updateLogisticsMapping(LogisticsInfo param) {
		logisticsInfoMapper.updateLogisticsMapping(param);
	}

	@Override
	public void insertLogisticsInfoList(List<LogisticsInfo> list) {
		logisticsInfoMapper.insertLogisticsInfoList(list);
	}

	@Override
	public Set<WarehouseMsg> queryWarehouse(String logisticsCode) {
		Set<WarehouseMsg> warehouseMsgList = new HashSet<WarehouseMsg>();
		List<LogisticsDTO> logisticsDTOs = logisticsInfoMapper.queryLogisticsListPage(new LogisticsInfo(logisticsCode,null,null));
		if(CollectionUtils.isNotEmpty(logisticsDTOs)) {
			for(LogisticsDTO logisticsDTO : logisticsDTOs ) {
				WarehouseDTO warehouseDTO = warehouseBasicsService.getByWarehouseId(logisticsDTO.getWarehouseId());
				WarehouseMsg warehouseMsg = new WarehouseMsg();
				warehouseMsg.setWareHouseCode(warehouseDTO.getWarehouseCode());
				warehouseMsg.setWareHouseName(warehouseDTO.getWarehouseName());
				warehouseMsg.setWarehouseId(warehouseDTO.getWarehouseId().toString());
				warehouseMsg.setWareHouseNameEn(Utils.translation(warehouseDTO.getWarehouseName()));
				warehouseMsgList.add(warehouseMsg);
			}
		}
		return warehouseMsgList;
	}

	@Override
	public void updateWarehouseStatus(String warehouseCode, String status) {
		logisticsInfoMapper.updateWarehouseStatus(warehouseCode, status);
	}

    

    private void convertLanguage(LogisticsDTO param) {
        param.setForeignShortName(Utils.i18n(param.getShortName()));
        param.setForeignCarrierName(Utils.i18n(param.getCarrierName()));
    }


}
