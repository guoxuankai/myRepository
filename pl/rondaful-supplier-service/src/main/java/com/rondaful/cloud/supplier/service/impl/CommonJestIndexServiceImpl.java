package com.rondaful.cloud.supplier.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.rondaful.cloud.supplier.service.CommonJestIndexService;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.indices.*;
import io.searchbox.indices.mapping.GetMapping;
import io.searchbox.indices.mapping.PutMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CommonJestIndexServiceImpl  implements CommonJestIndexService {

	private final static Logger log = LoggerFactory.getLogger(CommonJestIndexServiceImpl.class);
	
	@Resource
    private JestClient jestClient;
	
	
	/**
     * 创建index
     */
    public void createIndex(String index) {
        try {
            JestResult jestResult = jestClient.execute(new CreateIndex.Builder(index).build());
            log.info("createIndex:{}", jestResult.isSucceeded());
        } catch (IOException e) {
            log.error("createIndex error:", e);
        }
    }
    
    /**
     * 设置index的mapping
     */
    public void createIndexMapping(String index, String type, String mappingString) {
        PutMapping.Builder builder = new PutMapping.Builder(index, type, mappingString);
        try {
            JestResult jestResult = jestClient.execute(builder.build());
            log.info("createIndexMapping result:{}", jestResult.isSucceeded());
            if (!jestResult.isSucceeded()) {
                log.error("settingIndexMapping error:{}", jestResult.getErrorMessage());
            }
        } catch (IOException e) {
            log.error("settingIndexMapping error:", e);
        }
    }
    
    /**
     * 追加mapping字段
     *
     * @param index     index名称
     * @param type      type名称
     * @param fieldName 字段名称
     * @param fieldType 字段类型
     */
    public void addFieldMapping(String index, String type, String fieldName, String fieldType) {
        try {
            //设置分词
        	Map<String, Object> map5=new HashMap<String, Object>();
        	Map<String, Object> map4=new HashMap<String, Object>();
        	Map<String, Object> map3=new HashMap<String, Object>();
        	Map<String, Object> map2=new HashMap<String, Object>();
        	Map<String, Object> map1=new HashMap<String, Object>();
        	Map<String, Object> map0=new HashMap<String, Object>();
        	
        	map5.put("type", "text");
        	map5.put("analyzer", "ik_smart");//准确率高，使用ik_smart分词；召回率高，使用ik_max_word分词
        	map4.put("ik", map5);
        	map3.put("fields", map4);
        	map3.put("type", fieldType);
        	map2.put(fieldName, map3);
        	map1.put("properties", map2);
        	map0.put(type, map1);
        	String mapping=JSONObject.toJSONString(map0);
          
            PutMapping.Builder builder = new PutMapping.Builder(index, type, mapping);
            JestResult jestResult = jestClient.execute(builder.build());
            
            log.info("addFieldMapping result:{}", jestResult.isSucceeded());
            if (!jestResult.isSucceeded()) {
                log.error("addFieldMapping error:{}", jestResult.getErrorMessage());
            }
        } catch (IOException e) {
            log.error("addFieldMapping error", e);
        }
    }
    
    /**
     * 获取index的mapping
     */
    public String getMapping(String indexName, String typeName) {
        GetMapping.Builder builder = new GetMapping.Builder();
        builder.addIndex(indexName).addType(typeName);
        try {
            JestResult result = jestClient.execute(builder.build());
            if (result != null && result.isSucceeded()) {
                return result.getSourceAsObject(JsonObject.class).toString();
            }
        } catch (Exception e) {
            log.error("getMapping error", e);
        }
        return null;
    }
    
    /**
     * 判断index是否存在
     */
    public boolean indexExist(String index) {
        IndicesExists indicesExists = new IndicesExists.Builder(index).build();
        try {
            JestResult jestResult = jestClient.execute(indicesExists);
            if (jestResult != null) {
                return jestResult.isSucceeded();
            }
        } catch (IOException e) {
            log.error("indexExist error", e);
        }
        return false;
    }
    
    /**
     * 删除index
     */
    public void deleteIndex(String index) {
        try {
            JestResult jestResult = jestClient.execute(new DeleteIndex.Builder(index).build());
            log.info("deleteIndex result:{}", jestResult.isSucceeded());
        } catch (IOException e) {
            log.error("deleteIndex error", e);
        }
    }

    /**
     * 索引优化
     */
    public void optimizeIndex() {
        Optimize optimize = new Optimize.Builder().build();
        jestClient.executeAsync(optimize, new JestResultHandler<JestResult>() {
            @Override
            public void completed(JestResult jestResult) {
                log.info("optimizeIndex result:{}", jestResult.isSucceeded());
            }
            @Override
            public void failed(Exception e) {
                log.error("optimizeIndex error", e);
            }
        });
    }
    
    /**
     * 清理缓存
     */
    public void clearCache() {
        try {
            ClearCache clearCache = new ClearCache.Builder().build();
            jestClient.execute(clearCache);
        } catch (IOException e) {
            log.error("clearCache error", e);
        }
    }


}
