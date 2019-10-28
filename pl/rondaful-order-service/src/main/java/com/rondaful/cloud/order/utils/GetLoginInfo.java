package com.rondaful.cloud.order.utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rondaful.cloud.order.entity.supplier.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;


@Component
public final class GetLoginInfo {
	
	@Autowired
	GetLoginUserInformationByToken getLoginUserInformationByToken;

//	private static  
	
	public UserInfoVO getUserInfo() {
		UserInfoVO userInfo =  new UserInfoVO();
		//当前登录者信息
		 UserDTO userDTO = getLoginUserInformationByToken.getUserDTO();   //需要注入类 GetLoginUserInformationByToken
		 if(userDTO == null) {
			 return userInfo;
		 }
		 //判断是供应商平台
		if(UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())) {
			//判断当前账号是否主账号
			if( ! userDTO.getManage()) {
				List<String> warehouseIdList=userDTO.getBinds().get(0).getBindCode();
				userInfo.setwIds(warehouseIdList);
				userInfo.setTopFlag(1);
			}else {
				userInfo.setTopFlag(0);
			}
		}else if (UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())){ //判断是否管理后台
            if (!userDTO.getManage()){
                List<UserAccountDTO> list=userDTO.getBinds();
                for (UserAccountDTO dto:list) {
                    if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(dto.getBindType())){
                    	userInfo.setSuppliers(dto.getBindCode());
                    	userInfo.setTopFlag(1);
                    }
                }
            }else {
				userInfo.setTopFlag(0);
			}
		}
		userInfo.setUserId(userDTO.getUserId());
		userInfo.setLoginName(userDTO.getLoginName());
		userInfo.setTopUserId(userDTO.getTopUserId() == null ? null:userDTO.getTopUserId());
		userInfo.setTopUserLoginName(userDTO.getTopUserLoginName());
		userInfo.setPlatformType(userDTO.getPlatformType());
		return userInfo;
	}

}
