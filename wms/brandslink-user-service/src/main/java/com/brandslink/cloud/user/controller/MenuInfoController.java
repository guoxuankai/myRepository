package com.brandslink.cloud.user.controller;

import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.user.dto.request.AddTopMenuInfoRequestDTO;
import com.brandslink.cloud.user.dto.response.MenuInfoResponseDTO;
import com.brandslink.cloud.user.service.IMenuInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 菜单
 *
 * @ClassName MenuInfoController
 * @Author tianye
 * @Date 2019/6/10 10:01
 * @Version 1.0
 */
@RestController
@Api("菜单相关接口")
@RequestMapping(value = "/menu")
public class MenuInfoController {

    @Resource
    private IMenuInfoService menuInfoService;

    @ApiOperation("获取账号对应的菜单树列表")
    @GetMapping("/getTree")
    public List<MenuInfoResponseDTO> getTree(@ApiParam(name = "id", value = "账号id") @RequestParam(value = "id", required = false) Integer id,
                                             @ApiParam(name = "flag", value = "所属平台 0：wmsPC端 1：wmsPDA端 2：oms端 3：ocms端", required = true) @RequestParam(value = "flag") Integer flag) {
        return menuInfoService.getTree(id, flag);
    }

    @ApiOperation("获取账号对应的菜单树列表，过滤功能菜单以及子页面 --> 用于登录获取权限菜单")
    @GetMapping("/getTreeOfMenus")
    public List<MenuInfoResponseDTO> getTreeOfMenus(@ApiParam(name = "id", value = "账号id", required = true) @RequestParam(value = "id") Integer id,
                                                    @ApiParam(name = "flag", value = "所属平台 0：wmsPC端 1：wmsPDA端 2：oms端 3：ocms端", required = true) @RequestParam(value = "flag") Integer flag) {
        return menuInfoService.getTreeOfMenus(id, flag, 1);
    }

    @ApiOperation("获取账号对应的菜单树列表，过滤功能菜单 --> 用于登录获取权限菜单路由")
    @GetMapping("/getTreeOfMenusFilterFnc")
    public List<MenuInfoResponseDTO> getTreeOfMenusFilterFnc(@ApiParam(name = "id", value = "账号id", required = true) @RequestParam(value = "id") Integer id,
                                                             @ApiParam(name = "flag", value = "所属平台 0：wmsPC端 1：wmsPDA端 2：oms端 3：ocms端", required = true) @RequestParam(value = "flag") Integer flag) {
        return menuInfoService.getTreeOfMenus(id, flag, 2);
    }

    @ApiOperation("获取账号对应的菜单树列表，过滤功能菜单以及中间节点菜单  --> 用于首页快捷菜单")
    @GetMapping("/getTreeOfMenusByUserId")
    public List<MenuInfoResponseDTO> getTreeOfMenusByUserId(@ApiParam(name = "id", value = "账号id", required = true) @RequestParam(value = "id") Integer id,
                                                            @ApiParam(name = "flag", value = "所属平台 0：wmsPC端 1：wmsPDA端 2：oms端 3：ocms端", required = true) @RequestParam(value = "flag") Integer flag) {
        return menuInfoService.getTreeOfMenusByUserId(id, flag);
    }

    @ApiOperation("获取所有菜单树列表")
    @GetMapping("/getTreeAll")
    public List<MenuInfoResponseDTO> getTreeAll(@ApiParam(name = "flag", value = "所属平台 0：wmsPC端 1：wmsPDA端 2：oms端 3：ocms端", required = true) @RequestParam(value = "flag") Integer flag) {
        return menuInfoService.getTree(null, flag);
    }

    @ApiOperation("新增菜单")
    @PostMapping("/addTopNode")
    @RequestRequire(require = "name,type,seq,belong", parameter = AddTopMenuInfoRequestDTO.class)
    public void addTopNode(@RequestBody AddTopMenuInfoRequestDTO requestDTO) {
        menuInfoService.addTopNode(requestDTO);
    }

    @ApiOperation("新增子菜单")
    @PostMapping("/addSubNode")
    @RequestRequire(require = "name,type,seq,belong", parameter = AddTopMenuInfoRequestDTO.class)
    public void addSubNode(@ApiParam(name = "parentId", value = "父级id", required = true) @RequestParam("parentId") Integer parentId,
                           @RequestBody AddTopMenuInfoRequestDTO requestDTO) {
        menuInfoService.addSubNode(parentId, requestDTO);
    }

    @ApiOperation("编辑菜单")
    @PostMapping("/updateNodeDetail")
    @RequestRequire(require = "name,type,seq,belong", parameter = AddTopMenuInfoRequestDTO.class)
    public void updateNodeDetail(@ApiParam(name = "id", value = "id", required = true) @RequestParam("id") Integer id,
                                 @RequestBody AddTopMenuInfoRequestDTO requestDTO) {
        menuInfoService.updateNodeDetail(id, requestDTO);
    }

    @ApiOperation("删除菜单")
    @GetMapping("/deleteNode")
    public void deleteNode(@ApiParam(name = "id", value = "id", required = true) @RequestParam("id") Integer id,
                           @ApiParam(name = "flag", value = "所属平台 0：wmsPC端 1：wmsPDA端 2：oms端 3：ocms端", required = true) @RequestParam(value = "flag") Integer flag) {
        menuInfoService.deleteNode(id, flag);
    }

}
