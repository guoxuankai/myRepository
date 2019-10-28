package com.rondaful.cloud.transorder.controller;

import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.transorder.entity.ConvertOrderVO;
import com.rondaful.cloud.transorder.enums.PlatformTypeEnum;
import com.rondaful.cloud.transorder.service.TransferContext;
import com.rondaful.cloud.transorder.service.TransferService;
import com.rondaful.cloud.transorder.service.impl.AliexpressTransferStrategy;
import com.rondaful.cloud.transorder.service.impl.AmazonTransferStrategy;
import com.rondaful.cloud.transorder.service.impl.EbayTransferStrategy;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@Api(description = "转单")
@RequestMapping(value = "/transfer")
public class TransferController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(TransferController.class);


    @Autowired
    private TransferService TransferService;


    @Autowired
    private AliexpressTransferStrategy aliexpressTransferStrategy;

    @Autowired
    private EbayTransferStrategy ebayTransferStrategy;


    @ApiOperation(value = "转单", notes = "")
    @PostMapping("/transfer")
    public List<ConvertOrderVO> transfer(@RequestBody List<String> orderIdList, @RequestParam("platformType") int platformType) {
//        List<String> list = new ArrayList<>();
//        list.add("392335598634-925093879026");
//        list.add("392384979871-936833875026");
//        list.add("392388661838-936584554026");
//        int platformType = 2;
        TransferContext context = null;
        if (PlatformTypeEnum.AMAZON.getCode() == platformType) {
            context = new TransferContext(new AmazonTransferStrategy());
        } else if (PlatformTypeEnum.EBAY.getCode() == platformType) {
            context = new TransferContext(ebayTransferStrategy);
        } else if (PlatformTypeEnum.APLIEXPRESS.getCode() == platformType) {
            context = new TransferContext(aliexpressTransferStrategy);
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "找不到相应的平台类型");
        }
        List<ConvertOrderVO> resultList = TransferService.transfer(orderIdList, context);
        return resultList;
    }

}
