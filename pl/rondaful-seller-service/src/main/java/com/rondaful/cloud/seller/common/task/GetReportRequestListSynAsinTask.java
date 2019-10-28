package com.rondaful.cloud.seller.common.task;

import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.seller.common.mws.intface.GetReportRequestList;
import com.rondaful.cloud.seller.constants.PublishRequestReport;
import com.rondaful.cloud.seller.entity.AmazonPublishReport;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdDeveloper;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.enums.ReportTypeEnum;
import com.rondaful.cloud.seller.service.AmazonPublishReportService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取报告状态和报告ID
 */
@Component
public class GetReportRequestListSynAsinTask implements Runnable {


    private static final Logger logger = LoggerFactory.getLogger(GetReportRequestListSynAsinTask.class);

    @Autowired
    private AmazonPublishReportService amazonPublishReportService;

    @Autowired
    private GetReportRequestList getReportRequestList;

    @Autowired
    private RedissLockUtil redissLockUtil;

    private final String upListBeforeKey = "AmazonReportKey_";


    @Override
    public void run() {
        this.process(ReportTypeEnum._GET_MERCHANT_LISTINGS_DATA_);
    }

    /**
     * 获取报告状态和报告ID
     *
     * @param reportType 报告类型
     */
    public void process(ReportTypeEnum reportType) {

        logger.info("开始执行获取报告状态和报告ID任务，任务类型：{}", reportType.getReportTyp());
        AmazonPublishReport param1 = new AmazonPublishReport();
        param1.setReportType(reportType.getReportTyp());
        param1.setReportStatus(PublishRequestReport.reportStatus.PUBLISH.getStatus());
        List<AmazonPublishReport> all = amazonPublishReportService.findAll(param1);
        if (all == null || all.size() == 0)
            return;
        AmazonPublishReport param2 = new AmazonPublishReport();
        param2.setReportType(reportType.getReportTyp());
        param2.setReportStatus(PublishRequestReport.reportStatus.PUBLISH.getStatus());
        param2.setMerchantId(all.get(0).getMerchantId());
        all = amazonPublishReportService.findAll(param2);

        String marketplaceId = all.get(0).getMarketplaceId();
        String merchant = all.get(0).getMerchantId();
        String token = all.get(0).getMwsauthToken();

        MarketplaceId markModel = MarketplaceIdList.resourceMarketplaceForKeyId().get(marketplaceId);
        String uri = markModel.getUri();
        MarketplaceIdDeveloper developer = markModel.getMarketplaceIdDeveloper();
        if (developer == null)
            return;


        String lockKey = upListBeforeKey + reportType.getReportTyp() + PublishRequestReport.reportProgress.GetReportRequestList.getTheInterface() + "_" + merchant;
        if (!redissLockUtil.tryLock(lockKey, 10, 60 * 10)) //等待10秒，10分放开锁
        {
            logger.debug(lockKey + " 其它服务正在执行。locking....");
            return;
        }

        try {
            ArrayList<String> reportRequestIds = new ArrayList<>();          //todo 这里暂时不限制数量
            List<AmazonPublishReport> rightReports = new ArrayList<>();
            for (AmazonPublishReport report : all) {
                if (report.getMarketplaceId().equalsIgnoreCase(marketplaceId) ||
                        (uri.equalsIgnoreCase(MarketplaceIdList.resourceMarketplaceForKeyId().get(report.getMarketplaceId()).getUri()) &&
                                developer.getName().equalsIgnoreCase(MarketplaceIdList.resourceMarketplaceForKeyId().get(report.getMarketplaceId()).getMarketplaceIdDeveloper().getName()))) {
                    reportRequestIds.add(report.getReportRequestId());
                    rightReports.add(report);
                }
            }

            //String marketplaceId, String merchant, ReportTypeEnum reportTypeEnum, String token, List<String> reportRequestIds
            List<AmazonRequestReportResult> invokes = getReportRequestList.invoke(marketplaceId, merchant, reportType, token, reportRequestIds);

            List<AmazonPublishReport> resultReport = new ArrayList<>();

            if (invokes.size() == 1 && StringUtils.isNotBlank(invokes.get(0).getErrorCode())) {  //异常
                for (AmazonPublishReport port : rightReports) {
                    port.setReportStatus(PublishRequestReport.reportStatus.REPORT_ERROR.getStatus());
                    port.setErrorMessage(PublishRequestReport.reportProgress.GetReportRequestList.getTheInterface() + "_" + invokes.get(0).getErrorCode() + "---" + invokes.get(0).getResultDescription());
                    resultReport.add(port);
                }
            } else {  //无异常
                for (AmazonRequestReportResult result : invokes) {
                    for (AmazonPublishReport port : rightReports) {
                        if (port.getReportRequestId().equals(result.getSourceId())) {
                            if (StringUtils.isNotBlank(result.getReportProcessingStatus()) && result.getReportProcessingStatus().equalsIgnoreCase("_DONE_")) {
                                port.setReportStatus(PublishRequestReport.reportStatus.AMAZON_DONE.getStatus());
                            }
                            if (StringUtils.isNotBlank(result.getResultId())) {
                                port.setReportStatus(PublishRequestReport.reportStatus.REPORT_ID.getStatus());
                                port.setGeneratedReportId(result.getResultId());
                                //port.setBeginTime(result.getBeginTime());
                                //port.setBeginTime(DateUtils.dateToString(port.getCreateTime(),DateUtils.FORMAT_2));
                                port.setEndTime(result.getEndTime());
                            }
                            resultReport.add(port);
                        }
                    }
                }
            }
            for (AmazonPublishReport report : resultReport) {
                amazonPublishReportService.updateByPrimaryKeySelective(report);
            }
        } catch (Exception e) {
            logger.error("获取报告状态异常", e);
        } finally {
            logger.debug("释放同步锁.");
            redissLockUtil.unlock(lockKey); // 解放锁
        }
    }


}
