package com.rondaful.cloud.order.controller;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.order.model.dto.invoiceTemplate.InvoiceTemplateInsertDTO;
import com.rondaful.cloud.order.model.dto.invoiceTemplate.InvoiceTemplateListSearchDTO;
import com.rondaful.cloud.order.model.dto.invoiceTemplate.InvoiceTemplateUpdateDTO;
import com.rondaful.cloud.order.model.vo.invoiceTemplate.InvoiceTemplateDropDownListVO;
import com.rondaful.cloud.order.model.vo.invoiceTemplate.InvoiceTemplateInfoVO;
import com.rondaful.cloud.order.model.vo.invoiceTemplate.InvoiceTemplateListVO;
import com.rondaful.cloud.order.service.IInvoiceTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 发票模板控制层
 *
 * @author Blade
 * @date 2019-06-17 19:55:44
 **/
@Api(value = "发票模板")
@RestController("/invoiceTemplate")
public class InvoiceTemplateController extends BaseController {

    private IInvoiceTemplateService invoiceTemplateService;

    @Autowired
    public InvoiceTemplateController(IInvoiceTemplateService invoiceTemplateService) {
        this.invoiceTemplateService = invoiceTemplateService;
    }

    @ApiOperation("添加发票模板")
    @PostMapping("/insert")
    public void insert(@RequestBody InvoiceTemplateInsertDTO invoiceTemplateInsertDTO) {
        invoiceTemplateService.insert(invoiceTemplateInsertDTO);
    }

    @ApiOperation(value = "删除发票模板")
    @DeleteMapping("/delete")
    public void delete(@RequestParam Long id) {
        invoiceTemplateService.delete(id);
    }

    @ApiOperation("更新发票模板")
    @PutMapping("/update")
    public void update(@RequestBody InvoiceTemplateUpdateDTO invoiceTemplateUpdateDTO) {
        invoiceTemplateService.update(invoiceTemplateUpdateDTO);
    }

    @ApiOperation("分页查询")
    @PostMapping("/searchPage")
    public Page<InvoiceTemplateListVO> searchPage(@RequestBody InvoiceTemplateListSearchDTO searchDTO) {
        return invoiceTemplateService.page(searchDTO);
    }

    @ApiOperation(value = "根据ID查询发票模板")
    @GetMapping("/findById")
    public InvoiceTemplateInfoVO findById(@RequestParam Long id) {
        return invoiceTemplateService.findById(id);
    }

    @ApiOperation(value = "获取发票模板下拉列表")
    @GetMapping("/getDropDownList")
    public List<InvoiceTemplateDropDownListVO> getDropDownList(@RequestParam Integer sellerId) {
        return invoiceTemplateService.selectDropDownList(sellerId);
    }

}
