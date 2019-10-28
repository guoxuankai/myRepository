package com.rondaful.cloud.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.constant.ConstantAli;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.AliSMSUtils;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.user.constants.UserConstants;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.mapper.SellerMapper;
import com.rondaful.cloud.user.mapper.SupplierMapper;
import com.rondaful.cloud.user.service.*;
import com.rondaful.cloud.user.utils.CaptchaUtil;
import com.rondaful.cloud.user.utils.SendEmailUtil;
import com.rondaful.cloud.user.utils.ValidatorUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSON;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@Api("公共服务")
public class PublicCommomController {

    @Autowired
    private RedisUtils redisUtils;

    @Resource
    private AliSMSUtils aliSMSUtils;

    @Autowired
    private PublicCommomService publicCommomService;

    @Autowired
    private ManageService manageService;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SupplierMapper supplierMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    private Logger logger = LoggerFactory.getLogger(PublicCommomController.class);

    @AspectContrLog(descrption = "绑定手机===》判断该账户的手机是否为原手机",actionType = SysLogActionType.QUERY)
    @ApiOperation(value ="绑定手机===》判断该账户的手机是否为原手机")
    @RequestMapping(value="/getBingdingUserPhone",method= RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam( name = "userId", value = "供应链公司id", dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam( name = "platformType", value = "平台信息 0供应商 1卖家 2管理后台 9供应链公司", dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam( name = "phone", value = "手机号码", dataType = "string",paramType = "query",required = true)})
    public void getBingdingUserPhone(String userId,String platformType,String phone){
        try {
            String isPhone = publicCommomService.getBingdingUserPhone(Integer.parseInt(userId),Integer.parseInt(platformType));
            if (isPhone != null)
                if ( phone.trim().equals(isPhone.trim()) ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"请输入原手机号码");
        } catch (Exception e) {
            logger.error("判断该账户的手机是否为原手机失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"系统异常");
        }
    }

    @AspectContrLog(descrption = "绑定手机号码",actionType = SysLogActionType.UDPATE)
    @ApiOperation(value ="绑定手机号码")
    @RequestMapping(value="/bingdingUserPhone",method= RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam( name = "userId", value = "账户id", dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam( name = "platformType", value = "平台信息 0供应商 1卖家 2管理后台 9供应链公司", dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam( name = "phone", value = "手机号码", dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam( name = "code", value = "验证码", dataType = "string",paramType = "query",required = true)})
    public void bingdingUserPhone(String userId, String platformType, String phone,String code){
        if (!ValidatorUtil.isMobile(phone)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"手机格式错误");
        }
        //手机验证模式下
        if (!redisUtils.exists(phone)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100423);
        }
        String userRandomCode = (String)redisUtils.get(phone);
        //验证码不匹配
        if (!userRandomCode.equals(code)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100424);
        }
    }

    @AspectContrLog(descrption = "忘记密码",actionType = SysLogActionType.UDPATE)
    @ApiOperation(value ="忘记密码")
    @RequestMapping(value = "/api/userPasswordUpdate", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "codeType", value = "请求方式：1手机 2邮箱", dataType = "Integer",paramType = "query",required = true),
            @ApiImplicitParam(name = "phone", value = "手机号码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "platformType", value = "对应平台：1卖家 0供应商 2后台管理", dataType = "Integer",paramType = "query",required = true),
            @ApiImplicitParam(name = "newPassword", value = "新密码", dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam(name = "randomCode", value = "验证码", dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam(name = "email", value = "邮箱", dataType = "string",paramType = "query")
    })
    public void userPasswordUpdate(Integer codeType, String phone, Integer platformType,String newPassword,String randomCode,String email) {
        try {
           // UserAll userAll = getLoginUserInformationByToken.getUserInfo();
            if (phone != null) phone.trim();
            if (newPassword != null)newPassword.trim();
            if (randomCode != null)randomCode.trim();
            if (email != null)email.trim();
            boolean result01 = ValidatorUtil.isPassword(newPassword);//验证密码格式
            if ( !result01 )throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"密码格式不正确，请重新输入");
            if ( codeType == 1 ) {//判断请求的方式为手机还是邮箱//验证码校验
                if ( !redisUtils.exists(phone) )throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100423);
                String userRandomCode = (String)redisUtils.get(phone);
                if ( userRandomCode == null )  throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100423);
                else if ( !userRandomCode.equals(randomCode) ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100424);
            } else if (codeType == 2) {//验证码校验
                if ( !redisUtils.exists(email) )throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100423);
                String userRandomCode = (String)redisUtils.get(email);
                if ( userRandomCode == null ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100423);
                else if ( !userRandomCode.equals(randomCode) ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100424);
            }
            Integer result = null;//进行密码修改并反馈结构
            if (platformType.intValue() == 0) result = supplierService.getSupplierUserByPhoneAndPlatformType(phone,email, platformType, newPassword);
            else if (platformType.intValue() == 1) result = sellerService.getSellerUserByPhoneAndPlatformType(phone,email, platformType, newPassword);
            else result = manageService.getManageUserByPhoneAndPlatformType(phone,email, platformType, newPassword);
        } catch (GlobalException e){
            logger.error("忘记密码修改失败",e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        } catch (Exception e){
            logger.error("忘记密码修改失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "修改密码",actionType = SysLogActionType.UDPATE)
    @ApiOperation(value ="修改密码")
    @PostMapping(value = "/passwordUpdate")
    @ApiImplicitParams({@ApiImplicitParam(name = "userId", value = "当前用户id", dataType = "Integer",paramType = "query",required = true),
            @ApiImplicitParam(name = "oldPassword", value = "原密码", dataType = "string", paramType = "query",required = true),
            @ApiImplicitParam(name = "newPassword", value = "新密码", dataType = "string",paramType = "query",required = true)})
    public void PasswordUpdate(Integer userId ,String oldPassword, String newPassword) {
        try {
            if (oldPassword != null)oldPassword.trim();
            if (newPassword != null) newPassword.trim();
            boolean result02 = ValidatorUtil.isPassword(newPassword); //验证密码格式
            if (!result02)  throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"密码格式不正确，请重新输入");
            UserAll userAll = getLoginUserInformationByToken.getUserInfo();
            //进行密码修改并反馈结构
            Integer result = null;
            if ( userAll.getUser().getPlatformType().intValue() == 0 ) result = supplierService.supplierPasswordUpdate(userId,oldPassword,newPassword);
            else if ( userAll.getUser().getPlatformType().intValue() == 1 ) result = sellerService.sellerPasswordUpdate(userId,oldPassword,newPassword);
            else result = manageService.managePasswordUpdate(userId,oldPassword,newPassword);
        } catch (GlobalException e) {
            logger.error("密码修改失败",e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        }catch (Exception e){
            logger.error("密码修改失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "退出登录",actionType = SysLogActionType.DELETE)
    @ApiOperation(value ="退出登录")
    @PostMapping(value = "/loginOut")
    public void loginOut() {
        try {
            redisUtils.removePattern(getLoginUserInformationByToken.getToken());
        }catch (GlobalException e){
            logger.error("退出登录失败",e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        }
    }

    /************************图片验证码生成************************/
    @AspectContrLog(descrption = "生成验证码",actionType = SysLogActionType.QUERY)
    @ApiOperation(value ="图片验证码生成")
    @RequestMapping(value = "/api/getImageCode", method = RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam(name = "username", value = "用户名", dataType = "string",paramType = "query",required = true)})
    public void getImageCode(HttpServletResponse response, String username) {
        try {
            response.setHeader("Expires", "-1");// 通知浏览器不要缓存
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "-1");
            CaptchaUtil util = CaptchaUtil.Instance();
            String code = util.getString();// 将验证码输入到redis中，用来验证
            logger.debug("验证码===》"+code);
            redisUtils.set(username.trim(),code.trim(),120L);
            ImageIO.write(util.getImage(), "jpg", response.getOutputStream());// 输出打web页面
        } catch (GlobalException e) {
            logger.error("图片验证码生成失败",e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        } catch (Exception e){
            logger.error("图片验证码生成失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "邮箱验证码生成",actionType = SysLogActionType.QUERY)
    @ApiOperation(value ="邮箱验证码生成")
    @RequestMapping(value = "/api/getEmailCode", method = RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam(name = "codeType", value = "请求方式：1暂无 2卖家忘记密码 3供应商忘记密码",dataType = "string",
            paramType = "query",required = true),
            @ApiImplicitParam(name = "email", value = "邮箱", dataType = "string",paramType = "query",required = true)})
    public void getEmailCode(Integer codeType, String email, HttpServletRequest request){
        try {
            if (StringUtils.isNotBlank(email)) email.trim();
            if ( !ValidatorUtil.isEmail(email) ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
            if (codeType.intValue() != 1){//验证当前需要验证的手机是否在系统用户
                Integer isUsername = null;
                if ( codeType.intValue() == 2 ) isUsername = sellerMapper.isSellerUsernameByPhoneAndEmail(null,email);
                else isUsername = supplierMapper.isSupplierUsernameByPhoneAndEmail(null,email);
                if ( isUsername.intValue() == 0 )throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"当前邮箱未在被平台注册，请重试");
            }
            String randomCode = RandomStringUtils.randomNumeric(6);//生成短信验证码
            redisUtils.set(email, randomCode, 120L);//将短信验证码保存到Redis中
            //调用SMS服务发送短信
            String namePerson = email;//收件人
            String accounts = null;//邮件主题
            String text = null;//邮件内容
            String files = null;//"D:/RondafulProject/rondaful-user-service/src/main/resources/swagger.properties";  //附件路径

            String header = request.getHeader("i18n");
            if (StringUtils.isNotBlank(header)){
                //在传了国际化请求的header的时候
                if ( UserConstants.SELLER_FORGET_PASSWORD_TYPE.intValue() == codeType.intValue() ) {
                    accounts = "The seller changes the password for verification";//主题
                    text = "[Brandslink] Dear users, hello, you are using the forgotten password recovery operation, the verification code is:"+randomCode+", Effective within 2 minutes, do not leak!";//内容
                } else if ( UserConstants.SUPPLIER_FORGET_PASSWORD_TYPE.intValue() == codeType.intValue() ) {
                    accounts = "PLL supplier modified password verification";//主题
                    text = "[Brandslink] Dear users, hello, you are using the forgotten password recovery operation, the verification code is:"+randomCode+", Effective within 2 minutes, do not leak!";//内容
                }
            }else{
                if ( UserConstants.SELLER_FORGET_PASSWORD_TYPE.intValue() == codeType.intValue() ) {
                    accounts = "品联卖家修改密码验证";//主题
                    text = "【品连优选】 尊敬的用户，您好，您正在使用忘记密码找回操作，本次验证码为："+randomCode+", 2分钟内有效，请勿泄露!";//内容
                } else if ( UserConstants.SUPPLIER_FORGET_PASSWORD_TYPE.intValue() == codeType.intValue() ) {
                    accounts = "品联供应商修改密码验证";//主题
                    text = "【品连优选】 尊敬的用户，您好，您正在使用忘记密码找回操作，本次验证码为："+randomCode+", 2分钟内有效，请勿泄露!";//内容
                }
            }
            SendEmailUtil pool = new SendEmailUtil(namePerson, accounts, text);
            pool.send();
        } catch (GlobalException e) {
            logger.error("发送邮箱验证码失败",e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        } catch (Exception e){
            logger.error("发送邮箱验证码失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

}
