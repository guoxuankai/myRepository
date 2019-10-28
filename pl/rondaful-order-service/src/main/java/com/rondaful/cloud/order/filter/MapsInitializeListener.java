package com.rondaful.cloud.order.filter;

import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

/**
 * URL映射的初始化监听器
 *
 * @author zhangjinglei
 * @date 2019年05月06日上午11:31:52
 */
public class MapsInitializeListener implements ApplicationListener<ApplicationStartingEvent> {

    private String propertiesFileName;

    public MapsInitializeListener(String propertiesFileName) {
        super();
        this.propertiesFileName = propertiesFileName;
    }

    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        MapsUtils.loadAllProperties(propertiesFileName);
    }
}
