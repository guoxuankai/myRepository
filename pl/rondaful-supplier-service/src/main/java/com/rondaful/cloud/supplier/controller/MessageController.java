package com.rondaful.cloud.supplier.controller;


import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.entity.MessageDetailDTO;
import com.rondaful.cloud.supplier.entity.MessageSearchTerm;
import com.rondaful.cloud.supplier.remote.RemoteMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息接口
 * @author xieyanbin
 *
 */
@Api(description = "消息基础接口")
@RestController
@RequestMapping("/message")
public class MessageController extends BaseController {

	private final Logger logger = LoggerFactory.getLogger(MessageController.class);

	@Autowired
	private GetLoginUserInformationByToken getLoginUserInformationByToken;
	
	@Autowired
	private RemoteMessageService remoteMessageService;

    @ApiOperation(value = "分页及条件查询消息列表", notes ="belongSys 所属系统 0供应商，1卖家  2管理后台")
    @RequestRequire(parameter=String.class,require="belongSys")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", value = "当前页码", name = "currentPage", dataType = "String"),
        @ApiImplicitParam(paramType = "query", value = "每页显示行数", name = "pageSize", dataType = "String"),
        @ApiImplicitParam(paramType = "query", value = "开始时间", name = "startDate", dataType = "String"),
        @ApiImplicitParam(paramType = "query", value = "结束时间", name = "endDate", dataType = "String"),
        @ApiImplicitParam(paramType = "query", value = "标题", name = "title", dataType = "String"),
        @ApiImplicitParam(paramType = "query", value = "类型", name = "type", dataType = "String"),
        @ApiImplicitParam(paramType = "query", value = "状态 0未读 1已读 9删除 默认0未读", name = "status", dataType = "String"),
        @ApiImplicitParam(paramType = "query", value = "所属系统 0供应商，1卖家  2管理后台 ", name = "belongSys", dataType = "String",required=true),
        @ApiImplicitParam(paramType = "query", value = "所属语言 0中文 1英文 ", name = "languageSys", dataType = "String",required=true)})
    @GetMapping("/queryMessageList")
	public Object queryMessageList(@RequestParam(value="currentPage",defaultValue = "1") String currentPage, 
								@RequestParam(value="pageSize",defaultValue = "10")String pageSize,
								@RequestParam(value="languageSys",defaultValue = "0")String languageSys,String startDate,
								String endDate,String title,String type,String status,String belongSys) {
    	Object object = null;
    	try {
    		UserCommon user = getLoginUserInformationByToken.getUserInfo().getUser();
   		 	if(null == user) {
   		 		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406);
   		 	}
    		 String result = remoteMessageService.queryMessageList(currentPage, pageSize,languageSys,startDate, endDate, title, type, status, user.getLoginName(), belongSys,"1");
    		 String dataString = Utils.returnRemoteResultDataString(result, "管理后台服务异常");
             object = JSONObject.parseObject(dataString);
    	} catch (Exception e) {
			 logger.error("分页查询消息列表接口失败", e);
			 throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		 }
    	logger.info("查询结果：object={}",object);
   	 return object;
    }
	
    @ApiOperation(value = "分页及条件查询app消息列表", notes ="term  0通知，1公告")
    @RequestRequire(parameter=String.class,require="term")
    @GetMapping("/queryAppMessageList")
    public Object queryAppMessageList(@RequestParam(value="currentPage",defaultValue = "1") String currentPage, 
			@RequestParam(value="pageSize",defaultValue = "10")String pageSize,
			@RequestParam(value="languageSys",defaultValue = "0")String languageSys,String term) {
    	Object object = null;
    	try {
    		UserCommon user = getLoginUserInformationByToken.getUserInfo().getUser();
   		 	if(null == user) {
   		 		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406);
   		 	}
    		 String result = remoteMessageService.queryAppMessageList(currentPage, pageSize,languageSys,term,user.getLoginName());
    		 String dataString = Utils.returnRemoteResultDataString(result, "管理后台服务异常");
             object = JSONObject.parseObject(dataString);
    	} catch (Exception e) {
			 logger.error("分页查询消息列表接口失败", e);
			 throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		 }
    	logger.info("查询结果：object={}",object);
   	 return object;
    }
    
    @ApiOperation(value = "查询消息详情", notes = "id消息id")
    @RequestRequire(parameter = String.class,require="id")
    @GetMapping("/queryMessageDetail")
    public MessageDetailDTO queryMessageDetail(String id,@RequestParam(value="languageSys",defaultValue = "0")String languageSys) {
    	logger.info("查询消息详情接口开始：id={}",id);
    	MessageDetailDTO detail = null;
    	try {
    		UserCommon user = getLoginUserInformationByToken.getUserInfo().getUser();
   		 	if(null == user) {
   		 		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406);
   		 	}
    		String result = remoteMessageService.queryMessageDetail(id,"2",languageSys);
    		String dataString = Utils.returnRemoteResultDataString(result, "管理后台服务异常");
    		detail = JSONObject.parseObject(dataString, MessageDetailDTO.class);
    		if(null != detail) {
    			MessageSearchTerm term = new MessageSearchTerm();
				term.setId(id);
    			term.setStatus("1");
    			term.setUserName(user.getLoginName());
    			term.setBelongSys(user.getPlatformType().toString());
    			String updateStatusResult = remoteMessageService.updateMessageStatusById(term);
    			Utils.returnRemoteResultDataString(updateStatusResult, "管理后台服务异常");
    		}
    	} catch (Exception e) {
			 logger.error("查询消息详情接口失败", e);
			 throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		 }
    	return detail;
    }
    

	@ApiOperation(value = "查询未读总数")
    @GetMapping("/queryMessageCount")
	public Integer queryMessageCount() {
		logger.info("查询未读总数接口开始");
		Integer count = 0;
    	try {
    		UserCommon user = getLoginUserInformationByToken.getUserInfo().getUser();
   		 	if(null == user) {
   		 		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406);
   		 	}
    		String result = remoteMessageService.queryMessageCount(user.getLoginName(), "0");
    		String dataString = Utils.returnRemoteResultDataString(result, "管理后台服务异常");
    		count = JSONObject.parseObject(dataString, Integer.class);
    	} catch (Exception e) {
			 logger.error("查询未读总数接口失败", e);
			 throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
    	}
    	return count;
	}
}


