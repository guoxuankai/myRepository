package com.rondaful.cloud.order.remote;


import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.order.enums.ResponseCodeEnum;
import com.rondaful.cloud.order.model.dto.remoteseller.GetByplatformSkuAndSiteDTO;
import com.rondaful.cloud.order.model.vo.sysorder.EmpowerRequestVo;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 卖家服务查询该卖家有效Token
 */
@FeignClient(name = "rondaful-seller-service", fallback = RemoteSellerService.RemoteSellerServiceImpl.class)
public interface RemoteSellerService {
    /**
     * 查看用户在EBAY成功的刊登数
     *
     * @param seller
     * @return
     */
    @ApiOperation(value = "查看刊登数")
    @PostMapping("/ebay/data/getOnlineCount/{seller}")
    @ResponseBody
    String getOnlineCount(@PathVariable(value = "seller") String seller);

    /**
     * 用户亚马逊的成功刊登数量
     *
     * @param userName
     * @return
     */
    @ApiOperation("移动端查询亚马逊刊登成功过的条数")
    @GetMapping("/amazonMobile/findCount")
    @ResponseBody
    String findCount(@RequestParam("userName") String userName);

    /**
     * 移动端查询速卖通刊登成功过的条数
     *
     * @param userName
     * @return
     */
    @ApiOperation("移动端查询速卖通刊登成功过的条数")
    @GetMapping("/aliexpressMobile/findAliexpressPublishCount")
    @ResponseBody
    String findAliexpressPublishCount(@RequestParam("userName") String userName);


    /**
     * 查看单个授权信息
     * status 状态 0未授权  1 正常授权  2授权过期 3停用
     * @return
     */
    @ApiOperation(value = "查看单个授权信息", notes = "查看授权信息")
    @GetMapping("/Authorization/selectObjectByAccount")
    @ResponseBody
    String selectObjectByAccount(@RequestParam("empowerId") Integer empowerId, @RequestParam("pinlianAccount") String pinlianAccount, @RequestParam("status") Integer status,
                                 @RequestParam("account") String account, @RequestParam("platform") Integer platform);

    /**
     * 查看单个授权信息   重载   增加店铺ID
     * @return
     */
    @ApiOperation(value="查看单个授权信息",notes="查看授权信息")
    @GetMapping("/Authorization/selectObjectByAccount")
    @ResponseBody
    String selectObjectByAccount(@RequestParam("empowerId")Integer empowerId, @RequestParam("pinlianAccount")String pinlianAccount, @RequestParam("account")String account);

    /**
     * @param platform 平台 (1 ebay   2 Amazon)
     * @param account  第三方的账号或id
     * @param webName  站点名称
     * @return 授权对象
     */
    @GetMapping("/Authorization/findOneByAccount")
    @ResponseBody
    String findOneEmpowByAccount(@RequestParam("platform") Integer platform, @RequestParam("account") String account, @RequestParam("webName") String webName,
                                 @RequestParam("empowerId") String empowerId);


    @ApiOperation(value = "查询授权列表信息", notes = "查询授权列表信息")
    @GetMapping("/Authorization/findAllRemote")
    String findAllRemote(@RequestParam("platform") Integer platform, @RequestParam("status") Integer status);

    /**
     *
     * @param platformSkuList 平台sku list
     * @param site 目前只有亚马逊需要站点信息
     * @param type 亚马逊:AMAZON,速卖通:ALIEXPRESS,EBAY
     * @return json String
     */
    @ApiOperation(value = "查询刊登sku的仓库信息", notes = "查询刊登sku的仓库信息")
    @PostMapping("/amazon/rest/getByplatformSkuAndSite")
    String getByplatformSkuAndSite(@RequestBody GetByplatformSkuAndSiteDTO getByplatformSkuAndSiteDTO);

    /**
     * 查询授权的列表，不分页
     *
     * @param account        自定义名称
     * @param pinlianAccount 品连平台账号
     * @param status         状态  （0未授权  1 正常授权  2授权过期 3停用）
     * @param pinlianIds     品连的ID列表
     * @param platform       平台 (1 ebay   2 Amazon 3 aliexpress)
     * @return 返回数据
     */
    @GetMapping("/Authorization/findAllNoPage")
    @ResponseBody
    String findAllNoPage(@RequestParam("account") String account, @RequestParam("pinlianAccount") String pinlianAccount,
                         @RequestParam("status") Integer status, @RequestParam("pinlianIds") List<Integer> pinlianIds, @RequestParam("platform") Integer platform);


    /**
     * 查询授权的列表，不分页无数据权限
     *
     * @param account        自定义名称
     * @param pinlianAccount 品连平台账号
     * @param status         状态  （0未授权  1 正常授权  2授权过期 3停用）
     * @param pinlianIds     品连的ID列表
     * @param platform       平台 (1 ebay   2 Amazon 3 aliexpress)
     * @return 返回数据
     */
    @GetMapping("/Authorization/findAllNoPageNotLimit")
    @ResponseBody
    String findAllNoPageNotLimit(@RequestParam("account") String account, @RequestParam("pinlianAccount") String pinlianAccount,
                         @RequestParam("status") Integer status, @RequestParam("pinlianIds") List<Integer> pinlianIds, @RequestParam("platform") Integer platform);

    @PostMapping("/ebay/publish/getListingVariantByItemIdPlatformSku")
    String getProductPicture(@RequestParam(value = "platformSku") String sku,@RequestParam(value = "itemId") String itemId);

    /**
     * 查询店铺授权列表信息
     * @param vo
     * @return
     */
    @PostMapping("/empower/getEmpowerSearchVO")
    String getEmpowerSearchVO(@RequestBody EmpowerRequestVo vo);

    /**
     * 断路降级
     */
    @Service
    class RemoteSellerServiceImpl implements RemoteSellerService {

        @Override
        public String getOnlineCount(String seller) {
            return null;
        }

        @Override
        public String findCount(String userName) {
            return null;
        }

        @Override
        public String findAliexpressPublishCount(String userName) {
            return null;
        }

        @Override
        public String selectObjectByAccount(Integer empowerId, String pinlianAccount, Integer status, String account, Integer platform) {
            return null;
        }

        @Override
        public String selectObjectByAccount(Integer empowerId, String pinlianAccount, String account) {
            return null;
        }

        @Override
        public String findAllRemote(Integer platform, Integer status) {
            return null;
        }

        @Override
        public String getByplatformSkuAndSite(GetByplatformSkuAndSiteDTO getByplatformSkuAndSiteDTO) {
            return null;
        }

        @Override
        public String findOneEmpowByAccount(Integer platform, String thirdPartyName, String webName, String empowerId) {
            return null;
        }

        @Override
        public String findAllNoPage(String account, String pinlianAccount, Integer status, List<Integer> pinlianIds, Integer platform) {
            return null;
        }

        @Override
        public String findAllNoPageNotLimit(String account, String pinlianAccount, Integer status, List<Integer> pinlianIds, Integer platform) {
            return null;
        }

        @Override
        public String getProductPicture(String sku, String itemId) {
            return null;
        }

        @Override
        public String getEmpowerSearchVO(EmpowerRequestVo vo) {
            return null;
        }

        public String fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "卖家服务异常")).toString();
        }
    }
}





