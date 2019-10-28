//package com.amazonservices.mws.orders._2013_09_01.samples;
//
//
//import org.w3c.dom.Document;
//import org.xml.sax.SAXException;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.util.Iterator;
//
//public class test1 {
//    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
//        String msg ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
//                "  <ListOrdersResponse xmlns=\"https://mws.amazonservices.com/\n" +
//                "  Orders/2013-09-01\">\n" +
//                "    <ListOrdersResult>\n" +
//                "      <NextToken>2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=</NextToken>\n" +
//                "      <LastUpdatedBefore>2013-09-25T18%3A10%3A21.687Z</LastUpdatedBefore>\n" +
//                "      <Orders>\n" +
//                "        <Order>\n" +
//                "          <ShipmentServiceLevelCategory>Standard\n" +
//                "          </ShipmentServiceLevelCategory>\n" +
//                "          <ShipServiceLevel>Std JP Kanto8</ShipServiceLevel>\n" +
//                "          <EarliestShipDate>2013-08-20T19:51:16Z</EarliestShipDate>\n" +
//                "          <LatestShipDate>2013-08-25T19:49:35Z</LatestShipDate>\t\n" +
//                "          <MarketplaceId>A1VC38T7YXB528</MarketplaceId>\n" +
//                "          <SalesChannel>Amazon.com</SalesChannel>\n" +
//                "          <OrderType>Preorder</OrderType>\n" +
//                "          <BuyerEmail>5vlhEXAMPLEh9h5@marketplace.Amazon.com</BuyerEmail>\n" +
//                "          <FulfillmentChannel>MFN</FulfillmentChannel>\n" +
//                "          <OrderStatus>Pending</OrderStatus>\n" +
//                "          <BuyerName>John Jones</BuyerName>\t\n" +
//                "          <LastUpdateDate>2013-08-20T19:49:35Z</LastUpdateDate>\n" +
//                "          <PurchaseDate>2013-08-20T19:49:35Z</PurchaseDate>\n" +
//                "          <NumberOfItemsShipped>0</NumberOfItemsShipped>\n" +
//                "          <NumberOfItemsUnshipped>0</NumberOfItemsUnshipped>\n" +
//                "          <AmazonOrderId>902-3159896-1390916</AmazonOrderId>\n" +
//                "          <PaymentMethod>Other</PaymentMethod>\n" +
//                "        </Order>\n" +
//                "        <Order>\n" +
//                "          <AmazonOrderId>058-1233752-8214740</AmazonOrderId>\n" +
//                "          <PurchaseDate>2013-09-05T00%3A06%3A07.000Z</PurchaseDate>      \n" +
//                "          <LastUpdateDate>2013-09-07T12%3A43%3A16.000Z</LastUpdateDate>\n" +
//                "          <OrderStatus>Unshipped</OrderStatus>\n" +
//                "          <OrderType>StandardOrder</OrderType>\n" +
//                "          <ShipServiceLevel>Std JP Kanto8</ShipServiceLevel>\n" +
//                "          <FulfillmentChannel>MFN</FulfillmentChannel>\n" +
//                "          <OrderTotal>\n" +
//                "            <CurrencyCode>JPY</CurrencyCode>\n" +
//                "            <Amount>1507.00</Amount>\n" +
//                "          </OrderTotal>\n" +
//                "          <ShippingAddress>\n" +
//                "            <Name>Jane Smith</Name>\n" +
//                "            <AddressLine1>1-2-10 Akasaka</AddressLine1>\n" +
//                "            <City>Tokyo</City>\n" +
//                "            <PostalCode>107-0053</PostalCode>\n" +
//                "            <Country>JP</Country>\n" +
//                "          </ShippingAddress>\n" +
//                "          <NumberOfItemsShipped>0</NumberOfItemsShipped>\n" +
//                "          <NumberOfItemsUnshipped>1</NumberOfItemsUnshipped>\n" +
//                "          <PaymentExecutionDetail>\n" +
//                "            <PaymentExecutionDetailItem>\n" +
//                "              <Payment>\n" +
//                "                <Amount>10.00</Amount>\n" +
//                "                <CurrencyCode>JPY</CurrencyCode>\n" +
//                "              </Payment>\n" +
//                "              <PaymentMethod>PointsAccount</PaymentMethod>\n" +
//                "            </PaymentExecutionDetailItem>\n" +
//                "            <PaymentExecutionDetailItem>\n" +
//                "              <Payment>\n" +
//                "                <Amount>317.00</Amount>\n" +
//                "                <CurrencyCode>JPY</CurrencyCode>\n" +
//                "              </Payment>\n" +
//                "              <PaymentMethod>GC</PaymentMethod>\n" +
//                "            </PaymentExecutionDetailItem>\n" +
//                "            <PaymentExecutionDetailItem>\n" +
//                "              <Payment>\n" +
//                "                <Amount>1180.00</Amount>\n" +
//                "                <CurrencyCode>JPY</CurrencyCode>\n" +
//                "              </Payment>\n" +
//                "              <PaymentMethod>COD</PaymentMethod>\n" +
//                "            </PaymentExecutionDetailItem>\n" +
//                "          </PaymentExecutionDetail>\n" +
//                "          <PaymentMethod>COD</PaymentMethod>\n" +
//                "          <MarketplaceId>ATVPDKIKX0DER</MarketplaceId>\n" +
//                "          <BuyerName>Jane Smith</BuyerName>\n" +
//                "          <BuyerEmail>5vlhEXAMPLEh9h5@marketplace.Amazon.com</BuyerEmail>\n" +
//                "          <ShipmentServiceLevelCategory>Standard\n" +
//                "          </ShipmentServiceLevelCategory>\n" +
//                "        </Order>\n" +
//                "      </Orders>\n" +
//                "    </ListOrdersResult>\n" +
//                "    <ResponseMetadata>\n" +
//                "      <RequestId>88faca76-b600-46d2-b53c-0c8c4533e43a</RequestId>\n" +
//                "    </ResponseMetadata>\n" +
//                "  </ListOrdersResponse>";
//        test1.readStringXml(msg);
//    }
//   public static void readStringXml(String xml) {
//       try {
//           org.dom4j.Document document = DocumentHelper.parseText(xml);
//           Element rootElement = document.getRootElement();
//           System.out.println(rootElement.getText()); //ListOrdersResponse
//           Iterator it = rootElement.elementIterator("ListOrdersResult");
//           while (it.hasNext()) {
//               Element next = (Element) it.next();
//               Iterator orders = next.elementIterator("Orders");
//               while (orders.hasNext()) {
//                   Element next1 = (Element) orders.next();
//                   Iterator order = next1.elementIterator("Order");
//                   while (order.hasNext()) {
//                       Element next2 = (Element) order.next();
//                       String amazonOrderId = next2.elementTextTrim("AmazonOrderId");
//                       System.out.println(amazonOrderId);
//                   }
//               }
//           }
//       } catch (Exception e) {
//           e.printStackTrace();
//       }
//   }
//}
//
///* public static void readStringXml(String xml) {
//        try {
//            // 读取并解析XML文档
//            // SAXReader就是一个管道，用一个流的方式，把xml文件读出来
//            //
//            // SAXReader reader = new SAXReader(); //User.hbm.xml表示你要解析的xml文档
//            // Document document = reader.read(new File("User.hbm.xml"));
//            // 下面的是通过解析xml字符串的
//            org.dom4j.Document doc = (org.dom4j.Document) DocumentHelper.parseText(xml); // 将字符串转为XML
//
//            Element rootElt = doc.getRootElement(); // 获取根节点
//            System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
//
//            Iterator iter = rootElt.elementIterator("head"); // 获取根节点下的子节点head
//
//            // 遍历head节点
//            while (iter.hasNext()) {
//
//                Element recordEle = (Element) iter.next();
//                String title = recordEle.elementTextTrim("title"); // 拿到head节点下的子节点title值
//                System.out.println("title:" + title);
//
//                Iterator iters = recordEle.elementIterator("script"); // 获取子节点head下的子节点script
//
//                // 遍历Header节点下的Response节点
//                while (iters.hasNext()) {
//
//                    Element itemEle = (Element) iters.next();
//
//                    String username = itemEle.elementTextTrim("username"); // 拿到head下的子节点script下的字节点username的值
//                    String password = itemEle.elementTextTrim("password");
//
//                    System.out.println("username:" + username);
//                    System.out.println("password:" + password);
//                }
//            }
//            Iterator iterss = rootElt.elementIterator("body"); ///获取根节点下的子节点body
//            // 遍历body节点
//            while (iterss.hasNext()) {
//
//                Element recordEless = (Element) iterss.next();
//                String result = recordEless.elementTextTrim("result"); // 拿到body节点下的子节点result值
//                System.out.println("result:" + result);
//
//                Iterator itersElIterator = recordEless.elementIterator("form"); // 获取子节点body下的子节点form
//                // 遍历Header节点下的Response节点
//                while (itersElIterator.hasNext()) {
//
//                    Element itemEle = (Element) itersElIterator.next();
//
//                    String banlce = itemEle.elementTextTrim("banlce"); // 拿到body下的子节点form下的字节点banlce的值
//                    String subID = itemEle.elementTextTrim("subID");
//
//                    System.out.println("banlce:" + banlce);
//                    System.out.println("subID:" + subID);
//                }
//            }
//        } catch (DocumentException e) {
//            e.printStackTrace();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//    }*/
