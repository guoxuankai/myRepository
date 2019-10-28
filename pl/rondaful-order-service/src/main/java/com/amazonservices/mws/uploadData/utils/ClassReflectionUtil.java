package com.amazonservices.mws.uploadData.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.datatype.XMLGregorianCalendar;

import com.amazonservices.mws.uploadData.entity.amazon.AmazonAttr;
import com.amazonservices.mws.uploadData.entity.amazon.AmazonNode;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.common.exception.GlobalException;
import com.amazonservices.mws.uploadData.constants.DefVariationTheme;
import com.rondaful.cloud.seller.generated.Product.ProductData;


/**
 * 
 * @author ouxiangfeng
 *
 */
public class ClassReflectionUtil {
	
	public static final String BASE_CLASS_PATH = "com.rondaful.cloud.seller.generated.";
	
	public static final String PRODUCT_TYPE = "productType";
	
	public static final String ROOT_NODE = "ROOT";
	
	private static final Logger logger = LoggerFactory.getLogger(ClassReflectionUtil.class);
	/**
	 * 获取产品分类（第一级）
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public List<AmazonNode>  getFirstXsdTemplateCategory() throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		List<AmazonNode> categoryOne = getFields(new ProductData());
		List<AmazonNode> subNode = new ArrayList<>();
		for(AmazonNode amazonNode : categoryOne)
		{
			Class<?> classz = Class.forName(amazonNode.getClassPath());
			 subNode = new ArrayList<>();
			subNode.addAll(getFields(classz.newInstance(),new String[] {PRODUCT_TYPE}));
			amazonNode.setAmazonNode(subNode);
		}
		return categoryOne;
	}


	public List<AmazonNode>  getNextXsdTemplateCategory(String classPath) throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		List<AmazonNode> categoryOne = getProductTypeFields(Class.forName(classPath));//getFields(Class.forName(classPath));
		List<AmazonNode> subNode = new ArrayList<>();
		for(AmazonNode amazonNode : categoryOne)
		{
			Class<?> classz = Class.forName(amazonNode.getClassPath());
			String fitlers[] = new String[] {"value","ENUM$VALUES","$VALUES"};
			if(!classz.isEnum())
				fitlers = new String[] {};

			subNode = new ArrayList<>();
			subNode.addAll(getFieldsExcept(classz,fitlers));
			amazonNode.setAmazonNode(subNode);
		}
		return categoryOne;
	}

	/**
	 * 	 获取某个类中和属性
	 * @param entity
	 * 		类或对象
	 * @param filters
	 * 		仅包括filters的属性
	 * @return
	 */
	public static  List<AmazonNode> getFields(Object entity,String ...filters )
	{
		AmazonNode an = new AmazonNode();
		List<AmazonNode> anList  = new ArrayList<AmazonNode>();
	/*	List<String> fieldList = new ArrayList<String>();
		List<String> pathList = new ArrayList<String>();*/
		Class<?> classz = null;
		if(entity instanceof Class)
		{
			classz = (Class<?>)entity;
		}else
		{
			classz = entity.getClass();
		}
		Field dataFields [] = classz.getDeclaredFields();
		for(Field field : dataFields) // productData
		{
			if(!isFilterField(field.getName(), filters))
			{
				continue;
			}
			an = new AmazonNode();
			an.setFieldName(field.getName());
			an.setClassPath(field.getType().getName());
			anList.add(an);
		}
		return anList;
	}


	/**
	 * 	 获取某个类中和属性
	 * @param entity
	 * 		类或对象
	 * @param filters
	 * 		不 包括filters的属性
	 * @return
	 */
	public static  List<AmazonNode> getFieldsExcept(Object entity,String ...filters )
	{
		AmazonNode an = new AmazonNode();
		List<AmazonNode> anList  = new ArrayList<AmazonNode>();
	/*	List<String> fieldList = new ArrayList<String>();
		List<String> pathList = new ArrayList<String>();*/
		Class<?> classz = null;
		if(entity instanceof Class)
		{
			classz = (Class<?>)entity;
		}else
		{
			classz = entity.getClass();
		}
		Field dataFields [] = classz.getDeclaredFields();
		for(Field field : dataFields) // productData
		{
			if(isFilterField(field.getName(), filters))
			{
				continue;
			}
			an = new AmazonNode();
			if(field.getType().isEnum())
			{
				XmlEnumValue value = field.getAnnotation(XmlEnumValue.class);
				an.setFieldName(value == null ? field.getName() : value.value());
			}else {
				an.setFieldName(field.getName());
			}
			an.setClassPath(field.getType().getName());
			anList.add(an);
		}
		return anList;
	}

	public static  List<AmazonNode> getProductTypeFields(Object entity)
	{
		AmazonNode an = new AmazonNode();
		List<AmazonNode> anList  = new ArrayList<AmazonNode>();
	/*	List<String> fieldList = new ArrayList<String>();
		List<String> pathList = new ArrayList<String>();*/
		Class<?> classz = null;
		if(entity instanceof Class)
		{
			classz = (Class<?>)entity;
		}else
		{
			classz = entity.getClass();
		}
		Field dataFields [] = classz.getDeclaredFields();
		for(Field field : dataFields) // productData
		{
			if(!"productType".equalsIgnoreCase(field.getName()))
			{
				continue;
			}
			an = new AmazonNode();
			an.setFieldName(field.getName());
			an.setClassPath(field.getType().getName());
			anList.add(an);
		}
		return anList;
	}

	public static  boolean isFilterField(String tagStr, String ...filters)
	{
		if(ArrayUtils.isEmpty(filters))
		{
			return Boolean.TRUE;
		}
		return Arrays.asList(filters).contains(tagStr);
	}

	/**
	 * 	设置模版内的商品属性
	 * @param productData
	 * @param jsonData
	 * @param className
	 */
	public static void setProductData(ProductData productData ,String jsonData, String className)
	{
		try
		{
		Class<?> productDataClass = ProductData.class;
		String decapitalizeClassName = DecapitalizeChar.decapitalizeUpperCase(className);
		Class<?> reqClz = Class.forName(ClassReflectionUtil.BASE_CLASS_PATH + decapitalizeClassName);
		Object obj = JSON.parseObject(jsonData, reqClz);
		// 这里设计大对象，如productDate.setSprot()
		Method method = productDataClass.getMethod("set"+decapitalizeClassName, reqClz);
		method.invoke(productData, obj);

		// 才下涉及到内存引用，非熟悉的人请不要轻易修改
		Method methods[] = reqClz.getMethods();
		for(Method m : methods)
		{
			if(!"setProductType".equals(m.getName())) //setter
			{
				continue;
			}
			// 获取方法参数，
			Class<?> paramClss[] = m.getParameterTypes();
			for(Class<?> clz : paramClss)
			{
				if(clz.isEnum())
				{

					@SuppressWarnings("unchecked")
					java.util.Map<String,Object> mapV = JSON.parseObject(jsonData, java.util.Map.class);
					Method m1 = clz.getMethod("fromValue", String.class);
					Object value =m1.invoke(obj, mapV.get("productType"));
					m.invoke(obj, value);
					break;
				}

			}
			break;
		}

		}catch (Exception e) {
			logger.error("解释类错误", "",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"解释xml异常");
		}
	}



	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {


		//System.out.println(ProductType100EnumSport.fromValue("sportingGoods").value());
		/** 获取一级分类 */
	/*	List<AmazonNode> amazonNode = new ClassReflectionUtil().getFirstXsdTemplateCategory();
		System.out.println(JSON.toJSON( amazonNode));*/

		/** 获取下一级分类   如：com.rondaful.cloud.seller.generated.MiscType  */

		/*TypeUtils.compatibleWithJavaBean = true;
		List<AmazonNode> amazonNode1 = new ClassReflectionUtil().getNextXsdTemplateCategory("com.rondaful.cloud.seller.generated.Product");
		System.out.println(JSON.toJSON( amazonNode1));*/


		/** 获取产品类信息  */
		/*List<AmazonAttr> productAttr = new ArrayList<>();
		AmazonProduct ap = new AmazonProduct();
		ClassReflectionUtil.beanToJson(productAttr, Sports.class,null,new ArrayList<AmazonAttr>());
		System.out.println(JSON.toJSONString(productAttr, SerializerFeature.WriteNullStringAsEmpty));*/
		/* String[] arrayA = new String[] { "1", "2", "3", "3", "4", "5" };
		   String[] arrayB = new String[] { "3", "4", "4", "5", "6", "7" };
		   List<String> a = Arrays.asList(arrayA);
		   List<String> b = Arrays.asList(arrayB);
		   //并集
		   Collection<String> union = CollectionUtils.union(a, b);
		   //交集
		   Collection<String> intersection = CollectionUtils.intersection(a, b);
		   //交集的补集
		   Collection<String> disjunction = CollectionUtils.disjunction(a, b);
		   //集合相减
		   Collection<String> subtract = CollectionUtils.subtract(a, b);

		   Collections.sort((List<String>) union);
		   Collections.sort((List<String>) intersection);
		   Collections.sort((List<String>) disjunction);
		   Collections.sort((List<String>) subtract);

		   System.out.println("A: " + Arrays.toString(a.toArray()));
		   System.out.println("B: " + Arrays.toString(b.toArray()));
		   System.out.println("--------------------------------------------");
		   System.out.println("并集Union(A, B): " + Arrays.toString(union.toArray()));
		   System.out.println("交集Intersection(A, B): " + Arrays.toString(intersection.toArray()));
		   System.out.println("交集的补集Disjunction(A, B): " + Arrays.toString(disjunction.toArray()));
		   System.out.println("差集Subtract(A, B): " + Arrays.toString(subtract.toArray()));   */

		Class<?> reqClz = Class.forName("com.rondaful.cloud.seller.generated.Sports");
		Object obj = JSON.parseObject("{\"productType\": \"GolfClubWood\" , \"materialComposition\": \"rwrwewrw\"}", reqClz);
		System.out.println(obj);
	}

	/**
	  * 	将类所有属性转为json
	 * @param classz
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
/*	private static Object toJson(Class classz) throws IllegalArgumentException, IllegalAccessException, InstantiationException
	{
		// TODO 获取xsd流
		Object obj = classz.newInstance();
		Field fields[] = classz.getDeclaredFields();
		for (Field field : fields) {

			if (isBaseType(field.getType())) //
			{
				continue;
			}
			System.out.println(field.getGenericType().getClass() + "," + field.getType().getName() + ":" + field.getName());

			field.setAccessible(Boolean.TRUE);
			if(isListType(field))
			{
				continue;
			}
			field.set(obj,  field.getType().newInstance()); //实例
		}
		return obj;
	}*/

	/**
	 * 属必是否为list类型
	 * @param field
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private static boolean isListType(Field field) throws InstantiationException, IllegalAccessException
	{
		if(field.getType()  == List.class
				|| Boolean.class == field.getType())
		{
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	

	
	
	/**
	 * @param classz
	 * 		要解释的类
	 * @param productAttr
	 * 		上的个节点的名称,默认为ROOT第一级
	 * @param classz
	 * @param nextList
	 */
	public static void beanToJson(List<AmazonAttr> productAttr, Class<?> classz, AmazonAttr parentAmazonAttr, List<AmazonAttr> nextList) {
		// Class<?> classz = Product.class;
		Field fields[] = classz.getDeclaredFields();
		AmazonAttr attr = null;
		
		try {
			for (Field field : fields) {
				attr = new AmazonAttr();
				// list TODO 获取xsd的集合
				if(isListType(field))
				{
					attr.setAttrName(field.getName());
					attr.setAttrType(field.getType().getName());
					nextList.add(attr);
					continue;
				}
				
				// 是否为基础类型
				// 如果是基础类型，则直接获取获取属性
				if (isBaseType(field.getType()) && !classz.isEnum()) // 基本的数据类型不需要进入深层次类
				{
					attr.setAttrName(field.getName());
					attr.setAttrType(field.getType().getName());
					if(field.getName().equalsIgnoreCase("VariationTheme"))
					{
						attr.setDefaultValue(DefVariationTheme.getVariationTheme(classz.getTypeName()));
					}
					nextList.add(attr);
					if(parentAmazonAttr == null)
					{
						productAttr.add(attr);
					}
					continue;
				}

				// 如果是枚举类，则进入获取枚举值
				if (classz.isEnum()) {
					// 二种内置属性不需要 value,ENUM$VALUES
					if(discarded(field)) continue;
					//nextList.add(attr);
					if(parentAmazonAttr == null)
					{
						productAttr.add(attr);
						continue;
					}
					XmlEnumValue value = field.getAnnotation(XmlEnumValue.class);
					parentAmazonAttr.getDefaultValue().add(value == null ? field.getName() : value.value());
					continue;
				}
				
				// 内层类
				attr.setAttrName(field.getName());
				attr.setAttrType(field.getType().getName());
				nextList.add(attr);
				if(parentAmazonAttr == null)
				{
					productAttr.add(attr);
				}else
				{
					parentAmazonAttr.setNextNodes(nextList);
				}
				beanToJson(productAttr,field.getType(),attr,attr.getNextNodes());
			}
			
			if(parentAmazonAttr != null)
			{
				parentAmazonAttr.setNextNodes(nextList); //productAttr.addAll(nextList);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	/**
	   *   需要过滤掉的属性
	 */
	private static boolean discarded(Field field )
	{
		//value,ENUM$VALUES,$VALUES
		if(field.getName().equals("value")
				|| field.getName().equals("ENUM$VALUES")
				|| field.getName().equals("$VALUES"))
		{
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	/**
	 * 
	 * @param clz
	 * @return
	 */
/*	private static boolean isEnum(Class<?> clz)
	{
		if(clz.isEnum())
		{
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}*/
	
	/*private static String[] EnumtoString(List<Field> fields )
	{
		return fields.stream().map(Field::getName).collect(String::new);
	}*/
	private static boolean isBaseType(Class<?> clz)
	{
		if(String.class == clz 
				|| Integer.class == clz 
				|| clz.isPrimitive()
				|| BigInteger.class == clz || BigDecimal.class == clz
				|| XMLGregorianCalendar.class == clz)
		{
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/** 去除与变体主题无效的数值 */
	public static void repetitionVariationTheme(List<AmazonAttr> productAttr )
	{
		
		if(CollectionUtils.isEmpty(productAttr))
			return;
		
		// 主题内容
		List<String> themes = new ArrayList<>();
		//非题内容
		List<String> unVariationThemes = new ArrayList<>();
		
		// 标记是否需要更改
		boolean isWte = Boolean.FALSE;
		AmazonAttr temp = null;
		for(AmazonAttr attr : productAttr)
		{
			if(!"variationData".equalsIgnoreCase(attr.getAttrName()))
				continue;
			
			List<AmazonAttr>  tempAttr = attr.getNextNodes();
			for(AmazonAttr _temp : tempAttr)
			{
				if("variationTheme".equalsIgnoreCase(_temp.getAttrName()))
				{
					isWte = Boolean.TRUE;
					temp = _temp;
					themes.addAll(_temp.getDefaultValue());
					continue;
				}
				unVariationThemes.add(_temp.getAttrName());
			}
			break;
		}
		if(isWte)
		{
			// 先把首字母改为小写
			// themes.stream().forEach(v ->{ DecapitalizeChar.decapitalizeLowerCase(v);} );
			List<String> temp_themes = new ArrayList<>();
			for(String v : themes)
			{
				temp_themes.add(DecapitalizeChar.decapitalizeLowerCase(v));
			}
			@SuppressWarnings("unchecked")
			Collection<String> intersection = CollectionUtils.intersection(temp_themes, unVariationThemes);
			temp.setDefaultValue((List<String>)intersection);
		}
	/*	for(AmazonAttr _temp : tempAttr)
		{
			if("variationTheme".equalsIgnoreCase(_temp.getAttrName()))
			{
				
			}
		}*/
	}
	
}
