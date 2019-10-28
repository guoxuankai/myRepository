package com.rondaful.cloud.order.service;

import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrderDetail;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;

import java.util.List;

public interface IAmazonOrderItemService extends BaseService<AmazonOrderDetail> {
//    List<AmazonOrderDetail> GetOrderItemsByOrderIdAndInsertDb(List<AmazonOrder> list,String sellerId,String mwsAuthToken,String markerPlaceId,String plAccount)
//            throws Exception;

    List<String> selectAmazonItemASINList(String plAccount,String marketPlaceId,String time);


    void updatePlProcessStatus(Byte converSysDetailStatus, String sourceOrderLineItemId);


    List<SysOrderDTO> getPendingConverAmazonListByCondition(List<String> platformSKUList);
}
