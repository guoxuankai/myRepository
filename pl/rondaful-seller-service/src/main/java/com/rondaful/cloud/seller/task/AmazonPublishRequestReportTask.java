package com.rondaful.cloud.seller.task;


import com.rondaful.cloud.seller.common.task.RequestReportSynAsinTask;
import com.rondaful.cloud.seller.enums.ReportTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AmazonPublishRequestReportTask {
    private static Logger logger = LoggerFactory.getLogger(AmazonPublishRequestReportTask.class);

    @Autowired
    private RequestReportSynAsinTask requestReport;

    /**
     * 查询授权数据，定时将授权数据redis作为生成在售商品报告的依据
     */
    //@Scheduled(cron = "0 0-5 1 * * ?")   //每天凌晨1点到1点过2分每一分钟执行一次
    public void setGetMerchantListingsData(){
        logger.info(" 查询授权数据，定时将授权数据redis作为生成在售商品报告的依据开始执行");
        try {
            boolean b = requestReport.upEmpowerListToRedis(ReportTypeEnum._GET_MERCHANT_LISTINGS_DATA_);
            if(!b)
                logger.warn(" 查询授权数据，定时将授权数据redis作为生成在售商品报告的依据失败");
        }catch (Exception e){
            logger.error(" 查询授权数据，定时将授权数据redis作为生成在售商品报告的依据异常",e);
        }

    }


}
