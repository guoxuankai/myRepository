package com.rondaful.cloud.supplier.common;

	import java.text.SimpleDateFormat;
	import java.util.ArrayList;
	import java.util.Calendar;
	import java.util.Date;
import java.util.HashMap;
import java.util.List;
	import java.util.Map;

	import org.apache.commons.collections.CollectionUtils;
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
	import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.service.TranslationService;
import com.rondaful.cloud.supplier.entity.WarehouseInventory;
import com.rondaful.cloud.supplier.entity.WarehouseSync;
import com.rondaful.cloud.supplier.mapper.WarehouseInventoryMapper;
import com.rondaful.cloud.supplier.mapper.WarehouseSyncMapper;
import com.rondaful.cloud.supplier.remote.RemoteErpService;

	/**
	 * 取得ERP返回的VO数据同步库存
	* @ClassName: GetInventoryVoData
	* @Description: TODO(这里用一句话描述这个类的作用)
	* @author Administrator
	* @date 2019年1月23日
	*
	 */
	@Component
	public class SyncInventoryUtil {

		private final static Logger log = LoggerFactory.getLogger(SyncInventoryUtil.class);
		
		@Autowired
		private TranslationService translation;
		
		@Autowired
		RemoteErpService remoteErpService;
		
		@Autowired
		WarehouseInventoryMapper warehouseInventoryMapper;
		
		@Autowired
		WarehouseSyncMapper warehouseSyncMapper;
		
		
		public int syncInventory(JSONArray array) {
			int invCount=0;
			List<WarehouseInventory> inventorylist = new ArrayList<WarehouseInventory>();
			for (int j = 0; j < array.size(); j++) {

				WarehouseInventory warehouseInventory = this.getVoData((JSONObject) array.get(j));
				inventorylist.add(warehouseInventory);
			}
			this.insertInventory(inventorylist);
			log.info("读取ERP库存的数量{}",inventorylist.size());
			return invCount;
		}
		
		
		/**
		 * 远程调用ERP获取库存服务取得数据
		* @Title: getData
		* @Description: TODO(这里用一句话描述这个方法的作用)
		* @param @param params
		* @param @return    参数
		* @return JSONObject    返回类型
		* @throws
		 *//*
		public JSONObject getData(Map<String,String> params) {
			// 取得数据
			return 
		}*/
		
		public void syncInventoryBySupplierSku(JSONArray skus) {
			Map<String, String> param =new HashMap<>();
			param.put("warehouseProvider", "利朗达");
			List<WarehouseSync> whList = warehouseSyncMapper.selectWarehouseByParam(param);
			Map<String, String> erpParam =new HashMap<>();
			erpParam.put("page", "1");
			erpParam.put("pageSize", String.valueOf(skus.size()));
			erpParam.put("sku", JSONObject.toJSONString(skus));
			for(WarehouseSync wh : whList) {
				erpParam.put("warehouse_code", wh.getWarehouseCode());
			// 取得数据
			JSONObject parseData=remoteErpService.getInventory(erpParam);
			JSONArray arrayList = parseData.getJSONArray("lists");
			log.info("根据供应商Sku同步ERP的库存列表：{}",arrayList);
			if (CollectionUtils.isEmpty(arrayList)) {
				return;
			}
			List<WarehouseInventory> insertlist = new ArrayList<>();
			for (int j = 0; j < arrayList.size(); j++) {
				WarehouseInventory warehouseInventory = this.getVoData((JSONObject) arrayList.get(j));
					if(warehouseInventory != null) {
					int updateCount = warehouseInventoryMapper.updateBysupplierSku(warehouseInventory);
					log.info("库存更新结果："+updateCount);
					if (updateCount > 0) {
						continue;
					}
					WarehouseInventory  commodity = warehouseInventoryMapper.getCommodityBySupplierSku(warehouseInventory.getSupplierSku());
					log.info("查询商品信息{}",JSONObject.toJSON(commodity));
					if(commodity != null) {
						warehouseInventory.setCommodityName(commodity.getCommodityName());
						warehouseInventory.setCommodityNameEn(commodity.getCommodityNameEn());
						warehouseInventory.setPinlianSku(commodity.getPinlianSku());
						warehouseInventory.setSupplier(commodity.getSupplier());
						warehouseInventory.setSupplierId(commodity.getSupplierId());
						warehouseInventory.setSupplierCompanyName(commodity.getSupplierCompanyName());
					try {
						warehouseInventory.setWarehouseNameEn(translation.transcation(warehouseInventory.getWarehouseName(),"zh", "en"));
					} catch (Exception e) {
						log.error("翻译仓库名称异常",e);
						e.printStackTrace();
					}
					insertlist.add(warehouseInventory);
					}
				}
			 }
				this.insertInventory(insertlist);
			}
		}


		private void insertInventory(List<WarehouseInventory> insertlist) {
			if(!CollectionUtils.isEmpty(insertlist))
			{
				int invCount=warehouseInventoryMapper.syncWarehouseInventory(insertlist);
				log.info("插入库存数量：{}",invCount);
			}
		}
		
		/**
		 * @Title: getVoData @Description: 设置VO值 @param @param
		 * vo @param @return @param @throws ParseException 参数 @return WarehouseInventory
		 * 返回类型 @throws
		 */
		public WarehouseInventory getVoData(JSONObject vo) {
			WarehouseInventory warehouseInventory = new WarehouseInventory();
			warehouseInventory.setWarehouseCode(vo.getString("warehouse_code"));
			warehouseInventory.setWarehouseName(vo.getString("warehouse_name"));
			warehouseInventory.setSupplierSku(vo.getString("sku"));
			//warehouseInventory.setSupplier("深圳市利朗达科技有限公司");//getLoginInfo.getSupplier()
			warehouseInventory.setInstransitQty(vo.getInteger("instransit_quantity"));
			warehouseInventory.setAvailableQty(vo.getInteger("available_quantity"));
			warehouseInventory.setQty(vo.getInteger("quantity"));
			warehouseInventory.setWaitingShippingQty(vo.getInteger("waiting_shipping_quantity"));
			warehouseInventory.setDefectsQty(vo.getInteger("defects_quantity"));
			warehouseInventory.setAllocatingQty(vo.getInteger("allocating_quantity"));
			warehouseInventory.setPictureUrl(vo.getString("thumb"));
			warehouseInventory.setSyncTime(convertDate(vo.getInteger("updated_time")));
			return warehouseInventory;
		}
		
		/**
		 * 
		 * @Title: convertDate @Description: 转换时间类型 @param @param
		 * seconds @param @return @param @throws ParseException 参数 @return Date
		 * 返回类型 @throws
		 */
		private static Date convertDate(Integer seconds) {
			try {
				Calendar c = Calendar.getInstance();
				long millions = new Long(seconds).longValue() * 1000;
				c.setTimeInMillis(millions);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateString = sdf.format(c.getTime());
				Date syncTime = sdf.parse(dateString);
				return syncTime;
			} catch (Exception e) {
				log.error("convert time is error.", e);
				return null;
			}
		}

}