package com.rondaful.cloud.order.remote;

import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.order.enums.ResponseCodeEnum;
import com.rondaful.cloud.order.model.dto.sysorder.FeignResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "rondaful-user-service", fallback = RemoteUserService.RemoteUserSericeImpl.class)
public interface RemoteUserService {

    @ApiOperation(value = "获取国家信息")
    @GetMapping("/area/code/getArea")
    String getArea(@RequestParam(value = "countryName", defaultValue = "") String countryName,
                   @RequestParam(value = "countryCode", defaultValue = "")String countryCode);

    @ApiOperation(value = "根据店铺id获取子账号信息")
    @GetMapping("/provider/getsUserByStore")
    String getsUserByStore(@RequestParam("storeId") Integer storeId);

    @ApiOperation(value ="根据传入用户id找到与其绑定的供应链公司(platformType:1卖家 0供应商)")
    @PostMapping("/provider/getSupplyChainByUserId")
    String getSupplyChainByUserId(@RequestParam("platformType") String platformType, @RequestBody List<Integer> userIdList);

    @ApiOperation(value = "根据用户名或者id获取对应的供应链信息")
    @GetMapping(value = "/provider/getSupplyChinByUserIdOrUsername")
    String getSupplyChinByUserIdOrUsername(@RequestParam(value = "userid") Integer userid,
                                           @RequestParam(value = "username") String username,
                                          @RequestParam(value = "platformType") Integer platformType);


    @ApiOperation(value = "根据AppKey获取对应的信息")
    @PostMapping(value = "/provider/getByAppKey")
    String getByAppKey(@RequestParam(value = "appKey") String appKey);


    @ApiOperation(value = "获取获取子账号列表")
    @PostMapping(value = "/provider/getChildAccount")
    String getChildAccount(@RequestParam(value = "userId") Integer userId,
                           @RequestParam(value = "userName") String userName,
                           @RequestParam(value = "platformType") String platformType);

    @GetMapping("/provider/getAppAll")
    FeignResult getAppAll();

    @GetMapping("/provider/getSupplierList")
    FeignResult getSupplierList(@RequestParam("userIds") List<Integer> userIds,@RequestParam("platformType") Integer platformType);

    @ApiOperation(value = "根店铺绑定的卖家主账号id和店铺id【获取店铺利润阈值】")
    @GetMapping("/provider/getThreshold")
    String getThreshold(@RequestParam("sellerId") Integer sellerId,@RequestParam("storeId") Integer storeId);



    @Service
    class RemoteUserSericeImpl implements RemoteUserService {
        @Override
        public String getArea(String countryName, String countryCode) {
            return null;
        }

        @Override
        public String getsUserByStore(Integer storeId) {
            return null;
        }

        @Override
        public String getSupplyChainByUserId(String platformType, List<Integer> list) {
            return null;
        }


        @Override
        public String getSupplyChinByUserIdOrUsername(Integer userid, String username, Integer platformType) { return null; }

        @Override
        public String getByAppKey(String appKey) {
            return null;
        }

        @Override
        public String getChildAccount(Integer userId, String userName, String platformType) {
            return null;
        }

        @Override
        public FeignResult getAppAll() {
            return null;
        }

        public String fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "用户服务异常")).toString();
        }

        @Override
        public FeignResult getSupplierList(List<Integer> userIds, Integer platformType) {
            return null;
        }

        @Override
        public String getThreshold(Integer sellerId, Integer storeId) {
            return null;
        }

    }
}

