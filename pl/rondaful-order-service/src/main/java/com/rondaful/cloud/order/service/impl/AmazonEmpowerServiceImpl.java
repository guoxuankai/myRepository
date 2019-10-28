package com.rondaful.cloud.order.service.impl;

import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.order.entity.Amazon.AmazonEmpower;
import com.rondaful.cloud.order.mapper.AmazonEmpowerMapper;
import com.rondaful.cloud.order.service.IAmazonEmpowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2019-01-17 17:26
 * 包名: com.rondaful.cloud.order.service.impl
 * 描述:
 */
@Service
public class AmazonEmpowerServiceImpl extends BaseServiceImpl<AmazonEmpower> implements IAmazonEmpowerService {
    @Autowired
    private AmazonEmpowerMapper amazonEmpowerMapper;

    @Override
    public List<String> selectMWSTokenBySellerId(String sellerId, String marketplaceId) {
        return amazonEmpowerMapper.selectMWSTokenBySellerId(sellerId,marketplaceId);
    }
}
