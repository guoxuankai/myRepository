package com.brandslink.cloud.finance.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.finance.pojo.dto.CustomerConfig.*;
import com.brandslink.cloud.finance.pojo.entity.CustomerConfigEntity;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.AddCustomerConfigVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.EditorCustomerVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.EffectiveCstomerVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.QueryCustomerConfigVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerConfigMapper extends BaseMapper<CustomerConfigEntity> {
    /**
     * 获取客户列表
     * @return
     */
    List<SelectCustomerDto> selectCustomer();

    /**
     * 获取客户报价
     * @param queryCustomer
     * @return
     */
    List<QueryCustomerConfigDto> queryQuote(QueryCustomerConfigVo queryCustomer);

    /**
     * 新增客户报价
     * @param customerConfig
     * @return
     */
    int addCustomerConfig(AddCustomerConfigVo customerConfig);

    /**
     * 编辑客户报价
     * @param editorCustomer
     * @return
     */
    int editorCustomerConfig(EditorCustomerVo editorCustomer);

    /**
     * 客户报价提交
     * @param id
     * @return
     */
    int customerSubmit(Integer id);

    /**
     * 获取是否有多条同时间的报价
     * @param effectiveCstomer
     * @return
     */
    Integer judgeEffective(EffectiveCstomerVo effectiveCstomer);

    Integer customerEffective(EffectiveCstomerVo effectiveCstomer);

    List<EffectiveCstomerVo> getNowEffective();

    Integer loseEfficacy(List<EffectiveCstomerVo> effectiveCstomerList);

    Integer effective();

    /**
     * 根据版本号获取客户报价详情
     * @param version
     * @return
     */
    CustomerConfigEntity getCustomerInfo(@Param("version") String version);
}