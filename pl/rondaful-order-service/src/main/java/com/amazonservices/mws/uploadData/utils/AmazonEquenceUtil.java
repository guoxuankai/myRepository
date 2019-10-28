package com.amazonservices.mws.uploadData.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自增id，目前设计在是amazon刊登，messageid上使用
 * @author ouxiangfeng
 *
 */
public class AmazonEquenceUtil {
	
	// 简单定义一个原子对象操作
	private volatile static AtomicInteger atomicInteger = new AtomicInteger(0);
	
	static Lock lock = new  ReentrantLock();
	public static int VALUE()
	{
		try
		{
			lock.lock();
			return atomicInteger.incrementAndGet();
		}finally {
			lock.unlock();
		}
	}
	
	public static void main(String[] args) {
		for(;;)
		{
			System.out.println(VALUE());
		}
	}
}
