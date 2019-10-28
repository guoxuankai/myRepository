package com.rondaful.cloud.user.storage.controller;

import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.user.storage.entity.StorageApply;
import com.rondaful.cloud.user.storage.service.WarehousingStorageService;
import com.rondaful.cloud.user.storage.utils.StorageValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(description = "仓储物流商用户管理")
public class WarehousingStorageController {

    private Logger logger = LoggerFactory.getLogger(WarehousingStorageController.class);

    @Autowired
    private WarehousingStorageService warehousingStorageService;

    @AspectContrLog(descrption = "仓储物流商申请",actionType = SysLogActionType.QUERY)
    @ApiOperation(value ="仓储物流商申请")
    @PostMapping("/public/warehousingUserApply")
    public void warehousingUserApply(@RequestBody StorageApply storageApply){
        try {
            if (storageApply == null)
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"请提交申请信息");
            dateMatch(storageApply);//数据校验
            Integer result = warehousingStorageService.warehousingUserApply(storageApply);
        } catch (GlobalException e) {
            logger.error("仓储物流商申请失败",e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        } catch (Exception e){
            logger.error("仓储物流商申请失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    /**
     *  数据校验
     * @param storageApply
     */
    public void dateMatch(StorageApply storageApply){
        if (StringUtils.isNotBlank(storageApply.getCompanyName())){
            if ( !StorageValidator.isUsername(storageApply.getCompanyName()) )
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"公司名称格式错误");
        }
        if (StringUtils.isNotBlank(storageApply.getSite())){
            if (!StorageValidator.isAddress(storageApply.getSite()))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"地址名称格式错误");
        }
        if (StringUtils.isNotBlank(storageApply.getLinkman())){
            if ( !StorageValidator.isLinke(storageApply.getLinkman()) )
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"联系人格式错误");
        }
        if (StringUtils.isNotBlank(storageApply.getJob())){
            if ( !StorageValidator.isJob(storageApply.getJob()) )
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"职位名称格式错误");
        }
        if (StringUtils.isNotBlank(storageApply.getPhone())){
            if (!StorageValidator.isPhone(storageApply.getPhone()))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"手机格式错误");
        }
        if (StringUtils.isNotBlank(storageApply.getEmail())){
            if (!StorageValidator.isEmail(storageApply.getEmail()))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"邮箱格式错误");
        }

    }

}
