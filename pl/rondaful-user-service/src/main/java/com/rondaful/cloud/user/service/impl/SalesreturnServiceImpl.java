package com.rondaful.cloud.user.service.impl;

import com.rondaful.cloud.user.entity.Salesreturn;
import com.rondaful.cloud.user.mapper.SalesreturnMapper;
import com.rondaful.cloud.user.service.SalesreturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("salesreturnService")
public class SalesreturnServiceImpl implements SalesreturnService {

    @Autowired
    private SalesreturnMapper salesreturnMapper;

    /**
     * 判断当前用户的退货信息是否存在
     * @param userId
     * @return
     */
    @Override
    public Integer isSalesreturn(Integer userId, Integer platformType) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("userId",userId);
        map.put("platformTypes",platformType);
        Integer result = null;
        if (userId != null)  result = salesreturnMapper.isSalesreturn(map);
        return result;
    }

    /**
     * 新增用户的退货信息
     * @param salesreturn
     * @return
     */
    @Override
    public Integer insertSalesreturn(Salesreturn salesreturn, Integer platformType) {
        salesreturn.setPlatformType(platformType);
        Integer result = null;
        if (salesreturn != null) result= salesreturnMapper.insertSalesreturn(salesreturn);
        return result;
    }

    /**
     *修改用户的退货信息
     * @param salesreturn
     * @return
     */
    @Override
    public Integer updateSalesreturn(Salesreturn salesreturn,Integer platformType) {
        salesreturn.setPlatformType(platformType);
        Integer result = null;
        if (salesreturn != null) result = salesreturnMapper.updateSalesreturn(salesreturn);
        return result;
    }



}
