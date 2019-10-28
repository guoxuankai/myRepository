package com.rondaful.cloud.supplier.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.supplier.common.SyncInventoryUtil;
import com.rondaful.cloud.supplier.entity.WarehouseInventory;
import com.rondaful.cloud.supplier.mapper.WarehouseInventoryMapper;
import com.rondaful.cloud.supplier.service.impl.DeliveryRecordServiceimpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CommdityReceiver {

	@Autowired
	WarehouseInventoryMapper warehouseInventoryMapper;

	@Autowired
	SyncInventoryUtil syncInventoryUtil;

	private final static Logger log = LoggerFactory.getLogger(DeliveryRecordServiceimpl.class);

	ExecutorService service = Executors.newFixedThreadPool(50);

	/**
	 * 监听新增的商品 @Title: process @Description: TODO(这里用一句话描述这个方法的作用) @param @param
	 * message 参数 @return void 返回类型 @throws
	 */
	// 监听队列queue-a
	@RabbitListener(queues = "commodity-sku-add-queue")
	public void process(String message) {
		try {
			log.info("接收的商品信息==>{}", message);
			List<WarehouseInventory> inventoryList = JSONObject.parseArray(message,WarehouseInventory.class);
			//

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
