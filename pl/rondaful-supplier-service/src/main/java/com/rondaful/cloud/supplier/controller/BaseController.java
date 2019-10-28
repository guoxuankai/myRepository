package com.rondaful.cloud.supplier.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础控制层
 * */
public class BaseController {

    public static Integer WAREHOUSE_ID_LIST=0;
    public static Integer USER_ID_LIST=1;

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    @Autowired
    protected GetLoginUserInformationByToken userToken;


    /**
     * 获取用用户的门店  卖家账号绑定关系
     * KEY  1：店铺id    2用户id
     *
     * @param
     * @return
     */
    protected Map<Integer, List<Integer>> getBinds() {
        UserDTO userDTO=this.userToken.getUserDTO();
        Map<Integer, List<Integer>> result = new HashMap<>(2);
        if (UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())) {
            if (!userDTO.getManage()) {
                List<UserAccountDTO>  binds =userDTO.getBinds();
                if (CollectionUtils.isEmpty(binds)) {
                    return result;
                }
                for (UserAccountDTO bind:binds) {
                    if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(bind.getBindType())){
                        result.put(USER_ID_LIST, JSONArray.parseArray(JSONObject.toJSONString(bind.getBindCode()),Integer.class));
                    }
                }
            }
        } else if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDTO.getPlatformType())) {
            if (!userDTO.getManage()) {
                List<UserAccountDTO>  binds=userDTO.getBinds();
                if (CollectionUtils.isEmpty(binds)||CollectionUtils.isEmpty(binds.get(0).getBindCode())) {
                    return result;
                }
                result.put(WAREHOUSE_ID_LIST,JSONArray.parseArray(JSONObject.toJSONString(binds.get(0).getBindCode()),Integer.class));
            } else {
                List<Integer> userIds = new ArrayList<>();
                userIds.add(userDTO.getUserId());
                result.put(USER_ID_LIST,userIds);
            }
        }
        return result;
    }



}
