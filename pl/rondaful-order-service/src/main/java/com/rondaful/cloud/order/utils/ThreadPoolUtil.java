package com.rondaful.cloud.order.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Blade
 * @date 2019-07-04 20:40:06
 **/
public class ThreadPoolUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(ThreadPoolUtil.class);

    // 系统核数
    private final static int SYSTEM_CORE_SIZE = Runtime.getRuntime().availableProcessors();

    // 线程池核心线程数
    private final static int CORE_POOL_SIZE = SYSTEM_CORE_SIZE;
    // 线程池最大线程数
    private final static int MAX_POOL_SIZE = SYSTEM_CORE_SIZE * 2;
    // 线程池空闲时间
    private final static long KEEP_ALIVE_TIME = 30;
    // 队列 默认长度2048
    private final static BlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<Runnable>(2048);
    // 抛弃策略
    private final static RejectedExecutionHandler POLICY = new ThreadPoolExecutor.DiscardPolicy();
    // 线程工厂
    private final static ThreadFactory THREAD_FACTORY = new NamedThreadFactory("Common Thread Pool");
    // 线程池
    private static ThreadPoolExecutor threadPoolExecutor;

    private ThreadPoolUtil() {

    }

    /**
     * 获取线程池实例
     *
     * @return {@link ThreadPoolExecutor}
     */
    public static ThreadPoolExecutor getInstance() {
        if (null == threadPoolExecutor) {
            synchronized (ThreadPoolUtil.class) {
                if (null == threadPoolExecutor) {
                    threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                            TimeUnit.SECONDS, WORK_QUEUE, THREAD_FACTORY, POLICY);

                    LOGGER.info("common thread pool created: core_pool_size = {}, max_pool_size = {}, keep_alive_time = {}s, policy = {}",
                            CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, POLICY.getClass().getSimpleName());
                }
            }
        }

        return threadPoolExecutor;
    }

    /**
     * 执行线程
     *
     * @param thread 需要执行的线程
     */
    public static void executeThread(Thread thread) {
        int size = WORK_QUEUE.size();
        if (size % 10 == 0) {
            LOGGER.info("now thread queue size is : {}", size);
        }
        getInstance().execute(thread);
    }

    /**
     * 执行线程并返回指定结果
     *
     * @param task {@link Callable<T>}
     * @param <T>  T
     * @return {@link Future<T>}
     */
    public static <T> Future<T> submitThread(Callable<T> task) {
        return getInstance().submit(task);
    }

    public static void shutdown() {
        getInstance().shutdownNow();
    }
}
