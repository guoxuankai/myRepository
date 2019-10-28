package com.rondaful.cloud.commodity.enums;


/**
* @Description:sku操作内容
* @author:范津 
* @date:2019年8月27日 下午2:27:16
 */
public enum SkuOperateInfoEnum {

	/**创建保存商品到【待提交】*/
	ADD("创建保存商品到【待提交】"),
	
	/**编辑修改保存商品*/
    UPDATE("编辑修改保存商品"),
    
    /**提交审核商品到【审核中】*/
    TO_AUDIT("提交审核商品到【审核中】"),
    
    /**商品审核失败到【审核失败】*/
    AUDIT_FAIL("商品审核失败到【审核失败】"),
    
    /**商品审核通过到【待上架】*/
    TO_UP("商品审核通过到【待上架】"),
    
    /**商品上架完成到【已上架】*/
    DO_UP("商品上架完成到【已上架】"),
    
    /**商品下架完成到【待上架】*/
    DO_DOWN("商品下架完成到【待上架】"),
    
    /**商品sku删除*/
    DELETE("商品sku删除"),
    
    /**ERP推送新增商品*/
    ERP_ADD("ERP推送新增商品"),
    
    /**ERP推送编辑商品*/
    ERP_UPDATE("ERP推送编辑商品"),
    
    /**ERP上架商品*/
    ERP_UP("ERP上架商品"),
    
    /**ERP下架商品*/
    ERP_DOWN("ERP下架商品"),
    
    /**复制商品到【待提交】*/
	COPY_ADD("复制商品到【待提交】")
    ;



    private String msg;

    SkuOperateInfoEnum(String msg) {
        this.msg = msg;
    }
    
    
    public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}


	public static void main(String[] args) {
		System.out.println(SkuOperateInfoEnum.ADD.getMsg());
	}

}
