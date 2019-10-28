package com.amazonservices.mws.uploadData.common.task;/*
package com.rondaful.cloud.seller.common.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.rondaful.cloud.seller.entity.Amazon.AmazonRequestProduct;

public class AmaznoExecutors {
	// 20个线程，最大40个。
	ExecutorService executor = Executors.newFixedThreadPool(20);
	
	private static AmaznoExecutors amaznoExecutors;
	
	 public static AmaznoExecutors getInstance()
	 {
		 if (amaznoExecutors == null)
		 {
			 synchronized(AmaznoExecutors.class)
			 {
				 if (amaznoExecutors == null)
					 amaznoExecutors = new AmaznoExecutors(); 
			 }
		 }
		 return amaznoExecutors;
	 }
	
	*/
/**
	 * 增加任务
	 * @param command
	 *//*

	public void addTask(Runnable command)
	{
		executor.execute(command);
	}
	
	public <T> Future<T>  addTask(Callable command)
	{
		return executor.submit(command);
	}
	public void shutdown()
	{
		executor.shutdown();
	}
	
	public static void main(String[] args) {
		AmaznoExecutors.getInstance().addTask(new ProcessXmlTask(new AmazonRequestProduct<>()));
		//AmaznoExecutors.getInstance().shutdown();
		AmaznoExecutors.getInstance().addTask(new ProcessXmlTask(new AmazonRequestProduct<>()));
		//AmaznoExecutors.getInstance().shutdown();
		AmaznoExecutors.getInstance().addTask(new ProcessXmlTask(new AmazonRequestProduct<>()));
		AmaznoExecutors.getInstance().shutdown();
	}
	
}
*/
