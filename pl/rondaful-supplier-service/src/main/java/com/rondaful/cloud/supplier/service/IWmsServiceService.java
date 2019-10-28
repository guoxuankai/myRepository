package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.supplier.entity.basics.WarehouseList;
import com.rondaful.cloud.supplier.model.dto.logistics.TranLogisticsCostDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.TranLogisticsDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.WQueryDeliveryFeeDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/9/2
 * @Description:
 */
public interface IWmsServiceService {

    /**
     * 获取wms的仓库列表
     * @param appkey
     * @param appToken
     * @return
     */
    List<WarehouseList> getsWarehouseList(String appkey,String appToken,Integer firmId);



    /**
    * @Description 获取wms的物流列表
    * @Author  xieyanbin
    * @Param  appkey
    * @Param warehouseCode
    * @Return
    * @Exception
    *
    */
    List<TranLogisticsDTO>  getLogisticsList(String appkey, String appToken, String warehouseCode);


    /**
    * @Description 调用wms运费试算
    * @Author  xieyanbin
    * @Param  wQueryDeliveryFeeDTO
    * @Return      List
    * @Exception
    *
    */
    List<TranLogisticsCostDTO> getFreight(String appKey,String appToken,WQueryDeliveryFeeDTO wQueryDeliveryFeeDTO);


}
