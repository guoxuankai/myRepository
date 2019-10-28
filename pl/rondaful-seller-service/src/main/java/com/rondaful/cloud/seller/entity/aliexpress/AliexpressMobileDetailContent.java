package com.rondaful.cloud.seller.entity.aliexpress;

import java.io.Serializable;
import java.util.List;

public class AliexpressMobileDetailContent implements Serializable {
    private List<AliexpressMobileDetailContentImg> images;
    private String content;
    private String type;
    private String col;

    public List<AliexpressMobileDetailContentImg> getImages() {
        return images;
    }

    public void setImages(List<AliexpressMobileDetailContentImg> images) {
        this.images = images;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }
}
