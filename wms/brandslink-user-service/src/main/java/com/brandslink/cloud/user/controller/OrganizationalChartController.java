package com.brandslink.cloud.user.controller;

import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.user.dto.response.OrganizationalChartInfoResponseDTO;
import com.brandslink.cloud.user.service.IOrganizationalChartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 组织结构
 *
 * @ClassName OrganizationalChartController
 * @Author tianye
 * @Date 2019/6/5 18:37
 * @Version 1.0
 */
@RestController
@Api("组织架构相关接口")
@RequestMapping(value = "/organizationalChart")
public class OrganizationalChartController extends BaseController {

    @Resource
    private IOrganizationalChartService organizationalChartService;

    @ApiOperation("获取组织结构树列表")
    @GetMapping("/getTree")
    public List<OrganizationalChartInfoResponseDTO> getTree() {
        return organizationalChartService.getTree();
    }

    @ApiOperation("添加下级")
    @PostMapping("/addSubNode")
    @RequestRequire(require = "name,position", parameter = String.class)
    public void addSubNode(@ApiParam(name = "parentId", value = "父级id", required = true) @RequestParam("parentId") Integer parentId,
                           @ApiParam(name = "name", value = "公司/部门名称", required = true) @RequestParam("name") String name,
                           @ApiParam(name = "position", value = "职位名称，多个以逗号分隔", required = true) @RequestParam("position") String position) {
        organizationalChartService.addSubNode(parentId, name, position);
    }

    @ApiOperation("新增公司")
    @PostMapping("/addTopNode")
    @RequestRequire(require = "name", parameter = String.class)
    public void addTopNode(@ApiParam(name = "name", value = "公司/部门名称", required = true) @RequestParam("name") String name) {
        organizationalChartService.addTopNode(name);
    }

    @ApiOperation("编辑")
    @PostMapping("/updateNodeDetail")
    public void updateNodeDetail(@ApiParam(name = "id", value = "id", required = true) @RequestParam("id") Integer id,
                                 @ApiParam(name = "name", value = "公司/部门名称", required = true) @RequestParam("name") String name,
                                 @ApiParam(name = "position", value = "职位名称，多个以逗号分隔") @RequestParam(value = "position", required = false) String position) {
        organizationalChartService.updateNodeDetail(id, name, position);
    }

    @ApiOperation("删除")
    @GetMapping("/deleteNode")
    public void deleteNode(@ApiParam(name = "id", value = "id", required = true) @RequestParam("id") Integer id) {
        organizationalChartService.deleteNode(id);
    }

}
