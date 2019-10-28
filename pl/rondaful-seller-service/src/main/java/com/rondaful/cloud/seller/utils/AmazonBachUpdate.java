package com.rondaful.cloud.seller.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.seller.entity.amazon.AmazonImageRequest;
import com.rondaful.cloud.seller.entity.amazon.AmazonPublishListStatus;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.generated.CameraPhoto;
import com.rondaful.cloud.seller.generated.CameraPhoto.ProductType;
import com.rondaful.cloud.seller.generated.DigitalFrame;
import com.rondaful.cloud.seller.generated.LengthDimension;
import com.rondaful.cloud.seller.generated.LengthUnitOfMeasure;
import com.rondaful.cloud.seller.generated.ProductImage;
import com.rondaful.cloud.seller.vo.BatchUpdateVO.OperEnum;
import com.rondaful.cloud.seller.vo.BatchUpdateVO.OriginalJson;
import com.rondaful.cloud.seller.vo.BatchUpdateVO.ReplaceJson;

/**
 * 亚马逊批量更新
 * @author dingshulin
 * 寄蜉蝣于天地 渺沧海之一粟
 */
public class AmazonBachUpdate {

	private static Logger logger = LoggerFactory.getLogger(AmazonBachUpdate.class);
	
	/**
	 * //价格处理
	 * @param originalRatioJsonStr
	 * @param publishMessage
	 * @param publishType
	 * @param isText 1文本  0百分比
	 */
	public static AmazonRequestProduct disposeStandardPrice(String originalRatioJsonStr, AmazonRequestProduct amazonRequestProduct,
			Integer publishType,int isText,List<String> skus,Integer status) {
		OriginalJson originalRatio = JSON.parseObject(originalRatioJsonStr, OriginalJson.class);
	
		BigDecimal standardPrice = amazonRequestProduct.getStandardPrice();
		if(standardPrice == null) {
			standardPrice=new BigDecimal("0");
		}
		List<AmazonRequestProduct> varRequestProductList = amazonRequestProduct.getVarRequestProductList();
		
		standardPrice = isTextDispose(isText,standardPrice,originalRatio);
		if(publishType == AmazonConstants.PUBLISH_TYPE_ONLY) {
			amazonRequestProduct.setStandardPrice(standardPrice);
		}
		//2：单属性 1：多属性
		if(publishType == AmazonConstants.PUBLISH_TYPE_MORE) {
			for (AmazonRequestProduct product : varRequestProductList) {
				//根据sku来修改修改当前选中的sku
				for (String sku : skus) {
					if(status==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT) {
						if(sku.equals(product.getPlSku())) {
							//草稿状态根据品连sku修改
							updateStandardPriceBySku(isText, product, originalRatio);
						}
					}else if(status==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE || status==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_REST_PUSH){
						if(sku.equals(product.getSku())) {
							//在线状态根据品台sku修改
							updateStandardPriceBySku(isText, product, originalRatio);
						}
					}else {
						logger.warn("批量修改，处理价格传入状态为{},sku是{}",status,sku);
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"参数有误");
					}
				}
				
			}
			amazonRequestProduct.setVarRequestProductList(varRequestProductList);
		}
		return amazonRequestProduct;
		
	}
	

	private static void updateStandardPriceBySku(int isText,AmazonRequestProduct product,OriginalJson originalRatio) {
		BigDecimal varStandardPrice = isTextDispose(isText,product.getStandardPrice()==null ? new BigDecimal(0) : product.getStandardPrice(),originalRatio);
		product.setStandardPrice(varStandardPrice);
	}
	
	
	/**
	 * 价格文本或者百分比计算
	 * @param isText
	 * @param standardPrice
	 * @param originalRatio
	 * @return
	 */
	public static BigDecimal isTextDispose(int isText,BigDecimal standardPrice,OriginalJson originalRatio) {
		BigDecimal retVal=new BigDecimal(0D);
		if(isText ==0) {
			//加上百分比值
			BigDecimal percentumVal = operPercentum(originalRatio.getVal(), standardPrice);
			retVal=oper(originalRatio,standardPrice,percentumVal.toString());
		}else {
			//非百分比
			retVal=oper(originalRatio, standardPrice, originalRatio.getVal());
		}
		if(retVal.compareTo(new BigDecimal(0))<=0) {
			retVal=new BigDecimal(0D);
		}
		return retVal;
	}
	
	/**
	 * //处理是加或者减
	 * @param originalRatio
	 * @param standardPrice
	 * @param percentumVal
	 * @return
	 */
	public static BigDecimal oper(OriginalJson originalRatio,BigDecimal standardPrice,String percentumVal) {
		BigDecimal retVal=new BigDecimal("0");
		if(OperEnum.ADD.getCode().equals(originalRatio.getOperaTion())) {
			retVal=operAdd(percentumVal,standardPrice);
		}else {
			retVal=operSubtract(percentumVal,standardPrice);
		}
		return retVal;
	}
	
	/**
	 * //计算百分值
	 * @param val
	 * @param standardPrice
	 * @return
	 */
	public static BigDecimal operPercentum(String val,BigDecimal standardPrice) {
		BigDecimal bigDecimal=new BigDecimal(val);
		BigDecimal divide = bigDecimal.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
		BigDecimal multiply = standardPrice.multiply(divide);
		multiply=multiply.setScale(2,BigDecimal.ROUND_HALF_UP);
		return multiply;
	}
	
	public static BigDecimal operAdd(String val,BigDecimal standardPrice) {
		BigDecimal bigDecimal=new BigDecimal(val);
		return standardPrice.add(bigDecimal);
	}
	
	public static BigDecimal operSubtract(String val,BigDecimal standardPrice) {
		BigDecimal bigDecimal=new BigDecimal(val);
		return standardPrice.subtract(bigDecimal);
	}
	
	/**
	 * title替换处理
	 * @param requestProduct
	 * @param publishType
	 * @param replaceJsonStr
	 * @return
	 */
	public static AmazonRequestProduct setReplaceTitle(AmazonRequestProduct requestProduct,Integer publishType,String replaceJsonStr,
			List<String> skus,Integer status) {
		List<ReplaceJson> replaces=JSON.parseArray(replaceJsonStr,ReplaceJson.class);
		String title = requestProduct.getTitle();
		for (ReplaceJson replaceJson : replaces) {
			title=title.replace(replaceJson.getOldText(), replaceJson.getNewText());
		}
		if(publishType==AmazonConstants.PUBLISH_TYPE_ONLY) {
			requestProduct.setTitle(title);
		}
		
		if(publishType==AmazonConstants.PUBLISH_TYPE_MORE) {
			logger.debug("-------------------------setReplaceTitle()---多属性处理变体数据title------------------------------");
			List<AmazonRequestProduct> varRequestProductList = requestProduct.getVarRequestProductList();
			for (AmazonRequestProduct amazonRequestProduct : varRequestProductList) {
				for (String sku : skus) {
					if(status==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT) {
						if(sku.equals(amazonRequestProduct.getPlSku())) {
							//草稿状态根据品连sku修改
							String varTitle = amazonRequestProduct.getTitle();
							for (ReplaceJson replaceJson : replaces) {
								varTitle=varTitle.replace(replaceJson.getOldText(), replaceJson.getNewText());
							}
							amazonRequestProduct.setTitle(varTitle);
						}
					}else if(status==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE|| status==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_REST_PUSH){
						if(sku.equals(amazonRequestProduct.getSku())) {
							//在线状态根据品台sku修改
							String varTitle = amazonRequestProduct.getTitle();
							for (ReplaceJson replaceJson : replaces) {
								varTitle=varTitle.replace(replaceJson.getOldText(), replaceJson.getNewText());
							}
							amazonRequestProduct.setTitle(varTitle);
						}
					}else {
						logger.warn("批量修改，处理价格传入状态为{},sku是{}",status,sku);
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"参数有误");
					}
				}
				
			}
			requestProduct.setVarRequestProductList(varRequestProductList);
		}
		return requestProduct;
	}
	

	/**
	 * 处理title追加
	 * @param requestProduct
	 * @param publishType
	 * @param titleText
	 * @param isBefore AFTER BEFORE
	 */
	public static AmazonRequestProduct setAppendTitle(AmazonRequestProduct requestProduct, Integer publishType, String titleText,String isBefore,
			List<String> skus,Integer status) {
		String title = requestProduct.getTitle();
		if(isBefore.equals("BEFORE")) {
			title=titleText+title;
		}else {
			title=title+titleText;
		}
		
		if(publishType == AmazonConstants.PUBLISH_TYPE_ONLY) {
			requestProduct.setTitle(title);
		}
		
		//2：单属性 1：多属性
		if(publishType == AmazonConstants.PUBLISH_TYPE_MORE) {
			List<AmazonRequestProduct> varRequestProductList = requestProduct.getVarRequestProductList();
			for (AmazonRequestProduct amazonRequestProduct : varRequestProductList) {
				for (String sku : skus) {
					if(status==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT) {
						if(sku.equals(amazonRequestProduct.getPlSku())) {
							//草稿状态根据品连sku修改
							String t = amazonRequestProduct.getTitle();
							if(isBefore.equals("BEFORE")) {
								t=titleText+t;
							}else {
								t=t+titleText;
							}
							amazonRequestProduct.setTitle(t);
						}
					}else if(status==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE|| status==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_REST_PUSH){
						if(sku.equals(amazonRequestProduct.getSku())) {
							//在线状态根据品台sku修改
							String t = amazonRequestProduct.getTitle();
							if(isBefore.equals("BEFORE")) {
								t=titleText+t;
							}else {
								t=t+titleText;
							}
							amazonRequestProduct.setTitle(t);
						}
					}else {
						logger.warn("批量修改，处理价格传入状态为{},sku是{}",status,sku);
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"参数有误");
					}
				}
				
			}
			requestProduct.setVarRequestProductList(varRequestProductList);
		}
		return requestProduct;
	}
	
	
	/**
	 * 设置可售数
	 * @param requestProduct
	 * @param publishType
	 * @param quantityNum
	 * methodType : updateOnline在线更新  batchUpdate 批量更新
	 */
	public static AmazonRequestProduct setQuantityNum(AmazonRequestProduct requestProduct, Integer publishType, Long quantityNum
			,AmazonRequestProduct requestProductParams,List<String> skus,Integer status,String methodType) {
		if(methodType.equals("batchUpdate")) {
			if(publishType == AmazonConstants.PUBLISH_TYPE_ONLY) {
				requestProduct.setQuantity(quantityNum);
			}
		}else {
			requestProduct.setQuantity(quantityNum);
		}
		
		if(publishType == AmazonConstants.PUBLISH_TYPE_MORE) {
			if(requestProductParams != null) {
				List<AmazonRequestProduct> paramsList = requestProductParams.getVarRequestProductList();
				List<AmazonRequestProduct> varRequestProductList = requestProduct.getVarRequestProductList();
				int i=0;
				int j=0;
				for (AmazonRequestProduct paramsAmazonRequestProduct : paramsList) {
					i=i+1;
					for (AmazonRequestProduct amazonRequestProduct : varRequestProductList) {
						j=j+1;
						if(i==j) {
							amazonRequestProduct.setQuantity(paramsAmazonRequestProduct.getQuantity());
						}
					}
					j=0;
				}
				requestProduct.setVarRequestProductList(varRequestProductList);
			}else {
				List<AmazonRequestProduct> varRequestProductList = requestProduct.getVarRequestProductList();
				for(AmazonRequestProduct amazonRequestProduct: varRequestProductList) {
					for (String sku : skus) {
						if(status==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT) {
							if(sku.equals(amazonRequestProduct.getPlSku())) {
								//草稿状态根据品连sku修改
								amazonRequestProduct.setQuantity(quantityNum);
							}
						}else if(status==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE|| status==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_REST_PUSH){
							if(sku.equals(amazonRequestProduct.getSku())) {
								//在线状态根据品台sku修改
								amazonRequestProduct.setQuantity(quantityNum);
							}
						}else {
							logger.warn("批量修改，处理价格传入状态为{},sku是{}",status,sku);
							throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"参数有误");
						}
					}
					
				}
				requestProduct.setVarRequestProductList(varRequestProductList);
			}	
		}
		return requestProduct;
	}


	/**
	 * 固定价格值替换
	 * @param requestProduct
	 * @param publishType
	 * @param priceValue
	 * methodType : updateOnline在线更新  batchUpdate 批量更新
	 * @return
	 */
	public static AmazonRequestProduct setPriceValue(AmazonRequestProduct requestProduct, Integer publishType,
			String priceValue,AmazonRequestProduct requestProductParams,List<String> skus,Integer status,String methodType) {
		if(methodType.equals("batchUpdate")) {
			if(publishType == AmazonConstants.PUBLISH_TYPE_ONLY) {
				if("0".equals(priceValue)) {
					requestProduct.setStandardPrice(null);
				}else {
					requestProduct.setStandardPrice(new BigDecimal(priceValue));
				}
			}
		}else {
			if("0".equals(priceValue)) {
				requestProduct.setStandardPrice(null);
			}else {
				requestProduct.setStandardPrice(new BigDecimal(priceValue));
			}
		}
		
		
		if(publishType == AmazonConstants.PUBLISH_TYPE_MORE) {
			if(requestProductParams != null) {
				List<AmazonRequestProduct> paramsList = requestProductParams.getVarRequestProductList();
				List<AmazonRequestProduct> varRequestProductList = requestProduct.getVarRequestProductList();
				int i=0;
				int j=0;
				for (AmazonRequestProduct paramsAmazonRequestProduct : paramsList) {
					i=i+1;
					for (AmazonRequestProduct amazonRequestProduct : varRequestProductList) {
						j=j+1;
						if(i==j) {
							amazonRequestProduct.setStandardPrice(paramsAmazonRequestProduct.getStandardPrice());
						}
					}
					j=0;
				}
				requestProduct.setVarRequestProductList(varRequestProductList);
			}else {
				List<AmazonRequestProduct> varRequestProductList = requestProduct.getVarRequestProductList();
				for (AmazonRequestProduct amazonRequestProduct : varRequestProductList) {
					for (String sku : skus) {
						if(status==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT) {
							if(sku.equals(amazonRequestProduct.getPlSku())) {
								//草稿状态根据品连sku修改
								amazonRequestProduct.setStandardPrice(new BigDecimal(priceValue));
							}
						}else if(status==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE|| status==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_REST_PUSH){
							if(sku.equals(amazonRequestProduct.getSku())) {
								//在线状态根据品台sku修改
								amazonRequestProduct.setStandardPrice(new BigDecimal(priceValue));
							}
						}else {
							logger.warn("批量修改，处理价格传入状态为{},sku是{}",status,sku);
							throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"参数有误");
						}
					}
					
				}
				
				requestProduct.setVarRequestProductList(varRequestProductList);
			}
			
		}
		
		return requestProduct;
	}
	
	
	/**
	 * 覆盖处理title
	 * @param requestProduct
	 * @param publishType
	 * @param title
	 * @return
	 */
	public static AmazonRequestProduct setTitle(AmazonRequestProduct requestProduct, Integer publishType,
			String title,AmazonRequestProduct requestProductParams) {
		requestProduct.setTitle(title);
		if(publishType == AmazonConstants.PUBLISH_TYPE_MORE) {
			if(requestProductParams != null) {
				List<AmazonRequestProduct> paramsList = requestProductParams.getVarRequestProductList();
				List<AmazonRequestProduct> varRequestProductList = requestProduct.getVarRequestProductList();
				int i=0;
				int j=0;
				for (AmazonRequestProduct paramsAmazonRequestProduct : paramsList) {
					i=i+1;
					for (AmazonRequestProduct amazonRequestProduct : varRequestProductList) {
						j=j+1;
						if(i==j) {
							amazonRequestProduct.setTitle(paramsAmazonRequestProduct.getTitle());
						}
					}
					j=0;
				}
				requestProduct.setVarRequestProductList(varRequestProductList);
			}else {
				List<AmazonRequestProduct> varRequestProductList = requestProduct.getVarRequestProductList();
				for (AmazonRequestProduct amazonRequestProduct : varRequestProductList) {
					amazonRequestProduct.setTitle(amazonRequestProduct.getTitle());
				}
				requestProduct.setVarRequestProductList(varRequestProductList);
			}
		}
		return requestProduct;
	}
	
	/**
	 * 从亚马逊获取的数据没有币种需要添加处理币种
	 */
	public static AmazonRequestProduct SetStandardPriceUnit(AmazonRequestProduct requestProduct, Integer publishType,
			String standardPriceUnit,List<String> skus,Integer status) {
		if(StringUtils.isBlank(requestProduct.getStandardPriceUnit())) {
			requestProduct.setStandardPriceUnit(standardPriceUnit);
		}  
		if(publishType == AmazonConstants.PUBLISH_TYPE_MORE) {
			List<AmazonRequestProduct> varRequestProductList = requestProduct.getVarRequestProductList();
			for (AmazonRequestProduct amazonRequestProduct : varRequestProductList) {
				if(StringUtils.isBlank(amazonRequestProduct.getStandardPriceUnit())) {
					amazonRequestProduct.setStandardPriceUnit(standardPriceUnit);
				} 
			}
			requestProduct.setVarRequestProductList(varRequestProductList);
		}
		return requestProduct;
	}
	
	/**
	 * 同步图片到message
	 * @param requestProduct
	 * @param publishType
	 * @param images
	 * @return
	 */
	public static AmazonRequestProduct setImgs(AmazonRequestProduct requestProduct, Integer publishType,AmazonImageRequest images) {
		requestProduct.setImages(images.getImages().getMainImage());
		if(publishType == AmazonConstants.PUBLISH_TYPE_MORE) {
			List<ProductImage> newImgs=null;
			List<AmazonRequestProduct> paramsList = requestProduct.getVarRequestProductList();
			List<List<ProductImage>> subImage = images.getImages().getSubImage();
			for (AmazonRequestProduct paramsAmazonRequestProduct : paramsList) {
				newImgs=new ArrayList<>();
				for (List<ProductImage> list : subImage) {
					for (ProductImage productImage : list) {
						if(paramsAmazonRequestProduct.getSku().equals(productImage.getSKU())) {
							newImgs.add(productImage);
						}
					}
				}
				paramsAmazonRequestProduct.setImages(newImgs);
			}
			requestProduct.setVarRequestProductList(paramsList);
		}
		return requestProduct;
	}
	
	/**
	 * 清空asin
	 * @param requestProduct
	 * @param publishType
	 * @return
	 */
	public static AmazonRequestProduct cleanAsin(AmazonRequestProduct requestProduct, Integer publishType) {
		requestProduct.setAsin("");
		if(publishType == AmazonConstants.PUBLISH_TYPE_MORE) {
			List<AmazonRequestProduct> paramsList = requestProduct.getVarRequestProductList();
			for (AmazonRequestProduct paramsAmazonRequestProduct : paramsList) {
				paramsAmazonRequestProduct.setAsin("");
			}
			requestProduct.setVarRequestProductList(paramsList);
		}
		return requestProduct;
	}
	
	
	/**
	 * 同步设置asin
	 * @param requestProduct
	 * @param publishType
	 * @param requestProductParams
	 * @return
	 */
	public static AmazonRequestProduct setAsin(AmazonRequestProduct requestProduct, Integer publishType,AmazonRequestProduct requestProductParams) {
		requestProduct.setAsin(requestProductParams.getAsin());
		if(publishType == AmazonConstants.PUBLISH_TYPE_MORE) {
			List<AmazonRequestProduct> varRequestProductList = requestProduct.getVarRequestProductList();
			List<AmazonRequestProduct> varParamsList =requestProductParams.getVarRequestProductList();
			for (AmazonRequestProduct amazonRequestProduct : varRequestProductList) {
				for (AmazonRequestProduct amazonParams : varParamsList) {
					if(amazonRequestProduct.getSku().equals(amazonParams.getSku())) {
						amazonRequestProduct.setAsin(amazonParams.getAsin());
					}
					
				}
			}
			requestProduct.setVarRequestProductList(varRequestProductList);
		}
		return requestProduct;
	}
	
	/**
	 * 删除没有被操作的sku
	 * @param requestProduct
	 * @param skus 平台sku
	 * @return
	 */
	public static AmazonRequestProduct deleteBysku(AmazonRequestProduct requestProduct,List<String> skus) {
		List<AmazonRequestProduct> varRequestProductList = requestProduct.getVarRequestProductList();
		List<AmazonRequestProduct> removeList=new LinkedList<AmazonRequestProduct>();
		for (AmazonRequestProduct amazonRequestProduct : varRequestProductList) {
			for (String sku : skus) {
				if(!sku.equals(amazonRequestProduct.getSku())) {
					removeList.add(amazonRequestProduct);
				}
			}
		}
		if(CollectionUtils.isNotEmpty(removeList)) {
			varRequestProductList.removeAll(removeList);
		}
		requestProduct.setVarRequestProductList(varRequestProductList);
		return requestProduct;
	}
	
	
	public static void main(String[] args) {
		CameraPhoto cameraPhoto=new CameraPhoto();
		ClassXmlUtil classXmlUtil=new ClassXmlUtil();
		
		DigitalFrame digitalFrame=new DigitalFrame();
		LengthDimension lengthDimension=new LengthDimension();
		lengthDimension.setUnitOfMeasure(LengthUnitOfMeasure.CM);
		lengthDimension.setValue(BigDecimal.valueOf(11L));
		digitalFrame.setDisplaySize(lengthDimension);
		ProductType productType =  new ProductType();
		productType.setDigitalFrame(digitalFrame);
		cameraPhoto.setProductType(productType);
		BigDecimal operPercentum = operPercentum("60", new BigDecimal("4444.994"));
		System.out.println(operPercentum);
		
		System.err.println("cameraPhoto:"+classXmlUtil.toXML(cameraPhoto));
		
		String aa="[{\"listingId\":1212,\"skus\":\"52qq\"},{\"listingId\":1333,\"skus\":\"qqqq,wwwww,eeee,rrrr\"},{\"listingId\":1444,\"skus\":\"qqqq,wwwww,eeee,rrrr\"}]";
		List<JSONObject> parseArray = JSONArray.parseArray(aa, JSONObject.class);
		for (JSONObject jsonObject : parseArray) {
			System.out.println(jsonObject.getLongValue("listingId"));
			System.out.println(jsonObject.getString("skus"));
			List<String> skus =Arrays.asList(jsonObject.getString("skus").split(","));
			for (String string : skus) {
				System.out.println(string+"000000000000");
			}
		}
		
		
	} 
}
