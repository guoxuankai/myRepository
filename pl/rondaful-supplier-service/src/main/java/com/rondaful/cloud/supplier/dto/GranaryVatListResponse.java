package com.rondaful.cloud.supplier.dto;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.supplier.entity.VatDetailInfo;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 谷仓查询中转服务方式API
 *
 * @ClassName GranaryTransferWarehouseResponse
 * @Author tianye
 * @Date 2019/4/28 17:44
 * @Version 1.0
 */
public class GranaryVatListResponse extends GranaryResponseBase implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<VatDetail> data;

    public List<VatDetail> getData() {
        return data;
    }

    public void setData(List<VatDetail> data) {
        this.data = data;
    }

    public static class VatDetail extends VatDetailInfo {

        @ApiModelProperty(value = "营业执照/商业登记书")
        private FileDetail importerCompanyLicence;

        @ApiModelProperty(value = "增值税证明文件")
        private FileDetail gstCertificate;

        public FileDetail getImporterCompanyLicence() {
            return importerCompanyLicence;
        }

        public void setImporterCompanyLicence(FileDetail importerCompanyLicence) {
            this.importerCompanyLicence = importerCompanyLicence;
        }

        public FileDetail getGstCertificate() {
            return gstCertificate;
        }

        public void setGstCertificate(FileDetail gstCertificate) {
            this.gstCertificate = gstCertificate;
        }

        public static class FileDetail{
            @ApiModelProperty(value = "base64_encode文件")
            private String file;

            @ApiModelProperty(value = "返回文件类型")
            private String fileType;

            public String getFile() {
                return file;
            }

            public void setFile(String file) {
                this.file = file;
            }

            public String getFileType() {
                return fileType;
            }

            public void setFileType(String fileType) {
                this.fileType = fileType;
            }
        }

    }
}
