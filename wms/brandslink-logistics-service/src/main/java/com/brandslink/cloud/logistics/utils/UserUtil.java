package com.brandslink.cloud.logistics.utils;

import com.brandslink.cloud.common.entity.UserDetailInfo;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserUtil {

	@Resource
	private GetUserDetailInfoUtil getUserDetailInfoUtil;

	public UserDetailInfo getUser() {
		return getUserDetailInfoUtil.getUserDetailInfo();
	}

	public Long getUserId() {
//		return 1314L;
		return Long.valueOf(getUserDetailInfoUtil.getUserDetailInfo().getId());
	}

	public String getUserName() {
//		return "administer";
		return getUserDetailInfoUtil.getUserDetailInfo().getName();
	}

	public String getWarehouseCode(){
//		return "520";
		return getUserDetailInfoUtil.getUserDetailInfo().getWarehouseCode();
	}
}