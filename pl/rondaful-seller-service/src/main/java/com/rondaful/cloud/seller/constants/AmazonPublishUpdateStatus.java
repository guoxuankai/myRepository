package com.rondaful.cloud.seller.constants;

public interface AmazonPublishUpdateStatus {
    /** 初始值**/
    public static final Integer INIT=0;
    /** 在线未修改**/
    public static final Integer NOT_UPDATE=1;
    /** 在线修改成功**/
    public static final Integer UPDATE_SUCCESS=2;
    /** 在线修改失败**/
    public static final Integer UPDATE_FAIL=3;
    /** 在线修改中**/
    public static final Integer UPDATE_GOING=4;
}
