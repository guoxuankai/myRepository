package com.brandslink.cloud.finance.service.impl;

import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.finance.mapper.SysAccountFlowMapper;
import com.brandslink.cloud.finance.pojo.dto.SysAccountFlowDto;
import com.brandslink.cloud.finance.pojo.entity.SysAccountFlow;
import com.brandslink.cloud.finance.pojo.vo.SysAccountFlowVo;
import com.brandslink.cloud.finance.service.SysAccountFlowService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yangzefei
 * @Classname SysAccountFlowServiceImpl
 * @Description 客户资金流水服务
 * @Date 2019/8/26 10:08
 */
@Service
public class SysAccountFlowServiceImpl extends BaseServiceImpl<SysAccountFlow> implements SysAccountFlowService {

    @Resource
    private SysAccountFlowMapper sysAccountFlowMapper;

    public List<SysAccountFlowDto> getList(SysAccountFlowVo param){
        if(!param.isValidSort("createTime",SysAccountFlowDto.class)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"排序字段或者排序方式不正确");
        }
        List<SysAccountFlowDto> list= sysAccountFlowMapper.getList(param);
        list.stream().forEach(p->p.setSerialNo(list.indexOf(p)+1));
        return list;
    }
}
