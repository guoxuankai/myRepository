package com.rondaful.cloud.order.thread;


import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.utils.SpringContextUtil;
import com.rondaful.cloud.order.entity.ExcelOrder;
import com.rondaful.cloud.order.entity.ExcelOrderStatisticsDTO;
import com.rondaful.cloud.order.service.IImportExcelOrderInterface;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ImportOrderThread implements Runnable {
    private List<ExcelOrder> list;
    private CountDownLatch begin;
    private CountDownLatch end;
    private UserDTO userDTO;
    private AtomicInteger toutalNum;
    private ConcurrentHashMap<String, ExcelOrderStatisticsDTO> concurrentHashMap;


    //创建个构造函数初始化 list,和其他用到的参数
    public ImportOrderThread(List<ExcelOrder> list, CountDownLatch begin, CountDownLatch end, UserDTO userDTO, AtomicInteger toutalNum, ConcurrentHashMap<String, ExcelOrderStatisticsDTO> concurrentHashMap) {
        this.list = list;
        this.begin = begin;
        this.end = end;
        this.userDTO = userDTO;
        this.toutalNum = toutalNum;
        this.concurrentHashMap = concurrentHashMap;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < list.size(); i++) {
                IImportExcelOrderInterface service = SpringContextUtil.getBean(IImportExcelOrderInterface.class);
                service.fillOrderInfo(list.get(i), userDTO, toutalNum, concurrentHashMap);
                //执行完让线程直接进入等待
                begin.await();
            }
        } catch (InterruptedException e) {
                e.printStackTrace();
        } finally {
                //当一个线程执行完 了计数要减一不然这个线程会被一直挂起
                end.countDown();
        }
    }
}
