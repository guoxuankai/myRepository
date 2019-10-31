package com.brandslink.cloud.common.push;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.aspect.OpenAPIAspect;
import com.brandslink.cloud.common.constant.CustomerConstant;
import com.brandslink.cloud.common.entity.CustomerInfoEntity;
import com.brandslink.cloud.common.enums.OpenAPICodeEnum;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.enums.ThirdMsgTypeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.MD5;
import com.brandslink.cloud.common.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author yangzefei
 * @Classname PushThirdService
 * @Description 推送第三方服务
 * @Date 2019/8/3 10:10
 */
@Component
public class PushThirdService {
    private final static Logger log = LoggerFactory.getLogger(PushThirdService.class);
    /**
     * 通知间隔时间为：5分钟，30分钟，1小时，3小时，5小时，10小时
     */
    private int[] delayMinutes=new int[]{5,30,60,180,300,600};

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 第三方返回字符串
     */
    private String resultStr="success";

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 消息推送
     * @param appId 应用id
     * @param msgType 消息类型
     * @param message 消息内容
     */
    public void send(String appId, ThirdMsgTypeEnum msgType, String message){
        String msgId= UUID.randomUUID().toString().replaceAll("-","");
        CustomerInfoEntity appDTO=getAppDto(appId);
        if(StringUtils.isEmpty(appDTO.getUrl())){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"回调地址不存在");
        }

        TreeMap<String,String> sortMap=new TreeMap<>();
        sortMap.put("msgId",msgId);
        sortMap.put("msgCode",msgType.getMsgCode());
        sortMap.put("message",message);

        String access_token= MD5.getAccessToken(sortMap,appDTO.getCustomerAppId(),appDTO.getCustomerSecretKey());
        String url=appDTO.getUrl()+"?access_token="+access_token;
        PushThirdMsg thirdMsg=new PushThirdMsg(msgId,msgType.getMsgCode(),message);
        String result=postMessage(url,thirdMsg);
        String mqMsg=JSON.toJSONString(new MQThirdMsg(thirdMsg,url));
        log.info("推送消息:{},返回的消息:{}",mqMsg,result);
        if(!resultStr.equals(result)){
            sendMessage(mqMsg,delayMinutes[0]);
        }
    }

    /**
     * 获取第三方开发者帐号信息
     * @param appId
     * @return
     */
    private CustomerInfoEntity getAppDto(String appId){
        CustomerInfoEntity appDto= (CustomerInfoEntity)redisUtils.get(CustomerConstant.REDIS_PREFIX + appId);
        if(appDto==null){
            throw new GlobalException(OpenAPICodeEnum.RETURN_CODE_200202);
        }
        return appDto;
    }

    /**
     * 接收消息
     * @param message 消息内容
     */
    @RabbitListener(queues = PushThirdConfig.PUSH_THIRD_QUEUE)
    private void receivePushMessage(String message) {
        MQThirdMsg mqThirdMsg= JSON.parseObject(message,MQThirdMsg.class);
        String result=postMessage(mqThirdMsg.getUrl(),mqThirdMsg.getThirdMsg());
        int number=mqThirdMsg.getNumber();
        mqThirdMsg.setNumber(number+1);
        String mqMsg=JSON.toJSONString(mqThirdMsg);
        log.info("推送消息:{},返回的消息:{}",mqMsg,result);
        if(!resultStr.equals(result)&&number<delayMinutes.length){
            sendMessage(mqMsg,delayMinutes[number]);
        }
    }

    /**
     * 接收延时消息
     * @param message 消息内容
     */
    @RabbitListener(queues = PushThirdConfig.THIRD_DELAY_QUEUE)
    private void receiveDelayMessage(String message) {
        sendMessage(message);
    }


    /**
     * 发送消息到mq
     * @param message 消息内容
     */
    private void sendMessage(String message){
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        log.debug("第三方推送服务MQ消息开始发送==>{}{}", correlationId, message);
        rabbitTemplate.convertAndSend(PushThirdConfig.PUSH_THIRD_EXCHANGE, PushThirdConfig.PUSH_THIRD_KEY, message, correlationId);
    }

    /**
     * 发送延迟消息到mq
     * @param message 消息内容
     * @param delayMinute 延时时间(分钟)
     */
    private void sendMessage(String message,int delayMinute) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        log.debug("第三方延迟MQ消息开始发送==>{}{}", correlationId, message);
        rabbitTemplate.convertAndSend(PushThirdConfig.THIRD_DELAY_EXCHANGE, PushThirdConfig.THIRD_DELAY_KEY, message, m -> {
            m.getMessageProperties().setHeader("x-delay", delayMinute*1000*60); // 设置延迟时间
            return m;
        }, correlationId);
    }

    /**
     * 使用post推送消息
     * @param url
     * @param thirdMsg
     * @return
     */
    private String postMessage(String url,PushThirdMsg thirdMsg){
        //用于接收返回的结果
        String result;
        try {
            HttpPost post = new HttpPost(url);
            List<BasicNameValuePair> pairList = new ArrayList<>();
            // 迭代Map-->取出key,value放到BasicNameValuePair对象中-->添加到list中
            String dataJson=JSONObject.toJSONString(thirdMsg);
            pairList.add(new BasicNameValuePair("dataJson",dataJson));
            post.setEntity(new UrlEncodedFormEntity(pairList, "utf-8"));
            // 创建一个http客户端
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            // 发送post请求
            HttpResponse response = httpClient.execute(post);

            result= EntityUtils.toString(response.getEntity(),"UTF-8");
//            // 状态码为：200
//            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
//                // 返回数据：
//                result = EntityUtils.toString(response.getEntity(),"UTF-8");
//            }else{
//                return "";
//            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return result;
    }
}
