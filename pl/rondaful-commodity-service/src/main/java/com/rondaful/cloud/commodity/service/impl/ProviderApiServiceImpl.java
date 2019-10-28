package com.rondaful.cloud.commodity.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.commodity.mapper.CategoryMapper;
import com.rondaful.cloud.commodity.mapper.CommodityBaseMapper;
import com.rondaful.cloud.commodity.mapper.CommoditySpecMapper;
import com.rondaful.cloud.commodity.remote.RemoteSupplierService;
import com.rondaful.cloud.commodity.service.ProviderApiService;
import com.rondaful.cloud.commodity.vo.ApiCategoryResponseVo;
import com.rondaful.cloud.commodity.vo.ApiSkuResponse;
import com.rondaful.cloud.commodity.vo.ApiSkuWarehouseInfo;
import com.rondaful.cloud.commodity.vo.ApiSpuResponse;
import com.rondaful.cloud.common.entity.Page;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class ProviderApiServiceImpl implements ProviderApiService{
	private final static Logger log = LoggerFactory.getLogger(ProviderApiServiceImpl.class);
	
	@Autowired
    private CommodityBaseMapper commodityBaseMapper;
	
	@Autowired
	private CommoditySpecMapper commoditySpecMapper;
	
	@Autowired
    private CategoryMapper categoryMapper;
	
	@Autowired
	private RemoteSupplierService remoteSupplierService;

	
	
	@SuppressWarnings("unchecked")
	@Override
	public Page<ApiSpuResponse> getSpuList(Map<String, Object> map) {
		Page.builder((String) map.get("page"), String.valueOf(Integer.valueOf((String) map.get("row")) > 100 ? 100 : (String) map.get("row")));
        List<ApiSpuResponse> list = commodityBaseMapper.selectApiSpu(map);
        if (!list.isEmpty()) {
            map.put("list", list);
            Page.builder("1", String.valueOf(Integer.MAX_VALUE));
            List<ApiSkuResponse> listspc = commoditySpecMapper.selectApiSku(map);
            //构造仓库价格
            getInventory(listspc);
            
            for (ApiSpuResponse cb : list) {
                cb.setSkuList(new ArrayList<>());
                for (ApiSkuResponse cs : listspc) {
                    //sku与spu匹配
                    if (cs.getCommodityId().intValue() == cb.getId().intValue()) {
                        cb.getSkuList().add(cs);
                    }
                }
                //品牌翻译
                if (cb.getBrandId() == -1) {
                    cb.setBrandName("无品牌");
                }
            }
        }
        PageInfo pageInfo = new PageInfo(list);
        return new Page(pageInfo);
	}



	@Override
	public Page<ApiCategoryResponseVo> getCategoryList() {
		Map<String, Object> param=new HashMap<String, Object>();
		
		param.put("categoryLevel", 1);
		List<ApiCategoryResponseVo> list1 = categoryMapper.getForApi(param);
		param.clear();
		
		param.put("categoryLevel", 2);
		List<ApiCategoryResponseVo> list2 = categoryMapper.getForApi(param);
		param.clear();
		
		param.put("categoryLevel", 3);
		List<ApiCategoryResponseVo> list3 = categoryMapper.getForApi(param);
		param.clear();

		for (ApiCategoryResponseVo ca1 : list1) {
			ca1.setChildren(new ArrayList<>());
			for (ApiCategoryResponseVo ca2 : list2) {
				ca2.setChildren(new ArrayList<>());
				if (ca2.getCategoryParentId().intValue() == ca1.getId().intValue()) {
					ca1.getChildren().add(ca2);
				}
				for (ApiCategoryResponseVo ca3 : list3) {
					if (ca3.getCategoryParentId().intValue() == ca2.getId().intValue()) {
						ca2.getChildren().add(ca3);
					}
				}
			}
		}

		PageInfo pageInfo = new PageInfo(list1);
		return new Page(pageInfo);
	}
	
	public void getInventory(List<ApiSkuResponse> skuList) {
        try {
            List<String> list = new ArrayList<String>() {{
                for (ApiSkuResponse cs : skuList) {
                    this.add(cs.getSystemSku());
                }
            }};
            Map o = (Map) remoteSupplierService.getBySku(list);
            if (o != null && o.get("data") != null) {
                JSONArray ja = JSONArray.fromObject(o.get("data"));
                if (!ja.isEmpty() && ja.size() > 0) {
                    for (ApiSkuResponse cs : skuList) {
                    	List<ApiSkuWarehouseInfo> inventoryList=new ArrayList<ApiSkuWarehouseInfo>();
                    	String[] warehousePriceArr=null;
                    	if (StringUtils.isNotBlank(cs.getWarehousePriceGroup())) {
                    		warehousePriceArr=cs.getWarehousePriceGroup().split("\\|");
						}
                        for (int i = 0; i < ja.size(); i++) {
                    		String pinlianSku=(JSONObject.fromObject(ja.get(i))).getString("pinlianSku");
                            String inventory = (JSONObject.fromObject(ja.get(i))).getString("localAvailableQty");
                            Integer warehouseId = (JSONObject.fromObject(ja.get(i))).getInt("warehouseId");
                            
                            if (StringUtils.isNotBlank(pinlianSku) && StringUtils.isNotBlank(inventory) && pinlianSku.equals(cs.getSystemSku())) {
                            	ApiSkuWarehouseInfo vo=new ApiSkuWarehouseInfo();
                            	vo.setWarehouseId(warehouseId);
                            	if (warehousePriceArr != null && warehousePriceArr.length>0) {
									for (int j = 0; j < warehousePriceArr.length; j++) {
										if (String.valueOf(warehouseId).equals(warehousePriceArr[j].split(":")[0])) {
											vo.setWarehousePrice(warehousePriceArr[j].split(":")[1]);
										}
									}
								}
                            	if (StringUtils.isBlank(vo.getWarehousePrice())) {
                            		vo.setWarehousePrice(String.valueOf(cs.getCommodityPriceUs()));
								}
                            	inventoryList.add(vo);
                            	
                            }
                        }
                        if (inventoryList.size()>0) {
                        	cs.setWarehouseInfo(inventoryList);
						}
                    }
                }
            }
        } catch (Exception e) {
        	log.error("获取库存信息异常",e);
        }
    }

}
