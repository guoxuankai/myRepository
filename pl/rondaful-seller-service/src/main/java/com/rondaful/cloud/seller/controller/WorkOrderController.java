package com.rondaful.cloud.seller.controller;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.entity.WorkOrder;
import com.rondaful.cloud.seller.remote.RemoteUserService;
import com.rondaful.cloud.seller.remote.RemoteWorkOrderService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/workOrder")
public class WorkOrderController {

	@Autowired
	private RemoteWorkOrderService remoteWorkOrderService;
	
	@Autowired
	private RemoteUserService  remoteUserService;
	
	
	private final Logger logger = LoggerFactory.getLogger(WorkOrderController.class);
	
	@Autowired
	private GetLoginUserInformationByToken UserInfo;
	
//	//手机号码验证正则表达式
//	private String checkPhone="^[1][3,4,5,7,8,9][0-9]{9}$";
	
	@ApiOperation(value="意见信息入库",notes="意见信息入库")
	@PostMapping("/insertObject")
	public void insertObject (WorkOrder workOrder){
		if(workOrder.getType() == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"问题类型不能为空");
		if(StringUtils.isBlank(workOrder.getDescription()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"问题描述不能为空");
		try {
			//验证手机号码
//			boolean matches = Pattern.matches(checkPhone, workOrder.getPhone());
//			if(matches == false)
//				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"手机号码格式错误！");
			workOrder.setCreater(UserInfo.getUserInfo().getUser().getLoginName());
			workOrder.setCreateTime(new Date());
			workOrder.setSource(1);
			workOrder.setUserId(UserInfo.getUserInfo().getUser().getUserid());
			
			remoteWorkOrderService.insertObject(workOrder);
			
		} catch (Exception e) {
			logger.error("意见信息入库失败！",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"意见信息入库失败！");
		}
		
	}
	
	
	@ApiOperation(value="意见详情查询",notes="意见详情查询")
	@GetMapping("/searchObjectById")
	public Object searchObjectById(Integer id){
		try {
			String searchObjectById = remoteWorkOrderService.searchObjectById(id);
			String returnRemoteResultDataString = Utils.returnRemoteResultDataString(searchObjectById, "详情信息转换失败");
			Object object = JSONObject.parse(returnRemoteResultDataString);
			return object;
		} catch (Exception e) {
			logger.error("意见详情查询失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"意见详情查询失败");
		}
		
	}
	  
	
	@ApiOperation(value="列表查询",notes="列表查询")
	@PostMapping("/findAll")
	@RequestRequire(require = "page, row", parameter = String.class)
	public Object findAll(WorkOrder workOrder, String page, String row){
		try {
//			workOrder.setCreater(UserInfo.getUserInfo().getUser().getUsername());
//			workOrder.setBindCode(dataLimit());
			//用户数据权限
			UserDTO userDTO = UserInfo.getUserDTO();
			if(userDTO.getManage()){
				workOrder.setParentAccount(userDTO.getUserId()+"");
			}else{
				workOrder.setParentAccount(userDTO.getTopUserId()+"");
			}
			String findAll = remoteWorkOrderService.findAll(workOrder, page, row);
			String returnRemoteResultDataString = Utils.returnRemoteResultDataString(findAll, "列表查询转换失败");
			Object object = JSONObject.parse(returnRemoteResultDataString);
			return object;
		} catch (Exception e) {
			logger.error("列表查询失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"列表查询失败");
		}
	}
	
//
//	public List<Integer> dataLimit(){
//		UserDTO userDTO=UserInfo.getUserDTO();
//		ArrayList<Integer> list = new ArrayList<>();
//
//		if(userDTO.getManage()){//父账号
//			Integer userId = userDTO.getUserId();
//			String childAccount = remoteUserService.getChildAccount(userId, null, "1");
//			String result = Utils.returnRemoteResultDataString(childAccount, "转换失败");
//			if(result != null){
//			List<UserXieRequest> parseArray = JSONArray.parseArray(result,UserXieRequest.class);
//			for (UserXieRequest userXieRequest : parseArray) {
//				if(userXieRequest != null){
//					list.add(userXieRequest.getUserId());
//				}
//			}
//				list.add(userId);
//			}else{
//				list.add(userDTO.getUserId());
//			}
//		}else{//子账号
//			list.add(userDTO.getUserId());
//		}
//		return list;
//	}
//
//

	
	
	
	
	
	
}










