package com.rondaful.cloud.seller.remote;


import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "rondaful-order-service", fallback = RemoteCountryService.RemoteCountryServiceImpl.class)
public interface RemoteCountryService {

    /**
     * 根据条件查询国家列表
     *
     * @param id        id
     * @param iso       国家双字母缩写
     * @param iso3      国家三字母缩写
     * @param name      国家英文名称
     * @param nameZh    国家中文名称
     * @param nicename  国家英文昵称
     * @param numcode   国家数字编号
     * @param phonecode 国家长途区号
     * @return 返回数据
     */
    @RequestMapping(value = "/country/queryList", method = RequestMethod.GET)
    String queryList(@RequestParam("id") Integer id,
                     @RequestParam("iso") String iso,
                     @RequestParam("iso3") String iso3,
                     @RequestParam("name") String name,
                     @RequestParam("nameZh") String nameZh,
                     @RequestParam("nicename") String nicename,
                     @RequestParam("numcode") Short numcode,
                     @RequestParam("phonecode") Integer phonecode
    );

    @Service
    class RemoteCountryServiceImpl implements RemoteCountryService {

        @Override
        public String queryList(Integer id, String iso, String iso3, String name, String nameZh, String nicename, Short numcode, Integer phonecode) {
            return null;
        }
    }


}
