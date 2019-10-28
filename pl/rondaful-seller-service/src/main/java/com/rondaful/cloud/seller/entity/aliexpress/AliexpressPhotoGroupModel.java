package com.rondaful.cloud.seller.entity.aliexpress;

import com.rondaful.cloud.seller.entity.AliexpressPhotoGroup;

import java.util.List;

/**
 *
 * @author chenhan
 *
 */
public class AliexpressPhotoGroupModel {

    private String success;
    private List<AliexpressPhotoGroup> photoBankImageGroupList;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<AliexpressPhotoGroup> getPhotoBankImageGroupList() {
        return photoBankImageGroupList;
    }

    public void setPhotoBankImageGroupList(List<AliexpressPhotoGroup> photoBankImageGroupList) {
        this.photoBankImageGroupList = photoBankImageGroupList;
    }
}
