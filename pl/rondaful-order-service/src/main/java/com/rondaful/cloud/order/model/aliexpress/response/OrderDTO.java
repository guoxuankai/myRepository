package com.rondaful.cloud.order.model.aliexpress.response;

import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrder;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrderChild;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/4
 * @Description:
 */
public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 2077219059733921669L;

    private AliexpressOrder order;

    private List<AliexpressOrderChild> childList;

    public AliexpressOrder getOrder() {
        return order;
    }

    public void setOrder(AliexpressOrder order) {
        this.order = order;
    }

    public List<AliexpressOrderChild> getChildList() {
        return childList;
    }

    public void setChildList(List<AliexpressOrderChild> childList) {
        this.childList = childList;
    }
}
