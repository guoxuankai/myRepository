package com.rondaful.cloud.seller.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
public class TestReceiver {

    //监听队列queue-a
    @RabbitListener(queues = "queue-a")
    public void process(String message) {
        System.out.println("接收端Receiver  : " + message);
    }

}
