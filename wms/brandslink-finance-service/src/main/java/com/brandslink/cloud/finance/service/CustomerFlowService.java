package com.brandslink.cloud.finance.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.finance.pojo.dto.CustomerFlowDto;
import com.brandslink.cloud.finance.pojo.dto.CustomerSelfFlowDto;
import com.brandslink.cloud.finance.pojo.entity.CustomerFlow;
import com.brandslink.cloud.finance.pojo.vo.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yangzefei
 * @Classname CustomerFlowService
 * @Description 客户资金流水
 * @Date 2019/8/26 10:07
 */
public interface CustomerFlowService extends BaseService<CustomerFlow> {

    /**
     * 获取交易流水列表
     * @param param
     * @return
     */
    List<CustomerFlowDto> getList(CustomerFlowVo param);

    /**
     * 获取客户交易流水列表
     * @param param
     * @return
     */
    List<CustomerSelfFlowDto> getListBySelf(CustomerFlowVo param);

    /**
     * 获取流水详情
     * @param id
     * @return
     */
    CustomerFlowDto getById(Integer id);

    /**
     * 入库费计算
     * @param param
     */
    void calcInStockCost(StockCostVo param);

    /**
     * 销退费计算
     * @param param
     */
    void calcReturnCost(StockCostVo param);

    /**
     * 出库费计算
     * @param param
     */
    void calcOutStockCost(OutStockCostVo param);

    /**
     * 订单拦截费计算
     * @param param
     */
    void calcInterceptCost(InterceptCostVo param);

    /**
     * 预估物流费计算
     * @param param
     */
    void calcLogisticsCost(LogisticsCostVo param);

    /**
     * 实际物流费计算
     * @param sourceNo
     * @param logFreightCost
     */
    void calcActualLogisticsCost(String sourceNo, BigDecimal logFreightCost);
}
