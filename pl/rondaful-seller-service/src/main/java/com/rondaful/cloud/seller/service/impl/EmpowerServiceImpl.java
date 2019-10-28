package com.rondaful.cloud.seller.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ebay.sdk.*;
import com.ebay.sdk.call.ConfirmIdentityCall;
import com.ebay.sdk.call.FetchTokenCall;
import com.ebay.sdk.call.GetOrdersCall;
import com.ebay.sdk.call.GetSessionIDCall;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.rabbitmq.MessageSender;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.config.AliexpressConfig;
import com.rondaful.cloud.seller.config.EbayConfig;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.EmpowerLog;
import com.rondaful.cloud.seller.mapper.EmpowerLogMapper;
import com.rondaful.cloud.seller.mapper.EmpowerMapper;
import com.rondaful.cloud.seller.remote.RemoteOrderRuleService;
import com.rondaful.cloud.seller.remote.RemoteUserService;
import com.rondaful.cloud.seller.service.IEmpowerService;
import com.rondaful.cloud.seller.vo.EmpowerSearchVO;
import com.rondaful.cloud.seller.vo.MessageNoticeModel;
import com.taobao.api.internal.util.WebUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 *
 * @author chenhan
 *
 */

@Service
public class EmpowerServiceImpl implements IEmpowerService {

    private final Logger logger = LoggerFactory.getLogger(EmpowerServiceImpl.class);
    @Autowired
    private EmpowerMapper empowerMapper;
    @Autowired
    private EmpowerLogMapper empowerLogMapper;

    @Autowired
    private RemoteOrderRuleService remoteOrderRuleService;
    @Autowired
    private GetLoginUserInformationByToken getUserInfo;
    @Autowired
    private EbayConfig ebayConfig;
    @Autowired
    private AliexpressConfig aliexpressConfig;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private MessageSender messageSender;

    @Override
    public int updateEmpowerByStatus(Integer empowerId, Integer status){
        if(empowerId==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");
        }
        if(status==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "状态不能为空");
        }
        Empower empower=new Empower();
        empower.setEmpowerId(empowerId);
        empower.setStatus(status);
        empowerMapper.updateByPrimaryKeySelective(empower);
        String operation="";
        if(status==1){
            operation="启用授权";
        }else if(status==3){
            operation="停用授权";
        }else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "状态错误");
        }
        UserDTO userDTO=getUserInfo.getUserDTO();//取出数据
        this.insertEmpowerLog(empower.getEmpowerId(), userDTO.getLoginName(), operation);
        return 1;
    }

    @Override
    public int insertEmpower(Empower empower){
        if(empower!=null){
            UserDTO userDTO = getUserInfo.getUserDTO();
            //验证
            this.checkEmpower(empower);

            if(empower.getPlatform()==2){
                //站点多个
                List<String> webNames = new ArrayList<>(Arrays.asList(empower.getWebName().split(",")));
//                //验证站点
//                List<Empower> checkEmpowerList = empowerMapper.getEmpowerByWebName(webNames,empower.getThirdPartyName(),empower.getPinlianId(),1);
//                if(checkEmpowerList!=null && checkEmpowerList.size()>0){
//                    throw new GlobalException("6000", "检测到此店铺已在其他品连账号("+checkEmpowerList.get(0).getPinlianAccount()+")中已经有授权记录，无法完成授权");
//                }
                //转换账号
                Map<String, MarketplaceId> map = MarketplaceIdList.createMarketplaceForKeyId();
                List<String> accounts = new ArrayList<>();
                for (String str:webNames){
                    MarketplaceId marketplaceId = map.get(str);
                    if(marketplaceId!=null){
                        accounts.add(empower.getAccount()+marketplaceId.getCountryCode().toLowerCase());
                    }else{
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "亚马逊站点信息不能为空");
                    }
                }
                //验证账号
                List<Empower> checkAccount = empowerMapper.checkEmpowerAccounts(accounts,empower.getPlatform(),null,null);
                if (checkAccount != null && checkAccount.size() > 0) {
                    for (Empower cwEmpower : checkAccount) {
                        if (!cwEmpower.getPinlianId().equals(empower.getPinlianId())) {
                            throw new GlobalException("6000", "检测到此店铺已在其他品连账号(" +cwEmpower.getPinlianAccount() + ")中已经有授权记录，无法完成授权");
                        } else {
                            throw new GlobalException("6000", "检测到此店铺在此品连账号中已经有授权记录(" + cwEmpower.getAccount() + ")，无法完成授权");
                        }
                    }
                }
                List<Empower> checkWebName = empowerMapper.getEmpowerByWebName(webNames,empower.getThirdPartyName(),null,null);
                List<Empower> checkEmpowerThirdPartyNameWebName = Lists.newArrayList();
                if(checkWebName!=null) {
                    for (Empower cwEmpower : checkWebName) {
                        if (!cwEmpower.getPinlianId().equals(empower.getPinlianId())) {
                            throw new GlobalException("6000", "检测到此店铺已在其他品连账号(" + cwEmpower.getPinlianAccount() + ")中已经有授权记录，无法完成授权");
                        } else {
                            checkEmpowerThirdPartyNameWebName.add(cwEmpower);
                        }
                    }
                }
                //验证自己站点
                if(!"10".equals(empower.getDataType())){//确认授权
                    if (checkEmpowerThirdPartyNameWebName != null && checkEmpowerThirdPartyNameWebName.size() > 0) {
                        StringBuffer straccountName =new StringBuffer();
                        boolean bool = false;
                        for(Empower accountName:checkEmpowerThirdPartyNameWebName){
                            if(bool){
                                straccountName.append(","+accountName.getAccount());
                            }else {
                                bool = true;
                                straccountName.append(accountName.getAccount());
                            }
                        }
                        throw new GlobalException("6001", "检测到此店铺在此品连账号中已经有授权记录(" + straccountName + ")。您可以更新授权信息，也可以取消授权(原授权记录数据不变)。");
                    }
                }



                //远程调用接口验证 账号是否正确
                String checkAmazonTokenIsValid = remoteOrderRuleService.checkAmazonTokenIsValid(empower.getThirdPartyName(), webNames.get(0), empower.getToken());
                if(checkAmazonTokenIsValid==null){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权失败");
                }
                String returnRemoteResultDataString = Utils.returnRemoteResultDataString(checkAmazonTokenIsValid, "转换失败");
                String parse = JSONObject.parse(returnRemoteResultDataString).toString();
                if ("1".equals(parse)) {//验证成功
                    //授权结束时间按一年时间
                    Date date = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(calendar.DAY_OF_YEAR, 365);
                    date = calendar.getTime();
                    empower.setEndTime(date);
                    String account = empower.getAccount();
                    for (String str:webNames){
                        MarketplaceId marketplaceId = map.get(str);
                        if(marketplaceId!=null){
                            empower.setWebName(marketplaceId.getMarketplaceId());
                            boolean checkAccountBool = true;
                            if(checkEmpowerThirdPartyNameWebName!=null && checkEmpowerThirdPartyNameWebName.size()>0){
                                for(Empower ce:checkEmpowerThirdPartyNameWebName){
                                    if(ce.getWebName().equals(empower.getWebName())){
                                        checkAccountBool=false;
                                        Empower updateEmpower = new Empower();
                                        updateEmpower.setEmpowerId(ce.getEmpowerId());
                                        updateEmpower.setToken(empower.getToken()); //更新token
                                        updateEmpower.setEndTime(empower.getEndTime());//更新结束时间
                                        updateEmpower.setStatus(1);
                                        empowerMapper.updateByPrimaryKeySelective(updateEmpower);
                                        this.insertEmpowerLog(ce.getEmpowerId(), empower.getParentAccount(), "重新授权");
                                        break;
                                    }
                                }
                            }
                            if(checkAccountBool) {
                                empower.setAccount(account+marketplaceId.getCountryCode().toLowerCase());
                                empower.setEmpowerId(null);
                                empowerMapper.insertSelective(empower);
                                this.insertEmpowerLog(empower.getEmpowerId(), empower.getParentAccount(), "添加授权");
                                //子账号的时候添加店铺授权数据权限
                                if (!userDTO.getManage()) {
                                    remoteUserService.bindStore(userDTO.getUserId(),empower.getEmpowerId());
                                }
                            }
                        }
                    }
                }else{
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权失败");
                }
            }else if(empower.getPlatform()==1){
                String sessionID = (String)redisUtils.get("sessionID"+getUserInfo.getUserInfo().getUser().getUserid().toString());
                if(StringUtils.isBlank(sessionID)){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权等待时间过期");
                }
                String userId = "";//token的userid
                String token ="";
                Date expirationTime=null;//授权结束时间
                try {
                    userId = this.getEabyUserId(sessionID);
                    Map<String ,Object> map = this.getEabyToken(sessionID);
                    token = map.get("token").toString();
                    expirationTime = (Date) map.get("expirationTime");
                }catch (Exception e){
                    e.printStackTrace();
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权失败");
                }
                //验证账号
                List<Empower> checkEmpowerList = empowerMapper.checkEmpowerAccountUserId(empower.getAccount(),empower.getPlatform(),userId,null);
                List<Empower> checkAccount = Lists.newArrayList();
                for(Empower cEmpower:checkEmpowerList){
                    if(!cEmpower.getPinlianId().equals(empower.getPinlianId())){
                        throw new GlobalException("6000", "检测到此店铺已在其他品连账号(" + checkEmpowerList.get(0).getPinlianAccount() + ")中已经有授权记录，无法完成授权");
                    }else{
                        checkAccount.add(cEmpower);
                    }
                }

                if(!"10".equals(empower.getDataType())){//确认授权
                        StringBuffer straccountName =new StringBuffer();
                        boolean bool = false;
                        for(Empower cEmpower:checkAccount){
                            if(bool){
                                straccountName.append(","+cEmpower.getAccount());
                            }else {
                                bool = true;
                                straccountName.append(cEmpower.getAccount());
                            }
                        }
                        if(bool) {
                            throw new GlobalException("6001", "检测到此店铺在此品连账号中已经有授权记录(" + straccountName + ")。您可以更新授权信息，也可以取消授权(原授权记录数据不变)。");
                        }
                }
                empower.setRefreshToken(userId);
                empower.setToken(token);
                empower.setEndTime(expirationTime);

                if(checkAccount!=null && checkAccount.size()>0){
                    Integer empowerId = checkAccount.get(0).getEmpowerId();
                    Empower updateEmpower = new Empower();
                    updateEmpower.setEmpowerId(empowerId);
                    updateEmpower.setToken(empower.getToken()); //更新token
                    updateEmpower.setEndTime(empower.getEndTime());//更新结束时间
                    updateEmpower.setStatus(1);
                    empowerMapper.updateByPrimaryKeySelective(updateEmpower);
                    this.insertEmpowerLog(empowerId, empower.getParentAccount(), "重新授权");
                }else{
                    //paypal账号
//                    Integer countpaypal = empowerMapper.checkEmpowerPaypal(empower.getEmpowerId(),empower.getPaypalAccount01(),empower.getPaypalAccount02());
//                    if(countpaypal>0){
//                        throw new GlobalException("6003", "Paypal账号重复");
//                    }
                    empowerMapper.insertSelective(empower);
                    this.insertEmpowerLog(empower.getEmpowerId(), empower.getParentAccount(), "添加授权");
                    //子账号的时候添加店铺授权数据权限
                    if (!userDTO.getManage()) {
                        remoteUserService.bindStore(userDTO.getUserId(),empower.getEmpowerId());
                    }
                }
            }else if(empower.getPlatform()==3){
                List<Empower> checkAccount = empowerMapper.checkEmpowerAccount(empower.getAccount(),empower.getPlatform(),null);
                if(!"10".equals(empower.getDataType())){//确认授权
                    if (checkAccount != null && checkAccount.size() > 0) {
                        throw new GlobalException("6001", "检测到此店铺在此品连账号中已经有授权记录(" + checkAccount.get(0).getAccount() + ")。您可以更新授权信息，也可以取消授权(原授权记录数据不变)。");
                    }
                }
                Map<String,String> map = this.getAliexpressToken(empower.getToken());
                String accessToken = map.get("accessToken");
                String refreshToken = map.get("refreshToken");
                String userNick = map.get("userNick");
                String userId = map.get("userId");
                String expireTime = map.get("expireTime");

                empower.setToken(accessToken);
                empower.setRefreshToken(refreshToken);
                empower.setThirdPartyName(userId);
                //过期时间
                Date time = new Date(Long.valueOf(expireTime));
                empower.setEndTime(time);
                empower.setPaypalAccount01(userNick);
                //验证用户是否重复
                Empower queryEmpower = new Empower();
                queryEmpower.setThirdPartyName(userId);
                queryEmpower.setPlatform(3);
                Empower checkEmpower = empowerMapper.selectAmazonAccount(queryEmpower);
                if(checkEmpower==null || checkEmpower.getEmpowerId()==null){
                    empowerMapper.insertSelective(empower);
                    this.insertEmpowerLog(empower.getEmpowerId(), empower.getParentAccount(), "添加授权");
                }else{
                    if(checkEmpower.getPinlianAccount()!=null && !checkEmpower.getPinlianAccount().equals(empower.getPinlianAccount())){
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该授权信息已存在");
                    }
                    Empower updateEmpower = new Empower();
                    updateEmpower.setEmpowerId(checkEmpower.getEmpowerId());
                    updateEmpower.setToken(accessToken);
                    updateEmpower.setRefreshToken(refreshToken);
                    updateEmpower.setEndTime(time);
                    updateEmpower.setStatus(1);
                    empowerMapper.updateByPrimaryKeySelective(updateEmpower);
                    this.insertEmpowerLog(checkEmpower.getEmpowerId(), empower.getParentAccount(), "重新授权");
                    //子账号的时候添加店铺授权数据权限
                    if (!userDTO.getManage()) {
                        remoteUserService.bindStore(userDTO.getUserId(),empower.getEmpowerId());
                    }
                }

            }else if(empower.getPlatform()==4){
                List<Empower> checkAccount = empowerMapper.checkEmpowerAccount(empower.getAccount(),empower.getPlatform(),null);
                if(!"10".equals(empower.getDataType())){//确认授权
                    if (checkAccount != null && checkAccount.size() > 0) {
                        throw new GlobalException("6001", "检测到此店铺在此品连账号中已经有授权记录(" + checkAccount.get(0).getAccount() + ")。您可以更新授权信息，也可以取消授权(原授权记录数据不变)。");
                    }
                }
                //授权结束时间按一年时间
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(calendar.DAY_OF_YEAR, 5000);
                date = calendar.getTime();
                empower.setEndTime(date);
                if(checkAccount!=null && checkAccount.size()>0){
                    Empower updateEmpower = new Empower();
                    updateEmpower.setEmpowerId(checkAccount.get(0).getEmpowerId());
                    updateEmpower.setEndTime(empower.getEndTime());
                    updateEmpower.setStatus(1);
                    empowerMapper.updateByPrimaryKeySelective(updateEmpower);
                    this.insertEmpowerLog(updateEmpower.getEmpowerId(), empower.getParentAccount(), "重新授权");
                }else {
                    empowerMapper.insertSelective(empower);
                    this.insertEmpowerLog(empower.getEmpowerId(), empower.getParentAccount(), "添加授权");
                    //子账号的时候添加店铺授权数据权限
                    if (!userDTO.getManage()) {
                        remoteUserService.bindStore(userDTO.getUserId(),empower.getEmpowerId());
                    }
                }
            }
        }


        return 1;
    }

    @Override
    public int updateEmpower(Empower empower) {
        Empower queryEmpowerRentTime=empowerMapper.getEmpowerById(empower.getEmpowerId());
        if(queryEmpowerRentTime==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权id不能为空");
        }
        if(queryEmpowerRentTime.getRentStatus()!=null && queryEmpowerRentTime.getRentStatus()==1
                && queryEmpowerRentTime.getRentTime()==null){
            empower.setRentTime(new Date());
        }
        if(empower.getAutoTime()==null){
            empower.setAutoTime(new Date());
        }
        //卖家供应链ID,名称
        String company ="";
        Object sell = remoteUserService.getSupplyChinByUserIdOrUsername(empower.getPinlianId(), null, 1);
        if(sell!=null) {
            JSONObject selljs = (JSONObject) JSONObject.toJSON(sell);
            if ("true".equals(selljs.getString("success"))) {
                JSONObject jsonObjectcompany = selljs.getJSONObject("data");
                company = jsonObjectcompany.getString("supplyId");

            }
        }

        if(empower.getPlatform()==1){
            String sessionID = (String)redisUtils.get("sessionID"+getUserInfo.getUserInfo().getUser().getUserid().toString());
            if(StringUtils.isBlank(sessionID)){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权等待时间过期");
            }
            String token ="";
            String userId =null;
            Date expirationTime=null;//授权结束时间
            try {
                userId = this.getEabyUserId(sessionID);
                Map<String ,Object> map = this.getEabyToken(sessionID);
                token = map.get("token").toString();
                expirationTime = (Date) map.get("expirationTime");
            }catch (Exception e){
                e.printStackTrace();
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权失败");
            }
            //验证账号是否重复
//            List<Empower> checkEmpowerList = empowerMapper.checkEmpowerAccountUserId(empower.getAccount(),empower.getPlatform(),userId,empower.getEmpowerId());
//            if(checkEmpowerList!=null && checkEmpowerList.size()>0){
//                throw new GlobalException("6000", "检测到此店铺已在其他品连账号(" + checkEmpowerList.get(0).getPinlianAccount() + ")中已经有授权记录，无法完成授权");
//            }
            if (queryEmpowerRentTime.getRefreshToken() != null
                    && !queryEmpowerRentTime.getRefreshToken().equals(userId)) {
                throw new GlobalException("6000", "userid与重新授权账号数据不一致，无法重新授权");
            }else{
                //验证账号是否重复
                List<Empower> checkEmpowerList = empowerMapper.checkEmpowerAccountUserId(empower.getAccount(),empower.getPlatform(),userId,empower.getEmpowerId());
                if(checkEmpowerList!=null && checkEmpowerList.size()>0){
                    for(Empower cEmpower:checkEmpowerList){
                        if(!cEmpower.getPinlianId().equals(empower.getPinlianId())){
                            throw new GlobalException("6000", "检测到此店铺已在其他品连账号(" + cEmpower.getPinlianAccount() + ")中已经有授权记录，无法完成授权");
                        }else{
                            throw new GlobalException("6000", "检测到此店铺在此品连账号中已经有授权记录(" + cEmpower.getAccount() + ")，无法完成授权");
                        }
                    }
                }

            }
            //paypal账号
//            Integer countpaypal = empowerMapper.checkEmpowerPaypal(empower.getEmpowerId(),empower.getPaypalAccount01(),empower.getPaypalAccount02());
//            if(countpaypal>0){
//                throw new GlobalException("6003", "Paypal账号重复");
//            }

            Empower updateEmpower = this.setUpdateEmpower(empower);
            updateEmpower.setEmpowerId(empower.getEmpowerId());
            updateEmpower.setEndTime(expirationTime);
            updateEmpower.setToken(token);
            updateEmpower.setRefreshToken(userId);
            updateEmpower.setCompany(company);
            updateEmpower.setPaypalAccount01(empower.getPaypalAccount01());
            updateEmpower.setPaypalAccount02(empower.getPaypalAccount02());
            empowerMapper.updateByPrimaryKeySelective(updateEmpower);
        }else  if(empower.getPlatform()==2){
            if(empower.getRentStatus()==1){
                //远程调用接口验证 账号是否正确
                String checkAmazonTokenIsValid = remoteOrderRuleService.checkAmazonTokenIsValid(empower.getThirdPartyName(), empower.getWebName(), empower.getToken());
                if (checkAmazonTokenIsValid == null) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权失败");
                }
                String returnRemoteResultDataString = Utils.returnRemoteResultDataString(checkAmazonTokenIsValid, "转换失败");
                String parse = JSONObject.parse(returnRemoteResultDataString).toString();
                if (!"1".equals(parse)) {//验证成功
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权失败");
                }

                Empower updateEmpower = this.setUpdateEmpower(empower);
                updateEmpower.setEmpowerId(empower.getEmpowerId());
                updateEmpower.setToken(empower.getToken());
                updateEmpower.setRentTime(new Date());
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(calendar.DAY_OF_YEAR, 365);
                date = calendar.getTime();
                updateEmpower.setEndTime(date);
                updateEmpower.setCompany(company);
                empowerMapper.updateByPrimaryKeySelective(updateEmpower);
                this.insertEmpowerLog(updateEmpower.getEmpowerId(), empower.getParentAccount(), "添加授权");
            }else {
                EmpowerSearchVO vo = new EmpowerSearchVO();
                vo.setPinlianId(empower.getPinlianId());
                vo.setThirdPartyName(empower.getThirdPartyName());
                List<Empower> list = empowerMapper.getEmpowerVO(vo);
                if (list != null && list.size() > 0) {
                    //远程调用接口验证 账号是否正确
                    String checkAmazonTokenIsValid = remoteOrderRuleService.checkAmazonTokenIsValid(empower.getThirdPartyName(), list.get(0).getWebName(), empower.getToken());
                    if (checkAmazonTokenIsValid == null) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权失败");
                    }
                    String returnRemoteResultDataString = Utils.returnRemoteResultDataString(checkAmazonTokenIsValid, "转换失败");
                    String parse = JSONObject.parse(returnRemoteResultDataString).toString();
                    if (!"1".equals(parse)) {//验证成功
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权失败");
                    }

                    for (Empower e : list) {
                        Empower updateEmpower = this.setUpdateEmpower(empower);
                        updateEmpower.setEmpowerId(e.getEmpowerId());
                        updateEmpower.setToken(empower.getToken());

                        Date date = new Date();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(calendar.DAY_OF_YEAR, 365);
                        date = calendar.getTime();
                        updateEmpower.setEndTime(date);
                        updateEmpower.setCompany(company);
                        empowerMapper.updateByPrimaryKeySelective(updateEmpower);
                        this.insertEmpowerLog(updateEmpower.getEmpowerId(), empower.getParentAccount(), "重新授权");
                    }
                } else {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权失败");
                }
            }
        }else  if(empower.getPlatform()==3){
            Map<String,String> map = this.getAliexpressToken(empower.getToken());
            String accessToken = map.get("accessToken");
            String refreshToken = map.get("refreshToken");
            String userNick = map.get("userNick");
            String userId = map.get("userId");
            String expireTime = map.get("expireTime");

            empower.setToken(accessToken);
            empower.setRefreshToken(refreshToken);
            empower.setThirdPartyName(userId);
            //过期时间
            Date time = new Date(Long.valueOf(expireTime));
            empower.setEndTime(time);
            empower.setPaypalAccount01(userNick);
            //验证用户是否重复
            Empower queryEmpower = new Empower();
            queryEmpower.setThirdPartyName(userId);
            queryEmpower.setPlatform(3);
            Empower checkEmpower = empowerMapper.selectAmazonAccount(queryEmpower);
            if(checkEmpower!=null){
                if(checkEmpower.getPinlianAccount()!=null && !checkEmpower.getPinlianAccount().equals(empower.getPinlianAccount())){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该授权信息已存在");
                }
                Empower updateEmpower = this.setUpdateEmpower(empower);
                updateEmpower.setEmpowerId(checkEmpower.getEmpowerId());
                updateEmpower.setToken(accessToken);
                updateEmpower.setRefreshToken(refreshToken);
                updateEmpower.setEndTime(time);
                updateEmpower.setCompany(company);
                empowerMapper.updateByPrimaryKeySelective(updateEmpower);
                this.insertEmpowerLog(empower.getEmpowerId(), empower.getParentAccount(), "重新授权");
            }
        }else  if(empower.getPlatform()==4){
            empower.setUpdateTime(new Date());
            empower.setCompany(company);
            empowerMapper.updateByPrimaryKeySelective(empower);
            this.insertEmpowerLog(empower.getEmpowerId(), empower.getParentAccount(), "重新授权");
        }
        if(empower.getPlatform()!=4) {
            UserDTO userDTO = getUserInfo.getUserDTO();
            //子账号的时候添加店铺授权数据权限
            if (!userDTO.getManage()) {
                remoteUserService.bindStore(userDTO.getUserId(), empower.getEmpowerId());
            }
        }
        return 1;
    }

    /**
     * 修改填充对象
     * @param empower
     * @return
     */
    private Empower setUpdateEmpower(Empower empower){
        Empower updateEmpower = new Empower();
        updateEmpower.setStatus(1);
        updateEmpower.setRentType(1);
        updateEmpower.setPinlianId(empower.getPinlianId());
        updateEmpower.setPinlianAccount(empower.getPinlianAccount());
        updateEmpower.setParentAccount(empower.getParentAccount());
        updateEmpower.setRentTime(empower.getRentTime());
        updateEmpower.setAutoTime(empower.getAutoTime());
        updateEmpower.setUpdateTime(new Date());
        return updateEmpower;
    }

    public void insertEmpowerLog(Integer empowerId,String loginName,String operation){
        EmpowerLog empowerLog = new EmpowerLog();
        empowerLog.setCreatetime(new Date());
        empowerLog.setEmpowerid(empowerId);
        empowerLog.setHandler(loginName);
        empowerLog.setOperation(operation);
        empowerLogMapper.insertSelective(empowerLog);
    }

    /**
     * 速卖通获取token
     * @param code
     * @return
     */
    private Map<String,String> getAliexpressToken(String code){

        String url="https://oauth.aliexpress.com/token";
        Map<String,String> props=new HashMap<String,String>();
        props.put("grant_type","authorization_code");
        /*测试时，需把test参数换成自己应用对应的值*/
        props.put("code",code);
        props.put("client_id",aliexpressConfig.getAppKey());
        props.put("client_secret",aliexpressConfig.getAppSecret());
        //前端地址
        props.put("redirect_uri",aliexpressConfig.getUrl());
        props.put("view","web");
        props.put("sp","ae");
        String josnStr ="";
        try {
            josnStr = WebUtils.doPost(url, props, 30000, 30000);
        }catch (Exception e){
            e.printStackTrace();
        }
        logger.info("Aliexpress授权结果=",josnStr);
        JSONObject jsonObject = JSONObject.parseObject(josnStr);
        String accessToken = jsonObject.get("access_token")==null?null:jsonObject.get("access_token").toString();
        String refreshToken = jsonObject.get("refresh_token")==null?null:jsonObject.get("refresh_token").toString();
        String userNick = jsonObject.get("user_nick")==null?null:jsonObject.get("user_nick").toString();
        String userId = jsonObject.get("user_id")==null?null:jsonObject.get("user_id").toString();
        String expireTime = jsonObject.get("expire_time")==null?null:jsonObject.get("expire_time").toString();

        if(accessToken==null || expireTime==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,ResponseCodeEnum.RETURN_CODE_100400.getMsg());
        }
        Map<String,String> map =Maps.newHashMap();
        map.put("accessToken",accessToken);
        map.put("refreshToken",refreshToken);
        map.put("userNick",userNick);
        map.put("userId",userId);
        map.put("expireTime",expireTime);
        return map;
    }


    private Empower checkEmpower(Empower empower){
        if(empower.getPlatform()==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"所属平台信息不能为空");
        }
        if (StringUtils.isBlank(empower.getAccount())){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "自定义账号不能为空");
        }
//        if (empower.getStatus() == null) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权状态值不能为空");
//        }
        if(empower.getPlatform()==1){
            if (StringUtils.isBlank(empower.getPaypalAccount01())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "PayPal不能为空");
            }
        }else if(empower.getPlatform()==2){
            if (StringUtils.isBlank(empower.getToken())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权token不能为空");
            }
            if (StringUtils.isBlank(empower.getThirdPartyName())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "亚马逊sellerID信息不能为空");
            }
            if (StringUtils.isBlank(empower.getWebName())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "亚马逊站点信息不能为空");
            }
        }

        //公共参数的设置
        Date date = new Date();
        empower.setAutoTime(date);
        empower.setCreateTime(date);
        empower.setUpdateTime(date);
        //保存账号
        UserDTO userDTO = getUserInfo.getUserDTO();
        String pinlianAccount ="";
        String parentAccount ="";
        Integer userid = null;
        if (!userDTO.getManage()) {
            userid = userDTO.getTopUserId();
            pinlianAccount = userDTO.getTopUserLoginName();
            parentAccount = userDTO.getLoginName();
        } else {
            userid = userDTO.getUserId();
            pinlianAccount = userDTO.getLoginName();
            parentAccount = userDTO.getLoginName();
        }
        empower.setPinlianId(userid);
        empower.setPinlianAccount(pinlianAccount);
        empower.setParentAccount(parentAccount);

        if(empower.getRentStatus()==null){
            empower.setRentStatus(0);
        }
        if(empower.getStatus()==null){
            empower.setStatus(1);
        }
        empower.setRentType(1);
        //卖家供应链ID,名称
        String company ="";
        Object sell = remoteUserService.getSupplyChinByUserIdOrUsername(empower.getPinlianId(), null, 1);
        if(sell!=null) {
            JSONObject selljs = (JSONObject) JSONObject.toJSON(sell);
            if ("true".equals(selljs.getString("success"))) {
                JSONObject jsonObjectcompany = selljs.getJSONObject("data");
                company = jsonObjectcompany.getString("supplyId");

            }
        }
        empower.setCompany(company);
        return empower;
    }

    /**
     * 获取账号的sessionId
     */
    @Override
    public String getEbayUrl() {
        ApiContext apiContext = new ApiContext();
        ApiAccount apiAccount = new ApiAccount();
        apiAccount.setApplication(ebayConfig.getAppid());
        apiAccount.setDeveloper(ebayConfig.getDeveloper());
        apiAccount.setCertificate(ebayConfig.getCert());
        apiContext.getApiCredential().setApiAccount(apiAccount);
        apiContext.setApiServerUrl("https://api.ebay.com/wsapi");
        apiContext.setTimeout(500000);

        GetSessionIDCall getSessionIDCall = new GetSessionIDCall(apiContext);
        getSessionIDCall.setRuName(ebayConfig.getRuName());

        try {
            String sessionID = getSessionIDCall.getSessionID();
            String url = "https://signin.ebay.com/ws/eBayISAPI.dll?SignIn&runame=%s&SessID=%s";
            url = String.format(url, ebayConfig.getRuName(), sessionID);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Empower getEmpowerById(Integer id) {
        return empowerMapper.getEmpowerById(id);
    }

    @Override
    public List<Empower> getEmpowerVO(EmpowerSearchVO vo) {
        return empowerMapper.getEmpowerVO(vo);
    }

    @Override
    public int insertSelective(Empower empower) {
        return empowerMapper.insertSelective(empower);
    }

    @Override
    public int updateByPrimaryKeySelective(Empower record) {
        return empowerMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int deleteByPrimaryKey(Integer empowerId) {
        Empower empower = empowerMapper.getEmpowerById(empowerId);
        if(empower.getPinlianId()==null){
            return empowerMapper.deleteByPrimaryKey(empowerId);
        }
        return 0;
    }
    @Override
    public List<Empower> checkEmpowerAccount(String account, Integer platform,Integer empowerId){
        return empowerMapper.checkEmpowerAccount(account,platform,empowerId);
    }

    @Override
    public Integer checkAccountWebName(String webName, String thirdPartyName, Integer empowerId) {
        return empowerMapper.checkAccountWebName(webName,thirdPartyName,empowerId);
    }

    @Override
    public int checkEmpowerPaypal(String account, Integer platform, Integer empowerId, String paypalAccount01, String paypalAccount02) {
        List<Empower> list = empowerMapper.checkEmpowerAccount(account,platform,empowerId);
        if(list!=null && list.size()>0){
            return 1;
        }else{
            if(StringUtils.isNotEmpty(paypalAccount01)){
                return empowerMapper.checkEmpowerPaypal(empowerId,paypalAccount01,paypalAccount02);
            }
        }
        return 0;
    }
    /**
     * 获取账号的token
     * @throws Exception
     * @throws SdkException
     * @throws ApiException
     */
    private Map<String,Object> getEabyToken(String sessionID) throws ApiException, SdkException, Exception {
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        FetchTokenCall call = new FetchTokenCall();
        ApiContext apiContext = new ApiContext();
        apiContext.setApiServerUrl("https://api.ebay.com/wsapi");
        ApiCredential credential = new ApiCredential();
        ApiAccount account = new ApiAccount();
        account.setApplication(ebayConfig.getAppid());
        account.setDeveloper(ebayConfig.getDeveloper());
        account.setCertificate(ebayConfig.getCert());
        credential.setApiAccount(account);
        apiContext.setApiCredential(credential);
        call.setApiContext(apiContext);
        call.setSessionID(sessionID);
        call.fetchToken();
        Calendar expirationTime = call.getHardExpirationTime();

        Map<String,Object> map = Maps.newHashMap();
        map.put("token",call.fetchToken());
        map.put("expirationTime",expirationTime.getTime());
        return map;
    }

    /**
     * 获取账号的id
     * @throws Exception
     * @throws SdkException
     * @throws ApiException
     */
    private String getEabyUserId(String sessionID) throws ApiException, SdkException, Exception {
        ConfirmIdentityCall call = new ConfirmIdentityCall();
        ApiContext apiContext = new ApiContext();
        apiContext.setApiServerUrl("https://api.ebay.com/wsapi");
        ApiCredential credential = new ApiCredential();
        ApiAccount account = new ApiAccount();
        account.setApplication(ebayConfig.getAppid());
        account.setDeveloper(ebayConfig.getDeveloper());
        account.setCertificate(ebayConfig.getCert());
        credential.setApiAccount(account);
        apiContext.setApiCredential(credential);
        call.setApiContext(apiContext);
        call.setSessionID(sessionID);
        call.confirmIdentity();
        return call.getReturnedUserID();
    }

    /**
     * 查询列表信息
     */
    @Override
    public Page<Empower> getEmpowerPage(EmpowerSearchVO vo) {
        try {

            this.checkEndTime();

            Page.builder(vo.getPage(), vo.getRow());
            List<Empower> findAll = empowerMapper.getEmpowerVO(vo);

            if(findAll!=null && findAll.size()>0){
                boolean bool = false;
                for(Empower empower:findAll){
                    if(StringUtils.isNotEmpty(empower.getCompany())){
                        bool = true;
                        break;
                    }
                }
                if(bool) {
                    //供应链公司
                    String supplyChainCompanyName = remoteUserService.getsSelect(1);
                    String returnRemoteResultDataString = Utils.returnRemoteResultDataString(supplyChainCompanyName, "转换失败");

                    Map<String, String> map = Maps.newHashMap();
                    JSONArray arraysupply = JSONArray.parseArray(returnRemoteResultDataString);
                    if (arraysupply != null) {
                        for (int i = 0; i < arraysupply.size(); i++) {
                            JSONArray arrayValue = arraysupply.getJSONArray(i);
                            for (int j = 0 ; j<arrayValue.size(); j++) {
                                JSONObject jsonObject = arrayValue.getJSONObject(j);
                                String supplyId = jsonObject.get("key").toString();
                                String supplyChainName = jsonObject.get("name").toString();
                                map.put(supplyId, supplyChainName);
                            }
                        }
                    }
                    findAll.forEach(empower -> {
                        if (StringUtils.isNotEmpty(empower.getCompany())) {
                            empower.setCompanyName(map.get(empower.getCompany()));
                        }
                    });
                }
            }

            PageInfo<Empower> pageInfo = new PageInfo(findAll);
            return new Page(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void checkEndTime(){
        Integer count = empowerMapper.checkEndTime();
        if(count>0){
            empowerMapper.updateEndTime();
        }
    }

    @Override
    public List<Empower> getEmpowerAll(Integer platform,Integer status,Integer pinlianId) {
        return empowerMapper.getEmpowerAll(platform,status,pinlianId);
    }

    @Override
    public List<EmpowerLog> getEmpowerLogById(Integer id, String loginName) {
        if (id == null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id值不能为空");

        return empowerLogMapper.selectByPrimaryKey(id,loginName);
    }

    /**
     * MQ通知店铺快要到期
     */
    public void sendMsgEmpower() {
        List<Empower> list = empowerMapper.sendMsgEmpower(1);
        for (Empower empower : list) {
            MessageNoticeModel msg = new MessageNoticeModel();
            msg.setMessageCategory("COMMODITY_MESSAGE");
            msg.setMessageContent(empower.getAccount()+"#"+ DateUtils.getDiffDay(new Date(),empower.getEndTime()));
            msg.setMessagePlatform("0");
            msg.setMessageScceptUserName(empower.getPinlianAccount());
            msg.setReceiveSys("1");
            msg.setMessageType("AUTHORIZE_WARNING_NOTICE");
            messageSender.sendMessage(net.sf.json.JSONObject.fromObject(msg).toString());
        }
        List<Empower> listtwo = empowerMapper.sendMsgEmpower(2);
        for (Empower empower : listtwo) {
            MessageNoticeModel msg = new MessageNoticeModel();
            msg.setMessageCategory("COMMODITY_MESSAGE");
            msg.setMessageContent(empower.getAccount());
            msg.setMessagePlatform("0");
            msg.setMessageScceptUserName(empower.getPinlianAccount());
            msg.setReceiveSys("1");
            msg.setMessageType("AUTHORIZE_EXPIRE_NOTICE");
            messageSender.sendMessage(net.sf.json.JSONObject.fromObject(msg).toString());
        }
    }

    @Override
    public void updateMigrateEmpowerRent(Integer empowerId, Integer pinlianId, String pinlianAccount, String account, Integer platform) {
        List<Empower> listcheck = empowerMapper.checkEmpowerAccount(account,platform,null);
        if(listcheck!=null && listcheck.size()>0){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "账号重复");
        }

        Empower empower = empowerMapper.getEmpowerById(empowerId);
        if(empower==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "迁移店铺为空");
        }
        empower.setEmpowerId(null);
        Date date = new Date();
        empower.setUpdateTime(date);
        empower.setCreateTime(date);

        empower.setPinlianId(pinlianId);
        empower.setPinlianAccount(pinlianAccount);
        //卖家供应链ID,名称
        Object sell = remoteUserService.getSupplyChinByUserIdOrUsername(pinlianId, null, 1);
        if(sell!=null) {
            JSONObject selljs = (JSONObject) JSONObject.toJSON(sell);
            if ("true".equals(selljs.getString("success"))) {
                JSONObject jsonObjectcompany = selljs.getJSONObject("data");
                String company = jsonObjectcompany.getString("supplyId");
                empower.setCompany(company);
            }
        }
        empowerMapper.insertSelective(empower);
        Empower ordEmpower = new Empower();
        ordEmpower.setEmpowerId(empowerId);
        ordEmpower.setStatus(4);
        empowerMapper.updateByPrimaryKeySelective(ordEmpower);
        this.insertEmpowerLog(empower.getEmpowerId(),pinlianAccount,"店铺迁移");
    }
}
