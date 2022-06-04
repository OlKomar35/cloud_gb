package com.komar_olga.cloud_gb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage{
    //передача файла
    private String fileName;
    private byte[] data;
    private String actionPoint;

    public String getActionPoint() {
        return actionPoint;
    }


    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

    public FileMessage(Path path,String actionPoint) throws IOException {
        fileName=path.getFileName().toString();
        data= Files.readAllBytes(path);
        this.actionPoint=actionPoint;
    }
}
