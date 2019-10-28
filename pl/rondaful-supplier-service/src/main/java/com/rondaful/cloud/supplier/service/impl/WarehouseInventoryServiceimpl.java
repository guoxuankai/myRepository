package com.rondaful.cloud.supplier.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.rabbitmq.MessageSender;
import com.rondaful.cloud.common.service.TranslationService;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.common.ExportUtil;
import com.rondaful.cloud.supplier.common.GetLoginInfo;
import com.rondaful.cloud.supplier.common.SyncInventoryUtil;
import com.rondaful.cloud.supplier.dto.AuthorizeDTO;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.dto.WarehouseInventoryExportDTO;
import com.rondaful.cloud.supplier.entity.*;
import com.rondaful.cloud.supplier.listen.ThreadListen;
import com.rondaful.cloud.supplier.mapper.InventoryDynamicsMapper;
import com.rondaful.cloud.supplier.mapper.WareHouseAuthorizeMapper;
import com.rondaful.cloud.supplier.mapper.WarehouseInventoryMapper;
import com.rondaful.cloud.supplier.mapper.WarehouseSyncMapper;
import com.rondaful.cloud.supplier.remote.RemoteCommodityService;
import com.rondaful.cloud.supplier.remote.RemoteErpService;
import com.rondaful.cloud.supplier.remote.RemoteGranaryService;
import com.rondaful.cloud.supplier.remote.RemoteMessageService;
import com.rondaful.cloud.supplier.remote.RemoteUserService;
import com.rondaful.cloud.supplier.service.IWarehouseInventoryService;
import com.rondaful.cloud.supplier.vo.MessageNoticeVo;
import org.springframework.beans.BeanUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @ClassName: WarehouseInventoryServiceimpl
 * @Description: 仓库库存
 * @author lxx
 * @date 2018年12月5日
 *
 */
@Service
public class WarehouseInventoryServiceimpl implements IWarehouseInventoryService {

	private final static Logger log = LoggerFactory.getLogger(WarehouseInventoryServiceimpl.class);

	@Value("${erp.url}")
	private  String erpUrl;

	@Autowired
	private TranslationService translation;
	
	@Autowired
    private MessageSender messageSender;

	@Autowired
	WarehouseSyncMapper warehouseSyncMapper;

	@Autowired
	WarehouseInventoryMapper warehouseInventoryMapper;

	@Autowired
	ThreadListen threadListen;

	@Autowired
	RemoteCommodityService remoteCommodityService;

	@Autowired
	private InventoryDynamicsMapper inventoryDynamicsMapper;

	@Autowired
	GetLoginInfo getLoginInfo;

	@Autowired
	RemoteMessageService  remoteMessageService;

	@Autowired
	RemoteUserService remoteUserService;

	@Autowired
	RemoteGranaryService remoteGranaryService;
	
	@Autowired
	RemoteErpService remoteErpService;

	@Autowired
	SyncInventoryUtil syncInventoryUtil;


	private WarehouseInventory warehouseInventory;
	
	@Autowired
	private WareHouseAuthorizeMapper authorizeMapper;


	/**
	 *
	 * @Title: syncWarehouseInventory @Description:ERP推送的数据放到队列 @param 参数 @return
	 * void 返回类型 @throws
	 */
	@Override
	public void syncWarehouseInventory(List<WarehouseInventory> inventoryList) {
		log.info("把ERP更新的数据放入队列"+inventoryList.size());
		threadListen.getQueue().add(inventoryList);
	}
	
	/**
	 *  根据skus同步ERP库存
	 */
	@Override
	public void syncERPInventory() {
		int count = warehouseInventoryMapper.getAllSupplierSkusCount();
		log.info("商品总数量：{}",count);
		int pageSize = 100;
		int pageCount = (count + pageSize - 1) / pageSize;
		log.info("以100条分页的总页数：{}",pageCount);
		int rowNo = 1;
		for (int i = 0; i < pageCount; i++) {
			Map<String, Integer> pgInfo = new HashMap<>();
			pgInfo.put("pstart", rowNo);
			rowNo += pageSize;
			pgInfo.put("psize", pageSize);
			List<String> supplierSkuList = warehouseInventoryMapper.getAllSupplierSkus(pgInfo);
			log.info("同步库存的供应商SKU列表：{}",JSONObject.toJSON(supplierSkuList));
			syncInventoryUtil.syncInventoryBySupplierSku(JSONArray.parseArray(JSONObject.toJSONString(supplierSkuList)));
		}

	}

	/**
	 *  分页同步ERP库存
	 */
	private void getInitWarehouseInventory(String warehouseCode, Integer currPage, int count) {
		Map<String,String> params = new HashMap<>();
		params.put("warehouse_code", warehouseCode);
		params.put("page", String.valueOf(currPage));
		params.put("pageSize", "1000");
		// 取得数据
		JSONObject parseData = remoteErpService.getInventory(params);  // start page 1
		Integer totalCount = parseData.getInteger("count");
		log.info("ERP的库存总数量》》{}",totalCount);
		JSONArray array = parseData.getJSONArray("lists");
		if (CollectionUtils.isEmpty(array)) {
			return;
		}
		 syncInventoryUtil.syncInventory(array);
		count += array.size();
		log.info("每页的数量：" + count);
		if (count <= totalCount) {
			this.getInitWarehouseInventory(warehouseCode, ++currPage, count);
		}

	}
	/**
	 * 根据供应商同步库存
	 */
	@Override
	public void syncInventoryBySupplierSku(String skus) {
		syncInventoryUtil.syncInventoryBySupplierSku(JSONArray.parseArray(skus));
	}
	/**
	 * 同步谷仓库存
	* @Title: syncGranaryInventory
	* @Description: 同步谷仓库存
	* @return void    返回类型
	* @throws
	 */

	@Override
	public void syncGranaryInventory() {
		List<AuthorizeDTO> accountList=authorizeMapper.getAuthorizeList();
		if( ! CollectionUtils.isEmpty(accountList)) {
			for(AuthorizeDTO accInfo : accountList) {
				try {
					this.insertGranaryInvData(accInfo,1);
				} catch (Exception e) {
					log.error("调用谷仓服务异常:{0}",e);
					e.printStackTrace();
				}
			}
		}
	}

	private void insertGranaryInvData(AuthorizeDTO accInfo ,Integer currPage) throws Exception {
		Map<String,String> params = new HashMap<>();
		params.put("pageSize", String.valueOf(1000));
		params.put("page", String.valueOf(currPage));
		JSONObject body= remoteGranaryService.getInventory(params,accInfo.getAppToken(),accInfo.getAppKey());
		log.info("返回的库存数据：{}",body);
		String ask =  body.getString("ask");
		String message =  body.getString("message");
		if(! "Success".equals(ask) && ! "Success".equals(message)) {
			log.error("调用谷仓获取仓库服务异常：{}",body.getString("Error"));
			JSONObject convertError = JSONObject.parseObject(body.getString("Error"));
			throw new GlobalException(convertError.getString("errCode"), convertError.getString("errMessage"));
		}
		Boolean nextPage = body.getBoolean("nextPage");
		JSONArray arrayList = body.getJSONArray("data");
		if (CollectionUtils.isEmpty(arrayList)) {
			return;
		}
		List<WarehouseInventory> invList = new  ArrayList<>();
		for (int j = 0; j < arrayList.size(); j++) {
			JSONObject vo = (JSONObject) arrayList.get(j);
			WarehouseInventory  commodity = warehouseInventoryMapper.getCommodityByPinlianSku(vo.getString("product_sku"));
			log.info("查询商品信息{}",JSONObject.toJSON(commodity));
			if(commodity != null) {
				WarehouseInventory warehouseInventory = new WarehouseInventory();
				warehouseInventory.setProductBarcode(vo.getString("product_barcode"));
				warehouseInventory.setPinlianSku(vo.getString("product_sku"));
				warehouseInventory.setWarehouseCode("GC_"+accInfo.getCompanyCode()+"_"+vo.getString("warehouse_code"));
				warehouseInventory.setWarehouseName(accInfo.getCustomName()+ "-"+vo.getString("warehouse_desc"));
				warehouseInventory.setInstransitQty(vo.getInteger("onway"));
				warehouseInventory.setPendingQty(vo.getInteger("pending"));
				warehouseInventory.setAvailableQty(vo.getInteger("sellable"));
				warehouseInventory.setDefectsQty(vo.getInteger("unsellable"));
				warehouseInventory.setWaitingShippingQty(vo.getInteger("reserved"));
				warehouseInventory.setShippedQty(vo.getInteger("shipped"));
				warehouseInventory.setSoldSharedQty(vo.getInteger("sold_shared"));
				warehouseInventory.setStockingQty(vo.getInteger("stocking"));
				warehouseInventory.setPinoStockQty(vo.getInteger("pi_no_stock"));
				warehouseInventory.setTuneOutQty(vo.getInteger("tune_out"));
				warehouseInventory.setTuneInQty(vo.getInteger("tune_in"));
				warehouseInventory.setProductSalesValue(vo.getString("product_sales_value"));
				int result = warehouseInventoryMapper.updateByPinlianSku(warehouseInventory);
				//如果更新结果返回1,则继续下一次循环
				if(result > 0) {
					continue;
				}
				warehouseInventory.setCommodityName(commodity.getCommodityName());
				warehouseInventory.setCommodityNameEn(commodity.getCommodityNameEn());
				warehouseInventory.setSupplierSku(commodity.getSupplierSku());
				warehouseInventory.setSupplier(commodity.getSupplier());
				warehouseInventory.setSupplierId(commodity.getSupplierId());
				warehouseInventory.setSupplierCompanyName(commodity.getSupplierCompanyName());
				warehouseInventory.setPictureUrl(commodity.getPictureUrl());
				warehouseInventory.setWarehouseNameEn(translation.transcation(warehouseInventory.getWarehouseName(), "zh", "en" ));
				invList.add(warehouseInventory);
			}
		}
		warehouseInventoryMapper.syncGranaryInventory(invList);
		if(nextPage) { insertGranaryInvData(accInfo, ++currPage);}
	}


	/**
	 * 库存分页查询
	 */
	@Override
	public Page<WarehouseInventory> page(WarehouseInventory warehouseInventory ) {
		List<WarehouseInventory> inventoryList = warehouseInventoryMapper.page(warehouseInventory);
		if(! CollectionUtils.isEmpty(inventoryList)) {
			for(WarehouseInventory inv:inventoryList) {
				if(inv.getWarnVal()==null) {
					inv.setWarnVal(-1);
				}
			}
		}
		PageInfo<WarehouseInventory> pageInfo = new PageInfo (inventoryList);
		 return new Page(pageInfo);
	}

	/**
	 * 批量更新预警值
	 */
	@Override
	public void updateBatchWarnVal(Map<String, Object> param) {
		List<WarehouseInventory> inventoryList=warehouseInventoryMapper.selectByPrimaryKey(param);
		if(!CollectionUtils.isEmpty(inventoryList)) {
			for(WarehouseInventory inv:inventoryList) {
				if((Integer)param.get("warnVal") == -1) {
					inv.setWarnVal(null);
				}else {
					inv.setWarnVal((Integer)param.get("warnVal"));
				}
			}
			warehouseInventoryMapper.updateBatchWarnVal(inventoryList);
		}
	}

	/**
	 *
	 * @Title: getAvailableQty @Description: 根据供应商sku返回库存可用量 @param 参数 @return void
	 * List<WarehouseInventory> @throws
	 */
	@Override
	public List<WarehouseInventory> getAvailableQty(Map<String, Object> param) {

		List<WarehouseInventory> availableQtys = warehouseInventoryMapper.getInventoryListByPlSku(param);
		log.info("查询库存可用数"+availableQtys);
		return availableQtys;
	}
	
	@Override
	public Integer getInvAvailableQtyByParam(Map<String, String> param) {
		return warehouseInventoryMapper.getInvAvailableQtyByParam(param);
	}

	/**
	 * 库存列表导出
	 */
	@Override
	public void exportInventoryExcel(WarehouseInventory param,String ids,HttpServletResponse response) {
		/*log.info("导出库存:dto={}",param.toString());
		int currPage = 1;
		int pageSize = 2000;
		Workbook workbook = getPageDTO(param, ids, currPage, pageSize);
		ExcelExportUtil.closeExportBigExcel();
        // 写出数据输出流到页面
        try {
        	String fileName="库存明细导出";
        	response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xls");
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
        	log.error("库存明细下载异常{}",e);
            e.printStackTrace();
        }*/


	}

	private Workbook getPageDTO(WarehouseInventory param, String ids, int currPage, int pageSize) {
		PageDTO<WarehouseInventoryExportDTO> result=new PageDTO<>();
		List<WarehouseInventoryExportDTO> data=new ArrayList<>();
		if(StringUtils.isBlank(ids)) {
			PageHelper.startPage(currPage,pageSize);
		}
		List<WarehouseInventory> invList =writeData(param,ids);
		PageInfo<WarehouseInventory> pageInfo=new PageInfo<>(invList);
		result.setTotalCount((int)pageInfo.getTotal());
		result.setCurrentPage(currPage);
		log.info("当前页{}",currPage);
		if(CollectionUtils.isNotEmpty(pageInfo.getList())) {
			for (WarehouseInventory inv:pageInfo.getList()) {
				WarehouseInventoryExportDTO exportDTO = new WarehouseInventoryExportDTO();
				BeanUtils.copyProperties(inv,exportDTO);
/*				exportDTO.setPictureUrl(inv.getPictureUrl());
				exportDTO.setSupplier(inv.getSupplier());
				exportDTO.setPinlianSku(inv.getPinlianSku());
				exportDTO.setSupplierSku(inv.getSupplierSku());
				exportDTO.setCommodityName(inv.getCommodityName());
				exportDTO.setStatus(inv.getStatus());*/
				exportDTO.setWarehouseName(inv.getWarehouseName()+"/"+inv.getWarehouseCode());
				exportDTO.setInstransitQty((inv.getInstransitQty()+inv.getAllocatingQty())+"/--");
				exportDTO.setAvailableQty(inv.getAvailableQty()+"/"+inv.getDefectsQty());
				exportDTO.setWaitingShippingQty(inv.getWaitingShippingQty());
				String stockingQty = inv.getStockingQty() == null ? "--" : inv.getStockingQty().toString();
				String pinoStockQty = inv.getPinoStockQty() == null ? "--" : inv.getPinoStockQty().toString();
				exportDTO.setStockingQty(stockingQty+"/"+pinoStockQty);
				String tuneOut=inv.getTuneOutQty() == null ? "--" : inv.getTuneOutQty().toString();
				String tuneIn=inv.getTuneInQty() == null ? "--" : inv.getTuneInQty().toString();
				exportDTO.setTuneOutQty(tuneOut +"/"+ tuneIn );
				
				exportDTO.setSyncTime(inv.getLastUpdateDate());
				data.add(exportDTO);
			}
		}
		result.setList(data);
		Workbook workbook=null;
		if (CollectionUtils.isNotEmpty(result.getList())){
		    //workbook= ExcelExportUtil.exportBigExcel(new ExportParams(null, "Sheet1",ExcelType.HSSF),WarehouseInventoryExportDTO.class,result.getList());
		}
		//int pageCount = (result.getTotalCount() + pageSize - 1) / pageSize; //以pageSize条分页的总页数
		if(currPage*pageSize < result.getTotalCount()) {
			getPageDTO(param, ids, ++currPage, pageSize);
		}
		return workbook;
	}

	/**
	 * 管理后台库存列表导出
	 */
	@Override
	public void exportInventoryExcelByCms(WarehouseInventory param,String ids,HttpServletResponse response) {

		String title = "库存列表";
    	String[] rowsName = new String[]{"图片","供应商","品连sku","供应商sku","商品名称","仓库名称/代码","在途/待上架","可售/不可售","待出库","待调入/待调出","预警值","数据更新时间","库存状态"};
    	List<Object[]>  dataList = new ArrayList<Object[]>();
		List<WarehouseInventory> invList =writeData(param,ids);
		Object[] objs = null;
		for (WarehouseInventory inv:invList) {
			objs = new Object[rowsName.length];
			objs[0] = inv.getPictureUrl();
			objs[1] = inv.getSupplierCompanyName();
			objs[2] = inv.getPinlianSku();
			objs[3] = inv.getSupplierSku();
			objs[4] = inv.getCommodityName();
			objs[5] = inv.getWarehouseName()+"/"+inv.getWarehouseCode();
			objs[6] = (inv.getInstransitQty()+inv.getAllocatingQty())+"/--";
			objs[7] = inv.getAvailableQty()+"/"+inv.getDefectsQty();
			objs[8] = inv.getWaitingShippingQty();
			objs[9] = "--/--";
			objs[10] = inv.getWarnVal();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sycDate = df.format(inv.getSyncTime());
			objs[11] = sycDate;
			objs[12] = inv.getStatus();
			dataList.add(objs);
		}
		try {
			ExportUtil ex=new ExportUtil(title,rowsName,"仓库列表导出",dataList,response);
			ex.export();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	/**
	 * 库存动态导出
	 */
	@Override
	public void exportInventoryDynamics(InventoryDynamics inventoryDynamics, String ids, HttpServletResponse response) {

		String title = "库存动态列表";
    	String[] rowsName = new String[]{"品连sku","供应商sku","仓库名称","操作类型","变更库存","操作时间","操作人员"};
    	List<Object[]>  dataList = new ArrayList<Object[]>();
		List<InventoryDynamics> invList =new ArrayList<>();
				if(StringUtils.isBlank(ids)) {
					invList = inventoryDynamicsMapper.page(inventoryDynamics);
		    	}else {
		    		JSONArray array =JSONObject.parseArray(ids);
		    		List<Integer> idList=array.toJavaList(Integer.class);
		    		Map<String,Object> map=new HashMap<>();
		    		map.put("ids", idList);
		    		map.put("supplier", inventoryDynamics.getSupplier());
		    		invList=inventoryDynamicsMapper.getInventoryDyListByIds(map);
		    	}
		Object[] objs = null;
		for (InventoryDynamics invDy:invList) {
			objs = new Object[rowsName.length];
			objs[0] = invDy.getSku();
			objs[1] = invDy.getSupplierSku();
			objs[2] = invDy.getWarehouseName();
			objs[3] = invDy.getOperateType();
			objs[4] = invDy.getAlertInventory();
			String operateDate=null;
			if(invDy.getOperateDate()!=null) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				 operateDate = df.format(invDy.getOperateDate());
			}
			objs[5] = operateDate ;
			objs[6] = invDy.getOperateBy();
			dataList.add(objs);
		}
		try {
			ExportUtil ex=new ExportUtil(title,rowsName,"库存动态导出",dataList,response);
			ex.export();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	/**
     *
     *   	把数据写入Excel中
     */
    private List<WarehouseInventory> writeData(WarehouseInventory param,String ids) {
    	List<WarehouseInventory> list =new ArrayList<>();
    	if(StringUtils.isBlank(ids)) {
    		list = warehouseInventoryMapper.page(param);
    	}else {
    		JSONArray array =JSONObject.parseArray(ids);
    		List<Integer> idList=array.toJavaList(Integer.class);
    		Map<String,Object> map=new HashMap<>();
    		map.put("ids", idList);
    		map.put("supplierId", param.getSupplierId());
    		list=warehouseInventoryMapper.getInventoryListByIds(map);
    	}
    	log.info("记录数"+list.size());
    	return list;
    }

    /**
     * 筛选SKU,仓库编码，卖家数量返回仓库库存
     */
	@Override
	public  Map<String,Object> getInventoryListBySkus(List<String> skus) {
		Map<String,Object> mapList=new HashMap<>();
		List<WarehouseInventory> listTotal=new ArrayList<>();
			if(CollectionUtils.isNotEmpty(skus)) {
				for(String sku:skus) {
					 List<WarehouseInventory> list = warehouseInventoryMapper.getInventoryListBySku(sku);
			            listTotal.addAll(list);
				}
			}
				Map<String, List<WarehouseInventory>> groupByMap = listTotal.stream().collect(Collectors.groupingBy(WarehouseInventory::getWarehouseCode));
				for (Map.Entry<String, List<WarehouseInventory>> entry : groupByMap.entrySet()) {
					 List<WarehouseInventory> list = entry.getValue();
					int size=entry.getValue().size();
					 if(size == skus.size()){
						 mapList.put(entry.getKey(), list);
					 }
		           log.info("----------------品连sku对应的仓库----------------" + entry.getKey());
				}
		return mapList;
	}

	public  Map<String,Object> getInventoryListByParams(List<WarehouseInventory> invList) {
		Map<String,Object> reslutMap=new HashMap<>();
		Map<String,Object> map=new HashMap<>();
		List<WarehouseInventory> listTotal=new ArrayList<>();
		for(WarehouseInventory inv:invList) {
			Map<String,Object> param=new HashMap<>();
			 param.put("pinlianSku", inv.getPinlianSku());
			 param.put("qty", inv.getQty());
			 param.put("warehouseCode", inv.getWarehouseCode());
//			 param.put("supplierId",getLoginInfo.getSupplierId());
			warehouseInventory = warehouseInventoryMapper.getInventoryListByParams(param);
			if(warehouseInventory==null) {
				map.put(inv.getPinlianSku(), "在仓库不存在");
			}else {
				if("库存不足".equals(warehouseInventory.getStatus())) map.put(inv.getPinlianSku(), warehouseInventory.getAvailableQty());
				if("正常".equals(warehouseInventory.getStatus())) {
					map.put(inv.getPinlianSku(), warehouseInventory.getAvailableQty());
					listTotal.add(warehouseInventory);
				}
			}
		}
		if(listTotal.size()==invList.size()) {
			reslutMap.put("true",map );
		}else {
			reslutMap.put("false", map);
		}
		return reslutMap;
	}
	/**
	 * 取得供应商分页
	 */
	@Override
	public Page<InventoryDynamics> pageDynamics(InventoryDynamics inventoryDynamics) {
		//取得库存动态记录
		List<InventoryDynamics> list =  inventoryDynamicsMapper.page(inventoryDynamics);
		for(InventoryDynamics dy : list) {
			dy.setOperateType(Utils.translation(dy.getOperateType()));
			dy.setWarehouseName(Utils.translation(dy.getWarehouseName()));
		}
		PageInfo<WarehouseOperateInfo> pageInfo = new PageInfo(list);
		 return new Page(pageInfo);
	}

	/**
	 * 库存预警发送消息
	 */
	@Override
	public void inventoryWarnNotice() {
		List<WarehouseInventory> invList=warehouseInventoryMapper.getInventoryListByWarn();
		    log.info("预警结果列表"+invList.size());
		List<String> supplierList=new ArrayList<>();
		if( ! CollectionUtils.isEmpty(invList)) {
			for(WarehouseInventory inv : invList) {
				MessageNoticeVo param = new MessageNoticeVo();
				String content=inv.getPinlianSku()+"#"+inv.getWarehouseName()+"#"+inv.getWarnVal();
				param.setMessageCategory("INVENTORY_NOTICE");
				param.setMessageContent(content);
				param.setMessagePlatform("0");
				/*supplierList.add(inv.getSupplierCompanyName());
				Object userNameList=  remoteUserService.getUserNameByCompanyName(supplierList);
				log.info("远程根据供应商公司名查询主用户名的结果"+userNameList);
				String body=JSONObject.toJSONString(userNameList);
				JSONObject convertBody = JSONObject.parseObject(body);
		    	JSONArray array=JSON.parseArray(convertBody.getString("data"));*/
				param.setMessageScceptUserName(inv.getSupplier());
				param.setMessageType("INVENTORY_EARLY_WARNING_NOTICE");
				param.setReceiveSys("0");
				log.info("库存预警发送消息的结果{}",param);
				messageSender.sendMessage(JSONObject.toJSONString(param));
				//Object messageInfo=remoteMessageService.dispose(param);
				supplierList.clear();
			}
		}

	}

	/**
	 * 根据供应商id查询库存报表统计
	 */
	@Override
	public WarehouseInventoryReport getInvCommidtyReport() {
		WarehouseInventoryReport invReport = new WarehouseInventoryReport();
		invReport.setEntInvWareHouseCount(warehouseInventoryMapper.getEntInvWareHouseCount(getLoginInfo.getUserInfo().getTopUserId()));
		invReport.setInvCommidtyTotal(warehouseInventoryMapper.getInvCommidtyTotal(getLoginInfo.getUserInfo().getTopUserId()));
		invReport.setWarnInvCommidtyTotal(warehouseInventoryMapper.getWarnInvCommidtyTotal(getLoginInfo.getUserInfo().getTopUserId()));
		return  invReport;

	}
	
	/**
	 * sku映射表插入
	 */
	@Override
	public void insertSupplierSkuMap() {

			this.getCommodityInfo(1);

	}
	/**
	 * 远程商品服务取得商品信息
	 */
	private void getCommodityInfo(Integer currPage){
		// 远程商品服务，获得品连SKUS
					String body = JSONObject.toJSONString(remoteCommodityService.getSkuList(String.valueOf(currPage), "100",null));
						JSONObject convertBody = JSONObject.parseObject(body);
						JSONObject convertData = JSONObject.parseObject(convertBody.getString("data"));
						JSONObject convertPgInfo = JSONObject.parseObject(convertData.getString("pageInfo"));
						log.info("分页商品信息：{}",convertPgInfo);
						Integer endRow=convertPgInfo.getInteger("endRow");
						log.info("商品分页的数量：{}",endRow);
						if(endRow != 0) {
							JSONArray parseArray=JSONObject.parseArray(convertPgInfo.getString("list"));
							if (parseArray.size() != 0) {
								List<WarehouseInventory> inventoryList = new ArrayList<>();
								for (int j = 0; j < parseArray.size(); j++) {
									WarehouseInventory wi = new WarehouseInventory();
									JSONObject vo = (JSONObject) parseArray.get(j);
									wi.setSupplierSku(vo.getString("supplierSku"));
									wi.setPinlianSku(vo.getString("systemSku"));
									wi.setCommodityName(vo.getString("name"));
									wi.setSupplier(vo.getString("supplierName"));
									wi.setSupplierId(vo.getInteger("supplierId"));
									wi.setSupplierCompanyName(vo.getString("supplierCompanyName"));
									inventoryList.add(wi);
								}
								int skuCount=warehouseInventoryMapper.insertSupplierCommodity(inventoryList);
								log.info("插入商品的数量：{}",skuCount);
							}
							this.getCommodityInfo(++currPage);
					}
	}


}
