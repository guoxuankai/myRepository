package com.rondaful.cloud.order.service;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.OrderApplication;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.model.vo.freight.LogisticsDetailVo;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListDTO;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListSku;
import com.rondaful.cloud.order.entity.InvoiceTemplate;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.entity.system.SysOrderPackageDetail;
import com.rondaful.cloud.order.entity.system.SysOrderReceiveAddress;
import com.rondaful.cloud.order.mapper.EbayOrderMapper;
import com.rondaful.cloud.order.mapper.InvoiceTemplateMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageDetailMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageMapper;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDetailDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderTransferInsertOrUpdateDTO;
import com.rondaful.cloud.order.model.dto.sysOrderInvoice.SysOrderInvoiceInsertOrUpdateDTO;
import com.rondaful.cloud.order.model.vo.sysOrderInvoice.SysOrderInvoiceVO;
import com.rondaful.cloud.order.model.vo.sysorder.CalculateLogisticsResultVO;
import com.rondaful.cloud.order.quartz.SyncTask;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.seller.EmpowerMapper;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统订单测试类
 *
 * @author Blade
 * @date 2019-06-19 09:50:58
 **/
@SpringBootTest(classes = OrderApplication.class)
@RunWith(SpringRunner.class)
public class SysOrderServiceTest {

    private static Logger LOGGER = LoggerFactory.getLogger(SysOrderServiceTest.class);

    @Autowired
    private ISysOrderInvoiceService sysOrderInvoiceService;
    @Autowired
    private InvoiceTemplateMapper invoiceTemplateMapper;
    @Autowired
    private EmpowerMapper empowerMapper;
    @Autowired
    private EbayOrderMapper ebayOrderMapper;

    @Autowired
    private ISkuMapService skuMapService;
    @Autowired
    private ISyncEbayOrderService syncEbayOrderService;

    @Autowired
    private IEbayOrderService ebayOrderService;

    @Autowired
    private ISysOrderService sysOrderService;

    @Autowired
    private ISystemOrderService systemOrderService;

    @Autowired
    private SyncTask syncTask;

    @Autowired
    private SysOrderPackageDetailMapper sysOrderPackageDetailMapper;
    @Autowired
    private SysOrderPackageMapper sysOrderPackageMapper;

    @Test
    public void testExportPDF() throws Exception {
        InvoiceTemplate invoiceTemplate = invoiceTemplateMapper.selectByPrimaryKey(1L);
        LOGGER.info("invoiceTemplate={}", FastJsonUtils.toJsonString(invoiceTemplate));

        SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO = new SysOrderInvoiceInsertOrUpdateDTO();
        BeanUtils.copyProperties(invoiceTemplate, sysOrderInvoiceInsertOrUpdateDTO);
        sysOrderInvoiceInsertOrUpdateDTO.setInvoiceTemplateId(invoiceTemplate.getId());
        LOGGER.info("sysOrderInvoiceInsertOrUpdateDTO={}", FastJsonUtils.toJsonString(sysOrderInvoiceInsertOrUpdateDTO));
        String amazonPLOrderId = "PL20190614174834787OhNqXII6";
        String ebayPLOrderId = "PL20190616103800344M0JHFdog";
        String aliExpressPLOrderId = "";

        sysOrderInvoiceService.exportPDF("test", sysOrderInvoiceInsertOrUpdateDTO, amazonPLOrderId);
    }

    @Test
    public void testSyncEbay() throws Exception {
        Empower empower = empowerMapper.selectByPrimaryKey(1368L);
//        Empower empower = empowerMapper.selectByPrimaryKey(1711L);
        LOGGER.info("empower={}", FastJsonUtils.toJsonString(empower));
        syncTask.execute(empower);
    }

    @Test
    public void testGetNoShowEbayOrderIds() {
        List<String> ids = new ArrayList<>();
        ids.add("392312696582-916423726026");
        ids.add("392250602037-916554094026");
        List<String> ebayOrderIds = ebayOrderMapper.getNoShowEbayOrderIds(ids);
//        ebayOrderMapper.updateNoShowOrderToShow(ids);
        LOGGER.info("ebayOrderIds={}", ebayOrderIds);
    }

    @Test
    public void testPlatformSkuMapPlSku() {
        String sku = "GF0225000|3684233919";
        String plSku = skuMapService.queryPlSku(OrderRuleEnum.platformEnm.E_BAU.getPlatform(), String.valueOf(1554),
                sku, String.valueOf(824));
        LOGGER.info(plSku);
    }

    @Test
    public void testGetSysOrderInvoiceBySysOrderId() {
        String sysOrderId = "PL20190701141830912Gt5Qc4No";
        SysOrderInvoiceVO sysOrderInvoiceVO = sysOrderInvoiceService.getSysOrderInvoiceBySysOrderId(sysOrderId);
        LOGGER.info(sysOrderInvoiceVO.getCountryCnName());
    }

    @Test
    public void testSyncEbayOrder() {
        syncEbayOrderService.testSyncEbayOrder();
    }

    @Test
    public void testGetPreConvertEbayOrder() {
        List<SysOrderDTO> sysOrderDTOList = ebayOrderService.getPreConvertEbayOrder();

        try {

            if (CollectionUtils.isEmpty(sysOrderDTOList)) {
                LOGGER.info("没有需要转入的ebay订单");
                return;
            }

            skuMapService.orderMapByOrderListNew(OrderRuleEnum.platformEnm.E_BAU.getPlatform(), sysOrderDTOList);
            LOGGER.debug("sysOrderDTOList={}", FastJsonUtils.toJsonString(sysOrderDTOList));
        } catch (Exception e) {
            LOGGER.error("转系统订单全部映射失败", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "转系统订单全部映射失败");
        }
        try {
            SysOrderTransferInsertOrUpdateDTO sysOrderTransferInsertOrUpdateDTO = sysOrderService.splitInsertSysOrderData(sysOrderDTOList);
            sysOrderService.insertSysOrderBatch(sysOrderTransferInsertOrUpdateDTO);
            ebayOrderService.updateEbayOrderBatchForConvert(sysOrderTransferInsertOrUpdateDTO);
        } catch (Exception e) {
            LOGGER.error("批量插入出错", e);
        }
    }

    @Test
    public void testSyncEbayOrderOld() {
        try {
            String a = syncEbayOrderService.syncEbayOrders();
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCalculateEstimateFreight() {
        String sysOrderId = "PL20190802114918073XdF42q1q";
        SysOrderNew sysOrderNew = systemOrderService.getSysOrderNew(sysOrderId);

        List<SysOrderPackageDTO> sysOrderPackageDTOList = new ArrayList<>();

        for (SysOrderPackage sysOrderPackage : sysOrderNew.getSysOrderPackageList()) {

            SysOrderPackageDTO sysOrderPackageDTO = new SysOrderPackageDTO();
            BeanUtils.copyProperties(sysOrderNew.getSysOrderPackageList().get(0), sysOrderPackageDTO);
            List<SysOrderPackageDetailDTO> sysOrderPackageDetailDTOList = new ArrayList<>();
            List<SysOrderPackageDetail> sysOrderPackageDetails = sysOrderPackage.getSysOrderPackageDetailList();
            for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackageDetails) {
                SysOrderPackageDetailDTO sysOrderPackageDetailDTO = new SysOrderPackageDetailDTO();
                BeanUtils.copyProperties(sysOrderPackageDetail, sysOrderPackageDetailDTO);
                sysOrderPackageDetailDTOList.add(sysOrderPackageDetailDTO);
            }
            sysOrderPackageDTO.setSysOrderPackageDetailList(sysOrderPackageDetailDTOList);
            sysOrderPackageDTOList.add(sysOrderPackageDTO);
        }

        for (SysOrderPackageDTO sysOrderPackageDTO : sysOrderPackageDTOList) {
            SysOrderReceiveAddress sysOrderReceiveAddress = sysOrderNew.getSysOrderReceiveAddress();

            try {
                CalculateLogisticsResultVO calculateLogisticsResultVO = systemOrderService.calculateEstimateFreight(sysOrderPackageDTO, String.valueOf(2),
                        String.valueOf(sysOrderPackageDTO.getDeliveryWarehouseId()), sysOrderReceiveAddress.getShipToCountry(),
                        sysOrderReceiveAddress.getShipToPostalCode(), null, sysOrderPackageDTO.getDeliveryMethodCode(), sysOrderReceiveAddress.getShipToCity());
                LOGGER.info("数据组装之后的为{}", FastJsonUtils.toJsonString(calculateLogisticsResultVO));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testGetSuitLogisticsByType() {
        String sysOrderId = "PL201907261839285163RcXHcLO";
        SysOrderNew sysOrderNew = systemOrderService.getSysOrderNew(sysOrderId);

        SysOrderReceiveAddress sysOrderReceiveAddress = sysOrderNew.getSysOrderReceiveAddress();
        SysOrderPackage sysOrderPackage = sysOrderNew.getSysOrderPackageList().get(0);

        SearchLogisticsListDTO searchLogisticsListDTO = new SearchLogisticsListDTO();
        searchLogisticsListDTO.setCity(sysOrderReceiveAddress.getShipToCity());
        searchLogisticsListDTO.setCountryCode(sysOrderReceiveAddress.getShipToCountry());
        searchLogisticsListDTO.setPlatformType("1");
        searchLogisticsListDTO.setPostCode(sysOrderReceiveAddress.getShipToPostalCode());
        searchLogisticsListDTO.setWarehouseId(String.valueOf(sysOrderPackage.getDeliveryWarehouseId()));
        searchLogisticsListDTO.setSearchType(1);

        List<SearchLogisticsListSku> skuList = new ArrayList<>();
        for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackage.getSysOrderPackageDetailList()) {
            SearchLogisticsListSku searchLogisticsListSku = new SearchLogisticsListSku();
            searchLogisticsListSku.setSku(sysOrderPackageDetail.getSku());
            searchLogisticsListSku.setSkuNumber(sysOrderPackageDetail.getSkuQuantity());
            skuList.add(searchLogisticsListSku);
        }

        searchLogisticsListDTO.setSearchLogisticsListSkuList(skuList);

        List<LogisticsDetailVo> list = systemOrderService.getSuitLogisticsByType(searchLogisticsListDTO);
        LOGGER.info("获得的物流方式列表为: {}", FastJsonUtils.toJsonString(list));
    }

    @Test
    public void testEbayDeliveryCallBack() {
        String sysOrderId = "PL20190807141349035o9aSNQ4O";
        String orderTrackId = "TK141349035EMgEMg5H";
        SysOrderNew sysOrderNew = systemOrderService.getSysOrderNew(sysOrderId);
        SysOrderPackage deliveredPackage = sysOrderPackageMapper.queryOrderPackageByOrderTrackId(orderTrackId);
        List<SysOrderPackageDetail> deliveredPackageDetailList = sysOrderPackageDetailMapper.queryOrderPackageDetails(orderTrackId);
        deliveredPackage.setSysOrderPackageDetailList(deliveredPackageDetailList);
        // 判断是否手工创建的订单
        long count = sysOrderNew.getSysOrderDetails().stream().filter(x -> StringUtils.isBlank(x.getSourceOrderLineItemId())).count();
        if (count >= 1) {
            LOGGER.info("订单{} 是手工创建的订单，不进行回标操作", sysOrderId);
            return;
        }

        List<EbayOrder> list = systemOrderService.constructEbayOrderDeliverInfoNew(deliveredPackage);
        systemOrderService.sendEbayOrderDeliverInfoNew(list, sysOrderNew, deliveredPackage);
    }

    @Test
    public void testCreateWmsOrder() {
        String sysOrderId = "PL20190813132025468bCTBgLUz";

        try {
            systemOrderService.deliverGoodSingleNew(sysOrderId, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getOrderJsonStr() {
        List<String> key = Arrays.asList("401830714529-801778145027");
//        List<EbayOrder> list1 = ebayOrderMapper.selectBatchSysOrderByOrderId(key);
        List<SysOrderDTO> orders = ebayOrderService.getPreConvertEbayOrder();
        if (orders != null && !orders.isEmpty()) {
            LOGGER.info(JSON.toJSONString(orders));
            orders = orders.stream().filter(o -> o.getSourceOrderId().equalsIgnoreCase("401830714529-801778145027")).collect(Collectors.toList());
            if (!orders.isEmpty())
                LOGGER.info(JSON.toJSONString(orders));
        }

    }
}
