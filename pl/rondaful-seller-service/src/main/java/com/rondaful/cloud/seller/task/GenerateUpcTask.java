package com.rondaful.cloud.seller.task;


import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.seller.entity.PrefixCode;
import com.rondaful.cloud.seller.service.PrefixCodeService;
import com.rondaful.cloud.seller.service.UpcGenerateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 自动生成upc码定时任务
 *
 * @author guoxuankai
 * @date 2019/6/10
 */
@Component
public class GenerateUpcTask {

    private static Logger logger = LoggerFactory.getLogger(GenerateUpcTask.class);

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private PrefixCodeService prefixCodeService;

    @Autowired

    private UpcGenerateService upcGenerateService;

    /**
     * 扫描卖家表，将开启了自动生成upc的卖家查出来，再根据卖家的前缀码生成UPC码
     */
    public void execute() throws Exception {

        //查询前20条前缀码
        Page.builder("1", "20");
        PrefixCode prefixCodeQuery = new PrefixCode();
        prefixCodeQuery.setStatus(1);
        Page<PrefixCode> page = prefixCodeService.page(prefixCodeQuery);
        PageInfo<PrefixCode> pageInfo = (PageInfo) page.getPageInfo();
        List<PrefixCode> list = pageInfo.getList();

        for (PrefixCode prefixCode : list) {
            taskExecutor.execute(new GenerateUpcThread(prefixCode, prefixCodeService, upcGenerateService));
        }


    }
}
