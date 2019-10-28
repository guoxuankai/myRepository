package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.RateMessage;

public interface RateMessageMapper extends BaseMapper<RateMessage> {

    /**
     * 查询最后一条数据
     * @return 汇率数据
     */
    RateMessage findLastRate();

    void updateByPrimaryKeyWithBLOBs(RateMessage rateMessage);

}