
package com.brandslink.cloud.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: yangzefei
 * @date: 2019/8/14 14:22
 * @description: 公共线程池
 **/
public class ThreadUtil {
    private final static Logger logger = LoggerFactory.getLogger(ThreadUtil.class);
    // 线程工厂键值对，不同的业务线程对应不同的线程工厂
    private static Map<String,NamedThreadFactory> threadFactoryMap=new HashMap<>(32);
    // 系统CPU核数
    private static int SYSTEM_CORE_SIZE = Runtime.getRuntime().availableProcessors();
    // 线程池核心线程数=CPU核数-1, 留一个核心处理其它事情
    private static int CORE_POOL_SIZE =SYSTEM_CORE_SIZE-1;
    // 线程池最大线程数=(CPU核数*2)+1
    private static int MAX_POOL_SIZE =(SYSTEM_CORE_SIZE * 2)+1;
    // 线程池空闲时间
    private static long KEEP_ALIVE_TIME = 60;
    // 线程等待队列
    private static BlockingQueue WORK_QUEUE=new LinkedBlockingQueue<>(128);
    // 线程工厂
    private static NamedThreadFactory threadFactory=new NamedThreadFactory("pool-thread-util");
    //自定义拒绝策略，根据拒绝策略枚举选择不同的策略
    private static RejectedExecutionHandler rejectHandler=new CustomRejectedExecutionHandler();
    //不需要返回值线程的线程池
    private static ExecutorService threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE,KEEP_ALIVE_TIME,TimeUnit.SECONDS,WORK_QUEUE,threadFactory,rejectHandler);
    //需要返回值线程的线程池
    private static ExecutorService callablePoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,64,KEEP_ALIVE_TIME,TimeUnit.SECONDS,new SynchronousQueue<>(),threadFactory,rejectHandler);
    /**
     * 执行线程
     * @param threadName 线程名(不同的业务定义不同的线程名)
     * @param daemon 是否为守护线程(服务线程)
     * @param runnable 需要执行的线程
     * @return
     */
    private static void execute(String threadName,boolean daemon,Runnable runnable){
        execute(threadName,RejectedExecutionEnum.ResetPutQueue,daemon,runnable);
    }

    /**
     * 执行线程
     * @param threadName 线程名(不同的业务定义不同的线程名)
     * @param daemon 是否为守护线程(服务线程)
     * @param rejected 拒绝策略
     * @param runnable 需要执行的线程
     * @return
     */
    private static void execute(String threadName,RejectedExecutionEnum rejected,boolean daemon,Runnable runnable){
        NamedThreadFactory factory=threadFactoryMap.get(threadName);
        if(factory==null){
            factory=new NamedThreadFactory(threadName,daemon);
            threadFactoryMap.put(threadName,factory);
        }
        threadPoolExecutor.execute(factory.newThread(runnable,rejected));
    }
    /**
     * 执行线程
     * @param threadName 线程名(不同的业务定义不同的线程名)
     * @param runnable 需要执行的线程
     * @return
     */
    public static void execute(String threadName,Runnable runnable) {
        execute(threadName,RejectedExecutionEnum.ResetPutQueue,false,runnable);
    }

    /**
     * 执行线程
     * @param threadName 线程名(不同的业务定义不同的线程名)
     * @param rejected 拒绝策略
     * @param runnable 需要执行的线程
     * @return
     */
    public static void execute(String threadName,RejectedExecutionEnum rejected,Runnable runnable){
        execute(threadName,rejected,false,runnable);
    }

    /**
     * 提交线程
     * @param threadName
     * @param callable 具有返回值线程
     * @param <T> 返回值类型
     * @return
     */
    public static <T> Future<T> submit(String threadName,Callable<T> callable){
        return submit(threadName,RejectedExecutionEnum.ResetPutQueue,callable);
    }

    /**
     * 提交线程
     * @param threadName 线程名(不同的业务定义不同的线程名)
     * @param rejected 拒绝策略
     * @param callable 具有返回值线程
     * @param <T> 返回值类型
     * @return
     */
    public static <T> Future<T> submit(String threadName,RejectedExecutionEnum rejected,Callable<T> callable){
        FutureTask future=new FutureTask(callable);
        NamedThreadFactory factory=threadFactoryMap.get(threadName);
        if(factory==null){
            factory=new NamedThreadFactory(threadName,false);
            threadFactoryMap.put(threadName,factory);
        }
        callablePoolExecutor.execute(factory.newThread(new Thread(future),rejected));
        return future;
    }

    /**
     * 重写线程池拒绝策略，使所有提交的线程可以选择不同的策略
     */
    private static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                MyThread thread=(MyThread) r;
                logger.info(thread.getName()+":进入拒绝策略,使用策略:"+thread.rejectEnum.getDesc());
                // 当线程进入到拒绝策略时,根据自定义线程中的拒绝策略枚举，选择不同的策略
                switch (thread.rejectEnum){
                    case ResetPutQueue://重入队列
                        if(!executor.isShutdown()){ executor.getQueue().put(r);}
                        break;
                    case AbortPolicy://抛出异常
                        throw new RejectedExecutionException();
                    case CallerRunsPolicy://使用主线程执行
                        if(!executor.isShutdown()){ r.run();}
                        break;
                    case DiscardOldestPolicy://新线程替换旧线程
                        if(!executor.isShutdown()){ executor.getQueue().poll();executor.execute(r); }
                        break;
                    case DiscardPolicy://什么也不干
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * The class NamedThreadFactory.
     * Description:线程工厂,线程命名以及指定线程是否宿主
     * @author: yangzefei
     * @since: 2019-08-14 19:32
     */
    private static class NamedThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final String namePrefix;
        private final boolean isDaemon;
        public NamedThreadFactory(String name) {
            this(name, false);
        }
        public NamedThreadFactory(String prefix, boolean daemon) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = prefix + "-" + POOL_NUMBER.getAndIncrement() + "-thread-";
            isDaemon = daemon;
        }

        public MyThread newThread(Runnable r,RejectedExecutionEnum rejectEnum){
            MyThread thread=(MyThread)newThread(r);
            thread.rejectEnum=rejectEnum;
            return thread;
        }

        @Override
        public Thread newThread(Runnable r) {
            String threadName = namePrefix + threadNumber.getAndIncrement();
            MyThread thread = new MyThread(group, r, threadName);
            thread.setDaemon(isDaemon);
            return thread;
        }
    }
    private static class MyThread extends Thread{
        public RejectedExecutionEnum rejectEnum;
        public MyThread(ThreadGroup group, Runnable target, String name) {
            super(group,target, name);
        }
        @Override
        public void run() {
            try{
                super.run();
            }catch (Exception e){
                logger.error("线程:{}发生异常。异常信息:",getName(),e);
            }

        }
    }

    /**
     * 拒绝策略枚举
     */
    public enum RejectedExecutionEnum{
        ResetPutQueue(1,"重入队列"),
        AbortPolicy(2,"抛出异常"),
        CallerRunsPolicy(3,"使用主线程执行"),
        DiscardOldestPolicy(4,"新线程替换旧线程"),
        DiscardPolicy(5,"什么也不干");
        private int code;
        private String desc;
        RejectedExecutionEnum(Integer code,String desc){
            this.code=code;
            this.desc=desc;
        }
        public Integer getCode(){
            return this.code;
        }
        public String getDesc(){
            return this.desc;
        }
    }

}
