package com.rondaful.cloud.commodity.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.commodity.constant.ComomodityIndexConst;
import com.rondaful.cloud.commodity.service.CommonJestIndexService;
import com.rondaful.cloud.commodity.service.ICommodityService;

public class InitEsThread extends Thread{

    private ICommodityService commodityService;
	
	private CommonJestIndexService commonJestIndexService;

	
	public InitEsThread() {}
	
	public InitEsThread(ICommodityService commodityService,CommonJestIndexService commonJestIndexService) {
		this.commodityService=commodityService;
		this.commonJestIndexService=commonJestIndexService;
	}
	
	@Override
    public void run() {
		//如果索引不存在，先创建索引以及设置指定字段分词方式，映射 
		if(!commonJestIndexService.indexExist(ComomodityIndexConst.INDEX_NAME)) {
        	JSONObject properties=new JSONObject();
        	JSONObject analysisField=new JSONObject();
        	JSONObject field=new JSONObject();
        	field.put("type", "text");
        	field.put("analyzer", "ik_smart");
        	field.put("search_analyzer", "ik_smart");
        	analysisField.put(ComomodityIndexConst.ANALYSIS_FIELD, field);
        	properties.put("properties", analysisField);
			commonJestIndexService.createIndex(ComomodityIndexConst.INDEX_NAME);
			commonJestIndexService.createIndexMapping(ComomodityIndexConst.INDEX_NAME,ComomodityIndexConst.TYPE_NAME,properties.toJSONString());
		}
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("isUp", true);
		commodityService.initCommodityIndex(map);
    }
}
