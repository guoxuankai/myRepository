package com.rondaful.cloud.user.service.impl;

import com.rondaful.cloud.common.entity.user.MenuCommon;
import com.rondaful.cloud.common.entity.user.RoleCommon;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.RemoteUtil;
import com.rondaful.cloud.user.constants.UserConstants;
import com.rondaful.cloud.user.controller.utils.ControllerUtil;
import com.rondaful.cloud.user.entity.*;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.mapper.*;
import com.rondaful.cloud.user.remote.UserFinanceInitialization;
import com.rondaful.cloud.user.service.SellerService;
import com.rondaful.cloud.user.utils.MD5;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service("sellerService")
public class SellerServiceImpl extends ControllerUtil implements SellerService {

    @Autowired
    private UserFinanceInitialization userFinanceInitialization;

    @Autowired
    private SellerMapper sellerMapper;
    
    @Autowired
    private CompanyinfoMapper companyinfoMapper;

    @Autowired
    private RedisUtils redisUtils;
    
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    private final Logger logger = LoggerFactory.getLogger(SellerServiceImpl.class);

    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    /**
     * 卖家登录
     *
     * @param username
     * @param password
     * @param platformType
     * @return
     */
    @Override
    public Map<String,Object> sellerUserLogin(String username, String password, Integer platformType, HttpServletResponse response) throws IllegalAccessException,InvocationTargetException{
        Map<String, Object> map = new HashMap<>();
        //封装登录用户的基础信息，角色信息，权限信息
        UserAll userAll = new UserAll();
        List<RoleCommon> roleBeans = new ArrayList<>(); //当前登录用户的角色信息
        HashSet<MenuCommon> menuBeans = new HashSet<>();//当前登录用户的权限信息
        User user = getUserByPhoneOrEmail(null, null, username, platformType);//根据此用户名查询用户是否存在
        if (user == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100433.getCode(), "没有找到用户信息,请重试");
        else if (!user.getUsername().equals(username)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100001);
        else if (user.getStatus().intValue() == 0)throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100425.getCode(), "当前用户已停用或者已注销，请联系管理员重新分配");
        else if (platformType.intValue() == 1) {
            if (user.getPlatformType().intValue() != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100425.getCode(), "您不是本平台卖家用户，请重新登录");
        }
        String handlerPassword = password;
        if (user.getParentId() == null) handlerPassword = MD5.md5Password(password); //用户已经存在，进行密码加密
        //判断密码是否匹配
        if (!handlerPassword.equals(user.getPassword())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100001);
        //根据userId查询到对应的角色id
        List<Integer> roleIds = userRoleMapper.getRoleIdsByUserId(user.getUserid());

        //根据角色id获取角色信息
        Role role1;
        for (Integer roleId : roleIds) {
            role1 = roleMapper.selectRoleFindById(roleId);
            RoleCommon roleCommon = new RoleCommon();
            BeanUtils.copyProperties(roleCommon, role1);
            roleBeans.add(roleCommon);
        }

        //当账号是主账号德时候，不经过角色，直接拥有所属平台的所有菜单权限
        List<Menu> all;
        if (user.getParentId() == null) {
            all = menuMapper.findAll(new Menu() {{
                setPlatformType(user.getPlatformType());
                setDelFlag(0);
            }});
        } else {   //当账号不是主账号的时候，经过角色
            all = menuMapper.findAll(new Menu() {{
                setPlatformType(user.getPlatformType());
                setDelFlag(0);
                setUserId(user.getUserid());
                // setRoleIds(roleIds);
            }});
        }
        if (all == null || all.size() == 0) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100436);
        all.forEach(menu -> menuBeans.add(new MenuCommon(menu.getId(), menu.getPlatformType(), menu.getParentId(),
                menu.getParentIds(), menu.getName(), menu.getSort(), menu.getHref(), menu.getIcon(), menu.getLevel(),
                menu.getVshow(), menu.getPermission(), menu.getRemarks(), menu.getCreateDate(), menu.getUpdateDate(),
                menu.getDelFlag())));

        this.setMenuAndUrls(userAll, menuBeans);    //设置权限和菜单
        UserCommon userCommon = new UserCommon();//封装用户信息
        BeanUtils.copyProperties(userCommon, user);
        userAll.setUser(userCommon);
        userAll.setRoles(roleBeans);

        //判断当前用户是什么级别的账号，如果是子账号则找到他的父级账户
        userAll.setParentUser(new UserCommon());
        if ( userAll.getUser().getParentId() != null){//说明是子账户,则获取他的父账户信息
            User parentUser = sellerMapper.getParentUserBySubUserParentIdSeller(userAll.getUser().getParentId());
            UserCommon userCommonParent = new UserCommon();
            BeanUtils.copyProperties(userCommonParent,parentUser);
            userAll.setParentUser(userCommon);
            map.put("parentUsers",parentUser);
        }

        //添加会话
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestType = request.getHeader("user-agent");
        String token = null;
        String str = getUserMD5(userAll);
        if ( requestType.indexOf("Android") != -1 ){//安卓App情况下
            token = getPcUserToken(userAll);
            redisUtils.removePattern(str+"APP"+"*");
        }else{//在pc端情况下
            token = getPcUserToken(userAll);
            redisUtils.removePattern(str+"PC"+"*");
        }
        //将激活状态下用户生成token
        redisUtils.set(UserConstants.REDIS_USER_KEY_fix+token, userAll,UserConstants.REDIS_USER_TOKEN_TIMEOUT);
        response.setHeader("Access-Control-Expose-Headers","token");
        response.setHeader(UserConstants.REQUEST_HEADER_NAME, token);
        userAll.setUrls(null);
        map.put("token", token);
        map.put("user", userAll);
        return map;
    }

    /**
     * 卖家登录===》 根据子账户的parentId找到他的父账户
     * @param parentId
     * @return
     */
    @Override
    public User getSellerParentUserBySubUserParentId(Integer parentId) {
        User user = sellerMapper.getSellerParentUserBySubUserParentId(parentId);
        return user;
    }

    /**
     * 卖家个人中心
     * @param userId
     * @return
     */
    @Override
    public UserAndCompanyAndSalesReturnBean getSellerPersonalCenter(Integer userId) {
        UserAll userAll = getLoginUserInformationByToken.getUserInfo();
        if (userAll == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406,"未登录或登录超时请重新登录");

        UserAndCompanyAndSalesReturnBean userAndCompanyAndSalesReturnBean = new UserAndCompanyAndSalesReturnBean();
        User user = getUserById(userId);
        if (user == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100434);
        user.setPassword("");

        //获取企业信息
        if ( user != null ){
            userAndCompanyAndSalesReturnBean.setUser(user);
            Companyinfo companyinfo = companyinfoMapper.getSellerCompanyUserCompanyInfo(user.getUserid());
            if (companyinfo == null) userAndCompanyAndSalesReturnBean.setCompanyinfo(new Companyinfo());
            else userAndCompanyAndSalesReturnBean.setCompanyinfo(companyinfo);
            userAndCompanyAndSalesReturnBean.setSalesreturn(new Salesreturn());
        }

        //响应结果
        return userAndCompanyAndSalesReturnBean;
    }

    /**
     * 忘记密码
     * @param phone
     * @param email
     * @param platformType
     * @param newPassword
     * @return
     */
    @Override
    public Integer getSellerUserByPhoneAndPlatformType(String phone, String email, Integer platformType, String newPassword) {
        Map<String, Object> map = new HashMap<String, Object>();//封装条件数据
        map.put("phone", phone);
        map.put("platformType", platformType);
        map.put("email", email);
        User user = sellerMapper.getSellerUserByPhoneAndPlatformType(map);//根据手机号码或者邮箱及平台找到对应的用户数据
        if (user == null)  throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406.getCode(), "找不到要修改密码的用户");
        if (platformType.intValue() == 1) {
            if (user.getPlatformType().intValue() != 1)  throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100425.getCode(), "您不是本平台卖家用户");
        }
        //将新密码加密
        String newPasswordMd5 = MD5.md5Password(newPassword);
        if ( user.getPassword().equals(newPasswordMd5) ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100431.getCode(),"新密码与旧密码一致");
        map.put("newPasswordMd5", newPasswordMd5);
        map.put("update", new Date());
        //替换对应用户中的密码
        Integer result = sellerMapper.sellerUserpasswordUpadate(map);
        if (result == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100431);
        return result;
    }

    /**
     * 修改密码
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @Override
    public Integer sellerPasswordUpdate(Integer userId, String oldPassword, String newPassword) {
        UserAll userAll = getLoginUserInformationByToken.getUserInfo();
        User user = sellerMapper.selectByPrimaryKey(userId);
        Map<String, Object> map = new HashMap<String, Object>();
        Integer result = null;
        if (user != null) {
            //对比密码
            if (user.getPassword().equals(MD5.md5Password(oldPassword))) {
                newPassword = MD5.md5Password(newPassword);
                //对比新密码和旧密码是否一致
                if ( MD5.md5Password(oldPassword).equals(newPassword) ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"新密码与旧密码一致");
                map.put("userId", userId);
                map.put("newPassword", newPassword);
                map.put("update", new Date());
                map.put("remarks",StringUtils.isNotBlank(userAll.getUser().getUsername()) ? userAll.getUser().getUsername() : "");
                result = sellerMapper.sellerPasswordUpadate(map);
            } else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "原密码错误，请重新输入");
            }
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "未找到该用户，请重试");
        }
        //返回结果
        return result;
    }

    /**
     * 判断当前卖家信息是否已经添加
     * @param platformTypes
     * @param userId
     * @return
     */
    @Override
    public Integer isSellerInfo(Integer platformTypes, Integer userId) {
        Integer result = null;
        if (platformTypes != null && userId != null)
            result = sellerMapper.isSellerInfo(platformTypes,userId);
        return result;
    }

    /**
     * 添加卖家信息
     * @param sellerInfo
     * @return
     */
    @Override
    public Integer insertSellerInfo(SellerInfo sellerInfo) {
        Integer result = null;
        if (sellerInfo != null) result = sellerMapper.insertSellerInfo(sellerInfo);
        return result;
    }

    /**
     * 判断该用户是否已经激活
     * @param userId
     * @return
     */
    @Override
    public Integer isSellerUserDelfag(Integer userId) {
        Integer result = null;
        if (userId != null) result = sellerMapper.isSellerUserDelfag(userId);
        return result;
    }

    /**
     * 卖家手机号码绑定
     * @param userId
     * @param phone
     * @return
     */
    @Override
    public Integer sellerPhoneBinding(Integer userId, String phone) {
        UserAll userAll = getLoginUserInformationByToken.getUserInfo();
        Map<String,Object> map = new HashMap<>();
        Integer result = null;
        if (userId != null && phone != null){
            map.put("userId",userId);
            map.put("phone",phone);
            map.put("updateDate",new Date());
            map.put("remarks",StringUtils.isNotBlank(userAll.getUser().getUsername()) ? userAll.getUser().getUsername() : null);
            result = sellerMapper.sellerPhoneBinding(map);
        }
        if (result == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"卖家手机号码绑定失败，请重试");

        //将手机绑定开关改为true
        if ( result != null && result.intValue() != 0 ){
            map.clear();
            map.put("userId",userId);
            map.put("loginPhone",true);
            Integer loginBoolean = sellerMapper.isLoginBangdingPhoneTrue(map);
        }

        //修改卖家手机号码财务信息
        RemoteUtil.invoke(userFinanceInitialization.sellerUpdate(userId,null,null,null,null,
                StringUtils.isNotBlank(phone) ? phone : null, null,null));
//        Object obj = RemoteUtil.getObject();
//        boolean sellerFinanceUpdateResult = false;
//        if (obj instanceof Boolean ) sellerFinanceUpdateResult = (boolean) obj;
        return result;
    }

    /**
     * 获取是否需要绑定手机信息
     * @param userId
     * @return
     */
    @Override
    public Boolean getIsPhoneBinding(Integer userId) {
        Boolean result = sellerMapper.getIsPhoneBinding(userId);
        return Boolean.valueOf(result);
    }

    /**
     * 根据手机和邮箱找到用户
     * @param phone
     * @param email
     * @param username
     * @param platformType
     * @return
     */
    public User getUserByPhoneOrEmail(String phone, String email, String username,Integer platformType) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("phone", phone);
        map.put("email", email);
        map.put("username", username);
        map.put("platformType",platformType);
        return sellerMapper.getSellerUserByPhoneOrEmail(map);
    }

    /**
     * 根据id找到用户
     * @param id
     * @return
     */
    public User getUserById(Integer id) {
        return sellerMapper.selectByPrimaryKey(id);
    }

    /**
     * 设置用户对象的权限列表 和 菜单结构
     *
     * @param userAll   用户对象
     * @param menuBeans 用户所有的菜单的hash列表
     */
    private void setMenuAndUrls(UserAll userAll, HashSet<MenuCommon> menuBeans) {
        userAll.setMenus(new ArrayList<>());
        userAll.setUrls(new HashSet<>());
        menuBeans.forEach(m -> {
            if (StringUtils.isNotBlank(m.getHref())) {
                for (String s : m.getHref().split(",")) {
                    userAll.getUrls().add(s.trim());
                }
            }
            if (m.getParentId() == 0)
                userAll.getMenus().add(m);
            menuBeans.forEach(mm -> {
                if (mm.getParentId().equals(m.getId())) {
                    if (m.getChildren() == null) {
                        m.setChildren(new ArrayList<>());
                    }
                    m.getChildren().add(mm);
                }
            });
        });
    }

    /**
     * 判断当前手机是否已经绑定
     */
	@Override
	public Integer isPhoneSellerUser(String phone,Integer userId) {
	    Map<String,Object> map = new HashMap<>();
	    map.put("phone",phone);
	    map.put("userId",userId);
		Integer result = null;
		if (StringUtils.isNotBlank(phone)) result = sellerMapper.isPhoneSellerUser(map);
		return result;
	}

}
