package com.rondaful.cloud.seller.rabbitmq;


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

    //自定义交换机名称
    public static final String EXCHANGE1 = "exchange1";
    
  
    public static final String EBAY_OPERTION_QUEUE = "ebayOpertionQueue";
    //自定义ebay操作路由键
    public static final String EBAY_OPERTION_ROUTINGKEY = "ebayOpertionRoutingkey";
    
    public static final String EBAY_OPERTION_EXCHANGE = "ebayExchange";


    public static final String ALIEXPRESS_QUEUE = "aliexpress-publish-queue";
    //自定义aliexpress操作路由键
    public static final String ALIEXPRESS_ROUTINGKEY = "aliexpressRoutingkey";

    public static final String ALIEXPRESS_EXCHANGE = "aliexpress-exchange";

    
    public static final String Empower_QUEUE = "empowerQueue";
    public static final String Empower_ROUTINGKEY = "empowerRoutingkey";
    public static final String Empower_EXCHANGE = "empowerExchange";


    public static final String ALIEXPRESS_PHOTO_QUEUE = "aliexpress-photo-queue";
    public static final String ALIEXPRESS_PHOTO_ROUTINGKEY = "aliexpressPhotoRoutingkey";
    public static final String ALIEXPRESS_PHOTO_EXCHANGE = "aliexpress-photo-exchange";

    public static final String ALIEXPRESS_LISTING_QUEUE = "aliexpress-listing-queue";
    public static final String ALIEXPRESS_LISTING_ROUTINGKEY = "aliexpresslistingRoutingkey";
    public static final String ALIEXPRESS_LISTING_EXCHANGE = "aliexpress-listing-exchange";

    public static final String EBAY_LISTING_QUEUE = "ebay-listing-queue";
    public static final String EBAY_LISTING_ROUTINGKEY = "ebayListingRoutingkey";
    public static final String EBAY_LISTING_EXCHANGE = "ebay-listing-exchange";

    //自定义队列
    @Bean("ebayListingQueue")
    public Queue ebayListingQueue() {
        return new Queue(EBAY_LISTING_QUEUE, true);
    }

    //自定义交换机
    @Bean("ebayListingExchange")
    public TopicExchange ebayListingExchange() {
        return new TopicExchange(EBAY_LISTING_EXCHANGE);
    }

    //自定义队列
    @Bean("aliexpressListingQueue")
    public Queue aliexpressListingQueue() {
        return new Queue(ALIEXPRESS_LISTING_QUEUE, true);
    }

    //自定义交换机
    @Bean("aliexpressListingExchange")
    public TopicExchange aliexpressListingExchange() {
        return new TopicExchange(ALIEXPRESS_LISTING_EXCHANGE);
    }


    //自定义队列
    @Bean("aliexpressPhotoQueue")
    public Queue aliexpressPhotoQueue() {
        return new Queue(ALIEXPRESS_PHOTO_QUEUE, true);
    }

    //自定义交换机
    @Bean("aliexpressPhotoExchange")
    public TopicExchange aliexpressPhotoExchange() {
        return new TopicExchange(ALIEXPRESS_PHOTO_EXCHANGE);
    }


    //自定义队列
    @Bean("empowerQueue")
    public Queue empowerQueue() {
        return new Queue(Empower_QUEUE, true);
    }

    //自定义交换机
    @Bean("empowerExchange")
    public TopicExchange empowerExchange() {
        return new TopicExchange(Empower_EXCHANGE);
    }
    
    
    //自定义队列
    @Bean("aliexpressQueue")
    public Queue aliexpressQueue() {
        return new Queue(ALIEXPRESS_QUEUE, true);
    }

    //自定义交换机
    @Bean("aliexpressExchange")
    public TopicExchange aliexpressExchange() {
        return new TopicExchange(ALIEXPRESS_EXCHANGE);
    }


    //自定义队列
    @Bean("ebayOpertionQueue")
    public Queue ebayQueue() {
        return new Queue(EBAY_OPERTION_QUEUE, true);
    }
    
    //自定义交换机
    @Bean("ebayExchange")
    public TopicExchange ebayExchange() {
        return new TopicExchange(EBAY_OPERTION_EXCHANGE);
    }
    
    
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
