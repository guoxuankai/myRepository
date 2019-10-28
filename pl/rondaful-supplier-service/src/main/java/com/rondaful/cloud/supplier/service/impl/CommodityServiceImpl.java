package com.rondaful.cloud.supplier.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.entity.inventory.Commodity;
import com.rondaful.cloud.supplier.entity.inventory.CommoditySkuMap;
import com.rondaful.cloud.supplier.mapper.CommodityMapper;
import com.rondaful.cloud.supplier.mapper.CommoditySkuMapMapper;
import com.rondaful.cloud.supplier.model.dto.inventory.CommodityDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.InventoryDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.InventoryQueryDTO;
import com.rondaful.cloud.supplier.service.ICommodityService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xqq
 * @Date: 2019/6/17
 * @Description:
 */
@Service("commodityServiceImpl")
public class CommodityServiceImpl implements ICommodityService {
    private final Logger logger = LoggerFactory.getLogger(CommodityServiceImpl.class);

    @Autowired
    private CommodityMapper commodityMapper;
    @Autowired
    private CommoditySkuMapMapper skuMapMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 迁移商品服务数据
     */
    @Override
    public void init() {
        Boolean hasNext=false;
        Integer currentPage=1;
        Integer count=this.commodityMapper.getsOldCount();

        do {
            List<Map> list = this.commodityMapper.getsOldPage((currentPage-1)*500,500);

            if (CollectionUtils.isNotEmpty(list)){
                hasNext=true;
                currentPage++;
                List<Commodity> insertList=new ArrayList<>(500);
                List<CommoditySkuMap> skuList=new ArrayList<>(500);

                list.forEach(map->{
                    Commodity commodity=new Commodity();
                    commodity.setCommodityName(String.valueOf(map.get("commodity_name")));
                    commodity.setCommodityNameEn(String.valueOf(map.get("commodity_name_en")));
                    commodity.setPictureUrl(String.valueOf(map.get("picture_url")));
                    commodity.setPinlianSku(String.valueOf(map.get("pinlian_sku")));
                    commodity.setSupplierSku(String.valueOf(map.get("supplier_sku")));
                    commodity.setSupplierId(map.get("supplier_id")!=null?(int) map.get("supplier_id"):0);
                    commodity.setCreateDate(new Date());
                    commodity.setVersion(0);
                    insertList.add(commodity);
                    CommoditySkuMap skuMap=new CommoditySkuMap();
                    skuMap.setPinlianSku(commodity.getPinlianSku());
                    skuMap.setSupplierSku(commodity.getSupplierSku());
                    skuList.add(skuMap);

                });
                this.skuMapMapper.insertBatch(skuList);
                this.commodityMapper.insertBatch(insertList);
            }else {
                hasNext=false;
            }
        }while (hasNext);
    }

    /**
     * 分页获取平台sku
     *
     * @param tableIndex
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Override
    public PageDTO<String> getsSupplierSku(Integer tableIndex, Integer currentPage, Integer pageSize) {
        PageHelper.startPage(currentPage,pageSize);
        List<String> list=this.skuMapMapper.getsSupplierSku("t_commodity_sku_map_"+tableIndex);
        PageInfo<String> pageInfo=new PageInfo<>(list);
        PageDTO<String> result=new PageDTO<>((int)pageInfo.getTotal(),currentPage);
        result.setList(pageInfo.getList());
        return result;
    }

    /**
     * 根据平台sku查询商品信息
     *
     * @param supplierSku
     * @return
     */
    @Override
    public CommodityDTO getsBySSku(String supplierSku) {
        String key=COMMODITY_BY_SUPPLIER_SKU+supplierSku;
        ValueOperations operations = this.redisTemplate.opsForValue();
        CommodityDTO result=(CommodityDTO)operations.get(key);
        if (result==null){
            Commodity commodity=this.commodityMapper.getByPSku(this.skuMapMapper.getBySSku(supplierSku));
            result=new CommodityDTO();
            if (commodity!=null){
                BeanUtils.copyProperties(commodity,result);
                operations.set(key,result,90L, TimeUnit.DAYS);
            }
        }
        return result;
    }

    /**
     * 根据品连sku查询商品信息
     *
     * @param pinlianSku
     * @return
     */
    @Override
    public CommodityDTO getsByPSku(String pinlianSku) {
        String key=COMMODITY_BY_SUPPLIER_SKU+pinlianSku;
        ValueOperations operations = this.redisTemplate.opsForValue();
        CommodityDTO result=(CommodityDTO)operations.get(key);
        if (result==null){
            Commodity commodity=this.commodityMapper.getByPSku(pinlianSku);
            result=new CommodityDTO();
            if (commodity!=null){
                BeanUtils.copyProperties(commodity,result);
                operations.set(key,result,90L, TimeUnit.DAYS);
            }
        }
        return result;
    }
}
