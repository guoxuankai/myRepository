package com.rondaful.cloud.user.service.impl;

import com.rondaful.cloud.common.entity.user.MenuCommon;
import com.rondaful.cloud.common.entity.user.RoleCommon;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.user.constants.UserConstants;
import com.rondaful.cloud.user.controller.utils.ControllerUtil;
import com.rondaful.cloud.user.entity.*;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.mapper.*;
import com.rondaful.cloud.user.service.SupplierService;
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

/**
 * 供应商业务层
 */
@Service("supplierService")
public class SupplierServiceImpl extends ControllerUtil implements SupplierService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private CompanyinfoMapper companyinfoMapper;

    @Autowired
    private SalesreturnMapper salesreturnMapper;

    private final Logger logger = LoggerFactory.getLogger(SupplierServiceImpl.class);

    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    @Autowired
    private SupplierMapper supplierMapper;


    /**
     * 供应商登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public Map<String,Object> supplierUserLogin(String username, String password, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
        Map<String,Object> map = new HashMap<>();
        UserAll userAll = new UserAll();//封装登录用户的基础信息，角色信息，权限信息
        List<RoleCommon> roleBeans = new ArrayList<>();//当前登录用户的角色信息
        HashSet<MenuCommon> menuBeans = new HashSet<>();//当前登录用户的权限信息
        User user = getUserByPhoneOrEmail(username, null, null, UserConstants.SUPPLIERPLATFORM);//根据此用户名查询用户是否存在
        if (user == null)  throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100433.getCode(), "当前用户不存在，请注册或者重新登录");
        else if (!user.getUsername().equals(username)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100001);
        else if (user.getStatus().intValue() == 0) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100425.getCode(),"当前用户已停用或者已注销，请联系管理员重新分配");
        else if (user.getPlatformType().intValue() != 0) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100425.getCode(), "您不是本平台供应商用户，请重新登录");

        //判断密码是否匹配
        String handlerPassword = password;
        if ( user.getParentId() == null ) handlerPassword = MD5.md5Password(password);//用户已经存在，进行密码加密
        if (!handlerPassword.equals(user.getPassword())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100001);

        //根据userId查询到对应的角色id  根据角色id获取角色信息
        List<Integer> roleIds = userRoleMapper.getRoleIdsByUserId(user.getUserid());
        Role role1;
        for (Integer roleId : roleIds) {
            role1 = roleMapper.selectRoleFindById(roleId);
            RoleCommon roleCommon = new RoleCommon();
            BeanUtils.copyProperties(roleCommon,role1);
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
                menu.getParentIds(), menu.getName(), menu.getSort(), menu.getHref(), menu.getIcon(),menu.getLevel(),
                menu.getVshow(), menu.getPermission(), menu.getRemarks(),menu.getCreateDate(), menu.getUpdateDate(),
                menu.getDelFlag())));
        this.setMenuAndUrls(userAll, menuBeans);    //设置权限和菜单
        UserCommon userCommon = new UserCommon();   //封装用户信息
        BeanUtils.copyProperties(userCommon,user);
        userAll.setUser(userCommon);
        userAll.setRoles(roleBeans);

        //判断当前用户是什么级别的账号，如果是子账号则找到他的父级账户
        userAll.setParentUser(new UserCommon());
        if ( userAll.getUser().getParentId() != null){//说明是子账户,则获取他的父账户信息
            User parentUser = supplierMapper.getSupplierParentUserBySubUserParentId(userAll.getUser().getParentId());
            UserCommon userCommonParent = new UserCommon();
            BeanUtils.copyProperties(userCommonParent,parentUser);
            userAll.setParentUser(userCommon);
            map.put("parentUsers",parentUser);
        }

        //供应商会话机制
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestType = request.getHeader("user-agent");
        String token = null;
        String str = getUserMD5(userAll);
        if ( requestType.indexOf("Android") != -1 ){//安卓App情况下
            token = getAppUserToken(userAll);
            redisUtils.removePattern(str+"APP"+"*");
        }else{//在pc端情况下
            token = getPcUserToken(userAll);
            redisUtils.removePattern(str+"PC"+"*");
        }
        redisUtils.set(UserConstants.REDIS_USER_KEY_fix + token, userAll, UserConstants.REDIS_USER_TOKEN_TIMEOUT);
        response.setHeader("Access-Control-Expose-Headers", "token");
        response.setHeader(UserConstants.REQUEST_HEADER_NAME, token);
        userAll.setUrls(null);
        map.put("token", token);
        map.put("user", userAll);
        return map;
    }

    /**
     * 个人中心
     * @param userId
     * @return
     */
    @Override
    public UserAndCompanyAndSalesReturnBean getSupplierPersonalCenter(Integer userId) {
        UserAll userAll = getLoginUserInformationByToken.getUserInfo();
        if (userAll == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406,"未登录或登录超时请重新登录");
        UserAndCompanyAndSalesReturnBean userAndCompanyAndSalesReturnBean = new UserAndCompanyAndSalesReturnBean();
        User user = null;
        if (userId != null)  user = getSupplierUserById(userId);
        user.setPassword("");
        if ( user != null ){//获取企业信息
            userAndCompanyAndSalesReturnBean.setUser(user);
            Companyinfo companyinfo = companyinfoMapper.getSupplierCompanyUserCompanyInfo(user.getUserid());
            if (companyinfo != null) userAndCompanyAndSalesReturnBean.setCompanyinfo(companyinfo);
            else userAndCompanyAndSalesReturnBean.setCompanyinfo(new Companyinfo());//等于null的话返回一个空对象
            Salesreturn salesreturn = salesreturnMapper.getSupplierSalesReturn(user.getUserid()); //获取退货信息
            if (salesreturn != null)  userAndCompanyAndSalesReturnBean.setSalesreturn(salesreturn);
            else userAndCompanyAndSalesReturnBean.setSalesreturn(new Salesreturn());//等于null的话返回一个空对象
        }
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
    public Integer getSupplierUserByPhoneAndPlatformType(String phone, String email, Integer platformType, String newPassword) {
        Map<String, Object> map = new HashMap<String, Object>();//封装条件数据
        map.put("phone", phone);
        map.put("platformType", platformType);
        map.put("email", email);
        User user = supplierMapper.getSupplierUserByPhoneAndPlatformType(map);//根据手机号码或者邮箱及平台找到对应的用户数据
        if (user == null)  throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406.getCode(), "找不到要修改密码的用户");
        if (platformType.intValue() == 1) {
            if (user.getPlatformType().intValue() != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100425.getCode(), "您不是本平台卖家用户");
        }
        //将新密码加密
        String newPasswordMd5 = MD5.md5Password(newPassword);
        if ( user.getPassword().equals(newPasswordMd5) ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100431.getCode(),"新密码与旧密码一致");
        map.put("newPasswordMd5", newPasswordMd5);
        map.put("update", new Date());
        //替换对应用户中的密码
        Integer result = supplierMapper.supplierUserPasswordUpadate(map);
        if (result == null)  throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100431);
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
    public Integer supplierPasswordUpdate(Integer userId, String oldPassword, String newPassword) {
        UserAll userAll = getLoginUserInformationByToken.getUserInfo();
        User user = supplierMapper.selectSupplierByPrimaryKey(userId);
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
                result = supplierMapper.supplierPasswordUpadate(map);
            } else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "原密码错误，请重新输入");
            }
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "未找到该用户，请重试");
        }
        return result;
    }

    /**
     * 判断该账户有无进行财务初始化
     * @param userId
     * @return
     */
    @Override
    public String isInitSupplier(Integer userId) {
        String result = null;
        if ( userId != null ) result = supplierMapper.isInitSupplier(userId);
        return result;
    }

    /**
     * 将账户改为已经初始化财务账号
     * @param userId
     * @param insertResult
     * @return
     */
    @Override
    public Integer supplierInitResultOk(Integer userId, String insertResult) {
        Integer result = null;
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("userId",userId);
        map.put("insertResult",insertResult);
        if ( userId != null && insertResult != null ) result = supplierMapper.supplierInitResultOk(map);
        return result;
    }

    /**
     * 判断该供应商用户是否已经激活
     * @param userId
     * @return
     */
    @Override
    public Integer isSupplierUserDelfag(Integer userId) {
        Integer result = null;
        if (userId != null) result = supplierMapper.isSupplierUserDelfag(userId);
        return result;
    }

    /************根据账号id查询用户信息************/
    public User getSupplierUserById(Integer id) {
        return supplierMapper.getSupplierUserById(id);
    }

    /**
     * 根据子账户的parentId找到他的父账户
     * @param parentId
     * @return
     */
    @Override
    public User getSupplierParentUserBySubUserParentId(Integer parentId) {
        User user = supplierMapper.getSupplierParentUserBySubUserParentId(parentId);
        return user;
    }

    /**
     * 根据手机和邮箱找到用户
     * @param username
     * @param phone
     * @param email
     * @return
     */
    public User getUserByPhoneOrEmail(String username, String phone, String email,Integer platformType) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("phone", phone);
        map.put("email", email);
        map.put("username", username);
        map.put("platformType",platformType);
        return supplierMapper.getSupplierUserByPhoneOrEmail(map);
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



}
