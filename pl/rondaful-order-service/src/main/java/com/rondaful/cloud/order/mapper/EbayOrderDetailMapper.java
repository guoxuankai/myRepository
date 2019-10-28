package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.eBay.EbayOrderDetail;
import com.rondaful.cloud.order.model.dto.syncorder.UpdateSourceOrderDetailDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EbayOrderDetailMapper extends BaseMapper<EbayOrderDetail> {
    /**
     * 更新ebay订单项表状态
     *
     * @param ebayOrderDetail
     */
    int updateConverStatusByOrderItemId(EbayOrderDetail ebayOrderDetail);

    /**
     * 根据平台订单号orderId查询其下所有订单项的状态
     *
     * @param orderId
     * @return
     */
    List<EbayOrderDetail> selectDetailStausByOrderId(String orderId);

    /**
     * 根据订单项ID批量查询订单项对象集合
     *
     * @param orderLineItemIdList
     * @return
     */
    List<EbayOrderDetail> queryBatchEbayOrderDetailByOrderLineItemId(List<String> orderLineItemIdList);

    /**
     * 根据订单项ID批量更新shipment_tracking_number跟踪号,delivery_time发货时间,mark_deliver_status标记ebay平台发货状态
     * shipping_carrier_used物流商名称
     *
     * @param ebayOrderDetail
     */
    int updateMarkDeliverStatusByOrderItemId(EbayOrderDetail ebayOrderDetail);

    /**
     * 批量更新ebay订单项转换状态
     * @param list {@link List<UpdateSourceOrderDetailDTO>}
     */
    void updateConvertStatusStatusBatch(@Param("list") List<UpdateSourceOrderDetailDTO> list);

    /**
     * 根据订单项ID查找商品最迟发货时间
     * @param orderLineItemId
     * @return
     */
    String selectDeliverDeadLineByOrderLineItemId(String orderLineItemId);
}