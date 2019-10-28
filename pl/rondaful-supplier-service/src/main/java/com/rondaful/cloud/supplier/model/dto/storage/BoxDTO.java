package com.rondaful.cloud.supplier.model.dto.storage;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/30
 * @Description:
 */
public class BoxDTO implements Serializable {
    private static final long serialVersionUID = 8742931054352825013L;

    private Integer fileType;

    private byte[] data;

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
