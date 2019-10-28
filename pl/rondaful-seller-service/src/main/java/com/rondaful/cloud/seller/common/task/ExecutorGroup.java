package com.rondaful.cloud.seller.common.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorGroup {
	
	
	 private ExecutorGroup(){}
	 private static class ClassgetInstance{
	    private static final ExecutorGroup instance=new ExecutorGroup();
	 }
	 public static ExecutorGroup getInstance(){
	        return ClassgetInstance.instance;
	    }
	ExecutorService productExecutorService = Executors.newFixedThreadPool(20);
	ExecutorService relationshipExecutorService = Executors.newFixedThreadPool(20);
	ExecutorService inventoryExecutorService = Executors.newFixedThreadPool(20);
	ExecutorService pricingExecutorService = Executors.newFixedThreadPool(20);
	ExecutorService imageExecutorService = Executors.newFixedThreadPool(20);

	public void shutdown()
	{
		productExecutorService.shutdown();
		relationshipExecutorService.shutdown();
		inventoryExecutorService.shutdown();
		pricingExecutorService.shutdown();
		imageExecutorService.shutdown();
	}
}
