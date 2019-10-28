package com.rondaful.cloud.seller.entity.aliexpress;

import java.io.Serializable;
import java.util.List;

public class AliexpressAeopAeMultimedia implements Serializable {

    private List<AliexpressAeopAeVideo> aeopAEVideos;

    public List<AliexpressAeopAeVideo> getAeopAEVideos() {
        return aeopAEVideos;
    }

    public void setAeopAEVideos(List<AliexpressAeopAeVideo> aeopAEVideos) {
        this.aeopAEVideos = aeopAEVideos;
    }
}
