package com.brandslink.cloud.finance.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.finance.pojo.entity.LogisticsFees;
import com.brandslink.cloud.finance.pojo.vo.LogisticsCostVo;
import com.brandslink.cloud.finance.pojo.vo.LogisticsFeesVO;

import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/8/19 11:10
 */
public interface LogisticsFeesService extends BaseService<LogisticsFees> {


    List<LogisticsFees> list(LogisticsFeesVO logisticsFeesQuery);

    void updateStatus(int[] idArr, int status);

    /**
     * 导入物流商信息(修改物流实重、计重、运费)
     * @param logisticsFees 物流商收费实体
     */
    void importData(List<LogisticsFees> logisticsFees);

    /**
     * 新增物流费记录
     * @param param
     */
    void save(LogisticsCostVo param);
}
