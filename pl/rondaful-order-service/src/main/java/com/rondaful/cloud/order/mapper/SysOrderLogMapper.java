package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.SysOrderLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysOrderLogMapper extends BaseMapper<SysOrderLog> {
    List<SysOrderLog> selectSysOrderLogByOrderId(String sysOrderId);

    /**
     * 批量插入系统转单日志
     * @param list
     * @return
     */
    Integer inserts(List<SysOrderLog> list);

    /**
     * 根据订单id查询订单日志信息XD
     * @param orderId
     * @return
     */
    SysOrderLog getOrderLogByOrderId (String orderId);

    SysOrderLog findSysOrderLogByMessage(@Param("sysOrderId") String sysOrderId,
                                         @Param("content") String content);
}
