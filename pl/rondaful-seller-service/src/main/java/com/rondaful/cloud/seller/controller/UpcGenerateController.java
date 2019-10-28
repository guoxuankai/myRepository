package com.rondaful.cloud.seller.controller;

import com.rondaful.cloud.seller.task.GenerateUpcTask;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guoxuankai
 * @date 2019/6/10
 */
@RestController
@RequestMapping("/upcgenerate")
public class UpcGenerateController {

    @Autowired
    private GenerateUpcTask generateUpcTask;


    private final Logger logger = LoggerFactory.getLogger(UpcGenerateController.class);


    /**
     * 自动生成upc码，定时任务
     */
    @ApiOperation(value = "自动生成upc码，定时任务", notes = "自动生成upc码，定时任务")
    @GetMapping("/upcGenerateTask")
    public void upcGenerateTask() {
        try {
            generateUpcTask.execute();
        } catch (Exception e) {
            logger.error("生成upc码线程异常", e);
        }

    }


}
