package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amazonservices.mws.uploadData.common.mws.model.FeedSubmissionInfo;
import com.amazonservices.mws.uploadData.common.mws.model.GetFeedSubmissionListByNextTokenResponse;
import com.amazonservices.mws.uploadData.common.mws.model.GetFeedSubmissionListResponse;
import com.amazonservices.mws.uploadData.common.mws.samples.GetFeedSubmissionListByNextTokenSample;
import com.amazonservices.mws.uploadData.common.mws.samples.GetFeedSubmissionListSample;
import com.amazonservices.mws.uploadData.common.mws.samples.GetFeedSubmissionResultSample;
import com.google.common.collect.Lists;
import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.order.entity.Amazon.AmazonEmpower;
import com.rondaful.cloud.order.entity.Amazon.AmazonUploadData;
import com.rondaful.cloud.order.entity.Time;
import com.rondaful.cloud.order.mapper.AmazonEmpowerMapper;
import com.rondaful.cloud.order.mapper.AmazonOrderDetailMapper;
import com.rondaful.cloud.order.mapper.AmazonUploadDataMapper;
import com.rondaful.cloud.order.remote.RemoteSellerService;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.service.IAmazonEmpowerService;
import com.rondaful.cloud.order.service.IAmazonUploadDataService;
import com.rondaful.cloud.order.utils.OrderUtils;
import com.rondaful.cloud.seller.generated.AmazonEnvelope;
import com.rondaful.cloud.seller.generated.ProcessingReport;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2019-01-19 16:33
 * 包名: com.rondaful.cloud.order.service.impl
 * 描述:
 */
@Service
public class AmazonUploadDataServiceImpl extends BaseServiceImpl<AmazonUploadData> implements IAmazonUploadDataService {
    @Autowired
    private IAmazonEmpowerService amazonEmpowerService;

    @Autowired
    private AmazonEmpowerMapper amazonEmpowerMapper;
    @Autowired
    private AmazonUploadDataMapper amazonUploadDataMapper;
    @Autowired
    private AmazonOrderDetailMapper amazonOrderDetailMapper;
    @Autowired
    private RemoteSellerService remoteSellerService;
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(AmazonUploadDataServiceImpl.class);
    /*
    * 定时任务每两分钟查询一次结果并标记回品连
    * */
    @Override
    public void queryAmazonUploadDataAndDeal() {
//        //1.查出授权信息对象集合        优化：查出亚马逊发货信息表中发货状态为0的卖家授权信息   就不用查出全部授权信息来循环
        //远程调用查询有效的授权信息集合
        String allRemote = remoteSellerService.findAllRemote(2,1);
        //解析远程调用授权接口数据,返回亚马逊授权信息集合
        List<AmazonEmpower> list = getAmazonEmpowers(allRemote);
        if (list == null) {
            logger.error("异常：异常！调用卖家服务获得的亚马逊有效授权信息集合为空");
            return;
        }
        //开始循环查询上传数据结果
        //将集合分组  条件：SellerID
        Map<String, List<AmazonEmpower>> collect = Lists.reverse(list).stream().collect(Collectors.groupingBy(AmazonEmpower::getAmazonSellerId));//根据卖家ID分组
        for (String key : collect.keySet()) {
            List<AmazonEmpower> amazonEmpowerList = collect.get(key);  //得到一个卖家不同站点的授权信息集合
                //重置频率限制标记变量
                getFeedSubmissionListCount=0;
                count = 0;
                resultCount=0;
            for (AmazonEmpower amazonEmpower : amazonEmpowerList) {
                String feedType = "_POST_ORDER_FULFILLMENT_DATA_";
                String marketplaceId = amazonEmpower.getMarketplaceId();
                String amazonSellerId = amazonEmpower.getAmazonSellerId();
                //找出卖家在该站点下的授权token   TODO 如果授权过期，自己的授权表没改过来，就要调卖家服务查！  目前都是从自己的授权表查数据。
                List<String> list1 = amazonEmpowerService.selectMWSTokenBySellerId(amazonSellerId, marketplaceId);
                if (list1 == null || list1.size() == 0) {
                    continue;
                }
                String mwsToken = list1.get(0);//亚马逊授权token
                String maxTime = amazonUploadDataMapper.selectSubMaxTimeBySiteIdAndSellerId(marketplaceId, amazonSellerId);
                String minTime = amazonUploadDataMapper.selectSubMinTimeBySiteIdAndSellerId(marketplaceId, amazonSellerId);
                if (StringUtils.isBlank(maxTime) || StringUtils.isBlank(minTime)) {
                    continue;
                }
                try {
                    getResultAndUpdateStatus(feedType, marketplaceId, amazonSellerId, mwsToken, maxTime, minTime);
                } catch (Exception e) {
                    logger.error("异常：获取亚马逊上传数据结果并标记回品连发生异常！下次定时任务继续获取结果继续标记。品连账号："+amazonEmpower.getPlAccount()
                            +",卖家店铺账号："+amazonEmpower.getAccount()+",卖家平台账号："+amazonEmpower.getAmazonSellerId()+",站点："+amazonEmpower.getMarketplaceId(),e);
                }
            }
        }
    }
    private int getFeedSubmissionListCount=0;
    private int count = 0;
    private int resultCount=0;
    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void getResultAndUpdateStatus(String feedType, String marketplaceId, String amazonSellerId, String mwsToken, String maxTime, String minTime)
            throws ParseException, InterruptedException, FileNotFoundException {
        Time max = OrderUtils.getLastUpdateTimeDetail(maxTime, "Greenwich");  //时间转格林尼治时间
        Time min = OrderUtils.getLastUpdateTimeDetail(minTime, "Greenwich");
        if (getFeedSubmissionListCount >= 10) {
            Thread.sleep(50000);
        }
        GetFeedSubmissionListResponse getFeedSubmissionListResponse = GetFeedSubmissionListSample.GetFeedSubmissionList(marketplaceId, mwsToken, amazonSellerId, feedType, max, min);
        if (getFeedSubmissionListResponse == null) {
            return;
        }
        getFeedSubmissionListCount++;
        for (FeedSubmissionInfo feedSubmissionInfo : getFeedSubmissionListResponse.getGetFeedSubmissionListResult().getFeedSubmissionInfoList()) {
            String feedProcessingStatus = feedSubmissionInfo.getFeedProcessingStatus();
            String feedSubmissionId = feedSubmissionInfo.getFeedSubmissionId();
            amazonUploadDataMapper.updateProcessStatus(feedSubmissionId,feedProcessingStatus);//根据feedSessionID更改上传数据的处理状态
        }
        String nextToken = getFeedSubmissionListResponse.getGetFeedSubmissionListResult().getNextToken();
        while (nextToken != null) {
            if (count >= 30) {
                Thread.sleep(2000);
            }
            GetFeedSubmissionListByNextTokenResponse getFeedSubmissionListByNextTokenResponse = GetFeedSubmissionListByNextTokenSample.GetFeedSubmissionListByNextToken(nextToken,marketplaceId, mwsToken, amazonSellerId);
            for (FeedSubmissionInfo feedSubmissionInfo : getFeedSubmissionListResponse.getGetFeedSubmissionListResult().getFeedSubmissionInfoList()) {
                String feedProcessingStatus = feedSubmissionInfo.getFeedProcessingStatus();
                String feedSubmissionId = feedSubmissionInfo.getFeedSubmissionId();
                amazonUploadDataMapper.updateProcessStatus(feedSubmissionId,feedProcessingStatus);//根据feedSessionID更改上传数据的处理状态
            }
            count++;
            nextToken=getFeedSubmissionListByNextTokenResponse.getGetFeedSubmissionListByNextTokenResult().getNextToken();
        }
        //   END  _获取上传数据的处理结果存入DB
        //根据当前授权信息、ProcessStatus==_DONE_和uploadStatus=0 条件查询出对象集合
        logger.info("开始获取Amazon处理完毕的数据结果并更改上传数据状态");
        List<AmazonUploadData> uploadList = amazonUploadDataMapper.selectData(amazonSellerId, marketplaceId); //查询出一个店铺一个站点下的上传数据对象
        List<AmazonUploadData> reverse = Lists.reverse(uploadList);
        Map<String, List<AmazonUploadData>> collect = reverse.stream().collect(Collectors.groupingBy(AmazonUploadData::getFeedSubmissionId));
        for (String s : collect.keySet()) {  //一个站点一个店铺下的多个订单：一个sessionId下面可能有多个订单，不需要循环  每个分类出来的list搞一次获取结果就好
            List<AmazonUploadData> amazonUploadDatas = collect.get(s);
                if (resultCount >= 15) {
                    //根据sessionID去获取 结果  有频率限制
                    Thread.sleep(60000);
                }
                String feedSubmissionId = amazonUploadDatas.get(0).getFeedSubmissionId();
                AmazonEnvelope amazonEnvelope = GetFeedSubmissionResultSample.GetFeedSubmissionResult(marketplaceId, mwsToken, amazonSellerId, feedSubmissionId);
                List<AmazonEnvelope.Message> messageList = amazonEnvelope.getMessage();
                List<Integer> messageIdList_Fail=new ArrayList<>();
                for (AmazonEnvelope.Message message : messageList) {
                    //每个订单对应一个message
                    ProcessingReport processingReport = message.getProcessingReport();
                    ProcessingReport.ProcessingSummary processingSummary = processingReport.getProcessingSummary();
                    BigInteger messagesProcessed = processingSummary.getMessagesProcessed();
                    BigInteger messagesSuccessful = processingSummary.getMessagesSuccessful();
                    BigInteger messagesWithError = processingSummary.getMessagesWithError();
                    BigInteger messagesWithWarning = processingSummary.getMessagesWithWarning();
                        //TODO 注：默认上传数据到亚马逊就是成功，如果失败了update错误信息，然后标记失败回平台订单！！！！！！！！！  只有错误的是上传失败，警告包括其他为上传成功
                    if (!messagesWithWarning.equals(new BigInteger("0"))||!messagesWithError.equals(new BigInteger("0"))) {   // TODO  上传数据结果有 失败或者警告
                        List<ProcessingReport.Result> result = processingReport.getResult();
                        for (ProcessingReport.Result result1 : result) {
                            String resultCode = result1.getResultCode();
                            int messageId = Integer.parseInt(result1.getMessageID().toString());
                            Integer resultMessageCode = Integer.parseInt(result1.getResultMessageCode().toString());
                            String resultDescription = result1.getResultDescription();
                            ProcessingReport.Result.AdditionalInfo additionalInfo = result1.getAdditionalInfo();
                            String amazonOrderID = additionalInfo.getAmazonOrderID();
                            String amazonOrderItemCode = additionalInfo.getAmazonOrderItemCode();
                            if (resultCode.equalsIgnoreCase("Error")) {    //TODO  错误的
                            messageIdList_Fail.add(messageId);
                            logger.error("上传失败的参数和原因："+"messageId:"+messageId+",feedSubmissionId:"+feedSubmissionId+",amazonOrderID:"+amazonOrderID+",amazonOrderItemCode:"+amazonOrderItemCode+",resultMessageCode:"+resultMessageCode +",resultCode:"+resultCode+",resultDescription:"+resultDescription);
                            amazonUploadDataMapper.updateUploadStatusFail(amazonOrderID,amazonOrderItemCode,feedSubmissionId,messageId, resultCode,resultMessageCode,resultDescription);
                            //更改亚马逊订单项发货标记状态  失败
                            amazonOrderDetailMapper.updateMarkStatusFail(amazonOrderID,amazonOrderItemCode );
                            } else if (resultCode.equalsIgnoreCase("Warning")) {  //TODO 警告的
                                logger.error("上传警告的参数和原因："+"messageId:"+messageId+",feedSubmissionId:"+feedSubmissionId+",amazonOrderID:"+amazonOrderID+",amazonOrderItemCode:"+amazonOrderItemCode+",resultMessageCode:"+resultMessageCode +",resultCode:"+resultCode+",resultDescription:"+resultDescription);
                                amazonUploadDataMapper.updateUploadResultInfoOnly(amazonOrderID,amazonOrderItemCode,feedSubmissionId,messageId, resultCode,resultMessageCode,resultDescription);
                            }else{ //  TODO 未知的   遇到需重点关注
                                logger.error("需重点关注：遇到未知的错误代码resultCode："+resultCode+"messageId:"+messageId+",feedSubmissionId:"+feedSubmissionId+",amazonOrderID:"+amazonOrderID+",amazonOrderItemCode:"+amazonOrderItemCode+",resultMessageCode:"+resultMessageCode +",resultDescription:"+resultDescription);
                                amazonUploadDataMapper.updateUploadResultInfoOnly(amazonOrderID,amazonOrderItemCode,feedSubmissionId,messageId, resultCode,resultMessageCode,resultDescription);
                            }
                        }
                        //同一个feedSubmitID下除了标记失败的MESSAGEid  其余为成功。
                        amazonUploadDataMapper.updateUploadStatusSuccessful(feedSubmissionId,messageIdList_Fail );
                    }else {
                        //同一个feedSubmitID下全部messageID为成功
                        amazonUploadDataMapper.updateUploadStatusSuccessful(feedSubmissionId, messageIdList_Fail);
                    }
                }
                resultCount++;
        }
        logger.info("结束获取Amazon处理完毕的数据结果并更改上传数据状态");
    }


    /*
     * 解析远程调用授权接口数据，返回亚马逊授权信息集合
     * */
    private List<AmazonEmpower> getAmazonEmpowers(String allRemote1) {
        if (StringUtils.isBlank(allRemote1)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(allRemote1);
        String b = jsonObject.getString("success");
        if ("false".equals(b)) {
            logger.error("调用卖家服务失败");
            return null;
        }
        JSONArray data = jsonObject.getJSONArray("data");
        if (StringUtils.isBlank(data.toString())) {
            return null;
        }
   /*     String s = data.toString();
        System.out.println(s);*/
        List<Empower> list = data.toJavaList(Empower.class);
        List<AmazonEmpower> amazonEmpowerList = new ArrayList<>();
        for (Empower empower :list) {
            amazonEmpowerList.add(new AmazonEmpower(empower.getEmpowerid(),empower.getAccount(), empower
                    .getPinlianaccount(), empower.getPinlianid(),empower.getToken(), empower.getThirdpartyname(), empower
                    .getWebname(), empower.getStatus()
                    , (byte) 0));
        }
        return amazonEmpowerList;
    }
}
