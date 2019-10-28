package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.model.dto.syncorder.PreCovertEbayOrderDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface EbayOrderMapper extends BaseMapper<EbayOrder> {

    /**
     * 根据修改开始时间和结束时间查询这段时间内变动的订单
     *
     * @param map
     * @return
     */
    List<SysOrder> selectEbayOrderInterval(Map<String, String> map);

    /**
     * 根据平台orderId查出转系统订单数据，直接用SysOrder接收_ZJL
     *
     * @param ebayOrder
     */
    SysOrder selectConverDataSysOrderByOrderId(EbayOrder ebayOrder);


    PreCovertEbayOrderDTO selectConvertDataSysOrderByOrderId(EbayOrder ebayOrder);


    PreCovertEbayOrderDTO MappingConvertDataSysOrderByOrderId(String orderId);

    /**
     * 查询eBay订单列表
     *
     * @param map
     */
    List<EbayOrder> queryEbayOrderList(Map<String, Object> map);


    /**
     * 根据订单号orderid查询订单详情
     *
     * @param orderId
     * @return
     */
    EbayOrder queryEbayOrderDetail(String orderId);


    /**
     * 根据ebay平台订单号批量查询ebay订单对象集合
     *
     * @param list
     */
    List<EbayOrder> selectBatchSysOrderByOrderId(List<String> list);

    /**
     * 根据条件查询待转化的ebay订单集合_ZJL
     *
     * @param empowerID
     * @param platformSku
     * @return
     */
    List<SysOrder> getPendingConverEbayBySKU(@Param("empowerID") Integer empowerID, @Param("platformSku") String platformSku);

    /**
     * 根据条件查询待转化的ebay订单集合_ZJL
     *
     * @param empowerID
     * @param platformSku
     * @return
     */
    List<SysOrder> getPendingConverEbayByVariationSKU(@Param("empowerID") Integer empowerID, @Param("platformSku") String platformSku);

    /**
     * 根据ebay订单ID列表查询不在列表展示的订单
     *
     * @param ebayOrderIdList ebay订单ID列表
     * @return List<String>
     */
    List<String> getNoShowEbayOrderIds(@Param("ebayOrderIdList") List<String> ebayOrderIdList);

    /**
     * 不在ebay订单列表展示的订单变为展示订单
     *
     * @param ebayNoShowOrderIds 不展示的ebay订单id列表
     */
    void updateNoShowOrderToShow(@Param("ebayNoShowOrderIds") List<String> ebayNoShowOrderIds);

    /**
     * 在ebay订单列表展示的订单变为不展示订单
     *
     * @param ebayNoShowOrderIds 不展示的ebay订单id列表
     */
    void updateShowOrderToNoShow(@Param("ebayNoShowOrderIds") List<String> ebayNoShowOrderIds);

    /**
     * 获取转换失败的ebay订单
     *
     * @return {@link List<EbayOrder>}
     */
    List<EbayOrder> selectCovertFailEbayOrder();

//    /**
//     * 获取准备转化的ebay订单
//     *
//     * @return {@link List<EbayOrder>}
//     */
//    List<PreCovertEbayOrderDTO> selectPreConvertEbayOrder(@Param("startTime") String startTime,
//                                                          @Param("endTime") String endTime);
    /**
     * 获取准备转化的ebay订单
     *
     * @return {@link List<EbayOrder>}
     */
    List<PreCovertEbayOrderDTO> selectPreConvertEbayOrder();

    List<PreCovertEbayOrderDTO> getPendingConvertEbayBySKU(@Param("empowerID") Integer empowerID,
                                                           @Param("platformSku") String sku);

    List<PreCovertEbayOrderDTO> getPendingConvertEbayByVariationSKU(@Param("empowerID") Integer empowerID,
                                                                    @Param("platformSku") String sku);
}