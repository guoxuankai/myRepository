package com.rondaful.cloud.seller.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.utils.RedisUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础控制层
 * */
public class BaseController {

    @Autowired
    public HttpServletRequest request;

    @Autowired
    public HttpServletResponse response;

    @Autowired
    public RedisUtils redisUtils;


    /**
     * Empower表 店铺id
     * @return
     */
    protected List<Integer> getEmpowerIds(List<UserAccountDTO> binds){
        List<Integer> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(binds)){
            return result;
        }
        List<String> listBindCode = new ArrayList<>();
        for (UserAccountDTO dto:binds) {
            //填充数据  1 卖家平台账号
            if(dto.getBindType()!=null && 4==dto.getBindType()){
                listBindCode = dto.getBindCode();
                break;
            }
        }
        return JSONArray.parseArray(JSONObject.toJSONString(listBindCode),Integer.class);
    }

}
