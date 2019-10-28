package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrderDetail;
import com.rondaful.cloud.order.model.dto.syncorder.UpdateSourceOrderDetailDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AmazonOrderDetailMapper extends BaseMapper<AmazonOrderDetail> {
    /*根据订单ID查询订单项信息*/
    List<AmazonOrderDetail> selectAmazonOrderDetail(String orderId);

    /*根据订单项ID查询订单项信息*/
    AmazonOrderDetail selectAmazonOrderItem(@Param("amazonOrderitemId")String amazonOrderitemId);

    /*批量插入  wujiachuang*/
    int insertBulk(List<AmazonOrderDetail> list);

    List<String> selectAmazonItemASINList(@Param("plSellerAccount") String plSellerAccount,@Param("marketPlaceId") String marketPlaceId,@Param("time") String time);

    int updatePlProcessStatus(@Param("converSysDetailStatus") Byte converSysDetailStatus,@Param("sourceOrderLineItemId")
            String sourceOrderLineItemId);

    /**
     * 根据SKU集合查询待转入或转入失败的亚马逊订单项集合 wujiachuang
     * @param platformSKUList  平台SKU集合
     * @return
     */
    List<AmazonOrderDetail> selectAmazonOrderDetailBySkus(@Param("platformSKUList") List<String> platformSKUList);

    /**
     * 批量更改亚马逊订单项转入状态wujiachuang
     * @param list
     */
    void updateConvertStatusStatusBatch(@Param("list") List<UpdateSourceOrderDetailDTO> list);


    /**
     * 根据amazon订单项ID批量查询AmazonOrderDetail对象集合_ZJL（回传平台发货信息用）
     * @param orderLineItemIdList
     * @return
     */
    List<AmazonOrderDetail> queryBatchAmazonOrderDetailByOrderLineItemId(List<String> orderLineItemIdList);

    //根据订单ID和订单项ID更改商品发货标记状态(发货成功)wujiachuang
    void updateMarkStatusSuccessful(@Param("orderId") String orderId,@Param("orderItemId") String orderItemId);

    //根据订单ID和订单项ID更改商品发货标记状态（发货失败）wujiachuang
    void updateMarkStatusFail(@Param("orderId") String orderId,@Param("orderItemId") String orderItemId);

    /**
     * 根据订单项ID更新发货信息
     * @param detail
     */
    void updateDeliverInfoByAmazonOrderItemId(AmazonOrderDetail detail);


}