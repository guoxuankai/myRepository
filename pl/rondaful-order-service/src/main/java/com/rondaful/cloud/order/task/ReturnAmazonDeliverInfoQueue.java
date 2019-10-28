package com.rondaful.cloud.order.task;

import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.order.config.PropertyUtil;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

@Component
public class ReturnAmazonDeliverInfoQueue {

    @Autowired
    public RedisUtils redisUtils;

    /**
     * 类加载时就初始化
     */
    private static ReturnAmazonDeliverInfoQueue instance = new ReturnAmazonDeliverInfoQueue();

    /*static {
        //将实例放入Redis缓存，待Redis可用时候补充
        redisUtils.exists("amazonDeliverInfoQueue");
    }*/

    private LinkedBlockingQueue<AmazonOrder> returnAmazonDeliverInfoQueue =
            new LinkedBlockingQueue<AmazonOrder>(Integer.valueOf(PropertyUtil.getProperty("returnAmazonDeliverInfoQueue")));

    private Logger _log = LoggerFactory.getLogger(ReturnAmazonDeliverInfoQueue.class);

    private ReturnAmazonDeliverInfoQueue() {
    }

    public static ReturnAmazonDeliverInfoQueue getInstance() {
        //取的时候都是从Redis中取，待Redis可用时候补充
        return instance;
    }

    private LinkedBlockingQueue<AmazonOrder> getRedisReturnAmazonDeliverInfoQueue() {
        if (redisUtils.exists("returnAmazonDeliverInfoQueue")) {
            return (LinkedBlockingQueue<AmazonOrder>) redisUtils.get("returnAmazonDeliverInfoQueue");
        } else {
            LinkedBlockingQueue<AmazonOrder> returnAmazonDeliverInfoQueue =
                    new LinkedBlockingQueue<AmazonOrder>(Integer.valueOf(PropertyUtil.getProperty("returnAmazonDeliverInfoQueue")));
            redisUtils.set("returnAmazonDeliverInfoQueue", returnAmazonDeliverInfoQueue);
            return returnAmazonDeliverInfoQueue;
        }
    }

    public void put(AmazonOrder amazonOrder) throws InterruptedException {
//        returnAmazonDeliverInfoQueue.put(amazonOrder);
        this.getRedisReturnAmazonDeliverInfoQueue().put(amazonOrder);
        _log.info("向returnAmazonDeliverInfoQueue队列中放入一个元素，" + amazonOrder.getOrderId());
    }

    public AmazonOrder take() throws InterruptedException {
//        AmazonOrder amazonOrder = returnAmazonDeliverInfoQueue.take();
        AmazonOrder amazonOrder = this.getRedisReturnAmazonDeliverInfoQueue().take();
        _log.info("从returnAmazonDeliverInfoQueue队列中取出一个元素，" + amazonOrder.getPlSellerAccount());
        return amazonOrder;
    }

    public int size() {
//        int size = returnAmazonDeliverInfoQueue.size();
        int size = this.getRedisReturnAmazonDeliverInfoQueue().size();
        _log.info("returnAmazonDeliverInfoQueue队列元素个数为：" + size);
        return size;
    }

    public void clear() {
//        returnAmazonDeliverInfoQueue.clear();
        this.getRedisReturnAmazonDeliverInfoQueue().clear();
        _log.info("将returnAmazonDeliverInfoQueue队列所有元素清空。。。。。。");
    }
}
