package com.rondaful.cloud.user.model.response.user;

import com.rondaful.cloud.user.model.dto.user.ManageUserDetailDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/5/4
 * @Description:
 */
public class ManageUserResp extends ManageUserDetailDTO {

    private static final long serialVersionUID = 7231196229332435930L;
    private List<BindOrgaAllResp> accounts;

    public List<BindOrgaAllResp> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<BindOrgaAllResp> accounts) {
        this.accounts = accounts;
    }
}
