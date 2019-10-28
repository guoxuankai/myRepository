package com.rondaful.cloud.seller.service.impl;

import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.seller.entity.UpcGenerate;
import com.rondaful.cloud.seller.mapper.UpcGenerateMapper;
import com.rondaful.cloud.seller.service.UpcGenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author guoxuankai
 * @date 2019/6/10
 */
@Service
public class UpcGenerateServiceImpl extends BaseServiceImpl<UpcGenerate> implements UpcGenerateService {


    @Autowired
    private UpcGenerateService upcGenerateService;

    @Autowired
    private UpcGenerateMapper upcGenerateMapper;


    /**
     * 获得自动生成的upc码，每次查询之后，将upc码改为已使用
     *
     * @param row     取的条数
     * @return
     */
    @Override
    public synchronized List<UpcGenerate> getUpc(String row) {
        //查询出状态为未使用的upc码
        Page.builder("1", row);
        UpcGenerate upcGenerate = new UpcGenerate();
        upcGenerate.setStatus(0);
        Page<UpcGenerate> page = upcGenerateService.page(upcGenerate);
        PageInfo<UpcGenerate> pageInfo = (PageInfo) page.getPageInfo();
        List<UpcGenerate> list = pageInfo.getList();
        // 将查询出来的upc码从数据库删除
        for (UpcGenerate generate : list) {
            upcGenerateService.deleteByPrimaryKey(generate.getId().longValue());
        }
        return list;
    }

}
