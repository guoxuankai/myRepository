package com.rondaful.cloud.order.base;

import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.seller.Empower;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 同步订单的主要流程
 *
 * @author Blade
 * @date 2019-07-05 15:48:06
 **/
public abstract class AbstractSyncOrder {

    public Logger logger = LoggerFactory.getLogger(this.getClass());

    // 是否继续执行
    public boolean goOn = false;

    // 类名，主要是用来打印日志的
    public String className;

    public List<SysOrder> preConvertOrder;

    // 1、获取授权信息
    public abstract void getAuthorizedEmpower();

    // 2、抓取平台订单
    public abstract void syncPlatformOrder();

    // 3、平台订单入库
    public abstract void persistencePlatformOrder();

    // 4、平台订单转换成系统订单
    private void covertPlatformOrderToSystemOrder() {
        logger.info("{} 开始转换订单", className);
        this.goOn = true;
        logger.info("{} 转换订单的后续步骤标识是：{}", className, this.goOn);
    }

    // 5、系统订单入库
    private void persistenceSysOrder() {
        logger.info("{} 开始持久化已转换的系统订单", className);
        this.goOn = true;
        logger.info("{} 持久化系统订单的后续步骤标识是：{}", className, this.goOn);
    }

    public void syncOrder() {
        long startTime = System.currentTimeMillis();
        logger.info("{} 开始抓单", className);

        getAuthorizedEmpower();
        if (goOn) {
            syncPlatformOrder();
        }

        if (goOn) {
            persistencePlatformOrder();
        }

        if (goOn) {
            covertPlatformOrderToSystemOrder();
        }
        if (goOn) {
            persistenceSysOrder();
        }
        logger.info("{} 结束抓单", className);
        long endTime = System.currentTimeMillis();
        logger.info("{} 抓单共耗时：{}ms", className, (endTime - startTime));
    }
}
