package com.brandslink.cloud.finance.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.finance.pojo.entity.ImportFailure;
import com.brandslink.cloud.finance.pojo.entity.LogisticsFees;
import com.brandslink.cloud.finance.pojo.vo.LogisticsFeesVO;

import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/8/19 11:10
 */
public interface ImportFailureService extends BaseService<ImportFailure> {

    void deleteByIds(int[] ids);


}
