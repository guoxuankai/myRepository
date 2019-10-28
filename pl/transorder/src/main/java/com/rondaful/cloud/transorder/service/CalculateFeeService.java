package com.rondaful.cloud.transorder.service;

import com.rondaful.cloud.transorder.entity.system.SysOrderDTO;

/**
 * @author guoxuankai
 * @date 2019/10/8 17:32
 */
public interface CalculateFeeService {

    void calculateFee(SysOrderDTO sysOrderDTO);

}
