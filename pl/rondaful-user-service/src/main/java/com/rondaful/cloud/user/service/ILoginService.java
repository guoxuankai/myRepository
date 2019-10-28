package com.rondaful.cloud.user.service;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author: xqq
 * @Date: 2019/4/26
 * @Description:
 */
public interface ILoginService {

    /**
     * 登录验证
     * @param userName
     * @param passWord
     * @param type
     */
    Map<String,Object> login(String userName, String passWord, Integer type, HttpServletResponse response);
}
