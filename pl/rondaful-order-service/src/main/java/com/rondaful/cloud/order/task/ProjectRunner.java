package com.rondaful.cloud.order.task;

import com.rondaful.cloud.order.config.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class ProjectRunner implements CommandLineRunner {
    private static Logger _log = LoggerFactory.getLogger(ProjectRunner.class);

    int threadNum = Integer.valueOf(PropertyUtil.getProperty("manualSyncEbayOrderThread"));

    int poolSize = Runtime.getRuntime().availableProcessors() * 2;
    BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(512);
    RejectedExecutionHandler policy = new ThreadPoolExecutor.DiscardPolicy();
    private ExecutorService threadPool = new ThreadPoolExecutor(threadNum, threadNum, 0, TimeUnit.SECONDS, queue, policy);


    @Override
    public void run(String... strings) {
        for (int i = 0; i < threadNum; i++) {
            threadPool.execute(new ManualSyncEbayOrderTesk());
        }
        _log.info("_________项目启动，初始化手工同步订单线程池（线程数量为 {} ）完成，线程池拒绝策略为" +
                "_________{}_________开始执行消费队列中的任务_________", threadNum, "DiscardPolicy");
    }
}