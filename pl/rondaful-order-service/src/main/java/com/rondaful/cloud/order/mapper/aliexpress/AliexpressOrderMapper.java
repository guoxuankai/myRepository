package com.rondaful.cloud.order.mapper.aliexpress;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrder;
import com.rondaful.cloud.order.model.aliexpress.request.QueryPageDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AliexpressOrderMapper extends BaseMapper<AliexpressOrder> {

    /**
     * 根据时间分页查询
     * @param dto
     * @return
     */
    List<AliexpressOrder> pageByTime(QueryPageDTO dto);

    /**
     * 根据id批量查询
     * @param orderIds
     * @return
     */
    List<AliexpressOrder> getsByOrderId(List<String> orderIds);

    /**
     * 批量更新同步状态
     * @param map
     * @return
     */
    Integer updateBatchStatus(@Param("map") Map<String,Byte> map);

    /**
     * 批量插入
     * @param list
     * @return
     */
    Integer inserts(List<AliexpressOrder> list);

    /**
     * 根据订单号查找
     * @param orderId
     * @return
     */
    AliexpressOrder getByOrderId(String orderId);

    /**
     * 根据订单id修改订单状态
     * @param order
     * @return
     */
    Integer updateByOrderId(AliexpressOrder order);

    /**
     * 根据条件查询待转化的Aliexpress订单集合_ZJL
     * @param empowerID
     * @param platformSKUList
     * @return
     */
    List<SysOrder> getPendingConverAliListByCondition(@Param("empowerID") Integer empowerID, @Param("platformSKUList") List<String> platformSKUList);

}