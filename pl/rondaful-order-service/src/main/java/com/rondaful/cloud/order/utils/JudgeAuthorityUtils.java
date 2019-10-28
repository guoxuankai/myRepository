package com.rondaful.cloud.order.utils;

import com.alibaba.fastjson.JSONArray;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class JudgeAuthorityUtils {
    /**
     * 数据权限：判断平台类型》主子账号》设置账号ID集合、店铺ID集合
     * @param plSellerId
     * @param userDTO
     * @param userIds
     * @param empIds
     * @return
     */
    public boolean judgeUserAuthorityAndSetDataToList(Integer plSellerId,UserDTO userDTO, List<Integer> userIds, List<Integer> empIds) {
        int count=0;
        if (UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())){  //管理后台
            if (!userDTO.getManage()){   //子管理员：没有绑定就返回null，有绑定就查出所有店铺ID设置到集合；若有传店铺ID则设置到店铺ID集合
                for (UserAccountDTO accountDTO:userDTO.getBinds()) {   //可能多层绑定关系
                    if (accountDTO != null) {   //确定绑定关系非空
                        if (accountDTO.getBindType().equals(UserEnum.platformType.SELLER.getPlatformType())){     //判断该管理员是否有绑定卖家ID
                            userIds.addAll( JSONArray.parseArray(FastJsonUtils.toJsonString(accountDTO.getBindCode()),Integer.class));  //获得该管理员所绑定的所有账号ID并设置到 账号ID集合****
                            if (CollectionUtils.isEmpty(userIds)){  //账号ID集合为空，证明该管理员没有绑定任何账号ID，返回null.
                                return true;
                            }
                            if (plSellerId != null) {   //判断传入的用户ID有没有绑定
                                for (Integer userId : userIds) {   //判断传入的用户ID 是否存在绑定的用户ID中
                                    if (userId.equals(plSellerId)) {
                                        count++;
                                    }
                                }
                                if (count==0){
                                    return true;
                                }else {
                                    userIds.clear();
                                    userIds.add(plSellerId);
                                }
                            }
                        }else{
                            if ((userDTO.getBinds().indexOf(accountDTO)==userDTO.getBinds().size()-1)) { //如果是最后一层绑定关系，证明该子管理员账号没有绑定任何卖家账号，直接return
                                return true;
                            }
                        }
                    }else{
                        return true;
                    }
                }
            }else{     //超级管理员   账号ID、店铺ID非空就直接添加进集合
                if (plSellerId != null) {
                    userIds.add(plSellerId);
                }
            }
        }else if (UserEnum.platformType.SELLER.getPlatformType().equals(userDTO.getPlatformType())){   // 卖家平台
            if (!userDTO.getManage()){  //卖家子账号：没有绑定关系则返回null，有则设置主账号ID、店铺ID到集合
                Integer id=0;
                //卖家没有或者仅有一层绑定关系，对卖家子账号的绑定关系进行判空，可能没有绑定任何店铺ID
                if (CollectionUtils.isEmpty(userDTO.getBinds())&&CollectionUtils.isEmpty(userDTO.getBinds().get(0).getBindCode())){  //确定绑定关系不为空并且绑定店铺ID不为空
                    return true;
                }
                //获得卖家所绑定的店铺ID并设置到 店铺ID集合****
                List<Integer> shopIdList=new ArrayList<>();
                shopIdList.addAll(JSONArray.parseArray(FastJsonUtils.toJsonString(userDTO.getBinds().get(0).getBindCode()),Integer.class));
                if (CollectionUtils.isEmpty(shopIdList)) {
                    return true;
                }
                if (plSellerId != null) {    //
                    userIds.add(plSellerId);   //将卖家主账号添加进集合
                }else{
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "账号ID不能为空");
                }
                if (CollectionUtils.isNotEmpty(empIds)) {
                    for (Integer shopId : shopIdList) {
                        for (Integer empId : empIds) {
                            if (Objects.equals(empId,shopId)) {
                                id++;
                            }
                        }
                    }
                if (id == 0) {
                    return true;
                }
                }else{
                    empIds.addAll(shopIdList);
                }
            }else {     //卖家主账号：添加卖家主账号ID进集合，店铺ID不为空也添加进集合
                if (plSellerId != null) {    //
                    userIds.add(plSellerId);   //将卖家主账号添加进集合
                }else{
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "账号ID不能为空");
                }
            }
        }else {             //其他平台
            return true;
        }
        return false;
    }
     /*  public boolean judgeUserAuthorityAndSetDataToList(Integer plSellerId, Integer shopId, UserDTO userDTO, List<Integer> userIds, List<Integer> empIds) {
        int count=0;
        if (UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())){  //管理后台
            if (!userDTO.getManage()){   //子管理员：没有绑定就返回null，有绑定就查出所有店铺ID设置到集合；若有传店铺ID则设置到店铺ID集合
                for (UserAccountDTO accountDTO:userDTO.getBinds()) {   //可能多层绑定关系
                    if (accountDTO != null) {   //确定绑定关系非空
                        if (accountDTO.getBindType().equals(UserEnum.platformType.SELLER.getPlatformType())){     //判断该管理员是否有绑定卖家ID
                            userIds.addAll( JSONArray.parseArray(FastJsonUtils.toJsonString(accountDTO.getBindCode()),Integer.class));  //获得该管理员所绑定的所有账号ID并设置到 账号ID集合****
                            if (plSellerId != null) {   //判断传入的用户ID有没有绑定
                                for (Integer userId : userIds) {   //判断传入的用户ID 是否存在绑定的用户ID中
                                    if (userId.equals(plSellerId)) {
                                        count++;
                                    }
                                }
                                if (count==0){
                                    return true;
                                }else {
                                    userIds.clear();
                                    userIds.add(plSellerId);
                                }
                            }
                        }else{
                            if ((userDTO.getBinds().indexOf(accountDTO)==userDTO.getBinds().size()-1)) {
                                return true;
                            }
                        }
                    }else{
                        return true;
                    }
                }
                if (CollectionUtils.isEmpty(userIds)){  //账号ID集合为空，证明该管理员没有绑定任何账号ID，返回null.
                    return true;
                }
                if (shopId != null) {
                    empIds.add(shopId);
                }
            }else{     //超级管理员   账号ID、店铺ID非空就直接添加进集合
                if (plSellerId != null) {
                    userIds.add(plSellerId);
                }
                if (shopId != null) {
                    empIds.add(shopId);
                }
            }
        }else if (UserEnum.platformType.SELLER.getPlatformType().equals(userDTO.getPlatformType())){   // 卖家平台
            if (!userDTO.getManage()){  //卖家子账号：没有绑定关系则返回null，有则设置主账号ID、店铺ID到集合
                Integer id=0;
                //卖家没有或者仅有一层绑定关系，对卖家子账号的绑定关系进行判空，可能没有绑定任何店铺ID
                if (CollectionUtils.isEmpty(userDTO.getBinds())&&CollectionUtils.isEmpty(userDTO.getBinds().get(0).getBindCode())){  //确定绑定关系不为空并且绑定店铺ID不为空
                    return true;
                }
                //获得卖家所绑定的店铺ID并设置到 店铺ID集合****
                empIds.addAll(JSONArray.parseArray(FastJsonUtils.toJsonString(userDTO.getBinds().get(0).getBindCode()),Integer.class));
                if (plSellerId != null) {    //
                    userIds.add(plSellerId);   //将卖家主账号添加进集合
                }else{
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "账号ID不能为空");
                }
                if (shopId != null) {
                    for (Integer empId : empIds) {
                        if (empId.equals(shopId)) {
                            id++;
                        }
                    }
                    if (id == 0) {
                        return true;
                    }else{
                        empIds.clear();
                        empIds.add(shopId);
                    }
                }
            }else {     //卖家主账号：添加卖家主账号ID进集合，店铺ID不为空也添加进集合
                if (plSellerId != null) {    //
                    userIds.add(plSellerId);   //将卖家主账号添加进集合
                }else{
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "账号ID不能为空");
                }
                if (shopId != null) {
                    empIds.add(shopId);
                }
            }
        }else {             //其他平台
            return true;
        }
        return false;
    }
}*/
}
