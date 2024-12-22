package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    private String ID;
    private File fileName;
    private String filePath;
    private File blobSaveFileName;
    public byte[] bytes;
    Blob() {}
    Blob(File fileName, String filePath,String ID,byte[] bytes,File blobSaveFileName) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.ID = ID;
        this.bytes = bytes;
        this.blobSaveFileName = blobSaveFileName;
    }
    Blob(File fileName, String filePath,String ID,byte[] bytes) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.ID = ID;
        this.bytes = bytes;
    }
    Blob(byte[] bytes) {
        this.bytes = bytes;
    }
    Blob(File fileName){this.fileName = fileName;}
    public byte[] getBytes() {
        return Utils.readContents(fileName);
    }
    public String generatelID(byte[] bytes){
        return Utils.sha1(bytes);
    }
    //tttt
}
