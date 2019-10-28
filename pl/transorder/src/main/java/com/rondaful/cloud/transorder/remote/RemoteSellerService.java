package com.rondaful.cloud.transorder.remote;


import com.rondaful.cloud.transorder.entity.GetByplatformSkuAndSiteDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

/**
 * 卖家服务查询该卖家有效Token
 */
@FeignClient(name = "rondaful-seller-service", fallback = RemoteSellerService.RemoteSellerServiceImpl.class)
public interface RemoteSellerService {

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


    /**
     * 查询刊登sku的仓库信息
     */
    @PostMapping("/amazon/rest/getByplatformSkuAndSite")
    String getByplatformSkuAndSite(@RequestBody GetByplatformSkuAndSiteDTO getByplatformSkuAndSiteDTO);


    /**
     * 断路降级
     */
    @Service
    class RemoteSellerServiceImpl implements RemoteSellerService {


        @Override
        public String findOneEmpowByAccount(Integer platform, String thirdPartyName, String webName, String empowerId) {
            return null;
        }

        @Override
        public String getByplatformSkuAndSite(GetByplatformSkuAndSiteDTO getByplatformSkuAndSiteDTO) {
            return null;
        }

    }
}





