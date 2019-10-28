package com.rondaful.cloud.commodity.entity;

import java.io.Serializable;

/**
* @Description:同步cms类：MessageNoticeModel
* @date:2019年5月24日 上午9:50:49
 */
public class Message implements Serializable {
    private String messageCategory;
    private String messageContent;
    private String messagePlatform;
    private String userId;
    private String messageScceptUserName;
    private String messageType;
    private String receiveSys;
    private String isDialog;

    public String getMessageCategory() {
        return messageCategory;
    }

    public void setMessageCategory(String messageCategory) {
        this.messageCategory = messageCategory;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessagePlatform() {
        return messagePlatform;
    }

    public void setMessagePlatform(String messagePlatform) {
        this.messagePlatform = messagePlatform;
    }

    public String getMessageScceptUserName() {
        return messageScceptUserName;
    }

    public void setMessageScceptUserName(String messageScceptUserName) {
        this.messageScceptUserName = messageScceptUserName;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getReceiveSys() {
        return receiveSys;
    }

    public void setReceiveSys(String receiveSys) {
        this.receiveSys = receiveSys;
    }

	public String getIsDialog() {
		return isDialog;
	}

	public void setIsDialog(String isDialog) {
		this.isDialog = isDialog;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
    
}
