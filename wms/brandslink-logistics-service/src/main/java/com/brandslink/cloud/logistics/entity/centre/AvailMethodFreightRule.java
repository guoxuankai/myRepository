package com.brandslink.cloud.logistics.entity.centre;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "AvailMethodFreightRule")
public class AvailMethodFreightRule implements Serializable {
    /*private String methodId;
    private String methodName;
    private String methodCode;
    private String country;
    private String weightRange;*/
    private String method_id;
    private String method_name;
    private String method_code;
    private String country;
    private String weight_range;
}
