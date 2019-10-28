package com.brandslink.cloud.finance.job.timertask;

import com.brandslink.cloud.finance.mapper.CustomerConfigMapper;
import com.brandslink.cloud.finance.mapper.QuoteConfigMapper;
import com.brandslink.cloud.finance.pojo.entity.QuoteConfig;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.EffectiveCstomerVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 定时生效
 *
 * @Author: zhangjinhua
 * @Date: 2019/8/29 15:32
 */
@Slf4j
@Component
@Configuration
@EnableScheduling
public class EffectiveTask {
    @Autowired
    CustomerConfigMapper quoteMapper;
    @Autowired
    QuoteConfigMapper quoteConfigMapper;

    /**
     * 定时生效客户报价
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void effectiveCstomerTask() {

        //获取到所有今天生效的客户报价
        List<EffectiveCstomerVo> effectiveCstomerList = quoteMapper.getNowEffective();
        //把之前的报价修改为失效
        quoteMapper.loseEfficacy(effectiveCstomerList);
        //当天的生效
        quoteMapper.effective();

    }

    /**
     * 定时生效配置
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void effectiveConfigTask() {

        //获取到所有今天生效的配置
        List<QuoteConfig> quoteConfigList = quoteConfigMapper.getNowEffective();
        //把之前的配置修改为失效
        quoteConfigMapper.loseEfficacy(quoteConfigList);
        //当天的生效
        quoteConfigMapper.effective();

    }
}
