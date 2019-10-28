package com.rondaful.cloud.seller.entity.aliexpress;

import java.io.Serializable;

public class AliexpressAeopAeVideo implements Serializable {

    private Long aliMemberId;

    private Long mediaId;

    private String mediaStatus;

    private String mediaType;

    private String posterUrl;

    public Long getAliMemberId() {
        return aliMemberId;
    }

    public void setAliMemberId(Long aliMemberId) {
        this.aliMemberId = aliMemberId;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaStatus() {
        return mediaStatus;
    }

    public void setMediaStatus(String mediaStatus) {
        this.mediaStatus = mediaStatus;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}
