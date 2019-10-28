package com.rondaful.cloud.user.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.third.AppDTO;
import com.rondaful.cloud.common.entity.third.BindAccountDTO;
import com.rondaful.cloud.common.entity.third.FrequencyDTO;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.user.entity.ThirdApp;
import com.rondaful.cloud.user.entity.UserOrg;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.enums.UserStatusEnum;
import com.rondaful.cloud.user.mapper.ThirdAppMapper;
import com.rondaful.cloud.user.mapper.UserOrgMapper;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.service.IThirdAppService;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: xqq
 * @Date: 2019/7/6
 * @Description:
 */

@Service("thirdAppServiceImpl")
public class ThirdAppServiceImpl implements IThirdAppService {
    private Logger logger = LoggerFactory.getLogger(ThirdAppServiceImpl.class);
    @Autowired
    private ThirdAppMapper appMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserOrgMapper userOrgMapper;

    /**
     * 新增应用
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(AppDTO dto) {
        if (this.getByAppKey(dto.getAppKey())!=null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.app.appkey.exits");
        }
        ThirdApp appVo=new ThirdApp();
        dto.setAppToken(UUID.randomUUID().toString());
        BeanUtils.copyProperties(dto,appVo);
        appVo.setFrequencyAstrict(JSONObject.toJSONString(dto.getFrequencyAstricts()));
        appVo.setIp(JSONObject.toJSONString(dto.getIps()));
        if(this.appMapper.insert(appVo)>0) {
            this.insertBinds(dto.getBinds(),appVo.getId());
        	this.updateCache(dto);
        }
        return 1;
    }

    /**
     * 修改应用
     *
     * @param dto
     * @return
     */
    @Override
    public Integer update(AppDTO dto) {
        ThirdApp exits=this.appMapper.selectByPrimaryKey(dto.getId().longValue());
        if (exits==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.update.object.not.exist");
        }
    	ThirdApp appVo=new ThirdApp();
    	BeanUtils.copyProperties(dto,appVo,"appKey");
    	appVo.setId(exits.getId());
        appVo.setAppKey(exits.getAppKey());
        if(this.appMapper.updateByPrimaryKeySelective(appVo)>0) {
            this.userOrgMapper.deleteByUserId(exits.getId(),UserEnum.platformType.APP.getPlatformType());
            this.insertBinds(dto.getBinds(),exits.getId());
            this.updateCache(dto);
        }
       return 1;
    }

    /**
     * 修改状态
     *
     * @param appKey
     * @param status
     * @return
     */
    @Override
    public Integer updateStatus(String appKey, Integer status) {
        AppDTO oldDTO=this.getByAppKey(appKey);
        if (oldDTO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.update.object.not.exist");
        }
        ThirdApp appVo=new ThirdApp();
        appVo.setAppKey(appKey);
        appVo.setStatus(status);
        if(appMapper.updateByPrimaryKeySelective(appVo)>0) {
        	 oldDTO.setStatus(status);
             this.updateCache(oldDTO);
        }
        return 1;
    }

    /**
     * 根据appkey获取授权信息
     *
     * @param appKey
     * @return
     */
    @Override
    public AppDTO getByAppKey(String appKey) {
    	ThirdApp appVo=appMapper.getByAppKey(appKey);
        AppDTO result=this.appToDTO(appVo);
        if (result!=null){
            this.updateCache(result);
        }
        return result;
    }

    /**
     * 分页查询应用授权列表
     *
     * @param currentPage
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageDTO<AppDTO> getsPage(Integer currentPage,Integer pageSize,Integer status) {
        PageHelper.startPage(currentPage,pageSize);
    	List<ThirdApp> listApp=appMapper.getsPage(status);
    	PageInfo<ThirdApp> pageInfo = new PageInfo (listApp);
    	PageDTO<AppDTO> result=new PageDTO<>(pageInfo.getTotal(),currentPage.longValue());
    	 List<AppDTO> dataList=new ArrayList<>();
    	 pageInfo.getList().forEach(appVo -> {
    		 AppDTO dto =this.appToDTO(appVo);
    		 dataList.add(dto);
    		 this.updateCache(dto);
    	 });
    	 result.setList(dataList);
		 return result;
    }

    /**
     * 获取所有有效的第三方应用
     *
     * @return
     */
    @Override
    public List<AppDTO> getsAll() {
        List<ThirdApp> list=this.appMapper.getsPage(UserStatusEnum.ACTIVATE.getStatus());
        List<AppDTO> result=new ArrayList<>(list.size());
        list.forEach( app -> {
            result.add(this.appToDTO(app));
        });
        return result;
    }


    /**
     * 重置密码
     *
     * @param id
     * @return
     */
    @Override
    public Integer resetAppToken(Integer id) {
        ThirdApp exits=this.appMapper.selectByPrimaryKey(id.longValue());
        if (exits==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.update.object.not.exist");
        }
        exits.setAppToken(UUID.randomUUID().toString());
        if (this.appMapper.updateByPrimaryKeySelective(exits)>0){
            this.updateCache(this.appToDTO(exits));
        }
        return 1;
    }

    /**
     * 根据绑定类型获取列表
     *
     * @param bindType
     * @param bindCode
     * @return
     */
    @Override
    public List<AppDTO> getsByBindCode(Integer bindType, String bindCode) {
        List<Integer> appIds=this.userOrgMapper.getsByBindCode(UserEnum.platformType.APP.getPlatformType(),bindType,bindCode);
        if (CollectionUtils.isEmpty(appIds)){
            return null;
        }
        List<ThirdApp> apps=this.appMapper.getsById(appIds);
        if (CollectionUtils.isEmpty(apps)){
            return null;
        }
        List<AppDTO> result=new ArrayList<>();
        apps.forEach(app -> {
            result.add(this.appToDTO(app));
        });
        return result;
    }

    /**
     * 跟新缓存并永久存储据
     * @param dto
     */
    private void updateCache(AppDTO dto){
        String key=AppDTO.APP_KEY_BY_APPKEY+dto.getAppKey();
        this.redisTemplate.delete(key);
        if (UserStatusEnum.ACTIVATE.getStatus().equals(dto.getStatus())){
            ValueOperations operations=this.redisTemplate.opsForValue();
            operations.set(key,dto);
        }
    }


    /**
     * 数据库对象转成dto对象
     * @param app
     * @return
     */
    private AppDTO appToDTO(ThirdApp app){
        AppDTO result=new AppDTO();
        if (app==null){
            return null;
        }
        BeanUtils.copyProperties(app,result);
        if (StringUtils.isNotEmpty(app.getIp())){
            result.setIps(JSONArray.parseArray(app.getIp(),String.class));
        }
        if (StringUtils.isNotEmpty(app.getFrequencyAstrict())){
            result.setFrequencyAstricts(JSONArray.parseArray(app.getFrequencyAstrict(), FrequencyDTO.class));
        }
        List<UserOrg> list=this.userOrgMapper.getAccount(app.getId(),UserEnum.platformType.APP.getPlatformType());
        if (CollectionUtils.isNotEmpty(list)){
            Map<Byte,List<Integer>> map=new HashMap<>();
            for (UserOrg org:list) {
                if (!map.containsKey(org.getBindType())){
                    map.put(org.getBindType(),new ArrayList<>());
                }
                map.get(org.getBindType()).add(Integer.valueOf(org.getBindCode()));
            }
            List<BindAccountDTO> binds=new ArrayList<>();
            map.forEach( (k,v) -> {
                BindAccountDTO dto=new BindAccountDTO();
                dto.setBindType(k.intValue());
                dto.setBindCode(v);
                binds.add(dto);
            } );
            result.setBinds(binds);
        }
        return result;
    }

    /**
     * 插入应用和账号绑定关系
     * @param binds
     * @param userId
     */
    private void insertBinds(List<BindAccountDTO> binds,Integer userId){
        if (CollectionUtils.isNotEmpty(binds)){
            List<UserOrg> list=new ArrayList<>();
            for (BindAccountDTO accountDTO:binds) {
                accountDTO.getBindCode().forEach(account ->{
                    UserOrg org=new UserOrg();
                    org.setUserId(userId);
                    org.setUserPlatform(UserEnum.platformType.APP.getPlatformType().byteValue());
                    org.setBindType(accountDTO.getBindType().byteValue());
                    org.setBindCode(account.toString());
                    list.add(org);
                });
            }
            this.userOrgMapper.insertBatch(list);
        }
    }
}
