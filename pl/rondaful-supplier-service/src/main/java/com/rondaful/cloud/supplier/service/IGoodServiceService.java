package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.supplier.entity.basics.WarehouseList;
import com.rondaful.cloud.supplier.model.dto.logistics.GQueryDeliveryFeeDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.TranLogisticsCostDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.TranLogisticsDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/9/2
 * @Description:
 */
public interface IGoodServiceService {

    /**
     * 获取谷仓的仓库列表
     * @param appkey
     * @param appToken
     * @return
     */
    List<WarehouseList> getsWarehouseList(String appkey, String appToken,Integer firmId);

    /**
     * 获取所有物流方式
     * @param appkey
     * @param appToken
     * @return
     */
    List<TranLogisticsDTO> getsLogistics(String appkey, String appToken,String warehouseCode);

    /**
     * 运费试算
     * @param appkey
     * @param appToken
     * @param dto
     * @return
     */
    List<TranLogisticsCostDTO> getCalculateFee(String appkey, String appToken, GQueryDeliveryFeeDTO dto);


}
