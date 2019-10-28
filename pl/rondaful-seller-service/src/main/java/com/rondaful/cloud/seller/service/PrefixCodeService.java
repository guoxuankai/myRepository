package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.seller.entity.PrefixCode;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/6/10
 */
@Transactional
public interface PrefixCodeService extends BaseService<PrefixCode> {
    List<String> generateRandomUpc(String code);

    boolean isRepeat(String code);

    int getUpcAmout();
}
