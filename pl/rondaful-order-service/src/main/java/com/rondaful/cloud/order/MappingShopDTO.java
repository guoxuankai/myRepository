package com.rondaful.cloud.order;

import lombok.Data;

import java.util.List;

/**
 * @ProjectName: Rondaful
 * @Package: com.rondaful.cloud.order
 * @ClassName: mappingShopDTO
 * @Author: Superhero
 * @Description:
 * @Date: 2019/8/30 17:12
 */
@Data
public class MappingShopDTO {
    private List<String> accounts;
    private String platform;
}
