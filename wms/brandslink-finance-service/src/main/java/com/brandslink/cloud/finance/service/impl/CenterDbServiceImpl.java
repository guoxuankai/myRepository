package com.brandslink.cloud.finance.service.impl;

import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.finance.mapper.CenterDbMapper;
import com.brandslink.cloud.finance.pojo.dto.ProductDto;
import com.brandslink.cloud.finance.pojo.vo.StockCostDetailVo;
import com.brandslink.cloud.finance.pojo.vo.StockCostVo;
import com.brandslink.cloud.finance.service.CenterDbService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yangzefei
 * @Classname CenterDbServiceImpl
 * @Description 中心数据库服务
 * @Date 2019/9/4 17:05
 */
@Service
public class CenterDbServiceImpl extends BaseServiceImpl<ProductDto> implements CenterDbService {

    @Resource
    private CenterDbMapper centerDbMapper;

    @Override
    public StockCostVo setStockCostVo(StockCostVo param){
        param.setWarehouseName(centerDbMapper.getWarehouseName(param.getWarehouseCode()));
        Map<String,Integer> skuMap=param.getItems().stream().collect(Collectors.toMap(StockCostDetailVo::getSku, StockCostDetailVo::getSkuNumber, (key1, key2) -> key2));
        List<ProductDto> products= centerDbMapper.getBySkuList(new ArrayList<>(skuMap.keySet()));
        Map<String, List<ProductDto>> customerMap=new HashMap<>();
        for(ProductDto p:products){
            p.setProductNumber(skuMap.get(p.getProductSku()));
            if(!customerMap.containsKey(p.getCustomerCode())){
                customerMap.put(p.getCustomerCode(),new ArrayList<>());
            }
            customerMap.get(p.getCustomerCode()).add(p);
        }
        param.setCustomerMap(customerMap);
        return param;
    }
    @Override
    public String getWarehouseName(String warehouseCode){
        return centerDbMapper.getWarehouseName(warehouseCode);
    }
}
