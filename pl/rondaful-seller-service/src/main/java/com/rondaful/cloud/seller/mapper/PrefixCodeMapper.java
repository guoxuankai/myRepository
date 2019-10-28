package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.PrefixCode;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author guoxuankai
 * @date 2019/6/10
 */
public interface PrefixCodeMapper extends BaseMapper<PrefixCode> {

    PrefixCode getByCode(String code);

    int getUpcAmout();
}