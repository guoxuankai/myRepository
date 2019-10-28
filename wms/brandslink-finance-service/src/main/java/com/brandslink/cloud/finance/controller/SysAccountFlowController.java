package com.brandslink.cloud.finance.controller;

import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.finance.pojo.dto.SysAccountFlowDto;
import com.brandslink.cloud.finance.pojo.vo.SysAccountFlowVo;
import com.brandslink.cloud.finance.service.SysAccountFlowService;
import com.brandslink.cloud.finance.utils.ExcelUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yangzefei
 * @Classname SysAccountFlowController
 * @Description 平台资金流水控制器
 * @Date 2019/9/3 13:50
 */
@RestController
@RequestMapping("/sysAccountFlow")
@Api(value = "平台流水")
public class SysAccountFlowController extends BaseController {

    @Resource
    private SysAccountFlowService sysAccountFlowService;

    @GetMapping("/getList")
    @ApiOperation(value = "获取平台流水列表", notes = "平台流水列表")
    public Page<SysAccountFlowDto> getList(SysAccountFlowVo param){
        Page.builder(param.getPageNum(),param.getPageSize());
        List<SysAccountFlowDto> list=sysAccountFlowService.getList(param);
        return new Page(new PageInfo(list));
    }

    @ApiOperation(value = "平台流水导出",notes = "平台流水导出")
    @GetMapping("/export")
    public void export(SysAccountFlowVo param){
        param.setPageSize(50000);
        List<SysAccountFlowDto> list=sysAccountFlowService.getList(param);
        String fileName="平台流水";
        ExcelUtil.exportExcel(list,fileName,fileName,SysAccountFlowDto.class,fileName+".xls",response);
    }

}
