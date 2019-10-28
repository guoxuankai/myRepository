package com.rondaful.cloud.user.entity;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 系统记录
 * @author Administrator
 *
 */
@Document(collection = "system_operation_log")
public class SysLog implements Serializable{
	private static final long serialVersionUID = 4109357155591597203L;

	@Field("id")
	@ApiModelProperty(value = "id", required = false)
	private Long id;

	@Field("username")
	@ApiModelProperty(value = "用户名", required = false)
	private String username;

	@Field("account")
	@ApiModelProperty(value = "用户昵称", required = false)
	@Deprecated
    private String account;

	@Field("clientIp")
	@ApiModelProperty(value = "用户ip地址", required = false)
    private String clientIp;

	@Field("optionUrl")
	@ApiModelProperty(value = "操作路径", required = false)
    private String optionUrl;

	@Field("createDate")
	@ApiModelProperty(value = "操作时间", required = false)
    private Date createDate;

	@Field("optionParams")
	@ApiModelProperty(value = "操作提交的参数", required = false)
    private String optionParams;

	@Field("note")
	@ApiModelProperty(value = "注释", required = false)
    private String note;

	@Field("optionActiontype")
	@ApiModelProperty(value = "操作的类型：增，删，改，查", required = false)
    private String optionActiontype;

	@Field("optionDescrption")
	@ApiModelProperty(value = "操作内容", required = false)
    private String optionDescrption;
	@Field("platform")
	@ApiModelProperty(value = "平台", required = false)
	private String platform;
	@Field("userToken")
	@ApiModelProperty(value = "用户token", required = false)
	private String userToken;

	@Field("url")
	@ApiModelProperty(value = "当前操作路径", required = false)
	private String url ;

	@Field("loginName")
	@ApiModelProperty(value = "当前操作路径", required = false)
	private String loginName ;

	@Field("platformType")
	@ApiModelProperty(value = "当前用户类型", required = false)
	private Integer platformType;

    public Integer getPlatformType() {
		return platformType;
	}

	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public SysLog() {
		super();
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp == null ? null : clientIp.trim();
    }

    public String getOptionUrl() {
        return optionUrl;
    }

    public void setOptionUrl(String optionUrl) {
        this.optionUrl = optionUrl == null ? null : optionUrl.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getOptionParams() {
        return optionParams;
    }

    public void setOptionParams(String optionParams) {
        this.optionParams = optionParams == null ? null : optionParams.trim();
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }

    public String getOptionActiontype() {
        return optionActiontype;
    }

    public void setOptionActiontype(String optionActiontype) {
        this.optionActiontype = optionActiontype;
    }

    public String getOptionDescrption() {
        return optionDescrption;
    }

    public void setOptionDescrption(String optionDescrption) {
        this.optionDescrption = optionDescrption == null ? null : optionDescrption.trim();
    }
}