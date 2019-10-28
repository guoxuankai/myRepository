package com.rondaful.cloud.user.model.request.logger;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/9/6
 * @Description:
 */
public class HtmlPropertyReq implements Serializable {
    private static final long serialVersionUID = 1546023005893065676L;

    private String path;

    private List<String> show;

    private List<String> hide;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getShow() {
        return show;
    }

    public void setShow(List<String> show) {
        this.show = show;
    }

    public List<String> getHide() {
        return hide;
    }

    public void setHide(List<String> hide) {
        this.hide = hide;
    }
}
