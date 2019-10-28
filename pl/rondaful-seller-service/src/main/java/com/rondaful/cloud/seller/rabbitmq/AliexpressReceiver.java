package com.rondaful.cloud.seller.rabbitmq;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.security.UserSession;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.seller.common.aliexpress.ImgSet;
import com.rondaful.cloud.seller.common.aliexpress.JsonAnalysis;
import com.rondaful.cloud.seller.config.AliexpressConfig;
import com.rondaful.cloud.seller.entity.AliexpressPublishListing;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingDetail;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingProduct;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPublishModel;
import com.rondaful.cloud.seller.entity.amazon.AmazonPublishListStatus;
import com.rondaful.cloud.seller.enums.AliexpressEnum;
import com.rondaful.cloud.seller.enums.AliexpressOperationEnum;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;
import com.rondaful.cloud.seller.service.IAliexpressPublishListingService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AliexpressReceiver {

	
    private final Logger logger = LoggerFactory.getLogger(AliexpressReceiver.class);
	@Autowired
	private IAliexpressPublishListingService aliexpressPublishListingService;
	@Autowired
	private AliexpressConfig config;
	@Autowired
	private AuthorizationSellerService authorizationSellerService;
	@RabbitListener(queues = "aliexpress-publish-queue")
	public void process(AliexpressPublishModel model) {
		logger.info("aliexpress操作队列消费start:{}",JSONObject.toJSONString(model));
		/**
		 *
		 *
		 */

		try {

			//刊登推送
			if(model != null && AliexpressEnum.AliexpressStatusEnum.PUBLISH.getCode()==model.getPublishStatus()){
				Empower empower = new Empower();
				empower.setStatus(1);
				empower.setEmpowerId(model.getEmpowerId().intValue());
				empower.setPlatform(3);//速卖通平台
				empower = authorizationSellerService.selectOneByAcount(empower);
				//start图片需要上传到速卖通图片库才能够刊登
				Map<String,String> mapImage = this.imageUrl(model.getProductImage(),empower.getToken());
				if(mapImage!=null ){
					String img = mapImage.get("strImgs");
					model.setProductImage(img);
					if("true".equals(mapImage.get("updatebool"))){
						AliexpressPublishListing aliexpressPublishListing = new AliexpressPublishListing();
						aliexpressPublishListing.setId(model.getId());
						aliexpressPublishListing.setProductImage(img.replace(";","|"));
						aliexpressPublishListingService.updateByPrimaryKeySelective(aliexpressPublishListing);
					}
				}

				for(AliexpressPublishListingProduct product : model.getListProduct()){
					if (StringUtils.isNotBlank(product.getProductImage())) {
						Map<String,String> mapProductImage = this.imageUrl(product.getProductImage(),empower.getToken());
						if(mapProductImage!=null){
							String productImage=mapProductImage.get("strImgs");
							product.setProductImage(productImage);
							if("true".equals(mapProductImage.get("updatebool"))){
								AliexpressPublishListingProduct updateProduct = new AliexpressPublishListingProduct();
								updateProduct.setId(product.getId());
								updateProduct.setPublishProductImage(productImage);
								aliexpressPublishListingService.updateByAliexpressPublishListingProduct(updateProduct);
							}
						}
					}
				}
				//end图片需要上传到速卖通图片库才能够刊登
				//详情描述图片地址转换
				String productDetails = model.getProductDetails();
				if(productDetails!=null) {
					boolean boolUpdate = false;
					Set<String> imgs = ImgSet.getImgSet(productDetails);
					if (imgs != null && imgs.size() > 0) {
						for (String imgSrc : imgs) {
							Map<String,String> image = this.imageUrl(imgSrc,empower.getToken());
							if(image!=null) {
								String strImg = image.get("strImgs");
								if("true".equals(image.get("updatebool"))) {
									boolUpdate = true;
									productDetails = productDetails.replace(imgSrc, strImg);
								}
							}
						}
					}
					model.setProductDetails(productDetails);
					if(boolUpdate) {
						AliexpressPublishListingDetail detail = new AliexpressPublishListingDetail();
						detail.setPublishListingId(model.getId());
						detail.setProductDetails(productDetails);
						aliexpressPublishListingService.updateByPublishListingDetail(detail);
					}
				}
				String mobileRemark = model.getMobileRemark();
				if(mobileRemark!=null) {
					List<String> imgsList = Lists.newArrayList();
					Set<String> imgs = ImgSet.getImgSet(mobileRemark);
					if (imgs != null && imgs.size() > 0) {
						boolean boolUpdate = false;
						for (String imgSrc : imgs) {
							Map<String, String> image = this.imageUrl(imgSrc, empower.getToken());
							if (image != null) {
								String strImg = image.get("strImgs");
								if ("true".equals(image.get("updatebool"))) {
									boolUpdate = true;
									mobileRemark = mobileRemark.replace(imgSrc, strImg);
								}
								imgsList.add(strImg);
							}

						}
						model.setMobileRemark(mobileRemark);
						if(boolUpdate) {
							AliexpressPublishListingDetail detail = new AliexpressPublishListingDetail();
							detail.setPublishListingId(model.getId());
							detail.setMobileRemark(mobileRemark);
							aliexpressPublishListingService.updateByPublishListingDetail(detail);
						}
					}
					model.setMobileRemark(setmobileDetail(imgsList,mobileRemark));
				}

				String url = config.getAliexpressUrl()+"/api/aliexpress/saveProduct";
				Map<String, String> paramsMap = Maps.newHashMap();
				paramsMap.put("sessionKey", empower.getToken());
				paramsMap.put("jsonStr", JSONObject.toJSONString(model, SerializerFeature.WriteMapNullValue));
				String body = HttpUtil.post(url, paramsMap);
				logger.info("aliexpress刊登商品返回内容{}",body);
				aliexpressPublishListingService.updateAliexpressPublishListingSuccee(1,model.getId(),body);
			}else if(model != null && AliexpressEnum.AliexpressStatusEnum.AUDIT.getCode()==model.getPublishStatus()){
				Empower empower = new Empower();
				empower.setStatus(1);
				empower.setEmpowerId(model.getEmpowerId().intValue());
				empower.setPlatform(3);//速卖通平台
				empower = authorizationSellerService.selectOneByAcount(empower);

				String url = config.getAliexpressUrl()+"/api/aliexpress/findaeproductstatusbyid";
				Map<String, String> paramsMap = Maps.newHashMap();
				paramsMap.put("sessionKey", empower.getToken());
				paramsMap.put("productId", model.getItemId().toString());
				String body = HttpUtil.post(url, paramsMap);
				logger.info("aliexpress刊登查询审核商品返回内容{}",body);
				aliexpressPublishListingService.updateAliexpressPublishListingSuccee(2,model.getId(),body);
			}else if(model != null && (AliexpressEnum.AliexpressStatusEnum.SALE.getCode()==model.getPublishStatus() || AliexpressEnum.AliexpressStatusEnum.END.getCode()==model.getPublishStatus())){
				Empower empower = new Empower();
				empower.setStatus(1);
				empower.setEmpowerId(model.getEmpowerId().intValue());
				empower.setPlatform(3);//速卖通平台
				empower = authorizationSellerService.selectOneByAcount(empower);

				Map<String,String> mapImage = this.imageUrl(model.getProductImage(),empower.getToken());
				if(mapImage!=null ){
					String img = mapImage.get("strImgs");
					model.setProductImage(img);
					if("true".equals(mapImage.get("updatebool"))){
						AliexpressPublishListing aliexpressPublishListing = new AliexpressPublishListing();
						aliexpressPublishListing.setId(model.getId());
						aliexpressPublishListing.setProductImage(img.replace(";","|"));
						aliexpressPublishListingService.updateByPrimaryKeySelective(aliexpressPublishListing);
					}
				}

				for(AliexpressPublishListingProduct product : model.getListProduct()){
					if (StringUtils.isNotBlank(product.getProductImage())) {
						Map<String,String> mapProductImage = this.imageUrl(product.getProductImage(),empower.getToken());
						if(mapProductImage!=null){
							String productImage=mapProductImage.get("strImgs");
							product.setProductImage(productImage);
							if("true".equals(mapProductImage.get("updatebool"))){
								AliexpressPublishListingProduct updateProduct = new AliexpressPublishListingProduct();
								updateProduct.setId(product.getId());
								updateProduct.setPublishProductImage(productImage);
								aliexpressPublishListingService.updateByAliexpressPublishListingProduct(updateProduct);
							}
						}
					}
				}
				//end图片需要上传到速卖通图片库才能够刊登
				//详情描述图片地址转换
				String productDetails = model.getProductDetails();
				if(productDetails!=null) {
					boolean boolUpdate = false;
					Set<String> imgs = ImgSet.getImgSet(productDetails);
					if (imgs != null && imgs.size() > 0) {
						for (String imgSrc : imgs) {
							Map<String,String> image = this.imageUrl(imgSrc,empower.getToken());
							if(image!=null) {
								String strImg = image.get("strImgs");
								if("true".equals(image.get("updatebool"))) {
									boolUpdate = true;
									productDetails = productDetails.replace(imgSrc, strImg);
								}
							}
						}
					}
					model.setProductDetails(productDetails);
					if(boolUpdate) {
						AliexpressPublishListingDetail detail = new AliexpressPublishListingDetail();
						detail.setPublishListingId(model.getId());
						detail.setProductDetails(productDetails);
						aliexpressPublishListingService.updateByPublishListingDetail(detail);
					}
				}
				String mobileRemark = model.getMobileRemark();
				if(mobileRemark!=null) {
					List<String> imgsList = Lists.newArrayList();
					Set<String> imgs = ImgSet.getImgSet(mobileRemark);
					if (imgs != null && imgs.size() > 0) {
						boolean boolUpdate = false;
						for (String imgSrc : imgs) {
							Map<String, String> image = this.imageUrl(imgSrc, empower.getToken());
							if (image != null) {
								String strImg = image.get("strImgs");
								if ("true".equals(image.get("updatebool"))) {
									boolUpdate = true;
									mobileRemark = mobileRemark.replace(imgSrc, strImg);
								}
								imgsList.add(strImg);
							}

						}
						model.setMobileRemark(mobileRemark);
						if(boolUpdate) {
							AliexpressPublishListingDetail detail = new AliexpressPublishListingDetail();
							detail.setPublishListingId(model.getId());
							detail.setMobileRemark(mobileRemark);
							aliexpressPublishListingService.updateByPublishListingDetail(detail);
						}
					}
					model.setMobileRemark(setmobileDetail(imgsList,mobileRemark));
				}

				String url = config.getAliexpressUrl()+"/api/aliexpress/updateProduct";
				Map<String, String> paramsMap = Maps.newHashMap();
				paramsMap.put("sessionKey", empower.getToken());

				paramsMap.put("jsonStr", JSONObject.toJSONString(model, SerializerFeature.WriteMapNullValue));
				String body = HttpUtil.post(url, paramsMap);
				logger.info("aliexpress刊登商品返回内容{}",body);
				aliexpressPublishListingService.updateAliexpressPublishListingSuccee(3,model.getId(),body);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("aliexpress操作队列消费end:{}",model.getItemId());
	}

	private String setmobileDetail(List<String > imgsList,String mobileRemark){
		//{"mobileDetail":[{"content":"line 1","type":"text"},{"content":"line 2","type":"text"},{"col":1,"images":[{"imgUrl":"http://ae01.alicdn.com/kf/1.jpeg"}],"type":"image"},{"content":"line 4","type":"text"},{"col":2,"images":[{"imgUrl":"http://ae01.alicdn.com/kf/2.jpeg"},{"imgUrl":"http://ae01.alicdn.com/kf/3.jpeg"}],"type":"image"},{"content":"line 6","type":"text"}],"version":"1.0","versionNum":1}
		StringBuffer mobileDetail = new StringBuffer("{\"mobileDetail\":[");
		if(imgsList.size()>0){
			String details = mobileRemark;
			int i = 0;
			for(String imgSrc:imgsList){
				String img="<img src=\""+imgSrc+"\">";
				String replaceStr = "[img"+i+"]";
				details = details.replace(img,replaceStr);
				i++;
			}
			for(int j=(i-1);j>=0;j--){
				String rStr = "\\[img"+j+"\\]";
				String[] imgDetails = details.split(rStr);
				if(imgDetails.length==2){
					String content = imgDetails[0];
					mobileDetail.append("{\"content\":\""+content+"\",\"type\":\"text\"},");
				}
				mobileDetail.append("{\"col\":"+(1)+",\"images\":[{\"imgUrl\":\""+imgsList.get(j)+"\"}],\"type\":\"image\"},");
				if(j==0){
					String content = imgDetails[1];
					mobileDetail.append("{\"content\":\"" + content + "\",\"type\":\"text\"}");
				}
			}
		}else {
			mobileDetail.append("{\"content\":\""+mobileRemark+"\",\"type\":\"text\"}");
		}
		mobileDetail.append("],\"version\":\"1.0\",\"versionNum\":1}");
		return mobileDetail.toString();
	}

	private Map<String,String> imageUrl(String productImage,String token){
		if(StringUtils.isNotBlank(productImage)){
			String[] arrayImg = productImage.split("\\|");
			boolean bool = true;
			StringBuffer strImgs=new StringBuffer();
			//是否需要修改
			boolean updatebool = false;
			for(String imgStr:arrayImg){
				String img = "";
				if(this.alicdnImage(imgStr)){
					img = imgStr;
				}else {
					if(base64Image(imgStr)){
						String[] strImg = imgStr.split(",");
						if (strImg.length == 2) {
							String base64 = strImg[1];
							String imgName = strImg[0].replace("data:image/", "").replace(";base64", "");
							imgName = "img." + imgName;
							img = aliexpressPublishListingService.uploadimageforsdkBase64(base64, imgName, null, token, null);
						}
					}else {
						img = aliexpressPublishListingService.uploadimageforsdk(imgStr, null, token, null);
					}
					updatebool=true;
				}
				if(img!=null) {
					if(bool){
						bool=false;
						strImgs.append(img);
					}else {
						strImgs.append(";"+img);
					}
				}
			}
			Map<String,String> map=Maps.newHashMap();
			map.put("strImgs",strImgs.toString());
			map.put("updatebool",updatebool+"");
			return map;
		}
		return null;
	}

	/**
	 * 是否包含 alicdn
	 * @param imgStr
	 * @return true 包含
	 */
	private boolean alicdnImage(String imgStr){
		if(imgStr!=null && imgStr.contains("alicdn")){
			return true;
		}
		return false;
	}

	/**
	 * 是否包含 base64
	 * @param imgStr
	 * @return true 包含
	 */
	private boolean base64Image(String imgStr){
		if(imgStr!=null && imgStr.contains("base64")){
			return true;
		}
		return false;
	}
}
