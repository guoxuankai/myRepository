package com.rondaful.cloud.seller.constants;

public class PublishRequestReport {


    /**
     * 亚马逊报告的状态
     */
    public enum reportStatus {   //[1:初始创建 2:已提交报告创建请求获得请求编码 3:报告已生成 4:获得报告编码 5:已获得报告并处理完成 6:报告异常]
        INITIALIZE(1, "初始创建"),
        PUBLISH(2, "已提交报告创建请求获得请求编码"),
        AMAZON_DONE(3, "报告已生成"),
        REPORT_ID(4, "获得报告编码"),
        FINISH(5, "已获得报告并处理完成"),
        REPORT_ERROR(6, "报告异常"),
        NO_ASIN(7,"报告没有ASIN码"),
        OPERATION(8,"临时操作中");

        private Integer status;
        private String msg;

        reportStatus(Integer status, String msg) {
            this.status = status;
            this.msg = msg;
        }

        public Integer getStatus() {
            return status;
        }

        public String getMsg() {
            return msg;
        }
    }


    /**
     * 亚马逊报告的进度
     */
    public enum reportProgress {   //
        RequestReport("RequestReport", "请求创建报告"),
        GetReportRequestList("GetReportRequestList", "获取报告状态"),
        GetReport("GetReport", "获取报告类容"),
        GetMatchingProductList("GetMatchingProductList", "获取ASIN"),
        GetReportMsg("getReportMsg","同步品连没有的刊登");

        private String theInterface;
        private String msg;

        reportProgress(String theInterface, String msg) {
            this.theInterface = theInterface;
            this.msg = msg;
        }

        public String getTheInterface() {
            return theInterface;
        }


        public String getMsg() {
            return msg;
        }

    }

}
