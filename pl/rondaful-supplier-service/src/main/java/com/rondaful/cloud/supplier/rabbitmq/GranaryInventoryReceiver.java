package com.rondaful.cloud.supplier.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.service.TranslationService;
import com.rondaful.cloud.supplier.dto.AuthorizeDTO;
import com.rondaful.cloud.supplier.entity.WarehouseInventory;
import com.rondaful.cloud.supplier.entity.WarehouseSync;
import com.rondaful.cloud.supplier.mapper.WareHouseAuthorizeMapper;
import com.rondaful.cloud.supplier.mapper.WarehouseInventoryMapper;
import com.rondaful.cloud.supplier.mapper.WarehouseSyncMapper;
import com.rondaful.cloud.supplier.vo.GranaryInventoryVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class GranaryInventoryReceiver {

	@Autowired
	WarehouseInventoryMapper warehouseInventoryMapper;
	
	@Autowired
	WarehouseSyncMapper warehouseSyncMapper;
	
	@Autowired
	private WareHouseAuthorizeMapper authorizeMapper;

	@Autowired
	private TranslationService translation;


	private final static Logger log = LoggerFactory.getLogger(GranaryInventoryReceiver.class);

	ExecutorService service = Executors.newFixedThreadPool(50);

	/**
	 * 监听新增的商品 @Title: process @Description: TODO(这里用一句话描述这个方法的作用) @param @param
	 * message 参数 @return void 返回类型 @throws
	 */
	// 监听队列queue-a
	@RabbitListener(queues = "queue-goodcang-stockchange")
	public void process(String message) {

		log.info("接收的谷仓库存信息==>{}",message);
			try {
				if(StringUtils.isNoneBlank(message)) {
					JSONObject body =  JSONObject.parseObject(message); 
					String data=body.getString("message");
					GranaryInventoryVO granaryInventoryVO=JSONObject.parseObject(data, GranaryInventoryVO.class);
					WarehouseInventory  commodity = warehouseInventoryMapper.getCommodityByPinlianSku(granaryInventoryVO.getProduct_sku());
					log.info("查询商品信息{}",JSONObject.toJSON(commodity));
					//判断商品是否存在
					if(commodity != null) {
						WarehouseInventory warehouseInventory=new WarehouseInventory();
						 warehouseInventory.setPinlianSku(granaryInventoryVO.getProduct_sku());
						String warehouseCode="GC_"+granaryInventoryVO.getProduct_barcode().split("-")[0]+"_"+granaryInventoryVO.getWarehouse_code();
						warehouseInventory.setWarehouseCode(warehouseCode);
						warehouseInventory.setProductBarcode(granaryInventoryVO.getProduct_barcode());
						warehouseInventory.setAvailableQty(granaryInventoryVO.getSellableQty());
						warehouseInventory.setDefectsQty(granaryInventoryVO.getUnsellableQty());
						warehouseInventory.setStockingQty(granaryInventoryVO.getStockingQty());
						warehouseInventory.setSyncTime(granaryInventoryVO.getTransaction_time());
						//存在更新记录
						int updateCount = warehouseInventoryMapper.updateGranaryInventory(warehouseInventory);
						if(updateCount > 0) {
							return;
						}
						log.info("谷仓库存更新结果：{}",updateCount);
						 Map<String, String> param = new HashMap<>();
						 param.put("warehouseCode", warehouseCode);
						 List<WarehouseSync> whList=warehouseSyncMapper.selectWarehouseByParam(param);
						 String wh=CollectionUtils.isEmpty(whList) ? null : whList.get(0).getWarehouseName();
						 AuthorizeDTO account=authorizeMapper.getAuthorizeByCompanyCode(granaryInventoryVO.getProduct_barcode().split("-")[0]);
						 String acc = account == null ? null : account.getCustomName();
						String warehouseName= acc + "-"+wh;
						warehouseInventory.setWarehouseName(warehouseName);
						warehouseInventory.setCommodityName(commodity.getCommodityName());
						warehouseInventory.setCommodityNameEn(commodity.getCommodityNameEn());
						warehouseInventory.setSupplierSku(commodity.getSupplierSku());
						warehouseInventory.setSupplier(commodity.getSupplier());
						warehouseInventory.setSupplierId(commodity.getSupplierId());
						warehouseInventory.setSupplierCompanyName(commodity.getSupplierCompanyName());
						warehouseInventory.setPictureUrl(commodity.getPictureUrl());
						warehouseInventory.setWarehouseNameEn(translation.transcation(warehouseInventory.getWarehouseName(), "zh", "en" ));
						//不存在插入库存
						int insertCount = warehouseInventoryMapper.insertGranaryInventory(warehouseInventory);
						log.info("谷仓库存插入结果：{}",insertCount);
					}
				}
			} catch (Exception e) {
					 log.error("谷仓库存推送异常{}",e);
					e.printStackTrace();
			}
		}
}
