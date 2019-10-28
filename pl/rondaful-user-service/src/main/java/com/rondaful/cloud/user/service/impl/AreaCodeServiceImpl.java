package com.rondaful.cloud.user.service.impl;

import com.rondaful.cloud.user.entity.AreaCode;
import com.rondaful.cloud.user.entity.PhoneCode;
import com.rondaful.cloud.user.mapper.AreaCodeMapper;
import com.rondaful.cloud.user.mapper.PhoneCodeMapper;
import com.rondaful.cloud.user.model.dto.area.AreaCodeDTO;
import com.rondaful.cloud.user.model.dto.area.PhoneCodeDTO;
import com.rondaful.cloud.user.service.IAreaCodeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xqq
 * @Date: 2019/4/25
 * @Description:
 */
@Service("areaCodeServiceImpl")
public class AreaCodeServiceImpl implements IAreaCodeService {
    private Logger logger = LoggerFactory.getLogger(AreaCodeServiceImpl.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AreaCodeMapper mapper;
    @Autowired
    private PhoneCodeMapper codeMapper;


    /**
     * 获取行政区域组织树
     *
     * @return
     */
    @Override
    public List<AreaCodeDTO>  getTre(String languageTypeType) {
        logger.info("获取行政区域组织树");
        ListOperations operations=this.redisTemplate.opsForList();
        String key=AREA_CODE_KEY+languageTypeType;
        List<AreaCodeDTO> result=operations.range(key,0,1);
        if (CollectionUtils.isEmpty(result)){
            List<AreaCode> codeList=this.mapper.getsAll();
            List<AreaCodeDTO> list=new ArrayList<>(codeList.size());
            for (AreaCode areaCode:codeList) {
                AreaCodeDTO dto=new AreaCodeDTO();
                BeanUtils.copyProperties(areaCode,dto);
                if (StringUtils.isNotEmpty(languageTypeType)){
                    dto.setName(areaCode.getNameEn());
                }
                list.add(dto);
            }
            list.forEach(m -> {
                if (m.getParentId() == 0){
                    result.add(m);
                }
                list.forEach(mm -> {
                    if (mm.getParentId().equals(m.getId())) {
                        if (CollectionUtils.isEmpty(m.getChildList())) {
                            m.setChildList(new ArrayList<>());
                        }
                        m.getChildList().add(mm);
                    }
                });
            });
        }
        return result;
    }

    /**
     * 根据id获取区域名
     *
     * @param id
     * @return
     */
    @Override
    public String getName(Integer id,String languageTypeType) {
        AreaCode areaCode=this.mapper.selectByPrimaryKey(id.longValue());
        if (areaCode==null){
            return null;
        }
        return StringUtils.isEmpty(languageTypeType)?areaCode.getName():areaCode.getNameEn();
    }

    /**
     * 根据区号获取国家名
     *
     * @param code
     * @param languageType
     * @return
     */
    @Override
    public String getNameByCode(String code, String languageType,Integer level) {
        AreaCode areaCode=this.mapper.getNameByCode(code,level);
        if (areaCode==null){
            return null;
        }
        return StringUtils.isEmpty(languageType)?areaCode.getName():areaCode.getNameEn();
    }

    /**
     * 获取手机区号
     *
     * @param languageType
     * @return
     */
    @Override
    public List<PhoneCodeDTO> getsPhoneCode(String languageType) {
        String key=PHONT_CODE_KEY+languageType;
        ListOperations operations=this.redisTemplate.opsForList();
        List<PhoneCodeDTO> result=operations.range(key,0,-1);
        if (CollectionUtils.isEmpty(result)){
            List<PhoneCode> list=this.codeMapper.getsAll();
            result=new ArrayList<>(list.size());
            for (PhoneCode code:list) {
                if (StringUtils.isEmpty(languageType)){
                    result.add(new PhoneCodeDTO(code.getPhoneCode(),code.getCountryName()));
                }else {
                    result.add(new PhoneCodeDTO(code.getPhoneCode(),code.getCountryNameEn()));
                }
            }
            operations.leftPushAll(key,result);this.redisTemplate.expire(key,60L, TimeUnit.DAYS);
        }
        return result;
    }

    @Override
    public Integer update() {
        return null;
    }

    @Override
    public List<AreaCode> getsByLevel(Integer level, Integer parentId) {
        return mapper.getsByLevel(level,parentId);
    }

    @Override
    public AreaCode getArea(String countryName, String countryCode) {
        return mapper.getArea(countryName,countryCode);
    }

}
