package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    public String message;
    public List<Commit> parents;
    public String author;
    public String timestamp;
    public String ID;
    public String fileName;
    public Date currentDate;
    public Map<String,String> pathToBlobID ;
    public ArrayList<String> blobID;
    static int num;

    public File branch;
    public Map<String, String> fileMap;  // 文件到Blob的映射 (文件名 -> Blob ID)


    // 获取当前提交的文件树
    public Map<String, String> getFileMap() {
        return fileMap;
    }
    Commit(){}
    Commit (String message, List<Commit> parents,String timestamp,Map<String,String> pathToBlobID ) {
        this.message = message;
        this.parents = parents;
        this.timestamp = timestamp;
        this.pathToBlobID = pathToBlobID;

    }
    Commit (String message, List<Commit> parents,Date currentDate,Map<String,String> pathToBlobID ) {//用于生成hash id
        this.message = message;
        this.parents = parents;
        this.currentDate = currentDate;
        this.pathToBlobID = pathToBlobID;

    }
    Commit (String message, List<Commit> parents,Date currentDate,ArrayList<String> blobID ) {//用于生成hash id
        this.message = message;
        this.parents = parents;
        this.currentDate = currentDate;
        this.blobID = blobID;

    }
    Commit (String message, List<Commit> parents,String timestamp,Map<String,String> pathToBlobID ,String ID,String author,String fileName) {
        this.message = message;
        this.parents = parents;
        this.timestamp = timestamp;
        this.pathToBlobID = pathToBlobID;
        this.ID = ID;
        this.author = author;
        this.fileName = fileName;

    }
    Commit (String message, List<Commit> parents,String timestamp,ArrayList<String> blobID ,String ID,String author,String fileName, File branch,Map<String, String> fileMap) {
        this.message = message;
        this.parents = parents;
        this.timestamp = timestamp;
        this.blobID = blobID;
        this.ID = ID;
        this.author = author;
        this.fileName = fileName;
        this.branch = branch;
        this.fileMap = fileMap;

    }


    public static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    public String generatelID(){
        return Utils.sha1(dateToTimeStamp(currentDate), message, parents.toString());
    }
    public  void initCommit() {
        this.currentDate=new Date(0);
        Commit commit = new Commit("initial commit", new ArrayList<>(),currentDate,new HashMap<>());
        String initCommitHashID = commit.generatelID();
        Commit initCommit =new Commit("initial commit", new ArrayList<>(), dateToTimeStamp(currentDate),new HashMap<>(),initCommitHashID,"","initCommit");
        File f = join(Repository.COMMIT_DIR, initCommitHashID);
        try {
            f.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(f, initCommit);//把initcommit对象写入文件
        writeContents(Repository.HEAD_FILE, initCommitHashID);//把头指针指向初始化的commit
        writeContents(Repository.MASTER_DIR, initCommitHashID);
    }



    /* TODO: fill in the rest of this class. */
}
