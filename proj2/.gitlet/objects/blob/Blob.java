�� sr gitlet.Blob�ݡ�_�E L IDt Ljava/lang/String;L blobSaveFileNamet Ljava/io/File;[ bytest [BL fileContentq ~ L fileNameq ~ L filePathq ~ xpt (33485b574208f96d9edc7345dbca6071594f84cbppt�����sr��gitlet.Blob�ݡ�_�E��L��IDt��Ljava/lang/String;L��blobSaveFileNamet��Ljava/io/File;[��bytest��[BL��fileContentq��~��L��fileNameq��~��L��filePathq��~��xpt��(ebe5ff9eeca8d5f777923f97d67ae6e38781ca3dpptTpackage gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    private String ID;
    private File fileName;
    private String filePath;
    private File blobSaveFileName;
    public byte[] bytes;
    public String fileContent;
    Blob() {}
    Blob(File fileName, String filePath,String ID,byte[] bytes,File blobSaveFileName) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.ID = ID;
        this.bytes = bytes;
        this.blobSaveFileName = blobSaveFileName;
    }
    Blob(File fileName, String filePath,String ID,String fileContent) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.ID = ID;
        this.fileContent = fileContent;

    }
    Blob(byte[] bytes) {
        this.bytes = bytes;
    }
    Blob(File fileName){this.fileName = fileName;}
    public byte[] getBytes() {
        return Utils.readContents(fileName);
    }
    public String generatelID(String fileContent){
        return Utils.sha1(fileContent);
    }
    //tttt
}
sr��java.io.File-�E����L��pathq��~��xpt��-E:\CS61B\skeleton-sp21\proj2\gitlet\Blob.javaw��\xq��~��	sr java.io.File-�E�� L pathq ~ xpt ;E:\CS61B\skeleton-sp21\proj2\.gitlet\objects\blob\Blob.javaw \xq ~ 	