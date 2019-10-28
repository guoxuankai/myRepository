package com.brandslink.cloud.finance.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.finance.pojo.entity.StandardQuoteDetail;

import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/8/19 11:10
 */
public interface StandardQuoteDetailService extends BaseService<StandardQuoteDetail> {

    List<StandardQuoteDetail> getByQuoteId(Integer quoteId);

}
