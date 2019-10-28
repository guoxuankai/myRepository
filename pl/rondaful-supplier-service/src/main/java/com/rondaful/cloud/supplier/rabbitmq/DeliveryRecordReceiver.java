package com.rondaful.cloud.supplier.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.supplier.entity.DeliveryDetail;
import com.rondaful.cloud.supplier.entity.DeliveryRecord;
import com.rondaful.cloud.supplier.entity.InventoryDynamics;
import com.rondaful.cloud.supplier.mapper.DeliveryDetailMapper;
import com.rondaful.cloud.supplier.mapper.DeliveryRecordMapper;
import com.rondaful.cloud.supplier.mapper.InventoryDynamicsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class DeliveryRecordReceiver {

    @Autowired
    private DeliveryRecordMapper deliveryRecordMapper;

    @Autowired
    private DeliveryDetailMapper deliveryDetailMapper;

    @Autowired
    private InventoryDynamicsMapper inventoryDynamicsMapper;

    private final static Logger log = LoggerFactory.getLogger(DeliveryRecordReceiver.class);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final String OPERATE_TYPE = "订单出库";

    /**
     * 监听生成的出库记录 @Title: process @Description: TODO(这里用一句话描述这个方法的作用) @param @param
     * message 参数 @return void 返回类型 @throws
     */
    // 监听队列queue-a
    @RabbitListener(queues = "queue-warehouse-delivery-record")
    public void process(String message) {
        log.error("______________________收到出库记录信息______________________" + message + "______________________");
    	try {
	        DeliveryRecord deliveryRecord = JSONObject.parseObject(message, DeliveryRecord.class);
	        deliveryRecord.setDeliveryId(this.createDeliveryId());
	        deliveryRecord.setCreateDate(new Date());
	        deliveryRecord.setOrderStatus(1);
	        log.debug("记录：{}", JSONObject.toJSON(deliveryRecord));
	        JSONObject convertHead = (JSONObject) JSONObject.toJSON(deliveryRecord);
	        List<DeliveryDetail> dllist=JSONObject.parseArray(convertHead.getString("deliveryDetailList"), DeliveryDetail.class);
	        List<InventoryDynamics> dyList= new ArrayList<>();
	        for(DeliveryDetail detail: dllist) {
	            InventoryDynamics dy=new InventoryDynamics();
	            dy.setSku(detail.getSku());
	            dy.setSupplierSku(detail.getSupplierSku());
	            dy.setSupplierId(detail.getSupplierId());
	            dy.setSupplier(detail.getSupplierName());
	            dy.setWarehouseName(deliveryRecord.getDeliveryWarehouse());
	            dy.setWarehouseCode(deliveryRecord.getDeliveryWarehouseCode());
	            dy.setOperateType(OPERATE_TYPE);
	            dy.setAlertInventory(detail.getSkuQuantity());
	            dy.setOperateDate(deliveryRecord.getDeliveryTime());
	            detail.setDeliveryId(deliveryRecord.getDeliveryId());
	            dyList.add(dy);
	        }
	        inventoryDynamicsMapper.insertBatchInventoryDynamics(dyList);
	        deliveryDetailMapper.insertBatchDeliveryDetail(dllist);
	        List<DeliveryRecord> list = new ArrayList<DeliveryRecord>();
	        list.add(deliveryRecord);
	        deliveryRecordMapper.insertBatchDeliveryRecord(list);
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /******************************出库单号生成*****************************************/
    private String createDeliveryId() {
        StringBuilder sb = new StringBuilder();
        sb.append("CK");
        sb.append(sdf.format(new Date()));
        sb.append(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6));
        return sb.toString();
    }
}
