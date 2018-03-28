package ru.marksblog;

import javafx.beans.property.SimpleStringProperty;

public class FileData {

    SimpleStringProperty filename;
    SimpleStringProperty size;

    public FileData(String filename,String size){
        this.filename=new SimpleStringProperty(filename);
        this.size=new SimpleStringProperty(size);
    }

    public void setFilename(String filename){
        this.filename.set(filename);
    }

    public void setFilesize(String size) {
        this.size.set(size);
    }

    public String getFilename() {
        return filename.get();
    }

    public String getSize() {
        return size.get();
    }
}
