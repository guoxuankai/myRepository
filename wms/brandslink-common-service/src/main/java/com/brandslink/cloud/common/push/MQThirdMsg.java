package com.brandslink.cloud.common.push;

/**
 * @author yangzefei
 * @Classname MQThirdMsg
 * @Description 存放mq队列的消息
 * @Date 2019/8/3 10:07
 */
public class MQThirdMsg {

    /**
     * 推送消息主体
     */
    private PushThirdMsg thirdMsg;

    /**
     * 推送地址
     */
    private String url;

    /**
     * 推送次数
     */
    private Integer number;

    public MQThirdMsg(){}
    public MQThirdMsg(PushThirdMsg thirdMsg,String url){
        this.thirdMsg=thirdMsg;
        this.url=url;
        this.number=1;
    }

    public PushThirdMsg getThirdMsg() {
        return thirdMsg;
    }

    public void setThirdMsg(PushThirdMsg thirdMsg) {
        this.thirdMsg = thirdMsg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
