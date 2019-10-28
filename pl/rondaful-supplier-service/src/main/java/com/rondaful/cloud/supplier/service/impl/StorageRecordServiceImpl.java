package com.rondaful.cloud.supplier.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.granary.GranaryUtils;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.entity.storage.StorageCollecting;
import com.rondaful.cloud.supplier.entity.storage.StorageRecord;
import com.rondaful.cloud.supplier.entity.storage.StorageRecordItem;
import com.rondaful.cloud.supplier.entity.storage.StorageRecordSpecific;
import com.rondaful.cloud.supplier.mapper.StorageCollectingMapper;
import com.rondaful.cloud.supplier.mapper.StorageRecordItemMapper;
import com.rondaful.cloud.supplier.mapper.StorageRecordMapper;
import com.rondaful.cloud.supplier.mapper.StorageRecordSpecificMapper;
import com.rondaful.cloud.supplier.model.dto.FeignResult;
import com.rondaful.cloud.supplier.model.dto.KeyValueDTO;
import com.rondaful.cloud.supplier.model.dto.basics.InitWarehouseDTO;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseDTO;
import com.rondaful.cloud.supplier.model.dto.reomte.user.FeignUserDTO;
import com.rondaful.cloud.supplier.model.dto.storage.*;
import com.rondaful.cloud.supplier.model.enums.StorageEnums;
import com.rondaful.cloud.supplier.model.enums.TransitEnum;
import com.rondaful.cloud.supplier.model.enums.WarehouseFirmEnum;
import com.rondaful.cloud.supplier.remote.RemoteCommodityService;
import com.rondaful.cloud.supplier.remote.RemoteUserService;
import com.rondaful.cloud.supplier.service.IStorageRecordService;
import com.rondaful.cloud.supplier.service.IWarehouseBasicsService;
import com.rondaful.cloud.supplier.utils.IDUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xqq
 * @Date: 2019/6/18
 * @Description:
 */
@Service("storageRecordServiceImpl")
public class StorageRecordServiceImpl implements IStorageRecordService {
    private final Logger logger = LoggerFactory.getLogger(StorageRecordServiceImpl.class);

    @Autowired
    private StorageRecordMapper recordMapper;
    @Autowired
    private StorageCollectingMapper collectingMapper;
    @Autowired
    private StorageRecordItemMapper itemMapper;
    @Autowired
    private StorageRecordSpecificMapper specificMapper;
    @Autowired
    private IWarehouseBasicsService basicsService;
    @Autowired
    private GranaryUtils granaryUtils;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RemoteCommodityService commodityService;
    @Autowired
    private RemoteUserService userService;
    @Value("${wsdl.url}")
    private String wsdlUrl;
    @Value("${brandslink.wms.url}")
    private String wmsUrl;

    /**
     * 文件类型
     */
    private final static String FILE_TYPE = "type";
    /**
     * base64转码
     */
    private final static String FILE_BASE64 = "base64";


    /**
     * 新建入库单
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(StorageRecordDTO dto) {
        Long id= new IDUtil(0L,0L).nextId();
        dto.setId(id);
        dto.setVerify(dto.getVerify()==null?StorageEnums.DRAFT.getVerify():dto.getVerify());
        StorageRecord record=new StorageRecord();
        BeanUtils.copyProperties(dto,record);
        record.setId(id);
        record.setCreateTime(new Date());
        if (WarehouseFirmEnum.GOODCANG.getCode().equals(dto.getFirmCode())){
            record.setReferenceNo(id.toString());
            record.setReceivingCode(this.createGRN(dto,"createGRN"));
        } else if (WarehouseFirmEnum.WMS.getCode().equals(dto.getFirmCode())){
            record.setReceivingCode(id.toString());
            dto.setReceivingCode(id.toString());
            if (StorageEnums.NO_AUDIT.getVerify().equals(dto.getVerify())){
                this.createWmsGTR(dto);
                record.setVerify(StorageEnums.WAIT.getVerify());
            }
        }
        this.recordMapper.insert(record);
        if (WarehouseFirmEnum.GOODCANG.getCode().equals(dto.getFirmCode())){
            if (TransitEnum.TRANSFER.getType().equals(dto.getTransitType())){
                if(dto.getSpecificDTO()==null){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "参数校验未通过");
                }
                if (dto.getSpecificDTO().getClearanceService()!=null&&dto.getSpecificDTO().getCollectingService().equals(1)){
                    List<StorageCollecting> list=new ArrayList<>(8);
                    dto.getCollectings().forEach( address -> {
                        StorageCollecting collecting=new StorageCollecting();
                        BeanUtils.copyProperties(address,collecting);
                        collecting.setStorageId(id);
                        list.add(collecting);
                    });
                    this.collectingMapper.insertBatch(list);
                }
                StorageRecordSpecific specific=new StorageRecordSpecific();
                BeanUtils.copyProperties(dto.getSpecificDTO(),specific);
                specific.setId(id);
                this.specificMapper.insert(specific);
            }
        }

        List<StorageRecordItem> list=new ArrayList<>(dto.getItems().size());
        dto.getItems().forEach( item -> {
            StorageRecordItem recordItem=new StorageRecordItem();
            BeanUtils.copyProperties(item,recordItem);
            recordItem.setStorageId(id);
            list.add(recordItem);
        });
        this.itemMapper.insertBatch(list);
        return id.toString();
    }

    /**
     * 编辑入库单
     *
     * @param dto
     * @return
     */
    @Override
    public Integer update(StorageRecordDTO dto) {
        StorageRecord oldDTO=this.recordMapper.selectByPrimaryKey(dto.getId());
        if (oldDTO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "修改对象不存在");
        }
        if (!oldDTO.getTransitType().equals(dto.getTransitType())){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "入库单类型不能变更");
        }
        if (!(StorageEnums.DRAFT.getVerify().equals(oldDTO.getVerify())||StorageEnums.ERROR.getVerify().equals(oldDTO.getVerify()))){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "非草稿状态入库单不可编辑");
        }
        StorageRecord recordDO=new StorageRecord();
        BeanUtils.copyProperties(dto,recordDO);
        recordDO.setUpdateTime(new Date());
        if (WarehouseFirmEnum.GOODCANG.getCode().equals(dto.getFirmCode())){
            recordDO.setReceivingCode(this.createGRN(dto,"modifyGRN"));
            if (TransitEnum.TRANSFER.getType().equals(dto.getTransitType())){
                if(dto.getSpecificDTO()==null){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "参数校验未通过");
                }

                if (dto.getSpecificDTO().getClearanceService()!=null&&dto.getSpecificDTO().getCollectingService().equals(1)){
                    List<StorageCollecting> list=new ArrayList<>(8);
                    dto.getCollectings().forEach( address -> {
                        StorageCollecting collecting=new StorageCollecting();
                        BeanUtils.copyProperties(address,collecting);
                        collecting.setStorageId(dto.getId());
                        list.add(collecting);
                    });
                    this.collectingMapper.delByStorageId(dto.getId());
                    this.collectingMapper.insertBatch(list);
                }
                StorageRecordSpecific specific=new StorageRecordSpecific();
                BeanUtils.copyProperties(dto.getSpecificDTO(),specific);
                specific.setId(dto.getId());
                this.specificMapper.updateByPrimaryKey(specific);
            }
        }else if(WarehouseFirmEnum.WMS.getCode().equals(dto.getFirmCode())){
            if (StorageEnums.NO_AUDIT.getVerify().equals(dto.getVerify())){
                this.createWmsGTR(dto);
                recordDO.setVerify(StorageEnums.WAIT.getVerify());
            }
        }

        List<StorageRecordItem> list=new ArrayList<>(dto.getItems().size());
        dto.getItems().forEach( item -> {
            StorageRecordItem recordItem=new StorageRecordItem();
            BeanUtils.copyProperties(item,recordItem);
            recordItem.setStorageId(dto.getId());
            list.add(recordItem);
        });
        this.itemMapper.delByStorageId(dto.getId());
        this.itemMapper.insertBatch(list);
        return this.recordMapper.updateByPrimaryKeySelective(recordDO);
    }

    /**
     * 分页获取入库单
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<StoragePageDTO> getsPage(StorageQueryPageDTO dto) {

        PageHelper.startPage(dto.getCurrentPage(),dto.getPageSize());
        List<StorageRecord> list=this.recordMapper.getsPage(dto.getWarehouseId(),dto.getSupplierId(),dto.getReceivingCode(),dto.getStatus());
        PageInfo<StorageRecord> pageInfo=new PageInfo(list);
        PageDTO<StoragePageDTO> result=new PageDTO<>((int)pageInfo.getTotal(),dto.getCurrentPage());
        List<StoragePageDTO> dataList=new ArrayList<>();
        pageInfo.getList().forEach( record -> {
            StoragePageDTO dto1=new StoragePageDTO();
            BeanUtils.copyProperties(record,dto1);
            WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(record.getWarehouseId());
            dto1.setId(record.getId().toString());
            dto1.setFirmName(StringUtils.isEmpty(dto.getLanguageType())?WarehouseFirmEnum.getByCode(warehouseDTO.getFirmCode()):Utils.translation(WarehouseFirmEnum.getByCode(warehouseDTO.getFirmCode())));
            dto1.setWarehouseName(StringUtils.isEmpty(dto.getLanguageType())?warehouseDTO.getWarehouseName(): Utils.translation(warehouseDTO.getWarehouseName()));
            dto1.setFirmCode(warehouseDTO.getFirmCode());
            FeignResult<FeignUserDTO> feignResult=this.userService.getNewUser(record.getTopUserId(),null,UserEnum.platformType.SUPPLIER.getPlatformType());
            if (feignResult.getSuccess()){
                dto1.setSupplierName(feignResult.getData().getLoginName());
            }
            dto1.setName(StringUtils.isEmpty(dto.getLanguageType())?warehouseDTO.getName():Utils.translation(warehouseDTO.getName()));
            dataList.add(dto1);
        });
        result.setList(dataList);
        return result;
    }

    /**
     * 根据id获取入库单详细信息
     *
     * @param id
     * @return
     */
    @Override
    public StorageRecordDTO getById(Long id) {
        StorageRecordDTO result=new StorageRecordDTO();
        StorageRecord record=this.recordMapper.selectByPrimaryKey(id);
        if (record==null){
            return result;
        }
        BeanUtils.copyProperties(record,result);
        WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(record.getWarehouseId());
        result.setWarehouseName(warehouseDTO.getName()+"-"+warehouseDTO.getWarehouseName());
        result.setServiceName(WarehouseFirmEnum.getByName(warehouseDTO.getFirmCode()));
        result.setServiceId(warehouseDTO.getFirmCode().hashCode());
        List<StoregeItemDTO> items=new ArrayList<>();
        this.itemMapper.getsByStorageId(record.getId()).forEach(recordItem -> {
            StoregeItemDTO item=new StoregeItemDTO();
            BeanUtils.copyProperties(recordItem,item);
            Object obj=this.commodityService.getBySku(recordItem.getProductSku(),null,null);
            JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(obj)).getJSONObject("data");
            if (obj!=null){
                item.setCommodityName(jsonObject.getString("commodityNameCn"));
                item.setCommodityNameEn(jsonObject.getString("commodityNameEn"));
            }else {
                logger.error("sku={}的商品不存在",recordItem.getProductSku());
            }
            items.add(item);
        });
        result.setItems(items);
        if (WarehouseFirmEnum.GOODCANG.getCode().equals(record.getFirmCode())&&TransitEnum.TRANSFER.getType().equals(record.getTransitType())){
            StorageRecordSpecific specific=this.specificMapper.selectByPrimaryKey(record.getId());
            StorageSpecificDTO specificDTO=new StorageSpecificDTO();
            BeanUtils.copyProperties(specific,specificDTO);
            result.setSpecificDTO(specificDTO);
            for (SmCodeDTO smCode:this.getTransferWarehouse(record.getWarehouseId())) {
                if (smCode.getSmCode().equals(specific.getTransitWarehouseCode())){
                    specificDTO.setTransitWarehouseCodeName(smCode.getSmCodeName());
                    break;
                }
            }
            for (SmCodeDTO smCode:this.getsSmCode(Integer.valueOf(record.getReceivingShippingType()),record.getWarehouseId())) {
                if (smCode.getSmCode().equals(specific.getSmCode())){
                    specificDTO.setSmCodeName(smCode.getSmCodeName());
                    break;
                }
            }
            if (specific.getCollectingService().equals(1)){
                List<StorageCollecting> collectings=this.collectingMapper.getsByStorageId(id);
                List<StorageCollectingDTO> collectingDTOS=new ArrayList<>(collectings.size());
                collectings.forEach( collecting -> {
                    StorageCollectingDTO collectingDTO=new StorageCollectingDTO();
                    BeanUtils.copyProperties(collecting,collectingDTO);
                    collectingDTOS.add(collectingDTO);
                });
                result.setCollectings(collectingDTOS);
            }
        }
        return result;
    }

    /**
     * 获取中转服务方式
     *
     * @param type
     * @param warehouseId
     * @return
     */
    @Override
    public List<SmCodeDTO> getsSmCode(Integer type, Integer warehouseId) {
        String key=STORAGE_RECORD_SM_CODE+warehouseId;
        HashOperations operations = this.redisTemplate.opsForHash();
        String mapKey=null;
        switch (type){
            case 0:
                mapKey="AIR";
                break;
            case 1:
                mapKey="LCL";
                break;
            case 2:
                mapKey="EXPRESS";
                break;
            case 3:
                mapKey="TRAIN";
                break;
            case 4:
                mapKey="FCL";
                break;
                default:
                    return null;
        }
        if (!this.redisTemplate.hasKey(key)){
            WarehouseDTO dto=this.basicsService.getByWarehouseId(warehouseId);
            if (dto==null){
                return null;
            }
            Map<String,String> map=new HashMap<>(5);
            try {
                String response=this.granaryUtils.getInstance(dto.getAppToken(),dto.getAppKey(),this.wsdlUrl,new JSONObject().toJSONString(),"getSmcode").getCallService();
                JSONObject cmJson=JSONObject.parseObject(response);
                if (!"Success".equals(cmJson.getString("message"))){
                    throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),cmJson.getString("message"));
                }
                JSONObject aaa=cmJson.getJSONObject("data");
                for (Map.Entry<String,Object> entry:aaa.entrySet()) {
                    map.put(entry.getKey(),JSONObject.toJSONString(entry.getValue()));
                }
            } catch (Exception e) {
                throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),"调用谷仓服务异常");
            }
            operations.putAll(key,map);
            this.redisTemplate.expire(key,3L, TimeUnit.DAYS);
        }
        String red=(String) operations.get(key,mapKey);
        return JSONArray.parseArray(red,SmCodeDTO.class);
    }

    /**
     * 获取中转仓库
     *
     * @param warehouseId
     * @return
     */
    @Override
    public List<SmCodeDTO> getTransferWarehouse(Integer warehouseId) {
        WarehouseDTO dto=this.basicsService.getByWarehouseId(warehouseId);
        if (dto==null){
            return null;
        }
        String key=STORAGE_TRANSFER_WAREHOUSE_CODE+dto.getAppKey();
        ListOperations operations=this.redisTemplate.opsForList();
        List<SmCodeDTO> result=operations.range(key,0,-1);
        if (!this.redisTemplate.hasKey(key)){
            result=new ArrayList<>();
            HashOperations hashOperations=this.redisTemplate.opsForHash();
            try {
                String response=this.granaryUtils.getInstance(dto.getAppToken(),dto.getAppKey(),this.wsdlUrl,new JSONObject().toJSONString(),"getTransferWarehouse").getCallService();
                JSONObject tranJson=JSONObject.parseObject(response);
                if (!"Success".equals(tranJson.getString("message"))){
                    throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),tranJson.getString("message"));
                }
                for (int i = 0; i < tranJson.getJSONArray("data").size(); i++) {
                    JSONObject jsonObject=tranJson.getJSONArray("data").getJSONObject(i);
                    result.add(new SmCodeDTO(jsonObject.getString("transit_warehouse_code"),jsonObject.getString("transit_warehouse_name")));
                }
            } catch (Exception e) {
                throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),"调用谷仓服务异常");
            }
            operations.leftPushAll(key,result);
        }
        return result;
    }


    /**
     * 获取进出口商编码
     *
     * @param warehouseId
     * @param type
     * @return
     */
    @Override
    public List<KeyValueDTO> getCompany(Integer warehouseId, Integer type) {
        List<KeyValueDTO> result=new ArrayList<>();
        WarehouseDTO dto=this.basicsService.getByWarehouseId(warehouseId);
        if (dto==null){
            return result;
        }
        String key=STORAGE_TRANSFER_VAT_LIST+dto.getAppKey();
        HashOperations operations=this.redisTemplate.opsForHash();
        if (!this.redisTemplate.hasKey(key)){
            Map<String,List<String>> map=new HashMap<>();
            try {
                String response=this.granaryUtils.getInstance(dto.getAppToken(),dto.getAppKey(),this.wsdlUrl,new JSONObject().toJSONString(),"getVatList").getCallService();
                JSONObject tranJson=JSONObject.parseObject(response);
                if (!"Success".equals(tranJson.getString("ask"))){
                    throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),tranJson.getString("message"));
                }
                tranJson.getJSONArray("data");
                for (int i = 0; i < tranJson.getJSONArray("data").size(); i++) {
                    JSONObject dataJson=tranJson.getJSONArray("data").getJSONObject(i);
                    if (StringUtils.isEmpty(dataJson.getString("warehouse_code"))){
                        dataJson.put("warehouse_code","all");
                    }
                    if (!map.containsKey(dataJson.getString("warehouse_code"))){
                        map.put(dataJson.getString("warehouse_code"),new ArrayList<>());
                    }
                    map.get(dataJson.getString("warehouse_code")).add(dataJson.toJSONString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (Map.Entry<String,List<String>> entry:map.entrySet()) {
                operations.put(key,entry.getKey(),JSONArray.toJSONString(entry.getValue()));
            }
            this.redisTemplate.expire(key,7L,TimeUnit.DAYS);
        }

        String val=(String) operations.get(key,dto.getWarehouseCode());
        if (StringUtils.isEmpty(val)){
            return result;
        }
        JSONArray jsonArray=JSONArray.parseArray(val);
        String all=(String) operations.get(key,"all");
        if (StringUtils.isNotEmpty(all)){
            jsonArray.addAll(JSONArray.parseArray(all));
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject aa=JSONObject.parseObject(jsonArray.getString(i));
            if (type.equals(aa.getInteger("vat_type"))){
                result.add(new KeyValueDTO(aa.getString("cv_id"),aa.getString("company_name")));
            }
        }
        return result;
    }

    /**
     * 根据仓库id获取增值税号
     *
     * @param warehouseId
     * @return
     */
    @Override
    public List<VatListDTO> getsVat(Integer warehouseId) {
        List<VatListDTO> result=new ArrayList<>();
        WarehouseDTO dto=this.basicsService.getByWarehouseId(warehouseId);
        if (dto==null){
            return result;
        }
        String key=STORAGE_TRANSFER_VAT_LIST+dto.getAppKey();
        HashOperations operations=this.redisTemplate.opsForHash();
        if (!this.redisTemplate.hasKey(key)){
            Map<String,List<String>> map=new HashMap<>();
            try {
                String response=this.granaryUtils.getInstance(dto.getAppToken(),dto.getAppKey(),this.wsdlUrl,new JSONObject().toJSONString(),"getVatList").getCallService();
                JSONObject tranJson=JSONObject.parseObject(response);
                if (!"Success".equals(tranJson.getString("ask"))){
                    throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),tranJson.getString("message"));
                }
                tranJson.getJSONArray("data");
                for (int i = 0; i < tranJson.getJSONArray("data").size(); i++) {
                    JSONObject dataJson=tranJson.getJSONArray("data").getJSONObject(i);
                    if (StringUtils.isEmpty(dataJson.getString("warehouse_code"))){
                        continue;
                    }
                    if (!map.containsKey(dataJson.getString("warehouse_code"))){
                        map.put(dataJson.getString("warehouse_code"),new ArrayList<>());
                    }
                    map.get(dataJson.getString("warehouse_code")).add(dataJson.toJSONString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (Map.Entry<String,List<String>> entry:map.entrySet()) {
                operations.put(key,entry.getKey(),JSONArray.toJSONString(entry.getValue()));
            }
            this.redisTemplate.expire(key,7L,TimeUnit.DAYS);
        }

        String val=(String) operations.get(key,dto.getWarehouseCode());
        if (StringUtils.isEmpty(val)){
            return result;
        }
        JSONArray jsonArray=JSONArray.parseArray(val);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject aa=JSONObject.parseObject(jsonArray.getString(i));
            VatListDTO vatListDTO=new VatListDTO();
            vatListDTO.setCvId(aa.getInteger("cv_id"));
            vatListDTO.setEori(aa.getString("eori"));
            vatListDTO.setExemptionNumber(aa.getString("exemption_number"));
            vatListDTO.setVatNumber(aa.getString("vat_number"));
            vatListDTO.setVatType(aa.getInteger("vat_type"));
            result.add(vatListDTO);
        }
        return result;
    }

    /**
     * @param id
     * @param desc
     * @return
     */
    @Override
    public Integer updateDesc(Long id, String desc,String updateBy) {
        StorageRecord record=new StorageRecord();
        record.setId(id);
        record.setReceivingDesc(desc);
        record.setUpdateBy(updateBy);
        record.setUpdateTime(new Date());
        record.setVerify(null);
        return this.recordMapper.updateByPrimaryKeySelective(record);
    }

    /**
     * 删除入库单id
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer del(Long id) {
        StorageRecord record=this.recordMapper.selectByPrimaryKey(id);
        if (record==null){
            throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),"修改对象不存在");
        }
        if (WarehouseFirmEnum.GOODCANG.getCode().equals(record.getFirmCode())){
            WarehouseDTO dto=this.basicsService.getByWarehouseId(record.getWarehouseId());
            JSONObject params=new JSONObject();
            params.put("receiving_code",record.getReceivingCode());
            JSONObject jsonObject=new JSONObject();
            try {
                String response=this.granaryUtils.getInstance(dto.getAppToken(),dto.getAppKey(),this.wsdlUrl,params.toJSONString(),"delGRN").getCallService();
                jsonObject=JSONObject.parseObject(response);
            } catch (Exception e) {
                throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),"调用谷仓服务异常");
            }
            if (!"Success".equals(jsonObject.getString("ask"))){
                throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),jsonObject.getString("Error"));
            }
        }
        record.setUpdateTime(new Date());
        record.setVerify(StorageEnums.DEL.getVerify());
        return this.recordMapper.updateByPrimaryKeySelective(record);
    }


    /**
     * 审核入库单
     *
     * @param id
     * @return
     */
    @Override
    public Integer audit(Long id,String updateBy,String receivingShippingType,String trackingNumber) {
        if (StringUtils.isEmpty(receivingShippingType)||StringUtils.isEmpty(trackingNumber)){
            StorageRecord record=new StorageRecord();
            record.setId(id);
            record.setReceivingShippingType(receivingShippingType);
            record.setTrackingNumber(trackingNumber);
            this.recordMapper.updateByPrimaryKeySelective(record);
        }
        StorageRecordDTO storageRecordDTO=new StorageRecordDTO();
        StorageRecord upDO=new StorageRecord();
        StorageRecord recordDO=this.recordMapper.selectByPrimaryKey(id);
        BeanUtils.copyProperties(recordDO,storageRecordDTO);
        storageRecordDTO.setVerify(StorageEnums.NO_AUDIT.getVerify());
        if (StringUtils.isEmpty(storageRecordDTO.getTrackingNumber())&&StringUtils.isNotEmpty(trackingNumber)){
            storageRecordDTO.setTrackingNumber(trackingNumber);
            upDO.setTrackingNumber(trackingNumber);
        }
        if (WarehouseFirmEnum.GOODCANG.getCode().equals(storageRecordDTO.getFirmCode())&&TransitEnum.TRANSFER.getType().equals(storageRecordDTO.getTransitType())){
            StorageRecordSpecific specificDO=this.specificMapper.selectByPrimaryKey(id);
            StorageSpecificDTO specificDTO=new StorageSpecificDTO();
            BeanUtils.copyProperties(specificDO,specificDTO);
            storageRecordDTO.setSpecificDTO(specificDTO);
            if (specificDO.getCollectingService().equals(1)){
                List<StorageCollecting> list=this.collectingMapper.getsByStorageId(id);
                List<StorageCollectingDTO> collectingList=new ArrayList<>(list.size());
                list.forEach( address -> {
                    StorageCollectingDTO collectingDTO=new StorageCollectingDTO();
                    BeanUtils.copyProperties(address,collectingDTO);
                    collectingList.add(collectingDTO);
                });
                storageRecordDTO.setCollectings(collectingList);
            }
        }
        List<StorageRecordItem> items=this.itemMapper.getsByStorageId(id);
        List<StoregeItemDTO> itemDTOS=new ArrayList<>(items.size());
        items.forEach(item->{
            StoregeItemDTO itemDTO=new StoregeItemDTO();
            BeanUtils.copyProperties(item,itemDTO);
            itemDTOS.add(itemDTO);
        });

        storageRecordDTO.setItems(itemDTOS);
        if (WarehouseFirmEnum.GOODCANG.getCode().equals(storageRecordDTO.getFirmCode())){
            this.createGRN(storageRecordDTO,"modifyGRN");
            upDO.setVerify(StorageEnums.NO_AUDIT.getVerify());
        }else if (WarehouseFirmEnum.WMS.getCode().equals(storageRecordDTO.getFirmCode())){
            this.createWmsGTR(storageRecordDTO);
            upDO.setVerify(StorageEnums.WAIT.getVerify());
        }
        upDO.setUpdateTime(new Date());
        upDO.setUpdateBy(updateBy);
        upDO.setId(id);
        return this.recordMapper.updateByPrimaryKeySelective(upDO);
    }


    /**
     * 打印箱唛
     *
     * @param id
     * @param printSize
     * @param printType
     * @param boxArr
     * @return
     */
    @Override
    public BoxDTO printBox(Long id, Integer printSize, Integer printType, List<String> boxArr) {
        BoxDTO result=new BoxDTO();
        StorageRecord record=this.recordMapper.selectByPrimaryKey(id);
        if (record==null){
            throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),"修改对象不存在");
        }
        WarehouseDTO dto=this.basicsService.getByWarehouseId(record.getWarehouseId());
        JSONObject jsonObject=new JSONObject();
        JSONObject params=new JSONObject();
        params.put("receiving_code",record.getReceivingCode());
        params.put("print_size",printSize);
        params.put("print_type",printType);
        params.put("receiving_box_no_arr",boxArr);
        try {
            logger.info("打迎箱唛请求参数:params={}",params.toJSONString());
            String response=this.granaryUtils.getInstance(dto.getAppToken(),dto.getAppKey(),this.wsdlUrl,params.toJSONString(),"printGcReceivingBox").getCallService();
            jsonObject=JSONObject.parseObject(response);
        } catch (Exception e) {
            throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),"调用谷仓服务异常");
        }
        if (!"Success".equals(jsonObject.getString("ask"))){
            throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),jsonObject.getJSONObject("Error").getString("errMessage"));
        }
        result.setFileType(jsonObject.getJSONObject("data").getInteger("image_type"));
        try {
            byte[] b = new BASE64Decoder().decodeBuffer(jsonObject.getJSONObject("data").getString("label_image").replaceAll(" ", ""));
            result.setData(b);
        } catch (IOException e) {
            throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),"转文件异常");
        }
        return result;
    }

    /**
     * 打印sku
     *
     * @param id
     * @param printSize
     * @param printCode
     * @param skuArr
     * @return
     */
    @Override
    public BoxDTO printSku(Long id, Integer printSize, Integer printCode, List<String> skuArr) {
        StorageRecord record=this.recordMapper.selectByPrimaryKey(id);
        if (record==null){
            throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),"修改对象不存在");
        }
        List<StorageRecordItem> list=this.itemMapper.getsByStorageId(id);
        if (CollectionUtils.isEmpty(skuArr)){
            skuArr=new ArrayList<>(8);
        }
        for (StorageRecordItem item:list) {
            skuArr.add(item.getProductSku());
        }
        WarehouseDTO dto=this.basicsService.getByWarehouseId(record.getWarehouseId());
        JSONObject params=new JSONObject();
        params.put("print_size",printSize);
        params.put("print_code",printCode);
        params.put("product_sku_arr",skuArr);
        JSONObject jsonObject=new JSONObject();
        try {
            String response=this.granaryUtils.getInstance(dto.getAppToken(),dto.getAppKey(),this.wsdlUrl,params.toJSONString(),"printSku").getCallService();
            jsonObject=JSONObject.parseObject(response);
        } catch (Exception e) {
            throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),"调用谷仓服务异常");
        }
        if (!"Success".equals(jsonObject.getString("ask"))){
            throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),jsonObject.getJSONObject("Error").getString("errMessage"));
        }
        BoxDTO result=new BoxDTO();
        result.setFileType(jsonObject.getJSONObject("data").getInteger("image_type"));
        try {
            byte[] b = new BASE64Decoder().decodeBuffer(jsonObject.getJSONObject("data").getString("label_image").replaceAll(" ", ""));
            result.setData(b);
        } catch (IOException e) {
            throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),"转文件异常");
        }
        return result;
    }

    /**
     * 同步审核中和待收货的状态
     */
    @Override
    public void syncStatus() {
        List<Integer> supplierIds=new ArrayList<>(1);
        supplierIds.add(0);

        List<InitWarehouseDTO> dtos=this.basicsService.getAuth(1);
        dtos.forEach(dto->{
            List<Integer> ids=new ArrayList<>();
            dto.getItem().forEach(child->{
                ids.add(child.getId());
            });
            Integer start=1;
            Integer pageSize=100;
            Boolean hasNext=false;
            do {
                PageHelper.startPage(start,pageSize);
                List<StorageRecord> receivingCodes=this.recordMapper.getSyncStatus(ids);
                PageInfo<StorageRecord> pageInfo=new PageInfo<>(receivingCodes);
                if (start*pageSize<pageInfo.getTotal()){
                    start++;
                    hasNext=true;
                }else {
                    hasNext=false;
                }
                if (CollectionUtils.isNotEmpty(pageInfo.getList())){
                    pageInfo.getList().forEach(receivingCode -> {
                        if (WarehouseFirmEnum.GOODCANG.getCode().equals(receivingCode.getFirmCode())){
                            this.syncUpdateStatus(receivingCode.getReceivingCode(),dto.getAppKey(),dto.getAppToken());
                        }else if (WarehouseFirmEnum.WMS.getCode().equals(receivingCode.getFirmCode())){
                            this.updateWmsStatus(receivingCode.getReceivingCode(),dto.getAppKey(),dto.getAppToken());
                        }
                    });
                }
            }while (hasNext);
        });
    }


    /**
     * 谷仓入库单新增修改
     * @param dto
     * @param serviceName
     * @return
     */
    private String createGRN(StorageRecordDTO dto, String serviceName){
        WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(dto.getWarehouseId());
        JSONObject params=new JSONObject();
        params.put("reference_no",dto.getId().toString());
        params.put("transit_type",dto.getTransitType());
        params.put("receiving_shipping_type",dto.getReceivingShippingType());
        params.put("tracking_number",dto.getTrackingNumber());
        params.put("warehouse_code",warehouseDTO.getWarehouseCode());
        params.put("eta_date", dto.getEtaDate());
        params.put("receiving_desc", dto.getReceivingDesc());
        params.put("verify", dto.getVerify());
        params.put("receiving_code", dto.getReceivingCode());

        /**
         * 入库单明细
         */
        JSONArray itmes=new JSONArray(dto.getItems().size());
        dto.getItems().forEach(item->{
            JSONObject itJson=new JSONObject();
            itJson.put("box_no",item.getBoxNo());
            itJson.put("reference_box_no",item.getReferenceBoxNo());
            JSONArray boxAr=new JSONArray(1);
            JSONObject boxDetails=new JSONObject();
            boxDetails.put("product_sku",item.getProductSku());
            boxDetails.put("quantity",item.getQuantity());
            boxDetails.put("fba_product_code",item.getFbaProductCode());
            boxAr.add(boxDetails);
            itJson.put("box_details",boxAr);
            itmes.add(itJson);
        });
        params.put("items",itmes);

        /**
         * 入库单发货地址（仅适用于自发入库单）
         */
        JSONObject shiperAddress=new JSONObject();
        shiperAddress.put("sa_contacter",dto.getSaContacter());
        shiperAddress.put("sa_contact_phone",dto.getSaContactPhone());
        shiperAddress.put("sa_country_code",dto.getSaCountryCode());
        shiperAddress.put("sa_state",dto.getSaState());
        shiperAddress.put("sa_city",dto.getSaCity());
        shiperAddress.put("sa_region",dto.getSaRegion());
        shiperAddress.put("sa_address1",dto.getSaAddress1());
        shiperAddress.put("sa_address2",dto.getSaAddress2());
        params.put("shiper_address",shiperAddress);
        if (dto.getTransitType()==3){
            params.put("sm_code",dto.getSpecificDTO().getSmCode());
            params.put("weight",dto.getSpecificDTO().getWeight());
            params.put("volume",dto.getSpecificDTO().getVolume());
            params.put("transit_warehouse_code",dto.getSpecificDTO().getTransitWarehouseCode());
            params.put("pickup_form",dto.getSpecificDTO().getPickupForm());
            params.put("customs_type",dto.getSpecificDTO().getCustomsType());
            Map<String, String> map1=this.getFileTypeAndToBase64(dto.getSpecificDTO().getClearanceFile());
            JSONObject clearanceFile=new JSONObject();
            clearanceFile.put("clearance_base64",map1.get(FILE_BASE64));
            clearanceFile.put("clearance_type",map1.get(FILE_TYPE));
            params.put("clearance_file",clearanceFile);
            Map<String, String> map2=this.getFileTypeAndToBase64(dto.getSpecificDTO().getInvoiceBase64());
            params.put("invoice_base64",map2.get(FILE_BASE64));
            params.put("collecting_service",dto.getSpecificDTO().getCollectingService());
            /**
             * transit_type=3特有。揽收资料。必填条件：当collection_service=1（上门揽收）时必填
             */
            JSONArray collectingAddress=new JSONArray();
            if (CollectionUtils.isNotEmpty(dto.getCollectings())){
                dto.getCollectings().forEach(coll->{
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("ca_first_name",coll.getCaFirstName());
                    jsonObject.put("ca_last_name",coll.getCaLastName());
                    jsonObject.put("ca_contact_phone",coll.getCaContactPhone());
                    jsonObject.put("ca_state",coll.getCaState());
                    jsonObject.put("ca_city",coll.getCaCity());
                    jsonObject.put("ca_country_code",coll.getCaCountryCode());
                    jsonObject.put("ca_zipcode",coll.getCaZipcode());
                    jsonObject.put("ca_address1",coll.getCaAddress1());
                    jsonObject.put("ca_address2",coll.getCaAddress2());
                    collectingAddress.add(jsonObject);
                });
            }
            params.put("collecting_address",collectingAddress);

            params.put("collecting_time",dto.getSpecificDTO().getCollectingTime()==null?null:DateUtils.dateToString(dto.getSpecificDTO().getCollectingTime(),DateUtils.FORMAT_2));
            params.put("value_add_service",dto.getSpecificDTO().getValueAddService());
            params.put("export_company",dto.getSpecificDTO().getExportCompany());
            params.put("clearance_service",dto.getSpecificDTO().getClearanceService());
        }
        params.put("import_company",dto.getImportCompany());
        JSONObject resJson=null;
        try {
            logger.info("请求谷仓的参数:params={}",params.toJSONString());
            String response=this.granaryUtils.getInstance(warehouseDTO.getAppToken(),warehouseDTO.getAppKey(),this.wsdlUrl,params.toJSONString(),serviceName).getCallService();
            resJson=JSONObject.parseObject(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!"Success".equals(resJson.getString("ask"))){
            throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100400.getCode(),resJson.getJSONObject("Error").getString("errMessage"));
        }
        return resJson.getJSONObject("data").getString("receiving_code");

    }


    /**
     * 获取网络文件类型，并且编码为base64
     *
     * @param fileUrl
     * @return
     * @throws Exception
     */
    private Map<String, String> getFileTypeAndToBase64(String fileUrl) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isEmpty(fileUrl)){
            return map;
        }
        HttpURLConnection conn;
        if (StringUtils.isBlank(fileUrl)) {
            throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100407, "获取网络文件类型地址(url)为空!");
        }
        try {
            URL url = new URL(fileUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.connect();

            String filePathUrl = conn.getURL().getFile();
            filePathUrl = filePathUrl.replaceAll("[/;]", "\\\\");
            String fileFullName = filePathUrl.substring(filePathUrl.lastIndexOf(File.separatorChar) + 1);
            String fileType = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
            map.put(FILE_TYPE, fileType);
            if (StringUtils.equalsAny(fileType, "rar", "zip")) {
                Thread.sleep(1000);
            }
            InputStream inStream = conn.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[conn.getContentLength()];
            int len;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            inStream.close();
            map.put(FILE_BASE64, Base64Utils.encodeToString(outStream.toByteArray()));
            return map;
        } catch (Exception e) {
            throw new GlobalException(com.rondaful.cloud.supplier.enums.ResponseCodeEnum.RETURN_CODE_100407, "获取网络文件类型，并且编码为base64失败!");
        }
    }

    /**
     * 开始修改谷仓库入单状态
     * @param receivingCode
     * @param appkey
     * @param appToken
     */
    @Async
    @Transactional
    public void syncUpdateStatus(String receivingCode,String appkey,String appToken){
        JSONObject params=new JSONObject();
        params.put("receiving_code",receivingCode);
        JSONObject jsonObject=null;
        try {
            String resp=this.granaryUtils.getInstance(appToken,appkey,this.wsdlUrl,params.toJSONString(),"getGRNDetail").getCallService();
            jsonObject=JSONObject.parseObject(resp);
            if (jsonObject==null||!"Success".equals(jsonObject.getString("ask"))){
                return;
            }
        } catch (Exception e) {
            return;
        }
        JSONObject data=jsonObject.getJSONObject("data");
        StorageRecord record=new StorageRecord();
        switch (data.getInteger("receiving_status")){
            case 0:
            case 1:
                return;
            case 2:
                record.setVerify(StorageEnums.ERROR.getVerify());
                break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                record.setVerify(StorageEnums.WAIT.getVerify());
                break;
            case 10:
                record.setVerify(StorageEnums.PUT_AWAY.getVerify());
                break;
            case 100:
                record.setVerify(StorageEnums.DEL.getVerify());
                break;
            default:
                logger.info("未知谷仓状态");
                return;
        }
        record.setId(data.getLong("reference_no"));
        record.setUpdateTime(new Date());

        JSONArray transferDetail=data.getJSONArray("transfer_detail");
        JSONArray overseasDetail=data.getJSONArray("overseas_detail");
        //List<StorageRecordItem> items=new ArrayList<>(transferDetail.size());

        for (int i = 0; i < overseasDetail.size(); i++) {
            JSONObject overseas=overseasDetail.getJSONObject(i);
            StorageRecordItem item=new StorageRecordItem();
            item.setOverseasPreCount(overseas.getInteger("overseas_pre_count"));
            item.setOverseasReceivingCount(overseas.getInteger("overseas_receiving_count"));
            item.setOverseasShelvesCount(overseas.getInteger("overseas_shelves_count"));
            item.setStorageId(data.getLong("reference_no"));
            item.setProductSku(overseas.getString("product_sku"));
            item.setBoxNo(overseas.getInteger("box_no"));
            this.itemMapper.updateNum(item);
        }
        for (int i = 0; i < transferDetail.size(); i++) {
            JSONObject transfer=transferDetail.getJSONObject(i);
            StorageRecordItem item=new StorageRecordItem();
            item.setProductSku(transfer.getString("product_sku"));
            item.setTransitPreCount(transfer.getInteger("transit_pre_count"));
            item.setTransitReceivingCount(transfer.getInteger("transit_receiving_count"));
            item.setStorageId(data.getLong("reference_no"));
            item.setBoxNo(transfer.getInteger("box_no"));
            this.itemMapper.updateNum(item);
        }
        this.recordMapper.updateByPrimaryKeySelective(record);
    }

    /**
     * WMS 入库单
     * @param dto
     */
    private void createWmsGTR(StorageRecordDTO dto){
        WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(dto.getWarehouseId());

        Map<String,Object> params=new HashMap<>();
        params.put("sourceId",dto.getReceivingCode());
        params.put("sourceType","1");
        params.put("qualityType",dto.getQualityType().toString());
        params.put("plannedTime",dto.getEtaDate());
        params.put("warehouse",warehouseDTO.getWarehouseCode());
        params.put("comment",dto.getReceivingDesc());
        JSONArray jsonArray=new JSONArray(dto.getItems().size());
        dto.getItems().forEach( item ->{
            JSONObject it=new JSONObject();
            it.put("sourceId",dto.getReceivingCode());
            it.put("waybillId",dto.getTrackingNumber());
            it.put("sku",item.getProductSku());
            it.put("plannedQuantity",item.getQuantity().toString());
            it.put("logistics",dto.getReceivingShippingType());
            jsonArray.add(it);
        });
        params.put("receiveGoodDetails",jsonArray);
        String resp=null;
        try {
            URIBuilder uri=new URIBuilder(this.wmsUrl+"/inbound/receiveArrivalNotice/insertNoticeInfo");
            uri.addParameter("customerAppId",warehouseDTO.getAppKey());
            uri.addParameter("sign",warehouseDTO.getAppToken());
            resp=HttpUtil.wmsPost(uri.toString(),params);
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100407, "调用wms服务异常");
        }
        JSONObject respJson=JSONObject.parseObject(resp);
        if (!respJson.getBoolean("success")){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100407, respJson.getString("msg"));
        }
    }

    /**
     * wms 入库单同步
     * @param receivingCode
     * @param appkey
     * @param appToken
     */
    public void updateWmsStatus(String receivingCode,String appkey,String appToken){
        JSONObject respJson=null;
        try {
            URIBuilder uri=new URIBuilder(this.wmsUrl+"/inbound/receiveArrivalNotice/selectOrderInfo");
            uri.addParameter("customerAppId",appkey);
            uri.addParameter("sign",appToken);
            uri.addParameter("sourceId",receivingCode);
            String resp=HttpUtil.wmsGet(uri.toString());
            respJson=JSONObject.parseObject(resp);
        } catch (URISyntaxException e) {
            logger.error("拼接请求参数异常:receivingCode={},appkey={}",receivingCode,appkey);
            return;
        }
        if (!respJson.getBoolean("success")){
            logger.error("wms订单状态系统异常:receivingCode={}，msg={}",receivingCode,respJson.getString("msg"));
            return;
        }
        StorageRecord record=new StorageRecord();
        switch (respJson.getJSONObject("data").getJSONObject("receiveArrivalNotice").getInteger("status")){
            case 1:
            case 2:
            case 3:
                record.setVerify(StorageEnums.WAIT.getVerify());
                break;
            case 4:
                record.setVerify(StorageEnums.PUT_AWAY.getVerify());
                break;
            default:
                return;
        }
        record.setUpdateTime(new Date());
        record.setId(Long.valueOf(receivingCode));
        this.recordMapper.updateByPrimaryKeySelective(record);
    }
}
