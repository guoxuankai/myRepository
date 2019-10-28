package com.brandslink.cloud.finance.pojo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author yangzefei
 * @Classname DomainObject
 * @Description 适用于有更新操作的业务实体
 * @Date 2019/8/12 10:54
 */
public class DomainObject extends BaseObject{

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    public String getUpdateBy() {
        return updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置操作人员信息
     * @param by 操作人
     * @param isUpdate 更新还是创建
     */
    public void setOperate(String by,Boolean isUpdate) {
        if(isUpdate){
            this.updateBy=by;
            this.updateTime=new Date();
        }else{
            this.updateBy="";
            super.setOperate(by);
        }
    }
}
