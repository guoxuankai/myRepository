package com.rondaful.cloud.commodity.controller;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.commodity.entity.CommodityBase;
import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.commodity.entity.SpuTortRecord;
import com.rondaful.cloud.commodity.enums.CountryCodeEnum;
import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.commodity.mapper.CommodityBaseMapper;
import com.rondaful.cloud.commodity.mapper.CommoditySpecMapper;
import com.rondaful.cloud.commodity.rabbitmq.MQSender;
import com.rondaful.cloud.commodity.remote.RemoteOrderService;
import com.rondaful.cloud.commodity.service.ISpuTortService;
import com.rondaful.cloud.commodity.service.MessageService;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RemoteUtil;
import com.rondaful.cloud.common.utils.Utils;

import io.swagger.annotations.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.*;

/**
 * @Author: luozheng
 * @BelongsPackage:com.rondaful.cloud.commodity.controller
 * @Date: 2019-05-23 09:45:46
 * @FileName:${FILENAME}
 * @Description:
 */
@Api(description = "Sku侵权接口")
@RequestMapping("/tort")
@RestController
public class SkuTortController extends BaseController {
    private final static Logger log = LoggerFactory.getLogger(SkuTortController.class);

    @Autowired
    private ISpuTortService spuTortService;
    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;
    @Autowired
    private CommoditySpecMapper commoditySpecMapper;
	@Autowired
    private MessageService messageService;
	@Autowired
    private CommodityBaseMapper commodityBaseMapper;

	@Autowired
    private RemoteOrderService remoteOrderService;
	
	@Autowired
    private MQSender mqSender;
	
	

    /**功能描述 添加sku侵权信息
     * @date 2019/5/23
     * @param
     * @return void
     * @author lz
     * version 2.3.0
     */
    @PostMapping("/addSpuTort")
    @ApiOperation(value = "添加侵权信息", notes = "添加Spu侵权信息")
    @AspectContrLog(descrption = "添加Spu侵权信息", actionType = SysLogActionType.ADD)
    public void add(@RequestBody @Valid SpuTortRecord skuTortRecord, BindingResult bindingResult) {
        //校验前端传递参数是否为空或参数错误，如果错误就抛出异常
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        try {
            //获取当前登录账号，添加时新增操作人
            String userName = getLoginUserInformationByToken.getUserInfo().getUser().getUsername();
            skuTortRecord.setOptUser(userName);
            //英文下 站点转成对应的中文存
            if (isEnNameSearch() && StringUtils.isNotBlank(skuTortRecord.getSiteName())) {
            	skuTortRecord.setSiteName(CountryCodeEnum.getNameCnByNameEn(skuTortRecord.getSiteName()));
			}
        } catch (Exception e) {
            log.error("获取不到当前的登录账号，登录账号为空，用户服务异常");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取不到当前登录账号");
        }

        Map spu=new HashMap();
        spu.put("SPU",skuTortRecord.getSystemSpu());
        spu.put("isUp",true);
        List<CommodityBase> commodityBases=  commodityBaseMapper.selectCommodityListBySpec(spu);
        if (commodityBases == null || commodityBases.isEmpty()) {
            log.error("商品spu不存在，无法添加侵权记录");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品spu不存在或商品未上架");
        }
        int add = spuTortService.insertSelective(skuTortRecord);
        if (add <= 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "添加侵权信息失败");
        }
        
        Map<String,Object> param=new HashMap<String, Object>();
        param.put("SPU", skuTortRecord.getSystemSpu());
        param.put("isUp",true);
        List<CommoditySpec> skuList=commoditySpecMapper.selectSkuList(param);
        // 1：eBay，2：Amazon，3：wish，4：AliExpress
        String platform="";
        if (skuTortRecord.getPlatform() != null) {
			if (skuTortRecord.getPlatform().intValue()==1) {
				platform="eBay";
			}else if (skuTortRecord.getPlatform().intValue()==2) {
				platform="Amazon";
			}else if (skuTortRecord.getPlatform().intValue()==3) {
				platform="Wish";
			}else if (skuTortRecord.getPlatform().intValue()==4) {
				platform="AliExpress";
			}
		}
        
        //获取刊登记录里的卖家账号和ID，发送的平台用填写的侵权平台
        for (CommoditySpec commoditySpec : skuList) {
        	// MQ通知亚马逊刊登卖家端
            if (skuTortRecord.getPlatform().intValue()==2) {
            	mqSender.skuTortMq(commoditySpec.getSystemSku());
			}
            
        	//cms通知
            RemoteUtil.invoke(remoteOrderService.queryMapsNoLimit(commoditySpec.getSystemSku()));
            List<Map> list = RemoteUtil.getList();
            if (list != null && list.size() > 0) {
            	Set<String> keySet=new HashSet<String>();
            	for (Map map : list) {
            		StringBuilder sb=new StringBuilder();
                    String userId = (String) map.get("sellerPlId");
                    String account = (String) map.get("sellerPlAccount");
                    if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(account)) {
                    	keySet.add(sb.append(userId).append("==").append(account).toString());
					}
                }
            	for (String key : keySet) {
            		messageService.tortMsg(key.split("==")[0], key.split("==")[1], commoditySpec.getSystemSku(), platform);
				}
            }
		}
    }

    @ApiOperation(value = "分页查询spu侵权信息列表", notes = "查询spu侵权信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "创建时间开始时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "创建时间结束时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "systemSpu", value = "品连spu", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "platform", value = "侵权平台(1：eBay，2：Amazon，3：wish，4：AliExpress)", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "tortType", value = "侵权类型，1：商标侵权，2：专利侵权，3：著作权侵权，4：其他侵权", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "tortStartTime", value = "侵权时间开始时间[yyyy-mm-dd]", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "tortEndTime", value = "侵权时间结束时间[yyyy-mm-dd]", dataType = "string", paramType = "query")
    })
    @PostMapping("/searchTort")
    @RequestRequire(require = "page,row", parameter = String.class)
    @AspectContrLog(descrption = "分页查询spu侵权信息列表", actionType = SysLogActionType.QUERY)
    public String searchTort(@RequestParam("page") String page, @RequestParam("row") String row, @ApiIgnore SpuTortRecord skuTortRecord) {
    	Page.builder(page, row);
        //, SerializerFeature.WriteMapNullValue  字段为空显示null
        return JSONObject.toJSONString(spuTortService.page(skuTortRecord));
    }

    @ApiOperation(value = "根据id获取对应的侵权信息", notes = "根据id获取对应的侵权信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "侵权id", dataType = "int", paramType = "path", required = true)}
    )
    @GetMapping("/getTort/{id}")
    @AspectContrLog(descrption = "根据id获取侵权信息", actionType = SysLogActionType.QUERY)
    public String getTort(@PathVariable Integer id) {
        SpuTortRecord skuTortRecord = spuTortService.selectByPrimaryKey(Long.valueOf(id));
        if (skuTortRecord == null) {
            log.error("id不存在无法获取侵权信息");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id不存在");
        }
        skuTortRecord.setSiteName(Utils.translation(skuTortRecord.getSiteName()));
        return JSONObject.toJSONString(skuTortRecord);
    }

    @ApiOperation(value = "根据id删除侵权信息", notes = "根据id删除侵权信息")
    @ApiImplicitParam(name = "id", value = "侵权id", dataType = "int", paramType = "path", required = true)
    @GetMapping("/delTort/{id}")
    @AspectContrLog(descrption = "根据id删除侵权信息", actionType = SysLogActionType.DELETE)
    public void delTort(@PathVariable Integer id) {
        Integer del = spuTortService.deleteByPrimaryKey(Long.valueOf(id));
        if (del <= 0) {
            log.error("id参数错误，无法删除数据");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id参数错误");
        }
    }

    @ApiOperation(value = "根据id修改侵权信息", notes = "根据id修改侵权信息")
    @PostMapping("/updateTort")
    @AspectContrLog(descrption = "根据id修改侵权信息", actionType = SysLogActionType.UDPATE)
    public Integer updateTort(@RequestBody SpuTortRecord skuTortRecord) {
    	 //英文下 站点转成对应的中文存
    	if (isEnNameSearch() && StringUtils.isNotBlank(skuTortRecord.getSiteName())) {
        	skuTortRecord.setSiteName(CountryCodeEnum.getNameCnByNameEn(skuTortRecord.getSiteName()));
		}
        skuTortRecord.setUpdateTime(new Date());
        Integer up = spuTortService.updateByPrimaryKeySelective(skuTortRecord);
        if (up <= 0) {
            log.error("id参数不正确，不能更新侵权信息");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id参数错误");
        }
        return up;
    }
}
