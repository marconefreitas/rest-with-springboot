package br.com.marconefreitas.data.dto;

import java.io.Serializable;
import java.util.Objects;

public class UploadFileResponseDTO implements Serializable {


    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;

    public UploadFileResponseDTO() {
    }

    public UploadFileResponseDTO(String fileName, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        UploadFileResponseDTO that = (UploadFileResponseDTO) o;
        return getSize() == that.getSize() && Objects.equals(getFileName(), that.getFileName()) && Objects.equals(getFileDownloadUri(), that.getFileDownloadUri()) && Objects.equals(getFileType(), that.getFileType());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getFileName());
        result = 31 * result + Objects.hashCode(getFileDownloadUri());
        result = 31 * result + Objects.hashCode(getFileType());
        result = 31 * result + Long.hashCode(getSize());
        return result;
    }
}
