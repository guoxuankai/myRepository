package com.rondaful.cloud.transorder.service;

import com.rondaful.cloud.transorder.entity.system.SysOrder;
import com.rondaful.cloud.transorder.entity.system.SysOrderDTO;

import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/9/21 9:33
 */
public interface AliexpressOrderService {

    List<SysOrderDTO> assembleData(List<String> orderIds);

}
