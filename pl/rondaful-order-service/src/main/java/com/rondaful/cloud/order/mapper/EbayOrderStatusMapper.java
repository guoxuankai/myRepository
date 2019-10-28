package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.eBay.EbayOrderStatus;
import com.rondaful.cloud.order.model.dto.syncorder.UpdateSourceOrderDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EbayOrderStatusMapper extends BaseMapper<EbayOrderStatus> {
    /**
     * 根据ebay的orderId查询订单最近一次修改时间lastModifiedTime
     *
     * @param orderID
     * @return
     */
    Date selectLastModTimeById(String orderID);

    /**
     * 根据平台订单ID查询此订单转化状态
     *
     * @param orderId
     */
    Byte selectConverStatus(String orderId);

    /**
     * 根据订单号查询最迟发货时间wujiachuang
     *
     * @param orderId
     * @return
     */
    String selectLastShippingTimeByOrderId(String orderId);

    /**
     * 批量更新ebay状态表
     *
     * @param list {@link List<UpdateSourceOrderDTO>}
     */
    void updateConvertStatusBatch(@Param("list") List<UpdateSourceOrderDTO> list);
}