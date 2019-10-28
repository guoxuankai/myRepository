package com.brandslink.cloud.finance.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.finance.pojo.dto.QuoteConfig.CellsConfigDto;
import com.brandslink.cloud.finance.pojo.entity.QuoteConfig;
import com.brandslink.cloud.finance.pojo.vo.QuoteConfig.*;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuoteConfigMapper extends BaseMapper<QuoteConfig> {


    /**
     * 获取配置列表
     * @param quoteConfig
     * @return
     */
    List<QuoteConfig> getQuoteConfig(QueryQuoteConfigVo quoteConfig);
    /**
     * 增加配置信息
     * @param quoteConfig
     * @return
     */
    int addQuoteConfig(AddQuoteConfigVo quoteConfig);
    /**
     * 插入配置行信息
     * @param rowCla
     * @return
     */
    int insertConfigRow(ConfigRowCla rowCla);
    /**
     * 插入配置详情
     * @param cellsCla
     * @return
     */
    int insertConfigCells(ConfigCellsCla cellsCla);

    /**
     * 修改配置行信息
     * @param rowList
     * @return
     */
    int updateConfigRow( List<ConfigRowForUpdateVo> rowList);

    /**
     * 修改配置详细信息
     * @param cellsList
     * @return
     */
    int updateConfigCells( List<ConfigCellsForUpdateVo> cellsList);

    /**
     * 获取配置状态
     * @param version
     * @return
     */
    Integer getConfigState(@Param("version") String version);

    /**
     * 配置提交
     * @param id
     */
    Integer configSubmit(Integer id);

    Integer judgeEffective(EffectiveConfigVo effectiveConfig);

    void configEffective(EffectiveConfigVo effectiveConfig);

    List<QuoteConfig> getNowEffective();

    void loseEfficacy(List<QuoteConfig> quoteConfigList);

    void effective();

    /**
     * 根据版本号获取配置详情
     * @param version
     */
    List<CellsConfigDto> versionInfo(@Param("version") String version);
}