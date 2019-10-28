package com.brandslink.cloud.finance.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.finance.pojo.dto.QuoteConfig.CellsConfigDto;
import com.brandslink.cloud.finance.pojo.entity.QuoteConfig;
import com.brandslink.cloud.finance.pojo.vo.QuoteConfig.AddQuoteConfigVo;
import com.brandslink.cloud.finance.pojo.vo.QuoteConfig.EditorQuoteConfigVo;
import com.brandslink.cloud.finance.pojo.vo.QuoteConfig.EffectiveConfigVo;
import com.brandslink.cloud.finance.pojo.vo.QuoteConfig.QueryQuoteConfigVo;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Author: zhangjinhua
 * @Date: 2019/8/29 17:29
 */
public interface QuoteConfigService extends BaseService<QuoteConfig> {

    /**
     * 配置列表
     * @param quoteConfig
     * @return
     */
    PageInfo<QuoteConfig> getQuoteConfig(QueryQuoteConfigVo quoteConfig);

    /**
     * 增加配置
     * @param quoteConfig
     */
    void addQuoteConfig(AddQuoteConfigVo quoteConfig);

    /**
     * 编辑配置
     * @param quoteConfig
     */
    void editorQuoteConfig(EditorQuoteConfigVo quoteConfig);

    /**
     * 配置提交
     * @param id
     * @return
     */
    Integer configSubmit(Integer id);

    /**
     * 配置生效
     * @param effectiveConfig
     * @return
     */
    Integer configEffective(EffectiveConfigVo effectiveConfig);

    /**
     * 根据版本号获取配置详情
     * @param version
     */
    List<CellsConfigDto> versionInfo(String version);
}
