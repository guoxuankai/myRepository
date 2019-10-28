package com.rondaful.cloud.user.controller.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockUtils {

    private volatile int read;
    private volatile int write;

    //读写锁构造
    public ReadWriteLockUtils(){
        this.read = 0;
        this.write = 0;
    }

    //获取读锁



}

class ReadWriteBean{

    //读写锁对象
//    ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     *    读写锁实现
     *        读写锁的缓存机制
     */
    // 缓存的map
    private Map<String, Object> map = new HashMap<String, Object>();
    // 读写锁对象
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private String data="1";

    /**
     * 进行读操作
     * 可以多个读线程同时进入，写线程不能执行
     */
    public String getReadWriteLock(String tt) {
        //获取读锁，并加锁
        Lock readLock = readWriteLock.readLock();
        readLock.lock();
        try {
            System.out.println("线程名称："+Thread.currentThread().getName() + " be ready to read data!");
//            Thread.sleep((long) (Math.random() * 3000));
            Thread.sleep(3000);

//            this.data =tt;
            this.data = "tt";
            System.out.println(Thread.currentThread().getName() + "------->>>>have read data :"+data );
            return this.data;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //!!!!!!注意：锁的释放一定要在trycatch的finally中，因为如果前面程序出现异常，锁就不能释放了
            //释放读锁
            readLock.unlock();
        }

        return null;
    }


}
