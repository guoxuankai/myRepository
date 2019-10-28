package com.rondaful.cloud.order.service.aliexpress;

import com.rondaful.cloud.order.entity.aliexpress.AliExpressOrderInfoDTO;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrderChild;
import com.rondaful.cloud.order.model.PageDTO;
import com.rondaful.cloud.order.model.aliexpress.dto.ChildOrderDTO;
import com.rondaful.cloud.order.model.aliexpress.request.QueryPageDTO;
import com.rondaful.cloud.order.model.aliexpress.response.OrderDTO;
import com.rondaful.cloud.order.model.aliexpress.response.OrderExportDTO;
import com.rondaful.cloud.order.model.aliexpress.response.OrderOtherDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/3
 * @Description:
 */
public interface IAliexpressOrderService {

    /**
     * 手动初始化数据
     *
     * @param createTime
     * @param endTime
     * @param loginId    为空及初始化全部
     */
    void initData(String createTime, String endTime, String loginId);

    /**
     * 分页查询速卖通订单
     *
     * @param dto
     * @return
     */
    PageDTO<OrderDTO> getPage(QueryPageDTO dto);

    /**
     * 导出订单
     *
     * @param dto
     * @return
     */
    PageDTO<OrderExportDTO> export(QueryPageDTO dto);

    /**
     * 根据订单获取物流  金钱相关信息
     *
     * @param orderId
     * @return
     */
    OrderOtherDTO getOrderData(String orderId);

    /**
     * 根据订单号转入系统订单
     *
     * @param orderIds
     * @return
     */
    Integer toSysOrder(List<String> orderIds);

    /**
     * 异步同步订单
     *
     * @param paramsData
     * @return
     */
    void asyncOrder(String paramsData);

    /**
     * 订单监听
     *
     * @param orderId
     * @param orderStatus
     * @return
     */
    Integer syncOrder(String orderId, String orderStatus, String loginId);

    /**
     * 根据父订单号获取子订单列表
     *
     * @param orderId
     * @return
     */
    List<AliexpressOrderChild> getChilds(String orderId);

    /**
     * 订单物流回传
     *
     * @param logisticsOrderId
     * @param serviceName
     * @param orderId
     * @param token
     * @param sendType
     */
    void callBack(String logisticsOrderId, String serviceName, String orderId, String token, String sendType);

    /**
     * 更改sku绑定
     *
     * @param list
     * @return
     */
    Integer updatesSku(List<AliexpressOrderChild> list, List<String> unList);

    /**
     * 根据子订单号查询订单信息
     *
     * @param orderId
     * @return
     */
    ChildOrderDTO getByChild(String orderId);


    /**
     * 获取AliExpress订单新
     *
     * @param orderId 速卖通订单号
     * @return {@link AliExpressOrderInfoDTO}
     */
    AliExpressOrderInfoDTO findAliExpressOrderByOrderId(String orderId);
}
