package com.rondaful.cloud.order.mapper.aliexpress;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrderChild;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliexpressOrderChildMapper extends BaseMapper<AliexpressOrderChild> {

    /**
     * 批量插入子订单
     * @param list
     * @return
     */
    Integer inserts(List<AliexpressOrderChild> list);

    /**
     * 根据父级订单号查询子订单
     * @param parentId
     * @return
     */
    List<AliexpressOrderChild> getByParentId(@Param("parentId") String parentId);

    /**
     * 批量更改状态
     * @param list
     * @return
     */
    Integer updateBatchStatus(List<AliexpressOrderChild> list);

    /**
     * 批量更改物流发货
     * @param child
     * @return
     */
    Integer updateBatchLog(AliexpressOrderChild child);

    /**
     * 根据父级订单号修改状态
     * @param list
     * @return
     */
    Integer updateStatusByParentId(List<String> list);

    /**
     * 批量手动映射sku
     * @param list
     * @return
     */
    Integer handInsertSku(@Param("list") List<AliexpressOrderChild> list);

    /**
     * 根据sku查询父订单
     * @param list
     * @return
     */
    List<String> getPOrderId(@Param("list") List<AliexpressOrderChild> list);

    /**
     * 批量解绑sku
     * @param skus
     * @return
     */
    Integer handUntieSku(@Param("skus") List<String> skus);

    /**
     * 根据订单项ID批量查询速卖通订单项集合
     * @param sourceOrderLineItemIdList
     * @return
     */
    List<AliexpressOrderChild> queryBatchAliexpressDetailByOrderItemId(List<String> sourceOrderLineItemIdList);

    /**
     * 根据子订单号查询订单
     * @param orderId
     * @return
     */
    AliexpressOrderChild getByOrderId(@Param("orderId") String orderId);


    /**
     * 根据订单号获取所有的子订单
     * @param orderId orderId
     * @return {@link List<AliexpressOrderChild>}
     */
    List<AliexpressOrderChild> getOrderChildListByOrderId(@Param("orderId") String orderId);
}