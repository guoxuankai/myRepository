package com.rondaful.cloud.order.service;

import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.model.dto.wms.WmsOrderDTO;

import java.util.List;

/**
 * @author Blade
 * @date 2019-08-09 10:10:26
 **/
public interface IWmsService {

    /**
     * wms发货
     * @param wmsOrderDTO
     * @param warehouseId
     */
    public void createWmsOrder(WmsOrderDTO wmsOrderDTO, String warehouseId);

    /**
     * 组装wms订单数据
     * @param sysOrderNew
     * @param sysOrderPackage
     * @param sysOrderDetailList
     * @return
     */
    public WmsOrderDTO assembleWmsOrderDate(SysOrderNew sysOrderNew,
                                             SysOrderPackage sysOrderPackage,
                                             List<SysOrderDetail> sysOrderDetailList);


    public void cancelWmsOrder();

    /**
     * 查询wms包裹状态
     */
    public void getSysOrderWMSSpeedInfo() throws Exception;


}
