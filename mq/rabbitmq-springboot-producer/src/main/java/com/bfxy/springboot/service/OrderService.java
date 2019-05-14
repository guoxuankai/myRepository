package com.bfxy.springboot.service;

import com.bfxy.springboot.constant.Constants;
import com.bfxy.springboot.entity.BrokerMessageLog;
import com.bfxy.springboot.entity.Order;
import com.bfxy.springboot.mapper.BrokerMessageLogMapper;
import com.bfxy.springboot.producer.RabbitOrderSender;
import com.bfxy.springboot.utils.FastJsonConvertUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    private BrokerMessageLogMapper brokerMessageLogMapper;

    @Autowired
    private RabbitOrderSender rabbitOrderSender;


    public void createOrder(Order order) throws Exception {
        // log insert
        BrokerMessageLog brokerMessageLog = new BrokerMessageLog();
        brokerMessageLog.setMessageId(order.getMessageId());
        //save order message as json
        brokerMessageLog.setMessage(FastJsonConvertUtil.convertObjectToJSON(order));
        brokerMessageLog.setStatus("0");
        brokerMessageLog.setNextRetry(DateUtils.addMinutes(new Date(), Constants.ORDER_TIMEOUT));
        brokerMessageLog.setCreateTime(new Date());
        brokerMessageLog.setUpdateTime(new Date());
        brokerMessageLogMapper.insert(brokerMessageLog);
        // order message sender
        rabbitOrderSender.sendOrder(order);
    }

}
