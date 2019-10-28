package com.brandslink.cloud.user.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Component;


@JobHandler(value="这里自定义一个名字，等会儿管理平台会用到")
@Component
public class TestJob1 extends IJobHandler {

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("测试打印执行日志");
		System.out.println("开始执行任务");
		return SUCCESS;
	}

}
