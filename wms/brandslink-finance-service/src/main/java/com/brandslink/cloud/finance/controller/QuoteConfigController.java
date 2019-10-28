package com.brandslink.cloud.finance.controller;

import com.brandslink.cloud.finance.pojo.dto.QuoteConfig.CellsConfigDto;
import com.brandslink.cloud.finance.pojo.entity.QuoteConfig;
import com.brandslink.cloud.finance.pojo.vo.QuoteConfig.AddQuoteConfigVo;
import com.brandslink.cloud.finance.pojo.vo.QuoteConfig.EditorQuoteConfigVo;
import com.brandslink.cloud.finance.pojo.vo.QuoteConfig.EffectiveConfigVo;
import com.brandslink.cloud.finance.pojo.vo.QuoteConfig.QueryQuoteConfigVo;
import com.brandslink.cloud.finance.service.QuoteConfigService;
import com.brandslink.cloud.finance.utils.VersionsCreateUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @Author: zhangjinhua
 * @Date: 2019/8/29 17:13
 */

@Slf4j
@RestController
@RequestMapping("/quoteConfiguration")
@Api("配置列表")
public class QuoteConfigController {
    @Autowired
    QuoteConfigService quoteConfigService;
    @Autowired
    RedisTemplate redisTemplate;


    @ApiOperation(value = "配置列表", notes = "配置列表")
    @RequestMapping(value = "/queryQuote", method = RequestMethod.POST)

    public PageInfo<QuoteConfig> getQuoteConfig(@RequestBody QueryQuoteConfigVo quoteConfig) {

        return quoteConfigService.getQuoteConfig(quoteConfig);
    }

    @ApiOperation(value = "新增配置", notes = "新增配置")
    @RequestMapping(value = "/addQuote", method = RequestMethod.POST)
    public String getQuoteConfig(@RequestBody AddQuoteConfigVo quoteConfig) {
        String version = VersionsCreateUtil.configVersionsProcessor(redisTemplate, quoteConfig.getConfigType());
        quoteConfig.setVersion(version);
        quoteConfigService.addQuoteConfig(quoteConfig);
        return "增加成功";
    }

    @ApiOperation(value = "编辑配置", notes = "编辑配置")
    @RequestMapping(value = "/editorQuoteConfig", method = RequestMethod.POST)
    public String editorQuoteConfig(@RequestBody EditorQuoteConfigVo quoteConfig) {
        quoteConfigService.editorQuoteConfig(quoteConfig);
        return "修改成功";
    }


    @ApiOperation(value = "生效", notes = "生效")
    @RequestMapping(value = "/configEffective", method = RequestMethod.POST)
    public String configEffective(EffectiveConfigVo effectiveConfig) {
        quoteConfigService.configSubmit(effectiveConfig.getId());
        quoteConfigService.configEffective(effectiveConfig);
        return "提交生效成功！";
    }

    @ApiOperation(value = "版本号查详情", notes = "版本号查详情")
    @RequestMapping(value = "/versionInfo", method = RequestMethod.POST)
    public List<CellsConfigDto> versionInfo(String version) {

        return quoteConfigService.versionInfo(version);
    }


}
