package com.rondaful.cloud.order.RateQuartz;

import com.rondaful.cloud.order.utils.RateUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RateJob implements Job {

    private final Logger logger = LoggerFactory.getLogger(RateJob.class);

    @Autowired
    private  RateUtil rateUtil;





    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            logger.info("定时任务开始执行同步汇率");
            //JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            rateUtil.initRateMessage();
            logger.info("定时任务成功执行同步汇率");
        }catch (Exception e){
            logger.error("定时任务执行同步汇率异常",e);
        }

    }
}
