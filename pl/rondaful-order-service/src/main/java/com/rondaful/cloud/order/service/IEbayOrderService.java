package com.rondaful.cloud.order.service;

import com.rondaful.cloud.order.model.dto.syncorder.PreCovertEbayOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderTransferInsertOrUpdateDTO;

import java.util.List;

/**
 * @author Blade
 * @date 2019-07-18 11:27:51
 **/
public interface IEbayOrderService {

    /**
     * 查询准备转化的ebay订单
     */
    List<SysOrderDTO> getPreConvertEbayOrder();

    /**
     * 组装准备转化的订单数据
     *
     * @param sysOrderDTOList           {@link List<SysOrderDTO> sysOrderDTOList}
     * @param preCovertEbayOrderDTOList {@link List<PreCovertEbayOrderDTO>}
     */
    void assembleConvertOrderData(List<SysOrderDTO> sysOrderDTOList, List<PreCovertEbayOrderDTO> preCovertEbayOrderDTOList);

    void getEbayOrderDeliverDeadline(List<SysOrderDTO> sysOrderDTOList);

    /**
     * 计算并组装准备转化订单的金额
     *
     * @param sysOrderDTOList {@link List<SysOrderDTO>}
     */
    void assembleConvertOrderFee(List<SysOrderDTO> sysOrderDTOList);

    /**
     * 批量更新ebay订单
     *
     * @param sysOrderTransferInsertOrUpdateDTO {@link SysOrderTransferInsertOrUpdateDTO}
     */
    void updateEbayOrderBatchForConvert(SysOrderTransferInsertOrUpdateDTO sysOrderTransferInsertOrUpdateDTO);
}
