package com.rondaful.cloud.transorder.service;

import com.rondaful.cloud.transorder.entity.ConvertOrderVO;

import java.util.List;

public interface TransferService {

    List<ConvertOrderVO> transfer(List<String> orderIdList, TransferContext context);

}
