package com.rondaful.cloud.supplier.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.common.ExportUtil;
import com.rondaful.cloud.supplier.common.GetLoginInfo;
import com.rondaful.cloud.supplier.entity.DeliveryDetail;
import com.rondaful.cloud.supplier.entity.DeliveryRecord;
import com.rondaful.cloud.supplier.entity.InventoryDynamics;
import com.rondaful.cloud.supplier.entity.WarehouseInventory;
import com.rondaful.cloud.supplier.entity.WarehouseOperateInfo;
import com.rondaful.cloud.supplier.mapper.DeliveryRecordMapper;
import com.rondaful.cloud.supplier.mapper.InventoryDynamicsMapper;
import com.rondaful.cloud.supplier.mapper.DeliveryDetailMapper;
import com.rondaful.cloud.supplier.service.IDeliveryRecordService;
import com.sun.org.apache.commons.beanutils.BeanUtils;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
* @ClassName: DeliveryRecordServiceimpl
* @Description: 出库记录服务
* @author Administrator
* @date 2019年1月2日
*
 */
@Service
public class DeliveryRecordServiceimpl  implements IDeliveryRecordService{

	private final static Logger log = LoggerFactory.getLogger(DeliveryRecordServiceimpl.class);

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	@Autowired
	private DeliveryRecordMapper deliveryRecordMapper;
	
	@Autowired
	private DeliveryDetailMapper deliveryDetailMapper;
	
	@Autowired
	private InventoryDynamicsMapper inventoryDynamicsMapper;
	
	@Autowired
	GetLoginInfo getLoginInfo;
	
	@Autowired
	GetLoginUserInformationByToken loginUserInfo;
	
	private static final String OPERATE_TYPE = "订单出库";
	
	/******************************批量插入出库记录*****************************************/
	@Override
	public String insertBatchDeliveryRecord(List<DeliveryRecord> deliveryRecordList)  {
		int backCount=0;
		log.info("出库记录发货传值：{}",deliveryRecordList);
		for (DeliveryRecord deliveryRecord : deliveryRecordList) {
			deliveryRecord.setDeliveryId(this.createDeliveryId());
			deliveryRecord.setOrderStatus(1);
			deliveryRecord.setCreateDate(new Date());
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
				backCount=deliveryDetailMapper.insertBatchDeliveryDetail(dllist);
			
		}
		deliveryRecordMapper.insertBatchDeliveryRecord(deliveryRecordList);
		return String.valueOf(backCount);
	}
	
	/**
	 * 出库记录分页查询
	 */
	@Override
	public Page<DeliveryRecord> page(DeliveryRecord deliveryRecord) {
		List<DeliveryRecord> list = this.getDeliveryRecord(deliveryRecord);
		PageInfo<DeliveryRecord> pageInfo = new PageInfo<>(list);
		 return new Page<>(pageInfo);
	}
	
	/**
	 * 
	* @Title: getDeliveryRecord
	* @Description: 查询出库记录
	* @param @param deliveryRecord
	* @param @param supplier
	* @param @return    参数
	* @return List<DeliveryRecord>    返回类型
	* @throws
	 */
	private List<DeliveryRecord> getDeliveryRecord(DeliveryRecord deliveryRecord) {
		List<DeliveryRecord> drlist = new ArrayList<>();
		List<DeliveryDetail> dlList =new ArrayList<>();
		try {
			if(getLoginInfo.getUserInfo().getPlatformType()==0) {
				deliveryRecord.setSupplierId(getLoginInfo.getUserInfo().getTopUserId());
		    	if(CollectionUtils.isNotEmpty(getLoginInfo.getUserInfo().getwCodes())){
		    		deliveryRecord.setwCodes(getLoginInfo.getUserInfo().getwCodes());
		    	}
	   		}
	   		if(getLoginInfo.getUserInfo().getPlatformType()==2) {
	   			if(CollectionUtils.isNotEmpty(getLoginInfo.getUserInfo().getSuppliers())) {
	   				deliveryRecord.setSupplies(getLoginInfo.getUserInfo().getSuppliers());
	   			}
	   			
	   		}
				//查询出库记录
				drlist=deliveryRecordMapper.getDeliveryRecord(deliveryRecord);
				if(! CollectionUtils.isEmpty(drlist)) {
					//一对多，一个出库单号对应出库详情
					for(DeliveryRecord dr:drlist) {
						 dr.setDeliveryWarehouse(Utils.translation(dr.getDeliveryWarehouse()));
						 dr.setShipToCountryName(Utils.translation(dr.getShipToCountryName()));
						//根据出库单号查询出库记录详情
						dlList=this.getDeliveryDetail(dr.getDeliveryId());
				    		BigDecimal totalSellPrice=new BigDecimal("0");
				    		StringBuilder skus = new StringBuilder();
				    		StringBuilder skuCounts = new StringBuilder();
				    		StringBuilder itemPrices = new StringBuilder();
				    		List<String> supplierNames =new ArrayList<>();
				    		
				    		for(DeliveryDetail dl :dlList) {
				    			if(dlList.size()==1) {
				    				skus.append(dl.getSku());
				    				skuCounts.append(dl.getSkuQuantity().toString());
				    				itemPrices.append(dl.getItemPrice());
					    		}else {
					    			skus.append(dl.getSku()+"|");
					    			skuCounts.append(dl.getSkuQuantity()+"|");
					    			itemPrices.append(dl.getItemPrice()+"|");
					    		}
				    			String productPrice=dl.getItemPrice();
				    			BigDecimal price=new BigDecimal(productPrice).multiply(new BigDecimal(dl.getSkuQuantity()));
				    			totalSellPrice=totalSellPrice.add(price);
				    			supplierNames.add(dl.getSupplierName());
				    		}
				    		if(supplierNames.size()==1) dr.setSupplierName(supplierNames.get(0));
				    		dr.setDeliveryDetailList(dlList);
				    		if(dr.getOrderStatus()==0) {
				    			dr.setStatus("已退货");
				    		}
				    		if(dr.getOrderStatus()==1) {
				    			dr.setStatus("已发货");
				    		}
				    		if(dr.getOrderStatus()==2) {
				    			dr.setStatus("已完成");
				    		}
				    		dr.setSkuArr(skus.toString());
				    		dr.setSkuCountArr(skuCounts.toString());
				    		dr.setSkuPriceArr(itemPrices.toString());
				    		dr.setTotalSellPrice(totalSellPrice);
					}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return drlist;
	}

	/**
	 * 根据供应商取得出库单号
	* @Title: getDeliveryIds
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @return    参数
	* @return List<String>    返回类型
	* @throws
	 */
	private List<String> getDeliveryIds() {
		List<String> deliveryIds=deliveryDetailMapper.getDeliveryIdsBySupplier(getLoginInfo.getUserInfo().getTopUserId());//
		return deliveryIds;
	}
	
	/******************************出库单号生成*****************************************/
	private String createDeliveryId() {
		StringBuilder sb = new StringBuilder();
		sb.append("CK");
		sb.append(sdf.format(new Date()));
		sb.append(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6));
		return sb.toString();
	}

	/**
	 * 出库记录导出
	 */
	
	@Override
	public void exportDeliveryRecordExcel(DeliveryRecord param, HttpServletResponse response) {
		
		String title = "出库记录";
    	String[] colsName = new String[]{"出库单号","订单号","供应商","商品","商品单价（元）","商品数量","总售价（元）","目的地","发货仓库","订单状态","创建时间","收货时间","退货时间"};
    	List<Object[]>  dataList = new ArrayList<Object[]>();
		List<DeliveryRecord> invList = this.getDeliveryRecord(param);
		Object[] objs = null;
		for (DeliveryRecord dr:invList) {
			objs = new Object[colsName.length];
			objs[0] = dr.getDeliveryId();
			objs[1] = dr.getSysOrderId();
			objs[2] = dr.getSupplierName();
			objs[3] = dr.getSkuArr();
			objs[4] = dr.getSkuPriceArr();
			objs[5] = dr.getSkuCountArr();
			objs[6] = dr.getTotalSellPrice();
			objs[7] = dr.getShipToCountryName();
			objs[8] = dr.getDeliveryWarehouse();
			objs[9] = dr.getStatus();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(dr.getCreateDate() != null) {
				String createDate = df.format(dr.getCreateDate());
				objs[10] = createDate;
			}
			if(dr.getReceiveDate()!= null) {
				String receiveDate = df.format(dr.getReceiveDate());
				objs[11] = receiveDate;
				
			}
			if(dr.getRejectDate()!= null) {
				String rejectDate = df.format(dr.getRejectDate());
				objs[12] = rejectDate;
				
			}
			dataList.add(objs);
		}
		try {
			ExportUtil ex=new ExportUtil(title,colsName,"出库记录导出",dataList, response);
			ex.export();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    
    /**
	 * 取得出库记录详情分页
	 */
	@Override
	public List<DeliveryDetail> getDeliveryDetail(String  deliveryId){
		Map<String,Object> param=new HashMap<>();
		param.put("deliveryId", deliveryId);
		param.put("supplierId",  null);
		List<DeliveryDetail> list = deliveryDetailMapper.getDeliveryDetail(param);
		for(DeliveryDetail dl :list) {
			String productPrice=dl.getItemPrice();
			BigDecimal price=new BigDecimal(productPrice).multiply(new BigDecimal(dl.getSkuQuantity()));
			dl.setSkuTotal(price);
		}
		
		return list;
	}

	/**
	 * 根据系统订单更新状态
	 */
	@Override
	public void updateOrderStatusBySourceOrder(Map<String, Object> param) {
		deliveryRecordMapper.updateOrderStatusBySourceOrder(param);
		
	}
	
	/**
	 * 根据供应商查询出库商品的总数,出库商品的总金额
	 */
	@Override
	public DeliveryRecord getDeliveryCount() {
		DeliveryRecord dr=new DeliveryRecord();
		dr.setSkuCountArr(String.valueOf(deliveryDetailMapper.getSkuCount(getLoginInfo.getUserInfo().getTopUserId()))); 
		dr.setSkuPriceArr(String.valueOf(deliveryDetailMapper.getSkuPriceTotal(getLoginInfo.getUserInfo().getTopUserId())));
		return dr;
		
	}
}
