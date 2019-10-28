package com.rondaful.cloud.order.task;

import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.order.config.PropertyUtil;
import com.rondaful.cloud.order.seller.Empower;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

@Component
public class SyncOrderQueue {
    @Autowired
    public RedisUtils redisUtils;

    /**
     * 类加载时就初始化
     */
    private static volatile SyncOrderQueue instance = new SyncOrderQueue();

    static {
        //TODO 将实例放入Redis缓存，待Redis可用时候补充
    }

    private static LinkedBlockingQueue<Empower> ebayManualSyncOrderQueue =
            new LinkedBlockingQueue<>(Integer.valueOf(PropertyUtil.getProperty("linkedBlockingQueueSize")));

    private static Logger _log = LoggerFactory.getLogger(SyncOrderQueue.class);

    private SyncOrderQueue() {
    }

    public static SyncOrderQueue getInstance() {
        //TODO 取的时候都是从Redis中取，待Redis可用时候补充
        return instance;
    }

    public void put(Empower empower) throws InterruptedException {
        ebayManualSyncOrderQueue.put(empower);
        _log.info("向ebayManualSyncOrderQueue队列中放入一个元素，" + empower.getPinlianaccount());
    }

    public Empower take() throws InterruptedException {
        Empower empower = ebayManualSyncOrderQueue.take();
        _log.info("从ebayManualSyncOrderQueue队列中取出一个元素，" + empower.getPinlianaccount());
        return empower;
    }

    public int size() {
        int size = ebayManualSyncOrderQueue.size();
        _log.info("ebayManualSyncOrderQueue队列元素个数为：" + size);
        return size;
    }


    public void clear() {
        ebayManualSyncOrderQueue.clear();
        _log.info("将ebayManualSyncOrderQueue队列所有元素清空。。。");
    }
}
