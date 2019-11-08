package com.brandslink.cloud.common.push;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangzefei
 * @Classname PushThirdConfig
 * @Description 推送到第三方开发者服务配置
 * @Date 2019/8/3 9:57
 */
@Configuration
public class PushThirdConfig {


    /**
     * 推送第三方延迟队列名称
     */
    public static final String THIRD_DELAY_KEY = "third-delay-key";

    /**
     * 推送第三方延迟队列名称
     */
    public static final String THIRD_DELAY_QUEUE = "third-delay-queue";
    /**
     * 推送第三方延迟交换机
     */
    public static final String THIRD_DELAY_EXCHANGE = "third-delay-exchange";

    /**
     * 推送第三方消息key
     */
    public static final String PUSH_THIRD_KEY = "push-third-key";

    /**
     * 推送第三方队列名称
     */
    public static final String PUSH_THIRD_QUEUE = "push-third-queue";

    /**
     * 推送第三方交换机
     */
    public static final String PUSH_THIRD_EXCHANGE = "push-third-exchange";

    /**
     * 推送队列
     */
    @Bean
    public Queue pushThirdQueue() {
        return new Queue(PUSH_THIRD_QUEUE, true); //队列持久
    }

    /**
     * 推送交换机
     */
    @Bean
    public DirectExchange pushThirdExchange() {
        return new DirectExchange(PUSH_THIRD_EXCHANGE);
    }

    // 推送交换机与推送队列绑定
    @Bean
    public Binding bindPushQueue(Queue pushThirdQueue, DirectExchange pushThirdExchange){
        return BindingBuilder.bind(pushThirdQueue).to(pushThirdExchange).with(PUSH_THIRD_KEY);
    }

    // 延迟队列
    @Bean
    public Queue thirdDelayQueue() {
        return new Queue(THIRD_DELAY_QUEUE, true);
    }

    // 延迟交换机
    @Bean
    public CustomExchange thirdDelayExchange() {
        Map<String, Object> map = new HashMap<>();
        map.put("x-delayed-type", "direct");
        return new CustomExchange(THIRD_DELAY_EXCHANGE, "x-delayed-message",true, false, map);
    }
    //延迟交换机与延迟队列绑定
    @Bean("bindingDelayQueue")
    Binding bindingDelayQueue(Queue thirdDelayQueue, CustomExchange thirdDelayExchange){
        return BindingBuilder.bind(thirdDelayQueue).to(thirdDelayExchange).with(THIRD_DELAY_KEY).noargs();
    }

}
