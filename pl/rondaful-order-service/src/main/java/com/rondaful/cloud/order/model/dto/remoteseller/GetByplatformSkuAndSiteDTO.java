package com.rondaful.cloud.order.model.dto.remoteseller;

import java.io.Serializable;
import java.util.List;

/**
 * @author Blade
 * @date 2019-07-29 18:20:45
 **/
public class GetByplatformSkuAndSiteDTO implements Serializable {

    private static final long serialVersionUID = 4880413528261804302L;
    
    private List<String> platformSku;
    private String site;
    private String type;
    private Integer empowerId;

    public List<String> getPlatformSku() {
        return platformSku;
    }

    public void setPlatformSku(List<String> platformSku) {
        this.platformSku = platformSku;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getEmpowerId() {
        return empowerId;
    }

    public void setEmpowerId(Integer empowerId) {
        this.empowerId = empowerId;
    }
}