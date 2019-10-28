package com.amazonservices.mws.uploadData.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.rondaful.cloud.seller.generated.AmazonEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.rondaful.cloud.seller.generated.AmazonEnvelope;
//import com.rondaful.cloud.seller.generated.Header;

/**
 * xml与对象转换工具
 * @author ouxiangfeng
 *
 */
public class ClassXmlUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(ClassXmlUtil.class);
	
	/**
	 * 将对象生成xml
	 * @param t
	 * @return
	 */
	public static <T> String toXML(T t)
	{
		try {
			StringWriter writer = new StringWriter();
            JAXBContext context = AmazonJAXBContext.getContextInstance(t.getClass());
			Marshaller marshal = context.createMarshaller();

			marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // 格式化输出
			marshal.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); // 编码格式,默认为utf-8
			marshal.setProperty(Marshaller.JAXB_FRAGMENT, true); // 是否省略xml头信息 
			marshal.setProperty("jaxb.encoding", "utf-8");
			marshal.marshal(t, writer);

			return new String(writer.getBuffer());
		} catch (Exception e) {
			logger.error("生成xml异常。",e);
			return null;
		}
	}
	
	/**
	   * 将xm转换对象
	 * @param t
	 * @return
	 */
	public static <T> T xmlToBean(String xml,Class<T> t) throws JAXBException, IOException{
        JAXBContext context = JAXBContext.newInstance(t);  
        Unmarshaller unmarshaller = context.createUnmarshaller(); 
        T object = (T) unmarshaller.unmarshal(AmazonContentMD5.toInputStream(xml));
        return object;
    }
	
	/**
	   * 将xm转换对象
	 * @param t
	 * @return
	 */
	public static <T> T xmlToBean(InputStream stream,Class<T> t) throws JAXBException, IOException{
        JAXBContext context = AmazonJAXBContext.getContextInstance(t);
//        JAXBContext context = JAXBContext.newInstance(t);
      Unmarshaller unmarshaller = context.createUnmarshaller(); 
      T object = (T) unmarshaller.unmarshal(stream);
      return object;
  }
	
	/**
	   *    测试方法
	 * @return
	 *//*
	private static Object generatedObject()
	{
		AmazonEnvelope envelope = new AmazonEnvelope();
		
		Header header = new Header();
		header.setDocumentVersion("1.0.2.5");
		header.setMerchantIdentifier("ouxiangfeng");
		header.setOverrideReleaseId("xxx");
		envelope.setHeader(header);
		
		envelope.setMarketplaceName("site.id.1");
		AmazonEnvelope.Message  message = new AmazonEnvelope.Message();
		message.setMessageID(BigInteger.valueOf(11));
		envelope.getMessage().add(message);
		return envelope;
		
	}*/
	
	public static void main(String[] args) throws JAXBException, IOException {
	/*	long starttime = System.currentTimeMillis();
		String xml =  ClassXmlUtil.toXML(generatedObject());
		long endtime = System.currentTimeMillis();
		System.out.println(xml);
		AmazonEnvelope ae = ClassXmlUtil.xmlToBean(xml, AmazonEnvelope.class);
		System.out.println(ae);
		System.out.println("耗时："+(endtime - starttime )/1000 + "ms");*/
		
		/*String filepath = "D:\\git\\rondaful-seller-service\\feedSubmissionResult-error.xml";
		AmazonEnvelope aep = ClassXmlUtil.xmlToBean(new FileInputStream(new File(filepath)), AmazonEnvelope.class);
		System.out.println(aep);*/


        AmazonEnvelope ae1 = new ClassXmlUtil().xmlToBean(AmazonContentMD5.toInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                "<AmazonEnvelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\">\r\n" +
                "  <Header>\r\n" +
                "    <DocumentVersion>1.02</DocumentVersion>\r\n" +
                "    <MerchantIdentifier>A3UPZ1E6WSR9P</MerchantIdentifier>\r\n" +
                "  </Header>\r\n" +
                "  <MessageType>ProcessingReport</MessageType>\r\n" +
                "  <Message>\r\n" +
                "    <MessageID>1</MessageID>\r\n" +
                "    <ProcessingReport>\r\n" +
                "      <DocumentTransactionID>121203017959</DocumentTransactionID>\r\n" +
                "      <StatusCode>Complete</StatusCode>\r\n" +
                "      <ProcessingSummary>\r\n" +
                "        <MessagesProcessed>1</MessagesProcessed>\r\n" +
                "        <MessagesSuccessful>1</MessagesSuccessful>\r\n" +
                "        <MessagesWithError>0</MessagesWithError>\r\n" +
                "        <MessagesWithWarning>0</MessagesWithWarning>\r\n" +
                "      </ProcessingSummary>\r\n" +
                "    </ProcessingReport>\r\n" +
                "  </Message>\r\n" +
                "</AmazonEnvelope>"), AmazonEnvelope.class);
        System.out.println(ae1);
	}
}
