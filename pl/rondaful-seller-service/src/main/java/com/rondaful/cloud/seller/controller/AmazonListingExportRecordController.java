package com.rondaful.cloud.seller.controller;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.UserUtils;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.seller.entity.AmazonListingExportRecord;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.amazon.AmazonCategory;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.service.AmazonCategoryService;
import com.rondaful.cloud.seller.service.AmazonListingExportRecordService;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/exportRecord")
@Api(description = "Amazon导出liesting接口-tkx")
public class AmazonListingExportRecordController {
    private final Logger logger = LoggerFactory.getLogger(AmazonListingExportRecordController.class);

    @Autowired
    AmazonListingExportRecordService amazonListingExportRecordService;
    @Autowired
    UserUtils userUtils;
    @Autowired
    GetLoginUserInformationByToken getLoginUserInformationByToken;
    @Autowired
    AuthorizationSellerService authorizationSellerService;
    @Autowired
    AmazonPublishListingService amazonPublishListingService;

    @AspectContrLog(descrption = "查询导出列表", actionType = SysLogActionType.QUERY)
    @PostMapping("/getExportRecordPage")
    @ApiOperation(value = "查询导出列表", notes = "查询导出列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true), @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    public Page<AmazonListingExportRecord> getOrderAfterSalesPage(String page, String row) {
        try {
            String time = DateUtils.formatDate(DateUtils.getBeforeDateByDay(new Date(), 7), DateUtils.FORMAT_3);
            //Date date = DateUtils.parseDate(, "end"), DateUtils.FORMAT_2);
            Page.builder(page, row);
            String username = userUtils.getUser().getUsername();
            AmazonListingExportRecord exportRecord = new AmazonListingExportRecord();
            exportRecord.setPlAccount(username);
            exportRecord.setLimitTime(time);
            Page<AmazonListingExportRecord> exportRecordPage = amazonListingExportRecordService.page(exportRecord);
            List<AmazonListingExportRecord> listingExportRecords = exportRecordPage.getPageInfo().getList();
            if (null == listingExportRecords || listingExportRecords.size() == 0) {
                return exportRecordPage;
            }
            for (AmazonListingExportRecord amazonListingExportRecord : listingExportRecords) {
                if (AmazonConstants.EXPORT_STATUS_INIT.equals(amazonListingExportRecord.getExportStatus()) || AmazonConstants.EXPORT_STATUS_FAIL.equals(amazonListingExportRecord.getExportStatus())) {
                    amazonListingExportRecord.setExportName(null);
                }
            }
            return exportRecordPage;
        } catch (Exception e) {
            logger.error("查询导出列表异常",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询导出列表异常");
        }

    }

    @AspectContrLog(descrption = "导出", actionType = SysLogActionType.QUERY)
    @GetMapping("/export")
    @ApiOperation(value = "导出", notes = "导出")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "导出类型1.搜索导出2.选中导出", name = "exportType", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "ids选中导出时用", name = "ids", dataType = "List"),
            @ApiImplicitParam(paramType = "query", value = "刊登状态 1: 草稿  2: 刊登中 3: 在线 4: 刊登失败 5: 已下线", name = "publishStatus", dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", value = "刊登状态 1: 草稿  2: 刊登中 3: 在线 4: 刊登失败 5: 已下线", name = "publishStatusList", dataType = "List"),
            @ApiImplicitParam(paramType = "query", value = "刊登类型 1：单属性 2：多属性", name = "publishType", dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", value = "开始时间[yyyy-MM-dd]", name = "startCreateTime", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "结束时间[yyyy-MM-dd]", name = "endCreateTime", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "刊登账号", name = "publishAccount", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "刊登站点", name = "publishSite", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "asin", name = "asin", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "在亚马逊平台上的sku", name = "platformSku", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "品连sku", name = "plSku", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "产品标题", name = "title", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "0没有绑定品连sku,1绑定品连sku", name = "listingType", dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", value = "0正常 1下架 2缺货 3少货 4其他", name = "supplyStatus", dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", value = "在线修改状态 0初始值 1在线未修改 2修改成功 3修改失败 4修改中", name = "updateStatus", dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", value = "时间范围查询类型[0:创建时间 1:发布时间　2：上线时间 3：更新时间 ] 查询参数", name = "timeType", dataType = "Integer")
    })
    public void export(@ApiIgnore AmazonPublishListing model, String exportType) {
        logger.info("入口传入的参数" + JSONObject.toJSONString(model));
        try {
            UserAll userInfo = getLoginUserInformationByToken.getUserInfo();
            if (userInfo == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406, "该token已经失效，未登录或登录超时请重新登录，谢谢");
            }
            //处理数据权限
            UserDTO userdTO = getLoginUserInformationByToken.getUserDTO();
            if (userdTO == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406, "获取当前用户失败");
            }
            List<String> bindCode = new ArrayList<>();
            model.setPlAccount(userdTO.getTopUserLoginName());
            if (userdTO.getManage()) {
                //主账号
            } else {
                //子账号
                List<UserAccountDTO> binds = userdTO.getBinds();
                if (CollectionUtils.isEmpty(binds)) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401, "权限异常");
                }
                bindCode = binds.get(0).getBindCode();
                List<Empower> accounts = authorizationSellerService.getEmpowerByIds(strToInt(bindCode));
                if (accounts == null) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600, "权限异常");
                }
                bindCode.clear();
                for (Empower empower : accounts) {
                    if (!bindCode.contains(empower.getPinlianAccount())) {
                        bindCode.add(empower.getAccount());
                    }
                }
                model.setPublishAccounts(bindCode);
            }
            amazonListingExportRecordService.export(model, exportType);
        } catch (Exception e) {
            logger.error("查询亚马逊刊登信息异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询亚马逊刊登信息异常");
        }
    }

    private List<Integer> strToInt(List<String> bindCode) {
        List<Integer> empowerIds = new ArrayList<>();
        for (String str : bindCode) {
            empowerIds.add(Integer.parseInt(str));
        }
        return empowerIds;
    }


}















