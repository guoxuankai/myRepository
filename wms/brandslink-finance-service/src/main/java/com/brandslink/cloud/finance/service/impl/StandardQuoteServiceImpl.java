package com.brandslink.cloud.finance.service.impl;

import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.finance.mapper.StandardQuoteMapper;
import com.brandslink.cloud.finance.pojo.entity.StandardQuote;
import com.brandslink.cloud.finance.pojo.vo.StandardQuoteVO;
import com.brandslink.cloud.finance.service.StandardQuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StandardQuoteServiceImpl extends BaseServiceImpl<StandardQuote> implements StandardQuoteService {

    @Autowired
    StandardQuoteMapper StandardQuoteMapper;

    @Override
    public List<StandardQuote> list(StandardQuoteVO standardQuoteQuery) {
        return StandardQuoteMapper.list(standardQuoteQuery);
    }

    @Override
    public List<Integer> getEffectiveId() {
        return StandardQuoteMapper.getEffectiveId();
    }
}
