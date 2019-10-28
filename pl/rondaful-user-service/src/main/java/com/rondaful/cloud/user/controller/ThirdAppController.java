package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.common.entity.third.AppDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.service.IThirdAppService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @Author: lxx
 * @Date: 2019/7/9
 * @Description:
 */
@Api(description = "第三方应用相关业务")
@RestController
@RequestMapping("/third/app/")
public class ThirdAppController extends BaseController{

	@Autowired
	IThirdAppService thirdAppService;
	
	@ApiOperation(value = "根据key取得第三方应信息")
	@PostMapping("getByAppKey")
	@ApiImplicitParam(name = "appKey", value = "appKey", dataType = "string", paramType = "query")
	public AppDTO getByAppKey(String appKey) {
		return thirdAppService.getByAppKey(appKey);
	}
	
	@ApiOperation(value = "分页查询第三方应信息")
	@PostMapping("getsPage")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "页码" , required = true, dataType = "Integer", paramType = "query"),
        @ApiImplicitParam(name = "row", value = "每页显示行数", required = true, dataType = "Integer", paramType = "query"),
        @ApiImplicitParam(name = "status", value = "仓库编码", dataType = "Integer", paramType = "query")
	})
	public PageDTO<AppDTO> getsPage(Integer page, Integer row, Integer status) {
		return thirdAppService.getsPage(page, row, status);
	}
	
    @ApiOperation(value = "应用添加")
    @PostMapping("add")
    public Integer add(@RequestBody AppDTO dto){
        return thirdAppService.add(dto);
    }
	
    @ApiOperation(value = "更新应用")
    @PostMapping("update")
    public Integer update(@RequestBody AppDTO dto){
        return thirdAppService.update(dto);
    }


    @ApiOperation(value = "更新应用状态")
    @PostMapping("updateStatus")
    public Integer updateStatus(@RequestParam("appKey")String appKey,@RequestParam("status")Integer status){
        return thirdAppService.updateStatus(appKey,status);
    }

	@ApiOperation(value = "重置token")
	@PostMapping("resetAppToken")
	public Integer resetAppToken(Integer id){
		return thirdAppService.resetAppToken(id);
	}
}
