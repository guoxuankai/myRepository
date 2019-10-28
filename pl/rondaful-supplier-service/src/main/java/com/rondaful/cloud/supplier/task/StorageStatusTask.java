package com.rondaful.cloud.supplier.task;

import com.rondaful.cloud.supplier.service.IInventoryService;
import com.rondaful.cloud.supplier.service.IStorageRecordService;
import com.rondaful.cloud.supplier.service.IWarehouseBasicsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * @Author: xqq
 * @Date: 2019/7/10
 * @Description: 入库单相关定时任务
 */
@Component
public class StorageStatusTask {
    private final Logger logger = LoggerFactory.getLogger(StorageStatusTask.class);
    @Autowired
    private IStorageRecordService storageRecordService;
    @Autowired
    private IInventoryService inventoryService;
    @Autowired
    private IWarehouseBasicsService basicsService;

    /**
     * 入库单状态同步
     */
    @Scheduled(cron = "0 0/15 * * * *")
    public void syncStatus(){
        this.storageRecordService.syncStatus();
    }

    /**
     * 库存同步
     */
    @Scheduled(cron = "0 10 1 * * *")
    public void init(){
        this.inventoryService.init();
    }
}
