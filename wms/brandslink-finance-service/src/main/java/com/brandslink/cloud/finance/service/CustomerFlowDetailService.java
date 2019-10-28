package com.brandslink.cloud.finance.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.finance.pojo.entity.CustomerFlow;
import com.brandslink.cloud.finance.pojo.entity.CustomerFlowDetail;

import javax.servlet.http.HttpServletResponse;

/**
 * @author yangzefei
 * @Classname CustomerFlowDetailService
 * @Description 客户资金流水详情
 * @Date 2019/8/26 10:07
 */
public interface CustomerFlowDetailService extends BaseService<CustomerFlowDetail> {
    void export(Integer customerFlowId, HttpServletResponse response);
}
