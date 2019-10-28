package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDetailDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysOrderDetailMapper extends BaseMapper<SysOrderDetail> {
    /**
     * 根据订单ID查出订单项集合 wujiachuang
     *
     * @param orderId 订单ID
     * @return
     */
    List<SysOrderDetail> querySysOrderDetailByOrderId(String orderId);

    /**
     * 根据订单ID查出订单项集合 lijiantao
     *
     * @param orderId 订单ID
     * @return
     */
    List<SysOrderDetail> querySysOrderDetailCoantainAllSkuByOrderId(String orderId);

    /**
     * 根据订单ID和sku查出订单项集合 wujiachuang
     *
     * @param sysOrderId 订单ID
     * @return
     */
    SysOrderDetail querySysOrderDetailByOrderIdAndSku(@Param("sysOrderId") String sysOrderId,@Param("sku") String sku);

    /**
     * 批量插入系统订单项集合_ZJL
     *
     * @param persistOrderDetails
     */
    void insertBatch(List<SysOrderDetail> persistOrderDetails);

    /**
     * 批量更新订单项信息_WJC
     *
     * @param sysOrderDetails
     * @return
     */
    int updateBatch(List<SysOrderDetail> sysOrderDetails);

    /**
     * 根据系统订单ID批量删除订单项_ZJL
     *
     * @param collect
     * @return
     */
    int deleteBatchBySysOrderId(List<String> collect);


    /**
     * 更新系统订单项数据
     *
     * @param sysOrderDetail
     * @return
     */
    int updateBySysOrderDetailId(SysOrderDetail sysOrderDetail);


    /**
     * 通过系统订单ID查询订单详情
     *
     * @param sysOrderId 订单ID
     * @return
     */
    List<SysOrderDetail> selectOrderDetailBySysOrderId(String sysOrderId);

    /**
     * 更改订单项为售后状态
     *
     * @param sysOrderId
     */
    void updateOrderItemAfterStatus(@Param("sysOrderId") String sysOrderId, @Param("sku") String sku, @Param("status") Byte status);

    /**
     * 批量插入
     *
     * @param sysOrderDetailDTOList {@link List<SysOrderDetailDTO>}
     */
    void insertBatchSelective(@Param("list") List<SysOrderDetailDTO> sysOrderDetailDTOList);

    /**
     * 根据SKU查询订单ID集合 wujiachuang
     * @param sku
     * @return
     */
    List<String> querySysOrderIdListBySku(String sku);
}