package com.rondaful.cloud.order.utils;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.entity.Amazon.AmazonEmpower;
import com.rondaful.cloud.order.remote.RemoteSellerService;
import com.rondaful.cloud.order.remote.RemoteUserService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class GetAccountAndShopInfoUtils {
    public final static org.slf4j.Logger logger = LoggerFactory.getLogger(GetAccountAndShopInfoUtils.class);
    @Autowired
    public GetLoginUserInformationByToken getLoginUserInformationByToken;
    @Autowired
    public RemoteUserService remoteUserService;
    @Autowired
    public RemoteSellerService remoteSellerService;

    /**
     * 获取品连账号ID
     * @param sellerPlAccount
     * @param plSellerId
     * @return
     */
    public Integer getPlSellerIdIfNotNull(String sellerPlAccount, Integer plSellerId) {
        if (sellerPlAccount != null) {
            String[] arrString = new String[1];
            arrString[0]=sellerPlAccount;
            plSellerId = getPlId( plSellerId, sellerPlAccount,getLoginUserInformationByToken.getUserInfo().getUser().getPlatformType());
        }
        return plSellerId;
    }


    public Integer getPlId(Integer userId, String loginName,Integer platformType) {
        Integer plSellerId;
        String result = remoteUserService.getSupplyChinByUserIdOrUsername(userId,loginName,platformType);
        String data = Utils.returnRemoteResultDataString(result, "用户服务异常");
        if (JSONObject.parseObject(data)==null) {
            logger.error("找不到品连账号ID");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "找不到该品连账号ID");
        }else {
            plSellerId = (JSONObject.parseObject(data).getInteger("userId"));
        }
        return plSellerId;
    }

    public Integer getShopIdIfNotNull(String platformSellerAccount, String sellerPlAccount) {
        if (platformSellerAccount != null) {
            return GetSellerShopIdByShopName(platformSellerAccount,null);//根据店铺名得到店铺ID
        }
        return null;
    }

    public List<Integer> getShopIdListIfNotNull(List<String> platformSellerAccountList, String sellerPlAccount, Integer shopId) {
        List<Integer> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(platformSellerAccountList)) {
            platformSellerAccountList.forEach(s -> {
                list.add(GetSellerShopIdByShopName(s, null));//根据店铺名得到店铺ID
            });
        }else{
            list.add(shopId);
        }
        return list;
    }

    /**
     * 通过店铺名找到授权信息
     * @param shopName  店铺名
     * @param PlAccount  品连账号
     * @return
     */
    public  List<AmazonEmpower> GetSellerAuthorizationInfoByShopName(String shopName, String PlAccount) {
        String result = remoteSellerService.selectObjectByAccount(null, PlAccount,shopName);
        String data = Utils.returnRemoteResultDataString(result, "调用卖家微服务异常。。。");
        List<AmazonEmpower> amazonEmpowers = JSONObject.parseArray(data, AmazonEmpower.class);
        if (CollectionUtils.isEmpty(amazonEmpowers)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "店铺名输入有误！");
        }
        return amazonEmpowers;
    }

    /**
     * 通过店铺ID找到授权信息
     * @param shopId  店铺ID
     * @param PlAccount   品连账号
     * @return
     */
    public List<AmazonEmpower> GetSellerAuthorizationInfoByShopId(Integer shopId, String PlAccount) {
        String result = remoteSellerService.selectObjectByAccount(shopId,PlAccount,null);
        String data = Utils.returnRemoteResultDataString(result, "调用卖家微服务异常。。。");
        List<AmazonEmpower> amazonEmpowers = JSONObject.parseArray(data, AmazonEmpower.class);
        if (CollectionUtils.isEmpty(amazonEmpowers)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查不到卖家的授权信息！");
        }
        return amazonEmpowers;
    }

    /**
     * 通过店铺ID找到店铺名,   TODO 后期店铺名可以重复的时候就需要传品连账号
     * @param shopId
     * @return
     */
    public  String GetSellerShopNameByShopId( Integer shopId,String PlAccount) {
        List<AmazonEmpower> amazonEmpower = GetSellerAuthorizationInfoByShopId(shopId,null);
        return amazonEmpower.get(0).getAccount();
    }

    /**
     * 通过店铺名找到店铺ID
     * @param shopName
     * @return
     */
    public  Integer GetSellerShopIdByShopName( String shopName,String PlAccount) {
        List<AmazonEmpower> amazonEmpower = GetSellerAuthorizationInfoByShopName(shopName,PlAccount);
        return amazonEmpower.get(0).getEmpowerId();
    }


}
