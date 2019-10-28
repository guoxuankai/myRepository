package com.rondaful.cloud.user.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.user.entity.SupplyChainUser;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.enums.UserStatusEnum;
import com.rondaful.cloud.user.mapper.SupplyChainUserMapper;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.KeyValueDTO;
import com.rondaful.cloud.user.model.dto.user.QuerySuppluChinaDTO;
import com.rondaful.cloud.user.model.dto.user.SupplyChainUserDTO;
import com.rondaful.cloud.user.model.dto.user.SupplyChainUserPageDTO;
import com.rondaful.cloud.user.remote.RemoteSupplierService;
import com.rondaful.cloud.user.remote.UserFinanceInitialization;
import com.rondaful.cloud.user.service.INewSupplierService;
import com.rondaful.cloud.user.service.ISellerUserService;
import com.rondaful.cloud.user.service.ISupplyChainUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xqq
 * @Date: 2019/6/22
 * @Description:
 */
@Service("supplyChainUserServiceImpl")
public class SupplyChainUserServiceImpl implements ISupplyChainUserService {

    @Autowired
    private SupplyChainUserMapper mapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private INewSupplierService supplierService;
    @Autowired
    private ISellerUserService sellerUserService;
    @Autowired
    private UserFinanceInitialization financeInitialization;
    @Autowired
    private RemoteSupplierService remoteSupplierService;
    /**
     * 添加供应链公司
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(SupplyChainUserDTO dto) {
        SupplyChainUser userDO=new SupplyChainUser();
        BeanUtils.copyProperties(dto,userDO);
        userDO.setStatus(UserStatusEnum.ACTIVATE.getStatus());
        userDO.setUpdateBy(dto.getCreateBy());
        userDO.setCreateTime(new Date());
        userDO.setRegArea(dto.getRegArea());
        userDO.setUpdateTime(userDO.getCreateTime());
        userDO.setVersion(1);
        Integer result=this.mapper.insert(userDO);
        if (result>0){
            Object object=this.financeInitialization.addAccount(userDO.getId(),userDO.getCompanyName(),dto.getUserName(),userDO.getPhone(),StringUtils.isEmpty(userDO.getRegArea())?" ":userDO.getRegArea(),StringUtils.isEmpty(userDO.getAddress())?" ":userDO.getRegAddress());
            if (object==null){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.finance.service.error");
            }
            JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object));
            if (!jsonObject.getBoolean("data")){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),jsonObject.getString("msg"));
            }
        }
        return result;
    }

    /**
     * 根据id获取供应链公司
     *
     * @param id
     * @return
     */
    @Override
    public SupplyChainUserDTO get(Integer id) {
        String key=SUPPLY_CHINA_USER_ID+id;
        ValueOperations operations = this.redisTemplate.opsForValue();
        SupplyChainUserDTO result=(SupplyChainUserDTO)operations.get(key);
        if (result==null){
            SupplyChainUser userDO=this.mapper.selectByPrimaryKey(id.longValue());
            if (userDO==null){
                return result;
            }
            result=new SupplyChainUserDTO();
            BeanUtils.copyProperties(userDO,result);
            operations.set(key,result,90L, TimeUnit.DAYS);
        }
        return result;
    }

    /**
     * 修改供应链公司
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(SupplyChainUserDTO dto) {
        SupplyChainUserDTO oldDTO=this.get(dto.getId());
        if (oldDTO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.update.object.not.exist");
        }
        SupplyChainUser userDO=new SupplyChainUser();
        BeanUtils.copyProperties(dto,userDO);
        userDO.setStatus(UserStatusEnum.ACTIVATE.getStatus());
        userDO.setUpdateTime(new Date());
        if (this.mapper.updateByPrimaryKeySelective(userDO)>0){
            Object object=this.financeInitialization.updateAccountInfo(userDO.getId(),userDO.getCompanyName(),dto.getUserName(),userDO.getPhone(),StringUtils.isEmpty(userDO.getRegArea())?" ":userDO.getRegArea(),StringUtils.isEmpty(userDO.getAddress())?" ":userDO.getRegAddress());
            if (object==null){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.finance.service.error");
            }
            JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object));
            if (!jsonObject.getBoolean("data")){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),jsonObject.getString("msg"));
            }
            this.clearCache(dto.getId(),dto.getBindType());
            this.clearCache(null,oldDTO.getBindType());
        }
        return 1;
    }

    /**
     * 分页查询供应链公司
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<SupplyChainUserPageDTO> getsPage(QuerySuppluChinaDTO dto) {
        PageHelper.startPage(dto.getCurrentPage(),dto.getPageSize());
        List<SupplyChainUser> list = this.mapper.getsPage(dto.getDateType(),dto.getStartTime(),dto.getEndTime(),dto.getCompanyName());
        PageInfo<SupplyChainUser> pageInfo=new PageInfo<>(list);
        PageDTO<SupplyChainUserPageDTO> result=new PageDTO<>(pageInfo.getTotal(),dto.getCurrentPage().longValue());
        List<SupplyChainUserPageDTO> dataList=new ArrayList<>();
        for (SupplyChainUser userDO: pageInfo.getList()) {
            SupplyChainUserPageDTO pageDTO=new SupplyChainUserPageDTO();
            BeanUtils.copyProperties(userDO,pageDTO);
            pageDTO.setBindSupplier(this.supplierService.getTotalAccount(userDO.getId()));
            pageDTO.setBindSeller(this.sellerUserService.getTotalAccount(userDO.getId()));
            Object object=this.remoteSupplierService.getBindService(userDO.getId());
            JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object));
            pageDTO.setBindWarehouse(jsonObject.getInteger("data"));
            dataList.add(pageDTO);
        }
        result.setList(dataList);
        return result;
    }

    /**
     * 根据平台类型获取绑定供应链公司列表
     *
     * @param type
     * @return
     */
    @Override
    public List<KeyValueDTO> getsSelect(Integer type) {
        String key=SUPPLY_CHINA_USER_BIDN_TYPE+type;
        ListOperations operations=this.redisTemplate.opsForList();
        List<KeyValueDTO> result=operations.range(key,0,-1);
        if (CollectionUtils.isEmpty(result)){
            List<SupplyChainUser> list=this.mapper.getAll(UserStatusEnum.ACTIVATE.getStatus());
            if (CollectionUtils.isEmpty(list)){
                return null;
            }
            result=new ArrayList<>();
            for (SupplyChainUser userDO:list) {
                if (StringUtils.isNotEmpty(userDO.getBindType())){
                    if (!JSONObject.parseArray(userDO.getBindType()).contains(type.toString())){
                        continue;
                    }
                }
                result.add(new KeyValueDTO(userDO.getId().toString(),userDO.getCompanyName()));
            }
            operations.leftPush(key,result);
            this.redisTemplate.expire(key,90L,TimeUnit.DAYS);
        }
        return result;
    }

    /**
     * 清楚缓存
     * @param id
     * @param bindType
     */
    private void clearCache(Integer id,String bindType){
        String key=SUPPLY_CHINA_USER_ID+id;
        this.redisTemplate.delete(key);
        if (StringUtils.isNotEmpty(bindType)){
            JSONArray array=JSONObject.parseArray(bindType);
            for (int i = 0; i < array.size(); i++) {
                String bindKey=SUPPLY_CHINA_USER_BIDN_TYPE+Integer.valueOf(array.getString(i));
                this.redisTemplate.delete(bindKey);
            }
        }
    }
}
