package com.rondaful.cloud.order.thread;

import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.mapper.EbayOrderDetailMapper;
import com.rondaful.cloud.order.mapper.EbayOrderStatusMapper;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDetailDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderTransferInsertOrUpdateDTO;
import com.rondaful.cloud.order.model.dto.syncorder.UpdateSourceOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.UpdateSourceOrderDetailDTO;
import com.rondaful.cloud.order.service.IEbayOrderService;
import com.rondaful.cloud.order.service.ISkuMapService;
import com.rondaful.cloud.order.service.ISysOrderService;
import com.rondaful.cloud.order.utils.ApplicationContextProvider;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Blade
 * @date 2019-07-31 14:08:42
 **/
public class AutoConvertEbayOrderThread extends Thread {

    private static Logger LOGGER = LoggerFactory.getLogger(AutoConvertEbayOrderThread.class);

    private IEbayOrderService ebayOrderService;

    private ISkuMapService skuMapService;

    private ISysOrderService sysOrderService;

    private EbayOrderStatusMapper ebayOrderStatusMapper;

    private EbayOrderDetailMapper ebayOrderDetailMapper;

    public AutoConvertEbayOrderThread() {
        ebayOrderService = (IEbayOrderService) ApplicationContextProvider.getBean("ebayOrderServiceImpl");
        skuMapService = (ISkuMapService) ApplicationContextProvider.getBean("skuMapServiceImpl");
        sysOrderService = (ISysOrderService) ApplicationContextProvider.getBean("sysOrderServiceImpl");
        ebayOrderStatusMapper = (EbayOrderStatusMapper) ApplicationContextProvider.getBean("ebayOrderStatusMapper");
        ebayOrderDetailMapper = (EbayOrderDetailMapper) ApplicationContextProvider.getBean("ebayOrderDetailMapper");
    }

    /**
     * 执行被指派的所有任务
     */
    @Override
    public void run() {
        List<SysOrderDTO> sysOrderDTOList = ebayOrderService.getPreConvertEbayOrder();
        try {

            if (CollectionUtils.isEmpty(sysOrderDTOList)) {
                LOGGER.error("没有需要转入的ebay订单");
                return;
            }

            LOGGER.error("进入转单前：sysOrderDTOList={}", FastJsonUtils.toJsonString(sysOrderDTOList));
            skuMapService.orderMapByOrderListNew(OrderRuleEnum.platformEnm.E_BAU.getPlatform(), sysOrderDTOList);
            LOGGER.error("进入转单（转单成功）后：sysOrderDTOList={}", FastJsonUtils.toJsonString(sysOrderDTOList));
        } catch (Exception e) {
            LOGGER.error("转系统订单全部映射失败", e);
            LOGGER.error("进入转单后（转单失败）：sysOrderDTOList={}", FastJsonUtils.toJsonString(sysOrderDTOList));
            try {
                this.updateFailureStatus(sysOrderDTOList);
            } catch (Exception ex) {
                LOGGER.error("EBAY自动转单，修改平台订单状态失败：{}", ex);
            }
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "转系统订单全部映射失败");
        }

        try {
            SysOrderTransferInsertOrUpdateDTO sysOrderTransferInsertOrUpdateDTO = sysOrderService.splitInsertSysOrderData(sysOrderDTOList);
            sysOrderService.insertSysOrderBatch(sysOrderTransferInsertOrUpdateDTO);
            ebayOrderService.updateEbayOrderBatchForConvert(sysOrderTransferInsertOrUpdateDTO);
        } catch (Exception e) {
            LOGGER.error("批量插入异常", e);
        }
    }

    public void updateFailureStatus(List<SysOrderDTO> sysOrderDTOList) {
        // 需要更新的源订单
        List<UpdateSourceOrderDTO> updateSourceOrderDTOList = new ArrayList<>();
        List<UpdateSourceOrderDetailDTO> updateSourceOrderDetailDTOList = new ArrayList<>();
        for (SysOrderDTO sysOrderDTO : sysOrderDTOList) {
            UpdateSourceOrderDTO dto=new UpdateSourceOrderDTO();
            dto.setConverSysStatus(2);
            dto.setOrderId(sysOrderDTO.getSourceOrderId());
            dto.setUpdateBy(Constants.DefaultUser.SYSTEM);
            dto.setUpdateDate(new Date());
            updateSourceOrderDTOList.add(dto);
            for (SysOrderDetailDTO orderDetailDTO : sysOrderDTO.getSysOrderDetailList()) {
                UpdateSourceOrderDetailDTO detailDTO=new UpdateSourceOrderDetailDTO();
                detailDTO.setConverSysStatus(2);
                detailDTO.setOrderId(sysOrderDTO.getSourceOrderId());
                detailDTO.setOrderItemId(orderDetailDTO.getSourceOrderLineItemId());
                detailDTO.setUpdateBy(Constants.DefaultUser.SYSTEM);
                detailDTO.setUpdateDate(new Date());
                updateSourceOrderDetailDTOList.add(detailDTO);
            }
        }
        ebayOrderStatusMapper.updateConvertStatusBatch(updateSourceOrderDTOList);
        ebayOrderDetailMapper.updateConvertStatusStatusBatch(updateSourceOrderDetailDTOList);
    }

}
