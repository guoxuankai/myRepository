package com.rondaful.cloud.user.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.constant.UserConstants;
import com.rondaful.cloud.common.entity.user.MenuCommon;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.MD5;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.user.entity.Menu;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.enums.UserStatusEnum;
import com.rondaful.cloud.user.model.dto.user.*;
import com.rondaful.cloud.user.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: xqq
 * @Date: 2019/4/26
 * @Description:
 */
@Service("loginServiceImpl")
public class LoginServiceImpl implements ILoginService {

    @Autowired
    private IManageUserService manageUserService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private INewSupplierService supplierService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ISellerUserService sellerUserService;

    /**
     * 登录验证
     *
     * @param userName
     * @param passWord
     * @param type
     */
    @Override
    public Map<String,Object> login(String userName, String passWord, Integer type, HttpServletResponse response) {
        passWord=passWord.trim();
        UserAll userAll=new UserAll();
        switch (type){
            case 2:
                userAll=this.getManageUser(userName,passWord);
                break;
            case 1:
                userAll=this.getSellerUser(userName,passWord);
                break;
            case 0:
                userAll=this.getSupplierUser(userName,passWord);
                break;
            default:
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100433.getCode(), "login.error.account.not.exist");
        }
        if (!userAll.getUser().getPlatformType().equals(type)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100433.getCode(), "login.error.account.not.exist");
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestType = request.getHeader("user-agent");
        String token="";
        if ( requestType.indexOf("Android") != -1 ){
            token = getToken(userAll,"APP");
        }else{
            token = getToken(userAll,"PC");
        }
        this.redisUtils.set(UserConstants.REDIS_USER_KEY_fix + token, userAll, UserConstants.REDIS_USER_TOKEN_TIMEOUT);

        response.setHeader("Access-Control-Expose-Headers", "token");
        response.setHeader(com.rondaful.cloud.user.constants.UserConstants.REQUEST_HEADER_NAME, token);
        Map<String,Object> result=new HashMap<>(2);
        result.put("token", token);
        result.put("user", userAll);
        return result;
    }

    /**
     * 获取供应商商家账号
     * @param userName
     * @param passWord
     * @return
     */
    private UserAll getSupplierUser(String userName, String passWord){
        SupplierUserDetailDTO dto= this.supplierService.getByName(userName);
        if (dto==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100433.getCode(), "login.error.account.not.exist");
        }
        if (!dto.getPassWord().equals(passWord.length()<30?MD5.md5Password(passWord):passWord)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100001);
        }
        UserAll userAll = new UserAll();
        UserCommon userCommon=new UserCommon();
        UserCommon parentUser=new UserCommon();
        BeanUtils.copyProperties(dto,userCommon);
        userCommon.setPlatformType(UserEnum.platformType.SUPPLIER.getPlatformType());
        userCommon.setUserid(dto.getId());
        userCommon.setLoginName(dto.getLoginName());
        userCommon.setUsername(dto.getUserName());
        if (CollectionUtils.isNotEmpty(dto.getBinds())){
            userCommon.setBinds(JSONArray.parseArray(JSONObject.toJSONString(dto.getBinds()),UserAccountDTO.class));
        }
        List<MenuCommon> menus=new ArrayList<>();
        if (dto.getTopUserId()==0){
            menus = this.menuService.getsAll(UserEnum.platformType.SUPPLIER.getPlatformType(),false);
            parentUser.setUserid(dto.getId());
            parentUser.setUsername(dto.getUserName());
            parentUser.setLoginName(dto.getLoginName());
        }else {
            menus=this.menuService.getsByIds(dto.getMenuIds());
            SupplierUserDetailDTO detailDTO=this.supplierService.getById(dto.getTopUserId());
            parentUser.setUserid(detailDTO.getId());
            parentUser.setUsername(detailDTO.getUserName());
            parentUser.setLoginName(detailDTO.getLoginName());
            userCommon.setTopUserId(dto.getTopUserId());
        }
        userAll.setParentUser(parentUser);
        userAll.setUser(userCommon);
        userAll.setMenus(menus);
        return userAll;
    }

    /**
     * 获取总后台账号
     * @param userName
     * @param passWord
     * @return
     */
    private UserAll getManageUser(String userName, String passWord){
        ManageUserDTO userDTO=this.manageUserService.getByName(userName);
        if (userDTO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100433.getCode(), "login.error.account.not.exist");
        }
        if (!userDTO.getPassWord().equals(passWord)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100001);
        }
        UserAll userAll = new UserAll();
        UserCommon parentUser=new UserCommon();
        UserCommon userCommon=new UserCommon();
        BeanUtils.copyProperties(userDTO,userCommon);
        userCommon.setPlatformType(UserEnum.platformType.CMS.getPlatformType());
        userCommon.setUserid(userDTO.getId());
        userCommon.setUsername(userDTO.getUserName());
        userCommon.setLoginName(userDTO.getLoginName());
        if (CollectionUtils.isNotEmpty(userDTO.getBinds())){
            userCommon.setBinds(JSONArray.parseArray(JSONObject.toJSONString(userDTO.getBinds()),UserAccountDTO.class));
        }
        List<MenuCommon> menus=new ArrayList<>();
        parentUser.setUserid(userDTO.getParentId());
        parentUser.setUsername(userDTO.getUserName());
        parentUser.setLoginName(userDTO.getLoginName());
        if ("admin".equals(userDTO.getLoginName())){
            Menu query=new Menu();
            query.setPlatformType(2);
            query.setDelFlag(0);
            menus = this.menuService.getsAll(UserEnum.platformType.CMS.getPlatformType(),false);

        }else {
            menus=this.menuService.getsByIds(userDTO.getMenuIds());
            /*后台账号不用管父级的账号id
            ManageUserDetailDTO detailDTO=this.manageUserService.getById(userDTO.getTopUserId(),null);
            parentUser.setUserid(detailDTO.getId());
            parentUser.setUsername(detailDTO.getUserName());
            parentUser.setLoginName(detailDTO.getLoginName());*/
        }
        userAll.setUser(userCommon);
        userAll.setParentUser(parentUser);
        userAll.setMenus(menus);
        return userAll;
    }

    /**
     * 获取卖家账号
     * @param userName
     * @param passWord
     * @return
     */
    private UserAll getSellerUser(String userName, String passWord){
        SellerUserDetailDTO userDTO=null;
        if (StringUtils.isNumeric(userName)){
            userDTO=this.sellerUserService.getByPhone(userName);
            if (userDTO==null){
                userDTO=this.sellerUserService.getByName(userName);
            }
        }else {
            userDTO=this.sellerUserService.getByEmail(userName);
            if (userDTO==null){
                userDTO=this.sellerUserService.getByName(userName);
            }
        }
        if (userDTO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100433.getCode(), "login.error.account.not.exist");
        }
        if (!userDTO.getPassWord().equals(passWord.length()<30?MD5.md5Password(passWord):passWord)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100001);
        }
        List<MenuCommon> menus=new ArrayList<>();
        UserAll userAll = new UserAll();
        UserCommon parentUser=new UserCommon();
        if (UserStatusEnum.DISABLE.getStatus().equals(userDTO.getStatus())){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100433.getCode(), "login.error.account.not.exist");
        }
        if (userDTO.getParentId()==0){
            Menu query=new Menu();
            query.setPlatformType(1);
            query.setDelFlag(0);
            Integer sellerType=1;
            menus = this.menuService.getsAll(UserEnum.platformType.SELLER.getPlatformType(),sellerType.equals(userDTO.getSellerType()));
            parentUser.setUserid(userDTO.getParentId());
            parentUser.setUsername(userDTO.getUserName());
            parentUser.setLoginName(userDTO.getUserName());
        }else {
            menus=this.menuService.getsByIds(userDTO.getMenuIds());
            SellerUserDetailDTO detailDTO=this.sellerUserService.getById(userDTO.getTopUserId());
            parentUser.setUserid(detailDTO.getId());
            parentUser.setUsername(detailDTO.getLoginName());
            parentUser.setLoginName(detailDTO.getLoginName());
        }
        if (userDTO.getTopUserId()>0){
            SellerUserDetailDTO detailDTO= this.sellerUserService.getById(userDTO.getTopUserId());
            if (detailDTO==null||UserStatusEnum.DISABLE.getStatus().equals(userDTO.getStatus())){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100433.getCode(), "login.error.account.not.exist");
            }
        }
        UserCommon userCommon=new UserCommon();
        BeanUtils.copyProperties(userDTO,userCommon);
        userCommon.setPlatformType(UserEnum.platformType.SELLER.getPlatformType());
        userCommon.setUserid(userDTO.getId());
        userCommon.setUsername(userDTO.getUserName());
        userCommon.setLoginName(userDTO.getLoginName());
        userCommon.setUsername(userDTO.getUserName());
        userCommon.setImageSite(userDTO.getHeadImg());
        if (CollectionUtils.isNotEmpty(userDTO.getBinds())){
            userCommon.setBinds(JSONArray.parseArray(JSONObject.toJSONString(userDTO.getBinds()),UserAccountDTO.class));
        }
        userAll.setUser(userCommon);
        userAll.setParentUser(parentUser);
        userAll.setMenus(menus);
        return userAll;
    }






    /**
     * 根据当前用户信息生成token  App端
     * @param result
     * @return
     */
    public String getToken(UserAll result,String type) {
        StringBuilder builder = new StringBuilder();
        builder.append(UserConstants.REQUEST_HEADER_NAME).append("-");
        builder.append(result.getUser().getUserid()).append("-");
        builder.append(result.getUser().getUsername()).append("-");
        builder.append(result.getUser().getPlatformType()).append("-");
        String token = MD5.md5Password(builder.toString());
        token = token + "-"+ type +"-" + System.currentTimeMillis();
        return token;
    }

    public static void main(String[] args) {
        String pattern = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher("MJPT@qq.com");
        System.out.println(m.matches());
    }

}
