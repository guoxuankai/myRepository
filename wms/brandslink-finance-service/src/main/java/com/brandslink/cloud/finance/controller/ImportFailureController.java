package com.brandslink.cloud.finance.controller;


import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.finance.pojo.entity.ImportFailure;
import com.brandslink.cloud.finance.service.ImportFailureService;
import com.brandslink.cloud.finance.utils.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


/**
 * 物流商收费导入失败
 */
@RestController
@RequestMapping(value = "/importFailure")
@Api("物流商收费导入失败")
public class ImportFailureController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ImportFailureController.class);


    @Autowired
    private ImportFailureService importFailureService;


    @PostMapping("/list")
    @ApiOperation(value = "物流商收费导入失败列表", notes = "物流商收费导入失败列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "packageNo", value = "包裹号", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "waybill", value = "物流运单号", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "第几页", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "一页条数", required = true)
    })
    public Page<ImportFailure> list(@RequestParam(value = "packageNo", required = false) String packageNo, @RequestParam(value = "waybill", required = false) String waybill, @RequestParam("page") int page, @RequestParam("pageSize") int pageSize) {
        Page.builder(page, pageSize);
        ImportFailure importFailure = new ImportFailure();
        importFailure.setPackageNo(packageNo);
        importFailure.setWaybill(waybill);
        Page<ImportFailure> resultPage = importFailureService.page(importFailure);
        return resultPage;
    }

    @DeleteMapping("/delete/{ids}")
    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "ids", value = "主键id集合,逗号隔开", required = true)
    })
    public void delete(@PathVariable("ids") String ids) {
        String[] split = ids.split(",");
        int[] idArray = Arrays.asList(split).stream().mapToInt(Integer::parseInt).toArray();
        importFailureService.deleteByIds(idArray);
    }


    @GetMapping("/exportExcel")
    @ApiOperation(value = "导出", notes = "导出")
    public void exportExcel() {
        Page<ImportFailure> resultPage = importFailureService.page(new ImportFailure());
        List<ImportFailure> list = resultPage.getPageInfo().getList();
        ExcelUtil.exportExcel(list, "导入失败列表", "Sheet1", ImportFailure.class, "importFailure.xlsx", response);

    }


}
