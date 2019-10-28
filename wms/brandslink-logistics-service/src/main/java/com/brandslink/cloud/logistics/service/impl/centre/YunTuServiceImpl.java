package com.brandslink.cloud.logistics.service.impl.centre;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.constant.ConstantAli;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.service.FileService;
import com.brandslink.cloud.logistics.entity.centre.*;
import com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu.YunTuOrder;
import com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu.YunTuParcel;
import com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu.YunTuReceiver;
import com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu.YunTuSender;
import com.brandslink.cloud.logistics.thirdLogistics.RemoteYunTuLogisticsService;
import com.brandslink.cloud.logistics.service.LogisticsStrategyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/7/31 16:20
 */
@Service("yuntu")
public class YunTuServiceImpl implements LogisticsStrategyService {

    private final static Logger logger = LoggerFactory.getLogger(YunTuServiceImpl.class);


    @Autowired
    private RemoteYunTuLogisticsService yunTuLogisticsService;

    @Autowired
    private FileService fileService;


    @Override
    public PlaceOrderResult createOrder(BaseOrder baseOrder) throws Exception {
        PlaceOrderResult placeOrderResult = new PlaceOrderResult();
        YunTuOrder yunTuOrder = new YunTuOrder();
        voluation(baseOrder, yunTuOrder);
        logger.error("___________{}__________", JSON.toJSONString(yunTuOrder));
        String s = yunTuLogisticsService.createOrder(new ArrayList<YunTuOrder>(1){{
            this.add(yunTuOrder);
        }});
        List<JSONObject> jsonObjects = JSONObject.parseArray(s, JSONObject.class);
        JSONObject jsonObj = jsonObjects.get(0);
        String wayBillNumber = jsonObj.getString("WayBillNumber");
        placeOrderResult.setWayBillNumber(wayBillNumber);

        // 获取打印面单url
        try {
            List list = new ArrayList<>();
            list.add(wayBillNumber);
            String url = yunTuLogisticsService.print(list);

            RestTemplate rest = new RestTemplate();
            ResponseEntity<Resource> entity = rest.getForEntity(url, Resource.class);
            InputStream inputStream = entity.getBody().getInputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            String aliUrl = fileService.specifiedSaveFile(ConstantAli.BucketType.BUCKET_FILE_DEV, ConstantAli.FolderType.WMS_OUTBOUND, baseOrder.getCustomerOrderNumber() + "_tag.pdf", bytes);

            placeOrderResult.setFaceSheetUrl(ConstantLogistics.PRINT_URL + aliUrl);
        } catch (Exception e) {
            logger.error("下单获取面单信息异常,运单号:[{}],异常信息:", wayBillNumber, e);
        }

        return placeOrderResult;
    }

    @Override
    public PrintLabelResult printLabel(BaseLabel baseLabel) throws Exception {

        String waybillNumber = baseLabel.getWaybillNumber();
        List list = new ArrayList<>();
        list.add(waybillNumber);
        String url = yunTuLogisticsService.print(list);
        if (StringUtils.isBlank(url)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "<<云途物流>>找不到该运单");
        }

        RestTemplate rest = new RestTemplate();
        ResponseEntity<Resource> entity = rest.getForEntity(url, Resource.class);
        InputStream inputStream = entity.getBody().getInputStream();
        byte[] bytes = IOUtils.toByteArray(inputStream);
        String aliUrl =fileService.specifiedSaveFile(ConstantAli.BucketType.BUCKET_FILE_DEV, ConstantAli.FolderType.WMS_OUTBOUND, baseLabel.getOrderNumber()+"_tag.pdf", bytes);


        PrintLabelResult printLabelResult = new PrintLabelResult();
        printLabelResult.setOrderNumber(waybillNumber);
        printLabelResult.setPrintUrl(ConstantLogistics.PRINT_URL + aliUrl);

        return printLabelResult;
    }

    @Override
    public TrackingNumberResult getTrackingNumber(BaseTrackingNumber baseTrackingNumber) throws Exception {

        List list = new ArrayList<>();
        list.add(baseTrackingNumber.getOrderNumber());
        String items = yunTuLogisticsService.getTrackingNumber(list);
        if (StringUtils.isBlank(items)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "<<云途物流>>未查询到相应跟踪号");
        }
        List<JSONObject> jsonList = JSONObject.parseArray(items, JSONObject.class);
        JSONObject obj = jsonList.get(0);
        String trackingNumber = obj.getString("TrackingNumber");
        TrackingNumberResult trackingNumberResult = new TrackingNumberResult();
        trackingNumberResult.setOrderNumber(baseTrackingNumber.getOrderNumber());
        trackingNumberResult.setTrackingNumber(trackingNumber);

        return trackingNumberResult;
    }

    private void voluation(BaseOrder baseOrder, YunTuOrder yunTuOrder) {

        //收件人信息
        YunTuReceiver yunTuReceiver = new YunTuReceiver();
        yunTuReceiver.setCountryCode(baseOrder.getConsigneeCountryCode());
        yunTuReceiver.setStreet(baseOrder.getConsigneeStreet());
        yunTuReceiver.setCity(baseOrder.getConsigneeCity());
        yunTuReceiver.setFirstName(baseOrder.getConsigneeFirstName());
        yunTuReceiver.setLastName(baseOrder.getConsigneeLastName());
        yunTuReceiver.setPhone(baseOrder.getConsigneePhone());
        yunTuReceiver.setState(baseOrder.getConsigneeState());
        yunTuReceiver.setZip(baseOrder.getConsigneeZip());

        //发件人信息
        YunTuSender yunTuSender = new YunTuSender();
        yunTuSender.setCity(baseOrder.getConsignorCity());
        yunTuSender.setFirstName(baseOrder.getConsignorFirstName());
        yunTuSender.setLastName(baseOrder.getConsignorLastName());
        yunTuSender.setState(baseOrder.getConsignorState());
        yunTuSender.setPhone(baseOrder.getConsignorPhone());
        yunTuSender.setZip(baseOrder.getConsignorZip());
        yunTuSender.setCountryCode(baseOrder.getConsignorCountryCode());
        yunTuSender.setCompany(baseOrder.getConsignorCompany());
        yunTuSender.setStreet(baseOrder.getConsignorStreet());

        //运输方式code
        yunTuOrder.setShippingMethodCode(baseOrder.getLogisticsMethodCode());
        // 订单号
        yunTuOrder.setCustomerOrderNumber(baseOrder.getCustomerOrderNumber());

        yunTuOrder.setReceiver(yunTuReceiver);
        yunTuOrder.setSender(yunTuSender);

        List<BaseOrderChild> childs = baseOrder.getChilds();
        List<YunTuParcel> parcels = new ArrayList<>();

        BigDecimal amount = new BigDecimal(0);
        Integer count = 0;
        for (BaseOrderChild child : childs) {
            YunTuParcel yunTuParcel = new YunTuParcel();

            yunTuParcel.setEName(child.getEnName());
            yunTuParcel.setQuantity(child.getQuantity());
            yunTuParcel.setUnitPrice(child.getUnitPrice());
            yunTuParcel.setUnitWeight(child.getUnitWeight().setScale(3, BigDecimal.ROUND_HALF_UP));
            yunTuParcel.setCurrencyCode("USD");
            if (StringUtils.isBlank(child.getCnName())) {
                yunTuParcel.setCName(child.getEnName());
            } else {
                yunTuParcel.setCName(child.getCnName());
            }

            amount = amount.add(child.getUnitWeight().multiply(new BigDecimal(child.getQuantity())));
            count = count + child.getQuantity();
            parcels.add(yunTuParcel);

        }
        yunTuOrder.setParcels(parcels);
        // 总重量
        yunTuOrder.setWeight(amount.setScale(3, BigDecimal.ROUND_HALF_UP));
        yunTuOrder.setPackageCount(count);


    }
}
