package com.rondaful.cloud.commodity.init;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rondaful.cloud.commodity.service.MessageService;

@Component
public class SendUnAuditNum {
	
	@Autowired
	private MessageService messageService;
	

	@PostConstruct
	public void sendMsg() {
		messageService.unAuditBrandNumMsg();
		messageService.unAuditSkuNumMsg();
	}
}
