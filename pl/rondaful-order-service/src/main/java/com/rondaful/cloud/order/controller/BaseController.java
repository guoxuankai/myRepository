package com.rondaful.cloud.order.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.order.entity.ProviderUserDTO;
import com.rondaful.cloud.order.model.dto.sysorder.FeignResult;
import com.rondaful.cloud.order.remote.RemoteUserService;
import com.rondaful.cloud.order.utils.FastJsonUtils;
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
 */
public class BaseController {

    @Autowired
    public HttpServletRequest request;

    @Autowired
    public HttpServletResponse response;

    @Autowired
    public RedisUtils redisUtils;

    @Autowired
    protected GetLoginUserInformationByToken userToken;

    @Autowired
    private RemoteUserService remoteUserService;

    protected Integer EMP_ID_LIST = 1;
    protected Integer USER_ID_LIST = 2;

    /**
     * 获取当前账号关联的卖家帐号（总后台管理身份用）
     *
     * @return
     */
    protected List<Integer> getsUserId(List<UserAccountDTO> binds) {
        List<Integer> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(binds)) {
            return result;
        }
        for (UserAccountDTO accountDTO : binds) {
            if (UserEnum.platformType.SELLER.getPlatformType().equals(accountDTO.getBindType())) {
                if (CollectionUtils.isEmpty(accountDTO.getBindCode())) {
                    return result;
                }
                result = JSONArray.parseArray(FastJsonUtils.toJsonString(accountDTO.getBindCode()), Integer.class);
            }
        }
        return result;
    }

    /**
     * 商家身份获取店铺id
     *
     * @return
     */
    protected List<Integer> getsStoreId(List<UserAccountDTO> binds) {
        List<Integer> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(binds)) {
            return result;
        }
        return JSONArray.parseArray(FastJsonUtils.toJsonString(binds.get(0).getBindCode()), Integer.class);
    }

    /**
     * 根据用户id获取用户名
     *
     * @param userIds
     * @return
     */
    protected List<String> getsName(List<Integer> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return null;
        }
        List<String> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userIds)) {
            FeignResult<List<ProviderUserDTO>> feignResult= this.remoteUserService.getSupplierList(userIds,UserEnum.platformType.SELLER.getPlatformType());
            if (feignResult.isSuccess()&&CollectionUtils.isNotEmpty(feignResult.getData())){
                feignResult.getData().forEach(dto->{
                    result.add(dto.getLoginName());
                });
            }
        }
        return result;
    }

    /**
     * 获取用用户的门店  卖家账号绑定关系
     * KEY  1：店铺id    2用户id
     *
     * @param userCommon
     * @return
     */
    protected Map<Integer, List<Integer>> getBinds(UserCommon userCommon) {
        Map<Integer, List<Integer>> result = new HashMap<>(2);
        List<Integer> empIds = new ArrayList<>();
        List<Integer> userIds = new ArrayList<>();
        if (UserEnum.platformType.CMS.getPlatformType().equals(userCommon.getPlatformType())) {
            if (userCommon.getTopUserId() != 0) {//子账号，0为主账号
                userIds = this.getsUserId(userCommon.getBinds());
                if (CollectionUtils.isEmpty(userIds)) {
                    return null;
                }
            }
        } else if (UserEnum.platformType.SELLER.getPlatformType().equals(userCommon.getPlatformType())) {
            if (userCommon.getTopUserId() != 0) {
                empIds = this.getsStoreId(userCommon.getBinds());
                if (CollectionUtils.isEmpty(empIds)) {
                    return null;
                }
            } else {
                userIds.add(userCommon.getUserid());
            }
        } else {
            return null;
        }
        result.put(EMP_ID_LIST, empIds);
        result.put(USER_ID_LIST, userIds);
        return result;
    }

    /**
     * 获取浏览器类型
     *
     * @param request
     * @return
     */
    public String getExplorerType(HttpServletRequest request) {
        String agent = request.getHeader("USER-AGENT");
        if (agent != null && agent.toLowerCase().indexOf("firefox") > 0) {
            return "firefox";
        } else if (agent != null && agent.toLowerCase().indexOf("msie") > 0) {
            return "ie";
        } else if (agent != null && agent.toLowerCase().indexOf("chrome") > 0) {
            return "chrome";
        } else if (agent != null && agent.toLowerCase().indexOf("opera") > 0) {
            return "opera";
        } else if (agent != null && agent.toLowerCase().indexOf("safari") > 0) {
            return "safari";
        }
        return "others";
    }

}
