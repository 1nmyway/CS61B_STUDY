�� sr gitlet.Blob�ݡ�_�E L IDt Ljava/lang/String;L blobSaveFileNamet Ljava/io/File;[ bytest [BL fileContentq ~ L fileNameq ~ L filePathq ~ xpt (393f60d3b1aab845cb2a7948e2d4a2fa17005d70ppt�package gitlet;

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
    public List<String> parents;
    public String author;
    public String timestamp;
    public String ID;
    public String fileName;
    public Date currentDate;
    public Map<String,String> pathToBlobID ;
    static int num;
    Commit(){}
    Commit (String message, List<String> parents,String timestamp,Map<String,String> pathToBlobID ) {
        this.message = message;
        this.parents = parents;
        this.timestamp = timestamp;
        this.pathToBlobID = pathToBlobID;

    }
    Commit (String message, List<String> parents,Date currentDate,Map<String,String> pathToBlobID ) {//用于生成hash id
        this.message = message;
        this.parents = parents;
        this.currentDate = currentDate;
        this.pathToBlobID = pathToBlobID;

    }
    Commit (String message, List<String> parents,String timestamp,Map<String,String> pathToBlobID ,String ID,String author,String fileName) {
        this.message = message;
        this.parents = parents;
        this.timestamp = timestamp;
        this.pathToBlobID = pathToBlobID;
        this.ID = ID;
        this.author = author;
        this.fileName = fileName;

    }


    public static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    public String generatelID(){
        return Utils.sha1(dateToTimeStamp(currentDate), message, parents.toString(), pathToBlobID.toString());
    }
    public  void initCommit() {
        this.currentDate=new Date(0);
        Commit commit = new Commit("initial commit", new ArrayList<>(),currentDate,new HashMap<>());
        String initCommitHashID = commit.generatelID();
        Commit initCommit =new Commit("initCommit", new ArrayList<>(), dateToTimeStamp(currentDate),new HashMap<>(),initCommitHashID,"","initCommit");
        File f = join(Repository.COMMIT_DIR, initCommitHashID);
        try {
            f.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(f, initCommit);//把initcommit对象写入文件
        writeContents(Repository.HEAD_FILE, initCommitHashID);//把头指针指向初始化的commit
    }



    /* TODO: fill in the rest of this class. */
}
sr java.io.File-�E�� L pathq ~ xpt /E:\CS61B\skeleton-sp21\proj2\gitlet\Commit.javaw \xq ~ 	