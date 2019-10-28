package com.rondaful.cloud.supplier.service;

/**
 * @Author: xqq
 * @Date: 2019/8/2
 * @Description:
 */
public interface IMessageService {

    /**
     * 库存变更通知
     * @param warehouseId
     * @param supplierSku
     */
    void sendInventory(Integer warehouseId,String supplierSku);
}
