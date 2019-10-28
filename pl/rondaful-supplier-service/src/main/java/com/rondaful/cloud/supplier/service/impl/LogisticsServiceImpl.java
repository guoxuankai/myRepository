package com.rondaful.cloud.supplier.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;
import com.rondaful.cloud.common.model.vo.freight.SupplierGroupVo;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.entity.Logistics.Logistics;
import com.rondaful.cloud.supplier.entity.Logistics.LogisticsMap;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.mapper.LogisticsMapMapper;
import com.rondaful.cloud.supplier.mapper.LogisticsMapper;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseDTO;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseInitDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.*;
import com.rondaful.cloud.supplier.model.enums.StatusEnums;
import com.rondaful.cloud.supplier.model.enums.WarehouseFirmEnum;
import com.rondaful.cloud.supplier.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xqq
 * @Date: 2019/10/16
 * @Description:
 */
@Service("logisticsService")
public class LogisticsServiceImpl implements ILogisticsService {
    private final Logger logger = LoggerFactory.getLogger(LogisticsServiceImpl.class);

    @Autowired
    private LogisticsMapper mapper;

    @Autowired
    private LogisticsMapMapper mapMapper;

    @Autowired
    private IWarehouseBasicsService basicsService;

    @Autowired
    private IGoodServiceService goodService;
    @Autowired
    private IErpServiceService erpService;

    @Autowired
    private IWmsServiceService wmsService;


    /**
     * 初始话物流方式
     *
     * @param firmId
     * @return
     */
    @Async
    @Override
    public Integer init(Integer firmId) {
        WarehouseInitDTO dto=this.basicsService.getByFirmId(firmId);
        Map<String,Integer> map=new HashMap<>(dto.getList().size());
        dto.getList().forEach(item -> {
            map.put(item.getWarehouseCode(),item.getId());
        });
        List<TranLogisticsDTO> list=new ArrayList<>(20);
        switch (dto.getFirmCode()){
            case "RONDAFUL":
                list=this.erpService.getsLogistics(null);
                break;
            case "WMS":
                list=this.wmsService.getLogisticsList(dto.getAppKey(),dto.getAppToken(),null);
                break;
            case "GOODCANG":
                list=this.goodService.getsLogistics(dto.getAppKey(),dto.getAppToken(),null);
                break;
            default:
                return 0;
        }
        List<Logistics> insertList=new ArrayList<>(list.size());
        list.forEach(tranDTO -> {
            Logistics logistics=new Logistics();
            BeanUtils.copyProperties(tranDTO,logistics);
            logistics.setWarehouseId(map.get(tranDTO.getWarehouseCode()));
            logistics.setStatus(StatusEnums.ACTIVATE.getStatus());
            logistics.setVersion(1);
            insertList.add(logistics);
        });
        return this.mapper.insertBatch(insertList);
    }

    /**
     * 根据仓库id分页获取物流方式
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<LogisticsPageDTO> getsPage(QueryPageDTO dto) {
        PageHelper.startPage(dto.getCurrentPage(),dto.getPageSize());
        List<Logistics> list=this.mapper.getsPage(dto.getQueryType(),dto.getQueryText(),dto.getWarehouseId());
        PageInfo<Logistics> pageInfo=new PageInfo<>(list);
        PageDTO<LogisticsPageDTO> result=new PageDTO<>((int)pageInfo.getTotal(),dto.getCurrentPage());
        if (CollectionUtils.isEmpty(pageInfo.getList())){
            return result;
        }
        List<LogisticsPageDTO> listData=new ArrayList<>(pageInfo.getList().size());
        pageInfo.getList().forEach(pageDTO -> {
            LogisticsPageDTO data=new LogisticsPageDTO();
            BeanUtils.copyProperties(pageDTO,data);
            if (StringUtils.isNotEmpty(dto.getLanguageType())){
                data.setName(pageDTO.getNameEn());
                data.setSpName(pageDTO.getSpNameEn());
            }
            listData.add(data);
        });
        result.setList(listData);
        return result;
    }

    /**
     * 修改状态
     *
     * @param id
     * @param status
     * @return
     */
    @Override
    public Integer updateStatus(Integer id, Integer status) {
        Integer result=this.mapper.updateStatus(id,status);
        return result;
    }

    /**
     * 根据id获取物流详情信息
     *
     * @param id
     * @param languageType
     * @return
     */
    @Override
    public LogisticsDetailDTO get(Integer id, String languageType) {
        Logistics logistics=this.mapper.selectByPrimaryKey(id.longValue());
        if (logistics==null){
            return null;
        }
        LogisticsDetailDTO result=new LogisticsDetailDTO();
        BeanUtils.copyProperties(logistics,result);
        WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(logistics.getWarehouseId());
        result.setFirmName(warehouseDTO.getName());
        result.setFirmService(StringUtils.isEmpty(languageType)? WarehouseFirmEnum.getByCode(warehouseDTO.getFirmCode()): Utils.translation(WarehouseFirmEnum.getByCode(warehouseDTO.getFirmCode())));
        List<LogisticsMap> mapList=this.mapMapper.getByLogisticsId(id);
        if (CollectionUtils.isNotEmpty(mapList)){
            List<LogisticsMapDTO> maps=new ArrayList<>(mapList.size());
            mapList.forEach(logisticsMap -> {
                LogisticsMapDTO mapDTO=new LogisticsMapDTO();
                BeanUtils.copyProperties(logisticsMap,mapDTO);
                maps.add(mapDTO);
            });
            result.setMaps(maps);
        }
        return result;
    }

    /**
     * 修改平台物流映射
     *
     * @param list
     * @return
     */
    @Override
    public Integer updateMap(List<LogisticsMapDTO> list) {
        this.mapMapper.del(list.get(0).getLogisticsId());
        List<LogisticsMap> maps=new ArrayList<>(3);
        for (LogisticsMapDTO mapDTO:list) {
            if (StringUtils.isEmpty(mapDTO.getPlatformLogistics())){
                continue;
            }
            LogisticsMap logisticsMap=new LogisticsMap();
            BeanUtils.copyProperties(mapDTO,logisticsMap);
            maps.add(logisticsMap);
        }
        if (CollectionUtils.isEmpty(maps)){
            return 0;
        }
        Integer result=this.mapMapper.insertBatch(maps);
        return result;
    }

    /**
     * 查询物流费
     *
     * @param dto
     * @return
     */
    @Override
    public List<LogisticsSelectDTO> getSelect(QuerySelectDTO dto) {
        WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(dto.getWarehouseId());
        List<TranLogisticsCostDTO> list=this.getTranList(dto,warehouseDTO);
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        List<LogisticsSelectDTO> result=new ArrayList<>(list.size());
        for (TranLogisticsCostDTO tranDTO:list) {
            LogisticsDetailDTO smDTO=this.getByCode(tranDTO.getSmCode(),warehouseDTO.getWarehouseId());
            if (smDTO==null||StatusEnums.DISABLE.getStatus().equals(smDTO.getStatus())){
                continue;
            }
            if (dto.getChannelId()!=null){
                if (dto.getIsHandOrder()){
                    if (CollectionUtils.isEmpty(smDTO.getMaps())){
                        continue;
                    }
                    Boolean exits=false;
                    for (LogisticsMapDTO aa:smDTO.getMaps()) {
                        if (!exits){
                            exits=aa.getLogisticsId().equals(smDTO.getId());
                        }
                    }
                    if (!exits){
                        continue;
                    }
                }
            }
            LogisticsSelectDTO selectDTO=new LogisticsSelectDTO();
            BeanUtils.copyProperties(tranDTO,selectDTO);
            selectDTO.setSpCode(smDTO.getSpCode());
            selectDTO.setSpName(smDTO.getSpName());
            result.add(selectDTO);
        }
        result.sort( (e1,e2) -> {
            Integer aa=0;
            switch (dto.getQueryType()){
                case 2:
                    BigDecimal aa1=e1.getTotal().multiply(new BigDecimal("0.5")).add(new BigDecimal(e1.getSmDeliveryTimeMin()).multiply(new BigDecimal("0.5")));
                    BigDecimal aa2=e2.getTotal().multiply(new BigDecimal("0.5")).add(new BigDecimal(e2.getSmDeliveryTimeMin()).multiply(new BigDecimal("0.5")));
                    aa=aa1.compareTo(aa2);
                    break;
                case 3:
                    aa=e1.getSmDeliveryTimeMin().compareTo(e2.getSmDeliveryTimeMin());
                    break;
                case 1:
                    aa=e1.getTotal().compareTo(e2.getTotal());
                    break;
                default:
            }
            return aa;
        });
        return result.subList(0,result.size()>dto.getIndex()?dto.getIndex():result.size());
    }

    /**
     * 订单运费试算
     *
     * @param dto
     * @return
     */
    @Override
    public LogisticsCostVo orderLogistics(LogisticsCostVo dto) {
        logger.error("订单运费试算:dto={}", JSONObject.toJSONString(dto));
        WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(Integer.valueOf(dto.getWarehouseId()));
        if (warehouseDTO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"没有对应的仓库");
        }
        if (CollectionUtils.isNotEmpty(dto.getSellers())){
            dto.setSellers(this.getGroup(dto.getSellers(),dto.getPlatformType(),warehouseDTO,dto.getCountryCode(),dto.getCity(),dto.getPostCode(),dto.getLogisticsCode()));
        }
        if (CollectionUtils.isNotEmpty(dto.getSupplier())){
            dto.setSupplier(this.getGroup(dto.getSupplier(),dto.getPlatformType(),warehouseDTO,dto.getCountryCode(),dto.getCity(),dto.getPostCode(),dto.getLogisticsCode()));
        }
        return dto;
    }

    /**
     * 刊登时运费试算
     *
     * @param dto
     * @return
     */
    @Override
    public LogisticsPublishDTO publishLogistics(QuerySelectDTO dto) {
        dto.setIndex(1);
        dto.setIsHandOrder(false);
        List<LogisticsSelectDTO>  list=this.getSelect(dto);
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        LogisticsPublishDTO result=new LogisticsPublishDTO();
        BeanUtils.copyProperties(list.get(0),result);
        List<String> shippingArr=new ArrayList<>(1);
        shippingArr.add(result.getSmCode());
        dto.setShippingArr(shippingArr);
        Map<String,BigDecimal> map=new HashMap<>(dto.getSkus().size());
        dto.getSkus().forEach(skuNum -> {
            QuerySelectDTO childQuery=new QuerySelectDTO();
            BeanUtils.copyProperties(dto,childQuery);
            List<SkuNum> childSku=new ArrayList<>(1);
            childSku.add(skuNum);
            childQuery.setSkus(childSku);
            List<LogisticsSelectDTO> childList=this.getSelect(childQuery);
            map.put(skuNum.getSku(),childList.get(0).getTotal());
        });
        result.setSkuCost(map);
        return result;
    }

    /**
     * 获取仓库下所有的物流方式
     *
     * @param warehouseId
     * @return
     */
    @Override
    public List<LogisticsSelectDTO> getByWarehouseId(Integer warehouseId,List<String> codes) {
        List<Logistics> list=this.mapper.getByWarehouseId(warehouseId,codes);
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        List<LogisticsSelectDTO> result=new ArrayList<>();
        list.forEach(logistics -> {
            LogisticsSelectDTO dto=new LogisticsSelectDTO();
            BeanUtils.copyProperties(logistics,dto);
            dto.setSmCode(logistics.getCode());
            dto.setSmName(logistics.getName());
            dto.setSmNameEn(logistics.getNameEn());
            result.add(dto);
        });
        return result;
    }

    /**
     * 获取平台物流信息
     *
     * @param code
     * @param warehouseId
     * @param platform
     */
    @Override
    public LogisticsMapDTO getPlatLogisticsByCode(String code, Integer warehouseId, Integer platform) {
        LogisticsDetailDTO dto=this.getByCode(code,warehouseId);
        if (dto==null||CollectionUtils.isEmpty(dto.getMaps())){
            return null;
        }
        for (LogisticsMapDTO mapDTO:dto.getMaps()) {
            if (mapDTO.getLogisticsId().equals(dto.getId())){
                return mapDTO;
            }
        }
        return null;

    }

    /**
     * 根据仓库id和物流code查询物流信息
     * @param code
     * @param warehouseId
     * @return
     */
    private LogisticsDetailDTO getByCode(String code, Integer warehouseId){
        Logistics logistics=this.mapper.getByCode(code,warehouseId);
        if (logistics==null){
            return null;
        }
        LogisticsDetailDTO result=new LogisticsDetailDTO();
        BeanUtils.copyProperties(logistics,result);
        List<LogisticsMap> maps=this.mapMapper.getByLogisticsId(result.getId());
        if (CollectionUtils.isNotEmpty(maps)){
            List<LogisticsMapDTO> mapDTOList= JSONArray.parseArray(JSONArray.toJSONString(maps),LogisticsMapDTO.class);
            result.setMaps(mapDTOList);
        }
        return result;
    }

    /**
     * 调取运费
     * @param dto
     * @return
     */
    private List<TranLogisticsCostDTO>  getTranList(QuerySelectDTO dto,WarehouseDTO warehouseDTO){
        if (warehouseDTO==null){
            return null;
        }
        List<TranLogisticsCostDTO> list=new ArrayList<>();
        switch (warehouseDTO.getFirmCode()){
            case "RONDAFUL":
                EQueryDeliveryFeeDTO eFeeDTO=new EQueryDeliveryFeeDTO();
                BeanUtils.copyProperties(dto,eFeeDTO);
                if (CollectionUtils.isEmpty(dto.getSkus())){
                    eFeeDTO.setSearch_type("1");
                }else {
                    eFeeDTO.setSearch_type("2");
                }
                eFeeDTO.setWarehouse_code(warehouseDTO.getWarehouseCode());
                eFeeDTO.setCountry_code(dto.getCountryCode());
                eFeeDTO.setWeight(dto.getWeight()==null?null:dto.getWeight()*1000);
                eFeeDTO.setShipping_code_arr(dto.getShippingArr());
                list=this.erpService.getCalculateFee(eFeeDTO);
                break;
            case "WMS":
                WQueryDeliveryFeeDTO wFeeDTO = new WQueryDeliveryFeeDTO();
                BeanUtils.copyProperties(dto,wFeeDTO);
                if (CollectionUtils.isEmpty(dto.getSkus())){
                    wFeeDTO.setSearchType(1);
                }else {
                    wFeeDTO.setSearchType(2);
                }
                wFeeDTO.setPlatform(dto.getChannelId().toString());
                wFeeDTO.setWarehouse(warehouseDTO.getWarehouseCode());
                wFeeDTO.setCountry(dto.getCountryCode());
                wFeeDTO.setWeight(dto.getWeight()==null?null:dto.getWeight()*1000);
                if(CollectionUtils.isNotEmpty(dto.getShippingArr())){
                    wFeeDTO.setMethod(dto.getShippingArr().get(0));
                }
                wFeeDTO.setCity(dto.getCity());
                List<Map<String,Object>> skuList = new ArrayList<>();
                dto.getSkus().forEach(sku ->{
                    Map<String,Object> skuMap = new HashMap<>();
                    skuMap.put("sku",sku.getSku());
                    skuMap.put("quantity",sku.getNum());
                    skuList.add(skuMap);

                });
                wFeeDTO.setSkuQuantityList(skuList);
                list=this.wmsService.getFreight(warehouseDTO.getAppKey(),warehouseDTO.getAppToken(),wFeeDTO);
                break;
            case "GOODCANG":
                GQueryDeliveryFeeDTO gFeeDTO=new GQueryDeliveryFeeDTO();
                BeanUtils.copyProperties(dto,gFeeDTO);
                gFeeDTO.setSm_code(CollectionUtils.isEmpty(dto.getShippingArr())?null:dto.getShippingArr().get(0));
                gFeeDTO.setCountry_code(dto.getCountryCode());
                gFeeDTO.setPostcode(dto.getZip());
                if (CollectionUtils.isNotEmpty(dto.getSkus())){
                    List<String> sku=new ArrayList<>();
                    dto.getSkus().forEach(skuNum -> {
                        sku.add(skuNum.getSku()+":"+skuNum.getNum());
                    });
                    gFeeDTO.setSku(sku);
                }
                gFeeDTO.setWarehouse_code(warehouseDTO.getWarehouseCode());
                list=this.goodService.getCalculateFee(warehouseDTO.getAppKey(),warehouseDTO.getAppToken(),gFeeDTO);
                break;
            default:
                return null;
        }
        return list;
    }

    /**
     * 分组计算物流方式
     * @param list
     * @param platformType
     * @param warehouseDTO
     * @param countryCode
     * @param city
     * @param zip
     * @param logisticsCode
     * @return
     */
    private List<SupplierGroupVo> getGroup(List<SupplierGroupVo> list,String platformType,WarehouseDTO warehouseDTO,String countryCode,String city,String zip,String logisticsCode){
        QuerySelectDTO dto=new QuerySelectDTO();
        dto.setChannelId(Integer.valueOf(platformType));
        dto.setWarehouseId(warehouseDTO.getWarehouseId());
        dto.setCity(city);
        dto.setCountryCode(countryCode);
        dto.setZip(zip);
        List<String> shippingArr=new ArrayList<>(1);
        shippingArr.add(logisticsCode);
        dto.setShippingArr(shippingArr);
        list.forEach(group -> {
            List<SkuNum> skus1=new ArrayList<>();
            group.getItems().forEach(item -> {
                List<SkuNum> skus2=new ArrayList<>();
                SkuNum skuNum=new SkuNum(item.getSku(),item.getSkuNumber());
                skus2.add(skuNum);
                skus1.add(skuNum);
                dto.setSkus(skus2);
                List<TranLogisticsCostDTO> lList=this.getTranList(dto,warehouseDTO);
                item.setSkuCost(CollectionUtils.isEmpty(lList)?null:lList.get(0).getTotal());
            });
            dto.setSkus(skus1);
            List<TranLogisticsCostDTO> cList=this.getTranList(dto,warehouseDTO);
            group.setSupplierCost(CollectionUtils.isEmpty(cList)?null:cList.get(0).getTotal());
        });
        return list;
    }

}
