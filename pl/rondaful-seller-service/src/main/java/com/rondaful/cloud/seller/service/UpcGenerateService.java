package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.seller.entity.UpcGenerate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/6/12
 */
@Transactional
public interface UpcGenerateService extends BaseService<UpcGenerate> {

    List<UpcGenerate> getUpc(String row);


}
