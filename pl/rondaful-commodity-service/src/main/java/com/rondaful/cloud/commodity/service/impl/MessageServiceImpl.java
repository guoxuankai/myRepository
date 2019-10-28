package com.rondaful.cloud.commodity.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rondaful.cloud.commodity.entity.Message;
import com.rondaful.cloud.commodity.mapper.BrandMapper;
import com.rondaful.cloud.commodity.mapper.CommoditySpecMapper;
import com.rondaful.cloud.commodity.rabbitmq.MQSender;
import com.rondaful.cloud.commodity.service.MessageService;
import com.rondaful.cloud.commodity.vo.MessageDisposeVo;
import com.rondaful.cloud.common.rabbitmq.MessageSender;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class MessageServiceImpl implements MessageService {
	
	/**消息种类:商品消息*/
	private final static String COMMODITY_MESSAGE="COMMODITY_MESSAGE";
	
	@Autowired
    private MessageSender messageSender;
	
	@Autowired
    private MQSender mqSender;
	
	@Autowired
	private BrandMapper brandMapper;
	
	@Autowired
	private CommoditySpecMapper commoditySpecMapper;
	

	@Override
	public void priceChangeMsg(String userId, String account, String systemSku, String platform) {
		StringBuilder sb=new StringBuilder();
		sb.append(systemSku).append("#").append(platform);
		buildMessage("0","1","0",userId,account,sb.toString(),1);
	}



	@Override
	public void downStateMsg(String userId, String account, String systemSku, String platform) {
		StringBuilder sb=new StringBuilder();
		sb.append(systemSku).append("#").append(platform);
		buildMessage("0","1","1",userId,account,sb.toString(),2);
	}


	@Override
	public void tortMsg(String userId, String account, String systemSku, String platform) {
		StringBuilder sb=new StringBuilder();
		sb.append(systemSku).append("#").append(platform);
		buildMessage("0","1","1",userId,account,sb.toString(),3);
	}
	
	/**
	 * @param platform 消息接收平台 0：全部， 1：PC， 2：App
	 * @param receiver 通知对象  0供应商, 1卖家 , 2管理后台
	 * @param isDialog 是否弹窗 0否  1是
	 * @param userId
	 * @param account
	 * @param value 多个变量值用#拼接
	 * @param type 1：价格变动，2：商品下架，3：商品侵权
	 * @return void
	 */
	private void buildMessage(String platform,String receiver,String isDialog,String userId, String account,String value,int type) {
		Message msg = new Message();
        msg.setMessageCategory(COMMODITY_MESSAGE);
        msg.setMessagePlatform(platform);
        msg.setReceiveSys(receiver);
        msg.setIsDialog(isDialog);
        msg.setUserId(userId);
        msg.setMessageScceptUserName(account);
        msg.setMessageContent(value);
        if (type == 1) {
            msg.setMessageType("COMMODITY_PRICE_CHANGE");
        }else if (type == 2) {
            msg.setMessageType("COMMODITY_SHELVES");
        }else if (type == 3) {
        	msg.setMessageType("COMMODITY_TORT");
		}
        messageSender.sendMessage(JSONObject.fromObject(msg).toString());
	}



	@Override
	public void unAuditSkuNumMsg() {
		int skuNum=commoditySpecMapper.getUnAuditNum();
		MessageDisposeVo skuMsg=new MessageDisposeVo();
		skuMsg.setIdentify("COMMODITY_CHECK");
		skuMsg.setBelongSys(2);
		skuMsg.setNum(skuNum);
		mqSender.unAuditNum(JSONObject.fromObject(skuMsg).toString());
	}

	@Override
	public void unAuditBrandNumMsg() {
		int brandNum=brandMapper.getUnAuditNum();
		MessageDisposeVo brandMsg=new MessageDisposeVo();
		brandMsg.setIdentify("COMMODITY_BRAND_CHECK");
		brandMsg.setBelongSys(2);
		brandMsg.setNum(brandNum);
		mqSender.unAuditNum(JSONObject.fromObject(brandMsg).toString());
	}
}
