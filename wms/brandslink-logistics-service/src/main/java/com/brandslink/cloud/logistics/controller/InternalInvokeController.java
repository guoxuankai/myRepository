package com.brandslink.cloud.logistics.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.logistics.VO.MethodCollectorRelationVO;
import com.brandslink.cloud.logistics.model.MethodCollectorRelationModel;
import com.brandslink.cloud.logistics.model.MethodZoneFreightModel;
import com.brandslink.cloud.logistics.service.IMethodCollectorRelationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal")
public class InternalInvokeController {

    @Autowired
    private IMethodCollectorRelationService relationService;

    @GetMapping("/selectMethodByMethodCollectorRelation")
    @ApiOperation(value = "根据仓库编码和揽收商ID查询邮寄方式信息揽收方式 || 内部调用")
    public List<MethodCollectorRelationModel> selectMethodByMethodCollectorRelation(MethodCollectorRelationVO relationVO) {
        MethodCollectorRelationModel relationModel = JSONObject.parseObject(JSON.toJSONString(relationVO), MethodCollectorRelationModel.class);
        List<MethodCollectorRelationModel> list = relationService.selectMethodByMethodCollectorRelation(relationModel);
        return list;
    }
}
