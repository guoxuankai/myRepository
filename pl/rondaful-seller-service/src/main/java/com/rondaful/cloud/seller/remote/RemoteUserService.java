package com.rondaful.cloud.seller.remote;


import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;

import net.sf.json.JSONObject;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 远程调用用户服务
 * */
@FeignClient(name = "rondaful-user-service", fallback = RemoteUserService.RemoteUserServiceImpl.class)
public interface RemoteUserService {


	/**
	 * 根据用户id获取对应平台下子账号
	 * @param userId
	 * @param userName
	 * @param platformType
	 * @return
	 */
    @PostMapping("/provider/getChildAccount")
	String getChildAccount(@RequestParam("userId")Integer userId,@RequestParam("userName")String userName,@RequestParam("platformType")String platformType);
    
    /**
     * 根据用户名或者id获取对应的供应链信息//platformType 1卖家 0供应商
     * @param userid
     * @param username
     * @param platformType
     * @return
     */
    @GetMapping(value = "/provider/getSupplyChinByUserIdOrUsername")
    Object getSupplyChinByUserIdOrUsername(@RequestParam("userid") Integer userid, @RequestParam("username") String username,@RequestParam("platformType") Integer platformType);

    /**
     *根据主账号查询是否开启upc获取
     * @param id
     * @return
     */
    @GetMapping("/provider/getUpc")
    String getUpc(@RequestParam("id")Integer id);

    @GetMapping("/seller/user/bindStore")
    String bindStore(@RequestParam("userId")Integer userId,@RequestParam("empowerId")Integer empowerId);

    @PostMapping(value = "/getSupplyChainCompanyNameList")
    String getSupplyChainCompanyNameList();
    
    /**
     * 根据用户id数组批量获取基本信息
     * @param userIds
     * @param platformType
     * @return
     */
    @GetMapping("/provider/getSupplierList")
    String getSupplierList(@RequestParam("userIds")Integer[] userIds,@RequestParam("platformType")Integer platformType);

    /**
     * 根据平台类型获取绑定供应链公司列表
     * @param type
     * @return
     */
    @GetMapping("/supply/chain/getsSelect")
    String getsSelect(@RequestParam("type")Integer type);
    
    /**
             * 断路降级
     * */
    @Service
    class RemoteUserServiceImpl implements RemoteUserService {

        public Object fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "用户服务异常"));
        }


		@Override
		public String getChildAccount(Integer userId, String userName, String platformType) {
			// TODO Auto-generated method stub
			return null;
		}
        @Override
        public String getSupplyChinByUserIdOrUsername(Integer userid, String username,Integer platformType){

        // TODO Auto-generated method stub
            return null;
        }
        public String getUpc(Integer id){
            return null;
        }

        @Override
        public String bindStore(Integer userId,Integer empowerId){
            return null;
        }

        @Override
        public String getSupplyChainCompanyNameList() {
            return null;
        }


		@Override
		public String getSupplierList(Integer[] userIds, Integer platformType) {
			// TODO Auto-generated method stub
			return null;
		}

        @Override
        public String getsSelect(Integer type) {
            return null;
        }

    }
}