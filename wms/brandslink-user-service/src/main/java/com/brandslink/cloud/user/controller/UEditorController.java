package com.brandslink.cloud.user.controller;

import com.brandslink.cloud.common.constant.ConstantAli;
import com.brandslink.cloud.common.constant.UserConstant;
import com.brandslink.cloud.common.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于处理关于ueditor插件相关的请求
 *
 * @ClassName UEditorController
 * @Author tianye
 * @Date 2019/8/6 11:10
 * @Version 1.0
 */
@RestController
@CrossOrigin
@Api("富文本图片上传接口")
public class UEditorController {

    @Value("${brandslink.system.env}")
    public String env;

    @Resource
    private FileService fileService;

    @ApiOperation(value = "文件上传")
    @PostMapping(value = "/sys/upload")
    public Map<String, Object> uploadImage(@RequestParam("upfile") MultipartFile upfile) {
        Map<String, Object> params = new HashMap<>();
        try {
            String ext;
            String name = upfile.getOriginalFilename();
            if (name == null || "".equals(name) || !name.contains("."))
                ext = "";
            else {
                ext = name.substring(name.lastIndexOf("."));
            }
            String fileName = String.valueOf(System.currentTimeMillis()).concat("_").concat(String.valueOf(RandomUtils.nextInt(0, 6))).concat(ext);
            // 上传文件
            String visitUrl = fileService.saveFile(ConstantAli.getEnv(env), ConstantAli.getFolder("wms"), fileName, upfile.getInputStream(), null, null, null);
            // 替换域名
            visitUrl = visitUrl.replace(UserConstant.DEF[0], UserConstant.DO_MAIN[0])
                    .replace(UserConstant.DEF[1], UserConstant.DO_MAIN[1])
                    .replace(UserConstant.DEF[2], UserConstant.DO_MAIN[2]);
            params.put("state", "SUCCESS");
            params.put("url", visitUrl);
            params.put("size", upfile.getSize());
            params.put("original", name);
            params.put("title", fileName);
            params.put("type", ext);
        } catch (Exception e) {
            params.put("state", "ERROR");
        }
        return params;
    }
}
