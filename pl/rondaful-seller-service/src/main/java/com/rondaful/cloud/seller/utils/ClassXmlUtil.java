package com.rondaful.cloud.seller.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rondaful.cloud.seller.generated.AmazonEnvelope;
import com.rondaful.cloud.seller.generated.Header;
import com.sun.org.apache.xml.internal.serialize.*;
/**
 * xml与对象转换工具
 * 
 * @author ouxiangfeng
 *
 */
public class ClassXmlUtil {

	private static final Logger logger = LoggerFactory.getLogger(ClassXmlUtil.class);

	
	/**
	 * 将对象生成xml
	 * 
	 * @param t
	 * @return
	 */
	@SuppressWarnings("restriction")
	public <T> String toXML(T t)
	{
		return this.toXML(t, null);
	}
	
	
	/**
	 * 将对象生成xml
	 * 
	 * @param t
	 * @return
	 */
	/**
	 * 
	 * @param t
	 * 		生成的对象
	 * @param cDataElements
	 * 		需要增加"<![[ ]]>"的字段
	 * @return
	 * 		返回xml
	 */
	@SuppressWarnings("restriction")
	public <T> String toXML(T t , String [] cDataElements) {
		try {
			if(t == null)
			{
				return "数据不完整，不生成xml";
			}
			// 空节点，如<xxxx/>
			//String reg = "111<([a-zA-Z0-9])*/>";
			String reg = "<BatterySubgroup/>"; 
			// 节点空内容 如:<xxx></xxx>
			String reg1 = "11<([a-zA-Z0-9])*>(\\s*)<([a-zA-Z0-9])*/>";
			
			StringWriter writer = new StringWriter();
			JAXBContext context = AmazonJAXBContext.getContextInstance(t.getClass()); // JAXBContext.newInstance(t.getClass());
			Marshaller marshal = context.createMarshaller();
			
			marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // 格式化输出
			marshal.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); // 编码格式,默认为utf-8
			marshal.setProperty(Marshaller.JAXB_FRAGMENT, true); // 是否省略xml头信息 
			/*marshal.setProperty(Marshaller.JAXB_FRAGMENT,
					new CharacterEscapeHandler() {
						@Override
						public void escape(char[] ch, int start, int length, boolean isAttVal, Writer writer)
								throws IOException {
							writer.write(ch, start, length);
						}
					});
			marshal.marshal(t, writer);
			return new String(writer.getBuffer());*/
			if(cDataElements == null || cDataElements.length <= 0)
			{
				marshal.marshal(t, writer);
				String str = new String(writer.getBuffer());
				return str.replaceAll(reg, ""); //.replaceAll(reg1, "");
			}
			
			OutputFormat of = new 	OutputFormat();
            of.setCDataElements(cDataElements);
                //  new String[] { "^Title"}); //  
            of.setOmitXMLDeclaration(true);
            // set any other options you'd like
             of.setPreserveSpace(true);
            of.setIndenting(true); 
            of.setIndent(2);  
            
            // create the serializer
            ByteArrayOutputStream op = new ByteArrayOutputStream();  
            XMLSerializer serializer = new XMLSerializer(op, of);  
            
            SAXResult result = new SAXResult(serializer.asContentHandler());  
            marshal.marshal(t, result); 
            String str = op.toString("utf-8");
            op.close();
            
            return str.replaceAll(reg, ""); //.replaceAll(reg1, "");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("生成xml异常。", e);
			return null;
		}
	}

	/**
	 * 将xm转换对象
	 * 
	 * @param t
	 * @return
	 */
	public <T> T xmlToBean(String xml, Class<T> t) throws JAXBException, IOException {
		JAXBContext context = AmazonJAXBContext.getContextInstance(t);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		T object = (T) unmarshaller.unmarshal(AmazonContentMD5.toInputStream(xml));
		return object;
	}

	/**
	 * 将xm转换对象
	 * 
	 * @param t
	 * @return
	 */
	public <T> T xmlToBean(InputStream stream, Class<T> t) throws JAXBException, IOException {
		JAXBContext context = AmazonJAXBContext.getContextInstance(t);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		T object = (T) unmarshaller.unmarshal(stream);
		stream.close();
		return object;
	}

	/**
	 * 测试方法
	 * 
	 * @return
	 */
	private Object generatedObject() {
		AmazonEnvelope envelope = new AmazonEnvelope();

		Header header = new Header();
		header.setDocumentVersion("1.0.2.5");
		header.setMerchantIdentifier("ouxiangfeng");
		header.setOverrideReleaseId("xxx");
		envelope.setHeader(header);

		envelope.setMarketplaceName("site.id.1");
		AmazonEnvelope.Message message = new AmazonEnvelope.Message();
		message.setMessageID(BigInteger.valueOf(11));
		envelope.getMessage().add(message);
		return envelope;

	}

	public static void main(String[] args) throws JAXBException, IOException {

		/*
		 * AmazonEnvelope ae = new AmazonEnvelope(); Header header = new Header();
		 * header.setDocumentVersion("1.1"); header.setMerchantIdentifier("xxxxxxxxx");
		 * ae.setHeader(header); ae.setMessageType("Health");
		 * ae.setPurgeAndReplace(Boolean.FALSE); AmazonEnvelope.Message message = new
		 * AmazonEnvelope.Message();
		 * 
		 * 
		 * //---------------setter------------------- Health of = new Health(); //
		 * --------------------------- Product.ProductData pd = new
		 * Product.ProductData();
		 * 
		 * Health.ProductType pt = new Health.ProductType();
		 * pt.setDietarySupplements(new DietarySupplements()); DietarySupplements ds =
		 * pt.getDietarySupplements(); ds.setVariationData(new
		 * DietarySupplements.VariationData()); ds.getVariationData().setColor("color");
		 * 
		 * of.setProductType(pt); of.setWeightRecommendation(new
		 * WeightRecommendationType()); Product product = new Product();
		 * 
		 * product.setProductData(new ProductData());
		 * product.getProductData().setHealth(of); message.setProduct(product);
		 * ae.getMessage().add(message);
		 * 
		 * RawMaterials d= new RawMaterials(); d.setProductType("ss"); String xml =new
		 * ClassXmlUtil().toXML(d); xml = xml.replace("<AmazonEnvelope>",
		 * "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
		 * "<AmazonEnvelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"amznenvelope.xsd\">\r\n"
		 * + ""); System.out.println(xml);
		 */
		// 上报
		/*
		 * SubmitFeed submitFeed = (SubmitFeed)
		 * ApplicationContextProvider.getBean("submitFeed"); SubmitFeedResponse response
		 * = submitFeed.invoke("", "", "xml", "", ""); String feedSubmissionId =
		 * response.getSubmitFeedResult().getFeedSubmissionInfo().getFeedSubmissionId();
		 * 
		 * // 获取结果 String errorMsg = new GetFeedSubmissionListResultReport().invoke("",
		 * "", feedSubmissionId, "");
		 * 
		 * 
		 * 
		 * 
		 * System.out.println(xml);
		 */

		/*
		 * long starttime = System.currentTimeMillis(); String xml =
		 * ClassXmlUtil.toXML(generatedObject()); long endtime =
		 * System.currentTimeMillis(); System.out.println(xml);
		 */
		AmazonEnvelope ae1 = new ClassXmlUtil()
				.xmlToBean(AmazonContentMD5.toInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
						+ "<AmazonEnvelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\">\r\n"
						+ "	<Header>\r\n" + "		<DocumentVersion>1.02</DocumentVersion>\r\n"
						+ "		<MerchantIdentifier>A3UPZ1E6WSR9P</MerchantIdentifier>\r\n" + "	</Header>\r\n"
						+ "	<MessageType>ProcessingReport</MessageType>\r\n" + "	<Message>\r\n"
						+ "		<MessageID>1</MessageID>\r\n" + "		<ProcessingReport>\r\n"
						+ "			<DocumentTransactionID>121203017959</DocumentTransactionID>\r\n"
						+ "			<StatusCode>Complete</StatusCode>\r\n" + "			<ProcessingSummary>\r\n"
						+ "				<MessagesProcessed>1</MessagesProcessed>\r\n"
						+ "				<MessagesSuccessful>1</MessagesSuccessful>\r\n"
						+ "				<MessagesWithError>0</MessagesWithError>\r\n"
						+ "				<MessagesWithWarning>0</MessagesWithWarning>\r\n"
						+ "			</ProcessingSummary>\r\n" + "		</ProcessingReport>\r\n" + "	</Message>\r\n"
						+ "</AmazonEnvelope>"), AmazonEnvelope.class);
		System.out.println(ae1);
		// System.out.println("耗时："+(endtime - starttime )/1000 + "ms");*/

		/*
		 * String filepath =
		 * "D:\\git\\rondaful-seller-service\\feedSubmissionResult-error.xml";
		 * AmazonEnvelope aep = ClassXmlUtil.xmlToBean(new FileInputStream(new
		 * File(filepath)), AmazonEnvelope.class); System.out.println(aep);
		 */

	}
}
