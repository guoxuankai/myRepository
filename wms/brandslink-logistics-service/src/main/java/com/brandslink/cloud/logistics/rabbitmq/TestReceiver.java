package com.brandslink.cloud.logistics.rabbitmq;

//@Component
public class TestReceiver {

    //监听队列queue-a
    //@RabbitListener(queues = "commodity-spu-delete")
    public void process(String message) {
        System.out.println("接收端Receiver  : " + message);
    }

}
