package com.rondaful.cloud.order.RateQuartz;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class RateQuartzService {

    private final Scheduler rateScheduler;

    @Autowired
    public RateQuartzService(@Qualifier("rateScheduler") Scheduler rateScheduler) {
        this.rateScheduler = rateScheduler;
    }


    public void startJob() throws SchedulerException {
        startRateJob(rateScheduler);
        rateScheduler.start();
    }


    public void startRateJob(Scheduler rateScheduler) throws SchedulerException {
        // 通过JobBuilder构建JobDetail实例，JobDetail规定只能是实现Job接口的实例
        // JobDetail 是具体Job实例
        JobDetail jobDetail = JobBuilder.newJob(RateJob.class).withIdentity("rateJobName", "rateGroup").build();
        // 基于表达式构建触发器
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 0/1 * * ? *");
        // CronTrigger表达式触发器 继承于Trigger
        // TriggerBuilder 用于构建触发器实例
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("rateJob", "rateGroup")
                .withSchedule(cronScheduleBuilder).build();
        rateScheduler.scheduleJob(jobDetail, cronTrigger);
    }


}
