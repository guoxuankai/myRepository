package com.rondaful.cloud.commodity.listen;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.rondaful.cloud.commodity.dto.ExcelToSku;
import com.rondaful.cloud.commodity.entity.CommodityBase;
import com.rondaful.cloud.commodity.entity.SkuImport;
import com.rondaful.cloud.commodity.mapper.SkuImportMapper;
import com.rondaful.cloud.commodity.service.SkuOtherService;
import com.rondaful.cloud.commodity.utils.WebFileUtils;
import com.rondaful.cloud.common.utils.ExcelUtil;


/**
* @Description:处理导入sku的Excel
* @author:范津 
* @date:2019年6月20日 下午3:00:55
 */
@Component
public class HandleSkuExcelListen implements ApplicationRunner{
	
	
	private final static Logger log = LoggerFactory.getLogger(HandleSkuExcelListen.class);
	
	ConcurrentLinkedQueue<Map<String, Object>> queue=new ConcurrentLinkedQueue<Map<String, Object>>();
	
	public ConcurrentLinkedQueue<Map<String, Object>> getQueue() {
		return queue;
	}
	public void setQueue(ConcurrentLinkedQueue<Map<String, Object>> queue) {
		this.queue = queue;
	}

	@Autowired
	private SkuOtherService skuOtherService;
	
	@Autowired
	private SkuImportMapper skuImportMapper;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000*60*1);
						Map<String, Object> map=queue.poll();
						if (map != null) {
							Long importId=(Long) map.get("id");
							boolean isAdmin=(boolean) map.get("isAdmin");
							SkuImport record=skuImportMapper.selectByPrimaryKey(importId);
							if (record != null) {
								String url = (String) map.get("url");
								InputStream inStream=WebFileUtils.getFileInputStreamByUrl(url);
								ExcelUtil<CommodityBase> excelRead = new ExcelUtil<>(inStream);
								List<CommodityBase> list = excelRead.read(new ExcelToSku(), 2);
								if (list != null && list.size()>0) {
									String result="";
									if (record.getImType() != null) {
										if (record.getImType()==1) {
											result=skuOtherService.addSkuByExcel(list,record.getOptUser(),record.getSupplierId(),importId,isAdmin);
										}else if (record.getImType()==2) {
											result=skuOtherService.updateSkuByExcel(list,record.getOptUser(),record.getSupplierId(),importId);
										}
									}
					    			
					    			if (StringUtils.isNotBlank(result)) {
					    				//更新任务表状态及结果
						    			record.setStatus(1);
						    			record.setTaskDetail(result);
						    			skuImportMapper.updateByPrimaryKeySelective(record);
									}
								}
							}
						}
					} catch (Exception e) {
						log.error("监听处理导入sku的Excel异常",e);
					}
				}
			}
			
		}.start();
	}

}
