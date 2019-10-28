package com.brandslink.cloud.logistics.service.impl;

import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.logistics.mapper.InvoiceSpecificationMapper;
import com.brandslink.cloud.logistics.mapper.LogisticsMethodMapper;
import com.brandslink.cloud.logistics.model.InvoiceSpecificationModel;
import com.brandslink.cloud.logistics.service.IInvoiceSpecificationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceSpecificationServiceImpl extends BaseServiceImpl<InvoiceSpecificationModel> implements IInvoiceSpecificationService {
    @Autowired
    private InvoiceSpecificationMapper invoiceSpecificationMapper;
    @Autowired
    private LogisticsMethodMapper methodMapper;

    @Override
    public Long editInvoiceSpecification(InvoiceSpecificationModel specificationModel) {
        specificationModel.setExpressSheetSequence(this.getExpressSheetSequence(specificationModel.getProviderId()));
        invoiceSpecificationMapper.insertUpdateSelective(specificationModel);
        return specificationModel.getId();
    }

    public String getExpressSheetSequence(Long providerId) {
        List<String> list = methodMapper.selectAllUsedSequence(providerId);
        List<String> allList = this.getAllLetterCombination();
        if (CollectionUtils.isNotEmpty(list)){
            allList.removeAll(list);
        }
        return allList.get(0);
    }

    private List<String> getAllLetterCombination(){
        List<String> list = new ArrayList<>(676);
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
                list.add(String.valueOf((char)('A' + i)) + String.valueOf((char) ('A' + j)));
            }
        }
        return list;
    }
}
