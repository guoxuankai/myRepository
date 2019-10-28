package com.brandslink.cloud.finance.controller;


import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.finance.pojo.entity.StandardQuoteDetail;
import com.brandslink.cloud.finance.service.StandardQuoteDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


/**
 * 标准报价
 */
@RestController
@RequestMapping(value = "/quoteDetail")
@Api("标准报价明细")
public class StandardQuoteDetailController extends BaseController {


    @Autowired
    private StandardQuoteDetailService standardQuoteDetailService;


    @PostMapping("/list/{quoteId}")
    @ApiOperation(value = "标准报价列表", notes = "标准报价列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "quoteId", value = "财务报价id", required = true)
    })
    public List<StandardQuoteDetail> list(@PathVariable("quoteId") int quoteId) {
        return standardQuoteDetailService.getByQuoteId(quoteId);
    }

    @PutMapping("/update/{id}")
    @ApiOperation(value = "修改", notes = "修改")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "主键id", required = true)
    })
    public void update(@PathVariable("id") int id, @RequestParam("value") BigDecimal value) {
        StandardQuoteDetail standardQuoteDetail = new StandardQuoteDetail();
        standardQuoteDetail.setId(id);
        standardQuoteDetail.setQuoteValue(value);
        standardQuoteDetailService.updateByPrimaryKeySelective(standardQuoteDetail);

    }

    @PostMapping("/add")
    @ApiOperation(value = "新增", notes = "新增")
    public void add(StandardQuoteDetail standardQuoteDetail) {
        standardQuoteDetailService.insert(standardQuoteDetail);
    }


}
