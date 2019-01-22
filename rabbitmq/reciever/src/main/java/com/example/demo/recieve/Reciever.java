package com.example.demo.recieve;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "spring-boot-queue")
public class Reciever {

    @RabbitHandler
    public void process(String msg) {
        System.out.println("Receiver: " + msg);
    }

}