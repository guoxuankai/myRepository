package com.rondaful.cloud.order.RateQuartz;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;


@Configuration
public class RateQuartzConfig {


    private final RateJobFactory rateJobFactory;

    @Autowired
    public RateQuartzConfig(RateJobFactory rateJobFactory) {
        this.rateJobFactory = rateJobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(rateJobFactory);
        // 用于quartz集群,QuartzScheduler 启动时更新己存在的Job
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setStartupDelay(1);
        return schedulerFactoryBean;
    }




    @Bean("rateScheduler")
    public Scheduler scheduler() {
        return schedulerFactoryBean().getScheduler();
    }

}

