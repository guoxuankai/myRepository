package com.rondaful.cloud.order.rabbitmq;


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

    //****************************************************************************************************************

    //被销毁的仓库和邮寄方式消息队列名称
    public static final String QUEUE_DISCARD_WAREHOUSE_OR_MAIL = "queue-discard-warehouse-mail";

    //被销毁的仓库和邮寄方式消息路由键
    public static final String ROUTINGKEY_DISCARD_WAREHOUSE_OR_MAIL = "routingKey.discard.warehouse.mail";


    //****************************************************************************************************************

    //发送缺货消息队列名称
    public static final String QUEUE_ORDER_STOCK_OUT = "queue-order-stock-out";

    //发送缺货消息交换机
    public static final String EXCHANGE_ORDER_STOCK_OUT = "exchangeOrderStockOut";

    //发送缺货消息路由键
    public static final String  ROUTINGKEY_ORDER_STOCK_OUT = "routingKey.order.stock.out";

    //***************************************************************************************************************
    //发送取消财务订单消息队列
    public static final String QUEUE_FINANCE_ORDER_CANCEL = "queue-finance-order-cancel";

    //发送取消财务订单消息交换机
    public static final String EXCHANGE_FINANCE_ORDER_CANCEL = "exchangeFinanceOrderCancel";

    //发送取消财务订单消息路由键
    public static final String  ROUTINGKEY_FINANCE_ORDER_CANCEL = "routingKey.finance.order.cancel";

    //发送确认财务订单扣款消息队列
    public static final String QUEUE_FINANCE_ORDER_CONFIRM = "queue-finance-order-confirm";

    //发送确认财务订单扣款消息交换机
    public static final String EXCHANGE_FINANCE_ORDER_CONFIRM = "exchangeFinanceOrderConfirm";

    //发送确认财务订单扣款消息路由键
    public static final String  ROUTINGKEY_FINANCE_ORDER_CONFIRM = "routingKey.finance.order.confirm";
    //***************************************************************************************************************

    //发送出库记录消息队列
    public static final String QUEUE_WAREHOUSE_DELIVERY_RECORD = "queue-warehouse-delivery-record";

    //发送出库记录消息交换机
    public static final String EXCHANGE_WAREHOUSE_DELIVERY_RECORD = "exchangeWarehouseDeliveryRecord";

    //发送出库记录消息路由键
    public static final String ROUTINGKEY_WAREHOUSE_DELIVERY_RECORD = "routingKey.warehouse.delivery.record";

    //***************************************************************************************************************

    //发送谷仓库存变更消息队列
    public static final String QUEUE_GOODCANG_STOCKCHANGE = "queue-goodcang-stockchange";

    //发送谷仓库存变更消息交换机
    public static final String EXCHANGE_GOODCANG_STOCKCHANGE = "exchangeGoodCangStockChange";

    //发送谷仓库存变更消息路由键
    public static final String ROUTINGKEY_GOODCANG_STOCKCHANGE = "routingKey.goodcang.stockchange";
    //***************************************************************************************************************

    //发送谷仓入库单推送消息队列
    public static final String QUEUE_GOODCANG_SENDRECEIVING = "queue-goodcang-sendreceiving";

    //发送谷仓入库单推送消息交换机
    public static final String EXCHANGE_GOODCANG_SENDRECEIVING = "exchangeGoodCangSendReceiving";

    //发送谷仓入库单推送消息路由键
    public static final String ROUTINGKEY_GOODCANG_SENDRECEIVING = "routingKey.goodcang.sendreceiving";

    //***************************************************************************************************************

    //速卖通订单转换
    public static final String QUEUE_ALIEXPRESS_CONVERT_ORDER = "queue.aliexpress.convert.order";
    public static final String EXCHANGE_ALIEXPRESS_CONVERT_ORDER = "exchange.aliexpress.convert.order";
    public static final String ROUTINGKEY_ALIEXPRESS_CONVERT_ORDER = "routingKey.aliexpress.convert.order";

    // ebay，amazon，aliexpress 订单转换
    public static final String QUEUE_BASE_CONVERT_ORDER = "queue.base.convert.order";
    public static final String EXCHANGE_BASE_CONVERT_ORDER = "exchange.base.convert.order";
    public static final String ROUTINGKEY_BASE_CONVERT_ORDER = "routingKey.base.convert.order";


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
     * 被销毁的仓库和邮寄方式消息队列
     * @return
     */
    @Bean("queueDiscardWarehouseMail")
    public Queue queueDiscardWarehouseMail(){
        return new Queue(QUEUE_DISCARD_WAREHOUSE_OR_MAIL,true);
    }

    /**
     * 订单库存不足消息队列
     * @return 消息队列
     */
    @Bean(QUEUE_ORDER_STOCK_OUT)
    public Queue queueOrderStockOut(){
        return new Queue(QUEUE_ORDER_STOCK_OUT,true);
    }

    /**
     * 订单库存不足交换机
     * @return 交换机
     */
    @Bean(EXCHANGE_ORDER_STOCK_OUT)
    public TopicExchange exchangeOrderStockOut(){
        return new TopicExchange(EXCHANGE_ORDER_STOCK_OUT);
    }

    /**
     * 取消财务订单消息队列
     * @return 消息队列
     */
    @Bean("queueFinanceOrderCancel")
    public Queue queueFinanceOrderCancel(){
        return new Queue(QUEUE_FINANCE_ORDER_CANCEL,true);
    }



    /**
     * 取消财务订单交换机
     * @return 交换机
     */
    @Bean("exchangeFinanceOrderCancel")
    public TopicExchange exchangeFinanceOrderCancel(){
        return new TopicExchange(EXCHANGE_FINANCE_ORDER_CANCEL);
    }

    /**
     * 确认财务订单扣款消息队列
     * @return 消息队列
     */
    @Bean("queueFinanceOrderConfirm")
    public Queue queueFinanceOrderConfirm(){
        return new Queue(QUEUE_FINANCE_ORDER_CONFIRM,true);
    }

    /**
     * 确认财务订单扣款交换机
     * @return 交换机
     */
    @Bean("exchangeFinanceOrderConfirm")
    public TopicExchange exchangeFinanceOrderConfirm(){
        return new TopicExchange(EXCHANGE_FINANCE_ORDER_CONFIRM);
    }

    /**
     * 发送出库记录消息队列
     * @return 消息队列
     */
    @Bean("queueWarehouseDeliveryRecord")
    public Queue queueWarehouseDeliveryRecord(){
        return new Queue(QUEUE_WAREHOUSE_DELIVERY_RECORD,true);
    }

    /**
     * 发送出库记录交换机
     * @return 交换机
     */
    @Bean("exchangeWarehouseDeliveryRecord")
    public TopicExchange exchangeWarehouseDeliveryRecord(){
        return new TopicExchange(EXCHANGE_WAREHOUSE_DELIVERY_RECORD);
    }

    /**
     * 发送谷仓库存变更消息队列
     * @return 消息队列
     */
    @Bean("queueGoodcangStockchange")
    public Queue queueGoodcangStockchange(){
        return new Queue(QUEUE_GOODCANG_STOCKCHANGE,true);
    }

    /**
     * 发送谷仓库存变更交换机
     * @return 交换机
     */
    @Bean("exchangeGoodCangStockChange")
    public TopicExchange exchangeGoodCangStockChange(){
        return new TopicExchange(EXCHANGE_GOODCANG_STOCKCHANGE);
    }

    /**
     * 发送谷仓入库单推送消息队列
     * @return 消息队列
     */
    @Bean("queueGoodcangSendreceiving")
    public Queue queueGoodcangSendreceiving(){
        return new Queue(QUEUE_GOODCANG_SENDRECEIVING,true);
    }

    /**
     * 发送谷仓入库单推送交换机
     * @return 交换机
     */
    @Bean("exchangeGoodCangSendReceiving")
    public TopicExchange exchangeGoodCangSendReceiving(){
        return new TopicExchange(EXCHANGE_GOODCANG_SENDRECEIVING);
    }

    /**
     * 订单转换
     * @return
     */
    @Bean("queueAliexpressConvertOrder")
    public Queue queueAliexpressConvertOrder(){
        return new Queue(QUEUE_ALIEXPRESS_CONVERT_ORDER,true);
    }

    @Bean("exchangeAliexpressConvertOrder")
    public TopicExchange exchangeAliexpressConvertOrder(){
        return new TopicExchange(EXCHANGE_ALIEXPRESS_CONVERT_ORDER);
    }


    /**
     * ebay，amazon，aliexpress 订单转换交换机
     */
    @Bean("queueBaseConvertOrder")
    public Queue queueBaseConvertOrder(){
        return new Queue(QUEUE_BASE_CONVERT_ORDER,true);
    }

    /**
     * ebay，amazon，aliexpress 订单转换队列
     */
    @Bean("exchangeBaseConvertOrder")
    public TopicExchange exchangeBaseConvertOrder(){
        return new TopicExchange(EXCHANGE_BASE_CONVERT_ORDER);
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
