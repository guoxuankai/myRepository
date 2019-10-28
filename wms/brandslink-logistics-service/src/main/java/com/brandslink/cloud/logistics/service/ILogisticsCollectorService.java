package com.brandslink.cloud.logistics.service;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.logistics.entity.centre.CollectorVo;
import com.brandslink.cloud.logistics.model.CollectorMethodModel;
import com.brandslink.cloud.logistics.model.LogisticsCollectorModel;

public interface ILogisticsCollectorService extends BaseService<LogisticsCollectorModel> {

    Long editCollector(LogisticsCollectorModel collectorModel);

    Page<CollectorMethodModel> selectMethodListByCollectorId(Long collectorId);

    /**
     * 根据物流商code和邮寄方式code获得揽收商信息
     *
     * @param logisticsCode 物流商code
     * @param methodCode    邮寄方式code
     * @param warehouseCode    仓库code
     * @return
     */
    CollectorVo getByCode(String logisticsCode, String methodCode, String warehouseCode);

    Page<LogisticsCollectorModel> selectCollector(LogisticsCollectorModel collectorModel);
}
