package com.rondaful.cloud.supplier.controller;

import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.supplier.common.GetLoginInfo;
import com.rondaful.cloud.supplier.dto.GranarySmCodeResponse;
import com.rondaful.cloud.supplier.dto.GranaryTransferWarehouseResponse;
import com.rondaful.cloud.supplier.entity.VatDetailInfo;
import com.rondaful.cloud.supplier.entity.WarehouseWarrantRequest;
import com.rondaful.cloud.supplier.entity.WarehouseWarrantResponse;
import com.rondaful.cloud.supplier.service.IWarehouseWarrantService;
import com.rondaful.cloud.supplier.vo.ModifyWarehouseWarrantVo;
import com.rondaful.cloud.supplier.vo.UserInfoVO;
import com.rondaful.cloud.supplier.vo.WarehouseWarrantDetailResponseVo;
import com.rondaful.cloud.supplier.vo.WarehouseWarrantVo;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 入库单 Controller
 *
 * @ClassName WarehouseWarrantController
 * @Author tianye
 * @Date 2019/4/25 18:51
 * @Version 1.0
 */
@Api(description = "入库单接口")
@RestController
@RequestMapping(value = "/warrant")
public class WarehouseWarrantController {

    private final Logger logger = LoggerFactory.getLogger(WarehouseWarrantController.class);

    @Autowired
    private IWarehouseWarrantService warehouseWarrantService;

    @Autowired
    private GetLoginInfo getLoginInfo;

    @AspectContrLog(descrption = "根据模糊条件查询入库单列表", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "根据模糊条件查询入库单列表", notes = "")
    @PostMapping("/getWarrantList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "row", value = "每页显示行数", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "入库单状态 0:草稿,1:审核中,2:待收货,3:已入库,4:已取消,5:异常 9:所有", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "facilitatorCode", value = "仓库服务商代码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "warehouseCode", value = "目的仓代码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "receivingCode", value = "入库单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "supplier", value = "供应商名称", dataType = "string", paramType = "query")})
    @RequestRequire(require = "page, row", parameter = WarehouseWarrantRequest.class)
    public Page<WarehouseWarrantResponse> getWarrantList(@RequestBody WarehouseWarrantRequest request) {
        if (StringUtils.equals(request.getStatus(), "9")) {
            request.setStatus(StringUtils.EMPTY);
        }
        logger.info("当前登录平台：{}", getLoginInfo.getUserInfo().getPlatformType());
        request.setTopFlag(getLoginInfo.getUserInfo().getTopFlag());
        logger.info("当前登录账号状态：{}", getLoginInfo.getUserInfo().getTopFlag());
        if (getLoginInfo.getUserInfo().getPlatformType() == 0) {
            logger.info("供应商ID:{}", getLoginInfo.getUserInfo().getTopUserId());
            request.setSupplierId(getLoginInfo.getUserInfo().getTopUserId());
            if (CollectionUtils.isNotEmpty(getLoginInfo.getUserInfo().getwCodes())) {
                request.setCodeList(getLoginInfo.getUserInfo().getwCodes());
            }
        }
        if (getLoginInfo.getUserInfo().getPlatformType() == 2) {
            if (CollectionUtils.isNotEmpty(getLoginInfo.getUserInfo().getSuppliers())) {
                request.setSupplies(getLoginInfo.getUserInfo().getSuppliers());
            }
        }
        return warehouseWarrantService.getWarehouseWarrantListBySelective(request);
    }

    @AspectContrLog(descrption = "根据主键查询入库单明细", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "根据主键查询入库单明细", notes = "")
    @GetMapping("/getWarrantDetail/{id}")
    public WarehouseWarrantDetailResponseVo getWarrantDetail(@ApiParam(name = "id", value = "入库单id", required = true) @PathVariable("id") Long primaryKey) {
        return warehouseWarrantService.getWarehouseWarrantDetailByPrimaryKey(primaryKey);
    }

    @AspectContrLog(descrption = "创建入库单", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "创建入库单", notes = "")
    @PostMapping("/addWarrantAndCommit")
    public void addWarrantAndCommit(@RequestBody WarehouseWarrantVo vo) {
        UserInfoVO userInfo = getLoginInfo.getUserInfo();
        warehouseWarrantService.insertWarrant(vo, userInfo);
    }

    @AspectContrLog(descrption = "提交入库单", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "提交入库单", notes = "")
    @GetMapping("/commitWarrant/{id}")
    public void commitWarrant(@ApiParam(name = "id", value = "入库单id", required = true) @PathVariable("id") Long primaryKey) {
        warehouseWarrantService.commitWarrant(primaryKey);
    }

    @AspectContrLog(descrption = "获取中转仓库", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "获取中转仓库", notes = "")
    @PostMapping("/getTransferWarehouse")
    public List<GranaryTransferWarehouseResponse.TransferWarehouseDeatil> getTransferWarehouse(@ApiParam(name = "warehouseCode", value = "目的仓代码", required = true) @RequestParam("warehouseCode") String warehouseCode) {
        return warehouseWarrantService.getTransferWarehouse(warehouseCode);
    }

    @AspectContrLog(descrption = "获取中转服务方式", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "获取中转服务方式", notes = "")
    @PostMapping("/getSmcode")
    public GranarySmCodeResponse.SmCodeData getSmcode(@ApiParam(name = "warehouseCode", value = "目的仓代码", required = true) @RequestParam("warehouseCode") String warehouseCode) {
        return warehouseWarrantService.getSmCode(warehouseCode);
    }

    @AspectContrLog(descrption = "编辑入库单", actionType = SysLogActionType.UDPATE)
    @ApiOperation(value = "编辑入库单", notes = "")
    @PostMapping("/modifyWarrant")
    public void ModifyWarrant(@RequestBody ModifyWarehouseWarrantVo vo) {
        UserInfoVO userInfo = getLoginInfo.getUserInfo();
        warehouseWarrantService.updateWarrant(vo, userInfo);
    }

    @AspectContrLog(descrption = "根据主键删除入库单明细", actionType = SysLogActionType.DELETE)
    @ApiOperation(value = "根据主键删除入库单明细", notes = "")
    @GetMapping("/deleteWarehouseWarrantDetail/{id}")
    public void deleteWarehouseWarrantDetail(@ApiParam(name = "id", value = "入库单id", required = true) @PathVariable("id") Long primaryKey) {
        warehouseWarrantService.deleteWarehouseWarrantDetail(primaryKey);
    }

    @AspectContrLog(descrption = "编辑备注", actionType = SysLogActionType.UDPATE)
    @ApiOperation(value = "编辑备注", notes = "")
    @PostMapping("/editorsComment")
    public void editorsComment(
            @ApiParam(name = "id", value = "入库单id", required = true) @RequestParam("id") Long primaryKey,
            @ApiParam(name = "comment", value = "备注信息", required = true) @RequestParam("comment") String comment) {
        warehouseWarrantService.editorsComment(primaryKey, comment);
    }

    @AspectContrLog(descrption = "取消已经提交谷仓的入库单", actionType = SysLogActionType.DELETE)
    @ApiOperation(value = "取消已经提交谷仓的入库单", notes = "")
    @GetMapping("/discardWarehouseWarrant/{id}")
    public void discardWarehouseWarrant(@ApiParam(name = "id", value = "入库单id", required = true) @PathVariable("id") Long primaryKey) {
        warehouseWarrantService.deleteByDiscardWarehouseWarrant(primaryKey);
    }

    @AspectContrLog(descrption = "根据目的仓代码获取进出口商代码", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "根据目的仓代码获取进出口商代码", notes = "")
    @PostMapping("/getVatList")
    public Map<String, List<VatDetailInfo>> getVatList(@ApiParam(name = "warehouseCode", value = "目的仓代码", required = true) @RequestParam("warehouseCode") String warehouseCode) {
        return warehouseWarrantService.getVatList(warehouseCode);
    }

    @AspectContrLog(descrption = "打印箱唛", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "打印箱唛", notes = "")
    @PostMapping("/printGcReceivingBox")
    public void printGcReceivingBox(HttpServletResponse response,
                                    @ApiParam(name = "id", value = "入库单id", required = true) @RequestParam("id") Long primaryKey,
                                    @ApiParam(name = "receivingCode", value = "入库单号", required = true) @RequestParam("receivingCode") String receivingCode,
                                    @ApiParam(name = "printSize", value = "打印尺寸（1:A4，2:100*100，3:100*150）", required = true) @RequestParam("printSize") String printSize,
                                    @ApiParam(name = "printType", value = "打印类型(1：入库清单，2：箱唛)", required = true) @RequestParam("printType") String printType,
                                    @ApiParam(name = "receivingBoxNoArr", value = "print_type=1时，为空 print_type=2时，必填 入库单箱号(一次不能超过50个)") @RequestParam(value = "receivingBoxNoArr", required = false) String[] arr) {
        warehouseWarrantService.printGcReceivingBox(primaryKey, response, receivingCode, printSize, printType, arr);
    }

    @AspectContrLog(descrption = "打印SKU标签", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "打印SKU标签", notes = "")
    @PostMapping("/printSku")
    public void printSku(HttpServletResponse response,
                         @ApiParam(name = "id", value = "入库单id", required = true) @RequestParam("id") Long primaryKey,
                         @ApiParam(name = "printSize", value = "打印尺寸（1：60*20，2：70*30，3：100*30）", required = true) @RequestParam("printSize") String printSize,
                         @ApiParam(name = "printCode", value = "打印编码(1：产品名称，2：made in china，5：商品英文名称，3：1和2，，6:1和5 ， 7：2和5,8:1和2和5都选，4：都不选)", required = true) @RequestParam("printCode") String printCode,
                         @ApiParam(name = "productSkuArr", value = "产品编码(一次不能超过50个)", required = true) @RequestParam(value = "productSkuArr") String[] arr) {
        warehouseWarrantService.printSku(primaryKey, response, printSize, printCode, arr);
    }

}
