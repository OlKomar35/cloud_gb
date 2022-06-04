package com.komar_olga.cloud_gb;

public class FileRequest extends AbstractMessage {
    // передача запроса
    private String fileName;
    private String actionPoint;



    public String getActionPoint() {
        return actionPoint;
    }

    public String getFileName() {
        return fileName;
    }

    public FileRequest(String fileName,String actionPoint) {
        this.fileName = fileName;
        this.actionPoint=actionPoint;
    }
}