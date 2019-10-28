package com.rondaful.cloud.commodity.rabbitmq;


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

    //自定义队列名称-商品下架通知
    public static final String COMMODITY_LOWERFRAMES_QUEUE = "commodity-lowerframes-queue";

    //自定义路由键-SKU下架通知
    public static final String ROUTINGKEY_COMMODITY_LOWERFRAMES = "commodity.lowerframes.key";

    //自定义队列名称-商品删除通知
    public static final String COMMODITY_DELETE_QUEUE = "commodity-spu-delete";

    //自定义路由键-商品删除通知
    public static final String ROUTINGKEY_COMMODITY_DELETE = "commodity.spu.delete.key";

    //自定义队列名称-商品sku增加通知
    public static final String COMMODITY_SKU_ADD_QUEUE = "commodity-sku-add-queue";

    //自定义路由键-商品sku增加通知
    public static final String ROUTINGKEY_COMMODITY_SKU_ADD = "commodity.sku.add.key";

    //自定义交换机名称-商品服务
    public static final String EXCHANGE_COMMODITY = "commodity-exchange";
    
    //sku下架通知队列,卖家端用
    public static final String SKU_DOWN_STATE_QUEUE = "sku-down-queue";
    
    //SKU下架通知key,卖家端用
    public static final String ROUTINGKEY_SKU_DOWN_STATE = "sku.down.key";
    
    //sku上架架通知队列,卖家端用
    public static final String SKU_UP_STATE_QUEUE = "sku-up-queue";
    
    //SKU上架通知key,卖家端用
    public static final String ROUTINGKEY_SKU_UP_STATE = "sku.up.key";
    
    //待审核品牌，sku
    public static final String UNAUDIT_NUM_KEY = "unaudit.num.key";
    
    //待审核品牌，sku
    public static final String CMS_MESSAGE_DISPOSE_QUEUE = "cms-message-dispose-queue";
    
    //sku侵权通知队列,卖家端用
    public static final String SKU_TORT_QUEUE = "sku-tort-queue";
    
    //SKU侵权通知key,卖家端用
    public static final String ROUTINGKEY_SKU_TORT = "sku.tort.key";
    
    //转单通知队列,订单端用
    public static final String ORDER_TRANSFER_QUEUE = "order-transfer-queue";
    
    //转单通知key,订单端用
    public static final String ROUTINGKEY_ORDER_TRANSFER = "order.transfer.key";


    //自定义队列-商品服务下架通知
    @Bean(COMMODITY_LOWERFRAMES_QUEUE)
    public Queue queueA() {
        return new Queue(COMMODITY_LOWERFRAMES_QUEUE, true);
    }

    //自定义队列-商品服务删除通知
    @Bean(COMMODITY_DELETE_QUEUE)
    public Queue queueB() {
        return new Queue(COMMODITY_DELETE_QUEUE, true);
    }

    //自定义队列-商品sku增加通知
    @Bean(COMMODITY_SKU_ADD_QUEUE)
    public Queue queueC() {
        return new Queue(COMMODITY_SKU_ADD_QUEUE, true);
    }
    
    //sku下架通知队列,卖家端用
    @Bean(SKU_DOWN_STATE_QUEUE)
    public Queue queueD() {
        return new Queue(SKU_DOWN_STATE_QUEUE, true);
    }
    
    //sku上架架通知队列,卖家端用
    @Bean(SKU_UP_STATE_QUEUE)
    public Queue queueE() {
        return new Queue(SKU_UP_STATE_QUEUE, true);
    }
    
    @Bean(CMS_MESSAGE_DISPOSE_QUEUE)
    public Queue queueF() {
        return new Queue(CMS_MESSAGE_DISPOSE_QUEUE, true);
    }
    
    @Bean(SKU_TORT_QUEUE)
    public Queue queueG() {
        return new Queue(SKU_TORT_QUEUE, true);
    }
    
    @Bean(ORDER_TRANSFER_QUEUE)
    public Queue queueH() {
        return new Queue(ORDER_TRANSFER_QUEUE, true);
    }


    
    
    //自定义交换机-商品服务
    @Bean(EXCHANGE_COMMODITY)
    public TopicExchange exchange1() {
        return new TopicExchange(EXCHANGE_COMMODITY);
    }



    /**
     * 定义rabbit template用于数据的接收和发送
     * @return
     */
    @Bean("rabbitTemplateCommodity")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        /**若使用confirm-callback或return-callback，
         * 必须要配置publisherConfirms或publisherReturns为true
         * 每个rabbitTemplate只能有一个confirm-callback和return-callback
         */
        template.setConfirmCallback(new MsgSendConfirmCallBack());
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
