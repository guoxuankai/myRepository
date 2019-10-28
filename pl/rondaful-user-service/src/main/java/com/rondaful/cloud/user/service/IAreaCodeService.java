package com.rondaful.cloud.user.service;

import com.rondaful.cloud.user.entity.AreaCode;
import com.rondaful.cloud.user.model.dto.area.AreaCodeDTO;
import com.rondaful.cloud.user.model.dto.area.PhoneCodeDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/25
 * @Description:
 */
public interface IAreaCodeService {

    public static String AREA_CODE_KEY="area.code.v2";
    public static String PHONT_CODE_KEY="phone.code.v2";


    /**
     * 获取行政区域组织树
     * @return
     */
    List<AreaCodeDTO> getTre(String languageType);

    /**
     * 根据id获取区域名
     * @param id
     * @return
     */
    String getName(Integer id,String languageType);

    /**
     * 根据区号获取国家名
     * @param code
     * @param languageType
     * @return
     */
    String getNameByCode(String code,String languageType,Integer level);

    /**
     * 获取手机区号
     * @param languageType
     * @return
     */
    List<PhoneCodeDTO> getsPhoneCode(String languageType);



    Integer update();

    List<AreaCode> getsByLevel(Integer level, Integer parentId);

    AreaCode  getArea(String countryName, String countryCode);
}
