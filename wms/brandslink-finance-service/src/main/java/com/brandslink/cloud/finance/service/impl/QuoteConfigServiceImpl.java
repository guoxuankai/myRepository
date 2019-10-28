package com.brandslink.cloud.finance.service.impl;

import com.brandslink.cloud.common.entity.UserDetailInfo;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.finance.mapper.QuoteConfigMapper;
import com.brandslink.cloud.finance.pojo.dto.QuoteConfig.CellsConfigDto;
import com.brandslink.cloud.finance.pojo.entity.QuoteConfig;
import com.brandslink.cloud.finance.pojo.vo.QuoteConfig.AddQuoteConfigVo;
import com.brandslink.cloud.finance.pojo.vo.QuoteConfig.EditorQuoteConfigVo;
import com.brandslink.cloud.finance.pojo.vo.QuoteConfig.EffectiveConfigVo;
import com.brandslink.cloud.finance.pojo.vo.QuoteConfig.QueryQuoteConfigVo;
import com.brandslink.cloud.finance.service.QuoteConfigService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: zhangjinhua
 * @Date: 2019/8/29 17:37
 */
@Slf4j
@Service
public class QuoteConfigServiceImpl extends BaseServiceImpl<QuoteConfig> implements QuoteConfigService {

    @Autowired
    QuoteConfigMapper quoteConfigMapper;
    @Autowired
    GetUserDetailInfoUtil getUserDetailInfoUtil;

    @Override
    public PageInfo<QuoteConfig> getQuoteConfig(QueryQuoteConfigVo quoteConfig) {
        return new PageInfo<QuoteConfig>(quoteConfigMapper.getQuoteConfig(quoteConfig));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addQuoteConfig(AddQuoteConfigVo quoteConfig) {
        UserDetailInfo userDetailInfo = getUserDetailInfoUtil.getUserDetailInfo();
        quoteConfig.setCreateBy(userDetailInfo.getName());
        quoteConfigMapper.addQuoteConfig(quoteConfig);
        if (quoteConfig.getId() <= 0) {
            log.error("插入数据库tf_sys_config失败：" + quoteConfig.toString());

            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "系统异常,数据插入失败");
        }
        if (quoteConfig.getConfigType() == 1) {
            quoteConfig.getRowList().forEach((rowCla) -> {
                rowCla.setConfigId(quoteConfig.getId());
                rowCla.setCreateBy(userDetailInfo.getName());
                quoteConfigMapper.insertConfigRow(rowCla);
                if (rowCla.getId() <= 0) {
                    log.error("插入数据库tf_sys_config_row失败：" + rowCla.toString());
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "系统异常,数据插入失败");
                }
                rowCla.getCellsList().forEach((cellsCla) -> {
                    cellsCla.setRowId(rowCla.getId());
                    cellsCla.setConfigId(quoteConfig.getId());
                    cellsCla.setCreateBy(userDetailInfo.getName());
                    quoteConfigMapper.insertConfigCells(cellsCla);
                });
            });
            return;
        }
        quoteConfig.getCellsList().forEach((cellsCla) -> {
            cellsCla.setConfigId(quoteConfig.getId());
            cellsCla.setCreateBy(userDetailInfo.getName());
            quoteConfigMapper.insertConfigCells(cellsCla);
        });
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editorQuoteConfig(EditorQuoteConfigVo quoteConfig) {

        Integer state=quoteConfigMapper.getConfigState(quoteConfig.getVersion());
        if(state!=1){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "当前数据已提交，不可编辑");
        }
        quoteConfigMapper.updateConfigRow(quoteConfig.getRowList());
        quoteConfigMapper.updateConfigCells(quoteConfig.getCellsList());
    }

    @Override
    public Integer configSubmit(Integer id) {
        quoteConfigMapper.configSubmit(id);
        return null;
    }

    @Override
    public Integer configEffective(EffectiveConfigVo effectiveConfig) {

        Integer flag=quoteConfigMapper.judgeEffective(effectiveConfig);
        if (flag != null && flag > 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "当前配置类型的生效日期已存在重复数据，不可保存");
        }
        UserDetailInfo userDetailInfo=getUserDetailInfoUtil.getUserDetailInfo();
        effectiveConfig.setEffectiveDate(userDetailInfo.getName());

        quoteConfigMapper.configEffective(effectiveConfig);

        return null;
    }

    @Override
    public List<CellsConfigDto> versionInfo(String version) {
        return quoteConfigMapper.versionInfo(version);
    }
}
