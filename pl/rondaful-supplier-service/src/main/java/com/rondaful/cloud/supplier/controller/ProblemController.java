package com.rondaful.cloud.supplier.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.remote.RemoteProblemService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/problem")
public class ProblemController {

	@Autowired
	private RemoteProblemService remoteProblemService;
	
	
	@ApiOperation(value="查询问题(模糊)列表信息",notes="模糊查询问题列表信息")
	@GetMapping("/findAll")
	@RequestRequire(require = "page, row", parameter = String.class)
	public Object fuzzyFindAll(@RequestParam(value="page",defaultValue = "1")String page,
			                    @RequestParam(value="row",defaultValue = "10")String row,
										 String source,
										 String type,
										 String startDate,
										 String endDate,
										 String content,
							             String title,
										 String dateType,
										 String belongSys){
		
		try {
//			Page.builder(page, row);
			
			String queryProblem = remoteProblemService.queryProblem(page, row, source, type, startDate, endDate, content,title, dateType,belongSys);
			
			String returnRemoteResultDataString = Utils.returnRemoteResultDataString(queryProblem,"转换失败");
			Object data = JSONObject.parse(returnRemoteResultDataString);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
		
		
	}
	
	
	@ApiOperation(value="查询问题详情信息",notes="查询问题详情信息")
	@GetMapping("/selectProlem")
	public Object selectProlem(Long id){
		try {
			
			String queryProblemDetail = remoteProblemService.queryProblemDetail(id);
			String returnRemoteResultDataString = Utils.returnRemoteResultDataString(queryProblemDetail,"转换失败");
			Object data = JSONObject.parse(returnRemoteResultDataString);
			return data;
		} catch (Exception e) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查询问题详情信息失败");
		}
		
	}
	
	
	
	
	
	
	
	
}
