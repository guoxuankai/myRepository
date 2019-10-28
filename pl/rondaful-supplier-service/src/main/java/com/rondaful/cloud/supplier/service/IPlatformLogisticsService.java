package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.supplier.entity.Logistics.ErpProviderLogistics;

public interface IPlatformLogisticsService {


    /**
    * @Description 提供erp 获取ebay授权信息
    * @Author  xieyanbin
    * @Param  packageId 包裹id
    * @Return ErpProviderLogistics
    * @Exception
    *
    */
    ErpProviderLogistics getEdis(String packageId);


    /**
    * @Description 提供erp速卖通授权信息
    * @Author  xieyanbin
    * @Param  packageId 包裹id
    * @Return      ErpProviderLogistics
    * @Exception
    *
    */
    ErpProviderLogistics getAliExpress(String packageId);

}
