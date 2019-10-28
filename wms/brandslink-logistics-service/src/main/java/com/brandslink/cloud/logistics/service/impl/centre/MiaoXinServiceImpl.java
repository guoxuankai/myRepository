package com.brandslink.cloud.logistics.service.impl.centre;

import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.constant.ConstantAli;
import com.brandslink.cloud.common.service.FileService;
import com.brandslink.cloud.logistics.entity.centre.*;
import com.brandslink.cloud.logistics.service.ILogisticsCollectorService;
import com.brandslink.cloud.logistics.service.ILogisticsProviderService;
import com.brandslink.cloud.logistics.service.LogisticsStrategyService;
import com.brandslink.cloud.logistics.thirdLogistics.RemoteMiaoXinLogisticsService;
import com.brandslink.cloud.logistics.thirdLogistics.RemoteYunTuLogisticsService;
import com.brandslink.cloud.logistics.thirdLogistics.bean.MiaoXin.MiaoXinOrder;
import com.brandslink.cloud.logistics.thirdLogistics.bean.MiaoXin.MiaoXinPrintVO;
import com.brandslink.cloud.logistics.thirdLogistics.bean.MiaoXin.OrderInvoice;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/7/31 16:20
 */
@Service("miaoxin")
public class MiaoXinServiceImpl implements LogisticsStrategyService {

    private final static Logger logger = LoggerFactory.getLogger(MiaoXinServiceImpl.class);


    @Autowired
    private RemoteMiaoXinLogisticsService miaoXinLogisticsService;

    @Autowired
    private RemoteYunTuLogisticsService yunTuLogisticsService;

    @Autowired
    private ILogisticsProviderService logisticsProviderService;

    @Autowired
    private ILogisticsCollectorService logisticsCollectorService;

    @Autowired
    private FileService fileService;

    @Override
    public PlaceOrderResult createOrder(BaseOrder baseOrder) throws UnsupportedEncodingException {

        PlaceOrderResult placeOrderResult = new PlaceOrderResult();
        MiaoXinOrder miaoXinOrder = new MiaoXinOrder();
        voluation(baseOrder, miaoXinOrder);
        String s = miaoXinLogisticsService.createOrderApi(miaoXinOrder);
        JSONObject obj = JSONObject.parseObject(s);
        String orderId = obj.getString("order_id");
        placeOrderResult.setWayBillNumber(orderId);

        // 获取打印面单url
        try {
            MiaoXinPrintVO miaoXinPrintVO = new MiaoXinPrintVO();
            miaoXinPrintVO.setPrintType("A4");
            List list = new ArrayList<>();
            list.add(orderId);
            miaoXinPrintVO.setOrderIds(list);
            String url = miaoXinLogisticsService.printLabel(miaoXinPrintVO);

            RestTemplate rest = new RestTemplate();
            ResponseEntity<Resource> entity = rest.getForEntity(url, Resource.class);
            InputStream inputStream = entity.getBody().getInputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            String aliUrl = fileService.specifiedSaveFile(ConstantAli.BucketType.BUCKET_FILE_DEV, ConstantAli.FolderType.WMS_OUTBOUND, baseOrder.getCustomerOrderNumber() + "_tag.pdf", bytes);

            placeOrderResult.setFaceSheetUrl(ConstantLogistics.PRINT_URL + aliUrl);
        } catch (Exception e) {
            logger.error("下单获取面单信息异常,运单号:[{}],异常信息:", orderId, e);
        }

        return placeOrderResult;
    }

    @Override
    public PrintLabelResult printLabel(BaseLabel baseLabel) throws Exception {
        PrintLabelResult printLabelResult = new PrintLabelResult();
        MiaoXinPrintVO miaoXinPrintVO = new MiaoXinPrintVO();
        String waybillNumber = baseLabel.getWaybillNumber();
        miaoXinPrintVO.setPrintType("A4");
        List list = new ArrayList<>();
        list.add(waybillNumber);
        miaoXinPrintVO.setOrderIds(list);
        String url = miaoXinLogisticsService.printLabel(miaoXinPrintVO);

        RestTemplate rest = new RestTemplate();
        ResponseEntity<Resource> entity = rest.getForEntity(url, Resource.class);
        InputStream inputStream = entity.getBody().getInputStream();
        byte[] bytes = IOUtils.toByteArray(inputStream);
        String aliUrl = fileService.specifiedSaveFile(ConstantAli.BucketType.BUCKET_FILE_DEV, ConstantAli.FolderType.WMS_OUTBOUND, baseLabel.getOrderNumber() + "_tag.pdf", bytes);

        printLabelResult.setOrderNumber(waybillNumber);
        printLabelResult.setPrintUrl(ConstantLogistics.PRINT_URL + aliUrl);

        return printLabelResult;
    }

    @Override
    public TrackingNumberResult getTrackingNumber(BaseTrackingNumber baseTrackingNumber) {

        String trackingNumber = miaoXinLogisticsService.getOrderTrackingNumber(baseTrackingNumber.getOrderNumber(), null);
        TrackingNumberResult trackingNumberResult = new TrackingNumberResult();
        trackingNumberResult.setOrderNumber(baseTrackingNumber.getOrderNumber());
        trackingNumberResult.setTrackingNumber(trackingNumber);

        return trackingNumberResult;
    }


    private void voluation(BaseOrder baseOrder, MiaoXinOrder miaoXinOrder) {

        //收件人信息

        miaoXinOrder.setCountry(baseOrder.getConsigneeCountryCode());
        miaoXinOrder.setConsignee_address(baseOrder.getConsigneeStreet());
        miaoXinOrder.setConsignee_name(baseOrder.getConsigneeFirstName() + baseOrder.getConsigneeLastName());
        miaoXinOrder.setConsignee_mobile(baseOrder.getConsigneePhone());
        miaoXinOrder.setConsignee_postcode(baseOrder.getConsigneeZip());
        miaoXinOrder.setConsignee_state(baseOrder.getConsigneeState());
        miaoXinOrder.setConsignee_telephone(baseOrder.getConsigneePhone());
        miaoXinOrder.setConsignee_city(baseOrder.getConsigneeCity());


        //发件人信息
        miaoXinOrder.setShipper_address1(baseOrder.getConsignorStreet());
        miaoXinOrder.setShipper_city(baseOrder.getConsignorCity());
        miaoXinOrder.setShipper_country(baseOrder.getConsignorCountryCode());
        if (StringUtils.isNotBlank(baseOrder.getConsignorFirstName()) && StringUtils.isNotBlank(baseOrder.getConsignorLastName())) {
            miaoXinOrder.setShipper_name(baseOrder.getConsignorFirstName() + baseOrder.getConsignorLastName());
        }
        miaoXinOrder.setShipper_companyname(baseOrder.getConsignorCompany());
        miaoXinOrder.setShipper_postcode(baseOrder.getConsignorZip());
        miaoXinOrder.setShipper_state(baseOrder.getConsignorState());
        miaoXinOrder.setShipper_telephone(baseOrder.getConsignorPhone());


        // 订单号
        miaoXinOrder.setOrder_customerinvoicecode(baseOrder.getCustomerOrderNumber());
        // 交易类型
        miaoXinOrder.setTrade_type("ZYXT");
        // 运输方式id
        miaoXinOrder.setProduct_id(baseOrder.getLogisticsMethodCode());
        // 客户id
        miaoXinOrder.setCustomer_id("16462");
        // 登录人id
        miaoXinOrder.setCustomer_userid("13521");


        List<BaseOrderChild> childs = baseOrder.getChilds();
        List<OrderInvoice> orderInvoices = new ArrayList<>();
        BigDecimal amount = new BigDecimal(0);
        for (BaseOrderChild child : childs) {

            OrderInvoice orderInvoice = new OrderInvoice();
            // 申报单价，单位usd
            orderInvoice.setUnitPrice(child.getUnitPrice());
            // 申报总价值，单位usd
            orderInvoice.setInvoice_amount(child.getUnitPrice().multiply(new BigDecimal(child.getQuantity())).toString());
            // 件数
            orderInvoice.setInvoice_pcs(child.getQuantity().toString());
            // 英文品名
            orderInvoice.setInvoice_title(child.getEnName());
            orderInvoices.add(orderInvoice);
            amount = amount.add(child.getUnitPrice());
        }
        miaoXinOrder.setOrderInvoiceParam(orderInvoices);

        // 总重量
        miaoXinOrder.setWeight(amount.toString());


    }


}
