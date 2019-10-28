package com.brandslink.cloud.finance.controller;


import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.finance.constants.QuoteStatusConstant;
import com.brandslink.cloud.finance.pojo.entity.StandardQuote;
import com.brandslink.cloud.finance.pojo.vo.StandardQuoteVO;
import com.brandslink.cloud.finance.service.StandardQuoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


/**
 * 标准报价
 */
@RestController
@RequestMapping(value = "/quote")
@Api("标准报价")
public class StandardQuoteController extends BaseController {


    @Autowired
    private StandardQuoteService standardQuoteService;


    @GetMapping("/list")
    @ApiOperation(value = "标准报价列表", notes = "标准报价列表")
    public Page<StandardQuote> list(StandardQuoteVO StandardQuoteQuery) {
        List<Integer> effectiveId = standardQuoteService.getEffectiveId();
        Page.builder(StandardQuoteQuery.getPageNum(), StandardQuoteQuery.getPageSize());
        List<StandardQuote> list = standardQuoteService.list(StandardQuoteQuery);
        for (StandardQuote standardQuote : list) {
            Integer quoteStatus = standardQuote.getQuoteStatus();
            if (quoteStatus != QuoteStatusConstant.TO_SUBMIT) {
                Date updateTime = standardQuote.getUpdateTime();
                if (updateTime.compareTo(new Date()) > 0) {
                    standardQuote.setQuoteStatus(QuoteStatusConstant.TO_EFFECTIVE);
                } else {
                    if (effectiveId.contains(standardQuote.getId())) {
                        standardQuote.setQuoteStatus(QuoteStatusConstant.EXECUTED);
                    } else {
                        standardQuote.setQuoteStatus(QuoteStatusConstant.EFFECTIVENESS);
                    }
                }
            }
        }
        Page resultPage = new Page(list);
        return resultPage;
    }


    @PutMapping("/submit/{id}")
    @ApiOperation(value = "提交", notes = "提交")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "主键id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Date", name = "effectiveDate", value = "生效时间", required = true)
    })
    public void submit(@PathVariable("id") int id, @RequestParam("effectiveDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date effectiveDate) {
        StandardQuote standardQuote = new StandardQuote();
        standardQuote.setId(id);
        standardQuote.setUpdateTime(effectiveDate);
        standardQuote.setSubmitTime(new Date());
        standardQuote.setQuoteStatus(QuoteStatusConstant.TO_EFFECTIVE);
        standardQuoteService.updateByPrimaryKeySelective(standardQuote);
    }


}
