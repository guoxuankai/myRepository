package com.rondaful.cloud.order.RateQuartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RateQuartzRunner implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(RateQuartzRunner.class);

    private final RateQuartzService rateQuartzService;

    @Autowired
    public RateQuartzRunner(RateQuartzService rateQuartzService) {
        this.rateQuartzService = rateQuartzService;
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        logger.info("开机启动汇率垃取定时任务调度器");
        try {
            rateQuartzService.startJob();
            logger.info("开机启动汇率垃取定时任务调度器成功");
        }catch (Exception e){
            logger.error("开机启动汇率垃取定时任务调度器异常",e);
        }
    }
}
