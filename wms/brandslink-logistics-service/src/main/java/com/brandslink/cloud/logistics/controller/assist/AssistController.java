package com.brandslink.cloud.logistics.controller.assist;

import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.logistics.service.impl.AssistServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api("辅助预置数据")
@RestController
@RequestMapping("/assist")
public class AssistController extends BaseController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AssistServiceImpl assistService;

    private final static Logger _log = LoggerFactory.getLogger(AssistController.class);

    @GetMapping("/insertBatchMethodData")
    @ApiOperation(value = "预置邮寄方式数据")
    public void insertBatchMethodData() {
        assistService.insertBatchMethodData();
    }
}