package com.rondaful.cloud.supplier.listen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.service.TranslationService;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.entity.WarehouseInventory;
import com.rondaful.cloud.supplier.mapper.WarehouseInventoryMapper;
@Component
public class ThreadListen implements ApplicationRunner {
	 
	@Autowired
	WarehouseInventoryMapper warehouseInventoryMapper;
	
	@Autowired
	private TranslationService translation;
		
	ExecutorService service = Executors.newFixedThreadPool(5);
	
	private final static Logger log = LoggerFactory.getLogger(ThreadListen.class);
	
	// 声明一个容量为10的缓存队列
	LinkedBlockingDeque<List<WarehouseInventory>> queue = new LinkedBlockingDeque<List<WarehouseInventory>>();
	
	
	public LinkedBlockingDeque<List<WarehouseInventory>> getQueue() {
		return queue;
	}


	public void setQueue(LinkedBlockingDeque<List<WarehouseInventory>> queue) {
		this.queue = queue;
	}


	@Override
	public void run(ApplicationArguments args) throws Exception {
		new Thread() {
			public void run() {
				while (true) {
					try {
						List<WarehouseInventory> inventoryList = queue.take();
							service.execute(new Thread() {
								public void run() {
									//List<WarehouseInventory> inventoryList = queue.poll();
									List<WarehouseInventory> insertlist = new ArrayList<>();
									if (!CollectionUtils.isEmpty(inventoryList)) {
										log.info("读取线程池队列数据开始同步库存");
										// 批量更新数据
										for (WarehouseInventory whInventory:inventoryList) {
											log.info("ERP供应商sku:{}",whInventory.getSupplierSku());
											WarehouseInventory  commodity = warehouseInventoryMapper.getCommodityBySupplierSku(whInventory.getSupplierSku());
											log.info("查询商品信息{}",JSONObject.toJSON(commodity));
											if(commodity != null) {
												int result = warehouseInventoryMapper.updateBysupplierSku(whInventory);
									 			log.info("库存更新结果：{}",result);
												if (result > 0) {
													continue;
												}
												whInventory.setCommodityName(commodity.getCommodityName());
												whInventory.setCommodityNameEn(commodity.getCommodityNameEn());
												whInventory.setPinlianSku(commodity.getPinlianSku());
												whInventory.setSupplier(commodity.getSupplier());
												whInventory.setSupplierId(commodity.getSupplierId());
												whInventory.setSupplierCompanyName(commodity.getSupplierCompanyName());
												try {
													whInventory.setWarehouseNameEn(translation.transcation(whInventory.getWarehouseName(), "zh", "en"));
												} catch (Exception e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												insertlist.add(whInventory);
											}
										}
										
										if(!CollectionUtils.isEmpty(insertlist))
										{
											warehouseInventoryMapper.syncWarehouseInventory(insertlist);
										}
									}
								}
							});
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			
		}.start();
		
		
	}
}
