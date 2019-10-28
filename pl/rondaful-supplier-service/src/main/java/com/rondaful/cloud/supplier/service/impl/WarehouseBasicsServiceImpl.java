package com.rondaful.cloud.supplier.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.granary.GranaryUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.entity.basics.WarehouseFirm;
import com.rondaful.cloud.supplier.entity.basics.WarehouseList;
import com.rondaful.cloud.supplier.entity.basics.WarehouseListAddress;
import com.rondaful.cloud.supplier.entity.basics.WarehouseService;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.mapper.WarehouseFirmMapper;
import com.rondaful.cloud.supplier.mapper.WarehouseListAddressMapper;
import com.rondaful.cloud.supplier.mapper.WarehouseListMapper;
import com.rondaful.cloud.supplier.mapper.WarehouseServiceMapper;
import com.rondaful.cloud.supplier.model.dto.FeignResult;
import com.rondaful.cloud.supplier.model.dto.KeyValueDTO;
import com.rondaful.cloud.supplier.model.dto.basics.*;
import com.rondaful.cloud.supplier.model.dto.reomte.user.FeignUserDTO;
import com.rondaful.cloud.supplier.model.dto.reomte.user.LogisticsInfo;
import com.rondaful.cloud.supplier.model.enums.StatusEnums;
import com.rondaful.cloud.supplier.model.enums.WarehouseFirmEnum;
import com.rondaful.cloud.supplier.model.request.basic.WarehouseSelectDTO;
import com.rondaful.cloud.supplier.model.request.basic.WarehouseServiceDTO;
import com.rondaful.cloud.supplier.remote.RemoteUserService;
import com.rondaful.cloud.supplier.service.IErpServiceService;
import com.rondaful.cloud.supplier.service.IGoodServiceService;
import com.rondaful.cloud.supplier.service.IWarehouseBasicsService;
import com.rondaful.cloud.supplier.service.IWmsServiceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xqq
 * @Date: 2019/6/11
 * @Description:
 */
@Service("warehouseBasicsServiceImpl")
public class WarehouseBasicsServiceImpl implements IWarehouseBasicsService {
    private final Logger logger = LoggerFactory.getLogger(WarehouseBasicsServiceImpl.class);

    @Autowired
    private WarehouseFirmMapper firmMapper;
    @Autowired
    private WarehouseListMapper listMapper;
    @Autowired
    private GranaryUtils granaryUtils;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private WarehouseServiceMapper serviceMapper;
    @Autowired
    private WarehouseListAddressMapper addressMapper;
    @Autowired
    private RemoteUserService userService;
    @Autowired
    private IGoodServiceService goodService;
    @Autowired
    private IWmsServiceService wmsService;
    @Autowired
    private IErpServiceService erpService;
    @Value("${wsdl.url}")
    private String wsdlUrl;
    @Value("${erp.url}")
    private  String erpUrl;
    @Value("${brandslink.wms.url}")
    private String wmsUrl;

    /**
     * 新建仓库服务商
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(WarehouseFirmDTO dto) {
        WarehouseFirm firm=new WarehouseFirm();
        BeanUtils.copyProperties(dto,firm);
        firm.setStatus(1);
        firm.setUpdateBy(dto.getCreateBy());
        firm.setCreateDate(new Date());
        firm.setUpdateDate(firm.getCreateDate());
        FeignResult<LogisticsInfo> feignResult=this.userService.getLogSupplyId(dto.getLogisticsUserId());
        if (!feignResult.getSuccess()||feignResult.getData()==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"未找到对应仓储账号信息");
        }
        firm.setFirmCode(feignResult.getData().getShortName());
        if (this.firmMapper.insert(firm)<1){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"插入服务商异常");
        }


        List<WarehouseList> list=new ArrayList<>(10);
        if (WarehouseFirmEnum.GOODCANG.getCode().equals(dto.getFirmCode())){
            list.addAll(this.goodService.getsWarehouseList(dto.getAppKey(),dto.getAppToken(),firm.getId()));
        }else if(WarehouseFirmEnum.RONDAFUL.getCode().equals(dto.getFirmCode())){
            list.addAll(this.erpService.getsWarehouseList(firm.getId()));
        }else if (WarehouseFirmEnum.WMS.getCode().equals(dto.getFirmCode())){
            list.addAll(this.wmsService.getsWarehouseList(dto.getAppKey(),dto.getAppToken(),firm.getId()));
        }else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"未知服务商");
        }
        if (CollectionUtils.isEmpty(list)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"没有对应的仓库列表");
        }
        this.listMapper.insertBatch(list);
        return firm.getId();
    }

    @Override
    public Integer update() {
        List<WarehouseFirm> firms=this.firmMapper.getsByStatus(StatusEnums.ACTIVATE.getStatus());
        List<WarehouseList> insertList=new ArrayList<>();
        for (WarehouseFirm firm:firms) {
            List<WarehouseList> list=new ArrayList<>();
            if (WarehouseFirmEnum.GOODCANG.getCode().equals(firm.getFirmCode())){
                list.addAll(this.goodService.getsWarehouseList(firm.getAppKey(),firm.getAppToken(),firm.getId()));
            }else if(WarehouseFirmEnum.RONDAFUL.getCode().equals(firm.getFirmCode())){
                list.addAll(this.erpService.getsWarehouseList(firm.getId()));
            }else if (WarehouseFirmEnum.WMS.getCode().equals(firm.getFirmCode())){
                list.addAll(this.wmsService.getsWarehouseList(firm.getAppKey(),firm.getAppToken(),firm.getId()));
            }else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"未知服务商");
            }
            list.forEach( listDO -> {
                if (this.listMapper.getByCode(listDO.getWarehouseCode(),firm.getId())==null){
                    insertList.add(listDO);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(insertList)){
            this.listMapper.insertBatch(insertList);
        }
        return 1;
    }

    /**
     * 根据服务商id删除仓库
     *
     * @param firmId
     * @return
     */
    @Override
    public Integer del(Integer firmId) {
        this.firmMapper.deleteByPrimaryKey(firmId.longValue());
        this.listMapper.deleteByFirmId(firmId);
        return 1;
    }

    /**
     * 根据供应商id获取列表
     *
     * @param supplierIds
     * @return
     */
    @Override
    public List<WarehouseLogisTreeDTO> getTree(List<Integer> supplierIds,String languageType) {
        List<WarehouseFirm> firmList=this.firmMapper.getBySupplierId(supplierIds,StatusEnums.ACTIVATE.getStatus());
        List<WarehouseSelectDTO> common=new ArrayList<>(2);
        List<WarehouseSelectDTO> prive=new ArrayList<>(1);
        firmList.forEach(firm -> {
            WarehouseSelectDTO dto=new WarehouseSelectDTO(firm.getId(),Utils.translation(WarehouseFirmEnum.getByCode(firm.getFirmCode()))+"("+firm.getName()+")");
            List<WarehouseSelectDTO> childs=new ArrayList<>(20);
            List<WarehouseList> lists=this.listMapper.getsByFirmId(firm.getId(),StatusEnums.ACTIVATE.getStatus());
            lists.forEach(warehouseList -> {
                childs.add(new WarehouseSelectDTO(warehouseList.getId(),Utils.translation(warehouseList.getWarehouseName())));
            });
            dto.setChilds(childs);
            if ("WMS".equals(firm.getFirmCode())||"GOODCANG".equals(firm.getFirmCode())){
                common.add(dto);
            }else if ("RONDAFUL".equals(firm.getFirmCode())){
                prive.add(dto);
            }

        });
        List<WarehouseLogisTreeDTO> result=new ArrayList<>(2);
        result.add(new WarehouseLogisTreeDTO("common",common));
        result.add(new WarehouseLogisTreeDTO("prive",prive));
        return result;

    }

    /**
     * 改变仓库状态
     *
     * @param warehouseIds
     * @param status
     * @return
     */
    @Override
    public Integer updateStatus(List<Integer> warehouseIds,Integer status) {
        Integer result=this.listMapper.updateStatus(warehouseIds,status);
        if (result>0){
            warehouseIds.forEach(warehouseId -> {
                this.clearCache(warehouseId);
            });
        }
        return result;
    }

    /**
     * 获取有效的仓库
     *
     * @return
     */
    @Override
    public List<WarehouseInitDTO> getsInit() {
        List<WarehouseInitDTO> result=new ArrayList<>(5);
        List<WarehouseFirm> list=this.firmMapper.getsByStatus(1);
        list.forEach(firm -> {
            WarehouseInitDTO initDTO=new WarehouseInitDTO();
            BeanUtils.copyProperties(firm,initDTO);
            List<WarehouseListDTO> childs=new ArrayList<>();
            List<WarehouseList> childList=this.listMapper.getsByStatus(firm.getId(),1);
            childList.forEach( child -> {
                childs.add(new WarehouseListDTO(child.getId(),child.getWarehouseCode(),child.getWarehouseName()));
            });
            initDTO.setList(childs);
            result.add(initDTO);
        });
        return result;
    }

    /**
     * 根据仓库id获取仓库信息
     *
     * @param warehouseId
     * @return
     */
    @Override
    public WarehouseDTO getByWarehouseId(Integer warehouseId) {
        String key=SUPPLIER_WAREHOUSE_ID+warehouseId;
        ValueOperations operations=this.redisTemplate.opsForValue();
        WarehouseDTO result=(WarehouseDTO)operations.get(key);
        if (result==null){
            result=new WarehouseDTO();
            WarehouseList warehouseList=this.listMapper.selectByPrimaryKey(warehouseId.longValue());
            if (warehouseList==null){
                return null;
            }
            BeanUtils.copyProperties(warehouseList,result);
            result.setWarehouseId(warehouseList.getId());
            WarehouseFirm warehouseFirm=this.firmMapper.selectByPrimaryKey(warehouseList.getFirmId().longValue());
            BeanUtils.copyProperties(warehouseFirm,result,"status");
            result.setAppKey(warehouseFirm.getAppKey());
            result.setAppToken(warehouseFirm.getAppToken());
            operations.set(key,result,90L, TimeUnit.DAYS);
        }
        FeignResult<LogisticsInfo> feignResult=this.userService.getLogSupplyId(result.getLogisticsUserId());
        result.setSupplyId(feignResult.getData()==null?null:feignResult.getData().getSupplyId());
        result.setSupplyName(feignResult.getData()==null?null:feignResult.getData().getSupplyName());
        return result;
    }

    /**
     * 获取仓库服务商名称
     *
     * @return
     */
    @Override
    public List<WarehouseServiceDTO> getsServiceName(Integer type) {
        List<WarehouseServiceDTO> result=new ArrayList<>();
        List<WarehouseService>  list=this.serviceMapper.getAll(type);
        list.forEach(serviceDO->{
            WarehouseServiceDTO dto=new WarehouseServiceDTO(serviceDO.getServiceCode(),serviceDO.getServiceName());
            BeanUtils.copyProperties(serviceDO,dto);
            result.add(dto);
        });
        return result;
    }

    /**
     * @param warehouseIds
     * @param supplierIds
     * @return
     */
    @Override
    public List<WarehouseSelectDTO> getSelect(List<Integer> warehouseIds, List<Integer> supplierIds,String languageType,Integer status) {
        List<WarehouseSelectDTO> result=new ArrayList<>(2);
        if (CollectionUtils.isNotEmpty(warehouseIds)){
            List<WarehouseList> list=this.listMapper.getsByIds(warehouseIds);
            if (CollectionUtils.isEmpty(list)){
                return result;
            }
            Map<Integer,List<WarehouseSelectDTO>> map=new HashMap<>(2);
            list.forEach(dto->{
                WarehouseSelectDTO selectDTO=new WarehouseSelectDTO(dto.getId(),dto.getWarehouseName());
                if (!map.containsKey(dto.getFirmId())){
                    map.put(dto.getFirmId(),new ArrayList<>());
                }
                map.get(dto.getFirmId()).add(selectDTO);
            });
            WarehouseSelectDTO dto1=new WarehouseSelectDTO(WarehouseFirmEnum.GOODCANG.getCode().hashCode(),StringUtils.isEmpty(languageType)?WarehouseFirmEnum.GOODCANG.getName():Utils.translation(WarehouseFirmEnum.GOODCANG.getName()));
            dto1.setChilds(new ArrayList<>());
            WarehouseSelectDTO dto2=new WarehouseSelectDTO(WarehouseFirmEnum.RONDAFUL.getCode().hashCode(),StringUtils.isEmpty(languageType)?WarehouseFirmEnum.RONDAFUL.getName():Utils.translation(WarehouseFirmEnum.RONDAFUL.getName()));
            dto2.setChilds(new ArrayList<>());
            WarehouseSelectDTO dto3=new WarehouseSelectDTO(WarehouseFirmEnum.WMS.getCode().hashCode(),StringUtils.isEmpty(languageType)?WarehouseFirmEnum.WMS.getName():Utils.translation(WarehouseFirmEnum.WMS.getName()));
            dto3.setChilds(new ArrayList<>());
            map.forEach((k,v)->{
                WarehouseInitDTO initDTO=this.getByFirmId(k);
                List<WarehouseSelectDTO> list1=new ArrayList<>(v.size());
                v.forEach(d->{
                    WarehouseSelectDTO dto=new WarehouseSelectDTO(d.getId(),(StringUtils.isEmpty(languageType)?d.getName():Utils.translation(d.getName())));
                    list1.add(dto);
                });
                if (WarehouseFirmEnum.GOODCANG.getCode().equals(initDTO.getFirmCode())){
                    dto1.getChilds().addAll(list1);
                }else if (WarehouseFirmEnum.RONDAFUL.getCode().equals(initDTO.getFirmCode())){
                    dto2.getChilds().addAll(list1);
                }else if (WarehouseFirmEnum.WMS.getCode().equals(initDTO.getFirmCode())){
                    dto3.getChilds().addAll(list1);
                }
            });
            if (CollectionUtils.isNotEmpty(dto1.getChilds())&&(status==null||status==1)){
                result.add(dto1);
            }
            if (CollectionUtils.isNotEmpty(dto2.getChilds())&&(status==null||status==2)){
                result.add(dto2);
            }
            if (CollectionUtils.isNotEmpty(dto3.getChilds())&&(status==null||status==1)){
                result.add(dto3);
            }
            return result;
        }else {
            List<WarehouseFirmTreeDTO> list=this.getsTreeBySupplierId(supplierIds,null);
            for (WarehouseFirmTreeDTO dto:list) {
                if (status==null||(dto.getSupplierId()==0&&status==1)||((dto.getSupplierId()!=0&&status==2))){
                    List<WarehouseSelectDTO> dataList=new ArrayList<>();
                    dto.getChilds().forEach( child->{
                        child.getChilds().forEach( twoChild->{
                            dataList.add(new WarehouseSelectDTO(twoChild.getId(),(StringUtils.isEmpty(languageType)?twoChild.getName():Utils.translation(twoChild.getName()))));
                        });
                    });
                    WarehouseSelectDTO selectDTO=new WarehouseSelectDTO(dto.getId(),(StringUtils.isEmpty(languageType)?dto.getName():Utils.translation(dto.getName())));

                    selectDTO.setChilds(dataList);
                    result.add(selectDTO);
                }
            }
        }
        return result;
    }

    /**
     * 根据仓库服务商编码获取仓库列表
     *
     * @param serviceCode
     * @return
     */
    @Override
    public List<WarehouseSelectDTO> getSelectByServiceCode(String serviceCode,Integer userId) {
        List<WarehouseSelectDTO> result=new ArrayList<>();
        List<WarehouseFirm> list=this.firmMapper.getsByFirmCode(serviceCode,userId);
        list.forEach(firm->{
            List<WarehouseList> warehouseLists=this.listMapper.getsByFirmId(firm.getId(),null);
            warehouseLists.forEach(listDO->{
                WarehouseSelectDTO dto=new WarehouseSelectDTO();
                dto.setId(listDO.getId());
                dto.setName(listDO.getWarehouseName());
                result.add(dto);
            });

        });
        return result;
    }

    /**
     * 根据老版仓库code换取新版id
     *
     * @param warehouseCode
     * @return
     */
    @Override
    public Integer codeToId(String warehouseCode) {
        WarehouseList warehouseListDO=this.listMapper.getByCode(warehouseCode,null);
        if (warehouseListDO==null){
            return null;
        }
        return warehouseListDO.getId();
    }

    /**
     * 根据新版id换取老版仓库code
     *
     * @param warehouseId
     * @return
     */
    @Override
    public String idToCode(Integer warehouseId) {
        WarehouseDTO warehouseDTO=this.getByWarehouseId(warehouseId);
        if (warehouseDTO==null){
            return null;
        }
        return warehouseDTO.getWarehouseCode();
    }

    /**
     * 根据账号列表id获取仓库
     *
     * @param firmId
     * @return
     */
    @Override
    public WarehouseInitDTO getByFirmId(Integer firmId) {
        WarehouseInitDTO result=new WarehouseInitDTO();
        WarehouseFirm firmDO=this.firmMapper.selectByPrimaryKey(firmId.longValue());
        if (firmId==null){
            return result;
        }
        BeanUtils.copyProperties(firmDO,result);
        List<WarehouseList> list=this.listMapper.getsByFirmId(firmId,null);
        List<WarehouseListDTO> childs=new ArrayList<>(list.size());
        list.forEach(listDO->{
            WarehouseListDTO child=new WarehouseListDTO();
            BeanUtils.copyProperties(listDO,child);
            childs.add(child);
        });
        result.setList(childs);
        return result;
    }

    /**
     * 根据类型获取仓库类型的授权信息及列表
     *
     * @param type
     * @return
     */
    @Override
    public List<InitWarehouseDTO> getAuth(Integer type) {
        List<InitWarehouseDTO> result=new ArrayList<>(2);
        List<WarehouseFirm>  list=new ArrayList<>();
        if (type==null){
            list =this.firmMapper.getsByStatus(StatusEnums.ACTIVATE.getStatus());
        }else if (type==1){
            list=this.firmMapper.getBySupplierId(Arrays.asList(0),StatusEnums.ACTIVATE.getStatus());
        }else if (type==2){
            list=this.firmMapper.getBySupplierId(null,StatusEnums.ACTIVATE.getStatus());
        }
        list.forEach(firm->{
            if (StatusEnums.ACTIVATE.getStatus().equals(firm.getStatus())){
                InitWarehouseDTO dto=new InitWarehouseDTO();
                BeanUtils.copyProperties(firm,dto);
                dto.setCode(firm.getFirmCode());
                List<WarehouseList> items=this.listMapper.getsByFirmId(firm.getId(),StatusEnums.ACTIVATE.getStatus());
                List<InitWarehouseDTO> childs=new ArrayList<>();
                items.forEach(item->{
                    if (StatusEnums.ACTIVATE.getStatus().equals(item.getStatus())){
                        InitWarehouseDTO dto1=new InitWarehouseDTO();
                        dto1.setCode(item.getWarehouseCode());
                        dto1.setName(item.getWarehouseName());
                        dto1.setCountryCode(item.getCountryCode());
                        dto1.setId(item.getId());
                        childs.add(dto1);
                    }
                });
                dto.setItem(childs);
                result.add(dto);
            }
        });
        return result;
    }

    /**
     * 根据根据 供应商账号获取仓库服务上账号列表
     *
     * @param supplierIds
     * @return
     */
    @Override
    public List<WarehouseSelectDTO> getsFirm(List<Integer> supplierIds) {
        List<WarehouseSelectDTO> result=new ArrayList<>(2);
        List<WarehouseFirm> list=this.firmMapper.getBySupplierId(supplierIds,StatusEnums.ACTIVATE.getStatus());
        if (CollectionUtils.isEmpty(list)){
            return result;
        }
        Map<String,List<WarehouseSelectDTO>> map=new HashMap<>();
        list.forEach(firm->{
            if (!map.containsKey(firm.getFirmCode())){
                map.put(firm.getFirmCode(),new ArrayList<>(0));
            }
            WarehouseSelectDTO selectDTO=new WarehouseSelectDTO();
            selectDTO.setName(firm.getName());
            selectDTO.setId(firm.getId());
            map.get(firm.getFirmCode()).add(selectDTO);
        });
        map.forEach((k,v)->{
            WarehouseSelectDTO selectDTO=new WarehouseSelectDTO();
            selectDTO.setId(k.hashCode());
            selectDTO.setName(WarehouseFirmEnum.getByCode(k));
            selectDTO.setChilds(v);
            result.add(selectDTO);
        });
        return result;
    }



    /**
     * 获取服务商下所有的仓库id列表
     *
     * @param serviceCode
     * @return
     */
    @Override
    public List<Integer> getsByType(String serviceCode) {
        List<Integer> result=new ArrayList<>();
        List<WarehouseFirm> list=this.firmMapper.getsByFirmCode(serviceCode,null);
        list.forEach(firm->{
            WarehouseInitDTO initDTO=this.getByFirmId(firm.getId());
            initDTO.getList().forEach(dto->{
                result.add(dto.getId());
            });
        });
        return result;
    }

    /**
     * 获取谷仓公司名及账户
     *
     * @param appKey
     * @param appToken
     * @return
     */
    @Override
    public AccountDTO getAccount(String appKey, String appToken) {
        if (StringUtils.isEmpty(appKey)||StringUtils.isEmpty(appToken)){
            return null;
        }
        String resp=null;
        try {
            resp=this.granaryUtils.getInstance(appToken,appKey,this.wsdlUrl,null,"getAccount").getCallService();
        } catch (Exception e) {
            logger.error("调用谷仓服务异常");
        }
        if (StringUtils.isEmpty(resp)){
            return null;
        }
        JSONObject jsonObject=JSONObject.parseObject(resp);
        if (!"Success".equals(jsonObject.getString("ask"))){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),jsonObject.getString("message"));
        }
        AccountDTO result=jsonObject.getObject("data",AccountDTO.class);
        return result;
    }

    /**
     * 根据账号名查询
     *
     * @param name
     * @return
     */
    @Override
    public Integer getFirmByName(String name) {
        WarehouseFirm firm=this.firmMapper.getByName(name);
        return firm.getId();
    }

    /**
     * 根据appToken及warehouseCode获取仓库信息
     *
     * @param appToken
     * @param warehouseCode
     * @return
     */
    @Override
    public WarehouseDTO getByAppTokenAndCode(String appToken, String warehouseCode) {
        WarehouseFirm firmDO=this.firmMapper.getsByAppToken(appToken);
        if (firmDO==null){
            logger.error("查询无信息appToken={}",appToken);
            return null;
        }
        WarehouseList listDO=this.listMapper.selectByFIdCode(firmDO.getId(),warehouseCode);
        if (listDO==null){
            logger.error("appkey={}下查无仓库:{}信息",appToken,warehouseCode);
            return null;
        }
        WarehouseDTO result=new WarehouseDTO();
        BeanUtils.copyProperties(listDO,result);
        result.setAppToken(firmDO.getAppToken());
        result.setAppKey(firmDO.getAppKey());
        result.setWarehouseId(listDO.getId());
        return result;
    }

    /**
     * 根据appKey及warehouseCode获取仓库信息
     *
     * @param appKey
     * @param warehouseCode
     * @return
     */
    @Override
    public WarehouseDTO getByAppKeyAndCode(String appKey, String warehouseCode) {
        WarehouseFirm firmDO=this.firmMapper.getsByAppKey(appKey);
        if (firmDO==null){
            logger.error("查询无信息:appKey={}",appKey);
            return null;
        }
        WarehouseList listDO=this.listMapper.selectByFIdCode(firmDO.getId(),warehouseCode);
        if (listDO==null){
            logger.error("appkey={}下查无仓库:{}信息",appKey,warehouseCode);
            return null;
        }
        WarehouseDTO result=new WarehouseDTO();
        BeanUtils.copyProperties(listDO,result);
        result.setAppToken(firmDO.getAppToken());
        result.setAppKey(firmDO.getAppKey());
        result.setWarehouseId(listDO.getId());
        return result;
    }

    /**
     * 根据谷仓code获取仓库
     *
     * @param warehouseCode
     * @return
     */
    @Override
    public WarehouseDTO getByAppTokenAndCode(String warehouseCode) {
        WarehouseList listDO=this.listMapper.getByCode(warehouseCode,null);
        if (listDO==null){
            return null;
        }
        return this.getByWarehouseId(listDO.getId());
    }

    /**
     * 根据供应链公司获取绑定仓库服务商总数
     *
     * @param supplyId
     * @return
     */
    @Override
    public Integer getBindService(Integer supplyId) {
        return this.serviceMapper.getBindService(supplyId);
    }

    /**
     * 获取供应商主账号关联所有的仓库id
     *
     * @param supplierId
     * @return
     */
    @Override
    public List<Integer> getsIdBySupplierId(Integer supplierId) {
        List<Integer> supplierIds=new ArrayList<>();
        supplierIds.add(supplierId);
        supplierIds.add(0);
        List<WarehouseFirm> firms=this.firmMapper.getBySupplierId(supplierIds,StatusEnums.ACTIVATE.getStatus());
        List<Integer> firmIds=new ArrayList<>();
        firms.forEach(firmDO->{
            firmIds.add(firmDO.getId());
        });
        return this.listMapper.getsByFirmIds(firmIds);
    }

    /**
     * @param supplierIds
     * @param warehouseIds
     * @return
     */
    @Override
    public List<WarehouseCountryDTO> getsWarehouseList(List<Integer> supplierIds, List<Integer> warehouseIds,String languageType) {
        List<WarehouseCountryDTO> result=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(supplierIds)){
            List<WarehouseFirm> list=this.firmMapper.getBySupplierId(supplierIds,StatusEnums.ACTIVATE.getStatus());
            for (WarehouseFirm firm:list) {
                List<WarehouseList> list1=this.listMapper.getsByFirmId(firm.getId(),StatusEnums.ACTIVATE.getStatus());
                list1.forEach(listDO->{
                    WarehouseCountryDTO dto=new WarehouseCountryDTO();
                    dto.setCountryCode(listDO.getCountryCode());
                    dto.setWarehouseName((StringUtils.isEmpty(languageType)?listDO.getWarehouseName():Utils.translation(listDO.getWarehouseName())));
                    dto.setWarehouseId(listDO.getId());

                    Object resp=this.userService.getNameByCode(listDO.getCountryCode(),languageType,1);
                    if (resp!=null){
                        JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(resp));
                        dto.setCountryName(jsonObject.getString("data"));
                    }
                    result.add(dto);
                });
            }
        }else if (CollectionUtils.isNotEmpty(warehouseIds)){
            warehouseIds.forEach(id->{
                WarehouseDTO warehouseDTO=this.getByWarehouseId(id);
                WarehouseCountryDTO dto=new WarehouseCountryDTO();
                dto.setWarehouseId(id);
                dto.setWarehouseName((StringUtils.isEmpty(languageType)?warehouseDTO.getWarehouseName():Utils.translation(warehouseDTO.getWarehouseName())));
                dto.setCountryCode(dto.getCountryCode());
                Object resp=this.userService.getNameByCode(dto.getCountryCode(),languageType,1);
                if (resp!=null){
                    JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(resp));
                    dto.setCountryName(jsonObject.getString("data"));
                }
                result.add(dto);
            });
        }
        return result;
    }


    /**
     * 分页查询仓库列表
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<WarehousePageDTO> getsPage(WarehouseQueryDTO dto) {
        List<Integer> firmIds=this.firmMapper.getPage(dto.getFirmCode(),dto.getSupplierIds(),dto.getId());
        if ((StringUtils.isNotEmpty(dto.getFirmCode())||CollectionUtils.isNotEmpty(dto.getSupplierIds()))&&CollectionUtils.isEmpty(firmIds)){
            return new PageDTO<>(dto.getCurrentPage(),0);
        }
        if (CollectionUtils.isEmpty(dto.getSupplierIds())&&CollectionUtils.isNotEmpty(dto.getWarehouseIds())){
            firmIds=null;
        }
        PageHelper.startPage(dto.getCurrentPage(),dto.getPageSize());
        List<WarehouseList> list=this.listMapper.getPage(firmIds,dto.getStatus(),dto.getWarehouseIds());
        PageInfo<WarehouseList> pageInfo=new PageInfo<>(list);
        PageDTO<WarehousePageDTO> result=new PageDTO<>((int)pageInfo.getTotal(),dto.getCurrentPage());
        if (CollectionUtils.isEmpty(pageInfo.getList())){
            return result;
        }
        List<WarehousePageDTO> pageDatas=new ArrayList<>();
        pageInfo.getList().forEach(warehouseList->{
            WarehouseDTO warehouseDTO=this.getByWarehouseId(warehouseList.getId());
            WarehousePageDTO pageDTO=new WarehousePageDTO();
            BeanUtils.copyProperties(warehouseDTO,pageDTO,"warehouseName");
            pageDTO.setWarehouseId(warehouseList.getId());
            if (StringUtils.isNotEmpty(pageDTO.getCountryCode())){
                FeignResult<String> feignResult=this.userService.getNameByCode(pageDTO.getCountryCode(),dto.getLanguageType(),1);
                pageDTO.setCountryName(feignResult.getData());
            }
            pageDTO.setServiceName(StringUtils.isEmpty(dto.getLanguageType())?WarehouseFirmEnum.getByCode(warehouseDTO.getFirmCode()):Utils.translation(WarehouseFirmEnum.getByCode(warehouseDTO.getFirmCode())));
            pageDTO.setServiceCode(warehouseDTO.getFirmCode());
            pageDTO.setWarehouseName(StringUtils.isEmpty(dto.getLanguageType())?warehouseDTO.getWarehouseName():Utils.translation(warehouseDTO.getWarehouseName()));

            pageDTO.setSupplyId(warehouseDTO.getSupplyId());
            pageDTO.setSupplyName(warehouseDTO.getSupplyName());
            if (warehouseDTO.getSupplierId()!=null&&warehouseDTO.getSupplierId()>0){
                Object object=this.userService.getUser(warehouseDTO.getSupplierId(),null,UserEnum.platformType.SUPPLIER.getPlatformType());
                if (object!=null){
                    JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object));
                    if (jsonObject.getJSONObject("data")!=null){
                        pageDTO.setSupplierName(jsonObject.getJSONObject("data").getString("loginName"));
                    }
                }
            }
            pageDatas.add(pageDTO);
        });
        result.setList(pageDatas);
        return result;
    }

    /**
     * 根据仓库id获取仓库地址
     *
     * @param warehouseId
     * @return
     */
    @Override
    public AddressDTO getAddress(Integer warehouseId,String languageType) {
        AddressDTO result=new AddressDTO();
        WarehouseListAddress address = this.addressMapper.selectByPrimaryKey(warehouseId.longValue());
        if (address==null){
            return null;
        }
        BeanUtils.copyProperties(address,result);
        WarehouseDTO warehouseDTO=this.getByWarehouseId(warehouseId);
        result.setCountry(warehouseDTO.getCountryCode());
        if (StringUtils.isNotEmpty(warehouseDTO.getCountryCode())){
            FeignResult<String> feignResult=this.userService.getNameByCode(warehouseDTO.getCountryCode(),languageType,1);
            result.setCountryName(feignResult.getSuccess()?feignResult.getData():null);
        }
        return result;
    }

    /**
     * 修改地址
     *
     * @param dto
     * @return
     */
    @Override
    public Integer updateAddress(AddressDTO dto) {
        WarehouseDTO exits=this.getByWarehouseId(dto.getId());
        if (exits==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"修改对象不存在");
        }
        WarehouseListAddress address=new WarehouseListAddress();
        BeanUtils.copyProperties(dto,address);
        WarehouseListAddress old=this.addressMapper.selectByPrimaryKey(dto.getId().longValue());
        if (old==null){
            address.setVersion(1);
            this.addressMapper.insert(address);
        }else {
            address.setVersion(old.getVersion());
            this.addressMapper.updateByPrimaryKey(address);
        }
        return 1;
    }

    /**
     * 获取自定义账号
     *
     * @param supplierIds
     * @return
     */
    @Override
    public List<String> getsAccount(List<Integer> supplierIds) {
        List<WarehouseFirm> firmList =this.firmMapper.getBySupplierId(supplierIds,null);
        List<String> result=new ArrayList<>();
        firmList.forEach(firmDO -> {
            if (CollectionUtils.isEmpty(supplierIds)){
                if (!"0".equals(firmDO.getSupplierId().toString())){
                    result.add(firmDO.getName());
                }
            }else {
                result.add(firmDO.getName());
            }
        });
        return result;
    }

    /**
     * 获取仓库名
     *
     * @param supplierIds
     * @return
     */
    @Override
    public List<KeyValueDTO> getsWarehouseName(List<Integer> supplierIds,String languageType) {
        List<KeyValueDTO> result=new ArrayList<>();
        List<WarehouseFirm> firmList=this.firmMapper.getBySupplierId(supplierIds,null);
        List<Integer> ids=new ArrayList<>();
        if (CollectionUtils.isEmpty(firmList)){
            return result;
        }
        firmList.forEach(firmDO -> {
            if (CollectionUtils.isEmpty(supplierIds)){
                if (!"0".equals(firmDO.getSupplierId().toString())){
                    ids.add(firmDO.getId());
                }
            }else {
                ids.add(firmDO.getId());
            }
        });
        List<WarehouseList> lists=this.listMapper.getPage(ids,null,null);
        if (CollectionUtils.isEmpty(lists)){
            return result;
        }
        lists.forEach(listDO -> {
            result.add(new KeyValueDTO(listDO.getId().toString(),StringUtils.isEmpty(languageType)?listDO.getWarehouseName():Utils.translation(listDO.getWarehouseName())));
        });
        return result;
    }


    /**
     * 根据供应商id获取列表
     *
     * @param supplierId
     * @return
     */
    @Override
    public List<WarehouseSelectDTO> getSelectList(Integer supplierId,String languageType) {
        List<Integer> supplierIds=null;
        if (supplierId!=null){
            supplierIds=new ArrayList<>();
            supplierIds.add(supplierId);
        }
        List<WarehouseFirm> list=this.firmMapper.getBySupplierId(supplierIds,null);
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        Map<String,List<WarehouseFirm>> map=new HashMap<>(3);
        list.forEach(firm -> {
            if (!map.containsKey(firm.getFirmCode())){
                map.put(firm.getFirmCode(),new ArrayList<>(1));
            }
            map.get(firm.getFirmCode()).add(firm);
        });
        List<WarehouseSelectDTO> result=new ArrayList<>(3);
        map.forEach( (k,v) ->{
            WarehouseSelectDTO dto=new WarehouseSelectDTO(k.hashCode(),StringUtils.isEmpty(languageType)?WarehouseFirmEnum.getByCode(k):Utils.translation(WarehouseFirmEnum.getByCode(k)));
            List<WarehouseSelectDTO> child1=new ArrayList<>(v.size());
            v.forEach( vv -> {
                WarehouseSelectDTO dto1=new WarehouseSelectDTO(vv.getId(),StringUtils.isEmpty(languageType)?vv.getName():Utils.translation(vv.getName()));
                List<WarehouseSelectDTO> child2=new ArrayList<>();
                List<WarehouseList> list3=this.listMapper.getsByFirmId(vv.getId(),null);
                list3.forEach(listDO -> {
                    WarehouseSelectDTO dto2=new WarehouseSelectDTO(listDO.getId(),StringUtils.isEmpty(languageType)?listDO.getWarehouseName():Utils.translation(listDO.getWarehouseName()));
                    child2.add(dto2);
                });
                dto1.setChilds(child2);
                child1.add(dto1);
            });
            dto.setChilds(child1);
            result.add(dto);
        });
        return result;
    }

    /**
     * 根据erp仓库code及供应商账号获取仓库id
     *
     * @param warehouseCode
     * @param supplierId
     * @return
     */
    @Override
    public Integer getCodeAndId(String warehouseCode, Integer supplierId) {
        List<WarehouseFirm> list=this.firmMapper.getsByFirmCode(WarehouseFirmEnum.RONDAFUL.getCode(),supplierId);
        if (CollectionUtils.isEmpty(list)||list.size()>1){
            return null;
        }
        WarehouseList warehouseList=this.listMapper.selectByFIdCode(list.get(0).getId(),warehouseCode);
        if (warehouseList==null){
            return null;
        }
        return warehouseList.getId();
    }

    /**
     * 根据供应商id获取列表
     * @param supplierIds
     * @return
     */
    private List<WarehouseFirmTreeDTO> getsTreeBySupplierId(List<Integer> supplierIds,Integer status){
        List<WarehouseFirmTreeDTO> result=new ArrayList<>();
        List<WarehouseFirm> list=this.firmMapper.getBySupplierId(supplierIds,status);
        if (CollectionUtils.isEmpty(list)){
            return result;
        }
        Map<String,List<WarehouseFirmTreeDTO>> map=new HashMap<>(2);
        list.forEach(firmDO ->{
            if (!map.containsKey(firmDO.getFirmCode())){
                map.put(firmDO.getFirmCode(),new ArrayList<>());
            }
            WarehouseFirmTreeDTO treeDTO=new WarehouseFirmTreeDTO();
            BeanUtils.copyProperties(firmDO,treeDTO);
            treeDTO.setName(firmDO.getName());
            treeDTO.setStatus(firmDO.getStatus());
            treeDTO.setId(firmDO.getId());
            treeDTO.setSupplierId(firmDO.getSupplierId());
            if (firmDO.getSupplierId()!=null&&firmDO.getSupplierId()>0){
                List<Integer> userId=new ArrayList<>(1);
                userId.add(firmDO.getSupplierId());
                Object aa=this.userService.getSupplierList(userId, UserEnum.platformType.SUPPLIER.getPlatformType().toString());
                JSONArray jsonArray=JSONObject.parseObject(JSONObject.toJSONString(aa)).getJSONArray("data");
                if (CollectionUtils.isNotEmpty(jsonArray)){
                    treeDTO.setSupplierName(jsonArray.getJSONObject(0).getString("loginName"));
                }
            }

            FeignResult<LogisticsInfo> feignResult=this.userService.getLogSupplyId(firmDO.getLogisticsUserId());
            treeDTO.setCompanyName(feignResult.getData()==null?null:feignResult.getData().getSupplyName());

            List<WarehouseList> childDOList=this.listMapper.getsByFirmId(firmDO.getId(),status);
            List<WarehouseFirmTreeDTO> childList=new ArrayList<>();
            for (WarehouseList listDO:childDOList) {
                WarehouseFirmTreeDTO childDO=new WarehouseFirmTreeDTO();
                BeanUtils.copyProperties(listDO,childDO);
                childDO.setId(listDO.getId());
                childDO.setName(listDO.getWarehouseName());
                childDO.setCountry(listDO.getCountryCode());
                childDO.setStatus(listDO.getStatus());
                childDO.setCode(listDO.getWarehouseCode());
                childList.add(childDO);
            }
            treeDTO.setChilds(childList);

            map.get(firmDO.getFirmCode()).add(treeDTO);
        });

        map.forEach((k,v)->{
            WarehouseFirmTreeDTO first=new WarehouseFirmTreeDTO();
            first.setName(k);
            first.setCode(k);
            first.setSupplierId(v.get(0).getSupplierId());
            first.setId(k.hashCode());
            first.setName(WarehouseFirmEnum.getByCode(k));
            first.setChilds(v);
            result.add(first);
        });
        return result;
    }

    /**
     * 清楚缓存
     * @param warehouseId
     */
    private void clearCache(Integer warehouseId){
        String key=SUPPLIER_WAREHOUSE_ID+warehouseId;
        this.redisTemplate.delete(key);
    }
}
