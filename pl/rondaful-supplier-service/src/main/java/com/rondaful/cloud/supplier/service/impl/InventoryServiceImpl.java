package com.rondaful.cloud.supplier.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.tx.annotation.TxTransaction;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.constant.ConstantAli;
import com.rondaful.cloud.common.granary.GranaryUtils;
import com.rondaful.cloud.common.service.FileService;
import com.rondaful.cloud.common.utils.AliFileUtils;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.common.utils.SpringContextUtil;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.entity.inventory.Inventory;
import com.rondaful.cloud.supplier.entity.inventory.SkuWarehouseMap;
import com.rondaful.cloud.supplier.mapper.InventoryMapper;
import com.rondaful.cloud.supplier.mapper.SkuWarehouseMapMapper;
import com.rondaful.cloud.supplier.model.dto.FeignResult;
import com.rondaful.cloud.supplier.model.dto.KeyValueDTO;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseCountryDTO;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseDTO;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseInitDTO;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseListDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.*;
import com.rondaful.cloud.supplier.model.enums.StatusEnums;
import com.rondaful.cloud.supplier.model.enums.WarehouseFirmEnum;
import com.rondaful.cloud.supplier.remote.RemoteCommodityService;
import com.rondaful.cloud.supplier.remote.RemoteErpService;
import com.rondaful.cloud.supplier.service.IInventoryService;
import com.rondaful.cloud.supplier.service.IMessageService;
import com.rondaful.cloud.supplier.service.IWarehouseBasicsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @Author: xqq
 * @Date: 2019/6/14
 * @Description:
 */
@Service("inventoryServiceImpl")
public class InventoryServiceImpl implements IInventoryService {
    private final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private SkuWarehouseMapMapper skuMapMapper;

    @Autowired
    private IWarehouseBasicsService basicsService;

    @Autowired
    private GranaryUtils granaryUtils;

    @Autowired
    private RemoteErpService remoteErpService;

    @Autowired
    private RemoteCommodityService remoteCommodityService;

    @Value("${wsdl.url}")
    private String wsdlUrl;

    @Value("${brandslink.wms.url}")
    private String wmsUrl;
    @Autowired
    private IMessageService messageService;

    /**
     * 库存明细初始化
     */
    @Override
    public void init() {
        List<WarehouseInitDTO> warhouses=this.basicsService.getsInit();
        InventoryServiceImpl service=SpringContextUtil.getBean(InventoryServiceImpl.class);
        warhouses.forEach(firm->{
            if (WarehouseFirmEnum.GOODCANG.getCode().equals(firm.getFirmCode())){
                this.logger.info("开始同步谷仓库存数据");
                service.goodCangInit(firm);
            }else if (WarehouseFirmEnum.RONDAFUL.getCode().equals(firm.getFirmCode())){
                this.logger.info("开始同步erp库存数据");
                firm.getList().forEach(dto -> {
                    service.erpInit(dto);
                });
            }else if (WarehouseFirmEnum.WMS.getCode().equals(firm.getFirmCode())){
                this.logger.info("开始同步wms库存数据");
                service.wmsInit(firm);
            }
        });
        logger.info("同步库存完成");
    }



    /**
     * 分页查询库存明细
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<InventoryDTO> getsPage(InventoryQueryDTO dto) {
        List<String> pinskus=new ArrayList<>();
        if (StringUtils.isNotEmpty(dto.getSupplierSku())){
            FeignResult feignResult=this.remoteCommodityService.getTwoSku(dto.getSupplierSku());
            if (!feignResult.getSuccess()||feignResult.getData()==null){
                return new PageDTO<>(0,1);
            }
            JSONArray users=JSONObject.parseArray(JSONObject.toJSONString(feignResult.getData()));
            for (int i = 0; i < users.size(); i++) {
                JSONObject comm=users.getJSONObject(i);
                pinskus.add(comm.getString("systemSku"));
            }
        }
        if (StringUtils.isNotEmpty(dto.getPinlianSku())){
            pinskus.add(dto.getPinlianSku());
        }
        if (CollectionUtils.isNotEmpty(dto.getPinlianSkus())){
            pinskus.addAll(dto.getPinlianSkus());
        }
        List<Integer> warehouseIds=new ArrayList<>(1);
        if (dto.getWarehouseId()==null){
            warehouseIds.addAll(this.skuMapMapper.getsByPinsku(pinskus));
        }else{
            warehouseIds.add(dto.getWarehouseId());
        }
        if (CollectionUtils.isEmpty(warehouseIds)){
            return new PageDTO<>(0,dto.getCurrentPage());
        }
        PageHelper.startPage(dto.getCurrentPage(),dto.getPageSize());
        List<Inventory> list=this.inventoryMapper.getsPage(warehouseIds,pinskus,dto.getStatus(),dto.getSupplierIds(),dto.getSellerId());
        PageInfo<Inventory> pageInfo=new PageInfo<>(list);
        PageDTO<InventoryDTO> result=new PageDTO<>((int)pageInfo.getTotal(),dto.getCurrentPage());
        List<InventoryDTO> dataList=new ArrayList<>();
        pageInfo.getList().forEach(inventory -> {
            InventoryDTO dto1=new InventoryDTO();
            BeanUtils.copyProperties(inventory,dto1);
            dto1.setLocalAvailableQty(inventory.getAvailableQty()-inventory.getLocalWaitingShippingQty());
            Object object=this.remoteCommodityService.getBySku(inventory.getPinlianSku(),null,null);
            JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object)).getJSONObject("data");
            if (jsonObject!=null){
                dto1.setPictureUrl(StringUtils.isNotEmpty(jsonObject.getString("masterPicture"))?jsonObject.getString("masterPicture").split("\\|")[0]:"");
                if (StringUtils.isEmpty(dto.getLanguageType())){
                    dto1.setCommodityName(jsonObject.getString("commodityNameCn"));
                }else {
                    dto1.setCommodityName(jsonObject.getString("commodityNameEn"));
                }
            }else {
                logger.error("sku={}的商品不存在",inventory.getPinlianSku());
            }
            WarehouseDTO warehouseDTO = this.basicsService.getByWarehouseId(inventory.getWarehouseId());
            if (warehouseDTO!=null){
                dto1.setWarehouseCode(warehouseDTO.getWarehouseCode());
                dto1.setWarehouseName(StringUtils.isEmpty(dto.getLanguageType())?warehouseDTO.getWarehouseName(): Utils.translation(warehouseDTO.getWarehouseName()));
            }
            dataList.add(dto1);
        });
        result.setList(dataList);
        return result;
    }

    /**
     * 根据品联sku查询指定仓库商品
     *
     * @param warehouseId
     * @param pinlianSku
     * @return
     */
    @Override
    public InventoryDTO getByWPinlianSku(Integer warehouseId, String pinlianSku) {
        Inventory inventory=this.inventoryMapper.getByWPinlianSku(warehouseId,pinlianSku);
        if (inventory==null){
            return null;
        }
        InventoryDTO result=new InventoryDTO();
        BeanUtils.copyProperties(inventory,result);
        return result;
    }

    /**
     * @param warehouseId
     * @param pinlianSku
     * @param warnVal
     * @param updateBy
     * @return
     */
    @Override
    public Integer updateWarnVal(Integer warehouseId, List<String> pinlianSkus, Integer warnVal, String updateBy) {
        return this.inventoryMapper.updateWarnVal(warehouseId,pinlianSkus,warnVal,updateBy);
    }


    /**
     * 根据sku列表获取仓库列表(必须在同一个仓库)
     *
     * @param sku
     * @return
     */
    @Override
    public List<CombineSelectDTO> getCombineSku(List<String> sku,String languageType, JSONObject skuNum) {
        List<CombineSelectDTO> result=new ArrayList<>();

        List<SkuWarehouseMap> list=this.skuMapMapper.getCombineSku(sku);
        if (CollectionUtils.isEmpty(list)){
            return result;
        }
        Map<Integer,List<String>> map=new HashMap<>();
        list.forEach(warehouseMap->{
            if (!map.containsKey(warehouseMap.getWarehouseId())){
                map.put(warehouseMap.getWarehouseId(),new ArrayList<>());
            }
            map.get(warehouseMap.getWarehouseId()).add(warehouseMap.getPinlianSku());
        });
        map.forEach((key,val)->{
            if (val.size()==sku.size()){
                WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(key);
                if (warehouseDTO!=null&&StatusEnums.ACTIVATE.getStatus().equals(warehouseDTO.getStatus())){
                    CombineSelectDTO dto=new CombineSelectDTO();
                    BeanUtils.copyProperties(warehouseDTO,dto);
                    dto.setServiceCode(warehouseDTO.getFirmCode());
                    dto.setWarehouseName(warehouseDTO.getName()+"-"+(StringUtils.isEmpty(languageType)?warehouseDTO.getWarehouseName():Utils.translation(warehouseDTO.getWarehouseName())));
                    List<Inventory> inventories=this.inventoryMapper.getsBySku(key,val);
                    dto.setIsAmple(true);
                    for (Inventory inventory:inventories) {
                        /**
                         * erp 很傻B的特例没有库存也可以发货
                         */
                        /*if ("2_ZSW".equals(warehouseDTO.getWarehouseCode())||"6_JHW".equals(warehouseDTO.getWarehouseCode())){
                            break;
                        }*/

                        if ((inventory.getAvailableQty()-inventory.getLocalWaitingShippingQty())<(skuNum==null?1:skuNum.getInteger(inventory.getPinlianSku()))){
                            dto.setIsAmple(false);
                        }
                        if (!dto.getIsAmple()){
                            break;
                        }
                    }
                    result.add(dto);
                }
            }
        });
        return result;
    }

    /**
     * 根据sku列表获取仓库列表(有一个存在就返回,且库存大于0)
     *
     * @param sku
     * @return
     */
    @Override
    public List<KeyValueDTO> getOrSku(List<String> sku,String languageType) {
        List<KeyValueDTO> result=new ArrayList<>();
        List<Integer> list=this.skuMapMapper.getsByPinsku(sku);
        if (CollectionUtils.isEmpty(list)){
            return result;
        }
        List<Inventory> baseList=this.inventoryMapper.getsWIdSku(list,sku);
        baseList.forEach(inventory -> {
            WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(inventory.getWarehouseId());
            if (warehouseDTO!=null&& StatusEnums.ACTIVATE.getStatus().equals(warehouseDTO.getStatus())){
                result.add(new KeyValueDTO(warehouseDTO.getWarehouseId().toString(),warehouseDTO.getName()+"-"+(StringUtils.isEmpty(languageType)?warehouseDTO.getWarehouseName():Utils.translation(warehouseDTO.getWarehouseName()))));
            }
        });
        return result;
    }

    /**
     * 根据sku列表及仓库id获取明细
     *
     * @param warehouseId
     * @param skus
     * @return
     */
    @Override
    public List<InventoryDTO> getsBySku(Integer warehouseId, List<String> skus) {
        List<InventoryDTO> result=new ArrayList<>();
        List<Inventory> list=this.inventoryMapper.getsBySku(warehouseId,skus);
        if (CollectionUtils.isEmpty(list)){
            return result;
        }
        list.forEach(inventory -> {
            InventoryDTO dto=new InventoryDTO();
            BeanUtils.copyProperties(inventory,dto);
            result.add(dto);
        });
        return result;
    }

    /**
     * 根据sku获取库存明细
     *
     * @param skus
     * @return
     */
    @Override
    public List<InventoryDTO> getBySku(List<String> skus) {
        List<InventoryDTO> result=new ArrayList<>();
        if (CollectionUtils.isEmpty(skus)){
            return result;
        }
        List<SkuWarehouseMap> list=this.skuMapMapper.getCombineSku(skus);
        if (CollectionUtils.isEmpty(list)){
            return result;
        }
        List<Integer> warehouseIds=new ArrayList<>();
        list.forEach(map->{
            warehouseIds.add(map.getWarehouseId());
        });
        List<Inventory> inventoryList=this.inventoryMapper.getBySku(warehouseIds,skus);
        inventoryList.forEach(inventory -> {
            InventoryDTO dto=new InventoryDTO();
            BeanUtils.copyProperties(inventory,dto);
            WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(inventory.getWarehouseId());
            if (warehouseDTO!=null&&StatusEnums.ACTIVATE.getStatus().equals(warehouseDTO.getStatus())){
                dto.setWarehouseName(warehouseDTO.getWarehouseName());
                dto.setLocalAvailableQty(inventory.getAvailableQty()-inventory.getLocalWaitingShippingQty());
                result.add(dto);
            }
        });
        return result;
    }

    /**
     * 根据sku列表查询库存明细,且都在同一个仓库
     *
     * @param skus
     * @return
     */
    @Override
    public List<OrderInvDTO> getsInvBySku(List<String> skus) {
        List<OrderInvDTO> result=new ArrayList<>();

        List<SkuWarehouseMap> list=this.skuMapMapper.getCombineSku(skus);
        if (CollectionUtils.isEmpty(list)){
            return result;
        }
        Map<Integer,List<String>> map=new HashMap<>();
        list.forEach(warehouseMap->{
            if (!map.containsKey(warehouseMap.getWarehouseId())){
                map.put(warehouseMap.getWarehouseId(),new ArrayList<>());
            }
            map.get(warehouseMap.getWarehouseId()).add(warehouseMap.getPinlianSku());
        });
        map.forEach((key,val)->{
            if (val.size()==skus.size()){
                WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(key);
                OrderInvDTO invDTO=new OrderInvDTO();
                BeanUtils.copyProperties(warehouseDTO,invDTO);
                invDTO.setServiceCode(warehouseDTO.getFirmCode());
                List<Inventory> inventories=this.inventoryMapper.getsBySku(key,val);
                List<OrderInvNumberDTO> item=new ArrayList<>(inventories.size());
                for (Inventory inventory:inventories) {
                    OrderInvNumberDTO dto=new OrderInvNumberDTO();
                    BeanUtils.copyProperties(inventory,dto);
                    dto.setLocalAvailableQty((inventory.getLocalWaitingShippingQty()==null||inventory.getLocalWaitingShippingQty()<1)?inventory.getAvailableQty():(inventory.getAvailableQty()-inventory.getLocalWaitingShippingQty()));
                    item.add(dto);
                }
                invDTO.setItem(item);
                result.add(invDTO);
            }
        });
        return result;
    }

    /**
     * 谷仓库存监听
     *
     * @param appToken
     * @param pinlianSku
     * @param warehouseCode
     */
    @Override
    @Transactional
    public void updateGInventory(String appToken, String pinlianSku, String warehouseCode) {

        WarehouseDTO dto=this.basicsService.getByAppTokenAndCode(appToken,warehouseCode);
        JSONObject params=new JSONObject();
        params.put("pageSize",10);
        params.put("page",1);
        params.put("product_sku",pinlianSku);
        params.put("warehouse_code",dto.getWarehouseCode());
        String resp=null;
        try {
            resp=this.granaryUtils.getInstance(dto.getAppToken(),dto.getAppKey(),this.wsdlUrl,params.toJSONString(),"getProductInventory").getCallService();
        } catch (Exception e) {
            logger.error("调用谷仓服务异常");
        }
        JSONObject respJson=JSONObject.parseObject(resp);
        if (!"Success".equals(respJson.getString("ask"))){
            logger.error("谷仓服务响应异常:msg={}",respJson.getString("Error"));
        }
        JSONObject data=respJson.getJSONArray("data").getJSONObject(0);
        Inventory inventory=this.inventoryMapper.getByWPinlianSku(dto.getWarehouseId(),data.getString("product_sku"));
        Inventory inventoryDO=new Inventory();
        inventoryDO.setWarehouseId(dto.getWarehouseId());
        inventoryDO.setInstransitQty(data.getInteger("onway"));
        inventoryDO.setAvailableQty(data.getInteger("sellable"));
        inventoryDO.setQty(0);
        inventoryDO.setWaitingShippingQty(data.getInteger("reserved"));
        inventoryDO.setAllocatingQty(0);
        inventoryDO.setDefectsQty(data.getInteger("unsellable"));
        inventoryDO.setPinlianSku(data.getString("product_sku"));
        inventoryDO.setSupplierSku(data.getString("product_sku"));
        inventoryDO.setProductBarcode(data.getString("product_barcode"));
        inventoryDO.setPendingQty(data.getInteger("pending"));
        inventoryDO.setShippedQty(data.getInteger("shipped"));
        inventoryDO.setSoldSharedQty(data.getInteger("sold_shared"));
        inventoryDO.setPiNoStockQty(data.getInteger("pi_no_stock"));
        inventoryDO.setTuneInQty(data.getInteger("tune_in"));
        inventoryDO.setTuneOutQty(data.getInteger("tune_out"));
        inventoryDO.setProductSalesValue(data.getBigDecimal("product_sales_value"));
        inventoryDO.setSyncTime(new Date());
        inventoryDO.setUpdateDate(new Date());
        if (inventory==null){
            SkuWarehouseMap skuMap=new SkuWarehouseMap();
            skuMap.setPinlianSku(inventoryDO.getPinlianSku());
            skuMap.setWarehouseId(inventoryDO.getWarehouseId());
            Object object=this.remoteCommodityService.getBySku(inventoryDO.getPinlianSku(),null,null);
            if (object!=null){
                JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object));
                inventoryDO.setSupplierId(jsonObject.getJSONObject("data")==null?0:jsonObject.getJSONObject("data").getInteger("supplierId"));
            }
            this.inventoryMapper.insert(inventoryDO);
            this.skuMapMapper.insert(skuMap);
        }else {
            inventoryDO.setId(inventory.getId());
            this.inventoryMapper.updateByPrimaryKeySelective(inventoryDO);
        }
        this.messageService.sendInventory(inventoryDO.getWarehouseId(),inventoryDO.getPinlianSku());
    }

    /**
     * 谷仓数据初始化
     * @param initDTO
     */
    @Async
    @Override
    public void goodCangInit(WarehouseInitDTO initDTO){

        initDTO.getList().forEach( listDTO -> {

            JSONObject params=new JSONObject();
            params.put("pageSize",500);
            params.put("page",0);
            params.put("product_sku_arr",new ArrayList<>(0));
            params.put("warehouse_code_arr",new ArrayList<>(0));
            params.put("warehouse_code",listDTO.getWarehouseCode());
            Boolean hastNext=false;
            do {
                List<Inventory> insertList=new ArrayList<>(params.getInteger("pageSize"));
                List<SkuWarehouseMap> skuList=new ArrayList<>(params.getInteger("pageSize"));
                params.put("pageSize",params.getIntValue("pageSize")+1);
                JSONObject respJson =null;
                try {
                    String response=granaryUtils.getInstance(initDTO.getAppToken(),initDTO.getAppKey(),this.wsdlUrl,params.toJSONString(),"getProductInventory").getCallService();
                    if (StringUtils.isEmpty(response)){
                        break;
                    }
                    respJson=JSONObject.parseObject(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (respJson==null||!"Success".equals(respJson.getString("message"))){
                    logger.error("分页获取谷仓数据异常:,msg={}",respJson.getString("message"));
                    break;
                }
                hastNext=respJson.getBoolean("nextPage");
                for (Integer i=0;i<respJson.getJSONArray("data").size();i++) {
                    JSONObject inv=respJson.getJSONArray("data").getJSONObject(i);
                    Inventory inventoryDO = new Inventory();
                    inventoryDO.setWarehouseId(listDTO.getId());
                    inventoryDO.setInstransitQty(inv.getInteger("onway"));
                    inventoryDO.setAvailableQty(inv.getInteger("sellable"));
                    inventoryDO.setQty(0);
                    inventoryDO.setWaitingShippingQty(inv.getInteger("reserved"));
                    inventoryDO.setDefectsQty(inv.getInteger("unsellable"));
                    inventoryDO.setPinlianSku(inv.getString("product_sku"));
                    inventoryDO.setProductBarcode(inv.getString("product_barcode"));
                    inventoryDO.setPendingQty(inv.getInteger("pending"));
                    inventoryDO.setShippedQty(inv.getInteger("shipped"));
                    inventoryDO.setSoldSharedQty(inv.getInteger("sold_shared"));
                    inventoryDO.setStockingQty(inv.getInteger("sold_shared"));
                    inventoryDO.setPiNoStockQty(inv.getInteger("pi_no_stock"));
                    inventoryDO.setTuneInQty(inv.getInteger("tune_in"));
                    inventoryDO.setTuneOutQty(inv.getInteger("tune_out"));
                    inventoryDO.setProductSalesValue(inv.getBigDecimal("product_sales_value"));
                    inventoryDO.setSyncTime(new Date());
                    inventoryDO.setUpdateDate(new Date());
                    Inventory inventory=this.inventoryMapper.getByWPinlianSku(inventoryDO.getWarehouseId(),inventoryDO.getPinlianSku());
                    if (inventory==null){
                        Object object=this.remoteCommodityService.getBySku(inventoryDO.getPinlianSku(),null,null);
                        if (object!=null){
                            JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object));
                            inventoryDO.setSupplierId(jsonObject.getJSONObject("data")==null?0:jsonObject.getJSONObject("data").getInteger("supplierId"));
                            inventoryDO.setSupplierSku(jsonObject.getJSONObject("data")==null?null:jsonObject.getJSONObject("data").getString("supplierSku"));
                        }
                        SkuWarehouseMap skuMap=new SkuWarehouseMap();
                        skuMap.setWarehouseId(listDTO.getId());
                        skuMap.setPinlianSku(inv.getString("product_sku"));
                        insertList.add(inventoryDO);
                        skuList.add(skuMap);
                        //this.inventoryMapper.insert(inventoryDO);
                        //this.skuMapMapper.insert(skuMap);
                    }else {
                        inventoryDO.setId(inventory.getId());
                        if (inventory.getWarnVal()==null){
                            inventoryDO.setWarnVal(-1);
                        }else {
                            inventoryDO.setWarnVal(inventory.getWarnVal());
                        }
                        this.inventoryMapper.updateByPrimaryKey(inventoryDO);
                    }
                }
                if (CollectionUtils.isNotEmpty(insertList)){
                    this.inventoryMapper.insertBatch(insertList);
                    this.skuMapMapper.insertBatch(skuList);
                }
            }while (hastNext);
        });
    }

    /**
     * 初始化erp仓库数据
     * @param dto
     */
    @Async
    @Override
    public void erpInit(WarehouseListDTO dto){
        logger.info("同步的erp仓库:code={}",dto.getWarehouseCode());
        Integer currentPage=1;
        Integer pageSize=2000;
        Boolean hasNext=false;
        do {
            List<Inventory> insertList=new ArrayList<>(1000);
            List<SkuWarehouseMap> skuList=new ArrayList<>(1000);
            logger.info("查询仓库code:{},查询库存起始值={},当前查询页={}",dto.getWarehouseCode(),currentPage*pageSize,currentPage);
            Map<String,String> params=new HashMap<>(2);
            params.put("warehouse_code",dto.getWarehouseCode());
            params.put("page",currentPage.toString());
            params.put("pageSize",pageSize.toString());
            JSONObject erpRes=remoteErpService.getInventory(params);
            if (erpRes==null||erpRes.getJSONArray("lists")==null){
                logger.error("获取erp库存数异常:params={}",JSONObject.toJSONString(params));
                continue;
            }
            if (currentPage*pageSize<erpRes.getInteger("count")){
                hasNext=true;
                currentPage++;
            }else {
                hasNext=false;
            }
            for (int i = 0; i < erpRes.getJSONArray("lists").size(); i++) {
                this.insertErp(erpRes.getJSONArray("lists").getJSONObject(i));
            }

        }while (hasNext);
        logger.info("erp仓库:code={}同步完成",dto.getWarehouseCode());
    }


    /**
     * 根据sku拉去erp库存
     * @param sku
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void change(String sku){
        Map<String,String> params=new HashMap<>(2);
        params.put("page","1");
        params.put("pageSize","1000");
        params.put("sku",sku);
        JSONObject erpRes=remoteErpService.getInventory(params);
        if (erpRes.getInteger("count")>0){
            JSONArray jsonArray=erpRes.getJSONArray("lists");
            for (int i = 0; i < jsonArray.size(); i++) {
                this.insertErp(jsonArray.getJSONObject(i));
            }
        }
    }


    /**
     * 获取所有有商品的仓库
     *
     * @param languageType
     * @return
     */
    @Override
    public List<KeyValueDTO> getAll(String languageType) {
        List<KeyValueDTO> result=new ArrayList<>();
        List<WarehouseInitDTO> list=this.basicsService.getsInit();
        list.forEach(initDTO -> {
            for (WarehouseListDTO dto:initDTO.getList()) {
                if (this.inventoryMapper.getCount(dto.getId())>0){
                    result.add(new KeyValueDTO(dto.getId().toString(),(StringUtils.isEmpty(languageType)?dto.getWarehouseName():Utils.translation(dto.getWarehouseName()))));
                }
            }
        });
        return result;
    }

    /**
     * 导出指定仓库数据
     *
     * @param warehouseId
     * @param userId
     * @return
     */
    @Override
    public List<Object> export(Integer warehouseId, Integer userId,Integer currentPage,String languageType) {
        logger.info("导出查询的当前页:currentPage={}",currentPage);
        List<Object> result=new ArrayList<>();
        List<Integer> warehouseIds=new ArrayList<>();
        warehouseIds.add(warehouseId);
        List<Integer> supplierIds=new ArrayList<>();
        if (userId!=null){
            supplierIds.add(userId);
        }
        PageHelper.startPage(currentPage,100);
        List<Inventory> list=this.inventoryMapper.getsPage(warehouseIds,null,null,supplierIds,null);
        PageInfo<Inventory> pageInfo=new PageInfo<>(list);
        if (CollectionUtils.isEmpty(pageInfo.getList())){
            return null;
        }
        pageInfo.getList().forEach(info->{
            IneventoryExportDTO dto=new IneventoryExportDTO();

            Object object=this.remoteCommodityService.getBySku(info.getPinlianSku(),null,null);
            JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object)).getJSONObject("data");
            if (jsonObject!=null){
                dto.setPictureUrl(StringUtils.isNotEmpty(jsonObject.getString("masterPicture"))?jsonObject.getString("masterPicture").split("\\|")[0].split("\\?")[0]:"");
                if (StringUtils.isEmpty(languageType)){
                    dto.setCommodityName(jsonObject.getString("commodityNameCn"));
                }else {
                    dto.setCommodityName(jsonObject.getString("commodityNameEn"));
                }
            }
            dto.setPinlianSku(info.getPinlianSku());
            dto.setSupplierSku(info.getSupplierSku());
            WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(info.getWarehouseId());
            if (warehouseDTO!=null){
                dto.setWarehouseName(warehouseDTO.getName()+"-"+(StringUtils.isEmpty(languageType)?warehouseDTO.getWarehouseName():Utils.translation(warehouseDTO.getWarehouseName()))+"/"+warehouseDTO.getWarehouseCode());
            }
            dto.setInstransitQty(info.getInstransitQty()+"/"+(info.getPendingQty()==null?"--":info.getPendingQty()));
            dto.setAvailableQty((info.getAvailableQty()==null?"--":info.getAvailableQty())+"/"+(info.getDefectsQty()==null?"--":info.getDefectsQty()));
            dto.setWaitingShippingQty(info.getWaitingShippingQty()==null?0:info.getWaitingShippingQty());
            dto.setTune((info.getTuneInQty()==null?"--":info.getTuneInQty())+"/"+(info.getTuneOutQty()==null?"--":info.getTuneOutQty()));
            dto.setStockingQty((info.getStockingQty()==null?"--":info.getStockingQty())+"/"+(info.getPiNoStockQty()==null?"--":info.getPiNoStockQty()));
            dto.setWarnVal(info.getWarnVal()==null?-1:info.getWarnVal());
            dto.setLocalWaitingShippingQty(info.getLocalWaitingShippingQty());
            dto.setLocalAvailableQty(info.getAvailableQty()-((info.getLocalWaitingShippingQty()==null||info.getLocalWaitingShippingQty()<0)?0:info.getLocalWaitingShippingQty()));
            if (info.getWarnVal()==null||info.getWarnVal()==-1||dto.getLocalAvailableQty()>=info.getWarnVal()){
                dto.setStatus(StringUtils.isEmpty(languageType)?"正常":"Normal");
            }else {
                dto.setStatus(StringUtils.isEmpty(languageType)?"低于预警值":"Below warning");
            }
            dto.setSyncTime(info.getUpdateDate());
            result.add(dto);
        });
        return result;
    }

    /**
     * wms 库存监听
     *
     * @param appKey
     * @param sku
     * @param warehouseCode
     */
    @Override
    public void monitorWms(String appKey, String sku, String warehouseCode) {
        WarehouseDTO warehouseDTO=this.basicsService.getByAppKeyAndCode(appKey,warehouseCode);
        if (warehouseDTO==null){
            logger.error("appKey={}的信息不存在",appKey);
            return;
        }
        JSONObject params=new JSONObject();
        params.put("pageSize",1);
        params.put("pageNum",1);
        params.put("warehouseCode",warehouseCode);
        List<String> skus=new ArrayList<>(1);
        skus.add(sku);
        params.put("skuArray",skus);
        JSONObject respJson=null;
        try {
            URIBuilder uri=new URIBuilder(this.wmsUrl+"/center/external/skuInventory");
            uri.addParameter("customerAppId",warehouseDTO.getAppKey());
            uri.addParameter("sign",warehouseDTO.getAppToken());
            String response=HttpUtil.wmsPost(uri.toString(),params);
            respJson=JSONObject.parseObject(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (respJson==null||!respJson.getBoolean("success")){
            logger.error("分页获取WMS数据异常:,msg={}",respJson.getString("message"));
            return;
        }
        Inventory exits=this.inventoryMapper.getByWPinlianSku(warehouseDTO.getWarehouseId(),sku);
        JSONObject pageInfo=respJson.getJSONObject("data").getJSONObject("pageInfo");
        JSONObject inv=pageInfo.getJSONArray("list").getJSONObject(0);
        Inventory inventoryDO = this.wmsJsonToInv(inv);
        inventoryDO.setWarehouseId(warehouseDTO.getWarehouseId());
        if (exits==null){
            Object object=this.remoteCommodityService.getBySku(sku,null,null);
            if (object==null){
                logger.error("sku={}查询不出商品",sku);
                return;
            }
            JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object));
            if (jsonObject.getJSONObject("data")!=null){
                inventoryDO.setSupplierSku(jsonObject.getJSONObject("data").getString("systemSku"));
                inventoryDO.setSupplierId(jsonObject.getJSONObject("data").getInteger("supplierId"));
            }
            SkuWarehouseMap skuMap=new SkuWarehouseMap();
            skuMap.setWarehouseId(warehouseDTO.getWarehouseId());
            skuMap.setPinlianSku(inventoryDO.getPinlianSku());
            this.inventoryMapper.insert(inventoryDO);
            this.skuMapMapper.insert(skuMap);
        }else {
            inventoryDO.setId(exits.getId());
            this.inventoryMapper.updateByPrimaryKey(inventoryDO);
        }
    }

    /**
     * 修改本地待出货数量
     *
     * @param warehouseId
     * @param pinlianSku
     * @param qty
     * @return
     */
    @Override
    @TxTransaction(isStart = true)
    public Integer updateLocalShipping(Integer warehouseId, String pinlianSku, Integer qty) {
        WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(warehouseId);
        if (WarehouseFirmEnum.RONDAFUL.getCode().equals(warehouseDTO.getFirmCode())){
            return this.inventoryMapper.updateLocalShipping(warehouseId,pinlianSku,qty);
        }else if (WarehouseFirmEnum.GOODCANG.getCode().equals(warehouseDTO.getFirmCode())){
            this.updateGInventory(warehouseDTO.getAppToken(),pinlianSku,warehouseDTO.getWarehouseCode());
            return 1;
        }else {
            return 1;
        }
    }

    /**
     * 更改sku绑定的卖家
     *
     * @param skus
     * @return
     */
    @Async
    @Override
    public void bindSeller(List<String> skus) {
        if (CollectionUtils.isEmpty(skus)){
            return;
        }
        skus.forEach( sku -> {
            FeignResult<List<String>> feignResult=this.remoteCommodityService.getSkuSellerList(sku);
            if (!feignResult.getSuccess()){
                logger.error("调用商品服务异常:msg={}",feignResult.getMsg());
                return;
            }
            List<String> qsku=new ArrayList<>(1);
            qsku.add(sku);
            List<Integer> warehouseIds=this.skuMapMapper.getsByPinsku(qsku);
            warehouseIds.forEach(warehouseId -> {
                this.inventoryMapper.updateSellerId(CollectionUtils.isEmpty(feignResult.getData())?null:JSONObject.toJSONString(feignResult.getData()),warehouseId,sku);
            });
        });
    }

    @Override
    public void initBindSeller(){
        InventoryServiceImpl service=SpringContextUtil.getBean(InventoryServiceImpl.class);
        for (int i = 0; i < 20; i++) {
            logger.info("开始拉取第{}张表的数据",i);
            String tableName="t_sku_warehouse_map_"+i;
            Integer start=1;
            Integer pageSize=500;
            Boolean hasNext=false;
            do {
                PageHelper.startPage(start,pageSize);
                List<String> list=this.skuMapMapper.getsSku(tableName);
                PageInfo<String> pageInfo=new PageInfo<>(list);
                service.bindSeller(pageInfo.getList());
                if (hasNext=pageInfo.isHasNextPage()){
                    start++;
                }
            }while (hasNext);
        }
    }

    /**
     * 查询app的首页统计
     *
     * @param supplierId
     * @param warehouseIds
     * @return
     */
    @Override
    public AppCountDTO getCount(Integer supplierId, List<Integer> warehouseIds) {
        AppCountDTO result=new AppCountDTO(0,0,0);
        List<Integer> supplierIds=new ArrayList<>(2);
        supplierIds.add(supplierId);
        supplierIds.add(0);
        List<WarehouseCountryDTO> list=this.basicsService.getsWarehouseList(supplierIds,warehouseIds,null);
        if (CollectionUtils.isEmpty(list)){
            return result;
        }
        warehouseIds=new ArrayList<>();
        for (WarehouseCountryDTO dto:list) {
            warehouseIds.add(dto.getWarehouseId());
        }
        logger.info("仓库数量:size={}",list.size());
        result.setSkuCount(this.inventoryMapper.getAppCount(warehouseIds,supplierId,null));
        result.setAvailableCount(list.size());
        result.setWarnCount(this.inventoryMapper.getAppCount(warehouseIds,supplierId,1));
        return result;
    }

    @Async
    @Deprecated
    public void erpInsert(List<String> skus,String warehouseCode,Integer warehouseId){
        Map<String,String> params=new HashMap<>(2);
        params.put("sku_arr",JSONObject.toJSONString(skus));
        params.put("warehouse_code",warehouseCode);
        params.put("page","1");
        params.put("pageSize",String.valueOf(skus.size()));
        JSONObject erpRes=remoteErpService.getInventory(params);
        List<Inventory> insertList=new ArrayList<>(1000);
        List<SkuWarehouseMap> skuList=new ArrayList<>(1000);
        if (erpRes.getInteger("count")>0){
            for (int j = 0; j < erpRes.getJSONArray("lists").size(); j++) {
                JSONObject data=erpRes.getJSONArray("lists").getJSONObject(j);
                Inventory inventory=new Inventory();
                inventory.setWarehouseId(warehouseId);
                inventory.setSupplierSku(data.getString("sku"));
                inventory.setInstransitQty(data.getInteger("instransit_quantity"));
                inventory.setAvailableQty(data.getInteger("available_quantity"));
                inventory.setQty(data.getInteger("quantity"));
                inventory.setWaitingShippingQty(data.getInteger("waiting_shipping_quantity"));
                inventory.setAllocatingQty(data.getInteger("allocating_quantity"));
                inventory.setDefectsQty(data.getInteger("defects_quantity"));
                inventory.setUpdateDate(new Date());
                inventory.setSyncTime(data.getInteger("sync_time")==null?new Date():new Date(data.getInteger("sync_time")*1000));
                inventory.setWarnVal(0);
                SkuWarehouseMap skuMap=new SkuWarehouseMap();
                skuMap.setWarehouseId(warehouseId);

                Object object=this.remoteCommodityService.getBySku(data.getString("sku"),null,null);
                if (object!=null){
                    JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object));
                    if (jsonObject.getJSONObject("data")!=null){
                        inventory.setPinlianSku(jsonObject.getJSONObject("data").getString("systemSku"));
                        inventory.setSupplierId(jsonObject.getJSONObject("data").getInteger("supplierId"));
                        skuMap.setPinlianSku(inventory.getPinlianSku());
                    }else {
                        continue;
                    }

                }else {
                    continue;
                }
                Inventory exits=this.inventoryMapper.getByWPinlianSku(warehouseId,inventory.getPinlianSku());
                if (exits==null){
                    insertList.add(inventory);
                    skuList.add(skuMap);
                }else {
                    inventory.setId(exits.getId());
                    this.inventoryMapper.updateByPrimaryKey(inventory);
                }

            }
        }
        if (CollectionUtils.isNotEmpty(insertList)){
            this.inventoryMapper.insertBatch(insertList);
            this.skuMapMapper.insertBatch(skuList);
        }
    }

    /**
     * wms库存拉去同步
     * @param initDTO
     */
    @Async
    public void wmsInit(WarehouseInitDTO initDTO){
        initDTO.getList().forEach(warehouseListDTO->{

            JSONObject params=new JSONObject();
            params.put("pageSize",500);
            params.put("pageNum",0);
            params.put("warehouseCode",warehouseListDTO.getWarehouseCode());
            Boolean hastNext=false;
            do {
                List<Inventory> insertList=new ArrayList<>(params.getInteger("pageSize"));
                List<SkuWarehouseMap> skuList=new ArrayList<>(params.getInteger("pageSize"));
                params.put("pageNum",params.getIntValue("pageNum")+1);
                JSONObject respJson =null;
                try {
                    URIBuilder uri=new URIBuilder(this.wmsUrl+"/center/external/skuInventory");
                    uri.addParameter("customerAppId",initDTO.getAppKey());
                    uri.addParameter("sign",initDTO.getAppToken());
                    String response=HttpUtil.wmsPost(uri.toString(),params);
                    respJson=JSONObject.parseObject(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (respJson==null||!respJson.getBoolean("success")){
                    logger.error("分页获取WMS数据异常:,msg={}",respJson.getString("message"));
                    break;
                }
                JSONObject pageInfo=respJson.getJSONObject("data").getJSONObject("pageInfo");
                hastNext=pageInfo.getBoolean("hasNextPage");
                for (Integer i=0;i<pageInfo.getJSONArray("list").size();i++) {
                    JSONObject inv=pageInfo.getJSONArray("list").getJSONObject(i);
                    Inventory inventoryDO = this.wmsJsonToInv(inv);
                    inventoryDO.setWarehouseId(warehouseListDTO.getId());
                    Inventory inventory=this.inventoryMapper.getByWPinlianSku(inventoryDO.getWarehouseId(),inventoryDO.getPinlianSku());
                    if (inventory==null){
                        Object object=this.remoteCommodityService.getBySku(inventoryDO.getPinlianSku(),null,null);
                        if (object!=null){
                            JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object));
                            inventoryDO.setSupplierId(jsonObject.getJSONObject("data")==null?0:jsonObject.getJSONObject("data").getInteger("supplierId"));
                            inventoryDO.setSupplierSku(jsonObject.getJSONObject("data")==null?null:jsonObject.getJSONObject("data").getString("supplierSku"));
                        }
                        inventoryDO.setWarnVal(-1);
                        SkuWarehouseMap skuMap=new SkuWarehouseMap();
                        skuMap.setWarehouseId(warehouseListDTO.getId());
                        skuMap.setPinlianSku(inventoryDO.getPinlianSku());
                        insertList.add(inventoryDO);
                        skuList.add(skuMap);
                    }else {
                        inventoryDO.setId(inventory.getId());
                        this.inventoryMapper.updateByPrimaryKey(inventoryDO);
                    }
                }
                if (CollectionUtils.isNotEmpty(insertList)){
                    this.inventoryMapper.insertBatch(insertList);
                    this.skuMapMapper.insertBatch(skuList);
                }
            }while (hastNext);
        });
    }

    /**
     * wms  json 转换为 Inventory
     * @param jsonObject
     * @return
     */
    private Inventory wmsJsonToInv(JSONObject jsonObject){
        Inventory inventoryDO = new Inventory();
        inventoryDO.setInstransitQty(jsonObject.getInteger("inTransitGoods"));
        inventoryDO.setAvailableQty(jsonObject.getInteger("deliveryGoods"));
        inventoryDO.setQty(jsonObject.getInteger("insideGoods"));
        inventoryDO.setWaitingShippingQty(jsonObject.getInteger("reviewGoods"));
        inventoryDO.setDefectsQty(jsonObject.getInteger("freezeGoods"));
        inventoryDO.setPinlianSku(jsonObject.getString("goodsSku"));
        inventoryDO.setPendingQty(jsonObject.getInteger("pending"));
        inventoryDO.setStockingQty(jsonObject.getInteger("sold_shared"));
        inventoryDO.setPiNoStockQty(jsonObject.getInteger("stockoutGoods"));
        inventoryDO.setSyncTime(new Date());
        inventoryDO.setUpdateDate(new Date());
        return inventoryDO;
    }

    /**
     * erp的库存同步到db
     * @param data
     */
    private void insertErp(JSONObject data){

        Inventory inventory=new Inventory();
        inventory.setSupplierSku(data.getString("sku"));
        inventory.setInstransitQty((data.getInteger("instransit_quantity")==null?0:data.getInteger("instransit_quantity"))+(data.getInteger("allocating_quantity")==null?0:(data.getInteger("allocating_quantity"))));
        inventory.setAvailableQty(data.getInteger("available_quantity"));
        inventory.setQty(data.getInteger("quantity"));
        inventory.setWaitingShippingQty(data.getInteger("waiting_shipping_quantity"));
        inventory.setAllocatingQty(data.getInteger("allocating_quantity"));
        inventory.setDefectsQty(data.getInteger("defects_quantity"));
        inventory.setUpdateDate(new Date());
        inventory.setSyncTime(data.getInteger("sync_time")==null?new Date():new Date(data.getInteger("sync_time")*1000));
        FeignResult feignResult=this.remoteCommodityService.getTwoSku(data.getString("sku"));
        if (!feignResult.getSuccess()||feignResult.getData()==null){
            return;
        }
        JSONArray users=JSONObject.parseArray(JSONObject.toJSONString(feignResult.getData()));
        for (int j = 0; j < users.size(); j++) {
            JSONObject user=users.getJSONObject(j);
            Integer supplierId=user.getInteger("supplierId");
            Integer warehouseId=this.basicsService.getCodeAndId(data.getString("warehouse_code"),supplierId);
            if (warehouseId==null){
                continue;
            }
            inventory.setWarehouseId(warehouseId);
            inventory.setPinlianSku(user.getString("systemSku"));
            Inventory exits=this.inventoryMapper.getByWPinlianSku(warehouseId,user.getString("systemSku"));
            if (exits==null){
                inventory.setSupplierId(supplierId);
                inventory.setWarnVal(-1);
                SkuWarehouseMap skuMap=new SkuWarehouseMap();
                skuMap.setWarehouseId(inventory.getWarehouseId());
                skuMap.setPinlianSku(inventory.getPinlianSku());
                this.inventoryMapper.insert(inventory);
                this.skuMapMapper.insert(skuMap);
            }else {
                inventory.setId(exits.getId());
                this.inventoryMapper.updateByPrimaryKey(inventory);
            }
            this.messageService.sendInventory(inventory.getWarehouseId(),inventory.getPinlianSku());
        }
    }

}
