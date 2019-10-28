package com.brandslink.cloud.user.rabbitmq;


import org.springframework.amqp.core.FanoutExchange;
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
    public static final String TEST_QUEUE = "test-queue";

    //自定义路由键
    public static final String ROUTINGKEY_TEST = "test.key";

    //自定义交换机名称
    public static final String EXCHANGE_TEST = "test-exchange";

    //客户更新交换机
    public static final String EXCHANGE_CUSTOMER = "customer-exchange";

    //客户更新队列
    public static final String CUSTOMER_QUEUE = "customer-queue";


    //自定义队列
    @Bean(TEST_QUEUE)
    public Queue queueA() {
        return new Queue(TEST_QUEUE, true);
    }

    //更新客户队列
    @Bean(CUSTOMER_QUEUE)
    public Queue warehouseQueue1(){
        return new Queue(CUSTOMER_QUEUE);
    }


    //自定义交换机-商品服务
    @Bean(EXCHANGE_TEST)
    public TopicExchange exchange1() {
        return new TopicExchange(EXCHANGE_TEST);
    }

    //自定义广播交换机
    @Bean(EXCHANGE_CUSTOMER)
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(EXCHANGE_CUSTOMER);
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
