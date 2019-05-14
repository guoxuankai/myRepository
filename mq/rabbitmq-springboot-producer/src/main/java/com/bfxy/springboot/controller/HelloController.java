package com.bfxy.springboot.controller;

import com.bfxy.springboot.entity.Order;
import com.bfxy.springboot.producer.RabbitOrderSender;
import com.bfxy.springboot.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class HelloController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RabbitOrderSender rabbitOrderSender;

    @GetMapping("/hello/{msg}")
    public String hello(@PathVariable("msg") String msg) throws Exception {
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        String messageId = UUID.randomUUID().toString().replaceAll("-", "");
        Order order = new Order();
        order.setId(id);
        order.setName(msg);
        order.setMessageId(messageId);
        orderService.createOrder(order);


        return  msg;
    }
}
