package com.rondaful.cloud.seller.common.task;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.seller.common.mws.intface.RequestReport;
import com.rondaful.cloud.seller.constants.PublishRequestReport;
import com.rondaful.cloud.seller.entity.AmazonPublishReport;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.enums.ReportTypeEnum;
import com.rondaful.cloud.seller.mapper.EmpowerMapper;
import com.rondaful.cloud.seller.service.AmazonPublishReportService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 创建报告请求，并将请求提交至亚马逊MWS
 */
@Component
public class RequestReportSynAsinTask implements Runnable {


    private static final Logger logger = LoggerFactory.getLogger(RequestReportSynAsinTask.class);

    private final String upListBeforeKey = "UP_LIST_BEFORE_KYE";

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedissLockUtil redissLockUtil;

    @Autowired
    private EmpowerMapper empowerMapper;


    @Autowired
    private RequestReport requestReport;

   @Autowired
   private AmazonPublishReportService amazonPublishReportService;



    /**
     * 向redis上传需要生成报告的授权列表（本列表依据商城排序，将同一个卖家划分在了不同的时间段进行处理，保证卖家不收限制）
     * 所以只是那些不支持同一卖家不同商城一次提交生成报告的才建议使用本方法生产列表，否则建议使用卖家分组创建列表
     *
     * @param reportType 报告类型
     * @return 是否操作成功
     */
    public boolean upEmpowerListToRedis(ReportTypeEnum reportType) {                                         // todo 将同站点做成集合，之后进行批处理
        String lockKey = upListBeforeKey + reportType.getReportTyp();
        logger.debug("amazon_task_run......upEmpowerListToRedis....." + reportType.getReportTyp());
        if(!redissLockUtil.tryLock(lockKey, 10, 60 * 10)) //等待10秒，10分放开锁
        {
            logger.debug(lockKey + " 其它服务正在执行。locking....");
            return true;
        }
        try {
            String toKey = upListBeforeKey + reportType.getReportTyp() + "setList"; //+ this.getTomorrowDay();
            JSONObject listJsonMessageFromRight = redisUtils.getListJsonMessageFromRight(toKey);
            if(listJsonMessageFromRight != null){
                logger.error( " key为： " + toKey + " 的授权redis列表没有处理完" );
                redisUtils.setListJsonMessageFromLift(toKey,listJsonMessageFromRight);
            }else {
                List<AmazonPublishReport> amazonPublishReports = amazonPublishReportService.selectNotFinishMessage(reportType);
                if(amazonPublishReports == null || amazonPublishReports.size() == 0){
                    List<Empower> amazonEmpowerOrderByWebName = empowerMapper.getAmazonEmpowerOrderByWebName();
                    String str;
                    JSONObject jsonObject;
                    for (Empower empower:amazonEmpowerOrderByWebName){
                        str = JSONObject.toJSONString(empower);
                        jsonObject = JSONObject.parseObject(str);
                        redisUtils.setListJsonMessageFromLift(toKey,jsonObject);
                    }
                }else {
                    logger.error( " key为： " + toKey + " 的授权数据库没有处理完" );
                }
            }
           // String nowKey = upListBeforeKey + reportType.getReportTyp() + DateUtils.formatDate(new Date(),DateUtils.FORMAT_3);
            return true;
        } catch (Exception e) {
            logger.error("向redis上传需要生成报告的授权列表", e);
            return false;
        }finally {
            logger.debug("释放同步锁. " + lockKey);
            redissLockUtil.unlock(lockKey); // 解放锁
        }
    }


    private String getTomorrowDay(){
        Date tomorrowDate = DateUtils.getBeforeDateByDay(new Date(), 1);
        return DateUtils.formatDate(tomorrowDate,DateUtils.FORMAT_3);
    }



    /**
     * 创建报告
     * @param reportType 创建报告的类型
     *
     */
    public void process(ReportTypeEnum reportType ){
        logger.info("开始执行生成亚马逊报告任务，任务类型：{}",reportType.getReportTyp());

        String nowKey = upListBeforeKey + reportType.getReportTyp() + "setList";
        JSONObject nowobj = redisUtils.getListJsonMessageFromRight(nowKey);
        if(nowobj == null){
            upEmpowerListToRedis(reportType);
            return;
        }

        String token = nowobj.getString("token");
        String thirdPartyName = nowobj.getString("thirdPartyName");
        String webName = nowobj.getString("webName");
        if(!this.checkEmpower(token,thirdPartyName,webName,nowobj.getString("empowerId")))
            return;

        String lockKey = upListBeforeKey + reportType.getReportTyp() + PublishRequestReport.reportProgress.RequestReport.getTheInterface()+"_" +thirdPartyName;
        if(!redissLockUtil.tryLock(lockKey, 10, 60 * 10)) //等待10秒，10分放开锁
        {
            logger.debug(lockKey + " 其它服务正在执行。locking....");
            redisUtils.setListJsonMessageFromLift(nowKey,nowobj);
            return ;
        }
        try {
            Date date = DateUtils.getBeforeDateByMinit(new Date(),30);
            AmazonRequestReportResult invoke = requestReport.invoke(webName, thirdPartyName, reportType, token);
            String resultId = invoke.getResultId();
            if(StringUtils.isNotBlank(resultId)){
                AmazonPublishReport amazonPublishReport = new AmazonPublishReport(){{
                    setReportStatus(PublishRequestReport.reportStatus.PUBLISH.getStatus());
                    setMarketplaceId(webName);
                    setMerchantId(thirdPartyName);
                    setMwsauthToken(token);
                    setReportRequestId(resultId);
                    setBeginTime(DateUtils.dateToString(date,DateUtils.FORMAT_2));
                    setReportType(reportType.getReportTyp());
                }};
                amazonPublishReportService.insert(amazonPublishReport);
            }else {
                AmazonPublishReport amazonPublishReport = new AmazonPublishReport(){{
                    setReportStatus(PublishRequestReport.reportStatus.REPORT_ERROR.getStatus());
                    setMarketplaceId(webName);
                    setMerchantId(thirdPartyName);
                    setMwsauthToken(token);
                    setReportType(reportType.getReportTyp());
                    setBeginTime(DateUtils.dateToString(date,DateUtils.FORMAT_2));
                    setErrorMessage( PublishRequestReport.reportProgress.RequestReport.getTheInterface() +"_" +invoke.getErrorCode() + "---" + invoke.getResultDescription() );
                }};
                amazonPublishReportService.insert(amazonPublishReport);
            }
        }catch (Exception e){
            logger.error("创建报告异常",e);
        }finally {
            logger.debug("释放同步锁.");
            redissLockUtil.unlock(lockKey); // 解放锁
        }
    }

    private boolean checkEmpower(String token,String thirdPartyName,String webName,String id){
        boolean flag = true;
        if(StringUtils.isBlank(token)){
            logger.warn("亚马逊授权ID为：{}token为空",id);
            flag = false;
        }

        if(StringUtils.isBlank(thirdPartyName)){
            logger.warn("亚马逊授权ID为：{}thirdPartyName为空",id);
            flag = false;
        }

        if(StringUtils.isBlank(webName)){
            logger.warn("亚马逊授权ID为：{}webName为空",id);
            flag = false;
        }
        return flag;
    }



    @Override
    public void run() {
        process(ReportTypeEnum._GET_MERCHANT_LISTINGS_DATA_);
    }
}
