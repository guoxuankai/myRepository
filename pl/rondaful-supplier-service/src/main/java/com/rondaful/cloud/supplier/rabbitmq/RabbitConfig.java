package com.rondaful.cloud.supplier.rabbitmq;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq配置类
 * */
@Configuration
public class RabbitConfig {

    //自定义队列名称
    public static final String QUEUE1 = "queue-a";

    //自定义路由键
    public static final String ROUTINGKEY1 = "routingKey1";
    
    public static final String DISCARD_WAREHOUSE_MAIL = "routingKey.discard.warehouse.mail";

    //自定义交换机名称
    public static final String EXCHANGE1 = "exchange1";
    
    //仓库状态修改
    public static final String WAREHOUSEQUEUE = "wareHouseQueue";
    
    public static final String WAREHOUSEROUTINGKEY = "wareHouseRoutingKey";
    
    public static final String WAREHOUSEEXCHANGE = "wareHouseExchange";
    
    //仓库授权
    public static final String WAREHOUSEAUTHORIZEQUEUE = "wareHouseAuthorizeQueue";
    
    public static final String WAREHOUSEAUTHORIZEROUTINGKEY = "wareHouseAuthorizeRoutingKey";
    
    public static final String WAREHOUSEAUTHORIZEEXCHANGE = "wareHouseAuthorizeExchange";

    //自定义队列
    @Bean("queueA")
    public Queue queueA() {
        return new Queue(QUEUE1, true);
    }

    //自定义交换机
    @Bean("exchange1")
    public TopicExchange exchange1() {
        return new TopicExchange(EXCHANGE1);
    }

    @Bean("wareHouseQueue")
    public Queue wqreHouseQueue() {
        return new Queue(WAREHOUSEQUEUE, true);
    }
    
  
    @Bean("wareHouseExchange")
    public TopicExchange wareHouseExchange() {
        return new TopicExchange(WAREHOUSEEXCHANGE);
    }
    
    @Bean("wareHouseAuthorizeQueue")
    public Queue wqreHouseAuthorizeQueue() {
        return new Queue(WAREHOUSEAUTHORIZEQUEUE, true);
    }
    
    @Bean("wareHouseAuthorizeExchange")
    public TopicExchange wareHouseAuthorizeExchange() {
        return new TopicExchange(WAREHOUSEAUTHORIZEEXCHANGE);
    }

    /**
     * 消息发送交换机确认机制
     */
    @Bean
    public MsgSendConfirmCallBack msgSendConfirmCallBack(){
        return new MsgSendConfirmCallBack();
    }


    /**
     * 定义rabbit template用于数据的接收和发送
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MsgSendConfirmCallBack msgSendConfirmCallBack) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        /**若使用confirm-callback或return-callback，
         * 必须要配置publisherConfirms或publisherReturns为true
         * 每个rabbitTemplate只能有一个confirm-callback和return-callback
         */
        template.setConfirmCallback(msgSendConfirmCallBack);
        //template.setReturnCallback(msgSendReturnCallback());
        /**
         * 使用return-callback时必须设置mandatory为true，或者在配置中设置mandatory-expression的值为true，
         * 可针对每次请求的消息去确定’mandatory’的boolean值，
         * 只能在提供’return -callback’时使用，与mandatory互斥
         */
        //  template.setMandatory(true);
        return template;
    }

}
