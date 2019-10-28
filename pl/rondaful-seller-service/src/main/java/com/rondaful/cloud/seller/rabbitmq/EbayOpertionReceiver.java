package com.rondaful.cloud.seller.rabbitmq;


import com.rondaful.cloud.seller.entity.ebay.EbayListingMQModel;
import com.rondaful.cloud.seller.service.IEbayListingService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.seller.service.IEbayBaseService;
import com.rondaful.cloud.seller.vo.PublishListingVO;

@Component
public class EbayOpertionReceiver {

	
    private final Logger logger = LoggerFactory.getLogger(EbayOpertionReceiver.class);
	@Autowired
	private IEbayBaseService ebayBaseService;
	@Autowired
	private IEbayListingService ebayListingService;

	@RabbitListener(queues = "ebayOpertionQueue")
	public void process(PublishListingVO vo) {
		logger.info("ebay操作队列消费start:{}",JSONObject.toJSONString(vo));
		/**
		 * listingType 区分是单属性刊登还是多属性      【1=单属性一口价 2=多属性一口价 3=拍卖】
		 * status  区分是产品到期下架后重刊登，还是复制为草稿后在刊登 【1=草稿,2=刊登中,3=已下线,4=在线,5=刊登失败】  
		 */
		try {
			if (vo.getListingType() != 2) { 
				if (vo.getStatus() ==3){  
					ebayBaseService.relistItem(vo);
				}else if(vo.getStatus() == 2 || vo.getStatus() == 1 || vo.getStatus() == 5){
					ebayBaseService.addItem(vo);
				}else if (vo.getStatus() ==4 && StringUtils.isNotBlank(vo.getItemid())){
					ebayBaseService.reviseItem(vo);
				}
			} else {
				if (vo.getStatus() ==3){
					ebayBaseService.relistFixedPriceItem(vo);
				}else if(vo.getStatus() == 2 || vo.getStatus() == 1 || vo.getStatus() == 5){
					ebayBaseService.addFixedPriceItem(vo);
				}else if(vo.getStatus() ==4 && StringUtils.isNotBlank(vo.getItemid())){
					ebayBaseService.reviseFixedPriceItem(vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("ebay操作队列消费end:{}",vo.getTitle());
	}

	@RabbitListener(queues = "ebay-listing-queue")
	public void processEbayListing(String message) {
		logger.info("EbayListing操作队列消费start:{}",message);
		try {
			EbayListingMQModel model = JSONObject.parseObject(message,EbayListingMQModel.class);
			if(model==null){
				logger.info("aliexpressPhoto操作队列消费end无数据");
				return;
			}
			ebayListingService.saveEbayListing(model.getEmpowerId(),model.getUserId(),
					model.getUserName(), model.getSellerId(), null, model.getItemId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("EbayListing操作队列消费end");
	}


}
