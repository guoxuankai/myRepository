package com.brandslink.cloud.finance.service.impl;

import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.finance.mapper.ImportFailureMapper;
import com.brandslink.cloud.finance.mapper.LogisticsFeesMapper;
import com.brandslink.cloud.finance.pojo.entity.ImportFailure;
import com.brandslink.cloud.finance.pojo.entity.LogisticsFees;
import com.brandslink.cloud.finance.pojo.vo.LogisticsFeesVO;
import com.brandslink.cloud.finance.service.ImportFailureService;
import com.brandslink.cloud.finance.service.LogisticsFeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImportFailureServiceImpl extends BaseServiceImpl<ImportFailure> implements ImportFailureService {

    @Autowired
    ImportFailureMapper importFailureMapper;


    @Override
    public void deleteByIds(int[] ids) {
        importFailureMapper.deleteByIds(ids);
    }
}
