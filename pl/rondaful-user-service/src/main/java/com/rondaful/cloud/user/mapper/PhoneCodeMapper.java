package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.PhoneCode;

import java.util.List;

public interface PhoneCodeMapper extends BaseMapper<PhoneCode> {

    /**
     * 获取所有区号
     * @return
     */
    List<PhoneCode> getsAll();
}