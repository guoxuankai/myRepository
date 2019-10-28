package com.rondaful.cloud.order.quartz;

import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.order.config.PropertyUtil;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.seller.EmpowerMapper;
import com.rondaful.cloud.order.utils.ApplicationContextProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AutoSyncEbayOrdersJob implements BaseJob {
    private EmpowerMapper empowerMapper;
    private SyncTask syncTask;
    private Integer autoSyncEbayThreadNum;//线程数量

    private static Logger _log = LoggerFactory.getLogger(AutoSyncEbayOrdersJob.class);

    public AutoSyncEbayOrdersJob() {
        syncTask = (SyncTask) ApplicationContextProvider.getBean("syncTask");
        empowerMapper = (EmpowerMapper) ApplicationContextProvider.getBean("empowerMapper");
        autoSyncEbayThreadNum = Integer.valueOf(PropertyUtil.getProperty("autoSyncEbayThreadNum"));
    }

    public void execute(JobExecutionContext jobExecutionContext) {
        _log.error("_______________本次同步ebay订单Job开始_______________");
        try {
            List<Empower> empowers = empowerMapper.selectEbayAvailableToken();
            if (CollectionUtils.isEmpty(empowers) || empowers.size() == 0)
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询店铺授权表获取可用ebay平台token异常。。。");

            List<Empower>[] empowerListPerThread = this.distributeTasks(empowers, autoSyncEbayThreadNum);

            this.startAllThread(empowerListPerThread);
        } catch (Exception e) {
            _log.error("______________执行SyncTask同步ebay订单出错__________{}__________", e);
        }
        _log.error("_____________本次请求下来的所有订单处理完毕___________");
    }

    /**
     * 把任务分配给每个线程，先平均分配，剩于的依次附加给前面的线程
     */
    public List<Empower>[] distributeTasks(List<Empower> empowerList, int threadCount) {
        int minEmpowerCount = empowerList.size() / threadCount;
        int remainEmpowerCount = empowerList.size() % threadCount;
        int actualThreadCount = minEmpowerCount > 0 ? threadCount : remainEmpowerCount;
        List<Empower>[] empowerListPerThread = new List[actualThreadCount];
        int empowerIndex = 0;
        int remainIndces = remainEmpowerCount;
        for (int i = 0; i < empowerListPerThread.length; i++) {
            empowerListPerThread[i] = new ArrayList();
            if (minEmpowerCount > 0) {
                for (int j = empowerIndex; j < minEmpowerCount + empowerIndex; j++) {
                    empowerListPerThread[i].add(empowerList.get(j));
                }
                empowerIndex += minEmpowerCount;
            }
            if (remainIndces > 0) {
                empowerListPerThread[i].add(empowerList.get(empowerIndex++));
                remainIndces--;
            }
        }
        return empowerListPerThread;
    }

    public void startAllThread(List<Empower>[] empowerListPerThread) {
        for (int i = 0; i < empowerListPerThread.length; i++) {
            Thread workThread = new WorkThread(empowerListPerThread[i], i);
            List<String> storeList = empowerListPerThread[i].stream().map(x -> x.getAccount()).collect(Collectors.toList());
            _log.error("______________第{}个线程分配的任务为：{}______________", i, ArrayUtils.toString(storeList));
            workThread.start();
        }
//        try {
//            Thread.sleep(60 * 1000);//(毫秒)此处需要将主线程设置时间长一点，以保证子线程任务执行完。
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 自定义的工作线程，持有分派给它执行的Token列表
     */
    class WorkThread extends Thread {
        private List<Empower> empowerList;
        private int threadId;

        /**
         * 构造工作线程，为其指派任务列表，及命名线程 ID
         *
         * @param empowerList 欲执行的任务列表
         * @param threadId    线程 ID
         */
        public WorkThread(List<Empower> empowerList, int threadId) {
            this.empowerList = empowerList;
            this.threadId = threadId;
        }

        /**
         * 执行被指派的所有任务
         */
        public void run() {
            for (Empower empower : empowerList) {
                try {
                    syncTask.execute(empower);
                } catch (ParseException e) {
                    _log.error("______________ebay同步订单出错______________", e);
                }
            }
        }
    }

    public Integer getAutoSyncEbayThreadNum() {
        return autoSyncEbayThreadNum;
    }
}
