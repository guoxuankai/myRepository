package com.brandslink.cloud.finance.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.finance.pojo.entity.StandardQuote;
import com.brandslink.cloud.finance.pojo.vo.StandardQuoteVO;

import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/8/19 11:10
 */
public interface StandardQuoteService extends BaseService<StandardQuote> {

    List<StandardQuote> list(StandardQuoteVO standardQuoteQuery);

    /**
     * 获得生效数据的id
     * @return
     */
    List<Integer> getEffectiveId();

}
