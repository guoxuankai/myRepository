package com.rondaful.cloud.seller.rabbitmq;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.seller.common.aliexpress.AliexpressMethodNameEnum;
import com.rondaful.cloud.seller.common.aliexpress.HttpTaoBaoApi;
import com.rondaful.cloud.seller.common.aliexpress.JsonAnalysis;
import com.rondaful.cloud.seller.entity.AliexpressPhoto;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPhotoModel;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPhotoUrlModel;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressProductListModel;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressProductModel;
import com.rondaful.cloud.seller.service.IAliexpressListingService;
import com.rondaful.cloud.seller.service.IAliexpressPhotoBankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class AliexpressPhotoReceiver {

	
    private final Logger logger = LoggerFactory.getLogger(AliexpressPhotoReceiver.class);
	@Autowired
	private IAliexpressPhotoBankService aliexpressPhotoBankService;
	@Autowired
	private IAliexpressListingService aliexpressListingService;
	@Autowired
	private HttpTaoBaoApi httpTaoBaoApi;
	@Autowired
	private AliexpressSender sender;
	@RabbitListener(queues = "aliexpress-photo-queue")
	public void process(String message) {
		logger.info("aliexpressPhoto操作队列消费start:{}",JSONObject.toJSONString(message));
		try {
			AliexpressPhotoModel model = JSONObject.parseObject(message,AliexpressPhotoModel.class);
			if(model==null){
				logger.info("aliexpressPhoto操作队列消费end无数据");
				return;
			}
			Map<String,Object> map = Maps.newHashMap();
			map.put("sessionKey",model.getToken());
			String json = "";
			String paths = "";
			if(model.getType()==2){
				String[] image= model.getImageUrl().split("/");
				paths = image[image.length-1];
				map.put("paths",paths);
				json = httpTaoBaoApi.getTaoBaoApi(AliexpressMethodNameEnum.QUERYPHOTOBANKIMAGEBYPATHS.getCode(),map);
			}else {
				map.put("pageSize",model.getPageSize());
				map.put("currentPage",model.getCurrentPage());
				map.put("locationType",model.getLocationType());
				json = httpTaoBaoApi.getTaoBaoApi(AliexpressMethodNameEnum.FINDIMAGEPAGE.getCode(),map);
			}

			Map<String,Object> retmap = JsonAnalysis.getGatewayMsg(json);
			String success = retmap.get("success").toString();
			if("200".equals(success)) {
				if(model.getType()==2){
					JSONObject jsonObject = JSONObject.parseObject(retmap.get("data").toString());
					Object jsonimages = jsonObject.get("images");
					if(jsonimages!=null) {
						JSONObject jsonObjectimages = JSONObject.parseObject(jsonimages.toString());
						Object photoUrlModel = jsonObjectimages.get(paths);
						if (photoUrlModel != null) {
							AliexpressPhoto aliexpressPhoto = JSONObject.parseObject(photoUrlModel.toString(), AliexpressPhoto.class);
							aliexpressPhoto.setUrl(model.getImageUrl());
							List<AliexpressPhoto> images = Lists.newArrayList();
							images.add(aliexpressPhoto);
							aliexpressPhotoBankService.insertAliexpressPhoto(images, model.getEmpowerId(), model.getSellerId());
						}
					}
				}else {
					AliexpressPhotoUrlModel photoUrlModel = JSONObject.parseObject(retmap.get("data").toString(), AliexpressPhotoUrlModel.class);
					if (model != null) {
						aliexpressPhotoBankService.insertAliexpressPhoto(photoUrlModel.getImages(), model.getEmpowerId(), model.getSellerId());
					}
				}
			}else {
				logger.info("调用接口错误日志="+retmap.get("msg"));
				//您的请求太频繁，或者您查询的页码太大，您的请求受到限制，请稍候。
				if("1180531".equals(success)){
					Thread.sleep(30*1000);//暂停30秒
					sender.sendPhoto(model);//再把当前数据填充到队列中
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("aliexpressPhoto操作队列消费end");
	}

	@RabbitListener(queues = "aliexpress-listing-queue")
	public void processAliexpressListing(String message) {
		logger.info("aliexpressListing操作队列消费start:{}",JSONObject.toJSONString(message));
		AliexpressPhotoModel model = JSONObject.parseObject(message,AliexpressPhotoModel.class);

		if(model.getType()==3){
			Map<String,Object> map = Maps.newHashMap();

			map.put("sessionKey",model.getToken());
			map.put("pageSize",model.getPageSize());
			map.put("currentPage",model.getCurrentPage());
			map.put("productStatusType",model.getProductStatusType());

			String json = httpTaoBaoApi.getTaoBaoApi(AliexpressMethodNameEnum.FINDPRODUCTPAGE.getCode(), map);
			Map<String,Object>retmap = JsonAnalysis.getGatewayMsg(json);
			String success = retmap.get("success").toString();

			if ("200".equals(success)) {
				AliexpressProductListModel aliexpressProductListModel = JSONObject.parseObject(retmap.get("data").toString(), AliexpressProductListModel.class);

				if(aliexpressProductListModel.getAeopAEProductDisplayDTOList()!=null){
					for(AliexpressProductModel aliexpressProductModel : aliexpressProductListModel.getAeopAEProductDisplayDTOList()){
						AliexpressPhotoModel photoModelmodel = new AliexpressPhotoModel();
						photoModelmodel.setEmpowerId(model.getEmpowerId());
						photoModelmodel.setProductStatusType(model.getProductStatusType());
						photoModelmodel.setSellerId(model.getSellerId());
						photoModelmodel.setToken(model.getToken());
						photoModelmodel.setUserId(model.getUserId());
						photoModelmodel.setUserName(model.getUserName());
						photoModelmodel.setProductMaxPrice(aliexpressProductModel.getProductMinPrice());
						photoModelmodel.setProductMinPrice(aliexpressProductModel.getProductMaxPrice());
						photoModelmodel.setItemId(aliexpressProductModel.getProductId());
						sender.sendListing(photoModelmodel);
					}
				}
			}
		}else {
			String success = "";
			try {
				if (model == null) {
					logger.info("processaliexpressListing操作队列消费end无数据");
					return;
				}
				BigDecimal productMinPrice = new BigDecimal(model.getProductMinPrice() == null ? "0" : model.getProductMinPrice());
				BigDecimal productMaxPrice = new BigDecimal(model.getProductMaxPrice() == null ? "0" : model.getProductMaxPrice());
				aliexpressListingService.updateAliexpressListing(model.getEmpowerId(), model.getUserId(), model.getUserName(),
						model.getSellerId(), null, model.getItemId(), productMinPrice, productMaxPrice);

			} catch (GlobalException e) {
				success = e.getErrorCode();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if ("1180531".equals(success)) {
					Thread.sleep(30 * 1000);//暂停30秒
					sender.sendPhoto(model);//再把当前数据填充到队列中
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.info("processAliexpressListing操作队列消费end无数据");
	}

}
