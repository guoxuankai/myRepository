package com.rondaful.cloud.seller.controller;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.seller.entity.PrefixCode;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.service.PrefixCodeService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author guoxuankai
 * @date 2019/6/10
 */
@RestController
@RequestMapping("/prefixcode")
@Api(description = "前缀码接口")
public class PrefixCodeController {

    @Autowired
    private PrefixCodeService prefixCodeService;

    @Autowired
    private GetLoginUserInformationByToken getUserInfo;

    private final Logger logger = LoggerFactory.getLogger(PrefixCodeController.class);


    @ApiOperation(value = "查询前缀码列表信息", notes = "查询前缀码列表信息")
    @GetMapping("/findAll")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "page", value = "第几页", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "row", value = "一页条数", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "code", value = "前缀码", required = false)
    })
    public Page<PrefixCode> findAll(@RequestParam(value = "page") String page, @RequestParam(value = "row") String row,
                                    @RequestParam(value = "code", required = false) String code) {
        try {
            Page.builder(page, row);
            PrefixCode prefixCode = new PrefixCode();
            prefixCode.setPrefixcode(code);
            Page<PrefixCode> codePage = prefixCodeService.page(prefixCode);
            List<PrefixCode> list = codePage.getPageInfo().getList();
            list.forEach(
                    prefix->
                        prefix.setCount(100000-prefix.getCount()>0?100000-prefix.getCount():0)
            );
            return codePage;
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }

    }

    @ApiOperation(value = "卖家新增前缀码", notes = "卖家新增前缀码")
    @PostMapping("/prefixcode")
    public void insert(@ApiParam(value = "前缀码", name = "code", required = true) @RequestParam("code") String code) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        boolean matches = pattern.matcher(code).matches();
        if (matches == false || code.length() != 6) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "前缀码必须为6位数字");
        }else if(prefixCodeService.isRepeat(code)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "前缀码不允许重复");
        }
        PrefixCode prefixCode = new PrefixCode();
        prefixCode.setPrefixcode(code);
        prefixCodeService.insertSelective(prefixCode);

    }


    @ApiOperation(value = "根据前缀码，生成随机upc码", notes = "根据前缀码，生成随机upc码")
    @GetMapping("/randomUpc")
    public List<String> randomUpc(@ApiParam(value = "前缀码", name = "code", required = true) @RequestParam("code") String code) {
        try {
            return prefixCodeService.generateRandomUpc(code);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }

    }


    @ApiOperation(value = "卖家通过id删除所属自己的前缀码", notes = "卖家通过id删除所属自己的前缀码")
    @DeleteMapping("/prefixcode/{id}")
    public void delete(@PathVariable("id") Long id) {

        PrefixCode prefixCode = prefixCodeService.selectByPrimaryKey(id);
        if (prefixCode == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600);
        }
        prefixCodeService.deleteByPrimaryKey(id);


    }


    @ApiOperation(value = "前缀码是否重复校验，返回true表示重复，返回false表示没有重复", notes = "前缀码是否重复校验，返回true表示重复，返回false表示没有重复")
    @GetMapping("/isRepeat")
    public boolean isRepeat(@ApiParam(value = "前缀码", name = "code", required = true) @RequestParam("code") String code) {
        return prefixCodeService.isRepeat(code);
    }

    @ApiOperation(value = "统计生成的upc码现存的总量")
    @GetMapping("/getUpcAmout")
    public int getUpcAmout() {
        return prefixCodeService.getUpcAmout();
    }

}
