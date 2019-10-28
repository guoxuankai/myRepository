package com.rondaful.cloud.order.service.impl;

import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.utils.UserUtils;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.InvoiceTemplate;
import com.rondaful.cloud.order.model.dto.invoiceTemplate.InvoiceTemplateInsertDTO;
import com.rondaful.cloud.order.model.dto.invoiceTemplate.InvoiceTemplateListSearchDTO;
import com.rondaful.cloud.order.model.dto.invoiceTemplate.InvoiceTemplateUpdateDTO;
import com.rondaful.cloud.order.model.dto.remoteUser.GetSupplyChainByUserIdDTO;
import com.rondaful.cloud.order.model.dto.remoteUser.UserXieRequest;
import com.rondaful.cloud.order.model.vo.invoiceTemplate.InvoiceTemplateDropDownListVO;
import com.rondaful.cloud.order.model.vo.invoiceTemplate.InvoiceTemplateInfoVO;
import com.rondaful.cloud.order.model.vo.invoiceTemplate.InvoiceTemplateListVO;
import com.rondaful.cloud.order.mapper.InvoiceTemplateMapper;
import com.rondaful.cloud.order.remote.RemoteUserService;
import com.rondaful.cloud.order.service.IInvoiceTemplateService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 发票模板业务
 *
 * @author Blade
 * @date 2019-06-17 17:26:44
 **/
@Service
public class InvoiceTemplateServiceImpl implements IInvoiceTemplateService {

    private static Logger LOGGER = LoggerFactory.getLogger(InvoiceTemplateServiceImpl.class);

    @Autowired
    private InvoiceTemplateMapper invoiceTemplateMapper;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private ISystemOrderCommonService systemOrderCommonService;

    public void insert(InvoiceTemplateInsertDTO invoiceTemplateInsertDTO) {
        InvoiceTemplate invoiceTemplate = new InvoiceTemplate();
        BeanUtils.copyProperties(invoiceTemplateInsertDTO, invoiceTemplate);
        invoiceTemplate.setSellerId(userUtils.getUser().getUserid());
        invoiceTemplate.setCreator(userUtils.getUser().getUsername());
        invoiceTemplate.setCreateTime(new Date());
        invoiceTemplateMapper.insertSelective(invoiceTemplate);
    }

    public void delete(long id) {
        invoiceTemplateMapper.deleteByPrimaryKey(id);
    }

    public void update(InvoiceTemplateUpdateDTO invoiceTemplateUpdateDTO) {
        InvoiceTemplate invoiceTemplate = new InvoiceTemplate();
        BeanUtils.copyProperties(invoiceTemplateUpdateDTO, invoiceTemplate);
        invoiceTemplate.setModifier(userUtils.getUser().getUsername());
        invoiceTemplate.setModifiedTime(new Date());
        invoiceTemplateMapper.updateByPrimaryKeySelective(invoiceTemplate);
    }

    public Page<InvoiceTemplateListVO> page(InvoiceTemplateListSearchDTO searchDTO) {
        Page.builder(String.valueOf(searchDTO.getPageNumber()), String.valueOf(searchDTO.getPageSize()));
        List<Integer> accountIds = this.getChildAccountIds(userUtils.getUser().getUserid());
        List<InvoiceTemplateListVO> list = invoiceTemplateMapper.page(searchDTO, accountIds);
        PageInfo<InvoiceTemplateListVO> pageInfo = new PageInfo<>(list);
        return new Page<>(pageInfo);
    }

    public InvoiceTemplateInfoVO findById(Long id) {
        InvoiceTemplateInfoVO invoiceTemplateInfo = new InvoiceTemplateInfoVO();
        InvoiceTemplate invoiceTemplate = invoiceTemplateMapper.selectByPrimaryKey(id);
        BeanUtils.copyProperties(invoiceTemplate, invoiceTemplateInfo);
        invoiceTemplateInfo.setInvoiceTemplateId(invoiceTemplate.getId());
        return  invoiceTemplateInfo;
    }

    public List<InvoiceTemplateDropDownListVO> selectDropDownList(Integer sellerId) {
        if (null == sellerId) {
            sellerId = userUtils.getUser().getUserid();
        }

        List<Integer> accountIds = this.getChildAccountIds(sellerId);
        LOGGER.debug("发票模板下拉列表，TopUserId={}", accountIds);

        return invoiceTemplateMapper.selectDropDownList(accountIds);
    }

    private List<Integer> getChildAccountIds(Integer sellerId) {
        List<Integer> childAccountIds = new ArrayList<>();

        GetSupplyChainByUserIdDTO getSupplyChainByUserIdDTO = systemOrderCommonService
                .getSupplyChinByUserId(sellerId, Constants.System.PLATFORM_TYPE_SELLER);
        Integer topUserId = -1;
        if (null != getSupplyChainByUserIdDTO) {
            topUserId = getSupplyChainByUserIdDTO.getTopUserId();
        }

        List<UserXieRequest> userList = systemOrderCommonService.getChildAccountFromSeller(topUserId);
        for (UserXieRequest user : userList) {
            childAccountIds.add(user.getUserId());
        }
        childAccountIds.add(sellerId);
        childAccountIds.add(topUserId);

        return childAccountIds;
    }
}
