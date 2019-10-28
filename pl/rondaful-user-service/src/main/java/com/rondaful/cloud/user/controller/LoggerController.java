package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.logger.QueryLoggerDTO;
import com.rondaful.cloud.user.model.dto.logger.ShowPropertyDTO;
import com.rondaful.cloud.user.model.request.logger.HtmlPropertyReq;
import com.rondaful.cloud.user.model.request.logger.QueryLoggerReq;
import com.rondaful.cloud.user.service.ILoggerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;


@Api(description = "日志相关操作")
@RestController
@RequestMapping("/logger/")
public class LoggerController extends BaseController {

	@Autowired
	private ILoggerService loggerService;


	@ApiOperation(value = "分页获取日志")
	@PostMapping(value = "getsPage")
	public PageDTO getsPage(QueryLoggerReq req){
		UserDTO userDTO=super.userToken.getUserDTO();
		QueryLoggerDTO dto=new QueryLoggerDTO();
		BeanUtils.copyProperties(req,dto);
		dto.setPlatformType(userDTO.getPlatformType());
		if (StringUtils.isEmpty(req.getLoginName())){
			dto.setLoginName(userDTO.getLoginName());
		}
		dto.setLanguageType(super.request.getHeader("i18n"));
		ZoneId zoneId = ZoneId.systemDefault();
		if (StringUtils.isEmpty(req.getStartTime())||StringUtils.isEmpty(req.getEndTime())){
			LocalDateTime endDate=LocalDateTime.now();
			LocalDateTime startDate=endDate.with(TemporalAdjusters.firstDayOfMonth());
			dto.setStartTime(Date.from(startDate.atZone(zoneId).toInstant()));
			dto.setEndTime(Date.from(endDate.atZone(zoneId).toInstant()));
		}else {
			DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime start=LocalDateTime.parse(req.getStartTime(),df);
			LocalDateTime end=LocalDateTime.parse(req.getEndTime(),df);
			if (start.getYear()!=end.getYear()||start.getMonth()!=end.getMonth()){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.logger.query.date");
			}
			dto.setStartTime(Date.from(start.atZone(zoneId).toInstant()));
			dto.setEndTime(Date.from(end.atZone(zoneId).toInstant()));
		}
		return this.loggerService.getsPage(dto);
	}

	@ApiOperation(value = "根据id查询授信记录")
	@ApiImplicitParam(name = "userId", value = "卖家id", dataType = "Integer", paramType = "query", required = true)
	@GetMapping("getsCreditById")
	public List getsCreditById(Integer userId){
		return this.loggerService.getsCreditById(userId,super.request.getHeader("i18n"));
	}

	@ApiOperation(value = "插入对应页面展示的属性")
	@PostMapping("insertHtmlProperty")
	public Integer insertHtml(@RequestBody HtmlPropertyReq req){
		UserDTO userDTO=super.userToken.getUserDTO();
		ShowPropertyDTO dto=new ShowPropertyDTO();
		BeanUtils.copyProperties(req,dto);
		dto.setUserId(userDTO.getUserId());
		dto.setPlatformType(userDTO.getPlatformType());
		return this.loggerService.insertHtmlProperty(dto);
	}

	@ApiOperation(value = "根据页面路径标识查询当前用户页面展示列")
	@GetMapping("getHtmlProperty")
	public ShowPropertyDTO getHtmlProperty(String path){
		UserDTO userDTO=super.userToken.getUserDTO();
		return this.loggerService.getHtmlProperty(path,userDTO.getUserId(),userDTO.getPlatformType());
	}

}
