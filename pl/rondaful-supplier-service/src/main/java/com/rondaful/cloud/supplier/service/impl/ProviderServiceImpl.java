package com.rondaful.cloud.supplier.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.entity.procurement.Provider;
import com.rondaful.cloud.supplier.mapper.ProviderMapper;
import com.rondaful.cloud.supplier.model.dto.KeyValueDTO;
import com.rondaful.cloud.supplier.model.dto.procurement.ProviderDTO;
import com.rondaful.cloud.supplier.model.dto.procurement.ProviderNameDTO;
import com.rondaful.cloud.supplier.model.dto.procurement.ProviderPageDTO;
import com.rondaful.cloud.supplier.model.dto.procurement.ProviderQueryPageDTO;
import com.rondaful.cloud.supplier.model.enums.StatusEnums;
import com.rondaful.cloud.supplier.remote.RemoteCommodityService;
import com.rondaful.cloud.supplier.service.IProviderService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/19
 * @Description:
 */
@Service("providerServiceImpl")
public class ProviderServiceImpl implements IProviderService {
    private final Logger logger = LoggerFactory.getLogger(ProviderServiceImpl.class);
    @Autowired
    private ProviderMapper mapper;
    @Autowired
    private RemoteCommodityService commodityService;


    /**
     * 新增供货商
     *
     * @param dto
     * @return
     */
    @Override
    public Integer add(ProviderDTO dto) {
        Provider providerDO=new Provider();
        BeanUtils.copyProperties(dto,providerDO);
        providerDO.setUpdateBy(providerDO.getCreateBy());
        providerDO.setCreateTime(new Date());
        providerDO.setUpdateBy(providerDO.getCreateBy());
        providerDO.setStatus(StatusEnums.NO_AUDIT.getStatus());
        return this.mapper.insert(providerDO);
    }

    /**
     * 修改供货商状态
     *
     * @param id
     * @param status
     * @param remake
     * @return
     */
    @Override
    public Integer updateStatus(Integer id, Integer status, String remake,String updateBy) {
        ProviderDTO oldDTO=this.get(id);
        if (oldDTO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "修改对象不存在");
        }
        Provider providerDO=new Provider();
        providerDO.setId(id);
        providerDO.setUpdateTime(new Date());
        providerDO.setAuditTime(providerDO.getUpdateTime());
        providerDO.setUpdateBy(updateBy);
        providerDO.setAuditBy(updateBy);
        providerDO.setStatus(status);
        return this.mapper.updateByPrimaryKeySelective(providerDO);
    }

    /**
     * 修改供货商
     *
     * @param dto
     * @return
     */
    @Override
    public Integer update(ProviderDTO dto) {
        ProviderDTO oldDTO=this.get(dto.getId());
        if (oldDTO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "修改对象不存在");
        }
        Provider providerDO=new Provider();
        BeanUtils.copyProperties(dto,providerDO,"createBy");
        providerDO.setUpdateTime(new Date());
        return this.mapper.updateByPrimaryKeySelective(providerDO);
    }

    /**
     * 根据id获取供货商
     *
     * @param id
     * @return
     */
    @Override
    public ProviderDTO get(Integer id) {
        Provider providerDO=this.mapper.selectByPrimaryKey(id.longValue());
        if (providerDO==null){
            return null;
        }
        ProviderDTO result=new ProviderDTO();
        BeanUtils.copyProperties(providerDO,result);
        return result;
    }

    /**
     * 分页查询
     *
     * @param queryDTO
     * @return
     */
    @Override
    public PageDTO<ProviderPageDTO> getsPage(ProviderQueryPageDTO queryDTO) {
        PageHelper.startPage(queryDTO.getCurrentPage(),queryDTO.getPageSize());
        List<Provider> list=this.mapper.getsPage(queryDTO.getSupplierId(),queryDTO.getId(),queryDTO.getStartTime(),queryDTO.getEndTime(),queryDTO.getStatus());
        PageInfo<Provider> pageInfo=new PageInfo<>(list);
        PageDTO<ProviderPageDTO> result=new PageDTO<>((int)pageInfo.getTotal(),queryDTO.getCurrentPage());
        List<ProviderPageDTO> datas=new ArrayList<>();
        pageInfo.getList().forEach(data -> {
            ProviderPageDTO pageDTO=new ProviderPageDTO();
            BeanUtils.copyProperties(data,pageDTO);
            datas.add(pageDTO);
        });
        result.setList(datas);
        return result;
    }

    /**
     * 获取供货商名称列表
     *
     * @param supplierId
     * @return
     */
    @Override
    public List<KeyValueDTO> getSelectName(Integer supplierId,String pinlianSku) {
        List<Provider> list=this.mapper.getsName(supplierId,null);
        logger.info("获取供货商名称列表:supplierId={},pinlianSku={},size={}",supplierId,pinlianSku,list.size());
        List<KeyValueDTO> result=new ArrayList<>();
        Object object=this.commodityService.getBySku(pinlianSku,null,null);
        JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object));
        list.forEach(provider -> {
            if (provider.getLevelThree().equals(jsonObject.getJSONObject("data").getString("categoryLevel3"))){
                result.add(new KeyValueDTO(provider.getId().toString(),provider.getProviderName(),provider.getBuyer()));
            }
        });
        return result;
    }

    /**
     * 根据供应商获取对应级别的供货商
     *
     * @param supplierId
     * @param levelThree
     * @return
     */
    @Override
    public List<ProviderNameDTO> getsProviderName(Integer supplierId, String levelThree) {
        List<Provider> list=this.mapper.getsName(supplierId,levelThree);
        List<ProviderNameDTO> result=new ArrayList<>();
        list.forEach(providerDO->{
            ProviderNameDTO providerNameDTO=new ProviderNameDTO();
            BeanUtils.copyProperties(providerDO,providerNameDTO);
            result.add(providerNameDTO);
        });
        return result;
    }
}
