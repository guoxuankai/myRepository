package com.rondaful.cloud.transorder.service;

import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.transorder.entity.ebay.EbayOrder;
import com.rondaful.cloud.transorder.entity.system.SysOrder;
import com.rondaful.cloud.transorder.entity.system.SysOrderDTO;

import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/9/21 15:14
 */
public interface EbayOrderService extends BaseService<EbayOrder> {

    List<SysOrderDTO> assembleData(List<String> orderIds);

}
