package com.rondaful.cloud.order.service;

import com.rondaful.cloud.order.entity.goodcang.GoodCangOrder;
import com.rondaful.cloud.order.entity.goodcang.GoodCangSubscibe.GoodCangAccepDto;

public interface IGoodCangService {

    /**
     * 同步谷仓订单数据
     * @return
     * @throws Exception
     */
    void getGoodCangOrderList() throws Exception;


    /**
     * 推送订单至谷仓
     * @param goodCangOrder
     */
    String deliverGoodToGoodCang(GoodCangOrder goodCangOrder) throws Exception;

    /**
     *
     * @return
     * @throws Exception
     */
    void acceptOrderList(GoodCangAccepDto goodCangAccepDto) throws Exception;

   /* *//**
     * 取消谷仓仓库订单
     * @param sysOrder
     *//*
    void cancelGoodCangOrder(SysOrder sysOrder, String cancelReason) throws Exception;*/

    /**
     * 根据谷仓订单code查询订单信息 并对比数据库更新信息
     * @param orderCode
     * @return
     * @throws Exception
     */
    void getOrderByCodeAndUpdateStatus(String orderCode,String referenceNo) throws Exception;

    /**
     * 接收谷仓订单异常推送
     * @param goodCangAccepDto
     * @throws Exception
     */
    void getAcceptAbnormalOrderList(GoodCangAccepDto goodCangAccepDto) throws Exception;

    /**
     * 谷仓API推送订阅，接收谷仓推送过来的数据
     * @param dto
     */
    void goodCangAPISubscribe(GoodCangAccepDto dto) throws Exception;


    /**
     * 判断是否需要调用订单回传接口
     * @param trackId
     * @param orderStatus
     * @return
     */
    Boolean isUpdateSysLog(String trackId, String orderStatus,String shipTrackNumber,String shipOrderId,String warehouseType);
}
