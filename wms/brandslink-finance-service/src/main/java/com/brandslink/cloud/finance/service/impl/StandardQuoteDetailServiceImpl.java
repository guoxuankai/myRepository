package com.brandslink.cloud.finance.service.impl;

import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.finance.constants.QuoteStatusConstant;
import com.brandslink.cloud.finance.mapper.StandardQuoteDetailMapper;
import com.brandslink.cloud.finance.mapper.StandardQuoteMapper;
import com.brandslink.cloud.finance.pojo.entity.StandardQuote;
import com.brandslink.cloud.finance.pojo.entity.StandardQuoteDetail;
import com.brandslink.cloud.finance.service.StandardQuoteDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StandardQuoteDetailServiceImpl extends BaseServiceImpl<StandardQuoteDetail> implements StandardQuoteDetailService {

    @Autowired
    StandardQuoteDetailMapper standardQuoteDetailMapper;

    @Autowired
    StandardQuoteMapper standardQuoteMapper;


    @Override
    public List<StandardQuoteDetail> getByQuoteId(Integer quoteId) {
        return standardQuoteDetailMapper.getByQuoteId(quoteId);
    }

    @Override
    public int insert(StandardQuoteDetail standardQuoteDetail) {
        StandardQuote standardQuote = new StandardQuote();
        standardQuote.setQuoteStatus(QuoteStatusConstant.TO_SUBMIT);
        standardQuote.setQuoteType(standardQuoteDetail.getQuoteType());
        standardQuote.setSubmitTime(new Date());
        standardQuoteMapper.insert(standardQuote);
        standardQuoteDetailMapper.insert(standardQuoteDetail);
        return 1;
    }
}
