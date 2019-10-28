package com.amazonservices.mws.uploadData.common.mws;

import com.amazonservices.mws.uploadData.utils.ClassXmlUtil;
import com.rondaful.cloud.order.entity.Amazon.AmazonDelivery;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrderDetail;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.Time;
import com.rondaful.cloud.order.utils.OrderUtils;
import com.rondaful.cloud.order.utils.TimeUtil;
import com.rondaful.cloud.seller.generated.AmazonEnvelope;
import com.rondaful.cloud.seller.generated.Header;
import com.rondaful.cloud.seller.generated.OrderFulfillment;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2019-01-15 20:22
 * 包名: com.amazonservices.mws.uploadData.common.mws
 * 描述:
 */
public class UploadData {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(UploadData.class);
    public static void main(String[] args) throws JAXBException, IOException, ParseException {
      /*  String time = "2009-02-20T02:10:35+00:00";
        Date LatestShipDate = OrderUtils.getTimeToDate(time);
        System.out.println(TimeUtil.DateToString2(LatestShipDate));
        System.out.println(LatestShipDate);*/
        List<AmazonDelivery> amazonOrderList = new ArrayList<>();
        for (int i = 1; i < 200000; i++) {
            AmazonDelivery amazonOrder = new AmazonDelivery();
            List<AmazonOrderDetail> amazonOrderDetails = new ArrayList<>();
            AmazonOrderDetail amazonOrderDetail = new AmazonOrderDetail();
            amazonOrderDetail.setDeliveryTime("1995-12-15 14:45:6");
            amazonOrderDetail.setMarketplaceId("站点ID：666666666");
            amazonOrderDetail.setAmazonSellerAccount("卖家ID：88888");
            amazonOrderDetail.setShipmentTrackingNumber("跟踪号：1234656");
            amazonOrderDetail.setCarrierName("物流商代码：ABCD");
            amazonOrderDetail.setShippingMethod("邮寄方式;fly");
            amazonOrderDetail.setOrderId("订单ID：123");
            amazonOrderDetail.setAmazonOrderitemId("订单项ID：123456");
            amazonOrderDetail.setQuantity(new Integer("6"));
            amazonOrderDetails.add(amazonOrderDetail);
            amazonOrder.setAmazonOrderDetailList(amazonOrderDetails);
            amazonOrderList.add(amazonOrder);
        }
        setUploadDatasXML(amazonOrderList);
    }

    public static  String setUploadDatasXML(List<AmazonDelivery> amazonOrderList){
        Time time = null;
        if (amazonOrderList.get(0).getAmazonOrderDetailList() == null) {
            logger.error("回传数据AmazonOrderDetailList为null！请检查数据库表中的json串内容。");
            return null;
        }
        String sellerAccount = amazonOrderList.get(0).getAmazonOrderDetailList().get(0).getAmazonSellerAccount();
        if (StringUtils.isBlank(sellerAccount)) {
            logger.error("回传数据AmazonOrderDetailList中的品连账号为null！请检查数据库表中的json串内容。");
            return null;
        }
        AmazonEnvelope ae = new AmazonEnvelope();
        //header
        Header header = new Header();
        header.setDocumentVersion("1.01");
        header.setMerchantIdentifier(sellerAccount);
        ae.setHeader(header);
        //messageType
        ae.setMessageType("OrderFulfillment");
        List<AmazonEnvelope.Message> messageList = new ArrayList<>();
        int count=1;
        for (AmazonDelivery amazonDelivery1 : amazonOrderList) {
            //判空
            if (checkUploadDataIsNull(amazonDelivery1.getAmazonOrderDetailList())) {
                String orderItemIds="";
                for (AmazonOrderDetail amazonOrderDetail : amazonDelivery1.getAmazonOrderDetailList()) {
                    orderItemIds += amazonOrderDetail.getAmazonOrderitemId() + "#";
                }
                logger.error("异常：跟踪号或物流商或运送方式为空，拒绝上传！亚马逊订单号为："+amazonDelivery1.getAmazonOrderDetailList().get(0).getOrderId()
                        +"订单项ID为"+orderItemIds+"(#号分割)");
                continue;
            }
            AmazonEnvelope.Message message = new AmazonEnvelope.Message();
            message.setMessageID(new BigInteger(String.valueOf(count)));
            OrderFulfillment orderFulfillment = new OrderFulfillment();
            orderFulfillment.setAmazonOrderID(amazonDelivery1.getAmazonOrderDetailList().get(0).getOrderId());//亚马逊订单ID
            try {
                time = OrderUtils.getLastUpdateTimeDetail(TimeUtil.DateToString2(TimeUtil.stringToDate(amazonDelivery1.getAmazonOrderDetailList().get(0).getDeliveryTime())),"Greenwich");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //设置发货时间
            XMLGregorianCalendarImpl xmlGregorianCalendar = new XMLGregorianCalendarImpl(new GregorianCalendar(time.getYear(), time.getMonth()-1, time.getDay(),
                    time.getHour(), time.getMinute(), time.getSecond()));
            xmlGregorianCalendar.setTimezone(0);
            orderFulfillment.setFulfillmentDate(xmlGregorianCalendar);
            OrderFulfillment.FulfillmentData fulfillmentData = new OrderFulfillment.FulfillmentData();

            fulfillmentData.setCarrierName(amazonDelivery1.getAmazonOrderDetailList().get(0).getCarrierName());  //物流商名称
            fulfillmentData.setShippingMethod(amazonDelivery1.getAmazonOrderDetailList().get(0).getShippingMethod()); //邮寄方式
            fulfillmentData.setShipperTrackingNumber(amazonDelivery1.getAmazonOrderDetailList().get(0).getShipmentTrackingNumber());  //跟踪号
            List<OrderFulfillment.Item> itemList = new ArrayList<>();
            for (AmazonOrderDetail amazonOrderDetail : amazonDelivery1.getAmazonOrderDetailList()) {
                OrderFulfillment.Item item = new OrderFulfillment.Item();
                item.setAmazonOrderItemCode(amazonOrderDetail.getAmazonOrderitemId());   //订单项ID
                item.setQuantity(BigInteger.valueOf(amazonOrderDetail.getQuantity()));   //数量
                itemList.add(item);
            }
            orderFulfillment.getItem().addAll(itemList);
            orderFulfillment.setFulfillmentData(fulfillmentData);
            message.setOrderFulfillment(orderFulfillment);
            messageList.add(message);
            count++;
        }
        ae.getMessage().addAll(messageList);
        String xml = ClassXmlUtil.toXML(ae);
        xml = xml.replace("<AmazonEnvelope>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                "<AmazonEnvelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\">\r");
        return xml;
    }

    /**
     * 检验上传回亚马逊的跟踪号、物流商、运送方式是否为空
     * @param list
     * @return
     */
    public static boolean checkUploadDataIsNull(List<AmazonOrderDetail> list){
        for (AmazonOrderDetail amazonOrderDetail : list) {
            if (StringUtils.isBlank(amazonOrderDetail.getCarrierName()) || StringUtils.isBlank(amazonOrderDetail.getShipmentTrackingNumber())
                    || StringUtils.isBlank(amazonOrderDetail.getShippingMethod())) {
                return true;
            }
        }
        return false;
    }

    public static void uploadData(List<SysOrder> sysOrderList) throws ParseException {
        int count=1;    //标记信息编号
        AmazonEnvelope ae = new AmazonEnvelope();
        List<AmazonEnvelope.Message> messageList = ae.getMessage();
        //header
        Header header = new Header();
        header.setDocumentVersion("1.01");
        header.setMerchantIdentifier(sysOrderList.get(0).getPlatformSellerAccount());   //sellerID
        ae.setHeader(header);
        //messageType
        ae.setMessageType("OrderFulfillment");
        for (SysOrder sysOrder : sysOrderList) {
            List<OrderFulfillment.Item> itemList = new ArrayList<>();
            //message
            AmazonEnvelope.Message message = new AmazonEnvelope.Message();
            message.setMessageID(new BigInteger(String.valueOf(count)));    //------------------<MessageID>
            OrderFulfillment of = new OrderFulfillment();
            of.setAmazonOrderID(sysOrder.getSourceOrderId()); //订单ID
            Time time = OrderUtils.getLastUpdateTimeDetail(sysOrder.getDeliveryTime(), sysOrder.getMarketplaceId());
            XMLGregorianCalendarImpl.createDateTime(time.getYear(), time.getMonth(), time.getDay(),time.getHour(),
                    time.getMinute(),time.getSecond());
//            of.setFulfillmentDate(new XMLGregorianCalendarImpl(new GregorianCalendar(time.getYear(), time.getMonth(), time.getDay(),time.getHour(),
//                    time.getMinute(),time.getSecond())));  //发货时间
            of.setFulfillmentDate(XMLGregorianCalendarImpl.createDateTime(time.getYear(), time.getMonth(), time.getDay(),time.getHour(),
                    time.getMinute(),time.getSecond()));  //发货时间
            OrderFulfillment.FulfillmentData fulfillmentData = new OrderFulfillment.FulfillmentData();
            fulfillmentData.setCarrierCode(sysOrder.getShippingCarrierUsedCode()); //物流商代码
            fulfillmentData.setShippingMethod(sysOrder.getShippingCarrierUsed());  //邮寄方式
            fulfillmentData.setShipperTrackingNumber(sysOrder.getShipTrackNumber()); //跟踪号
            of.setFulfillmentData(fulfillmentData);
            for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
                OrderFulfillment.Item item = new OrderFulfillment.Item();   //设置商品
                item.setAmazonOrderItemCode(sysOrderDetail.getSourceOrderLineItemId());//订单项ID
                item.setQuantity(new BigInteger(String.valueOf(sysOrderDetail.getSkuQuantity()))); //商品数量
                itemList.add(item);
            }
            of.getItem().addAll(itemList);           //------------------item   集合
            message.setOrderFulfillment(of); //------------------<OrderFulfillment>
            messageList.add(message);       //-----------添加进message集合
            ae.getMessage().addAll(messageList);
            count++;
     /*   // 上报
        SubmitFeed submitFeed = (SubmitFeed) ApplicationContextProvider.getBean("submitFeed");
        SubmitFeedResponse response = submitFeed.invoke("", "", "xml", "", "");
        String feedSubmissionId = response.getSubmitFeedResult().getFeedSubmissionInfo().getFeedSubmissionId();

        // 获取结果
        String errorMsg = new GetFeedSubmissionListResultReport().invoke("",
                "", feedSubmissionId, "");
*/
        }
        String xml = ClassXmlUtil.toXML(ae);
        xml = xml.replace("<AmazonEnvelope>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                "<AmazonEnvelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"amznenvelope.xsd\">\r");
        System.out.println(xml);
    }
}
