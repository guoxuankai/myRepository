package com.rondaful.cloud.order.thread;

import com.rondaful.cloud.order.quartz.SyncTask;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.utils.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

/**
 * @author Blade
 * @date 2019-07-25 20:29:23
 **/
public class AutoSyncEbayOrderThread extends Thread {
    private static Logger _log = LoggerFactory.getLogger(AutoSyncEbayOrderThread.class);

    private SyncTask syncTask;

    private Empower empower;

    /**
     * 构造工作线程，为其指派任务列表，及命名线程 ID
     *
     * @param empower 执行ebay抓单的账号
     */
    public AutoSyncEbayOrderThread(Empower empower) {
        this.empower = empower;
        syncTask = (SyncTask) ApplicationContextProvider.getBean("syncTask");
    }

    /**
     * 执行被指派的所有任务
     */
    public void run() {
        try {
            syncTask.execute(empower);
        } catch (Throwable t) {
            _log.error("品连账号:{} , ebay店铺:{} 同步ebay订单出错", empower.getPinlianaccount(), empower.getAccount(), t);
        }
    }
}
