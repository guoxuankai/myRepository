package com.rondaful.cloud.commodity.service.impl;

import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.commodity.entity.Brand;
import com.rondaful.cloud.commodity.entity.CommodityBase;
import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.commodity.entity.Message;
import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.commodity.mapper.BrandMapper;
import com.rondaful.cloud.commodity.remote.RemoteUserService;
import com.rondaful.cloud.commodity.service.IBrandService;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.rabbitmq.MessageSender;
import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.common.utils.RemoteUtil;
import com.rondaful.cloud.common.utils.Utils;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BrandServiceImpl extends BaseServiceImpl<Brand> implements IBrandService {

    private final static Logger log = LoggerFactory.getLogger(BrandServiceImpl.class);

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private MessageSender messageSender;


    @Override
    @Cacheable(value = "brandCache", keyGenerator = "keyGenerator")
    public Page selectBranchList(String page, String row,Brand brand) {
    	Page.builder(page, row);
        List<Brand> list1 = brandMapper.page(brand);
        if (list1 != null && list1.size()>0) {
			for (Brand brand2 : list1) {
				brand2.setCountry(Utils.translation(brand2.getCountry()));
			}
		}
        constructionSupplier(list1);
        PageInfo pageInfo = new PageInfo(list1);
        return new Page(pageInfo);
    }

    @Override
    public void auditNoticMeassage(Integer stat, Brand brand) {
        List<Brand> list = new ArrayList<Brand>(){{
            this.add(brand);
        }};
        constructionSupplier(list);
        for (Brand br : list) {
            Message msg = new Message();
            msg.setMessageCategory("COMMODITY_MESSAGE");
            msg.setMessageContent(br.getBrandName());
            msg.setMessagePlatform("0");
            msg.setMessageScceptUserName(br.getName());
            msg.setReceiveSys("0");
            if (br.getState().intValue() == 1) {
                msg.setMessageType("BRAND_AUDITING_SUCCESSFUL");
            } else if (br.getState().intValue() == 2) {
                msg.setMessageType("BRAND_AUDITING_FAILED");
            }
            messageSender.sendMessage(JSONObject.fromObject(msg).toString());
        }
    }



    /**
     * 服务远程调用构造供应商信息
     *
     * @param brands
     */
    public void constructionSupplier(List<Brand> brands) {
        try {
            Set<Long> list = new HashSet<Long>() {{
                for (Brand b : brands) {
                    this.add(b.getSupplierId());
                }
            }};
            RemoteUtil.invoke(remoteUserService.getSupplierList(list, 0));
            List<Map> result = RemoteUtil.getList();
            if (result != null && !result.isEmpty()) {
                for (Brand b : brands) {
                    for (int i=0;i<result.size();i++) {
                        if (Long.valueOf((Integer) ((Map)result.get(i)).get("userId")) == b.getSupplierId().longValue()) {
                        	String companyName=(String) ((Map)result.get(i)).get("companyName");
                            b.setSupplierName(Utils.translation(companyName));
                            b.setName((String) ((Map)result.get(i)).get("loginName"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
