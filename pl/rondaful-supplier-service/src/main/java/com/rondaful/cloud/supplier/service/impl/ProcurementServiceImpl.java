package com.rondaful.cloud.supplier.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.entity.procurement.Procurement;
import com.rondaful.cloud.supplier.entity.procurement.ProcurementList;
import com.rondaful.cloud.supplier.entity.procurement.ProcurementSuggest;
import com.rondaful.cloud.supplier.mapper.ProcurementListMapper;
import com.rondaful.cloud.supplier.mapper.ProcurementMapper;
import com.rondaful.cloud.supplier.mapper.ProcurementSuggestMapper;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.CommodityDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.InventoryDTO;
import com.rondaful.cloud.supplier.model.dto.procurement.*;
import com.rondaful.cloud.supplier.model.enums.StatusEnums;
import com.rondaful.cloud.supplier.remote.RemoteCommodityService;
import com.rondaful.cloud.supplier.service.*;
import com.rondaful.cloud.supplier.utils.IDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/20
 * @Description:
 */
@Service("procurementServiceImpl")
public class ProcurementServiceImpl implements IProcurementService {

    @Autowired
    private ProcurementMapper mapper;
    @Autowired
    private ProcurementListMapper listMapper;
    @Autowired
    private ProcurementSuggestMapper suggestMapper;
    @Autowired
    private IInventoryService inventoryService;
    @Autowired
    private IProviderService providerService;
    @Autowired
    private IWarehouseBasicsService basicsService;
    @Autowired
    private RemoteCommodityService commodityService;

    /**
     * 修改子项的采购信息
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(ProcurementDTO dto) {
        Procurement procurementDO=new Procurement();
        BeanUtils.copyProperties(dto,procurementDO,"createBy");
        procurementDO.setSumPrice(new BigDecimal("0.00"));
        procurementDO.setStatus(StatusEnums.NO_AUDIT.getStatus());
        procurementDO.setUpdateBy(procurementDO.getCreateBy());
        procurementDO.setUpdateTime(new Date());
        procurementDO.setSumPrice(new BigDecimal("0.00"));
        List<ProcurementList> lists=new ArrayList<>(dto.getItems().size());
        dto.getItems().forEach( item -> {
            ProcurementList listDO=new ProcurementList();
            BeanUtils.copyProperties(item,listDO);
            listDO.setProcurementId(procurementDO.getId());
            listDO.setStatus(StatusEnums.DRAFT.getStatus());
            procurementDO.setSumPrice(procurementDO.getSumPrice().add(new BigDecimal(listDO.getBuyAmount()).multiply(item.getPrice())));
            procurementDO.setSumFreight(procurementDO.getSumPrice().add(item.getFreight()).toString());
            lists.add(listDO);
        });
        this.mapper.updateByPrimaryKeySelective(procurementDO);
        this.listMapper.updateBatch(lists);
        return 1;
    }

    /**
     * 分页查询采购单
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<ProcurementPageDTO> getsPage(QueryProcurementPageDTO dto) {
        PageHelper.startPage(dto.getCurrentPage(),dto.getPageSize());
        List<Procurement> list=this.mapper.getsPage(dto.getProviderId(),dto.getStartTime(),dto.getEndTime(),dto.getStatus(),dto.getWarehouseId());
        PageInfo<Procurement> pageInfo=new PageInfo<>(list);
        PageDTO<ProcurementPageDTO> result=new PageDTO<>((int)pageInfo.getTotal(),dto.getCurrentPage());
        List<ProcurementPageDTO> dataList=new ArrayList<>();
        pageInfo.getList().forEach( info -> {
            ProcurementPageDTO detailDTO=new ProcurementPageDTO();
            BeanUtils.copyProperties(info,detailDTO);
            detailDTO.setId(info.getId().toString());
            ProviderDTO providerDTO=this.providerService.get(info.getProviderId());
            if (providerDTO!=null){
                detailDTO.setProviderName(providerDTO.getProviderName());
                WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(info.getWarehouseId());
                detailDTO.setWarehouseName(warehouseDTO.getWarehouseName());
                detailDTO.setFirmName(warehouseDTO.getName());

                ProcurementList oneDO=this.listMapper.getOne(info.getId());

                CommodityDTO commodityDTO=new CommodityDTO();
                detailDTO.setCommodityName(commodityDTO.getCommodityName());
                detailDTO.setPrice(oneDO.getPrice());
                detailDTO.setFreight(oneDO.getFreight());
                detailDTO.setBuyAmount(oneDO.getBuyAmount());
                detailDTO.setPutawayAmount(oneDO.getPutawayAmount());
                dataList.add(detailDTO);
            }
        });
        result.setList(dataList);
        return result;
    }

    /**
     * 根据采购单id获取采购明细
     *
     * @param procurementId
     * @return
     */
    @Override
    public ProcurementDetailDTO getsDetailList(Long procurementId, String languageType) {
        ProcurementDetailDTO result=new ProcurementDetailDTO();
        Procurement procurement=this.mapper.selectByPrimaryKey(procurementId);
        if (procurement==null){
            return result;
        }
        BeanUtils.copyProperties(procurement,result);
        result.setId(procurement.getId().toString());
        ProviderDTO providerDTO=this.providerService.get(procurement.getProviderId());
        if (procurement!=null){
            result.setProviderName(providerDTO.getProviderName());
            result.setBuyer(providerDTO.getBuyer());
        }
        WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(procurement.getWarehouseId());
        if (warehouseDTO!=null){
            result.setWarehouseName(warehouseDTO.getName()+"-"+warehouseDTO.getWarehouseName());
        }
        List<ProcurementList> lists=this.listMapper.getByPId(procurementId);
        List<ProcurementListDeatilDTO> item=new ArrayList<>();
        lists.forEach( listDO -> {
            ProcurementListDeatilDTO detailDTO=new ProcurementListDeatilDTO();
            BeanUtils.copyProperties(listDO,detailDTO);
            CommodityDTO commodityDTO=new CommodityDTO();
            detailDTO.setPictureUrl(commodityDTO.getPictureUrl());
            detailDTO.setSupplierSku(commodityDTO.getSupplierSku());
            detailDTO.setCommodityName(commodityDTO.getCommodityName());
            detailDTO.setId(listDO.getId().toString());
            InventoryDTO inventoryDTO=this.inventoryService.getByWPinlianSku(procurement.getWarehouseId(),listDO.getPinlianSku());
            if (inventoryDTO!=null){
                detailDTO.setAvailableQty(inventoryDTO.getAvailableQty());
            }
            Object object=this.commodityService.getBySku(listDO.getPinlianSku(),null,null);
            if (object!=null){
                JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object));
                if (StringUtils.isEmpty(languageType)){
                    detailDTO.setCommodityName(jsonObject.getJSONObject("data").getString("commodityNameCn"));
                }else {
                    detailDTO.setCommodityName(jsonObject.getJSONObject("data").getString("commodityDescEn"));
                }
            }
            item.add(detailDTO);
        });
        result.setList(item);
        return result;
    }

    /**
     * @param procurementId
     * @param status
     * @param updateBy
     * @return
     */
    @Override
    public Integer updateStatus(Long procurementId, Integer status, String updateBy,String remake) {
        Procurement procurement=new Procurement();
        procurement.setId(procurementId);
        procurement.setStatus(status);
        procurement.setUpdateBy(updateBy);
        procurement.setUpdateTime(new Date());
        if (StatusEnums.ACTIVATE.getStatus().equals(status)||StatusEnums.AUDIT_FILE.getStatus().equals(status)){
            procurement.setAuditBy(updateBy);
            procurement.setAuditTime(procurement.getUpdateTime());
            procurement.setRemake(remake);
        }
        return this.mapper.updateByPrimaryKeySelective(procurement);
    }

    /**
     * 添加采购单
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(ProcurementDTO dto) {
        Procurement procurementDO=new Procurement();
        BeanUtils.copyProperties(dto,procurementDO);
        procurementDO.setId(new IDUtil(1L,1L).nextId());
        procurementDO.setSumPrice(new BigDecimal("0.00"));
        procurementDO.setStatus(dto.getStatus()==null?StatusEnums.NO_AUDIT.getStatus():dto.getStatus());
        procurementDO.setCreateTime(new Date());
        procurementDO.setUpdateBy(procurementDO.getCreateBy());
        procurementDO.setUpdateTime(procurementDO.getCreateTime());
        procurementDO.setSumPrice(new BigDecimal("0.00"));
        List<ProcurementList> lists=new ArrayList<>(dto.getItems().size());
        dto.getItems().forEach( item -> {
            ProcurementList listDO=new ProcurementList();
            BeanUtils.copyProperties(item,listDO);
            listDO.setProcurementId(procurementDO.getId());
            listDO.setStatus(StatusEnums.DRAFT.getStatus());
            listDO.setPutawayAmount(0);
            listDO.setId(new IDUtil(1L,1L).nextId());
            procurementDO.setSumPrice(procurementDO.getSumPrice().add(new BigDecimal(listDO.getBuyAmount()).multiply(item.getPrice())));
            procurementDO.setSumFreight(procurementDO.getSumPrice().add(item.getFreight()).toString());
            lists.add(listDO);
        });
        this.mapper.insert(procurementDO);
        this.listMapper.insertBatch(lists);
        return 1;
    }

    /**
     * 批量添加采购单
     *
     * @param list
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addBatch(List<ProcurementDTO> list) {
        List<Procurement> procurements=new ArrayList<>(list.size());
        List<ProcurementList> lists=new ArrayList<>(list.size());
        for (ProcurementDTO dto:list) {
            Procurement procurementDO=new Procurement();
            BeanUtils.copyProperties(dto,procurementDO);
            procurementDO.setId(new IDUtil(1L,1L).nextId());
            procurementDO.setSumPrice(new BigDecimal("0.00"));
            procurementDO.setStatus(StatusEnums.DRAFT.getStatus());
            procurementDO.setCreateTime(new Date());
            procurementDO.setUpdateBy(procurementDO.getCreateBy());
            procurementDO.setUpdateTime(procurementDO.getCreateTime());
            procurementDO.setSumPrice(new BigDecimal("0.00"));

            for (int i = 0; i < dto.getItems().size(); i++) {
                ProcurementList listDO=new ProcurementList();
                BeanUtils.copyProperties(dto.getItems().get(i),listDO);
                listDO.setProcurementId(procurementDO.getId());
                listDO.setStatus(StatusEnums.DRAFT.getStatus());
                listDO.setId(new IDUtil(2L,Long.valueOf(i)).nextId());
                if (dto.getItems().get(i).getPrice()!=null){
                    procurementDO.setSumPrice(procurementDO.getSumPrice().add(new BigDecimal(listDO.getBuyAmount()).multiply(dto.getItems().get(i).getPrice())));
                    procurementDO.setSumFreight(procurementDO.getSumPrice().add(dto.getItems().get(i).getFreight()).toString());
                }
                lists.add(listDO);
            }
            procurements.add(procurementDO);
        }
        this.mapper.insertBatch(procurements);
        this.listMapper.insertBatch(lists);
        return 1;
    }

    @Override
    public Integer addSuggest(SuggestDTO dto) {
        if (dto.getWarehouseId()==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "目的仓库不存在");
        }
        ProcurementSuggest suggestDO=this.suggestMapper.getByOrderId(dto.getOrderId());
        if (suggestDO==null){
            suggestDO=new ProcurementSuggest();
            BeanUtils.copyProperties(dto,suggestDO);
            suggestDO.setId(new IDUtil(1L,2L).nextId());
            suggestDO.setStatus(StatusEnums.NO_AUDIT.getStatus());
            suggestDO.setCreateTime(new Date());
            return this.suggestMapper.insert(suggestDO);
        }else {
            ProcurementSuggest suggest=new ProcurementSuggest();
            BeanUtils.copyProperties(dto,suggest,"id");
            suggest.setUpdateTime(new Date());
            suggest.setStatus(StatusEnums.NO_AUDIT.getStatus());
            suggest.setId(suggestDO.getId());
            return this.suggestMapper.updateByPrimaryKeySelective(suggest);
        }

    }

    /**
     * 修改采购建议状态
     *
     * @param status
     * @return
     */
    @Override
    public Integer updateSuggestStatus(String updateBy, Integer status,Long suggestId) {
        ProcurementSuggest suggestDO=new ProcurementSuggest();
        suggestDO.setId(suggestId);
        suggestDO.setStatus(status);
        suggestDO.setUpdateBy(updateBy);
        suggestDO.setUpdateTime(new Date());
        return this.suggestMapper.updateByPrimaryKeySelective(suggestDO);
    }

    /**
     * 采购建议分页查询
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<SuggestPageDTO> getsSuggestPage(QuerySuggestPageDTO dto) {
        PageHelper.startPage(dto.getCurrentPage(),dto.getPageSize());
        List<ProcurementSuggest> list=this.suggestMapper.getsSuggestPage(dto.getStartTime(),dto.getEndTime(),dto.getWarehouseId(),dto.getStatus());
        PageInfo<ProcurementSuggest> pageInfo=new PageInfo<>(list);
        PageDTO<SuggestPageDTO> result=new PageDTO<>((int)pageInfo.getTotal(),dto.getCurrentPage());
        List<SuggestPageDTO> dataList=new ArrayList<>();
        pageInfo.getList().forEach( page -> {
            SuggestPageDTO dto1=new SuggestPageDTO();
            BeanUtils.copyProperties(page,dto1);
            dto1.setId(page.getId().toString());

            Object comReq=this.commodityService.search("1","11",null,null,null,null,null,null,null,null,page.getPinlianSku(),null);
            JSONObject comReqJson=JSONObject.parseObject(JSONObject.toJSONString(comReq));
            if (comReqJson.getBoolean("success")){
                JSONObject jsonObject=comReqJson.getJSONObject("data").getJSONObject("pageInfo");
                if (jsonObject.getInteger("size")==1){
                    JSONObject jsonData=jsonObject.getJSONArray("list").getJSONObject(0);
                    if (StringUtils.isEmpty(dto.getLanguageType())){
                        dto1.setSort(jsonData.getString("categoryName1"));
                    }else {
                        dto1.setSort(jsonData.getString("categoryName2"));
                    }
                    dto1.setPictureUrl(jsonData.getString("masterPicture"));
                    dto1.setSupplierSku(jsonData.getString("supplierSku"));
                    dto1.setLevelThree(jsonData.getString("categoryLevel3"));
                }
            }
            WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(page.getWarehouseId());
            if (warehouseDTO!=null){
                dto1.setWarehouseName(warehouseDTO.getName()+"-"+warehouseDTO.getWarehouseName());
            }
            InventoryDTO inventoryDTO=this.inventoryService.getByWPinlianSku(page.getWarehouseId(),page.getPinlianSku());
            if (inventoryDTO!=null){
                BeanUtils.copyProperties(inventoryDTO,dto1);
            }
            dataList.add(dto1);
        });
        result.setList(dataList);
        return result;
    }

    /**
     * 入库采购商品
     *
     * @param id
     * @param num
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer storage(Long id, Integer num) {
        ProcurementList listDO=this.listMapper.selectByPrimaryKey(id);
        if (listDO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"修改对象不存在");
        }
        if (listDO.getBuyAmount()<listDO.getPutawayAmount()+num){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"入库数量超过最大值");
        }
        ProcurementList updateDO=new ProcurementList();
        updateDO.setId(id);
        updateDO.setPutawayAmount(listDO.getPutawayAmount()+num);
        this.listMapper.updateByPrimaryKeySelective(updateDO);
        Procurement procurementDO=new Procurement();
        procurementDO.setId(listDO.getProcurementId());
        if (updateDO.getPutawayAmount()<listDO.getBuyAmount()){
            procurementDO.setStatus(StatusEnums.PART_PUTWAY.getStatus());
        }else {
            procurementDO.setStatus(StatusEnums.PUTWAY.getStatus());
        }
        return this.mapper.updateByPrimaryKeySelective(procurementDO);
    }
}
