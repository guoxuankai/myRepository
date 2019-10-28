package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.order.config.PropertyUtil;
import com.rondaful.cloud.order.enums.OrderSourceEnum;
import com.rondaful.cloud.order.mapper.EbayOrderDetailMapper;
import com.rondaful.cloud.order.mapper.EbayOrderMapper;
import com.rondaful.cloud.order.mapper.EbayOrderStatusMapper;
import com.rondaful.cloud.order.model.dto.syncorder.PreCovertEbayOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.PreCovertEbayOrderDetailDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDetailDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDetailDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderReceiveAddressDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderTransferInsertOrUpdateDTO;
import com.rondaful.cloud.order.model.dto.syncorder.UpdateSourceOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.UpdateSourceOrderDetailDTO;
import com.rondaful.cloud.order.service.IEbayOrderHandleService;
import com.rondaful.cloud.order.service.IEbayOrderService;
import com.rondaful.cloud.order.utils.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Blade
 * @date 2019-07-18 11:27:29
 **/
@Service
public class EbayOrderServiceImpl implements IEbayOrderService {

    private static Logger LOGGER = LoggerFactory.getLogger(EbayOrderServiceImpl.class);

    private EbayOrderMapper ebayOrderMapper;
    private IEbayOrderHandleService ebayOrderHandleService;
    private EbayOrderDetailMapper ebayOrderDetailMapper;
    private EbayOrderStatusMapper ebayOrderStatusMapper;

    @Autowired
    public EbayOrderServiceImpl(EbayOrderMapper ebayOrderMapper, IEbayOrderHandleService ebayOrderHandleService,
                                EbayOrderDetailMapper ebayOrderDetailMapper, EbayOrderStatusMapper ebayOrderStatusMapper) {
        this.ebayOrderMapper = ebayOrderMapper;
        this.ebayOrderHandleService = ebayOrderHandleService;
        this.ebayOrderDetailMapper = ebayOrderDetailMapper;
        this.ebayOrderStatusMapper = ebayOrderStatusMapper;
    }

    //分钟
    private int searchMinute = Integer.valueOf(PropertyUtil.getProperty("searchminute"));

    @Override
    public List<SysOrderDTO> getPreConvertEbayOrder() {
        List<SysOrderDTO> sysOrderDTOList = new ArrayList<>();
        Date endTime = new Date();
        Date startTime = TimeUtil.dateAddSubtract(endTime, searchMinute);

        List<PreCovertEbayOrderDTO> preCovertEbayOrderDTOList = ebayOrderMapper.selectPreConvertEbayOrder();
//        List<PreCovertEbayOrderDTO> preCovertEbayOrderDTOList = ebayOrderMapper.selectPreConvertEbayOrder(
//                DateUtils.formatDate(startTime, DateUtils.FORMAT_2), DateUtils.formatDate(endTime, DateUtils.FORMAT_2));

//        List<PreCovertEbayOrderDTO> preCovertEbayOrderDTOList = ebayOrderMapper.selectPreConvertEbayOrder(
//                "2019-07-15 19:00:20", "2019-07-15 21:00:20");

        LOGGER.error("准备转化的ebay订单是: {}", JSON.toJSONString(preCovertEbayOrderDTOList));

        // 组装转单数据
        this.assembleConvertOrderData(sysOrderDTOList, preCovertEbayOrderDTOList);

        // 计算费用
        this.assembleConvertOrderFee(sysOrderDTOList);


        LOGGER.error("准备转化的SysOrderDTO订单是: {}", JSON.toJSONString(sysOrderDTOList));

        return sysOrderDTOList;
    }
    @Override
    public void getEbayOrderDeliverDeadline(List<SysOrderDTO> sysOrderDTOList) {
        //计算EBAY订单最迟发货时间
        for (SysOrderDTO sysOrderDTO : sysOrderDTOList) {
                List<String> list =new ArrayList<>();
                for (SysOrderDetailDTO item : sysOrderDTO.getSysOrderDetailList()) {
                    list.add(item.getDeliverDeadline());
                }
                try {
                    sysOrderDTO.setDeliverDeadline(TimeUtil.getMinTime(list));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 计算并组装准备转化订单的金额
     *
     * @param sysOrderDTOList {@link List<SysOrderDTO>}
     */
    @Override
    public void assembleConvertOrderFee(List<SysOrderDTO> sysOrderDTOList) {

        for (SysOrderDTO sysOrderDTO : sysOrderDTOList) {
            BigDecimal rateTotal = new BigDecimal(0);
            Map<String, BigDecimal> map0 = new HashMap<>();
//            sysOrderDTO.setShippingServiceCost(ebayOrderHandleService.string2BigDecimal(sysOrderDTO.getShippingServiceCostStr()));
            sysOrderDTO.setPlatformTotalPrice(ebayOrderHandleService.string2BigDecimal(sysOrderDTO.getPlatformTotal()));
            List<SysOrderDetailDTO> sysOrderDetailDTOList = sysOrderDTO.getSysOrderDetailList();
            for (SysOrderDetailDTO sysOrderDetailDTO : sysOrderDetailDTOList) {
                String variationSku = sysOrderDetailDTO.getVariationSku();
                String itemSku = sysOrderDetailDTO.getItemSku();
                if (StringUtils.isNotEmpty(variationSku)) {
                    sysOrderDetailDTO.setPlatformSKU(variationSku);
                    sysOrderDetailDTO.setSourceSku(variationSku);
                }

                if (StringUtils.isEmpty(variationSku) && StringUtils.isNotEmpty(itemSku)) {
                    sysOrderDetailDTO.setPlatformSKU(itemSku);
                    sysOrderDetailDTO.setSourceSku(itemSku);
                }
                BigDecimal platformSKUPrice = new BigDecimal(0);
                if (StringUtils.isNotEmpty(sysOrderDetailDTO.getPlatformSKUPriceStr())) {
                    platformSKUPrice = new BigDecimal(sysOrderDetailDTO.getPlatformSKUPriceStr().split("#")[0]);
                }

                Integer skuNum = sysOrderDetailDTO.getSkuQuantity();
                BigDecimal multiply = platformSKUPrice.multiply(new BigDecimal(skuNum));
                map0.put(sysOrderDetailDTO.getPlatformSKU(), multiply);
                rateTotal = rateTotal.add(multiply);
            }
            for (SysOrderDetailDTO sysOrderDetailDTO : sysOrderDetailDTOList) {
                String platformSKU = sysOrderDetailDTO.getPlatformSKU();
                for (Map.Entry<String, BigDecimal> entry : map0.entrySet()) {
                    String key = entry.getKey();
                    if (platformSKU.equals(key)) {
                        sysOrderDetailDTO.setApportion(entry.getValue().divide(rateTotal, 2, BigDecimal.ROUND_DOWN));
                        break;
                    }
                }
            }
        }
    }

    /**
     * 组装准备转化的订单数据
     *
     * @param sysOrderDTOList           {@link List<SysOrderDTO> sysOrderDTOList}
     * @param preCovertEbayOrderDTOList {@link List<PreCovertEbayOrderDTO>}
     */
    @Override
    public void assembleConvertOrderData(List<SysOrderDTO> sysOrderDTOList, List<PreCovertEbayOrderDTO> preCovertEbayOrderDTOList) {
        for (PreCovertEbayOrderDTO preCovertEbayOrderDTO : preCovertEbayOrderDTOList) {
            SysOrderDTO sysOrderDTO = new SysOrderDTO();
            // 订单信息
            BeanUtils.copyProperties(preCovertEbayOrderDTO, sysOrderDTO);
            sysOrderDTO.setOrderSource(Integer.valueOf(OrderSourceEnum.CONVER_FROM_EBAY.getValue()));
            sysOrderDTO.setPlatformShopId(sysOrderDTO.getEmpowerId());

            // 商品信息
            List<PreCovertEbayOrderDetailDTO> preCovertEbayOrderDetailDTOList = preCovertEbayOrderDTO.getPreCovertEbayOrderDetailDTOList();
            List<SysOrderDetailDTO> sysOrderDetailDTOList = sysOrderDTO.getSysOrderDetailList();
            for (PreCovertEbayOrderDetailDTO preCovertEbayOrderDetailDTO : preCovertEbayOrderDetailDTOList) {
                SysOrderDetailDTO sysOrderDetailDTO = new SysOrderDetailDTO();
                BeanUtils.copyProperties(preCovertEbayOrderDetailDTO, sysOrderDetailDTO);
                sysOrderDetailDTOList.add(sysOrderDetailDTO);
            }
            sysOrderDTO.setSysOrderDetailList(sysOrderDetailDTOList);

            // 发货信息
            SysOrderReceiveAddressDTO sysOrderReceiveAddressDTO = new SysOrderReceiveAddressDTO();
            BeanUtils.copyProperties(preCovertEbayOrderDTO, sysOrderReceiveAddressDTO);
            sysOrderDTO.setSysOrderReceiveAddress(sysOrderReceiveAddressDTO);

            // 包裹信息
            SysOrderPackageDTO sysOrderPackageDTO = new SysOrderPackageDTO();
            sysOrderPackageDTO.setSourceOrderId(preCovertEbayOrderDTO.getSourceOrderId());
            // 包裹里面的商品需要等到转换成品连sku的时候进行填充
            List<SysOrderPackageDetailDTO> sysOrderPackageDetailDTOList = new ArrayList<>();
            sysOrderPackageDTO.setSysOrderPackageDetailList(sysOrderPackageDetailDTOList);

            List<SysOrderPackageDTO> sysOrderPackageDTOList = new ArrayList<>();
            sysOrderPackageDTOList.add(sysOrderPackageDTO);
            sysOrderDTO.setSysOrderPackageList(sysOrderPackageDTOList);

            sysOrderDTOList.add(sysOrderDTO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEbayOrderBatchForConvert(SysOrderTransferInsertOrUpdateDTO sysOrderTransferInsertOrUpdateDTO) {
        List<UpdateSourceOrderDTO> updateSourceOrders = sysOrderTransferInsertOrUpdateDTO.getUpdateSourceOrderDTOList();
        List<UpdateSourceOrderDetailDTO> updateSourceOrderDetails = sysOrderTransferInsertOrUpdateDTO.getUpdateSourceOrderDetailDTOList();

        ebayOrderStatusMapper.updateConvertStatusBatch(updateSourceOrders);
        ebayOrderDetailMapper.updateConvertStatusStatusBatch(updateSourceOrderDetails);

    }
}
