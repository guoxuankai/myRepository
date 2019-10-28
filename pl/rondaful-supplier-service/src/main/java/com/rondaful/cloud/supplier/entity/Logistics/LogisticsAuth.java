package com.rondaful.cloud.supplier.entity.Logistics;

public class LogisticsAuth {

    private Long developId;

    private String secret;

    private String token;

    private Integer type;

    private String createTime;

    public  LogisticsAuth(){}

    public LogisticsAuth(Long developId, String secret, String token, Integer type) {
        this.developId = developId;
        this.secret = secret;
        this.token = token;
        this.type = type;
    }

    public Long getDevelopId() {
        return developId;
    }

    public void setDevelopId(Long developId) {
        this.developId = developId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }


}
