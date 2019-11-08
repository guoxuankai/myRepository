package com.brandslink.cloud.common.push;

/**
 * @author yangzefei
 * @Classname PushThirdMsg
 * @Description 推送到第三方服务的消息实体
 * @Date 2019/8/3 10:01
 */

public class PushThirdMsg {

    /**
     * 消息ID,后续用来查看日志
     */
    private String msgId;
    /**
     * 消息类型
     */
    private String msgCode;

    /**
     * 消息主体,json格式
     */
    private String message;

    public PushThirdMsg(){}
    public PushThirdMsg(String msgId,String msgCode,String message){
        this.msgId=msgId;
        this.msgCode=msgCode;
        this.message=message;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
