package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    private String ID;
    public String fileName;
    private String filePath;
    private File blobSaveFileName;
    public byte[] bytes;
    public byte[] fileContent;
    Blob() {}
    Blob(String fileName, String filePath,String ID,byte[] bytes,File blobSaveFileName) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.ID = ID;
        this.bytes = bytes;
        this.blobSaveFileName = blobSaveFileName;
    }
    Blob(String fileName, String filePath,String ID,byte[] fileContent) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.ID = ID;
        this.fileContent = fileContent;

    }
    Blob(byte[] bytes) {
        this.bytes = bytes;
    }
    Blob(String fileName){this.fileName = fileName;}
    //public byte[] getBytes() {
//        return Utils.readContents(fileName);
//    }
    public String generatelID(byte[] fileContent,String fileName){
        return Utils.sha1(fileContent,fileName);
    }
    //tttt
}
