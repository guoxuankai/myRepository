package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.supplier.entity.basics.WarehouseList;
import com.rondaful.cloud.supplier.model.dto.logistics.EQueryDeliveryFeeDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.LogisticsSelectDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.TranLogisticsCostDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.TranLogisticsDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/9/2
 * @Description:
 */
public interface IErpServiceService {

    /**
     * 获取erp的仓库列表
     * @return
     */
    List<WarehouseList> getsWarehouseList(Integer firmId);

    /**
     * 获取物流方式
     * @param warehouseType  1-本地仓   7-谷仓
     * @return
     */
    List<TranLogisticsDTO> getsLogistics(Integer warehouseType);

    /**
     * 运费试算
     * @param dto
     * @return
     */
    List<TranLogisticsCostDTO> getCalculateFee(EQueryDeliveryFeeDTO dto);


}
