package com.rondaful.cloud.user.model.response.user;

import com.rondaful.cloud.user.model.dto.user.PithyUserDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/5/5
 * @Description:
 */
public class PithyUserResp extends PithyUserDTO {
    private static final long serialVersionUID = 5304058150439095299L;

    private List<BindOrgaAllResp> accounts;

    public List<BindOrgaAllResp> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<BindOrgaAllResp> accounts) {
        this.accounts = accounts;
    }
}
