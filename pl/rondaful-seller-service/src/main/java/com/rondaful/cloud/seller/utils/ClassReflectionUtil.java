package com.rondaful.cloud.seller.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.seller.entity.AmazonTemplateAttribute;
import com.rondaful.cloud.seller.entity.amazon.AmazonAttr;
import com.rondaful.cloud.seller.entity.amazon.AmazonNode;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.generated.Product;
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


	private static final String CLASS_TYPE_ENUM = "enums";
	private static final String CLASS_TYPE_OBJECT = "Object";
	private static final String CLASS_TYPE_BASETYPE = "BaseType";

	private static final String VARIATIONDATA = "variationData";
	private static final String VARIATIONTHEME = "variationTheme";
	private static final String CLASSIFICATIONDATA = "classificationData";

	private static final String COLOR_SPECIFICATION = "colorSpecification";

	private static final Logger logger = LoggerFactory.getLogger(ClassReflectionUtil.class);

	/** options的分隔符 */
	public static final String ATTR_ENUM_VALUES_PREX = "\\|";

	private static List<String> attributeTrues = Arrays.asList(
			"itemDisplayHeight",
			"itemDisplayLength",
			"itemDisplayWeight",
			"itemDisplayWidth",
			"fileSize",
			"minimumManufacturerAgeRecommended",
			"maximumManufacturerAgeRecommended",
			"minimumMerchantAgeRecommended",
			"maximumMerchantAgeRecommended",
			"graphicsCardRamSize",
			"batteryCapacity",
			"graphicsRAMSize",
			"hardDriveSize",
			"processorSpeed",
			"ramSize",
			"itemVolume",
			"pitchCircleDiameter",
			"unitCount"
	);




	/**
	* 获取产品分类（第一级）
	* @return
	* @throws ClassNotFoundException
	* @throws IllegalAccessException
	* @throws InstantiationException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	*/
	public List<AmazonNode>  getFirstXsdTemplateCategory() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException
	{
		List<AmazonNode> categoryOne = getFields(new Product.ProductData());
		List<AmazonNode> subNode = new ArrayList<>();
		for(AmazonNode amazonNode : categoryOne)
		{
			Class<?> classz = Class.forName(amazonNode.getClassPath());
			 subNode = new ArrayList<>();
			subNode.addAll(getFields(classz.newInstance(),new String[] {PRODUCT_TYPE,"clothingType"}));
			amazonNode.setAmazonNode(subNode);
		}


		return  categoryOne;
	}



	public List<AmazonNode>  getNextXsdTemplateCategory(String classPath) throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		List<AmazonNode> categoryOne =getFieldsExcept(Class.forName(classPath),new String [] {"value","ENUM$VALUES","$VALUES"}); //getProductTypeFields(Class.forName(classPath), level);//getFields(Class.forName(classPath));
		return categoryOne;
	}

	/**
	 * 	 获取某个类中和属性
	 * @param entity
	 * 		类或对象
	 * @param filters
	 * 		仅包括filters的属性
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public  List<AmazonNode> getFields(Object entity,String ...filters ) throws NoSuchFieldException, SecurityException
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
			if(classz.getSimpleName().equalsIgnoreCase("Clothing") && field.getName().equalsIgnoreCase("ClassificationData"))
			{
				Field clothingField = field.getType().getDeclaredField("clothingType");
				an.setFieldName(clothingField.getName());
				an.setClassPath(clothingField.getType().getName());
				an.setType(clothingField.getType().isEnum() ? CLASS_TYPE_ENUM : CLASS_TYPE_OBJECT);
				anList.add(an);
				continue;
			}

			if(!isFilterField(field.getName(), filters))
			{
				continue;
			}
			an = new AmazonNode();

			XmlElement element = field.getAnnotation(XmlElement.class);
			if(element!= null)
			{
				an.setRequired(element.required());
			}

			an.setFieldName(field.getName());
			an.setClassPath(field.getType().getName());
			an.setType(CLASS_TYPE_OBJECT);
			if(field.getType().isEnum())
			{
				an.setType(CLASS_TYPE_ENUM);
			}
			anList.add(an);
		}
		return anList;
	}


	private  Class<?> getSubClass(Class<?> templementClass,String subClassName)
	{

		String decapitalizeClassName = DecapitalizeChar.decapitalizeUpperCaseFirst(subClassName);// DecapitalizeChar.decapitalizeUpperCase(subClassName);

		Class<?> reqClz = null;
		try {
			reqClz = Class.forName(ClassReflectionUtil.BASE_CLASS_PATH + decapitalizeClassName);
		} catch (ClassNotFoundException e) {
			try {
				reqClz = Class.forName(templementClass.getTypeName() +"$" + decapitalizeClassName);
			} catch (ClassNotFoundException e1) {
				return null;
			}
		}
		return reqClz;
	}

	/**
	 *   variationData中是否存在 fieldName
	 * @param entity
	 * 		二级分类对象
	 * @param fieldName
	 * @return
	 */
	public JSONObject setParentage(Object entity,String fieldName ,JSONObject jsonObj,boolean isPrent)
	{
		Class<?> classz = null;
		if(entity instanceof Class)
		{
			classz = (Class<?>)entity;
		}else
		{
			classz = entity.getClass();
		}

		JSONObject subJson  = new JSONObject();
		if( jsonObj.get("productType")  instanceof JSONObject)
		{
			subJson =  (JSONObject) jsonObj.getJSONObject("productType").get(DecapitalizeChar.decapitalizeLowerCase(classz.getSimpleName()));
		}

		Field dataFields [] = classz.getDeclaredFields();
		for(Field field : dataFields) // productData
		{
			if("variationData".equalsIgnoreCase(field.getName()) && !isBaseType(field.getType()))
			{
				Field datas [] = field.getType().getDeclaredFields();
				for(Field data : datas)
				{
					if(fieldName.equalsIgnoreCase(data.getName()))
					{
						if(subJson.get("variationData") != null)
						{
							subJson.getJSONObject("variationData").put("parentage", isPrent ? "parent" : "child");
							return jsonObj;
						}
					}else
					{
						if(subJson.get("variationData") != null)
						{
							subJson.getJSONObject("variationData").remove("parentage");
						}
						return null;
					}
				}
			}
		}
		return null;

	}


	/**
	 * 	 获取某个类中和属性
	 * @param entity
	 * 		类或对象
	 * @param filters
	 * 		不 包括filters的属性
	 * @return
	 */
	public  List<AmazonNode> getFieldsExcept(Object entity,String ...filters )
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
			// 自定义过滤掉名称
			if(discarded(filters,field.getName()))
			{
				continue;
			}
			// 玫举类默认过滤掉的名称
			if(field.getType().isEnum() && discarded(field))
			{
				continue;
			}
			an = new AmazonNode();
			XmlElement element = field.getAnnotation(XmlElement.class);
			if(element!= null)
			{
				an.setRequired(element.required());
			}
			an.setClassPath(field.getType().getName());
			if(field.getType().isEnum())
			{
				XmlEnumValue value = field.getAnnotation(XmlEnumValue.class);
				an.setFieldName(value == null ? field.getName() : value.value());
				// an.setFieldName(field.getName());
				an.setType(CLASS_TYPE_ENUM);
			}else if(isBaseType(field.getType())) {
				an.setFieldName(field.getName());
				an.setType(CLASS_TYPE_BASETYPE);
				//an.setClassPath(classz.getName());
			}else
			{
				an.setFieldName(field.getName());
				an.setType(CLASS_TYPE_OBJECT);
			}

			anList.add(an);
		}
		return anList;
	}

	public  List<AmazonNode> getProductTypeFields(Object entity ,Integer level)
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
			/*if(classz.getName().indexOf("$")!= -1)
			{

			}*/
			if(!"productType".equalsIgnoreCase(field.getName()) && level == 2)
			{
				continue;
			}
			an = new AmazonNode();
			XmlElement element = field.getAnnotation(XmlElement.class);
			if(element!= null)
			{
				an.setRequired(element.required());
			}
			an.setFieldName(field.getName());
			an.setClassPath(field.getType().getName());
			an.setType("object");
			if(field.getType().isEnum())
			{
				an.setType("enum");
			}
			anList.add(an);
		}
		return anList;
	}

	public  boolean isFilterField(String tagStr, String ...filters)
	{
		if(ArrayUtils.isEmpty(filters))
		{
			return Boolean.TRUE;
		}
		return Arrays.asList(filters).contains(tagStr);
	}


/*	private static <T> T TresolveEnumValue(String value, Class<T> type) {

		String uploadvalue = DecapitalizeChar.toUplowerStr(value);
	    for (T constant : type.getEnumConstants()){
	        if (constant.toString().equalsIgnoreCase(value)
	        		|| constant.toString().equalsIgnoreCase(uploadvalue)) {
	            return constant;
	        }
	    }
	    return null;
	}*/




	/**
	 * 根据jdk标准，将参数值反译成对应的标谁
	 * @param obj
	 * @param jsonObj
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	private void setEnumUplower(Class<?> obj,String subSipmleClassName,JSONObject jsonObj,Class<?> sourceClassz,JSONObject topObj)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException,
			NoSuchFieldException, SecurityException, ClassNotFoundException
	{

		if(jsonObj.isEmpty()) // json为空时，不需要做任何处理
		{
			logger.debug(jsonObj.toJSONString());
			return;
		}

		String classzName = obj.getSimpleName();
		Field fields[] = obj.getDeclaredFields();

		/** 针对cameraPhoto做补充，因为这个模板没有VariationData，真接为variationTheme,所以将变体的数据复制到一级属性 */
		if(obj.getSimpleName().equals("CameraPhoto"))
		{
			JSONObject cameraPhoto = new JSONObject();
			//cameraPhoto.put(VARIATIONTHEME, ((JSONObject)jsonObj.get(VARIATIONDATA)));
			//jsonObj.put(VARIATIONDATA, cameraPhoto);
			cameraPhoto =  (JSONObject) jsonObj.get(VARIATIONDATA);
			for(String key : cameraPhoto.keySet())
			{
				jsonObj.put(key, cameraPhoto.get(key) );
			}
		}

		for(Field f :fields )
		{
			String fieldName = f.getName();
			if(jsonObj.isEmpty() || jsonObj.get(f.getName()) == null
					|| StringUtils.isBlank(jsonObj.get(f.getName()).toString())
					|| "{}".endsWith(jsonObj.get(f.getName()).toString()))
			{
				// 如果他它分类名称，则保留
				if(subSipmleClassName.equalsIgnoreCase(f.getName()))
				{
					continue;
				}
		 		jsonObj.remove(f.getName()); //删除无值节点
				continue;

			}


			if(VARIATIONDATA.equals(fieldName) //变体数据
					|| (VARIATIONTHEME.equals(fieldName) && classzName.equals("CameraPhoto")))  //CameraPhoto模板特殊处理，因为这个模板没有variationData属性
			{

				// 将变体数据增加到当前主属性中
				JSONObject variationDataJsonObject =  (JSONObject)jsonObj.get(VARIATIONDATA);
				if(variationDataJsonObject==null || variationDataJsonObject.isEmpty())
				{
					continue;
				}
				Set<String>  variationDataJsonKeys = variationDataJsonObject.keySet();

				//ClassificationData
				JSONObject classificationDataJsonObject = (JSONObject)jsonObj.get(CLASSIFICATIONDATA);
				if(classificationDataJsonObject != null)
				{
					for(String variationDatakey : variationDataJsonKeys)
					{
	//					V2.4_7.2 copy variationdata的数据classificationdata
						// 增加这个代码的原因是前端将classificationdata的属性放在variationdata下了。
						// 注意：如果classificationdata与variationdata有相同的属性的话
						XmlType xmlType = f.getType().getAnnotation(XmlType.class);
						List<String> propOrder = Arrays.asList(xmlType.propOrder());
						if(!propOrder.contains(variationDatakey))
						{
							classificationDataJsonObject.put(variationDatakey, variationDataJsonObject.get(variationDatakey));
						}
					}
				}

				//variationDataJsonKeys.contains(o)
				// 迭代参数中的variationData中的key
				for(String variationDatakey : variationDataJsonKeys)
				{
					//这个colorSpecification特殊处理的,专为处理color。
					if(variationDataJsonKeys.contains(COLOR_SPECIFICATION))
					{
						jsonObj.put(COLOR_SPECIFICATION, variationDataJsonObject.get(COLOR_SPECIFICATION));
					}
					//如果当前key是主题
					if(VARIATIONTHEME.equalsIgnoreCase(variationDatakey))
					{
						Field themeClz = null;
						if(!f.getType().isEnum())
						{
							themeClz = f.getType().getDeclaredField("variationTheme");
						}else
						{
							themeClz = f;
						}
						String variationThemeValue = (String) variationDataJsonObject.get(variationDatakey);
						if(themeClz.getType().isEnum())
						{
							Object objArr [] = themeClz.getType().getEnumConstants();

							for(Object o : objArr)
							{
								
								/*if(variationThemeValue.indexOf("-") != -1) // 如果不跳出来，下面的代码会获取原值。
								{
									break;
								}*/
								String variationThemeValueTmp = DecapitalizeChar.toUplowerStr(variationThemeValue);
								XmlEnumValue value = o.getClass().getDeclaredField(variationThemeValueTmp).getAnnotation(XmlEnumValue.class);
								String v = (value == null) ? themeClz.getName() : value.value();
								variationDataJsonObject.put(variationDatakey, v);
								break;
							}

						}

						// key中有可能会出现ColorName-styleName的情况
						//String variationThemeValue = (String) variationDataJsonObject.get(variationDatakey);
						logger.debug("当前选择的主题：{}",variationThemeValue);
						String variationThemeArr[] = variationThemeValue.split("-");
						for(String var : variationThemeArr)
						{
							var = DecapitalizeChar.decapitalizeLowerCase(var);
							Object muitVal = variationDataJsonObject.get(var);
							if(muitVal == null) //如果parent前端传过来的参数变体部分没有主题值的话，需要从主parent节点取
							{
								muitVal = jsonObj.get(var);
							}
							//因为Size与SizeMap、color与colorMap是组合属性(注意：color是在ColorSpecification中，前面做过处理了 ，size和color格式是不一样的)
							if("size".equals(var))
							{
								Object sizeMapVal = variationDataJsonObject.get("sizeMap");
								jsonObj.put("sizeMap", sizeMapVal);
							}
							if("color".equalsIgnoreCase(var) ){                 // todo  临时处理线上问题代码
								Object colorMap = variationDataJsonObject.get("colorMap");
								if(colorMap != null)
								    jsonObj.put("colorMap", colorMap);
							}
							jsonObj.put(var, muitVal);

							//TODO 查看VariationData下是否存在该属性
							Field varfields []  = f.getType().getDeclaredFields();
							String varfieldNames []  = new String[varfields.length];
							for(int i = 0 ; i< varfields.length; i++)
							{
								varfieldNames[i] = varfields[i].getName();
							}
							//如果variationData中存在var属性，则写入当variationData对象中
							if(Arrays.asList(varfieldNames).contains(var))
							{
								logger.debug("发现variationData存在主题相同的属性:{}",var);
								// jsonObj.put(var, muitVal);
								// variationData中存了，在classificationData中需要删除，这样就不会产生二个地方需存在相同名称的属性
								if(variationDataJsonObject != null && !variationDataJsonObject.isEmpty())
								{
									variationDataJsonObject.put(var, muitVal);
								}
								if(classificationDataJsonObject != null && !classificationDataJsonObject.isEmpty())
								{
									classificationDataJsonObject.put(var, null);
								}
								continue;
							}else // 如不在variationData中的话，那就得查看当前类中是否存
							{
								logger.debug("variationData不存在主题相同的属性:{}",var);
								logger.debug("检查当前类:{} 中是否存在与主题相同的属性:{}",f.getDeclaringClass().getName(),var);
								Field declaredfields []  = f.getDeclaringClass().getDeclaredFields(); //上一级类
								String declaredfieldNames []  = new String[declaredfields.length];
								for(int i = 0 ; i< declaredfields.length; i++)
								{
									declaredfieldNames[i] = declaredfields[i].getName();
								}
								//如果variationData中存在var属性，则写入当variationData对象中
								if(Arrays.asList(declaredfieldNames).contains(var))
								{
									logger.debug("发现{}中存在主题相同的属性:{}，增加属性到是同级的json中",f.getDeclaringClass().getName(),var);
									jsonObj.put(var, muitVal);
								}else
								{
									logger.debug("variationData与{}都没有发现属性{}与主题相同的属性:{}",f.getDeclaringClass().getName(),var);
									logger.debug("向classificationData中增加属性:{}",var);
									if(classificationDataJsonObject == null /*&& !classificationDataJsonObject.isEmpty()*/)
									{
										classificationDataJsonObject = new JSONObject();
									}
									classificationDataJsonObject.put(var, muitVal);
									topObj.put(var, muitVal);



									/*if(variationDataJsonObject != null && !variationDataJsonObject.isEmpty())
									{
										variationDataJsonObject.put(var, null);
									}*/
								}
							}
						}
						break;
					}
				}
			}
			// 动态设置玫举名称，jdk.xjc标准
			if(!isBaseType(f.getType()) && !f.getType().isEnum())
			{
				if(java.util.List.class ==  f.getType())
				{
					if(f.getGenericType() != null ) {
						// String.class 之外
						if (f.getGenericType() == Boolean.class
								|| f.getGenericType() == Integer.class
								|| BigInteger.class == f.getGenericType()
								|| BigDecimal.class == f.getGenericType()
								|| XMLGregorianCalendar.class == f.getGenericType()
								|| String.class == f.getGenericType()) {
							continue;
						}
						ParameterizedType t = (ParameterizedType) f.getGenericType();
						Class temCenericType = Class.forName(t.getActualTypeArguments()[0].getTypeName());

						// instanceof 兼容前端，泛型list只传一个值,无中括号"[]"，前端将list当普通的字符串传入时
						Object tempObj = jsonObj.get(f.getName());
						if (tempObj != null &&  tempObj instanceof String) {


							JSONArray tempArr = new JSONArray();
							tempArr.add(jsonObj.getString(f.getName()));
							jsonObj.put(f.getName(), tempArr);
						}

						if(temCenericType.isEnum()){
							JSONArray array = jsonObj.getJSONArray(f.getName());
							if(array!=null && !array.isEmpty()){
								for(int i = 0; i< array.size(); i++){
									array.set(i,DecapitalizeChar.toUplowerStr(String.valueOf(array.get(i))));
								}
							}
						}else // 清空list中有空值的数据
						{
							JSONArray array = jsonObj.getJSONArray(f.getName());
							for(int i = 0; i< array.size(); i++){
								// array.set(i,DecapitalizeChar.toUplowerStr(String.valueOf(array.get(i))));
								Object objvalue = array.get(i);
								if(objvalue instanceof String)
								{
									if(objvalue == null || StringUtils.isBlank(objvalue.toString()))
									{
										jsonObj.remove(f.getName());
									}
									continue;
								}


								JSONObject o = (JSONObject) objvalue;
								if(o == null)
								{
									jsonObj.remove(f.getName());
									continue;
								}
								Set<String> keys = o.keySet();
								String strKeys [] = new String[keys.size()];
								keys.toArray(strKeys);
								for(String key : strKeys)
								{
									if(o.get(key) == null || StringUtils.isBlank(o.get(key).toString())
											|| o.get(key).toString().equals("{}"))
									{
										o.remove(key);
									}
								}
								jsonObj.put(f.getName(), array);
								if(o.isEmpty())
								{
									jsonObj.remove(f.getName());
								}
							}
						}


					}
					continue;
				}
				// 兼容productType中有对象，有string类型等，多类种组合。
				if(jsonObj.get(f.getName()).getClass() == String.class && PRODUCT_TYPE.equalsIgnoreCase(f.getName()) )
				{
					JSONObject producttypeObj = new JSONObject();
					producttypeObj.put(jsonObj.get(f.getName()).toString(), jsonObj.get(f.getName()));
					jsonObj.put(f.getName(), producttypeObj); //{"spirits":"spirits"}
					//jsonObj.put(f.getName(), producttypeObj);
					continue;
					//setEnumUplower(f.getType(),jsonObj.getJSONObject(f.getName()));;
				}
				if(jsonObj.getJSONObject(f.getName()) != null)
				{
					setEnumUplower(f.getType(),subSipmleClassName,jsonObj.getJSONObject(f.getName()),sourceClassz,topObj);
				}
			}
			if(f.getType().isEnum())
			{
				f.setAccessible(Boolean.TRUE);
				String name = f.getName();
				Object o = jsonObj.get(f.getName());
				String val = (String) jsonObj.get(f.getName());
				if(val == null)
				{
					continue;
				}
				jsonObj.put(f.getName(), DecapitalizeChar.toUplowerStr(val));
			}
			}

	}



	/**
	 * 	设置模版内的商品属性
	 * @param productData
	 * @param jsonData
	 * @param className
	 */
	public void setProductData(ProductData productData ,String jsonData, String className,String subClassName,boolean isParent)
	{
		try
		{
			// 以下代码顺序不可变。
			Class<?> productDataClass = ProductData.class;
			String decapitalizeClassName = DecapitalizeChar.decapitalizeUpperCase(className);
			Class<?> reqClz = Class.forName(ClassReflectionUtil.BASE_CLASS_PATH + decapitalizeClassName);

			//设置玫举
			JSONObject jsonObj = JSON.parseObject(jsonData);

			/*if( jsonObj.get("productType")  instanceof JSONObject)
			{
				JSONObject subJson =  (JSONObject) jsonObj.getJSONObject("productType").get(subClassName);
			}
			*/
			if(StringUtils.isNotBlank(subClassName))
			{
				Class subClass = getSubClass(reqClz, subClassName);
				if(subClass != null )
				{
					JSONObject extJson = setParentage(subClass, "parentage",jsonObj,isParent);
					if(extJson == null)
					{
						jsonObj.put("parentage", isParent ? "parent" : "child");
						//deleteMatcher(jsonObj,"parentage");
					}
				}
			}




			// 转换在jdk编译标准。
			setEnumUplower(reqClz,subClassName ,jsonObj,reqClz,jsonObj);
			logger.debug("根据jdk标准，将参数值反译成对应的标谁,class={},json={}",reqClz.getName(),jsonObj);

		    Object obj = jsonObj.toJavaObject(reqClz);
			//Object obj = jsonObj.parseObject(jsonObj.toJSONString(), reqClz, jsonConfig, null);
			//Object obj = JSON.parseObject(jsonObj.toJSONString(), reqClz);

			// 这里设计大对象，如productDate.setSprot()
			Method method = productDataClass.getMethod("set"+decapitalizeClassName, reqClz);
			method.invoke(productData, obj);
		}catch(JSONException e)
		{
			logger.error("解释类错误，请检查您输入项的数据类型是否正确", "",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"解释xml异常,请检查您输入项的数据类型是否正确");

		}catch (Exception e) {
			logger.error("解释类错误", "",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"解释xml异常");
		}
	}

	/**
	 * 获取指定字符串出现的次数
	 * @param srcText 源字符串
	 * @param findText 要查找的字符串
	 * @return
	 */
	private int matcherCount( String srcText, String findText)
	{
		int count = 0;
	    Pattern p = Pattern.compile(findText);
	    Matcher m = p.matcher(srcText);
	    while (m.find()) {
	        count++;
	    }
	   return count;
	}

	/**
	 * 	删除最外层的json属性
	 * @param jsonObj
	 * @param findText
	 */
	private void deleteMatcher(JSONObject jsonObj,String findText)
	{
		int count = matcherCount( jsonObj.toJSONString(), findText);
		if( count > 1) // 出现过二次的才会删除最外层的。
	    {
	    	jsonObj.remove(findText);
	    }
	}

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, DatatypeConfigurationException {


		// String jsonData = "{\"parentage\":\"child\",\"classificationData\":{},\"productType\":{\"advertisementCollectibles\":{\"theme\":\"as\",\"additionalProductInformation\":\"sda\",\"originality\":\"Original Limited Edition\",\"gradeRating\":\"asd\",\"genre\":\"asd\"},\"variationData\":{}}}";
		//health,healthMisc
		 String jsonData = "{\"parentage\":\"child\",\"classificationData\":{},\"productType\":{\"sportinggoods\":{}},\"battery\":{\"batterySubgroup\":[{}]},\"variationData\":{}}";
		//CameraPhoto,DigitalCamera
		//String jsonData = "{\"classificationData\":{},\"parentage\":\"child\",\"variationData\":{\"variationTheme\":\"StyleName-CustomerPackageType\",\"StyleName\":\"dddd\",\"customerPackageType\":\"1\"},\"productType\":{\"digitalCamera\":{\"computerPlatform\":[{}]}},\"battery\":{\"batterySubgroup\":[{}]},\"rebate\":[{}]}";
        //String jsonData = "{\"parentage\":\"child\",\"classificationData\":{},\"productType\":{\"computerProcessor\":{\"processorBrand\":\"3\",\"processorCount\":\"3\",\"processorSeries\":\"intel_atom_230\",\"processorSpeed\":{\"value\":\"3\",\"unitOfMeasure\":\"KHz\"},\"computerMemoryType\":[\"ddr2_sdram\"],\"variationData\":{}}},\"battery\":{\"batterySubgroup\":[{}]}}";
		Product pr = new Product();
		pr.setProductData(new ProductData());
		ClassReflectionUtil reflection = new ClassReflectionUtil();
		//reflection.setProductData(pr.getProductData(), jsonData, "baby","stroller",true);
		reflection.setProductData(pr.getProductData(), jsonData, "sports","sportinggoods",false);
		//reflection.setProductData(pr.getProductData(), jsonData, "computers","computerProcessor",false);
		System.out.println(jsonData);
		System.out.println(new ClassXmlUtil().toXML(pr));
		/*
		Gourmet gourmet = new Gourmet();
		Gourmet.ProductType t = new Gourmet.ProductType();
		t.setGourmetMisc(new GourmetMisc());
		gourmet.setProductType(t);
		pr.getProductData().setGourmet(gourmet);
		
		System.out.println(ClassXmlUtil.toXML(pr));
		System.out.println(JSON.toJSON( pr));*/
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
		
		/*Class<?> reqClz = Class.forName("com.rondaful.cloud.seller.generated.Sports");
		Object obj = JSON.parseObject("{\"productType\": \"GolfClubIro\",\"variationData\": {\"materialComposition\": \"sfsdfds\",\"packaging\": \"sdfsdf\",\"parentage\": \"parent\",\"apparentScaleSize\": {\"vlaue\": 22,\"unitOfMeasure\": \"M\"}}}""{\"productType\": \"GolfClubWood\" , \"materialComposition\": \"rwrwewrw\"}", reqClz);
		System.out.println(obj);*/

		// List<AmazonNode> list1 = new ClassReflectionUtil().getFirstXsdTemplateCategory();

//		List<AmazonNode> list = new ClassReflectionUtil().getNextXsdTemplateCategory(
//				"com.rondaful.cloud.seller.generated.LightMeter");
//		System.out.println(JSON.toJSONString(list));


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
	public static boolean isListType(Field field) throws InstantiationException, IllegalAccessException
	{
		if(field.getType()  == java.util.List.class
				|| Boolean.class == field.getType())
		{
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}



	/**
	 * 是否包括VariationTheme属性
	 * @param productAttr
	 * @return
	 */
	public static boolean isExtendsVariationTheme(List<AmazonAttr> productAttr)
	{
		for(AmazonAttr attr : productAttr)// 第一层，不能用递归
		{

			if("variationData".equalsIgnoreCase(attr.getAttrName()))
			{
				List<AmazonAttr>  tempAttr = attr.getNextNodes();
				for(AmazonAttr _temp : tempAttr)
				{
					if("variationTheme".equalsIgnoreCase(_temp.getAttrName()))
					{
						return Boolean.TRUE;
					}
				}
			}
		}

		return Boolean.FALSE;
	}


	// 清除多余的数属性
	public static void clearSurplusForAttr(List<AmazonAttr> productParentAttr, List<AmazonAttr> productChildAttr )
	{

		// 如果二级模板为空，则说明没有二级模板。否则使用二级模板的数据。
		List<AmazonAttr> tempAttr = CollectionUtils.isEmpty(productChildAttr) ?  productParentAttr : productChildAttr;
		List<String> composeAttr = new ArrayList<>();
		for(AmazonAttr attr : tempAttr)
		{
			// 不是variationData下的属性不处理
			if(!"variationData".equalsIgnoreCase(attr.getAttrName()))
			{
				continue;
			}
			for(AmazonAttr attr1  : attr.getNextNodes())
				composeAttr.add(attr1.getAttrName());
		}

		// 变体与属性的中有重复的属笥
		for(AmazonAttr attr : tempAttr)
		{
			// 不是ClassificationData下的属性不处理
			if(!"ClassificationData".equalsIgnoreCase(attr.getAttrName()))
			{
				continue;
			}

			for(int i = 0 ; i < attr.getNextNodes().size(); i++)
			{
				if(composeAttr.contains(attr.getNextNodes().get(i).getAttrName()))
				{
					attr.getNextNodes().remove(i--);
				}
			}
		}


		//TODO 一、二级的模板的属性可能有相同，取二级的即可。



	}

	// 清除一二级存在相同的属性
	public static void clearParentAndChildAttr(List<AmazonAttr> productParentAttr, List<AmazonAttr> productChildAttr )
	{
		if(CollectionUtils.isEmpty(productParentAttr) || CollectionUtils.isEmpty(productChildAttr))
		{
			return;
		}

		for(int i = 0; i < productParentAttr.size(); i++ )
		{
			for(int j = 0; j < productChildAttr.size(); j++ )
			{
				if(productParentAttr.get(i).getAttrName().equals(productChildAttr.get(j).getAttrName()))
				{
					productParentAttr.remove(i);
					break;
				}
			}
		}


	}
	/**
	 * @param classz
	 * 		要解释的类
	 * @param productAttr
	 * 		上的个节点的名称,默认为ROOT第一级
	 * @param classz
	 * @param nextList
	 */
	public static void beanToJson(List<AmazonAttr> productAttr, Class<?> classz,
			AmazonAttr parentAmazonAttr,List<AmazonAttr> nextList,
			List<AmazonTemplateAttribute> attrList) {
		// Class<?> classz = Product.class;
		Field fields[] = classz.getDeclaredFields();
		AmazonAttr attr = null;

		try {
			if(classz.getSimpleName().equalsIgnoreCase("StoneType"))
			{
				System.out.println("StoneType");
			}
			for (Field field : fields) {
				attr = new AmazonAttr();
				if(field.getName().equalsIgnoreCase(PRODUCT_TYPE))
				{
					attr.setAttrName(field.getName());
					attr.setAttrType(field.getType().getName());
					productAttr.add(attr); //TODO 过滤
					continue;
				}
				// 如果数据库中有必填的配置
				if(CollectionUtils.isNotEmpty(attrList))
				{
					for(AmazonTemplateAttribute dbAttr : attrList)
					{
						if(dbAttr.getAttributeName().indexOf(".") != -1)
						{
							String selectSplit [] = dbAttr.getAttributeName().split("\\.");
							if(field.getName().equalsIgnoreCase(selectSplit[1]) &&
									parentAmazonAttr != null &&
									parentAmazonAttr.getAttrName().equalsIgnoreCase(selectSplit[0]))
							{
								if(StringUtils.isNotBlank(dbAttr.getOptions()))
								{
									String arrays [] = dbAttr.getOptions().split(ATTR_ENUM_VALUES_PREX);
									List<String> listValues = new ArrayList<>(arrays.length);
									Collections.addAll(listValues, arrays);
									attr.setDefaultValue(listValues);
								}
								attr.setRequired(dbAttr.getRequired() == 1 ? Boolean.TRUE : Boolean.FALSE);
							}
						}
						if(!field.getName().equalsIgnoreCase(dbAttr.getAttributeName()))
						{
							continue;

						}
						if( dbAttr.getRequired() == 1) //必填
						{
							attr.setRequired(Boolean.TRUE);
						}

						if(StringUtils.isNotBlank(dbAttr.getOptions())) //可选项
						{
							String arrays [] = dbAttr.getOptions().split(ATTR_ENUM_VALUES_PREX);
							List<String> listValues = new ArrayList<>(arrays.length);
							Collections.addAll(listValues, arrays);
							attr.setDefaultValue(listValues);
							attr.setHaveOptions(Boolean.TRUE);
						}
						//break; //20190613:这里不能break,因为同一个二级模板有相同的属性名 //找到配置就可能退出当前迭代了。
					}

				}else //库中无必填的配置
				{
					/*XmlElement element = field.getAnnotation(XmlElement.class);
					if(element != null)
					{
						attr.setRequired(element.required());
					}
					// 属性级别
					XmlAttribute attribute = field.getAnnotation(XmlAttribute.class);
					if(attribute != null)
					{
						attr.setRequired(attribute.required());
					}*/
					attr.setRequired(Boolean.FALSE);
				}

				// list TODO 获取xsd的集合
				if(isListType(field))
				{
					attr.setAttrName(field.getName());
					attr.setAttrType(field.getType().getName());
					nextList.add(attr);

					// 泛型
					if(field.getGenericType() != null )
					{
						// String.class 之外
						if(field.getGenericType() == Boolean.class
								|| field.getGenericType() == Integer.class
								|| BigInteger.class == field.getGenericType()
								|| BigDecimal.class == field.getGenericType()
								|| XMLGregorianCalendar.class == field.getGenericType()
								|| String.class  == field.getGenericType())
						{
							if(parentAmazonAttr == null)
							{
								productAttr.add(attr);
							}
							continue;
						}
						ParameterizedType t = (ParameterizedType) field.getGenericType();
						Class obj = Class.forName(t.getActualTypeArguments()[0].getTypeName());
						attr.setGenericFlag("genericObj");
						if(parentAmazonAttr == null)
						{
							productAttr.add(attr);
						}
						if(isBaseType(obj))
						{
							continue;
						}
					/*	AmazonAttr nextListNode = new AmazonAttr();
						nextListNode.setAttrName(obj.getName());
						nextListNode.setAttrType(obj.getTypeName());
						attr.getNextNodes().add(nextListNode);*/
						beanToJson(productAttr,obj,attr,attr.getNextNodes(),attrList);
						// System.out.println("ss");
					}


					//attributeList.add(attr.getAttrName());
					continue;
				}

				// 是否为基础类型
				// 如果是基础类型，则直接获取获取属性
				if (isBaseType(field.getType()) && !classz.isEnum()) // 基本的数据类型不需要进入深层次类
				{
					attr.setAttrName(field.getName());
					attr.setAttrType(field.getType().getName());
					/*
					// 2.3_6.15
					if(field.getName().equalsIgnoreCase("VariationTheme"))
					{
						attr.setDefaultValue(DefVariationTheme.getVariationTheme(classz.getTypeName()));
					}*/
					nextList.add(attr);
					if(parentAmazonAttr == null)
					{
						productAttr.add(attr);
					}
					// 2.3_6.15
					else {                                     // TODO 姚明改了代码的 将fileSize下的value改为true
						/*if("braBandSize".equalsIgnoreCase(parentAmazonAttr.getAttrName()))
						{
							System.out.println("ddd");
						}
						attr.setRequired(parentAmazonAttr.isRequired());*/
						if(parentAmazonAttr.isRequired())
						{
							attr.setRequired(parentAmazonAttr.isRequired());
						}
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
					if(!parentAmazonAttr.isHaveOptions()){
						XmlEnumValue value = field.getAnnotation(XmlEnumValue.class);
						String v = (value == null) ? field.getName() : value.value();
						parentAmazonAttr.getDefaultValue().add(v);
					}
					continue;
				}

				// 内层类
				attr.setAttrName(field.getName());
				attr.setAttrType(field.getType().getName());


				nextList.add(attr);
				//attributeList.add(attr.getAttrName());
				if(parentAmazonAttr == null)
				{
					productAttr.add(attr);
				}else
				{
					if(parentAmazonAttr.isRequired())
					{
						attr.setRequired(parentAmazonAttr.isRequired());
					}
					parentAmazonAttr.setNextNodes(nextList);
				}
				beanToJson(productAttr,field.getType(),attr,attr.getNextNodes(),attrList);
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
		return Arrays.asList(discardedArray()).contains(field.getName());
	}
	private static boolean discarded(String [] array,String tagName)
	{
		return Arrays.asList(array).contains(tagName);
	}
	private static String[] discardedArray()
	{
		return new String [] {"value","ENUM$VALUES","$VALUES"};
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
	public static boolean isBaseType(Class<?> clz)
	{
		if(String.class == clz
				|| Integer.class == clz
				|| clz.isPrimitive()
				|| BigInteger.class == clz || BigDecimal.class == clz
				|| XMLGregorianCalendar.class == clz
				|| Boolean.class == clz)
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




		for(AmazonAttr attr : productAttr)// 第一层，不能用递归
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
				}else {
					unVariationThemes.add(_temp.getAttrName());
				}
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



	/**
	 * 计算主题算法
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static List<String> calculateThemeNew(Class classz,List<String> themeList)
	{
		List<String> attributeList =  getAttributeList(classz);
		// 计算主题是否有效
		List<String> tempVariationThemeList = new ArrayList<>();
		for(String theme : themeList)
		{
			String minTheme[] = theme.split("-");
			int flag = 0;
			for(String minT : minTheme)
			{
				if(attributeList.contains(DecapitalizeChar.decapitalizeLowerCase(minT)))
				{
					flag++;
				}
			}
			if(minTheme.length == flag)
			{
				tempVariationThemeList.add(theme);
			}
		}
		return tempVariationThemeList;
	}


	private static List<String> getAttributeList(Class classz)
	{
		List<String> attributeList = new ArrayList<>();
		// Class classz = Class.forName("com.rondaful.cloud.seller.generated.BuildingMaterials");
		Field fields []  = classz.getDeclaredFields();
		//禁止使用jdk8特性的lambda表达式
		for(Field field : fields) //一级属性
		{
			if("variationData".equalsIgnoreCase(field.getName())
					|| "classificationData".equalsIgnoreCase(field.getName()))
			{
				Field fieldSub [] = field.getType().getDeclaredFields();
				for(Field field1 :fieldSub) // 一级属性
				{
					/*if("VariationTheme".equalsIgnoreCase(field1.getName()) && field1.getType().isEnum())
					{
						Object objArr [] = field1.getType().getEnumConstants();
						for(Object o : objArr)
						{
							Method m = o.getClass().getMethod("value");
							variationThemeList.add((String)m.invoke(o));
						}
					}*/

					attributeList.add(field1.getName());
				}
				continue;
			}
			attributeList.add(field.getName());
		}
		return attributeList;
	}

	/**
	 * 计算主题算法
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static List<String> calculateTheme(Class classz/*,List<String> variationThemeList*/)
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		// 主题
		/*if(CollectionUtils.isEmpty(variationThemeList))
		{
			return  new ArrayList<>();
		}*/
		List<String> variationThemeList = new ArrayList<>();
		List<String> attributeList = new ArrayList<>();
		// Class classz = Class.forName("com.rondaful.cloud.seller.generated.BuildingMaterials");
		Field fields []  = classz.getDeclaredFields();
		//禁止使用jdk8特性的lambda表达式
		for(Field field : fields) //一级属性
		{
			if("variationData".equalsIgnoreCase(field.getName())
					|| "classificationData".equalsIgnoreCase(field.getName()))
			{
				Field fieldSub [] = field.getType().getDeclaredFields();
				for(Field field1 :fieldSub) // 一级属性
				{
					if("VariationTheme".equalsIgnoreCase(field1.getName()))
					{
						if(field1.getType() == String.class) //部分可能没有玫举
						{
							continue;
						}
						Object objArr [] = field1.getType().getEnumConstants();
						for(Object o : objArr)
						{
							Method m = o.getClass().getMethod("value");
							variationThemeList.add((String)m.invoke(o));
						}
						continue;
					}

					attributeList.add(field1.getName().toLowerCase());
				}
				continue;
			}

			// 有可能主题不在variationData节点下。直接就是主题，如：CameraPhoto模板
			if("VariationTheme".equalsIgnoreCase(field.getName()))
			{
				if(field.getType() == String.class) //部分可能没有玫举
				{
					continue;
				}
				Object objArr [] = field.getType().getEnumConstants();
				for(Object o : objArr)
				{
					Method m = o.getClass().getMethod("value");
					variationThemeList.add((String)m.invoke(o));
				}
				continue;
			}


			// ColorSpecification下的属性作为一级验证属性
			if("ColorSpecification".equalsIgnoreCase(field.getName()))
			{
				Field colorSpecificationFields[] =  field.getType().getDeclaredFields();
				for(Field colorSpecificationField : colorSpecificationFields)
				{
					attributeList.add(colorSpecificationField.getName().toLowerCase());
				}
			}

			attributeList.add(field.getName().toLowerCase());
		}

		// 计算主题是否有效
		List<String> tempVariationThemeList = new ArrayList<>();
		for(String theme : variationThemeList)
		{
			String minTheme[] = theme.split("-");
			int flag = 0;
			for(String minT : minTheme)
			{
				if(attributeList.contains(minT.toLowerCase()))
				{
					flag++;
				}
			}
			if(minTheme.length == flag)
			{
				tempVariationThemeList.add(theme);
			}
		}
		return tempVariationThemeList;
	}

}
