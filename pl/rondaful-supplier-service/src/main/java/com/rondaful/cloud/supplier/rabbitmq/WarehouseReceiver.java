package com.rondaful.cloud.supplier.rabbitmq;

import com.rondaful.cloud.supplier.mapper.LogisticsInfoMapper;
import com.rondaful.cloud.supplier.remote.RemoteGranaryService;
import com.rondaful.cloud.supplier.service.ILogisticsInfoService;
import com.rondaful.cloud.supplier.service.IThirdLogisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 仓库操作监听
 */
@Component
public class WarehouseReceiver {

    private final static Logger log = LoggerFactory.getLogger(WarehouseReceiver.class);
    
    @Autowired
    private IThirdLogisticsService thirdLogisticsService;

    @Autowired
    private ILogisticsInfoService logisticsInfoService;
    
    @Autowired
    private RemoteGranaryService remoteGranaryService;
    
    @Autowired
    private LogisticsInfoMapper logisticsInfoMapper;

    //监听仓库状态更新
    @RabbitListener(queues = "wareHouseQueue")
    public void receiveWarehouse(String message) {
/*    	try {
    		log.info("仓库数据为={}",message);
    		JSONObject json = JSONObject.parseObject(message);
    		String status = json.getString("status");//仓库状态
    		String warehouseCode = json.getString("wareHouseCode");
    		String warehouseName = json.getString("wareHouseName");
    		String warehouseType = json.getString("wareHouseType");
    		if("0".equals(warehouseType)) {//仓库为erp
    			if(StringUtils.isNotEmpty(warehouseCode)) logisticsInfoService.updateWarehouseStatus(warehouseCode, status);
    		}else if("2".equals(warehouseType)) {//仓库为谷仓
    			if(StringUtils.isNotEmpty(warehouseCode)) {
        			if("0".equals(status)) {//仓库状态为停用
        				logisticsInfoService.updateWarehouseStatus(warehouseCode,status);
        			} else {//仓库状态为启用 先查表是否有同样的数据，是更新仓库状态，否就拉取数据，否就拉取数据，插入库
        				List<LogisticsDTO> logisticsDTOList = logisticsInfoMapper.queryLogisticsListPage(new LogisticsInfo(warehouseCode,null));
        				if(CollectionUtils.isNotEmpty(logisticsDTOList)) {
        					logisticsInfoService.updateWarehouseStatus(warehouseCode,status);
        				}else {
        					Map<String,Object> paramMap = new HashMap<String,Object>();
        					String[] warehouseCodeStr = warehouseCode.split("_");
        					paramMap.put("warehouseCode", warehouseCodeStr[2]);
        					JSONObject jonsResult = remoteGranaryService.getShippingMethod(paramMap, json.getString("appToken"),json.getString("appKey"));
        					List<GranaryLogistics> GuLogisticsList = JSONObject.parseArray(jonsResult.getString("data"), GranaryLogistics.class);
        					thirdLogisticsService.insertGuLogisticsList(new WarehouseSync(warehouseName,warehouseCode), GuLogisticsList, json.getString("appKey"), json.getString("appToken"));
        				}
        			}
        		}
    		}
    		
		} catch (Exception e) {
			log.error("仓库状态更新通知异常",e);
		}*/
    }


    //监听队列新增仓库
    @RabbitListener(queues = "wareHouseAuthorizeQueue")
    public void addWareHouse(String warehouseStr) {
        try{
        	log.info("warehouseStr={}",warehouseStr);
        	//添加授权，仓库状态为停用，暂时不做处理
        }catch (Exception e){
            log.error("新增物流方式异常",e);
        }
    }
}
