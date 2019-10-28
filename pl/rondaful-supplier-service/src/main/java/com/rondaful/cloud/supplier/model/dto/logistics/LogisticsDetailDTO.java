package com.rondaful.cloud.supplier.model.dto.logistics;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/10/16
 * @Description:
 */
public class LogisticsDetailDTO extends LogisticsPageDTO {
    private static final long serialVersionUID = -1310516681655744954L;

    @ApiModelProperty(value = "仓储自定义名称")
    private String firmName;

    @ApiModelProperty(value = "仓储服务商")
    private String firmService;

    @ApiModelProperty(value = "平台映射关系")
    private List<LogisticsMapDTO> maps;

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getFirmService() {
        return firmService;
    }

    public void setFirmService(String firmService) {
        this.firmService = firmService;
    }

    public List<LogisticsMapDTO> getMaps() {
        return maps;
    }

    public void setMaps(List<LogisticsMapDTO> maps) {
        this.maps = maps;
    }
}
